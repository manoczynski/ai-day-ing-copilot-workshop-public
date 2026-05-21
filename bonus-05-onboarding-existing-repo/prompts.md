# Biblioteka promptów - generowanie AGENTS.md dla istniejących repo

Gotowe do skopiowania prompty dla każdego sub-kroku playbooka. Wszystkie testowane na typowych projektach Java/Python/TypeScript w bankowym kontekście.

---

## Kategoria 1 - Audit (Krok 1 playbooka)

### 1.1 Audit stosu technologicznego

```
Przeanalizuj root tego repo. Wypełnij tabelę:

| Pole | Wartość | Źródło (plik, w którym to widzisz) |
|---|---|---|
| Język główny | | |
| Wersja języka | | |
| Build tool | | |
| Framework główny | | |
| Framework testowy | | |
| Biblioteka asercji | | |
| Mockowanie | | |
| Linter / formatter | | |
| CI | | |
| Coverage tool | | |

Bazuj na faktycznych plikach: pom.xml/build.gradle/package.json/pyproject.toml,
.editorconfig, configs lintersów. NIE zgaduj. Jeśli nie widzisz pliku, wpisz "nie znalazłem".
```

### 1.2 Audit konwencji finansowych

```
W tym repo szukam konwencji finansowych. Sprawdź:

1. Wszystkie miejsca z `double` lub `float` dla pól/zmiennych z nazwami sugerującymi
   pieniądze (amount, price, balance, fee, rate, interest, principal, capital, value).
   Wymień jako plik:linia.

2. Wszystkie miejsca z BigDecimal.divide bez drugiego/trzeciego argumentu
   (czyli bez RoundingMode). Wymień jako plik:linia.

3. Wszystkie miejsca z BigDecimal.equals (które ZAWODZI dla 1.0 vs 1.00).
   Wymień jako plik:linia.

4. Czy są stałe nazwane dla magic numbers (BELKA_TAX_RATE, MIN_LOAN_AMOUNT)?
   Wymień, gdzie są zdefiniowane.

Sortuj wyniki po pliku, alfabetycznie. Maksymalnie 30 najgorszych przypadków.
NIE poprawiaj kodu.
```

### 1.3 Audit PII w kodzie i logach

```
Przeszukaj cały src/ tego repo i znajdź:

1. Logi (calls do logger.info/debug/warn/error, log.info/debug/...,
   System.out.println, console.log), które zawierają lub mogą zawierać PII:
   pola customer, client, account, name, lastName, firstName, pesel, iban,
   email, phone. Wymień jako plik:linia z fragmentem.

2. Komunikaty wyjątków (throw new ... lub raise) z PII w komunikacie.
   Wymień jako plik:linia.

3. toString() metody w klasach modelu, które zwracają PII.
   Wymień klasę i pola.

4. Fixtury testowe (src/test/) z realistycznymi PESEL (11 cyfr), IBAN (PL\d{26}),
   numerami kont (16 cyfr). Wymień plik:linia.

Format: tabela z kolumnami (plik:linia, typ, fragment). Maksymalnie 40 wpisów.
```

### 1.4 Audit struktury i naming

```
Przeanalizuj strukturę src/main/java (albo equivalent dla innego stacku):

1. Pakietowanie: by-feature (com.x.loans, com.x.accounts)
   czy by-layer (com.x.controller, com.x.service)?
2. Konwencja sufixów klas - jakich używamy: *Service, *Repository, *Controller,
   *Mapper, *DTO, *Entity, *Request, *Response, *Resource?
3. Naming testów - should_X_when_Y, testFoo, camelCase, BDD-style?
4. Records vs POJOs - co dominuje? Czy używamy sealed types?
5. Wzorce - widzę gdzieś Strategy, Factory, Builder, Visitor jako świadomą decyzję?
6. Inne konwencje, które widzisz powtarzające się?

Cytuj 2-3 reprezentatywne klasy dla każdego punktu. Wnioski w 1 paragrafie.
```

### 1.5 Audit infrastruktury i workflow

```
Sprawdź w tym repo obecność:

| Element | Plik | Konfiguracja |
|---|---|---|
| Migracje DB | (Flyway/Liquibase/Alembic/Prisma?) | |
| API contract | (OpenAPI/GraphQL/inny?) | |
| Feature flags | (LaunchDarkly/własne/brak?) | |
| Configi środowisk | (.env, application-*.yml) | |
| CI/CD | (.github/workflows/*, azure-pipelines.yml) | |
| Pre-commit hooki | (.husky/, lefthook.yml, .pre-commit-config.yaml) | |
| Coverage tool | (JaCoCo/coverage.py?) | |
| Linting | (Checkstyle/ESLint/black?) | |
| Dependency mgmt | (renovate.json/dependabot.yml?) | |

Wymień to, co znajdziesz. Dla każdego: 1 zdanie opisu, jak jest skonfigurowane.
```

---

## Kategoria 2 - Generowanie (Krok 2 playbooka)

### 2.1 Złożenie AGENTS.md z faktów

```
Na podstawie wcześniejszej analizy (audit punktów 1.1-1.5)
i szablonu <ścieżka_do_szablonu>:

Wygeneruj root AGENTS.md dla tego repo. Sekcje:

1. **Kontekst projektu** (1 akapit, 3-5 zdań): co to repo robi, dla kogo
2. **Stack** (lista 6-10 punktów z konkretami z audytu 1.1)
3. **Konwencje globalne**:
   - język identyfikatorów (PL/EN dla różnych elementów)
   - typy danych (BigDecimal/double, scale, rounding)
   - walidacja (null checks, signum checks)
   - testy (framework, naming, pokrycie)
   - logowanie (SLF4J/console, format)
   - dokumentacja (JavaDoc/JSDoc/docstrings, język)
4. **Style kodu i naming** (z audytu 1.4)
5. **Struktura katalogów** (1 akapit, opisz tylko nietypowe foldery)
6. **Czego unikać** (lista 5-8 konkretnych anti-patterns z audytu 1.2, 1.3)
7. **Zasady dla agentów modyfikujących**:
   - hierarchia plików instrukcji
   - format commit messages
   - PR / code review process

Język: polski. Unikaj kalek angielskich tam, gdzie istnieje naturalny polski odpowiednik
("public method" → "metoda publiczna", "use case" → "przypadek użycia"). Terminy techniczne
i ugruntowane w żargonie IT zachowuj (`feature`, `workflow`, `by-feature`, `endpoint`,
`commit`, `pull request`).
Bez em dash (Unicode U+2014). Maksymalnie 150 linii. Bullet pointy, konkretne przykłady.
```

### 2.2 Iteracja - skrócenie

```
Wygenerowany AGENTS.md ma <X> linii, to za dużo.
Skróć do 100-120 linii. Wycinanie:
- generyczne stwierdzenia ("dbamy o jakość", "kod ma być czytelny")
- przykłady, które można wnioskować z konwencji (1 przykład na sekcję wystarczy)
- powtórzenia (jeśli "BigDecimal" pojawia się 5 razy, niech będzie raz w sekcji finansów)
```

### 2.3 Iteracja - dodanie brakującej konwencji

```
Brakuje w AGENTS.md konwencji: <opis>.
Powód: <dlaczego ten zespół trzyma się tej konwencji - decyzja architektoniczna,
ticket, regulacja zewnętrzna>.

Dodaj jako bullet w odpowiedniej sekcji. Zachowaj zwięzłość, max 2 linie.
```

### 2.4 Iteracja - poprawa konkretu

```
Bullet "Walidacja inputów" jest za ogólny. Skonkretyzuj:
- Co dokładnie waliduje (null, signum, zakres, format)?
- Jakim mechanizmem (Objects.requireNonNull, @Valid, manual if)?
- Co rzuca przy błędzie (NPE, IAE, własny wyjątek)?
- Jaki komunikat (PL/EN, z wartością/bez)?

Daj 1 konkretny przykład kodu (3-5 linii).
```

### 2.5 Per-folder AGENTS.md

```
W tym repo katalog <ścieżka> ma inny stack/konwencje niż reszta repo
(np. frontend React, podczas gdy reszta to Java backend).

Wygeneruj AGENTS.md specyficzny dla tego katalogu.
Powinien:
- Zacząć od "Uszczegóławia root AGENTS.md"
- Wymienić TYLKO różnice od root (nie powtarzaj wspólnych konwencji)
- Skupić się na stack-specyficznych anti-patterns

Maksymalnie 80 linii.
```

---

## Kategoria 3 - Walidacja (Krok 3 playbooka)

### 3.1 Self-review

```
Przeczytaj wygenerowany AGENTS.md krok po kroku.
Dla każdego bullet zadaj 3 pytania:

1. Czy ten bullet jest egzekwowalny? (czy jest konkretny test, który by go zweryfikował?)
2. Czy ten bullet jest unikatowy dla TEGO repo, czy mówi rzecz oczywistą dla całej Javy?
3. Czy ten bullet jest sprzeczny z innym?

Wymień bullety, które się nie kwalifikują w którymkolwiek z 3 punktów.
Zaproponuj poprawkę dla każdego.
```

### 3.2 Test na Copilocie

```
Wczytałem nowy AGENTS.md w workspace. Zadaję teraz zadanie typowe dla tego projektu:

<konkretne zadanie, np. "Dodaj endpoint GET /accounts/{id} zwracający AccountResponse">

Wygeneruj kod. Zwróć uwagę:
- Czy używasz BigDecimal czy double?
- Czy walidujesz inputy?
- Czy dodajesz testy?
- Czy używasz framework testowego z AGENTS.md (JUnit 5? AssertJ?)?

Po wygenerowaniu kodu sam zweryfikuj: które konwencje z AGENTS.md zignorowałeś,
a które uwzględniłeś?
```

### 3.3 Identyfikacja luk

```
Po przeczytaniu AGENTS.md, wymień 3 sytuacje typowe w tym repo,
dla których AGENTS.md NIE daje wskazówki, jak Copilot ma się zachować.

Przykłady takich sytuacji:
- Dodanie nowej kolumny do tabeli (czy migrate-only? czy z testem?)
- Refaktor public metody (czy z deprecation? czy bezpośrednio?)
- Dodanie zewnętrznego API call (czy z circuit breaker? z retry?)

Dla każdej sytuacji zaproponuj 1-2 zdanie konwencji do dodania.
```

### 3.4 Audit po implementacji

```
Po 2 tygodniach używania AGENTS.md, zbierz statystyki:

1. Ile razy zmodyfikowałem AGENTS.md w tym okresie?
2. Które bullety usunąłem (były bezużyteczne)?
3. Które bullety dodałem (luki wykryte przez praktykę)?
4. Czy Copilot trzyma się konwencji? (subjective, 1-10)

Zaproponuj 3 dalsze ulepszenia AGENTS.md na podstawie tych statystyk.
```

---

## Kategoria 4 - Zaawansowane

### 4.1 Generowanie AGENTS.md dla 10 repo naraz (Copilot CLI)

```
for repo in ~/projects/work/*; do
    echo "=== $(basename $repo) ==="
    (cd "$repo" && copilot run "Wykonaj audit punkty 1.1-1.5 z bonus-05-onboarding-existing-repo/PLAYBOOK.md. Output: kompaktowy raport max 30 linii." 2>/dev/null)
done > /tmp/audit-results.txt

# Później dla każdego repo, gdzie audit ma sens:
copilot run "Wczytaj /tmp/audit-results.txt dla projektu X. Wygeneruj AGENTS.md."
```

### 4.2 AGENTS.md jako PR template

```
Stwórz template PR review w .github/pull_request_template.md, który:
- Linkuje do AGENTS.md
- Ma checkbox "Sprawdziłem zgodność z AGENTS.md"
- Ma sekcję "Jeśli zmieniam konwencje, AGENTS.md jest też zaktualizowany"

Format: minimalistyczny, max 20 linii.
```

### 4.3 AGENTS.md w skill

```
Zapakuj playbook z bonus-05 jako skill Copilot:
.github/skills/agents-md-generator/SKILL.md

description: "Use when working in a repo that lacks AGENTS.md, or
when reviewing existing AGENTS.md for completeness."

Body powinien zawierać:
- 3 kroki z PLAYBOOK.md skondensowane
- Pytania kontrolne self-review
- Format wynikowego AGENTS.md
- Antywzorce z PLAYBOOK.md
```

---

## Zasady pisania własnych promptów do generowania AGENTS.md

1. **Fakty PRZED aspiracjami.** Najpierw audit kodu, potem AGENTS.md. NIE wpisuj "BigDecimal wszędzie", jeśli w kodzie jest 80% double - to fikcja, którą Copilot przyjmie za prawdę, a ty stracisz kontakt z rzeczywistością.

2. **Konkrety, nie ogólniki.** "Dbamy o jakość kodu" jest bezużyteczne. "BigDecimal scale 2, RoundingMode HALF_UP dla wszystkich pól z domeny pieniężnej" jest egzekwowalne.

3. **Krótko.** Maks 150 linii root AGENTS.md. Dłuższy nikt nie przeczyta, Copilot też pewnie skróci go w swoim kontekście.

4. **Iteracja.** Pierwszy AGENTS.md jest zawsze za długi i za ogólny. Drugi po 2 tygodniach pracy jest dużo lepszy. Trzeci po miesiącu jest dobry.

5. **Per-stack szablony.** Jeśli masz 10 repo Spring Boot, jeden dobry szablon Spring Boot oszczędza godziny pracy. Dlatego `templates/` w tym bonusie.
