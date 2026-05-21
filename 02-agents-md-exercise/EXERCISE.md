# Blok 2 - Ćwiczenie: zobacz różnicę z `AGENTS.md` (8 min)

To jest twoja instrukcja na czas Bloku 2 warsztatu. Pracujesz w parze: jedna osoba klika i pisze do Copilota, druga obserwuje. Zmiana ról po ok. 4 minutach.

**Co osiągniesz:** zadasz dokładnie to samo pytanie Copilotowi dwa razy - raz bez `AGENTS.md`, raz z `AGENTS.md` - i zobaczysz na własne oczy, co plik konwencji zmienia w odpowiedzi. To moment, w którym zaczniesz wierzyć, że ten plik ma sens.

## Setup (1 minuta, przed startem zegara)

Otwórz **nowy, pusty katalog** jako VS Code workspace. **Musi być POZA repo warsztatowym**, bo inaczej Copilot zobaczy `AGENTS.md` z warsztatu i ćwiczenie nie zadziała.

**Najprościej, uniwersalnie dla Windows i macOS:**

1. Otwórz VS Code (jeśli był otwarty z repo warsztatowym, zostaw go - zaraz otworzysz drugie okno)
2. **File → New Window** (`Ctrl + Shift + N` w Windows/Linux, `Cmd + Shift + N` w macOS)
3. W nowym oknie: **File → Open Folder**
4. W oknie dialogowym kliknij **New folder**, nazwij go `workshop-blok2` i wybierz dowolną lokalizację (np. Pulpit / Desktop). Potwierdź.

**Alternatywnie, z terminala:**

- **Windows (PowerShell):**
  ```powershell
  mkdir $HOME\workshop-blok2
  code $HOME\workshop-blok2
  ```
- **macOS / Linux:**
  ```bash
  mkdir -p ~/workshop-blok2 && code ~/workshop-blok2
  ```

Otwórz Copilot Chat: `Ctrl + Shift + I` (Windows/Linux) lub `Cmd + Shift + I` (macOS). Jeśli panel chatu się nie pojawił, sprawdź pasek statusu (prawy dolny róg VS Code) - ikona Copilot powinna pokazywać "Ready".

W górnym pasku panelu chatu jest rozwijane menu z trybami (typowo **Ask** / **Edit** / **Agent**). Kliknij i wybierz **Agent**.

## Krok 1 - Zadanie BEZ `AGENTS.md` (3 min)

W nowym workspace utwórz plik `BankAccount.java`:

- **File → New File**, zapisz jako `BankAccount.java`

W Copilot Chat **rozpocznij nowy wątek** (przycisk "+" / "New chat" w górnym pasku panelu chatu) i wpisz **dokładnie ten prompt**:

```
Napisz klasę BankAccount z metodą deposit(amount) i getBalance(). Java.
```

Poczekaj, aż Copilot odpowie. Wstaw kod do pliku `BankAccount.java`.

**Zadanie obserwatora - zapisz na kartce albo w pliku notatek:**

- [ ] Jaki typ ma `amount`? (`double`, `BigDecimal`, `int`?)
- [ ] Czy są walidacje wejścia? (sprawdzenie `null`, sprawdzenie wartości ujemnej?)
- [ ] Czy Copilot dodał testy? Jeśli tak, jakim frameworkiem (JUnit 4? JUnit 5? Brak adnotacji?)
- [ ] Czy jest JavaDoc? W jakim języku?
- [ ] Czy są jakieś stałe nazwane, czy magic numbers?

Zostaw plik otwarty - przyda się do porównania.

## Krok 2 - Stwórz `AGENTS.md` i zadaj to samo pytanie (3 min)

W tym samym workspace utwórz drugi plik, **obok** `BankAccount.java`. Nazwa: `AGENTS.md`. Wklej zawartość:

```markdown
# AGENTS.md

## Stack
- Java 17
- JUnit 5 + AssertJ dla testów

## Konwencje
- BigDecimal dla wszystkich kwot pieniężnych (NIGDY double ani float)
- Scale 2 dla kwot, RoundingMode.HALF_UP
- Walidacja metod publicznych: Objects.requireNonNull dla null, IllegalArgumentException dla wartości ujemnych
- JavaDoc po polsku dla metod publicznych (@param, @return, @throws)
- Nazewnictwo testów: should_<oczekiwany>_when_<warunek>()
- Brak System.out.println, używamy SLF4J

## Czego unikać
- double / float dla pieniędzy
- magic numbers i magic strings
- jednoliterowych nazw zmiennych poza pętlami
```

Zapisz plik (`Ctrl + S` w Windows/Linux, `Cmd + S` w macOS).

**Teraz kluczowe: rozpocznij NOWY wątek** w Copilot Chat (przycisk "+" / "New chat"). Jeśli zostawisz poprzedni wątek, Copilot będzie pamiętał stary kontekst i ćwiczenie nie pokaże różnicy.

W nowym wątku wpisz **dokładnie ten sam prompt co w Kroku 1**:

```
Napisz klasę BankAccount z metodą deposit(amount) i getBalance(). Java.
```

**Zadanie obserwatora - przejdź jeszcze raz tę samą listę kontrolną:**

- [ ] Jaki typ ma `amount` teraz?
- [ ] Czy są walidacje? Jakie konkretnie?
- [ ] Czy są testy? Jakim frameworkiem?
- [ ] Czy jest JavaDoc? W jakim języku?
- [ ] Czy są stałe nazwane?

## Krok 3 - Porównaj wyniki w parze (2 min)

Postaw obok siebie obie wersje kodu (jeśli pierwszą wkleiłeś do `BankAccount.java`, drugą zostaw w panelu chatu - albo wklej do drugiego pliku `BankAccount_v2.java`).

**Pytania do dyskusji w parze:**

1. Co konkretnie się zmieniło między wersją bez `AGENTS.md` a z `AGENTS.md`?
2. Czy zmiana jest tylko "kosmetyczna" (np. inny komentarz), czy faktycznie inna struktura kodu?
3. Gdybyś dostał drugą wersję do code review, czy nadal byś zgłaszał uwagi? Jakie?

Spodziewane różnice (jeśli wszystko zadziałało):

- `double amount` → `BigDecimal amount`
- Brak walidacji → `Objects.requireNonNull` + sprawdzenie signum
- Brak JavaDoc → JavaDoc po polsku
- Brak testów lub generyczne → testy JUnit 5 + AssertJ w stylu `should_X_when_Y`

## Najczęstsze problemy

**Copilot zwrócił to samo z `AGENTS.md` i bez** - dwie przyczyny:

1. Nie rozpocząłeś **nowego wątku** w Kroku 2. Copilot pamięta stary kontekst i powtarza. Rozwiązanie: kliknij wyraźnie "+" / "New chat" przed drugim promptem.
2. Twoje `AGENTS.md` jest w innym katalogu niż otwarte pliki. Copilot widzi tylko pliki w aktualnym workspace. Rozwiązanie: sprawdź w panelu **Explorer** (lewy pasek VS Code, ikona z folderami), że oba pliki - `AGENTS.md` i `BankAccount.java` - są w tym samym katalogu.

**Nie widzę przycisku "new chat"** - w nowszych wersjach VS Code jest to przycisk "+" w górnym pasku panelu chatu Copilota. Jeśli nie widać, kliknij ikonę z trzema kropkami "..." obok i wybierz "Start new chat" z menu.

**Otworzyłem workspace, ale Copilot nie reaguje** - sprawdź ikonę Copilot w status bar (prawy dolny róg VS Code). Powinna pokazywać "Ready" (zielona). Jeśli "Sign in" - zaloguj się. Jeśli "Error" - sprawdź sieć.

## Co dalej

Ten sam mechanizm - `AGENTS.md` w workspace - skaluje się na duże repo. W Bloku 4 (główne ćwiczenie) zobaczysz, jak `AGENTS.md` z warsztatu prowadzi Copilota przez refaktor 80 linii brzydkiego kodu na czysty `BigDecimal` z testami. **To nie magia. To plik, który właśnie napisałeś.**

---

## ✅ Blok 2 skończony

**Zostaw otwarte okno `workshop-blok2/` na razie** - możesz je zamknąć dopiero przed Blokiem 4 (żeby Copilot w głównym ćwiczeniu nie mylił workspace).

Czekaj na sygnał prowadzącego do startu **Bloku 3 (Plan Mode, 15 min)**. Następny plik dla ciebie: `../03-plan-mode-exercise/EXERCISE.md`.

## Powiązane pliki w repo

- `../AGENTS.md` (root) - `AGENTS.md` całego warsztatu, do podejrzenia jak wygląda "prawdziwy" plik
- `../04-main-exercise/AGENTS.md` - konwencje dla głównego ćwiczenia, czyli folder-specific override
- `../.github/copilot-instructions.md` - dodatkowa konfiguracja Copilota dla tego repo
- `../bonus-05-onboarding-existing-repo/PLAYBOOK.md` - jak wygenerować `AGENTS.md` dla istniejącego repo (po warsztacie, do samodzielnej pracy)
