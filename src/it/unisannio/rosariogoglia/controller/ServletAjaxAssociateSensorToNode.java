package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.unisannio.rosariogoglia.dao.SensorDAO;
import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.model.Sensor;
import it.unisannio.rosariogoglia.model.SensorNode;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP;
import it.unisannio.rosariogoglia.model.SensorNodeMQTT;

/**
 * Servlet implementation class ServletAjaxAssociateSensorToNode
 */
@WebServlet("/ServletAjaxAssociateSensorToNode")
public class ServletAjaxAssociateSensorToNode extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	   /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletAjaxAssociateSensorToNode() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
    	
 //   	try (PrintWriter out = response.getWriter()) {

        	SensorNodeDAO snDAO = new SensorNodeDAO();
    		SensorDAO sDAO = new SensorDAO();
    		    		
            //operation e id sono i campi della variabile data inviata dalla pagina jsp index nella funzione ajax
            String op = request.getParameter("operation");
            System.out.println("OPERATION: " + op);
           
            
            
            if (op.equals("sensorNode")) {
            	System.out.println("IN SENSOR NODE");
                //List<SensorNodeSimply> snlist = snDAO.getSensorsNodeSimply();
                List<SensorNode> snlist = snDAO.getSensorsNode();
                for(int i=0; i<snlist.size(); i++) {
                	System.out.println("SENSORNODE: " + snlist.get(i).toString());
                }                
    
               Gson json = new Gson();
               String sensorNodeList = json.toJson(snlist);
               System.out.println("SENSOR NODE LIST: " + sensorNodeList);
               response.setContentType("text/html");
               response.getWriter().write(sensorNodeList);
            }

            
            if (op.equals("sensor1")) {
              
            	int idSensorNode = Integer.parseInt(request.getParameter("idSensorNode"));
            	System.out.println("IDSENSORNODE RICEVUTO: " + idSensorNode);
         
            	System.out.println("IN SENSOR 1");
            	
            	//DEVO FORNIRE SOLO I SENSORI NON ANCORA ASSOCIATI A NESSUN NODO SENSORE
            	List<Sensor> slist = sDAO.getSensorMancantiByIdSensorNode();
            	for(int i=0; i<slist.size(); i++) {
                	System.out.println(slist.get(i).toString());
            	}
            	
            	Gson json = new Gson();
                String sensorList = json.toJson(slist);
                System.out.println("SENSOR LIST: " + sensorList);
                
                response.setContentType("text/html");
                response.getWriter().write(sensorList);
                
            }

            if (op.equals("sensor2")) {
            	
            	System.out.println("IN SENSOR 2");
            	
            	int idSensorNode = Integer.parseInt(request.getParameter("idSensorNode"));
            	System.out.println("IDSENSORNODE RICEVUTO: " + idSensorNode);
           
            	//DEVO FORNIRE SOLO I SENSORI NON ANCORA ASSOCIATI AL NODO SENSORE SCELTO
            	List<Sensor> slist = sDAO.getSensorMancantiByIdSensorNode();
                Gson json = new Gson();
                String sensorList = json.toJson(slist);
                response.setContentType("text/html");
                response.getWriter().write(sensorList);
            }
            
            
    //    }
    	
    }

    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
		System.out.println("SONO IN POST");
		
	
	}


}
