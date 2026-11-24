import { describe, expect, it } from 'vitest'
import { textesPour } from '../i18n'
import { parametresLisibles, premierOutilReussi } from './outils'

const lignesFr = textesPour('FR').lignes
const lignesEn = textesPour('EN').lignes

describe('parametresLisibles', () => {
  it('affiche chaque paramètre simple', () => {
    expect(parametresLisibles('{"periode":"2026-10","langue":"EN"}', lignesFr)).toEqual([
      ['periode', '2026-10'],
      ['langue', 'EN'],
    ])
  })

  it('résume une liste par son nombre de lignes', () => {
    expect(parametresLisibles('{"salaires":[{},{},{}]}', lignesFr)).toEqual([['salaires', '3 lignes']])
    expect(parametresLisibles('{"salaires":[{}]}', lignesFr)).toEqual([['salaires', '1 ligne']])
  })

  it('écrit le nombre de lignes dans la langue de l’interface', () => {
    expect(parametresLisibles('{"salaires":[{},{}]}', lignesEn)).toEqual([['salaires', '2 rows']])
    expect(parametresLisibles('{"salaires":[{}]}', lignesEn)).toEqual([['salaires', '1 row']])
  })

  it('ignore un texte qui n’est pas du JSON', () => {
    expect(parametresLisibles('pas du json', lignesFr)).toEqual([])
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
