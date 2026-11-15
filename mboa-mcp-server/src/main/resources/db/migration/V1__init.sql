-- =====================================================================
-- Mboa Gouvia : schéma initial (13 tables), issu du MLD du dossier UML
-- Héritage Utilisateur : une seule table avec une colonne "type" (SINGLE_TABLE)
-- Compositions : ON DELETE CASCADE
-- =====================================================================

-- ---------- Comptes ----------
CREATE TABLE utilisateur (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type                VARCHAR(20)  NOT NULL CHECK (type IN ('CITOYEN', 'REPRESENTANT', 'ADMIN')),
    email               VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe_hache  VARCHAR(100) NOT NULL,
    nom                 VARCHAR(150) NOT NULL,
    langue              VARCHAR(2)   NOT NULL DEFAULT 'FR' CHECK (langue IN ('FR', 'EN')),
    date_creation       TIMESTAMP    NOT NULL DEFAULT now(),
    actif               BOOLEAN      NOT NULL DEFAULT TRUE,
    theme               VARCHAR(10)  NOT NULL DEFAULT 'CLAIR' CHECK (theme IN ('CLAIR', 'SOMBRE')),
    taille_texte        INTEGER      NOT NULL DEFAULT 100,
    date_consentement   TIMESTAMP    NOT NULL,
    tentatives_echouees INTEGER      NOT NULL DEFAULT 0,
    verrouille_jusqua   TIMESTAMP,
    telephone           VARCHAR(20),   -- Citoyen
    fonction            VARCHAR(100),  -- RepresentantEntreprise
    matricule_agent     VARCHAR(50)    -- Administrateur
);

CREATE TABLE entreprise (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    raison_sociale    VARCHAR(200) NOT NULL,
    niu               VARCHAR(20)  NOT NULL UNIQUE,
    regime_fiscal     VARCHAR(20)  NOT NULL CHECK (regime_fiscal IN ('REEL', 'SIMPLIFIE', 'LIBERATOIRE')),
    ville             VARCHAR(100),
    representant_id   BIGINT       NOT NULL UNIQUE REFERENCES utilisateur (id) ON DELETE CASCADE
);

CREATE TABLE employe (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom                  VARCHAR(150)   NOT NULL,
    salaire_brut_mensuel NUMERIC(15, 2) NOT NULL CHECK (salaire_brut_mensuel > 0),
    date_embauche        DATE,
    actif                BOOLEAN        NOT NULL DEFAULT TRUE,
    entreprise_id        BIGINT         NOT NULL REFERENCES entreprise (id) ON DELETE CASCADE
);

-- ---------- Assistant ----------
CREATE TABLE conversation (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titre           VARCHAR(200),
    date_creation   TIMESTAMP NOT NULL DEFAULT now(),
    date_maj        TIMESTAMP NOT NULL DEFAULT now(),
    utilisateur_id  BIGINT    NOT NULL REFERENCES utilisateur (id) ON DELETE CASCADE
);

CREATE TABLE message (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role             VARCHAR(10) NOT NULL CHECK (role IN ('USER', 'ASSISTANT')),
    contenu          TEXT        NOT NULL,
    langue           VARCHAR(2)  NOT NULL DEFAULT 'FR',
    horodatage       TIMESTAMP   NOT NULL DEFAULT now(),
    conversation_id  BIGINT      NOT NULL REFERENCES conversation (id) ON DELETE CASCADE
);

-- message_id est facultatif : un visiteur n'a pas de conversation enregistrée,
-- mais ses appels d'outils sont tout de même tracés (corrélés par correlation_id).
CREATE TABLE execution_outil (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom_outil       VARCHAR(100) NOT NULL,
    parametres      TEXT,
    resultat        TEXT,
    statut          VARCHAR(30)  NOT NULL
                    CHECK (statut IN ('SUCCES', 'ERREUR_VALIDATION', 'ERREUR_REFERENTIEL', 'ERREUR_EXTERNE')),
    duree_ms        BIGINT       NOT NULL,
    correlation_id  UUID         NOT NULL,
    horodatage      TIMESTAMP    NOT NULL DEFAULT now(),
    message_id      BIGINT       REFERENCES message (id) ON DELETE CASCADE
);

CREATE TABLE retour (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    utile           BOOLEAN   NOT NULL,
    commentaire     TEXT,
    date            TIMESTAMP NOT NULL DEFAULT now(),
    message_id      BIGINT    NOT NULL UNIQUE REFERENCES message (id) ON DELETE CASCADE,
    utilisateur_id  BIGINT    REFERENCES utilisateur (id) ON DELETE SET NULL
);

-- ---------- Référentiel ----------
CREATE TABLE source_officielle (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    intitule           VARCHAR(255) NOT NULL,
    url                VARCHAR(500),
    organisme          VARCHAR(100) NOT NULL,
    date_consultation  DATE         NOT NULL,
    administrateur_id  BIGINT       REFERENCES utilisateur (id) ON DELETE SET NULL
);

CREATE TABLE procedure (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titre_fr     VARCHAR(255) NOT NULL,
    titre_en     VARCHAR(255) NOT NULL,
    categorie    VARCHAR(30)  NOT NULL
                 CHECK (categorie IN ('IDENTITE', 'ETAT_CIVIL', 'JUSTICE', 'CREATION_ENTREPRISE')),
    cout         NUMERIC(15, 2),
    delai_jours  INTEGER,
    lieu         VARCHAR(255),
    date_maj     DATE         NOT NULL DEFAULT CURRENT_DATE,
    source_id    BIGINT       NOT NULL REFERENCES source_officielle (id)
);

CREATE TABLE piece_justificative (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    libelle_fr    VARCHAR(255) NOT NULL,
    libelle_en    VARCHAR(255) NOT NULL,
    obligatoire   BOOLEAN      NOT NULL DEFAULT TRUE,
    ordre         INTEGER      NOT NULL DEFAULT 1,
    procedure_id  BIGINT       NOT NULL REFERENCES procedure (id) ON DELETE CASCADE
);

CREATE TABLE taux (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type        VARCHAR(40)    NOT NULL
                CHECK (type IN ('TVA', 'CNPS_PENSION_EMPLOYEUR', 'CNPS_PENSION_SALARIE',
                                'CNPS_ALLOCATIONS_FAMILIALES', 'CNPS_ACCIDENTS_TRAVAIL')),
    valeur      NUMERIC(7, 4)  NOT NULL CHECK (valeur >= 0),
    plafond     NUMERIC(15, 2),
    date_effet  DATE           NOT NULL,
    date_fin    DATE,
    source_id   BIGINT         NOT NULL REFERENCES source_officielle (id),
    CONSTRAINT taux_periode_valide CHECK (date_fin IS NULL OR date_fin >= date_effet)
);

CREATE TABLE echeance (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    libelle            VARCHAR(255) NOT NULL,
    type_contribuable  VARCHAR(30)  NOT NULL
                       CHECK (type_contribuable IN ('PARTICULIER', 'ENTREPRISE_REEL', 'ENTREPRISE_SIMPLIFIE', 'TOUS')),
    periodicite        VARCHAR(20)  NOT NULL CHECK (periodicite IN ('MENSUELLE', 'TRIMESTRIELLE', 'ANNUELLE')),
    jour_limite        INTEGER      NOT NULL CHECK (jour_limite BETWEEN 1 AND 31),
    source_id          BIGINT       NOT NULL REFERENCES source_officielle (id)
);

CREATE TABLE rappel (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    jours_avant        INTEGER     NOT NULL CHECK (jours_avant >= 0),
    date_envoi_prevue  DATE        NOT NULL,
    statut             VARCHAR(20) NOT NULL DEFAULT 'PROGRAMME' CHECK (statut IN ('PROGRAMME', 'ENVOYE', 'ANNULE')),
    utilisateur_id     BIGINT      NOT NULL REFERENCES utilisateur (id) ON DELETE CASCADE,
    echeance_id        BIGINT      NOT NULL REFERENCES echeance (id) ON DELETE CASCADE
);

-- ---------- Index utiles aux recherches fréquentes ----------
CREATE INDEX idx_taux_type_effet        ON taux (type, date_effet);
CREATE INDEX idx_message_conversation   ON message (conversation_id);
CREATE INDEX idx_execution_horodatage   ON execution_outil (horodatage);
CREATE INDEX idx_rappel_envoi           ON rappel (date_envoi_prevue, statut);
