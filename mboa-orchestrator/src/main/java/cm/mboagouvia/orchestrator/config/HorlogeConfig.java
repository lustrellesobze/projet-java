package cm.mboagouvia.orchestrator.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Horloge injectable : en test, on peut figer le temps pour vérifier le verrouillage. */
@Configuration
public class HorlogeConfig {

    @Bean
    Clock horloge() {
        return Clock.system(ZoneId.of("Africa/Douala"));
    }
}
