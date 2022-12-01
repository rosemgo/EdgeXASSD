package it.unisannio.rosariogoglia.listner;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import it.unisannio.rosariogoglia.exportCloud.MOMExportDataCloud;

/**
 * Application Lifecycle Listener implementation class ContextInizializer
 *
 */
@WebListener
public class ContextInitializer implements ServletContextListener {

	private Logger logger = Logger.getLogger(ContextInitializer.class); 
	
    /**
     * Default constructor. 
     */
    public ContextInitializer() {
        // TODO Auto-generated constructor stub
    }


	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
        
    	ServletContext context = sce.getServletContext();
    	
    	//***** CONFIGURAZIONE LOG4J ***
    	
    	/* In questo modo viene preparato il log4j ad ogni avvio di Tomcat*/
             
        //log4j-config-location è il nome scelto nel web.xml per indicare la risorsa log4j.properties
        //String log4jConfigFile = context.getInitParameter("log4j-config-location");
        //dato che non uso il web.xml indico direttamente il percorso del log4j.properties
        String log4jConfigFile = "log4j.properties";
                
        String fullPath = context.getRealPath("") + log4jConfigFile;
        System.out.println("Stampo il fullpath: " + fullPath);
        
        System.out.println("Nel LISTNER CONTEXTINITIALIZER ");
        System.out.println("Stampo il realpath: " + context.getRealPath(""));
                
        //La classe PropertyConfigurator è importante perchè consente di configurare un Logger usando un file .properties. I Logger definiti funzionano perchè c'è la seguente istruzione
        PropertyConfigurator.configure(fullPath);
        
        /*setto la proprietà rootPath in modo da poterla usare sempre nel sistema, e mi da il percorso in cui Tomcat deploya la mia applicazione
         * in modo tale da poterla usare nel log4j.properties per la creazione del file di log*/
        System.setProperty("rootPath", context.getRealPath("/")); //in realtà la proprietà rootPath non è stata più usata nel log4j.properties per un problema di deploy con Tomcat
        System.out.println("Stampo il rootPath: " + context.getRealPath("/"));
                
        //*******    	
    	 	
    	
    	//MAPTHREAD E' UNA MAPPA CHE HA COME CHIAVE L'ID DEL NODO SENSORE E COME VALORE IL THREAD ASSOCIATO A QUEL NODO SENSORE
    	//IN SEGUITO ALL'ASSOCIAZIONE DEI SENSORI AD UN NODO SENSORE , SI AVVIA UN THRED CHE FA PARTITE IL MONITORING DEI SENSORI (ServletAssociateSensorToNode)
    	Map<Integer, Thread> mapThread = new HashMap<Integer, Thread>();
    	
    	context.setAttribute("mapThread", mapThread); //inserisco la hashmap nel contesto
    	
    	
    	
    	//FAR PARTIRE IL THREAD CHE PRELEVA I DATI ESPORTATI
       	MOMExportDataCloud mom = new MOMExportDataCloud();
    	mom.start();
    	System.out.println("THREAD DI ESPORTAZIONE PARTITO");
    	    	
    }
    
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    
    	ServletContext context = sce.getServletContext();
        context.removeAttribute("mapThread");
        
        
        if(LogManager.class.getClassLoader().equals(this.getClass().getClassLoader())){
			logger.info("Log4j was loaded by application classloader; shutting down.");
			LogManager.shutdown();
		}
		else{
			logger.info("Log4j was loaded by some other ClassLoader; not shutting down.");
		}
	        
    
    }
    
    
	
}
