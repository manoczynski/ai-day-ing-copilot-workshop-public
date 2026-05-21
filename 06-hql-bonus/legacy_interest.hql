-- =========================================================================
-- Monthly interest accrual + top customer ranking
-- Legacy ETL job (Hive QL) - kandydat do migracji na PySpark.
-- Tabele wejściowe:
--   raw.accounts            (account_id STRING, customer_id STRING, account_type STRING,
--                            opened_at DATE, closed_at DATE, base_rate DECIMAL(10,6))
--   raw.daily_balances      (account_id STRING, balance_date DATE, balance DECIMAL(18,2))
--   raw.transactions        (account_id STRING, tx_date DATE, tx_type STRING, amount DECIMAL(18,2))
-- Tabela docelowa:
--   analytics.monthly_interest_report
--     PARTITIONED BY (report_month DATE)
-- =========================================================================

SET hivevar:report_month = '2026-04-01';
SET hivevar:min_activity_threshold = 1000.00;

-- Hive wymaga, by CTE (WITH) były ZA `SET ...` i PRZED `INSERT OVERWRITE`.
-- Konwersja stringa hivevar na DATE jawnie, żeby Hive nie polegał na implicit cast.
WITH active_accounts AS (
    SELECT account_id, customer_id, account_type, base_rate
    FROM raw.accounts
    WHERE opened_at <= CAST('${hivevar:report_month}' AS DATE)
      AND (closed_at IS NULL OR closed_at > CAST('${hivevar:report_month}' AS DATE))
),
monthly_balances AS (
    SELECT b.account_id,
           AVG(b.balance) AS avg_balance,
           MAX(b.balance) AS peak_balance
    FROM raw.daily_balances b
    WHERE b.balance_date BETWEEN CAST('${hivevar:report_month}' AS DATE)
                              AND LAST_DAY(CAST('${hivevar:report_month}' AS DATE))
    GROUP BY b.account_id
),
monthly_activity AS (
    SELECT account_id, SUM(amount) AS total_volume
    FROM raw.transactions
    WHERE tx_date BETWEEN CAST('${hivevar:report_month}' AS DATE)
                       AND LAST_DAY(CAST('${hivevar:report_month}' AS DATE))
    GROUP BY account_id
)
INSERT OVERWRITE TABLE analytics.monthly_interest_report
    PARTITION (report_month = DATE '${hivevar:report_month}')
SELECT a.customer_id,
       a.account_id,
       a.account_type,
       mb.avg_balance,
       mb.peak_balance,
       COALESCE(ma.total_volume, 0) AS total_volume,
       ROUND(mb.avg_balance * a.base_rate / 12, 2) AS accrued_interest,
       ROW_NUMBER() OVER (PARTITION BY a.customer_id ORDER BY mb.avg_balance DESC) AS rank_in_customer
FROM active_accounts a
JOIN monthly_balances mb ON a.account_id = mb.account_id
LEFT JOIN monthly_activity ma ON a.account_id = ma.account_id
WHERE mb.avg_balance >= ${hivevar:min_activity_threshold};
