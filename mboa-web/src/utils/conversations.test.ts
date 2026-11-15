import { describe, expect, it } from 'vitest'
import { historiquePourIa, remonterEnTete, type ElementFil } from './conversations'

const a = { id: 1, titre: 'Passeport', dateMaj: '2026-11-10T10:00:00' }
const b = { id: 2, titre: 'CNPS octobre', dateMaj: '2026-11-11T10:00:00' }

describe('remonterEnTete', () => {
  it('ajoute une nouvelle conversation en premier', () => {
    const c = { id: 3, titre: 'TVA', dateMaj: '2026-11-12T10:00:00' }
    expect(remonterEnTete([b, a], c).map((x) => x.id)).toEqual([3, 2, 1])
  })

  it('fait remonter une conversation existante sans la dupliquer', () => {
    const aMiseAJour = { ...a, dateMaj: '2026-11-12T11:00:00' }
    expect(remonterEnTete([b, a], aMiseAJour).map((x) => x.id)).toEqual([1, 2])
  })
})

describe('historiquePourIa', () => {
  it('garde les questions et les réponses, sans les erreurs', () => {
    const fil: ElementFil[] = [
      { type: 'usager', texte: 'CNPS pour 300 000 FCFA ?' },
      { type: 'erreur', texte: 'Erreur 500' },
      { type: 'assistant', texte: 'Voici le calcul...', outils: [] },
    ]
    expect(historiquePourIa(fil)).toEqual([
      { role: 'USER', contenu: 'CNPS pour 300 000 FCFA ?' },
      { role: 'ASSISTANT', contenu: 'Voici le calcul...' },
    ])
  })

  it('ne garde que les 10 derniers messages', () => {
    const fil: ElementFil[] = Array.from({ length: 14 }, (_, i) => ({ type: 'usager' as const, texte: `q${i + 1}` }))
    const historique = historiquePourIa(fil)
    expect(historique).toHaveLength(10)
    expect(historique[0].contenu).toBe('q5')
    expect(historique[9].contenu).toBe('q14')
  })
})
