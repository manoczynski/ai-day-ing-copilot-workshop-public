# 4 bankowe przykłady do samodzielnej pracy

Zestaw 4 konkretnych przypadków bankowych, na których możesz przećwiczyć TDD z Copilotem. Każdy jest pełnym setupem: opis biznesowy, lista wymagań, lista edge cases, sugerowana strategia testowania.

Wykonuj kolejno albo wybiórczo. Każdy zajmuje 20-30 minut w cyklu Red-Green-Refactor.

---

## Przykład 1 - Kalkulator złożonych odsetek (8 edge cases)

### Opis biznesowy

Pożyczka bankowa. Klient wpłaca `principal`, na okres `years` lat, ze stopą `annualRate`. Po końcu okresu otrzymuje kwotę `A = P × (1 + r)^n`. Bank pobiera potem podatek Belka 19% od zysku.

### Klasy do zaimplementowania

```
CompoundInterestCalculator
  BigDecimal calculate(BigDecimal principal, BigDecimal annualRate, int years)

NetAmountCalculator
  BigDecimal calculateNet(BigDecimal principal, BigDecimal annualRate, int years)
    // zwraca kwotę po podatku Belka 19% od zysku
```

### Wymagania funkcjonalne

- BigDecimal, scale 2 dla kwot, scale 6 dla stóp
- RoundingMode.HALF_UP
- `principal ≥ 0`, w przeciwnym razie IAE
- `annualRate ∈ [0, 1]`, w przeciwnym razie IAE
- `years ∈ [0, 30]`, w przeciwnym razie IAE
- Dla null obiektów BigDecimal: NPE
- Dla wartości z `years = 0`: `result = principal`
- Dla wartości z `principal = 0`: `result = 0`
- Dla wartości z `annualRate = 0`: `result = principal`

### 8 edge cases (lista minimum)

1. `principal=0, rate=0.05, years=1` → 0
2. `principal=100.00, rate=0, years=10` → 100.00
3. `principal=100.00, rate=0.05, years=0` → 100.00
4. `principal=100.00, rate=0.05, years=1` → 105.00
5. `principal=100.00, rate=0.05, years=10` → 162.89 (cross-check z Excelem)
6. `principal=1000000.00, rate=0.07, years=30` → 7612255.04 (BigDecimal precision check, cross-check z Excelem)
7. `principal=-1, rate=0.05, years=1` → IAE z polskim komunikatem
8. `principal=100.00, rate=1.5, years=1` → IAE z polskim komunikatem

### Strategia TDD

Cykl: ćwiczenie 1 z `EXERCISES.md` (Spec → Test → Code). Spodziewany czas: 20 min.

---

## Przykład 2 - Walidator PESEL

### Opis biznesowy

PESEL: 11-cyfrowy identyfikator obywatela. Cyfry: YYMMDD + 3 cyfry numeru porządkowego + 1 cyfra płci + 1 cyfra kontrolna. Algorytm sumy kontrolnej: pomnóż każdą z 10 pierwszych cyfr przez wagi [1,3,7,9,1,3,7,9,1,3], zsumuj, weź modulo 10. Cyfra kontrolna powinna być równa `(10 - suma % 10) % 10`.

### Klasy do zaimplementowania

```
PeselValidator
  boolean isValid(String pesel)
  // zwraca true dla poprawnego PESEL, false dla błędnej sumy kontrolnej
  // rzuca NPE dla null, IAE dla pustego/białych znaków/długości != 11/nie-cyfr
```

### Wymagania

- Akceptuj PESEL z wiodącymi zerami
- Tolerancja na białe znaki: trimuj input PRZED walidacją
- Tolerancja wielkości liter: PESEL to cyfry, więc nie ma znaczenia
- NIE waliduj daty (nie sprawdzaj, czy YYMMDD to realny dzień). To dla osobnej metody `isDateValid`.

### Edge cases (minimum 10)

1. `null` → NPE
2. `""` → IAE
3. `"   "` → IAE
4. `"12345"` (za krótki) → IAE
5. `"123456789012"` (za długi) → IAE
6. `"1234567890a"` (z literą) → IAE
7. Poprawny PESEL 1970-01-01 → true
8. Poprawny PESEL 2010-12-31 → true (PESEL po 2000)
9. PESEL z błędną sumą kontrolną → false
10. PESEL z otaczającymi spacjami i poprawną sumą → true (po trim)

### Strategia TDD

Cykl 1 + parametryzacja (cykl 3 z `EXERCISES.md`). Wygeneruj `@CsvFileSource` z 15 case'ami.

### Specyfika bankowa

W realnym banku PeselValidator to standalone library, używana przez 10+ aplikacji. Każdy bug w nim = 10 incydentów. Coverage musi być 100%, testy mutacyjne też zielone.

---

## Przykład 3 - Podatek Belka z progami

### Opis biznesowy

Podatek od zysków kapitałowych. 19% od `profit`, gdzie `profit = capital_after - capital_before`. Strata (profit < 0) NIE generuje zwrotu podatku, tylko zerowy podatek. Zaokrąglanie: zawsze w górę dla wartości >= .5, w dół dla < .5 (HALF_UP).

Plus opcjonalna logika progowa: dla zysków powyżej 1 000 000 PLN dodatkowe 4% (placeholder dla "Estonian tax"). Może być wyłączona przez flagę.

### Klasy do zaimplementowania

```
BelkaTaxCalculator
  BigDecimal taxOn(BigDecimal profit)  // 19% od dodatniego, 0 dla ujemnego/zero
  BigDecimal taxOn(BigDecimal profit, BigDecimal capitalBefore, boolean estonianTaxEnabled)
    // capitalBefore służy TYLKO walidacji (musi być nieujemny),
    // próg estoński liczymy względem profit, nie capitalBefore
```

### Wymagania

- BigDecimal, scale 2, HALF_UP
- Domyślna stawka: BELKA_TAX_RATE = 0.19
- Próg estoński: ESTONIAN_THRESHOLD = 1000000.00 (próg dotyczy profit)
- Dodatkowa stawka ponad próg: ESTONIAN_RATE = 0.04
- Walidacja: `profit` nie może być null (NPE), `capitalBefore < 0` → IAE

### Edge cases

1. `profit = 0` → 0.00
2. `profit = -100` → 0.00 (strata, brak podatku)
3. `profit = 100` → 19.00
4. `profit = 100.50` → 19.10 (zaokrąglenie w górę: 100.50 * 0.19 = 19.095 → 19.10)
5. `profit = 99.99` → 19.00 (zaokrąglenie w dół: 99.99 * 0.19 = 18.9981 → 19.00)
6. `profit = 1_500_000, capitalBefore = 100_000, estonian = false` → 285_000.00
7. `profit = 1_500_000, capitalBefore = 100_000, estonian = true` → 285_000.00 + 4% od 500_000 powyżej progu = 305_000.00
8. `profit = 100, capitalBefore = -1, estonian = true` → IAE (kapitał ujemny)
9. `profit = null` → NPE

### Strategia TDD

Cykl 1 + 3 (parametryzacja) + 4 (test odtwarzający case z księgowości). Łącznie ok. 25 min.

### Specyfika bankowa

Audyt księgowy regularnie sprawdza zgodność z Excelem. Co miesiąc dostajesz CSV z 50 wartościami "expected", musisz dodać je do testów cross-check.

---

## Przykład 4 - Harmonogram spłat (raty równe + raty malejące)

### Opis biznesowy

Pożyczka z ratami **równymi** (annuity) lub **malejącymi** (equal principal).

Konwencja stóp: `annualRate` to stopa **roczna** (BigDecimal, scale 6). We wzorach poniżej `r = annualRate / 12` (stopa miesięczna) i `n = months` (liczba rat). Spójnie z `bonus-01-spec-kit/specs/001-loan-calculator/spec.md`.

- Raty równe: `installment = P × (r × (1+r)^n) / ((1+r)^n − 1)`
- Raty malejące: `principal_part = P / n`, `interest_part = remaining_balance × r`

### Klasy do zaimplementowania

```
record InstallmentScheduleParams(
    BigDecimal principal, BigDecimal annualRate, int months, ScheduleType type
)

enum ScheduleType { EQUAL_INSTALLMENT, EQUAL_PRINCIPAL }

record Installment(
    int number,
    BigDecimal principalPart,
    BigDecimal interestPart,
    BigDecimal totalAmount,
    BigDecimal remainingBalance
) {}

InstallmentScheduler
  List<Installment> generate(InstallmentScheduleParams params)
```

### Wymagania

- BigDecimal, scale 2 dla kwot, scale 6 dla stóp
- Korekta ostatniej raty: `sum(installment.principalPart) == principal` (do 0.00 dokładnie)
- `remainingBalance` po ostatniej racie = 0.00
- Dla typu EQUAL_INSTALLMENT: każda rata ma tę samą `totalAmount` (poza ostatnią z korektą)
- Dla typu EQUAL_PRINCIPAL: każda rata ma tę samą `principalPart`, ale różną `interestPart`
- Walidacja: `months ∈ [1, 360]`, `annualRate ∈ [0, 0.30]`, `principal > 0`

### Edge cases (minimum 12)

1. `principal=10000, rate=0.05, months=12, EQUAL_INSTALLMENT` → 12 rat, suma principalPart = 10000.00
2. `principal=10000, rate=0.05, months=12, EQUAL_PRINCIPAL` → 12 rat, każda principalPart = 833.33 (poza ostatnią)
3. `principal=10000, rate=0, months=12, EQUAL_INSTALLMENT` → 12 rat po 833.33 każda (poza ostatnią)
4. `principal=10000, rate=0.05, months=1` → 1 rata
5. `principal=10000, rate=0.05, months=360, EQUAL_INSTALLMENT` → 360 rat, sprawdź sumę
6. Cross-check z Excelem PMT (5 wartości) dla EQUAL_INSTALLMENT
7. Cross-check ręcznie obliczonej tabeli dla EQUAL_PRINCIPAL (3 wartości)
8. Monotoniczność principalPart w EQUAL_INSTALLMENT (rośnie z każdą ratą)
9. Monotoniczność interestPart w EQUAL_INSTALLMENT (maleje z każdą ratą)
10. `principal = 1000000, months = 360, rate = 0.07` → BigDecimal precision check
11. `months = 0` → IAE
12. `months = 361` → IAE

### Strategia TDD

Wszystkie 5 cyklów z `EXERCISES.md`:
- Cykl 1: spec → test → code dla EQUAL_INSTALLMENT
- Cykl 2: characterization tests dla istniejącego kalkulatora w core (jeśli masz)
- Cykl 3: parametryzacja - jeden test parametryzowany sprawdza sumę principalPart dla 20 wariantów
- Cykl 4: bug fix - dodaj test odtwarzający bug z księgowości (np. ostatnia rata różni się o 0.01 PLN)
- Cykl 5: fixtury bez PII (nie używaj realnych nazw klientów, IBAN-ów)

Łącznie ok. 45-60 min. To największy przykład w tym katalogu i najbliższy realnemu kodowi z `bonus-01-spec-kit/`.

### Specyfika bankowa

Harmonogram spłat to **jedyny dokument**, który klient widzi przy podpisaniu umowy pożyczkowej. Każda niezgodność z Excelem księgowej = pretensja, czasem postępowanie z KNF. Cross-check z arkuszem to nie ozdobnik, tylko twarda obrona.

---

## Co dalej

- **Wybierz najbliższy do twojego projektu** i wykonaj cały cykl TDD
- **Zapisz dobre prompty** do `~/.copilot/prompts/` jako pliki - łatwo użyć ponownie
- **Połącz z `bonus-01-spec-kit/`**: ten sam kalkulator pożyczek, ale z formalnym `spec.md` i `plan.md`
- **Zobacz `bonus-02-copilot-skills/.github/skills/banking-code-review/SKILL.md`** punkt "Testy" - jak wymusić TDD przez code review
