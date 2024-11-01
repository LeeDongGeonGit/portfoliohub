package com.example.portfoliohubback.jwt;


import com.example.portfoliohubback.service.UserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private String SECRET_KEY = "qwrmlasfmlqw2491u4291urjeiqowfjkwklfnlksdnfi13fnou3nounf13";
    private final long ACCESS_TOKEN_VALID_TIME = 3000 * 60 * 1000L;
    private final UserDetailService userDetailService;
    public String createToken(String userPk) {
        // 권한 가져오기
        Claims claims = Jwts.claims().setSubject(userPk);  // 정보는 key/value 쌍으로 저장됩니다.
        Date now = new Date();
        // Access Token 생성
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return accessToken;
    }
    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Claims claims = parseJwt(token);
        String s=claims.getSubject();

        UserDetails userDetails = userDetailService.loadUserByUsername(s);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public Claims parseJwt(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(jwt)
                .getBody();

        return claims;
    }
}