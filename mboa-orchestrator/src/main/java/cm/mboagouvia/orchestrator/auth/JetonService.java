package cm.mboagouvia.orchestrator.auth;

import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import cm.mboagouvia.orchestrator.compte.Utilisateur;
import cm.mboagouvia.orchestrator.config.MboaProperties;

/** Émet le jeton JWT d'accès : l'identifiant de l'usager (sub), son rôle et sa langue. */
@Service
public class JetonService {

    private final JwtEncoder encoder;
    private final MboaProperties proprietes;

    public JetonService(JwtEncoder encoder, MboaProperties proprietes) {
        this.encoder = encoder;
        this.proprietes = proprietes;
    }

    public String emettre(Utilisateur utilisateur) {
        Instant maintenant = Instant.now();
        JwtClaimsSet revendications = JwtClaimsSet.builder()
                .issuer("mboa-gouvia")
                .subject(String.valueOf(utilisateur.getId()))
                .issuedAt(maintenant)
                .expiresAt(maintenant.plus(proprietes.jwtDuree()))
                .claim("role", utilisateur.role())
                .claim("langue", utilisateur.getLangue().name())
                .build();
        JwsHeader entete = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(entete, revendications)).getTokenValue();
    }
}
