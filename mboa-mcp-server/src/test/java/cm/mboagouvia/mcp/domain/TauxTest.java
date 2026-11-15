package cm.mboagouvia.mcp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TauxTest {

    private final SourceOfficielle source =
            new SourceOfficielle("Barème test", null, "CNPS", LocalDate.of(2026, 1, 1));

    @Test
    void un_taux_sans_date_de_fin_est_en_vigueur_apres_sa_date_d_effet() {
        Taux taux = new Taux(TypeTaux.CNPS_PENSION_SALARIE, new BigDecimal("4.2"), null,
                LocalDate.of(2026, 1, 1), null, source);

        assertThat(taux.estEnVigueur(LocalDate.of(2026, 10, 1))).isTrue();
        assertThat(taux.estEnVigueur(LocalDate.of(2025, 12, 31))).isFalse();
    }

    @Test
    void un_taux_ferme_ne_s_applique_plus_apres_sa_date_de_fin() {
        Taux taux = new Taux(TypeTaux.TVA, new BigDecimal("19.25"), null,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), source);

        assertThat(taux.estEnVigueur(LocalDate.of(2025, 12, 31))).isTrue();
        assertThat(taux.estEnVigueur(LocalDate.of(2026, 1, 1))).isFalse();
    }

    @Test
    void l_assiette_est_limitee_au_plafond() {
        Taux taux = new Taux(TypeTaux.CNPS_PENSION_EMPLOYEUR, new BigDecimal("4.2"), new BigDecimal("750000"),
                LocalDate.of(2026, 1, 1), null, source);

        assertThat(taux.assiette(new BigDecimal("900000"))).isEqualByComparingTo("750000");
        assertThat(taux.assiette(new BigDecimal("300000"))).isEqualByComparingTo("300000");
    }
}
