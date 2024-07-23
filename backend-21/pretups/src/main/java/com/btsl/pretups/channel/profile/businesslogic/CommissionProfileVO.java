/**
 * @(#)CommissionProfileProductsVO.java
 *                                      Copyright(c) 2005, Comviva Technologies
 *                                      Ltd.
 *                                      All Rights Reserved
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Vikas Jauhari May 12, 2011 Initital
 *                                      Creation
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 * 
 */
package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;

public class CommissionProfileVO implements Serializable {

    private CommissionProfileProductsVO commissionProfileProductsVO = null;
    private CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
    private AdditionalProfileServicesVO additionalProfileServicesVO = null;
    private AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
    private CommissionProfileSetVO commissionProfileSetVO = null;
    private CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;
    private ArrayList _commProfileSlabDetail;
    private ArrayList _addCommProfSlabDetails;
    private boolean _flagAddCommProfExist = false;

    public AdditionalProfileDeatilsVO getAdditionalProfileDeatilsVO() {
        return additionalProfileDeatilsVO;
    }

    public void setAdditionalProfileDeatilsVO(AdditionalProfileDeatilsVO additionalProfileDeatilsVO) {
        this.additionalProfileDeatilsVO = additionalProfileDeatilsVO;
    }

    public AdditionalProfileServicesVO getAdditionalProfileServicesVO() {
        return additionalProfileServicesVO;
    }

    public void setAdditionalProfileServicesVO(AdditionalProfileServicesVO additionalProfileServicesVO) {
        this.additionalProfileServicesVO = additionalProfileServicesVO;
    }

    public CommissionProfileDeatilsVO getCommissionProfileDeatilsVO() {
        return commissionProfileDeatilsVO;
    }

    public void setCommissionProfileDeatilsVO(CommissionProfileDeatilsVO commissionProfileDeatilsVO) {
        this.commissionProfileDeatilsVO = commissionProfileDeatilsVO;
    }

    public CommissionProfileProductsVO getCommissionProfileProductsVO() {
        return commissionProfileProductsVO;
    }

    public void setCommissionProfileProductsVO(CommissionProfileProductsVO commissionProfileProductsVO) {
        this.commissionProfileProductsVO = commissionProfileProductsVO;
    }

    public CommissionProfileSetVO getCommissionProfileSetVO() {
        return commissionProfileSetVO;
    }

    public void setCommissionProfileSetVO(CommissionProfileSetVO commissionProfileSetVO) {
        this.commissionProfileSetVO = commissionProfileSetVO;
    }

    public CommissionProfileSetVersionVO getCommissionProfileSetVersionVO() {
        return commissionProfileSetVersionVO;
    }

    public void setCommissionProfileSetVersionVO(CommissionProfileSetVersionVO commissionProfileSetVersionVO) {
        this.commissionProfileSetVersionVO = commissionProfileSetVersionVO;
    }

    public ArrayList getCommProfileSlabDetail() {
        return _commProfileSlabDetail;
    }

    public void setCommProfileSlabDetail(ArrayList profileSlabDetail) {
        this._commProfileSlabDetail = profileSlabDetail;
    }

    public ArrayList getAddCommProfSlabDetails() {
        return _addCommProfSlabDetails;
    }

    public void setAddCommProfSlabDetails(ArrayList addCommProfSlabDetails) {
        this._addCommProfSlabDetails = addCommProfSlabDetails;
    }

    public boolean isFlagAddCommProfExist() {
        return _flagAddCommProfExist;
    }

    public void setFlagAddCommProfExist(boolean addCommProfExist) {
        _flagAddCommProfExist = addCommProfExist;
    }
}
