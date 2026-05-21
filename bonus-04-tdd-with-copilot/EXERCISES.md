# EXERCISES - 5 progresywnych ćwiczeń TDD

5 ćwiczeń, łącznie około 1 godziny. Każde ćwiczenie wprowadza jedną nową technikę. Wykonuj kolejno: każde następne zakłada, że poprzednie jest opanowane.

Wszystkie ćwiczenia używają stosu Java 17 + Maven + JUnit 5 + AssertJ. Pełne setupy: zobacz `examples/README.md`.

---

## Ćwiczenie 1 - Spec → Test → Code (czysty cykl TDD)

**Cel:** Poczuć cykl Red-Green-Refactor w pełnym zakresie, z Copilotem generującym szkielet, ale tobą podejmującym decyzje.
**Czas:** około 15 minut.

### Setup

Nowy katalog poza repo warsztatowym, otwarty jako VS Code workspace. `pom.xml` z Java 17, JUnit 5 i AssertJ (skopiuj z `04-main-exercise/pom.xml`).

### Wymaganie

> **Walidator PESEL**. Metoda `boolean isValid(String pesel)` zwraca `true` dla poprawnego PESELa, `false` dla niepoprawnej sumy kontrolnej. Rzuca:
> - `NullPointerException` dla null
> - `IllegalArgumentException` dla pustego napisu, długości innej niż 11 znaków, znaków niebędących cyframi
>
> Suma kontrolna PESEL: pomnóż każdą z 10 pierwszych cyfr przez wagi [1,3,7,9,1,3,7,9,1,3], zsumuj, weź modulo 10. Cyfra kontrolna (11) powinna być równa `(10 - suma % 10) % 10`.

### Krok 1 (Red): testy najpierw

W Copilot Chat (Agent Mode), pusty plik testowy `PeselValidatorTest.java`:

```
Wczytaj wymaganie z komentarza nad plikiem [albo wklej powyżej].
Wygeneruj testy JUnit 5 + AssertJ. Naming should_<X>_when_<Y>.
Pokryj:
- 3 poprawne PESELe (różne lata)
- 2 PESELe z błędną sumą kontrolną
- null → NPE
- pusty napis → IAE
- 5-cyfrowy → IAE
- 11 znaków z literą → IAE
NIE generuj jeszcze implementacji klasy. Tylko testy.
```

Uruchom `mvn test`. Powinno być wszystko czerwone (brak klasy `PeselValidator`).

### Krok 2 (Green): implementacja

```
Wygeneruj klasę PeselValidator, która przechodzi te testy.
Brak optymalizacji, najprostsza implementacja zgodna z AGENTS.md.
```

`mvn test`. Iteruj, aż wszystkie zielone.

### Krok 3 (Refactor)

```
Wydziel obliczanie sumy kontrolnej do prywatnej metody computeChecksum.
Wydziel walidację formatu do prywatnej metody validateFormat.
Zachowaj zielony wynik testów.
```

### Lekcja

TDD nie polega na pisaniu testów po kodzie. Polega na tym, że testy są twoją specyfikacją. Copilot pomaga w obu krokach, ale różnica jest w tobie: czy patrzysz na test jako na specyfikację (test najpierw), czy jako na weryfikację (kod najpierw).

---

## Ćwiczenie 2 - Characterization tests przed refactorem legacy

**Cel:** Zabezpieczyć refactor legacy testami, które fiksują **bieżące zachowanie**, nawet jeśli jest dziwne.
**Czas:** około 15 minut.

### Setup

Otwórz w VS Code: `04-main-exercise/src/main/java/com/n8/workshop/CalculateInterest.java`. To legacy kod z głównego ćwiczenia warsztatu.

Załóż, że masz zrefaktorować ten kod (cel: Blok 4 warsztatu). Ale **najpierw** zabezpieczasz testami.

### Krok 1: charakteryzacja zamiast specyfikacji

```
#file:CalculateInterest.java

Wygeneruj characterization tests dla wszystkich metod publicznych.
Zasady:
- Testy fiksują BIEŻĄCE zachowanie, nawet jeśli wydaje się dziwne (double, magic numbers)
- NIE poprawiaj kodu, nawet jeśli widzisz błąd
- Asercje na konkretne wartości double (użyj isCloseTo z offsetem 0.001 dla porównań)
- Pokryj 3 typy konta (R, S, P) i 2 okresy (1 rok, 5 lat)
- Pokryj progi opłat (49999, 50000, 50001, 99999, 100000, 100001)
- Pokryj wartości graniczne dla principal=0
```

### Krok 2: uruchom testy na BIEŻĄCYM kodzie

`mvn test`. Wszystkie powinny być zielone (bo testy są dopasowane do bieżącego zachowania, nawet z bugami).

### Krok 3: refactor z safety net

Teraz możesz refaktorować `CalculateInterest.java` (główne ćwiczenie warsztatu). Po każdym kroku refaktoru uruchamiasz `mvn test`. Jeśli zielone, refaktor nie zmienił zachowania. Jeśli czerwone, masz regresję - cofnij.

### Lekcja

Charakteryzacyjne testy to **zamrażacz zachowania**. Nie sprawdzają, czy zachowanie jest poprawne. Sprawdzają, czy się nie zmieniło. Dla legacy kodu z 8 lat to jedyna bezpieczna podstawa refaktoru. Copilot pisze je w 2 minuty zamiast w 2 godziny ręcznie.

### Pułapka

Jeśli legacy ma bug, charakteryzacyjne testy zamrażają też bug. To **OK na etapie refaktoru** (bo refaktor nie zmienia zachowania). Po refaktorze, **jako osobny commit**, możesz naprawić buga - wtedy odpowiedni test się czerwieni, a ty świadomie go zmieniasz (i dodajesz nowy test fiksujący poprawne zachowanie).

---

## Ćwiczenie 3 - Testy parametryzowane z fixturami CSV

**Cel:** Pokazać tabelę przypadków zamiast 15 osobnych testów. Czytelnie i skalowalnie.
**Czas:** około 10 minut.

### Setup

Kontynuuj z ćwiczenia 1 albo nowy projekt z walidatorem IBAN.

### Wymaganie

> Walidator IBAN PL. Sprawdza długość 28 znaków, kod kraju PL, sumę kontrolną mod-97.

### Krok 1: parametryzowane testy

```
Wygeneruj testy dla IbanValidator.isValid jako @ParameterizedTest z @CsvFileSource.
Plik fixtury: src/test/resources/iban-cases.csv

Format CSV:
iban,expected,description

15 case'ów:
- 3 poprawne PL IBAN (różne banki)
- 3 IBAN z błędną sumą kontrolną
- 2 IBAN za krótki
- 2 IBAN za długi
- 1 IBAN z literą zamiast cyfry
- 1 pusty napis
- 1 same spacje
- 2 z DE/FR (poza scope PL → false albo wyjątek)

Wygeneruj plik CSV razem z testem.
```

### Krok 2: weryfikacja

```bash
mvn test
```

### Lekcja

Testy parametryzowane skalują się tam, gdzie listowanie ręczne nie. Dodanie 5 case'ów = 5 linii w CSV, zamiast 5 nowych metod `@Test`. Dla banku z dziesiątkami progów, walut, typów kont, to różnica między 200-linijkową klasą testową a 30-linijkową plus 200-linijkowy CSV.

### Anti-pattern

NIE generuj parametryzowanych testów dla rzeczy jakościowo różnych. Jeden test parametryzowany = jedna **logika**, wiele danych. Jeśli case A testuje walidację, B testuje suma kontrolna, C testuje normalizację - to są 3 różne testy, każdy może być parametryzowany niezależnie.

---

## Ćwiczenie 4 - Test odtwarzający bug, potem fix

**Cel:** Cykl bug → test → fix → regresja. Najczęstszy realny scenariusz TDD w utrzymaniu.
**Czas:** około 10 minut.

### Setup

Załóż, że klient zgłosił bug: "dla pożyczki 100 000.50 zł na 3 lata ze stopą 5%, kalkulator zwraca 115 763.09 zł, a powinien 115 763.08 zł (z Excela księgowej). Różnica 1 grosz. Audyt księgowy wskazuje na błąd zaokrąglania per iteracja w pętli (implementacja robi `setScale(2, HALF_UP)` po KAŻDYM roku zamiast tylko na końcu, co kumuluje błędy zaokrągleń)."

**Math weryfikacja (na potrzeby ćwiczenia, sprawdzone w Decimal pełnej precyzji):**
- Pełna precyzja (Excel): `100000.50 × 1.05^3 = 100000.50 × 1.157625 = 115763.0788125` → HALF_UP scale 2 = `115763.08`
- Per-iteracja (buggy): Y1 = `105000.525 → 105000.53`, Y2 = `110250.5565 → 110250.56`, Y3 = `115763.088 → 115763.09`
- Różnica: 1 grosz. Dla dłuższych okresów (np. 30 lat) i mniej regularnych frakcji różnica rośnie do kilku-kilkunastu groszy. To realny audytowy bug.

### Krok 1: odtwórz buga jako test

```
Mam zgłoszenie błędu (różnica 1 grosz w stosunku do Excela):
- Wejście: principal=100000.50, annualRate=0.05, years=3
- Oczekiwane: 115763.08 (Excel: P × (1+r)^n, zaokrąglone na końcu, scale 2 HALF_UP)
- Rzeczywiste: 115763.09 (implementacja zaokrągla setScale(2, HALF_UP) po KAŻDYM roku)
- Hipoteza: kumulacja błędu zaokrąglenia w pętli

Wygeneruj failing test JUnit 5 odtwarzający ten case. Test ma NAZYWAĆ się
should_match_excel_pmt_for_known_case_loan_100000_50_3y_5pct.
Asercja: assertThat(result).isEqualByComparingTo("115763.08").
Komentarz nad testem wskazuje na ticket #INC-12345 (placeholder).
```

### Krok 2: uruchom test, powinien być czerwony

```bash
mvn test
```

### Krok 3: poproś o fix

```
Test should_match_excel_pmt_for_known_case_loan_100k_5y_7pct nie przechodzi.
Hipoteza: błąd zaokrąglania w ostatniej iteracji.
Sprawdź metodę calculate, popraw zaokrąglanie tak, żeby ostatni wynik
zgadzał się z wartością z testu.
NIE zmieniaj testu.
```

### Krok 4: regresja

Po fixie uruchom **wszystkie** testy:

```bash
mvn test
```

Wszystkie powinny być zielone. Nowy test pokrywa naprawiony case, stare testy pokrywają stare przypadki - jeśli któreś się zaczerwieniło, mamy regresję i trzeba cofnąć.

### Lekcja

Test odtwarzający bug PRZED fixiem to fundamentalna higiena. Daje 2 gwarancje: (1) bug został faktycznie naprawiony (nie tylko zniknął na chwilę), (2) bug nie wróci niezauważony za 6 miesięcy. Bez tego testu fix jest "wiarą prowadzącego", nie regresją.

---

## Ćwiczenie 5 - Bezpieczne fixtury bez PII

**Cel:** Pokazać, jak Copilot domyślnie generuje realistyczne PII, i jak go od tego odwieść.
**Czas:** około 5 minut.

### Setup

Załóż AGENTS.md w projekcie z regułą: **"fixtury testowe nie używają realistycznych PESEL, IBAN, numerów kont, imion. Używaj `00000000000`, `PL00 0000 0000 0000 0000 0000 0000`, `Jan Testowy`, `1234` jako last 4 digits."**

### Krok 1: prompt bez konwencji

W **nowym wątku** (Copilot bez kontekstu AGENTS.md - symulujemy programistę, który zapomniał):

```
Wygeneruj fixture danych dla CustomerRepository: 5 klientów z PESEL, imieniem, IBAN.
```

**Obserwuj wynik**: Copilot wygeneruje realistyczne PESEL (np. `87010112345`), realistyczne IBAN (z poprawną sumą kontrolną), prawdziwie brzmiące imiona.

### Krok 2: prompt z konwencją

W **nowym wątku**, z AGENTS.md w workspace:

```
Wygeneruj fixture danych dla CustomerRepository: 5 klientów z PESEL, imieniem, IBAN.
Trzymaj się AGENTS.md.
```

**Obserwuj różnicę**: Copilot powinien użyć `00000000000`, `PL00 0000 …`, `Jan Testowy`.

### Krok 3: review

W realnym repo: skill `pii-mask` z `bonus-02-copilot-skills/examples/README.md` opisuje, jak zautomatyzować takie sprawdzenie w code review.

### Lekcja

Copilot domyślnie pisze tak, jak widział w internecie, a w internecie są realistyczne dane (bo to działa na blogach i tutorialach). W bankowym kodzie produkcyjnym to ryzyko zgodności. AGENTS.md albo skill flagujący PII to nie kosmetyka, tylko granica między compliance a zgłoszeniem incydentu.

### Anti-pattern

NIE generuj fixtur produkcyjnych przez Copilota bez code review po stronie człowieka. Nawet z AGENTS.md może się pomylić. Każda fixtura PII = sprawdzenie ręczne.

---

## Co dalej

- Powtórz ten cykl z **własną** klasą domeny w swoim projekcie (1-2 godziny)
- Dodaj cycle TDD do swojego pre-commit hooka (np. `mvn test` w `.husky/pre-commit` albo `lefthook`)
- Zobacz `bonus-02-copilot-skills/examples/README.md` punkt `pii-mask` - jak zautomatyzować ćwiczenie 5 na całe repo
