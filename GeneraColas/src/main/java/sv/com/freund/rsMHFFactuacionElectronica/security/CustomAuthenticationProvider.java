package sv.com.freund.rsMHFFactuacionElectronica.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Clase para customizar el proveedor de autenticacion
 *
 * @author Sault
 * @since 21/02/2020
 * @version 1.0.0
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    static Logger log = Logger.getLogger(JwtTokenUtil.class);

    public CustomAuthenticationProvider() {
        super();
    }

    /**
     * Metodo para autenticar el usuario
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String isValid = "N";
        final String name = authentication.getName(); // Nombre de usuario obligatorio en la atenticacion
        final String token = authentication.getDetails() != null ? authentication.getDetails().toString() : null; // Se recibe el Token desde las otras peticiones, previamente validado
        final String password = authentication.getCredentials() != null ? authentication.getCredentials().toString() : null; // Password, viene vacia de las peticiones diferentes al de Login, pero se valida con el token

        log.trace("CustomAuthenticationProvider.authenticate.name -->" + name);

        //Si el usuario y la contraseña no estan vacias valida mediante usuario LDAP,
        //Si no se valida con el token 
        if (name != null && password != null) {
            isValid = validateSession(name, password);
        } else if (name != null && token != null) {
            isValid = "Y";
        }

        log.trace("CustomAuthenticationProvider.authenticate.isValid -->" + isValid);

        if (isValid.equals("Y")) {
            final List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("RLA_EVC_CLIMA_ORGANIZACIONAL"));
            final UserDetails principal = new User(name, password != null ? password : token, grantedAuths);
            return new UsernamePasswordAuthenticationToken(principal, password != null ? password : token, grantedAuths);
        } else {
            log.trace("CustomAuthenticationProvider.authenticate.BadCredentialsException --> Usuario o contraseña no válida");
            throw new BadCredentialsException("Usuario o contraseña no válida");
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * Se consume Rest Service para validar el usuario
     *
     * @param userName Es el usuario LDAP
     * @param password Es la contraseña LDAP
     * @return String Y o N
     */
    public String validateSession(String userName, String password) {
        String result = "N";
        try {
            URL url = new URL(Constants.URL_LOGIN);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String urlParameters = "username=" + userName + "&password=" + password;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            OutputStream os = conn.getOutputStream();
            os.write(postData);
            os.flush();

            // Se verifica el codigo de respuesta  sino es 200, 201, 202 se lanza exception
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_CREATED:
                    break;
                case HttpURLConnection.HTTP_ACCEPTED:
                    break;
                default:
                    log.trace("CustomAuthenticationProvider.validateSession.getResponseCode --> " + conn.getResponseCode() + conn.getResponseMessage());
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode() + conn.getResponseMessage());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                result = output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            log.trace("CustomAuthenticationProvider.validateSession.MalformedURLException --> " + e.toString());

        } catch (IOException e) {
            log.trace("CustomAuthenticationProvider.validateSession.IOException --> " + e.toString());

        }
        return result;
    }

}
