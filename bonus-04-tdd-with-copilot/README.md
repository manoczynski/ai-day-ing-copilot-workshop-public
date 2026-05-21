# Bonus 04 - Test-Driven Development z Copilotem

> **NIE omawiane na warsztacie.** Materiał do samodzielnej eksploracji.

## Po co to jest

W głównym ćwiczeniu warsztatu (Blok 4, krok 4) używamy `@workspace wygeneruj testy` jako weryfikację refaktoru. To wystarcza dla 25-minutowego ćwiczenia, ale w realnej pracy bankowej testy są punktem wyjścia, nie weryfikacją na końcu.

Ten bonus pokazuje, jak używać Copilota w cyklu **TDD** (test-driven development) zamiast w trybie "wygeneruj testy po fakcie". Cykl jest stary jak Kent Beck, ale Copilot zmienia w nim jedną rzecz: pisanie testów przestaje być narzutem, bo pierwszy szkielet generuje agent.

## Dlaczego TDD z Copilotem ma sens w banku

- **Regulacja**: DORA i audyt wewnętrzny wymagają udokumentowanego pokrycia testami. Testy napisane PRZED kodem są bardziej zgodne z wymaganiami niż dorobione na koniec.
- **Bezpieczeństwo refaktoru**: bez testów charakteryzujących każda zmiana w legacy może niezauważalnie zmienić zachowanie. To w bankowym kodzie wybuchowo droga awaria.
- **Specyfikacja przez przykład**: testy parametryzowane z fixturami CSV są często czytelniejsze niż 3 strony dokumentacji.
- **Edge cases jako twardy wymóg**: 0%, kwoty graniczne, daty końca miesiąca, ujemne wartości. W bankowości to nie "ciekawostki", tylko codzienność.

## Co jest w tym katalogu

```
bonus-04-tdd-with-copilot/
├── README.md          ← jesteś tutaj
├── HOW_TO_USE.md      ← cykl Red-Green-Refactor z Copilotem
├── EXERCISES.md       ← 5 progresywnych ćwiczeń
├── prompts.md         ← biblioteka promptów do testów
└── examples/
    └── README.md      ← 4 bankowe przykłady do samodzielnej pracy
```

## Quick start

```bash
# 1. Otwórz w VS Code workspace na root repo
code .

# 2. Przeczytaj cykl pracy
cat bonus-04-tdd-with-copilot/HOW_TO_USE.md

# 3. Wybierz pierwsze ćwiczenie
cat bonus-04-tdd-with-copilot/EXERCISES.md

# 4. Wykonuj w pair programming (1 godz. łącznie wszystkie 5 ćwiczeń)
```

## Filozofia

> Najpierw pisz, co kod ma robić. Potem pisz, jak ma to robić.

W praktyce z Copilotem:

1. Opisz wymaganie w 3-5 zdaniach
2. Poproś o testy, które zweryfikują to wymaganie
3. Przejrzyj testy i akceptuj jako specyfikację
4. Dopiero potem poproś o implementację, która te testy przejdzie
5. Refactor zarówno kodu, jak i testów

Różnica od "wygeneruj testy po fakcie": testy nie są dodatkiem do działającego kodu, tylko **definicją** tego, co kod ma robić. Copilot pomaga w obu kierunkach, ale jakość wyniku zależy od kolejności.

## Jak to pasuje do reszty warsztatu

- **Blok 4** głównego ćwiczenia używa testów do weryfikacji refaktoru. Ten bonus pokazuje, jak je pisać PRZED refaktorem (characterization tests, ćwiczenie 2).
- **Bonus 01 (Spec Kit)** używa testów jako kryterium akceptacji w `spec.md`. Ten bonus pokazuje, jak implementować to kryterium w praktyce.
- **Bonus 02 (Skills)**: skill `banking-code-review` wymaga AssertJ + JUnit 5 + parametryzowanych testów. Ten bonus jest praktycznym uzasadnieniem tych konwencji.
