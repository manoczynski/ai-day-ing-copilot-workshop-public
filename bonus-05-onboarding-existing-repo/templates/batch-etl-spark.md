# AGENTS.md - <NAZWA_PROJEKTU>

> **Szablon**: Batch ETL job z użyciem Sparka (PySpark lub Scala). Skopiuj do swojego repo jako `AGENTS.md`, wypełnij placeholdery, dostosuj po audycie.

## Kontekst projektu

`<NAZWA_PROJEKTU>` to zadanie ETL `<rodzaj: nocne batch | godzinowe | event-driven>` przetwarzające dane `<źródło: Hive/S3/PostgreSQL>` w celu wyprodukowania `<wynik: tabele analityczne | raporty regulacyjne | feed dla downstream>`. Krytyczność: `<eksperymentalne | reporting | regulacyjne (SLA <godziny>)>`.

## Stack

- **PySpark** `<3.x>` lub **Spark Scala** `<3.x>` (wybierz jeden)
- **Python** `<3.10+>` jeśli PySpark, **Scala 2.13** + **sbt** jeśli Spark Scala
- **Airflow** `<2.x>` dla orchestracji
- **Hive** + **S3** (lub **HDFS**) jako warstwa storage
- **dbt** dla warstwy analitycznej (opcjonalnie, częste w hybrydach)
- **pytest** + **chispa** dla PySpark; **ScalaTest** + **spark-fast-tests** dla Scala
- **mypy** + **ruff** + **black** dla PySpark; **scalafmt** dla Scala
- **Great Expectations** lub **dbt tests** dla data quality

## Konwencje globalne

### Język
- **Identyfikatory** (zmienne, funkcje, klasy): angielski
- **Docstringi, README**: polski (bo dokumentacja dla zespołu PL)
- **Logi**: angielski
- **Commit messages**: angielski, conventional commits

### Typy danych dla finansów
- **`DecimalType(18, 2)`** dla kwot, **`DecimalType(10, 6)`** dla stóp
- NIGDY `DoubleType` ani `FloatType` dla pieniędzy
- `F.round(col, 2)` zawsze przed castem na `DecimalType(18, 2)` (cast NIE zaokrągla, tylko ucina)
- Stałe nazwane w module `constants.py` (lub `Constants.scala`), NIE magic numbers w kodzie

### DataFrame API > spark.sql
- Używamy **DataFrame API** dla większości operacji (czytelność, type safety w Scali)
- `spark.sql(string)` tylko gdy:
  - migracja z legacy HQL
  - wymagana składnia, której DataFrame API nie wspiera (rare)
- Multi-line SQL w plikach `.sql` (nie inline w kodzie Python/Scala)

### Idempotentność i deterministyczność
- Każdy job MUSI być **idempotentny**: wielokrotne uruchomienie dla tej samej partycji daje ten sam wynik
- Brak `current_timestamp()`, `random()`, `uuid()` w transformacjach (nie deterministyczne)
- Jeśli potrzebny timestamp: parametr `run_date` przekazywany z Airflow
- `INSERT OVERWRITE PARTITION (date=<run_date>)` zamiast `INSERT INTO`
- `spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")` dla dynamicznych partycji

### Testy
- Naming: `test_<oczekiwany>_when_<warunek>()` (Python) lub `should "<oczekiwany>" when "<warunek>"` (Scala)
- Fixtury jako małe DataFrame builders (`spark.createDataFrame([...], schema)`)
- Brak realistycznych PII w fixturach
- Cross-check z **arkuszem księgowej** dla regulacyjnych raportów (minimum 10 wierszy)
- Testy schematu output (kolumny, typy, nullability)

### Logowanie
- Python: `logging` standardowy, formatter z trace ID
- Scala: SLF4J + Logback
- Brak PII w logach
- DataFrame nie loguje się jako `df.show()` w produkcji (tylko w debug)

### Dokumentacja
- Docstringi Python: format Google albo NumPy (wybierz jeden i trzymaj się)
- ScalaDoc dla Scala
- Każda funkcja transformująca: opis WEJŚCIA (schemat), WYJŚCIA (schemat), REGUŁY (1-2 zdania)
- README projektu opisuje: cel jobu, schedule, dependencies, SLA, ownership

## Style kodu i naming

- **PySpark zmienne**: `snake_case`. Scala: `camelCase`
- **DataFrames**: nazwij po treści, nie po typie. `active_accounts` lepsze niż `df1` lub `accountsDf`
- **Window functions**: zawsze przez `Window.partitionBy().orderBy()`, NIGDY raw SQL
- **Joins**: jawnie podaj `how="inner|left|right|outer"`, NIE polegaj na domyślnym
- **Column references**: `F.col("name")` zamiast `df["name"]` (lepsze dla optymalizatora i typu)
- **Aliases**: `df.alias("a").join(...).select("a.col1", ...)` dla join'ów z konfliktami nazw

## Struktura katalogów

```
src/
├── jobs/             # entry points dla każdego jobu
│   └── <job_name>.py
├── transformations/  # czyste funkcje DataFrame → DataFrame
├── readers/          # czytanie z Hive/S3/JDBC
├── writers/          # zapis do Hive/S3/JDBC
├── schemas/          # schematy StructType dla wejścia/wyjścia
└── common/           # config, logging, utils

tests/
├── unit/             # testy małych transformacji
├── integration/      # testy całych jobs z fixturami Hive
└── data/             # CSV/JSON fixtury

dags/                 # Airflow DAG-i (jeśli w tym samym repo)

config/
├── dev.yml
├── staging.yml
└── prod.yml
```

## Czego unikać

- ❌ `DoubleType` / `FloatType` dla kwot finansowych
- ❌ `cast(DecimalType(...))` bez wcześniejszego `round` (cast ucina, nie zaokrągla)
- ❌ `df.collect()` w produkcji (OOM ryzyko dla dużych zbiorów)
- ❌ `spark.sql(f"SELECT * FROM {table} WHERE x={user_input}")` (SQL injection w kontekście Sparka)
- ❌ `random()`, `current_timestamp()`, `uuid()` w transformacjach (nieidempotentność)
- ❌ `INSERT INTO` bez warunku idempotentności (lepsze: `INSERT OVERWRITE PARTITION`)
- ❌ `df.show()` w produkcji (loguje dane do stdout, ryzyko PII w logach)
- ❌ Pisanie do Hive bez `partitionBy` na partycjonowanej tabeli
- ❌ Joins bez jawnego `how`
- ❌ Magic strings dla nazw tabel (stałe w `constants.py`)
- ❌ PII w fixturach testowych

## Zasady dla agentów modyfikujących

1. **Każdy nowy job ma SLA w README** (godzina/minuta startu, max czas trwania, ownership oncall)
2. **Każda zmiana schemy output**: backward compatibility check z konsumentami (downstream jobs, dashboards)
3. **Każda zmiana partycjonowania**: migracja historycznych partycji + komunikat dla zespołu data platform
4. **Każdy nowy join**: review pod kątem skewness (broadcast join hint dla małych tabel)
5. **Commit messages** w formacie conventional commits
6. **Performance regression**: jeśli czas joba rośnie >20% w stosunku do baseline, blok PR

## Hierarchia plików instrukcji

1. User prompt files (VS Code Settings, `chat.instructionsFilesLocations`) - globalne preferencje
2. `AGENTS.md` (root) ← ten plik
3. `.github/instructions/python.instructions.md` lub `scala.instructions.md`
4. `<job>/AGENTS.md` - jeśli pojedynczy job ma odmienne konwencje (rzadkie)

---

> **TODO przed użyciem szablonu**: usuń tę sekcję po wypełnieniu placeholderów `<...>`. Wybierz jedną gałąź stosu (PySpark albo Scala) i usuń drugą z każdego punktu.
