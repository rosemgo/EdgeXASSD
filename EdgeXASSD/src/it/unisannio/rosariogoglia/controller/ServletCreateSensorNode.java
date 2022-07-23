package it.unisannio.rosariogoglia.controller;

import static java.lang.System.out;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.model.SensorNode;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP;
import it.unisannio.rosariogoglia.model.SensorNodeMQTT;
import it.unisannio.rosariogoglia.model.SensorNodeREST;
import it.unisannio.rosariogoglia.model.Protocol;

import org.apache.log4j.Logger;

import it.unisannio.rosariogoglia.controller.ServletCreateSensorNode;
import it.unisannio.rosariogoglia.dao.ProtocolDAO;

/**
 * Servlet implementation class ServletCreateSensorNode
 */
@WebServlet("/ServletCreateSensorNode")
public class ServletCreateSensorNode extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Logger logger = Logger.getLogger(ServletCreateSensorNode.class); 
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String messaggio = "";
		Integer sensorNodeProtocol = null;
		SensorNodeDAO snDAO = new SensorNodeDAO();
		ProtocolDAO pDAO = new ProtocolDAO();
		
		
		//RICEVERE I PARAMENTRI DEL NODO SENSORE DA CREARE
		String sensorNodeName = request.getParameter("device");
		
		String protocolSN = request.getParameter("idProtocol"); //farlo scegliere tra una lista ajax di protocolli
		if(!protocolSN.equals("Select Protocol")) {
			sensorNodeProtocol = Integer.parseInt(protocolSN);
		} 
				
		
		if(sensorNodeName.equals("")){
			
			messaggio = "Devi inserire un nome valido!";
			response.setContentType("text/plain");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(messaggio);
			
		}else if(sensorNodeProtocol==null) {
			
			messaggio = "Devi scegliere un protocollo di comunicazione!";
			response.setContentType("text/plain");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(messaggio);
		
		}
		else if(snDAO.checkSensorNodeByNome(sensorNodeName)){ //VERIFICARE SE ESISTE GI� UN DEVICE CON LO STESSO NOME
			messaggio = "Gi� esiste un nodo sensore con lo stesso nome!";
			response.setContentType("text/plain");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(messaggio);
		}
		else {
			
		//	SensorNodeDAO snDAO = new SensorNodeDAO();
		//	ProtocolDAO pDAO = new ProtocolDAO();
			

		//CREARE UN METODO DAO IN SENSORNODE CHE FORNISCE IL NUMERO DI PORTA PI� GRANDE PRESENTE NEL DB
		// SE NON C'� NESSUN NODO COAP INIZIARE CON UN NUMERO FISSO
			

			
			Protocol protocol = pDAO.getProtocolById(sensorNodeProtocol);
			
			SensorNode sensorNode = null;
			
			if (protocol.getProtocol().equals("MQTT")){
				sensorNode = new SensorNodeMQTT();
			}
			if (protocol.getProtocol().equals("COAP")) { 
				//PASSARE LA PORTA DELLA COMPONENTE SERVER SEMPRE CRESCENTE, AD OGNI NODO CHE HA IL PROTOCOLLO COAP
				Integer maxPort = snDAO.getMaxPortServerCOAP(); //ottengo il valore di porta pi� grande associato all'ultimo nodo sensore COAP inserito nel DB
				Integer port;
				if(maxPort == null)
					port = 45000;
				else				
					port = maxPort + 1; //creiamo una porta di uno pi� grande della massima presente 
				sensorNode = new SensorNodeCOAP(port); 
			}
			if (protocol.getProtocol().equals("REST")) {
				sensorNode = new SensorNodeREST();
			}	
			
			sensorNode.setDevice(sensorNodeName);
			sensorNode.setProtocollo(protocol);
			
			
			int idSensorNode = -1;
			//SCRIVERE NEL DATABASE I DATI DEL NODO SENSORE CREATO
			idSensorNode = snDAO.insertSensorNode(sensorNode);
			
			if(idSensorNode!=-1) {
				messaggio = "Nodo Sensore n� "+ sensorNode.getIdSensorNode() + " " + sensorNode.getDevice() + " creato correttamente!";
				logger.info(new Date()+" "+messaggio);
				
				String deviceEdgeX = "";
			
				//DIFFERENZIARE LE STRINGHE DA CREARE IN BASE AL TIPO DI PROTOCOLLO SCELTO
				//IL NOME SCELTO DALL'UTENTE SAR� IL DEVICENAME NEL DATABASE ED IN EDGEX
				
				if(sensorNode.getProtocollo().getProtocol().equals("MQTT")) {
					deviceEdgeX = "[{\"apiVersion\": \"v2\",\"device\": {\"name\": \""+ sensorNode.getDevice() +"\",\"description\": \"Sensor Node MQTT creato dall'utente in data: "+new Date()+"\",\"adminState\": \"UNLOCKED\",\"operatingState\": \"UP\",\"labels\": [\"mqtt\",\"utente\"],\"serviceName\": \"device-mqtt\",\"profileName\": \"Test-Device-MQTT-Profile\",\"protocols\": {\"mqtt\": {\"CommandTopic\": \"command/" + sensorNode.getDevice() + "\"}}}}]";
				}
				else if(sensorNode.getProtocollo().getProtocol().equals("COAP")){
					deviceEdgeX = "[{\"apiVersion\": \"v2\",\"device\": {\"name\": \""+ sensorNode.getDevice() +"\",\"description\": \"Sensor Node COAP creato dall'utente in data: "+new Date()+"\",\"adminState\": \"UNLOCKED\",\"operatingState\": \"UP\",\"labels\": [\"coap\",\"utente\"],\"serviceName\": \"device-coap\",\"profileName\": \"example-datatype\",\"protocols\": {\"COAP\": {\"ED_ADDR\": \"192.168.138.252\", \"ED_PORT\": \""+ ((SensorNodeCOAP) sensorNode).getPort() +"\",\"ED_SecurityMode\": \"NoSec\"}}}}]";
				}
				else if(sensorNode.getProtocollo().getProtocol().equals("REST")){
					deviceEdgeX = "[{\"apiVersion\": \"v2\",\"device\": {\"name\": \""+ sensorNode.getDevice() +"\",\"description\": \"Sensor Node REST creato dall'utente in data: "+new Date()+ "\",\"adminState\": \"UNLOCKED\",\"operatingState\": \"UP\",\"labels\": [\"rest\",\"utente\"],\"serviceName\": \"device-rest\",\"profileName\": \"sample-json\",\"protocols\": {\"other\": {}}}}]";
				}
			
				//ESEGUIRE UNA RICHIESTA POST PER CARICARE IL DEVICE IN EDGEX
				
				HttpClient client = HttpClient.newBuilder().build();
				
				HttpRequest requestEdgeX = HttpRequest.newBuilder()  
				        .uri(URI.create("http://192.168.204.133:59881/api/v2/device")) //invio una richiesta REST al servizio metadata per aggiungere un device ad EdgeX
				        .timeout(Duration.ofMinutes(2))
				        .header("Content-Type", "application/json")
				        .POST(BodyPublishers.ofString( deviceEdgeX )) //BodyPublisher converte un oggetto java di alto livello in un flusso di dati
				        .build();
				
				try {
					HttpResponse<String> responseEdgeX = client.send(requestEdgeX, BodyHandlers.ofString());  //E' il duale di BodyPublisher, converte un fusso di dati in un oggetto java di alto livello
					out.printf("Response code is: %d %n", responseEdgeX.statusCode());
					out.printf("The response body is:%n %s %n", responseEdgeX.body());			
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
				
			}
			else {
				messaggio="Nodo Sensore "+ sensorNodeName +" non creato correttamente!!!";
				logger.info(new Date()+" "+messaggio);	
				
			}
					
			
		//	request.setAttribute("messaggio", messaggio);
		//	request.getRequestDispatcher("dashboard.html").forward(request, response);
			
			
			
			response.setContentType("text/plain");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(messaggio);
			
			
			
			
			//response.getWriter().append("Served at: ").append(request.getContextPath());
						
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