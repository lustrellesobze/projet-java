package cm.mboagouvia.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Serveur MCP de Mboa Gouvia : expose les outils métier (procédures, CNPS, TVA, échéances…)
 * au protocole MCP. Il ne connaît pas l'IA : il valide, lit le référentiel, calcule et trace.
 */
@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
}
