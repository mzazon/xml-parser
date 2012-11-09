package com.cse769.group3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TPSFormOfPayment {
	
	protected Integer id;
	protected String cardNumber;
	protected String lastFour;
	protected String fullName;
	protected Date expiration;
	protected String cvv2;  // Don't think this is supposed to be stored, delete after processing?
	protected String phoneNumber;
	protected TPSPaymentType type;
	protected TPSUser user;
	protected TPSAddress billingAddress;

	public TPSFormOfPayment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		return "TPSFormOfPayment [id=" + id + ", cardNumber=" + cardNumber
				+ ", lastFour=" + lastFour + ", fullName=" + fullName
				+ ", expiration=" + expiration + ", cvv2=" + cvv2
				+ ", phoneNumber=" + phoneNumber + ", type=" + type + ", user="
				+ user + "]";
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

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
		this.lastFour = cardNumber.substring(12);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public TPSPaymentType getType() {
		return type;
	}

	public void setType(TPSPaymentType type) {
		this.type = type;
	}

	public TPSUser getUser() {
		return user;
	}

	public void setUser(TPSUser user) {
		this.user = user;
	}
	
	public TPSAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(TPSAddress billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	//
	// Static database methods
	//

	public static TPSFormOfPayment findById(int id) {
		Connection conn = DBConnection.getInstance().getConn();
		Statement stmt = null;
		TPSFormOfPayment pmpt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select P.*, T.NAME AS CARD_TYPE_NAME FROM TPS_FORMSOFPAYMENT P JOIN TPS_PAYMENT_TYPES T WHERE P.CARD_TYPE = T.ID AND P.ID = " + id);
			if (rs.first()) {
				// Got user
				pmpt = new TPSFormOfPayment();
				pmpt.setId(rs.getInt(1));
				pmpt.setCardNumber(rs.getString(3));
				pmpt.setFullName(rs.getString(5));
				pmpt.setCvv2(rs.getString(7));
				pmpt.setPhoneNumber(rs.getString(8));
				pmpt.setType(TPSPaymentType.valueOf(rs.getString(14)));
				pmpt.setUser(TPSUser.findById(rs.getInt(2)));
				
				TPSAddress addr = new TPSAddress();
				addr.setAddress(rs.getString(10));
				addr.setCity(rs.getString(11));
				addr.setState(rs.getString(12));
				addr.setZipCode(rs.getString(13));
				pmpt.setBillingAddress(addr);
				
				DateFormat formatter = new SimpleDateFormat("MMyy");
				try {
					Date d = formatter.parse(rs.getString(6));
					pmpt.setExpiration(d);
				} catch (ParseException e) {
					System.out.println("Couldn't read expiration date for card id " + pmpt.getId());
					e.printStackTrace();
				}
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
		return pmpt;
	}
	
	public static TPSFormOfPayment findByNameAndLastFour(String name, String lastFour) {
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
			pstmt = conn.prepareStatement("INSERT INTO TPS_FORMSOFPAYMENT (USER_ID,CARD_NUM,LAST_FOUR,FULL_NAME,EXPIRATION,CVV2,PHONE,CARD_TYPE,ADDRESS,CITY,STATE,ZIP) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, this.user.getId());
			pstmt.setString(2, this.cardNumber);
			pstmt.setString(3, this.lastFour);
			pstmt.setString(4, this.fullName);
			pstmt.setString(6, this.cvv2);
			pstmt.setString(7, this.phoneNumber);
			pstmt.setInt(8, this.type.ordinal());
			pstmt.setString(9, this.billingAddress.getAddress());
			pstmt.setString(10, this.billingAddress.getCity());
			pstmt.setString(11, this.billingAddress.getState());
			pstmt.setString(12, this.billingAddress.getZipCode());
			
			DateFormat formater = new SimpleDateFormat("MMyy");
			pstmt.setString(5, formater.format(this.expiration));
			
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
	
	public Boolean delete() {
		// TODO: formOfPayment.delete()
		return Boolean.TRUE;
	}
}
