package it.unisannio.rosariogoglia.listner;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class ContextInizializer
 *
 */
@WebListener
public class ContextInizializer implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public ContextInizializer() {
        // TODO Auto-generated constructor stub
    }


	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
        
    	Map<Integer, Thread> mapThread = new HashMap<Integer, Thread>();
    	
    	ServletContext context = sce.getServletContext();
    	
    	context.setAttribute("mapThread", mapThread); //inserisco la hashmap nel contesto
    	
    	
    }
    
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    
    	ServletContext context = sce.getServletContext();
        context.removeAttribute("mapThread");
    
    }
    
    
	
}
