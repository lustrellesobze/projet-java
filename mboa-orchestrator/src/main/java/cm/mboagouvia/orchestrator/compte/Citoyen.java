package cm.mboagouvia.orchestrator.compte;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CITOYEN")
public class Citoyen extends Utilisateur {

    /** Téléphone du citoyen. */
    private String telephone;

    protected Citoyen() {
        // requis par JPA
    }

    public Citoyen(String email, String motDePasseHache, String nom, Langue langue) {
        super(email, motDePasseHache, nom, langue);
    }

    @Override
    public String role() {
        return "CITOYEN";
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
