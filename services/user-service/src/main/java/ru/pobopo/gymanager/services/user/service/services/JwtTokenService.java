package ru.pobopo.gymanager.services.user.service.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.pobopo.gymanager.services.user.service.exception.TokenExpiredException;
import ru.pobopo.gymanager.services.user.service.services.api.UserService;
import ru.pobopo.gymanager.services.user.service.entity.UserEntity;

@Service
public class JwtTokenService {
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 3600000;
    private final String secret;

    private final UserService userService;

    @Autowired
    public JwtTokenService(UserService userService, Environment env) {
        this.userService = userService;
        this.secret = env.getProperty("JWT_SECRET", "littlesecret");
        if(StringUtils.isBlank(secret)){
            throw new RuntimeException("Jwt secret is missing!");
        }
    }

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String login) {
        UserEntity userEntity = userService.getUserByLogin(login);
        Objects.requireNonNull(userEntity);
        return generateToken(userEntity);
    }

    //generate token for user
    public String generateToken(UserEntity userEntity) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userEntity.getLogin());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String validateAndGetLogin(String token) throws TokenExpiredException {
        if (StringUtils.isBlank(token)) {
            throw new JwtException("Token is missing!");
        }
        if (isTokenExpired(token)) {
            throw new TokenExpiredException("Token expired");
        }

        String login = getUsernameFromToken(token);
        if (StringUtils.isBlank(login)) {
            throw new JwtException("Login is blank");
        }
        return login;
    }
}
