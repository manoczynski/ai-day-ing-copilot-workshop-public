# Blok 3 - Ćwiczenie: Plan Mode vs bez planu (9 min)

To jest twoja instrukcja na czas Bloku 3 warsztatu. Pracujesz w parze: jedna osoba klika i pisze do Copilota, druga obserwuje.

**Co osiągniesz:** wykonasz to samo małe zadanie dwa razy - raz bez Plan Mode, raz z Plan Mode - i porównasz, czym się różniły wyniki. To budowanie intuicji, kiedy plan przed kodem się opłaca.

## Setup (30 sek, przed startem zegara)

### 1. Sprawdź, że VS Code jest otwarty na root repo warsztatowego

W panelu Explorer (lewy pasek VS Code) na samej górze powinieneś widzieć folder zawierający `README.md`, `AGENTS.md`, `04-main-exercise/`, `bonus-01-spec-kit/` itd.

❌ Jeśli widzisz tylko `reference/`, `src/`, `pom.xml`, `EXERCISE_STEPS.md` - to podfolder `04-main-exercise/`. Zamknij VS Code, użyj **File → Open Folder** i wskaż folder **wyżej** (ten zawierający `04-main-exercise/`).

### 2. Otwórz plik z TODO

Najszybciej: `Ctrl + P` (Windows/Linux) lub `Cmd + P` (macOS), wpisz `IbanValidator.java`, Enter.

Pełna ścieżka: `04-main-exercise/src/main/java/com/n8/workshop/iban/IbanValidator.java`.

W pliku jest TODO w komentarzu nad polem `formatForDisplay` - tym się zajmiemy.

### 3. Zrozum, co już jest w repo

Metoda `isValid()` (walidacja mod-97) jest **już zaimplementowana** w klasie. Prowadzący przed chwilą pokazał na demo, jak Copilot mógłby ją wygenerować, ale w twoim repo masz ją gotową.

**Twoje zadanie to wyłącznie druga metoda, `formatForDisplay()`.** Możesz patrzeć na `isValid()` jako wzorzec stylu (walidacja inputu, polskie komunikaty błędów).

### 4. Otwórz Copilot Chat i sprawdź dostępne tryby

`Ctrl + Shift + I` (Windows/Linux) lub `Cmd + Shift + I` (macOS).

W górnym pasku panelu chatu jest rozwijane menu z trybami: **Ask** / **Edit** / **Agent** / **Plan**. To ćwiczenie używa **Agent** w Wariancie A i **Plan** w Wariancie B - przełącznik jest kluczowy, **zobaczysz go dwa razy** w tym ćwiczeniu.

### 5. Treść zadania (do skopiowania w obu wariantach)

```
Dodaj do IbanValidator metodę formatForDisplay(String iban),
która zwraca IBAN w formacie ze spacjami co 4 znaki
(np. "PL61 1090 1014 0000 0712 1981 2874").
Zasady:
- usuń istniejące spacje i zamień na wielkie litery
- nie waliduj sumy kontrolnej (to jest formatowanie, nie walidacja)
- null → NullPointerException
- pusty napis → IllegalArgumentException z polskim komunikatem
Dodaj 3-4 testy JUnit 5 + AssertJ.
```

## Wariant A - bez Plan Mode (3 min)

W panelu chatu sprawdź, że tryb to **Agent** (nie Plan, nie Ask) - rozwijane menu na górze panelu.

**Rozpocznij nowy wątek** (przycisk "+" / "New chat" na górze panelu).

Wklej treść zadania (tę z bloku powyżej) i wyślij.

**Co obserwujesz:**

- Czy Copilot najpierw o coś dopytał, czy od razu pisał kod?
- Ile razy musiałeś poprawiać prompt, żeby dostać sensowny wynik?
- Czy testy mają konkretne wartości w asercjach, czy tylko `isNotNull`?

**Zadanie obserwatora - zapisz na kartce:**

| Wymiar | Wariant A |
|---|---|
| Czas do działającej metody (sek) | |
| Liczba iteracji / poprawek | |
| Czy testy pokrywają edge cases (null, pusty)? (tak/nie) | |
| Zaufanie do wyniku 1-5 (1=zupełnie nie ufam, 5=wzorcowo) | |

Po 3 minutach **przełącz role przy klawiaturze**.

## Wariant B - z Plan Mode (4 min)

**Rozpocznij NOWY wątek** w Copilot Chat (przycisk "+" / "New chat"). To kluczowe - bez nowego wątku Copilot pamięta poprzednie próby z Wariantu A i porównanie nic nie da.

W rozwijanym menu trybu (na górze panelu chatu, tym samym, w którym wcześniej był **Agent**) **przełącz na "Plan"**. Wpisz:

```
/plan
```

a po nim wklej **dokładnie tę samą** treść zadania co w Wariancie A.

**Co zobaczysz:** Copilot wygeneruje listę kroków (4-8 punktów), **nie kod**. Lista powinna obejmować mniej więcej: normalizacja wejścia, walidacja, wstawianie spacji, testy.

**Przeczytaj plan w parze.** Jeśli czegoś brakuje (np. brak testu dla null), dopytaj w tym samym wątku, dalej w trybie Plan:

```
W planie brakuje testu dla null. Dodaj go.
```

Gdy plan wygląda OK, **przełącz tryb chatu z Plan na Agent** w tym samym rozwijanym menu, w którym wcześniej zmieniłeś na Plan. Plan Mode **blokuje modyfikacje plików** - dopóki jesteś w Plan, Copilot nie wygeneruje zmian, tylko zwróci kolejną wersję planu. Po przełączeniu na Agent będzie mógł realnie pisać kod.

Po przełączeniu na Agent, w tym samym wątku wpisz:

```
Wykonaj plan, który właśnie zatwierdziliśmy. Idź krok po kroku.
Po każdym kroku poczekaj na moje OK.
```

**Zadanie obserwatora - dopisz drugą kolumnę:**

| Wymiar | Wariant A | Wariant B |
|---|---|---|
| Czas do działającej metody (sek) | | |
| Liczba iteracji / poprawek | | |
| Czy testy pokrywają edge cases? (tak/nie) | | |
| Zaufanie do wyniku 1-5 | | |

## Wariant C - porównanie (2 min)

Postaw obok siebie obie wersje (jeśli pracowałeś na tym samym pliku, użyj `git diff` żeby zobaczyć obie).

**Pytania do dyskusji w parze:**

1. Który wariant zajął więcej czasu **łącznie** (od pierwszego promptu do działającego kodu)?
2. Który wariant dał wynik bliższy temu, czego oczekiwałeś od początku?
3. Czy w Wariancie B przeczytanie planu wykryło coś, co inaczej byś zauważył dopiero po wygenerowaniu kodu?
4. Gdzie kupiłbyś czas Plan Mode, a gdzie nie?

## Najczęstsze problemy

**Copilot w Wariancie B zwraca tylko plan i nie modyfikuje plików** - przyczyna: nie przełączyłeś trybu z Plan na Agent po zatwierdzeniu planu. Plan Mode blokuje zmiany w plikach. Sprawdź rozwijane menu na górze panelu - musi być **Agent**, nie Plan.

**`/plan` nie działa jako slash command** - w starszych wersjach VS Code może nie być rozpoznawany. Wtedy zacznij prompt od słów **"Tylko plan, nie pisz jeszcze kodu"** zamiast `/plan`.

**Wariant A czasem trafia szybko** - to się zdarza, bo zadanie jest małe (jedna metoda + parę testów). Lekcja Plan Mode wzmacnia się dopiero przy większych zadaniach - zobaczysz to w Bloku 4. Tu chodzi o **wyczucie**, nie o twarde porównanie czasów.

## Co dalej

Plan Mode jest narzędziem do większych refaktorów, takich jak ten z Bloku 4. Jeśli to ćwiczenie pokazało ci, że "plan przed kodem" daje czystszy wynik, to w Bloku 4 zobaczysz, jak ten sam mechanizm prowadzi cię przez refaktor 80 linii kodu z `double` i magic numbers na `BigDecimal` z testami.

---

## ✅ Blok 3 skończony

Czekaj na sygnał prowadzącego do startu **Bloku 4 (★ główne ćwiczenie warsztatu, 25 min)**. Następny plik dla ciebie: `../04-main-exercise/EXERCISE_STEPS.md`.

**Przed Blokiem 4:** zamknij okno VS Code z Bloku 2 (`workshop-blok2/`), jeśli jeszcze otwarte. W Bloku 4 Copilot musi widzieć tylko repo warsztatowe.

## Powiązane pliki w repo

- `../04-main-exercise/src/main/java/com/n8/workshop/iban/IbanValidator.java` - plik, na którym pracujesz w tym ćwiczeniu
- `../04-main-exercise/EXERCISE_STEPS.md` - następne ćwiczenie (Blok 4, główne ćwiczenie warsztatu)
- `../AGENTS.md` (root) - konwencje warsztatu, których trzyma się Copilot
- `../bonus-03-copilot-cli/EXERCISES.md` - Plan Mode w terminalu (Copilot CLI), do samodzielnej eksploracji po warsztacie
