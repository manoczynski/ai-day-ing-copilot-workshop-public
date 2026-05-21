# Plan - 001: Loan Calculator

## Architektura

```
LoanCalculator (facade, public API)
├── InstallmentAmountCalculator    # FR-1: pojedyncza rata
├── InstallmentScheduler           # FR-2: harmonogram
└── TotalCostCalculator            # FR-3: suma kosztu

Wspólne typy:
- LoanParameters (record: principal, annualRate, months)  # input
- Installment (record: number, principalPart, interestPart, remainingBalance)  # output
```

## Decyzje techniczne

| Decyzja | Wybór | Alternatywa | Uzasadnienie |
|---|---|---|---|
| Język | Java 17 | Kotlin | Stos i ekspertyza zespołu |
| Typ pieniędzy | `BigDecimal` | `Money` (Joda) | Brak zewnętrznych zależności, wymóg constitution |
| Scale | 2 (kwoty) / 6 (stawki) | MathContext dla każdego przypadku | Prostota i czytelność |
| Zaokrąglanie | `HALF_UP` | `HALF_EVEN` (bankers) | Zgodność z polskim standardem księgowym |
| Liczba rat | `int` | `Months` (Joda Time) | YAGNI, miesiące to liczba całkowita |
| Testy | JUnit 5 + AssertJ | JUnit 4, TestNG | Wymóg constitution, nowoczesne narzędzia |
| Pokrycie | JaCoCo | Cobertura | De facto standard |
| Build | Maven | Gradle | Wymóg constitution |

## Wzory matematyczne

### Rata równa (FR-1)

```
monthlyRate = annualRate / 12
factor = (1 + monthlyRate)^months
installment = principal × (monthlyRate × factor) / (factor − 1)
```

Edge case `monthlyRate == 0`:
```
installment = principal / months
```

### Harmonogram (FR-2)

Dla każdej raty `i ∈ 1..months`:
```
interestPart[i] = remainingBalance[i-1] × monthlyRate
principalPart[i] = installment − interestPart[i]
remainingBalance[i] = remainingBalance[i-1] − principalPart[i]
```

Korekta ostatniej raty: `principalPart[months] = remainingBalance[months-1]`, `installment[months] = principalPart[months] + interestPart[months]`, `remainingBalance[months] = 0.00`.

## Fazy implementacji

### Faza 1 - Types & validation (1h)
- `LoanParameters` record + walidacja w canonical constructor
- `Installment` record
- Wyjątki: standardowe `NullPointerException`, `IllegalArgumentException` (komunikaty PL)

### Faza 2 - Core (2h)
- `InstallmentAmountCalculator` + testy
- `TotalCostCalculator` + testy

### Faza 3 - Schedule (2h)
- `InstallmentScheduler` + testy
- Korekta rounding error w ostatniej racie

### Faza 4 - Facade & cross-check (1h)
- `LoanCalculator` jako fasada
- Cross-check testy z wartościami z Excela (NFR-1)

### Faza 5 - Jakość i dokumentacja (1h)
- JavaDoc po polsku dla API publicznego
- JaCoCo > 90%
- README z przykładami użycia
- zielona lista kontrolna (`checklists/requirements.md`)

## Ryzyka

| Ryzyko | Środki zaradcze |
|---|---|
| Błędy zaokrąglania w harmonogramie | Korekta ostatniej raty plus test sumy |
| Przepełnienie przy dużych `principal × months` | `BigDecimal` ma dowolną precyzję, więc brak ryzyka |
| Wydajność dla 360 rat | Brak ryzyka, proste pętle, < 10ms |
| Niezgodność z Excelem | Test krzyżowy z 10 wartościami z arkusza księgowej |
