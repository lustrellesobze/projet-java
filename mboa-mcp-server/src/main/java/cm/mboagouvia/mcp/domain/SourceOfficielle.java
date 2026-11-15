package cm.mboagouvia.mcp.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Document officiel d'où provient une information du référentiel (CNPS, DGI, Journal officiel…). */
@Entity
@Table(name = "source_officielle")
public class SourceOfficielle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intitule;

    private String url;

    private String organisme;

    private LocalDate dateConsultation;

    protected SourceOfficielle() {
        // requis par JPA
    }

    public SourceOfficielle(String intitule, String url, String organisme, LocalDate dateConsultation) {
        this.intitule = intitule;
        this.url = url;
        this.organisme = organisme;
        this.dateConsultation = dateConsultation;
    }

    /** Texte affiché à l'usager sous la réponse, par exemple « CNPS : Barème 2026 (consulté le 2026-10-01) ». */
    public String libelleCitation() {
        return organisme + " : " + intitule + " (consulté le " + dateConsultation + ")";
    }

    public Long getId() {
        return id;
    }

    public String getIntitule() {
        return intitule;
    }

    public String getUrl() {
        return url;
    }

    public String getOrganisme() {
        return organisme;
    }

    public LocalDate getDateConsultation() {
        return dateConsultation;
    }
}
