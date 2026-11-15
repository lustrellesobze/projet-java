package cm.mboagouvia.mcp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cm.mboagouvia.mcp.domain.Taux;
import cm.mboagouvia.mcp.domain.TypeTaux;

public interface TauxRepository extends JpaRepository<Taux, Long> {

    @Query("""
            select t from Taux t join fetch t.source
            where t.type = :type
              and t.dateEffet <= :date
              and (t.dateFin is null or t.dateFin >= :date)
            order by t.dateEffet desc
            """)
    List<Taux> findEnVigueur(@Param("type") TypeTaux type, @Param("date") LocalDate date);

    /** Le taux le plus récent en vigueur à la date donnée, s'il existe. */
    default Optional<Taux> trouverEnVigueur(TypeTaux type, LocalDate date) {
        return findEnVigueur(type, date).stream().findFirst();
    }
}
