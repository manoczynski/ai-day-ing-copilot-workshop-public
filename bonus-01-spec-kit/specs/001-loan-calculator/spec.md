# Spec - 001: Loan Calculator z harmonogramem spłat

## Streszczenie

Komponent do obliczania **rat równych** dla pożyczek bankowych, z generowaniem harmonogramu spłat (każda rata: część kapitałowa, część odsetkowa, saldo po racie).

Wzór klasyczny: rata równa = `P × (r × (1+r)^n) / ((1+r)^n - 1)`, gdzie:
- `P` - kapitał początkowy
- `r` - miesięczna stopa procentowa (`annualRate / 12`)
- `n` - liczba rat (months)

## Motywacja

Obecny kod kalkulatora pożyczek w core bankowym używa `double` i ma ok. 6 znanych miejsc rozjazdu względem Excela księgowej. Refaktoryzacja tej części systemu to długi projekt, a ten komponent jest **wzorcowym wycinkiem**, na którym przećwiczymy podejście.

Także: pokazanie zespołowi, jak wygląda Spec-Driven Development w praktyce.

## Wymagania funkcjonalne

### FR-1: Obliczanie raty równej

**Wejście:**
- `principal: BigDecimal` (kwota pożyczki, > 0, scale 2)
- `annualRate: BigDecimal` (roczna stopa, 0 ≤ x ≤ 1, scale 6, np. `0.07` = 7%)
- `months: int` (liczba rat, > 0, max 360)

**Wyjście:**
- `BigDecimal` - wartość pojedynczej raty, scale 2, `HALF_UP`

**Przypadek brzegowy:** dla `annualRate = 0` (pożyczka 0%) rata = `principal / months`.

### FR-2: Harmonogram spłat

**Wejście:** to samo co FR-1.

**Wyjście:** `List<Installment>` (długość = `months`), gdzie:

```java
record Installment(
    int number,                    // 1..months
    BigDecimal principalPart,      // część kapitałowa, scale 2
    BigDecimal interestPart,       // część odsetkowa, scale 2
    BigDecimal remainingBalance    // saldo po racie, scale 2
) {}
```

**Reguły zaokrąglania:**
- Ostatnia rata koryguje błąd zaokrąglenia tak, żeby `sum(principalPart) == principal`
- `remainingBalance` po ostatniej racie == `0.00`

### FR-3: Suma kosztu kredytu

**Wejście:** `principal`, `annualRate`, `months`.
**Wyjście:** `BigDecimal` - `(rata × months) − principal`, scale 2.

## Wymagania niefunkcjonalne

### NFR-1: Precyzja

Różnica vs wzór Excel `PMT(rate/12, months, -principal)` < 0.01 PLN dla wszystkich kombinacji `principal ∈ [1000, 1_000_000]`, `annualRate ∈ [0, 0.30]`, `months ∈ [1, 360]`.

### NFR-2: Wydajność

Generowanie 360-ratowego harmonogramu < 10ms na typowym laptopie (Java 17, JIT warm).

### NFR-3: Determinizm

Identyczne wejścia → identyczne wyjścia. Brak zależności od `Locale`, `TimeZone`, `Random`.

### NFR-4: Thread-safety

Komponent musi być bezstanowy (stateless). Bezpieczny do współdzielenia między wątkami.

## Poza zakresem

- ❌ Raty malejące, jako osobna funkcjonalność i osobny spec
- ❌ Karencja (grace period), jako osobna funkcjonalność
- ❌ Wcześniejsza spłata, jako osobna funkcjonalność
- ❌ Persistence, DB i REST API; to biblioteka domenowa
- ❌ Wielowalutowość (FX); wszystko w PLN

## Kryteria akceptacji

- [ ] FR-1: testy dla 5 reprezentatywnych zestawów + edge: `annualRate = 0`
- [ ] FR-2: harmonogram zsumowany == `rata × months`, `remainingBalance[last] == 0.00`
- [ ] FR-3: testy dla 3 zestawów
- [ ] NFR-1: test cross-check z 10 wartościami z Excela
- [ ] Wszystkie metody publiczne walidują dane wejściowe (NPE + IAE)
- [ ] JavaDoc po polsku dla public API
- [ ] `mvn test` zielone, `mvn site` generuje raport JaCoCo > 90% line coverage
