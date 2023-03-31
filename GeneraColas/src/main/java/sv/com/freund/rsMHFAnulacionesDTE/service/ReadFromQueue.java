package sv.com.freund.rsMHFAnulacionesDTE.service;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author misaelg
 */
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.HashMap;
import org.apache.log4j.Logger;
import sv.com.freund.rsMHFAnulacionesDTE.security.Constants;

public class ReadFromQueue implements MessageListener {

    static Logger log = Logger.getLogger(ReadFromQueue.class);

    public final static String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";

    private QueueConnectionFactory queueConnectionFactory;
    private QueueSession queueSession;
    private QueueConnection queueConnection;
    private QueueReceiver queueReceiver;
    private Queue queue;
    public Boolean quit = false;

    private EnvioDTEService service;
    private Integer countListener;
    private HashMap<String, String> params = null;
    
    JsonParser parser = new JsonParser();

    public ReadFromQueue(EnvioDTEService service, Integer countListener) {
        if (service == null) {
            System.err.println("El service es null en el readqueueq");
            log.trace("El service es null en el readqueueq");
        }
        this.service = service;
        this.countListener = countListener;
        this.service.obtenerParametrosFirmar();
        params = this.service.getParams();
    }

    public void inicializar(Context context, String queueName) throws NamingException, JMSException {
        queueConnectionFactory = (QueueConnectionFactory) context.lookup(params.get("nombreFactoryAnula"));
        queue = (Queue) context.lookup(params.get("nombreColaAnula"));
        queueConnection = queueConnectionFactory.createQueueConnection();
        queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queueReceiver = queueSession.createReceiver(queue);
        queueReceiver.setMessageListener(this);
        queueConnection.start();
    }

    public void close() throws JMSException {
        queueReceiver.close();
        queueSession.close();
        queueConnection.close();
    }

    public InitialContext getInitialContext() throws NamingException {
        /*Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
		p.put(Context.PROVIDER_URL, "t3://localhost:7001");*/

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, params.get("servercola"));
        return new InitialContext(env);
    }

    public void init() {
       if (Constants.MODE_DEBUG) {
        log.trace("Iniciando el proceso " + countListener + " de la lectura de cola...");
        System.err.println("Iniciando el proceso " + countListener + " de la lectura de cola...");
       }
       
        try {
            InitialContext initialContext = getInitialContext();
            inicializar(initialContext, params.get("nombreColaAnula"));

            synchronized (this) {
                while (!quit) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {
                        log.error("Ocurrio un error en el proceso " + countListener + ".. " + ie.getMessage());
                        System.err.println("Ocurrio un error en el proceso " + countListener + ".. " + ie.getMessage());
                    }
                    close();

                }
            }

            close();
            if (Constants.MODE_DEBUG) {
             log.trace("Finalizando el proceso..." + countListener);
             System.err.println("Finalizando el proceso..." + countListener);
            }
            
        } catch (Exception e) {
            log.error("Ocurrio un error en la cola en el proceso " + countListener + ".. " + e.getMessage());
            System.err.println("Ocurrio un error en la cola en el proceso " + countListener + ".. " + e.getMessage());
        }

    }

    @Override
    public void onMessage(Message message) {

        //
        String respuesta = "";
        try {
            String msgText;
            if (message instanceof TextMessage) {
                msgText = ((TextMessage) message).getText();
            } else {
                msgText = message.toString();
            }
      
            try {
                FileWriter fw = new FileWriter("JMS.xml");
                fw.write(msgText);
                fw.close();
                //HashMap<String, String> dte = service.obtenerDTECola(msgText);
                HashMap<String, String> dte = obtenerDTECola(msgText);
                //
                 if (Constants.MODE_DEBUG) {
                    log.trace("El mensaje recibido en el proceso " + countListener + " de la cola es: " + msgText+ "codigogeneracion"+ dte.get("codigogeneracion"));
                    System.err.println("El mensaje recibido en el proceso " + countListener + " de la cola es: " + msgText + "codigogeneracion"+ dte.get("codigogeneracion"));
                  }
                //
                //
                String recepcion = service.recepcionDTE(dte.get("dte"), dte.get("codigogeneracion"), dte.get("tipoDte"));
                if (Constants.MODE_DEBUG) {                  
                  System.err.println("codigogeneracion "+ dte.get("codigogeneracion")+" El valor de recepcion es: "+ recepcion);  
                  log.trace("codigogeneracion"+ dte.get("codigogeneracion")+ "El valor de recepcion es: "+ recepcion);
                };
                //
                if (recepcion.contains("NOT_FOUND")) {
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + service.actualizar_estador_dte(dte.get("codigogeneracion"), Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_RECEPCION, recepcion.split("NOT_FOUND,")[1].trim());
                }
                if (recepcion.contains("FORBIDDEN")) {
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + service.actualizar_estador_dte(dte.get("codigogeneracion"), Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_FORBIDDEN, recepcion.split("FORBIDDEN,")[1].trim());
                }
                if (recepcion.contains("BAD_REQUEST")) {
                    JsonObject objectJson = parser.parse(recepcion.split("BAD_REQUEST,")[1].trim()).getAsJsonObject();
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + service.actualizar_estador_dte(dte.get("codigogeneracion"), Constants.ESTADO_RECHAZADO, Integer.parseInt(objectJson.get("clasificaMsg").getAsString()), recepcion.split("BAD_REQUEST,")[1].trim());
                }
                if (recepcion.contains("ERROR")) {
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + service.actualizar_estador_dte(dte.get("codigogeneracion"), Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_RECEPCION, recepcion.split("ERROR,")[1].trim());
                }
                if (recepcion.contains("EXCEPTION")) {
                    respuesta = Constants.ESTADO_RECHAZADO + "|" + service.actualizar_estador_dte(dte.get("codigogeneracion"), Constants.ESTADO_RECHAZADO, Constants.ERROR_DTE_RECEPCION, recepcion.split("EXCEPTION,")[1].trim());
                }
                if (recepcion.contains("OK")) {
                    JsonObject objectJson = parser.parse(recepcion.split("OK,")[1].trim()).getAsJsonObject();
                    JsonObject dteOrigen = parser.parse(params.get("dteOrigen").trim()).getAsJsonObject();
                    dteOrigen.addProperty("selloRecibido",objectJson.get("selloRecibido").getAsString());
                    System.err.println("EL DTE ORIGEN MODIFICADO ES: "+ dteOrigen.toString());
                    respuesta =service.actualizar_documento_valido(dte.get("codigogeneracion"), objectJson.get("codigoMsg").getAsString().equals("001")?Constants.ESTADO_EXITOSO: Constants.ESTADO_ERROR, Integer.parseInt(objectJson.get("clasificaMsg").getAsString()), dteOrigen.toString(), objectJson.get("selloRecibido").getAsString());
                    if(respuesta.contains("OK")){
                        respuesta= service.setearColaCorreo(dteOrigen.toString());
                    }
                }
                if (Constants.MODE_DEBUG) {
                    log.trace("El mensaje respuesta en el proceso " + countListener + " de la cola es: " + respuesta);
                    System.err.println("El mensaje respuesta en el proceso " + countListener + " de la cola es: " + respuesta); 
                }
              
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (msgText.equalsIgnoreCase("quit")) {
                synchronized (this) {
                if (Constants.MODE_DEBUG) {
                   log.trace("Se termino la lectura en el proceso " + countListener);
                   System.err.println("Se termino la lectura en el proceso " + countListener);
                }                    
                quit = true;
                this.notifyAll();
            }
            }
        } catch (JMSException jmsException) {
            log.error("El proceso " + countListener + " Exception: " + jmsException.getMessage());
            System.err.println("El proceso " + countListener + " Exception: " + jmsException.getMessage());
        } finally {
        }
    }
    
    public HashMap<String, String> obtenerDTECola(String strJson) {
        JsonObject objectJson = parser.parse(strJson).getAsJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("codigogeneracion", objectJson.get("codigogeneracion").getAsString());
        params.put("dte", objectJson.getAsJsonObject("dte").toString());
        params.put("tipoDte", objectJson.getAsJsonObject("dte").get("tipoDte").getAsString());
        params.put("dteOrigen", objectJson.getAsJsonObject("dteOrigen").toString());
        return params;
    }
}
