package cm.mboagouvia.orchestrator.assistant;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import cm.mboagouvia.orchestrator.config.MboaProperties;
import cm.mboagouvia.orchestrator.erreur.QuotaDepasseException;

/**
 * Limite le nombre de questions par jour : protège le coût de l'IA.
 * Version en mémoire, suffisante pour une seule instance ; à remplacer par Redis si on en déploie plusieurs.
 */
@Service
public class QuotaService {

    private final Map<String, AtomicInteger> compteurs = new ConcurrentHashMap<>();
    private final MboaProperties proprietes;
    private final Clock horloge;

    public QuotaService(MboaProperties proprietes, Clock horloge) {
        this.proprietes = proprietes;
        this.horloge = horloge;
    }

    /**
     * @param cle       identifiant de l'usager connecté, ou adresse IP pour un visiteur
     * @param connecte  vrai si l'usager est authentifié
     */
    public void consommer(String cle, boolean connecte) {
        int limite = connecte ? proprietes.quotaInscrit() : proprietes.quotaVisiteur();
        String cleDuJour = LocalDate.now(horloge) + ":" + cle;
        int utilise = compteurs.computeIfAbsent(cleDuJour, k -> new AtomicInteger()).incrementAndGet();
        if (utilise > limite) {
            throw new QuotaDepasseException(limite);
        }
    }
}
