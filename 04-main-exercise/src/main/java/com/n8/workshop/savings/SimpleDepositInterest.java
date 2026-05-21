package com.n8.workshop.savings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Kalkulator prostych odsetek dla pojedynczego depozytu krótkoterminowego.
 *
 * <p>Wzór: <code>odsetki = kapitał × stopa_roczna × (dni / 365)</code>. Inaczej niż
 * {@link com.n8.workshop.CalculateInterest}, ta klasa:</p>
 * <ul>
 *   <li>Liczy odsetki <strong>proste</strong> (nie złożone)</li>
 *   <li>Operuje na dniach, nie latach</li>
 *   <li>Używa {@link BigDecimal} (NIGDY double)</li>
 *   <li>Nie nalicza podatku Belka (poza zakresem tej klasy, to robi warstwa wyższa)</li>
 *   <li>Nie nalicza opłat (poza zakresem)</li>
 * </ul>
 *
 * <p>Celowo trzymana <strong>bardzo prosto</strong> jako drugi przykład w repo do demo
 * {@code #codebase} (Blok 5 warsztatu): Copilot ma znaleźć minimum dwa miejsca,
 * w których liczymy odsetki, i je porównać.</p>
 */
public class SimpleDepositInterest {

    private static final BigDecimal DAYS_IN_YEAR = new BigDecimal("365");
    private static final int RATE_SCALE = 6;
    private static final int AMOUNT_SCALE = 2;

    /**
     * Oblicza odsetki proste dla depozytu.
     *
     * @param principal kapitał (musi być nieujemny)
     * @param annualRate roczna stopa procentowa, np. 0.05 dla 5% (musi być w [0, 1])
     * @param days liczba dni trwania depozytu (musi być nieujemna)
     * @return wartość odsetek, scale = 2, {@link RoundingMode#HALF_UP}
     * @throws NullPointerException gdy principal lub annualRate są null
     * @throws IllegalArgumentException gdy principal, annualRate lub days są poza dozwolonym zakresem
     */
    public BigDecimal interestFor(BigDecimal principal, BigDecimal annualRate, int days) {
        Objects.requireNonNull(principal, "principal cannot be null");
        Objects.requireNonNull(annualRate, "annualRate cannot be null");
        if (principal.signum() < 0) {
            throw new IllegalArgumentException("Kapitał nie może być ujemny: " + principal);
        }
        if (annualRate.signum() < 0 || annualRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Stopa musi być w zakresie [0, 1]: " + annualRate);
        }
        if (days < 0) {
            throw new IllegalArgumentException("Liczba dni nie może być ujemna: " + days);
        }

        var dayFraction = new BigDecimal(days).divide(DAYS_IN_YEAR, RATE_SCALE, RoundingMode.HALF_UP);
        return principal.multiply(annualRate)
                .multiply(dayFraction)
                .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
    }
}
