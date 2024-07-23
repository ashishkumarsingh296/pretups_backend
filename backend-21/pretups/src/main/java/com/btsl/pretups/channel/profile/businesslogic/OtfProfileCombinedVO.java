package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
public class OtfProfileCombinedVO implements Serializable {

    private OtfProfileVO otfProfileVO;
    private ArrayList _slabsList;
    
	public ArrayList getSlabsList() {
        return _slabsList;
    }
    public void setSlabsList(ArrayList otfProfileSlabList) {
        this._slabsList = otfProfileSlabList;
    }
    public OtfProfileVO getOtfProfileVO() {
        return otfProfileVO;
    }
    public void setOtfProfileVO(OtfProfileVO otfProfileVO) {
        this.otfProfileVO = otfProfileVO;
    }
}
