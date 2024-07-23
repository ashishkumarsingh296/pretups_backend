package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalcommSlabVO;
import com.btsl.pretups.channel.profile.businesslogic.CBCcommSlabDetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionSlabDetVO;

/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class O2CTransAckVoucherDetail  {
	private String voucherBatchNumber;
	private String vomsProductName;
	private String batchType;
	private String totalNoofVouchers;
	private String fromSerialNumber;
	private String toSerialNumber;
	
	public String getVoucherBatchNumber() {
		return voucherBatchNumber;
	}
	public void setVoucherBatchNumber(String voucherBatchNumber) {
		this.voucherBatchNumber = voucherBatchNumber;
	}
	public String getVomsProductName() {
		return vomsProductName;
	}
	public void setVomsProductName(String vomsProductName) {
		this.vomsProductName = vomsProductName;
	}
	public String getBatchType() {
		return batchType;
	}
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	public String getTotalNoofVouchers() {
		return totalNoofVouchers;
	}
	public void setTotalNoofVouchers(String totalNoofVouchers) {
		this.totalNoofVouchers = totalNoofVouchers;
	}
	public String getFromSerialNumber() {
		return fromSerialNumber;
	}
	public void setFromSerialNumber(String fromSerialNumber) {
		this.fromSerialNumber = fromSerialNumber;
	}
	public String getToSerialNumber() {
		return toSerialNumber;
	}
	public void setToSerialNumber(String toSerialNumber) {
		this.toSerialNumber = toSerialNumber;
	}

	
}
