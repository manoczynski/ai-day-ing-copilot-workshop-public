---
name: java-modernization
description: Use when migrating Java 8 or Java 11 code to Java 17 - applies records, switch expressions, pattern matching, var, text blocks, and sealed types with clear when-to-use criteria
---

# Java Modernization

Skill aktywuje się przy migracji legacy kodu Java 8 / 11 na Java 17. Pomaga decydować, **kiedy** użyć nowych features, a kiedy zostawić jak było.

## When to use

- Migracja modułu Java 8 / 11 → 17
- Code review legacy kodu w projekcie, który jest już na Java 17
- Refaktoryzacja klasy POJO z polami + getterami / setterami
- Refaktoryzacja `switch` statementów z dużą liczbą `case`
- Refaktoryzacja kodu, który mocno używa `instanceof + cast`

## When NOT to use

- Refaktoryzacja domeny biznesowej (najpierw spec, potem clean architecture, dopiero potem modern Java)
- Optymalizacja wydajności (funkcje Java 17 dotyczą czytelności, nie szybkości)
- Klasy z publicznym mutowalnym stanem (najpierw uczyń je niemutowalnymi, potem zastąp rekordami)

## Kiedy `record` vs `class`

### ✅ Użyj `record` gdy:

- Klasa to **data container** (DTO, value object, result holder)
- Wszystkie fields są final
- Brak nadmiarowej logiki biznesowej w klasie
- Klasa nie jest częścią hierarchii JPA / Spring Data

### ❌ Zostaw `class` gdy:

- Klasa ma mutable state
- Klasa dziedziczy z innej klasy (records nie wspierają inheritance)
- Klasa jest JPA `@Entity` (Hibernate ma issues z recordami)
- Klasa potrzebuje `equals` / `hashCode` zaprojektowane custom (records wymuszają strukturalne)
- Klasa potrzebuje builder pattern z opcjonalnymi polami (record ma jeden kanoniczny konstruktor)

### Przykład

```java
// PRZED
public class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) { this.amount = amount; this.currency = currency; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }

    @Override public boolean equals(Object o) { /* boilerplate */ }
    @Override public int hashCode() { /* boilerplate */ }
    @Override public String toString() { /* boilerplate */ }
}

// PO (Java 17)
public record Money(BigDecimal amount, String currency) {
    public Money {
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(currency, "currency cannot be null");
    }
}
```

## Kiedy switch expression vs statement

### ✅ Switch expression gdy:

- Każdy `case` zwraca wartość (przypisanie do zmiennej)
- Lub każdy `case` rzuca wyjątek
- Lub każdy `case` ma prostą akcję w bloku `{ ... }`

### Przykład

```java
// PRZED
String desc;
switch (status) {
    case ACTIVE: desc = "Aktywne"; break;
    case CLOSED: desc = "Zamknięte"; break;
    case BLOCKED: desc = "Zablokowane"; break;
    default: desc = "Nieznane";
}

// PO
String desc = switch (status) {
    case ACTIVE -> "Aktywne";
    case CLOSED -> "Zamknięte";
    case BLOCKED -> "Zablokowane";
};
// Java 17 wymusza pokrycie wszystkich wariantów dla enum: kompilator zgłosi brakujący case
```

## Kiedy pattern matching for instanceof

### Przykład

```java
// PRZED
if (obj instanceof String) {
    String s = (String) obj;
    return s.length();
}

// PO
if (obj instanceof String s) {
    return s.length();
}
```

Zawsze gdy widzisz `instanceof X` i rzutowanie `(X) obj` w następnej linii, zamień.

## Kiedy `var`

### ✅ Użyj `var` gdy:

- Typ jest oczywisty z prawej strony (`var list = new ArrayList<Customer>()`)
- Lokalna zmienna w metodzie
- Iteracja `for (var x : list)`

### ❌ NIE używaj `var` gdy:

- Pole klasy (Java zabrania)
- Parameter metody (Java zabrania)
- Typ nie jest oczywisty (`var x = service.process()`: co to jest?)
- Wartość to interfejs, a chciałbyś podkreślić abstrakcję (`List<X> list = ...` jest jaśniejsze niż `var list = ...`, gdy chcesz polimorfizm)

## Kiedy text blocks

### ✅ Text block gdy:

- Multi-line string (SQL, JSON, XML, prompt)
- Wiele cudzysłowów które trzeba by escape'ować

```java
// PRZED
String sql = "SELECT id, name\n" +
             "FROM customers\n" +
             "WHERE active = true";

// PO
String sql = """
        SELECT id, name
        FROM customers
        WHERE active = true
        """;
```

## Kiedy sealed types

### ✅ Sealed gdy:

- Modelujesz closed hierarchię ("typ X może być A lub B lub C, koniec")
- Chcesz exhaustive pattern matching w switchu
- Reprezentujesz **Result** / **Either** / **Sum types**

### Przykład

```java
public sealed interface PaymentResult
        permits PaymentSuccess, PaymentRejected, PaymentPending {}

public record PaymentSuccess(String transactionId) implements PaymentResult {}
public record PaymentRejected(String reason) implements PaymentResult {}
public record PaymentPending(Instant retryAt) implements PaymentResult {}

// W konsumencie:
String message = switch (result) {
    case PaymentSuccess s -> "OK: " + s.transactionId();
    case PaymentRejected r -> "FAIL: " + r.reason();
    case PaymentPending p -> "WAIT: " + p.retryAt();
};
```

## Kolejność modernizacji (rekomendowana)

1. **`Objects.requireNonNull`** w metodach publicznych: fundament
2. **`var`** dla lokalnych zmiennych: niskie ryzyko, czystszy kod
3. **Pattern matching dla instanceof**: niskie ryzyko, lepsza czytelność
4. **Switch expressions**: średnie ryzyko (zmiana semantyki dla niektórych przypadków), więcej czytelności
5. **Records** zamiast POJO: wymagają przeglądu wszystkich wywołań getterów (różne API: `record.field()` vs `pojo.getField()`)
6. **Text blocks**: przy edycji wielowierszowych napisów
7. **Sealed types**: przy projektowaniu nowej hierarchii, niemal nigdy w refaktorze

## Antywzorce w modernizacji

### Antywzorzec: record wszędzie

```java
// ❌ ŹLE: Customer ma logikę biznesową
public record Customer(String id, String name, BigDecimal balance) {
    public Customer deposit(BigDecimal amount) { /* logika */ return new Customer(...); }
    public Customer withdraw(BigDecimal amount) { /* logika */ return new Customer(...); }
}
```

Tu klasyczna klasa może być bardziej czytelna. Rekord sprawdza się przede wszystkim dla DTO między warstwami, nie dla encji.

### Antywzorzec: var wszędzie

```java
// ❌ ŹLE: co to jest?
var result = service.process(input);

// ✅ DOBRZE
PaymentResult result = service.process(input);
```

`var` ma być skrótem ergonomicznym, nie zaciemnieniem.
