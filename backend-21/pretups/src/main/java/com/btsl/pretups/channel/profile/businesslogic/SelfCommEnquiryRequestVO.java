package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class SelfCommEnquiryRequestVO {
	
	@JsonProperty("identifierType")
	private String identifierType;
	
	@JsonProperty("identifierValue")
	private String identifierValue;
	
	@JsonProperty("identifierType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */)
	public String getIdentifierType() {
		return identifierType;
	}
	
	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}
	
	@JsonProperty("identifierValue")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getIdentifierValue() {
		return identifierValue;
	}
	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
	
	
	
	@Override
	public String toString(){
		 StringBuilder sb = new StringBuilder();
		    sb.append("class SelfCommEnquiryResponseVO {\n");
		    
		    sb.append("    IdentifierType: ").append(identifierType).append("\n");
		    sb.append("    IdentifierValue: ").append(identifierValue).append("\n");
		    return sb.toString();
		 
	 }
	

}
