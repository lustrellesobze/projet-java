package cm.mboagouvia.mcp.tool;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cm.mboagouvia.common.cotisation.LigneSalaire;
import cm.mboagouvia.mcp.domain.StatutExecution;
import cm.mboagouvia.mcp.service.CotisationService;
import cm.mboagouvia.mcp.service.JournalExecutionService;

@ExtendWith(MockitoExtension.class)
class CotisationsToolsTest {

    @Mock
    private CotisationService cotisationService;

    @Mock
    private JournalExecutionService journal;

    @InjectMocks
    private CotisationsTools outils;

    @Test
    void une_periode_mal_formee_est_refusee_et_tracee() {
        List<LigneSalaire> salaires = List.of(new LigneSalaire("Employé A", new BigDecimal("200000")));

        assertThatThrownBy(() -> outils.calculerCotisationsCnps("octobre", salaires))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AAAA-MM");

        verify(journal).enregistrer(eq(CotisationsTools.NOM), any(), any(),
                eq(StatutExecution.ERREUR_VALIDATION), anyLong());
    }
}
