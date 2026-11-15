package cm.mboagouvia.mcp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Taux officiel (TVA ou CNPS), historisé : on ne modifie jamais un ancien taux,
 * on le ferme avec une date de fin et on en crée un nouveau.
 * La valeur est exprimée en pourcentage (4.2 signifie 4,2 %).
 */
@Entity
@Table(name = "taux")
public class Taux {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeTaux type;

    private BigDecimal valeur;

    /** Plafond de l'assiette en FCFA ; null si la cotisation n'est pas plafonnée. */
    private BigDecimal plafond;

    private LocalDate dateEffet;

    /** Null tant que le taux est en vigueur. */
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id")
    private SourceOfficielle source;

    protected Taux() {
        // requis par JPA
    }

    public Taux(TypeTaux type, BigDecimal valeur, BigDecimal plafond, LocalDate dateEffet, LocalDate dateFin,
            SourceOfficielle source) {
        this.type = type;
        this.valeur = valeur;
        this.plafond = plafond;
        this.dateEffet = dateEffet;
        this.dateFin = dateFin;
        this.source = source;
    }

    /** Le taux s'applique-t-il à la date donnée ? */
    public boolean estEnVigueur(LocalDate date) {
        boolean commence = !date.isBefore(dateEffet);
        boolean pasTermine = dateFin == null || !date.isAfter(dateFin);
        return commence && pasTermine;
    }

    /** Assiette de calcul : le salaire brut, limité au plafond s'il existe. */
    public BigDecimal assiette(BigDecimal salaireBrut) {
        return plafond == null ? salaireBrut : salaireBrut.min(plafond);
    }

    public Long getId() {
        return id;
    }

    public TypeTaux getType() {
        return type;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public BigDecimal getPlafond() {
        return plafond;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public SourceOfficielle getSource() {
        return source;
    }
}
