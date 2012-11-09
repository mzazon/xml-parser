package com.cse769.group3;

public class TPSAddress {

	private String address;
	private String city;
	private String state;
	private String zipCode;
	
	@Override
	public String toString() {
		return "TPSAddress [address=" + address + ", city=" + city + ", state="
				+ state + ", zipCode=" + zipCode + "]";
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}
