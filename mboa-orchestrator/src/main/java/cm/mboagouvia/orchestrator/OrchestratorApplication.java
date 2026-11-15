package cm.mboagouvia.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Orchestrateur de Mboa Gouvia : API REST de l'interface, comptes et sécurité,
 * dialogue avec le LLM (Spring AI) et transmission des appels d'outils au serveur MCP.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class OrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }
}
