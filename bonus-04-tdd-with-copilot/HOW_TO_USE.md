# HOW_TO_USE - Cykl TDD z Copilotem

## Cykl Red-Green-Refactor (klasyczny)

```
RED      → napisz test, który nie przechodzi (bo nie ma implementacji)
GREEN    → napisz najprostszą implementację, która test przechodzi
REFACTOR → posprzątaj kod (i testy), zachowując zielony wynik
```

Cykl ma trzydzieści lat. Copilot zmienia w nim jedną rzecz: szkielet każdego kroku generuje się szybciej. Zostaje rygor decyzji, kiedy iść do kolejnego kroku.

## Cykl Red-Green-Refactor z Copilotem (szczegółowo)

### Faza 1: spec do testu

W Copilot Chat (Agent Mode), pusty plik testowy w workspace:

```
Wymaganie: kalkulator złożonych odsetek dla pożyczki bankowej.
Wzór: A = P × (1 + r)^n, gdzie P=kapitał, r=stopa roczna, n=lata.
Walidacje:
- principal musi być nieujemny (BigDecimal, scale 2)
- annualRate musi być w [0, 1] (BigDecimal, scale 6)
- years musi być w [0, 30]
- null dla obiektów BigDecimal → NullPointerException
- wartości poza zakresem → IllegalArgumentException z polskim komunikatem

Wygeneruj testy JUnit 5 + AssertJ, naming should_<X>_when_<Y>.
Pokryj:
- 3 happy path (rate=0%, 5%, 7%)
- edge case principal=0
- edge case years=0
- minimum 4 invalid inputs (null principal, null rate, principal ujemny, rate=1.5)

NIE generuj jeszcze implementacji klasy. Tylko testy.
```

**Co Copilot zwraca**: szkielet pliku testowego z 8-12 testami, każdy z `// given/when/then`, z konkretnymi wartościami w asercjach.

**Co robisz**: czytasz testy jak specyfikację. Każdy test odpowiada na pytanie "co kod ma robić w sytuacji X". Jeśli któryś test wygląda dziwnie, znaczy że Copilot źle zrozumiał wymaganie. Wtedy poprawiasz prompt, nie test.

### Faza 2: implementacja minimalna

Gdy testy akceptujesz:

```
Wygeneruj implementację klasy CompoundInterestCalculator, która przechodzi te testy.
Trzymaj się AGENTS.md (BigDecimal, walidacja, RoundingMode.HALF_UP).
Brak optymalizacji - najprostsza implementacja, która zwraca poprawne wartości.
```

**Co Copilot zwraca**: klasę z metodą `calculate(BigDecimal principal, BigDecimal annualRate, int years)`.

**Co robisz**: uruchamiasz `mvn test`. Jeśli zielone, super. Jeśli czerwone, **nie zmieniaj testów**. Wracaj do Copilota z konkretnym komunikatem:

> Test `should_throw_when_rate_above_one` oczekuje IllegalArgumentException dla rate=1.5, a implementacja go nie rzuca. Popraw walidację w klasie.

### Faza 3: refactor

Gdy zielone:

```
Refactor: wydziel walidację do osobnej metody prywatnej validateInputs.
Wydziel pętlę odsetek do metody compoundFactor.
Zachowaj zielony wynik testów.
```

**Co Copilot zwraca**: zrefaktorowaną wersję klasy. Testy nadal przechodzą (sprawdź).

**Co robisz**: kolejna iteracja `mvn test`. Jeśli zielone i kod czytelniejszy, akceptujesz. Jeśli refactor zepsuł testy, **wracasz**, nie poprawiasz testów.

## Kiedy używać TDD z Copilotem

### ✅ Dobre dopasowanie

- **Pure domain** (kalkulatory, walidatory, modele) - łatwe do przetestowania w izolacji
- **Algorytm z jasnymi regułami** (suma kontrolna IBAN, podatek progresywny, harmonogram spłat)
- **Refactor legacy** - charakteryzacyjne testy przed zmianą, ćwiczenie 2
- **Bug fix** - test odtwarzający bug, potem fix, ćwiczenie 4

### ❌ Słabe dopasowanie

- **Integracja z zewnętrznym API** (testy mockujące dają fałszywe poczucie pokrycia)
- **UI / frontend** (TDD na widoki to torture, lepsze testy E2E na końcu)
- **Eksploracja / spike** (jeszcze nie wiesz, co kod ma robić, więc nie wiesz, co testować)
- **Pierwszy commit do projektu** (nie znasz konwencji projektu, najpierw obserwuj)

## Pułapki TDD z AI

### Pułapka 1: Copilot pisze testy, które przechodzą zawsze

Przykład: `assertThat(result).isNotNull()` jako jedyna asercja. Test zielony zawsze, niezależnie od logiki.

**Co robić**: czytaj asercje. Każda powinna fiksować konkretną wartość albo zachowanie. Jeśli widzisz `isNotNull` bez kontekstu, poproś:

> Asercja `isNotNull` jest za słaba. Zamień na asercję z konkretną wartością BigDecimal, oczekiwaną na podstawie wzoru.

### Pułapka 2: Copilot zmienia testy, żeby przeszły

Jeśli w Fazie 2 Copilot dostaje czerwony test i implementację, czasem proponuje zmianę **testu** zamiast **implementacji**. To anti-pattern. Wprost zabraniaj:

> Test jest poprawny i odpowiada wymaganiu. Popraw implementację, nie test.

### Pułapka 3: Testy parametryzowane bez różnorodności

Copilot wygeneruje 5 wariantów `@ParameterizedTest` z bardzo podobnymi danymi. Brakuje edge cases.

**Co robić**: po wygenerowaniu pytasz wprost:

> Dodaj 5 brakujących edge cases do tego parametryzowanego testu. Konkretnie: wartości graniczne progów, zero, wartości maksymalne (BigDecimal precision), polskie znaki w napisach (gdzie to ma sens).

### Pułapka 4: Fixtury z PII

Copilot z biegu napisze fixturę z PESEL `12345678901`. To prawdziwy format, więc test "wygląda realnie", ale jeśli wyciekuje przez logi audytu, mamy problem.

**Co robić**: w `AGENTS.md` (lub w planowanym skillu `pii-mask`, zobacz `bonus-02-copilot-skills/examples/README.md` punkt 3) napisz wprost: "fixtury testowe nie używają realistycznych PESEL, IBAN, numerów kont. Używaj `00000000000`, `PL00 0000…` albo wyraźnie sztucznych wzorców."

## Następny krok

Otwórz `EXERCISES.md` i wybierz pierwsze ćwiczenie. Sugerowana kolejność: 1 → 2 → 3 → 4 → 5.
