package com.example.kongjavajwttest.service;

import com.example.kongjavajwttest.domain.AppJwtCredential;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtAuthService {

    private Map<String, String> secrets = new HashMap<>();

    private SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
        @Override
        public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
            return TextCodec.BASE64.decode(secrets.get(header.getAlgorithm()));
        }
    };

    @Getter
    private AppJwtCredential.AppJwtCredentialBuilder jwtCredentialBuilder;

    @PostConstruct
    public void setup() {
        generateSecrets();
        jwtCredentialBuilder = AppJwtCredential.builder()
                .algorithm(SignatureAlgorithm.HS256.getValue())
                .secret(getHS256Secret());
    }

    public String createJwt(String aud) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setAudience(aud)
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS256, getHS256SecretBytes())
                .compact();
    }

    public void verifyJwtAuth(String jwtAuth, long userId) {
        String token;
        Jws<Claims> jws;
        //Authentication (redundant since the same procedure are done on Kong)
        try {
            token = jwtAuth.substring("Bearer".length()).trim();
            jws = Jwts.parser()
                    .setSigningKeyResolver(getSigningKeyResolver())
                    .parseClaimsJws(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        //Authorization
        try {
            if (!String.valueOf(userId).equals(jws.getBody().getAudience())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN");
        }
    }
    
    public SigningKeyResolver getSigningKeyResolver() {
        return signingKeyResolver;
    }

    private byte[] getHS256SecretBytes() {
        return TextCodec.BASE64.decode(secrets.get(SignatureAlgorithm.HS256.getValue()));
    }

    private String getHS256Secret() {
        return TextCodec.BASE64.decodeToString(secrets.get(SignatureAlgorithm.HS256.getValue()));
    }

    private Map<String, String> generateSecrets() {
        secrets.put(SignatureAlgorithm.HS256.getValue(), TextCodec.BASE64.encode("yWheEwm53xZox7VGArEi2V9zSJNXkrh7".getBytes()));
        return secrets;
    }
}
