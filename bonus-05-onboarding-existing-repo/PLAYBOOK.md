# PLAYBOOK - 3 kroki do AGENTS.md dla istniejącego repo

Pełny przepis krok po kroku. Łącznie 15-30 minut na średniej wielkości repo (do 200 plików kodu źródłowego). Wykonuj kolejno, każdy krok zakłada wynik poprzedniego.

## Założenia wstępne

- Otwarte VS Code z **istniejącym** repo jako workspace
- Copilot Chat aktywny, tryb **Agent**, model Claude Opus 4.5 albo równoważny
- Brak `AGENTS.md` w root repo (jeśli jest, masz wybór: rozszerz albo zacznij od nowa)
- 15-30 minut nieprzerwanego czasu

## Krok 1 - Audit konwencji (5-10 min)

Cel: zebrać fakty o repo, zanim Copilot zacznie zgadywać. Im więcej faktów, tym lepsza pierwsza wersja `AGENTS.md`.

### Sub-krok 1.1: Stack i build

W Copilot Chat:

```
Przeanalizuj root tego repo i wypełnij następujące pola:

- Język główny (Java? Python? TypeScript? Kotlin? Mieszany?)
- Wersja języka (Java 11/17/21? Python 3.10/3.11/3.12? Node 18/20/22?)
- Build tool (Maven? Gradle? npm? Poetry? sbt?)
- Framework główny (Spring Boot? Quarkus? FastAPI? Express? React? Angular?)
- Framework testowy (JUnit 5? TestNG? pytest? Jest? Vitest?)
- Biblioteka asercji (AssertJ? Hamcrest? Chai? expect?)
- Mockowanie (Mockito? wiremock? msw? unittest.mock?)
- Linter / formatter (Checkstyle? ESLint? black? prettier?)

Bazuj na faktycznych plikach: pom.xml/build.gradle/package.json/pyproject.toml,
.editorconfig, configs lintersów, importach w kilku reprezentatywnych klasach.
NIE zgaduj, jeśli nie widzisz pliku, napisz "nie znalazłem".
```

**Co dostaniesz**: tabelę z 8 polami. Jeśli któreś jest "nie znalazłem", to znak, że masz okazję dodać brakujący config (np. brak `.editorconfig` w repo to dziura, którą warto załatać).

### Sub-krok 1.2: Konwencje finansowe / domenowe

Dla projektów bankowych:

```
Sprawdź w kodzie:

1. Czy używamy double/float gdziekolwiek dla kwot pieniężnych?
   - jeśli tak, wymień 5 najgorszych miejsc z lokalizacją (plik:linia)
2. Czy używamy BigDecimal z RoundingMode wszędzie?
   - jeśli widzisz BigDecimal.divide bez RoundingMode, wymień
3. Czy są walidacje argumentów public methods (Objects.requireNonNull)?
4. Czy są PII w logach (PESEL, IBAN, imię)?
   - wymień podejrzane miejsca
5. Czy używamy SLF4J czy System.out.println?
6. Czy testy używają realistycznych PESEL/IBAN?

NIE poprawiaj kodu na tym etapie. Tylko inwentaryzacja.
```

**Co dostaniesz**: listę kierunkową. Każde "tak, używamy double w 14 miejscach" to fakt, który **wprost wpisujesz w AGENTS.md** jako konwencję "BigDecimal wszędzie, double wycofujemy". To różnica między AGENTS.md, który opisuje stan idealny, a AGENTS.md, który opisuje **rzeczywistość plus kierunek**.

### Sub-krok 1.3: Struktura i naming

```
Spójrz na strukturę src/main/java (albo równoważnej dla innych stacków):

1. Czy projekt jest pakietowy by-feature (com.x.loans, com.x.accounts)
   czy by-layer (com.x.controller, com.x.service, com.x.repository)?
2. Naming klas - czy używamy *Service, *Repository, *Controller, *Mapper, *DTO sufixów?
3. Naming testów - should_X_when_Y? testFoo? camelCase?
4. Records czy POJOs?
5. Czy są sealed types?
6. Jakaś inna konwencja, którą zauważasz?

Cytuj 2-3 reprezentatywne klasy.
```

**Co dostaniesz**: opis stylu kodu, który możesz przekleić do AGENTS.md jako sekcję "Style kodu i naming".

### Sub-krok 1.4: Specyfika projektu

```
Czy w repo widzę:

- Migracje DB (Flyway? Liquibase? Alembic? prisma?)
- API contract (OpenAPI? GraphQL schema?)
- Feature flags (LaunchDarkly? własne?)
- Configi środowiskowe (.env, application-{dev,prod}.yml)?
- CI/CD (.github/workflows? azure-pipelines.yml? .gitlab-ci.yml?)
- Coverage tool (JaCoCo? coverage.py?)

Wymień to, co znajdziesz, plus 1 zdanie opisu jak jest skonfigurowane.
```

**Co dostaniesz**: kontekst, który chcesz wpisać w AGENTS.md jako "Infrastruktura i workflow".

## Krok 2 - Generowanie AGENTS.md (5-10 min)

Cel: zebrać wyniki Kroku 1 w spójny `AGENTS.md`. Tu Copilot robi 80% pracy.

### Sub-krok 2.1: Wybierz szablon

Otwórz `bonus-05-onboarding-existing-repo/templates/README.md` i wybierz najbliższy szablon dla typu twojego projektu:

- REST microservice → `spring-boot-rest.md`
- Batch ETL → `batch-etl-spark.md`
- Pure domain library → `java-library.md`
- dbt project → `dbt-project.md`
- React frontend → `frontend-react.md`

Jeśli żaden nie pasuje, zacznij od najbliższego i adaptuj.

### Sub-krok 2.2: Złóż AGENTS.md z faktów

```
Na podstawie wcześniejszej analizy tego repo (kroki 1.1-1.4)
i szablonu <ścieżka_do_szablonu>:

Wygeneruj root AGENTS.md dla tego repo. Format:
- Kontekst projektu (1 akapit, czym to repo jest)
- Stack (lista, wzięta z analizy)
- Konwencje (lista, wzięta z analizy + szablonu)
- Struktura katalogów (krótki opis na podstawie src/)
- Czego unikać (konkretne anti-patterns z faktów, np. "double dla pieniędzy")
- Zasady dla agentów modyfikujących (krótka lista)

Język: polski. Bez kalek angielskich. Bez em dash.
Maksymalnie 150 linii. Krótkie sekcje, bullet pointy, konkretne przykłady.
```

**Co dostaniesz**: szkielet `AGENTS.md` w stylu zgodnym z warsztatowym.

### Sub-krok 2.3: Iteracja

Pierwsza wersja prawie zawsze jest:
- za długa (200+ linii, nikt nie przeczyta)
- za ogólna ("dbamy o jakość kodu" - bezużyteczne)
- pomijająca rzecz, którą uważasz za ważną (bo Copilot tego nie widział)

Iteruj wprost:

```
Pierwsza wersja jest za długa. Skróć do 120 linii.
Usuń wszystko, co nie jest specyficzne dla TEGO repo
(np. "używamy Javy" jest oczywiste, "BigDecimal scale 2 dla PLN" już nie).
```

```
Brakuje sekcji o testach mutacyjnych - używamy PIT.
Dodaj konwencję: każde PR musi mieć mutation coverage > 70%.
```

```
W "czego unikać" dodaj: "Lombok @Data jest zakazany w tym repo,
używamy records albo manual POJO z final fields".
Powód: poprzednia decyzja architektoniczna z 2024.
```

## Krok 3 - Walidacja w parze (5-10 min)

Cel: świeże oko. Copilot zgadł na podstawie kodu, ty wiesz **dlaczego** kod jest tak napisany.

### Sub-krok 3.1: Self-review (2 min)

Przeczytaj wygenerowany `AGENTS.md` na świeżo, pytaj:

- **Czy każda konwencja jest egzekwowalna?** "Dbamy o jakość" jest nieegzekwowalne. "BigDecimal scale 2 dla PLN, RoundingMode.HALF_UP" jest.
- **Czy każdy bullet jest unikatowy dla tego repo?** Jeśli mówi rzecz oczywistą (np. "używamy Javy"), wykreśl.
- **Czy są sprzeczności?** "Records preferowane" i jednocześnie "Lombok @Data dla DTOs" to sprzeczność.
- **Czy jest sekcja "kontekst projektu"?** Bez tego Copilot w przyszłości nie zrozumie, dlaczego konwencje są takie.

### Sub-krok 3.2: Pair review (3-5 min)

Pokaż `AGENTS.md` koledze z zespołu (najlepiej: ktoś, kto był w projekcie dłużej niż ty). Zadaj 2 pytania:

1. **Co Copilot zgadł źle?** (np. wpisał "JUnit 4", a my dawno migrowaliśmy na 5)
2. **Czego brakuje?** (np. konwencja, która jest oczywista dla starych członków zespołu, ale Copilot jej nie zobaczył)

To 5 minut pracy kolegi z 5-letnim stażem w projekcie. Wartość: AGENTS.md fiksuje **wiedzę plemienną** w pliku, który Copilot przeczyta przy każdym promtcie.

### Sub-krok 3.3: Test na Copilocie (2 min)

Po commitcie `AGENTS.md`:

1. **Nowy wątek** Copilot Chat
2. Zadaj zadanie typowe dla tego projektu, np.: *"Dodaj endpoint GET /loans/{id} zgodnie z naszymi konwencjami"*
3. Zobacz, czy Copilot trzyma się tego, co właśnie wpisałeś w AGENTS.md
4. Jeśli łamie konwencję, znaczy że bullet w AGENTS.md jest niejasny - popraw

## Po playbooku

- Commit `AGENTS.md` jako `chore: add AGENTS.md for Copilot context`
- Dodaj do CODEOWNERS line dla AGENTS.md: ten plik wymaga review tech leada przy każdej zmianie
- Powiadom zespół: nowa konwencja, którą trzeba przeczytać i komentować
- Po 2 tygodniach: review co działa, co nie, popraw

## Pułapki playbooka

### Pułapka 1: kopiuj-wklej z szablonu bez personalizacji

Szablony w `templates/` to **punkt startu**, nie kopia. Jeśli `AGENTS.md` w 90% wygląda jak szablon, to znaczy że Copilot nie patrzył na **twoje** repo. Wymuś personalizację:

```
W wygenerowanym AGENTS.md za dużo jest z szablonu.
Wymień 5 rzeczy, które są specyficzne dla TEGO repo (nie generyczne dla Spring Boot).
Jeśli nie ma 5, wymyśl pytania, których powinieneś zadać, żeby je znaleźć.
```

### Pułapka 2: aspiracje zamiast faktów

Naturalna pokusa: `AGENTS.md` opisuje, jak chcielibyśmy, żeby kod wyglądał. Ale Copilot nie poprawi za nas legacy w jednym ruchu. Jeśli `AGENTS.md` mówi "BigDecimal wszędzie", a w kodzie jest 14 miejsc z double, to Copilot dla nowych klas użyje BigDecimal, ale **nie poprawi tych 14 miejsc**. To OK, jeśli wiesz. Nie OK, jeśli liczysz, że samo się posprząta.

Lepsze sformułowanie: `BigDecimal dla nowego kodu i refaktoru. Istniejące 14 miejsc z double są technical debt do osobnej fali (ticket DEBT-42).`

### Pułapka 3: brak sekcji "co się ZMIENIAJ vs co się NIE ZMIENIA"

`AGENTS.md` może też ograniczać. Przykład: w projekcie z legacy interface `Customer` używamy zarówno `Customer` jak i `CustomerDto`. Jeśli `AGENTS.md` mówi tylko "używamy records dla DTOs", Copilot może spróbować zamienić `Customer` na record - i ścina kontrakt z 20 callerami.

Dobra praktyka: sekcja "co jest stabilne" wymienia publiczne API, które NIE można modyfikować bez migracji wszystkich callerów.

### Pułapka 4: AGENTS.md w monorepo bez per-modułowych nadpisań

W monorepo z 10 modułami **głównie** Java backend + 1 modułem frontend, top-level `AGENTS.md` powie "Java 17, BigDecimal", a Copilot zastosuje to do React kodu. Rozwiązanie: dodaj `AGENTS.md` w katalogu frontend z konwencjami React. Hierarchia z Bloku 2 warsztatu (gdy folder-specific nadpisuje root) załatwia to elegancko.

## Następne kroki

- Powtórz playbook dla 2-3 innych repo w twoim zespole
- Po 5 repo: zauważasz wzorce. Stwórz **własny** szablon dla swojego typu projektu, dodaj do `bonus-05-onboarding-existing-repo/templates/`
- Po 10 repo: rozważ zapakowanie playbooka w skill (`bonus-02-copilot-skills/.github/skills/agents-md-generator/SKILL.md`) - wtedy ten workflow aktywuje się automatycznie, gdy Copilot widzi repo bez AGENTS.md
