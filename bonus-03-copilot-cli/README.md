# Bonus 03 - GitHub Copilot CLI

> **NIE omawiane na warsztacie.** Materiał do samodzielnej eksploracji.

## Co to jest

**GitHub Copilot CLI** to agent uruchamiany z terminala. Daje Ci Copilota poza VS Code: w pracy z gitem, w skryptach, w pair-debuggingu przez SSH na zdalnym serwerze.

```bash
copilot              # sesja interaktywna
copilot run "wyjaśnij, co robi ten log błędu"
```

Działa z **Twoją istniejącą licencją** Copilot (Pro / Business / Enterprise), nie potrzeba osobnej subskrypcji.

## Po co Ci to

- **Praca w terminalu na pierwszym miejscu**: `git diff | copilot run "zrób review tych zmian"`
- **Skrypty automatyzacji**: Copilot w pliku pipeline lub w `Makefile`
- **Praca na wielu repo**: `copilot run` w `for repo in *; do ...; done`
- **Sesja SSH**: Copilot dostępny tam, gdzie nie ma VS Code
- **Plan Mode w terminalu**: `copilot plan "..."` dla większych zadań

## Co jest w tym folderze

```
bonus-03-copilot-cli/
├── README.md            ← jesteś tutaj
├── HOW_TO_INSTALL.md    ← instalacja krok po kroku
├── EXERCISES.md         ← 4 progresywne ćwiczenia
└── prompts/
    └── example-prompts.md   ← 24 prompty w 8 kategoriach
```

## Quick start (3 minuty)

```bash
# 1. Zainstaluj (wymaga Node 22+ i gh CLI zalogowanego)
npm install -g @githubnext/github-copilot-cli   # nazwa pakietu może się zmienić, patrz docs

# 2. Zaloguj się
copilot auth login

# 3. Sprawdź
copilot --version

# 4. Pierwsze użycie (w katalogu z kodem)
copilot run "co znajduje się w tym repo i jak go uruchomić"
```

Pełna instrukcja: `HOW_TO_INSTALL.md`.

## Filozofia

Copilot CLI to **agent**, nie autouzupełnianie. Pisz prompty jak do współpracownika, nie jak do wyszukiwarki.

- ❌ `copilot run "git diff"`  (Copilot to nie alias dla gita)
- ✅ `copilot run "przejrzyj diff i powiedz, czy są tu PII w testach"`

Dla większych zadań użyj **Plan Mode** (`copilot plan "..."`) PRZED `copilot run`. Plan oddziela **decyzję** od **wykonania**.

## Status produktu

Copilot CLI to świeży produkt (2025). API i nazwa pakietu mogą się zmieniać. Sprawdź [oficjalną dokumentację](https://docs.github.com/copilot/copilot-in-the-cli), żeby poznać aktualną nazwę pakietu i polecenia.
