package sv.com.freund.rsMHFAnulacionesDTE.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import sv.com.freund.rsMHFAnulacionesDTE.entity.User;
import sv.com.freund.rsMHFAnulacionesDTE.security.CustomAuthenticationProvider;
import sv.com.freund.rsMHFAnulacionesDTE.security.JwtTokenUtil;

/**
 * Controlador para validar la session
 *
 * @author Sault
 * @since 21/02/2020
 * @version 1.0.0
 */
@ApiIgnore
@RestController
@CrossOrigin
public class JwtAuthenticationController {

    static Logger log = Logger.getLogger(JwtAuthenticationController.class);

    @Autowired
    private CustomAuthenticationProvider authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Metodo para autenticar el usuario
     *
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) throws Exception {
        try {
            log.trace("JwtAuthenticationController.createAuthenticationToken.username -->" + user.getUser());
            authenticate(user.getUser(), user.getPwd());
            final String token = authenticate(user.getUser(), user.getPwd());
            log.trace("JwtAuthenticationController.createAuthenticationToken.token -->" + token);
            return ResponseEntity.ok(new User(user.getUser(), null, token));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new User(user.getUser(), null, e.getMessage()));
        }
    }

    /**
     * Funcion para validar si el usuario es válido, si es asi retorna el token,
     * sino se lanza la exception AuthenticationException
     *
     * @param username
     * @param password
     * @return token
     * @throws AuthenticationException
     */
    private String authenticate(String username, String password) throws AuthenticationException {
        String token = "";
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication auth = authenticationManager.authenticate(authReq);
            if (auth.isAuthenticated()) {
                token = jwtTokenUtil.generateToken(username);
            }
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);

        } catch (DisabledException e) {
            log.trace("JwtAuthenticationController.authenticate.DisabledException --> " + e.toString());
            throw new AuthenticationException("USER_DISABLED", e) {
            };
        } catch (BadCredentialsException e) {
            log.trace("JwtAuthenticationController.authenticate.BadCredentialsException --> " + e.toString());
            throw new AuthenticationException("INVALID_CREDENTIALS", e) {
            };
        }

        return token;
    }
}
