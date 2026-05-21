# Biblioteka promptów - TDD z Copilotem

Gotowe do skopiowania prompty do każdego etapu cyklu Red-Green-Refactor. Wszystkie przetestowane na Java 17 + JUnit 5 + AssertJ. Konwencja: `<placeholder>` zastąp swoją wartością.

---

## Kategoria 1 - Generowanie testów z wymagania

### 1.1 Testy z opisu funkcjonalnego

```
Wymaganie: <2-5 zdań opisu funkcjonalności w języku biznesowym>

Walidacje:
- <warunek 1>
- <warunek 2>
...

Wygeneruj testy JUnit 5 + AssertJ dla klasy <Nazwa>, której jeszcze NIE MA w projekcie.
Naming testów: should_<oczekiwany>_when_<warunek>.
Pokryj: <N> happy path + <M> edge cases + <K> invalid inputs.
NIE generuj jeszcze implementacji klasy. Tylko testy.
```

### 1.2 Testy z wzoru matematycznego

```
Wzór: <wzór z dokumentu specyfikacyjnego>

Granice:
- <zmienna 1>: <zakres>
- <zmienna 2>: <zakres>

Wygeneruj testy JUnit 5 + AssertJ dla klasy <Nazwa>, która oblicza ten wzór.
Każdy test ma w komentarzu cytat ze wzoru, dla którego sprawdza ten przypadek.
Asercje na konkretne wartości BigDecimal z dokładnością do 0.01.
Pokryj minimum 3 znane cases z dokumentacji (jeśli są podane) plus edge cases na granicach.
```

### 1.3 Testy z kontraktu API

```
Endpoint: <METHOD /path>
Request body: <JSON schema>
Response body: <JSON schema>
Status codes: <lista z opisem>

Wygeneruj testy @WebMvcTest dla <NazwaController>, które weryfikują kontrakt.
Pokryj:
- happy path z poprawnym body
- walidacja: <pole> jest null
- walidacja: <pole> jest poza zakresem
- 401 dla braku tokenu (jeśli endpoint chroniony)
- 404 dla nieistniejącego zasobu
Użyj MockMvc + AssertJ.
```

---

## Kategoria 2 - Charakteryzacja legacy

### 2.1 Characterization tests dla pojedynczej klasy

```
#file:<LegacyClass>.java

Wygeneruj characterization tests dla wszystkich metod publicznych.
Zasady:
- Testy fiksują BIEŻĄCE zachowanie, nawet jeśli wydaje się dziwne
- NIE poprawiaj kodu, nawet jeśli widzisz oczywisty błąd (to robimy w osobnym kroku po refaktorze)
- Dla porównań double użyj isCloseTo(value, offset(0.001))
- Pokryj <N> typów wejścia, edge cases <opis>
- Komentarz pod każdym testem: "characterization (current behavior, may include bugs)"
```

### 2.2 Characterization tests z istniejących logów

```
Mam logi produkcyjne dla metody <metoda>:
<wklej 5-10 par input/output z logów>

Wygeneruj characterization tests, które dla każdego pary z logu sprawdzają,
czy implementacja zwraca tę samą wartość. Format: @ParameterizedTest z @MethodSource.
```

### 2.3 Cross-check z arkuszem księgowej

```
Mam plik <arkusz.csv> z 20 wierszami w formacie:
principal,years,rate,expected_result

Każdy wiersz to znany case z arkusza księgowej.
Wygeneruj test @ParameterizedTest z @CsvFileSource("arkusz.csv"), który sprawdza,
czy InterestCalculator.calculate zwraca wartość zgodną z expected_result
z dokładnością do 0.01 PLN.
```

---

## Kategoria 3 - Parametryzacja

### 3.1 Inline parameterized

```
W klasie <TestClass> jest <N> bardzo podobnych testów: <test1>, <test2>...
Przepisz je na pojedynczy @ParameterizedTest z @CsvSource.
Zachowaj wszystkie asercje, dodaj nazwę dynamiczną przez @DisplayName.
```

### 3.2 CSV file source

```
Wygeneruj test @ParameterizedTest z @CsvFileSource dla <metoda>.
Plik fixtury: src/test/resources/<nazwa>.csv
Kolumny: <kolumna1>, <kolumna2>, expected, description

Wygeneruj plik CSV razem z testem. Minimum <N> wierszy pokrywających:
- <kategoria 1>
- <kategoria 2>
- <kategoria 3>
```

### 3.3 Method source z budowanymi obiektami

```
Wygeneruj test @ParameterizedTest z @MethodSource("<methodName>") dla <metoda>.
Method source zwraca Stream<Arguments> z 5 wariantami <NazwaTypu>:
<wariant 1>
<wariant 2>
...
Każdy Arguments ma 3 pola: input, expected, description.
```

---

## Kategoria 4 - Bug fix workflow

### 4.1 Test odtwarzający buga

```
Mam zgłoszenie błędu:
- Wejście: <konkretne wartości>
- Oczekiwane: <wartość, z dokumentu lub uzgodnione z biznesem>
- Rzeczywiste: <wartość, którą zwraca obecna implementacja>
- Hipoteza: <opcjonalnie, jeśli wiesz>

Wygeneruj failing test JUnit 5 odtwarzający ten case.
Nazwa testu wskazuje na ticket: should_match_<X>_for_known_case_<short_id>.
Komentarz nad testem ma odnośnik do ticketu.
```

### 4.2 Fix po failing tescie

```
Test <NazwaTest> nie przechodzi. Hipoteza: <opis>.
Sprawdź <klasa/metoda>, popraw <co konkretnie>, żeby ten test przeszedł.
NIE zmieniaj testu.
Po fixie uruchom wszystkie testy i sprawdź, czy nie ma regresji.
```

### 4.3 Test regresji po publicznym CVE

```
Mam CVE <numer>: <skrócony opis luki>.
Obecna implementacja w <klasa> jest podatna.

Wygeneruj test, który:
- Próbuje wykorzystać podatność na obecnej implementacji
- Powinien się czerwienić (bug obecny)
- Po naprawie luki powinien być zielony

Nazwa testu: should_reject_<atak>_attempt.
```

---

## Kategoria 5 - Refactor testów

### 5.1 Konsolidacja duplikatów

```
W klasie <TestClass> jest <N> testów z bardzo podobnym kodem given/when/then.
Wyciągnij wspólny kod do @BeforeEach albo do prywatnej metody helper.
Zachowaj wszystkie asercje, zachowaj zielony wynik mvn test.
```

### 5.2 Custom AssertJ assertion

```
W projekcie mam <N> testów, które robią ten sam zestaw asercji na obiekcie <Typ>:
<lista asercji>

Wygeneruj custom assertion w klasie <TypAssert> rozszerzający
AbstractAssert<TypAssert, Typ>.
Przepisz testy żeby używały nowej asercji.
```

### 5.3 Zamiana mocków na fakes

```
W klasie <TestClass> używamy Mockito.mock dla <interfejs>.
Zamień mocki na in-memory implementację <Interfejs> jako klasę testową
<InterfejsFake>, która trzyma stan w HashMapie.
Plus: zaktualizuj testy.
```

---

## Kategoria 6 - Property-based testing (jqwik)

### 6.1 Property na całym domenowym zakresie

```
Wygeneruj test property-based (jqwik) dla <metoda>.
Property: <opis własności matematycznej>, np.:
- "Suma odsetek miesięcznych = odsetki roczne ± 1 grosz"
- "Wynik dla principal=0 to zawsze 0"
- "calculate(P, r, n) > P dla r > 0 i n > 0"

Użyj @Property z @ForAll i własnymi @Provide generatorami,
które trzymają wartości w realistycznym zakresie (BigDecimal scale 2, 0-1_000_000).
```

### 6.2 Property z stress testem

```
Wygeneruj jqwik property test dla <metoda>:
- @Property(tries = 1000)
- @ForAll generatory dla wszystkich parametrów
- Asercja: metoda NIE rzuca ArithmeticException dla żadnego poprawnego inputu

Cel: znaleźć edge case'y, których ja nie pomyślałem.
```

---

## Kategoria 7 - Bezpieczeństwo fixtur

### 7.1 PESEL audit w testach

```
Przeszukaj wszystkie pliki .java w src/test, znajdź miejsca z 11-cyfrowymi
sekwencjami w stringach (potencjalne realistyczne PESEL).
Zaproponuj zamianę na fixturę bez PII: "00000000000", "11111111111",
albo placeholder typu "PESEL_TEST_<numer>".
```

### 7.2 IBAN audit w testach

```
Przeszukaj wszystkie pliki .java w src/test, znajdź IBAN-y (PL\d{26}, DE\d{20}).
Sprawdź, które mają poprawną sumę kontrolną (czyli mogą być realne).
Zaproponuj zamianę na fixturę "PL00 0000 0000 0000 0000 0000 0000".
```

### 7.3 Imię i nazwisko audit

```
Przeszukaj pliki .java w src/test, znajdź pola typu String name/firstName/lastName
z wartościami przypominającymi imiona polskie (typowe formaty Jan/Anna/Krzysztof).
Zaproponuj zamianę na "Jan Testowy", "Anna Testowa" albo "TEST_USER_<id>".
```

---

## Zasady pisania własnych promptów do testów

1. **Spec PRZED implementacją.** Najpierw wymaganie, potem testy, dopiero potem kod. Kolejność ma znaczenie.
2. **Wartości konkretne w asercjach.** `assertThat(x).isEqualByComparingTo("100.00")` lepsze niż `isNotNull`.
3. **Naming `should_<X>_when_<Y>`.** Czytelność testów to twoja żywa dokumentacja; reviewer dowiaduje się z nazwy, co kod ma robić.
4. **Każdy test = jedna własność.** Nie pakuj 5 asercji do jednego testu, jeśli każda fiksuje inne zachowanie.
5. **AssertJ, nie Hamcrest.** AssertJ ma fluent API, Hamcrest ma assertion error messages z ery JUnit 4.
6. **`isEqualByComparingTo` dla BigDecimal.** `isEqualTo` zwróci false dla 1.00 vs 1.0, bo porównuje scale.
