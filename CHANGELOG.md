# Historique des versions

Format inspiré de [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/).

## [Non publié]

### Ajouté
- Structure du projet : modules commun, serveur MCP, orchestrateur, interface React.
- Schéma de base de données (13 tables) et données de démonstration.
- Outil MCP `calculer_cotisations_cnps` avec journal des exécutions.
- Inscription et connexion avec jeton JWT et verrouillage après 5 échecs.
- Pipeline CI/CD GitHub Actions.
- Bouton « Nouvelle conversation » et historique des conversations (CU-11) : liste, relecture, suppression ;
  l'assistant garde le fil grâce aux 10 derniers messages.
- Écran Assistant conforme à la maquette : barre latérale, écran d'accueil avec suggestions,
  cartes de réponse, panneau « Outils exécutés » (outils MCP réellement appelés), FR / EN, mode sombre.
- Adresse `GET /api/statut` : état du serveur MCP affiché dans l'en-tête.
- Le fil de discussion est gardé : chaque nouvelle question s'ajoute sous les précédentes, et actualiser
  la page ne l'efface pas. Pour un visiteur, le navigateur renvoie les 10 derniers messages pour que l'IA garde le fil.
- Script `initialiser-github.ps1` : invitations, sprints 1 à 5, Kanban à 5 colonnes, backlog réparti entre les 3 membres.
