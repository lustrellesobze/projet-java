package cm.mboagouvia.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cm.mboagouvia.common.cotisation.LigneSalaire;
import cm.mboagouvia.common.cotisation.ResultatCotisations;
import cm.mboagouvia.mcp.service.CotisationService;

/**
 * Test d'intégration : démarre l'application complète sur une vraie base PostgreSQL
 * (fournie par GitHub Actions ou par docker compose en local), applique les migrations
 * Flyway et calcule avec les données de démonstration.
 */
@SpringBootTest
class McpServerApplicationIT {

    @Autowired
    private CotisationService cotisationService;

    @Test
    void calcule_avec_le_referentiel_de_la_base() {
        ResultatCotisations resultat = cotisationService.calculer(YearMonth.of(2026, 10),
                List.of(new LigneSalaire("Employé A", new BigDecimal("200000"))));

        assertThat(resultat.totalSalarie()).isEqualByComparingTo("8400");
        assertThat(resultat.source()).contains("CNPS");
    }
}
