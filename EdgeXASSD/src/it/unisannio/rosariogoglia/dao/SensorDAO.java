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
				if(type.equals("temp")) {
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
				if(type.equals("temp")) {
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

	

}
