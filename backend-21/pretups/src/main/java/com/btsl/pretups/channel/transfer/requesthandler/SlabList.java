package com.btsl.pretups.channel.transfer.requesthandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



public class SlabList {

	@JsonInclude(Include.NON_NULL)
	private String voucherType;

	@JsonInclude(Include.NON_NULL)
	private String segmentType;
	
	@JsonInclude(Include.NON_NULL)
	private String voucherMrp;

	@JsonInclude(Include.NON_NULL)
	private String  toSerialNo;	

	@JsonInclude(Include.NON_NULL)
	private String  qty;
	
	@JsonInclude(Include.NON_NULL)
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "100", required= true, description= "Voucher Mrp")
	public String getVoucherMrp() {
		return voucherMrp;
	}

	@JsonProperty("voucherMrp")
	public void setVoucherMrp(String voucherMrp) {
		this.voucherMrp = voucherMrp;
	}

	@JsonProperty("fromSerialNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "9900690000000007", required= true, description= "Voucher From Serial no")
	public String getFromSerialNo() {
		return fromSerialNo;
	}

	@JsonProperty("fromSerialNo")
	public void setFromSerialNo(String fromSerialNo) {
		this.fromSerialNo = fromSerialNo;
	}

	@JsonProperty("toSerialNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "9900690000000007", required= true, description= "Voucher To Serail no")
	public String getToSerialNo() {
		return toSerialNo;
	}

	@JsonProperty("toSerialNo")
	public void setToSerialNo(String toSerialNo) {
		this.toSerialNo = toSerialNo;
	}

	@JsonProperty("qty")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required= true, description= "Voucher quantity")
	public String getQty() {
		return qty;
	}

	@JsonProperty("qty")
	public void setQty(String qty) {
		this.qty = qty;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "digital", required= true, description= "Voucher Type")
	@JsonProperty("voucherType")
	public String getVoucherType() {
		return voucherType;
	}

	@JsonProperty("voucherType")
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	
	@JsonProperty("segmentType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NL", required= true, description= "Segment Type")
	public String getSegmentType() {
		return segmentType;
	}

	@JsonProperty("segmentType")
	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
	}
	
	

}
