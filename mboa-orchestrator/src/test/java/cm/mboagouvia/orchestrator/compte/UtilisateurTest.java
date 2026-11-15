package cm.mboagouvia.orchestrator.compte;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UtilisateurTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 3, 10, 0);
    private static final Duration QUINZE_MINUTES = Duration.ofMinutes(15);

    @Test
    void le_compte_est_verrouille_au_cinquieme_echec() {
        Citoyen citoyen = new Citoyen("a@b.cm", "hache", "Awa", Langue.FR);

        for (int i = 0; i < 4; i++) {
            citoyen.enregistrerEchecConnexion(5, QUINZE_MINUTES, MAINTENANT);
        }
        assertThat(citoyen.estVerrouille(MAINTENANT)).isFalse();

        citoyen.enregistrerEchecConnexion(5, QUINZE_MINUTES, MAINTENANT);
        assertThat(citoyen.estVerrouille(MAINTENANT.plusMinutes(10))).isTrue();
        assertThat(citoyen.estVerrouille(MAINTENANT.plusMinutes(16))).isFalse();
    }

    @Test
    void une_connexion_reussie_leve_le_verrouillage() {
        Citoyen citoyen = new Citoyen("a@b.cm", "hache", "Awa", Langue.FR);
        for (int i = 0; i < 5; i++) {
            citoyen.enregistrerEchecConnexion(5, QUINZE_MINUTES, MAINTENANT);
        }

        citoyen.reinitialiserEchecs();

        assertThat(citoyen.estVerrouille(MAINTENANT)).isFalse();
        assertThat(citoyen.getTentativesEchouees()).isZero();
    }

    @Test
    void chaque_sous_classe_porte_son_role() {
        assertThat(new Citoyen("a@b.cm", "h", "A", null).role()).isEqualTo("CITOYEN");
        assertThat(new RepresentantEntreprise("b@b.cm", "h", "B", null).role()).isEqualTo("REPRESENTANT");
        assertThat(new Administrateur("c@b.cm", "h", "C", null).role()).isEqualTo("ADMIN");
    }
}
