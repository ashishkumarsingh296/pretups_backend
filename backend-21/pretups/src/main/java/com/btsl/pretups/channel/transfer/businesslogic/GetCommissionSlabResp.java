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
public class GetCommissionSlabResp extends BaseResponseMultiple {

	private String commissionType;
	private String applicableFrom;
	private List<CommissionSlabDetVO> listcommissionSlabDetVO;
	private List<CBCcommSlabDetVO> listcBCcommSlabDetVO;
	private List<AdditionalcommSlabVO> listAdditionalCommSlabVO;
	

		
	public String getCommissionType() {
		return commissionType;
	}


	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}


	public String getApplicableFrom() {
		return applicableFrom;
	}


	public void setApplicableFrom(String applicableFrom) {
		this.applicableFrom = applicableFrom;
	}


	public List<CommissionSlabDetVO> getListcommissionSlabDetVO() {
		return listcommissionSlabDetVO;
	}


	public void setListcommissionSlabDetVO(List<CommissionSlabDetVO> listcommissionSlabDetVO) {
		this.listcommissionSlabDetVO = listcommissionSlabDetVO;
	}


	public List<CBCcommSlabDetVO> getListcBCcommSlabDetVO() {
		return listcBCcommSlabDetVO;
	}


	public void setListcBCcommSlabDetVO(List<CBCcommSlabDetVO> listcBCcommSlabDetVO) {
		this.listcBCcommSlabDetVO = listcBCcommSlabDetVO;
	}


	public List<AdditionalcommSlabVO> getListAdditionalCommSlabVO() {
		return listAdditionalCommSlabVO;
	}


	public void setListAdditionalCommSlabVO(List<AdditionalcommSlabVO> listAdditionalCommSlabVO) {
		this.listAdditionalCommSlabVO = listAdditionalCommSlabVO;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" GetCommissionSlabResp : [ ParentName :")

				.append("]");
		return sb.toString();
	}

}
