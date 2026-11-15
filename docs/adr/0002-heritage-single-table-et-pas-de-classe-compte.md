# ADR-0002 : une seule table pour les utilisateurs, pas de classe Compte

- **Date :** 2026-11-03
- **Statut :** acceptée
- **Auteurs :** Lustrelle SOBZE, MENARD EBIANG, Didier Raoul

## Contexte

Citoyen, RepresentantEntreprise et Administrateur partagent l'e-mail, le mot de passe, la langue,
le consentement et le verrouillage. Chaque personne possède exactement un compte.

## Décision

- Classe abstraite `Utilisateur`, héritée par les trois rôles, stockée dans **une seule table**
  `utilisateur` avec une colonne `type` (stratégie JPA `SINGLE_TABLE`).
- Pas de classe `Compte` séparée : la relation Utilisateur 1-1 Compte est fusionnée dans `Utilisateur`.

## Options écartées

- **Une table par classe (JOINED)** : une jointure à chaque connexion, sans bénéfice ici.
- **Classe Compte séparée** : une classe et une jointure sans information nouvelle.

## Conséquences

- Connexion rapide : une seule requête sur `utilisateur`.
- Les colonnes propres à un rôle (`telephone`, `fonction`, `matricule_agent`) sont vides pour les autres.
