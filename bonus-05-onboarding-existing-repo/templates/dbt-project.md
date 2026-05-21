# AGENTS.md - <NAZWA_PROJEKTU>

> **Szablon**: dbt project (modele analityczne). Skopiuj do swojego repo jako `AGENTS.md`, wypełnij placeholdery, dostosuj po audycie.

## Kontekst projektu

`<NAZWA_PROJEKTU>` to dbt project budujący warstwę analityczną `<typ: customer 360 | risk scoring | regulacyjny | finansowy>` w `<warehouse: Snowflake | BigQuery | Redshift | Databricks>`. Konsumowany przez `<liczba dashboardów>` dashboardów (Tableau/PowerBI/Looker) i `<liczba downstream jobs>` jobów downstream. Krytyczność: `<eksperymentalne | reporting | regulacyjne (SLA 6h)>`.

## Stack

- **dbt-core** `<1.7+>` lub **dbt Cloud**
- **Warehouse**: `<Snowflake | BigQuery | Redshift | Databricks>`
- **Adapter**: `dbt-<snowflake|bigquery|redshift|databricks>` `<wersja>`
- **dbt-utils** dla wspólnych makr
- **dbt-expectations** dla zaawansowanych testów (opcjonalnie)
- **sqlfluff** + **sqlfmt** dla lint i format SQL
- **Jinja 3** dla makr (oczywiste, bo dbt)
- **Git pre-commit** z hookami: `dbt parse`, `sqlfluff lint`, `sqlfmt format`

## Konwencje globalne

### Język
- **Nazwy tabel i kolumn**: angielski (zgodność z warehouse standards)
- **Opisy w `schema.yml`** (`description:` pola): polski
- **README, docs/**: polski
- **Komentarze SQL**: polski dla logiki biznesowej, angielski dla technicznych decyzji
- **Commit messages**: angielski, conventional commits

### Modele - warstwy
Standardowa hierarchia warstw, NIE łącz odpowiedzialności między warstwami:

- **`staging/`** (`stg_<source>__<table>.sql`): 1-do-1 z `source`, tylko rename kolumn, typowanie, usunięcie wierszy testowych. **Nie ma joinów, nie ma agregacji.**
- **`intermediate/`** (`int_<concept>__<verb>.sql`): logika pomocnicza między staging a marts. Joins, deduplication, type casting domeny. **Nie eksponowana** dla konsumentów.
- **`marts/`** (`dim_<concept>.sql`, `fct_<concept>.sql`): finalne tabele dla konsumentów. Dimensions + facts. Stabilne kontrakty.

### Konwencje SQL
- **CTEs** zamiast subqueries (dbt domyślnie tak rekomenduje)
- **Każdy model zaczyna od listy `with` CTE'ek**, jednej dla każdego źródła:
  ```sql
  with
  source as (select * from {{ ref('stg_accounts') }}),
  transactions as (select * from {{ ref('stg_transactions') }}),
  ...
  ```
- **`source()`** tylko w warstwie `staging/`
- **`ref()`** wszędzie indziej
- **Schemy** kolumn opisane w `schema.yml`, każda dimension/fact ma opis pola
- **Typowanie BigDecimal**: `DECIMAL(18, 2)` dla kwot, `DECIMAL(10, 6)` dla stóp (Snowflake/BQ syntax różni się)

### Testy
- **Schema tests** w `schema.yml` dla każdego modelu w `marts/`:
  - `unique` dla primary key
  - `not_null` dla pól wymaganych
  - `relationships` dla foreign keys
  - `accepted_values` dla enumów
- **Custom tests** w `tests/` dla bardziej zaawansowanych reguł (`dbt-expectations`)
- **Singular tests** (jeden konkretny case, np. dystrybucja wartości po dacie) w `tests/singular/`
- **Coverage**: każdy `marts/` model ma minimum 4 testy schemy + 1 singular

### Dokumentacja
- **`description:`** dla każdego modelu w `schema.yml`
- **`description:`** dla każdej kolumny w `schema.yml`
- **`docs/`** dla doc blocków (`{% docs %} ... {% enddocs %}`) opisujących domenę biznesową
- **`dbt docs generate`** część CI, hostowany jako site dla zespołu

### Incremental models
- `materialized: incremental` dla dużych tabel (>10M wierszy)
- `unique_key` zdefiniowany
- `incremental_strategy: <merge | delete+insert | append>` w zależności od warehouse
- `is_incremental()` w SQL dla logiki "tylko nowe partycje"

## Style kodu i naming

### Naming modeli
- `stg_<source>__<table>` - staging
- `int_<domain>__<action>` - intermediate (np. `int_customers__deduplicated`)
- `dim_<concept>` - dimension (np. `dim_customers`)
- `fct_<concept>` - fact (np. `fct_loan_payments`)
- `agg_<concept>__<grain>` - aggregated (np. `agg_revenue__monthly`)

### Naming kolumn
- `id` dla primary key, `<entity>_id` dla foreign keys (`customer_id`, `account_id`)
- `_at` sufix dla timestampów (`created_at`, `updated_at`)
- `_date` sufix dla dat (`opened_date`)
- `is_<x>` prefix dla booleans (`is_active`, `is_premium`)
- `<x>_<unit>` dla kwot z jednostką (`balance_pln`, `volume_eur`)

### SQL formatting
- **Lowercase keywords** (`select`, `from`, `where`) w nowym kodzie (legacy z uppercase zostawiamy do refaktoru)
- **Trailing commas** na końcu linii (dbt convention)
- **One column per line** w SELECT
- **80-100 chars line limit**

## Struktura katalogów

```
<dbt_project>/
├── dbt_project.yml
├── profiles.yml (gitignored, .example commited)
├── models/
│   ├── staging/
│   │   ├── <source>/
│   │   │   ├── _<source>__sources.yml
│   │   │   ├── _<source>__models.yml
│   │   │   ├── stg_<source>__<table>.sql
│   │   │   └── ...
│   ├── intermediate/
│   │   └── int_<domain>__<action>.sql
│   └── marts/
│       ├── <domain>/
│       │   ├── _<domain>__models.yml
│       │   ├── dim_<concept>.sql
│       │   └── fct_<concept>.sql
├── tests/
│   ├── generic/    # custom generic tests
│   └── singular/   # one-off tests
├── macros/
├── docs/
└── seeds/          # CSV static data
```

## Czego unikać

- ❌ Joiny w warstwie `staging/` (staging robi 1-do-1 z source)
- ❌ `select *` w produkcyjnych modelach (jawnie wymień kolumny)
- ❌ `current_timestamp()` w transformacjach (nieidempotentność)
- ❌ Hardkodowane nazwy tabel (zawsze `{{ ref('...') }}` lub `{{ source('...', '...') }}`)
- ❌ Logika biznesowa w staging (tylko rename i type cast)
- ❌ Brak testu `unique` na primary key w `marts/`
- ❌ Inkrementalność bez `unique_key`
- ❌ Modele z 500+ linii bez podziału na CTE
- ❌ PII w `seeds/` (CSV testowe)
- ❌ `dbt run --full-refresh` na produkcji bez review

## Zasady dla agentów modyfikujących

1. **Każdy nowy model w `marts/` ma minimum 4 testy schemy** w `schema.yml`
2. **Każda kolumna w `marts/` ma `description`** w `schema.yml`
3. **Każda zmiana primary key**: ticket migracyjny dla konsumentów (dashboards, downstream)
4. **Każda zmiana incremental_strategy**: testowanie pełnego refresh w staging
5. **Commit messages** w formacie conventional commits z `(<warstwa>)` scope: `feat(marts): add fct_loan_originations`
6. **PR template**: lista zmodyfikowanych modeli, lista zmienionych kolumn, link do dashboardów które mogą się zepsuć

## Hierarchia plików instrukcji

1. User prompt files (VS Code Settings, `chat.instructionsFilesLocations`)
2. `AGENTS.md` (root) ← ten plik
3. `.github/instructions/sql.instructions.md` - jeśli istnieje, dodatki dla plików .sql
4. `models/<warstwa>/AGENTS.md` - jeśli warstwa ma odmienne konwencje (rzadkie)

---

> **TODO przed użyciem szablonu**: usuń tę sekcję po wypełnieniu placeholderów `<...>`. Wybierz jeden warehouse i usuń odniesienia do innych.
