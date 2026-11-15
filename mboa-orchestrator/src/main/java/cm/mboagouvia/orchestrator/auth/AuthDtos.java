package cm.mboagouvia.orchestrator.auth;

import cm.mboagouvia.orchestrator.compte.Langue;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Objets échangés avec l'interface pour l'inscription et la connexion. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public enum TypeCompte {
        CITOYEN,
        ENTREPRISE
    }

    public record DemandeInscription(
            @NotNull TypeCompte typeCompte,
            @NotBlank String nom,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, message = "8 caractères minimum") String motDePasse,
            Langue langue,
            @AssertTrue(message = "Le consentement est obligatoire (loi n° 2024/017)") boolean consentement) {
    }

    public record DemandeConnexion(
            @NotBlank @Email String email,
            @NotBlank String motDePasse) {
    }

    public record ReponseConnexion(
            String jeton,
            String role,
            String langue,
            String theme,
            int tailleTexte) {
    }
}
