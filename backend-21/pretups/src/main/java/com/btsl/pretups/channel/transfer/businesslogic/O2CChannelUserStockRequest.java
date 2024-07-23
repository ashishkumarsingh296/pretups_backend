package com.btsl.pretups.channel.transfer.businesslogic;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author piyush.bansal
 *
 */

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class O2CChannelUserStockRequest {
	
	@JsonProperty("fromDate")
	private String fromDate;
	
	@JsonProperty("toDate")
	private String toDate;
	

	// Getter Methods
	@JsonProperty("fromDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12/01/20", required = true/* , defaultValue = "" */)
	public String getFromDate() {
		return fromDate;
	}

	@JsonProperty("toDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "12/02/20", required = true/* , defaultValue = "" */)
	public String getToDate() {
		return toDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CChannelUserStockRequest [fromDate=").append(fromDate).append(", toDate=").append(toDate)
				.append("]");
		return builder.toString();
	}
		
}
