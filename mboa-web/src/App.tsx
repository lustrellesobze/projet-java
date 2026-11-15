import { useEffect, useState } from 'react'
import { deconnecter, estConnecte, lireStatut, roleConnecte, type Role, type Statut } from './api/client'
import EnTete from './components/EnTete'
import { textesPour, type Langue } from './i18n'
import Assistant from './pages/Assistant'
import Connexion from './pages/Connexion'

type Page = 'assistant' | 'connexion'

// Préférences gardées dans le navigateur ; protégé si le stockage est indisponible.
function lirePreference(cle: string): string | null {
  try {
    return localStorage.getItem(cle)
  } catch {
    return null
  }
}
function ecrirePreference(cle: string, valeur: string) {
  try {
    localStorage.setItem(cle, valeur)
  } catch {
    // stockage indisponible : la préférence ne sera simplement pas retenue
  }
}

export default function App() {
  const [page, setPage] = useState<Page>('assistant')
  const [langue, setLangue] = useState<Langue>(lirePreference('mboa-langue') === 'EN' ? 'EN' : 'FR')
  const [sombre, setSombre] = useState(lirePreference('mboa-theme') === 'sombre')
  const [role, setRole] = useState<Role | null>(estConnecte() ? roleConnecte() : null)
  const [statut, setStatut] = useState<Statut | null | 'chargement'>('chargement')
  const [menuOuvert, setMenuOuvert] = useState(false)
  const t = textesPour(langue)

  useEffect(() => {
    document.documentElement.classList.toggle('sombre', sombre)
    ecrirePreference('mboa-theme', sombre ? 'sombre' : 'clair')
  }, [sombre])

  useEffect(() => {
    document.documentElement.lang = langue === 'EN' ? 'en' : 'fr'
    ecrirePreference('mboa-langue', langue)
  }, [langue])

  // État du serveur MCP, vérifié au démarrage puis toutes les 30 secondes.
  useEffect(() => {
    const verifier = () =>
      lireStatut()
        .then(setStatut)
        .catch(() => setStatut(null))
    verifier()
    const minuteur = setInterval(verifier, 30_000)
    return () => clearInterval(minuteur)
  }, [])

  return (
    <div className="flex h-full flex-col">
      <EnTete
        t={t}
        langue={langue}
        onLangue={setLangue}
        sombre={sombre}
        onTheme={() => setSombre((s) => !s)}
        statut={statut}
        role={role}
        onConnexion={() => setPage('connexion')}
        onDeconnexion={() => {
          deconnecter()
          setRole(null)
          setPage('assistant')
        }}
        onMenu={() => setMenuOuvert(true)}
      />
      {page === 'assistant' ? (
        <Assistant
          t={t}
          langue={langue}
          connecte={role !== null}
          statut={statut}
          menuOuvert={menuOuvert}
          onFermerMenu={() => setMenuOuvert(false)}
        />
      ) : (
        <Connexion
          t={t}
          onConnecte={(r) => {
            setRole(r)
            setPage('assistant')
          }}
        />
      )}
    </div>
  )
}
