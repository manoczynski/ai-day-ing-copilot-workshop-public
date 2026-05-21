# Blok 5 - Follow-along: Memory + `#codebase` (5 min na własnym laptopie)

Blok 5 (70:00-80:00) to **demo prowadzącego, nie hands-on**. Patrzysz na ekran, nie klikasz w trakcie. Ten plik jest na potem - 4-5 promptów, które możesz powtórzyć po warsztacie albo w przerwie lunchowej na własnym laptopie, żeby Memory i `#codebase` weszły ci w nawyk.

**Co zrozumiesz po przejściu tego pliku:**

- Kiedy zapisać preferencję w `AGENTS.md`, a kiedy wystarczy Memory.
- Dlaczego `#codebase` to nie grep - rozumie semantycznie.
- Jak indeksowanie repo zmienia codzienną pracę z dużym monorepo.

## Setup

Otwórz to repo warsztatowe (`ai-day-ing-copilot-workshop-public`) w VS Code **na poziomie root** (folder zawierający `README.md`, `AGENTS.md`, `04-main-exercise/`, `bonus-01-spec-kit/` itd. - nie podfolder pojedynczego ćwiczenia):

```bash
code .
```

Jeśli widzisz w Explorer tylko zawartość podfolderu (np. tylko `reference/`, `src/`, `pom.xml`) - zamknij i otwórz poziom wyżej. To ćwiczenie używa `#codebase` na całym repo - bez root nie zadziała poprawnie.

Otwórz Copilot Chat: `Ctrl + Shift + I` (Windows/Linux) lub `Cmd + Shift + I` (macOS). W górnym pasku panelu chatu w rozwijanym menu trybów wybierz **Agent**.

## Część 1 - Copilot Memory (2 min)

Memory zapamiętuje preferencje **między sesjami chat**. Jeśli powiesz Copilotowi raz "w tym projekcie używamy SLF4J", przy następnym otwarciu nowego wątku Copilot będzie wiedział - bez powtarzania w prompcie.

To **różni się** od `AGENTS.md`:

| Aspekt | `AGENTS.md` | Memory |
|---|---|---|
| Gdzie żyje | w repo (plik) | na koncie (sesja Copilota) |
| Dla kogo | dla całego zespołu | tylko dla ciebie |
| Aktywacja | jawna, w pliku | niejawna, z konwersacji |
| Edycja | edytujesz plik | mówisz "zapomnij X" / "zapamiętaj X" |

### Prompt 1.1 - Zapisz preferencję

**Rozpocznij nowy wątek** (przycisk "+" / "New chat" na górze panelu chatu). Wklej:

```
Zapamiętaj: w tym projekcie zawsze używamy SLF4J zamiast System.out.println.
Każdy generowany kod produkcyjny powinien używać Logger, nie standardowego output.
```

**Co dostaniesz:** Copilot odpowie "Zapamiętałem" albo podobnie. Jeśli zwraca generyczne "OK, mam to" bez wzmianki o Memory - być może w twojej organizacji **Memory policy jest OFF**. Patrz "Plan B" niżej.

### Prompt 1.2 - Test pamięci (w **nowym** wątku)

**Rozpocznij kolejny nowy wątek** (przycisk "+" / "New chat"). Wpisz:

```
Napisz krótką klasę do debugowania, która loguje stan zmiennej x.
```

**Co dostaniesz:** klasę z `Logger` SLF4J - **mimo że nie napisałeś tego w prompcie**. To Memory: poprzednia preferencja przeniosła się do nowego wątku.

**Jeśli Copilot użył `System.out.println` mimo Memory:** zapis się nie zachował (np. Memory policy OFF w organizacji). Patrz Plan B.

### Co zapisywać w Memory, a co w `AGENTS.md`

Zasada w jednym zdaniu:

> Jeśli preferencja mieści się w jednym zdaniu - użyj Memory. Trzy akapity - to powinien być `AGENTS.md`.

**Dobre kandydaci na Memory:**
- "Zawsze polskie komunikaty błędów"
- "JUnit 5, nie JUnit 4"
- "Krótkie odpowiedzi, bez wstępów"

**Złe kandydaci** (powinno być w `AGENTS.md`):
- Pełna lista konwencji nazewniczych
- Schemat walidacji argumentów (3 akapity z przykładami)
- Architektura testów

### Plan B - Memory POLICY jest OFF

W banku polityka organizacji **może wyłączać Memory** ze względów compliance. Wtedy zapis się nie utrwala. Fallback - **local memory w VS Code**:

1. Otwórz Settings: `Ctrl + ,` (Windows/Linux) lub `Cmd + ,` (macOS)
2. Wyszukaj `copilot memory`
3. Włącz checkbox "Enable Memory" (lokalny)

Local memory działa **per laptop**, nie per konto. Nie synchronizuje się między urządzeniami. Dla typowego scenariusza (jeden developer, jeden laptop, jeden projekt) - wystarcza.

## Część 2 - Indeksowanie + `#codebase` (3 min)

Copilot indeksuje całe repo (semantycznie + składniowo). Operator `#codebase` pyta **w kontekście całego kodu**, nie tylko otwartego pliku.

| Operator | Co robi |
|---|---|
| `#codebase` | Cały workspace, wyszukiwanie semantyczne |
| `#file:Name.java` | Konkretny plik |
| `#folder:src/main` | Cały katalog |
| `#symbol:CalculateInterest` | Konkretna klasa lub metoda |
| `#selection` | Aktualne zaznaczenie w edytorze |

To repo ma **dwa miejsca** liczące odsetki - celowo, żeby `#codebase` miał co znaleźć:

- `04-main-exercise/src/main/java/com/n8/workshop/CalculateInterest.java` (legacy z głównego ćwiczenia)
- `04-main-exercise/src/main/java/com/n8/workshop/savings/SimpleDepositInterest.java` (drugi kontekst, czysty `BigDecimal`)

### Prompt 2.1 - Zapytanie semantyczne

**Rozpocznij nowy wątek**. Wpisz:

```
#codebase znajdź wszystkie miejsca, gdzie liczymy odsetki bankowe w tym projekcie.
Wymień plik + krótkie streszczenie, co tam się dzieje.
```

**Co dostaniesz:** listę 2 plików powyżej, plus opis "legacy z double, multi-account, podatek Belka inline" vs "BigDecimal, walidacja, pojedynczy depozyt".

**Kluczowy moment:** Copilot **nie szukał napisu** "odsetki" jak `grep`. On rozumie, że obie klasy liczą odsetki, nawet jeśli używają różnych nazw zmiennych i komentarzy. Zwykły grep by tu nie pomógł, bo `SimpleDepositInterest` w komentarzu mówi tylko "compound interest", nie "odsetki".

### Prompt 2.2 - Zapytanie analityczne

W tym samym wątku, idź głębiej:

```
#codebase porównaj te dwa miejsca: czym się różnią pod kątem precyzji (BigDecimal vs double),
walidacji wejścia, i podejścia do podatku Belka?
```

**Co dostaniesz:** Copilot porównuje. Wskazuje precyzję, walidację, podatek. Wymienia konkretne linie kodu.

Ten moment to **Copilot przestaje być autouzupełnianiem, a staje się partnerem do audytu**. Na żywym repo z 500 klasami możesz w 2 minuty zrobić skan: "gdzie nadal używamy double? gdzie brakuje walidacji? gdzie są PII w logach?"

### Prompt 2.3 - Zastosowanie dla twojego zespołu

Wyobraź sobie: masz 8 lat starszego kodu Spring. W swoim repo (po warsztacie, nie teraz) odpal:

```
#codebase znajdź wszystkie miejsca, które używają wycofanego API Spring Security 5
(np. WebSecurityConfigurerAdapter, AntPathRequestMatcher z deprecated overloads).
Wymień plik:linia + krótki opis, co trzeba zmienić.
```

To jest dokładnie ten typ zadania, gdzie `#codebase` zmienia sposób pracy. Zamiast 2h ręcznego skanowania - 2 minuty z weryfikacją.

## Część 3 - Hierarchia kontekstu (1 min, do przeczytania)

6 warstw kontekstu Copilota, od najmniej do najbardziej specyficznego:

```
User prompt files (VS Code Settings)        ← globalne preferencje konta
  chat.instructionsFilesLocations             (poza repo, sync przez Settings Sync)
        ↓
AGENTS.md (root repo)                       ← konwencje całego repo
        ↓
.github/copilot-instructions.md             ← repo-specific
        ↓
.github/instructions/*.instructions.md      ← pattern-matched (np. *.java)
        ↓
04-main-exercise/AGENTS.md                  ← folder-specific
        ↓
+ inline directives w prompcie              ← najbardziej specyficzne
```

> Uwaga: w innych narzędziach Copilot user-level instructions żyją gdzie indziej - **Copilot CLI** używa `~/.copilot/copilot-instructions.md`, **JetBrains** używa `~/.config/github-copilot/global-copilot-instructions.md`. Powyższy diagram dotyczy **VS Code Chat**.

Każdy poziom nadpisuje poprzedni. Każdy jest opcjonalny. W praktyce 90% projektów potrzebuje tylko `AGENTS.md` w root.

W tym warsztacie używałeś trzech z tych sześciu warstw:
- root `AGENTS.md` (Blok 2)
- per-folder `AGENTS.md` w `04-main-exercise/` (Blok 4)
- inline w prompcie (cały czas)

Pozostałe trzy masz pod ręką, gdy ich potrzebujesz.

## Pułapki

**`#codebase` zwraca puste albo "indeksowanie..."** - przy pierwszym użyciu w dużym repo indeksowanie zajmuje 1-2 minuty. Zaczekaj, spróbuj jeszcze raz. Jeśli problem zostaje > 5 min, zrestartuj VS Code.

**Memory zapisał coś, czego nie chcę** - powiedz Copilotowi wprost: `Zapomnij że używamy SLF4J w tym projekcie`. Albo wejdź w ustawienia konta Copilot i wyczyść Memory ręcznie.

**`#codebase` zwraca pliki, które wyglądają semantycznie podobnie, ale nie pasują** - to się zdarza. Zawsze weryfikuj otwierając plik. Copilot daje **dobry punkt startu**, nie ostateczną odpowiedź.

## Co dalej

- **Bonus HQL → PySpark** (`../06-hql-bonus/FOLLOW_ALONG.md`) - jeśli pracujesz z data engineering, ten bonus pokazuje migrację HQL na PySpark z Plan Mode + `#codebase`. ~10 min, fajny dla zespołów data.
- **`bonus-03-copilot-cli/`** - `#codebase` ma analog w terminalu (`copilot run "..."` w katalogu z kodem). 24 prompty do skopiowania.
- **`bonus-05-onboarding-existing-repo/`** - generator `AGENTS.md` dla istniejącego repo. Pierwszy krok playbooka to dokładnie to, co tu robisz - audit przez `#codebase`.

## Powiązane pliki w repo

- `../04-main-exercise/src/main/java/com/n8/workshop/CalculateInterest.java` - pierwszy "obiekt" `#codebase`
- `../04-main-exercise/src/main/java/com/n8/workshop/savings/SimpleDepositInterest.java` - drugi
- `../AGENTS.md` (root) - konwencje warsztatu, alternatywa dla Memory
- `../.github/copilot-instructions.md` - dodatkowa warstwa instrukcji repo-specific
