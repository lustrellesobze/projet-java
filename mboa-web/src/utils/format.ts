// Formatage des montants en francs CFA, à la camerounaise : 1 250 000 FCFA

export function formatFcfa(montant: number): string {
  const arrondi = Math.round(montant)
  const groupes = Math.abs(arrondi)
    .toString()
    .replace(/\B(?=(\d{3})+(?!\d))/g, ' ')
  return `${arrondi < 0 ? '-' : ''}${groupes} FCFA`
}

export function periodeValide(periode: string): boolean {
  return /^\d{4}-(0[1-9]|1[0-2])$/.test(periode)
}
