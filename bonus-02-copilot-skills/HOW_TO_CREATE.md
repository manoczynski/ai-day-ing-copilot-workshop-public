# HOW_TO_CREATE - Własny Copilot Skill

## Budowa skilla

```
.github/skills/<skill-name>/
└── SKILL.md
```

Czasem dodaje się też `examples/`, `scripts/`, `references/` w katalogu skilla. Copilot widzi te pliki i może je czytać podczas używania skilla.

## Plik SKILL.md

### Frontmatter (obowiązkowy)

```yaml
---
name: <unique-name>
description: <kiedy ten skill ma się aktywować - opis dla agenta>
---
```

`description` to **najważniejsza linia całego pliku**. Agent czyta tylko nazwę i opis (description), decydując, czy załadować skill. Zły opis oznacza, że skill nigdy się nie aktywuje albo aktywuje się, gdy nie trzeba.

**Dobra description:**
> Use when reviewing Java code in banking context - checks for BigDecimal precision, PII leaks, JUnit 5 vs JUnit 4, conventional commits

**Zła description:**
> Helps with code review

### Body (markdown)

Brak sztywnej struktury. Sprawdzone sekcje:

- **When to use** - sytuacje, w których skill jest właściwy
- **When NOT to use** - sytuacje, gdy NIE należy go używać (równie ważne)
- **Checklist** - konkretna lista rzeczy do zrobienia
- **Anti-patterns** - przykłady kodu z komentarzem dlaczego są złe
- **Examples** - input/output examples (1-3)

## Reguły jakości

### 1. Skill robi JEDNĄ rzecz dobrze

Nie pakuj "code review + refaktor + testowanie" do jednego skilla. Rozbij na trzy.

### 2. Description ≥ tytuł

Tytuł mówi "co". Description mówi **kiedy**. Bez kiedy, agent nie wie, kiedy aktywować.

### 3. Pełne zdania zamiast wypunktowań (zależnie od kontekstu)

Skill bywa wczytywany w środku długiej rozmowy. Krótkie wypunktowania się gubią. Pełne zdania z uzasadnieniem są bezpieczniejsze.

### 4. Pokazuj negatywy

> ❌ Nie używaj `Mockito.mock(BigDecimal.class)`. To value object, mockowanie zaciemnia.
> ✅ Użyj prawdziwych instancji: `new BigDecimal("100.00")`.

To jest bezcenne, bo agent inaczej spróbuje "być pomocny" i zaproponuje bzdurę.

### 5. Skill ma test

Po napisaniu skilla uruchom agenta na 3 reprezentatywnych scenariuszach. Czy aktywuje się? Czy daje sensowne odpowiedzi? Iteruj.

## Jak debugować skill, który się nie aktywuje

1. **Czy `description` zawiera słowa kluczowe** z promptu użytkownika?
2. **Czy `description` jest specyficzny**? "Use when reviewing code" nie wystarczy. Agent ma 50 skilli, każdy z "reviewing code".
3. **Czy nie kolidujesz z innym skillem**? Może agent wybrał inny, bo jego opis jest trafniejszy.
4. **Czy włączyłeś skille w ustawieniach**?

## Hierarchia: skills vs AGENTS.md vs copilot-instructions.md

| Plik | Kiedy się aktywuje | Zawartość |
|---|---|---|
| User prompt files (VS Code Settings, `chat.instructionsFilesLocations`) | Zawsze | Preferencje użytkownika (poza repo; w Copilot CLI tę samą rolę gra `~/.copilot/copilot-instructions.md`) |
| `AGENTS.md` (root) | Zawsze, dla tego repo | Konwencje obowiązujące w całym warsztacie |
| `.github/copilot-instructions.md` | Zawsze, dla tego repo | Konfiguracja konkretnego repo |
| `.github/instructions/*.instructions.md` | Po dopasowaniu wzorca (np. `*.java`) | Konwencje dla danego typu pliku |
| `.github/skills/*/SKILL.md` | **Na żądanie, gdy `description` pasuje** | Ekspertyza dla danego zadania |

Zasada praktyczna:
- Zawsze prawdziwe → `AGENTS.md` lub `copilot-instructions.md`
- Czasem prawdziwe → skill

## Kontrola dostępu

W organizacji enterprise lub business administrator może wymagać białej listy zatwierdzonych skilli. Sprawdź politykę, zanim wgrasz skill do `.github/skills/` w repo banku.

## Dodatkowe zasoby

- [Dokumentacja GitHub - Copilot Skills](https://docs.github.com/copilot) (sprawdź aktualną sekcję)
- Katalog `examples/` w tym repo: 4 pomysły na własne skille
