"""Monthly interest accrual + top customer ranking - PySpark migration.

Reference target dla demo Blok 5 (bonus HQL → PySpark). To, co powinien
wyprodukować Copilot przy dobrze sformułowanym prompcie. Porównaj
z wygenerowanym kodem na live demo.

Mapping HQL -> PySpark:
    WITH CTE             -> osobne DataFrame, opcjonalnie .cache()
    AVG / MAX / SUM      -> F.avg / F.max / F.sum (BEZ cast w agregacji, żeby
                            zachować pełną precyzję, identycznie jak Hive)
    COALESCE(x, 0)       -> F.coalesce(F.col("x"), F.lit(0))
    ROW_NUMBER OVER ()   -> F.row_number().over(Window.partitionBy().orderBy())
    LAST_DAY(date)       -> F.last_day(date)
    DECIMAL(18, 2)       -> DecimalType(18, 2) tylko jako typ KOLUMN docelowych,
                            nie w środku agregacji
    ROUND(x, 2)          -> F.round(x, 2) (HALF_UP, identycznie jak Hive)
    INSERT OVERWRITE     -> .write.insertInto(target_table, overwrite=True)
                            z `partitionOverwriteMode=dynamic` (nadpisuje tylko
                            wskazaną partycję, nie całą tabelę)
"""

from __future__ import annotations

from datetime import date
from decimal import Decimal

from pyspark.sql import DataFrame, SparkSession, Window
from pyspark.sql import functions as F
from pyspark.sql.types import DecimalType


AMOUNT_TYPE = DecimalType(18, 2)
RATE_TYPE = DecimalType(10, 6)
REPORT_MONTH = date(2026, 4, 1)
TARGET_TABLE = "analytics.monthly_interest_report"

# Kolejność kolumn musi być zgodna ze schemą tabeli docelowej,
# bo `insertInto` wpisuje wartości po POZYCJI, nie po nazwie.
TARGET_COLUMNS = [
    "customer_id",
    "account_id",
    "account_type",
    "avg_balance",
    "peak_balance",
    "total_volume",
    "accrued_interest",
    "rank_in_customer",
]


def build_monthly_interest_report(
    spark: SparkSession,
    accounts: DataFrame,
    daily_balances: DataFrame,
    transactions: DataFrame,
    report_month: date,
    min_activity_threshold: Decimal = Decimal("1000.00"),
) -> DataFrame:
    """Replikuje logikę legacy_interest.hql.

    Argumenty:
        spark: aktywna sesja Sparka.
        accounts: DataFrame `raw.accounts`.
        daily_balances: DataFrame `raw.daily_balances`.
        transactions: DataFrame `raw.transactions`.
        report_month: pierwszy dzień miesiąca raportowego (DateType).
        min_activity_threshold: minimalne średnie saldo, by konto trafiło do raportu.

    Zwraca:
        DataFrame z kolumnami: customer_id, account_id, account_type, avg_balance,
        peak_balance, total_volume, accrued_interest, rank_in_customer.

    Uwaga o precyzji:
        Agregacje (F.avg, F.max, F.sum) ZACHOWUJEMY w pełnej precyzji Decimal,
        identycznie jak Hive. Cast na DecimalType(18,2) UCINA, a nie zaokrągla,
        więc wprowadzałby cichą rozbieżność wartości w stosunku do HQL.
        Zaokrąglenie końcowych kolumn finansowych (avg_balance, peak_balance,
        total_volume) zostawiamy schemie tabeli docelowej.
    """
    month_start = F.lit(report_month).cast("date")
    month_end = F.last_day(month_start)

    active_accounts = (
        accounts
        .where((F.col("opened_at") <= month_start)
               & (F.col("closed_at").isNull() | (F.col("closed_at") > month_start)))
        .select("account_id", "customer_id", "account_type", "base_rate")
    )

    monthly_balances = (
        daily_balances
        .where(F.col("balance_date").between(month_start, month_end))
        .groupBy("account_id")
        .agg(
            F.avg("balance").alias("avg_balance"),
            F.max("balance").alias("peak_balance"),
        )
    )

    monthly_activity = (
        transactions
        .where(F.col("tx_date").between(month_start, month_end))
        .groupBy("account_id")
        .agg(F.sum("amount").alias("total_volume"))
    )

    rank_in_customer = (
        F.row_number()
        .over(Window.partitionBy("customer_id").orderBy(F.col("avg_balance").desc()))
    )

    return (
        active_accounts
        .join(monthly_balances, on="account_id", how="inner")
        .join(monthly_activity, on="account_id", how="left")
        .where(F.col("avg_balance") >= F.lit(min_activity_threshold).cast(AMOUNT_TYPE))
        .select(
            "customer_id",
            "account_id",
            "account_type",
            F.col("avg_balance").cast(AMOUNT_TYPE).alias("avg_balance"),
            F.col("peak_balance").cast(AMOUNT_TYPE).alias("peak_balance"),
            F.coalesce(F.col("total_volume"), F.lit(0)).cast(AMOUNT_TYPE).alias("total_volume"),
            F.round(F.col("avg_balance") * F.col("base_rate") / F.lit(12), 2)
                .cast(AMOUNT_TYPE).alias("accrued_interest"),
            rank_in_customer.alias("rank_in_customer"),
        )
    )


def write_report(report: DataFrame, target_table: str, report_month: date) -> None:
    """Zapisuje raport do partycjonowanej tabeli analityk.

    Odpowiednik `INSERT OVERWRITE TABLE ... PARTITION (report_month=...)` z Hive,
    czyli nadpisuje wyłącznie partycję `report_month`, a nie całą tabelę.

    Założenie: tabela docelowa jest już utworzona i partycjonowana po `report_month`.
    `insertInto` używa schematu i partycjonowania z tabeli docelowej, więc nie
    podajemy `partitionBy` na writerze, ani `.mode("overwrite")` (sam argument
    `overwrite=True` w `insertInto` wystarcza).

    Kolejność kolumn w DataFrame MUSI być zgodna ze schemą tabeli, bo `insertInto`
    wpisuje wartości po pozycji, nie po nazwie. Stąd jawne `select` na końcu.
    """
    report.sparkSession.conf.set(
        "spark.sql.sources.partitionOverwriteMode", "dynamic"
    )
    (report
     .select(*TARGET_COLUMNS)
     .withColumn("report_month", F.lit(report_month).cast("date"))
     .write
     .insertInto(target_table, overwrite=True))


if __name__ == "__main__":
    spark = (SparkSession.builder
             .appName("monthly-interest-report")
             .getOrCreate())

    report = build_monthly_interest_report(
        spark=spark,
        accounts=spark.table("raw.accounts"),
        daily_balances=spark.table("raw.daily_balances"),
        transactions=spark.table("raw.transactions"),
        report_month=REPORT_MONTH,
    )
    write_report(report, target_table=TARGET_TABLE, report_month=REPORT_MONTH)
