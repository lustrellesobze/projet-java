package cm.mboagouvia.orchestrator.assistant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PseudonymiseurTest {

    private final Pseudonymiseur pseudonymiseur = new Pseudonymiseur();

    @Test
    void masque_les_emails_et_les_telephones() {
        String texte = "Écrivez à jean.ndongo@exemple.cm ou appelez le +237 6 99 12 34 56";

        String resultat = pseudonymiseur.pseudonymiser(texte);

        assertThat(resultat).doesNotContain("jean.ndongo@exemple.cm").contains("[e-mail masqué]");
    }

    @Test
    void masque_un_numero_mobile_camerounais() {
        assertThat(pseudonymiseur.pseudonymiser("Mon numéro : 699123456"))
                .isEqualTo("Mon numéro : [téléphone masqué]");
    }

    @Test
    void laisse_intact_un_texte_sans_donnee_personnelle() {
        assertThat(pseudonymiseur.pseudonymiser("Quels papiers pour un passeport ?"))
                .isEqualTo("Quels papiers pour un passeport ?");
    }
}
