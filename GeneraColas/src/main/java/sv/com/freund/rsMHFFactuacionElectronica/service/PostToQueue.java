/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFFactuacionElectronica.service;

/**
 *
 * @author misaelg
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import sv.com.freund.rsMHFFactuacionElectronica.security.Constants;
public class PostToQueue {




    private QueueConnectionFactory queueConnectionFactory;
    private QueueSession queueSession;
    private QueueConnection queueConnection;
    private QueueSender queueSender;
    private Queue queue;
    private TextMessage message;

    public void init(Context context, String queueName, String nombreFactory) throws NamingException, JMSException {
        queueConnectionFactory = (QueueConnectionFactory) context.lookup(nombreFactory);
        queue = (Queue) context.lookup(queueName);
        queueConnection = queueConnectionFactory.createQueueConnection();
        queueSession = queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
        queueSender = queueSession.createSender(queue);
        message = queueSession.createTextMessage();
        queueConnection.start();
    }

    public void post(String msg) throws JMSException {
        message.setText(msg);
        queueSender.send(message);
    }

    public void close() throws JMSException {
        queueSender.close();
        queueSession.close();
        queueConnection.close();
    }

    protected void sendToServer(PostToQueue queuePoster) throws IOException, JMSException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        boolean readFlag = true;
        System.out.println("Sending JMS XML Content to WebLogic server");
        while(readFlag) {
        System.out.println("Enter Messages: ");
        String msg = "Hey";
        if(msg.equals("quit")){
            queuePoster.post(msg);
            System.exit(0);
        }
        queuePoster.post(msg);
        System.out.println();
        }
            bufferedReader.close();
    }

    public InitialContext getInitialContext(String server) throws NamingException {

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,Constants.JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, server);
        return new InitialContext(env);
    }

}