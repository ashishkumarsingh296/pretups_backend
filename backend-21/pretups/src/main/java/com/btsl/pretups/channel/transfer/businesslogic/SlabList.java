package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;



@Schema(description = "This is a Slab list")
public class SlabList {
	
	
	@JsonProperty("voucherType")
	private String voucherType;

	@JsonProperty("segmentType")
	private String segmentType;
	
	@JsonProperty("voucherMrp")
	private String voucherMrp;

	@JsonProperty("toSerialNo")
	private String  toSerialNo;	

	@JsonProperty("qty")
	private String  qty;
	
	@JsonProperty("fromSerialNo")
	private String fromSerialNo;	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SlabList [voucherType=");
		builder.append(voucherType);
		builder.append(", segmentType=");
		builder.append(segmentType);
		builder.append(", voucherMrp=");
		builder.append(voucherMrp);
		builder.append(", toSerialNo=");
		builder.append(toSerialNo);
		builder.append(", qty=");
		builder.append(qty);
		builder.append(", fromSerialNo=");
		builder.append(fromSerialNo);
		builder.append("]");
		return builder.toString();
	}



	
	@JsonProperty("voucherMrp")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Voucher Mrp")
	public String getVoucherMrp() {
		return voucherMrp;
	}


	public void setVoucherMrp(String voucherMrp) {
		this.voucherMrp = voucherMrp;
	}

	@JsonProperty("fromSerialNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Voucher From Serial no")
	public String getFromSerialNo() {
		return fromSerialNo;
	}


	public void setFromSerialNo(String fromSerialNo) {
		this.fromSerialNo = fromSerialNo;
	}

	@JsonProperty("toSerialNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Voucher To Serail no")
	public String getToSerialNo() {
		return toSerialNo;
	}


	public void setToSerialNo(String toSerialNo) {
		this.toSerialNo = toSerialNo;
	}

	@JsonProperty("qty")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Voucher quantity")
	public String getQty() {
		return qty;
	}


	public void setQty(String qty) {
		this.qty = qty;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Voucher Type")
	@JsonProperty("voucherType")
	public String getVoucherType() {
		return voucherType;
	}

	
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	
	@JsonProperty("segmentType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required= true, description= "Segment Type")
	public String getSegmentType() {
		return segmentType;
	}


	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
	}
	
	


}
