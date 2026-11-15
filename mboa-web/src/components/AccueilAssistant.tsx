import type { ReactNode } from 'react'
import type { Textes } from '../i18n'
import {
  IconeBatiment,
  IconeCalculatrice,
  IconeCalendrier,
  IconeEmployes,
  IconeGraphique,
  IconeIdentite,
} from './Icones'

const ICONES: ReactNode[] = [
  <IconeIdentite key="id" />,
  <IconeEmployes key="emp" />,
  <IconeCalculatrice key="calc" />,
  <IconeCalendrier key="cal" />,
  <IconeGraphique key="gra" />,
  <IconeBatiment key="bat" />,
]

/** Écran « Premier lancement » : ce que voit l'usager dans une nouvelle conversation. */
export default function AccueilAssistant({ t, onChoisir }: { t: Textes; onChoisir: (question: string) => void }) {
  return (
    <div className="mx-auto flex max-w-3xl flex-col items-center gap-3 px-4 py-12 text-center">
      <h1 className="text-3xl font-bold md:text-4xl">{t.accueilTitre}</h1>
      <p className="text-texte-doux">{t.accueilSousTitre}</p>
      <div className="mt-6 grid w-full gap-3 sm:grid-cols-2">
        {t.suggestions.map((suggestion, i) => (
          <button
            key={suggestion}
            onClick={() => onChoisir(suggestion)}
            className="flex items-center gap-4 rounded-xl border border-bord bg-surface px-4 py-4 text-left shadow-sm transition hover:border-vert/40 hover:shadow"
          >
            <span className="grid h-10 w-10 shrink-0 place-items-center rounded-lg bg-vert-clair text-vert">
              {ICONES.at(i)}
            </span>
            <span className="text-[15px]">{suggestion}</span>
          </button>
        ))}
      </div>
    </div>
  )
}
