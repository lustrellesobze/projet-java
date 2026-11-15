import type { MessagePrecedent, OutilExecute, ResumeConversation } from '../api/client'

/** Un élément du fil affiché à l'écran. */
export type ElementFil =
  | { type: 'usager'; texte: string }
  | { type: 'assistant'; texte: string; outils: OutilExecute[] | null }
  | { type: 'erreur'; texte: string }

/** Nombre de messages précédents envoyés à l'IA (même valeur que côté serveur). */
export const TAILLE_HISTORIQUE = 10

/**
 * Après un échange, la conversation remonte en tête de la liste ;
 * une conversation qui vient d'être créée y est ajoutée.
 */
export function remonterEnTete(
  liste: ResumeConversation[],
  conversation: ResumeConversation,
): ResumeConversation[] {
  return [conversation, ...liste.filter((c) => c.id !== conversation.id)]
}

/**
 * Les derniers messages du fil, au format attendu par le serveur.
 * Les messages d'erreur ne sont pas envoyés : l'IA ne les a jamais écrits.
 */
export function historiquePourIa(elements: ElementFil[]): MessagePrecedent[] {
  const messages: MessagePrecedent[] = []
  for (const el of elements) {
    if (el.type === 'usager') messages.push({ role: 'USER', contenu: el.texte })
    else if (el.type === 'assistant') messages.push({ role: 'ASSISTANT', contenu: el.texte })
  }
  return messages.slice(-TAILLE_HISTORIQUE)
}

const CLE_FIL = 'mboa-fil'

/** Garde le fil en cours pour la session du navigateur : actualiser la page ne l'efface pas. */
export function sauverFil(conversationId: number | null, elements: ElementFil[]): void {
  try {
    sessionStorage.setItem(CLE_FIL, JSON.stringify({ conversationId, elements }))
  } catch {
    // Stockage indisponible (navigation privée...) : le fil reste seulement en mémoire.
  }
}

export function lireFil(): { conversationId: number | null; elements: ElementFil[] } {
  try {
    const brut = sessionStorage.getItem(CLE_FIL)
    if (brut) {
      const fil = JSON.parse(brut)
      if (Array.isArray(fil.elements)) {
        return { conversationId: typeof fil.conversationId === 'number' ? fil.conversationId : null, elements: fil.elements }
      }
    }
  } catch {
    // Contenu illisible : on repart d'un fil vide.
  }
  return { conversationId: null, elements: [] }
}

export function effacerFil(): void {
  try {
    sessionStorage.removeItem(CLE_FIL)
  } catch {
    // Rien à faire.
  }
}
