package com.cse769.group3;

import java.sql.*;

public class DBConnection {
	
	private static DBConnection instance;
	
	private Connection conn;

	protected DBConnection() {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Could not load H2 driver!");
			System.exit(1);
		}
		try {
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "TestDB", "abcd");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The H2 database connection failed!");
			System.exit(2);
		}
			
	}

	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new DBConnection();
		}
		return instance;
	}

	public Connection getConn() {
		return conn;
	}
	
	public void closeConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			// ignore
			e.printStackTrace();
		}
		
		instance = null;
	}
}
