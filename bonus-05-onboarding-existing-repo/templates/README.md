# 5 szablonów AGENTS.md dla typowych projektów

Każdy szablon to **punkt startu**, nie kopia. Wybierz najbliższy do twojego projektu, skopiuj, **personalizuj na podstawie audytu** (Krok 1 playbooka).

## Mapowanie projektów ING na szablony

| Typ projektu w ING | Szablon | Stack |
|---|---|---|
| Microserwis REST (np. account-api, loan-api) | `spring-boot-rest.md` | Spring Boot 3, Java 17, PostgreSQL, JPA |
| Job ETL nocny (np. balance-aggregator, settlement-report) | `batch-etl-spark.md` | Spark, Python lub Scala, Airflow, Hive/S3 |
| Biblioteka kalkulacyjna (np. interest-calc, tax-calc) | `java-library.md` | Java 17, Maven, JUnit 5, bez Springa |
| Modele analityczne (np. customer-360, risk-scoring) | `dbt-project.md` | dbt-core, Snowflake/BigQuery, Jinja |
| Wewnętrzny dashboard (np. ops-console, admin-panel) | `frontend-react.md` | React 18, TypeScript, Vite, TanStack Query |

## Co każdy szablon zawiera

Spójny format dla wszystkich 5:

1. **Kontekst projektu** (placeholder do wypełnienia)
2. **Stack** (konkretne wersje i biblioteki)
3. **Konwencje globalne** (5-8 reguł)
4. **Style kodu i naming** (5-7 reguł)
5. **Struktura katalogów** (jak by-feature albo by-layer)
6. **Czego unikać** (5-8 anti-patterns)
7. **Zasady dla agentów modyfikujących**
8. **Hierarchia plików instrukcji**

## Jak używać

```bash
# 1. Skopiuj szablon do swojego repo
cp templates/spring-boot-rest.md ~/projects/my-service/AGENTS.md

# 2. Otwórz w VS Code w swoim repo
cd ~/projects/my-service && code AGENTS.md

# 3. Wykonaj audit (Krok 1 playbooka), żeby zebrać fakty
# 4. Personalizuj AGENTS.md, wstaw konkretne wartości w miejscach <placeholder>
# 5. Iteracja z Copilotem (Krok 2.3 playbooka)
# 6. Walidacja w parze (Krok 3 playbooka)
# 7. Commit + push
```

## Co jeśli żaden szablon nie pasuje

Najczęstsze sytuacje:

- **Monorepo z 5 modułami w różnych stosach**: zacznij od top-level AGENTS.md z **wspólnymi** konwencjami (commit messages, code review process). Per-moduł AGENTS.md dla różnych stosów (zob. prompt 2.5 w `prompts.md`).
- **Projekt łączący backend Java i frontend React**: dwa szablony, dwa AGENTS.md. Top-level wspólne, `frontend/AGENTS.md` dla React, `backend/AGENTS.md` dla Spring Boot.
- **Stack, którego nie ma na liście** (Kotlin? Quarkus? FastAPI? Go?): weź najbliższy szablon (Spring Boot dla każdego REST backend) i adaptuj. Po wykonaniu - rozważ dodanie nowego szablonu do tego katalogu i wysłanie PR do warsztatowego repo.
- **Projekt jest tak specyficzny, że szablon przeszkadza**: zaczynaj od czystej kartki, ale trzymaj się sekcji 1-8 z listy "Co każdy szablon zawiera" powyżej.

## Co NIE robić

- ❌ **Skopiować szablon bez personalizacji**. AGENTS.md, który wygląda identycznie jak szablon, to znak, że nie wykonałeś Kroku 1 audytu. Skutki: Copilot dostaje generyczne zalecenia zamiast specyficznych, jakość spada.
- ❌ **Dodać szablon do **wszystkich** repo bez wybiórczości**. Microserwis REST i biblioteka domain mają zupełnie inne konwencje. Wymuszanie jednego AGENTS.md = wszystkie projekty są generyczne.
- ❌ **Pisać AGENTS.md za zespół, bez ich wiedzy**. AGENTS.md jest **kontraktem zespołu**. Jeśli wpiszesz "BigDecimal wszędzie", a zespół tego nie wie, Copilot będzie wymuszał, a developerzy odrzucać PR. Walidacja w parze (Krok 3) jest niezbędna.

## Wniosek po pierwszych 5 użyciach

Po użyciu szablonów na 5 swoich repo:

1. Zauważasz **wzorce** specyficzne dla swojej organizacji
2. Wzorce te NIE są w generycznych szablonach
3. Stwórz **własny szablon** w katalogu `~/.copilot/templates/agents-md/`
4. Po 10 użyciach: stwórz skill `agents-md-generator` (zob. prompt 4.3 w `prompts.md`)

To jest dokładnie ścieżka adopcji, którą Tribe ING chce zbudować w 2026: od ad-hoc używania Copilota do **skillu organizacyjnego**, który skaluje jakość kodu na całą organizację.
