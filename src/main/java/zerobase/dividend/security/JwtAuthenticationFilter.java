package zerobase.dividend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer "; //jwt토큰 사용하는 경우 Bearer + 한칸띄고 붙임

    private final TokenProvider tokenProvider;

    @Override //요청이들어오면 필터가 먼저 실행되어 헤더에 토큰있는지 확인 후 유효하면 콘텍스트에 담고, 아니면 바로 실행
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.resolveTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            log.info("토큰 검증 시작: {}", token); // 토큰 검증 시작 로그 추가
            if (this.tokenProvider.validateToken(token)) {
                Authentication auth = this.tokenProvider.getAuthentication(token);
                log.info("인증 정보 설정됨: {}", auth.getName()); // 인증 정보 로그 추가
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("유효하지 않은 토큰: {}", token); // 토큰이 유효하지 않으면 경고 로그
            }
        }

        filterChain.doFilter(request, response); //스프링의 필터
    }


    public String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader((TOKEN_HEADER));

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) { //토큰형태 포함
            return token.substring(TOKEN_PREFIX.length()); //실제토큰 부위
        }

        return null;
    }
}
