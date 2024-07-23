package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @(#)CommissionProfileCombinedVO.java
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
 *                                      of single commission profile
 * 
 */
public class CommissionProfileCombinedVO implements Serializable {

    private CommissionProfileProductsVO _commissionProfileProductVO;
    private ArrayList _slabsList;// contains VO's of CommissionProfileDeatilsVO

    /**
     * @return Returns the commissionProfileProductVO.
     */
    public CommissionProfileProductsVO getCommissionProfileProductVO() {
        return _commissionProfileProductVO;
    }

    /**
     * @param commissionProfileProductVO
     *            The commissionProfileProductVO to set.
     */
    public void setCommissionProfileProductVO(CommissionProfileProductsVO commissionProfileProductVO) {
        _commissionProfileProductVO = commissionProfileProductVO;
    }

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

}
