package cm.mboagouvia.orchestrator.conversation;

import java.time.LocalDateTime;

import cm.mboagouvia.orchestrator.compte.Utilisateur;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Une discussion complète avec l'assistant (diagramme de classes : Utilisateur 1 possède 0..* Conversation).
 * Seuls les usagers connectés ont des conversations enregistrées.
 */
@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMaj;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    protected Conversation() {
        // requis par JPA
    }

    public Conversation(Utilisateur utilisateur, String titre, LocalDateTime maintenant) {
        this.utilisateur = utilisateur;
        this.titre = titre;
        this.dateCreation = maintenant;
        this.dateMaj = maintenant;
    }

    /** Fait remonter la conversation en tête de liste après un nouvel échange. */
    public void toucher(LocalDateTime maintenant) {
        this.dateMaj = maintenant;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateMaj() {
        return dateMaj;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
}
