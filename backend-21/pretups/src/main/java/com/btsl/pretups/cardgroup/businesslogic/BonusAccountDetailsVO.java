package com.btsl.pretups.cardgroup.businesslogic;

/*
 * Created on Jun 16, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author vinay.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BonusAccountDetailsVO implements Serializable {
    private String cardGroupSetID = null;
    private String version = null;
    private String cardGroupID = null;
    private String bundleID = null;
    private String type = "AMT";
    private String bonusValidity = "0";
    private String bonusValue = "0";
    private String multFactor = "1";
    private String bonusName = null;
    private String bundleType = null;
    private String restrictedOnIN = "Y";
    private ArrayList bonusAccDetailList = null;
    private String bonusCode = null;

    public BonusAccountDetailsVO() {
   
    };

    /**
     * @param bonusAccountDetailsVO
     */
    public BonusAccountDetailsVO(
                    BonusAccountDetailsVO bonusAccountDetailsVO) {
        this.cardGroupSetID = bonusAccountDetailsVO.cardGroupSetID;
        this.version = bonusAccountDetailsVO.version;
        this.cardGroupID = bonusAccountDetailsVO.cardGroupID;
        this.bundleID = bonusAccountDetailsVO.bundleID;
        this.type = bonusAccountDetailsVO.type;
        this.bonusValidity = bonusAccountDetailsVO.bonusValidity;
        this.bonusValue = bonusAccountDetailsVO.bonusValue;
        this.multFactor = bonusAccountDetailsVO.multFactor;
        this.multFactor = bonusAccountDetailsVO.bonusName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuilder sb = new StringBuilder("CardGroupSetVO Data ");
        sb.append("cardGroupSetID=" + cardGroupSetID + ",");
        sb.append("version=" + version + ",");
        sb.append("cardGroupID=" + cardGroupID + ",");
        sb.append("bundleID=" + bundleID + ",");
        sb.append("type=" + type + ",");
        sb.append("bonusValidity=" + bonusValidity + ",");
        sb.append("bonusValue=" + bonusValue + ",");
        sb.append("multFactor=" + multFactor + ",");
        sb.append("bonusName=" + bonusName + ",");
        sb.append("bundleType=" + bundleType + ",");
        sb.append("restrictedOnIN=" + restrictedOnIN + ",");

        return sb.toString();
    }

    /**
     * @return Returns the bonusValidity.
     */
    public String getBonusValidity() {
        return bonusValidity;
    }

    /**
     * @param bonusValidity
     *            The bonusValidity to set.
     */
    public void setBonusValidity(String bonusValidity) {
    	this. bonusValidity = bonusValidity;
    }

    /**
     * @return Returns the bonusValue.
     */
    public String getBonusValue() {
        return bonusValue;
    }

    /**
     * @param bonusValue
     *            The bonusValue to set.
     */
    public void setBonusValue(String bonusValue) {
    	this.bonusValue = bonusValue;
    }

    /**
     * @return Returns the bundleCode.
     */
    public String getBundleID() {
        return bundleID;
    }

    /**
     * @param bundleCode
     *            The bundleCode to set.
     */
    public void setBundleID(String bundleID) {
    	this.bundleID = bundleID;
    }

    /**
     * @return Returns the cardGroupID.
     */
    public String getCardGroupID() {
        return cardGroupID;
    }

    /**
     * @param cardGroupID
     *            The cardGroupID to set.
     */
    public void setCardGroupID(String cardGroupID) {
    	this.cardGroupID = cardGroupID;
    }

    /**
     * @return Returns the cardGroupSetID.
     */
    public String getCardGroupSetID() {
        return cardGroupSetID;
    }

    /**
     * @param cardGroupSetID
     *            The cardGroupSetID to set.
     */
    public void setCardGroupSetID(String cardGroupSetID) {
    	this.cardGroupSetID = cardGroupSetID;
    }

    /**
     * @return Returns the multFactor.
     */
    public String getMultFactor() {
        return multFactor;
    }

    /**
     * @param multFactor
     *            The multFactor to set.
     */
    public void setMultFactor(String multFactor) {
    	this. multFactor = multFactor;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
    	this. type = type;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
    	this.version = version;
    }

    /**
     * @return Returns the bonusAccDetailList.
     */
    public ArrayList getBonusAccDetailList() {
        return bonusAccDetailList;
    }

    /**
     * @param bonusAccDetailList
     *            The bonusAccDetailList to set.
     */
    public void setBonusAccDetailList(ArrayList bonusAccDetailList) {
    	this. bonusAccDetailList = bonusAccDetailList;
    }

    /**
     * @return Returns the bonusName.
     */
    public String getBonusName() {
        return bonusName;
    }

    /**
     * @param bonusName
     *            The bonusName to set.
     */
    public void setBonusName(String bonusName) {
    	this.bonusName = bonusName;
    }

    /**
     * @return Returns the bundleType.
     */
    public String getBundleType() {
        return bundleType;
    }

    /**
     * @param bundleType
     *            The bundleType to set.
     */
    public void setBundleType(String bundleType) {
    	this.bundleType = bundleType;
    }

    /**
     * @return Returns the restrictedOnIN.
     */
    public String getRestrictedOnIN() {
        return restrictedOnIN;
    }

    /**
     * @param restrictedOnIN
     *            The restrictedOnIN to set.
     */
    public void setRestrictedOnIN(String restrictedOnIN) {
    	this.restrictedOnIN = restrictedOnIN;
    }

    public String getBonusCode() {
        return bonusCode;
    }

    public void setBonusCode(String code) {
        bonusCode = code;
    }
}
