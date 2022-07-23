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

import it.unisannio.rosariogoglia.dao.SensorNodeDAO;
import it.unisannio.rosariogoglia.dao.ProtocolDAO;
import it.unisannio.rosariogoglia.dao.SensorDAO;
import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;
import it.unisannio.rosariogoglia.model.Sensor;
import it.unisannio.rosariogoglia.model.SensorHumidity;
import it.unisannio.rosariogoglia.model.SensorNode;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP;
import it.unisannio.rosariogoglia.model.SensorNodeMQTT;
import it.unisannio.rosariogoglia.model.SensorNodeREST;
import it.unisannio.rosariogoglia.model.SensorTemperature;
import it.unisannio.rosariogoglia.model.Protocol;


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
				
				int idProtocollo = rs.getInt("protocol_idprotocol"); //seleziono la tipologia di protocollo usato dal nodo sensore
				ProtocolDAO pDao = new ProtocolDAO();
				Protocol protocol = pDao.getProtocolById(idProtocollo);
				String protocollo = protocol.getProtocol();
				
				if(protocollo.equals("MQTT")) {
					sensorNode = new SensorNodeMQTT();
				}
				else if(protocollo.equals("COAP")) {
					sensorNode = new SensorNodeCOAP();
				} 
				else if(protocollo.equals("REST")){ //completare con altri protocolli usati dai nodi sensori
					sensorNode = new SensorNodeREST();
					logger.debug("SENSORE REST");	
				}			
				
				sensorNode.setIdSensorNode(rs.getInt("idsensorNode"));
				sensorNode.setDevice(rs.getString("deviceName"));
				sensorNode.setProtocollo(protocol);
				
				System.out.println("PROVO LA SERIALIZZAZIONE");
				Gson json = new Gson();
	            System.out.println(json.toJson(sensorNode));
				
				listaSensorNode.add(sensorNode);
				System.out.println("(" + sensorNode.getIdSensorNode() + ", " + sensorNode.getDevice() + ", " + sensorNode.getProtocollo().getProtocol() + ")");
				logger.debug("(" + sensorNode.getIdSensorNode() + ", " + sensorNode.getDevice() + ", " + sensorNode.getProtocollo().getProtocol() + ")");
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
								
				int idProtocollo = rs.getInt("protocol_idprotocol"); 
				ProtocolDAO pDao = new ProtocolDAO();
				Protocol protocol = pDao.getProtocolById(idProtocollo);
				String protocollo = protocol.getProtocol(); 
				
				//seleziono la tipologia di protocollo usato dal nodo sensore
				if(protocollo.equals("MQTT")) {
					sensorNode = new SensorNodeMQTT();
					logger.debug("SENSORE MQTT");
				}
				else if(protocollo.equals("COAP")) {
					//caricare anche la porta
					int port = rs.getInt("portServerCOAP");
					sensorNode = new SensorNodeCOAP();
					logger.debug("SENSORE COAP");
				} 
				else if(protocollo.equals("REST")){ //completare con altri protocolli usati dai nodi sensori
					sensorNode = new SensorNodeREST();
					logger.debug("SENSORE REST");	
				}				
			
				sensorNode.setIdSensorNode(rs.getInt("idsensorNode"));
				sensorNode.setDevice(rs.getString("deviceName"));
				sensorNode.setProtocollo(protocol);				
				
				//NOTE BENE CARICO LA LISTA DI SENSORI ASSOCIATI AL NODO SENSORE				
				sensorNode.setSensors(this.getSensorsBySensorNodeID(sensorNodeId)); 
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
	
	
	
	public SensorNode getSensorNodeByNome(String sensorNodeName){
			
			logger.debug("in getSensorNodeByName");
			SensorNode sensorNode = null;
			Connection connection = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				
				connection = DatabaseUtil.getConnection();
				String query = "SELECT * FROM sensornode WHERE (deviceName = ?)" ;
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, sensorNodeName);
				logger.debug("Select Query: " + query);
				rs = pstmt.executeQuery();
				if (rs.next()){
									
					int idProtocollo = rs.getInt("protocol_idprotocol"); 
					ProtocolDAO pDao = new ProtocolDAO();
					Protocol protocol = pDao.getProtocolById(idProtocollo);
					String protocollo = protocol.getProtocol(); 
					
					//seleziono la tipologia di protocollo usato dal nodo sensore
					if(protocollo.equals("MQTT")) {
						sensorNode = new SensorNodeMQTT();
						logger.debug("SENSORE MQTT");
					}
					else if(protocollo.equals("COAP")) {
						sensorNode = new SensorNodeCOAP();
						logger.debug("SENSORE COAP");
					} 
					else if(protocollo.equals("REST")){ //completare con altri protocolli usati dai nodi sensori
						sensorNode = new SensorNodeREST();
						logger.debug("SENSORE REST");	
					}				
				
					sensorNode.setIdSensorNode(rs.getInt("idsensorNode"));
					sensorNode.setDevice(rs.getString("deviceName"));
					sensorNode.setProtocollo(protocol);				
					
					//NOTA BENE CARICO LA LISTA DI SENSORI ASSOCIATI AL NODO SENSORE				
					sensorNode.setSensors(this.getSensorsBySensorNodeID(sensorNode.getIdSensorNode())); 
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
	
	/**
	 * Verifica se esiste già un Nodo Sensore con lo stesso nome
	 * 
	 * @param sensorNodeName
	 * @return sensorNodeExisting variabile booleana parti a true se il nodo sensore è già presente nel DB
	 */
	public boolean checkSensorNodeByNome(String sensorNodeName){
		
		logger.debug("in checkSensorNodeByName");
		
		boolean sensorNodeExisting = false; 
		SensorNode sensorNode = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			connection = DatabaseUtil.getConnection();
			String query = "SELECT * FROM sensornode WHERE (deviceName = ?)" ;
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, sensorNodeName);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery();
			if (rs.next()){
				sensorNodeExisting = true;		
			}
			logger.debug("Il nodo sensore esiste: " + sensorNodeExisting);

			
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
		return sensorNodeExisting;
		
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
			
			String query = "SELECT * FROM sensor WHERE (sensornode_idsensorNode = ?)";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, sensorNodeId);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery();
						
			while(rs.next()){
				//carico il sensore associato al nodo, a partire dall'id
				Sensor sensor = sDAO.getSensorById(rs.getInt("idsensor"));
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
	
	public Integer insertSensorNode(SensorNode sensorNode) {
		logger.debug("in insertSensorNode");
		Integer sensorNodeId = -1;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			connection = DatabaseUtil.getConnection();
			connection.setAutoCommit(false);
			
			if(sensorNode instanceof SensorNodeCOAP) {
				String sql = "INSERT INTO sensornode (deviceName, protocol_idprotocol, portServerCOAP) VALUES (?, ?, ?)";
				pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, sensorNode.getDevice());
				pstmt.setInt(2, sensorNode.getProtocollo().getIdProtocol());				
				pstmt.setInt(3, ((SensorNodeCOAP) sensorNode).getPort());
			}			
			else {
				String sql = "INSERT INTO sensornode (deviceName, protocol_idprotocol) VALUES (?, ?)";
				pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, sensorNode.getDevice());
				pstmt.setInt(2,sensorNode.getProtocollo().getIdProtocol());				
			}
						
			logger.debug("Insert Query: " + pstmt.toString());
			
			int insertRow = pstmt.executeUpdate();
					
			if(insertRow != -1){
				rs = pstmt.getGeneratedKeys();
				if(rs.next()) {
					sensorNodeId = rs.getInt(1);
					logger.info("Inserimento nuovo Nodo Sensore (" + sensorNodeId + ")");
					sensorNode.setIdSensorNode(sensorNodeId);
				}
	
			}
				
			connection.commit();
			
			
		} catch (Exception e) {
			logger.debug("Inserimento Nodo Sensore non riuscito!!!");
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
		
		return sensorNodeId;
		
	}

	
	// insertSensorNodeHasSensors(idNodoSensore, idSensors);
	
	public Integer updateSensorListBySensorNodeId(Integer idNodoSensore, List<Sensor> sensorList) {
		logger.debug("in updateSensorListBySensorNodeId");
		Integer insertRow = -1;
		SensorDAO sDAO = new SensorDAO();
		
		for(int i=0; i<sensorList.size(); i++) {			
			insertRow = insertRow + sDAO.updateSensor(sensorList.get(i), idNodoSensore); //associo ogni sensore al nodo sensore scelto
		}			

		return insertRow;
				
	}
	
	
		/**
		 * Metodo usato per ottenere il numero di porta, usato per la componente server di un nodo sensore COAP, più grande di quelli presenti.
		 * @return numero di porta
		 */
		public Integer getMaxPortServerCOAP() {
			logger.debug("in getMaxPortServerCOAP");
			Integer port = -1;
			
			Connection connection = null;
			PreparedStatement  pstmt = null;
			try {
				
				connection = DatabaseUtil.getConnection();
				connection.setAutoCommit(false);
				
				String sql = "SELECT MAX(portServerCOAP) as max_port FROM sensor;";
		
				pstmt = connection.prepareStatement(sql);
				logger.debug("Query:" + pstmt.toString());
				port = pstmt.executeUpdate();
								
				connection.commit();
				logger.info("Max COAP port: " + port);
		
			}catch (SQLException  e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (connection!=null) {
					try {
						pstmt.close();
						connection.setAutoCommit(true);
						connection.close();
						logger.debug("Connection chiusa");
					} catch (SQLException  e) {
						
						e.printStackTrace();
					}
				}
			}
			return port;
		}
		
	
	
	
	
	
	
	
	
	
	
/*	
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
	
*/
	
	
	
	

}