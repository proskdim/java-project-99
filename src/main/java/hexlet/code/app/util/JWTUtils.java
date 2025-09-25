package hexlet.code.app.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public final class JWTUtils {

    @Autowired
    private JwtEncoder encoder;

    public String generateToken(String username) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("self").issuedAt(now).expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(username).build();
        var params = JwtEncoderParameters.from(claims);
        return encoder.encode(params).getTokenValue();
    }
}
