package cm.mboagouvia.orchestrator.compte;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrateur extends Utilisateur {

    /** Matricule de l'agent. */
    private String matriculeAgent;

    protected Administrateur() {
        // requis par JPA
    }

    public Administrateur(String email, String motDePasseHache, String nom, Langue langue) {
        super(email, motDePasseHache, nom, langue);
    }

    @Override
    public String role() {
        return "ADMIN";
    }

    public String getMatriculeAgent() {
        return matriculeAgent;
    }

    public void setMatriculeAgent(String matriculeAgent) {
        this.matriculeAgent = matriculeAgent;
    }
}
