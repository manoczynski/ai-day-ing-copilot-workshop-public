# EXERCISES - Copilot CLI

4 progresywne ćwiczenia, ~15 min każde. Wymagają zainstalowanego Copilot CLI (`HOW_TO_INSTALL.md`).

## Ćwiczenie 1 - Rubber duck w terminalu

**Cel:** Poczuć, że Copilot CLI to nie wyszukiwarka, tylko **partner do gadania**.

### Setup

Cofnij się do dowolnego projektu Java lub repo które dobrze znasz.

### Polecenie

```bash
copilot run "Chcę zrefaktoryzować klasę X tak, żeby Y. Zadaj mi 5 pytań,
których odpowiedzi byś potrzebował, zanim zaczniesz."
```

### Co obserwować

- Pytania konkretne czy ogólne? Jeśli ogólne, daj więcej kontekstu (`#file:src/main/.../X.java`)
- Czy Copilot bierze pod uwagę to, co już jest w repo, czy proponuje ogólnik "co byś zrobił z dowolną klasą"
- Czy pytania mają wartość, czy odpowiadasz "nie wiem, sprawdzę"

### Lekcja

Rubber ducking to nie marnowanie czasu. To wymuszanie precyzji **u Ciebie**. Jeśli nie wiesz, co odpowiedzieć Copilotowi, prawdopodobnie też nie wiesz, jak rozwiązać problem.

---

## Ćwiczenie 2 - Plan Mode dla większego zadania

**Cel:** Zobaczyć, że **plan przed kodem** = lepsze wyniki.

### Setup

Wybierz zadanie z backlogu: poprawka błędu, mała funkcjonalność, refaktor. Nie cały tydzień pracy, raczej jeden dzień.

### Polecenie

```bash
copilot plan "<konkretny opis zadania, 2-3 zdania>"
```

Przykład:

```bash
copilot plan "Chcę dodać endpoint POST /loans, który waliduje LoanRequest
przez JSR-303, zapisuje przez LoanService, zwraca 201 z LoanResponse.
Stos: Spring Boot 3, Java 17, JPA. Walidacja zgodna z constitution
projektu (BigDecimal, sprawdzenia null, polskie komunikaty błędów)."
```

### Co obserwować

- Czy plan ma uporządkowane fazy (najpierw typy → serwis → kontroler → testy)?
- Czy plan podaje konkretne nazwy klas?
- Czy plan zawiera estymacje (jeśli o nie poprosisz)?
- Czy plan respektuje `AGENTS.md` w bieżącym katalogu?

### Drugi krok

Po przeczytaniu planu popraw go:

```bash
copilot plan "Plan był OK, ale brakuje sekcji obsługi błędów.
Dodaj fazę: @RestControllerAdvice z mapowaniem walidacji na 400."
```

### Lekcja

Plan Mode to nie "kosztowna formalność", tylko oszczędność czasu w fazie wykonania. Każda godzina w planie oszczędza 3 godziny refaktoru.

---

## Ćwiczenie 3 - `/delegate` (wieloagentowo)

**Cel:** Użyć Copilota do orkiestracji większego zadania, gdzie poszczególne podzadania są **niezależne**.

### Setup

Wybierz zadanie, które ma 3 lub więcej niezależnych podzadań. Np. dodanie nowego endpointu plus testów, dokumentacji i wpisu w changelogu.

### Polecenie

```bash
copilot run "Rozbij to zadanie na niezależne podzadania, które mogą być
wykonane równolegle. Format: lista z opisem każdego podzadania.
Zaproponuj, czy każde z nich powinien zrobić ten sam agent, czy delegować
do osobnych sesji (rozumowanie)."

# Zadanie: Dodaj endpoint GET /loans/{id} z testami integracyjnymi
# i wpisem do CHANGELOG, plus zaktualizuj OpenAPI spec.
```

### Co obserwować

- Czy Copilot rozumie, że niektóre rzeczy są **sekwencyjne** (testy zależą od endpointu)?
- Czy `/delegate` (jeśli dostępne w Twojej wersji CLI) faktycznie uruchamia podagentów?
- Czy efekt końcowy jest spójny i nie ma sprzeczności między tym, co zrobił agent A, B i C?

### Drugi krok

```bash
copilot run "Wykonaj podzadanie 1 (endpoint). Po skończeniu daj znać, że
podzadanie 2 może się zacząć."
```

### Lekcja

Delegacja działa świetnie dla zadań, w których **podzadania są niezależne** lub mają **jasny interfejs** (`endpoint zwraca DTO X` → testy mogą wiedzieć, czego się spodziewać). Działa źle, gdy podzadania mają subtelne, niejawne zależności.

---

## Ćwiczenie 4 - Praca na wielu repo

**Cel:** Zobaczyć, że Copilot CLI naprawdę się sprawdza tam, gdzie VS Code nie sięga, czyli **w pracy na wielu repo naraz**.

### Setup

Wybierz katalog, w którym masz 3+ repo (np. monorepo wspólnych libów, albo katalog `~/code/work/`).

### Polecenie

```bash
for repo in */; do
  echo "=== $repo ==="
  (cd "$repo" && copilot run "Czy ten projekt używa BigDecimal, czy double dla finansów? Jeśli double, wymień najgorsze przypadki." 2>/dev/null)
done
```

### Co obserwować

- Czy Copilot rozpoznaje, że jest w innym repo za każdym razem?
- Czy odpowiedzi są spójne formatowo (porównywalne między repo)?
- Czy są fałszywe trafienia (np. `double` w testach jednostkowych jest OK)?

### Drugi krok

Zbierz wyniki i użyj Copilota do agregacji:

```bash
# Zapisz wyniki z poprzedniego loopa do pliku
... > scan-results.txt

copilot run "Przeczytaj scan-results.txt. Podsumuj: które repo wymagają
priorytetowej migracji double → BigDecimal? Sortuj po liczbie wystąpień."
```

### Lekcja

Copilot CLI to automatyzacja na poziomie ekosystemu, nie pojedynczego pliku. W ten sposób można zrobić audyt bezpieczeństwa, audyt konwencji i audyt zależności w 20 repo w ciągu 10 minut.

---

## Co dalej

- Eksperymentuj z `prompts/example-prompts.md`: biblioteka 24 promptów do skopiowania
- Zacznij budować **swoje** prompty do zadań, które robisz **co tydzień**. One dają największy zwrot z inwestycji.
- Połącz z `gh` CLI: `gh pr list --json number,title,additions,deletions | copilot run "wybierz PR z najwyższym ryzykiem"`
