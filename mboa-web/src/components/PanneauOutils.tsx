import { useEffect, useRef } from 'react'
import type { OutilExecute } from '../api/client'
import type { Textes } from '../i18n'
import { parametresLisibles } from '../utils/outils'
import { IconeCadenas, IconeFermer } from './Icones'

interface Props {
  t: Textes
  /** Tous les outils de la conversation, du plus récent au plus ancien. */
  outils: OutilExecute[]
  /** Outils de la réponse sur laquelle l'usager a cliqué « voir le détail » : encadrés et rendus visibles. */
  selection: OutilExecute[]
  /** Sur petit écran, le panneau est caché et s'ouvre par-dessus la page au clic sur « voir le détail ». */
  ouvertMobile: boolean
  onFermer: () => void
}

/** Panneau de droite « Outils exécutés » : la preuve que les chiffres viennent du code Java. */
export default function PanneauOutils({ t, outils, selection, ouvertMobile, onFermer }: Props) {
  const premiereCarteChoisie = useRef<HTMLDivElement>(null)

  // Au clic sur « voir le détail », on fait défiler le panneau jusqu'aux outils de cette réponse.
  useEffect(() => {
    if (selection.length > 0) {
      premiereCarteChoisie.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
    }
  }, [selection])

  const indexPremiereChoisie = outils.findIndex((o) => selection.includes(o))

  return (
    <>
      {ouvertMobile && (
        <div className="fixed inset-0 z-20 bg-black/30 lg:hidden" onClick={onFermer} aria-hidden="true" />
      )}
      <aside
        className={`fixed inset-y-0 right-0 z-30 w-[310px] max-w-[90vw] shrink-0 flex-col gap-4 overflow-y-auto border-l border-bord bg-surface p-5 shadow-xl lg:static lg:z-auto lg:flex lg:max-w-none lg:shadow-none ${
          ouvertMobile ? 'flex' : 'hidden'
        }`}
      >
        <div className="flex items-baseline justify-between gap-2">
          <h2 className="text-lg font-semibold">{t.outilsExecutes}</h2>
          <span className="text-xs text-texte-doux">{t.cetteConversation}</span>
          <button onClick={onFermer} className="self-center text-texte-doux lg:hidden" aria-label={t.fermer}>
            <IconeFermer />
          </button>
        </div>
        <p className="text-sm text-texte-doux">{t.explicationOutils}</p>

        {outils.length === 0 ? (
          <p className="rounded-xl border border-dashed border-bord p-4 text-sm text-texte-doux">{t.aucunOutil}</p>
        ) : (
          outils.map((outil, i) => {
            const choisi = selection.includes(outil)
            return (
              <div
                key={i}
                ref={i === indexPremiereChoisie ? premiereCarteChoisie : undefined}
                className={`rounded-xl border p-4 transition-shadow ${
                  choisi ? 'border-vert bg-vert-clair ring-2 ring-vert' : 'border-vert/25 bg-vert-clair/50'
                }`}
              >
                <div className="mb-3 flex items-center justify-between gap-2">
                  <code className="break-all font-mono text-[13px]">{outil.nom}</code>
                  <span
                    className={`shrink-0 rounded-full px-2 py-0.5 text-xs font-semibold text-white ${
                      outil.succes ? 'bg-vert' : 'bg-rouge-texte'
                    }`}
                  >
                    {outil.succes ? t.succes : t.erreur}
                  </span>
                </div>
                <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-1 text-sm">
                  {parametresLisibles(outil.parametres, t.lignes).map(([cle, valeur]) => (
                    <div key={cle} className="contents">
                      <dt className="text-texte-doux">{cle}</dt>
                      <dd className="truncate font-mono text-[13px]">{valeur}</dd>
                    </div>
                  ))}
                  <dt className="text-texte-doux">{t.duree}</dt>
                  <dd className="font-mono text-[13px]">{outil.dureeMs} ms</dd>
                </dl>
              </div>
            )
          })
        )}

        <p className="flex gap-2 rounded-xl border border-dashed border-bord p-4 text-sm text-texte-doux">
          <span className="mt-0.5 shrink-0">
            <IconeCadenas />
          </span>
          {t.confidentialite}
        </p>
      </aside>
    </>
  )
}
