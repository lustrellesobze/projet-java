package cm.mboagouvia.mcp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cm.mboagouvia.common.cotisation.LigneCotisation;
import cm.mboagouvia.common.cotisation.LigneSalaire;
import cm.mboagouvia.common.cotisation.ResultatCotisations;
import cm.mboagouvia.common.erreur.ReferentielIncompletException;
import cm.mboagouvia.mcp.domain.Taux;
import cm.mboagouvia.mcp.domain.TypeTaux;
import cm.mboagouvia.mcp.repository.TauxRepository;

/**
 * Calcul des cotisations CNPS. Tout est fait en BigDecimal, jamais en double,
 * avec les taux en vigueur au premier jour de la période demandée.
 */
@Service
public class CotisationService {

    private static final BigDecimal CENT = BigDecimal.valueOf(100);

    private final TauxRepository tauxRepository;

    public CotisationService(TauxRepository tauxRepository) {
        this.tauxRepository = tauxRepository;
    }

    @Transactional(readOnly = true)
    public ResultatCotisations calculer(YearMonth periode, List<LigneSalaire> salaires) {
        if (salaires == null || salaires.isEmpty()) {
            throw new IllegalArgumentException("La liste des salariés est vide.");
        }
        LocalDate date = periode.atDay(1);
        List<Taux> tauxCnps = chargerTauxCnps(date);

        List<LigneCotisation> lignes = new ArrayList<>();
        BigDecimal totalEmployeur = BigDecimal.ZERO;
        BigDecimal totalSalarie = BigDecimal.ZERO;

        for (LigneSalaire salaire : salaires) {
            if (salaire.salaireBrut() == null || salaire.salaireBrut().signum() <= 0) {
                throw new IllegalArgumentException("Salaire brut invalide pour " + salaire.libelle() + ".");
            }
            BigDecimal partEmployeur = BigDecimal.ZERO;
            BigDecimal partSalarie = BigDecimal.ZERO;
            for (Taux taux : tauxCnps) {
                BigDecimal montant = taux.assiette(salaire.salaireBrut())
                        .multiply(taux.getValeur())
                        .divide(CENT, 0, RoundingMode.HALF_UP);
                if (taux.getType().estPartSalariale()) {
                    partSalarie = partSalarie.add(montant);
                } else {
                    partEmployeur = partEmployeur.add(montant);
                }
            }
            lignes.add(new LigneCotisation(salaire.libelle(), salaire.salaireBrut(), partEmployeur, partSalarie));
            totalEmployeur = totalEmployeur.add(partEmployeur);
            totalSalarie = totalSalarie.add(partSalarie);
        }

        return new ResultatCotisations(periode.toString(), lignes, totalEmployeur, totalSalarie,
                citerSources(tauxCnps));
    }

    private List<Taux> chargerTauxCnps(LocalDate date) {
        List<Taux> resultat = new ArrayList<>();
        Arrays.stream(TypeTaux.values())
                .filter(TypeTaux::estCnps)
                .forEach(type -> resultat.add(tauxRepository.trouverEnVigueur(type, date)
                        .orElseThrow(() -> new ReferentielIncompletException(
                                "Aucun taux " + type + " en vigueur au " + date
                                        + ". Le référentiel doit être complété par un administrateur."))));
        return resultat;
    }

    private String citerSources(List<Taux> taux) {
        Set<String> sources = new LinkedHashSet<>();
        taux.forEach(t -> sources.add(t.getSource().libelleCitation()));
        return String.join(" ; ", sources);
    }
}
