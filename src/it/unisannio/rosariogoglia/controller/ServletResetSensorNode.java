package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import it.unisannio.rosariogoglia.controller.ServletResetSensorNode;
import it.unisannio.rosariogoglia.dao.SensorDAO;

/**
 * Servlet implementation class ServletResetSensorNode
 */
@WebServlet("/ServletResetSensorNode")
public class ServletResetSensorNode extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Logger logger = Logger.getLogger(ServletResetSensorNode.class); 
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//PRENDERE DA UNA PAGINA JSP IL SENSOR NODE CHE VOGLIAMO RESETTARE (POTREBBE ANCHE ESSERE LA DASHBOARD CON UN MENù LATERALE ED UN MENù A SCORRIMENTO PER SELEZIONARE IL NODO SENSORE)
		
		System.out.println("SONO IN RESET SENSOR NODE");		
		
		Integer idNodoSensore = null;
		String idNS = request.getParameter("idNodoSensore");
		if(!idNS.equals("Select Sensor Node")) {
			idNodoSensore = Integer.parseInt(idNS);
		} 
		
		if(idNodoSensore==null) {
			
			//	messaggio = "Devi scegliere il Nodo Sensore da resettare!";
				String result = "{\"result\":false,\"messaggio\":\"Devi scegliere il Nodo Sensore da resettare!\",\"redirect\":true,\"redirect_url\":\"dashboard.jsp\"}";
				
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
							
				String messaggio = "";
				String resultF = "";
				//EFFETTUARE IL RESET, AMMAZZARE IL THREAD SUL NODO SENSORE
				//INVIARE L'UTENTE ALLA DASHBOARD PER EFFETTUARE L'ASSOCIAZIONE NODO SENSORE-SENSORI
					
				//REALIZZARE UN METODO DAO CHE DISASSOCI IL SENSORE DAL NODO SENSORE: UN UPDATE DAO CHE PONGA A NULL IL CAMPO sensornode_idsensorNode
				
				//POI ESEGUIRE A QUERY UPDATE `sensoringdb`.`sensor` SET `sensornode_idsensorNode` = NULL WHERE (`idsensor` = ?);
				
				//SCEGLIERE SE CANCELLARE I DATI PRODOTTI DAL SENSORE E MEMORIZZATI NEL DB, IN QUANTO QUEL SENSORE PUò ESSERE ASSOCIATO AD UN NUOVO NODO
				//SI è SCELTO DI NON CANCELLARE I DATI PER AVERNE TRACCIA
				
				//UCCIDO IL THREAD IN ESECUZIONE DEL NODO SENSORE CHE VOGLIO RESETTARE: arresto il monitoraggio dei sensori associati al nodo
				ServletContext context = getServletContext();
				Map<Integer, Thread> mapThread = (Map<Integer, Thread>) context.getAttribute("mapThread");
				
				//SOLO SE C'E' UN THREAD IN ESECUZIONE SUL NODO SENSORE SELEZIONATO SI DEVE AMMAZZARE IL THREAD
				if(mapThread.get(idNodoSensore) != null) {
					try{  
				        mapThread.get(idNodoSensore).interrupt(); //uccido il thread
				       
				        /* POSSO UCCIDERE IL THREAD ANCHE SENZA LA HASHMAP, MA SETTANDO UNA VARIABILE BOLLEANA A FALSE, E PROTEGGERE IL METODO run() DEL THREAD CON QUELLA VARIABILE 
				         	sensorNode = snDAO.getSensorNodeByID(idNodoSensore);
				        	sensorNode.stopThreadh();
				        */
					}catch(Exception e){System.out.println("Exception handled "+e);}  
				        
				    System.out.println("thread ammazzato");
				}
			 
			    //RESET DEL NODO SENSORE: disassocio i sensori ad esso associati		
			  	SensorDAO sDAO = new SensorDAO();
			  	int updateRows = -1;
			  	updateRows = sDAO.updateSensorReset(idNodoSensore);
								
				if(updateRows != -1) {
					System.out.println("RESET NODO SENSORE OK");
					
					messaggio="Nodo Sensore "+ idNodoSensore +" resettato!";
					resultF = "true";
					
				}
				else {
					resultF = "false";
					messaggio="Nodo Sensore "+ idNodoSensore +" non resettato!";
					logger.info(new Date()+" "+messaggio);
				}
				

				String result = "{\"result\":"+resultF+",\"messaggio\":\"" +messaggio+ "\",\"redirect\":true,\"redirect_url\":\"dashboard.jsp\"}";
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
		doGet(request, response);
	}

}
