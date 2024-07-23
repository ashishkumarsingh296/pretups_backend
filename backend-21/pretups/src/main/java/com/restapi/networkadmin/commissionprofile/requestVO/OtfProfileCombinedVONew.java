package com.restapi.networkadmin.commissionprofile.requestVO;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;

public class OtfProfileCombinedVONew implements Serializable {
	private OtfProfileVO otfProfileVO;
    private ArrayList<OTFDetailsVO> slabsList;
    
    
	public OtfProfileVO getOtfProfileVO() {
		return otfProfileVO;
	}
	public void setOtfProfileVO(OtfProfileVO otfProfileVO) {
		this.otfProfileVO = otfProfileVO;
	}
	public ArrayList<OTFDetailsVO> getSlabsList() {
		return slabsList;
	}
	public void setSlabsList(ArrayList<OTFDetailsVO> slabsList) {
		this.slabsList = slabsList;
	}
	
	
}
