package cm.mboagouvia.orchestrator.conversation;

import java.time.LocalDateTime;

/** Objets envoyés à l'interface pour la liste des conversations et leur contenu. */
public final class ConversationDtos {

    private ConversationDtos() {
    }

    public record ResumeConversation(Long id, String titre, LocalDateTime dateMaj) {

        static ResumeConversation depuis(Conversation c) {
            return new ResumeConversation(c.getId(), c.getTitre(), c.getDateMaj());
        }
    }

    public record MessageAffiche(String role, String contenu, LocalDateTime horodatage) {

        static MessageAffiche depuis(Message m) {
            return new MessageAffiche(m.getRole().name(), m.getContenu(), m.getHorodatage());
        }
    }
}
