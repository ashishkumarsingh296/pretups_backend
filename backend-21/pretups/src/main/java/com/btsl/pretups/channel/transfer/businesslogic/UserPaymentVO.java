package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

public class UserPaymentVO {

	private String paymentCode;
	private String paymentType;
	private List paymentGatewayList;
	public List getPaymentGatewayList() {
		return paymentGatewayList;
	}
	public void setPaymentGatewayList(List paymentGatewayList) {
		this.paymentGatewayList = paymentGatewayList;
	}
	public String getPaymentCode() {
		return paymentCode;
	}
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("paymentCode = ");
		sb.append(paymentCode);
		sb.append("paymentType = ");
		sb.append(paymentType);
		return sb.toString();
	}
	
	public class ListValue
	{
		private String codeName;
		public String getCodeName() {
			return codeName;
		}
		public void setCodeName(String codeName) {
			this.codeName = codeName;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		private String value;
	}
}
