package cm.mboagouvia.orchestrator.assistant;

/**
 * Un appel d'outil MCP fait pendant une réponse, affiché dans le panneau « Outils exécutés ».
 *
 * @param nom        nom de l'outil (par exemple calculer_cotisations_cnps)
 * @param parametres paramètres envoyés par l'IA, au format JSON
 * @param dureeMs    durée de l'appel en millisecondes
 * @param succes     faux si l'outil a renvoyé une erreur
 */
public record OutilExecute(String nom, String parametres, long dureeMs, boolean succes) {
}
