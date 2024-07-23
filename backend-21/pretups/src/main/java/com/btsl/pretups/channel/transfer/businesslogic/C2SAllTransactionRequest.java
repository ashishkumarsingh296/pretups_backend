package com.btsl.pretups.channel.transfer.businesslogic;



import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Help class - C2C Recent Buy Service
 * 
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class C2SAllTransactionRequest {

	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("fromDate")
	private String fromDate;
	
	@JsonProperty("toDate")
	private String toDate;
	
	@JsonProperty("serviceType")
	private String serviceType;

	// Getter Methods
	@JsonProperty("fromDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12/01/20", required = false/* , defaultValue = "" */)
	public String getFromDate() {
		return fromDate;
	}

	@JsonProperty("toDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12/02/20", required = false/* , defaultValue = "" */)
	public String getToDate() {
		return toDate;
	}


	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = false/* , defaultValue = "" */)
	public String getLanguage1() {
		return language1;
	}

	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	public String getExtnwcode() {
		return extnwcode;
	}

	@JsonProperty("serviceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "RC", required = true/* , defaultValue = "" */)
	public String getServiceType() {
		return serviceType;
	}

	// Setter Methods
	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2SAllTransactionRequest [language1=").append(language1).append(", extnwcode=")
				.append(extnwcode).append(", fromDate=").append(fromDate).append(", toDate=").append(toDate)
				.append(", serviceType=").append(serviceType).append("]");
		return builder.toString();
	}

	
}
