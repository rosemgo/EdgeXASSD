package it.unisannio.rosariogoglia.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;
import it.unisannio.rosariogoglia.model.Sensor;
import it.unisannio.rosariogoglia.model.SensorHumidity;
import it.unisannio.rosariogoglia.model.SensorNode;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP;
import it.unisannio.rosariogoglia.model.SensorNodeMQTT;
import it.unisannio.rosariogoglia.model.SensorTemperature;

public class SensorNodeDAO {
	
	
	private static Logger logger = Logger.getLogger(SensorNodeDAO.class);
	
	public List<SensorNode> getSensorsNode(){
		
		logger.debug("in getSensorsNode");
		List<SensorNode> listaSensorNode = new ArrayList<SensorNode>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection();
			String query = "SELECT * FROM sensorNode ORDER BY deviceName ASC";
			pstmt = connection.prepareStatement(query);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery(query);
			
			
			while(rs.next()){
				SensorNode sensorNode = null;
				String protocollo = rs.getString("protocollo"); //seleziono la tipologia del sensore
				if(protocollo.equals("MQTT")) {
					sensorNode = new SensorNodeMQTT();
				}
				else if(protocollo.equals("COAP")) {
					sensorNode = new SensorNodeCOAP();
				} 
				else{ //completare con altri protocolli usati dai nodi sensori
					
				}				
				
				sensorNode.setIdSensorNode(rs.getInt("idsensorNode"));
				sensorNode.setDevice(rs.getString("deviceName"));
				sensorNode.setProtocollo(rs.getString("protocollo"));
				
				System.out.println("PROVO LA SERIALIZZAZIONE");
				Gson json = new Gson();
	            System.out.println(json.toJson(sensorNode));
				
				listaSensorNode.add(sensorNode);
				System.out.println("(" + sensorNode.getIdSensorNode() + ", " + sensorNode.getDevice() + ", " + sensorNode.getProtocollo() + ")");
				logger.debug("(" + sensorNode.getIdSensorNode() + ", " + sensorNode.getDevice() + ", " + sensorNode.getProtocollo() + ")");
			}
			logger.debug("Caricati " + listaSensorNode.size() + " sensori");
			
		} catch (SQLException  e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(rs != null) {
					rs.close();
					}
					
				if(pstmt != null) {
					pstmt.close();
					}
					
				if(connection != null) {
					connection.close();
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return listaSensorNode;
		
	}
	
	
	public SensorNode getSensorNodeByID(Integer sensorNodeId){
		
		logger.debug("in getSensorNodeByID");
		SensorNode sensorNode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			connection = DatabaseUtil.getConnection();
			String query = "SELECT * FROM sensornode WHERE (idsensorNode = ?)" ;
			pstmt = connection.prepareStatement(query);
			System.out.println("SENSORNODE ID: " + sensorNodeId);
			pstmt.setInt(1, sensorNodeId);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery();
			if (rs.next()){
				String protocollo = rs.getString("protocollo"); //seleziono la tipologia del sensore
				if(protocollo.equals("MQTT")) {
					sensorNode = new SensorNodeMQTT();
					logger.debug("SENSORE MQTT");
				}
				else if(protocollo.equals("COAP")) {
					sensorNode = new SensorNodeCOAP();
					logger.debug("SENSORE COAP");
				} 
				else{ //completare con altri protocolli usati dai nodi sensori
						
				}				
			
				sensorNode.setIdSensorNode(rs.getInt("idsensorNode"));
				sensorNode.setDevice(rs.getString("deviceName"));
				sensorNode.setProtocollo(rs.getString("protocollo"));				
				sensorNode.setSensors(this.getSensorsBySensorNodeID(sensorNodeId)); //carico la lista di sensori associati al nodo
			}
			System.out.println("(" + sensorNode.getIdSensorNode() + ", " + sensorNode.getDevice() + ", " + sensorNode.getProtocollo() + ")");
			logger.debug("(" + sensorNode.getIdSensorNode() + ", " + sensorNode.getDevice() + ", " + sensorNode.getProtocollo() + ")");

			
		} catch (SQLException  e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(rs != null) {
					rs.close();
					}
					
				if(pstmt != null) {
					pstmt.close();
					}
					
				if(connection != null) {
					connection.close();
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return sensorNode;
		
	}
	
	
	
	public List<Sensor> getSensorsBySensorNodeID(int sensorNodeId){
		
		logger.debug("in getSensorsBySensorNodeID");
		List<Sensor> listaSensori = new ArrayList<Sensor>();
		SensorDAO sDAO = new SensorDAO();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection();
			String query = "SELECT * FROM sensornode_has_sensor WHERE (sensorNode_idsensorNode = ?)";
			pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, sensorNodeId);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery();
			
			
			while(rs.next()){
				//carico il sensore associato al nodo, a partire dall'id
				Sensor sensor = sDAO.getSensorById(rs.getInt("sensor_idsensor"));
				listaSensori.add(sensor);
				logger.debug("(" + sensor.getIdSensor() + ", " + sensor.getName() + sensor.getType() + ")");
			}
			logger.debug("Caricate " + listaSensori.size() + " sensori");
			
		} catch (SQLException  e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(rs != null) {
					rs.close();
					}
					
				if(pstmt != null) {
					pstmt.close();
					}
					
				if(connection != null) {
					connection.close();
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return listaSensori;
		
		
	}
	
	
	
	public Integer insertSensorNodeHasSensors(Integer idSensorNode, List<Integer> idSensors){
		logger.debug("in insertSensorNodeHasSensors");
		Integer insertRow = -1;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
				connection = DatabaseUtil.getConnection();
			
				connection.setAutoCommit(false);
				for(int i=0; i<idSensors.size(); i++) {			
					String sql = "INSERT INTO sensornode_has_sensor(sensorNode_idsensorNode, sensor_idsensor) VALUES (?, ?)";
					pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					pstmt.setInt(1, idSensorNode);
					pstmt.setInt(2, idSensors.get(i));
					logger.debug("Insert Query: " + pstmt.toString());
					try{
						insertRow = pstmt.executeUpdate();
					}catch (Exception e) {
						logger.debug("Associazione sensorNode-sensore non riuscito!!!");
					}	
					if(insertRow != -1){
						logger.info("Inserimento nuovo sensornode_has_sensor (" + idSensorNode + ", " + idSensors.get(i) + ")");
						connection.commit();
					}
				}			
			
		} catch (SQLException  e) {
			e.printStackTrace();
			try {
				connection.rollback();
				logger.debug("Rollback in inserimento sensornode_has_sensor");
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
			
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			if (connection!=null) {
				try {
					if(rs != null)
						rs.close();
					pstmt.close();
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException  e) {
					
					e.printStackTrace();
				}
				logger.debug("Connection chiusa");
			}
		}	
			
		return insertRow;
	}
	
	
	
	
	
	

}
