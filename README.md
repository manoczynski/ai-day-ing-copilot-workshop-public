# Warsztat GitHub Copilot - AI Day Tribe ING

**Witaj!** To repo zawiera wszystkie materiały do warsztatu **GitHub Copilot dla developerów Java**, który odbędzie się **21 maja 2026** podczas AI Day Tribe ING.

Warsztat trwa **90 minut**. Pracujemy w parach, na żywym kodzie, z Copilotem włączonym w VS Code.

---

## Co dziś zrobimy

1. **Nauczymy Copilota, jak ma pracować na nasz sposób** (konwencje banku, BigDecimal, walidacje, testy).
2. **Zrefaktoryzujemy razem stary, brzydki kod** kalkulatora odsetek - klasycznie i z Copilotem.
3. **Pokażemy 5 narzędzi**, które robią różnicę między "Copilot to autouzupełnianie" a "Copilot to partner do pracy".

Pod koniec warsztatu wyjdziesz z konkretnymi technikami, które możesz zastosować od razu w swoim projekcie.

---

## Zanim zaczniesz

Sprawdź te punkty na swoim laptopie. Każdy zajmuje 30 sekund.

### Lista kontrolna

- [ ] **Java 17** lub nowsza
  ```bash
  java -version
  ```
  Jeśli pokazuje wersję 8 lub 11, daj znać organizatorowi przed warsztatem.

- [ ] **Maven 3.8** lub nowszy
  ```bash
  mvn -version
  ```

- [ ] **VS Code 1.116** lub nowsza
  ```bash
  code --version
  ```

- [ ] **Rozszerzenie GitHub Copilot** zainstalowane w VS Code
  - Sprawdź: View → Extensions → wyszukaj "GitHub Copilot" → powinno być **Installed** i **Enabled**.

- [ ] **Ikona Copilot w status bar** (prawy dolny róg VS Code) pokazuje **"Ready"** (a nie "Sign in" ani "Error")

- [ ] **Git** zainstalowany
  ```bash
  git --version
  ```

Jeśli wszystkie 5 punktów ✅, **jesteś gotowy**. Jeśli któryś nie - patrz `SETUP.md`, znajdziesz tam komendy instalacji dla macOS, Linux i Windows.

---

## Pierwsze uruchomienie (3 minuty)

### Krok 1: Pobierz repo

```bash
git clone https://github.com/manoczynski/ai-day-ing-copilot-workshop-public.git
cd ai-day-ing-copilot-workshop-public
```

### Krok 2: Otwórz w VS Code **na poziomie root repo**

```bash
code .
```

⚠️ **Ważne**: musi być na poziomie root (czyli folder, w którym jest `README.md`, `AGENTS.md`, `04-main-exercise/` itd.). Jeśli otworzysz tylko podfolder `04-main-exercise`, Copilot nie zobaczy reszty konwencji i lekcja będzie połowiczna.

**Jak sprawdzić, że jesteś na root:** w panelu Explorer (lewy pasek VS Code) na samej górze widzisz folder zawierający `README.md`, `AGENTS.md`, `04-main-exercise/`, `bonus-01-spec-kit/` itd. Jeśli widzisz tylko `reference/`, `src/`, `pom.xml` (czyli zawartość pojedynczego ćwiczenia) - otworzyłeś podfolder.

**Sygnał ostrzegawczy w trakcie warsztatu:** Copilot pokaże dialog **"Allow reading external files?"** z komunikatem typu *"AGENTS.md is outside of the current folder"*. To znaczy, że twój workspace to podfolder, a `AGENTS.md` z root jest poza nim. Kliknij **Skip**, zamknij VS Code, otwórz ponownie poziom wyżej.

### Krok 3: Sprawdź, że kod się kompiluje

```bash
cd 04-main-exercise
mvn clean compile
cd ..
```

Powinien zakończyć się `BUILD SUCCESS`. Jeśli nie - patrz sekcja "Najczęstsze problemy" niżej.

### Krok 4: Otwórz Copilot Chat

W VS Code:
- Windows/Linux: **Ctrl + Shift + I**
- macOS: **Cmd + Shift + I**

W panelu chatu, w prawym górnym rogu, **wybierz tryb Agent** (nie Ask).

### Krok 5: Sprawdź, że Copilot widzi konwencje projektu

W chacie wpisz:

```
Jakie konwencje obowiązują w tym projekcie?
```

Copilot powinien wymienić: Java 17, BigDecimal, JUnit 5, AssertJ, JavaDoc po polsku, walidacja inputów. Jeśli zwraca generyczne odpowiedzi (typu "stosujemy dobre praktyki") - to znak, że VS Code jest otwarty nie na root, tylko w podfolderze. Zamknij i wróć do kroku 2.

**Wszystko działa? Świetnie. Czekaj na sygnał od prowadzącego do startu Bloku 1.**

---

## Jak pracujemy w parach

10 stanowisk, dwie osoby przy każdym. To celowe - nauczysz się więcej, gdy będziesz musiał wytłumaczyć drugiej osobie, co właśnie robisz.

### Dwie role (zmieniają się co 5 minut)

**Osoba przy klawiaturze:**
- Pisze prompty do Copilota
- Akceptuje lub odrzuca jego sugestie
- Skupia się na mechanice, nie na krytycznej ocenie

**Obserwator:**
- Patrzy na ekran z drugiej strony, ma "świeże oko"
- Notuje, gdzie Copilot się pomylił albo zaskoczył
- Mówi "STOP" gdy widzi, że Copilot łamie konwencję z `AGENTS.md`
- Po 5 minutach: zamiana ról

### Zasada warsztatu: STOP, jeśli Copilot się myli

Jeśli widzisz, że Copilot proponuje coś sprzecznego z `AGENTS.md`, nie akceptuj. Powiedz mu wprost: "to łamie konwencję X, popraw". Albo zacznij od nowa z lepszym promptem. **Twoim zadaniem nie jest klikać "accept" w kółko, tylko nauczyć się, JAK używać Copilota dobrze.**

---

## Co jest w tym repo

### Ćwiczenia w trakcie warsztatu

Pięć folderów z instrukcjami dla uczestnika, ponumerowane po blokach warsztatu:

📁 **`01-bad-vs-good-prompt/`** - **Blok 1 (10 min, demo prowadzącego)**. Blok 1 jest pokazywany na ekranie - nie musisz klikać. Ten plik jest **follow-along na potem**: 3 minuty na własnym laptopie, żeby samemu poczuć różnicę BAD vs GOOD prompt.
- `EXERCISE.md` - 2 prompty do skopiowania (BAD i GOOD) + porównanie wyników.

📁 **`02-agents-md-exercise/`** - **Blok 2 (8 min, hands-on)**. Zobacz różnicę między pytaniem do Copilota bez `AGENTS.md` a tym samym pytaniem z `AGENTS.md`. Mały, ostry kontrast.
- `EXERCISE.md` - 3 kroki, pracujesz w nowym pustym workspace poza repo warsztatowym.

📁 **`03-plan-mode-exercise/`** - **Blok 3 (9 min, hands-on)**. Wykonaj jedno zadanie dwa razy - raz bez Plan Mode, raz z Plan Mode - i porównaj wyniki.
- `EXERCISE.md` - 2 warianty + porównanie, pracujesz na pliku `IbanValidator.java` z głównego ćwiczenia.

📁 **`04-main-exercise/`** - **Blok 4 (25 min, ★ główne ćwiczenie warsztatu)**. Refaktor brzydkiego kodu kalkulatora odsetek na czysty BigDecimal z testami.
- `src/main/java/com/n8/workshop/CalculateInterest.java` - **brzydki, stary kod** (double, magic numbers, brak testów). Ten plik refaktoryzujemy.
- `EXERCISE_STEPS.md` - **5 kroków scenariusza**, według którego pracujemy.
- `AGENTS.md` - konwencje, których Copilot ma się trzymać.

📁 **`05-memory-codebase-followalong/`** - **Blok 5 (10 min, demo prowadzącego)**. Memory + `#codebase` pokazywane na ekranie. Ten plik to **follow-along na potem**: 5 minut na własnym laptopie, żeby Memory i `#codebase` weszły ci w nawyk.
- `FOLLOW_ALONG.md` - 4-5 promptów do powtórzenia po demo.

📁 **`06-hql-bonus/`** - **Blok 6 (5 min, demo prowadzącego, bonus dla data engineerów)**. Migracja realnego ETL z HQL na PySpark z Plan Mode. Pokazane na ekranie, **kod jest w folderze** - możesz powtórzyć na własnym laptopie po warsztacie.
- `FOLLOW_ALONG.md` - 10-minutowy scenariusz: Plan Mode + Agent Mode + porównanie z referencją
- `legacy_interest.hql` - przykładowy ETL HQL (~50 linii, realistyczny pipeline bankowy)
- `expected_pyspark.py` - wzorcowy efekt migracji do porównania

### Bonusy (do samodzielnej eksploracji po warsztacie)

📁 **`bonus-01-spec-kit/`** - jak używać Spec Kita do większych funkcjonalności (specyfikacja przed kodem).

📁 **`bonus-02-copilot-skills/`** - jak tworzyć własne "skille" Copilota dla konkretnych zadań (np. code review w bankowym stylu).

📁 **`bonus-03-copilot-cli/`** - Copilot w terminalu plus biblioteka 24 promptów, które możesz skopiować od razu do swojej pracy.

📁 **`bonus-04-tdd-with-copilot/`** - Test-Driven Development z Copilotem, 5 ćwiczeń i 4 bankowe przykłady (walidator PESEL, podatek Belka, harmonogram spłat).

📁 **`bonus-05-onboarding-existing-repo/`** - jak wygenerować `AGENTS.md` dla istniejących projektów (gdy przeniesiecie repo na GitHub pod koniec 2026).

### Konfiguracja Copilota

📄 **`AGENTS.md`** - główny plik konwencji, czytany przez Copilota automatycznie. Jeśli kiedyś chcesz zobaczyć, jak takie pliki się pisze, zacznij od tego.

📄 **`.github/copilot-instructions.md`** - dodatkowe instrukcje dla Copilota specyficzne dla tego repo.

📄 **`.github/instructions/java-conventions.instructions.md`** - konwencje aktywujące się tylko dla plików `.java`.

---

## Najczęstsze problemy

### 1. "Copilot nie odpowiada"

- Sprawdź ikonę w status bar (prawy dolny róg VS Code). Powinno być "Ready".
- Jeśli "Sign in" - kliknij i zaloguj się przez przeglądarkę.
- Jeśli "Error" - sprawdź połączenie z internetem, ewentualnie restart VS Code.

### 2. "Copilot generuje generyczne odpowiedzi, nie czyta `AGENTS.md`" lub pokazuje dialog "Allow reading external files?"

Najczęstsza przyczyna: VS Code jest otwarty w podfolderze (np. `04-main-exercise/`), nie na root. Wtedy główny `AGENTS.md` jest **poza** workspace - Copilot albo go nie widzi, albo prosi o jednorazowe pozwolenie ("Allow Once" / "Skip") za każdym razem.

Zamknij VS Code i otwórz go ponownie z **root repo**:

```bash
cd ai-day-ing-copilot-workshop-public
code .
```

Po otwarciu: w Explorer (lewy pasek) na samej górze powinieneś widzieć folder z `README.md`, `AGENTS.md`, `04-main-exercise/`, `bonus-01-spec-kit/` itd. Jeśli widzisz tylko zawartość pojedynczego ćwiczenia - dalej jesteś w podfolderze.

### 3. "BUILD FAILURE" przy `mvn clean compile`

- Sprawdź wersję Javy: `java -version`. Musi być 17 lub nowsza.
- Sprawdź, że jesteś w `04-main-exercise/`, nie w root.
- Jeśli to bank laptop z restrykcjami sieciowymi: Maven może nie móc pobrać zależności. Powiedz organizatorowi.

### 4. "Wynik Copilota jest słaby"

Większość warsztatu jest właśnie o tym, jak to naprawić. Krótko mówiąc:
- Zacznij od **konkretu** (nie "napisz mi metodę", tylko "napisz Java 17 metodę X z wymaganiami: Y, Z, W").
- Daj Copilotowi **kontekst** (`#file:NazwaPliku.java`, `#selection`, `#codebase`).
- Użyj **Plan Mode** (`/plan ...`) przed napisaniem kodu.

Dowiesz się więcej w Bloku 1 i Bloku 3.

---

## Po warsztacie

Jeśli warsztat się skończył i wszystko poszło dobrze, możesz wrócić do tego repo w wolnym czasie. Sugerowana kolejność:

1. **Powtórz główne ćwiczenie** sam (`04-main-exercise/`) - tym razem bez presji czasu.
2. **Zajrzyj do bonus-03** (`bonus-03-copilot-cli/`) - 24 prompty do skopiowania od razu do swojej pracy.
3. **Wygeneruj AGENTS.md dla swojego projektu** (`bonus-05-onboarding-existing-repo/`) - 15-30 minut, działa od razu.
4. **Spróbuj TDD z Copilotem** (`bonus-04-tdd-with-copilot/`) - 4 bankowe przykłady, każdy 20-30 min.

Materiały będą dostępne tutaj cały czas, możesz wracać.

---

## Filozofia warsztatu

> *"Niektórzy z Was próbowali Copilota i nie byli zadowoleni z efektów."*

Wiemy to. I właśnie dlatego ten warsztat nie pokazuje **co Copilot potrafi** (to już widziałeś), tylko **JAK go używać, żeby faktycznie pomagał**.

Pięć rzeczy, które zmieniają wszystko:

1. **`AGENTS.md`** - mówisz Copilotowi raz, jakie są twoje konwencje. Reszta sama się dzieje.
2. **Jawny kontekst** (`#selection`, `#file`, `#folder`) - zamiast pozwalać Copilotowi szukać po omacku.
3. **Plan Mode** - plan przed kodem, mniej iteracji w kółko.
4. **Human-in-the-loop** - każdy krok wymaga twojej akceptacji. Nie automatyzacja, praca pod nadzorem.
5. **Drugi przebieg** - po wygenerowaniu kodu, code review przez Copilota wyłapuje resztę.

To 5 rzeczy. Jedna zasada. 90 minut. Lecimy.


## Prowadzący

**Marcin Noczynski** - prowadzący warsztat na AI Day Tribe ING (21.05.2026).

LinkedIn: [linkedin.com/in/mnoczynski](https://www.linkedin.com/in/mnoczynski/) - złap kontakt, jeśli chcesz podyskutować o Copilocie, agentach AI albo praktyce wdrażania ich w bankowości.

## Licencja

MIT - patrz `LICENSE`.
