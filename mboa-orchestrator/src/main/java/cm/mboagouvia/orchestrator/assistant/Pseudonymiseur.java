package cm.mboagouvia.orchestrator.assistant;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Retire les données personnelles directes avant tout envoi au fournisseur d'IA
 * (loi n° 2024/017) : adresses e-mail et numéros de téléphone camerounais.
 * Les noms des employés sont remplacés en amont par « Employé A, B… » lors de l'appel d'outil.
 */
@Component
public class Pseudonymiseur {

    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    private static final Pattern TELEPHONE = Pattern.compile("(\\+?237[\\s.-]?)?6\\d{2}([\\s.-]?\\d{2}){3}");

    public String pseudonymiser(String texte) {
        if (texte == null) {
            return "";
        }
        String resultat = EMAIL.matcher(texte).replaceAll("[e-mail masqué]");
        return TELEPHONE.matcher(resultat).replaceAll("[téléphone masqué]");
    }
}
