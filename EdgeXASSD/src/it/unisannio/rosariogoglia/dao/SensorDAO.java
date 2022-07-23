package it.unisannio.rosariogoglia.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;
import it.unisannio.rosariogoglia.model.Sensor;
import it.unisannio.rosariogoglia.model.SensorHumidity;
import it.unisannio.rosariogoglia.model.SensorTemperature;

import org.apache.log4j.Logger;

import it.unisannio.rosariogoglia.dao.SensorDAO;

public class SensorDAO {
	
	Logger logger = Logger.getLogger(SensorDAO.class);
		
	public List<Sensor> getSensors(){
		
		logger.debug("in getSensors");
		List<Sensor> listaSensori = new ArrayList<Sensor>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection();
			String query = "SELECT * FROM sensor ORDER BY name ASC";
			pstmt = connection.prepareStatement(query);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery(query);
			
			
			while(rs.next()){
								
				Sensor sensor = null;
				String type = rs.getString("type"); //seleziono la tipologia del sensore
				if(type.equals("temperature")) {
					sensor = new SensorTemperature();
					sensor.setIdSensor(rs.getInt("idsensor"));
					sensor.setName(rs.getString("name"));
					sensor.setType(type);
				}
				else if(type.equals("humidity")) {
					sensor = new SensorHumidity();
					sensor.setIdSensor(rs.getInt("idsensor"));
					sensor.setName(rs.getString("name"));
					sensor.setType(type);
				} 
				else{ //completare con altre tipologie di sensori
					
				}				
				
				System.out.println("");
				
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
	
	
	
	public Sensor getSensorById(Integer idSensor) {
		logger.debug("in getSensorById");
		Sensor sensor = null;
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			connection = DatabaseUtil.getConnection();
			
			String sql = "SELECT * FROM sensor WHERE (idsensor = ?)";
			pstmt = connection.prepareStatement(sql);
			
			pstmt.setInt(1, idSensor);
			logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			if (rs.next()){
				String type = rs.getString("type");
				if(type.equals("temperature")) {
					sensor = new SensorTemperature();
					sensor.setIdSensor(rs.getInt("idsensor"));
					sensor.setName(rs.getString("name"));
					sensor.setType(type);
				}
				else if(type.equals("humidity")) {
					sensor = new SensorHumidity();
					sensor.setIdSensor(rs.getInt("idsensor"));
					sensor.setName(rs.getString("name"));
					sensor.setType(type);
				} 
				else{ //completare con altre tipologie di sensori
					
				}	
			}
						
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
		return sensor;
	
	}
	

	/**
	 * Il metodo è usato per ottenere tutti i sensori "liberi", ossia non ancora associati a nessun nodo sensore
	 * 
	 * @param idSensorNode
	 * @return
	 */
	public List<Sensor> getSensorMancantiByIdSensorNode(Integer idSensorNode){
		logger.debug("in getSensorMancantiByIdSensorNode");
		List<Sensor> sensorsList = new ArrayList<Sensor>();
		Sensor sensor = null;
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			connection = DatabaseUtil.getConnection();
			String sql = "SELECT * FROM sensor WHERE sensornode_idsensorNode IS NULL";
			pstmt = connection.prepareStatement(sql);
			
			logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String type = rs.getString("type");
				if(type.equals("temperature")) {
					sensor = new SensorTemperature();
					sensor.setIdSensor(rs.getInt("idsensor"));
					sensor.setName(rs.getString("name"));
					sensor.setType(type);
				}
				else if(type.equals("humidity")) {
					sensor = new SensorHumidity();
					sensor.setIdSensor(rs.getInt("idsensor"));
					sensor.setName(rs.getString("name"));
					sensor.setType(type);
				} 
				else{ //completare con altre tipologie di sensori
					
				}
				
				sensorsList.add(sensor);
			
			} 
		
		}catch (SQLException  e) {
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
				
		return sensorsList;
		
	}
	
	/**
	 * Metodo usato per aggiornare la chiave esterna della tabella sensore. Viene inserito l'id del nodo sensore a cui viene associato il sensore.
	 * Ogni sensore è associato ad un solo nodo sensore (Relazione 1 a N)
	 * @param sensor
	 * @param idSensorNode
	 * @return 1 se l'aggiornamento è andato a buon fine, -1 se non è stato effettuato nessun aggironamento
	 */
	public Integer updateSensor(Sensor sensor, Integer idSensorNode) {
		logger.debug("in updateSensor");
		Integer uptadedRows = -1;
		
		Connection connection = null;
		PreparedStatement  pstmt = null;
		try {
			
			connection = DatabaseUtil.getConnection();
			
			connection.setAutoCommit(false);
			
			String sql = "UPDATE sensor SET sensornode_idsensorNode = ? WHERE idsensor = ?";
	
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, idSensorNode);
			pstmt.setInt(2, sensor.getIdSensor());
			logger.debug("Update Query:" + pstmt.toString());
			uptadedRows = pstmt.executeUpdate();
			
			SensorNodeDAO snDAO = new SensorNodeDAO();
			sensor.setSensorNode(snDAO.getSensorNodeByID(idSensorNode)); 
			
			connection.commit();
			logger.info("Sensore "+ sensor.getName() +" associato correttamente al nodo sensore " + sensor.getSensorNode().getDevice() );
	
		}catch (SQLException  e1) {
			e1.printStackTrace();
	
			try {
				connection.rollback();
				logger.debug("Roolback in aggiornamento prodotto"); 
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		return uptadedRows;
	}
	
	
	
	
	
	

}
