# Requirements Checklist - przed PR

Quality gate. Każdy punkt musi być ✅ przed merge.

## Wymagania funkcjonalne

- [ ] **FR-1**: `InstallmentAmountCalculator` ma minimum 5 happy path testów + edge `annualRate = 0`
- [ ] **FR-2**: `InstallmentScheduler` zwraca listę o długości `months`
- [ ] **FR-2**: suma `installment.principalPart()` po wszystkich elementach harmonogramu jest równa `principal` (z dokładnością do 0.00)
- [ ] **FR-2**: `schedule.last().remainingBalance` == `0.00`
- [ ] **FR-3**: `TotalCostCalculator` ma minimum 3 testy

## Wymagania niefunkcjonalne

- [ ] **NFR-1**: Cross-check test z 10 wartościami z Excela, różnica < 0.01 PLN
- [ ] **NFR-2**: Benchmark / mikro-pomiar pokazuje < 10ms dla 360 rat
- [ ] **NFR-3**: Brak `Locale`, `TimeZone`, `Random` w kodzie produkcyjnym
- [ ] **NFR-4**: Wszystkie klasy są stateless (brak `private mutableField`)

## Constitution

- [ ] Każda metoda publiczna waliduje dane wejściowe (null + signum)
- [ ] Brak `double` lub `float` w kodzie produkcyjnym
- [ ] Wszystkie kwoty mają `scale = 2`
- [ ] Wszystkie stawki mają `scale = 6`
- [ ] `RoundingMode.HALF_UP` wszędzie
- [ ] JavaDoc po polsku dla każdej metody publicznej z `@param`, `@return`, `@throws`
- [ ] Brak PII w logach i danych testowych
- [ ] Conventional commits, każdy z identyfikatorem zadania

## Pokrycie testami

- [ ] `mvn test` zielone
- [ ] JaCoCo line coverage > 90%
- [ ] JaCoCo branch coverage > 85%
- [ ] Naming testów: `should_<oczekiwany>_when_<warunek>()`
- [ ] Brak `@Disabled` w głównej gałęzi kodu

## Code review

- [ ] Co najmniej 1 reviewer poza autorem
- [ ] `/review` przez Copilota zaadresowany (każdy BLOCKER / WARNING)
- [ ] PR description ma sekcję "Decisions" z linkami do spec.md i plan.md
- [ ] Diff < 800 linii (jeśli więcej, rozbij PR)

## Build & dokumentacja

- [ ] `mvn clean install` zielone na lokalnej maszynie
- [ ] CI zielone (build + testy)
- [ ] README zaktualizowany z przykładami użycia
- [ ] CHANGELOG ma wpis dla tej zmiany (jeśli projekt go ma)
