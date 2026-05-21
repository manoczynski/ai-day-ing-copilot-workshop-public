# Bonus 05 - Migracja istniejącego repo na AGENTS.md

> **NIE omawiane na warsztacie.** Materiał do samodzielnej eksploracji, ale **najwyższy ROI w skali zespołu**.

## Problem, który ten bonus rozwiązuje

W całym warsztacie pokazujemy, jak `AGENTS.md` zmienia jakość pracy Copilota. Ale to działa pod jednym warunkiem: każdy projekt, w którym Copilot jest używany, **ma własny** `AGENTS.md`. Bez tego Copilot wraca do swojego domyślnego zachowania: `double` dla pieniędzy, brak walidacji, brak JavaDoc, magic numbers w kodzie.

Tymczasem Tribe ING ma **dziesiątki repo na Azure DevOps** i plan migracji na GitHub pod koniec 2026. Pisanie `AGENTS.md` ręcznie dla każdego z nich to setki godzin pracy. W praktyce zostanie napisany tylko dla 5-10 projektów najbardziej zaangażowanych zespołów. Reszta nigdy nie zobaczy wartości Copilota.

## Rozwiązanie

3-krokowy **playbook**, w którym Copilot generuje pierwszą wersję `AGENTS.md` dla **istniejącego** projektu, na podstawie analizy jego kodu, struktury i zależności. Ty walidujesz, korygujesz, commitujesz. Łącznie 15-30 minut na repo, zamiast 2-3 godzin ręcznego pisania.

## Co jest w tym katalogu

```
bonus-05-onboarding-existing-repo/
├── README.md          ← jesteś tutaj
├── PLAYBOOK.md        ← 3 kroki: audit → generate → validate
├── prompts.md         ← biblioteka promptów do każdego kroku
└── templates/
    ├── README.md             ← przegląd 5 szablonów dla typowych projektów ING
    ├── spring-boot-rest.md   ← REST microservice (Spring Boot 3, Java 17)
    ├── batch-etl-spark.md    ← Batch ETL (Spark + PySpark/Scala)
    ├── java-library.md       ← Pure domain library (Java 17, brak Springa)
    ├── dbt-project.md        ← dbt project (modele analityczne)
    └── frontend-react.md     ← Wewnętrzne dashboardy (React + TS)
```

## Quick start

```bash
# 1. Wejdź do projektu, dla którego chcesz wygenerować AGENTS.md
cd ~/projects/<twoje-istniejace-repo>

# 2. Sprawdź, że nie ma jeszcze AGENTS.md
ls AGENTS.md  # powinno być "No such file"

# 3. Otwórz to repo workshop w drugim oknie VS Code i przeczytaj playbook
code ~/projects/ai-day-ing-copilot-workshop/bonus-05-onboarding-existing-repo/PLAYBOOK.md

# 4. Wykonaj kroki 1-3 z playbooka w swoim repo
```

## Zysk w skali zespołu

Przykład rachunku dla zespołu z 30 repo:

| Metoda | Czas | Procent ukończenia |
|---|---|---|
| Ręczne pisanie AGENTS.md | 30 × 2.5h = 75 godzin | 20% (6 repo) realnie |
| Playbook z tego bonusa | 30 × 0.5h = 15 godzin | 80% (24 repo) realnie |

**5× szybciej, 4× więcej pokrytych projektów.** To różnica między "kilka osób ma Copilota dobrze skonfigurowanego" a "cały zespół ma".

## Co playbook NIE robi

- **Nie zastępuje walidacji człowieka.** Wygenerowany `AGENTS.md` to **pierwsza wersja**, nie ostateczna. Krok 3 (walidacja w parze) jest niezbędny.
- **Nie pokrywa skomplikowanych monorepo.** Dla repo z 10 modułami i 4 stosami technologicznymi playbook generuje top-level `AGENTS.md`, ale per-moduł `AGENTS.md` musisz zrobić osobno.
- **Nie wykrywa konwencji "niepisanych".** Jeśli zespół umawia się ustnie "nie używamy lomboka", ale w kodzie jest mieszane, Copilot zgadnie na podstawie częstotliwości, czasem źle.

## Jak ten bonus pasuje do reszty warsztatu

- **Blok 2** warsztatu pokazuje hierarchię `AGENTS.md` i hands-on tworzenie nowego pliku. Ten bonus pokazuje **tę samą umiejętność zastosowaną do istniejącego, dużego repo**.
- **Bonus 03 (Copilot CLI)** punkt 8.1 (audit konwencji w monorepo) jest **wstępem** do tego bonusa: pokazuje, jak zeskanować wiele repo. Ten bonus to logiczna kontynuacja: skoro zeskanowaliśmy, to teraz generujemy `AGENTS.md` dla każdego.
- **Bonus 02 (Skills)** wprowadza pojęcie skilla dla konkretnego scenariusza. Ten playbook **sam mógłby być skillem** (`agents-md-generator`) - to dobre ćwiczenie po opanowaniu tego bonusa: zapakować go w `.github/skills/`.
