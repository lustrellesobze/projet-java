package cm.mboagouvia.orchestrator.auth;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.mboagouvia.orchestrator.auth.AuthDtos.DemandeConnexion;
import cm.mboagouvia.orchestrator.auth.AuthDtos.DemandeInscription;
import cm.mboagouvia.orchestrator.auth.AuthDtos.ReponseConnexion;
import cm.mboagouvia.orchestrator.auth.AuthDtos.TypeCompte;
import cm.mboagouvia.orchestrator.compte.Citoyen;
import cm.mboagouvia.orchestrator.compte.RepresentantEntreprise;
import cm.mboagouvia.orchestrator.compte.Utilisateur;
import cm.mboagouvia.orchestrator.compte.UtilisateurRepository;
import cm.mboagouvia.orchestrator.config.MboaProperties;
import cm.mboagouvia.orchestrator.erreur.AuthException;

/** Inscription (CU-01) et authentification (CU-02), conformes au diagramme de séquence. */
@Service
public class AuthService {

    private final UtilisateurRepository utilisateurs;
    private final PasswordEncoder encodeur;
    private final JetonService jetons;
    private final MboaProperties proprietes;
    private final Clock horloge;

    public AuthService(UtilisateurRepository utilisateurs, PasswordEncoder encodeur, JetonService jetons,
            MboaProperties proprietes, Clock horloge) {
        this.utilisateurs = utilisateurs;
        this.encodeur = encodeur;
        this.jetons = jetons;
        this.proprietes = proprietes;
        this.horloge = horloge;
    }

    @Transactional
    public ReponseConnexion inscrire(DemandeInscription demande) {
        if (utilisateurs.existsByEmailIgnoreCase(demande.email())) {
            throw new AuthException(HttpStatus.CONFLICT, "Un compte existe déjà avec cette adresse e-mail.");
        }
        String hache = encodeur.encode(demande.motDePasse());
        Utilisateur nouveau = demande.typeCompte() == TypeCompte.ENTREPRISE
                ? new RepresentantEntreprise(demande.email(), hache, demande.nom(), demande.langue())
                : new Citoyen(demande.email(), hache, demande.nom(), demande.langue());
        return reponse(utilisateurs.save(nouveau));
    }

    @Transactional(noRollbackFor = AuthException.class)
    public ReponseConnexion connecter(DemandeConnexion demande) {
        Utilisateur utilisateur = utilisateurs.findByEmailIgnoreCase(demande.email())
                .orElseThrow(AuthException::identifiantsInvalides);

        LocalDateTime maintenant = LocalDateTime.now(horloge);
        if (utilisateur.estVerrouille(maintenant)) {
            throw new AuthException(HttpStatus.LOCKED, "Compte temporairement verrouillé. Réessayez plus tard.");
        }

        if (encodeur.matches(demande.motDePasse(), utilisateur.getMotDePasseHache())) {
            utilisateur.reinitialiserEchecs();
            utilisateurs.save(utilisateur);
            return reponse(utilisateur);
        }

        utilisateur.enregistrerEchecConnexion(proprietes.maxTentatives(), proprietes.dureeVerrouillage(), maintenant);
        utilisateurs.save(utilisateur);
        throw AuthException.identifiantsInvalides();
    }

    private ReponseConnexion reponse(Utilisateur u) {
        return new ReponseConnexion(jetons.emettre(u), u.role(), u.getLangue().name(), u.getTheme().name(),
                u.getTailleTexte());
    }
}
