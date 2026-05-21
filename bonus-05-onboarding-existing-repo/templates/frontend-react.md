# AGENTS.md - <NAZWA_PROJEKTU>

> **Szablon**: React + TypeScript frontend (wewnętrzny dashboard albo SPA). Skopiuj do swojego repo jako `AGENTS.md`, wypełnij placeholdery, dostosuj po audycie.

## Kontekst projektu

`<NAZWA_PROJEKTU>` to `<typ: wewnętrzny dashboard | konsola operacyjna | panel admina | klient mobilny>` dla `<użytkownicy: oncall | analitycy | klienci końcowi>`. Krytyczność: `<dev | staging | prod | core>`. Liczba użytkowników dziennych: `<liczba>`.

## Stack

- **React** `<18>` z **hooks** (NIE class components w nowym kodzie)
- **TypeScript** `<5.x>` w trybie `strict: true`
- **Vite** `<5.x>` jako build tool (NIE Webpack, NIE Create React App)
- **TanStack Query** (React Query) `<5.x>` dla data fetching i cache
- **react-hook-form** + **zod** dla formularzy i walidacji
- **React Router** `<6.x>` dla routingu
- **Tailwind CSS** `<3.x>` dla stylingu (NIE styled-components, NIE CSS modules)
- **Vitest** + **@testing-library/react** dla testów jednostkowych
- **Playwright** dla testów E2E
- **MSW** (Mock Service Worker) dla mocków API w testach
- **ESLint** + **Prettier** dla lint i format
- **pnpm** dla pakietów (NIE npm, NIE yarn)

### Czego NIE używamy

- ❌ **Redux** (TanStack Query + React Context wystarcza)
- ❌ **Axios** (TanStack Query używa `fetch` wewnętrznie)
- ❌ **moment.js** (deprecated, używamy `date-fns` albo `Temporal`)
- ❌ **lodash** (modern JS ma większość tych funkcji natywnie)
- ❌ **JS** (tylko TS, brak `.js` w `src/` poza configami)

## Konwencje globalne

### Język
- **Identyfikatory**: angielski (TS standard)
- **JSDoc dla skomplikowanych funkcji**: polski
- **Teksty UI dla użytkownika**: polski (i18n przez `react-i18next`)
- **Komentarze w kodzie**: polski dla logiki biznesowej, angielski dla technicznych decyzji
- **Commit messages**: angielski, conventional commits

### Typy danych dla finansów
- **`string` kanonicznie** dla kwot pieniężnych (np. `"1234.56"`); NIE `number`, bo IEEE 754 traci precyzję
- Konwersja na ten sam string przy POST do API (`BigDecimal` po stronie backendu parsuje 1:1)
- Formatowanie do wyświetlenia: `Intl.NumberFormat('pl-PL', { style: 'currency', currency: 'PLN' })`
- Operacje arytmetyczne tylko po stronie backendu; jeśli frontend potrzebuje porównań, używaj bibliotek typu `dinero.js` lub konwertuj do groszy przez `bigint`
- Validacja w `zod`: `z.string().regex(/^\d+(\.\d{1,2})?$/)` zamiast `z.number()`

### TypeScript strict
- `strict: true` w `tsconfig.json`
- Brak `any`, brak `as unknown as Foo`, brak `// @ts-ignore` bez komentarza z uzasadnieniem
- Każda funkcja eksportowana ma jawnie zadeklarowany typ return
- `unknown` zamiast `any` dla zewnętrznych danych
- `zod.infer<typeof Schema>` dla typu z runtime schemy

### Komponenty
- **Function components** z hooks
- Każdy komponent w osobnym pliku, nazwa pliku = nazwa komponentu (`AccountCard.tsx`)
- Pliki indeksowe `index.ts` tylko jeśli rzeczywiście re-eksportują (nie pisz `export { X } from './X'`, jeśli można importować bezpośrednio)
- **Props**: TypeScript interface, NIE `type` (chyba że union)
- Default exports tylko dla komponentów Lazy-loaded i routów; reszta - named exports

### Data fetching i state
- **TanStack Query** dla wszystkich requestów do API
- Query keys hierarchiczne (`['accounts', accountId, 'transactions']`)
- `useMutation` dla side-effects, `useQuery` dla read
- `staleTime` i `gcTime` zdefiniowane na poziomie hooków (nie global default)
- Brak globalnego store dla danych z API (TanStack Query JEST tym store'em)
- React Context tylko dla **stałego** stanu (theme, locale, auth user)

### Formularze
- **react-hook-form** dla wszystkich formularzy
- **zod** dla schemy walidacji
- `zodResolver` jako resolver
- Komunikaty błędów po polsku, definiowane w zod schema albo w i18n
- Submit handler `(data: SchemaType) => Promise<void>` (typed input)

### Testy
- Naming: `<NazwaKomponentu>.test.tsx`
- AAA (Arrange-Act-Assert)
- `render` z `@testing-library/react`, `screen` dla queries
- **MSW** dla mock API w testach (nie ręczne `vi.fn()` dla fetch)
- Brak `data-testid` dla elementów z dostępną rolą (lepiej `getByRole`)
- Coverage: `<próg>%` line coverage dla komponentów krytycznych
- E2E w Playwright dla 5-10 critical paths (login, główny flow, edycja)

### Stylowanie
- **Tailwind utility classes** dla 95% przypadków
- **`@apply`** w `.css` plikach tylko dla powtarzających się grup klas (4+ użyć)
- Brak inline styles `style={{...}}` (poza dynamic computed values)
- Dark mode przez Tailwind `dark:` prefix (jeśli wspierany)
- Custom theme w `tailwind.config.ts`, kolory ING brand

### Logowanie i monitoring
- `console.error` dla błędów (capture przez Sentry)
- `console.log` zabronione w produkcji (linter ESLint flaguje)
- Brak PII w logach (zwłaszcza w error.message do Sentry)
- User identyfikator jako UUID, NIE email/PESEL

## Style kodu i naming

- **Komponenty**: `PascalCase` (`AccountCard`, `LoanForm`)
- **Hooks**: `camelCase` z prefixem `use` (`useAccountQuery`, `useDebounce`)
- **Stałe**: `UPPER_SNAKE_CASE` (`MAX_LOAN_AMOUNT`)
- **Funkcje pomocnicze**: `camelCase` (`formatCurrency`, `parseDate`)
- **Typy / interfejsy**: `PascalCase` bez prefixu `I` (NIE `IAccount`, tak `Account`)
- **Generic types**: pojedyncza litera `T`, `U`, lub opisowo dla skomplikowanych
- **Eventy w propsach**: prefiks `on*` (`onSubmit`, `onAccountSelect`)

## Struktura katalogów

```
src/
├── features/                  # by-feature pakietowanie
│   ├── accounts/
│   │   ├── api/              # TanStack Query hooks
│   │   ├── components/       # komponenty UI
│   │   ├── hooks/            # custom hooks
│   │   ├── types.ts          # typy domeny
│   │   └── index.ts          # public API feature
│   └── loans/
│       └── ...
├── shared/                    # współdzielone
│   ├── components/           # design system (Button, Modal)
│   ├── hooks/                # ogólne hooks
│   ├── lib/                  # narzędzia (formatters, validators)
│   └── api/                  # baseline API client
├── pages/                     # top-level strony (route components)
├── App.tsx
└── main.tsx
```

## Czego unikać

- ❌ `any` w TypeScript (poza dosłownie 1-2 udokumentowanymi wyjątkami)
- ❌ Class components (function + hooks)
- ❌ Redux dla nowego state (TanStack Query albo Context)
- ❌ Inline styles (poza dynamic values)
- ❌ Magic strings dla nazw queries TanStack (stałe albo factories)
- ❌ `useEffect` dla data fetching (TanStack Query)
- ❌ Wykonywanie matematyki na kwotach po stronie frontu
- ❌ `localStorage` dla tokenów (sessionStorage + httpOnly cookie lepsze)
- ❌ `dangerouslySetInnerHTML` (XSS ryzyko)
- ❌ Komponenty z 500+ liniami (rozbij na pod-komponenty)

## Zasady dla agentów modyfikujących

1. **Każdy nowy komponent ma test** (`<Komponent>.test.tsx` z minimum render + interakcja)
2. **Każdy nowy formularz**: zod schema + react-hook-form
3. **Każde nowe API call**: TanStack Query hook z query key
4. **TypeScript errors blokują commit** (pre-commit hook)
5. **Commit messages** w formacie conventional commits
6. **PR template**: screenshot UI (jeśli zmiana wizualna), lista breaking changes w API

## Hierarchia plików instrukcji

1. User prompt files (VS Code Settings, `chat.instructionsFilesLocations`)
2. `AGENTS.md` (root) ← ten plik
3. `.github/instructions/typescript.instructions.md` - jeśli istnieje
4. `src/features/<feature>/AGENTS.md` - jeśli feature ma odmienne konwencje (rzadkie)

---

> **TODO przed użyciem szablonu**: usuń tę sekcję po wypełnieniu placeholderów `<...>`.
