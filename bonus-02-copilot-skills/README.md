# Bonus 02 - GitHub Copilot Agent Skills

> **NIE omawiane na warsztacie.** Materiał do samodzielnej eksploracji.

## Co to są Skills

**Agent Skills** to plikowe definicje "ekspertyz", które Copilot Agent może załadować na żądanie. Każdy skill to folder pod `.github/skills/<nazwa>/` z plikiem `SKILL.md`, który zawiera:

- **Frontmatter** (YAML) - `name`, `description`, opcjonalnie `triggers`, `tools`
- **Body** (Markdown) - instrukcje, kontekst, przykłady, anti-patterns

Gdy agent rozpozna sytuację, dla której istnieje skill (po `description` i `triggers`), załaduje treść SKILL.md jako dodatkowy kontekst, bez wpychania jej do każdej rozmowy.

To rozbudowana wersja `AGENTS.md`: zamiast jednego pliku dla całego repo masz **bibliotekę ekspertyz** wybieranych dynamicznie.

## Co jest w tym folderze

```
bonus-02-copilot-skills/
├── README.md                  ← jesteś tutaj
├── HOW_TO_CREATE.md           ← jak zrobić własny skill
├── .github/skills/
│   ├── banking-code-review/SKILL.md       ← przykład 1: review bankowy
│   └── java-modernization/SKILL.md        ← przykład 2: Java 8 → 17
└── examples/README.md         ← 4 pomysły na własne skille (zarys, do dokończenia)
```

## Dwa przykłady

### `banking-code-review`

Skill aktywuje się przy code review pliku Java w kontekście bankowym. Sprawdza: BigDecimal precision, PII w logach, walidacja inputów, brak Mockito w domain, AssertJ vs Hamcrest, conventional commits.

### `java-modernization`

Skill aktywuje się przy modernizacji starszego kodu Java 8 / 11 → 17. Sugeruje: `record` zamiast POJO, switch expressions, pattern matching, `var`, text blocks, sealed types, z kontekstem KIEDY tego użyć, a kiedy zostawić, jak jest.

## Status w produktach Copilota

Agent Skills to funkcja wprowadzona w 2025 roku. Może wymagać:
- włączenia w polityce organizacji (przez administratora)
- włączenia w ustawieniach VS Code (`github.copilot.advanced.skills.enabled`)
- odpowiedniej wersji VS Code i rozszerzenia GitHub Copilot Chat

Sprawdź [oficjalną dokumentację](https://docs.github.com/copilot), by zweryfikować aktualny stan.

## Jak tego używać

Patrz `HOW_TO_CREATE.md` - instrukcja krok po kroku jak tworzyć własne skille.

## Dlaczego to ma znaczenie

W głównym ćwiczeniu warsztatu mamy jeden `AGENTS.md` z konwencjami globalnymi dla całego repo. Działa to dla małego projektu.

Dla dużego monorepo (10+ zespołów, 50+ projektów) tracisz kontrolę: `AGENTS.md` puchnie do 500 linii i nikt go nie czyta. Skills rozwiązują to, dzieląc wiedzę na **moduły wczytywane na żądanie**.
