package com.club.events_dashboard.security;

import com.club.events_dashboard.constants.Role;
import com.club.events_dashboard.entity.User;
import com.club.events_dashboard.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    private final long jwtExpirationMs = 86400000; // 1 day

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    //Claim Generation Methods
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Role extractRole(String token) {
        String roleString = extractClaim(token, claims -> claims.get("role", String.class));
        if (roleString == null) {
            throw new IllegalArgumentException("Role is missing in token");
        }
        return Role.valueOf(roleString); // converts "SUPER_ADMIN" -> Role.SUPER_ADMIN
    }
    
    public Long extractClubId(String token) {
    return extractClaim(token, claims -> {
        Integer clubId = claims.get("clubId", Integer.class);
        return clubId != null ? clubId.longValue() : null;
    });
}

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    //Token Generation Methods
    public String generateToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Map<String, Object> claims = new HashMap<>();
        if(user.getRole() != null) {
            claims.put("role", user.getRole().name()); // Role must be non-null
            if (user.getRole() == Role.CLUB_ADMIN && user.getClub() != null) {
                claims.put("clubId", user.getClub().getId());
                claims.put("clubName", user.getClub().getName());
            }
        }
        return createToken(claims, email);
    }

    // public String generateTokenForGuest(String email, Role role) {
    //     Map<String, Object> claims = new HashMap<>();
    //     claims.put("role", role.name());
    //     return createToken(claims, email);
    // }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //Token Validation Methods
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
}
