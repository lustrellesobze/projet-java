import { describe, expect, it } from 'vitest'
import { formatFcfa, periodeValide } from './format'

describe('formatFcfa', () => {
  it('groupe les milliers par trois chiffres', () => {
    expect(formatFcfa(1250000)).toBe('1 250 000 FCFA')
  })

  it('arrondit à l’unité', () => {
    expect(formatFcfa(8399.6)).toBe('8 400 FCFA')
  })

  it('gère les montants négatifs', () => {
    expect(formatFcfa(-500)).toBe('-500 FCFA')
  })
})

describe('periodeValide', () => {
  it('accepte le format AAAA-MM', () => {
    expect(periodeValide('2026-10')).toBe(true)
  })

  it('refuse un mois impossible', () => {
    expect(periodeValide('2026-13')).toBe(false)
  })
})
