package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.google.gson.Gson;

import it.unisannio.rosariogoglia.dao.MeasurementDAO;
import it.unisannio.rosariogoglia.model.Measurement;
import netscape.javascript.JSObject;

/**
 * Servlet implementation class ServletSensorMonitoring
 */
@WebServlet("/ServletSensorMonitoring")
public class ServletSensorMonitoring extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletSensorMonitoring() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		System.out.println("SONO IN SENSOR MONITORING");		
		
		Integer idNodoSensore = null;
		Integer idSensor1= null;
        
		String messaggio = "";
       
        
		String idNS = request.getParameter("idNodoSensore");
		if(!idNS.equals("Select Sensor Node")) {
			idNodoSensore = Integer.parseInt(idNS);
		} 
		String idS1 = request.getParameter("idSensore1"); 
		if(!idS1.equals("Select Sensor 1")) {
			idSensor1= Integer.parseInt(idS1);
		}
		//postId indica il numero della misurazione inserita nel grafico. Se postId è ugulae a 0 significa che nessuna misurazione è stata ancora inserita
		//POSTID MI SERVE PER CAPIRE QUALE TUPLA LEGGERE NEL DATABASE, IN MODO DA AGGIORNARE IL GRAFICO REAL-TIME
		Integer postId = Integer.parseInt(request.getParameter("postId")); 
		System.out.println("POSTID: " + postId);
		
		MeasurementDAO mDAO = new MeasurementDAO();
		List<Measurement> measurementList = new ArrayList<>();
		
		if(postId == 0) {
			
			System.out.println("POST ID 0");
			//********** E' LA PRIMA RICHIESTA EFFETTUATA DI MONITORAGGIO DEL SENSORE, SOLO IN QUESTO CASO E' NECESSARIO OTTENERE TUTTE 
			//LE MISURAZIONI PRESENTI NELLA TABELLA FINO A QUEL MOMENTO ED INSERIRLE NEL GRAFICO
			measurementList = mDAO.getMeasurementListBySensorID(idSensor1);
				
			
		}
		else {
			
			System.out.println("POST ID > 0");
			
			//********* EFFETTUARE UNA SINGOLA LETTURA NEL DB. LEGGERE LA TUPLA CORRISPONDENTE ALLA MISURAZIONE NUMERO PARI AL VALORE DI POSTID. 
			Measurement measurement = mDAO.getMeasurementByIdSensor(idSensor1, postId);
			measurementList.add(measurement); //lista di una sola misurazione
			
			if(measurement != null)
				System.out.println("SINGOLA MISURAZIONE: " + measurement.toString());
			
		}

		System.out.println("MEASUREMENT LIST: ");
		for(int i=0; i<measurementList.size(); i++) {
			if(measurementList.get(i) != null)
			System.out.println("MEASUREMENT " + i + " : " + measurementList.get(i).toString());
		}                
		

		
		
	//	JSONObject j = new JSONObject(measurementList)
	   	 
       Gson json = new Gson();
       String measurementListFinal = json.toJson(measurementList);
       System.out.println("SENSOR NODE LIST: " + measurementListFinal);
       response.setContentType("text/html");
       response.getWriter().write(measurementListFinal);    
			
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
