package cm.mboagouvia.orchestrator.assistant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Collecte les appels d'outils faits pendant UNE réponse.
 * Spring AI exécute les outils sur le même fil d'exécution que la requête :
 * une liste par fil (ThreadLocal) suffit à ne pas mélanger deux usagers.
 */
@Component
public class TraceOutils {

    private final ThreadLocal<List<OutilExecute>> enCours = new ThreadLocal<>();

    public void demarrer() {
        enCours.set(new ArrayList<>());
    }

    public void enregistrer(OutilExecute outil) {
        List<OutilExecute> liste = enCours.get();
        if (liste != null) {
            liste.add(outil);
        }
    }

    /** Renvoie les outils appelés depuis {@link #demarrer()} et libère la mémoire du fil. */
    public List<OutilExecute> terminer() {
        List<OutilExecute> liste = enCours.get();
        enCours.remove();
        return liste == null ? List.of() : List.copyOf(liste);
    }
}
