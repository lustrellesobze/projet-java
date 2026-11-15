package cm.mboagouvia.orchestrator.conversation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.mboagouvia.orchestrator.conversation.ConversationDtos.MessageAffiche;
import cm.mboagouvia.orchestrator.conversation.ConversationDtos.ResumeConversation;

/** Liste, relecture et suppression des conversations de l'usager connecté (CU-11). */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResumeConversation> lister(@AuthenticationPrincipal Jwt jeton) {
        return service.lister(idUsager(jeton));
    }

    @GetMapping("/{id}/messages")
    public List<MessageAffiche> messages(@AuthenticationPrincipal Jwt jeton, @PathVariable Long id) {
        return service.messages(idUsager(jeton), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@AuthenticationPrincipal Jwt jeton, @PathVariable Long id) {
        service.supprimer(idUsager(jeton), id);
    }

    private static Long idUsager(Jwt jeton) {
        return Long.valueOf(jeton.getSubject());
    }
}
