import type { ResumeConversation, Statut } from '../api/client'
import type { Textes } from '../i18n'
import { IconeBulle, IconeFermer, IconePlus } from './Icones'

interface Props {
  t: Textes
  ouverte: boolean
  onFermer: () => void
  connecte: boolean
  conversations: ResumeConversation[]
  conversationActive: number | null
  statut: Statut | null | 'chargement'
  onNouvelle: () => void
  onOuvrir: (id: number) => void
  onSupprimer: (id: number) => void
}

/** Barre de gauche : nouvelle conversation, menu, historique, état des services. */
export default function BarreLaterale(p: Props) {
  const enLigne = p.statut !== null && p.statut !== 'chargement'
  const nombreOutils = enLigne && p.statut !== 'chargement' && p.statut !== null ? p.statut.nombreOutils : 0

  return (
    <>
      {p.ouverte && (
        <div className="fixed inset-0 z-20 bg-black/30 md:hidden" onClick={p.onFermer} aria-hidden="true" />
      )}
      <aside
        className={`fixed inset-y-0 left-0 z-30 flex w-[270px] flex-col gap-5 border-r border-bord bg-surface p-4 transition-transform md:static md:z-auto md:translate-x-0 ${
          p.ouverte ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <button onClick={p.onFermer} className="self-end text-texte-doux md:hidden" aria-label={p.t.fermer}>
          <IconeFermer />
        </button>

        <button
          onClick={p.onNouvelle}
          className="flex items-center justify-center gap-2 rounded-xl bg-vert px-4 py-3 font-semibold text-white shadow-sm hover:bg-vert-fonce"
        >
          <IconePlus />
          {p.t.nouvelleConversation}
        </button>

        <nav className="flex flex-col gap-1 text-[15px]">
          <span className="rounded-lg bg-vert-clair px-3 py-2 font-semibold text-vert">{p.t.assistant}</span>
          <span className="cursor-not-allowed rounded-lg px-3 py-2 text-texte-doux" title={p.t.bientot}>
            {p.t.echeancier}
          </span>
          <span className="cursor-not-allowed rounded-lg px-3 py-2 text-texte-doux" title={p.t.bientot}>
            {p.t.monEntreprise}
          </span>
        </nav>

        <div className="flex min-h-0 flex-1 flex-col gap-2">
          {p.connecte && p.conversations.length > 0 && (
            <p className="px-3 text-xs font-semibold uppercase tracking-wider text-texte-doux">{p.t.historique}</p>
          )}
          {!p.connecte ? (
            <div className="mt-6 flex flex-col items-center gap-2 px-4 text-center text-sm text-texte-doux">
              <IconeBulle taille={26} />
              {p.t.connectezVous}
            </div>
          ) : p.conversations.length === 0 ? (
            <div className="mt-6 flex flex-col items-center gap-2 px-4 text-center text-sm text-texte-doux">
              <IconeBulle taille={26} />
              {p.t.aucuneConversation}
            </div>
          ) : (
            <ul className="flex flex-col gap-1 overflow-y-auto">
              {p.conversations.map((c) => (
                <li
                  key={c.id}
                  className={`group flex items-center rounded-lg ${
                    c.id === p.conversationActive ? 'bg-vert-clair' : 'hover:bg-fond'
                  }`}
                >
                  <button
                    onClick={() => p.onOuvrir(c.id)}
                    className={`flex-1 truncate px-3 py-2 text-left text-[15px] ${
                      c.id === p.conversationActive ? 'font-medium text-texte' : 'text-texte-doux'
                    }`}
                    title={c.titre}
                  >
                    {c.titre}
                  </button>
                  <button
                    onClick={() => p.onSupprimer(c.id)}
                    className="px-2 text-texte-doux opacity-0 hover:text-rouge-texte focus:opacity-100 group-hover:opacity-100"
                    aria-label={`${p.t.supprimer} « ${c.titre} »`}
                  >
                    <IconeFermer />
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="flex flex-col gap-2 text-sm">
          <p className="flex items-center gap-2 rounded-lg border border-bord bg-fond px-3 py-2">
            <span className={`h-2 w-2 rounded-full ${enLigne ? 'bg-vert' : 'bg-texte-doux'}`} />
            {enLigne ? p.t.assistantEnLigne : p.t.assistantHorsLigne}
          </p>
          {enLigne && (
            <p className="flex items-center gap-2 rounded-lg border border-bord bg-fond px-3 py-2">
              <span className={`h-2 w-2 rounded-full ${nombreOutils > 0 ? 'bg-vert' : 'bg-texte-doux'}`} />
              {p.t.outilsDisponibles(nombreOutils)}
            </p>
          )}
        </div>
      </aside>
    </>
  )
}
