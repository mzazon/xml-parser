package com.cse769.group3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TPSTransaction {
	
	protected Integer id;
	protected TPSFormOfPayment formOfPayment;
	protected TPSUser user;
	protected TPSTicket ticket;
	protected Boolean isCompleted;

	public TPSTransaction() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return "TPSTransaction [id=" + id + ", formOfPayment=" + formOfPayment
				+ ", user=" + user + ", ticket=" + ticket + ", isCompleted="
				+ isCompleted + "]";
	}
	
	//
	// Getters / setters
	//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TPSFormOfPayment getFormOfPayment() {
		return formOfPayment;
	}

	public void setFormOfPayment(TPSFormOfPayment formOfPayment) {
		this.formOfPayment = formOfPayment;
	}

	public TPSUser getUser() {
		return user;
	}

	public void setUser(TPSUser user) {
		this.user = user;
	}

	public TPSTicket getTicket() {
		return ticket;
	}

	public void setTicket(TPSTicket ticket) {
		this.ticket = ticket;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	
	//
	// Static database methods
	//
	
	public static TPSTransaction findById(int id) {
		Connection conn = DBConnection.getInstance().getConn();
		Statement stmt = null;
		TPSTransaction trans = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from TPS_TRANSACTIONS t where t.ID = " + id);
			if (rs.first()) {
				// Got user
				trans = new TPSTransaction();
				trans.setId(rs.getInt(1));
				trans.setFormOfPayment(TPSFormOfPayment.findById(rs.getInt(2)));
				trans.setUser(TPSUser.findById(rs.getInt(3)));
				trans.setTicket(TPSTicket.findById(4));
				trans.setIsCompleted(rs.getBoolean(5));
			} 
		} catch (SQLException e) {
			// Something's wrong, will return null
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// ignore
					e.printStackTrace();
				}
			}
		}
		return trans;
	}
	
	public static TPSTransaction findByTicket(TPSTicket ticket) {
		//TODO: need to find transaction by ticket?
		return null;
	}

	//
	// Database methods
	//
	
	public Boolean insert() {
		Connection conn = DBConnection.getInstance().getConn();
		PreparedStatement pstmt = null;
		Boolean result = false;

		try {
			pstmt = conn.prepareStatement("INSERT INTO TPS_TRANSACTIONS (PAYMENT_ID,USER_ID,TICKET_ID,IS_COMPLETE) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, this.formOfPayment.getId());
			pstmt.setInt(2, this.user.getId());
			pstmt.setInt(3, this.ticket.getId());
			pstmt.setBoolean(4, this.isCompleted);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.first();
			this.setId(rs.getInt(1));
			result = true;
		} catch (SQLException e) {
			// Something's wrong, will return false
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					// ignore
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	public Boolean update() {
		//TODO: transaction.update()
		return Boolean.TRUE;
	}
	
	public Boolean delete() {
		//TODO: transaction.delete()
		return Boolean.TRUE;
	}

}
