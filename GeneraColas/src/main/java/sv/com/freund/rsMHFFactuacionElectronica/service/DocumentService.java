/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFFactuacionElectronica.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileOutputStream;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sv.com.freund.rsMHFFactuacionElectronica.repository.ConsultaRepositoryImp;
import sv.com.freund.rsMHFFactuacionElectronica.security.Constants;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.jms.*;
import javax.naming.*;

/**
 * Clase tipo service para el envío de documentos a la plataforma dokmee
 *
 * @author misaelg
 * @since 01/03/2023
 * @version 1.0.0
 */
@Service
public class DocumentService {

    static Logger log = Logger.getLogger(DocumentService.class);

    @Autowired
    ConsultaRepositoryImp consultaServiceImp;

    Gson gson = new Gson();

    JsonParser parser = new JsonParser();

    PostToQueue queuePoster = new PostToQueue();

    HashMap<String, String> params = new HashMap<>();

    @PersistenceContext(unitName = "lecturaDTEWSPU")
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    @Autowired
    private ConsultaRepositoryImp consultaRepositoryImp;
     */
    public String actualizar_estador_dte(String tic_cod_gen, String tic_ban_estado, Integer tin_cod_respuesta, String tic_mensaje_respuesta) {

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("pkg_mhf_procesos_02.prc_actualizacion_estado_documento");

        // Registrar los parámetros de entrada y salida
        storedProcedureQuery.registerStoredProcedureParameter("tic_cod_gen", String.class, ParameterMode.IN.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_ban_estado", String.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tin_cod_respuesta", Integer.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_mensaje_respuesta", String.class, ParameterMode.IN);

        storedProcedureQuery.registerStoredProcedureParameter("toc_respuesta", String.class, ParameterMode.OUT);

        // Configuramos el valor de entrada
        storedProcedureQuery.setParameter("tic_cod_gen", tic_cod_gen);
        storedProcedureQuery.setParameter("tic_ban_estado", tic_ban_estado);
        storedProcedureQuery.setParameter("tin_cod_respuesta", tin_cod_respuesta);
        storedProcedureQuery.setParameter("tic_mensaje_respuesta", tic_mensaje_respuesta);

        // Realizamos la llamada al procedimiento
        storedProcedureQuery.execute();

        // Obtenemos los valores de salida
        String outputValue1 = (String) storedProcedureQuery.getOutputParameterValue("toc_respuesta");

        return outputValue1;
    }
    
    /*
    @Autowired
    private ConsultaRepositoryImp consultaRepositoryImp;
     */
    public String actualizar_estado_anula(String tic_cod_gen, String tic_ban_estado, Integer tin_cod_respuesta, String tic_mensaje_respuesta) {

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("pkg_mhf_procesos_02.prc_actualiza_estado_anulacion");

        // Registrar los parámetros de entrada y salida
        storedProcedureQuery.registerStoredProcedureParameter("tic_cod_gen", String.class, ParameterMode.IN.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_ban_estado", String.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tin_cod_respuesta", Integer.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_mensaje_respuesta", String.class, ParameterMode.IN);

        storedProcedureQuery.registerStoredProcedureParameter("toc_respuesta", String.class, ParameterMode.OUT);

        // Configuramos el valor de entrada
        storedProcedureQuery.setParameter("tic_cod_gen", tic_cod_gen);
        storedProcedureQuery.setParameter("tic_ban_estado", tic_ban_estado);
        storedProcedureQuery.setParameter("tin_cod_respuesta", tin_cod_respuesta);
        storedProcedureQuery.setParameter("tic_mensaje_respuesta", tic_mensaje_respuesta);

        // Realizamos la llamada al procedimiento
        storedProcedureQuery.execute();

        // Obtenemos los valores de salida
        String outputValue1 = (String) storedProcedureQuery.getOutputParameterValue("toc_respuesta");

        return outputValue1;
    }
    

    public void obtenerParametrosFirmar() {
        if (params.isEmpty()) {
            if (Constants.MODE_DEBUG) {
                System.out.println("AutenticacionController.obtenerParametrosFirmar -->");
                log.trace("AutenticacionController.obtenerParametrosFirmar -->");
            }

            String urlFirmador = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.URL_FIRMADOR_MHF);
            String urlValidar = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.URL_VSCHEMA_MHF);
            String nombreCola = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_COLA);
            String nombreFactory = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_FACTORY);
            String servidorCola = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.SERVIDOR_COLA);
            String resp = consultaServiceImp.fnc_buscar_user_passwd(Constants.USER_CLAVE_AUTENTICACION_MHF);
            // Separando el split de la respuesta
            String separador = Pattern.quote("|");
            String[] parts = resp.split(separador);
            String usuario = parts[0];
            String clave = parts[1];
            //Cola de anulaciones
            String nombreColaAnula = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_COLA_ANULA);
            String nombreFactoryAnula = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_FACTORY_ANULA);
            //
            if (Constants.MODE_DEBUG) {
                System.out.println("AutenticacionController.authClient.obtenerParametrosFirmar.urlFirmador -->" + urlFirmador + " usuario " + usuario + " clave " + clave + " nombreCola " + nombreCola + " urlValidar " + urlValidar);
                log.trace("AutenticacionController.obtenerParametrosFirmar.urlFirmador -->" + urlFirmador + " usuario " + usuario + " clave " + clave + " nombreCola " + nombreCola + " urlValidar " + urlValidar);
            }
            params.put("urlFirmar", urlFirmador);
            params.put("urlValidarSchema", urlValidar);
            params.put("usuario", usuario);
            params.put("clave", clave);
            params.put("cola", nombreCola);
            params.put("factory", nombreFactory);
            params.put("servercola", servidorCola);
            params.put("factoryAnula", nombreFactoryAnula);
            params.put("servercolaAnula", nombreColaAnula);
        }
        //return params;
    }

    public String firmarDocumento(String dte, String codgeneracion, String codtd) {
        obtenerParametrosFirmar();
        String status = "";
        String body = "";
        JsonObject rsJson = new JsonObject();
        JsonObject jsRespuesta = new JsonObject();
        jsRespuesta.addProperty("codigogeneracion", codgeneracion);
        rsJson.addProperty("idEnvio", 1);
        rsJson.addProperty("codigoGeneracion", codgeneracion);
        rsJson.addProperty("tipoDte", codtd);
        String json = "{"
                + "  \"passwordPub\": \"" + params.get("clave").trim() + "\","
                + "  \"passwordPri\": \"" + params.get("clave").trim() + "\","
                + "  \"nit\": \"" + params.get("usuario") + "\","
                + "  \"nombreDocumento\": \"NIT\",\n"
                + "  \"nombreFirma\": \"06140108580017.crt\","
                + "  \"dteJson\": " + dte + ","
                + "  \"activo\": true,"
                + "  \"compactSerialization \": null,"
                + "  \"dte\": null"
                + "}";

        String respuesta = "";
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        String ambiente = jsonObject.getAsJsonObject("dteJson").getAsJsonObject("identificacion").get("ambiente").getAsString();
        System.err.println("El valor del ambiente es: " + ambiente);
        rsJson.addProperty("ambiente", ambiente);
        String version = jsonObject.getAsJsonObject("dteJson").getAsJsonObject("identificacion").get("version").getAsString();
        System.err.println("El valor del version es: " + version);
        rsJson.addProperty("version", version);
        ResponseEntity<?> response = null;
        if (Constants.MODE_DEBUG) {
            System.out.println("DocumentService.firmarDocumento.jsonRequest-->" + json);
            log.trace("DocumentService.firmarDocumento.jsonRequest -->" + json);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        try {
            System.out.println("Realizara el firmado-->");
            log.trace("Realizara el firmado -->");
            response = restTemplate.exchange(params.get("urlFirmar"), HttpMethod.POST, entity, Object.class);
            if (Constants.MODE_DEBUG) {
                System.err.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                System.err.println("Response =" + response.getBody());
                log.trace("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                log.trace("Response =" + response.getBody());
            }
            if (response.getStatusCode() == HttpStatus.OK) {
                if (Constants.MODE_DEBUG) {
                    System.out.println("DocumentService.firmarDocumento.response -->" + response);
                    log.trace("DocumentService.firmarDocumento.response -->" + response);
                }
                String strJson = gson.toJson(response.getBody());
                JsonObject objectJson = parser.parse(strJson).getAsJsonObject();
                JsonObject dteObject = parser.parse(dte).getAsJsonObject();
                status = objectJson.get("status").getAsString();
                body = objectJson.get("body").getAsString();
                if (status.equalsIgnoreCase("OK")) {
                    if (Constants.MODE_DEBUG) {
                        System.err.println("Se va ingresar a la cola -->" + jsRespuesta.toString());
                        log.trace("Se va ingresar a la cola -->" + jsRespuesta.toString());
                    }
                    rsJson.addProperty("documento", body);
                    jsRespuesta.add("dte", rsJson);
                    jsRespuesta.add("dteOrigen", dteObject);
                    respuesta = setearCola(jsRespuesta.toString(), params.get("cola"), params.get("factory"), params.get("servercola"));
                } else {
                    if (Constants.MODE_DEBUG) {
                        System.out.println("El status cod es -->" + status);
                        log.trace("El status cod es -->" + status);
                    }
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + actualizar_estador_dte(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_FIRMA, strJson);
                }
                //return response;
            } else {
                if (Constants.MODE_DEBUG) {
                    System.out.println("El status cod es -->" + response.getStatusCode());
                    log.trace("El status cod es -->" + response.getStatusCode());
                }
                respuesta = Constants.ESTADO_RECHAZADO + "|" + actualizar_estador_dte(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_FIRMA, response.getBody().toString());
            }
            //return response;
        } catch (Exception e) {
            System.err.println("Error en la invocacion :" + e.getMessage());
            log.error(" error en DocumentService.firmarDocumento", e);
            respuesta = Constants.ESTADO_RECHAZADO + "|" + actualizar_estador_dte(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_EXCEPTION, e.getMessage());
            //response=null;
            //return ResponseEntity.unprocessableEntity().body(e);
            //return response;
        }
        return respuesta;
    }

    public String firmarDocumentoAnula(String dte, String codgeneracion, String codtd) {
        obtenerParametrosFirmar();
        String status = "";
        String body = "";
        JsonObject rsJson = new JsonObject();
        JsonObject jsRespuesta = new JsonObject();
        jsRespuesta.addProperty("codigogeneracion", codgeneracion);
        rsJson.addProperty("idEnvio", 1);
        rsJson.addProperty("codigoGeneracion", codgeneracion);
        rsJson.addProperty("tipoDte", codtd);
        String json = "{"
                + "  \"passwordPub\": \"" + params.get("clave").trim() + "\","
                + "  \"passwordPri\": \"" + params.get("clave").trim() + "\","
                + "  \"nit\": \"" + params.get("usuario") + "\","
                + "  \"nombreDocumento\": \"NIT\",\n"
                + "  \"nombreFirma\": \"06140108580017.crt\","
                + "  \"dteJson\": " + dte + ","
                + "  \"activo\": true,"
                + "  \"compactSerialization \": null,"
                + "  \"dte\": null"
                + "}";

        String respuesta = "";
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        String ambiente = jsonObject.getAsJsonObject("dteJson").getAsJsonObject("identificacion").get("ambiente").getAsString();
        System.err.println("El valor del ambiente es: " + ambiente);
        rsJson.addProperty("ambiente", ambiente);
        String version = jsonObject.getAsJsonObject("dteJson").getAsJsonObject("identificacion").get("version").getAsString();
        System.err.println("El valor del version es: " + version);
        rsJson.addProperty("version", version);
        ResponseEntity<?> response = null;
        if (Constants.MODE_DEBUG) {
            System.out.println("DocumentService.firmarDocumento.jsonRequest-->" + json);
            log.trace("DocumentService.firmarDocumento.jsonRequest -->" + json);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> entity = new HttpEntity<String>(json, headers);

        try {
            if (Constants.MODE_DEBUG) {
               System.out.println("Realizara el firmado-->");
               log.trace("Realizara el firmado -->");
            }
            //
            response = restTemplate.exchange(params.get("urlFirmar"), HttpMethod.POST, entity, Object.class);
            if (Constants.MODE_DEBUG) {
                System.err.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                System.err.println("Response =" + response.getBody());
                log.trace("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                log.trace("Response =" + response.getBody());
            }
            if (response.getStatusCode() == HttpStatus.OK) {
                if (Constants.MODE_DEBUG) {
                    System.out.println("DocumentService.firmarDocumento.response -->" + response);
                    log.trace("DocumentService.firmarDocumento.response -->" + response);
                }
                String strJson = gson.toJson(response.getBody());
                JsonObject objectJson = parser.parse(strJson).getAsJsonObject();
                JsonObject dteObject = parser.parse(dte).getAsJsonObject();
                status = objectJson.get("status").getAsString();
                body = objectJson.get("body").getAsString();
                if (status.equalsIgnoreCase("OK")) {
                    if (Constants.MODE_DEBUG) {
                        System.err.println("Se va ingresar a la cola -->" + jsRespuesta.toString());
                        log.trace("Se va ingresar a la cola -->" + jsRespuesta.toString());
                    }
                    rsJson.addProperty("documento", body);
                    jsRespuesta.add("dte", rsJson);
                    jsRespuesta.add("dteOrigen", dteObject);
                    //
                      
            
                    if (Constants.MODE_DEBUG) {
                        log.trace("params.toString(): "+params.toString());
                        log.trace(" servercolaAnula: "+params.get("servercolaAnula")+ " factoryAnula: " + params.get("factoryAnula")+ " servercola: " +params.get("servercola")  );
                        log.trace(" jsRespuesta.toString(): "+jsRespuesta.toString() );
                    }
                    respuesta = setearColaAnula(jsRespuesta.toString(), params.get("servercolaAnula"), params.get("factoryAnula"), params.get("servercola"));
                    //
                } else {
                    if (Constants.MODE_DEBUG) {
                        System.out.println("El status cod es -->" + status);
                        log.trace("El status cod es -->" + status);
                    }
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + actualizar_estado_anula(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_FIRMA, strJson);
                }
                //return response;
            } else {
                if (Constants.MODE_DEBUG) {
                    System.out.println("El status cod es -->" + response.getStatusCode());
                    log.trace("El status cod es -->" + response.getStatusCode());
                }
                respuesta = Constants.ESTADO_RECHAZADO + "|" + actualizar_estado_anula(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_FIRMA, response.getBody().toString());
            }
            //return response;
        } catch (Exception e) {
            System.err.println("Error en la invocacion :" + e.getMessage());
            log.error(" error en DocumentService.firmarDocumento", e);
            respuesta = Constants.ESTADO_RECHAZADO + "|" + actualizar_estado_anula(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_EXCEPTION, e.getMessage());
            //response=null;
            //return ResponseEntity.unprocessableEntity().body(e);
            //return response;
        }
        return respuesta;
    }
    
    
    public String validarSchema(String json, String tDoc) {
        obtenerParametrosFirmar();
        String respuesta = "";
        if (Constants.MODE_DEBUG) {
            System.out.println("DocumentService.validarSchema.jsonRequest-->" + json);
            log.trace("DocumentService.validarSchema.jsonRequest -->" + json);
        }
        ResponseEntity<?> response = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        Map<String, String> uriVariables = new HashMap<>();
           
        uriVariables.put("tDoc", tDoc);
        try {
            System.out.println("Realizara la validacion SCHEMA-->");
            log.trace("Realizara la validacion SCHEMA -->");
            response = restTemplate.exchange(params.get("urlValidarSchema").concat("/").concat(tDoc), HttpMethod.POST, entity, String.class);
            if (Constants.MODE_DEBUG) {
                System.err.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                System.err.println("Response =" + response.getBody());
                log.trace("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                log.trace("Response =" + response.getBody());
            }
            if (response.getStatusCode() == HttpStatus.OK) {
                respuesta = "OK, " + response.getBody();
            } else {
                respuesta = "ERROR, " + response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Ocurrio una excepcion al validar el schema " + e.getMessage());
            log.error("Ocurrio una excepcion al validar el schema " + e.getMessage());
            respuesta = "ERROR, " + e.getMessage();
        }
        return respuesta;
    }

    /*
    public String setearCola(String jsonResponse, String nombreCola) {
        String respuesta = "";
        try {
            QueueConnectionFactory qconFactory;
            QueueConnection qcon;
            QueueSession qsession;
            QueueSender qsender;
            Queue queue;
            TextMessage msg;

            InitialContext ic = new InitialContext();

            qconFactory = (QueueConnectionFactory) ic.lookup("javax.jms.QueueConnectionFactory");
            qcon = qconFactory.createQueueConnection();
            qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            queue = (Queue) ic.lookup(nombreCola);

            qsender = qsession.createSender(queue);
            msg = qsession.createTextMessage();
            qcon.start();

            msg.setText(jsonResponse);
            qsender.send(msg);
            qsender.close();
            qsession.close();
            qcon.close();
            respuesta = Constants.ESTADO_EXITOSO + "|" + "OK, mensaje colocado en la cola";
        } catch (Exception e) {
            respuesta = Constants.ESTADO_RECHAZADO + "|" + "ERROR, " + e.getMessage();
        }
        return respuesta;
    }
     */
    public String setearCola(String jsonResponse, String nombreCola, String factory, String server) {
        String respuesta = "";
        try {
            InitialContext initialContext = queuePoster.getInitialContext(server);
            queuePoster.init(initialContext, nombreCola, factory);
            queuePoster.post(jsonResponse);
            log.trace("------------------------Mensaje enviado a la cola------------------------");
            queuePoster.close();
            respuesta = Constants.ESTADO_EXITOSO + "|" + "OK, mensaje colocado en la cola";
        } catch (Exception e) {
            respuesta = Constants.ESTADO_RECHAZADO + "|" + "ERROR, " + e.getMessage();
        }
        return respuesta;
    }
    
    //Procedimiento para setear la cola de anulaciones 
    public String setearColaAnula(String jsonResponse, 
                                   String nombreCola, 
                                   String factory, 
                                   String server) {
        String respuesta = "";
        try {
            InitialContext initialContext = queuePoster.getInitialContext(server);
            queuePoster.init(initialContext, nombreCola, factory);
            queuePoster.post(jsonResponse);
            log.trace("------------------------Mensaje enviado a la cola------------------------");
            queuePoster.close();
            respuesta = Constants.ESTADO_EXITOSO + "|" + "OK, mensaje colocado en la cola";
        } catch (Exception e) {
            respuesta = Constants.ESTADO_RECHAZADO + "|" + "ERROR, " + e.getMessage();
        }
        return respuesta;
    }

    public ResponseEntity<?> obtenerToken() {

        if (Constants.MODE_DEBUG) {
            System.out.println("AutenticacionController.autenticar -->");
            log.trace("AutenticacionController.autenticar -->");
        }

        String urlLogin = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.URL_AUTENTICACION_MHF);
        String resp = consultaServiceImp.fnc_buscar_user_passwd(Constants.USER_CLAVE_AUTENTICACION_MHF);
        // Separando el split de la respuesta
        String separador = Pattern.quote("|");
        String[] parts = resp.split(separador);
        String usuario = parts[0];
        String clave = parts[1];

        if (Constants.MODE_DEBUG) {
            System.out.println("AutenticacionController.authClient.autenticar.urlLogin -->" + urlLogin + " usuario " + usuario + " clave " + clave);
            log.trace("AutenticacionController.autenticar.urlLogin -->" + urlLogin + " usuario " + usuario + " clave " + clave);
        }
        ResponseEntity<?> response = autenticar(usuario, clave, urlLogin);
        //
        if (Constants.MODE_DEBUG) {
            System.out.println("AutenticacionController.autenticar.response -->" + response);
            log.trace("AutenticacionController.autenticar.response -->" + response);
        }

        return response;
    }

    public ResponseEntity<?> autenticar(String usuario, String clave, String url) {

        if (Constants.MODE_DEBUG) {
            System.out.println("AutenticacionMhImp.usuario-->" + usuario + " Clave: " + clave + " url " + url);
            log.trace("AutenticacionMhImp.usuario -->" + usuario + " Clave: " + clave + " url " + url);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> uriParam = new LinkedMultiValueMap<>();
        uriParam.add("user", usuario);
        uriParam.add("pwd", clave);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<?> entity = new HttpEntity(uriParam, headers);

        try {
            ResponseEntity<?> response = new RestTemplate().exchange(url, HttpMethod.POST, entity, Object.class);
            //
            if (Constants.MODE_DEBUG) {
                System.out.println("AutenticacionMhImp.autenticar.response -->" + response);
                log.trace("AutenticacionMhImp.autenticar.response -->" + response);
            }
            return response;

        } catch (Exception e) {
            log.error(" error en AutenticacionMhImp.autenticar", e);
            return ResponseEntity.unprocessableEntity().body(e);
        }

    }

}
