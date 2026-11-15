package cm.mboagouvia.orchestrator.erreur;

import org.springframework.http.HttpStatus;

/** Erreur d'authentification portant le code HTTP à renvoyer (401, 409, 423…). */
public class AuthException extends RuntimeException {

    private final HttpStatus statut;

    public AuthException(HttpStatus statut, String message) {
        super(message);
        this.statut = statut;
    }

    public HttpStatus getStatut() {
        return statut;
    }

    /** Message neutre : on ne révèle jamais si c'est l'e-mail ou le mot de passe qui est faux. */
    public static AuthException identifiantsInvalides() {
        return new AuthException(HttpStatus.UNAUTHORIZED, "Identifiants invalides.");
    }
}
