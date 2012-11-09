package com.cse769.group3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TPSUser {
	
	protected Integer id;
	protected String username;
	protected String password;  // assume this is already encrypted? otherwise setter can do it
	protected String email;
	protected TPSAddress address;

	public TPSUser() {
		// TODO Auto-generated constructor stub
	}
	
	//
	// Getters / setters
	//

	@Override
	public String toString() {
		return "TPSUser [id=" + id + ", username=" + username + ", password="
				+ password + ", email=" + email + ", address=" + address + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public TPSAddress getAddress() {
		return address;
	}

	public void setAddress(TPSAddress address) {
		this.address = address;
	}

	
	//
	// Static database methods
	//
	
	public static TPSUser findById(int id) {
		Connection conn = DBConnection.getInstance().getConn();
		Statement stmt = null;
		TPSUser user = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from TPS_USERS u where u.ID = " + id);
			if (rs.first()) {
				// Got user
				user = new TPSUser();
				user.setId(rs.getInt(1));
				user.setUsername(rs.getString(2));
				user.setPassword(rs.getString(3));
				user.setEmail(rs.getString(4));
				TPSAddress addr = new TPSAddress();
				addr.setAddress(rs.getString(5));
				addr.setCity(rs.getString(6));
				addr.setState(rs.getString(7));
				addr.setZipCode(rs.getString(8));
				user.setAddress(addr);
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
		return user;
	}
	
	public static TPSUser findByUsername (String username) {
		// TODO: findByUsername needed?
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
			pstmt = conn.prepareStatement("INSERT INTO TPS_USERS (USERNAME,PASSWORD,EMAIL,ADDRESS,CITY,STATE,ZIP ) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, this.username);
			pstmt.setString(2, this.password);
			pstmt.setString(3, this.email);
			pstmt.setString(4, this.address.getAddress());
			pstmt.setString(5, this.address.getCity());
			pstmt.setString(6, this.address.getState());
			pstmt.setString(7, this.address.getZipCode());
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
		// TODO: user.update()
		return Boolean.TRUE;
	}
	
	public Boolean delete() {
		// TODO: user.delete()
		return Boolean.TRUE;
	}

}
