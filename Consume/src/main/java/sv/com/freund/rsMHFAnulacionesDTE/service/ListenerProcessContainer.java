/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFAnulacionesDTE.service;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import sv.com.freund.rsMHFAnulacionesDTE.security.Constants;

/**
 *
 * @author misaelg
 */


@Configuration
public class ListenerProcessContainer {
    
    @Autowired
    private EnvioDTEService service;
    
    List<ProcessThread> listaHilos= new ArrayList<ProcessThread>();

    static Logger log = Logger.getLogger(ListenerProcessContainer.class);
    
    public ListenerProcessContainer(){
        
    }
    
    @PostConstruct
    public void init(){
        ProcessThread proceso=null;
         if(Constants.MODE_DEBUG){
           System.err.println("Iniciando el proceso del Hilo");
           log.trace("Iniciando el proceso del Hilo");
         }
        
        for(int i=1; i<=4;i++){
            if(service==null){
                System.err.println("El service es null al iniciar el proceso");
                log.trace("El service es null al iniciar el proceso");
            }
            proceso= new ProcessThread(service, i);
            proceso.start();
            listaHilos.add(proceso);
        }
    }
    
    @PreDestroy
    public void destroy(){
        if(Constants.MODE_DEBUG){
           System.err.println("Finalizando los procesos de los Hilos");
           log.trace("Finalizando los procesos de los Hilos");
        }
       
        int i=1;
       for(ProcessThread proceso : listaHilos){
            System.err.println("Finalizando el proceso de los Hilo "+i);
            log.trace("Finalizando el proceso de los Hilo "+i);
            i++;
           proceso.interrupt();
       } 
    }

    public void AgregaHilosAdd(ProcessThread listaHilos) {
        this.listaHilos.add(listaHilos);
    }
    
    
}
