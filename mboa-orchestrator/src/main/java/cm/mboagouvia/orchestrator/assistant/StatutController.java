package cm.mboagouvia.orchestrator.assistant;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * État affiché dans l'en-tête (« Serveur MCP connecté ») et dans la barre latérale.
 * Le serveur MCP est considéré connecté s'il répond et expose au moins un outil.
 */
@RestController
public class StatutController {

    public record Statut(boolean serveurMcp, int nombreOutils) {
    }

    private final ObjectProvider<ToolCallbackProvider> outilsMcp;

    public StatutController(ObjectProvider<ToolCallbackProvider> outilsMcp) {
        this.outilsMcp = outilsMcp;
    }

    @GetMapping("/api/statut")
    public Statut statut() {
        try {
            int nombre = outilsMcp.orderedStream().mapToInt(f -> f.getToolCallbacks().length).sum();
            return new Statut(nombre > 0, nombre);
        }
        catch (RuntimeException ex) {
            return new Statut(false, 0);
        }
    }
}
