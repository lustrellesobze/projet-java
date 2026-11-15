package cm.mboagouvia.orchestrator.conversation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cm.mboagouvia.orchestrator.compte.Citoyen;
import cm.mboagouvia.orchestrator.compte.Langue;
import cm.mboagouvia.orchestrator.compte.UtilisateurRepository;
import cm.mboagouvia.orchestrator.erreur.ConversationIntrouvableException;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversations;

    @Mock
    private MessageRepository messages;

    @Mock
    private UtilisateurRepository utilisateurs;

    private ConversationService service;

    private final Citoyen awa = new Citoyen("awa@exemple.cm", "hache", "Awa", Langue.FR);

    @BeforeEach
    void preparer() {
        Clock horloge = Clock.fixed(Instant.parse("2026-11-12T15:00:00Z"), ZoneId.of("Africa/Douala"));
        service = new ConversationService(conversations, messages, utilisateurs, horloge);
    }

    @Test
    void sans_identifiant_une_nouvelle_conversation_est_creee_et_titree() {
        when(utilisateurs.getReferenceById(7L)).thenReturn(awa);
        when(conversations.save(any(Conversation.class))).thenAnswer(appel -> appel.getArgument(0));

        Conversation creee = service.obtenirOuCreer(7L, null, "  Quels papiers pour un passeport ?  ");

        assertThat(creee.getTitre()).isEqualTo("Quels papiers pour un passeport ?");
        assertThat(creee.getUtilisateur()).isSameAs(awa);
    }

    @Test
    void avec_identifiant_la_conversation_existante_est_reprise() {
        Conversation existante = new Conversation(awa, "CNPS octobre", null);
        when(conversations.findByIdAndUtilisateurId(3L, 7L)).thenReturn(Optional.of(existante));

        assertThat(service.obtenirOuCreer(7L, 3L, "Et pour novembre ?")).isSameAs(existante);
        verify(conversations, never()).save(any());
    }

    @Test
    void la_conversation_d_un_autre_usager_est_introuvable() {
        when(conversations.findByIdAndUtilisateurId(3L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.messages(99L, 3L))
                .isInstanceOf(ConversationIntrouvableException.class);
    }

    @Test
    void l_historique_est_remis_dans_l_ordre_chronologique() {
        Conversation c = new Conversation(awa, "Test", null);
        Message ancien = new Message(c, RoleMessage.USER, "premier", Langue.FR, null);
        Message recent = new Message(c, RoleMessage.ASSISTANT, "second", Langue.FR, null);
        when(messages.findTop10ByConversationIdOrderByHorodatageDescIdDesc(5L)).thenReturn(List.of(recent, ancien));

        assertThat(service.derniersMessages(5L)).extracting(Message::getContenu).containsExactly("premier", "second");
    }

    @Test
    void un_titre_trop_long_est_coupe_proprement() {
        String question = "a".repeat(100);

        String titre = ConversationService.titreDepuis(question);

        assertThat(titre).hasSize(ConversationService.LONGUEUR_TITRE).endsWith("…");
    }

    @Test
    void une_question_vide_donne_un_titre_par_defaut() {
        assertThat(ConversationService.titreDepuis("   ")).isEqualTo("Nouvelle conversation");
    }
}
