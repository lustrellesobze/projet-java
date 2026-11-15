package cm.mboagouvia.orchestrator.erreur;

public class QuotaDepasseException extends RuntimeException {

    public QuotaDepasseException(int limite) {
        super("Limite de " + limite + " questions par jour atteinte. Réessayez demain ou connectez-vous.");
    }
}
