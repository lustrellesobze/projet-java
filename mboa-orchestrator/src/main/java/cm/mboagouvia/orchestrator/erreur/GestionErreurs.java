package cm.mboagouvia.orchestrator.erreur;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Transforme les exceptions en réponses JSON normalisées (RFC 9457, ProblemDetail). */
@RestControllerAdvice
public class GestionErreurs {

    @ExceptionHandler(AuthException.class)
    ProblemDetail auth(AuthException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getStatut(), ex.getMessage());
    }

    @ExceptionHandler(QuotaDepasseException.class)
    ProblemDetail quota(QuotaDepasseException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(ConversationIntrouvableException.class)
    ProblemDetail conversation(ConversationIntrouvableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
}
