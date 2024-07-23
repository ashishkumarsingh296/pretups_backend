package com.restapi.user.service;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
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
public class C2CStockTransferRequestVO {
	@io.swagger.v3.oas.annotations.media.Schema(required=false,hidden=true)
    @JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
	
	@io.swagger.v3.oas.annotations.media.Schema(required=false,hidden=true)
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(required=false,hidden=true)
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(required=false,hidden=true)
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(required=false,hidden=true)
    @JsonProperty("servicePort")
    private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(required=false,hidden=true)
    @JsonProperty("sourceType")
    private String sourceType;
	

    @JsonProperty("data")
    private DataStkTrf data;
    
    
    @JsonProperty("data")
    public DataStkTrf getData() {
		return data;
	}
    @JsonProperty("data")
	public void setData(DataStkTrf data) {
		this.data = data;
	}
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


@Schema(description = "This is a data field")
class DataStkTrf {
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
    @JsonProperty("refnumber")
    private String refnumber;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("loginid2")
    private String loginid2;
    @JsonProperty("extcode2")
    private String extcode2;
    @JsonProperty("paymentdetails")
    private List<PaymentDetails> paymentdetails;
    @JsonProperty("products")
    private List<Products> products = null;
    @JsonProperty("language1")
    private String language1;
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = false/* , defaultValue = "" */)
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }
    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = false/* , defaultValue = "" */)
    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }
    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = false/* , defaultValue = "" */)
    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }
    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = false/* , defaultValue = "" */)
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = false/* , defaultValue = "" */)
    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }
    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("refnumber")
    public String getRefnumber() {
        return refnumber;
    }
    @JsonProperty("refnumber")
    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
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
    @JsonProperty("products")
    public List<Products> getProducts() {
        return products;
    }
    @JsonProperty("products")
    public void setProducts(List<Products> products) {
        this.products = products;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentdetails")
    public List<PaymentDetails> getPaymentDetails() {
        return paymentdetails;
    }
    @JsonProperty("paymentdetails")
    public void setPaymentDetails(List<PaymentDetails> paymentdetails) {
        this.paymentdetails = paymentdetails;
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
    
    @JsonProperty("fileType")
    public String getFileType() {
		return fileType;
	}
    @JsonProperty("fileType")
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	@JsonProperty("fileName")
	public String getFileName() {
		return fileName;
	}
	@JsonProperty("fileName")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@JsonProperty("fileAttachment")
	public String getFileAttachment() {
		return fileAttachment;
	}
	@JsonProperty("fileAttachment")
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	@JsonProperty("fileUploaded")
	public String getFileUploaded() {
		return fileUploaded;
	}
	@JsonProperty("fileUploaded")
	public void setFileUploaded(String fileUploaded) {
		this.fileUploaded = fileUploaded;
	}
	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("language1").append( language1)
        		.append("products = ").append( products)
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
        		.append("paymentdetails = ").append(paymentdetails)
        		.append("fileName = ").append(fileName)
        		.append("fileAttachment = ").append(fileAttachment)
        		.append("fileType = ").append(fileType)
        		.append("fileUploaded = ").append(fileUploaded)
        		).toString();
    }
}

@Schema(description = "This is a data field")
 class Products {
    @JsonProperty("productcode")
    private String productcode;
    @JsonProperty("qty")
    private String qty;
    @io.swagger.v3.oas.annotations.media.Schema(example = "102", required = true/* , defaultValue = "" */)
    @JsonProperty("productcode")
    public String getProductcode() {
        return productcode;
    }
    @JsonProperty("productcode")
    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "23", required = true/* , defaultValue = "" */)
    @JsonProperty("qty")
    public String getQty() {
        return qty;
    }
    @JsonProperty("qty")
    public void setQty(String qty) {
        this.qty = qty;
    }
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("productcode").append(productcode)
        		.append("qty = ").append(qty)
        		).toString();
    }
}

@Schema(description = "This is a data field")
 class PaymentDetails {
	    @JsonProperty("paymenttype")
	    private String paymenttype;
	    @JsonProperty("paymentinstnumber")
	    private String paymentinstnumber;
	    @JsonProperty("paymentdate")
	    private String paymentdate;
	  
	    @io.swagger.v3.oas.annotations.media.Schema(example = "102", required = true/* , defaultValue = "" */)
	    @JsonProperty("paymenttype")
	    public String getPaymenttype() {
	        return paymenttype;
	    }
	    @JsonProperty("paymenttype")
	    public void setPaymenttype(String paymenttype) {
	        this.paymenttype = paymenttype;
	    }
	    @io.swagger.v3.oas.annotations.media.Schema(example = "23/12/20", required = true/* , defaultValue = "" */)
	    @JsonProperty("paymentdate")
	    public String getPaymentdate() {
	        return paymentdate;
	    }
	    @JsonProperty("paymentdate")
	    public void setPaymentdate(String paymentdate) {
	        this.paymentdate = paymentdate;
	    }
	    @io.swagger.v3.oas.annotations.media.Schema(example = "23", required = true/* , defaultValue = "" */)
	    @JsonProperty("paymentinstnumber")
	    public String getPaymentinstnumber() {
	        return paymentinstnumber;
	    }
	    @JsonProperty("paymentinstnumber")
	    public void setPaymentinstnumber(String paymentinstnumber) {
	        this.paymentinstnumber = paymentinstnumber;
	    }
	   
	   
	    @Override
	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	        return (sb.append("paymenttype").append(paymenttype)
	        		.append("paymentinstnumber = ").append(paymentinstnumber)
	        		.append("paymentdate = ").append(paymentdate)
	        		).toString();
	    }
	}