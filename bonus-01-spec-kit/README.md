# Bonus 01 - Spec Kit (Spec-Driven Development)

> **NIE omawiane na warsztacie.** Materiał do samodzielnej eksploracji po wydarzeniu.

## Co to jest

[GitHub Spec Kit](https://github.com/github/spec-kit) to lekki framework do **Spec-Driven Development** - zamiast od razu pisać kod, najpierw budujesz:

1. **Constitution** - zasady projektu (`.specify/memory/constitution.md`)
2. **Spec** - co budujemy i dlaczego (`specs/<id>/spec.md`)
3. **Plan** - jak to zbudujemy (`specs/<id>/plan.md`)
4. **Tasks** - konkretna lista pracy (`specs/<id>/tasks.md`)
5. **Checklists** - quality gates (`specs/<id>/checklists/*.md`)

Każdy z tych dokumentów jest kontekstem dla agenta AI (Copilot, Claude, Cursor) podczas implementacji.

## Przykład w tym folderze

Specyfikacja: **Kalkulator pożyczek z harmonogramem spłat**.

Plik | Co jest w środku
--- | ---
`.specify/memory/constitution.md` | 7 zasad projektu (BigDecimal, walidacja, testy, JavaDoc, immutability, no PII, conventional commits)
`specs/001-loan-calculator/spec.md` | Wymagania funkcjonalne i niefunkcjonalne, scenariusze, akceptacja
`specs/001-loan-calculator/plan.md` | Architektura, wybór technologii, fazy implementacji
`specs/001-loan-calculator/tasks.md` | 18 atomic tasks z estymacją
`specs/001-loan-calculator/checklists/requirements.md` | Quality checklist przed PR

## Jak tego użyć

Patrz `HOW_TO_USE.md`: instrukcja instalacji, uruchomienia oraz tego, jak instruować agenta z tymi dokumentami w kontekście.

## Dlaczego to robi różnicę

Bez specu: agent zgaduje, co masz na myśli. Dostajesz coś, co działa, ale nie to, czego chciałeś.

Ze specem: agent **wie**, co budujesz i dlaczego. Można też trzymać go w dyscyplinie ("trzymaj się constitution") i wracać do specu przy code review.

To jest dokładnie ta sama idea co `AGENTS.md` z głównego ćwiczenia, tylko bardziej formalna i zorganizowana dla każdej funkcjonalności osobno.
