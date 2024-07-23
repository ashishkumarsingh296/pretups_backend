package com.restapi.networkadmin.commissionprofile.requestVO;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;

public class AdditionalProfileCombinedVONew  implements Serializable{
	private AdditionalProfileServicesVO additionalProfileServicesVO;
    private ArrayList<AdditionalProfileDeatilsVO> slabsList;
    
    
	public AdditionalProfileServicesVO getAdditionalProfileServicesVO() {
		return additionalProfileServicesVO;
	}
	public void setAdditionalProfileServicesVO(AdditionalProfileServicesVO additionalProfileServicesVO) {
		this.additionalProfileServicesVO = additionalProfileServicesVO;
	}
	public ArrayList<AdditionalProfileDeatilsVO> getSlabsList() {
		return slabsList;
	}
	public void setSlabsList(ArrayList<AdditionalProfileDeatilsVO> slabsList) {
		this.slabsList = slabsList;
	}
    
    
}
