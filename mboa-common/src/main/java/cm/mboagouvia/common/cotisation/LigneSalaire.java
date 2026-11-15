package cm.mboagouvia.common.cotisation;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Un salarié et son salaire brut mensuel, tel qu'envoyé à l'outil de calcul CNPS.
 * Le libellé est un pseudonyme (« Employé A ») : les vrais noms ne quittent jamais la plateforme.
 */
public record LigneSalaire(
        @NotBlank String libelle,
        @NotNull @Positive BigDecimal salaireBrut) {
}
