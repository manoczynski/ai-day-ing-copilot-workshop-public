# Constitution - Loan Calculator

Zasady projektu, których agent AI nie może łamać. Każde naruszenie musi być uzasadnione w PR description.

## 1. BigDecimal dla finansów

Każda kwota, stawka, procent → `java.math.BigDecimal`. NIGDY `double` lub `float`.

- Scale **2** dla kwot pieniężnych
- Scale **6** dla stawek procentowych
- `RoundingMode.HALF_UP` jako wartość domyślna

## 2. Walidacja danych wejściowych

Każda metoda publiczna waliduje argumenty na wejściu:

- `null` → `NullPointerException` z opisem (użyj `Objects.requireNonNull`)
- Wartość ujemna, gdy musi być dodatnia → `IllegalArgumentException`
- Komunikat błędu po polsku, z wartością, która spowodowała błąd

## 3. Testy są częścią definicji "done"

Żaden task nie jest skończony bez testów:

- JUnit 5 + AssertJ (NIE JUnit 4, NIE Hamcrest)
- Pokrycie: happy path + min 3 edge cases + invalid inputs
- Naming: `should_<oczekiwany>_when_<warunek>()`
- Bez Mockito w warstwie domain (czysta logika)

## 4. JavaDoc po polsku dla API publicznego

Każda metoda publiczna ma JavaDoc po polsku z `@param`, `@return`, `@throws`. Język polski, bo dokumentacja dla polskiego banku.

## 5. Niemutowalność domyślnie

- `record` dla kontenerów danych
- pola `final` w klasach
- wartości zwracane są niemutowalne (dla kolekcji zwracaj defensywne kopie)

## 6. Brak PII w logach

Logger NIGDY nie zawiera danych osobowych: PESEL, numeru konta, imienia ani nazwiska klienta.

Zamiast PESEL użyj identyfikatora klienta (UUID).
Zamiast numeru konta użyj hasha albo 4 ostatnich cyfr.

## 7. Conventional commits

Format:
- `feat(loan): add monthly schedule calculation (T-08)`
- `fix(loan): correct rounding for last installment (T-15)`
- `refactor(loan): extract InstallmentScheduler (T-12)`
- `test(loan): add edge cases for zero principal (T-09)`
- `docs(loan): update README with examples`

Każdy commit odnosi się do konkretnego ID zadania z `tasks.md` albo jest oznaczony jako poza zakresem (`chore`, `docs`).
