# AGENTS.md - konwencje repo

Plik dla Copilota, Claude i Cursor pracujących w tym repo. Opisuje, jakie konwencje kodu obowiązują i jak nazywać commity. Każdy podkatalog może mieć swój `AGENTS.md` z dokładniejszymi regułami (np. wymagania per ćwiczenie).

## Stack

- **Java 17** (NIE 8, NIE 11)
- **Maven** (NIE Gradle)
- **JUnit 5 + AssertJ** (NIE JUnit 4, NIE Hamcrest)
- **SLF4J** dla logowania (NIGDY `System.out.println`)

## Konwencje kodu

### Język
- **Identyfikatory** (klasy, metody, zmienne): angielski
- **JavaDoc, komentarze, dokumentacja**: polski (kod dla polskiego banku)
- **Wiadomości commitów**: angielski, w stylu conventional commits

### Typy dla finansów
- `BigDecimal` dla każdej kwoty pieniężnej, NIGDY `double` ani `float`
- Scale **2** dla kwot, **6** dla stawek procentowych
- `RoundingMode.HALF_UP`
- Porównania przez `compareTo`, nigdy `equals` (bo `1.0` ≠ `1.00`)
- `BigDecimal.divide` zawsze z drugim/trzecim argumentem (`scale`, `RoundingMode`)

### Walidacja metod publicznych
- `Objects.requireNonNull` dla obiektów
- Wartości poza zakresem → `IllegalArgumentException` z komunikatem po polsku
- Walidacja PRZED logiką biznesową, nie pośrodku

### Testy
- Naming: `should_<oczekiwany>_when_<warunek>()`
- AssertJ fluent API (`assertThat(x).isEqualByComparingTo("100.00")`)
- Brak `Mockito.mock` na value objectach (`BigDecimal`, `LocalDate`)
- Pokrycie: happy path plus minimum 3 przypadki brzegowe plus invalid inputs
- Fixtury bez realistycznych PII (PESEL `00000000000`, IBAN `PL00 0000…`)

### Modern Java
- `record` dla data containerów (DTO, value object)
- Switch expressions zamiast switch statements, gdzie czytelnie
- Pattern matching dla `instanceof` (`if (obj instanceof String s)`)
- `var` dla lokalnych zmiennych z oczywistym typem
- Sealed types dla zamkniętych hierarchii

## Czego unikać

- ❌ `double` lub `float` dla kwot finansowych
- ❌ Magic numbers / magic strings (stałe nazwane, np. `BELKA_TAX_RATE`)
- ❌ Jednoliterowe zmienne (poza `i`, `j` w pętlach)
- ❌ `System.out.println` zamiast SLF4J
- ❌ PII w logach lub w fixturach testowych
- ❌ JUnit 4 (`org.junit.Test`); używamy JUnit 5 (`org.junit.jupiter.api.Test`)
- ❌ Klasyczne POJO, gdy można użyć `record`
- ❌ `if (obj instanceof X) { X x = (X) obj; ... }` (Java 17 ma pattern matching)

## Commit messages

Format conventional commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`. Po angielsku, w trybie rozkazującym.

Przykłady:
- `feat(loan): add monthly schedule calculation`
- `fix(interest): correct rounding for last installment`
- `refactor(account): extract AccountFeePolicy`

## Hierarchia plików instrukcji

Kolejność precedencji (od ogólnej do najbardziej specyficznej):

1. User prompt files (VS Code Settings) - twoje globalne preferencje, poza repo. Konfigurowane przez `chat.instructionsFilesLocations`, domyślnie m.in. `~/.copilot/instructions/*.instructions.md`. Sync między urządzeniami przez VS Code Settings Sync. **Uwaga:** plik `~/.copilot/copilot-instructions.md` w katalogu domowym jest oddzielnym mechanizmem dla Copilot CLI, nie dla VS Code.
2. `AGENTS.md` (root) ← ten plik
3. `.github/copilot-instructions.md` - dodatki dla tego repo
4. `.github/instructions/*.instructions.md` - dla wybranych typów plików (np. tylko `.java`)
5. `04-main-exercise/AGENTS.md` - dla konkretnego katalogu
6. Inline directives w prompcie - tu i teraz

Im niżej w hierarchii, tym wyższy priorytet.

