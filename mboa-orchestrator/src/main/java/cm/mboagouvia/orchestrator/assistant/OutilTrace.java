package cm.mboagouvia.orchestrator.assistant;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * Enveloppe un outil MCP : l'appel est transmis tel quel, mais son nom, ses paramètres,
 * sa durée et son issue sont notés pour être montrés à l'usager.
 */
public class OutilTrace implements ToolCallback {

    private final ToolCallback outil;
    private final TraceOutils trace;

    public OutilTrace(ToolCallback outil, TraceOutils trace) {
        this.outil = outil;
        this.trace = trace;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return outil.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return outil.getToolMetadata();
    }

    @Override
    public String call(String parametres) {
        return mesurer(parametres, () -> outil.call(parametres));
    }

    @Override
    public String call(String parametres, ToolContext contexte) {
        return mesurer(parametres, () -> outil.call(parametres, contexte));
    }

    private String mesurer(String parametres, java.util.function.Supplier<String> appel) {
        long debut = System.nanoTime();
        boolean succes = false;
        try {
            String resultat = appel.get();
            succes = true;
            return resultat;
        }
        finally {
            long dureeMs = (System.nanoTime() - debut) / 1_000_000;
            trace.enregistrer(new OutilExecute(getToolDefinition().name(), parametres, dureeMs, succes));
        }
    }
}
