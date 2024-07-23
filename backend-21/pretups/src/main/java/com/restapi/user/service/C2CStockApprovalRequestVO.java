package com.restapi.user.service;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "reqGatewayLoginId",
    "data",
    "sourceType",
    "reqGatewayType",
    "reqGatewayPassword",
    "servicePort",
    "reqGatewayCode"
})

public class C2CStockApprovalRequestVO {
	C2CStockApprovalRequestVO(){
		DataStApp data = new DataStApp();
		this.setData(data);
		
	}
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
	@JsonProperty("reqGatewayLoginId")
    private String reqGatewayLoginId;
	
    @JsonProperty("data")
    private DataStApp data;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("sourceType")
    private String sourceType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayType")
    private String reqGatewayType;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayPassword")
    private String reqGatewayPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("servicePort")
    private String servicePort;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("reqGatewayCode")
    private String reqGatewayCode;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayLoginId")
    public String getReqGatewayLoginId() {
        return reqGatewayLoginId;
    }

    @JsonProperty("reqGatewayLoginId")
    public void setReqGatewayLoginId(String reqGatewayLoginId) {
        this.reqGatewayLoginId = reqGatewayLoginId;
    }

    @JsonProperty("data")
    public DataStApp getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(DataStApp data) {
        this.data = data;
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = false/* , defaultValue = "" */)
    @JsonProperty("reqGatewayType")
    public String getReqGatewayType() {
        return reqGatewayType;
    }

    @JsonProperty("reqGatewayType")
    public void setReqGatewayType(String reqGatewayType) {
        this.reqGatewayType = reqGatewayType;
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "190", required = false/* , defaultValue = "" */)
    @JsonProperty("servicePort")
    public String getServicePort() {
        return servicePort;
    }

    @JsonProperty("servicePort")
    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
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

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("reqGatewayLoginId = ").append(reqGatewayLoginId)
        		.append("data = ").append( data).append("sourceType").append( sourceType)
        		.append("reqGatewayType = ").append( reqGatewayType)
        		.append("reqGatewayPassword = ").append(reqGatewayPassword)
        		.append("servicePort = ").append(servicePort)
        		.append("reqGatewayCode = ").append(reqGatewayCode)).toString();
    }

	

}
class DataStApp {

	@JsonProperty("date")
    private String date;
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
    @JsonProperty("txnid")
    private String txnid;
    @JsonProperty("products")
    private List<ProductAppr> products = null;
    @JsonProperty("refnumber")
    private String refnumber;
    @JsonProperty("paymentdetails")
    private List<PaymentdetailAppr> paymentdetails = null;
    @JsonProperty("currentstatus")
    private String currentstatus;
    @JsonProperty("status")
    private String status;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("language1")
    private String language1;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "24/12/19", required = true/* , defaultValue = "" */)
    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "CT200106.1214.700001", required = true/* , defaultValue = "" */)
    @JsonProperty("txnid")
    public String getTxnid() {
        return txnid;
    }

    @JsonProperty("txnid")
    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("products")
    public List<ProductAppr> getProducts() {
        return products;
    }

    @JsonProperty("products")
    public void setProducts(List<ProductAppr> products) {
        this.products = products;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "9995120000000134", required = true/* , defaultValue = "" */)
    @JsonProperty("refnumber")
    public String getRefnumber() {
        return refnumber;
    }

    @JsonProperty("refnumber")
    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentdetails")
    public List<PaymentdetailAppr> getPaymentdetails() {
        return paymentdetails;
    }

    @JsonProperty("paymentdetails")
    public void setPaymentdetails(List<PaymentdetailAppr> paymentdetails) {
        this.paymentdetails = paymentdetails;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "NEW", required = true/* , defaultValue = "" */)
    @JsonProperty("currentstatus")
    public String getCurrentstatus() {
        return currentstatus;
    }

    @JsonProperty("currentstatus")
    public void setCurrentstatus(String currentstatus) {
        this.currentstatus = currentstatus;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */)
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "Test Data", required = true/* , defaultValue = "" */)
    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "0", required = true/* , defaultValue = "" */)
    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("txnid = ").append(txnid)
        		.append("products = ").append( products)
        		.append("language1 = ").append( language1)
        		.append("refnumber = ").append(refnumber)
        		.append("paymentdetails = ").append( paymentdetails)
        		.append("currentstatus = ").append( currentstatus)
        		.append("remarks = ").append( remarks)
        		.append("status = ").append(status)
        		.append("extnwcode = ").append(extnwcode)
        		.append("msisdn = ").append(msisdn)
        		.append("pin = ").append(pin)
        		.append("loginid = ").append(loginid)
        		.append("password = ").append(password)
        		.append("extcode = ").append(extcode)).toString();
    }
    
}

class PaymentdetailAppr {
	
	@JsonProperty("paymenttype")
	private String paymenttype;
	@JsonProperty("paymentinstnumber")
	private String paymentinstnumber;
	@JsonProperty("paymentdate")
	private String paymentdate;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "CASH", required = true/* , defaultValue = "" */)
	@JsonProperty("paymenttype")
	public String getPaymenttype() {
	    return paymenttype;
	}
	
	@JsonProperty("paymenttype")
	public void setPaymenttype(String paymenttype) {
	    this.paymenttype = paymenttype;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "13452343", required = true/* , defaultValue = "" */)
	@JsonProperty("paymentinstnumber")
	public String getPaymentinstnumber() {
	    return paymentinstnumber;
	}
	
	@JsonProperty("paymentinstnumber")
	public void setPaymentinstnumber(String paymentinstnumber) {
	    this.paymentinstnumber = paymentinstnumber;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "06/01/20", required = true/* , defaultValue = "" */)
	@JsonProperty("paymentdate")
	public String getPaymentdate() {
	    return paymentdate;
	}
	
	@JsonProperty("paymentdate")
	public void setPaymentdate(String paymentdate) {
	    this.paymentdate = paymentdate;
	}
	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("paymenttype = ").append(paymenttype)
        		.append("paymentdate = ").append( paymentdate)
        		.append("paymentinstnumber = ").append( paymentinstnumber)).toString();
    }
}
class ProductAppr {
	
	@JsonProperty("productcode")
    private String productcode;
    @JsonProperty("qty")
    private String qty;
    @io.swagger.v3.oas.annotations.media.Schema(example = "101", required = true/* , defaultValue = "" */)
    @JsonProperty("productcode")
    public String getProductcode() {
        return productcode;
    }

    @JsonProperty("productcode")
    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2", required = true/* , defaultValue = "" */)
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
        return (sb.append("productcode = ").append(productcode)
        		.append("qty = ").append( qty)).toString();
    }
}