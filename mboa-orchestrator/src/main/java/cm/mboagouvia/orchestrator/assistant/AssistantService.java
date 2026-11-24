package cm.mboagouvia.orchestrator.assistant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import cm.mboagouvia.orchestrator.assistant.AssistantDtos.MessagePrecedent;
import cm.mboagouvia.orchestrator.compte.Langue;
import cm.mboagouvia.orchestrator.conversation.RoleMessage;

/**
 * Cœur de l'orchestrateur : envoie la question au LLM avec la liste des outils MCP
 * et les derniers messages de la conversation (lus en base pour un usager connecté,
 * envoyés par le navigateur pour un visiteur).
 * Spring AI gère la boucle « le LLM choisit un outil → appel MCP → résultat → réponse rédigée ».
 * Principe directeur : l'IA comprend, le code Java calcule et vérifie.
 */
@Service
public class AssistantService {

    private static final String CONSIGNES = """
            Tu es l'assistant de Mboa Gouvia, plateforme d'information sur les services publics du Cameroun.
            Règles impératives :
            1. Pour tout chiffre (taux, montant, coût, délai, date limite), utilise TOUJOURS un outil.
               N'invente jamais un chiffre et ne calcule jamais toi-même.
            2. Cite la source renvoyée par l'outil à la fin de ta réponse.
            3. Si un outil renvoie une erreur de référentiel incomplet, dis-le honnêtement.
            4. Si aucune information officielle n'est disponible, précise que ta réponse est
               une information générale non vérifiée.
            5. Réponds TOUJOURS dans la langue indiquée par la ligne « Langue de réponse »
               au début du message de l'usager, même si la question ou les messages précédents
               sont écrits dans une autre langue. Sois clair et bref.
            """;

    /** Texte rédigé par l'IA et outils MCP réellement appelés pour l'obtenir. */
    public record ReponseIa(String texte, List<OutilExecute> outils) {
    }

    private final ChatClient chatClient;
    private final Pseudonymiseur pseudonymiseur;
    private final TraceOutils trace;

    public AssistantService(ChatClient.Builder builder, ObjectProvider<ToolCallbackProvider> outilsMcp,
            Pseudonymiseur pseudonymiseur, TraceOutils trace) {
        ToolCallbackProvider[] outilsTraces = outilsMcp.orderedStream()
                .map(fournisseur -> new FournisseurOutilsTrace(fournisseur, trace))
                .toArray(ToolCallbackProvider[]::new);
        this.chatClient = builder
                .defaultSystem(CONSIGNES)
                .defaultTools((Object[]) outilsTraces)
                .build();
        this.pseudonymiseur = pseudonymiseur;
        this.trace = trace;
    }

    /**
     * @param historique messages précédents de la conversation (vide pour une nouvelle
     *                   conversation), du plus ancien au plus récent
     */
    public ReponseIa repondre(String question, Langue langue, List<MessagePrecedent> historique) {
        Langue langueReponse = langue == null ? Langue.FR : langue;
        String consigneLangue = "Langue de réponse : " + (langueReponse == Langue.EN ? "anglais" : "français");
        trace.demarrer();
        try {
            String texte = chatClient.prompt()
                    .messages(versMessagesIa(historique))
                    .user(consigneLangue + "\n\n" + pseudonymiseur.pseudonymiser(question))
                    .call()
                    .content();
            return new ReponseIa(texte, trace.terminer());
        }
        finally {
            trace.terminer();
        }
    }

    /** Convertit l'historique en messages Spring AI, pseudonymisés eux aussi. */
    List<org.springframework.ai.chat.messages.Message> versMessagesIa(List<MessagePrecedent> historique) {
        List<org.springframework.ai.chat.messages.Message> resultat = new ArrayList<>();
        for (MessagePrecedent m : historique) {
            String texte = pseudonymiseur.pseudonymiser(m.contenu());
            resultat.add(m.role() == RoleMessage.USER ? new UserMessage(texte) : new AssistantMessage(texte));
        }
        return resultat;
    }
}
