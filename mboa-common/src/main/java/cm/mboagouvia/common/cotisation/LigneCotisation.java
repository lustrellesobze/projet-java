package cm.mboagouvia.common.cotisation;

import java.math.BigDecimal;

/** Résultat du calcul CNPS pour un salarié. Montants en FCFA, arrondis à l'unité. */
public record LigneCotisation(
        String libelle,
        BigDecimal salaireBrut,
        BigDecimal partEmployeur,
        BigDecimal partSalarie) {
}
