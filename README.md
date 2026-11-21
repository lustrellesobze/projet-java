# Mboa Gouvia

**Plateforme IA native d'interaction avec les services publics camerounais en langage naturel.**

L'usager pose sa question en français ou en anglais, comme à un agent au guichet :
« Quels papiers pour un passeport ? », « Calcule la CNPS de mes 3 employés pour octobre ».
L'IA comprend la demande et choisit un **outil MCP** ; c'est ensuite le **code Java** qui lit le
référentiel officiel, calcule et cite sa source. **L'IA comprend, Java calcule et vérifie.**

> Projet du cours *Environnement et programmation Java*, M. FOTSO KUATE Victor
> Master 1 Système d'information et génie logiciel, IUC Douala, 2026-2027

## Sommaire

1. [L'équipe](#léquipe)
2. [Fonctionnalités](#fonctionnalités)
3. [Technologies](#technologies)
4. [Architecture](#architecture)
5. [Structure du code](#structure-du-code)
6. [Installation du poste de travail](#installation-du-poste-de-travail)
7. [Lancer le projet](#lancer-le-projet)
8. [Tests et qualité](#tests-et-qualité)
9. [API REST](#api-rest)
10. [Documentation](#documentation)
11. [Organisation du travail](#organisation-du-travail)

## L'équipe

| Membre | Responsabilité principale |
| --- | --- |
| Lustrelle SOBZE | Coordination Kanban, pipeline CI/CD, interface React |
| MENARD EBIANG | Orchestrateur : comptes, sécurité JWT, dialogue avec l'IA |
| Didier Raoul | Serveur MCP : outils métier, référentiel, base PostgreSQL |

## Fonctionnalités

| Cas d'utilisation | Fonctionnalité | État |
| --- | --- | --- |
| CU-01 | S'inscrire (citoyen ou entreprise), avec consentement | ✅ Fait |
| CU-02 | Se connecter (jeton JWT, verrouillage après 5 échecs) | ✅ Fait |
| CU-04 | Poser une question à l'assistant, avec ou sans compte | ✅ Fait |
| CU-05 | Tracer chaque appel d'outil MCP (journal des exécutions) | ✅ Fait |
| CU-08 | Calculer les cotisations CNPS | ✅ Fait |
| CU-11 | **Nouvelle conversation**, fil de discussion gardé, historique des conversations | ✅ Fait |
| CU-06 | Consulter une procédure administrative | ⏳ Sprint 2 |
| CU-07 | Calculer la TVA | ⏳ Sprint 3 |
| CU-09, CU-14 | Échéances et rappels par e-mail | ⏳ Sprint 3 |
| CU-10 | Indicateurs publics (API Banque mondiale) | ⏳ Sprint 3 |
| CU-18, CU-19 | Gérer et importer ses employés | ⏳ Sprint 3 |
| CU-20 à CU-22 | Back-office : référentiel, journal, statistiques | ⏳ Sprints 3 et 4 |
| CU-12, 13, 15 à 17 | Export PDF, notes, préférences, export et suppression des données | ⏳ Sprint 4 |

## Technologies

| Domaine | Choix |
| --- | --- |
| Langage serveur | Java 21 |
| Framework | Spring Boot 4.1 (Web MVC, Data JPA, Security, Validation, Actuator) |
| Intelligence artificielle | Spring AI 2.0 : ChatClient, client et serveur MCP |
| Base de données | PostgreSQL 16, migrations Flyway |
| Sécurité | JWT (HS256), BCrypt, rôles CITOYEN, REPRESENTANT, ADMIN |
| Interface | React 19, TypeScript, Tailwind CSS 4, Vite |
| Tests | JUnit 5, Mockito, AssertJ, Vitest, couverture JaCoCo |
| Livraison | Docker, docker compose, GitHub Actions (CI/CD), ghcr.io, Render |

## Architecture

```
Interface React ──HTTPS + JWT──► Orchestrateur (Spring Boot + Spring AI) ──► Fournisseur d'IA
                                        │ protocole MCP (Streamable HTTP)
                                        ▼
                                 Serveur MCP (Spring Boot, outils @McpTool) ──► API Banque mondiale
                                        │ JPA
                                        ▼
                                   PostgreSQL 16 (schéma géré par Flyway)
```

| Module | Rôle | Port |
| --- | --- | --- |
| `mboa-common` | Objets partagés (DTO, exceptions) | aucun |
| `mboa-mcp-server` | Outils MCP, règles métier, référentiel, migrations Flyway | 8081 |
| `mboa-orchestrator` | API REST, sécurité, comptes, conversations, quotas, dialogue avec l'IA | 8090 |
| `mboa-web` | Interface React | 5173 (dev) / 3000 (Docker) |

Pourquoi deux applications Java ? Voir [docs/adr/0001](docs/adr/0001-separer-orchestrateur-et-serveur-mcp.md).

## Structure du code

```
mboa-gouvia/
├── pom.xml                         POM parent : versions de Spring Boot, Spring AI, JaCoCo
├── mvnw, mvnw.cmd                  Maven Wrapper (Maven n'a pas besoin d'être installé)
├── docker-compose.yml              PostgreSQL + les 3 services
├── .env.example                    Modèle des secrets, à copier en .env (jamais commité)
├── .github/                        CI/CD, modèles d'issues et de pull requests, Dependabot
├── .vscode/extensions.json         Extensions VS Code recommandées
├── docs/                           Décisions d'architecture (ADR), guide utilisateur
├── scripts/initialiser-github.ps1  Crée labels, sprints, backlog et tableau Kanban
│
├── mboa-common/                    Objets partagés : LigneSalaire, ResultatCotisations…
│
├── mboa-mcp-server/
│   └── src/main/java/cm/mboagouvia/mcp/
│       ├── domain/                 Entités : Taux, SourceOfficielle, ExecutionOutil
│       ├── repository/             Accès aux données (Spring Data JPA)
│       ├── service/                Règles métier : CotisationService, JournalExecutionService
│       └── tool/                   Outils appelés par l'IA (@McpTool) : CotisationsTools
│   └── src/main/resources/
│       ├── application.yml         Configuration (port 8081, base, serveur MCP)
│       └── db/migration/           V1__init.sql (13 tables), V2 données de démonstration
│
├── mboa-orchestrator/
│   └── src/main/java/cm/mboagouvia/orchestrator/
│       ├── config/                 Sécurité JWT, CORS, paramètres (quotas, verrouillage)
│       ├── compte/                 Utilisateur (abstraite), Citoyen, RepresentantEntreprise, Administrateur
│       ├── auth/                   Inscription, connexion
│       ├── assistant/              Questions à l'IA, quotas, pseudonymisation
│       ├── conversation/           Nouvelle conversation, historique, relecture, suppression
│       └── erreur/                 Réponses d'erreur normalisées (401, 404, 423, 429)
│
└── mboa-web/
    └── src/
        ├── api/client.ts           Appels à l'API
        ├── i18n.ts                 Textes français et anglais
        ├── pages/                  Assistant, Connexion
        ├── components/             EnTete, BarreLaterale, AccueilAssistant, CarteReponse, PanneauOutils, ZoneSaisie
        ├── utils/                  Format FCFA, conversations, paramètres d'outils (et leurs tests)
        └── index.css               Couleurs : vert #1F7A4D, or #C9A227, fond #F6F7F4
```

Chaque application suit l'**architecture en couches** : contrôleur ou outil (entrée), service (règles),
repository (données), entités (modèle du diagramme de classes).

## Installation du poste de travail

Spring Boot et React ne s'installent pas comme des logiciels : ce sont des **bibliothèques**
téléchargées automatiquement, Spring Boot par Maven (`pom.xml`), React par npm (`package.json`).
Il faut seulement installer les outils qui les font fonctionner.

**1. Les logiciels** (PowerShell, une ligne à la fois) :

```powershell
winget install EclipseAdoptium.Temurin.21.JDK   # Java 21 (JDK), pour Spring Boot
winget install OpenJS.NodeJS.LTS                 # Node.js et npm, pour React
winget install Git.Git                           # Git
winget install Docker.DockerDesktop              # Docker, pour PostgreSQL
winget install GitHub.cli                        # GitHub CLI
```

**2. Les extensions VS Code** (fermer puis rouvrir PowerShell avant) :

```powershell
code --install-extension vscjava.vscode-java-pack
code --install-extension vmware.vscode-boot-dev-pack
code --install-extension bradlc.vscode-tailwindcss
code --install-extension ms-azuretools.vscode-containers
```

**3. Vérification** :

```powershell
java -version      # 21
node --version     # v22 ou plus
git --version
docker --version
```

**4. Télécharger Spring Boot et React dans le projet** :

```powershell
.\mvnw.cmd install -DskipTests   # télécharge Spring Boot, Spring AI et compile les modules Java
cd mboa-web
npm install                      # télécharge React, Vite, Tailwind
cd ..
```

## Lancer le projet

**Option 1 : l'interface seule** (pour voir les écrans, sans serveur) :

```powershell
cd mboa-web
npm run dev        # http://localhost:5173
```

**Option 2 : toute l'application en développement** (trois terminaux) :

```powershell
# Terminal 1 : base de données, puis serveur MCP (applique les migrations Flyway)
docker compose up -d postgres
.\mvnw.cmd -pl mboa-mcp-server spring-boot:run

# Terminal 2 : orchestrateur
$env:ANTHROPIC_API_KEY = "votre-cle"
.\mvnw.cmd -pl mboa-orchestrator spring-boot:run

# Terminal 3 : interface
cd mboa-web
npm run dev
```

**Option 3 : tout avec Docker** :

```powershell
Copy-Item .env.example .env      # puis remplir ANTHROPIC_API_KEY et JWT_SECRET
docker compose up --build
# Interface : http://localhost:3000    API : http://localhost:8090/swagger-ui.html
```

## Tests et qualité

```powershell
.\mvnw.cmd test          # tests unitaires Java (*Test.java), sans base de données
.\mvnw.cmd verify        # + tests d'intégration (*IT.java) et couverture ; PostgreSQL doit tourner
cd mboa-web; npm test    # tests de l'interface (Vitest)
```

Rapport de couverture : `mboa-mcp-server/target/site/jacoco/index.html` (idem pour l'orchestrateur).
À chaque push, la CI GitHub Actions relance tous ces tests automatiquement.

## API REST

Documentation interactive complète : `http://localhost:8090/swagger-ui.html` (générée par springdoc).

| Méthode | Chemin | Accès | Rôle |
| --- | --- | --- | --- |
| POST | `/api/auth/inscription` | Public | Créer un compte (CU-01) |
| POST | `/api/auth/connexion` | Public | Se connecter, recevoir un jeton JWT (CU-02) |
| POST | `/api/assistant/questions` | Public (quota) | Poser une question ; sans `conversationId`, démarre une nouvelle conversation ; un visiteur joint `historique` (10 derniers messages) (CU-04) |
| GET | `/api/statut` | Public | État du serveur MCP et nombre d'outils disponibles |
| GET | `/api/conversations` | Connecté | Lister ses conversations (CU-11) |
| GET | `/api/conversations/{id}/messages` | Connecté | Relire une conversation |
| DELETE | `/api/conversations/{id}` | Connecté | Supprimer une conversation |

Outil MCP exposé à l'IA : `calculer_cotisations_cnps` (point d'accès `http://localhost:8081/mcp`).

## Documentation

| Document | Contenu |
| --- | --- |
| [docs/README.md](docs/README.md) | Index de toute la documentation |
| [docs/adr/](docs/adr/) | Décisions d'architecture et leurs raisons |
| [docs/guide-utilisateur.md](docs/guide-utilisateur.md) | Guide pour les usagers et le jury |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Règles de travail en équipe |
| [CHANGELOG.md](CHANGELOG.md) | Historique des versions |
| Cahier des charges, dossier de conception UML | Documents Word et PDF du projet |

## Organisation du travail

Kanban sur GitHub Projects (colonnes Backlog, À faire, En cours, En revue, Terminé), 5 sprints,
une branche par issue créée depuis `develop`, une pull request relue par un autre membre, CI verte obligatoire
avant fusion, et une fusion `develop` → `main` à la fin de chaque sprint.
Détails dans [CONTRIBUTING.md](CONTRIBUTING.md).

Tout le tableau (sprints, labels, tâches réparties entre les 3 membres) se crée en une commande :

```powershell
.\scripts\initialiser-github.ps1 -Depot "proprietaire/groupe-java" -Lustrelle "compte1" -Menard "compte2" -Didier "compte3"
```

## Secrets

La clé de l'IA n'est **jamais** écrite dans le code. Elle se place :

| Où | Comment |
| --- | --- |
| Sur son poste | Fichier `.env` (copié de `.env.example`, ignoré par Git) ou `$env:ANTHROPIC_API_KEY` |
| Sur GitHub | Settings > Secrets and variables > Actions > `ANTHROPIC_API_KEY` |
| En production | Variable d'environnement Render, ou Secret Kubernetes |

## Données de démonstration

`V2__donnees_demonstration.sql` insère des **taux CNPS de démonstration, non officiels**.
Ils doivent être remplacés par les valeurs publiées par la CNPS, avec leur source, avant toute démonstration publique.
