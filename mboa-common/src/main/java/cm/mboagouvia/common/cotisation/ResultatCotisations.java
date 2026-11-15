package cm.mboagouvia.common.cotisation;

import java.math.BigDecimal;
import java.util.List;

/**
 * Résultat complet renvoyé par l'outil MCP {@code calculer_cotisations_cnps}.
 * La source permet à l'usager de vérifier l'origine des taux appliqués.
 */
public record ResultatCotisations(
        String periode,
        List<LigneCotisation> lignes,
        BigDecimal totalEmployeur,
        BigDecimal totalSalarie,
        String source) {
}
