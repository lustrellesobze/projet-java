package cm.mboagouvia.orchestrator.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import cm.mboagouvia.orchestrator.auth.AuthDtos.DemandeConnexion;
import cm.mboagouvia.orchestrator.auth.AuthDtos.ReponseConnexion;
import cm.mboagouvia.orchestrator.compte.Citoyen;
import cm.mboagouvia.orchestrator.compte.Langue;
import cm.mboagouvia.orchestrator.compte.UtilisateurRepository;
import cm.mboagouvia.orchestrator.config.MboaProperties;
import cm.mboagouvia.orchestrator.erreur.AuthException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtilisateurRepository utilisateurs;

    @Mock
    private PasswordEncoder encodeur;

    @Mock
    private JetonService jetons;

    private AuthService service;

    private final Citoyen awa = new Citoyen("awa@exemple.cm", "hache", "Awa", Langue.FR);

    @BeforeEach
    void preparer() {
        MboaProperties proprietes = new MboaProperties("x".repeat(32), Duration.ofHours(1), 5,
                Duration.ofMinutes(15), 10, 100, List.of("http://localhost:5173"));
        Clock horloge = Clock.fixed(Instant.parse("2026-11-03T09:00:00Z"), ZoneId.of("Africa/Douala"));
        service = new AuthService(utilisateurs, encodeur, jetons, proprietes, horloge);
    }

    @Test
    void compte_inconnu_donne_un_message_neutre() {
        when(utilisateurs.findByEmailIgnoreCase("inconnu@exemple.cm")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.connecter(new DemandeConnexion("inconnu@exemple.cm", "secret123")))
                .isInstanceOf(AuthException.class)
                .hasMessage("Identifiants invalides.");
    }

    @Test
    void bon_mot_de_passe_renvoie_un_jeton() {
        when(utilisateurs.findByEmailIgnoreCase("awa@exemple.cm")).thenReturn(Optional.of(awa));
        when(encodeur.matches("secret123", "hache")).thenReturn(true);
        when(jetons.emettre(awa)).thenReturn("jeton-jwt");

        ReponseConnexion reponse = service.connecter(new DemandeConnexion("awa@exemple.cm", "secret123"));

        assertThat(reponse.jeton()).isEqualTo("jeton-jwt");
        assertThat(reponse.role()).isEqualTo("CITOYEN");
    }

    @Test
    void cinq_mauvais_mots_de_passe_verrouillent_le_compte() {
        when(utilisateurs.findByEmailIgnoreCase("awa@exemple.cm")).thenReturn(Optional.of(awa));
        when(encodeur.matches(any(), any())).thenReturn(false);
        DemandeConnexion mauvaise = new DemandeConnexion("awa@exemple.cm", "faux");

        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> service.connecter(mauvaise)).isInstanceOf(AuthException.class);
        }

        assertThatThrownBy(() -> service.connecter(mauvaise))
                .isInstanceOfSatisfying(AuthException.class,
                        ex -> assertThat(ex.getStatut()).isEqualTo(HttpStatus.LOCKED));
        verify(utilisateurs, org.mockito.Mockito.times(5)).save(awa);
    }
}
