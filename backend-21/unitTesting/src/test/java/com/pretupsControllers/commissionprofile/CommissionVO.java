package com.pretupsControllers.commissionprofile;

import java.util.Arrays;

public class CommissionVO {
	
	private long _senderDebitQty;
	private long _receiverCreditQty;
	private double _productTotalMRP;
	private long _payableAmount;
	private long _netPayableAmount;
	private long _commissionQty;
	
	public long getSenderDebitQty() {
		return _senderDebitQty;
	}
	public void setSenderDebitQty(long _senderDebitQty) {
		this._senderDebitQty = _senderDebitQty;
	}
	public long getReceiverCreditQty() {
		return _receiverCreditQty;
	}
	public void setReceiverCreditQty(long _receiverCreditQty) {
		this._receiverCreditQty = _receiverCreditQty;
	}
	public double getProductTotalMRP() {
		return _productTotalMRP;
	}
	public void setProductTotalMRP(double _productTotalMRP) {
		this._productTotalMRP = _productTotalMRP;
	}
	public long getPayableAmount() {
		return _payableAmount;
	}
	public void setPayableAmount(long _payableAmount) {
		this._payableAmount = _payableAmount;
	}
	public long getNetPayableAmount() {
		return _netPayableAmount;
	}
	public void setNetPayableAmount(long _netPayableAmount) {
		this._netPayableAmount = _netPayableAmount;
	}
	public long getCommissionQty() {
		return _commissionQty;
	}
	public void setCommissionQty(long _commissionQty) {
		this._commissionQty = _commissionQty;
	}
	
	public String toString() {
		final StringBuilder sbd = new StringBuilder("CommissionVO ");
        sbd.append("senderDebitQty=").append(_senderDebitQty).append(",");
        sbd.append("receiverCreditQty=").append(_receiverCreditQty).append(",");
        sbd.append("productTotalMRP=").append(Arrays.asList(_productTotalMRP)).append(",");
        sbd.append("payableAmount=").append(_payableAmount).append(",");
        sbd.append("netPayableAmount=").append(_netPayableAmount).append(",");
        sbd.append("commissionQty=").append(_commissionQty).append(",");
        return sbd.toString();
	}
}
