-- =====================================================================
-- DONNÉES DE DÉMONSTRATION : NE SONT PAS DES VALEURS OFFICIELLES.
-- Elles permettent de faire tourner les calculs pendant le développement.
-- Avant toute mise en production, l'administrateur remplace ces lignes par
-- les taux et plafonds publiés par la CNPS, avec la vraie source.
-- =====================================================================

INSERT INTO source_officielle (intitule, url, organisme, date_consultation)
VALUES ('Barème de démonstration (à remplacer par le barème officiel)', NULL, 'CNPS', DATE '2026-01-01');

INSERT INTO taux (type, valeur, plafond, date_effet, date_fin, source_id)
SELECT t.type, t.valeur, t.plafond, DATE '2026-01-01', NULL, s.id
FROM source_officielle s,
     (VALUES ('CNPS_PENSION_EMPLOYEUR',      4.2000, 750000.00),
             ('CNPS_PENSION_SALARIE',        4.2000, 750000.00),
             ('CNPS_ALLOCATIONS_FAMILIALES', 7.0000, 750000.00),
             ('CNPS_ACCIDENTS_TRAVAIL',      1.7500, 750000.00))
     AS t(type, valeur, plafond)
WHERE s.organisme = 'CNPS';
