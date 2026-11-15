package cm.mboagouvia.mcp.domain;

/** Types de taux gérés par le référentiel. */
public enum TypeTaux {

    TVA(false, false),
    CNPS_PENSION_EMPLOYEUR(true, false),
    CNPS_PENSION_SALARIE(true, true),
    CNPS_ALLOCATIONS_FAMILIALES(true, false),
    CNPS_ACCIDENTS_TRAVAIL(true, false);

    private final boolean cnps;
    private final boolean partSalariale;

    TypeTaux(boolean cnps, boolean partSalariale) {
        this.cnps = cnps;
        this.partSalariale = partSalariale;
    }

    public boolean estCnps() {
        return cnps;
    }

    /** Vrai si la cotisation est prélevée sur le salarié, faux si elle est payée par l'employeur. */
    public boolean estPartSalariale() {
        return partSalariale;
    }
}
