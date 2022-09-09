package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import it.unisannio.rosariogoglia.controller.ServletAssociateSensorToNode;
import it.unisannio.rosariogoglia.dao.SensorDAO;
import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.model.SensorNode;
import it.unisannio.rosariogoglia.model.Sensor;

/**
 * Servlet utilizzata per associare una lista sensori ad un Nodo sensore
 * In seguito all'associazione avvia il Thread di esecuzione del Nodo Sensore, in cui i sensori associati al Nodo effettuano misurazioni ed il Nodo invia secondo il protocollo di comunicazione scelto per lo specifico Nodo Sensore, i valori misurati dai sensori, al Gateway con installato EdgeX
 */
@WebServlet("/ServletAssociateSensorToNode")
public class ServletAssociateSensorToNode extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(ServletAssociateSensorToNode.class); 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("SONO IN ASSOCIATE SENSOR TO NODE");		
		
		Integer idNodoSensore = null;
		Integer idSensor1= null;
        Integer idSensor2 = null;
        Integer idSensor3 = null;
        String messaggio = "";
        
        List<Integer> idSensorList = new ArrayList<>();        
        
		String idNS = request.getParameter("idNodoSensore");
		if(!idNS.equals("Select Sensor Node")) {
			idNodoSensore = Integer.parseInt(idNS);
		} 
		String idS1 = request.getParameter("idSensore1"); 
		if(!idS1.equals("Select Sensor 1")) {
			idSensor1= Integer.parseInt(idS1);
			idSensorList.add(idSensor1);
		}
		String idS2 = request.getParameter("idSensore2");
		if(!idS2.equals("Select Sensor 2")) {
			idSensor2= Integer.parseInt(idS2);
			idSensorList.add(idSensor2);
		}
		String idS3 = request.getParameter("idSensore3"); 
		if(!idS3.equals("Select Sensor 3")) {
			idSensor3 = Integer.parseInt(idS3);
			idSensorList.add(idSensor3);
		}
        
		//creao un insieme in cui inserisco gli idSensor scelti, se viene scelto più volte lo stesso sensore, sollevo l'errore settando una variabile a true
		//questo metodo è utile nel caso si hanno tanti sensore, ma nel nostro caso essendo solo 3 sensori è sufficiente fare un if((idSensor1==idSensor2) || (idSensor1 == idSensor3) || (idSensor2==idSensor3))
		boolean sceltaRipetuta = false;
		Set<Integer> s = new HashSet<Integer>();
		for(Integer idSensor : idSensorList) {
			if(!s.contains(idSensor)) 
				s.add(idSensor1);
			else
				sceltaRipetuta = true;
		}
			
		
		if(idNodoSensore==null) {
									
			String result = "{\"result\":\"false\",\"messaggio\":\"Non hai scelto un Nodo Sensore valido\",\"redirect\":true,\"redirect_url\":\"dashboard.jsp\"}";
			JSONObject m = null;
			try {
				 m = new JSONObject(result);
				System.out.println("mess: " +m.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
    //		String mess = json.toJson(m);
			
	//		messaggio = "Non hai scelto un Nodo Sensore valido";
			
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
	//		response.getWriter().write(messaggio);
			response.getWriter().write(m.toString());
			
		}
		else if (idSensor1==null && idSensor2==null && idSensor3==null){
			
			String result = "{\"result\":\"false\",\"messaggio\":\"Non hai scelto nemmeno un Sensore\",\"redirect\":true,\"redirect_url\":\"dashboard.jsp\"}";
			JSONObject m = null;
			try {
				 m = new JSONObject(result);
				System.out.println("mess: " +m.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(m.toString());
			
			
		}
		//verificare se è stato scelto più volte lo stesso sensore: (idSensor1==idSensor2) || (idSensor1 == idSensor3) || (idSensor2==idSensor3) 
		else if(sceltaRipetuta) {
			
            String result = "{\"result\":\"false\",\"messaggio\":\"Hai scelto più volte lo stesso Sensore\",\"redirect\":true,\"redirect_url\":\"dashboard.jsp\"}";
			JSONObject m = null;
			try {
				 m = new JSONObject(result);
				System.out.println("mess: " +m.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(m.toString());
            
            
		}		
		else {
						
			
/*			SE VOLESSI USARE LE EXPRESSION LANGUAGE JSP
 * 
 			HttpSession session= request.getSession();
			session.setAttribute("idSensorNode", idNodoSensore);
			
			List<Integer> idSensors = new ArrayList<Integer>();
			if(idSensor1 != null) {
	        	idSensors.add(idSensor1);
	        }
	        if(idSensor2 != null) {
	    		idSensors.add(idSensor2);
	        }
	        if(idSensor3 != null) {
	        	idSensors.add(idSensor3);
	        }
			
	        session.setAttribute("idSensorList", idSensors);
	        request.getRequestDispatcher("/ServletStartMonitoring").forward(request, response);
*/			
			
			SensorNodeDAO snDAO = new SensorNodeDAO();
			SensorDAO sDAO = new SensorDAO();
		
				
	        List<Sensor> idSensors = new ArrayList<Sensor>();
	        if(idSensor1 != null) {
	        	Sensor sensor = sDAO.getSensorById(idSensor1);
	        	idSensors.add(sensor);
	        }
	        if(idSensor2 != null) {
	        	Sensor sensor = sDAO.getSensorById(idSensor2);
	    		idSensors.add(sensor);
	        }
	        if(idSensor3 != null) {
	        	Sensor sensor = sDAO.getSensorById(idSensor3);
	        	idSensors.add(sensor);
	        }

	        String resultF = "";
			int insertRow = -1;
			
			//ASSOCIO LA LISTA DI SENSORI AL NODO SENSORE NEL DATABASE
		//	insertRow = snDAO.insertSensorNodeHasSensors(idNodoSensore, idSensors);
				
			insertRow = snDAO.updateSensorListBySensorNodeId(idNodoSensore, idSensors);
			System.out.println("INSERT ROW: " + insertRow);
			if(insertRow != -1){		
				
				messaggio = "Associazione Sensori-Nodo Sensore avvenuta correttamente: Monitaroggio avviato!";
				resultF = "true";
				
				for(int i=0; i<idSensors.size(); i++)
					System.out.println("SensorNodeId " + idNodoSensore + " associato correttamente al sensore " + idSensors.get(i).getName() + "!!!"); 
				
				
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
		       	
				//inserisco il thread relativo al nodo sensore nella mappa
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

	    
			}
			else {
				
				messaggio = "Associazione non avvenuta correttamente";
				resultF = "false";
				logger.info(new Date()+" "+messaggio);	
			}
					
			
	  //    messaggio = "Associazione Sensori-Nodo Sensore avvenuta correttamente: Monitaroggio avviato!";
	        String result = "{\"result\":"+resultF+",\"messaggio\":\""+messaggio+"\",\"redirect\":true,\"redirect_url\":\"dashboard.jsp\"}";
			JSONObject m = null;
			try {
				 m = new JSONObject(result);
				System.out.println("mess: " +m.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(m.toString());
	        		
			
			
		}
		
		
        
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("SONO IN ASSOCIATE SENSOR TO NODE POST");	
		
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
