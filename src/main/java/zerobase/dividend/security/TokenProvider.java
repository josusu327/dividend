package zerobase.dividend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.dividend.service.MemberService;

import java.util.Date;
import java.util.List;

@Slf4j
@Component  // ✅ @Component 추가하여 빈으로 등록
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성 (발급)
     */
    public String generateToken(String username, List<String> roles) {
        log.info("토큰 생성 시작: 사용자 - {}, 역할 - {}", username, roles); // 로그 추가

        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        String token = null;
        try {
            token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now) // 토큰 생성 시간
                    .setExpiration(expiredDate) // 토큰 만료 시간
                    .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
                    .compact();
            log.info("토큰 생성 완료: {}", token); // 토큰 발급 완료 후 로그 추가
        } catch (Exception e) {
            log.error("토큰 생성 중 오류 발생: ", e);
        }

        return token;
    }

    /**
     * JWT 에서 사용자 정보 가져오기
     */
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUserName(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserName(String token) {
        return this.parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        log.info("* 토큰 유효성 검사 시작");
        if (!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date()); //토큰 이 유효한지 (만료시간이 현재보다 이전인지 아닌지)
    }

    /**
     * JWT 파싱
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
