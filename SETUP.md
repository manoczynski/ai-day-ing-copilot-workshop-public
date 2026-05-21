# Przygotowanie środowiska przed warsztatem

Sprawdź te punkty PRZED 21.05.2026, żeby warsztat zaczął się od pracy, a nie od instalacji.

## Lista kontrolna (5 minut)

- [ ] **Java 17** zainstalowana - `java -version` pokazuje 17.x lub wyższe
- [ ] **Maven 3.8+** zainstalowany - `mvn -version` pokazuje 3.8.x lub wyższe
- [ ] **VS Code** zainstalowany w wersji **co najmniej 1.116** - `code --version`
- [ ] **Rozszerzenie GitHub Copilot** zainstalowane i aktywne
- [ ] **Aktywna licencja Copilot** - w VS Code, ikona Copilot w status bar pokazuje "Ready"
- [ ] **Git** zainstalowany - `git --version`
- [ ] **Repo sklonowane** lokalnie

## Co jeśli czegoś brakuje

### Java 17

```bash
# macOS (Homebrew)
brew install openjdk@17

# Linux (apt)
sudo apt install openjdk-17-jdk

# Windows
winget install Microsoft.OpenJDK.17
```

### Maven

```bash
# macOS
brew install maven

# Linux
sudo apt install maven

# Windows
winget install Apache.Maven
```

### VS Code 1.116+

Aktualizacja: w VS Code → Help → Check for Updates.
Jeśli brak prawa do aktualizacji (laptop bankowy) - skontaktuj się z IT przed wydarzeniem.

### Rozszerzenie GitHub Copilot

W VS Code: View → Extensions → wyszukaj "GitHub Copilot" → Install.
Po instalacji zaloguj się przez prompt w VS Code (otworzy browser).

## Weryfikacja działania (2 minuty)

Po setupie, w VS Code:

1. Otwórz `04-main-exercise/src/main/java/com/n8/workshop/CalculateInterest.java`
2. Otwórz Copilot Chat (Cmd/Ctrl + Shift + I)
3. Napisz: `Wyjaśnij co robi ta klasa`
4. Jeśli dostajesz sensowną odpowiedź - wszystko działa ✅

## Build kontrolny

```bash
cd 04-main-exercise
mvn clean compile
```

Powinno zakończyć się komunikatem `BUILD SUCCESS`. Jeśli nie, porównaj wersję Javy w `pom.xml` z zainstalowaną.

## Pytania / problemy przed warsztatem

Kontakt: Mateusz Styrc (zespół ING) lub Marcin Noczynski (prowadzący).

## Dla zespołu ING (organizatorów)

Przed warsztatem (rano 21.05):

- [ ] 10 laptopów z aktywną licencją Copilot Business
- [ ] Repo sklonowane na każdym laptopie (do `~/workshop/` lub podobnie)
- [ ] VS Code otwarty z folderem repo
- [ ] Internet + dostęp do `api.githubcopilot.com` zweryfikowane
- [ ] Jeden zapasowy laptop (na wypadek awarii)
