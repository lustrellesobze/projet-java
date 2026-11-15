import { useEffect, useRef, useState } from 'react'
import {
  lireConversation,
  listerConversations,
  poserQuestion,
  supprimerConversation,
  type OutilExecute,
  type ResumeConversation,
  type Statut,
} from '../api/client'
import AccueilAssistant from '../components/AccueilAssistant'
import BarreLaterale from '../components/BarreLaterale'
import CarteReponse from '../components/CarteReponse'
import PanneauOutils from '../components/PanneauOutils'
import ZoneSaisie from '../components/ZoneSaisie'
import type { Langue, Textes } from '../i18n'
import {
  effacerFil,
  historiquePourIa,
  lireFil,
  remonterEnTete,
  sauverFil,
  type ElementFil,
} from '../utils/conversations'

/** Les outils du fil, du plus récent au plus ancien, pour le panneau « Outils exécutés ». */
function outilsDuFil(elements: ElementFil[]): OutilExecute[] {
  return elements.flatMap((el) => (el.type === 'assistant' && el.outils ? el.outils : [])).reverse()
}

interface Props {
  t: Textes
  langue: Langue
  connecte: boolean
  statut: Statut | null | 'chargement'
  menuOuvert: boolean
  onFermerMenu: () => void
}

/**
 * Écran Assistant de la maquette : barre latérale, fil de la conversation, panneau des outils.
 * Chaque nouvelle question s'ajoute sous les précédentes : seul « + Nouvelle conversation » vide l'écran.
 * Le fil est gardé pendant la session du navigateur : actualiser la page ne l'efface pas.
 */
export default function Assistant({ t, langue, connecte, statut, menuOuvert, onFermerMenu }: Props) {
  const [filInitial] = useState(lireFil)
  const [conversations, setConversations] = useState<ResumeConversation[]>([])
  const [conversationId, setConversationId] = useState<number | null>(filInitial.conversationId)
  const [elements, setElements] = useState<ElementFil[]>(filInitial.elements)
  const [outils, setOutils] = useState<OutilExecute[]>(() => outilsDuFil(filInitial.elements))
  const [enCours, setEnCours] = useState(false)
  const [surligne, setSurligne] = useState(false)
  const finDuFil = useRef<HTMLDivElement>(null)

  // Connexion : on charge l'historique. Déconnexion : on repart de zéro.
  const dejaConnecte = useRef(connecte)
  useEffect(() => {
    if (!connecte) {
      setConversations([])
      setConversationId(null)
      // Après une déconnexion, on ne laisse pas la conversation de l'usager à l'écran.
      if (dejaConnecte.current) {
        setElements([])
        setOutils([])
      }
      dejaConnecte.current = false
      return
    }
    dejaConnecte.current = true
    listerConversations()
      .then(setConversations)
      .catch(() => setElements((e) => [...e, { type: 'erreur', texte: t.erreurChargement }]))
  }, [connecte, t.erreurChargement])

  useEffect(() => {
    finDuFil.current?.scrollIntoView({ behavior: 'smooth' })
  }, [elements, enCours])

  useEffect(() => {
    sauverFil(conversationId, elements)
  }, [conversationId, elements])

  function nouvelleConversation() {
    setConversationId(null)
    setElements([])
    setOutils([])
    effacerFil()
    onFermerMenu()
  }

  async function ouvrir(id: number) {
    onFermerMenu()
    try {
      const messages = await lireConversation(id)
      setConversationId(id)
      setOutils([])
      setElements(
        messages.map((m) =>
          m.role === 'USER'
            ? { type: 'usager', texte: m.contenu }
            : { type: 'assistant', texte: m.contenu, outils: null },
        ),
      )
    } catch (err) {
      setElements([{ type: 'erreur', texte: err instanceof Error ? err.message : t.erreurInattendue }])
    }
  }

  async function supprimer(id: number) {
    try {
      await supprimerConversation(id)
      setConversations((liste) => liste.filter((c) => c.id !== id))
      if (id === conversationId) nouvelleConversation()
    } catch (err) {
      setElements((e) => [...e, { type: 'erreur', texte: err instanceof Error ? err.message : t.erreurInattendue }])
    }
  }

  async function envoyer(question: string) {
    if (enCours) return
    // Le visiteur n'a rien en base : on renvoie les messages déjà affichés pour que l'IA garde le fil.
    const historique = connecte ? [] : historiquePourIa(elements)
    setElements((e) => [...e, { type: 'usager', texte: question }])
    setEnCours(true)
    try {
      const reponse = await poserQuestion(question, langue, conversationId, historique)
      const outilsReponse = reponse.outils ?? []
      setElements((e) => [...e, { type: 'assistant', texte: reponse.texte, outils: outilsReponse }])
      setOutils((o) => [...outilsReponse, ...o])
      if (reponse.conversationId !== null && reponse.titre !== null) {
        setConversationId(reponse.conversationId)
        const resume = { id: reponse.conversationId, titre: reponse.titre, dateMaj: new Date().toISOString() }
        setConversations((liste) => remonterEnTete(liste, resume))
      }
    } catch (err) {
      setElements((e) => [...e, { type: 'erreur', texte: err instanceof Error ? err.message : t.erreurInattendue }])
    } finally {
      setEnCours(false)
    }
  }

  function voirOutils() {
    setSurligne(true)
    setTimeout(() => setSurligne(false), 1500)
  }

  return (
    <div className="flex min-h-0 flex-1">
      <BarreLaterale
        t={t}
        ouverte={menuOuvert}
        onFermer={onFermerMenu}
        connecte={connecte}
        conversations={conversations}
        conversationActive={conversationId}
        statut={statut}
        onNouvelle={nouvelleConversation}
        onOuvrir={ouvrir}
        onSupprimer={supprimer}
      />

      <main className="flex min-w-0 flex-1 flex-col">
        <div className="min-h-0 flex-1 overflow-y-auto">
          {elements.length === 0 && !enCours ? (
            <div className="flex min-h-full items-center">
              <AccueilAssistant t={t} onChoisir={envoyer} />
            </div>
          ) : (
            <div className="mx-auto flex max-w-4xl flex-col gap-5 px-4 py-8 md:px-8">
              {elements.map((el, i) =>
                el.type === 'usager' ? (
                  <p
                    key={i}
                    className="max-w-[85%] self-end whitespace-pre-wrap rounded-2xl rounded-br-md bg-vert px-5 py-3 text-white"
                  >
                    {el.texte}
                  </p>
                ) : el.type === 'assistant' ? (
                  <CarteReponse key={i} t={t} texte={el.texte} outils={el.outils} onVoirOutils={voirOutils} />
                ) : (
                  <p key={i} className="self-start rounded-xl bg-rouge-clair px-4 py-3 text-sm text-rouge-texte">
                    {el.texte}
                  </p>
                ),
              )}
              {enCours && (
                <p className="flex items-center gap-3 self-start rounded-2xl border border-bord bg-surface px-5 py-3 text-texte-doux">
                  <span className="h-2 w-2 animate-pulse rounded-full bg-vert" />
                  {t.reflexion}
                </p>
              )}
              <div ref={finDuFil} />
            </div>
          )}
        </div>
        <ZoneSaisie t={t} enCours={enCours} onEnvoyer={envoyer} />
      </main>

      {elements.length > 0 && <PanneauOutils t={t} outils={outils} surligne={surligne} />}
    </div>
  )
}
