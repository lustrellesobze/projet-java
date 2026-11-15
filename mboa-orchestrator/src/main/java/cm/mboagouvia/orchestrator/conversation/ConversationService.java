package cm.mboagouvia.orchestrator.conversation;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.mboagouvia.orchestrator.compte.Langue;
import cm.mboagouvia.orchestrator.compte.UtilisateurRepository;
import cm.mboagouvia.orchestrator.conversation.ConversationDtos.MessageAffiche;
import cm.mboagouvia.orchestrator.conversation.ConversationDtos.ResumeConversation;
import cm.mboagouvia.orchestrator.erreur.ConversationIntrouvableException;

/**
 * Historique des conversations (CU-11) : créer, lister, relire, supprimer.
 * Chaque méthode vérifie que la conversation appartient bien à l'usager connecté.
 */
@Service
public class ConversationService {

    static final int LONGUEUR_TITRE = 60;

    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final UtilisateurRepository utilisateurs;
    private final Clock horloge;

    public ConversationService(ConversationRepository conversations, MessageRepository messages,
            UtilisateurRepository utilisateurs, Clock horloge) {
        this.conversations = conversations;
        this.messages = messages;
        this.utilisateurs = utilisateurs;
        this.horloge = horloge;
    }

    @Transactional(readOnly = true)
    public List<ResumeConversation> lister(Long utilisateurId) {
        return conversations.findByUtilisateurIdOrderByDateMajDesc(utilisateurId).stream()
                .map(ResumeConversation::depuis)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageAffiche> messages(Long utilisateurId, Long conversationId) {
        Conversation conversation = trouver(utilisateurId, conversationId);
        return messages.findByConversationIdOrderByHorodatageAscIdAsc(conversation.getId()).stream()
                .map(MessageAffiche::depuis)
                .toList();
    }

    /**
     * Sans identifiant : c'est une nouvelle conversation, titrée avec le début de la question.
     * Avec identifiant : on reprend la conversation existante de cet usager.
     */
    @Transactional
    public Conversation obtenirOuCreer(Long utilisateurId, Long conversationId, String question) {
        if (conversationId != null) {
            return trouver(utilisateurId, conversationId);
        }
        Conversation nouvelle = new Conversation(utilisateurs.getReferenceById(utilisateurId), titreDepuis(question),
                LocalDateTime.now(horloge));
        return conversations.save(nouvelle);
    }

    /** Les derniers messages, du plus ancien au plus récent, pour que l'IA garde le fil. */
    @Transactional(readOnly = true)
    public List<Message> derniersMessages(Long conversationId) {
        List<Message> recents = new ArrayList<>(messages.findTop10ByConversationIdOrderByHorodatageDescIdDesc(conversationId));
        Collections.reverse(recents);
        return recents;
    }

    @Transactional
    public void enregistrerEchange(Conversation conversation, String question, String reponse, Langue langue) {
        LocalDateTime maintenant = LocalDateTime.now(horloge);
        messages.save(new Message(conversation, RoleMessage.USER, question, langue, maintenant));
        messages.save(new Message(conversation, RoleMessage.ASSISTANT, reponse, langue, maintenant));
        conversation.toucher(maintenant);
        conversations.save(conversation);
    }

    @Transactional
    public void supprimer(Long utilisateurId, Long conversationId) {
        conversations.delete(trouver(utilisateurId, conversationId));
    }

    /** Titre lisible dans la liste : le début de la première question. */
    static String titreDepuis(String question) {
        String nettoye = question == null ? "" : question.strip().replaceAll("\\s+", " ");
        if (nettoye.isEmpty()) {
            return "Nouvelle conversation";
        }
        return nettoye.length() <= LONGUEUR_TITRE ? nettoye : nettoye.substring(0, LONGUEUR_TITRE - 1) + "…";
    }

    private Conversation trouver(Long utilisateurId, Long conversationId) {
        return conversations.findByIdAndUtilisateurId(conversationId, utilisateurId)
                .orElseThrow(() -> new ConversationIntrouvableException(conversationId));
    }
}
