# Contribuer à Mboa Gouvia

## Le tableau Kanban (GitHub Projects)

| Colonne | Signification | Limite |
| --- | --- | --- |
| **Backlog** | Toutes les tâches connues, triées par priorité | aucune |
| **À faire** | Tâches choisies pour le sprint en cours | aucune |
| **En cours** | Quelqu'un travaille dessus (une personne assignée) | **2 par personne** |
| **En revue** | Pull request ouverte, en attente de relecture | 3 au total |
| **Terminé** | PR fusionnée, CI verte | aucune |

Règle d'or : **on finit avant de commencer**. Si vous avez déjà 2 cartes « En cours »,
aidez à relire les cartes « En revue » plutôt que d'en prendre une nouvelle.

## Les branches (Git Flow simplifié)

```
feature/12-calcul-cnps ─┐
fix/31-verrouillage ────┼──► develop ──(fin de sprint)──► main ──► déploiement
docs/7-maj-uml ─────────┘    intégration                  version stable
```

| Branche | Rôle | Qui y écrit |
| --- | --- | --- |
| `main` | Version **stable**, celle qu'on montre et qu'on déploie (CD vers Render) | Personne directement : uniquement par une PR `develop` → `main`, en fin de sprint |
| `develop` | Branche d'**intégration** : le travail terminé de toute l'équipe | Personne directement : uniquement par des PR depuis les branches de tâche |
| `feature/…`, `fix/…` | **Une branche par issue**, créée depuis `develop` | La personne assignée à l'issue |

Les deux branches `main` et `develop` sont **protégées** : pull request obligatoire,
1 approbation, CI verte. Sur GitHub, `develop` est la **branche par défaut**,
pour que chaque nouvelle PR vise `develop` sans y penser.

| Type | Exemple |
| --- | --- |
| Fonctionnalité | `feature/12-calcul-cnps` |
| Correction | `fix/31-verrouillage-compte` |
| Tests | `test/18-tests-auth` |
| Documentation | `docs/7-maj-dossier-uml` |
| Pipeline | `ci/3-deploiement-render` |

## Le cycle d'une tâche, pas à pas (PowerShell)

```powershell
git switch develop
git pull                                  # 1. partir de la dernière version de develop
git switch -c feature/12-calcul-cnps      # 2. créer sa branche
# ... coder, tester ...
.\mvnw.cmd test                           # 3. vérifier en local
git add .
git commit -m "feat(mcp): calcul des cotisations CNPS (#12)"
git push -u origin feature/12-calcul-cnps # 4. publier la branche
# 5. Sur GitHub : « Compare & pull request » vers develop, remplir le modèle, écrire « Ferme #12 »
# 6. Attendre la CI verte + 1 approbation, puis « Squash and merge »
```

## La fin de sprint : de develop vers main

Quand toutes les cartes du sprint sont dans « Terminé » et que la CI de `develop` est verte :

1. Sur GitHub, ouvrir une pull request **`develop` → `main`**, titre : `Sprint 2 : assistant`.
2. Relecture par un autre membre, puis **« Create a merge commit »** (pas de squash, pour garder l'historique du sprint).
3. Créer une version (onglet **Releases**) : `v0.2.0`, avec la liste des fonctionnalités du sprint.

La CD se déclenche alors toute seule et publie la nouvelle version.

## Les messages de commit (Conventional Commits)

`type(module): description courte (#issue)`

| Type | Quand |
| --- | --- |
| `feat` | nouvelle fonctionnalité |
| `fix` | correction de bug |
| `test` | ajout ou modification de tests |
| `docs` | documentation |
| `refactor` | réorganisation sans changer le comportement |
| `ci` / `chore` | pipeline, configuration, dépendances |

Exemples : `feat(auth): verrouillage après 5 échecs (#9)`, `test(mcp): cas du référentiel incomplet (#14)`.

## Définition de « Terminé »

Une carte passe dans **Terminé** seulement si :

1. le code compile et **la CI est verte** ;
2. des **tests** couvrent le nouveau comportement ;
3. **aucun secret** n'est dans le code (clés dans `.env` ou dans les secrets GitHub) ;
4. un **autre membre** a relu et approuvé la PR ;
5. la documentation (README, dossier UML) est à jour si besoin.

## Relire une pull request

- Le code fait-il ce que l'issue demande ?
- Les noms sont-ils clairs ? Les montants en `BigDecimal` ?
- Y a-t-il un test pour le cas normal **et** un cas d'erreur ?
- Commentez avec bienveillance : on critique le code, pas la personne.
