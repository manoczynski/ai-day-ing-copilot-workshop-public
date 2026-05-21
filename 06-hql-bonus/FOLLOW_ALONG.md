# Blok 6 - Follow-along: Migracja HQL → PySpark (10 min na własnym laptopie)

Blok 6 (80:00-85:00) to **demo prowadzącego dla zespołu data, nie hands-on**. Patrzysz na ekran, nie klikasz w trakcie. Ten plik jest na potem - kompletny scenariusz migracji HQL na PySpark z Plan Mode, do powtórzenia po warsztacie albo w przerwie lunchowej.

**Dla kogo:** data engineer / data analityk pracujący z legacy ETL (HQL, SAS, T-SQL). W ING typowa sytuacja: migracja na Python + Spark, dziesiątki jobów do przepisania.

**Co zrozumiesz po przejściu tego pliku:**

- Jak Copilot rozpoznaje mapowanie HQL → PySpark (CTE → DataFrame, ROW_NUMBER → Window, COALESCE, LATERAL VIEW EXPLODE itp.)
- Dlaczego Plan Mode oszczędza godziny przy migracji - błędy mapowania znajdziesz na poziomie planu, nie po wygenerowaniu 80 linii kodu
- Co dokładnie weryfikujesz jako data engineer po wygenerowaniu kodu (precyzja Decimal, partycjonowanie, semantyka `coalesce`)

## Setup (30 sek)

### 1. Otwórz repo warsztatowe na root w VS Code

W panelu Explorer (lewy pasek) powinieneś widzieć na samej górze folder zawierający `README.md`, `AGENTS.md`, `06-hql-bonus/`, `04-main-exercise/` itd.

❌ Jeśli widzisz tylko zawartość podfolderu (`legacy_interest.hql`, `expected_pyspark.py`) - **otworzyłeś `06-hql-bonus/` jako workspace**. Zamknij i otwórz poziom wyżej (folder zawierający `06-hql-bonus/`).

### 2. Otwórz plik źródłowy

Najszybciej: `Ctrl + P` (Windows/Linux) lub `Cmd + P` (macOS), wpisz `legacy_interest.hql`, Enter.

Pełna ścieżka: `06-hql-bonus/legacy_interest.hql`.

To ok. 50 linii HQL: agregacja sald i odsetek miesięcznych, funkcja okna dla rankingu, filtrowanie aktywnych kont. Realistyczny ETL bankowy.

### 3. Otwórz Copilot Chat

`Ctrl + Shift + I` (Windows/Linux) lub `Cmd + Shift + I` (macOS). Tryb startowy: **Plan** (zmienimy potem na Agent).

## Krok 1 - Plan migracji (3 min)

**Rozpocznij nowy wątek** (przycisk "+" / "New chat"). W rozwijanym menu trybu na górze panelu chatu **wybierz Plan**.

Wklej prompt:

```
#file:legacy_interest.hql

Zmigruj ten HQL do PySpark. Tylko plan, nie pisz jeszcze kodu.

Wymagania:
- Idiomatyczny PySpark (DataFrame API, nie surowe spark.sql z napisem HQL)
- DecimalType(18, 2) dla kwot finansowych, DecimalType(10, 6) dla stóp procentowych (NIGDY DoubleType)
- Funkcje okna przez pyspark.sql.window
- Konfigurowalne parametry (data raportowa, próg aktywności) jako argumenty funkcji
- Adnotacje typów (Python 3.10+)
- Brak ścieżek na sztywno: wejście i wyjście jako parametry
- Pokaż mapowanie HQL → PySpark dla każdej operacji (CTE → DataFrame z cache, ROW_NUMBER OVER → Window, COALESCE itp.)

Plan w punktach.
```

**Co zobaczysz:** plan migracji, zwykle 6-10 kroków z mapowaniem operacji HQL na odpowiedniki w PySparku, z flagami "tu trzeba zdecydować".

**Co konkretnie sprawdzić w planie:**

- [ ] Czy Copilot rozróżnia **`coalesce(col1, col2)`** (NULL handling, odpowiednik HQL COALESCE) od **`coalesce(numPartitions)`** (repartycjonowanie)? To częste źródło bugów.
- [ ] Czy plan wspomina o tym, że agregacje (`F.avg`, `F.sum`) **zachowują pełną precyzję Decimal** i cast na `DecimalType(18,2)` powinien być **tylko na finalnych kolumnach**? Wczesny cast w środku agregacji ucina precyzję - to subtelna rozbieżność vs HQL.
- [ ] Czy plan adresuje `INSERT OVERWRITE PARTITION` → `insertInto(target, overwrite=True)` z `partitionOverwriteMode=dynamic`? Bez tej konfiguracji Spark nadpisuje **całą** tabelę, nie pojedynczą partycję.
- [ ] Czy plan zawiera **walidację schemy** wynikowej (kolejność kolumn musi być zgodna z tabelą docelową, bo `insertInto` wpisuje po pozycji, nie nazwie)?

Jeśli któryś z tych punktów brakuje - dopisz w planie (dalej w trybie Plan):

```
Brakuje w planie: <konkretny punkt>. Dodaj go i wyjaśnij, dlaczego ma znaczenie.
```

## Krok 2 - Wykonanie planu (4 min)

Gdy plan wygląda OK, **przełącz tryb chatu z Plan na Agent** w tym samym rozwijanym menu, w którym wcześniej zmieniłeś na Plan.

W tym samym wątku, w trybie Agent, wpisz:

```
Wykonaj plan. Zapisz wynik do 06-hql-bonus/legacy_interest_migrated.py.
```

**Co zobaczysz:** Copilot pisze plik. ~50-70 linii PySpark.

## Krok 3 - Porównanie z referencją (3 min)

Otwórz `06-hql-bonus/expected_pyspark.py` obok wygenerowanego `legacy_interest_migrated.py` (split view: prawym przyciskiem na tab → **Split Up/Down** albo `Ctrl + \` / `Cmd + \`).

**Co konkretnie porównać:**

| Element | Co sprawdzić |
|---|---|
| Typy danych | Czy obie wersje używają `DecimalType(18, 2)` dla kwot i `DecimalType(10, 6)` dla stóp? Czy nie ma `DoubleType` lub `FloatType`? |
| `COALESCE` | Czy obie używają `F.coalesce(col, F.lit(0))` (NULL handling) - **nie** `coalesce(numPartitions)`? |
| Agregacje | Czy `F.avg`, `F.sum` są **bez cast** w agregacji? (Cast na DecimalType w środku ucina precyzję - subtelna rozbieżność vs HQL) |
| `ROW_NUMBER OVER` | Czy obie używają `Window.partitionBy().orderBy()`? |
| `INSERT OVERWRITE PARTITION` | Czy obie ustawiają `partitionOverwriteMode=dynamic` i używają `insertInto(target, overwrite=True)` z jawnym `partitionBy`? |
| Kolejność kolumn | Czy finalna lista kolumn (`TARGET_COLUMNS` w referencji) jest zgodna ze schemą tabeli docelowej? |

**Spodziewany wynik:** 90-95% kodu się zgadza. Różnice to drobiazgi (nazewnictwo zmiennych, kolejność importów). Reszta to **twoja praca jako data engineera** - weryfikacja semantyki, nie kopiowanie.

Jeśli Copilot popełnił błąd w jednym z punktów powyżej - **świadomie go popraw promptem**:

```
W wygenerowanym kodzie funkcja `coalesce` jest użyta z jednym argumentem
liczbowym - to repartycjonowanie, nie NULL handling. Popraw na
`F.coalesce(F.col("total_volume"), F.lit(0))`.
```

## Krok 4 - Wnioski (30 sek)

> *To nie zastępuje data engineera. Przyspiesza 80% rutynowej pracy migracyjnej, żebyś mógł skupić się na 20%: strojeniu wydajności, partycjonowaniu, przypadkach brzegowych, testach walidacyjnych.*

Ten przepływ pracy można zapakować jako **Copilot Skill** (zobacz `../bonus-02-copilot-skills/.github/skills/`). Skill `sql-to-spark` aktywuje się automatycznie, gdy w workspace pojawi się plik `.hql` albo prompt wspomina migrację HQL. Wtedy nie musisz za każdym razem wpisywać tego samego long-promptu - skill ma go w sobie.

## Najczęstsze problemy

**Copilot użył `coalesce(2)` zamiast `F.coalesce(col, lit(0))`** - klasyczna pułapka. `coalesce(numPartitions)` w PySpark to **repartycjonowanie DataFrame**, a `F.coalesce(col1, col2, ...)` to **NULL handling** (odpowiednik HQL COALESCE). Popraw promptem, ucząc Copilota różnicy.

**Plan jest bardzo szczegółowy, ale brak mapowania `INSERT OVERWRITE PARTITION`** - to subtelność, którą łatwo przeoczyć. Bez `partitionOverwriteMode=dynamic` Spark nadpisuje całą tabelę. Dopytaj wprost: "Jak zamapować INSERT OVERWRITE PARTITION na PySpark zgodnie z konwencją Hive? Pokaż wymaganą konfigurację Spark session."

**Wygenerowany kod ma `cast(AMOUNT_TYPE)` wewnątrz `.agg(F.avg(...).cast(AMOUNT_TYPE))`** - to **subtelny bug**. Cast ucina (nie zaokrągla) precyzję wewnątrz agregacji, prowadząc do rozbieżności wartości wobec HQL. Cast tylko na **finalnych kolumnach** w `.select(...)`. Patrz komentarz w `expected_pyspark.py` linia 73-78.

**Plan Mode blokuje generowanie kodu** - jeśli Copilot w Kroku 2 dalej zwraca tylko plan zamiast pisać plik, nie przełączyłeś trybu na Agent. Sprawdź rozwijane menu trybu na górze panelu chatu - musi być **Agent**.

## Co dalej

- **Zastosuj na własnym joback HQL.** Weź jeden konkretny ETL z twojego repo, powtórz Krok 1-3. Im więcej powtórzeń, tym bardziej Copilot uczy się twoich konwencji (przez Memory).
- **`../bonus-02-copilot-skills/examples/README.md` punkt 1 (`sql-to-spark`)** - jak zapakować ten przepływ jako skill, żeby aktywował się automatycznie.
- **`../bonus-03-copilot-cli/`** - jeśli robisz migrację 30 jobów naraz, terminal + `copilot run` + bash loop skaluje to lepiej niż VS Code Chat.

## Powiązane pliki w repo

- `legacy_interest.hql` - przykładowy ETL HQL (50 linii, realistyczny: agregacje miesięczne, window function, partition by report_month)
- `expected_pyspark.py` - referencyjna migracja (komentarze inline wyjaśniają subtelności precyzji Decimal i `insertInto`)
- `../04-main-exercise/AGENTS.md` - konwencje Java/finansów (analogiczne idee dla Pythona można dodać do swojego repo)
- `../bonus-02-copilot-skills/` - jak zapakować ten workflow jako Copilot Skill

---

## ✅ Blok 6 (HQL bonus) skończony

To był ostatni blok merytoryczny warsztatu. Następnie prowadzący zamyka warsztat (Blok 7 - Wrap-up, 5 min).
