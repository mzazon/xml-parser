package com.cse769.group3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TPSTicket {

	private int id;
	private TPSEvent event;
	private boolean soldFlag;
	
	private static Connection conn = null;
	private static Statement stmt = null;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TPSEvent getEvent() {
		return event;
	}
	public void setEvent(TPSEvent event) {
		this.event = event;
	}
	public boolean isSoldFlag() {
		return soldFlag;
	}
	public void setSoldFlag(boolean soldFlag) {
		this.soldFlag = soldFlag;
	}	
	
	
	
	public static TPSTicket getTicketById(int id) throws SQLException {
		conn = DBConnection.getInstance().getConn();
		stmt = conn.createStatement();
		TPSTicket ticket = new TPSTicket();
		
		String query = "SELECT * FROM Ticket WHERE TicketId='" + id + "'";		
		ResultSet rs = stmt.executeQuery(query);
		
		if (rs.next()) {
			ticket.setId(rs.getInt("TicketId"));
			ticket.setSoldFlag(rs.getBoolean("Sold"));						
			TPSEvent event = TPSEvent.getEventbyId(rs.getInt("EventId"));
			ticket.setEvent(event);			
			return ticket;
		}
		return null;
	}
	
	
	//
	// Static database methods
	//
	
	public static TPSTicket findById(int id) {
		// wrap getTicketById
		TPSTicket tkt = null;
		try {
			tkt = getTicketById(id);
		} catch (SQLException e) {
			// ignore
			e.printStackTrace();
		}
		return tkt;
	}

	//
	// Database methods
	//
	
	public Boolean insert() {
		//TODO: ticket.insert()
		return Boolean.TRUE;
	}
	
	public Boolean update() {
		//TODO: ticket.update()
		return Boolean.TRUE;
	}
	
	public Boolean delete() {
		//TODO: ticket.delete()
		return Boolean.TRUE;
	}
	
}
