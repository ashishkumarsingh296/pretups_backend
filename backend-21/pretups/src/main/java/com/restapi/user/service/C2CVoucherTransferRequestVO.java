package com.restapi.user.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reqGatewayLoginId",
    "reqGatewayPassword",
    "reqGatewayCode",
    "reqGatewayType",
    "servicePort",
    "sourceType",
    "data"
})
public class C2CVoucherTransferRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("servicePort")
    private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("sourceType")
    private String sourceType;
	
    @JsonProperty("data")
    private DataVcrTrf data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayLoginId")
    public String getReqGatewayLoginId() {
        return reqGatewayLoginId;
    }

    @JsonProperty("reqGatewayLoginId")
    public void setReqGatewayLoginId(String reqGatewayLoginId) {
        this.reqGatewayLoginId = reqGatewayLoginId;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayPassword")
    public String getReqGatewayPassword() {
        return reqGatewayPassword;
    }

    @JsonProperty("reqGatewayPassword")
    public void setReqGatewayPassword(String reqGatewayPassword) {
        this.reqGatewayPassword = reqGatewayPassword;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayCode")
    public String getReqGatewayCode() {
        return reqGatewayCode;
    }

    @JsonProperty("reqGatewayCode")
    public void setReqGatewayCode(String reqGatewayCode) {
        this.reqGatewayCode = reqGatewayCode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayType")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */)
    @JsonProperty("servicePort")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = false/* , defaultValue = "" */)
    @JsonProperty("sourceType")
    public String getSourceType() {
        return sourceType;
    }

    @JsonProperty("sourceType")
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @JsonProperty("data")
    public DataVcrTrf getDatabuyvcr() {
        return data;
    }

    @JsonProperty("data")
    public void setDatabuyvcr(DataVcrTrf data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("reqGatewayLoginId = ").append(reqGatewayLoginId)
        		.append("sourceType").append( sourceType)
        		.append("reqGatewayType = ").append( reqGatewayType)
        		.append("reqGatewayPassword = ").append(reqGatewayPassword)
        		.append("servicePort = ").append(servicePort)
        		.append("reqGatewayCode = ").append(reqGatewayCode).append("data = ").append( data)).toString();
    }


}


class DataVcrTrf {

    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("password")
    private String password;
    @JsonProperty("extcode")
    private String extcode;
    @JsonProperty("extrefnum")
    private String extrefnum;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("loginid2")
    private String loginid2;
    @JsonProperty("extcode2")
    private String extcode2;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("paymentinstcode")
    private String paymentinstcode;
    @JsonProperty("paymentinstdate")
    private String paymentinstdate;
    @JsonProperty("paymentinstnum")
    private String paymentinstnum;
    @JsonProperty("voucherDetails")
    private List<VoucherDetailTrf> voucherDetails = null;
    @JsonProperty("remarks")
    private String remarks;
    
    /*
     * Chnages for File upload functionality
     */
    @io.swagger.v3.oas.annotations.media.Schema(example = "jpg", required = true/* , position = 1 */, description="File Type(pdf, jpg, png")
    @JsonProperty("fileType")
    private String fileType;
    @io.swagger.v3.oas.annotations.media.Schema(example = "c2cBatchTransfer", required = true/* , position = 2 */, description="File Name")
    @JsonProperty("fileName")
    private String fileName;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true/* , position = 3 */, description="Base64 Encoded File as String")
    @JsonProperty("fileAttachment")
    private String fileAttachment;
    @io.swagger.v3.oas.annotations.media.Schema(example = "true", required = true/* , position = 4 */, description="boolean value (true/false)")
    @JsonProperty("fileUploaded")
    private String fileUploaded;
   
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = true/* , defaultValue = "" */)
    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */)
    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("extrefnum")
    public String getExtrefnum() {
        return extrefnum;
    }

    @JsonProperty("extrefnum")
    public void setExtrefnum(String extrefnum) {
        this.extrefnum = extrefnum;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
    @JsonProperty("msisdn2")
    public String getMsisdn2() {
        return msisdn2;
    }

    @JsonProperty("msisdn2")
    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
    @JsonProperty("loginid2")
    public String getLoginid2() {
        return loginid2;
    }

    @JsonProperty("loginid2")
    public void setLoginid2(String loginid2) {
        this.loginid2 = loginid2;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("extcode2")
    public String getExtcode2() {
        return extcode2;
    }

    @JsonProperty("extcode2")
    public void setExtcode2(String extcode2) {
        this.extcode2 = extcode2;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentinstcode")
    public String getPaymentinstcode() {
        return paymentinstcode;
    }

    @JsonProperty("paymentinstcode")
    public void setPaymentinstcode(String paymentinstcode) {
        this.paymentinstcode = paymentinstcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "01/01/2020", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentinstdate")
    public String getPaymentinstdate() {
        return paymentinstdate;
    }

    @JsonProperty("paymentinstdate")
    public void setPaymentinstdate(String paymentinstdate) {
        this.paymentinstdate = paymentinstdate;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentinstnum")
    public String getPaymentinstnum() {
        return paymentinstnum;
    }

    @JsonProperty("paymentinstnum")
    public void setPaymentinstnum(String paymentinstnum) {
        this.paymentinstnum = paymentinstnum;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("voucherDetails")
    public List<VoucherDetailTrf> getVoucherDetails() {
        return voucherDetails;
    }

    @JsonProperty("voucherDetails")
    public void setVoucherDetails(List<VoucherDetailTrf> voucherDetails) {
        this.voucherDetails = voucherDetails;
    }
   
    @io.swagger.v3.oas.annotations.media.Schema(example = "remarks", required = true/* , defaultValue = "" */)
    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    
    public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	public String getFileUploaded() {
		return fileUploaded;
	}

	public void setFileUploaded(String fileUploaded) {
		this.fileUploaded = fileUploaded;
	}

	
	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("language1").append( language1)
        		.append("paymentinstcode = ").append( paymentinstcode)
        		.append("paymentinstdate = ").append( paymentinstdate)
        		.append("paymentinstnum = ").append( paymentinstnum)
        		.append("voucherDetails = ").append( voucherDetails)
        		.append("remarks").append( remarks)
        		.append("extnwcode = ").append(extnwcode)
        		.append("msisdn = ").append(msisdn)
        		.append("pin = ").append(pin)
        		.append("loginid = ").append(loginid)
        		.append("password = ").append(password)
        		.append("extcode = ").append(extcode)
        		.append("msisdn2 = ").append(msisdn2)
        		.append("loginid2 = ").append(loginid2)
        		.append("extcode2 = ").append(extcode2)
        		.append("fileType = ").append(fileType)   
                .append("fileName = ").append(fileName)
                .append("fileAttachment = ").append(fileAttachment)
                .append("fileUploaded = ").append(fileUploaded)).toString();
    }
    
}

 class VoucherDetailTrf {

    @JsonProperty("denomination")
    private String denomination;
    @JsonProperty("fromSerialNo")
    private String fromSerialNo;
    @JsonProperty("toSerialNo")
    private String toSerialNo;
    @JsonProperty("voucherType")
    private String voucherType;
    @JsonProperty("vouchersegment")
    private String vouchersegment;
   
    @io.swagger.v3.oas.annotations.media.Schema(example = "102", required = true/* , defaultValue = "" */)
    @JsonProperty("denomination")
    public String getDenomination() {
        return denomination;
    }

    @JsonProperty("denomination")
    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "9999960003002068", required = true/* , defaultValue = "" */)
    @JsonProperty("fromSerialNo")
    public String getFromSerialNo() {
        return fromSerialNo;
    }

    @JsonProperty("fromSerialNo")
    public void setFromSerialNo(String fromSerialNo) {
        this.fromSerialNo = fromSerialNo;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "9999960003002068", required = true/* , defaultValue = "" */)
    @JsonProperty("toSerialNo")
    public String getToSerialNo() {
        return toSerialNo;
    }

    @JsonProperty("toSerialNo")
    public void seToSerialNo(String toSerialNo) {
        this.toSerialNo = toSerialNo;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "physical", required = true/* , defaultValue = "" */)
    @JsonProperty("voucherType")
    public String getVoucherType() {
        return voucherType;
    }

    @JsonProperty("voucherType")
    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "NL", required = true/* , defaultValue = "" */)
    @JsonProperty("vouchersegment")
    public String getVouchersegment() {
        return vouchersegment;
    }

    @JsonProperty("vouchersegment")
    public void setVouchersegment(String vouchersegment) {
        this.vouchersegment = vouchersegment;
    }
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("language1").append(denomination)
        		.append("fromSerialNo = ").append(fromSerialNo)
        		.append("toSerialNo = ").append(toSerialNo)
        		.append("vouchersegment = ").append(vouchersegment)
        		.append("vouchertype = ").append(voucherType)
        		).toString();
    }
}