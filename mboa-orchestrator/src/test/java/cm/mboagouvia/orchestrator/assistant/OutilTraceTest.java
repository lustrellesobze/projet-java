package cm.mboagouvia.orchestrator.assistant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

class OutilTraceTest {

    private final TraceOutils trace = new TraceOutils();

    @Test
    void un_appel_reussi_est_note_avec_son_nom_et_ses_parametres() {
        OutilTrace outil = new OutilTrace(faux("calculer_cotisations_cnps", false), trace);

        trace.demarrer();
        String resultat = outil.call("{\"periode\":\"2026-10\"}");
        List<OutilExecute> notes = trace.terminer();

        assertThat(resultat).isEqualTo("ok");
        assertThat(notes).singleElement().satisfies(o -> {
            assertThat(o.nom()).isEqualTo("calculer_cotisations_cnps");
            assertThat(o.parametres()).contains("2026-10");
            assertThat(o.succes()).isTrue();
        });
    }

    @Test
    void un_appel_en_erreur_est_note_comme_echec() {
        OutilTrace outil = new OutilTrace(faux("rechercher_procedure", true), trace);

        trace.demarrer();
        assertThatThrownBy(() -> outil.call("{}")).isInstanceOf(IllegalStateException.class);

        assertThat(trace.terminer()).singleElement().extracting(OutilExecute::succes).isEqualTo(false);
    }

    @Test
    void sans_demarrage_rien_n_est_garde() {
        new OutilTrace(faux("x", false), trace).call("{}");

        assertThat(trace.terminer()).isEmpty();
    }

    private static ToolCallback faux(String nom, boolean echoue) {
        ToolDefinition definition = new ToolDefinition() {
            @Override
            public String name() {
                return nom;
            }

            @Override
            public String description() {
                return "outil de test";
            }

            @Override
            public String inputSchema() {
                return "{}";
            }
        };
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return definition;
            }

            @Override
            public String call(String parametres) {
                if (echoue) {
                    throw new IllegalStateException("erreur de l'outil");
                }
                return "ok";
            }
        };
    }
}
