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
@RequestMapping("/envioDTE")
public class DTEController {

    static Logger log = Logger.getLogger(DTEController.class);

    @Autowired
    private DocumentService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> firstPage() {
        return ResponseEntity.ok("Hello World!!");
    }

    /**
     * Metodo para procesar el json DTE del MH
     *
     * @param codgeneracion identificador unico del documento DTE
     * @param codtd identificador unico del tipo de documento DTE
     * @param json { "identificacion": { "version": 3, "ambiente": "01",
     * "tipoDte": "03", "numeroControl": "DTE-03-00000000-000000000018764",
     * "codigoGeneracion": "67D90B38-8A93-452B-9C67-A156278E21B3", "tipoModelo":
     * 1, "tipoOperacion": 1, "tipoContingencia": null, "motivoContin": null,
     * "fecEmi": "2022-05-19", "horEmi": "16:01:33", "tipoMoneda": "USD" },
     * "documentoRelacionado": null, "emisor": { "nit": "06141704670022", "nrc":
     * "3050", "nombre": "PRODUCTIVE BUSINESS SOLUTIONS EL SALVADOR, SA DE CV",
     * "codActividad": "46510", "descActividad": "Venta al por mayor de
     * computadoras, equipo periferico y programas informaticos",
     * "nombreComercial": "PBS EL SALVADOR, SA DE CV", "tipoEstablecimiento":
     * "02", "direccion": { "departamento": "05", "municipio": "01",
     * "complemento": "Final Boulevard Santa Elena y Boulevard Orden de Malta,
     * Edificio Xerox" }, "telefono": "22393000", "correo":
     * "ernesto.guevara@grouppbs.com", "codEstableMH": null, "codEstable": null,
     * "codPuntoVentaMH": null, "codPuntoVenta": null }, "receptor": { "nit":
     * "06140108580017", "nrc": "418", "nombre": "FREUND DE EL SALVADOR, S.A. DE
     * C.V.", "codActividad": "46632", "descActividad": "VENTA AL POR MAYOR DE
     * ARTÍCULOS DE FERRETERÍA Y PINTURERÍAS\n\r", "nombreComercial": "FREUND DE
     * EL SALVADOR, S.A. DE C.V.", "direccion": { "departamento": "06",
     * "municipio": "14", "complemento": "PROLONGACION AUTOPISTA NORTE COL.
     * MOMPEGON, CALLE PPAL Y PJE FREUND # 3,SAN SALVADOR" }, "telefono":
     * "25008459", "correo": "marios@freundsa.com" }, "ventaTercero": null,
     * "cuerpoDocumento": [ { "numItem": 1, "tipoItem": 1, "cantidad": 1.0,
     * "codigo": "OTROS", "uniMedida": 59, "descripcion": "RENOVACION DE ORACLE
     * CLOUD SERVICES||||||||||||RENOVACION DE ORACLE CLOUD SERVICES|",
     * "precioUni": 100.0, "montoDescu": 0.0, "ventaNoSuj": 0.0, "ventaExenta":
     * 0.0, "ventaGravada": 100.0, "noGravado": 0.0, "tributos": [ "20" ],
     * "psv": 0.0 } ], "resumen": { "totalNoSuj": 0.0, "totalExenta": 0.0,
     * "totalGravada": 100.0, "subTotalVentas": 100.0, "descuNoSuj": 0.0,
     * "descuExenta": 0.0, "descuGravada": 0.0, "porcentajeDescuento": 0.0,
     * "totalDescu": 0.0, "tributos": [ { "codigo": "20", "descripcion":
     * "Impuesto al Valor Agregado 13%", "valor": 13 } ], "subTotal": 100.0,
     * "ivaPerci1": 0.0, "ivaRete1": 0.0, "reteRenta": 0.0,
     * "montoTotalOperacion": 113.0, "totalNoGravado": 0.0, "totalPagar": 113.0,
     * "totalLetras": "Ciento trece Dólares US con 00/100", "saldoFavor": 0.0,
     * "condicionOperacion": 2, "pagos": [ { "codigo": "99", "montoPago": 113.0,
     * "referencia": null, "plazo": "02", "periodo": 1 } ],
     * "numPagoElectronico": null }, "extension": { "nombEntrega": "Mauro
     * Francisco Mejía López", "docuEntrega": "015055460", "nombRecibe":
     * "EDUARDO DAVID FREUND WAIDERGOR", "docuRecibe": "00489008-3",
     * "observaciones": "Sin Observaciones", "placaVehiculo": null },
     * "apendice": [ { "campo": "apendices01", "etiqueta": "apendices01",
     * "valor": "|SAL-FO-FE1-CF-1736|1|1|" }, { "campo": "apendices02",
     * "etiqueta": "apendices02", "valor": "central-pbs|FO|0012592|13|" }, {
     * "campo": "apendices03", "etiqueta": "apendices03", "valor": "||||" }, {
     * "campo": "apendices04", "etiqueta": "apendices04", "valor": " " }, {
     * "campo": "listaEmails", "etiqueta": "listaEmails", "valor":
     * "marios@freundsa.com" }, { "campo": "direntrega", "etiqueta":
     * "direntrega", "valor": " " } ], "responseMH": { "version": 2, "ambiente":
     * "01", "versionApp": 2, "estado": "PROCESADO", "codigoGeneracion":
     * "67D90B38-8A93-452B-9C67-A156278E21B3", "numeroControl":
     * "DTE-03-00000000-000000000018764", "selloRecibido":
     * "2022E88422D758DA4BA793B983379A73A2A8YZ1E", "fhProcesamiento":
     * "19/05/2022 16:01:34", "codigoMsg": "001", "descripcionMsg": "RECIBIDO",
     * "observaciones": [] }, "codigoEmpresa": "PBS", "token":
     * "eyJhbGciOiJSUzUxMiJ9.ewogICJpZGVudGlmaWNhY2lvbiI6IHsKICAgICJ2ZXJzaW9uIjogMywKICAgICJhbWJpZW50ZSI6ICIwMSIsCiAgICAidGlwb0R0ZSI6ICIwMyIsCiAgICAibnVtZXJvQ29udHJvbCI6ICJEVEUtMDMtMDAwMDAwMDAtMDAwMDAwMDAwMDE4NzY0IiwKICAgICJjb2RpZ29HZW5lcmFjaW9uIjogIjY3RDkwQjM4LThBOTMtNDUyQi05QzY3LUExNTYyNzhFMjFCMyIsCiAgICAidGlwb01vZGVsbyI6IDEsCiAgICAidGlwb09wZXJhY2lvbiI6IDEsCiAgICAidGlwb0NvbnRpbmdlbmNpYSI6IG51bGwsCiAgICAibW90aXZvQ29udGluIjogbnVsbCwKICAgICJmZWNFbWkiOiAiMjAyMi0wNS0xOSIsCiAgICAiaG9yRW1pIjogIjE2OjAxOjMzIiwKICAgICJ0aXBvTW9uZWRhIjogIlVTRCIKICB9LAogICJkb2N1bWVudG9SZWxhY2lvbmFkbyI6IG51bGwsCiAgImVtaXNvciI6IHsKICAgICJuaXQiOiAiMDYxNDE3MDQ2NzAwMjIiLAogICAgIm5yYyI6ICIzMDUwIiwKICAgICJub21icmUiOiAiUFJPRFVDVElWRSBCVVNJTkVTUyBTT0xVVElPTlMgRUwgU0FMVkFET1IsIFNBIERFIENWIiwKICAgICJjb2RBY3RpdmlkYWQiOiAiNDY1MTAiLAogICAgImRlc2NBY3RpdmlkYWQiOiAiVmVudGEgYWwgcG9yIG1heW9yIGRlIGNvbXB1dGFkb3JhcywgZXF1aXBvIHBlcmlmZXJpY28geSBwcm9ncmFtYXMgaW5mb3JtYXRpY29zIiwKICAgICJub21icmVDb21lcmNpYWwiOiAiUEJTIEVMIFNBTFZBRE9SLCBTQSBERSBDViIsCiAgICAidGlwb0VzdGFibGVjaW1pZW50byI6ICIwMiIsCiAgICAiZGlyZWNjaW9uIjogewogICAgICAiZGVwYXJ0YW1lbnRvIjogIjA1IiwKICAgICAgIm11bmljaXBpbyI6ICIwMSIsCiAgICAgICJjb21wbGVtZW50byI6ICJGaW5hbCBCb3VsZXZhcmQgU2FudGEgRWxlbmEgeSBCb3VsZXZhcmQgT3JkZW4gZGUgTWFsdGEsIEVkaWZpY2lvIFhlcm94IgogICAgfSwKICAgICJ0ZWxlZm9ubyI6ICIyMjM5MzAwMCIsCiAgICAiY29ycmVvIjogImVybmVzdG8uZ3VldmFyYUBncm91cHBicy5jb20iLAogICAgImNvZEVzdGFibGVNSCI6IG51bGwsCiAgICAiY29kRXN0YWJsZSI6IG51bGwsCiAgICAiY29kUHVudG9WZW50YU1IIjogbnVsbCwKICAgICJjb2RQdW50b1ZlbnRhIjogbnVsbAogIH0sCiAgInJlY2VwdG9yIjogewogICAgIm5pdCI6ICIwNjE0MDEwODU4MDAxNyIsCiAgICAibnJjIjogIjQxOCIsCiAgICAibm9tYnJlIjogIkZSRVVORCBERSBFTCBTQUxWQURPUiwgUy5BLiBERSBDLlYuIiwKICAgICJjb2RBY3RpdmlkYWQiOiAiNDY2MzIiLAogICAgImRlc2NBY3RpdmlkYWQiOiAiVkVOVEEgQUwgUE9SIE1BWU9SIERFIEFSVMONQ1VMT1MgREUgRkVSUkVURVLDjUEgWSBQSU5UVVJFUsONQVNcblxyIiwKICAgICJub21icmVDb21lcmNpYWwiOiAiRlJFVU5EIERFIEVMIFNBTFZBRE9SLCBTLkEuIERFIEMuVi4iLAogICAgImRpcmVjY2lvbiI6IHsKICAgICAgImRlcGFydGFtZW50byI6ICIwNiIsCiAgICAgICJtdW5pY2lwaW8iOiAiMTQiLAogICAgICAiY29tcGxlbWVudG8iOiAiUFJPTE9OR0FDSU9OIEFVVE9QSVNUQSBOT1JURSBDT0wuIE1PTVBFR09OLCBDQUxMRSBQUEFMIFkgUEpFIEZSRVVORCAjIDMsU0FOIFNBTFZBRE9SIgogICAgfSwKICAgICJ0ZWxlZm9ubyI6ICIyNTAwODQ1OSIsCiAgICAiY29ycmVvIjogIm1hcmlvc0BmcmV1bmRzYS5jb20iCiAgfSwKICAib3Ryb3NEb2N1bWVudG9zIjogbnVsbCwKICAidmVudGFUZXJjZXJvIjogbnVsbCwKICAiY3VlcnBvRG9jdW1lbnRvIjogWwogICAgewogICAgICAibnVtSXRlbSI6IDEsCiAgICAgICJ0aXBvSXRlbSI6IDEsCiAgICAgICJudW1lcm9Eb2N1bWVudG8iOiBudWxsLAogICAgICAiY29kaWdvIjogIk9UUk9TIiwKICAgICAgImNvZFRyaWJ1dG8iOiBudWxsLAogICAgICAiZGVzY3JpcGNpb24iOiAiUkVOT1ZBQ0lPTiBERSBPUkFDTEUgQ0xPVUQgU0VSVklDRVN8fHx8fHx8fHx8fHxSRU5PVkFDSU9OIERFIE9SQUNMRSBDTE9VRCBTRVJWSUNFU3wiLAogICAgICAiY2FudGlkYWQiOiAxLjAsCiAgICAgICJ1bmlNZWRpZGEiOiA1OSwKICAgICAgInByZWNpb1VuaSI6IDEzMjE1LjAsCiAgICAgICJtb250b0Rlc2N1IjogMC4wLAogICAgICAidmVudGFOb1N1aiI6IDAuMCwKICAgICAgInZlbnRhRXhlbnRhIjogMC4wLAogICAgICAidmVudGFHcmF2YWRhIjogMTMyMTUuMCwKICAgICAgInRyaWJ1dG9zIjogWwogICAgICAgICIyMCIKICAgICAgXSwKICAgICAgInBzdiI6IDAuMCwKICAgICAgIm5vR3JhdmFkbyI6IDAuMAogICAgfQogIF0sCiAgInJlc3VtZW4iOiB7CiAgICAidG90YWxOb1N1aiI6IDAuMCwKICAgICJ0b3RhbEV4ZW50YSI6IDAuMCwKICAgICJ0b3RhbEdyYXZhZGEiOiAxMzIxNS4wLAogICAgInN1YlRvdGFsVmVudGFzIjogMTMyMTUuMCwKICAgICJkZXNjdU5vU3VqIjogMC4wLAogICAgImRlc2N1RXhlbnRhIjogMC4wLAogICAgImRlc2N1R3JhdmFkYSI6IDAuMCwKICAgICJwb3JjZW50YWplRGVzY3VlbnRvIjogMC4wLAogICAgInRvdGFsRGVzY3UiOiAwLjAsCiAgICAidHJpYnV0b3MiOiBbCiAgICAgIHsKICAgICAgICAiY29kaWdvIjogIjIwIiwKICAgICAgICAiZGVzY3JpcGNpb24iOiAiSW1wdWVzdG8gIGFsIFZhbG9yIEFncmVnYWRvIDEzJSIsCiAgICAgICAgInZhbG9yIjogMTcxNy45NQogICAgICB9CiAgICBdLAogICAgInN1YlRvdGFsIjogMTMyMTUuMCwKICAgICJpdmFQZXJjaTEiOiAwLjAsCiAgICAiaXZhUmV0ZTEiOiAwLjAsCiAgICAicmV0ZVJlbnRhIjogMC4wLAogICAgIm1vbnRvVG90YWxPcGVyYWNpb24iOiAxNDkzMi45NSwKICAgICJ0b3RhbE5vR3JhdmFkbyI6IDAuMCwKICAgICJ0b3RhbFBhZ2FyIjogMTQ5MzIuOTUsCiAgICAidG90YWxMZXRyYXMiOiAiQ2F0b3JjZSBNaWwgTm92ZWNpZW50b3MgVHJlaW50YSB5IERvcyBEw7NsYXJlcyBVUyBjb24gOTUvMTAwIiwKICAgICJzYWxkb0Zhdm9yIjogMC4wLAogICAgImNvbmRpY2lvbk9wZXJhY2lvbiI6IDIsCiAgICAicGFnb3MiOiBbCiAgICAgIHsKICAgICAgICAiY29kaWdvIjogIjk5IiwKICAgICAgICAibW9udG9QYWdvIjogMTQ5MzIuOTUsCiAgICAgICAgInJlZmVyZW5jaWEiOiBudWxsLAogICAgICAgICJwbGF6byI6ICIwMiIsCiAgICAgICAgInBlcmlvZG8iOiAxCiAgICAgIH0KICAgIF0sCiAgICAibnVtUGFnb0VsZWN0cm9uaWNvIjogbnVsbAogIH0sCiAgImV4dGVuc2lvbiI6IHsKICAgICJub21iRW50cmVnYSI6ICJNYXVybyBGcmFuY2lzY28gTWVqw61hIEzDs3BleiIsCiAgICAiZG9jdUVudHJlZ2EiOiAiMDE1MDU1NDYwIiwKICAgICJub21iUmVjaWJlIjogIkVEVUFSRE8gREFWSUQgRlJFVU5EIFdBSURFUkdPUiIsCiAgICAiZG9jdVJlY2liZSI6ICIwMDQ4OTAwOC0zIiwKICAgICJvYnNlcnZhY2lvbmVzIjogIlNpbiBPYnNlcnZhY2lvbmVzIiwKICAgICJwbGFjYVZlaGljdWxvIjogbnVsbAogIH0sCiAgImFwZW5kaWNlIjogWwogICAgewogICAgICAiY2FtcG8iOiAiYXBlbmRpY2VzMDEiLAogICAgICAiZXRpcXVldGEiOiAiYXBlbmRpY2VzMDEiLAogICAgICAidmFsb3IiOiAifFNBTC1GTy1GRTEtQ0YtMTczNnwxfDF8IgogICAgfSwKICAgIHsKICAgICAgImNhbXBvIjogImFwZW5kaWNlczAyIiwKICAgICAgImV0aXF1ZXRhIjogImFwZW5kaWNlczAyIiwKICAgICAgInZhbG9yIjogImNlbnRyYWwtcGJzfEZPfDAwMTI1OTJ8MTN8IgogICAgfSwKICAgIHsKICAgICAgImNhbXBvIjogImFwZW5kaWNlczAzIiwKICAgICAgImV0aXF1ZXRhIjogImFwZW5kaWNlczAzIiwKICAgICAgInZhbG9yIjogInx8fHwiCiAgICB9LAogICAgewogICAgICAiY2FtcG8iOiAiYXBlbmRpY2VzMDQiLAogICAgICAiZXRpcXVldGEiOiAiYXBlbmRpY2VzMDQiLAogICAgICAidmFsb3IiOiAiICIKICAgIH0sCiAgICB7CiAgICAgICJjYW1wbyI6ICJsaXN0YUVtYWlscyIsCiAgICAgICJldGlxdWV0YSI6ICJsaXN0YUVtYWlscyIsCiAgICAgICJ2YWxvciI6ICJtYXJpb3NAZnJldW5kc2EuY29tIgogICAgfSwKICAgIHsKICAgICAgImNhbXBvIjogImRpcmVudHJlZ2EiLAogICAgICAiZXRpcXVldGEiOiAiZGlyZW50cmVnYSIsCiAgICAgICJ2YWxvciI6ICIgIgogICAgfQogIF0KfQ.S4Yf6K95LyEiA_TM6wKk_xEKx58MMvVscdX8p3PVfoVaARZgykZycSK9zijvJvTtFQbXGv5dPj7c0beNA-qSdHTSz6sVyLva190kIDUdQWgHVmg3B_dFxquRrckKqfPWjIZH_hGptbrfWNhWC3oY7_deEAzGFT-AXG80cJ1i2SYxTHG4rRNRTdMrKOJIKfY7UaXct3pl1VPDIJJUZA4_Pj7yl2t4GXa811zEdFaxKTVisv-dt00zzGgLBD8eNh6dowrSEJiTSYsnCPArB74mVJHAy7vLeRLiM9c2wvd3K3hBaFvcfSk2yaODzDGiCle-mHTgyeh1Q_9EZ_mcX2v9aA"
     * }
     * @return mensaje sobre el resultado del procesamiento del documento DTE
     */
    //@RequestMapping(value = "/procesarJson", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN)
    @PostMapping(value = "/procesarJson/{codgeneracion}/{codtd}",produces = "application/json", consumes = "application/json")
    public String processJson(@PathVariable String codgeneracion, @PathVariable String codtd, @RequestBody String strGson) {
        //String strGson;
        String respuesta = "";
        JsonParser parser = new JsonParser();
        JsonObject gsonObj;
        String respuestaSchema="";
        try {
            respuestaSchema= service.validarSchema(strGson, codtd);
            if (respuestaSchema.contains("ERROR")) {
                respuesta = Constants.ESTADO_RECHAZADO+"|"+service.actualizar_estador_dte(codgeneracion, Constants.ESTADO_RECHAZADO, Constants.ERROR_SCHEMA, respuestaSchema);
            }
            else{
                respuesta=service.firmarDocumento(strGson, codgeneracion, codtd);
            }
            log.trace("DTEController.processJson.respuesta -->" + respuesta);
            System.err.println("DTEController.processJson.respuesta -->" + respuesta);

        } catch (JsonSyntaxException e) {
            log.error(">> Error parsing StringBuilder " + e.getMessage());
            gsonObj = parser.parse("{  \"t_result\": 0, \"t_message\":\"" + e.getMessage() + "\" }").getAsJsonObject();
            System.out.println("OperacionesServiciosController.ejecutaroperacion_no_auth.JsonSyntaxException.gsonObj -->" + gsonObj);
        }
        return respuesta;

    }
}
