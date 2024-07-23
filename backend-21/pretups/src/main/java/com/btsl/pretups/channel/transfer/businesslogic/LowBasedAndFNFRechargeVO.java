package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;

public class LowBasedAndFNFRechargeVO implements Serializable {
	private long minTransferValue=0;
	private long maxTrnasferValue=0;
	private long count=0;
	private long amount=0;
	private boolean isExists=false;
	public long getMinTransferValue() {
		return minTransferValue;
	}
	public void setMinTransferValue(long transferValue) {
		minTransferValue = transferValue;
	}
	public long getMaxTrnasferValue() {
		return maxTrnasferValue;
	}
	public void setMaxTrnasferValue(long trnasferValue) {
		maxTrnasferValue = trnasferValue;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		
		this.amount = amount;
	}
	public boolean isIsExists() {
		return isExists;
	}
	public void setIsExists(boolean exists) {
		isExists = exists;
	}
	

}
