package example.ms_especialidades.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String username = request.getHeader("X-Auth-User");
        String rolesHeader = request.getHeader("X-Auth-Roles");

        if (username != null && rolesHeader != null) {

            log.info("Roles recibidos: {}", rolesHeader);

            List<SimpleGrantedAuthority> authorities =
                    Arrays.stream(
                                    rolesHeader
                                            .replace("[", "")
                                            .replace("]", "")
                                            .split(",")
                            )
                            .map(String::trim)

                            .map(role ->
                                    role.startsWith("ROLE_")
                                            ? role
                                            : "ROLE_" + role
                            )

                            .map(SimpleGrantedAuthority::new)
                            .toList();
                authorities.forEach(a ->
                        log.info("Authority creada: {}", a.getAuthority())
                );

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("Usuario autenticado desde gateway: {}", username);
        }

        filterChain.doFilter(request, response);
    }
}