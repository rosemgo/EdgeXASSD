package it.unisannio.rosariogoglia.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.unisannio.rosariogoglia.dao.ProtocolDAO;
import it.unisannio.rosariogoglia.model.Protocol;
import it.unisannio.rosariogoglia.model.SensorNode;

/**
 * Servlet implementation class ServletSensorNodeList
 */
@WebServlet("/ServletAjaxProtocolList")
public class ServletAjaxProtocolList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("IN PROTOCOL LIST");
        
		 String op = request.getParameter("operation");
         System.out.println("OPERATION: " + op);
                     
         if (op.equals("protocolList")) {
		
        	System.out.println("IN PROTOCOLLIST");
        	
			ProtocolDAO pDAO = new ProtocolDAO();		
	        List<Protocol> protocolList = pDAO.getProtocol(); //carico la lista di protocolli
	        System.out.println("LISTA PROTOCOLLI SIZE: " + protocolList.size()); 
	        
	         for(int i=0; i<protocolList.size(); i++) {
	         	System.out.println("PROTOCOL: " + protocolList.get(i).toString());
	         }                
	
	        Gson json = new Gson();
	        String protocolListJson = json.toJson(protocolList);
	        System.out.println("PROTOCOL LIST JSON: " + protocolListJson);
	        response.setContentType("text/html");
	        response.getWriter().write(protocolListJson);	
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
