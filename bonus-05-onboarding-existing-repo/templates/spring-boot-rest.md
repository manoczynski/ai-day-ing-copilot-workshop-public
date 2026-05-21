# AGENTS.md - <NAZWA_PROJEKTU>

> **Szablon**: Spring Boot REST microservice. Skopiuj do swojego repo jako `AGENTS.md`, wypełnij placeholdery `<...>`, dostosuj po audycie.

## Kontekst projektu

`<NAZWA_PROJEKTU>` to `<typ: microservice REST | klient zewnętrznego API | gateway | inny>` obsługujący domenę `<domena: konta | pożyczki | klienci | płatności | inne>`. Używany przez `<liczba konsumentów>` aplikacji i `<liczba użytkowników końcowych>` końcowych użytkowników. Krytyczność: `<dev | staging | prod | core | mission-critical>`.

## Stack

- **Java 17** (NIE 8, NIE 11)
- **Spring Boot 3.x**
- **Maven** (NIE Gradle)
- **PostgreSQL** + **Spring Data JPA** + **Hibernate 6**
- **Flyway** dla migracji DB
- **JUnit 5** + **AssertJ** + **Mockito** (`@MockBean` dla beanów Springa)
- **Spring Boot Test** + **MockMvc** dla testów REST
- **Testcontainers** dla testów integracyjnych z bazą
- **SLF4J** + **Logback** dla logowania
- **OpenAPI 3** (springdoc-openapi) dla dokumentacji API
- **Spring Security** + **OAuth2 Resource Server** (JWT) dla autentykacji

## Konwencje globalne

### Język
- **Identyfikatory** (klasy, metody, zmienne): angielski
- **JavaDoc, komentarze, README, opisy w OpenAPI**: polski
- **Komunikaty błędów dla użytkownika** (`ProblemDetail.detail`): polski
- **Komunikaty logów**: angielski (bo zespół oncall międzynarodowy)
- **Commit messages**: angielski, conventional commits

### Typy danych dla finansów
- **BigDecimal** dla kwot pieniężnych, scale 2, `RoundingMode.HALF_UP`
- **BigDecimal** dla stóp procentowych, scale 6
- NIGDY `double` ani `float`
- `BigDecimal.compareTo` dla porównań, NIGDY `equals` (bo 1.0 ≠ 1.00 dla equals)
- `BigDecimal.divide` zawsze z `RoundingMode` w argumencie

### Walidacja API
- Request DTO z adnotacjami `@Valid`, `@NotNull`, `@Positive`, `@Min`, `@Max`, `@Pattern`
- Walidacja domeny dodatkowo w warstwie Service (`Objects.requireNonNull`, signum checks)
- Błędy walidacji → `400 Bad Request` z `ProblemDetail` (RFC 9457)
- Komunikat błędu po polsku, ale bez ujawniania PII

### Testy
- Naming: `should_<oczekiwany>_when_<warunek>()`
- Asercje: AssertJ fluent (`assertThat(x).isEqualByComparingTo("100.00")`)
- Pokrycie: `<próg>%` line coverage, `<próg>%` branch coverage
- `@WebMvcTest` dla testów kontrolerów (slice tests)
- `@SpringBootTest` + Testcontainers dla testów integracyjnych
- Brak `Mockito.mock` na value objectach (BigDecimal, LocalDate, String)
- Fixtury testowe BEZ realistycznych PII (PESEL `00000000000`, IBAN `PL00...`)

### Logowanie
- SLF4J `Logger`, NIE `System.out.println`
- Placeholdery `{}`, NIE konkatenacja (`"x=" + x`)
- Logi nigdy nie zawierają PII (PESEL, IBAN, imion klientów)
- Identyfikator klienta jako UUID, NIE PESEL
- Trace ID (MDC) w każdym requeście

### Dokumentacja
- JavaDoc po polsku dla publicznych klas i metod
- `@param`, `@return`, `@throws` opisane konkretnie (nie generycznie)
- OpenAPI annotations (`@Operation`, `@ApiResponse`) dla każdego endpointu

## Style kodu i naming

- **Pakietowanie**: `<by-feature | by-layer>` (np. `com.ing.<projekt>.loans.{controller,service,repository}` lub `com.ing.<projekt>.{loans,accounts,customers}`)
- **Sufixy klas**: `*Controller`, `*Service`, `*Repository`, `*Mapper`, `*Request`, `*Response`, `*Entity`
- **Records** dla DTO (Request/Response). NIE używamy Lomboka w nowym kodzie.
- **Entity** to klasyczne klasy (Hibernate ma issues z records dla `@Entity`)
- **Mappers** dedykowane (MapStruct opcjonalnie, ale wstrzykiwany przez Spring)
- Switch expressions zamiast switch statements, gdzie się da
- Pattern matching dla `instanceof` (Java 17)
- `var` dla lokalnych zmiennych z oczywistym typem

## Struktura katalogów

```
src/main/java/com/ing/<projekt>/
├── <feature>/
│   ├── api/         # controllers, DTOs
│   ├── domain/      # business logic, value objects
│   ├── persistence/ # entities, repositories
│   └── client/      # zewnętrzne API clients (opcjonalnie)
├── config/          # Spring config, security, OpenAPI
├── common/          # wspólne wyjątki, ProblemDetail handler
└── Application.java # entry point

src/main/resources/
├── application.yml
├── application-{dev,staging,prod}.yml
└── db/migration/    # Flyway scripts
```

## Czego unikać

- ❌ `double` lub `float` dla kwot pieniężnych
- ❌ Magic numbers i magic strings (stałe nazwane w `<feature>/domain/Constants.java`)
- ❌ Jednoliterowe zmienne (poza `i`, `j` w pętlach)
- ❌ Logika biznesowa w `*Controller` (Controller deleguje do `*Service`)
- ❌ JPA Entity zwracane bezpośrednio w response (zawsze przez Mapper → Response DTO)
- ❌ `@Transactional` w `*Controller` (transakcje w warstwie Service)
- ❌ `findAll()` bez paginacji (zawsze `Pageable`)
- ❌ `Optional<X>` jako parametr metody (Optional jest dla return value)
- ❌ Wyjątki w response body bez `ProblemDetail`
- ❌ PII w logach lub w testowych fixturach

## Zasady dla agentów modyfikujących

1. **Trzymaj się hierarchii AGENTS.md**: globalny → per katalog
2. **Każda zmiana sygnatury publicznego API**: deprecation + ticket migracyjny dla konsumentów
3. **Każda nowa migracja DB**: rollback script obok (Flyway `U__*.sql`)
4. **Każda zmiana logowania**: review pod kątem PII przez tech leada
5. **Commit messages** w formacie conventional commits: `feat:`, `fix:`, `refactor:`, `docs:`, `test:`, `chore:`
6. **PR template**: link do AGENTS.md, checkbox "zgodność z konwencjami", test plan

## Hierarchia plików instrukcji

1. User prompt files (VS Code Settings, `chat.instructionsFilesLocations`) - globalne preferencje użytkownika
2. `AGENTS.md` (root) ← ten plik
3. `.github/copilot-instructions.md` - jeśli istnieje, repo-specific dodatki
4. `.github/instructions/*.instructions.md` - pattern-matched (np. `*.java`, `*.sql`)
5. `<feature>/AGENTS.md` - jeśli któraś domena ma odmienne konwencje
6. Inline directives w prompcie

---

> **TODO przed użyciem szablonu**: usuń tę sekcję po wypełnieniu placeholderów `<...>`.
