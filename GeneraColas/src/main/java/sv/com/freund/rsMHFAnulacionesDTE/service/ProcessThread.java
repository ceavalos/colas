/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFAnulacionesDTE.service;

import org.apache.log4j.Logger;
import sv.com.freund.rsMHFAnulacionesDTE.security.Constants;

/**
 *
 * @author misaelg
 */
public class ProcessThread extends Thread {
    
    static Logger log = Logger.getLogger(ProcessThread.class);

    private ReadFromQueue readFromQueue = null;
    private Integer countListener ;
    
    public ProcessThread(EnvioDTEService service, Integer countListener) {
        if (service == null) {
            System.err.println("El service es null en el processthread");
            log.trace("El service es null en el processthread");
        }
        this.countListener = countListener;
        //
        readFromQueue = new ReadFromQueue(service, countListener);
    }

    @Override
    public void run() {
         if(Constants.MODE_DEBUG){
           System.err.println("Se va iniciar el proceso del hilo " + this.countListener);
           log.trace("Se va iniciar el proceso del hilo " + this.countListener);
         }
         //
        readFromQueue.init();
        //
         if(Constants.MODE_DEBUG){             
            System.err.println("Se va terminar el proceso del hilo");
            log.trace("Se va terminar el proceso del hilo");
         }
        
    }
}
