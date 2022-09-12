package it.unisannio.rosariogoglia.databaseUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabase {

	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		
		
		 //scrivo anche nel database locale
        Connection connection = DatabaseUtil.getConnection(); 
        connection.setAutoCommit(false);
        
        System.out.println("Database: " + connection);
        
        String sql = "INSERT INTO measurement (value, sensor, time) VALUES (?, ?, NOW())";
        PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setDouble(1, Double.valueOf(500.90));
        pstmt.setString(2, "io");
        
        int insertRows = pstmt.executeUpdate();
		System.out.println("righe inserite: "+ insertRows);
        
        connection.commit();
        
        System.out.println(pstmt);
        
	}

}
