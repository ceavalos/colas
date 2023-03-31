/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFAnulacionesDTE.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sv.com.freund.rsMHFAnulacionesDTE.repository.ConsultaRepositoryImp;
import sv.com.freund.rsMHFAnulacionesDTE.security.Constants;

import javax.naming.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import static sv.com.freund.rsMHFAnulacionesDTE.service.ReadFromQueue.log;

/**
 * Clase tipo service para el envío de documentos al Ministerio de Hacienda
 *
 * @author misaelg
 * @since 01/03/2023
 * @version 1.0.0
 */
@Service
public class EnvioDTEService {

    static Logger log = Logger.getLogger(EnvioDTEService.class);

    @Autowired
    ConsultaRepositoryImp consultaServiceImp;

    Gson gson = new Gson();

    JsonParser parser = new JsonParser();

    PostToQueue queuePoster = new PostToQueue();

    @Setter
    @Getter
    private HashMap<String, String> params = new HashMap<>();

    @PersistenceContext(unitName = "envioDTEWSPU")
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    @Autowired
    private ConsultaRepositoryImp consultaRepositoryImp;
     */
    public String actualizar_estador_dte(String tic_cod_gen, String tic_ban_estado, Integer tin_cod_respuesta, String tic_mensaje_respuesta) {

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

        if (Constants.MODE_DEBUG) {
          log.trace("EnvioDTEService.actualizar_estador_dte. tic_cod_gen: " + tic_cod_gen + " tic_ban_estado: " + tic_ban_estado + " tin_cod_respuesta: "+tin_cod_respuesta + " tic_mensaje_respuesta: " +tic_mensaje_respuesta );                    
        }        
        // Realizamos la llamada al procedimiento
        storedProcedureQuery.execute();

        // Obtenemos los valores de salida
        String outputValue1 = (String) storedProcedureQuery.getOutputParameterValue("toc_respuesta");
        //
        if (Constants.MODE_DEBUG) {
          log.trace("EnvioDTEService.actualizar_estador_dte.outputValue1: " + outputValue1 );                    
        }     

        return outputValue1;
    }

    public String actualizar_documento_valido(String tic_cod_gen, String tic_ban_estado, Integer tin_cod_respuesta, String tic_mensaje_respuesta, String tic_sello_recepcion) {

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("pkg_mhf_procesos_02.prc_actualizacion_anulacion_valida");

        // Registrar los parámetros de entrada y salida
        storedProcedureQuery.registerStoredProcedureParameter("tic_cod_gen", String.class, ParameterMode.IN.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_ban_estado", String.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tin_cod_respuesta", Integer.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_mensaje_respuesta", String.class, ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("tic_sello_recepcion", String.class, ParameterMode.IN);

        storedProcedureQuery.registerStoredProcedureParameter("toc_respuesta", String.class, ParameterMode.OUT);

        // Configuramos el valor de entrada
        storedProcedureQuery.setParameter("tic_cod_gen", tic_cod_gen);
        storedProcedureQuery.setParameter("tic_ban_estado", tic_ban_estado);
        storedProcedureQuery.setParameter("tin_cod_respuesta", tin_cod_respuesta);
        storedProcedureQuery.setParameter("tic_mensaje_respuesta", tic_mensaje_respuesta);
        storedProcedureQuery.setParameter("tic_sello_recepcion", tic_sello_recepcion);

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
            String urlLogin = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.URL_AUTENTICACION_MHF);
            String urlRecepcion = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.URL_RECEPCION_MHF);
            String urlConsulta = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.URL_CONSULTA_MHF);
            String nombreCola = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_COLA);
            String colaCorreo = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_COLA_CORREO);
            String nombreFactory = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_FACTORY);
            String servidorCola = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.SERVIDOR_COLA);
            String resp = consultaServiceImp.fnc_buscar_user_passwd(Constants.USER_CLAVE_AUTENTICACION_MHF);
            //
            //Cola de anulaciones
            String nombreColaAnula = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_COLA_ANULA);
            String nombreFactoryAnula = consultaServiceImp.findParametrosWl(Constants.MENU_MH, Constants.NOMBRE_FACTORY_ANULA);            
            // Separando el split de la respuesta
            String separador = Pattern.quote("|");
            String[] parts = resp.split(separador);
            String usuario = parts[0];
            String clave = parts[1];
            //
            if (Constants.MODE_DEBUG) {
                System.out.println("AutenticacionController.authClient.obtenerParametrosFirmar.urlRecepcion -->" + urlRecepcion + " usuario " + usuario + " clave " + clave + " nombreCola " + nombreCola + " urlLogin " + urlLogin + " urlConsulta " + urlConsulta);
                log.trace("AutenticacionController.obtenerParametrosFirmar.urlRecepcion -->" + urlRecepcion + " usuario " + usuario + " clave " + clave + " nombreCola " + nombreCola + " urlLogin " + urlLogin + " urlConsulta " + urlConsulta);
            }
            params.put("urlLogin", urlLogin);
            params.put("urlRecepcion", urlRecepcion);
            params.put("urlConsulta", urlConsulta);
            params.put("usuario", usuario);
            params.put("clave", clave);
            params.put("cola", nombreCola);
            params.put("colaCorreo", colaCorreo);
            params.put("factory", nombreFactory);
            params.put("servercola", servidorCola);
            //
            params.put("nombreColaAnula", nombreColaAnula);
            params.put("nombreFactoryAnula", nombreFactoryAnula);
        }
        //return params;
    }

    public String obtenerToken() {
        obtenerParametrosFirmar();
        String status = "";
        String token = "";
        String body = "";
        String respuesta = "";
        if (Constants.MODE_DEBUG) {
            System.out.println("EnvioDTEService.obtenerToken -->");
            log.trace("EnvioDTEService.obtenerToken -->");
        }

        if (Constants.MODE_DEBUG) {
            System.out.println("EnvioDTEService.authClient.autenticar.urlLogin -->" + params.get("urlLogin") + " usuario " + params.get("usuario") + " clave " + params.get("clave"));
            log.trace("EnvioDTEService.autenticar.urlLogin -->" + params.get("urlLogin") + " usuario " + params.get("usuario") + " clave " + params.get("clave"));
        }
        ResponseEntity<?> response = autenticar(params.get("usuario"), params.get("clave"), params.get("urlLogin"));
        //
        if (Constants.MODE_DEBUG) {
            System.out.println("EnvioDTEService.autenticar.response -->" + response);
            log.trace("EnvioDTEService.autenticar.response -->" + response);
        }
        if (response.getStatusCode() == HttpStatus.OK) {
            String strJson = gson.toJson(response.getBody());
            //
            if (Constants.MODE_DEBUG) {
                System.err.println("El response es -->" + strJson);
                log.trace("El response es -->" + strJson);
            }
            
            JsonObject objectJson = parser.parse(strJson).getAsJsonObject();
            status = objectJson.get("status").getAsString();
            // body = objectJson.get("body").getAsString();
            token = objectJson.getAsJsonObject("body").get("token").getAsString();
            if (status.equalsIgnoreCase("OK")) {
                if (Constants.MODE_DEBUG) {
                  System.err.println("El token es -->" + token);
                  log.trace("El token es -->" + token);
                }
                respuesta = "OK," + token;
            } else {
                if (Constants.MODE_DEBUG) {
                      System.out.println("El status cod es -->" + status);
                      log.trace("El status cod es -->" + status);
                }              
                respuesta = "ERROR, " + response.getBody();                
            }
        } else {            
            System.out.println("El status cod es -->" + response.getStatusCode());
            log.trace("El status cod es -->" + response.getStatusCode());            
            respuesta = "ERROR, " + response.getBody();
        }
        return respuesta;
    }

    public ResponseEntity<?> autenticar(String usuario, String clave, String url) {

        if (Constants.MODE_DEBUG) {
            System.out.println("EnvioDTEService.usuario-->" + usuario + " Clave: " + clave + " url " + url);
            log.trace("EnvioDTEService.usuario -->" + usuario + " Clave: " + clave + " url " + url);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //
        RestTemplate restTemplate = new RestTemplate();
        //
        HttpEntity<?> entity = new HttpEntity(headers);
        //
        try {
            ResponseEntity<?> response = new RestTemplate().exchange(url, HttpMethod.POST, entity, Object.class);
            //
            if (Constants.MODE_DEBUG) {
                System.out.println("EnvioDTEService.autenticar.response -->" + response);
                log.trace("EnvioDTEService.autenticar.response -->" + response);
            }
            return response;

        } catch (Exception e) {
            log.error(" error en EnvioDTEService.autenticar", e);
            return ResponseEntity.unprocessableEntity().body(e);
        }

    }

    /**
     * Metodo para procesar el json DTE del MH
     *
     * @param codgeneracion codigo único de identificacion del documento
     * electronico
     * @param codtd codigo del tipo de documento
     * @param json { "version":2, "idEnvio":1, "ambiente":"01",
     * "documento":"eyJhbGciOiJSUzUxMiJ9.ewogICJpZGVudGlmaWNhY2lvbiIgOiB7CiAgICAidmVyc2lvbiIgOiAzLAogICAgImFtYmllbnRlIiA6ICIwMSIsCiAgICAidGlwb0R0ZSIgOiAiMDMiLAogICAgIm51bWVyb0NvbnRyb2wiIDogIkRURS0wMy0wMDAwMDAwMC0wMDAwMDAwMDAwMTg3NjQiLAogICAgImNvZGlnb0dlbmVyYWNpb24iIDogIjY3RDkwQjM4LThBOTMtNDUyQi05QzY3LUExNTYyNzhFMjFCMyIsCiAgICAidGlwb01vZGVsbyIgOiAxLAogICAgInRpcG9PcGVyYWNpb24iIDogMSwKICAgICJmZWNFbWkiIDogIjIwMjItMDUtMTkiLAogICAgImhvckVtaSIgOiAiMTY6MDE6MzMiLAogICAgInRpcG9Nb25lZGEiIDogIlVTRCIKICB9LAogICJlbWlzb3IiIDogewogICAgIm5pdCIgOiAiMDYxNDE3MDQ2NzAwMjIiLAogICAgIm5yYyIgOiAiMzA1MCIsCiAgICAibm9tYnJlIiA6ICJQUk9EVUNUSVZFIEJVU0lORVNTIFNPTFVUSU9OUyBFTCBTQUxWQURPUiwgU0EgREUgQ1YiLAogICAgImNvZEFjdGl2aWRhZCIgOiAiNDY1MTAiLAogICAgImRlc2NBY3RpdmlkYWQiIDogIlZlbnRhIGFsIHBvciBtYXlvciBkZSBjb21wdXRhZG9yYXMsIGVxdWlwbyBwZXJpZmVyaWNvIHkgcHJvZ3JhbWFzIGluZm9ybWF0aWNvcyIsCiAgICAibm9tYnJlQ29tZXJjaWFsIiA6ICJQQlMgRUwgU0FMVkFET1IsIFNBIERFIENWIiwKICAgICJ0aXBvRXN0YWJsZWNpbWllbnRvIiA6ICIwMiIsCiAgICAiZGlyZWNjaW9uIiA6IHsKICAgICAgImRlcGFydGFtZW50byIgOiAiMDUiLAogICAgICAibXVuaWNpcGlvIiA6ICIwMSIsCiAgICAgICJjb21wbGVtZW50byIgOiAiRmluYWwgQm91bGV2YXJkIFNhbnRhIEVsZW5hIHkgQm91bGV2YXJkIE9yZGVuIGRlIE1hbHRhLCBFZGlmaWNpbyBYZXJveCIKICAgIH0sCiAgICAidGVsZWZvbm8iIDogIjIyMzkzMDAwIiwKICAgICJjb3JyZW8iIDogImVybmVzdG8uZ3VldmFyYUBncm91cHBicy5jb20iCiAgfSwKICAicmVjZXB0b3IiIDogewogICAgIm5pdCIgOiAiMDYxNDAxMDg1ODAwMTciLAogICAgIm5yYyIgOiAiNDE4IiwKICAgICJub21icmUiIDogIkZSRVVORCBERSBFTCBTQUxWQURPUiwgUy5BLiBERSBDLlYuIiwKICAgICJjb2RBY3RpdmlkYWQiIDogIjQ2NjMyIiwKICAgICJkZXNjQWN0aXZpZGFkIiA6ICJWRU5UQSBBTCBQT1IgTUFZT1IgREUgQVJUw41DVUxPUyBERSBGRVJSRVRFUsONQSBZIFBJTlRVUkVSw41BU1xuXHIiLAogICAgIm5vbWJyZUNvbWVyY2lhbCIgOiAiRlJFVU5EIERFIEVMIFNBTFZBRE9SLCBTLkEuIERFIEMuVi4iLAogICAgImRpcmVjY2lvbiIgOiB7CiAgICAgICJkZXBhcnRhbWVudG8iIDogIjA2IiwKICAgICAgIm11bmljaXBpbyIgOiAiMTQiLAogICAgICAiY29tcGxlbWVudG8iIDogIlBST0xPTkdBQ0lPTiBBVVRPUElTVEEgTk9SVEUgQ09MLiBNT01QRUdPTiwgQ0FMTEUgUFBBTCBZIFBKRSBGUkVVTkQgIyAzLFNBTiBTQUxWQURPUiIKICAgIH0sCiAgICAidGVsZWZvbm8iIDogIjI1MDA4NDU5IiwKICAgICJjb3JyZW8iIDogIm1hcmlvc0BmcmV1bmRzYS5jb20iCiAgfSwKICAiY3VlcnBvRG9jdW1lbnRvIiA6IFsgewogICAgIm51bUl0ZW0iIDogMSwKICAgICJ0aXBvSXRlbSIgOiAxLAogICAgImNhbnRpZGFkIiA6IDEuMCwKICAgICJjb2RpZ28iIDogIk9UUk9TIiwKICAgICJ1bmlNZWRpZGEiIDogNTksCiAgICAiZGVzY3JpcGNpb24iIDogIlJFTk9WQUNJT04gREUgT1JBQ0xFIENMT1VEIFNFUlZJQ0VTfHx8fHx8fHx8fHx8UkVOT1ZBQ0lPTiBERSBPUkFDTEUgQ0xPVUQgU0VSVklDRVN8IiwKICAgICJwcmVjaW9VbmkiIDogMTAwLjAsCiAgICAibW9udG9EZXNjdSIgOiAwLjAsCiAgICAidmVudGFOb1N1aiIgOiAwLjAsCiAgICAidmVudGFFeGVudGEiIDogMC4wLAogICAgInZlbnRhR3JhdmFkYSIgOiAxMDAuMCwKICAgICJub0dyYXZhZG8iIDogMC4wLAogICAgInRyaWJ1dG9zIiA6IFsgIjIwIiBdLAogICAgInBzdiIgOiAwLjAKICB9IF0sCiAgInJlc3VtZW4iIDogewogICAgInRvdGFsTm9TdWoiIDogMC4wLAogICAgInRvdGFsRXhlbnRhIiA6IDAuMCwKICAgICJ0b3RhbEdyYXZhZGEiIDogMTAwLjAsCiAgICAic3ViVG90YWxWZW50YXMiIDogMTAwLjAsCiAgICAiZGVzY3VOb1N1aiIgOiAwLjAsCiAgICAiZGVzY3VFeGVudGEiIDogMC4wLAogICAgImRlc2N1R3JhdmFkYSIgOiAwLjAsCiAgICAicG9yY2VudGFqZURlc2N1ZW50byIgOiAwLjAsCiAgICAidG90YWxEZXNjdSIgOiAwLjAsCiAgICAidHJpYnV0b3MiIDogWyB7CiAgICAgICJjb2RpZ28iIDogIjIwIiwKICAgICAgImRlc2NyaXBjaW9uIiA6ICJJbXB1ZXN0byAgYWwgVmFsb3IgQWdyZWdhZG8gMTMlIiwKICAgICAgInZhbG9yIiA6IDEzCiAgICB9IF0sCiAgICAic3ViVG90YWwiIDogMTAwLjAsCiAgICAiaXZhUGVyY2kxIiA6IDAuMCwKICAgICJpdmFSZXRlMSIgOiAwLjAsCiAgICAicmV0ZVJlbnRhIiA6IDAuMCwKICAgICJtb250b1RvdGFsT3BlcmFjaW9uIiA6IDExMy4wLAogICAgInRvdGFsTm9HcmF2YWRvIiA6IDAuMCwKICAgICJ0b3RhbFBhZ2FyIiA6IDExMy4wLAogICAgInRvdGFsTGV0cmFzIiA6ICJDaWVudG8gdHJlY2UgRMOzbGFyZXMgVVMgY29uIDAwLzEwMCIsCiAgICAic2FsZG9GYXZvciIgOiAwLjAsCiAgICAiY29uZGljaW9uT3BlcmFjaW9uIiA6IDIsCiAgICAicGFnb3MiIDogWyB7CiAgICAgICJjb2RpZ28iIDogIjk5IiwKICAgICAgIm1vbnRvUGFnbyIgOiAxMTMuMCwKICAgICAgInBsYXpvIiA6ICIwMiIsCiAgICAgICJwZXJpb2RvIiA6IDEKICAgIH0gXQogIH0sCiAgImV4dGVuc2lvbiIgOiB7CiAgICAibm9tYkVudHJlZ2EiIDogIk1hdXJvIEZyYW5jaXNjbyBNZWrDrWEgTMOzcGV6IiwKICAgICJkb2N1RW50cmVnYSIgOiAiMDE1MDU1NDYwIiwKICAgICJub21iUmVjaWJlIiA6ICJFRFVBUkRPIERBVklEIEZSRVVORCBXQUlERVJHT1IiLAogICAgImRvY3VSZWNpYmUiIDogIjAwNDg5MDA4LTMiLAogICAgIm9ic2VydmFjaW9uZXMiIDogIlNpbiBPYnNlcnZhY2lvbmVzIgogIH0sCiAgImFwZW5kaWNlIiA6IFsgewogICAgImNhbXBvIiA6ICJhcGVuZGljZXMwMSIsCiAgICAiZXRpcXVldGEiIDogImFwZW5kaWNlczAxIiwKICAgICJ2YWxvciIgOiAifFNBTC1GTy1GRTEtQ0YtMTczNnwxfDF8IgogIH0sIHsKICAgICJjYW1wbyIgOiAiYXBlbmRpY2VzMDIiLAogICAgImV0aXF1ZXRhIiA6ICJhcGVuZGljZXMwMiIsCiAgICAidmFsb3IiIDogImNlbnRyYWwtcGJzfEZPfDAwMTI1OTJ8MTN8IgogIH0sIHsKICAgICJjYW1wbyIgOiAiYXBlbmRpY2VzMDMiLAogICAgImV0aXF1ZXRhIiA6ICJhcGVuZGljZXMwMyIsCiAgICAidmFsb3IiIDogInx8fHwiCiAgfSwgewogICAgImNhbXBvIiA6ICJhcGVuZGljZXMwNCIsCiAgICAiZXRpcXVldGEiIDogImFwZW5kaWNlczA0IiwKICAgICJ2YWxvciIgOiAiICIKICB9LCB7CiAgICAiY2FtcG8iIDogImxpc3RhRW1haWxzIiwKICAgICJldGlxdWV0YSIgOiAibGlzdGFFbWFpbHMiLAogICAgInZhbG9yIiA6ICJtYXJpb3NAZnJldW5kc2EuY29tIgogIH0sIHsKICAgICJjYW1wbyIgOiAiZGlyZW50cmVnYSIsCiAgICAiZXRpcXVldGEiIDogImRpcmVudHJlZ2EiLAogICAgInZhbG9yIiA6ICIgIgogIH0gXSwKICAicmVzcG9uc2VNSCIgOiB7CiAgICAidmVyc2lvbiIgOiAyLAogICAgImFtYmllbnRlIiA6ICIwMSIsCiAgICAidmVyc2lvbkFwcCIgOiAyLAogICAgImVzdGFkbyIgOiAiUFJPQ0VTQURPIiwKICAgICJjb2RpZ29HZW5lcmFjaW9uIiA6ICI2N0Q5MEIzOC04QTkzLTQ1MkItOUM2Ny1BMTU2Mjc4RTIxQjMiLAogICAgIm51bWVyb0NvbnRyb2wiIDogIkRURS0wMy0wMDAwMDAwMC0wMDAwMDAwMDAwMTg3NjQiLAogICAgInNlbGxvUmVjaWJpZG8iIDogIjIwMjJFODg0MjJENzU4REE0QkE3OTNCOTgzMzc5QTczQTJBOFlaMUUiLAogICAgImZoUHJvY2VzYW1pZW50byIgOiAiMTkvMDUvMjAyMiAxNjowMTozNCIsCiAgICAiY29kaWdvTXNnIiA6ICIwMDEiLAogICAgImRlc2NyaXBjaW9uTXNnIiA6ICJSRUNJQklETyIsCiAgICAib2JzZXJ2YWNpb25lcyIgOiBbIF0KICB9LAogICJjb2RpZ29FbXByZXNhIiA6ICJQQlMiLAogICJ0b2tlbiIgOiAiZXlKaGJHY2lPaUpTVXpVeE1pSjkuZXdvZ0lDSnBaR1Z1ZEdsbWFXTmhZMmx2YmlJNklIc0tJQ0FnSUNKMlpYSnphVzl1SWpvZ015d0tJQ0FnSUNKaGJXSnBaVzUwWlNJNklDSXdNU0lzQ2lBZ0lDQWlkR2x3YjBSMFpTSTZJQ0l3TXlJc0NpQWdJQ0FpYm5WdFpYSnZRMjl1ZEhKdmJDSTZJQ0pFVkVVdE1ETXRNREF3TURBd01EQXRNREF3TURBd01EQXdNREU0TnpZMElpd0tJQ0FnSUNKamIyUnBaMjlIWlc1bGNtRmphVzl1SWpvZ0lqWTNSRGt3UWpNNExUaEJPVE10TkRVeVFpMDVRelkzTFVFeE5UWXlOemhGTWpGQ015SXNDaUFnSUNBaWRHbHdiMDF2WkdWc2J5STZJREVzQ2lBZ0lDQWlkR2x3YjA5d1pYSmhZMmx2YmlJNklERXNDaUFnSUNBaWRHbHdiME52Ym5ScGJtZGxibU5wWVNJNklHNTFiR3dzQ2lBZ0lDQWliVzkwYVhadlEyOXVkR2x1SWpvZ2JuVnNiQ3dLSUNBZ0lDSm1aV05GYldraU9pQWlNakF5TWkwd05TMHhPU0lzQ2lBZ0lDQWlhRzl5UlcxcElqb2dJakUyT2pBeE9qTXpJaXdLSUNBZ0lDSjBhWEJ2VFc5dVpXUmhJam9nSWxWVFJDSUtJQ0I5TEFvZ0lDSmtiMk4xYldWdWRHOVNaV3hoWTJsdmJtRmtieUk2SUc1MWJHd3NDaUFnSW1WdGFYTnZjaUk2SUhzS0lDQWdJQ0p1YVhRaU9pQWlNRFl4TkRFM01EUTJOekF3TWpJaUxBb2dJQ0FnSW01eVl5STZJQ0l6TURVd0lpd0tJQ0FnSUNKdWIyMWljbVVpT2lBaVVGSlBSRlZEVkVsV1JTQkNWVk5KVGtWVFV5QlRUMHhWVkVsUFRsTWdSVXdnVTBGTVZrRkVUMUlzSUZOQklFUkZJRU5XSWl3S0lDQWdJQ0pqYjJSQlkzUnBkbWxrWVdRaU9pQWlORFkxTVRBaUxBb2dJQ0FnSW1SbGMyTkJZM1JwZG1sa1lXUWlPaUFpVm1WdWRHRWdZV3dnY0c5eUlHMWhlVzl5SUdSbElHTnZiWEIxZEdGa2IzSmhjeXdnWlhGMWFYQnZJSEJsY21sbVpYSnBZMjhnZVNCd2NtOW5jbUZ0WVhNZ2FXNW1iM0p0WVhScFkyOXpJaXdLSUNBZ0lDSnViMjFpY21WRGIyMWxjbU5wWVd3aU9pQWlVRUpUSUVWTUlGTkJURlpCUkU5U0xDQlRRU0JFUlNCRFZpSXNDaUFnSUNBaWRHbHdiMFZ6ZEdGaWJHVmphVzFwWlc1MGJ5STZJQ0l3TWlJc0NpQWdJQ0FpWkdseVpXTmphVzl1SWpvZ2V3b2dJQ0FnSUNBaVpHVndZWEowWVcxbGJuUnZJam9nSWpBMUlpd0tJQ0FnSUNBZ0ltMTFibWxqYVhCcGJ5STZJQ0l3TVNJc0NpQWdJQ0FnSUNKamIyMXdiR1Z0Wlc1MGJ5STZJQ0pHYVc1aGJDQkNiM1ZzWlhaaGNtUWdVMkZ1ZEdFZ1JXeGxibUVnZVNCQ2IzVnNaWFpoY21RZ1QzSmtaVzRnWkdVZ1RXRnNkR0VzSUVWa2FXWnBZMmx2SUZobGNtOTRJZ29nSUNBZ2ZTd0tJQ0FnSUNKMFpXeGxabTl1YnlJNklDSXlNak01TXpBd01DSXNDaUFnSUNBaVkyOXljbVZ2SWpvZ0ltVnlibVZ6ZEc4dVozVmxkbUZ5WVVCbmNtOTFjSEJpY3k1amIyMGlMQW9nSUNBZ0ltTnZaRVZ6ZEdGaWJHVk5TQ0k2SUc1MWJHd3NDaUFnSUNBaVkyOWtSWE4wWVdKc1pTSTZJRzUxYkd3c0NpQWdJQ0FpWTI5a1VIVnVkRzlXWlc1MFlVMUlJam9nYm5Wc2JDd0tJQ0FnSUNKamIyUlFkVzUwYjFabGJuUmhJam9nYm5Wc2JBb2dJSDBzQ2lBZ0luSmxZMlZ3ZEc5eUlqb2dld29nSUNBZ0ltNXBkQ0k2SUNJd05qRTBNREV3T0RVNE1EQXhOeUlzQ2lBZ0lDQWlibkpqSWpvZ0lqUXhPQ0lzQ2lBZ0lDQWlibTl0WW5KbElqb2dJa1pTUlZWT1JDQkVSU0JGVENCVFFVeFdRVVJQVWl3Z1V5NUJMaUJFUlNCRExsWXVJaXdLSUNBZ0lDSmpiMlJCWTNScGRtbGtZV1FpT2lBaU5EWTJNeklpTEFvZ0lDQWdJbVJsYzJOQlkzUnBkbWxrWVdRaU9pQWlWa1ZPVkVFZ1FVd2dVRTlTSUUxQldVOVNJRVJGSUVGU1ZNT05RMVZNVDFNZ1JFVWdSa1ZTVWtWVVJWTERqVUVnV1NCUVNVNVVWVkpGVXNPTlFWTmNibHh5SWl3S0lDQWdJQ0p1YjIxaWNtVkRiMjFsY21OcFlXd2lPaUFpUmxKRlZVNUVJRVJGSUVWTUlGTkJURlpCUkU5U0xDQlRMa0V1SUVSRklFTXVWaTRpTEFvZ0lDQWdJbVJwY21WalkybHZiaUk2SUhzS0lDQWdJQ0FnSW1SbGNHRnlkR0Z0Wlc1MGJ5STZJQ0l3TmlJc0NpQWdJQ0FnSUNKdGRXNXBZMmx3YVc4aU9pQWlNVFFpTEFvZ0lDQWdJQ0FpWTI5dGNHeGxiV1Z1ZEc4aU9pQWlVRkpQVEU5T1IwRkRTVTlPSUVGVlZFOVFTVk5VUVNCT1QxSlVSU0JEVDB3dUlFMVBUVkJGUjA5T0xDQkRRVXhNUlNCUVVFRk1JRmtnVUVwRklFWlNSVlZPUkNBaklETXNVMEZPSUZOQlRGWkJSRTlTSWdvZ0lDQWdmU3dLSUNBZ0lDSjBaV3hsWm05dWJ5STZJQ0l5TlRBd09EUTFPU0lzQ2lBZ0lDQWlZMjl5Y21Wdklqb2dJbTFoY21sdmMwQm1jbVYxYm1SellTNWpiMjBpQ2lBZ2ZTd0tJQ0FpYjNSeWIzTkViMk4xYldWdWRHOXpJam9nYm5Wc2JDd0tJQ0FpZG1WdWRHRlVaWEpqWlhKdklqb2diblZzYkN3S0lDQWlZM1ZsY25CdlJHOWpkVzFsYm5Sdklqb2dXd29nSUNBZ2V3b2dJQ0FnSUNBaWJuVnRTWFJsYlNJNklERXNDaUFnSUNBZ0lDSjBhWEJ2U1hSbGJTSTZJREVzQ2lBZ0lDQWdJQ0p1ZFcxbGNtOUViMk4xYldWdWRHOGlPaUJ1ZFd4c0xBb2dJQ0FnSUNBaVkyOWthV2R2SWpvZ0lrOVVVazlUSWl3S0lDQWdJQ0FnSW1OdlpGUnlhV0oxZEc4aU9pQnVkV3hzTEFvZ0lDQWdJQ0FpWkdWelkzSnBjR05wYjI0aU9pQWlVa1ZPVDFaQlEwbFBUaUJFUlNCUFVrRkRURVVnUTB4UFZVUWdVMFZTVmtsRFJWTjhmSHg4Zkh4OGZIeDhmSHhTUlU1UFZrRkRTVTlPSUVSRklFOVNRVU5NUlNCRFRFOVZSQ0JUUlZKV1NVTkZVM3dpTEFvZ0lDQWdJQ0FpWTJGdWRHbGtZV1FpT2lBeExqQXNDaUFnSUNBZ0lDSjFibWxOWldScFpHRWlPaUExT1N3S0lDQWdJQ0FnSW5CeVpXTnBiMVZ1YVNJNklERXpNakUxTGpBc0NpQWdJQ0FnSUNKdGIyNTBiMFJsYzJOMUlqb2dNQzR3TEFvZ0lDQWdJQ0FpZG1WdWRHRk9iMU4xYWlJNklEQXVNQ3dLSUNBZ0lDQWdJblpsYm5SaFJYaGxiblJoSWpvZ01DNHdMQW9nSUNBZ0lDQWlkbVZ1ZEdGSGNtRjJZV1JoSWpvZ01UTXlNVFV1TUN3S0lDQWdJQ0FnSW5SeWFXSjFkRzl6SWpvZ1d3b2dJQ0FnSUNBZ0lDSXlNQ0lLSUNBZ0lDQWdYU3dLSUNBZ0lDQWdJbkJ6ZGlJNklEQXVNQ3dLSUNBZ0lDQWdJbTV2UjNKaGRtRmtieUk2SURBdU1Bb2dJQ0FnZlFvZ0lGMHNDaUFnSW5KbGMzVnRaVzRpT2lCN0NpQWdJQ0FpZEc5MFlXeE9iMU4xYWlJNklEQXVNQ3dLSUNBZ0lDSjBiM1JoYkVWNFpXNTBZU0k2SURBdU1Dd0tJQ0FnSUNKMGIzUmhiRWR5WVhaaFpHRWlPaUF4TXpJeE5TNHdMQW9nSUNBZ0luTjFZbFJ2ZEdGc1ZtVnVkR0Z6SWpvZ01UTXlNVFV1TUN3S0lDQWdJQ0prWlhOamRVNXZVM1ZxSWpvZ01DNHdMQW9nSUNBZ0ltUmxjMk4xUlhobGJuUmhJam9nTUM0d0xBb2dJQ0FnSW1SbGMyTjFSM0poZG1Ga1lTSTZJREF1TUN3S0lDQWdJQ0p3YjNKalpXNTBZV3BsUkdWelkzVmxiblJ2SWpvZ01DNHdMQW9nSUNBZ0luUnZkR0ZzUkdWelkzVWlPaUF3TGpBc0NpQWdJQ0FpZEhKcFluVjBiM01pT2lCYkNpQWdJQ0FnSUhzS0lDQWdJQ0FnSUNBaVkyOWthV2R2SWpvZ0lqSXdJaXdLSUNBZ0lDQWdJQ0FpWkdWelkzSnBjR05wYjI0aU9pQWlTVzF3ZFdWemRHOGdJR0ZzSUZaaGJHOXlJRUZuY21WbllXUnZJREV6SlNJc0NpQWdJQ0FnSUNBZ0luWmhiRzl5SWpvZ01UY3hOeTQ1TlFvZ0lDQWdJQ0I5Q2lBZ0lDQmRMQW9nSUNBZ0luTjFZbFJ2ZEdGc0lqb2dNVE15TVRVdU1Dd0tJQ0FnSUNKcGRtRlFaWEpqYVRFaU9pQXdMakFzQ2lBZ0lDQWlhWFpoVW1WMFpURWlPaUF3TGpBc0NpQWdJQ0FpY21WMFpWSmxiblJoSWpvZ01DNHdMQW9nSUNBZ0ltMXZiblJ2Vkc5MFlXeFBjR1Z5WVdOcGIyNGlPaUF4TkRrek1pNDVOU3dLSUNBZ0lDSjBiM1JoYkU1dlIzSmhkbUZrYnlJNklEQXVNQ3dLSUNBZ0lDSjBiM1JoYkZCaFoyRnlJam9nTVRRNU16SXVPVFVzQ2lBZ0lDQWlkRzkwWVd4TVpYUnlZWE1pT2lBaVEyRjBiM0pqWlNCTmFXd2dUbTkyWldOcFpXNTBiM01nVkhKbGFXNTBZU0I1SUVSdmN5QkV3N05zWVhKbGN5QlZVeUJqYjI0Z09UVXZNVEF3SWl3S0lDQWdJQ0p6WVd4a2IwWmhkbTl5SWpvZ01DNHdMQW9nSUNBZ0ltTnZibVJwWTJsdmJrOXdaWEpoWTJsdmJpSTZJRElzQ2lBZ0lDQWljR0ZuYjNNaU9pQmJDaUFnSUNBZ0lIc0tJQ0FnSUNBZ0lDQWlZMjlrYVdkdklqb2dJams1SWl3S0lDQWdJQ0FnSUNBaWJXOXVkRzlRWVdkdklqb2dNVFE1TXpJdU9UVXNDaUFnSUNBZ0lDQWdJbkpsWm1WeVpXNWphV0VpT2lCdWRXeHNMQW9nSUNBZ0lDQWdJQ0p3YkdGNmJ5STZJQ0l3TWlJc0NpQWdJQ0FnSUNBZ0luQmxjbWx2Wkc4aU9pQXhDaUFnSUNBZ0lIMEtJQ0FnSUYwc0NpQWdJQ0FpYm5WdFVHRm5iMFZzWldOMGNtOXVhV052SWpvZ2JuVnNiQW9nSUgwc0NpQWdJbVY0ZEdWdWMybHZiaUk2SUhzS0lDQWdJQ0p1YjIxaVJXNTBjbVZuWVNJNklDSk5ZWFZ5YnlCR2NtRnVZMmx6WTI4Z1RXVnF3NjFoSUV6RHMzQmxlaUlzQ2lBZ0lDQWlaRzlqZFVWdWRISmxaMkVpT2lBaU1ERTFNRFUxTkRZd0lpd0tJQ0FnSUNKdWIyMWlVbVZqYVdKbElqb2dJa1ZFVlVGU1JFOGdSRUZXU1VRZ1JsSkZWVTVFSUZkQlNVUkZVa2RQVWlJc0NpQWdJQ0FpWkc5amRWSmxZMmxpWlNJNklDSXdNRFE0T1RBd09DMHpJaXdLSUNBZ0lDSnZZbk5sY25aaFkybHZibVZ6SWpvZ0lsTnBiaUJQWW5ObGNuWmhZMmx2Ym1Weklpd0tJQ0FnSUNKd2JHRmpZVlpsYUdsamRXeHZJam9nYm5Wc2JBb2dJSDBzQ2lBZ0ltRndaVzVrYVdObElqb2dXd29nSUNBZ2V3b2dJQ0FnSUNBaVkyRnRjRzhpT2lBaVlYQmxibVJwWTJWek1ERWlMQW9nSUNBZ0lDQWlaWFJwY1hWbGRHRWlPaUFpWVhCbGJtUnBZMlZ6TURFaUxBb2dJQ0FnSUNBaWRtRnNiM0lpT2lBaWZGTkJUQzFHVHkxR1JURXRRMFl0TVRjek5ud3hmREY4SWdvZ0lDQWdmU3dLSUNBZ0lIc0tJQ0FnSUNBZ0ltTmhiWEJ2SWpvZ0ltRndaVzVrYVdObGN6QXlJaXdLSUNBZ0lDQWdJbVYwYVhGMVpYUmhJam9nSW1Gd1pXNWthV05sY3pBeUlpd0tJQ0FnSUNBZ0luWmhiRzl5SWpvZ0ltTmxiblJ5WVd3dGNHSnpmRVpQZkRBd01USTFPVEo4TVROOElnb2dJQ0FnZlN3S0lDQWdJSHNLSUNBZ0lDQWdJbU5oYlhCdklqb2dJbUZ3Wlc1a2FXTmxjekF6SWl3S0lDQWdJQ0FnSW1WMGFYRjFaWFJoSWpvZ0ltRndaVzVrYVdObGN6QXpJaXdLSUNBZ0lDQWdJblpoYkc5eUlqb2dJbng4Zkh3aUNpQWdJQ0I5TEFvZ0lDQWdld29nSUNBZ0lDQWlZMkZ0Y0c4aU9pQWlZWEJsYm1ScFkyVnpNRFFpTEFvZ0lDQWdJQ0FpWlhScGNYVmxkR0VpT2lBaVlYQmxibVJwWTJWek1EUWlMQW9nSUNBZ0lDQWlkbUZzYjNJaU9pQWlJQ0lLSUNBZ0lIMHNDaUFnSUNCN0NpQWdJQ0FnSUNKallXMXdieUk2SUNKc2FYTjBZVVZ0WVdsc2N5SXNDaUFnSUNBZ0lDSmxkR2x4ZFdWMFlTSTZJQ0pzYVhOMFlVVnRZV2xzY3lJc0NpQWdJQ0FnSUNKMllXeHZjaUk2SUNKdFlYSnBiM05BWm5KbGRXNWtjMkV1WTI5dElnb2dJQ0FnZlN3S0lDQWdJSHNLSUNBZ0lDQWdJbU5oYlhCdklqb2dJbVJwY21WdWRISmxaMkVpTEFvZ0lDQWdJQ0FpWlhScGNYVmxkR0VpT2lBaVpHbHlaVzUwY21WbllTSXNDaUFnSUNBZ0lDSjJZV3h2Y2lJNklDSWdJZ29nSUNBZ2ZRb2dJRjBLZlEuUzRZZjZLOTVMeUVpQV9UTTZ3S2tfeEVLeDU4TU12VnNjZFg4cDNQVmZvVmFBUlpneWtaeWNTSzl6aWp2SnZUdEZRYlhHdjVkUGo3YzBiZU5BLXFTZEhUU3o2c1Z5THZhMTkwa0lEVWRRV2dIVm1nM0JfZEZ4cXVScmNrS3FmUFdqSVpIX2hHcHRicmZXTmhXQzNvWTdfZGVFQXpHRlQtQVhHODBjSjFpMlNZeFRIRzRyUk5SVGRNcktPSklLZlk3VWFYY3QzcGwxVlBESUpKVVpBNF9Qajd5bDJ0NEdYYTgxMXpFZEZheEtUVmlzdi1kdDAwenpHZ0xCRDhlTmg2ZG93clNFSmlUU1lzbkNQQXJCNzRtVkpIQXk3dkxlUkxpTTljMnd2ZDNLM2hCYUZ2Y2ZTazJ5YU9EekRHaUNsZS1tSFRneWVoMVFfOUVaX21jWDJ2OWFBIgp9.ThC1iX-LGACPU-LcBKR9sBHAcVdeRXAovCnvXGsEwnkjYaErbQBgEoxjEM8tVc7-Irbf8m9VbVtw2QCIW5vS6yJHTpg6DgS1kfG-IVX-sGpqOVb4Io0Su0Ew_AejhFfJsZi1bEbgRR_8N-wIqRX52CECYhOR7NGjiRzQSCZS5LvWkkkKu0EQ2xjpm--b9PQ8byYnNT3P6DrhbEkizLTC7cMh_RS3P5qgtCR5YBGmo4Aeb0mqISfkGNZbgRE8l282pYN434i0BqWDPgaGvPXn4PwKFHOCpjEoM5thEPO2IGAeuHfOAPl7B340eMQrxDVUIaxAKj684sEZ0U3DgNBWhw"
     * }
     * @return mensaje sobre el resultado del procesamiento del documento DTE
     */
    public String recepcionDTE(String json, String codGen, String tipoDte) {
        String token = obtenerToken();
        String respuesta = "";
        if (token.contains("ERROR")) {
            if (Constants.MODE_DEBUG) {
                System.out.println("EnvioDTEService.recepcionDTE.token-->" + token);
                log.trace("EnvioDTEService.recepcionDTE.token -->" + token);
            }
            return token;
        }
        if (Constants.MODE_DEBUG) {
            System.out.println("EnvioDTEService.recepcionDTE.jsonRequest-->" + json);
            log.trace("EnvioDTEService.recepcionDTE.jsonRequest -->" + json);
        }
        token = token.split(",")[1].trim();
        if (Constants.MODE_DEBUG) {
            System.out.println("EnvioDTEService.recepcionDTE.token bearer-->" + token);
            log.trace("EnvioDTEService.recepcionDTE.token bearer -->" + token);
        }
        respuesta = llamadaRecepcion(json, token, codGen, tipoDte);
        return respuesta;
    }

    /**
     * Metodo para el procesamiento del envío del documento y el manejo de su re
     * - proceso
     *
     * @param json { "version":2, "idEnvio":1, "ambiente":"01",
     * "documento":"eyJhbGciOiJSUzUxMiJ9.ewogICJpZGVudGlmaWNhY2lvbiIgOiB7CiAgICAidmVyc2lvbiIgOiAzLAogICAgImFtYmllbnRlIiA6ICIwMSIsCiAgICAidGlwb0R0ZSIgOiAiMDMiLAogICAgIm51bWVyb0NvbnRyb2wiIDogIkRURS0wMy0wMDAwMDAwMC0wMDAwMDAwMDAwMTg3NjQiLAogICAgImNvZGlnb0dlbmVyYWNpb24iIDogIjY3RDkwQjM4LThBOTMtNDUyQi05QzY3LUExNTYyNzhFMjFCMyIsCiAgICAidGlwb01vZGVsbyIgOiAxLAogICAgInRpcG9PcGVyYWNpb24iIDogMSwKICAgICJmZWNFbWkiIDogIjIwMjItMDUtMTkiLAogICAgImhvckVtaSIgOiAiMTY6MDE6MzMiLAogICAgInRpcG9Nb25lZGEiIDogIlVTRCIKICB9LAogICJlbWlzb3IiIDogewogICAgIm5pdCIgOiAiMDYxNDE3MDQ2NzAwMjIiLAogICAgIm5yYyIgOiAiMzA1MCIsCiAgICAibm9tYnJlIiA6ICJQUk9EVUNUSVZFIEJVU0lORVNTIFNPTFVUSU9OUyBFTCBTQUxWQURPUiwgU0EgREUgQ1YiLAogICAgImNvZEFjdGl2aWRhZCIgOiAiNDY1MTAiLAogICAgImRlc2NBY3RpdmlkYWQiIDogIlZlbnRhIGFsIHBvciBtYXlvciBkZSBjb21wdXRhZG9yYXMsIGVxdWlwbyBwZXJpZmVyaWNvIHkgcHJvZ3JhbWFzIGluZm9ybWF0aWNvcyIsCiAgICAibm9tYnJlQ29tZXJjaWFsIiA6ICJQQlMgRUwgU0FMVkFET1IsIFNBIERFIENWIiwKICAgICJ0aXBvRXN0YWJsZWNpbWllbnRvIiA6ICIwMiIsCiAgICAiZGlyZWNjaW9uIiA6IHsKICAgICAgImRlcGFydGFtZW50byIgOiAiMDUiLAogICAgICAibXVuaWNpcGlvIiA6ICIwMSIsCiAgICAgICJjb21wbGVtZW50byIgOiAiRmluYWwgQm91bGV2YXJkIFNhbnRhIEVsZW5hIHkgQm91bGV2YXJkIE9yZGVuIGRlIE1hbHRhLCBFZGlmaWNpbyBYZXJveCIKICAgIH0sCiAgICAidGVsZWZvbm8iIDogIjIyMzkzMDAwIiwKICAgICJjb3JyZW8iIDogImVybmVzdG8uZ3VldmFyYUBncm91cHBicy5jb20iCiAgfSwKICAicmVjZXB0b3IiIDogewogICAgIm5pdCIgOiAiMDYxNDAxMDg1ODAwMTciLAogICAgIm5yYyIgOiAiNDE4IiwKICAgICJub21icmUiIDogIkZSRVVORCBERSBFTCBTQUxWQURPUiwgUy5BLiBERSBDLlYuIiwKICAgICJjb2RBY3RpdmlkYWQiIDogIjQ2NjMyIiwKICAgICJkZXNjQWN0aXZpZGFkIiA6ICJWRU5UQSBBTCBQT1IgTUFZT1IgREUgQVJUw41DVUxPUyBERSBGRVJSRVRFUsONQSBZIFBJTlRVUkVSw41BU1xuXHIiLAogICAgIm5vbWJyZUNvbWVyY2lhbCIgOiAiRlJFVU5EIERFIEVMIFNBTFZBRE9SLCBTLkEuIERFIEMuVi4iLAogICAgImRpcmVjY2lvbiIgOiB7CiAgICAgICJkZXBhcnRhbWVudG8iIDogIjA2IiwKICAgICAgIm11bmljaXBpbyIgOiAiMTQiLAogICAgICAiY29tcGxlbWVudG8iIDogIlBST0xPTkdBQ0lPTiBBVVRPUElTVEEgTk9SVEUgQ09MLiBNT01QRUdPTiwgQ0FMTEUgUFBBTCBZIFBKRSBGUkVVTkQgIyAzLFNBTiBTQUxWQURPUiIKICAgIH0sCiAgICAidGVsZWZvbm8iIDogIjI1MDA4NDU5IiwKICAgICJjb3JyZW8iIDogIm1hcmlvc0BmcmV1bmRzYS5jb20iCiAgfSwKICAiY3VlcnBvRG9jdW1lbnRvIiA6IFsgewogICAgIm51bUl0ZW0iIDogMSwKICAgICJ0aXBvSXRlbSIgOiAxLAogICAgImNhbnRpZGFkIiA6IDEuMCwKICAgICJjb2RpZ28iIDogIk9UUk9TIiwKICAgICJ1bmlNZWRpZGEiIDogNTksCiAgICAiZGVzY3JpcGNpb24iIDogIlJFTk9WQUNJT04gREUgT1JBQ0xFIENMT1VEIFNFUlZJQ0VTfHx8fHx8fHx8fHx8UkVOT1ZBQ0lPTiBERSBPUkFDTEUgQ0xPVUQgU0VSVklDRVN8IiwKICAgICJwcmVjaW9VbmkiIDogMTAwLjAsCiAgICAibW9udG9EZXNjdSIgOiAwLjAsCiAgICAidmVudGFOb1N1aiIgOiAwLjAsCiAgICAidmVudGFFeGVudGEiIDogMC4wLAogICAgInZlbnRhR3JhdmFkYSIgOiAxMDAuMCwKICAgICJub0dyYXZhZG8iIDogMC4wLAogICAgInRyaWJ1dG9zIiA6IFsgIjIwIiBdLAogICAgInBzdiIgOiAwLjAKICB9IF0sCiAgInJlc3VtZW4iIDogewogICAgInRvdGFsTm9TdWoiIDogMC4wLAogICAgInRvdGFsRXhlbnRhIiA6IDAuMCwKICAgICJ0b3RhbEdyYXZhZGEiIDogMTAwLjAsCiAgICAic3ViVG90YWxWZW50YXMiIDogMTAwLjAsCiAgICAiZGVzY3VOb1N1aiIgOiAwLjAsCiAgICAiZGVzY3VFeGVudGEiIDogMC4wLAogICAgImRlc2N1R3JhdmFkYSIgOiAwLjAsCiAgICAicG9yY2VudGFqZURlc2N1ZW50byIgOiAwLjAsCiAgICAidG90YWxEZXNjdSIgOiAwLjAsCiAgICAidHJpYnV0b3MiIDogWyB7CiAgICAgICJjb2RpZ28iIDogIjIwIiwKICAgICAgImRlc2NyaXBjaW9uIiA6ICJJbXB1ZXN0byAgYWwgVmFsb3IgQWdyZWdhZG8gMTMlIiwKICAgICAgInZhbG9yIiA6IDEzCiAgICB9IF0sCiAgICAic3ViVG90YWwiIDogMTAwLjAsCiAgICAiaXZhUGVyY2kxIiA6IDAuMCwKICAgICJpdmFSZXRlMSIgOiAwLjAsCiAgICAicmV0ZVJlbnRhIiA6IDAuMCwKICAgICJtb250b1RvdGFsT3BlcmFjaW9uIiA6IDExMy4wLAogICAgInRvdGFsTm9HcmF2YWRvIiA6IDAuMCwKICAgICJ0b3RhbFBhZ2FyIiA6IDExMy4wLAogICAgInRvdGFsTGV0cmFzIiA6ICJDaWVudG8gdHJlY2UgRMOzbGFyZXMgVVMgY29uIDAwLzEwMCIsCiAgICAic2FsZG9GYXZvciIgOiAwLjAsCiAgICAiY29uZGljaW9uT3BlcmFjaW9uIiA6IDIsCiAgICAicGFnb3MiIDogWyB7CiAgICAgICJjb2RpZ28iIDogIjk5IiwKICAgICAgIm1vbnRvUGFnbyIgOiAxMTMuMCwKICAgICAgInBsYXpvIiA6ICIwMiIsCiAgICAgICJwZXJpb2RvIiA6IDEKICAgIH0gXQogIH0sCiAgImV4dGVuc2lvbiIgOiB7CiAgICAibm9tYkVudHJlZ2EiIDogIk1hdXJvIEZyYW5jaXNjbyBNZWrDrWEgTMOzcGV6IiwKICAgICJkb2N1RW50cmVnYSIgOiAiMDE1MDU1NDYwIiwKICAgICJub21iUmVjaWJlIiA6ICJFRFVBUkRPIERBVklEIEZSRVVORCBXQUlERVJHT1IiLAogICAgImRvY3VSZWNpYmUiIDogIjAwNDg5MDA4LTMiLAogICAgIm9ic2VydmFjaW9uZXMiIDogIlNpbiBPYnNlcnZhY2lvbmVzIgogIH0sCiAgImFwZW5kaWNlIiA6IFsgewogICAgImNhbXBvIiA6ICJhcGVuZGljZXMwMSIsCiAgICAiZXRpcXVldGEiIDogImFwZW5kaWNlczAxIiwKICAgICJ2YWxvciIgOiAifFNBTC1GTy1GRTEtQ0YtMTczNnwxfDF8IgogIH0sIHsKICAgICJjYW1wbyIgOiAiYXBlbmRpY2VzMDIiLAogICAgImV0aXF1ZXRhIiA6ICJhcGVuZGljZXMwMiIsCiAgICAidmFsb3IiIDogImNlbnRyYWwtcGJzfEZPfDAwMTI1OTJ8MTN8IgogIH0sIHsKICAgICJjYW1wbyIgOiAiYXBlbmRpY2VzMDMiLAogICAgImV0aXF1ZXRhIiA6ICJhcGVuZGljZXMwMyIsCiAgICAidmFsb3IiIDogInx8fHwiCiAgfSwgewogICAgImNhbXBvIiA6ICJhcGVuZGljZXMwNCIsCiAgICAiZXRpcXVldGEiIDogImFwZW5kaWNlczA0IiwKICAgICJ2YWxvciIgOiAiICIKICB9LCB7CiAgICAiY2FtcG8iIDogImxpc3RhRW1haWxzIiwKICAgICJldGlxdWV0YSIgOiAibGlzdGFFbWFpbHMiLAogICAgInZhbG9yIiA6ICJtYXJpb3NAZnJldW5kc2EuY29tIgogIH0sIHsKICAgICJjYW1wbyIgOiAiZGlyZW50cmVnYSIsCiAgICAiZXRpcXVldGEiIDogImRpcmVudHJlZ2EiLAogICAgInZhbG9yIiA6ICIgIgogIH0gXSwKICAicmVzcG9uc2VNSCIgOiB7CiAgICAidmVyc2lvbiIgOiAyLAogICAgImFtYmllbnRlIiA6ICIwMSIsCiAgICAidmVyc2lvbkFwcCIgOiAyLAogICAgImVzdGFkbyIgOiAiUFJPQ0VTQURPIiwKICAgICJjb2RpZ29HZW5lcmFjaW9uIiA6ICI2N0Q5MEIzOC04QTkzLTQ1MkItOUM2Ny1BMTU2Mjc4RTIxQjMiLAogICAgIm51bWVyb0NvbnRyb2wiIDogIkRURS0wMy0wMDAwMDAwMC0wMDAwMDAwMDAwMTg3NjQiLAogICAgInNlbGxvUmVjaWJpZG8iIDogIjIwMjJFODg0MjJENzU4REE0QkE3OTNCOTgzMzc5QTczQTJBOFlaMUUiLAogICAgImZoUHJvY2VzYW1pZW50byIgOiAiMTkvMDUvMjAyMiAxNjowMTozNCIsCiAgICAiY29kaWdvTXNnIiA6ICIwMDEiLAogICAgImRlc2NyaXBjaW9uTXNnIiA6ICJSRUNJQklETyIsCiAgICAib2JzZXJ2YWNpb25lcyIgOiBbIF0KICB9LAogICJjb2RpZ29FbXByZXNhIiA6ICJQQlMiLAogICJ0b2tlbiIgOiAiZXlKaGJHY2lPaUpTVXpVeE1pSjkuZXdvZ0lDSnBaR1Z1ZEdsbWFXTmhZMmx2YmlJNklIc0tJQ0FnSUNKMlpYSnphVzl1SWpvZ015d0tJQ0FnSUNKaGJXSnBaVzUwWlNJNklDSXdNU0lzQ2lBZ0lDQWlkR2x3YjBSMFpTSTZJQ0l3TXlJc0NpQWdJQ0FpYm5WdFpYSnZRMjl1ZEhKdmJDSTZJQ0pFVkVVdE1ETXRNREF3TURBd01EQXRNREF3TURBd01EQXdNREU0TnpZMElpd0tJQ0FnSUNKamIyUnBaMjlIWlc1bGNtRmphVzl1SWpvZ0lqWTNSRGt3UWpNNExUaEJPVE10TkRVeVFpMDVRelkzTFVFeE5UWXlOemhGTWpGQ015SXNDaUFnSUNBaWRHbHdiMDF2WkdWc2J5STZJREVzQ2lBZ0lDQWlkR2x3YjA5d1pYSmhZMmx2YmlJNklERXNDaUFnSUNBaWRHbHdiME52Ym5ScGJtZGxibU5wWVNJNklHNTFiR3dzQ2lBZ0lDQWliVzkwYVhadlEyOXVkR2x1SWpvZ2JuVnNiQ3dLSUNBZ0lDSm1aV05GYldraU9pQWlNakF5TWkwd05TMHhPU0lzQ2lBZ0lDQWlhRzl5UlcxcElqb2dJakUyT2pBeE9qTXpJaXdLSUNBZ0lDSjBhWEJ2VFc5dVpXUmhJam9nSWxWVFJDSUtJQ0I5TEFvZ0lDSmtiMk4xYldWdWRHOVNaV3hoWTJsdmJtRmtieUk2SUc1MWJHd3NDaUFnSW1WdGFYTnZjaUk2SUhzS0lDQWdJQ0p1YVhRaU9pQWlNRFl4TkRFM01EUTJOekF3TWpJaUxBb2dJQ0FnSW01eVl5STZJQ0l6TURVd0lpd0tJQ0FnSUNKdWIyMWljbVVpT2lBaVVGSlBSRlZEVkVsV1JTQkNWVk5KVGtWVFV5QlRUMHhWVkVsUFRsTWdSVXdnVTBGTVZrRkVUMUlzSUZOQklFUkZJRU5XSWl3S0lDQWdJQ0pqYjJSQlkzUnBkbWxrWVdRaU9pQWlORFkxTVRBaUxBb2dJQ0FnSW1SbGMyTkJZM1JwZG1sa1lXUWlPaUFpVm1WdWRHRWdZV3dnY0c5eUlHMWhlVzl5SUdSbElHTnZiWEIxZEdGa2IzSmhjeXdnWlhGMWFYQnZJSEJsY21sbVpYSnBZMjhnZVNCd2NtOW5jbUZ0WVhNZ2FXNW1iM0p0WVhScFkyOXpJaXdLSUNBZ0lDSnViMjFpY21WRGIyMWxjbU5wWVd3aU9pQWlVRUpUSUVWTUlGTkJURlpCUkU5U0xDQlRRU0JFUlNCRFZpSXNDaUFnSUNBaWRHbHdiMFZ6ZEdGaWJHVmphVzFwWlc1MGJ5STZJQ0l3TWlJc0NpQWdJQ0FpWkdseVpXTmphVzl1SWpvZ2V3b2dJQ0FnSUNBaVpHVndZWEowWVcxbGJuUnZJam9nSWpBMUlpd0tJQ0FnSUNBZ0ltMTFibWxqYVhCcGJ5STZJQ0l3TVNJc0NpQWdJQ0FnSUNKamIyMXdiR1Z0Wlc1MGJ5STZJQ0pHYVc1aGJDQkNiM1ZzWlhaaGNtUWdVMkZ1ZEdFZ1JXeGxibUVnZVNCQ2IzVnNaWFpoY21RZ1QzSmtaVzRnWkdVZ1RXRnNkR0VzSUVWa2FXWnBZMmx2SUZobGNtOTRJZ29nSUNBZ2ZTd0tJQ0FnSUNKMFpXeGxabTl1YnlJNklDSXlNak01TXpBd01DSXNDaUFnSUNBaVkyOXljbVZ2SWpvZ0ltVnlibVZ6ZEc4dVozVmxkbUZ5WVVCbmNtOTFjSEJpY3k1amIyMGlMQW9nSUNBZ0ltTnZaRVZ6ZEdGaWJHVk5TQ0k2SUc1MWJHd3NDaUFnSUNBaVkyOWtSWE4wWVdKc1pTSTZJRzUxYkd3c0NpQWdJQ0FpWTI5a1VIVnVkRzlXWlc1MFlVMUlJam9nYm5Wc2JDd0tJQ0FnSUNKamIyUlFkVzUwYjFabGJuUmhJam9nYm5Wc2JBb2dJSDBzQ2lBZ0luSmxZMlZ3ZEc5eUlqb2dld29nSUNBZ0ltNXBkQ0k2SUNJd05qRTBNREV3T0RVNE1EQXhOeUlzQ2lBZ0lDQWlibkpqSWpvZ0lqUXhPQ0lzQ2lBZ0lDQWlibTl0WW5KbElqb2dJa1pTUlZWT1JDQkVSU0JGVENCVFFVeFdRVVJQVWl3Z1V5NUJMaUJFUlNCRExsWXVJaXdLSUNBZ0lDSmpiMlJCWTNScGRtbGtZV1FpT2lBaU5EWTJNeklpTEFvZ0lDQWdJbVJsYzJOQlkzUnBkbWxrWVdRaU9pQWlWa1ZPVkVFZ1FVd2dVRTlTSUUxQldVOVNJRVJGSUVGU1ZNT05RMVZNVDFNZ1JFVWdSa1ZTVWtWVVJWTERqVUVnV1NCUVNVNVVWVkpGVXNPTlFWTmNibHh5SWl3S0lDQWdJQ0p1YjIxaWNtVkRiMjFsY21OcFlXd2lPaUFpUmxKRlZVNUVJRVJGSUVWTUlGTkJURlpCUkU5U0xDQlRMa0V1SUVSRklFTXVWaTRpTEFvZ0lDQWdJbVJwY21WalkybHZiaUk2SUhzS0lDQWdJQ0FnSW1SbGNHRnlkR0Z0Wlc1MGJ5STZJQ0l3TmlJc0NpQWdJQ0FnSUNKdGRXNXBZMmx3YVc4aU9pQWlNVFFpTEFvZ0lDQWdJQ0FpWTI5dGNHeGxiV1Z1ZEc4aU9pQWlVRkpQVEU5T1IwRkRTVTlPSUVGVlZFOVFTVk5VUVNCT1QxSlVSU0JEVDB3dUlFMVBUVkJGUjA5T0xDQkRRVXhNUlNCUVVFRk1JRmtnVUVwRklFWlNSVlZPUkNBaklETXNVMEZPSUZOQlRGWkJSRTlTSWdvZ0lDQWdmU3dLSUNBZ0lDSjBaV3hsWm05dWJ5STZJQ0l5TlRBd09EUTFPU0lzQ2lBZ0lDQWlZMjl5Y21Wdklqb2dJbTFoY21sdmMwQm1jbVYxYm1SellTNWpiMjBpQ2lBZ2ZTd0tJQ0FpYjNSeWIzTkViMk4xYldWdWRHOXpJam9nYm5Wc2JDd0tJQ0FpZG1WdWRHRlVaWEpqWlhKdklqb2diblZzYkN3S0lDQWlZM1ZsY25CdlJHOWpkVzFsYm5Sdklqb2dXd29nSUNBZ2V3b2dJQ0FnSUNBaWJuVnRTWFJsYlNJNklERXNDaUFnSUNBZ0lDSjBhWEJ2U1hSbGJTSTZJREVzQ2lBZ0lDQWdJQ0p1ZFcxbGNtOUViMk4xYldWdWRHOGlPaUJ1ZFd4c0xBb2dJQ0FnSUNBaVkyOWthV2R2SWpvZ0lrOVVVazlUSWl3S0lDQWdJQ0FnSW1OdlpGUnlhV0oxZEc4aU9pQnVkV3hzTEFvZ0lDQWdJQ0FpWkdWelkzSnBjR05wYjI0aU9pQWlVa1ZPVDFaQlEwbFBUaUJFUlNCUFVrRkRURVVnUTB4UFZVUWdVMFZTVmtsRFJWTjhmSHg4Zkh4OGZIeDhmSHhTUlU1UFZrRkRTVTlPSUVSRklFOVNRVU5NUlNCRFRFOVZSQ0JUUlZKV1NVTkZVM3dpTEFvZ0lDQWdJQ0FpWTJGdWRHbGtZV1FpT2lBeExqQXNDaUFnSUNBZ0lDSjFibWxOWldScFpHRWlPaUExT1N3S0lDQWdJQ0FnSW5CeVpXTnBiMVZ1YVNJNklERXpNakUxTGpBc0NpQWdJQ0FnSUNKdGIyNTBiMFJsYzJOMUlqb2dNQzR3TEFvZ0lDQWdJQ0FpZG1WdWRHRk9iMU4xYWlJNklEQXVNQ3dLSUNBZ0lDQWdJblpsYm5SaFJYaGxiblJoSWpvZ01DNHdMQW9nSUNBZ0lDQWlkbVZ1ZEdGSGNtRjJZV1JoSWpvZ01UTXlNVFV1TUN3S0lDQWdJQ0FnSW5SeWFXSjFkRzl6SWpvZ1d3b2dJQ0FnSUNBZ0lDSXlNQ0lLSUNBZ0lDQWdYU3dLSUNBZ0lDQWdJbkJ6ZGlJNklEQXVNQ3dLSUNBZ0lDQWdJbTV2UjNKaGRtRmtieUk2SURBdU1Bb2dJQ0FnZlFvZ0lGMHNDaUFnSW5KbGMzVnRaVzRpT2lCN0NpQWdJQ0FpZEc5MFlXeE9iMU4xYWlJNklEQXVNQ3dLSUNBZ0lDSjBiM1JoYkVWNFpXNTBZU0k2SURBdU1Dd0tJQ0FnSUNKMGIzUmhiRWR5WVhaaFpHRWlPaUF4TXpJeE5TNHdMQW9nSUNBZ0luTjFZbFJ2ZEdGc1ZtVnVkR0Z6SWpvZ01UTXlNVFV1TUN3S0lDQWdJQ0prWlhOamRVNXZVM1ZxSWpvZ01DNHdMQW9nSUNBZ0ltUmxjMk4xUlhobGJuUmhJam9nTUM0d0xBb2dJQ0FnSW1SbGMyTjFSM0poZG1Ga1lTSTZJREF1TUN3S0lDQWdJQ0p3YjNKalpXNTBZV3BsUkdWelkzVmxiblJ2SWpvZ01DNHdMQW9nSUNBZ0luUnZkR0ZzUkdWelkzVWlPaUF3TGpBc0NpQWdJQ0FpZEhKcFluVjBiM01pT2lCYkNpQWdJQ0FnSUhzS0lDQWdJQ0FnSUNBaVkyOWthV2R2SWpvZ0lqSXdJaXdLSUNBZ0lDQWdJQ0FpWkdWelkzSnBjR05wYjI0aU9pQWlTVzF3ZFdWemRHOGdJR0ZzSUZaaGJHOXlJRUZuY21WbllXUnZJREV6SlNJc0NpQWdJQ0FnSUNBZ0luWmhiRzl5SWpvZ01UY3hOeTQ1TlFvZ0lDQWdJQ0I5Q2lBZ0lDQmRMQW9nSUNBZ0luTjFZbFJ2ZEdGc0lqb2dNVE15TVRVdU1Dd0tJQ0FnSUNKcGRtRlFaWEpqYVRFaU9pQXdMakFzQ2lBZ0lDQWlhWFpoVW1WMFpURWlPaUF3TGpBc0NpQWdJQ0FpY21WMFpWSmxiblJoSWpvZ01DNHdMQW9nSUNBZ0ltMXZiblJ2Vkc5MFlXeFBjR1Z5WVdOcGIyNGlPaUF4TkRrek1pNDVOU3dLSUNBZ0lDSjBiM1JoYkU1dlIzSmhkbUZrYnlJNklEQXVNQ3dLSUNBZ0lDSjBiM1JoYkZCaFoyRnlJam9nTVRRNU16SXVPVFVzQ2lBZ0lDQWlkRzkwWVd4TVpYUnlZWE1pT2lBaVEyRjBiM0pqWlNCTmFXd2dUbTkyWldOcFpXNTBiM01nVkhKbGFXNTBZU0I1SUVSdmN5QkV3N05zWVhKbGN5QlZVeUJqYjI0Z09UVXZNVEF3SWl3S0lDQWdJQ0p6WVd4a2IwWmhkbTl5SWpvZ01DNHdMQW9nSUNBZ0ltTnZibVJwWTJsdmJrOXdaWEpoWTJsdmJpSTZJRElzQ2lBZ0lDQWljR0ZuYjNNaU9pQmJDaUFnSUNBZ0lIc0tJQ0FnSUNBZ0lDQWlZMjlrYVdkdklqb2dJams1SWl3S0lDQWdJQ0FnSUNBaWJXOXVkRzlRWVdkdklqb2dNVFE1TXpJdU9UVXNDaUFnSUNBZ0lDQWdJbkpsWm1WeVpXNWphV0VpT2lCdWRXeHNMQW9nSUNBZ0lDQWdJQ0p3YkdGNmJ5STZJQ0l3TWlJc0NpQWdJQ0FnSUNBZ0luQmxjbWx2Wkc4aU9pQXhDaUFnSUNBZ0lIMEtJQ0FnSUYwc0NpQWdJQ0FpYm5WdFVHRm5iMFZzWldOMGNtOXVhV052SWpvZ2JuVnNiQW9nSUgwc0NpQWdJbVY0ZEdWdWMybHZiaUk2SUhzS0lDQWdJQ0p1YjIxaVJXNTBjbVZuWVNJNklDSk5ZWFZ5YnlCR2NtRnVZMmx6WTI4Z1RXVnF3NjFoSUV6RHMzQmxlaUlzQ2lBZ0lDQWlaRzlqZFVWdWRISmxaMkVpT2lBaU1ERTFNRFUxTkRZd0lpd0tJQ0FnSUNKdWIyMWlVbVZqYVdKbElqb2dJa1ZFVlVGU1JFOGdSRUZXU1VRZ1JsSkZWVTVFSUZkQlNVUkZVa2RQVWlJc0NpQWdJQ0FpWkc5amRWSmxZMmxpWlNJNklDSXdNRFE0T1RBd09DMHpJaXdLSUNBZ0lDSnZZbk5sY25aaFkybHZibVZ6SWpvZ0lsTnBiaUJQWW5ObGNuWmhZMmx2Ym1Weklpd0tJQ0FnSUNKd2JHRmpZVlpsYUdsamRXeHZJam9nYm5Wc2JBb2dJSDBzQ2lBZ0ltRndaVzVrYVdObElqb2dXd29nSUNBZ2V3b2dJQ0FnSUNBaVkyRnRjRzhpT2lBaVlYQmxibVJwWTJWek1ERWlMQW9nSUNBZ0lDQWlaWFJwY1hWbGRHRWlPaUFpWVhCbGJtUnBZMlZ6TURFaUxBb2dJQ0FnSUNBaWRtRnNiM0lpT2lBaWZGTkJUQzFHVHkxR1JURXRRMFl0TVRjek5ud3hmREY4SWdvZ0lDQWdmU3dLSUNBZ0lIc0tJQ0FnSUNBZ0ltTmhiWEJ2SWpvZ0ltRndaVzVrYVdObGN6QXlJaXdLSUNBZ0lDQWdJbVYwYVhGMVpYUmhJam9nSW1Gd1pXNWthV05sY3pBeUlpd0tJQ0FnSUNBZ0luWmhiRzl5SWpvZ0ltTmxiblJ5WVd3dGNHSnpmRVpQZkRBd01USTFPVEo4TVROOElnb2dJQ0FnZlN3S0lDQWdJSHNLSUNBZ0lDQWdJbU5oYlhCdklqb2dJbUZ3Wlc1a2FXTmxjekF6SWl3S0lDQWdJQ0FnSW1WMGFYRjFaWFJoSWpvZ0ltRndaVzVrYVdObGN6QXpJaXdLSUNBZ0lDQWdJblpoYkc5eUlqb2dJbng4Zkh3aUNpQWdJQ0I5TEFvZ0lDQWdld29nSUNBZ0lDQWlZMkZ0Y0c4aU9pQWlZWEJsYm1ScFkyVnpNRFFpTEFvZ0lDQWdJQ0FpWlhScGNYVmxkR0VpT2lBaVlYQmxibVJwWTJWek1EUWlMQW9nSUNBZ0lDQWlkbUZzYjNJaU9pQWlJQ0lLSUNBZ0lIMHNDaUFnSUNCN0NpQWdJQ0FnSUNKallXMXdieUk2SUNKc2FYTjBZVVZ0WVdsc2N5SXNDaUFnSUNBZ0lDSmxkR2x4ZFdWMFlTSTZJQ0pzYVhOMFlVVnRZV2xzY3lJc0NpQWdJQ0FnSUNKMllXeHZjaUk2SUNKdFlYSnBiM05BWm5KbGRXNWtjMkV1WTI5dElnb2dJQ0FnZlN3S0lDQWdJSHNLSUNBZ0lDQWdJbU5oYlhCdklqb2dJbVJwY21WdWRISmxaMkVpTEFvZ0lDQWdJQ0FpWlhScGNYVmxkR0VpT2lBaVpHbHlaVzUwY21WbllTSXNDaUFnSUNBZ0lDSjJZV3h2Y2lJNklDSWdJZ29nSUNBZ2ZRb2dJRjBLZlEuUzRZZjZLOTVMeUVpQV9UTTZ3S2tfeEVLeDU4TU12VnNjZFg4cDNQVmZvVmFBUlpneWtaeWNTSzl6aWp2SnZUdEZRYlhHdjVkUGo3YzBiZU5BLXFTZEhUU3o2c1Z5THZhMTkwa0lEVWRRV2dIVm1nM0JfZEZ4cXVScmNrS3FmUFdqSVpIX2hHcHRicmZXTmhXQzNvWTdfZGVFQXpHRlQtQVhHODBjSjFpMlNZeFRIRzRyUk5SVGRNcktPSklLZlk3VWFYY3QzcGwxVlBESUpKVVpBNF9Qajd5bDJ0NEdYYTgxMXpFZEZheEtUVmlzdi1kdDAwenpHZ0xCRDhlTmg2ZG93clNFSmlUU1lzbkNQQXJCNzRtVkpIQXk3dkxlUkxpTTljMnd2ZDNLM2hCYUZ2Y2ZTazJ5YU9EekRHaUNsZS1tSFRneWVoMVFfOUVaX21jWDJ2OWFBIgp9.ThC1iX-LGACPU-LcBKR9sBHAcVdeRXAovCnvXGsEwnkjYaErbQBgEoxjEM8tVc7-Irbf8m9VbVtw2QCIW5vS6yJHTpg6DgS1kfG-IVX-sGpqOVb4Io0Su0Ew_AejhFfJsZi1bEbgRR_8N-wIqRX52CECYhOR7NGjiRzQSCZS5LvWkkkKu0EQ2xjpm--b9PQ8byYnNT3P6DrhbEkizLTC7cMh_RS3P5qgtCR5YBGmo4Aeb0mqISfkGNZbgRE8l282pYN434i0BqWDPgaGvPXn4PwKFHOCpjEoM5thEPO2IGAeuHfOAPl7B340eMQrxDVUIaxAKj684sEZ0U3DgNBWhw"
     * }
     * @param token Valor del token para la comunicación con el Ministerio de
     * Hacienda
     * @param codGen Código único del Documento DTE
     * @param tipoDte Código del tipo de documento
     * @return mensaje sobre el resultado del procesamiento del documento DTE
     */
    public String llamadaRecepcion(String json, String token, String codGen, String tipoDte) {
        String respuesta = "";
        String jsonConsulta = "{\n"
                + "   \"nitEmisor\":\"" + params.get("usuario") + "\",\n"
                + "   \"tdte\":\"" + tipoDte + "\",\n"
                + "   \"codigoGeneracion\":\"" + codGen + "\"\n"
                + "}";
        int intentos = 1;
        while (intentos <= 3) {
            ResponseEntity<?> response = null;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", token);

            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<String> entity = new HttpEntity<String>(json, headers);
            //
            if (Constants.MODE_DEBUG) {
                System.err.println("El json a enviar en la llamada recepcion es: " + json);
                log.trace("El json a enviar en la llamada recepcion es: " + json);
            }
            
            try {
                if (Constants.MODE_DEBUG) {
                    System.out.println("Realizara la recepcion DTE-->");
                    log.trace("Realizara la recepcion DTE -->");
                }    
                
                response = restTemplate.exchange(params.get("urlRecepcion"), HttpMethod.POST, entity, String.class);
                if (Constants.MODE_DEBUG) {
                    System.err.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                    System.err.println("Response =" + response.getBody());
                    log.trace("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                    log.trace("Response =" + response.getBody());
                }
                if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED
                        || response.getStatusCode() == HttpStatus.CREATED) {
                    respuesta = "OK, " + response.getBody();
                    return respuesta;
                }

            } catch (JsonSyntaxException e) {
                System.out.println("Error de Jsonexcepcion al cargar el archivo :" + e.getMessage());
                System.out.println("------------------------------------------------------");
                System.out.println(" Error. codGen : "+codGen);
                log.error("Error de Jsonexcepcion al cargar el archivo :" + e.getMessage());
                log.error("------------------------------------------------------");
                log.error(" Error. codGen : "+codGen);
                respuesta = "EXCEPTION, " + e.getMessage();
                intentos = 4;

            } //ResourceAccessException
            catch (ResourceAccessException e) {
                System.out.println("Error 400 URL :" + e.getMessage());
                System.out.println("------------------------------------------------------");
                System.out.println(" Error. codGen : "+codGen);
                respuesta = manejoStatusRecepcion(jsonConsulta, token, null, e);
                if (respuesta.contains("OK")) {
                    intentos = 4;
                } else {
                    intentos++;
                }
            } catch (HttpStatusCodeException e) {
                System.out.println("Error de RestClientexcepcion bodyo  :" + e.getResponseBodyAsString());
                System.out.println("Error de RestClientexcepcion statusCode  :" + e.getRawStatusCode());
                System.out.println(" Error. codGen : "+codGen);
                System.out.println("------------------------------------------------------");
                log.error("Error de RestClientexcepcion bodyo  :" + e.getResponseBodyAsString());
                log.error("Error de RestClientexcepcion statusCode  :" + e.getRawStatusCode());
                log.error("------------------------------------------------------");
                //log.error("Error el BodyData:" + response.getBody());
                //log.error("------------------------------------------------------");
                respuesta = manejoStatusRecepcion(jsonConsulta, token, e, null);
                System.out.println("------------------------------------------------------");
                System.out.println(" Valor respuesta : "+respuesta + "codGen : " + codGen);
                System.out.println("------------------------------------------------------");                
                //
                if (respuesta.contains("BAD_REQUEST")) {
                    intentos = 4;
                }
                if (respuesta.contains("FORBIDDEN")) {
                    intentos = 3;
                } else {
                    intentos++;
                }
            } catch (Exception e) {
                System.err.println("Ocurrio una excepcion a la recepcionDTE " + e.getMessage());
                log.error("Ocurrio una excepcion a la recepcionDTE " + e.getMessage());
                System.out.println(" Error. codGen : "+codGen);
                respuesta = "EXCEPTION, " + e.getMessage();
                intentos = 4;
            }
        }

        return respuesta;
    }

    /**
     * Metodo para el procesamiento de las excepciones y reproceso sobre el
     * envío del documento
     *
     * @param json { "nitEmisor":"06140108580017", "tdte":"01",
     * "codigoGeneracion":"D30E0944-B080-00BA-E053-0A01206EA94B" }
     * @param token Valor del token para la comunicación con el Ministerio de
     * Hacienda
     * @param e Excepción generada por un error generado en el WS distinto a
     * error 400
     * @param re Exepción utilizada para manejar el error 400
     * @return mensaje sobre el resultado del procesamiento del documento DTE
     */
    public String manejoStatusRecepcion(String jsonConsulta, 
                                        String token, 
                                        HttpStatusCodeException e, 
                                        ResourceAccessException re) {
        String respuesta = "";
        String body = "";
        int status = 0;
        if (e != null) {            
            body = e.getResponseBodyAsString();
            status = e.getRawStatusCode();
            System.err.println(" e != null "+ status);
        }
        if (re != null) {
            respuesta = consultaRecepcion(jsonConsulta, token);
            if (respuesta.contains("OK")) {
                return respuesta;
            }
            respuesta = "NOT_FOUND, " + respuesta.split("ERROR,")[1].trim();
        } else if (status == 403) {
            token = obtenerToken();
            token = token.split(",")[1].trim();
            respuesta = "FORBIDDEN, " + body;
        } else if (status == 400) {
            respuesta = "BAD_REQUEST, " + body;
            System.err.println(" BAD_REQUEST, status==400 "+ status);
        } else {
            respuesta = "ERROR, " + body;
        }
        System.err.println(" respuesta Status: "+ status);
        return respuesta;
    }

    /**
     * Metodo para la consulta del Status dle DTE
     *
     * @param json { "nitEmisor":"06140108580017", "tdte":"01",
     * "codigoGeneracion":"D30E0944-B080-00BA-E053-0A01206EA94B" }
     * @param token Valor del token para la comunicación con el Ministerio de
     * Hacienda
     * @return mensaje sobre el resultado del procesamiento del documento DTE
     */
    public String consultaRecepcion(String json, String token) {
       if (Constants.MODE_DEBUG) {
           System.err.println("Se va ejecutar el proceso de ConsultaRecepcion------------");           
           System.err.println("El json consultaRecepcion es:" + json);
           System.err.println("El token consultaRecepcion es:" + token);
       }    
        String respuesta = "";    
        ResponseEntity<?> response = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        try {
            Thread.sleep(8000);
            if (Constants.MODE_DEBUG) {
                System.out.println("Realizara la ConsultaRecepcion DTE-->");
                log.trace("Realizara la ConsultaRecepcion DTE -->");
            }
            //
            response = restTemplate.exchange(params.get("urlConsulta"), HttpMethod.POST, entity, String.class);
            if (Constants.MODE_DEBUG) {
                System.err.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                System.err.println("Response =" + response.getBody());
                log.trace("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
                log.trace("Response =" + response.getBody());
            }
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED
                    || response.getStatusCode() == HttpStatus.CREATED) {
                respuesta = "OK, " + response.getBody();
            } else {
                respuesta = "ERROR, " + response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Ocurrio una excepcion a la consultaDTE " + e.getMessage());
            log.error("Ocurrio una excepcion a la consultaDTE" + e.getMessage());
            respuesta = "ERROR, " + e.getMessage();
        }

        return respuesta;
    }

    public String setearColaCorreo(String jsonResponse) {
        System.err.println("El json response a ingresar es :" + params.get("servercola"));
        String respuesta = "";
        try {
            InitialContext initialContext = queuePoster.getInitialContext(params.get("servercola"));
            queuePoster.init(initialContext, params.get("colaCorreo"), params.get("factory"));                        
            //queuePoster.init(initialContext, params.get("nombreColaAnula"), params.get("nombreFactoryAnula"));
            queuePoster.post(jsonResponse);
            queuePoster.close();
            respuesta = Constants.ESTADO_EXITOSO + "|" + "OK, mensaje colocado en la cola";
        } catch (Exception e) {
            respuesta = Constants.ESTADO_RECHAZADO + "|" + "ERROR, " + e.getMessage();
        }
        return respuesta;
    }

    public HashMap<String, String> obtenerDTECola(String strJson) {
        JsonObject objectJson = parser.parse(strJson).getAsJsonObject();
        params.put("codigogeneracion", objectJson.get("codigogeneracion").getAsString());
        params.put("dte", objectJson.getAsJsonObject("dte").toString());
        params.put("tipoDte", objectJson.getAsJsonObject("dte").get("tipoDte").getAsString());
        params.put("dteOrigen", objectJson.getAsJsonObject("dteOrigen").toString());
        return params;
    }

}
