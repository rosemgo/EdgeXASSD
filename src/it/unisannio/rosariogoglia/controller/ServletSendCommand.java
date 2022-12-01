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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.model.SensorNode;

/**
 * Servlet implementation class ServletSendCommand
 */
@WebServlet("/ServletSendCommand")
public class ServletSendCommand extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletSendCommand() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		System.out.println("SONO NELLA SERVLET SEND COMMAND");		
		
		Integer idNodoSensore = null;
		
		//ricevo l'id del nodoSensore di cui voglio richiedere una misurazione
		String idNS = request.getParameter("idNodoSensore");
		if(!idNS.equals("Select Sensor Node")) {
			idNodoSensore = Integer.parseInt(idNS);
		}
		SensorNodeDAO sdao = new SensorNodeDAO();
		SensorNode sn = sdao.getSensorNodeByID(idNodoSensore);
				
		
		//RICEVO IL CLICK SUL TASTO SENDCOMMAND E SI ATTIVA LA SERVLET CHE INVIA UNA RICHIESTA REST AL SERVIZIO CORE-COMMAND, 
		//CON LO SCOPO DI COMANDARE IL DEVICE INTERNO AD EDGEX RELATIVO AL NODO SENSORE DEL SENSORE CHE STIAMO MONITORANDO, 
		//PER INVIARE UN COMANDO DI MISURAZIONE AL NODO SENSORE.
		
		//LINK A CUI INVIARE LA RICHIESTA REST:
		//http://localhost:59882/api/v2/device/name/{NOME-DEVICE}/{RESOURCE PRESENTE NEL PROFILO}?ds-pushevent=yes&ds-returnevent=yes
		var client = HttpClient.newBuilder().build();
		
		HttpRequest requestGET = HttpRequest.newBuilder()  //sample-json è il nome del device SOSTITUIRE CON this.device
			      //.uri(URI.create("http://192.168.204.133:59986/api/v2/resource/"+ this.device +"/json")) // USARE QUESTO INDIRIZZO PER EDGEX SU MACCHINA VIRTUALE LOCALE
					.uri(URI.create("http://15.160.35.22:59882/api/v2/device/name/"+ sn.getDevice() +"/json?ds-pushevent=yes&ds-returnevent=yes")) // USARE QUESTO INDIRIZZO PER EDGEX SU CLOUD AWS
					.timeout(Duration.ofMinutes(2))
			        .header("Content-Type", "application/json")
			        .build();
			  			  				   
		HttpResponse<String> responseGET = null;
		try {
			responseGET = client.send(requestGET, BodyHandlers.ofString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		System.out.println("COMANDO REST SEND COMMAND INVIATO");
		System.out.println("Response code is: " + responseGET.statusCode());
		System.out.println("The response body is: " + responseGET.body());
		
	//	out.printf("Response code is: %d %n", responseGET.statusCode());
	//	out.printf("The response body is:%n %s %n", responseGET.body());
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
