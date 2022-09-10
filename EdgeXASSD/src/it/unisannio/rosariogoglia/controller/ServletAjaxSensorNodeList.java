package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.model.SensorNode;

/**
 * Servlet implementation class ServletAjaxSensorNodeList
 */
@WebServlet("/ServletAjaxSensorNodeList")
public class ServletAjaxSensorNodeList extends HttpServlet {
	private static final long serialVersionUID = 1L;
   

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SensorNodeDAO snDAO = new SensorNodeDAO();
		    		
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

		
		
		
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}


}
