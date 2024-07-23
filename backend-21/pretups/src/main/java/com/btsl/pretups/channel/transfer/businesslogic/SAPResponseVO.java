package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SAPResponseVO extends BaseResponse{
	
	@JsonProperty("city")
	private String city;
	
	@JsonProperty("address")
	private String address;

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("telephone")
	private String telephone;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("state")
	private String state;
	
	@JsonProperty("country")
	private String country;
	
	@JsonProperty("empCode")
	private String empCode;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SAPResponseVO [city=");
		builder.append(city);
		builder.append(", address=");
		builder.append(address);
		builder.append(", name=");
		builder.append(name);
		builder.append(", telephone=");
		builder.append(telephone);
		builder.append(", email=");
		builder.append(email);
		builder.append(", state=");
		builder.append(state);
		builder.append(", country=");
		builder.append(country);
		builder.append(", empCode=");
		builder.append(empCode);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
