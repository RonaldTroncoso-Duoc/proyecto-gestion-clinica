package example.api_gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Aquí podrías añadir configuraciones extra si las necesitaras
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Verificar si la petición trae el encabezado de Authorization
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Falta encabezado de autorización", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            
            // 2. El token suele venir como "Bearer eyJhbGci..."
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            try {

                var claims = Jwts.parserBuilder()
                    .setSigningKey(
                        Keys.hmacShaKeyFor(
                            secret.getBytes(StandardCharsets.UTF_8)
                    )
            )
                    .build()
                    .parseClaimsJws(authHeader)
                    .getBody();

                String username = claims.getSubject();

                Object roles = claims.get("roles");

                log.info("JWT valido para usuario: {}", username);

                ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r.headers(headers -> {

                        headers.add("X-Auth-User", username);

                        headers.add("X-Auth-Roles", roles.toString());

                    }))
                    .build();

                return chain.filter(modifiedExchange);

            } catch (Exception e) {

                log.error("JWT invalido: {}", e.getMessage());

                return onError(
                    exchange,
                    "Token invalido o expirado",
                    HttpStatus.UNAUTHORIZED
    );
}

        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}