package sv.com.freund.rsMHFFactuacionElectronica.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.apache.log4j.Logger;

/**
 * Clase de utilerias para el manejo de tokens jwt
 *
 * @author Sault
 * @since 21/02/2020
 * @version 1.0.0
 */
@Component
public class JwtTokenUtil {

    private static final long serialVersionUID = -2550185165626007488L;

    static Logger log = Logger.getLogger(JwtTokenUtil.class);

    /**
     * Funcion para obterner la fecha de expriracion del token
     *
     * @param token
     * @return Fecha de expiracion
     */
    public Date getExpirationDateFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;

    }

    /**
     * Funcion para validar si un token ha expirado
     *
     * @param token
     * @return true si ha expirado o false si no ha expirado
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        log.trace("JwtTokenUtil.isTokenExpired -->" + expiration);
        return expiration.before(new Date(System.currentTimeMillis()));
    }

    /**
     * Funcion para obtener el username de un token
     *
     * @param token
     * @return username
     */
    public String getUserNameFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Funcion para obtener el body de un token
     *
     * @param token
     * @return
     */
    private Claims getAllClaimsFromToken(String token) {
        Claims body = null;
        try {

            body = Jwts.parser().setSigningKey(Constants.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody();

        } catch (ExpiredJwtException e) {
            log.trace("JwtTokenUtil.getAllClaimsFromToken.ExpiredJwtException -->" + token + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.trace("JwtTokenUtil.getAllClaimsFromToken.UnsupportedJwtException -->" + token + e.getMessage());
        } catch (MalformedJwtException e) {
            log.trace("JwtTokenUtil.getAllClaimsFromToken.MalformedJwtException -->" + token + e.getMessage());
        } catch (SignatureException e) {
            log.trace("JwtTokenUtil.getAllClaimsFromToken.SignatureException -->" + token + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.trace("JwtTokenUtil.getAllClaimsFromToken.IllegalArgumentException -->" + token + e.getMessage());
        }
        return body;
    }

    /**
     * Funcion para obtener el token generado
     * @param userName
     * @return 
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userName);
    }

    /**
     * Funcion para generar el token para cada usuario autenticado
     * @param claims
     * @param subject
     * @return 
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, Constants.JWT_SECRET.getBytes()).compact();
    }

    /**
     * Funcion para validar que el token no haya expirado
     * @param token
     * @param user
     * @return 
     */
    public Boolean validateToken(String token, String user) {
        final String username = getUserNameFromToken(token);
        return (username.equals(user) && !isTokenExpired(token));
    }
}
