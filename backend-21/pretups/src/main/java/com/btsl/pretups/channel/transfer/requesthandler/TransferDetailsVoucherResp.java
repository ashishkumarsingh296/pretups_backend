package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class TransferDetailsVoucherResp extends BaseResponse {
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalMRP")
	 private String totalMRP;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalNetPayableAmount")
	 private String totalNetPayableAmount;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalPayableAmount")
	 private String totalPayableAmount;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalReqQty")
	 private String totalReqQty;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalTransferedAmount")
	 private String totalTransferedAmount;
	 @JsonInclude(Include.NON_NULL)
	 @JsonProperty("totalOtfValue")
	 private String totalOtfValue;
	 
	 @JsonProperty("totalOtfValue")
	public String getTotalOtfValue() {
		return totalOtfValue;
	}
	 @JsonProperty("totalOtfValue")
	public void setTotalOtfValue(String totalOtfValue) {
		this.totalOtfValue = totalOtfValue;
	}
	@JsonProperty("tansferProductdetailList")
	public ArrayList<ChannelVoucherTransferDetails> getTansferProductdetailList() {
		return tansferProductdetailList;
	}
	@JsonProperty("tansferProductdetailList")
	public void setTansferProductdetailList(ArrayList<ChannelVoucherTransferDetails> tansferProductdetailList) {
		this.tansferProductdetailList = tansferProductdetailList;
	}
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalTax1")
	 private String totalTax1;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalTax2")
	 private String totalTax2;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalTax3")
	 private String totalTax3;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("totalComm")
	 private String totalComm;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("commissionQuantity")
	 private String commissionQuantity;
	 
	 @JsonInclude(Include.NON_NULL)
		@JsonProperty("totalVoucherOrderQuantity")
		 private String totalVoucherOrderQuantity;
	 
	 @JsonInclude(Include.NON_NULL)
		@JsonProperty("totalVoucherOrderAmount")
		 private String totalVoucherOrderAmount;
	 
		@JsonProperty("totalVoucherOrderQuantity")
	 public String getTotalVoucherOrderQuantity() {
		return totalVoucherOrderQuantity;
	}
		@JsonProperty("totalVoucherOrderQuantity")
	public void setTotalVoucherOrderQuantity(String totalVoucherOrderQuantity) {
		this.totalVoucherOrderQuantity = totalVoucherOrderQuantity;
	}
		@JsonProperty("totalVoucherOrderAmount")
	public String getTotalVoucherOrderAmount() {
		return totalVoucherOrderAmount;
	}
		@JsonProperty("totalVoucherOrderAmount")
	public void setTotalVoucherOrderAmount(String totalVoucherOrderAmount) {
		this.totalVoucherOrderAmount = totalVoucherOrderAmount;
	}
	@JsonInclude(Include.NON_NULL)
    @JsonProperty("tansferProductdetailList")
	  private ArrayList<ChannelVoucherTransferDetails> tansferProductdetailList = null;
	 @JsonInclude(Include.NON_NULL)
	 @JsonProperty("slabDetails")
		private ArrayList<VoucherSlabListDetails> slabDetails = null;
	@JsonProperty("totalMRP")
	public String getTotalMRP() {
		return totalMRP;
	}
	public ArrayList<VoucherSlabListDetails> getSlabDetails() {
		return slabDetails;
	}
	public void setSlabDetails(ArrayList<VoucherSlabListDetails> slabDetails) {
		this.slabDetails = slabDetails;
	}
	@JsonProperty("totalMRP")
	public void setTotalMRP(String totalMRP) {
		this.totalMRP = totalMRP;
	}
	@JsonProperty("totalNetPayableAmount")
	public String getTotalNetPayableAmount() {
		return totalNetPayableAmount;
	}
	@JsonProperty("totalNetPayableAmount")
	public void setTotalNetPayableAmount(String totalNetPayableAmount) {
		this.totalNetPayableAmount = totalNetPayableAmount;
	}
	@JsonProperty("totalPayableAmount")
	public String getTotalPayableAmount() {
		return totalPayableAmount;
	}
	@JsonProperty("totalPayableAmount")
	public void setTotalPayableAmount(String totalPayableAmount) {
		this.totalPayableAmount = totalPayableAmount;
	}
	@JsonProperty("totalReqQty")
	public String getTotalReqQty() {
		return totalReqQty;
	}
	@JsonProperty("totalReqQty")
	public void setTotalReqQty(String totalReqQty) {
		this.totalReqQty = totalReqQty;
	}
	@JsonProperty("totalTransferedAmount")
	public String getTotalTransferedAmount() {
		return totalTransferedAmount;
	}
	@JsonProperty("totalTransferedAmount")
	public void setTotalTransferedAmount(String totalTransferedAmount) {
		this.totalTransferedAmount = totalTransferedAmount;
	}
	@JsonProperty("totalTax1")
	public String getTotalTax1() {
		return totalTax1;
	}
	@JsonProperty("totalTax1")
	public void setTotalTax1(String totalTax1) {
		this.totalTax1 = totalTax1;
	}
	@JsonProperty("totalTax2")
	public String getTotalTax2() {
		return totalTax2;
	}
	@JsonProperty("totalTax2")
	public void setTotalTax2(String totalTax2) {
		this.totalTax2 = totalTax2;
	}
	@JsonProperty("totalTax3")
	public String getTotalTax3() {
		return totalTax3;
	}
	@JsonProperty("totalTax3")
	public void setTotalTax3(String totalTax3) {
		this.totalTax3 = totalTax3;
	}
	@JsonProperty("totalComm")
	public String getTotalComm() {
		return totalComm;
	}
	@JsonProperty("totalComm")
	public void setTotalComm(String totalComm) {
		this.totalComm = totalComm;
	}
	@JsonProperty("commissionQuantity")
	public String getCommissionQuantity() {
		return commissionQuantity;
	}
	@JsonProperty("commissionQuantity")
	public void setCommissionQuantity(String commissionQuantity) {
		this.commissionQuantity = commissionQuantity;
	}
	@JsonProperty("senderDrQty")
	public String getSenderDrQty() {
		return senderDrQty;
	}
	@JsonProperty("senderDrQty")
	public void setSenderDrQty(String senderDrQty) {
		this.senderDrQty = senderDrQty;
	}
	@JsonProperty("receiverCrQty")
	public String getReceiverCrQty() {
		return receiverCrQty;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransferDetailsVoucherResp [totalMRP=");
		builder.append(totalMRP);
		builder.append(", totalNetPayableAmount=");
		builder.append(totalNetPayableAmount);
		builder.append(", totalPayableAmount=");
		builder.append(totalPayableAmount);
		builder.append(", totalReqQty=");
		builder.append(totalReqQty);
		builder.append(", totalTransferedAmount=");
		builder.append(totalTransferedAmount);
		builder.append(", totalTax1=");
		builder.append(totalTax1);
		builder.append(", totalTax2=");
		builder.append(totalTax2);
		builder.append(", totalTax3=");
		builder.append(totalTax3);
		builder.append(", totalComm=");
		builder.append(totalComm);
		builder.append(", commissionQuantity=");
		builder.append(commissionQuantity);
		builder.append(", totalVoucherOrderQuantity=");
		builder.append(totalVoucherOrderQuantity);
		builder.append(", totalVoucherOrderAmount=");
		builder.append(totalVoucherOrderAmount);
		builder.append(", tansferProductdetailList=");
		builder.append(tansferProductdetailList);
		builder.append(", slabDetails=");
		builder.append(slabDetails);
		builder.append(", senderDrQty=");
		builder.append(senderDrQty);
		builder.append(", receiverCrQty=");
		builder.append(receiverCrQty);
		builder.append(", totalOtfValue=");
		builder.append(totalOtfValue);
		builder.append("]");
		return builder.toString();
	}
	@JsonProperty("receiverCrQty")
	public void setReceiverCrQty(String receiverCrQty) {
		this.receiverCrQty = receiverCrQty;
	}
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("senderDrQty")
	 private String senderDrQty;
	 @JsonInclude(Include.NON_NULL)
	@JsonProperty("receiverCrQty")
	 private String receiverCrQty;
	

}
