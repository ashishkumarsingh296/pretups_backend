package com.btsl.pretups.channel.transfer.requesthandler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VoucherSlabListDetails {
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoucherSlabListDetails [salbAmount=");
		builder.append(salbAmount);
		builder.append(", slabQty=");
		builder.append(slabQty);
		builder.append("]");
		return builder.toString();
	}
	@JsonProperty("salbAmount")
	private String salbAmount;
	@JsonProperty("slabQty")
	private String slabQty;
	
	
	@JsonProperty("salbAmount")
	public String getSalbAmount() {
		return salbAmount;
	}
	@JsonProperty("salbAmount")
	public void setSalbAmount(String salbAmount) {
		this.salbAmount = salbAmount;
	}
	@JsonProperty("slabQty")
	public String getSlabQty() {
		return slabQty;
	}
	@JsonProperty("slabQty")
	public void setSlabQty(String slabQty) {
		this.slabQty = slabQty;
	}

}
