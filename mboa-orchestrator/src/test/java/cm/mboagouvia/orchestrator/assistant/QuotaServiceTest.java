package cm.mboagouvia.orchestrator.assistant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import cm.mboagouvia.orchestrator.config.MboaProperties;
import cm.mboagouvia.orchestrator.erreur.QuotaDepasseException;

class QuotaServiceTest {

    private final MboaProperties proprietes = new MboaProperties("x".repeat(32), Duration.ofHours(1), 5,
            Duration.ofMinutes(15), 2, 3, List.of());

    private final QuotaService quota = new QuotaService(proprietes, Clock.systemDefaultZone());

    @Test
    void un_visiteur_est_bloque_au_dela_de_sa_limite() {
        quota.consommer("ip:1.2.3.4", false);
        quota.consommer("ip:1.2.3.4", false);

        assertThatThrownBy(() -> quota.consommer("ip:1.2.3.4", false))
                .isInstanceOf(QuotaDepasseException.class);
    }

    @Test
    void un_usager_connecte_a_une_limite_plus_haute() {
        quota.consommer("u:7", true);
        quota.consommer("u:7", true);

        assertThatCode(() -> quota.consommer("u:7", true)).doesNotThrowAnyException();
    }
}
