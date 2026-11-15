package cm.mboagouvia.mcp.tool;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import cm.mboagouvia.common.cotisation.LigneSalaire;
import cm.mboagouvia.common.cotisation.ResultatCotisations;
import cm.mboagouvia.common.erreur.ReferentielIncompletException;
import cm.mboagouvia.mcp.domain.StatutExecution;
import cm.mboagouvia.mcp.service.CotisationService;
import cm.mboagouvia.mcp.service.JournalExecutionService;

/**
 * Outil MCP de calcul des cotisations CNPS. Le LLM choisit cet outil et fournit les paramètres ;
 * le calcul lui-même est fait ici, en Java, à partir du référentiel officiel.
 * Une RuntimeException est renvoyée au LLM sous forme d'erreur d'outil, qu'il reformule à l'usager.
 */
@Component
public class CotisationsTools {

    static final String NOM = "calculer_cotisations_cnps";

    private final CotisationService cotisationService;
    private final JournalExecutionService journal;

    public CotisationsTools(CotisationService cotisationService, JournalExecutionService journal) {
        this.cotisationService = cotisationService;
        this.journal = journal;
    }

    @McpTool(name = NOM, description = """
            Calcule les cotisations sociales CNPS (Cameroun) d'une entreprise pour un mois donné :
            part employeur et part salarié de chaque salarié, et les totaux.
            À utiliser quand l'usager demande combien il doit payer à la CNPS pour ses employés.
            Ne pas utiliser pour la TVA ni pour l'impôt sur le revenu.""")
    public ResultatCotisations calculerCotisationsCnps(
            @McpToolParam(description = "Mois concerné au format AAAA-MM, par exemple 2026-10", required = true)
            String periode,
            @McpToolParam(description = "Salariés : libellé pseudonymisé (Employé A…) et salaire brut mensuel en FCFA",
                    required = true)
            List<LigneSalaire> salaires) {

        long debut = System.nanoTime();
        Map<String, Object> parametres = Map.of("periode", String.valueOf(periode),
                "nombreSalaries", salaires == null ? 0 : salaires.size());
        try {
            ResultatCotisations resultat = cotisationService.calculer(YearMonth.parse(periode), salaires);
            journal.enregistrer(NOM, parametres, resultat, StatutExecution.SUCCES, dureeMs(debut));
            return resultat;
        }
        catch (DateTimeParseException ex) {
            journal.enregistrer(NOM, parametres, ex.getMessage(), StatutExecution.ERREUR_VALIDATION, dureeMs(debut));
            throw new IllegalArgumentException("Période invalide : utilisez le format AAAA-MM, par exemple 2026-10.");
        }
        catch (IllegalArgumentException ex) {
            journal.enregistrer(NOM, parametres, ex.getMessage(), StatutExecution.ERREUR_VALIDATION, dureeMs(debut));
            throw ex;
        }
        catch (ReferentielIncompletException ex) {
            journal.enregistrer(NOM, parametres, ex.getMessage(), StatutExecution.ERREUR_REFERENTIEL, dureeMs(debut));
            throw ex;
        }
    }

    private static long dureeMs(long debutNano) {
        return (System.nanoTime() - debutNano) / 1_000_000;
    }
}
