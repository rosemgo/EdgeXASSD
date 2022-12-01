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
	public List<Measurement> getAllMeasurementListBySensorID(Integer idSensor) {
		logger.debug("in getAllMeasurementListBySensor");
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
				measurement.setDateMeasurement(rs.getTimestamp("time"));
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
	 * Fornisce la lista di tutte le ultime 200 misurazioni prodotte dallo specifico sensore (definito dall'id passato come parametro)
	 * 
	 * @param idSensor
	 * @return
	 */
	public List<Measurement> getMeasurementListBySensorID(Integer idSensor) {
		logger.debug("in getMeasurementListBySensor");
		List<Measurement> measurementList = new ArrayList<>();
		List<Measurement> measurementListFinal = new ArrayList<>();
		
		int limitMeasurement = 200; //INDICA IL NUMERO DI MISURAZIONI DA VISUALIZZARE NEL GRAFICO LA PRIMA VOLTA CHE VIENE AVVIATO IL MONITORAGGIO DI UN SENSORE
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		
			connection = DatabaseUtil.getConnection();
			
		//	String sql = "SELECT * FROM measurement WHERE (sensor_idsensor = ?) ORDER BY idmeasurement ASC";
			String sql = "SELECT * FROM measurement WHERE (sensor_idsensor = ?) ORDER BY idmeasurement DESC LIMIT ?";
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, idSensor);
			pstmt.setInt(2, limitMeasurement);
			logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Measurement measurement = new Measurement();
				
				measurement.setIdMeasurement(rs.getInt("idmeasurement"));
				measurement.setValue(rs.getFloat("value"));
				measurement.setUnitOfMeasurement(rs.getString("unitOfMeasurement"));
				measurement.setType(rs.getString("type"));
				measurement.setDateMeasurement(rs.getTimestamp("time"));
				measurement.setSensor(rs.getInt("sensor_idsensor"));
				measurement.setSensorNode(rs.getInt("sensorNode_idsensorNode"));
				
		//		logger.debug("(" + measurement.getIdMeasurement()+ ", " + measurement.getValue() + ", " + measurement.getDateMeasurement() + ", " + measurement.getSensor() + ", " + measurement.getSensorNode() +")");
				
				measurementList.add(measurement);
			}
			
			
			if(measurementList.size()<limitMeasurement)
				for(int i=measurementList.size(); i>0; i--) {
					measurementListFinal.add(measurementList.get(i-1));
				}
			else {
				for(int i=limitMeasurement; i>0; i--) {
					measurementListFinal.add(measurementList.get(i-1));
				}
			}
			
			//INVERSIONE DELL'ORDINE DELLA LISTA DI MISURAZIONI OTTENUTE, PERCHE' DALLA QUERY SQL OTTENIAMO LE MISURAZIONI ORDINATE IN ORDINE DECRESCENTE (DALLA PIU' RECENTE ALLA MENO RECENTE) PERCHE' A NOI SERVONO LE ULTIME X MISURAZIONI
			//PERO' VA INVERTITO L'ORDINE PERCHè NEL GRAFICO DEVONO COMPARIRE DALLA MENO RECENTE ALLA PIù RECENTE
			for(int i=0; i<measurementList.size(); i++) {
				System.out.print(" " + measurementList.get(i).getIdMeasurement());
			}
			System.out.println("");
			for(int i=0; i<measurementListFinal.size(); i++) {
				System.out.print(" " + measurementListFinal.get(i).getIdMeasurement());
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
		
		return measurementListFinal;
				
	}
	
	
	/**
	 * Fornisce l'n-esima misurazione (indicata da idNumberMeasurement)prodotta da uno specifico sensore
	 * 
	 * 
	 * @param idSensor
	 * @param idNumberMeasurement indica l' n-esima misurazione (sarebbe la misurazione che si vuole inserire nel grafico real time) (DA NON CONFONDERE CON L'IDMEASUREMENT)
	 * @return
	 */
	public List<Measurement> getMeasurementByIdSensor(Integer idSensor, Integer idNumberMeasurement) {
	//	logger.debug("in getMeasurementBySensor");
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<Measurement> measurementList = new ArrayList<>();
		try {
			
			connection = DatabaseUtil.getConnection();
		//	String sql = "SELECT * FROM measurement WHERE (sensor_idsensor = ?) ORDER BY idmeasurement "
		//			+ "LIMIT  ? , 18446744073709551615"; //LIMIT significa che deve selezionare 18446744073709551615 tuple (indicato dall'18446744073709551615 come secondo paramentro del LIMIT) a partire dalla numero N (a partire da 0) indicata dall'offset (primo parametro del LIMIT) 
			String sql = "SELECT * FROM measurement WHERE (sensor_idsensor = ? AND idmeasurement > ?) ORDER BY idmeasurement ASC";
						
			pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, idSensor);
			pstmt.setInt(2, idNumberMeasurement);
	//		logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Measurement measurement = new Measurement();
				
				measurement.setIdMeasurement(rs.getInt("idmeasurement"));
				measurement.setValue(rs.getFloat("value"));
				measurement.setUnitOfMeasurement(rs.getString("unitOfMeasurement"));
				measurement.setType(rs.getString("type"));
				measurement.setDateMeasurement(rs.getTimestamp("time"));
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
	
	
	
	
	

}

