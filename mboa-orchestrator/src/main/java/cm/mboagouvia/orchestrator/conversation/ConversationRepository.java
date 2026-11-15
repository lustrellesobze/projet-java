package cm.mboagouvia.orchestrator.conversation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /** Les conversations d'un usager, la plus récente en premier. */
    List<Conversation> findByUtilisateurIdOrderByDateMajDesc(Long utilisateurId);

    /** Ne renvoie la conversation que si elle appartient bien à cet usager. */
    Optional<Conversation> findByIdAndUtilisateurId(Long id, Long utilisateurId);
}
