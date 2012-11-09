package com.cse769.group3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TPSVenue {
	
	protected Integer id;
	protected String name;
	protected String description;
	protected TPSAddress address;
	protected Integer size;
	
	private static Connection conn = null;
	private static Statement stmt = null;

	public TPSVenue() {
		// TODO Auto-generated constructor stub
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TPSAddress getAddress() {
		return address;
	}

	public void setAddress(TPSAddress address) {
		this.address = address;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
public Boolean insert() throws SQLException {
		
		conn = DBConnection.getInstance().getConn();
		stmt = conn.createStatement();
				
		String query = "INSERT INTO Venue (Name, Description, Size, Address, City, State, ZipCode) VALUES ("
				+ "'" + this.getName() + "'"
				+ ',' 
				+ "'" + this.getDescription() + "'"
				+ ','
				+ "'" + this.getSize() + "'"
				+ ','
				+ "'" + this.getAddress().getAddress() + "'"
				+ ','
				+ "'" + this.getAddress().getCity() + "'"
				+ ','
				+ "'" + this.getAddress().getState() + "'"
				+ ','
				+ "'" + this.getAddress().getZipCode() + "'" 
				+ ")";
		
		try {			
			stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet r = stmt.getGeneratedKeys();
			if (r.next()) {
				this.setId(r.getInt(1));								
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		
		return true;
	}
	
	public Boolean update() {
		return Boolean.TRUE;
	}
	
	public Boolean delete() {
		return Boolean.TRUE;
	}
	
	public static TPSVenue getVenueById(int id) throws SQLException {
		conn = DBConnection.getInstance().getConn();
		stmt = conn.createStatement();
		TPSVenue venue = new TPSVenue();
		
		String query = "SELECT * FROM Venue WHERE VenueId='" + id + "'";		
		ResultSet rs = stmt.executeQuery(query);
		
		if (rs.next()) {
			venue.setId(rs.getInt("VenueId"));
			venue.setName(rs.getString("Name"));			
			venue.setDescription(rs.getString("Description"));
			venue.setSize(rs.getInt("Size"));
			TPSAddress address = new TPSAddress();
			address.setAddress(rs.getString("Address"));
			address.setCity(rs.getString("City"));
			address.setState(rs.getString("State"));
			address.setZipCode(rs.getString("ZipCode"));
			venue.setAddress(address);
			return venue;
		}
		return null;
	}

}
