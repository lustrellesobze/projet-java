package cm.mboagouvia.orchestrator.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Paramètres propres à Mboa Gouvia (préfixe {@code mboa} dans application.yml).
 *
 * @param jwtSecret         clé HMAC d'au moins 32 caractères (variable d'environnement JWT_SECRET)
 * @param jwtDuree          durée de validité du jeton d'accès
 * @param maxTentatives     nombre d'échecs de connexion avant verrouillage
 * @param dureeVerrouillage durée du verrouillage du compte
 * @param quotaVisiteur     questions par jour pour un visiteur
 * @param quotaInscrit      questions par jour pour un usager connecté
 * @param originesAutorisees origines autorisées par CORS (adresse de l'interface web)
 */
@ConfigurationProperties(prefix = "mboa")
public record MboaProperties(
        String jwtSecret,
        Duration jwtDuree,
        int maxTentatives,
        Duration dureeVerrouillage,
        int quotaVisiteur,
        int quotaInscrit,
        List<String> originesAutorisees) {
}
