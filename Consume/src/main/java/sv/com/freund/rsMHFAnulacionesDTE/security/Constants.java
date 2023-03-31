package sv.com.freund.rsMHFAnulacionesDTE.security;

/**
 * Clase para el manejo de las constantes utilizadas en la aplicacion
 *
 * @author Sault
 * @since 21/02/2020
 * @version 1.0.0
 */
public class Constants {

    public static final String AUTH_LOGIN_URL = "/login";
    public static final String URL_AUTH_NOT_REQUIRED = "/**";

    // Signing key for HS512 algorithm
    public static final String JWT_SECRET = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjZWF2YWxvcyIsImlhdCI6MTY2Mzc5MTIwOCwiZXhwIjoxNjYzOTY0MDA4fQ.iiJpNhH90KVFEpqkp5l8atlRN5iroak1ge31UMouUwmxON-JCLkjWnHPUPb4DMm80yC2pNk2U3zTM74AahY7Aw";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";
    public static final long TOKEN_EXPIRATION_TIME = 172_800_000; // 48 horas

    // Service for autenticate
    //public static final String URL_LOGIN = "http://freundapp.freundsa.com:9677/rsLoginAD/faces/webresources/login/adLogin/"; //prod
    public static final String URL_LOGIN = "http://freundappt.freundsa.com:9677/rsLoginAD/faces/webresources/login/adLogin/";  //test

    // Time out servicio externo
    public static final int CONN_TIME_OUT_API_EXTERNA = 60000; // 1 MIN
    public static final int READ_TIME_OUT_API_EXTERNA = 60000; // 1 MIN
    public static final int MAX_TOTAL_API_EXTERNA = 1000; // 1 MIN
    
    // Variables para validación de autenticación MHF
    public static final String MENU_MH = "MHF";
    public static final String URL_AUTENTICACION_MHF = "P_URL_LOGIN_TOKEN";
    public static final String URL_RECEPCION_MHF = "P_URL_RECEPCION_MHF";
    public static final String URL_CONSULTA_MHF = "P_URL_CONSULTA_MHF";
    public static final String URL_FIRMADOR_MHF = "P_URL_FIRMADOR_MHF";
    public static final String URL_VSCHEMA_MHF = "P_URL_VSCHEMA_MHF";
    public static final String USER_CLAVE_AUTENTICACION_MHF="LOGIN_MH_FAC_ELECTRONICA";
    
    
    public static final String ESTADO_RECHAZADO= "R";
    public static final String ESTADO_EXITOSO= "A";
    public static final String ESTADO_ERROR= "E";
    public static final Integer ERROR_SCHEMA= 1001;
    public static final Integer ERROR_DTE_FIRMA= 1002;
    public static final Integer ERROR_DTE_RECEPCION= 1003;
    public static final Integer ERROR_DTE_FORBIDDEN= 1011;
    public static final Integer ERROR_DTE_BADREQUEST= 400;
    public static final Integer ERROR_EXCEPTION= 1005;
    public static final Integer PROCESAR_OK= 200;
    public static final String NOMBRE_COLA="P_NOMBRE_COLA";
    public static final String NOMBRE_COLA_CORREO="P_NOMBRE_COLA_CORREO";
    public static final String NOMBRE_FACTORY="P_NOMBRE_FACTORY";
    public static final String SERVIDOR_COLA="P_SERVIDOR_COLA";
    public final static String JNDI_FACTORY= "weblogic.jndi.WLInitialContextFactory";
    // Variables de cola de Anulaciones
    public static final String NOMBRE_COLA_ANULA="P_NOMBRE_COLA_ANULA";
    public static final String NOMBRE_FACTORY_ANULA="P_NOMBRE_FACTORY_ANULA";    
    //
    
    public static final boolean MODE_DEBUG= true;
    
   

    
    private Constants() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}
