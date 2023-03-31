package sv.com.freund.rsMHFFactuacionElectronica.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import sv.com.freund.rsMHFFactuacionElectronica.security.Constants;
import sv.com.freund.rsMHFFactuacionElectronica.service.DocumentService;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RequestMapping("/AnulacionDTE")
public class DTEAnulacionController {

    static Logger log = Logger.getLogger(DTEAnulacionController.class);

    @Autowired
    private DocumentService service;

    /**
     * Metodo para procesar el json DTE del MH
     *
     * @param codgeneracion identificador unico del documento DTE
     * @param codtd identificador unico del tipo de documento DTE
     * @param json documento a anular en formato JSON segun formato MH
     * @return mensaje sobre el resultado del procesamiento del documento DTE
     */
    //@RequestMapping(value = "/procesarJson", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN)
    @PostMapping(value = "/procesarAnulacion/{codgeneracion}/{codTipoDoc}",
                 produces = "application/json", 
                 consumes = "application/json")
    public String procesaAnulacionJson(@PathVariable String codgeneracion, 
                                       @PathVariable String codTipoDoc, 
                                       @RequestBody String strGson) {
        //String strGson;
        String  respuesta = "";
        JsonParser parser = new JsonParser();
        JsonObject gsonObj;
        String respuestaSchema="";
        Gson gson = new Gson();
        //log.trace("DTEController.processJson.json -->" + strGson);
        //System.err.println("DTEController.processJson.json -->" + strGson);
        try {
             if (Constants.MODE_DEBUG) {
                //Obteniendo el objeto json enviado en el body
                //strGson = gson.toJson(json);
                log.trace("DTEController.processJson.strGson -->" + strGson);
                System.err.println("DTEController.processJson.strGson -->" + strGson);
             }
            respuestaSchema= service.validarSchema(strGson, codTipoDoc);
            
            if (respuestaSchema.contains("ERROR")) {
                respuesta = Constants.ESTADO_RECHAZADO+"|"+service.actualizar_estado_anula(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_SCHEMA, respuestaSchema);
            }
            else{
                respuesta=service.firmarDocumentoAnula(strGson, codgeneracion, codTipoDoc);
                //respuesta = "Dentro de firmar Documento"; //Cavalos
            }
           if (Constants.MODE_DEBUG) {
              log.trace("DTEAnulacionController.procesaAnulacionJson.respuesta -->" + respuesta);
              System.err.println("DTEAnulacionController.procesaAnulacionJson.respuesta -->" + respuesta);
           }            
        } catch (JsonSyntaxException e) {
            log.error(">> DTEAnulacionController.procesaAnulacionJson. Error parsing StringBuilder " + e.getMessage());
            log.error(e);
            gsonObj = parser.parse("{  \"t_result\": 0, \"t_message\":\"" + e.getMessage() + "\" }").getAsJsonObject();            
        }

        return respuesta;

    }
}
