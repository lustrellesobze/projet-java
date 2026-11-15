package cm.mboagouvia.orchestrator.assistant;

import java.util.List;

import cm.mboagouvia.orchestrator.compte.Langue;
import cm.mboagouvia.orchestrator.conversation.RoleMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AssistantDtos {

    /** Nombre de messages précédents transmis à l'IA pour qu'elle garde le fil. */
    public static final int TAILLE_HISTORIQUE = 10;

    private AssistantDtos() {
    }

    /** Un message déjà échangé dans la conversation (question de l'usager ou réponse de l'assistant). */
    public record MessagePrecedent(
            @NotNull RoleMessage role,
            @NotBlank @Size(max = 8000) String contenu) {
    }

    /**
     * @param conversationId vide pour démarrer une nouvelle conversation ;
     *                       ignoré pour un visiteur, dont rien n'est enregistré
     * @param historique     messages déjà affichés à l'écran, envoyés seulement par un visiteur :
     *                       comme rien n'est enregistré pour lui, c'est le navigateur qui garde le fil.
     *                       Un usager connecté n'en a pas besoin : son historique est lu en base.
     */
    public record DemandeQuestion(
            @NotBlank @Size(max = 2000) String question,
            Langue langue,
            Long conversationId,
            @Size(max = 50) List<@Valid MessagePrecedent> historique) {

        /** Les derniers messages de l'historique envoyé (au plus 10), du plus ancien au plus récent. */
        public List<MessagePrecedent> derniersMessages() {
            if (historique == null || historique.isEmpty()) {
                return List.of();
            }
            int debut = Math.max(0, historique.size() - TAILLE_HISTORIQUE);
            return List.copyOf(historique.subList(debut, historique.size()));
        }
    }

    /**
     * conversationId et titre sont vides pour un visiteur.
     * outils = les outils MCP appelés pour cette réponse (panneau « Outils exécutés »).
     */
    public record ReponseAssistant(String texte, Long conversationId, String titre, List<OutilExecute> outils) {
    }
}
