package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @(#)AdditionalProfileCombinedVO.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Mohit Goel 24/08/2005 Initial Creation
 * 
 *                                      This class is used to contain the data
 *                                      of single additional profile
 * 
 */
public class AdditionalProfileCombinedVO implements Serializable {

    private AdditionalProfileServicesVO additionalProfileServicesVO;
    private ArrayList _slabsList;// contains VO's of AdditionalProfileDeatilsVO

    /**
     * @return Returns the slabsList.
     */
    public ArrayList getSlabsList() {
        return _slabsList;
    }

    /**
     * @param slabsList
     *            The slabsList to set.
     */
    public void setSlabsList(ArrayList commissionProfileSlabList) {
        this._slabsList = commissionProfileSlabList;
    }

    /**
     * @return Returns the additionalProfileServicesVO.
     */
    public AdditionalProfileServicesVO getAdditionalProfileServicesVO() {
        return additionalProfileServicesVO;
    }

    /**
     * @param additionalProfileServicesVO
     *            The additionalProfileServicesVO to set.
     */
    public void setAdditionalProfileServicesVO(AdditionalProfileServicesVO additionalProfileServicesVO) {
        this.additionalProfileServicesVO = additionalProfileServicesVO;
    }
}
