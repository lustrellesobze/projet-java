package cm.mboagouvia.mcp.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Trace d'un appel d'outil MCP : quel outil, avec quels paramètres, quel résultat,
 * combien de temps et avec quelle issue. C'est ce qui rend l'IA contrôlable.
 */
@Entity
@Table(name = "execution_outil")
public class ExecutionOutil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomOutil;

    @Column(columnDefinition = "text")
    private String parametres;

    @Column(columnDefinition = "text")
    private String resultat;

    @Enumerated(EnumType.STRING)
    private StatutExecution statut;

    private long dureeMs;

    private UUID correlationId;

    private LocalDateTime horodatage;

    protected ExecutionOutil() {
        // requis par JPA
    }

    public ExecutionOutil(String nomOutil, String parametres, String resultat, StatutExecution statut,
            long dureeMs, UUID correlationId) {
        this.nomOutil = nomOutil;
        this.parametres = parametres;
        this.resultat = resultat;
        this.statut = statut;
        this.dureeMs = dureeMs;
        this.correlationId = correlationId;
        this.horodatage = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNomOutil() {
        return nomOutil;
    }

    public String getParametres() {
        return parametres;
    }

    public String getResultat() {
        return resultat;
    }

    public StatutExecution getStatut() {
        return statut;
    }

    public long getDureeMs() {
        return dureeMs;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public LocalDateTime getHorodatage() {
        return horodatage;
    }
}
