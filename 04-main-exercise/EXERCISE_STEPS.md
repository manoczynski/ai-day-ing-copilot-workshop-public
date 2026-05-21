# Refaktor `CalculateInterest.java` - 5 kroków

To jest twoja instrukcja na czas Bloku 4 warsztatu. Pracujesz w parze: jedna osoba klika i pisze do Copilota, druga obserwuje. Co 5 minut zamiana ról.

**Co osiągniesz po tym ćwiczeniu:** legacy kod z `double`, magic numbers i magic strings przerobiony na czysty kod z `BigDecimal`, enumem, walidacją i testami - z Copilotem jako asystentem, ale przy zachowaniu twojej kontroli nad każdym krokiem.

## Setup (1 minuta, jeszcze przed startem zegara)

### 1. Zamknij wszystkie inne okna VS Code

Jeśli z Bloku 2 zostało okno `workshop-blok2/`, zamknij je. Copilot powinien widzieć **tylko** repo warsztatowe.

### 2. Otwórz repo warsztatowe jako folder w VS Code

W VS Code: **File → Open Folder** (`Ctrl + K` potem `Ctrl + O` w Windows/Linux, `Cmd + K` potem `Cmd + O` w macOS). W oknie dialogowym wskaż folder, do którego sklonowałeś repo - typowo nazywa się `ai-day-ing-copilot-workshop-public` (lub `ai-day-ing-copilot-workshop`).

⚠️ **Nie wybieraj podfolderu `04-main-exercise/`.** Musisz wskazać folder **wyżej** - ten, który **zawiera** `04-main-exercise/`, `README.md`, `AGENTS.md`, `bonus-01-spec-kit/` itd.

### 3. Sprawdź, że workspace jest poprawny

Spójrz na panel Explorer (lewy pasek VS Code, ikona z folderami). Na samej górze widzisz nazwę folderu, który otworzyłeś.

✅ **Dobry workspace** - widzisz:
- pliki `README.md`, `AGENTS.md`, `SETUP.md`, `LICENSE`
- foldery `01-bad-vs-good-prompt/`, `02-agents-md-exercise/`, `03-plan-mode-exercise/`, `04-main-exercise/`, `05-memory-codebase-followalong/`, `bonus-01-spec-kit/`, ... `facilitator/` (jeśli pracujesz z prywatnej kopii)

❌ **Zły workspace (podfolder)** - widzisz tylko:
- foldery `src/`, `reference/`, `target/`
- pliki `pom.xml`, `EXERCISE_STEPS.md`, `README.md`, `AGENTS.md`

Jeśli masz zły workspace - powtórz krok 2 i wskaż folder poziom wyżej.

### 4. Otwórz plik, który będziemy refaktorować

Najszybciej: `Ctrl + P` (Windows/Linux) lub `Cmd + P` (macOS), wpisz `CalculateInterest.java`, Enter.

Alternatywnie: w panelu Explorer rozwiń `04-main-exercise/` → `src/main/java/com/n8/workshop/` i kliknij `CalculateInterest.java`.

### 5. Otwórz Copilot Chat

`Ctrl + Shift + I` (Windows/Linux) lub `Cmd + Shift + I` (macOS).

Jeśli panel chatu się nie pojawił - sprawdź pasek statusu (prawy dolny róg VS Code). Ikona Copilot powinna pokazywać "Ready". Jeśli "Sign in" - kliknij i zaloguj się. Jeśli "Error" - sprawdź sieć i zrestartuj VS Code.

### 6. Wybierz tryb Agent w panelu chatu

W górnym pasku panelu chatu jest rozwijane menu z trybami: typowo **Ask** / **Edit** / **Agent** (nazwy mogą się różnić w wersji VS Code, czasem dochodzi **Plan**). Kliknij menu i wybierz **Agent**.

### 7. Sprawdź, że Copilot widzi konwencje projektu

W panelu chatu wpisz:

```
Pokaż konwencje, których przestrzegamy w tym projekcie
```

✅ **Wszystko OK:** odpowiedź wymienia BigDecimal, JUnit 5, AssertJ, JavaDoc po polsku, walidacja inputów. Możesz przejść do Kroku 1 ćwiczenia.

❌ **Czerwony sygnał #1:** Copilot pokazuje dialog **"Allow reading external files?"** z komunikatem typu *"AGENTS.md is outside of the current folder"*. To znaczy: twój workspace to podfolder, a główny `AGENTS.md` jest poza nim. Kliknij **Skip**, wróć do kroku 2 i wskaż folder poziom wyżej.

❌ **Czerwony sygnał #2:** odpowiedź jest generyczna ("dbamy o jakość kodu", bez konkretów). To samo rozwiązanie: wróć do kroku 2.

## Krok 1 - Zrozum kod (2 min)

**Zaznacz całą klasę** `CalculateInterest` w edytorze (`Ctrl + A` w Windows/Linux, `Cmd + A` w macOS). W panelu chatu wpisz:

```
#selection wyjaśnij co robi ten kod, jakie ma problemy i jakie są jego ukryte założenia
```

**Co zobaczysz:** Copilot wymieni między innymi: magic stringi `"R"`, `"S"`, `"P"`, użycie `double` dla pieniędzy, duplikację stawek między metodami `calc` a `getMonthlyBalances`, brak walidacji wejścia, brak JavaDoc.

**Uwaga:** `#selection` daje Copilotowi jawny kontekst (tylko to, co zaznaczyłeś). Bez tego Copilot szuka po całym repo i odpowiada wolniej oraz mniej trafnie.

## Krok 2 - Zaplanuj refaktor (3 min)

W tym samym wątku chatu, bez zamykania pliku, wpisz:

```
/plan Zrefaktoruj ten kod zgodnie z AGENTS.md. Skup się na:
- BigDecimal zamiast double
- enum AccountType
- wydzielenie BelkaTaxCalculator i AccountFeePolicy
- walidacja inputów
Wymień plan w punktach przed wykonaniem.
```

**Co zobaczysz:** lista około 6-10 kroków refaktoryzacji, ułożonych w bezpiecznej kolejności (najpierw stałe i typy, potem ekstrakcja klas, na końcu testy).

**Jeśli Copilot zacznie od razu generować kod zamiast planu:** przerwij (Esc) i powtórz prompt z naciskiem na **"TYLKO plan, nie pisz jeszcze kodu"**. Plan przed kodem oszczędza czas: tańsza jest poprawka na poziomie planu niż refaktor 5 plików.

## Krok 3 - Wykonaj plan (4 min)

**Najpierw przełącz tryb Copilota z Plan na Agent.** Plan Mode blokuje modyfikacje plików - dopóki w nim jesteś, Copilot nie wygeneruje zmian, tylko zwróci kolejną wersję planu. Przełącznik trybów jest w rozwijanym menu na górze panelu Copilot Chat (Plan / Agent / Ask). Wybierz **Agent**.

Po przełączeniu, w tym samym wątku chatu, wpisz:

```
Wykonaj plan, który przed chwilą zatwierdziliśmy. Idź krok po kroku.
Po każdym kroku poczekaj na moje OK przed przejściem do następnego.
```

**Co zobaczysz:** Copilot zacznie generować zmiany - jeden krok planu na raz. Po każdym kroku zatrzyma się i poczeka na potwierdzenie.

**Jeśli Copilot dalej zwraca tylko plan zamiast modyfikować pliki:** nie przełączyłeś się na Agent. Sprawdź rozwijane menu na górze panelu chatu - musi być tam **Agent**, nie **Plan**.

**Jak akceptować:** Copilot pokazuje diff (zmianę przed/po). Przeczytaj zmianę. Jeśli zgodna z `AGENTS.md` - kliknij "accept" albo napisz "OK, dalej". Jeśli widzisz problem (np. Copilot użył `double` w nowej klasie) - napisz "nie, popraw: użyj BigDecimal w polu X".

**Zadanie dla obserwatora:** patrz na ekran z perspektywy kogoś, kto NIE wpisuje. Twoim zadaniem jest powiedzieć "STOP" kiedy klikająca osoba akceptuje zmianę, która łamie konwencję z `AGENTS.md`. To jest praktyka **human-in-the-loop**: ludzka decyzja przed każdym commitem.

## Krok 4 - Wygeneruj testy (3 min)

Po skończonym refaktorze sprawdź, że kod kompiluje się. Maven uruchamiamy **z katalogu `04-main-exercise/`**, bo tam jest `pom.xml`. Jeśli zacząłeś z root repo, najpierw `cd 04-main-exercise/` w terminalu (`Ctrl + ` ` w Windows/Linux, `` Cmd + ` `` w macOS, żeby otworzyć terminal w VS Code):

```bash
cd 04-main-exercise && mvn clean compile
```

W chacie wpisz:

```
@workspace wygeneruj testy JUnit 5 + AssertJ dla nowo wydzielonych klas. Pokryj:
- happy path dla każdego AccountType
- edge cases: principal = 0, próg LARGE_ACCOUNT_THRESHOLD
- invalid inputs: null, ujemne wartości
Naming: should_<oczekiwany>_when_<warunek>().
```

**Co zobaczysz:** Copilot wygeneruje plik testowy z 10-15 testami pokrywającymi typowe i graniczne przypadki.

**Czego sprawdzić w wygenerowanych testach:**
- Czy asercje dla `BigDecimal` używają `isEqualByComparingTo("100.00")` zamiast `isEqualTo`? `isEqualTo` zwraca `false` dla `1.0` vs `1.00` (różny scale, choć ta sama wartość).
- Czy są asercje na konkretne wartości, czy tylko `isNotNull`? Test ze samym `isNotNull` jest słaby - przechodzi zawsze.

Uruchom testy z katalogu `04-main-exercise/`:

```bash
cd 04-main-exercise && mvn test
```

Powinny być zielone. Jeśli czerwone - przeczytaj komunikat błędu, porównaj wartość oczekiwaną z rzeczywistą, popraw lub wróć do Copilota z konkretnym pytaniem.

## Krok 5 - Code review przez Copilota (3 min)

W chacie wpisz:

```
/review przejrzyj zmiany w tym projekcie. Skup się na:
- BigDecimal precision i RoundingMode
- czy nie zostały gdzieś double / magic numbers
- czy walidacja metod publicznych jest kompletna
- czy testy pokrywają wszystkie progi i typy konta
Format: BLOCKER / WARNING / SUGGESTION.
```

**Co zobaczysz:** lista 3-5 uwag w trzech kategoriach:
- **BLOCKER** - musi być naprawione, np. brakujący test dla przypadku granicznego
- **WARNING** - powinno być naprawione, np. brak `RoundingMode` w `divide`
- **SUGGESTION** - opcjonalna poprawa, np. ekstrakcja stałej

Każdą uwagę przeczytaj i zdecyduj: naprawić od razu, zostawić na później, czy odrzucić.

## Co dalej

- **Zachowaj swoją wersję refaktora** w branchu - być może wykorzystasz ją do dyskusji po lunchu.
- **Wzorcowy efekt refaktoru** jest w `reference/CalculateInterest_REFACTORED.md`. Zajrzyj **dopiero po** zakończeniu ćwiczenia - inaczej zepsujesz sobie naukę z porównania własnej drogi z wzorcem.

---

## ✅ Blok 4 skończony

Czekaj na sygnał prowadzącego. Po 5-minutowym omówieniu pary z najciekawszymi rozwiązaniami przechodzimy do **Bloku 5 (Memory + #codebase, demo prowadzącego, 10 min)**.

Plik dla samodzielnej eksploracji po warsztacie / na lunchu: `../05-memory-codebase-followalong/FOLLOW_ALONG.md` (opcjonalnie, 5 min na własnym laptopie).

## Najczęstsze pułapki

- **Copilot generuje kod zamiast planu** w Kroku 2: powtórz prompt z naciskiem "TYLKO plan".
- **Copilot łamie konwencję z `AGENTS.md`** (np. używa `double` w nowej klasie): nie akceptuj, popraw promptem.
- **Testy są zielone, ale za słabe** (`isNotNull` zamiast konkretnej wartości): dopytaj Copilota o asercje z konkretnymi wartościami.
- **`mvn test` jest czerwony**: nie poprawiaj testów, żeby przeszły. Przeczytaj komunikat błędu - być może refaktor zmienił zachowanie. Wróć do Copilota z konkretnym pytaniem o to, co poszło nie tak.

## Powiązane pliki w repo

- `AGENTS.md` (w tym katalogu) - dokładne wymagania techniczne refaktoru
- `../AGENTS.md` (root) - konwencje całego repo
- `../.github/copilot-instructions.md` - dodatkowa konfiguracja Copilota
