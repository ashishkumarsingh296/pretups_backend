package com.btsl.pretups.channel.transfer.businesslogic;

public class PaymentDetails {
	private String paymenttype;
	private String paymentinstnumber;
	private String paymentdate;
	public String getPaymenttype() {
		return paymenttype;
	}

	public void setPaymenttype(String paymenttype) {
		this.paymenttype = paymenttype;
	}

	public String getPaymentinstnumber() {
		return paymentinstnumber;
	}

	public void setPaymentinstnumber(String paymentinstnumber) {
		this.paymentinstnumber = paymentinstnumber;
	}

	public String getPaymentdate() {
		return paymentdate;
	}

	public void setPaymentdate(String paymentdate) {
		this.paymentdate = paymentdate;
	}
	
	@Override
	public String toString() {
		StringBuilder strBuff = new StringBuilder("PaymentDetails [");
		strBuff.append(" paymentType=" + paymenttype);
		strBuff.append(", paymentInstNumber=" + paymentinstnumber);
		strBuff.append(", paymentDate=" + paymentdate);
		strBuff.append(" ]");
		return strBuff.toString();
	}
}
