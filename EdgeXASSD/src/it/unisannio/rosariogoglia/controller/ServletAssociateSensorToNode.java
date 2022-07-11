package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.unisannio.rosariogoglia.dao.SensorDAO;
import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.model.SensorNode;

/**
 * Servlet utilizzata per associare una lista sensori ad un Nodo sensore
 */
@WebServlet("/ServletAssociateSensorToNode")
public class ServletAssociateSensorToNode extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("SONO IN GET");		
		
        String idNS = request.getParameter("idNodoSensore");
        String idS1 = request.getParameter("idSensore1");
        String idS2 = request.getParameter("idSensore2");
        String idS3 = request.getParameter("idSensore3");
		
        System.out.println("Nodo Sensore : " + idNS);
        System.out.println("Sensore 1: " + idS1);
        System.out.println("Sensore 2: " + idS2);
        System.out.println("Sensore 3: " + idS3);
		
        Integer idNodoSensore = Integer.parseInt(idNS);
        Integer idSensor1= Integer.parseInt(idS1);
        Integer idSensor2 = Integer.parseInt(idS2);
        Integer idSensor3 = Integer.parseInt(idS3);
             
        
        List<Integer> idSensors = new ArrayList<Integer>();
        if(idSensor1 != null)
        	idSensors.add(idSensor1);
        if(idSensor2 != null)
        	idSensors.add(idSensor2);
        if(idSensor3 != null)
        	idSensors.add(idSensor3);
        
        
        SensorNodeDAO snDAO = new SensorNodeDAO();
		SensorDAO sDAO = new SensorDAO();
		
		int insertRow = -1;
		//ASSOCIO LA LISTA DI SENSORI AL NODO SENSORE NEL DATABASE
		insertRow = snDAO.insertSensorNodeHasSensors(idNodoSensore, idSensors);
		
		System.out.println("INSERT ROW: " + insertRow);
		if(insertRow != -1){			
			for(int i=0; i<idSensors.size(); i++)
				System.out.println("SensorNodeId " + idNodoSensore + " associato correttamente al sensore " + sDAO.getSensorById(idSensors.get(i)).getName() + "!!!"); 
		}	
		
		System.out.println("ID NODO SENSORE DA CERCARE: " + idNodoSensore);
		SensorNode sensorNode = snDAO.getSensorNodeByID(idNodoSensore); //sto caricando il nodo sensore con tutti i sensori associati
		System.out.println("SENSOR NODE TROVATO: " + sensorNode.toString());
			
		//inserire il thread in un mappa statica fatta di chiave id del nodo sensore e valore il thread in modo da ricordarsi tutti i thread avviati e se viene effettuato un cambiamento al nodo sensore bisogna arrestare il thread. 
		ServletContext context = getServletContext();
	    Map<Integer, Thread> mapThread = (Map<Integer, Thread>) context.getAttribute("mapThread");
	    
	    //se già esiste un thread in esecuzione sul nodo sensore caricato, è necessario ucciderlo ed avviarne uno nuovo in quanto sono stati associati nuovi sensori al nodo sensore
	    //PER OGNI NODO SENSORE DEVO AVERE UN SOLO THREAD IN ESECUZIONE
	    if(mapThread.get(sensorNode.getIdSensorNode()) != null) {
	    	Integer idSensorNodeToKill = sensorNode.getIdSensorNode();
	    	try{ 
	    		mapThread.get(idSensorNodeToKill).interrupt(); //ammazzo il thread	    	
	    	}catch(Exception e){System.out.println("Exception handled "+e);}
	    } 		
		
	    //devo avviare il thread per il nodo sensore selezionato
		Thread threadSN = new Thread(sensorNode);
		threadSN.start();

		System.out.println("thread avviato");
       			
		mapThread.put(idNodoSensore, threadSN);
        
        //stampo il contenuto della mappa
        Iterator<Map.Entry<Integer, Thread>> entries = mapThread.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Thread> entry = entries.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        
 /*     Altro metodo per stampare il contenuto della hashmap    
    	for (Map.Entry<Integer, Thread> entry : mapThread.entrySet()) {
            Integer key = entry.getKey();
            Thread value = entry.getValue();
            System.out.println("Key = " + key + ", Value = " + value);
        }
 */          
        
  //     this.printMap(mapThread);

        
                
        
        
        
        
        
   /*     
        
    //QUESTO PEZZO DI CODICE VA INSERITO NELLA SERVLET CHE ANDRà A MODIFICARE I SENSORI ASSOCIATI AL NODO SENSORE    
        
        long time1 = System.currentTimeMillis();		
		long time2 = 0;		
		//aspetta 10 secondi 
		time2 = System.currentTimeMillis();
		System.out.println("PRIMA DELL'ATTESA");
		while(time2 - time1 < 100000) {
			
			time2 = System.currentTimeMillis();
			
		}
		
        System.out.println("DEVO AMMAZZARE IL THREAD");
        //PROVO AD AMMAZARE IL THREAD
        Integer idSensorNodeToKill = sensorNode.getIdSensorNode(); //idSensorNodeToKill sarà passato dalla pagina jsp che utilizzeremo per modificare il nodo sensore: request.getParameter("idSensorNodeToKill")
        mapThread = (Map<Integer, Thread>) context.getAttribute("mapThread");
        
        try{  
        	mapThread.get(idSensorNodeToKill).interrupt(); //uccido il thread
       
        /* POSSO UCCIDERE IL THREAD ANCHE SENZA LA HASHMAP, MA SETTANDO UNA VARIABILE BOLLEANA A FALSE, E PROTEGGERE IL METODO run() DEL THREAD CON QUELLA VARIABILE 
         	sensorNode = snDAO.getSensorNodeByID(idNodoSensore);
        	sensorNode.stopThreadh();
        */
  /*      }catch(Exception e){System.out.println("Exception handled "+e);}  
        
        System.out.println("thread ammazzato");
 
        */
        
        //FINO A QUI INCOLLARE NELLA SERVLET DI MODIFICA NODO SENSORE     
        
        	
        response.getWriter().append("Served at: ").append(request.getContextPath());
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		System.out.println("SONO IN POST");
		
	}

	
	
	
/*Altro metodo per stampare il contenuto della mappa 
   public static void printMap(Map mp) {
         Iterator it = mp.entrySet().iterator();
         while (it.hasNext()) {
             Map.Entry pair = (Map.Entry) it.next();
             System.out.println(pair.getKey() + " = " + pair.getValue());
             it.remove(); // avoids a ConcurrentModificationException
         }
     }
*/	

}
