// Client de l'API REST de l'orchestrateur. Le jeton JWT est gardé pour la session du navigateur.
import type { Langue } from '../i18n'

const BASE = import.meta.env.VITE_API_URL ?? ''

export type { Langue }
export type Role = 'CITOYEN' | 'REPRESENTANT' | 'ADMIN'

export interface ReponseConnexion {
  jeton: string
  role: Role
  langue: Langue
  theme: 'CLAIR' | 'SOMBRE'
  tailleTexte: number
}

/** Un appel d'outil MCP fait pour produire une réponse. */
export interface OutilExecute {
  nom: string
  parametres: string
  dureeMs: number
  succes: boolean
}

/** conversationId et titre sont nuls pour un visiteur : rien n'est enregistré. */
export interface ReponseAssistant {
  texte: string
  conversationId: number | null
  titre: string | null
  outils: OutilExecute[]
}

export interface ResumeConversation {
  id: number
  titre: string
  dateMaj: string
}

export interface MessageAffiche {
  role: 'USER' | 'ASSISTANT'
  contenu: string
  horodatage: string
}

/** Message déjà affiché, renvoyé au serveur par un visiteur pour que l'IA garde le fil. */
export interface MessagePrecedent {
  role: 'USER' | 'ASSISTANT'
  contenu: string
}

export interface Statut {
  serveurMcp: boolean
  nombreOutils: number
}

let jeton: string | null = sessionStorage.getItem('mboa-jeton')
let role: Role | null = sessionStorage.getItem('mboa-role') as Role | null

export function estConnecte(): boolean {
  return jeton !== null
}

export function roleConnecte(): Role | null {
  return role
}

export function deconnecter(): void {
  jeton = null
  role = null
  sessionStorage.removeItem('mboa-jeton')
  sessionStorage.removeItem('mboa-role')
}

async function appeler<T>(methode: 'GET' | 'POST' | 'DELETE', chemin: string, corps?: unknown): Promise<T> {
  const entetes: Record<string, string> = {}
  if (corps !== undefined) entetes['Content-Type'] = 'application/json'
  if (jeton) entetes.Authorization = `Bearer ${jeton}`

  const reponse = await fetch(`${BASE}${chemin}`, {
    method: methode,
    headers: entetes,
    body: corps === undefined ? undefined : JSON.stringify(corps),
  })
  if (!reponse.ok) {
    const probleme = await reponse.json().catch(() => null)
    throw new Error(probleme?.detail ?? `Erreur ${reponse.status}`)
  }
  if (reponse.status === 204) return undefined as T
  return reponse.json() as Promise<T>
}

export async function connecter(email: string, motDePasse: string): Promise<ReponseConnexion> {
  const resultat = await appeler<ReponseConnexion>('POST', '/api/auth/connexion', { email, motDePasse })
  jeton = resultat.jeton
  role = resultat.role
  sessionStorage.setItem('mboa-jeton', resultat.jeton)
  sessionStorage.setItem('mboa-role', resultat.role)
  return resultat
}

/**
 * Sans conversationId, le serveur démarre une nouvelle conversation.
 * historique : seulement pour un visiteur (un usager connecté a son historique en base).
 */
export function poserQuestion(
  question: string,
  langue: Langue,
  conversationId: number | null,
  historique: MessagePrecedent[] = [],
): Promise<ReponseAssistant> {
  return appeler<ReponseAssistant>('POST', '/api/assistant/questions', {
    question,
    langue,
    conversationId,
    historique,
  })
}

export function listerConversations(): Promise<ResumeConversation[]> {
  return appeler<ResumeConversation[]>('GET', '/api/conversations')
}

export function lireConversation(id: number): Promise<MessageAffiche[]> {
  return appeler<MessageAffiche[]>('GET', `/api/conversations/${id}/messages`)
}

export function supprimerConversation(id: number): Promise<void> {
  return appeler<void>('DELETE', `/api/conversations/${id}`)
}

export function lireStatut(): Promise<Statut> {
  return appeler<Statut>('GET', '/api/statut')
}
