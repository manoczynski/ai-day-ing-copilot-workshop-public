# HOW_TO_INSTALL - GitHub Copilot CLI

## Wymagania wstępne

- **Node.js 22+** (sprawdź: `node --version`)
- **gh CLI** (oficjalne CLI GitHuba). Sprawdź: `gh --version`
- **Aktywna licencja GitHub Copilot** (Pro / Business / Enterprise)
- **Token z dostępem do Copilota**, przyznawany automatycznie po `gh auth login`

## Krok 1 - Node.js 22+

```bash
# macOS (Homebrew)
brew install node@22

# Linux (nvm)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
nvm install 22
nvm use 22

# Windows (winget)
winget install OpenJS.NodeJS.LTS

# Weryfikacja
node --version    # v22.x.x
npm --version     # 10.x.x lub wyżej
```

## Krok 2 - gh CLI

```bash
# macOS
brew install gh

# Linux (apt)
sudo apt install gh

# Windows
winget install GitHub.cli

# Weryfikacja
gh --version
```

## Krok 3 - Zaloguj się do GitHuba przez gh

```bash
gh auth login
# wybierz: GitHub.com / HTTPS / browser / Authorize
```

Po zakończeniu:

```bash
gh auth status
# powinno pokazać "Logged in to github.com account <ty>"
```

## Krok 4 - Zainstaluj Copilot CLI

> ⚠️ **Sprawdź bieżącą nazwę pakietu** w oficjalnej dokumentacji GitHub. Poniższa komenda jest reprezentatywna, może wymagać korekty.

```bash
npm install -g @githubnext/github-copilot-cli
# albo (jeśli zostało zmienione):
# gh extension install github/gh-copilot
```

## Krok 5 - Pierwsze uruchomienie

```bash
copilot --version
copilot --help
```

Jeśli wszystko działa, zacznij sesję:

```bash
cd /jakiś/projekt
copilot
# albo non-interactive:
copilot run "Wyjaśnij strukturę tego projektu"
```

## Krok 6 - Konfiguracja (opcjonalnie)

Plik `~/.copilot/config.yml`:

```yaml
model: claude-opus-4.5
defaultMode: agent       # agent | ask | plan
verbose: false
```

I plik `~/.copilot/copilot-instructions.md` (analog do `AGENTS.md`, ale globalny dla wszystkich projektów - **tylko dla Copilot CLI**; dla VS Code Chat user-level instructions konfiguruje się przez setting `chat.instructionsFilesLocations`, nie przez ten plik):

```markdown
# Globalne preferencje

- Odpowiadaj zwięźle, max 5 zdań chyba że pytam o szczegóły
- Język odpowiedzi: polski jeśli pytanie po polsku, inaczej angielski
- Code review: format BLOCKER/WARNING/SUGGESTION
- Zawsze pokaż dokładne polecenia bash (nie pseudokod)
```

## Najczęstsze problemy

### "command not found: copilot"

Sprawdź `npm prefix -g`: czy `bin` z prefiksu jest w `$PATH`. Jeśli nie:

```bash
export PATH="$(npm prefix -g)/bin:$PATH"
# dodaj to do ~/.zshrc lub ~/.bashrc
```

### "Not authenticated"

Sprawdź:
```bash
gh auth status
gh auth refresh -s copilot
```

### "Rate limit exceeded"

Copilot CLI używa Twojej licencji Copilot. Jeśli przekroczysz miesięczny limit, dostaniesz throttling. Sprawdź zużycie w ustawieniach GitHub.

### Firmowy rejestr npm blokuje pakiety

Skonfiguruj `~/.npmrc`:

```
registry=https://<internal-npm-mirror>/
@githubnext:registry=https://registry.npmjs.org/
```

Albo zainstaluj przez `gh extension install` (binary dystrybucja).

## Weryfikacja end-to-end

```bash
cd $(mktemp -d)
git init
echo "hello" > test.txt
copilot run "co znajduje się w tym katalogu i jaką wiadomość commita zaproponujesz dla pierwszego commita"
```

Powinno odpowiedzieć sensownie. Jeśli tak, gotowe.
