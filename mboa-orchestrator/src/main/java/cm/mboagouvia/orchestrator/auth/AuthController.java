package cm.mboagouvia.orchestrator.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cm.mboagouvia.orchestrator.auth.AuthDtos.DemandeConnexion;
import cm.mboagouvia.orchestrator.auth.AuthDtos.DemandeInscription;
import cm.mboagouvia.orchestrator.auth.AuthDtos.ReponseConnexion;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public ReponseConnexion inscrire(@Valid @RequestBody DemandeInscription demande) {
        return authService.inscrire(demande);
    }

    @PostMapping("/connexion")
    public ReponseConnexion connecter(@Valid @RequestBody DemandeConnexion demande) {
        return authService.connecter(demande);
    }
}
