package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;



public class C2CVoucherTransferDetailsData {

	 @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("commissionProfileID")
    private String commissionProfileID;
    @JsonProperty("commissionProfileVersion")
    private String commissionProfileVersion;
    @JsonProperty("dualCommission")
    private String dualCommission;
    @JsonProperty("dualCommission")
    private String requestType;
    @JsonProperty("slablist")
    SlabList[] slablist;
    @JsonProperty("language1")
    private String language1;
    
    @JsonProperty("slablist")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */, description = "slablist")
	public SlabList[] getSlablist() {
		return slablist;
	}
    @JsonProperty("slablist")
	public void setSlablist(SlabList[] slablist) {
		this.slablist = slablist;
	}
	@JsonProperty("requestType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "TRFVINI", required = true, defaultValue = "TRFVINI", description = "requestType")
	public String getRequestType() {
		return requestType;
	}
	@JsonProperty("requestType")
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	@JsonProperty("dualCommission")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NC", required = true, defaultValue = "NC", description = "Dual Commission")
	public String getDualCommission() {
		return dualCommission;
	}
	@JsonProperty("dualCommission")
	public void setDualCommission(String dualCommission) {
		this.dualCommission = dualCommission;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1192", required = true, description = "Commission Profile Id")
	@JsonProperty("commissionProfileID")
	public String getCommissionProfileID() {
		return commissionProfileID;
	}
	@JsonProperty("commissionProfileID")
	public void setCommissionProfileID(String commissionProfileID) {
		this.commissionProfileID = commissionProfileID;
	}
	@JsonProperty("commissionProfileVersion")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */, description = "Commission Profile Version")
	public String getCommissionProfileVersion() {
		return commissionProfileVersion;
	}
	@JsonProperty("commissionProfileVersion")
	public void setCommissionProfileVersion(String commissionProfileVersion) {
		this.commissionProfileVersion = commissionProfileVersion;
	}
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */, description = "language1")
    public String getLanguage1() {
		return language1;
	}
	@JsonProperty("language1")
	public void setLanguage1(String language1) {
		this.language1 = language1;
	}
   
    @JsonProperty("extnwcode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = true, defaultValue = "NG", description = "External Network Code")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2CVoucherTransferDetailsData [extnwcode=");
		builder.append(extnwcode);
		builder.append(", commissionProfileID=");
		builder.append(commissionProfileID);
		builder.append(", commissionProfileVersion=");
		builder.append(commissionProfileVersion);
		builder.append(", dualCommission=");
		builder.append(dualCommission);
		builder.append(", requestType=");
		builder.append(requestType);
		builder.append(", slablist=");
		builder.append(Arrays.toString(slablist));
		builder.append(", language1=");
		builder.append(language1);
		builder.append("]");
		return builder.toString();
	}
    
	
}
