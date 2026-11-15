// Icônes au trait, dessinées en SVG (aucune bibliothèque à installer).
import type { ReactNode } from 'react'

function Icone({ children, taille = 18 }: { children: ReactNode; taille?: number }) {
  return (
    <svg
      width={taille}
      height={taille}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {children}
    </svg>
  )
}

export const IconePlus = () => (
  <Icone>
    <path d="M12 5v14M5 12h14" />
  </Icone>
)
export const IconeBulle = ({ taille }: { taille?: number }) => (
  <Icone taille={taille}>
    <path d="M4 5h16v11H9l-5 4z" />
  </Icone>
)
export const IconeEnvoyer = () => (
  <Icone>
    <path d="M5 12h14M13 6l6 6-6 6" />
  </Icone>
)
export const IconeLune = () => (
  <Icone>
    <path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z" />
  </Icone>
)
export const IconeSoleil = () => (
  <Icone>
    <circle cx="12" cy="12" r="4" />
    <path d="M12 2v2M12 20v2M2 12h2M20 12h2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" />
  </Icone>
)
export const IconeCadenas = () => (
  <Icone taille={16}>
    <rect x="5" y="11" width="14" height="9" rx="2" />
    <path d="M8 11V8a4 4 0 0 1 8 0v3" />
  </Icone>
)
export const IconeCoche = () => (
  <Icone taille={14}>
    <path d="M5 12l4 4 10-10" />
  </Icone>
)
export const IconeCle = () => (
  <Icone taille={16}>
    <path d="M14.7 6.3a4 4 0 0 0-5.4 5.4L3 18v3h3l6.3-6.3a4 4 0 0 0 5.4-5.4l-2.6 2.6-2.4-2.4z" />
  </Icone>
)
export const IconeMenu = () => (
  <Icone>
    <path d="M4 7h16M4 12h16M4 17h16" />
  </Icone>
)
export const IconeFermer = () => (
  <Icone taille={16}>
    <path d="M6 6l12 12M18 6L6 18" />
  </Icone>
)
export const IconeTelecharger = () => (
  <Icone taille={16}>
    <path d="M12 4v11M7 10l5 5 5-5M5 20h14" />
  </Icone>
)
export const IconePouceHaut = () => (
  <Icone taille={18}>
    <path d="M7 11v9H4v-9zM7 11l4-7a2 2 0 0 1 3 2l-1 5h5a2 2 0 0 1 2 2.3l-1.2 6A2 2 0 0 1 16.8 21H7" />
  </Icone>
)
export const IconePouceBas = () => (
  <Icone taille={18}>
    <path d="M17 13V4h3v9zM17 13l-4 7a2 2 0 0 1-3-2l1-5H6a2 2 0 0 1-2-2.3l1.2-6A2 2 0 0 1 7.2 3H17" />
  </Icone>
)
export const IconeIdentite = () => (
  <Icone>
    <rect x="3" y="5" width="18" height="14" rx="2" />
    <circle cx="9" cy="11" r="2" />
    <path d="M6 16c.6-1.5 1.7-2 3-2s2.4.5 3 2M14 10h4M14 13h3" />
  </Icone>
)
export const IconeEmployes = () => (
  <Icone>
    <circle cx="9" cy="8" r="3" />
    <path d="M3 20c.8-3.5 3.2-5 6-5s5.2 1.5 6 5M18 8v6M15 11h6" />
  </Icone>
)
export const IconeCalculatrice = () => (
  <Icone>
    <rect x="5" y="3" width="14" height="18" rx="2" />
    <path d="M8 7h8M8 11h.01M12 11h.01M16 11h.01M8 15h.01M12 15h.01M16 15h.01M8 18h.01M12 18h.01" />
  </Icone>
)
export const IconeCalendrier = () => (
  <Icone>
    <rect x="3" y="5" width="18" height="16" rx="2" />
    <path d="M3 10h18M8 3v4M16 3v4" />
  </Icone>
)
export const IconeGraphique = () => (
  <Icone>
    <path d="M4 20h16M7 16v-4M12 16V8M17 16v-6" />
  </Icone>
)
export const IconeBatiment = () => (
  <Icone>
    <path d="M4 20V9l8-5 8 5v11zM9 20v-5h6v5" />
  </Icone>
)
