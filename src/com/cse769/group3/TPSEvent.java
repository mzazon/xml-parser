/**
 * 
 */
package com.cse769.group3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;


public class TPSEvent {
	
	protected int id;
	protected String name;
	protected Date date;
	private TPSVenue venue;
	protected String description;
	protected double cost;
	private int quantity;
	protected String category;

	private static Connection conn = null;
	private static Statement stmt = null;
	
	/**
	 * 
	 */
	public TPSEvent(String name, Date date, TPSVenue venue, String description, String category, double cost, int quantity) {
		this.name = name;
		this.date = date;
		this.venue = venue;
		this.description = description;
		this.category = category;
		this.cost = cost;
		this.quantity = quantity;
	}

	public TPSEvent() {
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TPSVenue getVenue() {
		return venue;
	}

	public void setVenue(TPSVenue venue) {
		this.venue = venue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public Boolean insert() throws SQLException {
		
		conn = DBConnection.getInstance().getConn();
		stmt = conn.createStatement();
		
		//TODO: why not just use this.date?
		java.sql.Date date = new java.sql.Date(this.date.getTime());
		
		String query = "INSERT INTO Event(Name, Date, VenueId, Description, Category, Cost) VALUES ("
				+ "'" + this.name + "'"
				+ ','
				+ "'" + date + "'"
				+ ','
				+ "'" + this.venue.getId() + "'"
				+ ','
				+ "'" + this.description + "'"
				+ ','
				+ "'" + this.category + "'"
				+ ','
				+ "'" + this.cost + "'" + ")";
		
		try {			
			stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet r = stmt.getGeneratedKeys();
			if (r.next()) {
				this.setId(r.getInt(1));				
				for (int i = 0; i < this.venue.getSize(); i++) {
					query = "INSERT INTO Ticket(EventId, Sold) VALUES ('" + this.id + "'," + "false)";
					stmt.executeUpdate(query);
				}
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
	
	public static TPSEvent getEventbyId(int id) throws SQLException {
		
		conn = DBConnection.getInstance().getConn();
		stmt = conn.createStatement();
		TPSEvent event = new TPSEvent();
		
		String query = "SELECT * FROM EVENT WHERE EventId='" + id + "'";
		
		ResultSet rs = stmt.executeQuery(query);
		
		if (rs.next()) {
			event.setId(rs.getInt("EventId"));
			event.setName(rs.getString("Name"));
			event.setDate(rs.getDate("Date"));
			event.setVenue(TPSVenue.getVenueById(rs.getInt("VenueId")));
			event.setDescription(rs.getString("Description"));
			event.setCategory(rs.getString("Category"));
			event.setCost(rs.getFloat("Cost"));
			return event;
		}
		return null;
	}

}
