# Biblioteka 24 promptów do Copilot CLI

24 gotowe do skopiowania prompty pogrupowane w 8 kategorii (po 3 w każdej). Wszystkie testowane w realnych projektach Java, Python i TypeScript.

Konwencja: `<placeholder>` zastąp swoją wartością.

---

## Kategoria 1 - Eksploracja kodu

### 1.1 Onboarding do nieznanego repo

```
copilot run "Przeczytaj README, package.json/pom.xml i podstawowe katalogi.
Wytłumacz w 10 zdaniach: co robi ten projekt, jaki ma stack, jak go uruchomić,
gdzie jest entry point, jakie są główne moduły."
```

### 1.2 Mapa zależności klasy

```
copilot run "Klasa <nazwa> - kto jej używa, czego ona używa.
Pokaż graf (textual) z głębokością 2."
```

### 1.3 Znajdź martwy kod

```
copilot run "Znajdź metody publiczne, które nie są wywoływane nigdzie indziej
w repo. Wyklucz testy. Sortuj po pliku, podaj numer linii."
```

---

## Kategoria 2 - Code review

### 2.1 Samodzielny review przed pushem

```
copilot run "Przejrzyj niezcommitowane zmiany (git diff). Format raportu:
BLOCKER / WARNING / SUGGESTION. Skup się na: precyzji BigDecimal,
braku walidacji metod publicznych, PII w logach, braku testów dla nowych
metod publicznych."
```

### 2.2 Review konkretnego commita

```
copilot run "git show <SHA> - zrób code review. Pytania:
1. Czy commit message jest zgodny z conventional commits?
2. Czy zmiana ma adekwatne testy?
3. Czy są regresje, których nie widać?"
```

### 2.3 Security review

```
copilot run "Przejrzyj zmiany pod kątem bezpieczeństwa. Sprawdź: SQL injection,
sekrety na sztywno w kodzie, PII w logach, brak walidacji danych wejściowych
od użytkownika, renderowanie surowego HTML."
```

---

## Kategoria 3 - Debugging

### 3.1 Stack trace → przyczyna źródłowa

```
cat stacktrace.txt | copilot run "Wyjaśnij ten błąd.
Co jest przyczyną źródłową, a co konsekwencją?
Zaproponuj kolejność debugowania."
```

### 3.2 Flaky test

```
copilot run "Test <ClassName.testName> jest niestabilny: zielony lokalnie,
czerwony na CI. Najczęstsze przyczyny dla testów Spring Boot z
@SpringBootTest? Lista do sprawdzenia, posortowana wg prawdopodobieństwa."
```

### 3.3 Regresja wydajności

```
copilot run "Endpoint /loans/{id} miał 50ms p99, teraz ma 500ms.
Lista pomysłów co sprawdzić, posortowana wg prawdopodobieństwa
dla typowego Spring Boot plus Postgres."
```

---

## Kategoria 4 - Refaktoryzacja

### 4.1 Plan refaktoru

```
copilot plan "Klasa <name> ma 800 linii i miesza 4 odpowiedzialności.
Zaproponuj plan refaktoru z minimalnym ryzykiem dla miejsc wywołań."
```

### 4.2 Wprowadzenie wzorca

```
copilot run "Endpoint <name> ma if/else po typie konta (REGULAR/SAVINGS/PREMIUM).
Zaproponuj refaktor ze wzorcem Strategy. Pokaż diff."
```

### 4.3 Migracja Java 8 → 17

```
copilot run "Wymień klasy w src/main/java, które najbardziej skorzystałyby
na funkcjach Java 17 (records, switch expressions, pattern matching).
Sortuj po wpływie. Wyklucz @Entity (JPA)."
```

---

## Kategoria 5 - Testowanie

### 5.1 Generuj testy dla przypadków brzegowych

```
copilot run "Metoda <Class.method> ma testy dla happy path. Zaproponuj
listę 10 przypadków brzegowych i niepoprawnych danych wejściowych,
których brakuje. Format: nazwa testu w stylu should_X_when_Y."
```

### 5.2 Parametrized test z fixturami z CSV

```
copilot run "Przepisz testy w <TestClass> na @ParameterizedTest z
@CsvFileSource. Wartości testowe wyciągnij do src/test/resources/<name>.csv."
```

### 5.3 Testy charakteryzujące (przed refaktorem)

```
copilot run "Zanim zrobimy refaktor <Class>, wygeneruj testy charakteryzujące,
czyli testy, które utrwalają BIEŻĄCE zachowanie (nawet jeśli jest dziwne),
żebyśmy widzieli, kiedy refaktor je zmienia."
```

---

## Kategoria 6 - Dokumentacja

### 6.1 Wygeneruj JavaDoc po polsku

```
copilot run "Dla metod publicznych w <Class> wygeneruj JavaDoc po polsku
z @param, @return, @throws. Konwencja: opis w jednym zdaniu, potem szczegóły
w punktach."
```

### 6.2 README dla podmodułu

```
copilot run "Katalog <path> ma kod, nie ma README. Zrób README:
- co tu jest (3 zdania)
- jak uruchomić
- główne klasy i funkcje
- linki do testów"
```

### 6.3 Changelog z git log

```
git log <ostatni-tag>..HEAD --oneline | copilot run "Pogrupuj commity w
sekcje (Features / Fixes / Refactor / Docs). Format: Keep a Changelog."
```

---

## Kategoria 7 - Git workflow

### 7.1 Commit message z diffa

```
git diff --staged | copilot run "Zaproponuj wiadomość commita w stylu
conventional commits dla tych zmian. Format: typ(zakres): podsumowanie
i opcjonalnie treść."
```

### 7.2 Opis PR

```
git diff main...HEAD | copilot run "Zrób opis PR w formacie:
## Summary (3 punkty)
## Changes (lista)
## Test plan (pola wyboru)
Krótko, bez marketingowych haseł."
```

### 7.3 Strategia rebase

```
copilot run "Moja gałąź ma 12 commitów. Chcę zostawić czystą historię
przed PR. Zaproponuj plan: squash plus reword. Pokaż dokładne polecenia git rebase -i."
```

---

## Kategoria 8 - Cross-cutting

### 8.1 Audyt konwencji w monorepo

```
for repo in */; do
    (cd "$repo" && copilot run "Czy ten projekt ma AGENTS.md albo .github/copilot-instructions.md? Jeśli nie, pokaż, jak bym miał napisać dla niego (max 30 linii)." 2>/dev/null)
done
```

### 8.2 Migracja zależności (np. JUnit 4 → 5)

```
copilot run "W pom.xml widzę JUnit 4. Zrób plan migracji do JUnit 5:
- zmiana zależności
- skrypt sed lub find-replace dla najczęstszych wzorców
- lista rzeczy do ręcznej zmiany (nie da się ich załatwić sedem)"
```

### 8.3 Onboarding nowego programisty

```
copilot run "Nowy programista dołącza do projektu. Wygeneruj listę kontrolną
pierwszych 3 dni: co przeczytać, co uruchomić, jakie zgłoszenie dostać
na rozgrzewkę. Format: dzień po dniu, maksymalnie 5 punktów dziennie."
```

---

## Zasady pisania własnych promptów

1. **Konkrety zamiast ogólników.** Zamiast "przejrzyj kod" napisz "przejrzyj kod pod kątem X, Y, Z".
2. **Format wyniku.** Powiedz, w jakim formacie chcesz odpowiedź (lista punktów, JSON, tabela).
3. **Ograniczenia.** "Maksymalnie 5 zdań", "wyklucz testy", "tylko BLOCKERY" - to oszczędza czas.
4. **Iteracja.** Pierwszy prompt rzadko jest najlepszy. Spodziewaj się 2-3 prób.
5. **Zachowaj te, które działają.** Najlepsze prompty wrzuć do `~/.copilot/prompts/` jako pliki, łatwo wtedy ich powtórnie użyć.

## Aliasy w shell

Często używanym promptom warto zrobić alias:

```bash
# ~/.zshrc
alias cr='copilot run "Samodzielny review niezcommitowanych zmian: BigDecimal, walidacja, PII, testy. Format BLOCKER/WARNING/SUGGESTION"'
alias cplan='copilot plan'  # uwaga: nazwa "plan" jest częsta, użyj prefiksu
alias cmsg='git diff --staged | copilot run "Wiadomość commita w stylu conventional commits dla zmian w staging."'
```

Dzięki temu `cr` to 2 znaki zamiast 200.
