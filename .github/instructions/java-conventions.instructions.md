---
applyTo: "**/*.java"
---

# Java Conventions

Konwencje stosowane do **wszystkich plików .java** w tym repo. Aktywują się automatycznie przy pracy z plikami Java.

## Wymagana wersja

Java 17. Używaj nowoczesnych funkcji języka tam, gdzie poprawia to czytelność:
- `record` zamiast POJO (gdy klasa pełni rolę kontenera danych)
- Switch expressions (`switch (x) { case A -> ...; }`)
- Pattern matching dla `instanceof` (`if (obj instanceof String s)`)
- `var` dla lokalnych zmiennych z oczywistym typem
- Text blocks (`"""..."""`)
- `sealed` dla zamkniętych hierarchii

## Finanse - RYGORYSTYCZNIE

Każda kwota, stawka, procent → `java.math.BigDecimal`. NIGDY `double` lub `float`.

```java
// ✅ DOBRZE
private static final BigDecimal TAX_RATE = new BigDecimal("0.19");
BigDecimal result = principal.multiply(rate)
    .setScale(2, RoundingMode.HALF_UP);

// ❌ ŹLE
private static final double TAX_RATE = 0.19;
double result = principal * rate;
```

Porównywanie:

```java
// ✅ DOBRZE
if (amount.compareTo(BigDecimal.ZERO) > 0) { ... }

// ❌ ŹLE - equals zwraca false dla 1.0 vs 1.00
if (amount.equals(BigDecimal.ONE)) { ... }
```

Dzielenie zawsze z RoundingMode:

```java
// ✅ DOBRZE
BigDecimal monthly = annual.divide(TWELVE, 6, RoundingMode.HALF_UP);

// ❌ ŹLE - może rzucić ArithmeticException
BigDecimal monthly = annual.divide(TWELVE);
```

## Naming

```java
// ✅ DOBRZE
public BigDecimal calculateMonthlyPayment(BigDecimal principal, int months, BigDecimal annualRate)
private static final BigDecimal MIN_LOAN_AMOUNT = new BigDecimal("1000");

// ❌ ŹLE
public double calc(double a, int t, double r)
private double min = 1000;
```

## Walidacja metod publicznych

```java
public BigDecimal calculate(BigDecimal principal, int years, AccountType type) {
    Objects.requireNonNull(principal, "principal cannot be null");
    Objects.requireNonNull(type, "type cannot be null");
    if (principal.signum() < 0) {
        throw new IllegalArgumentException("Kwota nie może być ujemna: " + principal);
    }
    if (years < 0) {
        throw new IllegalArgumentException("Okres nie może być ujemny: " + years);
    }
    // ... logika
}
```

## JavaDoc - po polsku, dla metod publicznych

```java
/**
 * Oblicza końcową kwotę po odsetkach, podatku Belka i opłatach.
 *
 * @param principal kwota początkowa (musi być nieujemna)
 * @param years okres w latach (musi być nieujemny)
 * @param accountType typ konta (nie może być null)
 * @return końcowa kwota po wszystkich operacjach, scale = 2
 * @throws IllegalArgumentException gdy principal lub years są ujemne
 * @throws NullPointerException gdy accountType jest null
 */
public BigDecimal calculate(BigDecimal principal, int years, AccountType accountType) {
    // ...
}
```

## Testy

```java
@Test
void should_apply_premium_rate_when_account_type_is_premium() {
    // given
    var calculator = new InterestCalculator();
    var principal = new BigDecimal("10000.00");

    // when
    var result = calculator.calculate(principal, 1, AccountType.PREMIUM);

    // then
    assertThat(result).isEqualByComparingTo("10567.00");
}
```

## Logging

```java
private static final Logger log = LoggerFactory.getLogger(InterestCalculator.class);

log.info("Calculating: principal={}, years={}, type={}", principal, years, type);

// NIGDY: log.info("Customer PESEL: {}", customer.getPesel());
```
