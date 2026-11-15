package cm.mboagouvia.orchestrator.assistant;

import java.util.Arrays;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

/**
 * Enveloppe la liste des outils MCP. La liste est demandée au serveur MCP au moment
 * de chaque question (et non au démarrage), puis chaque outil est enveloppé par {@link OutilTrace}.
 */
public class FournisseurOutilsTrace implements ToolCallbackProvider {

    private final ToolCallbackProvider fournisseur;
    private final TraceOutils trace;

    public FournisseurOutilsTrace(ToolCallbackProvider fournisseur, TraceOutils trace) {
        this.fournisseur = fournisseur;
        this.trace = trace;
    }

    @Override
    public ToolCallback[] getToolCallbacks() {
        return Arrays.stream(fournisseur.getToolCallbacks())
                .map(outil -> new OutilTrace(outil, trace))
                .toArray(ToolCallback[]::new);
    }
}
