package com.zmc.ierbar_web_app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_STRING="O_Cheie_Super_Secreta_Si_Lunga_Pentru_Ierbarel_App_2026!";
    private final SecretKey key=Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    private final long EXPIRATION_TIME=86400000;

    public String genereazaToken(int userId, String email, String tip_user){
        return Jwts.builder().claim("id", userId).claim("tip_user", tip_user).subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(key).compact();
    }
}
