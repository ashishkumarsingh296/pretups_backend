package com.restapi.o2c.service;

import java.util.List;



/**
 * 
 * @author md.sohail
 *
 */
public class O2CStockAppRequestVO {
	private List<O2CDataStApp> o2cStockAppRequests = null;

	@io.swagger.v3.oas.annotations.media.Schema(required = true, description = "O2C stock approval requestVO")
	public List<O2CDataStApp> getO2cStockAppRequests() {
		return o2cStockAppRequests;
	}
	public void setO2cStockAppRequests(List<O2CDataStApp> o2cStockAppRequests) {
		this.o2cStockAppRequests = o2cStockAppRequests;
	}
	@Override
	public String toString() {
		return "O2CStockAppRequestVO [o2cStockAppRequests=" + o2cStockAppRequests + "]";
	}
	

	
    
}

class O2CDataStApp {
    private String currentStatus;
    private String extNwCode;
    private String status;
    private String txnId;
    private String pin;
    
    private String remarks;
    private String extTxnNumber;
    private String extTxnDate;
    private List<O2CProductAppr> products = null;
    private O2CPaymentdetailAppr paymentDetails = null;
    private String refNumber;
    private String toMsisdn;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "NEW", required = true, description = "Current status")
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = false, description = "External Network Code", hidden = true)
	public String getExtNwCode() {
		return extNwCode;
	}
	public void setExtNwCode(String extNwCode) {
		this.extNwCode = extNwCode;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "approve", required = true, description = "Status: approve/reject")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "OT990811.1923.100001", required = true, description = "Transaction Id")
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true, description = "Pin", hidden = true)
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "Test remarks", required = true, description = "Remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "123456", required = true, description = "External transaction number")
	public String getExtTxnNumber() {
		return extTxnNumber;
	}
	public void setExtTxnNumber(String extTxnNumber) {
		this.extTxnNumber = extTxnNumber;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "30/10/20", required = true, description = "External transaction date")
	public String getExtTxnDate() {
		return extTxnDate;
	}
	public void setExtTxnDate(String extTxnDate) {
		this.extTxnDate = extTxnDate;
	}
	@io.swagger.v3.oas.annotations.media.Schema( required = true, description = "Product details")
	public List<O2CProductAppr> getProducts() {
		return products;
	}
	public void setProducts(List<O2CProductAppr> products) {
		this.products = products;
	}
	@io.swagger.v3.oas.annotations.media.Schema( required = true, description = "Payment details")
	public O2CPaymentdetailAppr getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(O2CPaymentdetailAppr paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "123456789", required = false, description = "Reference Number")
	public String getRefNumber() {
		return refNumber;
	}
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252", required = true, description = "MSISDN of receiver")
	public String getToMsisdn() {
		return toMsisdn;
	}
	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}
	
	@Override
	public String toString() {
		return "O2CDataStApp [currentStatus=" + currentStatus + ", extNwCode=" + extNwCode + ", status=" + status
				+ ", txnId=" + txnId + ", pin=" + pin + ", remarks=" + remarks + ", extTxnNumber=" + extTxnNumber
				+ ", extTxnDate=" + extTxnDate + ", products=" + products + ", paymentDetails=" + paymentDetails
				+ ", refNumber=" + refNumber + ", toMsisdn=" + toMsisdn + "]";
	}
}

class O2CPaymentdetailAppr {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "CASH", required = true, description = "Payment type")
	private String paymentType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "13452343", required = true, description = "Payment instrument number")
	private String paymentInstNumber;
	@io.swagger.v3.oas.annotations.media.Schema(example = "06/01/20", required = true, description = "Payment Date")
	private String paymentDate;
	
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentInstNumber() {
		return paymentInstNumber;
	}
	public void setPaymentInstNumber(String paymentInstNumber) {
		this.paymentInstNumber = paymentInstNumber;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	@Override
	public String toString() {
		return "O2CPaymentdetailAppr [paymentType=" + paymentType + ", paymentInstNumber=" + paymentInstNumber
				+ ", paymentDate=" + paymentDate + "]";
	}
	
	
}
class O2CProductAppr {
	@io.swagger.v3.oas.annotations.media.Schema(example = "ETOPUP", required = true, description= "Product code")
    private String productCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "10", required = true,  description= "Approval quantity")
    private String appQuantity;
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getAppQuantity() {
		return appQuantity;
	}
	public void setAppQuantity(String appQuantity) {
		this.appQuantity = appQuantity;
	}
	
	@Override
	public String toString() {
		return "O2CProductAppr [productCode=" + productCode + ", appQuantity=" + appQuantity + "]";
	}
	
	
}
