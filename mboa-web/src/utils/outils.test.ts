import { describe, expect, it } from 'vitest'
import { parametresLisibles, premierOutilReussi } from './outils'

describe('parametresLisibles', () => {
  it('affiche chaque paramètre simple', () => {
    expect(parametresLisibles('{"periode":"2026-10","langue":"EN"}')).toEqual([
      ['periode', '2026-10'],
      ['langue', 'EN'],
    ])
  })

  it('résume une liste par son nombre de lignes', () => {
    expect(parametresLisibles('{"salaires":[{},{},{}]}')).toEqual([['salaires', '3 lignes']])
  })

  it('ignore un texte qui n’est pas du JSON', () => {
    expect(parametresLisibles('pas du json')).toEqual([])
  })
})

describe('premierOutilReussi', () => {
  it('ignore les outils en erreur', () => {
    const outils = [
      { nom: 'a', succes: false },
      { nom: 'b', succes: true },
    ]
    expect(premierOutilReussi(outils)).toBe('b')
  })
})
