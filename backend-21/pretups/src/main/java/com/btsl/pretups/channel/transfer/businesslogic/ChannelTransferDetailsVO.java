package com.btsl.pretups.channel.transfer.businesslogic;

public class ChannelTransferDetailsVO {


	
	public long getRequestedQty() {
		return requestedQty;
	}
	public void setRequestedQty(long requestedQty) {
		this.requestedQty = requestedQty;
	}
	public String getCommType() {
		return commType;
	}
	public void setCommType(String commType) {
		this.commType = commType;
	}
	public double getCommRate() {
		return commRate;
	}
	public void setCommRate(double commRate) {
		this.commRate = commRate;
	}
	public long getCommValue() {
		return commValue;
	}
	public void setCommValue(long commValue) {
		this.commValue = commValue;
	}
	public String getTax1Type() {
		return tax1Type;
	}
	public void setTax1Type(String tax1Type) {
		this.tax1Type = tax1Type;
	}
	public double getTax1Rate() {
		return tax1Rate;
	}
	public void setTax1Rate(double tax1Rate) {
		this.tax1Rate = tax1Rate;
	}
	public long getTax1Value() {
		return tax1Value;
	}
	public void setTax1Value(long tax1Value) {
		this.tax1Value = tax1Value;
	}
	public String getTax2Type() {
		return tax2Type;
	}
	public void setTax2Type(String tax2Type) {
		this.tax2Type = tax2Type;
	}
	public double getTax2Rate() {
		return tax2Rate;
	}
	public void setTax2Rate(double tax2Rate) {
		this.tax2Rate = tax2Rate;
	}
	public long getTax2Value() {
		return tax2Value;
	}
	public void setTax2Value(long tax2Value) {
		this.tax2Value = tax2Value;
	}
	public String getTax3Type() {
		return tax3Type;
	}
	public void setTax3Type(String tax3Type) {
		this.tax3Type = tax3Type;
	}
	public double getTax3Rate() {
		return tax3Rate;
	}
	public void setTax3Rate(double tax3Rate) {
		this.tax3Rate = tax3Rate;
	}
	public long getTax3Value() {
		return tax3Value;
	}
	public void setTax3Value(long tax3Value) {
		this.tax3Value = tax3Value;
	}
	public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}
	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		this.otfTypePctOrAMt = otfTypePctOrAMt;
	}
	
	public long getPayableAmount() {
		return payableAmount;
	}
	public void setPayableAmount(long payableAmount) {
		this.payableAmount = payableAmount;
	}
	public long getNetPayableAmount() {
		return netPayableAmount;
	}
	public void setNetPayableAmount(long netPayableAmount) {
		this.netPayableAmount = netPayableAmount;
	}
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public long getSenderDebitQty() {
		return senderDebitQty;
	}
	public void setSenderDebitQty(long senderDebitQty) {
		this.senderDebitQty = senderDebitQty;
	}
	public long getReceiverCreditQty() {
		return receiverCreditQty;
	}
	public void setReceiverCreditQty(long receiverCreditQty) {
		this.receiverCreditQty = receiverCreditQty;
	}
	private String commType;
    private double commRate;
    private long commValue;
    private String commValueDisplay;
    private String tax1Type;
    private double tax1Rate;
    private long tax1Value;
    private String tax1ValueStr;
    private String tax2Type;
    private double tax2Rate;
    private long tax2Value;
    private String tax2DisplayValue;
    private String tax3Type;
    private double tax3Rate;
    private long tax3Value;
    private String tax3DisplayValue;
    
	private String otfTypePctOrAMt;
    private double cbcRate;
    
    
    public String getTax3DisplayValue() {
		return tax3DisplayValue;
	}
	public void setTax3DisplayValue(String tax3DisplayValue) {
		this.tax3DisplayValue = tax3DisplayValue;
	}
    
    
    public double getCbcRate() {
		return cbcRate;
	}
	public void setCbcRate(double cbcRate) {
		this.cbcRate = cbcRate;
	}
	private long cbcAmount;
	private String  cbcAmountDisplayValue;
	
    public long getCbcAmount() {
		return cbcAmount;
	}
	public void setCbcAmount(long cbcAmount) {
		this.cbcAmount = cbcAmount;
	}
	private long payableAmount;
	private String payableAmountDisplay;
    private long netPayableAmount;
    private String netPayableAmountDisplay;
    private String productCode;
    private String productName;
    private String commQuantityAsString;
    public String getCommQuantityAsString() {
		return commQuantityAsString;
	}
	public void setCommQuantityAsString(String commQuantityAsString) {
		this.commQuantityAsString = commQuantityAsString;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	private long senderDebitQty = 0;
	private String senderDebitQtyDisplay;
    private long receiverCreditQty = 0;
	private String receiverCreditQtyDisplay;
    private long requestedQty;
	public String getTax1ValueStr() {
		return tax1ValueStr;
	}
	public void setTax1ValueStr(String tax1ValueStr) {
		this.tax1ValueStr = tax1ValueStr;
	}
	public String getTax2DisplayValue() {
		return tax2DisplayValue;
	}
	public void setTax2DisplayValue(String tax2DisplayValue) {
		this.tax2DisplayValue = tax2DisplayValue;
	}
	public String getCommValueDisplay() {
		return commValueDisplay;
	}
	public void setCommValueDisplay(String commValueDisplay) {
		this.commValueDisplay = commValueDisplay;
	}
	public String getCbcAmountDisplayValue() {
		return cbcAmountDisplayValue;
	}
	public void setCbcAmountDisplayValue(String cbcAmountDisplayValue) {
		this.cbcAmountDisplayValue = cbcAmountDisplayValue;
	}
	public String getPayableAmountDisplay() {
		return payableAmountDisplay;
	}
	public void setPayableAmountDisplay(String payableAmountDisplay) {
		this.payableAmountDisplay = payableAmountDisplay;
	}
	public String getNetPayableAmountDisplay() {
		return netPayableAmountDisplay;
	}
	public void setNetPayableAmountDisplay(String netPayableAmountDisplay) {
		this.netPayableAmountDisplay = netPayableAmountDisplay;
	}
	public String getSenderDebitQtyDisplay() {
		return senderDebitQtyDisplay;
	}
	public void setSenderDebitQtyDisplay(String senderDebitQtyDisplay) {
		this.senderDebitQtyDisplay = senderDebitQtyDisplay;
	}
	public String getReceiverCreditQtyDisplay() {
		return receiverCreditQtyDisplay;
	}
	public void setReceiverCreditQtyDisplay(String receiverCreditQtyDisplay) {
		this.receiverCreditQtyDisplay = receiverCreditQtyDisplay;
	}
	
}
