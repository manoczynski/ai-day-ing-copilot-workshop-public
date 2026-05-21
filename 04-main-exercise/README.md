# 01 - Główne ćwiczenie: refaktoryzacja `CalculateInterest.java`

★ **To jest kluczowe ćwiczenie warsztatu (Blok 4, 25 minut).**

## Co robimy

Bierzemy typowy starszy kod bankowy: `CalculateInterest.java` (ok. 80 linii, double, magic numbers, magic strings, mieszane odpowiedzialności). Refaktoryzujemy go z pomocą GitHub Copilota w pair programming, według 5-krokowego scenariusza.

## Po co

- Zobaczyć **różnicę** między promptem "z biegu" a promptem z kontekstem (`AGENTS.md` + `#selection` + Plan Mode)
- Zobaczyć **Agent Mode** w działaniu (Cmd+Shift+I → przełącznik na Agent)
- Wyrobić nawyk **human-in-the-loop**: każda zmiana wymaga akceptacji
- Zobaczyć, że **drugi przebieg** (`/review`) wyłapuje rzeczy, których człowiek by nie zauważył

## Quick start

Maven uruchamiamy z katalogu `04-main-exercise/`, bo tu jest `pom.xml`. Jeśli VS Code jest otwarty na root repo, najpierw wejdź do tego katalogu:

```bash
cd 04-main-exercise && mvn clean compile
```

Następnie otwórz `EXERCISE_STEPS.md` i jedź według 5 kroków.

## Pliki

- **`src/main/java/com/n8/workshop/CalculateInterest.java`**: **legacy bad code** (do refaktoryzacji)
- **`AGENTS.md`**: konwencje dla tego ćwiczenia (Copilot czyta je automatycznie)
- **`EXERCISE_STEPS.md`**: 5 kroków scenariusza dla pary
- **`reference/CalculateInterest_REFACTORED.md`**: wzorzec, do którego możesz porównać swój wynik (otwórz **po** wykonaniu ćwiczenia, nie wcześniej, żeby nie zepsuć sobie nauki)
- **`src/main/java/com/n8/workshop/iban/IbanValidator.java`**: pusty szkielet, używany w Bloku 3 (Agent Mode + Plan Mode mini-ćwiczenie)
- **`src/main/java/com/n8/workshop/savings/SimpleDepositInterest.java`**: drugie miejsce w repo, w którym liczymy odsetki, używane w demo `#codebase` (Blok 5)

## Reguły dla par

1. **Klikająca osoba**: pisze prompty, akceptuje sugestie. Skupia się na mechanice, nie na krytycznej ocenie. Ocena to zadanie obserwatora.
2. **Obserwator**: dba o krytyczne myślenie. Jeśli Copilot proponuje coś, co łamie konwencje z `AGENTS.md`, **zatrzymaj**.
3. **Zmiana ról** co 5 minut.
4. **Stop, jeśli Copilot myli się trzy razy z rzędu**: to znak, że trzeba dać mu lepszy kontekst, a nie iść głębiej.

## Zasoby

- `../AGENTS.md` (root): konwencje obowiązujące w całym warsztacie
- `../.github/copilot-instructions.md`: konfiguracja Copilota na poziomie repo
