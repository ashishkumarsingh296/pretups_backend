package com.btsl.pretups.channel.profile.businesslogic;

import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.user.businesslogic.UserVO;

public class ProfileThresholdResponseVO {
	
	private UserTransferCountsVO userTransferCountsVO;
	private TransferProfileVO transferProfileVO;
	public TransferProfileVO getTransferProfileVO() {
		return transferProfileVO;
	}

	public void setTransferProfileVO(TransferProfileVO transferProfileVO) {
		this.transferProfileVO = transferProfileVO;
	}
	
	private boolean subscriberOutCountFlag;

	private UserVO userVO;
	
	private boolean unctrlTransferFlag;

	
	public UserTransferCountsVO getUserTransferCountsVO() {
		return userTransferCountsVO;
	}

	public void setUserTransferCountsVO(UserTransferCountsVO userTransferCountsVO) {
		this.userTransferCountsVO = userTransferCountsVO;
	}

	public UserVO getUserVO() {
		return userVO;
	}

	public void setUserVO(UserVO userVO) {
		this.userVO = userVO;
	}

	public boolean isUnctrlTransferFlag() {
		return unctrlTransferFlag;
	}

	public void setUnctrlTransferFlag(boolean unctrlTransferFlag) {
		this.unctrlTransferFlag = unctrlTransferFlag;
	}

	public boolean getSubscriberOutCountFlag() {
		return subscriberOutCountFlag;
	}

	public void setSubscriberOutCountFlag(boolean subscriberOutCountFlag) {
		this.subscriberOutCountFlag = subscriberOutCountFlag;
	}

	
}
