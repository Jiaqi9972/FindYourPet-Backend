package me.findthepeach.findyourpet.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 检查受保护的 URI，这里可以包含多个路径
        String requestUri = request.getRequestURI();
        if (isProtectedUrl(requestUri)) {
            String idToken = request.getHeader(WebConstants.AUTHORIZATION_HEADER);

            if (idToken == null || idToken.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Firebase ID-Token");
                return;
            }

            try {
                // 验证 Firebase ID token
                FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken.replace("Bearer ", ""));

                // 获取用户权限
                List<GrantedAuthority> authorities = getAuthoritiesFromToken(token);

                // 设置认证信息到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(
                        new FirebaseAuthenticationToken(idToken, token, authorities));

                SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase ID-Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 添加一个方法来检查是否需要保护该路径
    private boolean isProtectedUrl(String requestUri) {
        return requestUri.startsWith("/api/v1/pets") || requestUri.startsWith("/api/v1/users/updateInfo");
        // 你可以根据需要添加更多路径
    }

    private static List<GrantedAuthority> getAuthoritiesFromToken(FirebaseToken token) {
        Object claims = token.getClaims().get("authorities");
        List<String> permissions = (List<String>) claims;

        List<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;

        if (permissions != null && !permissions.isEmpty()) {
            authorities = AuthorityUtils.createAuthorityList(permissions);
        }
        return authorities;
    }
}
