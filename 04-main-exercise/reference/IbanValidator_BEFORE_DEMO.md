# IbanValidator - wersja "przed demo Bloku 3"

**Tylko dla prowadzącego.** Plik referencyjny do skopiowania na `src/main/java/com/n8/workshop/iban/IbanValidator.java` **tuż przed** demo Bloku 3 (ok. 28:00, podczas omówienia Bloku 2), żeby demo wygenerowania `isValid()` w Agent Mode miał punkt startu.

W publicznym repo `IbanValidator.java` ma już **zaimplementowaną metodę `isValid()`** (mod-97). Uczestnicy widzą gotową metodę od początku - mogą jej używać w mini-ćwiczeniu Bloku 3, gdzie dodają drugą metodę `formatForDisplay()`.

Po demo nie musisz wracać do pustej wersji - uczestnicy nie kompilują `IbanValidator.java` u siebie (mini-ćwiczenie używa tylko Copilota do generowania kodu, kompilacja jest opcjonalna).

## Setup przed demo (1 minuta)

W terminalu, w katalogu `04-main-exercise/`:

```bash
# Backup aktualnej wersji (gotowy isValid)
cp src/main/java/com/n8/workshop/iban/IbanValidator.java /tmp/IbanValidator_FULL.java

# Skopiuj treść poniższego bloku do IbanValidator.java
# (lub: otwórz oba pliki w VS Code i przekopiuj sekcję klasy)
```

Po demo, jeśli chcesz wrócić do gotowej wersji:

```bash
cp /tmp/IbanValidator_FULL.java src/main/java/com/n8/workshop/iban/IbanValidator.java
# albo: git checkout src/main/java/com/n8/workshop/iban/IbanValidator.java
```

## Treść do skopiowania na czas demo

```java
package com.n8.workshop.iban;

/**
 * Walidator numerów IBAN.
 *
 * <p>Pusty szkielet, do zaimplementowania w Bloku 3 warsztatu: najpierw demo
 * Agent Mode (suma kontrolna mod-97), potem mini-ćwiczenie Plan Mode
 * (metoda {@code formatForDisplay}).</p>
 *
 * <p>Wymagania (do zaimplementowania z Copilotem):</p>
 * <ul>
 *   <li>Algorytm walidacji: ISO 13616, suma kontrolna mod-97 (musi dawać resztę 1 z dzielenia przez 97)</li>
 *   <li>Obsługa kodów krajów: minimum PL (28 znaków), opcjonalnie DE (22), FR (27), GB (22)</li>
 *   <li>Walidacja długości IBAN dla każdego kraju</li>
 *   <li>Tolerancja na spacje i wielkość liter w danych wejściowych</li>
 *   <li>Wyjątki:
 *     <ul>
 *       <li>{@code null} → {@link NullPointerException}</li>
 *       <li>pusty / białe znaki → {@link IllegalArgumentException} z komunikatem PL</li>
 *       <li>nieznany kod kraju → {@link IllegalArgumentException} z komunikatem PL</li>
 *       <li>zła długość → {@link IllegalArgumentException} z komunikatem PL</li>
 *       <li>zła suma kontrolna → zwraca {@code false} (nie wyjątek - to "niepoprawny IBAN")</li>
 *     </ul>
 *   </li>
 * </ul>
 */
public class IbanValidator {

    // TODO: do zaimplementowania podczas demo Bloku 3
    //  public boolean isValid(String iban) { ... }
    //
    //  Algorytm mod-97:
    //  1. usuń spacje, zamień na wielkie litery
    //  2. przenieś pierwsze 4 znaki na koniec
    //  3. zamień litery na cyfry: A=10, B=11, ..., Z=35
    //  4. sprawdź, czy powstała wartość BigInteger % 97 == 1

}
```

## Prompt do demo (30:00-33:00, Blok 3)

W `IbanValidator.java` (po skopiowaniu pustej wersji powyżej):

1. Zaznacz całą klasę (`Ctrl + A` w Windows/Linux, `Cmd + A` w macOS)
2. W Copilot Chat (tryb Agent), nowy wątek:

```
#selection zaimplementuj metodę isValid(String iban) zgodnie z JavaDoc tej klasy.
Wymagania w komentarzu TODO. Pamiętaj o:
- BigInteger dla obliczeń (wartość przekracza zakres long)
- Stała mapa COUNTRY_LENGTHS dla PL/DE/FR/GB
- Komunikaty wyjątków po polsku
- Bez dodawania innych metod (formatForDisplay zostawiam na mini-ćwiczenie)
```

**Czego się spodziewasz:** Copilot wygeneruje kompletną implementację `isValid()` z normalizacją wejścia, walidacją długości per kraj, przesunięciem 4 znaków, konwersją liter na cyfry, sprawdzeniem `mod 97 == 1`. Wszystko zgodne z JavaDoc.

**Komentarz na sali:**

> *"Spójrzcie: Copilot przeczytał JavaDoc, TODO w klasie i wygenerował implementację, która spełnia wszystkie wymagania - włącznie z BigInteger zamiast long, bo zauważył, że wartość przekracza zakres. To nie dlatego, że jest mądry - dlatego, że dałem mu kontekst w postaci konkretnych wymagań w komentarzu klasy."*
