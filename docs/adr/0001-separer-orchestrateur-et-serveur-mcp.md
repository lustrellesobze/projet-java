# ADR-0001 : séparer l'orchestrateur et le serveur MCP

- **Date :** 2026-11-03
- **Statut :** acceptée
- **Auteurs :** Lustrelle SOBZE, MENARD EBIANG, Didier Raoul

## Contexte

L'assistant doit comprendre des questions en langage naturel (rôle du LLM) mais donner
des chiffres exacts et sourcés (taux CNPS, TVA, procédures). Un LLM seul peut inventer un chiffre.

## Décision

Deux applications Spring Boot distinctes :

- l'**orchestrateur** gère les comptes, la sécurité, l'historique et le dialogue avec le LLM ;
- le **serveur MCP** expose les outils métier (`@McpTool`) qui lisent le référentiel et calculent en `BigDecimal`.

Le LLM choisit l'outil ; le code Java calcule. Ils communiquent par le protocole MCP (Streamable HTTP).

## Options écartées

- **Une seule application** avec de simples fonctions appelées par le LLM : plus simple, mais les outils
  ne seraient pas réutilisables par d'autres clients MCP et la clé du LLM côtoierait le code métier.
- **Le LLM calcule lui-même** : rejeté, risque de chiffres inventés.

## Conséquences

- Les outils se testent sans IA, et chaque appel est tracé (`ExecutionOutil`).
- Deux services à déployer au lieu d'un ; docker compose les lance ensemble.
