package it.unisannio.rosariogoglia.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import it.unisannio.rosariogoglia.dao.ProtocolDAO;
import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;
import it.unisannio.rosariogoglia.model.Protocol;
import it.unisannio.rosariogoglia.model.Sensor;
import it.unisannio.rosariogoglia.model.SensorHumidity;
import it.unisannio.rosariogoglia.model.SensorTemperature;

public class ProtocolDAO {
	
	Logger logger = Logger.getLogger(ProtocolDAO.class);
	
	public List<Protocol> getProtocol(){
		
		logger.debug("in getProtocol");
		List<Protocol> listaProtocolli = new ArrayList<Protocol>();
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection();
			String query = "SELECT * FROM protocol ORDER BY protocol ASC";
			pstmt = connection.prepareStatement(query);
			logger.debug("Select Query: " + query);
			rs = pstmt.executeQuery(query);
						
			while(rs.next()){
				Protocol protocol = new Protocol();
				protocol.setIdProtocol(rs.getInt("idProtocol"));
				protocol.setProtocol(rs.getString("protocol"));	
						
				logger.debug("(" + protocol.getIdProtocol() + ", " + protocol.getProtocol() +")");
				
				listaProtocolli.add(protocol);
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
		return listaProtocolli;
		
	}
	
	public Protocol getProtocolById(Integer idProtocol) {
		logger.debug("in getProtocolById");
		Protocol protocol = null;
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {

			connection = DatabaseUtil.getConnection();
			
			String sql = "SELECT * FROM protocol WHERE (idprotocol = ?)";
			pstmt = connection.prepareStatement(sql);
			
			pstmt.setInt(1, idProtocol);
			logger.debug("Select Query:" + pstmt.toString());
			rs = pstmt.executeQuery();
			if (rs.next()){
				
				protocol = new Protocol();
				protocol.setIdProtocol(rs.getInt("idprotocol"));
				protocol.setProtocol(rs.getString("protocol"));
				
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
		return protocol;
	
	}


}
