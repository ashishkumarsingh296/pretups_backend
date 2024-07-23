package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



public class C2CVoucherTransferDetailsData {
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("extnwcode")
    private String extnwcode;
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("commissionProfileID")
    private String commissionProfileID;
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("commissionProfileVersion")
    private String commissionProfileVersion;
	 
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("dualCommission")
    private String dualCommission;
	 
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("requestType")
    private String requestType;

    @JsonProperty("slablist")
    SlabList[] slablist;
	 
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("language1")
    private String language1;
	 
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("transferType")
    private String transferType;
	 
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("transferSubType")
    private String transferSubType;
	 
	 @JsonInclude(Include.NON_NULL)
    @JsonProperty("cbcflag")
    private String cbcflag;
    
	 @JsonInclude(Include.NON_NULL)
	 @JsonProperty("paymentInfo")
	  private String paymentInfo;
	 
		@JsonProperty("paymentInfo")
		@io.swagger.v3.oas.annotations.media.Schema(example = "ALL/CASH", required = true, defaultValue = "ALL/CASH", description = "slablist")
    public String getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "TRANSFER/BUY", required = true, defaultValue = "TRANSFER", description = "requestType")
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true, defaultValue = "NG", description = "External Network Code")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
    @JsonProperty("transferType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "O2C/C2C", required = true, defaultValue = "O2C/C2C", description = "transferType")
	public String getTransferType() {
		return transferType;
	}
    @JsonProperty("transferType")
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
    @JsonProperty("transferSubType")
    @io.swagger.v3.oas.annotations.media.Schema(example = "V", required = true, defaultValue = "V", description = "transferSubType V is Fixed")
	public String getTransferSubType() {
		return transferSubType;
	}
    @JsonProperty("transferSubType")
	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}
    @JsonProperty("cbcflag")
    @io.swagger.v3.oas.annotations.media.Schema(example = "N", required = true, defaultValue = "N", description = "cbcflag")
	public String getCbcflag() {
		return cbcflag;
	}
    @JsonProperty("cbcflag")
	public void setCbcflag(String cbcflag) {
		this.cbcflag = cbcflag;
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
		builder.append(", cbcflag=");
		builder.append(cbcflag);
		builder.append(", transferSubType=");
		builder.append(transferSubType);
		builder.append(", transferType=");
		builder.append(transferType);
		builder.append("]");
		return builder.toString();
	}
    

}
