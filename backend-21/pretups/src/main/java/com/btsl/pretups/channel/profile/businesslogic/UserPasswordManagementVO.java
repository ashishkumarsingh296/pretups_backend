package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class UserPasswordManagementVO /*extends OAuthUser*/ {

	
	
	@JsonProperty("childLoginId")
	private String childLoginId;
	
	@JsonProperty("childMsisdn")
	private String childMsisdn;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("operationID")
	private int operationID;
	
	@JsonProperty("operationID")
	@io.swagger.v3.oas.annotations.media.Schema(example = "4", required = true/* , defaultValue = "" */)
	public int getOperationID() {
		return operationID;
	}

	public void setOperationID(int operationID) {
		this.operationID = operationID;
	}

	@JsonProperty("childMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72545454", required = true/* , defaultValue = "" */)
	public String getChildMsisdn() {
		return childMsisdn;
	}

	public void setChildMsisdn(String childMsisdn) {
		this.childMsisdn = childMsisdn;
	}

	
	
	@JsonProperty("childLoginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "dealer", required = false/* , defaultValue = "" */)
	public String getChildLoginId() {
		return childLoginId;
	}



	public void setChildLoginId(String childLoginId) {
		this.childLoginId = childLoginId;
	}

	

	@JsonProperty("remarks")
	@io.swagger.v3.oas.annotations.media.Schema(example = "change password", required = true/* , defaultValue = "" */)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	 @Override
	  public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class UserPasswordManagementVO {\n");
	    

	    sb.append("    childLoginId: ").append(childLoginId).append("\n");
	    sb.append("    childMsisdn: ").append(childMsisdn).append("\n");
	    sb.append("    remarks: ").append(remarks).append("\n");
	    sb.append("    operationID: ").append(operationID).append("\n");

	    return sb.toString();
	  }
	
}
