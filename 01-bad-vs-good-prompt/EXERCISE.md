# Blok 1 - Follow-along: BAD prompt vs GOOD prompt (3 min na własnym laptopie)

To jest **opcjonalny** plik do powtórzenia demo z Bloku 1 na własnym laptopie. W trakcie warsztatu Blok 1 (5:00-15:00) prowadzący pokazuje to demo na ekranie - nie musisz wtedy klikać. Ten plik jest na potem: gdy chcesz **samemu poczuć** różnicę między BAD a GOOD promptem, możesz to zrobić w 3 minuty.

**Co zobaczysz:** ten sam Copilot, ta sama subskrypcja, ta sama sekunda - dwa różne wyniki. Różnica to nie magia ani konfiguracja, tylko **30 sekund myślenia o wymaganiach** przed pierwszym promptem.

## Setup (30 sek)

Otwórz dowolny katalog **poza** repo warsztatowym jako VS Code workspace. Najprościej `Scratch` na pulpicie:

- **Windows (PowerShell):**
  ```powershell
  mkdir $HOME\scratch-blok1
  code $HOME\scratch-blok1
  ```
- **macOS / Linux:**
  ```bash
  mkdir -p ~/scratch-blok1 && code ~/scratch-blok1
  ```

Utwórz w workspace plik `Scratch.java` (File → New File, zapisz).

Otwórz Copilot Chat: `Ctrl + Shift + I` (Windows/Linux) lub `Cmd + Shift + I` (macOS). Jeśli panel chatu się nie pojawił, sprawdź pasek statusu (prawy dolny róg VS Code) - ikona Copilot powinna pokazywać "Ready".

W górnym pasku panelu chatu jest rozwijane menu z trybami (typowo **Ask** / **Edit** / **Agent**, zależnie od wersji VS Code może być też **Plan**). Kliknij i wybierz **Agent**.

## Wariant BAD (1 min)

**Rozpocznij nowy wątek** (przycisk "+" / "New chat" na górze panelu chatu). Wklej **dokładnie ten prompt** - jednolinijkowiec:

```
napisz mi metodę do parsowania CSV
```

Wyślij. Wstaw odpowiedź Copilota do `Scratch.java`.

**Co obserwujesz:**

- [ ] Czy metoda obsługuje cudzysłowy w komórkach (`"Smith, John"`)?
- [ ] Czy obsługuje escapowane cudzysłowy (`"He said ""hi"""`)?
- [ ] Co robi w przypadku błędnego wiersza - wyjątek, skip, milczy?
- [ ] Czy przyjmuje plik, napis, czy `Reader`?
- [ ] Czy są testy?

Spodziewany wynik: generyczna metoda ze `String.split(",")`, bez obsługi cudzysłowów, bez error handlingu, prawdopodobnie zwracająca `String[][]` lub coś podobnego.

## Wariant GOOD (1.5 min)

**Rozpocznij NOWY wątek** (przycisk "+" / "New chat"). To kluczowe - bez nowego wątku Copilot pamięta poprzedni prompt i porównanie się rozmywa.

Wklej **ten prompt**:

```
Napisz Java 17 metodę do parsowania CSV z następującymi wymaganiami:
- handle quoted fields with embedded commas (np. "Smith, John")
- handle escaped quotes inside quoted fields ("He said ""hi""")
- skip header row
- return List<Map<String,String>> (klucz = nazwa kolumny z headera)
- throw MalformedCsvException dla błędnego wiersza, z numerem linii
- input: java.io.Reader (nie File - wstrzykiwalne dla testów)
- bez zewnętrznych zależności (nie Apache Commons CSV)

Po napisaniu metody: zaproponuj 5 testów JUnit 5 + AssertJ pokrywających happy path i 4 edge cases.
```

Wyślij. Wstaw odpowiedź do nowego pliku `ScratchGood.java`.

**Co obserwujesz tym razem:**

- [ ] Czy metoda używa prawdziwego state machine dla parsowania (obsługa cudzysłowów)?
- [ ] Czy jest własny wyjątek z numerem linii?
- [ ] Czy `input` to `java.io.Reader`?
- [ ] Czy dostałeś listę 5 testów z konkretnymi przypadkami?

## Porównanie (30 sek)

Postaw obok siebie obie wersje. Pytania do siebie:

1. Czy to ten sam Copilot? **Tak.**
2. Czy zmieniła się subskrypcja, model, konfiguracja? **Nie.**
3. Co się zmieniło? **Tylko prompt.**

To jest cała lekcja Bloku 1: **różnica między dobrym a złym wynikiem nie leży w Copilocie - leży w 30 sekundach myślenia o wymaganiach przed pierwszym promptem**.

Kolejne 75 minut warsztatu to systematyczna nauka, jak te 30 sekund robić powtarzalnie:

- **Blok 2:** zamiast za każdym razem wpisywać wymagania do promptu, zapisujesz je raz w `AGENTS.md` - i Copilot je czyta automatycznie. (`../02-agents-md-exercise/EXERCISE.md`)
- **Blok 3:** zamiast od razu generować kod, najpierw plan - wyłapuje pomyłki tańszym kosztem. (`../03-plan-mode-exercise/EXERCISE.md`)
- **Blok 4:** wszystko razem na realnym, bankowym kodzie. (`../04-main-exercise/EXERCISE_STEPS.md`)

## Bonus - mini-teaser Plan Mode

Jeśli zostały ci 30 sek, w tym samym wątku z wynikiem GOOD:

W rozwijanym menu trybu chatu **przełącz na Plan**. Wpisz:

```
/plan Zrefaktoruj wygenerowaną metodę tak, żeby state machine był w osobnej klasie. Pokaż mi plan, nie rób jeszcze refaktoru.
```

Copilot zwróci **plan**, nie kod. To Plan Mode w pigułce - szczegóły w `../03-plan-mode-exercise/EXERCISE.md`.

## Najczęstsze problemy

**Wariant BAD przypadkowo dał dobry wynik** - zdarza się. To też lekcja: "tym razem trafiło, ale czy mogę liczyć, że trafi za każdym razem? Nie. Robimy systematycznie." Wynik Wariantu GOOD jest **przewidywalny**; wynik BAD jest **loteryjny**.

**Wariant GOOD dał coś gorszego niż BAD** - rzadkie, ale możliwe. Natura modelu językowego. Dlatego w Bloku 4 (krok 5) zobaczysz `/review` jako drugi pass weryfikacyjny.

**Nie wiem jak rozpocząć nowy wątek** - w panelu Copilot Chat w górnym pasku jest przycisk "+" lub "New chat". W nowszych wersjach VS Code to ikona z plusem; w starszych - menu "..." → "Start new chat".

## Powiązane pliki w repo

- `../README.md` (root) - quick start dla całego warsztatu
- `../02-agents-md-exercise/EXERCISE.md` - następny blok, AGENTS.md w akcji
- `../03-plan-mode-exercise/EXERCISE.md` - Blok 3, Plan Mode
- `../04-main-exercise/EXERCISE_STEPS.md` - główne ćwiczenie warsztatu (Blok 4)
