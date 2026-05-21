package com.n8.workshop.iban;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

/**
 * Walidator numerów IBAN zgodnie ze standardem ISO 13616.
 *
 * <p>Metoda {@link #isValid(String)} sprawdza, czy numer IBAN ma poprawną sumę kontrolną
 * (mod-97 == 1) i poprawną długość dla danego kraju. Obsługiwane kody krajów: PL (28 znaków),
 * DE (22), FR (27), GB (22).</p>
 *
 * <p>Algorytm walidacji (mod-97):</p>
 * <ol>
 *   <li>Usuń białe znaki, zamień na wielkie litery</li>
 *   <li>Sprawdź, że długość zgadza się z mapą dla danego kodu kraju</li>
 *   <li>Przenieś pierwsze 4 znaki (kod kraju + cyfry kontrolne) na koniec</li>
 *   <li>Zamień litery na cyfry: A=10, B=11, ..., Z=35</li>
 *   <li>Sprawdź, czy powstała wartość {@link BigInteger} {@code mod 97 == 1}</li>
 * </ol>
 *
 * <p>Hands-on mini-ćwiczenie (Blok 3): dodaj metodę {@code formatForDisplay(String iban)},
 * która zwraca IBAN w formacie ze spacjami co 4 znaki (np. "PL61 1090 1014 0000 0712 1981 2874").
 * Metoda <strong>nie waliduje sumy kontrolnej</strong>, tylko formatuje wejście:
 * usuwa istniejące białe znaki, zmienia na wielkie litery, wstawia spacje co 4 znaki.
 * Dla {@code null} rzuca {@link NullPointerException}, dla pustego napisu
 * {@link IllegalArgumentException} z komunikatem po polsku.</p>
 */
public class IbanValidator {

    private static final Map<String, Integer> COUNTRY_LENGTHS = Map.of(
            "PL", 28,
            "DE", 22,
            "FR", 27,
            "GB", 22
    );

    private static final BigInteger NINETY_SEVEN = BigInteger.valueOf(97);

    /**
     * Sprawdza, czy podany numer IBAN jest poprawny (długość + suma kontrolna mod-97).
     *
     * @param iban numer IBAN do walidacji, dopuszczalne spacje i małe litery
     * @return {@code true} jeśli IBAN ma poprawną długość i sumę kontrolną
     * @throws NullPointerException jeśli {@code iban} jest {@code null}
     * @throws IllegalArgumentException jeśli IBAN jest pusty, ma nieznany kod kraju
     *         lub niepoprawną długość dla swojego kraju
     */
    public boolean isValid(String iban) {
        Objects.requireNonNull(iban, "IBAN nie może być null");
        String normalized = iban.replaceAll("\\s+", "").toUpperCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("IBAN nie może być pusty");
        }
        if (normalized.length() < 4) {
            throw new IllegalArgumentException("IBAN za krótki: " + normalized);
        }
        String countryCode = normalized.substring(0, 2);
        Integer expectedLength = COUNTRY_LENGTHS.get(countryCode);
        if (expectedLength == null) {
            throw new IllegalArgumentException("Nieznany kod kraju IBAN: " + countryCode);
        }
        if (normalized.length() != expectedLength) {
            throw new IllegalArgumentException(
                    "Niepoprawna długość IBAN dla kraju " + countryCode
                            + ": " + normalized.length() + ", oczekiwano " + expectedLength);
        }
        String rearranged = normalized.substring(4) + normalized.substring(0, 4);
        StringBuilder digitsOnly = new StringBuilder(rearranged.length() * 2);
        for (int i = 0; i < rearranged.length(); i++) {
            char character = rearranged.charAt(i);
            if (Character.isDigit(character)) {
                digitsOnly.append(character);
            } else if (character >= 'A' && character <= 'Z') {
                digitsOnly.append(character - 'A' + 10);
            } else {
                throw new IllegalArgumentException(
                        "IBAN zawiera niedozwolony znak: '" + character + "' w " + normalized);
            }
        }
        BigInteger numericValue = new BigInteger(digitsOnly.toString());
        return numericValue.mod(NINETY_SEVEN).equals(BigInteger.ONE);
    }

    // TODO: hands-on mini-ćwiczenie Bloku 3
    //  public String formatForDisplay(String iban) { ... }
    //
    //  Zwraca IBAN w formacie ze spacjami co 4 znaki, np. "PL61 1090 1014 0000 0712 1981 2874".
    //  Bez walidacji sumy kontrolnej - tylko formatowanie:
    //  1. usuń istniejące spacje
    //  2. zamień na wielkie litery
    //  3. wstaw spacje co 4 znaki
    //  null → NullPointerException
    //  pusty napis → IllegalArgumentException z komunikatem PL

}
