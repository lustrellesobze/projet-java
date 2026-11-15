import type { OutilExecute } from '../api/client'
import type { Textes } from '../i18n'
import { premierOutilReussi } from '../utils/outils'
import { IconeCle, IconeCoche, IconePouceBas, IconePouceHaut, IconeTelecharger } from './Icones'

interface Props {
  t: Textes
  texte: string
  /** null pour une réponse relue depuis l'historique (outils non conservés). */
  outils: OutilExecute[] | null
  onVoirOutils: () => void
}

/** Réponse de l'assistant : texte, badge de vérification, outils utilisés, actions. */
export default function CarteReponse({ t, texte, outils, onVoirOutils }: Props) {
  const verifie = outils ? premierOutilReussi(outils) : null

  return (
    <article className="max-w-3xl self-start rounded-2xl border border-bord bg-surface p-5 shadow-sm">
      <p className="whitespace-pre-wrap leading-relaxed">{texte}</p>

      {outils !== null && (
        <div className="mt-4 flex flex-wrap gap-2 text-sm">
          {verifie ? (
            <span className="inline-flex items-center gap-1.5 rounded-md bg-vert-clair px-2.5 py-1 font-medium text-vert">
              <IconeCoche /> {t.verifiePar(verifie)}
            </span>
          ) : (
            <span className="inline-flex items-center rounded-md bg-or-clair px-2.5 py-1 font-medium text-or-texte">
              {t.nonVerifie}
            </span>
          )}
        </div>
      )}

      {outils !== null && outils.length > 0 && (
        <button
          onClick={onVoirOutils}
          className="mt-3 inline-flex items-center gap-2 text-sm font-medium text-vert hover:underline"
        >
          <IconeCle /> {t.outilsUtilises(outils.length)}
        </button>
      )}

      {outils !== null && (
        <div className="mt-4 flex flex-wrap items-center justify-between gap-3 border-t border-bord pt-4">
          <button
            disabled
            title={t.bientot}
            className="inline-flex cursor-not-allowed items-center gap-2 rounded-lg bg-vert px-4 py-2 text-sm font-semibold text-white opacity-60"
          >
            <IconeTelecharger /> {t.exporterPdf}
          </button>
          <div className="flex items-center gap-2 text-sm text-texte-doux">
            {t.aide}
            <button disabled title={t.bientot} className="cursor-not-allowed rounded-lg border border-bord p-2 opacity-60">
              <IconePouceHaut />
            </button>
            <button disabled title={t.bientot} className="cursor-not-allowed rounded-lg border border-bord p-2 opacity-60">
              <IconePouceBas />
            </button>
          </div>
        </div>
      )}
    </article>
  )
}
