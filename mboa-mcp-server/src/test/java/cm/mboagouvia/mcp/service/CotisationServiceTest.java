package cm.mboagouvia.mcp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import cm.mboagouvia.common.cotisation.LigneSalaire;
import cm.mboagouvia.common.cotisation.ResultatCotisations;
import cm.mboagouvia.common.erreur.ReferentielIncompletException;
import cm.mboagouvia.mcp.domain.SourceOfficielle;
import cm.mboagouvia.mcp.domain.Taux;
import cm.mboagouvia.mcp.domain.TypeTaux;
import cm.mboagouvia.mcp.repository.TauxRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CotisationServiceTest {

    private static final LocalDate OCTOBRE = LocalDate.of(2026, 10, 1);

    @Mock
    private TauxRepository tauxRepository;

    private CotisationService service;

    private final SourceOfficielle source =
            new SourceOfficielle("Barème test", null, "CNPS", LocalDate.of(2026, 1, 1));

    @BeforeEach
    void preparer() {
        service = new CotisationService(tauxRepository);
        BigDecimal plafond = new BigDecimal("750000");
        donner(TypeTaux.CNPS_PENSION_EMPLOYEUR, "4.2", plafond);
        donner(TypeTaux.CNPS_PENSION_SALARIE, "4.2", plafond);
        donner(TypeTaux.CNPS_ALLOCATIONS_FAMILIALES, "7.0", plafond);
        donner(TypeTaux.CNPS_ACCIDENTS_TRAVAIL, "1.75", plafond);
    }

    @Test
    void calcule_les_parts_employeur_et_salarie() {
        ResultatCotisations resultat = service.calculer(YearMonth.of(2026, 10),
                List.of(new LigneSalaire("Employé A", new BigDecimal("200000"))));

        // employeur : 200 000 × (4,2 + 7 + 1,75) % = 8 400 + 14 000 + 3 500 = 25 900
        assertThat(resultat.totalEmployeur()).isEqualByComparingTo("25900");
        // salarié : 200 000 × 4,2 % = 8 400
        assertThat(resultat.totalSalarie()).isEqualByComparingTo("8400");
        assertThat(resultat.periode()).isEqualTo("2026-10");
        assertThat(resultat.source()).contains("CNPS");
    }

    @Test
    void applique_le_plafond_aux_hauts_salaires() {
        ResultatCotisations resultat = service.calculer(YearMonth.of(2026, 10),
                List.of(new LigneSalaire("Employé B", new BigDecimal("1000000"))));

        // salarié : plafond 750 000 × 4,2 % = 31 500
        assertThat(resultat.totalSalarie()).isEqualByComparingTo("31500");
    }

    @Test
    void additionne_plusieurs_salaries() {
        ResultatCotisations resultat = service.calculer(YearMonth.of(2026, 10), List.of(
                new LigneSalaire("Employé A", new BigDecimal("200000")),
                new LigneSalaire("Employé B", new BigDecimal("100000"))));

        assertThat(resultat.lignes()).hasSize(2);
        assertThat(resultat.totalSalarie()).isEqualByComparingTo("12600");
    }

    @Test
    void signale_un_referentiel_incomplet_au_lieu_d_inventer_un_taux() {
        when(tauxRepository.trouverEnVigueur(eq(TypeTaux.CNPS_ACCIDENTS_TRAVAIL), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calculer(YearMonth.of(2026, 10),
                List.of(new LigneSalaire("Employé A", new BigDecimal("200000")))))
                .isInstanceOf(ReferentielIncompletException.class)
                .hasMessageContaining("CNPS_ACCIDENTS_TRAVAIL");
    }

    @Test
    void refuse_une_liste_vide() {
        assertThatThrownBy(() -> service.calculer(YearMonth.of(2026, 10), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void donner(TypeTaux type, String valeur, BigDecimal plafond) {
        Taux taux = new Taux(type, new BigDecimal(valeur), plafond, LocalDate.of(2026, 1, 1), null, source);
        when(tauxRepository.trouverEnVigueur(eq(type), eq(OCTOBRE))).thenReturn(Optional.of(taux));
    }
}
