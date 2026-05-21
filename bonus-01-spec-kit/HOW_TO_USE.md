# HOW_TO_USE - Spec Kit w tym repo

## Instalacja (jednorazowo)

Spec Kit nie wymaga instalacji jako paczka, bo to konwencja katalogowa plus szablony markdown. Można pracować z dowolnym agentem AI (Copilot Chat, Claude Code, Cursor, Codex).

Opcjonalnie: CLI `specify` (zob. [github.com/github/spec-kit](https://github.com/github/spec-kit)). Spec Kit jest dystrybuowany jako narzędzie Pythona uruchamiane przez `uvx`:

```bash
# wymaga: Python 3.10+ oraz uv (https://docs.astral.sh/uv/)
uvx --from git+https://github.com/github/spec-kit.git specify init
```

To wygeneruje szkielet `.specify/` plus `specs/`. W tym folderze masz już gotowy przykład, więc nie musisz nic generować.

## Workflow

### 1. Constitution (raz na projekt)

`.specify/memory/constitution.md` - zasady, których agent NIE może łamać. Trzymaj krótko (5-10 zasad).

### 2. Spec (raz na funkcjonalność)

`specs/<id>-<slug>/spec.md` - **co i dlaczego**. Wymagania funkcjonalne, niefunkcjonalne, scenariusze od strony użytkownika, kryteria akceptacji.

**Pisz spec z agentem:**

```
@workspace przeczytaj constitution.md. Pomóż mi napisać spec dla funkcji: <jednolinijkowy opis>.
Format: spec.md w stylu Spec Kit, zgodny z naszą constitution.
```

### 3. Plan (z agentem)

`specs/<id>/plan.md` - **jak**. Architektura, decyzje techniczne, fazy.

```
@workspace na podstawie spec.md i constitution.md, zaproponuj plan implementacji.
Format: plan.md w stylu Spec Kit.
```

### 4. Tasks (z agentem)

`specs/<id>/tasks.md` - **co konkretnie zrobić**. Małe atomic tasks z estymacją.

```
@workspace na podstawie plan.md, rozbij implementację na atomic tasks.
Każdy task max 30 min pracy. Numeruj, podawaj zależności.
```

### 5. Implementacja

Dla każdego taska:

```
#file:specs/001-loan-calculator/tasks.md #file:specs/001-loan-calculator/plan.md
Zaimplementuj task T-05. Trzymaj się constitution.md.
```

Po skończeniu - odhacz w `tasks.md`, commit z conventional commits message wskazującym task ID:

```bash
git commit -m "feat(loan): implement principal validation (T-05)"
```

### 6. Quality gate (przed PR)

```
@workspace przejrzyj zmiany. Sprawdź każdy punkt z specs/001-loan-calculator/checklists/requirements.md.
```

## Jak ten folder pasuje do warsztatu

W głównym ćwiczeniu (`04-main-exercise/`) używaliśmy lżejszej wersji tego podejścia - `AGENTS.md` zamiast pełnego constitutiona, ad-hoc plan w `EXERCISE_STEPS.md` zamiast formalnego `plan.md`.

Spec Kit jest tym samym pomysłem **dostosowanym do dużych funkcjonalności** (powyżej 1 dnia pracy). Dla małego refaktora `AGENTS.md` wystarcza.

## Anti-pattern

❌ "Wygeneruj mi spec na 50 stron". Spec krótszy niż 2 strony jest do uratowania. Spec na 50 stron jest do wyrzucenia - nikt go nie przeczyta, agent go nie zrozumie.

✅ Spec ma być **najmniejszą** ilością tekstu, która zatrzyma agenta przed pójściem w złą stronę.
