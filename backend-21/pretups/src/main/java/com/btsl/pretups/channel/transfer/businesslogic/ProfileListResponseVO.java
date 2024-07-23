package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ProfileListResponseVO extends BaseResponse{

    private List<CommissionProfileSetVO> commissionProfileList = null;
	
	private List<GradeVO> gradeList = null;
	
	private List<ListValueVO> transferProfileList =null;
	
	private List<ListValueVO> transferRuleTypeList =null;
	
	private List<ListValueVO> lMSProfileList =null;

	public List<CommissionProfileSetVO> getCommissionProfileList() {
		return commissionProfileList;
	}

	public void setCommissionProfileList(List<CommissionProfileSetVO> commissionProfileList) {
		this.commissionProfileList = commissionProfileList;
	}

	public List<GradeVO> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<GradeVO> gradeList) {
		this.gradeList = gradeList;
	}

	public List<ListValueVO> getTransferProfileList() {
		return transferProfileList;
	}

	public void setTransferProfileList(List<ListValueVO> transferProfileList) {
		this.transferProfileList = transferProfileList;
	}

	public List<ListValueVO> getlMSProfileList() {
		return lMSProfileList;
	}

	public void setlMSProfileList(List<ListValueVO> lMSProfileList) {
		this.lMSProfileList = lMSProfileList;
	}
	
	public List<ListValueVO> getTransferRuleTypeList() {
		return transferRuleTypeList;
	}

	public void setTransferRuleTypeList(List<ListValueVO> transferRuleTypeList) {
		this.transferRuleTypeList = transferRuleTypeList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProfileListResponseVO [commissionProfileList=").append(commissionProfileList)
				.append(", gradeList=").append(gradeList).append(", transferProfileList=").append(transferProfileList)
				.append(", lMSProfileList=").append(lMSProfileList).append("]");
		return builder.toString();
	}

	

	
}

