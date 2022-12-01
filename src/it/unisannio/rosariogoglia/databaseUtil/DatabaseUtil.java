package it.unisannio.rosariogoglia.databaseUtil;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.lang.Class;

public class DatabaseUtil {

	private static Connection connection;
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
	
/*		Properties properties = new Properties();
		properties.load(new FileInputStream(fileProperties));

        String jdbcDriver = properties.getProperty("driver");
        String hostname = properties.getProperty("hostname");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");     
        String database = properties.getProperty("database");     
 */       
		//utilizza il servizio mysql SAAS Amazon RDS (MySql 5.7)
		 String hostnameMySqlRDSAmazon = "jdbc:mysql://ros-mysql.cxajrtljcxhc.eu-south-1.rds.amazonaws.com:33000/"; 
		 //utilizza il servizio mysql installato nell'istanza EC2 di Amazon (MySql 5.7 di Ubuntu 18)
		 String hostnameMySqlEC2Amazon = "jdbc:mysql://ec2-15-160-35-226.eu-south-1.compute.amazonaws.com:3306/"; //posso usare l'indirizzo 15.160.35.226:3306 
		 String hostnameLocal = "jdbc:mysql://localhost:3306/"; //UTILIZZANDO localhost:3306 COME INDIRIZZO E' IMPORTANTE CHE IL DATABASE MySQL SIA SULLA STESSA MACCHINA DOVE E' PRESENTE TOMCAT (OSSIA IL WEB SERVER SU CUI E' DEPLOYATA L'APP)
		 
		 String jdbcDriver = "com.mysql.jdbc.Driver";
	     //jdbc:driver://hostname:port/dbName?user=userName&password=password
		 String hostname = hostnameMySqlRDSAmazon; //hostnameMySqlRDSAmazon;
	     String username = "admin";
	     String password = "rosariomarcofrancesco";  //cambiare password per database su amazon AWS con quella dell' utente root di mysql server  
//		 String username = "root";
//	     String password = "root"; 
		 String database = "sensoringdb";
	     String property = "?allowMultiQueries=true"; //consente di eseguire più query concatenate, mi serve per Resettare un Sensor Node nel metodo "updateSensorReset(Integer idSensorNode)" 
		
		//Carico il driver JDBC per la connessione con il database MySQL
		Class.forName(jdbcDriver);
		//String URL = jdbc:mysql://localhost:3306/ecommerce
	
	//	connection = DriverManager.getConnection(hostname + database, username, password); //Definiamo l'URL per la connessione

		connection = DriverManager.getConnection(hostname + database + property, username, password); //Definiamo l'URL per la connessione
		
		return connection;
		
	}
		
	
/*
	// Chiude la connessione con il Database
		public static void closeConnection() {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		public static void rollback (){
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		public static void beginTransaction(){
			try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public static void endTransaction(){
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		*/
}
