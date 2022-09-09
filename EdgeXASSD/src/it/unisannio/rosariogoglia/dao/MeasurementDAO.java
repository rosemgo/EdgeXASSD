package it.unisannio.rosariogoglia.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.unisannio.rosariogoglia.dao.MeasurementDAO;
import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;
import it.unisannio.rosariogoglia.model.Measurement;

public class MeasurementDAO {
	
	Logger logger = Logger.getLogger(MeasurementDAO.class);
	
	
	/**
	 * Fornisce la lista di tutte le misurazioni prodotte dallo specifico sensore (definito dall'id passato come parametro)
	 * 
	 * @param idSensor
	 * @return
	 */
	public List<Measurement> getMeasurementListBySensorID(Integer idSensor) {
		logger.debug("in getMeasurementListBySensor");
		List<Measurement> measurementList = new ArrayList<>();
		
		
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		
			connection = DatabaseUtil.getConnection();
			
			String sql = "SELECT * FROM measurement WHERE (sensor_idsensor = ?) ORDER BY idmeasurement ASC";
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, idSensor);
			logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Measurement measurement = new Measurement();
				
				measurement.setIdMeasurement(rs.getInt("idmeasurement"));
				measurement.setValue(rs.getFloat("value"));
				measurement.setUnitOfMeasurement(rs.getString("unitOfMeasurement"));
				measurement.setType(rs.getString("type"));
				measurement.setDateMeasurement(rs.getDate("time"));
				measurement.setSensor(rs.getInt("sensor_idsensor"));
				measurement.setSensorNode(rs.getInt("sensorNode_idsensorNode"));
				
		//		logger.debug("(" + measurement.getIdMeasurement()+ ", " + measurement.getValue() + ", " + measurement.getDateMeasurement() + ", " + measurement.getSensor() + ", " + measurement.getSensorNode() +")");
				
				measurementList.add(measurement);
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
		
		return measurementList;
				
	}
	
	
	/**
	 * Fornisce l'n-esima misurazione (indicata da idNumberMeasurement)prodotta da uno specifico sensore
	 * 
	 * 
	 * @param idSensor
	 * @param idNumberMeasurement indica l' n-esima misurazione (sarebbe la misurazione che si vuole inserire nel grafico real time) (DA NON CONFONDERE CON L'IDMEASUREMENT)
	 * @return
	 */
	public Measurement getMeasurementByIdSensor(Integer idSensor, Integer idNumberMeasurement) {
		logger.debug("in getMeasurementBySensor");
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Measurement measurement = null;
		try {
			
			connection = DatabaseUtil.getConnection();
			String sql = "SELECT * FROM measurement WHERE (sensor_idsensor = ?) ORDER BY idmeasurement "
					+ "LIMIT  ? , 1"; //LIMIT significa che deve selezionare 1 sola tupla (indicato dall'1 come secondo paramentro del LIMIT) e deve essere la numero N (a partire da 0) indicata dall'offset (primo parametro del LIMIT) 
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, idSensor);
			pstmt.setInt(2, idNumberMeasurement);
			logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				measurement = new Measurement();
				
				measurement.setIdMeasurement(rs.getInt("idmeasurement"));
				measurement.setValue(rs.getFloat("value"));
				measurement.setUnitOfMeasurement(rs.getString("unitOfMeasurement"));
				measurement.setType(rs.getString("type"));
				measurement.setDateMeasurement(rs.getDate("time"));
				measurement.setSensor(rs.getInt("sensor_idsensor"));
				measurement.setSensorNode(rs.getInt("sensorNode_idsensorNode"));				
				
				logger.debug("(" + measurement.getIdMeasurement()+ ", " + measurement.getValue() + ", " + measurement.getDateMeasurement() + ", " + measurement.getSensor() + ", " + measurement.getSensorNode() +")");
							
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
			
		
		return measurement;
	}
	
	
	
	
	

}

