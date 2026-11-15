import { useState, type FormEvent } from 'react'
import type { Textes } from '../i18n'
import { IconeEnvoyer } from './Icones'

/** Champ de question en bas de l'écran, avec le bouton d'envoi rond. */
export default function ZoneSaisie({ t, enCours, onEnvoyer }: { t: Textes; enCours: boolean; onEnvoyer: (q: string) => void }) {
  const [question, setQuestion] = useState('')

  function soumettre(e: FormEvent) {
    e.preventDefault()
    if (!question.trim() || enCours) return
    onEnvoyer(question.trim())
    setQuestion('')
  }

  return (
    <div className="shrink-0 border-t border-bord bg-surface px-4 pb-3 pt-4">
      <form
        onSubmit={soumettre}
        className="mx-auto flex max-w-3xl items-center gap-2 rounded-2xl border-2 border-vert bg-surface py-1.5 pl-4 pr-1.5"
      >
        <input
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder={t.placeholder}
          maxLength={2000}
          className="flex-1 bg-transparent py-2 text-[15px] outline-none placeholder:text-texte-doux"
          aria-label={t.placeholder}
        />
        <button
          type="submit"
          disabled={enCours || !question.trim()}
          className="grid h-11 w-11 place-items-center rounded-xl bg-vert text-white hover:bg-vert-fonce disabled:opacity-50"
          aria-label={t.envoyer}
        >
          <IconeEnvoyer />
        </button>
      </form>
      <p className="mt-2 text-center text-xs text-texte-doux">{t.avertissement}</p>
    </div>
  )
}
