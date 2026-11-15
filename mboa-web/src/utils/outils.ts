// Transforme les paramètres JSON d'un appel d'outil en lignes lisibles pour le panneau.

export function parametresLisibles(json: string): Array<[string, string]> {
  try {
    const objet = JSON.parse(json)
    if (objet === null || typeof objet !== 'object' || Array.isArray(objet)) return []
    return Object.entries(objet).map(([cle, valeur]) => [
      cle,
      Array.isArray(valeur) ? `${valeur.length} ligne${valeur.length > 1 ? 's' : ''}` : String(valeur),
    ])
  } catch {
    return []
  }
}

/** Le premier outil réussi d'une réponse, pour le badge « Vérifié par l'outil … ». */
export function premierOutilReussi<T extends { nom: string; succes: boolean }>(outils: T[]): string | null {
  return outils.find((o) => o.succes)?.nom ?? null
}
