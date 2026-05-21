# GitHub Copilot Instructions - Workshop Repo

Ten plik konfiguruje zachowanie GitHub Copilota dla całego repozytorium. Skrót kluczowych konwencji; pełna wersja w `AGENTS.md` (root) oraz w plikach dla poszczególnych katalogów.

## Kontekst

Repo Java z legacy kodem kalkulacji odsetek bankowych do refaktoru, plus 5 katalogów bonusowych. Domena: polski bank (PLN, podatek Belka 19%, IBAN PL).

## Kluczowe konwencje

### Java
- Java 17 features (records, switch expressions, pattern matching, var, text blocks, sealed types)
- Maven build system
- JUnit 5 + AssertJ dla testów

### Finanse
- `java.math.BigDecimal` wszędzie dla kwot, stawek, procentów
- NIGDY `double` lub `float` dla pieniędzy
- Scale 2 dla kwot, 6 dla stawek, `RoundingMode.HALF_UP`
- Stałe nazwane zamiast magic numbers (`BELKA_TAX_RATE`, etc.)

### Naming
- Brak jednoliterowych zmiennych (poza loop indices)
- Pełne angielskie słowa: `principal`, `interestRate`, `accountType`
- Metody: czasowniki (`calculateInterest`, `validateInput`)
- Stałe: `UPPER_SNAKE_CASE`

### Walidacja
- Każda metoda publiczna waliduje argumenty
- `null` → `NullPointerException` z opisem
- Out-of-range → `IllegalArgumentException` z komunikatem PL

### Logging
- SLF4J (`org.slf4j.Logger`), NIGDY `System.out.println`
- Bez PII w logach (PESEL, numery kont)

### Dokumentacja
- JavaDoc po polsku dla metod publicznych (kod dla polskiego banku)
- `@param`, `@return`, `@throws` opisane

### Testy
- Naming: `should_<oczekiwany>_when_<warunek>()`
- AssertJ fluent API: `assertThat(x).isEqualByComparingTo("1.00")`
- Pokrycie: happy path + edge cases + invalid inputs
- Bez Mockito w warstwie domain

## Instrukcje szczegółowe

### Generowanie kodu refaktoryzującego
- Najpierw zaproponuj plan
- Wykonuj zmiany krok po kroku z możliwością akceptacji
- Po każdej zmianie sugeruj `mvn test`

### Generowanie testów
- `@workspace` jako kontekst (żeby Copilot widział `pom.xml`)
- Pokryj happy path plus minimum 3 przypadki brzegowe
- Użyj testów parametryzowanych dla podobnych scenariuszy

### Code review
- Skup się na: precyzji BigDecimal, wyciekach PII, walidacji, brakujących testach
- Format: BLOCKER / WARNING / SUGGESTION

## Czego unikać

- ❌ `double` dla kwot finansowych
- ❌ Magic numbers i magic strings w kodzie
- ❌ Jednoliterowe zmienne (poza `i`, `j` w pętlach)
- ❌ `System.out.println` zamiast SLF4J
- ❌ PII w logach lub w danych testowych
- ❌ JUnit 4 (`org.junit.Test`); używamy JUnit 5
- ❌ Klasyczne POJO, gdy można użyć `record`
- ❌ `if (obj instanceof X) { X x = (X) obj; ... }`; pattern matching jest dostępne w Java 17
