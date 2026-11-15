package cm.mboagouvia.orchestrator.compte;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REPRESENTANT")
public class RepresentantEntreprise extends Utilisateur {

    /** Fonction dans l'entreprise (gérant, comptable…). */
    private String fonction;

    protected RepresentantEntreprise() {
        // requis par JPA
    }

    public RepresentantEntreprise(String email, String motDePasseHache, String nom, Langue langue) {
        super(email, motDePasseHache, nom, langue);
    }

    @Override
    public String role() {
        return "REPRESENTANT";
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }
}
