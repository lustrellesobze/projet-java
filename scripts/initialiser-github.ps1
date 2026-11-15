<#
.SYNOPSIS
    Prépare le dépôt GitHub de Mboa Gouvia en une seule commande :
    invitations de l'équipe, labels, sprints, tableau Kanban, backlog réparti entre les 3 membres.

.DESCRIPTION
    À lancer par une seule personne, depuis la racine du projet, APRÈS avoir poussé le code sur GitHub.
    Le script peut être relancé sans risque : ce qui existe déjà n'est pas recréé.

    Prérequis :
      1. GitHub CLI installé :   winget install --id GitHub.cli
      2. Connexion :             gh auth login
      3. Droit sur les projets : gh auth refresh -s project

    Une personne invitée ne peut recevoir de tâches qu'après avoir ACCEPTÉ l'invitation
    (lien reçu par e-mail). Si l'attribution échoue, relancez simplement la même commande
    une fois que MENARD et Didier ont accepté : seules les attributions manquantes seront faites.

.EXAMPLE
    .\scripts\initialiser-github.ps1 -Depot "lustrellesobze/groupe-java" -Lustrelle "lustrellesobze" -Menard "menardebiang" -Didier "didierraoul"
#>
param(
    [Parameter(Mandatory = $true)] [string]$Depot,
    [Parameter(Mandatory = $true)] [string]$Lustrelle,
    [Parameter(Mandatory = $true)] [string]$Menard,
    [Parameter(Mandatory = $true)] [string]$Didier
)

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
$Proprietaire = $Depot.Split("/")[0]
$NomDepot = $Depot.Split("/")[1]
$Titre = "Mboa Gouvia - Kanban"
$Comptes = @{ L = $Lustrelle; M = $Menard; D = $Didier }

function Verifier([string]$Etape) {
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Échec : $Etape. Lisez le message ci-dessus, corrigez, puis relancez le script." -ForegroundColor Red
        exit 1
    }
}

# ---------------------------------------------------------------- 0. Vérifications
Write-Host "0/6 Vérifications..." -ForegroundColor Green
gh auth status | Out-Null
Verifier "vous n'êtes pas connecté à GitHub (lancez : gh auth login)"
gh repo view $Depot --json name | Out-Null
Verifier "le dépôt $Depot est introuvable (poussez d'abord le code sur GitHub)"

# ---------------------------------------------------------------- 1. Équipe
Write-Host "1/6 Invitation de l'équipe..." -ForegroundColor Green
foreach ($membre in @($Menard, $Didier)) {
    if ($membre -ne $Proprietaire) {
        gh api -X PUT "repos/$Depot/collaborators/$membre" -f permission=push --silent 2>$null
        if ($LASTEXITCODE -eq 0) { Write-Host "  + $membre invité(e) (droit d'écriture)" }
        else { Write-Host "  ! Impossible d'inviter $membre : vérifiez son nom d'utilisateur GitHub." -ForegroundColor Yellow }
    }
}

# ---------------------------------------------------------------- 2. Labels
Write-Host "2/6 Création des labels..." -ForegroundColor Green
$labels = @(
    @{ n = "type: fonctionnalité";  c = "1F7A4D"; d = "Nouvelle fonctionnalité (récit utilisateur)" },
    @{ n = "type: bug";             c = "D73A4A"; d = "Quelque chose ne fonctionne pas" },
    @{ n = "type: technique";       c = "5A5A5A"; d = "Pipeline, configuration, infrastructure" },
    @{ n = "type: documentation";   c = "0075CA"; d = "Dossier UML, README, soutenance" },
    @{ n = "module: mcp-server";    c = "C9A227"; d = "Outils MCP et logique métier" },
    @{ n = "module: orchestrateur"; c = "E3B341"; d = "API, sécurité, IA" },
    @{ n = "module: web";           c = "7BD3A8"; d = "Interface React" },
    @{ n = "module: infra";         c = "1D76DB"; d = "Docker, Kubernetes, CI/CD, déploiement" },
    @{ n = "module: mobile";        c = "8250DF"; d = "Application mobile" },
    @{ n = "priorité: haute";       c = "B60205"; d = "À traiter en premier" }
)
foreach ($l in $labels) {
    gh label create $l.n --repo $Depot --color $l.c --description $l.d --force | Out-Null
    Verifier "création du label $($l.n)"
}

# ---------------------------------------------------------------- 3. Sprints
Write-Host "3/6 Création des sprints (milestones)..." -ForegroundColor Green
$S1 = "Sprint 1 : socle"
$S2 = "Sprint 2 : assistant"
$S3 = "Sprint 3 : services"
$S4 = "Sprint 4 : qualité et livraison"
$S5 = "Sprint 5 : Kubernetes et mobile"
$sprints = [ordered]@{
    $S1 = @{ fin = "2026-11-15T23:59:59Z"; d = "Structure du projet, comptes, connexion, CI/CD" }
    $S2 = @{ fin = "2026-11-29T23:59:59Z"; d = "Assistant IA de bout en bout : clé API, outils MCP, historique" }
    $S3 = @{ fin = "2026-12-13T23:59:59Z"; d = "TVA, échéances, indicateurs, employés, back-office, Docker" }
    $S4 = @{ fin = "2026-12-27T23:59:59Z"; d = "Journal, exports, couverture de tests, déploiement, soutenance" }
    $S5 = @{ fin = "2027-01-10T23:59:59Z"; d = "Déploiement Kubernetes et version mobile" }
}
$existants = @(gh api "repos/$Depot/milestones?state=all&per_page=100" --jq ".[].title")
foreach ($titre in $sprints.Keys) {
    if ($existants -notcontains $titre) {
        gh api "repos/$Depot/milestones" -f title="$titre" -f due_on="$($sprints[$titre].fin)" -f description="$($sprints[$titre].d)" --silent
        Verifier "création du sprint $titre"
        Write-Host "  + $titre"
    }
}

# ---------------------------------------------------------------- 4. Tableau Kanban
Write-Host "4/6 Tableau Kanban (GitHub Projects)..." -ForegroundColor Green
$projets = gh project list --owner $Proprietaire --format json --limit 100 | ConvertFrom-Json
Verifier "lecture des projets (lancez : gh auth refresh -s project)"
$projet = $projets.projects | Where-Object { $_.title -eq $Titre } | Select-Object -First 1
if (-not $projet) {
    $projet = gh project create --owner $Proprietaire --title $Titre --format json | ConvertFrom-Json
    Verifier "création du tableau"
    gh project link $projet.number --owner $Proprietaire --repo $NomDepot | Out-Null
    Write-Host "  + Tableau créé"
}
$numeroProjet = $projet.number

# Colonnes du Kanban : on remplace Todo / In Progress / Done par les 5 colonnes de l'équipe
$colonnes = @("Backlog", "À faire", "En cours", "En revue", "Terminé")
$champs = gh project field-list $numeroProjet --owner $Proprietaire --format json | ConvertFrom-Json
$statut = $champs.fields | Where-Object { $_.name -eq "Status" } | Select-Object -First 1
$nomsActuels = @($statut.options | ForEach-Object { $_.name })
if (($nomsActuels -join "|") -ne ($colonnes -join "|")) {
    $mutation = @'
mutation($id: ID!) {
  updateProjectV2Field(input: {
    fieldId: $id,
    singleSelectOptions: [
      {name: "Backlog",  color: GRAY,   description: "Pas encore planifié"},
      {name: "À faire",  color: BLUE,   description: "Prévu pour le sprint en cours"},
      {name: "En cours", color: YELLOW, description: "Quelqu'un travaille dessus"},
      {name: "En revue", color: ORANGE, description: "Pull request ouverte, en attente de relecture"},
      {name: "Terminé",  color: GREEN,  description: "Fusionné dans main"}
    ]
  }) { projectV2Field { ... on ProjectV2SingleSelectField { id } } }
}
'@
    # La requête passe par un fichier : Windows PowerShell 5.1 abîme les guillemets
    # d'un long texte passé directement en argument à un programme externe.
    $fichierRequete = Join-Path $env:TEMP "mboa-colonnes.graphql"
    [System.IO.File]::WriteAllText($fichierRequete, $mutation, (New-Object System.Text.UTF8Encoding $false))
    gh api graphql -F query=@$fichierRequete -f id=$($statut.id) --silent
    Remove-Item $fichierRequete -ErrorAction SilentlyContinue
    if ($LASTEXITCODE -eq 0) { Write-Host "  + Colonnes : $($colonnes -join ', ')" }
    else { Write-Host "  ! Colonnes non modifiées : ajoutez-les à la main dans le champ Status." -ForegroundColor Yellow }
    $champs = gh project field-list $numeroProjet --owner $Proprietaire --format json | ConvertFrom-Json
    $statut = $champs.fields | Where-Object { $_.name -eq "Status" } | Select-Object -First 1
}
$idProjet = $projet.id
$options = @{}
foreach ($o in $statut.options) { $options[$o.name] = $o.id }

# ---------------------------------------------------------------- 5. Backlog
Write-Host "5/6 Création du backlog et répartition des tâches..." -ForegroundColor Green
$F = "type: fonctionnalité"; $T = "type: technique"; $Doc = "type: documentation"
$MCP = "module: mcp-server"; $ORC = "module: orchestrateur"; $WEB = "module: web"
$INF = "module: infra"; $MOB = "module: mobile"; $HAUTE = "priorité: haute"

# r = responsable(s) : L = Lustrelle, M = MENARD, D = Didier
# s = colonne de départ ; fait = $true pour ce qui est déjà réalisé (l'issue est fermée)
$backlog = @(
    # Sprint 1 : socle
    @{ t = "[Infra] Structure Maven multi-modules et Maven Wrapper";         l = @($T, $INF);         m = $S1; r = "L";   s = "Terminé";  fait = $true;  b = "Modules commun, serveur MCP, orchestrateur, interface React. Build : .\mvnw.cmd clean test." },
    @{ t = "[Infra] Pipeline CI/CD GitHub Actions";                          l = @($T, $INF);         m = $S1; r = "L";   s = "Terminé";  fait = $true;  b = "ci.yml (tests Java avec PostgreSQL, tests React, images Docker) et cd.yml (ghcr.io, Render)." },
    @{ t = "[Infra] Protéger la branche main";                                l = @($T, $INF, $HAUTE); m = $S1; r = "L";   s = "À faire";  fait = $false; b = "Settings > Branches : pull request obligatoire, 1 approbation, CI verte avant fusion." },
    @{ t = "[Infra] Installer le poste et réussir le build en local";         l = @($T);               m = $S1; r = "LMD"; s = "En cours"; fait = $false; b = "Chaque membre : JDK 21, Node 24 LTS, Git, VS Code. Puis .\mvnw.cmd clean test (29 tests) et npm test dans mboa-web. Cocher son nom en commentaire une fois réussi." },
    @{ t = "[BD] Créer la base PostgreSQL locale et appliquer Flyway";        l = @($T, $MCP);         m = $S1; r = "D";   s = "À faire";  fait = $false; b = "CREATE USER mboa, CREATE DATABASE mboagouvia, puis lancer le serveur MCP : les migrations V1 et V2 s'appliquent." },
    @{ t = "[BD] Valider le schéma Flyway V1 avec le MLD";                    l = @($T, $MCP);         m = $S1; r = "D";   s = "À faire";  fait = $false; b = "Comparer V1__init.sql au MLD du dossier UML (13 tables, contraintes, ON DELETE CASCADE)." },
    @{ t = "[CU-01] S'inscrire (citoyen ou entreprise)";                      l = @($F, $ORC);         m = $S1; r = "M";   s = "Terminé";  fait = $true;  b = "API POST /api/auth/inscription, consentement obligatoire, e-mail unique." },
    @{ t = "[CU-02] S'authentifier (JWT, verrouillage après 5 échecs)";       l = @($F, $ORC);         m = $S1; r = "M";   s = "Terminé";  fait = $true;  b = "API POST /api/auth/connexion : 401 neutre, 423 si verrouillé, 200 + jeton." },
    @{ t = "[Web] Écran de connexion";                                        l = @($F, $WEB);         m = $S1; r = "L";   s = "Terminé";  fait = $true;  b = "Maquette 2, messages d'erreur 401 et 423." },

    # Sprint 2 : assistant
    @{ t = "[CU-04] Poser une question à l'assistant (ChatClient + MCP)";    l = @($F, $ORC);         m = $S2; r = "M";   s = "Terminé";  fait = $true;  b = "Quotas 10/100 par jour, pseudonymisation, historique transmis à l'IA." },
    @{ t = "[CU-05] Tracer chaque exécution d'outil";                         l = @($F, $MCP);         m = $S2; r = "D";   s = "Terminé";  fait = $true;  b = "Table execution_outil : statut, durée, paramètres." },
    @{ t = "[CU-08] Outil calculer_cotisations_cnps";                         l = @($F, $MCP);         m = $S2; r = "D";   s = "Terminé";  fait = $true;  b = "BigDecimal, taux en vigueur, plafond, erreur si référentiel incomplet." },
    @{ t = "[CU-11] Nouvelle conversation et historique";                     l = @($F, $ORC);         m = $S2; r = "M";   s = "Terminé";  fait = $true;  b = "Conversation et Message enregistrés, l'IA reçoit les 10 derniers messages." },
    @{ t = "[Web] Écran Assistant avec panneau « Outils exécutés »";          l = @($F, $WEB);         m = $S2; r = "L";   s = "Terminé";  fait = $true;  b = "Maquette 3 : fil de discussion gardé, cartes de réponse, outils MCP appelés, FR/EN, mode sombre." },
    @{ t = "[Infra] Clé API Claude : achat, .env local et secret GitHub";     l = @($T, $INF, $HAUTE); m = $S2; r = "L";   s = "À faire";  fait = $false; b = "Acheter des crédits sur console.anthropic.com, créer la clé, la mettre dans .env (jamais commité) et dans Settings > Secrets > Actions sous le nom ANTHROPIC_API_KEY." },
    @{ t = "[Test] Premier échange réel avec l'IA de bout en bout";           l = @($T, $ORC, $HAUTE); m = $S2; r = "M";   s = "Backlog";  fait = $false; b = "Serveur MCP + orchestrateur + interface : la question CNPS doit appeler l'outil et citer la source." },
    @{ t = "[BD] Remplacer les taux CNPS de démonstration par les taux officiels"; l = @($T, $MCP, $HAUTE); m = $S2; r = "D"; s = "À faire"; fait = $false; b = "Nouvelle migration V3 avec les taux publiés par la CNPS et leur source (URL, date de consultation)." },
    @{ t = "[CU-06] Outils rechercher_procedure et lister_categories";        l = @($F, $MCP);         m = $S2; r = "D";   s = "Backlog";  fait = $false; b = "Entités Procedure et PieceJustificative, recherche bilingue." },
    @{ t = "[Web] Écran d'inscription";                                       l = @($F, $WEB);         m = $S2; r = "L";   s = "Backlog";  fait = $false; b = "Citoyen ou entreprise, case de consentement, erreur 409 si e-mail déjà utilisé." },
    @{ t = "[Design] Maquettes Figma et prototype cliquable";                 l = @($Doc, $WEB);       m = $S2; r = "L";   s = "Backlog";  fait = $false; b = "Reprendre les maquettes validées, partager le lien du prototype dans le README." },

    # Sprint 3 : services
    @{ t = "[CU-07] Outils calculer_tva et preparer_declaration_tva";         l = @($F, $MCP);         m = $S3; r = "D";   s = "Backlog";  fait = $false; b = "Taux de TVA du référentiel, brouillon de déclaration." },
    @{ t = "[CU-09] Outil lister_echeances";                                  l = @($F, $MCP);         m = $S3; r = "D";   s = "Backlog";  fait = $false; b = "Echeance.prochaineDate(d), tri par date." },
    @{ t = "[CU-10] Outil consulter_indicateur_public (API Banque mondiale)";  l = @($F, $MCP);         m = $S3; r = "D";   s = "Backlog";  fait = $false; b = "Client REST, gestion des pannes (ERREUR_EXTERNE), test avec WireMock." },
    @{ t = "[CU-14] Programmer un rappel et l'envoyer par e-mail";            l = @($F, $ORC);         m = $S3; r = "M";   s = "Backlog";  fait = $false; b = "Tâche @Scheduled quotidienne, SMTP, statut ENVOYE." },
    @{ t = "[CU-18/19] Gérer et importer des employés (CSV)";                 l = @($F, $ORC);         m = $S3; r = "M";   s = "Backlog";  fait = $false; b = "CRUD Employe, import CSV avec rapport d'erreurs." },
    @{ t = "[CU-20] Back-office du référentiel";                              l = @($F, $ORC, $WEB);   m = $S3; r = "ML";  s = "Backlog";  fait = $false; b = "Rôle ADMIN : sources, procédures, taux (date d'effet), échéances." },
    @{ t = "[CU-15] Préférences langue, thème, taille du texte";              l = @($F, $WEB);         m = $S3; r = "L";   s = "Backlog";  fait = $false; b = "Enregistrer les préférences dans le compte (le mode sombre existe déjà côté écran)." },
    @{ t = "[Infra] Docker : images et docker compose sur les 3 postes";      l = @($T, $INF);         m = $S3; r = "L";   s = "Backlog";  fait = $false; b = "docker compose up --build doit afficher l'interface sur http://localhost:3000." },

    # Sprint 4 : qualité et livraison
    @{ t = "[CU-21/22] Journal des exécutions et statistiques";               l = @($F, $ORC, $WEB);   m = $S4; r = "ML";  s = "Backlog";  fait = $false; b = "Maquette 8, filtres par outil et par statut." },
    @{ t = "[CU-16/17] Exporter et supprimer ses données (loi 2024/017)";     l = @($F, $ORC);         m = $S4; r = "M";   s = "Backlog";  fait = $false; b = "Export JSON, suppression en cascade." },
    @{ t = "[CU-12/13] Exporter une réponse en PDF et la noter";              l = @($F, $ORC, $WEB);   m = $S4; r = "ML";  s = "Backlog";  fait = $false; b = "Activer les boutons « Exporter en PDF » et 👍 👎 de la carte de réponse." },
    @{ t = "[Qualité] Couverture JaCoCo minimale sur les services";           l = @($T);               m = $S4; r = "D";   s = "Backlog";  fait = $false; b = "Activer jacoco:check (objectif 70 % sur les packages service)." },
    @{ t = "[Infra] Déploiement sur Render";                                  l = @($T, $INF);         m = $S4; r = "L";   s = "Backlog";  fait = $false; b = "Créer les services Render, ajouter ANTHROPIC_API_KEY et les deploy hooks dans les secrets." },
    @{ t = "[Doc] Dossier UML à jour et préparation de la soutenance";        l = @($Doc);             m = $S4; r = "LMD"; s = "Backlog";  fait = $false; b = "Exporter les diagrammes de Visual Paradigm, captures de l'application, répétition." },

    # Sprint 5 : Kubernetes et mobile
    @{ t = "[Infra] Kubernetes : manifests et déploiement sur minikube";      l = @($T, $INF);         m = $S5; r = "L";   s = "Backlog";  fait = $false; b = "Deployment et Service pour chaque module, Secret pour ANTHROPIC_API_KEY et JWT_SECRET, PostgreSQL en StatefulSet." },
    @{ t = "[Infra] Kubernetes : déploiement automatique depuis la CI";       l = @($T, $INF);         m = $S5; r = "LD";  s = "Backlog";  fait = $false; b = "Étape kubectl apply dans cd.yml avec les images de ghcr.io." },
    @{ t = "[Mobile] Version installable (PWA)";                              l = @($F, $MOB);         m = $S5; r = "L";   s = "Backlog";  fait = $false; b = "Manifest et service worker : l'interface web s'installe sur le téléphone." },
    @{ t = "[Mobile] Application mobile React Native";                        l = @($F, $MOB);         m = $S5; r = "LM";  s = "Backlog";  fait = $false; b = "Réutilise la même API Spring Boot (jeton JWT)." }
)

$issues = gh issue list --repo $Depot --state all --limit 300 --json title,url,number | ConvertFrom-Json
Verifier "lecture des issues"
$elementsProjet = gh project item-list $numeroProjet --owner $Proprietaire --format json --limit 300 | ConvertFrom-Json
$attributionsManquantes = 0

foreach ($i in $backlog) {
    $existante = $issues | Where-Object { $_.title -eq $i.t } | Select-Object -First 1
    if ($existante) {
        $url = $existante.url
    }
    else {
        $argsLabels = @()
        foreach ($lab in $i.l) { $argsLabels += @("--label", $lab) }
        $url = gh issue create --repo $Depot --title $i.t --body $i.b --milestone $i.m @argsLabels
        Verifier "création de l'issue $($i.t)"
        Write-Host "  + $($i.t)"
    }

    # Attribution aux responsables (échoue tant que l'invitation n'est pas acceptée)
    foreach ($code in $i.r.ToCharArray()) {
        $compte = $Comptes["$code"]
        gh issue edit $url --add-assignee $compte 2>$null | Out-Null
        if ($LASTEXITCODE -ne 0) { $attributionsManquantes++ }
    }

    # Ajout au tableau (une seule fois), puis colonne de départ si la carte n'en a pas encore.
    # Une carte déjà rangée dans une colonne n'est jamais déplacée : on peut relancer le script sans risque.
    $elementTableau = $elementsProjet.items | Where-Object { $_.content.url -eq $url } | Select-Object -First 1
    if (-not $elementTableau) {
        $elementTableau = gh project item-add $numeroProjet --owner $Proprietaire --url $url --format json | ConvertFrom-Json
    }
    $colonneActuelle = $elementTableau.status
    if ((-not $colonneActuelle -or $colonnes -notcontains $colonneActuelle) -and $options.ContainsKey($i.s)) {
        gh project item-edit --id $elementTableau.id --project-id $idProjet --field-id $statut.id --single-select-option-id $options[$i.s] | Out-Null
    }

    if ($i.fait) {
        gh issue close $url --reason completed 2>$null | Out-Null
    }
}

# ---------------------------------------------------------------- 6. Bilan
Write-Host "6/6 Terminé." -ForegroundColor Green
Write-Host ""
Write-Host "Tableau Kanban : $($projet.url)" -ForegroundColor Green
Write-Host "Sprints        : https://github.com/$Depot/milestones" -ForegroundColor Green
if ($attributionsManquantes -gt 0) {
    Write-Host ""
    Write-Host "$attributionsManquantes attribution(s) en attente : MENARD et/ou Didier n'ont pas encore accepté l'invitation." -ForegroundColor Yellow
    Write-Host "Une fois l'invitation acceptée, relancez exactement la même commande." -ForegroundColor Yellow
}
