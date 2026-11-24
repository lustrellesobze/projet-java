// Transforme les paramètres JSON d'un appel d'outil en lignes lisibles pour le panneau.

/**
 * @param lignes texte affiché pour une liste, dans la langue de l'interface (ex. « 3 lignes », « 3 rows »)
 */
export function parametresLisibles(json: string, lignes: (n: number) => string): Array<[string, string]> {
  try {
    const objet = JSON.parse(json)
    if (objet === null || typeof objet !== 'object' || Array.isArray(objet)) return []
    return Object.entries(objet).map(([cle, valeur]) => [
      cle,
      Array.isArray(valeur) ? lignes(valeur.length) : String(valeur),
    ])
  } catch {
    return []
  }
}

/** Le premier outil réussi d'une réponse, pour le badge « Vérifié par l'outil … ». */
export function premierOutilReussi<T extends { nom: string; succes: boolean }>(outils: T[]): string | null {
  return outils.find((o) => o.succes)?.nom ?? null
}
