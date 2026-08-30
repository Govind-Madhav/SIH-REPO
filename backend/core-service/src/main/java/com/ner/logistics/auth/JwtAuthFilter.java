package com.ner.logistics.auth;

import com.ner.logistics.user.Permission;
import com.ner.logistics.user.User;
import com.ner.logistics.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String email = tokenProvider.getEmailFromToken(jwt);

                User user = userRepository.findByEmail(email)
                        .or(() -> userRepository.findByUsername(email))
                        .orElse(null);

                if (user != null) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    // Add Role Authority (e.g. ROLE_ADMIN, ROLE_LOGISTICS_OPERATOR, ROLE_EMERGENCY_OPERATOR)
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                    // Add Granular Permission Authorities (e.g. USER_MANAGE, SOS_DISPATCH, INCIDENT_REPORT)
                    if (user.getRole().getPermissions() != null) {
                        for (Permission perm : user.getRole().getPermissions()) {
                            authorities.add(new SimpleGrantedAuthority(perm.name()));
                        }
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
