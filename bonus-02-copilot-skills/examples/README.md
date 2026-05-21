# Pomysły na własne skille

Lista zaczątków do dokończenia przez Ciebie / Twój zespół. Każdy jest na pół godziny pracy + iteracja na własnych przykładach.

## 1. `sql-to-spark`

**Trigger:** "Przepisz to HQL na PySpark" / "migracja Hive QL"

**Co skill powinien zawierać:**
- Mapping HQL operators → Spark equivalents (`COALESCE`, `LATERAL VIEW EXPLODE`, window functions)
- Type system differences (NULL handling, decimal precision)
- Pułapki wydajnościowe (broadcast join hints, `repartition` vs `coalesce`)
- Strategia testowania (`spark-testing-base`, `chispa`)
- Antywzorce: `SELECT *`, ignorowanie partycji
- Idiom: czemu często Spark SQL bywa lepszy od DataFrame API dla migracji 1 do 1

**Przypadek użycia:** ING ma legacy ETL w HQL/SAS, docelowo Python i Spark. Skill aktywuje się dla par developerów robiących migrację.

## 2. `spring-boot-rest`

**Trigger:** "Utwórz nowy REST endpoint" / "kontroler Spring"

**Co skill powinien zawierać:**
- Standard layered architecture (Controller → Service → Repository)
- Walidacja request DTO przez `@Valid` + `@NotNull`, `@Positive`, etc.
- Error handling przez `@RestControllerAdvice` + `ProblemDetail` (RFC 9457)
- Pagination via `Pageable`
- OpenAPI annotations
- Testy: `@WebMvcTest` dla kontrolera, `@SpringBootTest` dla testów integracyjnych
- Antywzorzec: logika biznesowa w kontrolerze, wyjątki w body odpowiedzi bez `ProblemDetail`

**Przypadek użycia:** Standaryzacja nowych endpointów w zespole. Bez tego każdy programista pisze inaczej.

## 3. `pii-mask`

**Trigger:** "Czy ten kod nie zostawia PII w logach" / "PII audit"

**Co skill powinien zawierać:**
- Lista pól typu PII w domenie bankowej: PESEL, numer konta, imię, nazwisko, adres, telefon, email
- Heurystyki nazewnicze (pola `customer`, `client`, `account`, `name`, `email`, `pesel`, etc.)
- Sposoby maskowania: pełna redakcja (`***`), hash (SHA-256 z saltem), last-4-digits
- Lokalizacje do sprawdzenia: SLF4J calls, `toString()`, `equals()`, exception messages, test fixtures, swagger docs
- Konkretne regexy do PESEL (`\d{11}`), IBAN PL (`PL\d{26}`)
- Anti-patterns: `log.debug(customer)`, `throw new Exception("Customer " + name)`, hardcoded PESEL w fixturach

**Przypadek użycia:** Audyt przed merge do main oraz brama zgodności przed wdrożeniem na środowisko przedprodukcyjne.

## 4. `cobol-explainer`

**Trigger:** "Wyjaśnij ten COBOL" / "co robi ten paragraph"

**Co skill powinien zawierać:**
- Słowniczek pojęć COBOL → Java: PARAGRAPH → metoda, PERFORM → wywołanie, klauzule PIC → typy
- Schemat objaśniający (DATA DIVISION, PROCEDURE DIVISION)
- Reguły interpretacji ARITHMETIC, MOVE, COMPUTE
- Pułapki: PERFORM THRU, ALTER, łańcuchy GOTO, COMP-3 packed decimals
- Format wyjścia: krok po kroku, pseudokod, ewentualnie odpowiednik w Javie
- Antywzorzec dla skilla: zaczynanie od wyjaśnienia linijka po linijce. Lepiej: wyjaśnienie na poziomie paragrafu.

**Przypadek użycia:** Banki mają legacy COBOL w core. Ekspert COBOL idzie na emeryturę, kod zostaje. Skill pomaga początkującym developerom Java zrozumieć, co się tam dzieje, zanim zaczną migrować.

## Wspólny szkielet do startu

```markdown
---
name: <name>
description: Use when <kiedy się aktywuje>
---

# <Skill Title>

## When to use
- ...

## When NOT to use
- ...

## Checklist / Procedure
1. ...

## Examples
### Example 1: <title>
**Input:** ...
**Output:** ...

## Anti-patterns
- ❌ ...
- ✅ ...
```

Skopiuj, wypełnij, przetestuj na 3 reprezentatywnych scenariuszach, iteruj.
