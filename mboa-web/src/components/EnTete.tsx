import type { Role, Statut } from '../api/client'
import type { Langue, Textes } from '../i18n'
import { IconeBulle, IconeLune, IconeMenu, IconeSoleil } from './Icones'

interface Props {
  t: Textes
  langue: Langue
  onLangue: (langue: Langue) => void
  sombre: boolean
  onTheme: () => void
  statut: Statut | null | 'chargement'
  role: Role | null
  onConnexion: () => void
  onDeconnexion: () => void
  onMenu: () => void
}

/** En-tête de la maquette : logo, état du serveur MCP, FR / EN, thème, compte. */
export default function EnTete(p: Props) {
  const connecte = p.statut !== null && p.statut !== 'chargement' && p.statut.serveurMcp
  const libelleStatut =
    p.statut === 'chargement' ? p.t.mcpVerification : connecte ? p.t.mcpConnecte : p.t.mcpHorsLigne

  return (
    <header className="flex h-16 shrink-0 items-center justify-between border-b border-bord bg-surface px-4 md:px-6">
      <div className="flex items-center gap-2 md:gap-3">
        <button
          onClick={p.onMenu}
          className="rounded-lg p-2 text-texte-doux hover:bg-fond md:hidden"
          aria-label={p.t.menu}
        >
          <IconeMenu />
        </button>
        <span className="relative grid h-9 w-9 place-items-center rounded-lg bg-vert text-white">
          <IconeBulle taille={18} />
          <span className="absolute right-1 top-1 h-2 w-2 rounded-full bg-or" />
        </span>
        <span className="hidden whitespace-nowrap text-lg font-semibold min-[440px]:inline">Mboa Gouvia</span>
      </div>

      <div className="flex items-center gap-2 md:gap-3">
        <span
          className={`hidden items-center gap-2 rounded-full border px-3 py-1 text-xs font-medium sm:flex ${
            connecte ? 'border-vert/30 bg-vert-clair text-vert' : 'border-bord bg-fond text-texte-doux'
          }`}
        >
          <span className={`h-2 w-2 rounded-full ${connecte ? 'bg-vert' : 'bg-texte-doux'}`} />
          {libelleStatut}
        </span>

        <div className="flex overflow-hidden rounded-lg border border-bord text-sm font-medium">
          {(['FR', 'EN'] as const).map((l) => (
            <button
              key={l}
              onClick={() => p.onLangue(l)}
              className={`px-2.5 py-1.5 sm:px-3 ${p.langue === l ? 'bg-vert text-white' : 'bg-surface text-texte-doux hover:text-texte'}`}
              aria-pressed={p.langue === l}
            >
              {l}
            </button>
          ))}
        </div>

        <button
          onClick={p.onTheme}
          className="rounded-lg border border-bord bg-surface p-2 text-texte-doux hover:text-texte"
          aria-label={p.sombre ? p.t.modeClair : p.t.modeSombre}
          title={p.sombre ? p.t.modeClair : p.t.modeSombre}
        >
          {p.sombre ? <IconeSoleil /> : <IconeLune />}
        </button>

        {p.role ? (
          <div className="flex items-center gap-2">
            <span className="grid h-9 w-9 place-items-center rounded-full bg-vert-clair text-sm font-semibold text-vert">
              {p.role === 'REPRESENTANT' ? 'RE' : p.role === 'ADMIN' ? 'AD' : 'CI'}
            </span>
            <div className="hidden leading-tight lg:block">
              <p className="text-sm font-medium">{p.t.compte[p.role]}</p>
              <button onClick={p.onDeconnexion} className="text-xs text-texte-doux hover:text-vert">
                {p.t.seDeconnecter}
              </button>
            </div>
          </div>
        ) : (
          <button
            onClick={p.onConnexion}
            className="whitespace-nowrap rounded-lg bg-vert px-3 py-2 text-sm font-semibold text-white hover:bg-vert-fonce"
          >
            {p.t.seConnecter}
          </button>
        )}
      </div>
    </header>
  )
}
