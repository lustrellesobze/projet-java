package cm.mboagouvia.orchestrator.assistant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cm.mboagouvia.orchestrator.assistant.AssistantDtos.DemandeQuestion;
import cm.mboagouvia.orchestrator.assistant.AssistantDtos.MessagePrecedent;
import cm.mboagouvia.orchestrator.compte.Langue;
import cm.mboagouvia.orchestrator.conversation.RoleMessage;

class AssistantDtosTest {

    @Test
    void sansHistoriqueLaListeEstVide() {
        DemandeQuestion demande = new DemandeQuestion("Bonjour", Langue.FR, null, null);

        assertThat(demande.derniersMessages()).isEmpty();
    }

    @Test
    void garde10DerniersMessagesDansLOrdre() {
        List<MessagePrecedent> historique = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            historique.add(new MessagePrecedent(i % 2 == 1 ? RoleMessage.USER : RoleMessage.ASSISTANT, "message " + i));
        }
        DemandeQuestion demande = new DemandeQuestion("Et pour novembre ?", Langue.FR, null, historique);

        List<MessagePrecedent> derniers = demande.derniersMessages();

        assertThat(derniers).hasSize(AssistantDtos.TAILLE_HISTORIQUE);
        assertThat(derniers.get(0).contenu()).isEqualTo("message 5");
        assertThat(derniers.get(9).contenu()).isEqualTo("message 14");
    }
}
