package hexlet.code.component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "rsa")
public final class RsaKeyProperties {
    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;
}
