# Wzorcowy efekt refaktoryzacji `CalculateInterest`

⚠️ **NIE OTWIERAJ TEGO PRZED WYKONANIEM ĆWICZENIA.**

To jest referencja dla prowadzącego, do porównania **po** zakończeniu Bloku 4. Każda para zrobi to inaczej i to jest OK. Poniżej jeden z wzorcowych kształtów refaktora.

---

## Co się zmieniło - diff koncepcyjnie

| Przed | Po |
|---|---|
| `double a, int t, String type` | `BigDecimal principal, int years, AccountType type` |
| `if (type.equals("R")) r = 0.02;` (zduplikowane w `calc` i `getMonthlyBalances`) | `AccountType.REGULAR.baseRate()` |
| `res * 0.19` magic number | `BELKA_TAX_RATE` jako stała `BigDecimal` |
| `if (a > 100000) res -= 50;` | `AccountFeePolicy.feeFor(principal)` |
| Wszystko w jednej metodzie `calc()` | `InterestCalculator` + `BelkaTaxCalculator` + `AccountFeePolicy` + `AccountInterestService` (orkiestracja) |
| Powtórzenie stawek w `calc()` i `getMonthlyBalances()` | Wspólne źródło prawdy w `AccountType` |
| `System.out.println` | SLF4J `log.info` |
| Brak testów | JUnit 5 + AssertJ, happy path + edge cases + invalid inputs |
| Brak walidacji | `Objects.requireNonNull` + signum check |

## Struktura plików po refaktorze

```
src/main/java/com/n8/workshop/
├── AccountType.java                    # enum + base rate
├── AccountFeePolicy.java               # progi opłat
├── BelkaTaxCalculator.java             # podatek 19%
├── InterestCalculator.java             # odsetki brutto
├── InterestCalculationResult.java      # record (gross, tax, fee, net)
└── AccountInterestService.java         # orkiestracja (zewnętrzne API)

src/test/java/com/n8/workshop/
├── AccountFeePolicyTest.java
├── BelkaTaxCalculatorTest.java
├── InterestCalculatorTest.java
└── AccountInterestServiceTest.java
```

## Szkic kluczowych klas

### `AccountType`

```java
public enum AccountType {
    REGULAR(new BigDecimal("0.02")),
    SAVINGS(new BigDecimal("0.05")),
    PREMIUM(new BigDecimal("0.07"));

    private final BigDecimal baseRate;

    AccountType(BigDecimal baseRate) { this.baseRate = baseRate; }

    public BigDecimal baseRate() { return baseRate; }
}
```

### `BelkaTaxCalculator`

```java
public class BelkaTaxCalculator {

    public static final BigDecimal BELKA_TAX_RATE = new BigDecimal("0.19");

    public BigDecimal taxOn(BigDecimal profit) {
        Objects.requireNonNull(profit, "profit cannot be null");
        if (profit.signum() <= 0) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return profit.multiply(BELKA_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
```

### `AccountFeePolicy`

```java
public class AccountFeePolicy {

    public static final BigDecimal LARGE_ACCOUNT_THRESHOLD  = new BigDecimal("100000");
    public static final BigDecimal MEDIUM_ACCOUNT_THRESHOLD = new BigDecimal("50000");
    public static final BigDecimal LARGE_ACCOUNT_FEE        = new BigDecimal("50.00");
    public static final BigDecimal MEDIUM_ACCOUNT_FEE       = new BigDecimal("20.00");

    public BigDecimal feeFor(BigDecimal principal) {
        Objects.requireNonNull(principal, "principal cannot be null");
        if (principal.compareTo(LARGE_ACCOUNT_THRESHOLD) > 0)  return LARGE_ACCOUNT_FEE;
        if (principal.compareTo(MEDIUM_ACCOUNT_THRESHOLD) > 0) return MEDIUM_ACCOUNT_FEE;
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
```

### `InterestCalculator`

```java
public class InterestCalculator {

    public BigDecimal calculateGross(BigDecimal principal, int years, AccountType type) {
        Objects.requireNonNull(principal, "principal cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        if (principal.signum() < 0) throw new IllegalArgumentException("Kwota nie może być ujemna: " + principal);
        if (years < 0) throw new IllegalArgumentException("Okres nie może być ujemny: " + years);

        var rate = BigDecimal.ONE.add(type.baseRate());
        var result = principal;
        for (int i = 0; i < years; i++) {
            result = result.multiply(rate);
        }
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
```

### `InterestCalculationResult`

```java
public record InterestCalculationResult(
        BigDecimal gross,
        BigDecimal tax,
        BigDecimal fee,
        BigDecimal net
) {}
```

### `AccountInterestService`

```java
public class AccountInterestService {

    private final InterestCalculator interest = new InterestCalculator();
    private final BelkaTaxCalculator tax = new BelkaTaxCalculator();
    private final AccountFeePolicy fees = new AccountFeePolicy();

    public InterestCalculationResult compute(BigDecimal principal, int years, AccountType type) {
        var gross = interest.calculateGross(principal, years, type);
        var profit = gross.subtract(principal);
        var belka = tax.taxOn(profit);
        var fee = fees.feeFor(principal);
        var net = gross.subtract(belka).subtract(fee).setScale(2, RoundingMode.HALF_UP);
        return new InterestCalculationResult(gross, belka, fee, net);
    }
}
```

## Charakterystyczne testy

```java
@Test
void should_apply_premium_rate_when_account_type_is_premium() {
    var service = new AccountInterestService();
    var result = service.compute(new BigDecimal("10000.00"), 1, AccountType.PREMIUM);
    assertThat(result.gross()).isEqualByComparingTo("10700.00");
}

@Test
void should_apply_medium_fee_when_principal_just_above_threshold() {
    var policy = new AccountFeePolicy();
    var fee = policy.feeFor(new BigDecimal("50000.01"));
    assertThat(fee).isEqualByComparingTo("20.00");
}

@Test
void should_throw_when_principal_is_negative() {
    var calc = new InterestCalculator();
    assertThatThrownBy(() -> calc.calculateGross(new BigDecimal("-1"), 1, AccountType.REGULAR))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Kwota nie może być ujemna");
}
```

## Co nadal można poprawić (zaawansowane, poza zakresem warsztatu)

- `MathContext` z konfigurowalną precyzją zamiast scale na sztywno
- Wzorzec Strategy dla `AccountFeePolicy` (zamiast progów w if/else)
- Typ Money (Joda Money lub własny) zamiast surowego `BigDecimal`
- Wzór odsetek złożonych przez `rate.pow(years, MathContext)` zamiast pętli (uwaga: trzeba dopilnować scale)
- Integracja z `java.time.YearMonth` w `getMonthlyBalances`

Te punkty są dobrym materiałem na rozszerzenie po warsztacie albo na drugą iterację z Copilotem.
