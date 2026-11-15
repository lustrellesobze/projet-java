package cm.mboagouvia.mcp.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cm.mboagouvia.mcp.domain.ExecutionOutil;
import cm.mboagouvia.mcp.domain.StatutExecution;
import cm.mboagouvia.mcp.repository.ExecutionOutilRepository;

/** Enregistre chaque appel d'outil MCP, qu'il réussisse ou non (journal des exécutions, CU-21). */
@Service
public class JournalExecutionService {

    private final ExecutionOutilRepository repository;

    public JournalExecutionService(ExecutionOutilRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID enregistrer(String nomOutil, Object parametres, Object resultat, StatutExecution statut,
            long dureeMs) {
        UUID correlationId = UUID.randomUUID();
        repository.save(new ExecutionOutil(nomOutil, String.valueOf(parametres), String.valueOf(resultat), statut,
                dureeMs, correlationId));
        return correlationId;
    }
}
