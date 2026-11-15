package cm.mboagouvia.common.erreur;

/**
 * Levée quand le référentiel ne contient pas la donnée officielle en vigueur
 * (par exemple aucun taux CNPS pour la période demandée). L'assistant doit alors
 * le dire honnêtement au lieu d'inventer un chiffre.
 */
public class ReferentielIncompletException extends RuntimeException {

    public ReferentielIncompletException(String message) {
        super(message);
    }
}
