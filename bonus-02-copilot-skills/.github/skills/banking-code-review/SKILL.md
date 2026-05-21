---
name: banking-code-review
description: Use when reviewing Java code for a Polish bank - checks BigDecimal precision, PII leaks, validation, JUnit 5 vs 4, conventional commits, and Java 17 idioms
---

# Banking Code Review

Skill aktywuje się przy code review (`/review`, "przejrzyj zmiany", "review tego PR") plików `.java` w kontekście bankowym.

## When to use

- Code review PRs w repo bankowym
- Pre-merge check przed mergem do `main` / `develop`
- Self-review po skończeniu feature'a
- Drugi pass po wygenerowaniu kodu przez Copilota

## When NOT to use

- Code review kodu generycznego (nie bankowy domain - wtedy general code review)
- Code review pliku innego niż `.java` (np. `.sql`, `.yaml` - osobne skille)
- Pierwszy raz patrzysz na zupełnie nowy kod - wtedy `/explain` przed `/review`

## Checklist (format raportu)

Format każdej uwagi: **[POZIOM] [KATEGORIA] opis + lokalizacja**.

Poziomy:
- **BLOCKER** - musi być naprawione przed merge
- **WARNING** - powinno być naprawione lub uzasadnione
- **SUGGESTION** - opcjonalna poprawa

### Finanse (zawsze BLOCKER)

- [ ] Brak `double` / `float` w sygnaturach metod lub fieldach
- [ ] Wszystkie kwoty mają `scale = 2`
- [ ] Wszystkie stawki mają `scale = 6`
- [ ] `RoundingMode` jawnie podany przy każdym `divide()`, `setScale()`
- [ ] Porównania `BigDecimal` przez `compareTo`, nigdy `equals`
- [ ] Brak magic numbers - stałe nazwane (`BELKA_TAX_RATE`, etc.)

### PII (zawsze BLOCKER)

- [ ] Brak PESEL, numerów kont, imion klientów w logach
- [ ] Brak PESEL w fixturach testowych
- [ ] Brak PII w toString() klas modelu
- [ ] Brak PII w komunikatach wyjątków (`"Customer 123 not found"` zamiast `"PESEL 12345678901 not found"`)

### Walidacja (BLOCKER)

- [ ] Każda metoda publiczna waliduje argumenty
- [ ] `null` checki przez `Objects.requireNonNull`
- [ ] Out-of-range → `IllegalArgumentException` z opisem PL
- [ ] Walidacja **przed** logiką biznesową, nie pośrodku

### Testy (WARNING jeśli brak, BLOCKER jeśli słabe)

- [ ] JUnit 5 (`org.junit.jupiter.api.Test`), NIE JUnit 4 (`org.junit.Test`)
- [ ] AssertJ (`assertThat`), NIE Hamcrest (`assertThat(x, is(...))`)
- [ ] `isEqualByComparingTo` dla BigDecimal, NIE `isEqualTo`
- [ ] Naming: `should_<oczekiwany>_when_<warunek>()`
- [ ] Pokrycie: happy path + min 3 edge cases + invalid inputs
- [ ] Brak `Mockito.mock` na value objectach (BigDecimal, String, LocalDate)
- [ ] Brak `@Disabled` w mainline bez uzasadnienia

### Nowoczesna Java (SUGGESTION)

- [ ] `record` zamiast ręcznie pisanego POJO, gdy klasa to data holder
- [ ] Switch expressions zamiast switch statements
- [ ] Pattern matching `instanceof String s` zamiast rzutowania po sprawdzeniu
- [ ] `var` dla lokalnych zmiennych z oczywistym typem
- [ ] Brak getterów ani setterów na rekordach

### Logowanie (WARNING)

- [ ] SLF4J `Logger`, NIE `System.out.println`
- [ ] Placeholdery `{}`, NIE konkatenacja napisów (`"x=" + x`)
- [ ] Poziom logowania adekwatny: `debug` dla diagnostyki, `info` dla zdarzeń biznesowych, `warn` dla anomalii, `error` dla rzeczywistych błędów

### Commity (jeśli widzisz historię commitów)

- [ ] Conventional commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`
- [ ] Treść commita po angielsku (zespoły międzynarodowe)
- [ ] Każdy commit odnosi się do ID zgłoszenia lub zadania, jeśli ma to sens

## Anti-patterns (przykłady do flagowania)

### Anti-pattern: BigDecimal.equals

```java
// ❌ ŹLE
if (amount.equals(new BigDecimal("100"))) { ... }
// 100 vs 100.00 → false!

// ✅ DOBRZE
if (amount.compareTo(new BigDecimal("100")) == 0) { ... }
```

### Anti-pattern: PII w logach

```java
// ❌ ŹLE
log.info("Processing customer: PESEL={}", customer.getPesel());

// ✅ DOBRZE
log.info("Processing customer: id={}", customer.getId());
```

### Anti-pattern: niejawny RoundingMode

```java
// ❌ ŹLE - może rzucić ArithmeticException dla 1/3
BigDecimal r = amount.divide(BigDecimal.valueOf(3));

// ✅ DOBRZE
BigDecimal r = amount.divide(BigDecimal.valueOf(3), 6, RoundingMode.HALF_UP);
```

### Anti-pattern: walidacja w środku metody

```java
// ❌ ŹLE
public BigDecimal calculate(BigDecimal a, int t) {
    var result = a;
    for (int i = 0; i < t; i++) {
        if (a == null) throw new NPE(); // za późno
        // ...
    }
}

// ✅ DOBRZE
public BigDecimal calculate(BigDecimal a, int t) {
    Objects.requireNonNull(a, "a cannot be null");
    if (t < 0) throw new IllegalArgumentException("t cannot be negative: " + t);
    // ... reszta
}
```

## Format wyjścia

```
## Code Review - <tytuł PR albo nazwa gałęzi>

### BLOCKERS (do naprawienia przed merge)

1. **[FINANSE]** `InterestCalculator.java:24` - używasz `double` dla kwoty. Zamień na `BigDecimal`.
2. ...

### WARNINGS

1. ...

### SUGGESTIONS

1. ...

### Podsumowanie

- BLOCKERS: 2
- WARNINGS: 3
- SUGGESTIONS: 5
- Rekomendacja: REQUEST CHANGES albo APPROVE
```
