package cm.mboagouvia.orchestrator.erreur;

/** La conversation n'existe pas, ou elle appartient à un autre usager (on ne fait pas la différence). */
public class ConversationIntrouvableException extends RuntimeException {

    public ConversationIntrouvableException(Long id) {
        super("Conversation " + id + " introuvable.");
    }
}
