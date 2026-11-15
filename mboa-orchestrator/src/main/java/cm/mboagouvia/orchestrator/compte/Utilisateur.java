package cm.mboagouvia.orchestrator.compte;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Classe abstraite : on ne crée jamais un « Utilisateur » tout court, mais un Citoyen,
 * un RepresentantEntreprise ou un Administrateur. Elle porte aussi les informations du compte
 * (pas de classe Compte séparée : la relation était 1-1 et a été fusionnée).
 * Héritage traduit par une seule table avec la colonne « type » (SINGLE_TABLE).
 */
@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe_hache", nullable = false)
    private String motDePasseHache;

    private String nom;

    @Enumerated(EnumType.STRING)
    private Langue langue = Langue.FR;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private boolean actif = true;

    @Enumerated(EnumType.STRING)
    private Theme theme = Theme.CLAIR;

    private int tailleTexte = 100;

    @Column(nullable = false)
    private LocalDateTime dateConsentement;

    private int tentativesEchouees;

    private LocalDateTime verrouilleJusqua;

    protected Utilisateur() {
        // requis par JPA
    }

    protected Utilisateur(String email, String motDePasseHache, String nom, Langue langue) {
        this.email = email;
        this.motDePasseHache = motDePasseHache;
        this.nom = nom;
        this.langue = langue == null ? Langue.FR : langue;
        this.dateConsentement = LocalDateTime.now();
    }

    /** Rôle porté dans le jeton JWT. */
    public abstract String role();

    public boolean estVerrouille(LocalDateTime maintenant) {
        return verrouilleJusqua != null && maintenant.isBefore(verrouilleJusqua);
    }

    /** +1 échec ; au-delà du seuil, le compte est verrouillé pour la durée donnée. */
    public void enregistrerEchecConnexion(int maxTentatives, Duration duree, LocalDateTime maintenant) {
        tentativesEchouees++;
        if (tentativesEchouees >= maxTentatives) {
            verrouilleJusqua = maintenant.plus(duree);
            tentativesEchouees = 0;
        }
    }

    /** Connexion réussie : on remet le compteur à zéro et on lève le verrouillage. */
    public void reinitialiserEchecs() {
        tentativesEchouees = 0;
        verrouilleJusqua = null;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasseHache() {
        return motDePasseHache;
    }

    public String getNom() {
        return nom;
    }

    public Langue getLangue() {
        return langue;
    }

    public Theme getTheme() {
        return theme;
    }

    public int getTailleTexte() {
        return tailleTexte;
    }

    public boolean isActif() {
        return actif;
    }

    public int getTentativesEchouees() {
        return tentativesEchouees;
    }

    public LocalDateTime getVerrouilleJusqua() {
        return verrouilleJusqua;
    }
}
