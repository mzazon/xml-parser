package com.cse769.group3;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;

public class XMLParser {

	Document domEvent;
	Document domVenue;
	Document domUser;
	Document domTransaction;
	Document domPayment;
	List<TPSEvent> myEvents;
	List<TPSVenue> myVenues;
	List<TPSTransaction> myTransactions;
	List<TPSUser> myUsers;
	List<TPSFormOfPayment> myPayments;
	
	public class SimpleErrorHandler implements ErrorHandler {
	    public void warning(SAXParseException e) throws SAXException {
	        System.out.println("Warning: " + e.getMessage());
	    }

	    public void error(SAXParseException e) throws SAXException {
	        System.out.println("Error: " + e.getMessage());
	    }

	    public void fatalError(SAXParseException e) throws SAXException {
	        System.out.println("Fatal error: " + e.getMessage());
	    }
	}
	
	protected static void createTables() {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Could not load H2 driver!");
			System.exit(1);
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The H2 database connection failed!");
			System.exit(2);
		}
		
		try {
			Statement stmt = conn.createStatement();
			
			// Create tables
			stmt.execute("CREATE TABLE IF NOT EXISTS TPS_USERS (ID INT AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(255), PASSWORD VARCHAR(255), EMAIL VARCHAR(255), ADDRESS VARCHAR(255), CITY VARCHAR(255), STATE CHAR(2), ZIP VARCHAR(10))");
			stmt.execute("CREATE TABLE IF NOT EXISTS TPS_PAYMENT_TYPES (ID INT PRIMARY KEY, NAME VARCHAR(255))");
			stmt.execute("INSERT INTO TPS_PAYMENT_TYPES SELECT 0,'BuckId' FROM DUAL WHERE NOT EXISTS (SELECT ID FROM TPS_PAYMENT_TYPES WHERE ID = 0)");
			stmt.execute("INSERT INTO TPS_PAYMENT_TYPES SELECT 1,'Visa' FROM DUAL WHERE NOT EXISTS (SELECT ID FROM TPS_PAYMENT_TYPES WHERE ID = 1)");
			stmt.execute("INSERT INTO TPS_PAYMENT_TYPES SELECT 2,'MasterCard' FROM DUAL WHERE NOT EXISTS (SELECT ID FROM TPS_PAYMENT_TYPES WHERE ID = 2)");
			stmt.execute("INSERT INTO TPS_PAYMENT_TYPES SELECT 3,'Discover' FROM DUAL WHERE NOT EXISTS (SELECT ID FROM TPS_PAYMENT_TYPES WHERE ID = 3)");
			stmt.execute("CREATE TABLE IF NOT EXISTS TPS_FORMSOFPAYMENT (ID INT AUTO_INCREMENT PRIMARY KEY, USER_ID INT, CARD_NUM CHAR(16), LAST_FOUR CHAR(4), FULL_NAME VARCHAR(255), EXPIRATION CHAR(4), CVV2 CHAR(3), PHONE CHAR(10), CARD_TYPE INT, ADDRESS VARCHAR(255), CITY VARCHAR(255), STATE CHAR(2), ZIP VARCHAR(10), FOREIGN KEY (CARD_TYPE) REFERENCES TPS_PAYMENT_TYPES (ID), FOREIGN KEY (USER_ID) REFERENCES TPS_USERS (ID))");
			stmt.execute("CREATE TABLE IF NOT EXISTS Venue(VenueId int AUTO_INCREMENT PRIMARY KEY, Name varchar(255), Description varchar(255), Size int, Address varchar(255), City varchar(255), State varchar(255), ZipCode varchar(255))");
			stmt.execute("CREATE TABLE IF NOT EXISTS Event (EventId int AUTO_INCREMENT PRIMARY KEY, Name varchar(255), Date date, VenueId int, Description varchar(255), Category varchar(255), Cost decimal, FOREIGN KEY (VenueId) REFERENCES Venue(VenueId))");
			stmt.execute("CREATE TABLE IF NOT EXISTS Ticket (TicketId int AUTO_INCREMENT PRIMARY KEY, EventId int, Sold boolean, FOREIGN KEY (EventId) REFERENCES Event(EventId))");
			stmt.execute("CREATE TABLE IF NOT EXISTS TPS_TRANSACTIONS (ID INT AUTO_INCREMENT PRIMARY KEY, PAYMENT_ID INT, USER_ID INT, TICKET_ID INT, IS_COMPLETE BOOL, FOREIGN KEY (PAYMENT_ID) REFERENCES TPS_FORMSOFPAYMENT (ID), FOREIGN KEY (USER_ID) REFERENCES TPS_USERS (ID), FOREIGN KEY (TICKET_ID) REFERENCES Ticket (TicketId))");
			// Create user
			stmt.execute("CREATE USER IF NOT EXISTS TestDB PASSWORD 'abcd'");
			// Grant permissions
			stmt.execute("GRANT ALL ON TPS_USERS TO TestDB");
			stmt.execute("GRANT ALL ON TPS_TRANSACTIONS TO TestDB");
			stmt.execute("GRANT ALL ON TPS_PAYMENT_TYPES TO TestDB");
			stmt.execute("GRANT ALL ON TPS_FORMSOFPAYMENT TO TestDB");
			stmt.execute("GRANT ALL ON Venue TO TestDB");
			stmt.execute("GRANT ALL ON Event TO TestDB");
			stmt.execute("GRANT ALL ON Ticket TO TestDB");
			
			// Done
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
	
	public void ParseXML() {
		passOne();
		passTwo();
	}
	
	private void passOne(){
		
		DocumentBuilderFactory dbf1 = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory dbf3 = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory dbf4 = DocumentBuilderFactory.newInstance();
		DocumentBuilderFactory dbf5 = DocumentBuilderFactory.newInstance();
		
		dbf1.setValidating(true);
		dbf1.setNamespaceAware(true);
		
		dbf2.setValidating(true);
		dbf2.setNamespaceAware(true);
		
		dbf3.setValidating(true);
		dbf3.setNamespaceAware(true);
		
		dbf4.setValidating(true);
		dbf4.setNamespaceAware(true);
		
		dbf5.setValidating(true);
		dbf5.setNamespaceAware(true);
		
		dbf1.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
			      "http://www.w3.org/2001/XMLSchema");
		dbf2.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
			      "http://www.w3.org/2001/XMLSchema");
		dbf3.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
			      "http://www.w3.org/2001/XMLSchema");
		dbf4.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
			      "http://www.w3.org/2001/XMLSchema");		
		dbf5.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
			      "http://www.w3.org/2001/XMLSchema");
		
		try {
			DocumentBuilder db1 = dbf1.newDocumentBuilder();
			DocumentBuilder db2 = dbf2.newDocumentBuilder();
			DocumentBuilder db3 = dbf3.newDocumentBuilder();
			DocumentBuilder db4 = dbf4.newDocumentBuilder();
			DocumentBuilder db5 = dbf5.newDocumentBuilder();
			
			db1.setErrorHandler(new SimpleErrorHandler());
			db2.setErrorHandler(new SimpleErrorHandler());
			db3.setErrorHandler(new SimpleErrorHandler());
			db4.setErrorHandler(new SimpleErrorHandler());
			db5.setErrorHandler(new SimpleErrorHandler());

			domEvent = db1.parse("1tps_event_data.xml");
			domVenue = db2.parse("1tps_venue_data.xml");
			domUser = db3.parse("1tps_user_data.xml");
			domTransaction = db4.parse("1tps_transaction_data.xml");
			domPayment = db5.parse("1tps_payment_data.xml");

		}
		catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void passTwo(){
		Element rootElement = domUser.getDocumentElement();
		NodeList nl = rootElement.getElementsByTagName("User");
		
		//Step1: Parse our user data (domUser)
		rootElement = domUser.getDocumentElement();
		nl = rootElement.getElementsByTagName("User");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength() ; i++) {
				Element e = (Element)nl.item(i);
				TPSUser u = getUser(e);
				//myUsers.add(u);
				if(!u.insert())
					System.out.println("User insert failed.");
			}
		}
		
		//Step2: Parse our payment data (domPayment)
		rootElement = domPayment.getDocumentElement();
		nl = rootElement.getElementsByTagName("Payment");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength() ; i++) {
				Element e = (Element)nl.item(i);
				TPSFormOfPayment pay = getPayment(e);
				if(!pay.insert())
					System.out.println("Payment import failed.");
			}
		}
		
		//Step3: Parse our venues dom (domVenue)
		rootElement = domVenue.getDocumentElement();
		nl = rootElement.getElementsByTagName("Venue");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength() ; i++) {
				Element e = (Element)nl.item(i);
				TPSVenue ven = getVenue(e);
				try {
				if(!ven.insert())
					System.out.println("Venue import failed.");
				} catch(SQLException se) {
					se.printStackTrace();
				}
			}
		}
		
		//Step4: Parse our events dom (domEvent)
		rootElement = domEvent.getDocumentElement();
		nl = rootElement.getElementsByTagName("Event");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength() ; i++) {
				Element e = (Element)nl.item(i);
				TPSEvent ev = getEvent(e);
				try {
					if(!ev.insert())
						System.out.println("Event import failed.");
					} catch(SQLException se) {
						se.printStackTrace();
					}
				}
		}

		//Step5: Parse our transaction data (domTransaction)
		rootElement = domTransaction.getDocumentElement();
		nl = rootElement.getElementsByTagName("Transaction");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength() ; i++) {
				Element e = (Element)nl.item(i);
				TPSTransaction trans = getTransaction(e);
				if(!trans.insert())
					System.out.println("Transaction insert failed.");
			}
		}
		

	}
	
	private TPSEvent getEvent(Element eventElement) {
		
		TPSEvent retEvent = new TPSEvent();
		
		retEvent.setName(nodeValueToString(eventElement,"Name"));
		
		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-mm-dd");
		try {
			retEvent.setDate((Date)formatter.parse(nodeValueToString(eventElement,"Date")));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		TPSVenue tmpVenue = new TPSVenue();
		try {
			tmpVenue = TPSVenue.getVenueById(Integer.parseInt(nodeValueToString(eventElement,"VenueID")));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		retEvent.setVenue(tmpVenue);
		
		retEvent.setDescription(nodeValueToString(eventElement,"Description"));
		retEvent.setCategory(nodeValueToString(eventElement,"Category"));
		retEvent.setCost(Float.parseFloat(nodeValueToString(eventElement,"Cost")));

		return retEvent;
	}
	
	private TPSVenue getVenue(Element venueElement) {
		TPSVenue retVenue = new TPSVenue();
		TPSAddress addr = new TPSAddress();
		
		addr.setAddress(nodeValueToString(venueElement,"Address"));
		addr.setCity(nodeValueToString(venueElement,"City"));
		addr.setState(nodeValueToString(venueElement,"State"));
		addr.setZipCode(nodeValueToString(venueElement,"Zip"));
		
		retVenue.setName(nodeValueToString(venueElement,"Name"));
		retVenue.setDescription(nodeValueToString(venueElement,"Description"));
		retVenue.setAddress(addr);
		retVenue.setSize(Integer.parseInt(nodeValueToString(venueElement,"Size")));

		return retVenue;
	}
	
	private TPSUser getUser(Element userElement) {
		TPSUser retUser = new TPSUser();
		TPSAddress addr = new TPSAddress();
		
		addr.setAddress(nodeValueToString(userElement,"Address"));
		addr.setCity(nodeValueToString(userElement,"City"));
		addr.setState(nodeValueToString(userElement,"State"));
		addr.setZipCode(nodeValueToString(userElement,"Zip"));
		
		retUser.setUsername(nodeValueToString(userElement,"Username"));
		retUser.setPassword(nodeValueToString(userElement,"Password"));
		retUser.setEmail(nodeValueToString(userElement,"Email"));
		retUser.setAddress(addr);
		
		return retUser;
	}

	private TPSTransaction getTransaction(Element tElement) {
		
		TPSTransaction retTransaction = new TPSTransaction();
		retTransaction.setIsCompleted(Boolean.parseBoolean(nodeValueToString(tElement,"IsCompleted")));
		
		TPSFormOfPayment tmpPayment = TPSFormOfPayment.findById(Integer.parseInt(nodeValueToString(tElement,"PaymentId")));
		TPSUser tmpUser = TPSUser.findById(Integer.parseInt(nodeValueToString(tElement,"UserId")));
		
		//TODO: look up ticket by eventID, need to create new method of ticket class to do this
		TPSTicket tmpTicket = TPSTicket.findById(Integer.parseInt(nodeValueToString(tElement,"EventId")));
		
		retTransaction.setTicket(tmpTicket);
		retTransaction.setFormOfPayment(tmpPayment);
		retTransaction.setUser(tmpUser);
		
		return retTransaction;
	}
	
	private TPSFormOfPayment getPayment(Element paymentElement) {

		TPSFormOfPayment retPayment = new TPSFormOfPayment();
		retPayment.setFullName(nodeValueToString(paymentElement,"FullName"));
		retPayment.setPhoneNumber(nodeValueToString(paymentElement,"PhoneNumber"));
		retPayment.setCvv2(nodeValueToString(paymentElement,"Cvv2"));
		retPayment.setCardNumber(nodeValueToString(paymentElement,"CardNumber"));
	
		TPSUser tmpUser = TPSUser.findById(Integer.parseInt(nodeValueToString(paymentElement,"UserId")));
		
		TPSAddress tmpAddress = new TPSAddress();
		tmpAddress.setAddress(nodeValueToString(paymentElement,"Address"));
		tmpAddress.setCity(nodeValueToString(paymentElement,"City"));
		tmpAddress.setState(nodeValueToString(paymentElement, "State"));
		tmpAddress.setZipCode(nodeValueToString(paymentElement, "Zip"));
		retPayment.setBillingAddress(tmpAddress);
		
		retPayment.setType(TPSPaymentType.valueOf(nodeValueToString(paymentElement,"Type")));
		
		retPayment.setUser(tmpUser);

		DateFormat formatter;
		formatter = new SimpleDateFormat("mm-yy");
		try {
			retPayment.setExpiration((Date)formatter.parse(nodeValueToString(paymentElement,"Expiration")));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retPayment;
	}
	
	/**
	 * @author mike
	 * @param e
	 * @param tag
	 * @return string containing the element value at tag specified
	 */
	private String nodeValueToString(Element e, String tag) {
		NodeList nl = e.getElementsByTagName(tag);
		String txtValue = null;
		if (nl != null && nl.getLength() > 0) {
			Element e2 = (Element)nl.item(0);
			if (e2.getFirstChild() != null) {
				txtValue = e2.getFirstChild().getNodeValue();
			}
		}
		return txtValue;
	}	
	
	public static void main(String[] args) {
		// Make sure tables exist
		createTables();
	
		XMLParser parser = new XMLParser();
		parser.ParseXML();
		
		// Make sure db connection is closed
		DBConnection.getInstance().closeConn();
	}

}
