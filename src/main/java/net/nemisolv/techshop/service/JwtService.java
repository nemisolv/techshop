package net.nemisolv.techshop.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.core.properties.JWTTokenProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {



    private final JWTTokenProperties tokenProperties;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractTokenExpire(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken( UserDetails userDetails) {
        return buildToken(new HashMap<>(),userDetails, tokenProperties.getTokenExpireTime());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(),userDetails, tokenProperties.getRefreshTokenExpireTime());
    }


    public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims,userDetails, tokenProperties.getTokenExpireTime());
    }

    public String generateRefreshToken(Map<String,Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims,userDetails, tokenProperties.getRefreshTokenExpireTime());
    }

    public String generateTokenWithExpire(UserDetails userDetails, long expire) {
        return buildToken(new HashMap<>(),userDetails, expire);
    }




    private String buildToken(Map<String,Object> extraClaims, UserDetails userDetails, long expire) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire) )
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }






    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && extractTokenExpire(token).after(new Date());
    }

    private Key getSignInKey() {
        byte [] keyBytes = Decoders.BASE64.decode(tokenProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);

    }

}