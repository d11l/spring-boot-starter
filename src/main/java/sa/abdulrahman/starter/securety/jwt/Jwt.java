package sa.abdulrahman.starter.securety.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sa.abdulrahman.starter.models.CustomUserDetails;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@Slf4j
public class Jwt {
     @Value("${jwt.secret}")
     private String secretKey;
     private static Key key;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    @PostConstruct
    public void init() {
        log.info("Initializing JWT signing key");
        key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        log.info("JWT signing key initialized successfully");
    }

    public static Key getKey() {
        return key;
    }

    private String generateToken(Authentication authentication, List<String> roles, long expirationMs) {
        String username = authentication.getName();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, (int) expirationMs);
        Date expireDate = calendar.getTime();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("user_id", ((CustomUserDetails) authentication.getPrincipal()).getId());

        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt( new Date())
                .setExpiration(expireDate)
                .signWith(getKey(),SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), expiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
