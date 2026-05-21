# Tasks - 001: Loan Calculator

Estymacja: ok. 7h łącznie. Format: `[ ]` pole wyboru, `T-NN` identyfikator zadania, opis, zależności, estymacja.

## Faza 1 - Typy i walidacja

- [ ] **T-01** Utwórz record `LoanParameters(BigDecimal principal, BigDecimal annualRate, int months)` z walidacją w kanonicznym konstruktorze. Zależności: brak. Estymacja: 20 min.
- [ ] **T-02** Utwórz record `Installment(int number, BigDecimal principalPart, BigDecimal interestPart, BigDecimal remainingBalance)`. Zależności: brak. Estymacja: 10 min.
- [ ] **T-03** Testy dla `LoanParameters`: null, ujemny principal, ujemna stopa, stopa > 1, `months ≤ 0`, `months > 360`. Zależności: T-01. Estymacja: 30 min.

## Faza 2 - Rdzeń

- [ ] **T-04** `InstallmentAmountCalculator.calculate(LoanParameters)`: implementacja wzoru raty równej. Zależności: T-01. Estymacja: 30 min.
- [ ] **T-05** Przypadek brzegowy w T-04: `annualRate == 0` → `principal / months`. Zależności: T-04. Estymacja: 15 min.
- [ ] **T-06** Testy dla `InstallmentAmountCalculator`: 5 happy path + przypadek brzegowy `rate=0` + porównanie z Excelem. Zależności: T-04, T-05. Estymacja: 45 min.
- [ ] **T-07** `TotalCostCalculator.calculate(LoanParameters)`: `rata × months − principal`. Zależności: T-04. Estymacja: 15 min.
- [ ] **T-08** Testy dla `TotalCostCalculator`: 3 happy path. Zależności: T-07. Estymacja: 20 min.

## Faza 3 - Harmonogram

- [ ] **T-09** `InstallmentScheduler.generate(LoanParameters)`: naiwna implementacja, bez korekty zaokrąglania. Zależności: T-04. Estymacja: 45 min.
- [ ] **T-10** Korekta ostatniej raty w `InstallmentScheduler`, żeby `suma == principal` i `remainingBalance == 0`. Zależności: T-09. Estymacja: 30 min.
- [ ] **T-11** Testy dla `InstallmentScheduler`: `długość == months`, `suma == principal`, `balance[last] == 0`, monotoniczność `principalPart`. Zależności: T-10. Estymacja: 45 min.

## Faza 4 - Fasada i porównanie z Excelem

- [ ] **T-12** `LoanCalculator` jako fasada: delegacja do trzech komponentów. Zależności: T-04, T-07, T-09. Estymacja: 20 min.
- [ ] **T-13** JavaDoc po polsku dla API publicznego `LoanCalculator`. Zależności: T-12. Estymacja: 20 min.
- [ ] **T-14** Test krzyżowy z 10 wartościami z Excela (NFR-1). Zależności: T-12. Estymacja: 30 min.

## Faza 5 - Jakość i dokumentacja

- [ ] **T-15** Konfiguracja JaCoCo w `pom.xml`, próg 90% pokrycia linii. Zależności: brak. Estymacja: 15 min.
- [ ] **T-16** README projektu z przykładami użycia (3 przykłady). Zależności: T-12. Estymacja: 30 min.
- [ ] **T-17** Przejście listy kontrolnej (`checklists/requirements.md`), wszystkie pozycje ✅. Zależności: T-15, T-16. Estymacja: 30 min.
- [ ] **T-18** Squash, conventional commit i opis PR z linkami do `spec`/`plan`. Zależności: T-17. Estymacja: 15 min.

## Kolejność wykonania

```
T-01 → T-02 → T-03
       ↓
       T-04 → T-05 → T-06
              ↓
              T-07 → T-08
              ↓
              T-09 → T-10 → T-11
              ↓
              T-12 → T-13 → T-14
                     ↓
                     T-15 → T-16 → T-17 → T-18
```

## Zależności krytyczne

- T-04 blokuje całą warstwę core
- T-12 blokuje fazę walidacji końcowej

## Definicja "ukończono" dla każdego zadania

1. Kod skompilowany (`mvn compile`)
2. Testy zielone (`mvn test`)
3. Zgodność z constitution (BigDecimal, walidacja, JavaDoc po polsku, brak PII, conventional commit)
4. PR (lub commit) odnosi się do ID zadania
