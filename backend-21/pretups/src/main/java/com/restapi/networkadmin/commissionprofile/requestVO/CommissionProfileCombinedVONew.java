package com.restapi.networkadmin.commissionprofile.requestVO;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;


public class CommissionProfileCombinedVONew implements Serializable {

	
	private CommissionProfileProductsVO commissionProfileProductVO;
    private ArrayList<CommissionProfileDeatilsVO> slabsList;// contains VO's of CommissionProfileDeatilsVO
    
    
	public CommissionProfileProductsVO getCommissionProfileProductVO() {
		return commissionProfileProductVO;
	}
	public void setCommissionProfileProductVO(CommissionProfileProductsVO commissionProfileProductVO) {
		this.commissionProfileProductVO = commissionProfileProductVO;
	}
	public ArrayList<CommissionProfileDeatilsVO> getSlabsList() {
		return slabsList;
	}
	public void setSlabsList(ArrayList<CommissionProfileDeatilsVO> slabsList) {
		this.slabsList = slabsList;
	}

   
}
