package cm.mboagouvia.orchestrator.assistant;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.mboagouvia.orchestrator.assistant.AssistantDtos.DemandeQuestion;
import cm.mboagouvia.orchestrator.assistant.AssistantDtos.MessagePrecedent;
import cm.mboagouvia.orchestrator.assistant.AssistantDtos.ReponseAssistant;
import cm.mboagouvia.orchestrator.conversation.Conversation;
import cm.mboagouvia.orchestrator.conversation.ConversationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * CU-04 : poser une question à l'assistant, avec ou sans compte.
 * Usager connecté : la question et la réponse sont enregistrées dans sa conversation (CU-11).
 * Visiteur : rien n'est enregistré ; le navigateur renvoie les messages déjà affichés
 * pour que l'IA garde le fil de la discussion.
 */
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistantService;
    private final QuotaService quotaService;
    private final ConversationService conversationService;

    public AssistantController(AssistantService assistantService, QuotaService quotaService,
            ConversationService conversationService) {
        this.assistantService = assistantService;
        this.quotaService = quotaService;
        this.conversationService = conversationService;
    }

    @PostMapping("/questions")
    public ReponseAssistant poser(@Valid @RequestBody DemandeQuestion demande,
            @AuthenticationPrincipal Jwt jeton, HttpServletRequest requete) {

        if (jeton == null) {
            quotaService.consommer("ip:" + requete.getRemoteAddr(), false);
            AssistantService.ReponseIa reponse = assistantService.repondre(demande.question(), demande.langue(),
                    demande.derniersMessages());
            return new ReponseAssistant(reponse.texte(), null, null, reponse.outils());
        }

        Long utilisateurId = Long.valueOf(jeton.getSubject());
        quotaService.consommer("u:" + utilisateurId, true);
        // Conversation existante : on la retrouve (et on vérifie qu'elle est bien à cet usager)
        Conversation conversation = demande.conversationId() == null ? null
                : conversationService.obtenirOuCreer(utilisateurId, demande.conversationId(), demande.question());
        AssistantService.ReponseIa reponse = assistantService.repondre(demande.question(), demande.langue(),
                conversation == null ? List.of() : historiqueEnregistre(conversation));
        // Nouvelle conversation : créée seulement une fois la réponse obtenue, pour ne pas laisser de conversation vide
        if (conversation == null) {
            conversation = conversationService.obtenirOuCreer(utilisateurId, null, demande.question());
        }
        conversationService.enregistrerEchange(conversation, demande.question(), reponse.texte(), demande.langue());
        return new ReponseAssistant(reponse.texte(), conversation.getId(), conversation.getTitre(), reponse.outils());
    }

    /** Les 10 derniers messages enregistrés de la conversation, au format attendu par l'IA. */
    private List<MessagePrecedent> historiqueEnregistre(Conversation conversation) {
        return conversationService.derniersMessages(conversation.getId()).stream()
                .map(m -> new MessagePrecedent(m.getRole(), m.getContenu()))
                .toList();
    }
}
