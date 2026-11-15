package cm.mboagouvia.mcp.domain;

/** Issue d'un appel d'outil MCP, affichée dans le journal des exécutions (CU-21). */
public enum StatutExecution {
    SUCCES,
    ERREUR_VALIDATION,
    ERREUR_REFERENTIEL,
    ERREUR_EXTERNE
}
