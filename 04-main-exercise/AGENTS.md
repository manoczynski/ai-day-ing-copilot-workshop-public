# AGENTS.md - 04-main-exercise

Konwencje dla głównego ćwiczenia. Uszczegóławia [root `AGENTS.md`](../AGENTS.md).


## Wymagania po refaktorze `CalculateInterest.java`

### Struktura klas

Po refaktorze powstają osobne klasy z jasno rozdzielonymi odpowiedzialnościami:

- **enum** `AccountType` z wartościami `REGULAR`, `SAVINGS`, `PREMIUM` (zamiast magic stringów `"R"`, `"S"`, `"P"`)
- **record** `InterestCalculationResult(BigDecimal gross, BigDecimal tax, BigDecimal fee, BigDecimal net)`
- `InterestCalculator` - obliczanie odsetek brutto
- `BelkaTaxCalculator` - podatek od zysków kapitałowych (19%)
- `AccountFeePolicy` - opłaty progowe (50/100 tys.)
- Klasa orkiestrująca (np. `AccountInterestService`) łączy powyższe

### Typy danych

- `BigDecimal` wszędzie, NIGDY `double` ani `float`
- Scale **2** dla kwot, **6** dla stawek
- `RoundingMode.HALF_UP`

### Stałe nazwane

```java
public static final BigDecimal BELKA_TAX_RATE          = new BigDecimal("0.19");
public static final BigDecimal LARGE_ACCOUNT_THRESHOLD = new BigDecimal("100000");
public static final BigDecimal MEDIUM_ACCOUNT_THRESHOLD = new BigDecimal("50000");
public static final BigDecimal LARGE_ACCOUNT_FEE       = new BigDecimal("50.00");
public static final BigDecimal MEDIUM_ACCOUNT_FEE      = new BigDecimal("20.00");
```

Stawki bazowe per `AccountType` w enumie.

### Walidacja

- `Objects.requireNonNull(principal, "principal cannot be null")`
- `principal.signum() < 0` → `IllegalArgumentException` (komunikat po polsku)
- `years < 0` → `IllegalArgumentException`

### Testy

- JUnit 5 + AssertJ
- Happy path dla każdego `AccountType`
- Przypadki brzegowe: `principal = 0`, `years = 0`, progi `MEDIUM_ACCOUNT_THRESHOLD` i `LARGE_ACCOUNT_THRESHOLD` od dołu i od góry
- Invalid inputs: null, ujemne wartości
- Naming: `should_<oczekiwany>_when_<warunek>()`
- Bez Mockito (warstwa domain pure)

### Dokumentacja

- JavaDoc po polsku dla każdej metody publicznej
- `@param`, `@return`, `@throws` opisane konkretnie

## Czego NIE robić w refaktorze

- Nie wprowadzaj Springa, JPA ani warstwy persistence. To jest pure domain.
- Nie dodawaj Lomboka. Używamy nowoczesnej Javy (records, sealed).
- Nie zmieniaj API publicznego po cichu. Każda zmiana sygnatury w PR description w sekcji "Breaking change".
- Nie generuj testów z `Mockito` w warstwie domain.

## Powiązane pliki

- `EXERCISE_STEPS.md` - 5 kroków scenariusza dla uczestnika
- `reference/CalculateInterest_REFACTORED.md` - wzorzec po refaktorze (zaglądnij PO ćwiczeniu)
