package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Help class - AutoCompleteRequestParentVO
 * 
 *
 */
@Schema(description = "This is a data field")
public class AutoCompleteUserDetailsData {
    
	@io.swagger.v3.oas.annotations.media.Schema(example = "1132s", required= true/*, position =1*/, description= "External Code")
	@JsonProperty("extcode")
	private String extcode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required= true/* , position =2 */, description ="Login Id")
	@JsonProperty("loginid")
	private String loginid;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required= true/* , position =3 */, description ="Password")
	@JsonProperty("password")
	private String password;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required= false/*, position =8*/, description ="Language2")
	@JsonProperty("language2")
	private String language2;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required= false/*, position =7*/, description ="Language1")
	@JsonProperty("language1")
	private String language1;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required= true/* , position =6 */, description ="External Network Code" )
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required= true/* , position =4 */, description ="Msisd")
	@JsonProperty("msisdn")
	private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required= true,/* position =5*/ description = "Pin")
	@JsonProperty("pin")
	private String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "923", required= true/*, position =9*/, description ="Msisdn to be searched for Users")
	@JsonProperty("msisdnToSearch")
	private String msisdnToSearch;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "dee", required= true/* , position =10 */, description ="Login Id to be searched for Users")
	@JsonProperty("loginidToSearch")
	private String loginidToSearch;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUT", required= true/*, position =11*/, description ="User Name to be searched for Users")
	@JsonProperty("usernameToSearch")
	private String usernameToSearch;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Distributor", required= false/*, position =12*/, description ="Domain of a User to be searched")
	@JsonProperty("domainCode")
	private String domainCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Super Distributor", required= false/* , position =10 */, description ="Category of a User to be Searched")
	@JsonProperty("categoryCode")
	private String categoryCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DELHI", required= false/*, position =13*/, description ="Geo domain of a User to be Searched")
	@JsonProperty("geoDomainCode")
	private String geoDomainCode;
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required= false/* , position =4 */, description = "specificSearch", hidden = true)
	@JsonProperty("specificSearch")
	private String specificSearch;
	
	public String getSpecificSearch() {
		return specificSearch;
	}

	public void setSpecificSearch(String specificSearch) {
		this.specificSearch = specificSearch;
	}

	@JsonProperty("extcode")
	public String getExtcode() {
		return extcode;
	}

	
	@JsonProperty("loginid")
	public String getLoginid() {
		return loginid;
	}

	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	@JsonProperty("pin")
	public String getPin() {
		return pin;
	}

	@JsonProperty("msisdnToSearch")
	public String getMsisdnToSearch() {
		return msisdnToSearch;
	}


	@JsonProperty("loginidToSearch")
	public String getLoginidToSearch() {
		return loginidToSearch;
	}

	@JsonProperty("usernameToSearch")
	public String getUsernameToSearch() {
		return usernameToSearch;
	}

	@JsonProperty("domainCode")
	public String getDomainCode() {
		return domainCode;
	}

	@JsonProperty("categoryCode")
	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	@JsonProperty("language2")
	public String getLanguage2() {
		return language2;
	}

	
	@JsonProperty("language1")
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("extnwcode")
	public String getExtnwcode() {
		return extnwcode;
	}



	@JsonProperty("msisdn")
	public String getMsisdn() {
		return msisdn;
	}

	// Setter Methods


	public void setPin(String pin) {
		this.pin = pin;
	}


	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}


	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMsisdnToSearch(String msisdnToSearch) {
		this.msisdnToSearch = msisdnToSearch;
	}


	public void setLoginidToSearch(String loginidToSearch) {
		this.loginidToSearch = loginidToSearch;
	}


	public void setUsernameToSearch(String usernameToSearch) {
		this.usernameToSearch = usernameToSearch;
	}


	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	
	public String getGeoDomainCode() {
		return geoDomainCode;
	}


	public void setGeoDomainCode(String geoDomainCode) {
		this.geoDomainCode = geoDomainCode;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AutoCompleteRequestParentVO [extcode=");
		sb.append(extcode);
		sb.append(", loginid=");
		sb.append(loginid);
		sb.append(", password=");
		sb.append(password);
		sb.append(", language2=");
		sb.append(language2);
		sb.append(", language1=");
		sb.append(language1);
		sb.append(", extnwcode=");
		sb.append(extnwcode);
		sb.append( ", msisdn=");
		sb.append(msisdn);
		sb.append(", pin=");
		sb.append(pin);
		sb.append(", loginidToSearch=");
		sb.append(loginidToSearch);
		sb.append(", msisdnToSearch=");
		sb.append(msisdnToSearch);
		sb.append(", usernameToSearch=");
		sb.append(usernameToSearch);
		sb.append(", domainCode=");
		sb.append(domainCode);
		sb.append(", categoryCode=");
		sb.append(categoryCode);
		sb.append(", geoDomainCode=");
		sb.append(geoDomainCode);
		return sb.toString();
		}

}
