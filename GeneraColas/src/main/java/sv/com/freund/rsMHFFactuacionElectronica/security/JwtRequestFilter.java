package sv.com.freund.rsMHFFactuacionElectronica.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

/**
 * Clase para filtrar las solicitudes al API Rest
 *
 * @author Sault
 * @since 21/02/2020
 * @version 1.0.0
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    static Logger log = Logger.getLogger(JwtRequestFilter.class);

    @Autowired
    private CustomAuthenticationProvider authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Metodo para filtrar las solicitudes, si no esta autorizado la peticion es
     * rechazada
     *
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Obtiene del encabezado de la solicitud la llave Authorization
        final String requestTokenHeader = request.getHeader(Constants.TOKEN_HEADER);
        String username = null;
        String jwtToken = null;

        // Si en la solicitud viene la llave Authorization y la palabra clave Bearer (usada por jwt)
        if (requestTokenHeader != null && requestTokenHeader.startsWith(Constants.TOKEN_PREFIX)) {
            // Se obtiene el token de la solicitud excluyendo la palaba Bearer 
            jwtToken = requestTokenHeader.substring(7);
            try {
                // Leyendo el usuario del token recibido
                username = jwtTokenUtil.getUserNameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.trace("JwtRequestFilter.doFilterInternal.IllegalArgumentException -->" + e.toString());
            } catch (ExpiredJwtException e) {
                log.trace("JwtRequestFilter.doFilterInternal.ExpiredJwtException -->" + e.toString());
            }
        } 
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtTokenUtil.validateToken(jwtToken, username)) {
                log.trace("JwtRequestFilter.doFilterInternal.validateToken -->" + jwtToken + username);
                
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null);
                usernamePasswordAuthenticationToken.setDetails(jwtToken);
                Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
                SecurityContext sc = SecurityContextHolder.getContext();
                sc.setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
