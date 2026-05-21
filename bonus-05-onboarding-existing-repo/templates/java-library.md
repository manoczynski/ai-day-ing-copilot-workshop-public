# AGENTS.md - <NAZWA_PROJEKTU>

> **Szablon**: Pure domain library w Java (bez Springa, bez frameworków webowych). Skopiuj do swojego repo jako `AGENTS.md`, wypełnij placeholdery, dostosuj po audycie.

## Kontekst projektu

`<NAZWA_PROJEKTU>` to biblioteka domain `<typ: kalkulacja | walidacja | reguły biznesowe | parser | konwerter>` w domenie `<domena: odsetki | podatki | walidatory | inne>`. Konsumowana przez `<liczba aplikacji>` microserwisów i `<liczba jobs>` jobów ETL. Filozofia: **API stabilne, semantic versioning, brak zewnętrznych zależności frameworkowych**.

## Stack

- **Java 17** (NIE 8, NIE 11)
- **Maven** (NIE Gradle)
- **JUnit 5** + **AssertJ** dla testów
- **jqwik** dla property-based testing (opcjonalnie, dla bibliotek matematycznych)
- **JaCoCo** dla pokrycia (próg: 90% line, 85% branch)
- **PIT** (mutation testing) dla bibliotek krytycznych (próg: 70% mutation coverage)
- **Maven Central** jako miejsce dystrybucji (lub wewnętrzny Nexus/Artifactory)

### Czego NIE używamy

- ❌ **Spring** (to ma być pure Java, używalne też z Quarkus, Micronaut, Vert.x)
- ❌ **Lombok** (modern Java ma `record`, nie potrzebujemy)
- ❌ **JPA / Hibernate** (to library domain, nie persistence)
- ❌ **SLF4J / Logback** w `compile` scope (logging robi consumer, nie library)
- ❌ Logiczne zależności na inne libraries z naszego ekosystemu (chyba że to core util)

## Konwencje globalne

### Język
- **Identyfikatory**: angielski
- **JavaDoc**: polski (dokumentacja dla zespołu PL)
- **Komunikaty wyjątków**: polski (czyta je deweloper consumer aplikacji)
- **Commit messages**: angielski, conventional commits

### Typy danych dla finansów
- **BigDecimal** dla kwot, scale 2, `RoundingMode.HALF_UP`
- **BigDecimal** dla stóp, scale 6
- NIGDY `double` ani `float`
- `BigDecimal.compareTo` dla porównań
- `BigDecimal.divide` z `RoundingMode`

### Immutability i bezpieczeństwo wątkowe
- **Wszystkie publiczne klasy są niemutowalne** (immutable) i thread-safe
- `record` dla data containers, `final class` z `final` polami dla pozostałych
- Brak mutable state (brak `private int counter` modyfikowanego w metodach)
- Wartości zwracane są obronnymi kopiami (defensive copies) dla kolekcji
- Sealed interfaces dla closed hierarchies (np. `sealed interface ValidationResult permits Valid, Invalid`)

### API publiczne
- Każda klasa publiczna jest **świadomą decyzją API**
- Pola pakietowe nie wystawiamy jako public
- Klasy implementacyjne w pakiecie `internal` (Java module system albo konwencja)
- `@Deprecated` z `since` i `forRemoval` dla zmiany API
- Każda zmiana breaking = major version (semver)

### Walidacja inputów
- Każda publiczna metoda waliduje argumenty na wejściu
- `Objects.requireNonNull` dla null
- `IllegalArgumentException` dla wartości poza zakresem, komunikat po polsku z wartością
- Walidacja PRZED logiką biznesową, nie pośrodku
- Brak `@NotNull`, `@Valid` (to dla Springa/Jakarta Validation, nie pure Java)

### Testy
- Naming: `should_<oczekiwany>_when_<warunek>()`
- AssertJ (`assertThat(x).isEqualByComparingTo("100.00")`)
- Pokrycie: **90% line coverage, 85% branch coverage** (próg JaCoCo)
- Property-based testy (jqwik) dla bibliotek matematycznych
- Brak `Mockito.mock` na value objectach (BigDecimal, LocalDate, String)
- Brak Mockito w warstwie domain (czyste funkcje, można testować w izolacji)
- Cross-check tests z znanymi wartościami (np. z arkusza księgowej)

### Dokumentacja
- JavaDoc po polsku dla **każdej** publicznej klasy i metody
- `@param`, `@return`, `@throws` opisane konkretnie
- Przykłady użycia w JavaDoc dla głównych klas (sekcja `<pre>` lub `{@snippet}`)
- README zawiera: cel, wersjonowanie, przykład użycia, dependencies, kontakt

## Style kodu i naming

- **Pakietowanie**: by-domain (`com.ing.<library>.<subdomain>`)
- **Klasy publiczne** w głównym pakiecie, klasy wewnętrzne w `internal/`
- **Records** wszędzie, gdzie data container
- **Sealed types** dla wynikowych enumów (np. `Result.Success | Result.Failure`)
- **Pattern matching** dla `instanceof`
- **`var`** dla lokalnych zmiennych z oczywistym typem
- Switch expressions zamiast switch statements
- Brak getterów/setterów na recordach (dostęp przez `record.field()`)
- Brak `class XBuilder` w nowych klasach (records mają canonical constructor)

## Struktura katalogów

```
src/main/java/com/ing/<library>/
├── <NazwaKalkulatora>.java        # główna klasa publiczna
├── <Parametr>.java                # record dla input parameters
├── <Wynik>.java                   # record dla output
└── internal/                      # implementacja wewnętrzna
    ├── <Helper>.java
    └── ...

src/test/java/com/ing/<library>/
├── <NazwaKalkulatora>Test.java
├── property/                      # jqwik property-based tests
│   └── <NazwaKalkulatora>PropertyTest.java
└── crosscheck/                    # testy zgodności z arkuszem księgowej
    └── ExcelCrossCheckTest.java
```

## Czego unikać

- ❌ `double` lub `float` dla finansów
- ❌ Mutable state w publicznych klasach
- ❌ Wystawianie klas implementacyjnych jako public
- ❌ Spring, Lombok, JPA, SLF4J (compile scope) w `dependencies`
- ❌ Logowanie do `System.out`/`System.err` (biblioteka nie loguje)
- ❌ Wyjątki typu `RuntimeException` ogólnego (zawsze konkretny, np. `IllegalArgumentException`)
- ❌ `null` jako wynik metody (użyj `Optional` albo sealed result)
- ❌ Magic numbers (stałe nazwane z opisem)
- ❌ Breaking changes bez migracji w major version
- ❌ Konstruktory z 10 parametrami (rozbij na value objects albo builder, jeśli naprawdę trzeba)

## Zasady dla agentów modyfikujących

1. **Trzymaj się hierarchii AGENTS.md**
2. **Każda zmiana publicznego API**: deprecation + major version bump, ticket migracyjny w README
3. **Każda nowa metoda publiczna**: JavaDoc PL + minimum 3 testy (happy + edge + invalid)
4. **Każda zmiana algorytmu**: cross-check test z arkusza księgowej
5. **Commit messages** w formacie conventional commits
6. **PR template**: link do CHANGELOG.md, sekcja "Breaking changes", checkbox "Semver bump correct"

## Hierarchia plików instrukcji

1. User prompt files (VS Code Settings, `chat.instructionsFilesLocations`) - globalne preferencje
2. `AGENTS.md` (root) ← ten plik
3. `.github/instructions/java.instructions.md` - jeśli istnieje, dodatki dla plików .java

---

> **TODO przed użyciem szablonu**: usuń tę sekcję po wypełnieniu placeholderów `<...>`.
