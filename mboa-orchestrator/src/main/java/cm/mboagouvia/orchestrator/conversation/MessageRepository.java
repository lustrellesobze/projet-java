package cm.mboagouvia.orchestrator.conversation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByHorodatageAscIdAsc(Long conversationId);

    /** Les 10 derniers messages, du plus récent au plus ancien (contexte envoyé à l'IA). */
    List<Message> findTop10ByConversationIdOrderByHorodatageDescIdDesc(Long conversationId);
}
