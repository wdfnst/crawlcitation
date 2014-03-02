package dao;

import java.sql.*;

public class JDBCTester {

	public static void main(String args[]) {
		System.out.println("JDBC Tester v1.0");
		try {
			Statement stmt;
			
			// Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");
			
			// Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url = "jdbc:mysql://localhost:3306/jzusdb";
			
			// Get a connection to the database for a 
			// user named xxx with a xxx password.
			Connection con = DriverManager.getConnection(url, "jzus","mzxwswws");
			
			// Display URL and connection information
			System.out.println("URL: " + url);
			System.out.println("Connection: " + con);
			
			// Get a Statement object
			stmt = con.createStatement();
			
			//Execute a simple command;We do not trap the response
			//We are only wanting a connection test
			boolean result;
			result = stmt.execute("show tables;");
			if(result)
			{
				System.out.println("SQL command was executed sucessfully");
			}
			
			con.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int tryDBConnection(String url, String username, String password) {
		try {
			Statement stmt;
			
			// Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");
			
			// Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			if (url.equals("")) {
				url = "jdbc:mysql://localhost:3306/jzusdb";
			}			
			// Get a connection to the database for a 
			// user named xxx with a xxx password.
			Connection con = DriverManager.getConnection(url, username,password);
			
			// Display URL and connection information
			System.out.println("URL: " + url);
			System.out.println("Connection: " + con);
			
			// Get a Statement object
			stmt = con.createStatement();
			
			//Execute a simple command;We do not trap the response
			//We are only wanting a connection test
//			boolean result;
//			result = stmt.execute("show tables;");
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM articlecitation");
		    // get the number of rows from the result set
		    rs.next();
		    int rowCount = rs.getInt(1);
		    System.out.println(rowCount);
//			if(result)
//			{
//				System.out.println("SQL command was executed sucessfully");
//			}
			con.close();
			stmt.close();
			return rowCount;
		} catch (Exception e) {
			return -1;
		}
	}
}