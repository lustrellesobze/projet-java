import { useState, type FormEvent } from 'react'
import { connecter, type Role } from '../api/client'
import type { Textes } from '../i18n'

interface Props {
  t: Textes
  onConnecte: (role: Role) => void
}

export default function Connexion({ t, onConnecte }: Props) {
  const [email, setEmail] = useState('')
  const [motDePasse, setMotDePasse] = useState('')
  const [erreur, setErreur] = useState<string | null>(null)

  async function soumettre(e: FormEvent) {
    e.preventDefault()
    setErreur(null)
    try {
      const reponse = await connecter(email, motDePasse)
      onConnecte(reponse.role)
    } catch (err) {
      setErreur(err instanceof Error ? err.message : t.erreurInattendue)
    }
  }

  return (
    <section className="flex flex-1 items-start justify-center overflow-y-auto px-4 py-12">
      <form
        onSubmit={soumettre}
        className="flex w-full max-w-md flex-col gap-4 rounded-2xl border border-bord bg-surface p-6 shadow-sm"
      >
        <h1 className="text-xl font-semibold">{t.connexion}</h1>
        <label className="flex flex-col gap-1 text-sm">
          {t.email}
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="rounded-lg border border-bord bg-surface px-3 py-2 outline-none focus:border-vert"
          />
        </label>
        <label className="flex flex-col gap-1 text-sm">
          {t.motDePasse}
          <input
            type="password"
            required
            value={motDePasse}
            onChange={(e) => setMotDePasse(e.target.value)}
            className="rounded-lg border border-bord bg-surface px-3 py-2 outline-none focus:border-vert"
          />
        </label>
        {erreur && <p className="rounded-lg bg-rouge-clair px-3 py-2 text-sm text-rouge-texte">{erreur}</p>}
        <button type="submit" className="rounded-xl bg-vert py-3 font-semibold text-white hover:bg-vert-fonce">
          {t.seConnecter}
        </button>
      </form>
    </section>
  )
}
