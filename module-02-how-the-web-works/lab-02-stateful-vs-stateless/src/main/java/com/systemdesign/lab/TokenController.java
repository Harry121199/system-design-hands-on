package com.systemdesign.lab;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/token")
public class TokenController {
    private static final String SECRET = "ON9R17JessoIL/ripZDpfwKghjUoK2df4mCh0XwByMM=";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));


    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username) {
        String token = Jwts.builder()
                .subject(username)
                .claim("cart", List.of())
                .issuedAt(new Date())
                .signWith(KEY)
                .compact();

        return Map.of("token",token,"message","Logged in as: "+username);
    }

    @PostMapping("/cart/add")
    public Map<String, Object> addToCart(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String item){
        Claims claims = parseToken(authHeader);
        String username = claims.getSubject();
        List<String> cart = (List<String>) claims.get("cart");
        cart.add(item);

        // Build a NEW token with updated cart
        String newToken = Jwts.builder()
                .subject(username)
                .claim("cart",cart)
                .issuedAt(new Date())
                .signWith(KEY)
                .compact();

        return Map.of("token", newToken,"cart",cart,"user",username);
    }
    @GetMapping("/cart")
    public Map<String, Object> viewCart(
            @RequestHeader("Authorization") String authHeader){
        Claims claims = parseToken(authHeader);
        String username = claims.getSubject();
        List<String> cart = (List<String>) claims.get("cart");

        return Map.of("username", username, "cart", cart);
    }

    private Claims parseToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
