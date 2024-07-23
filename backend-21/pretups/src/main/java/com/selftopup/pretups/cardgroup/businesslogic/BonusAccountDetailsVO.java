package com.selftopup.pretups.cardgroup.businesslogic;

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
    private String _cardGroupSetID = null;
    private String _version = null;
    private String _cardGroupID = null;
    private String _bundleID = null;
    private String _type = "AMT";
    private String _bonusValidity = "0";
    private String _bonusValue = "0";
    private String _multFactor = "1";
    private String _bonusName = null;
    private String _bundleType = null;
    private String _restrictedOnIN = "Y";
    private ArrayList _bonusAccDetailList = null;
    private String _bonusCode = null;

    public BonusAccountDetailsVO() {
    };

    public BonusAccountDetailsVO(BonusAccountDetailsVO bonusAccountDetailsVO) {
        this._cardGroupSetID = bonusAccountDetailsVO._cardGroupSetID;
        this._version = bonusAccountDetailsVO._version;
        this._cardGroupID = bonusAccountDetailsVO._cardGroupID;
        this._bundleID = bonusAccountDetailsVO._bundleID;
        this._type = bonusAccountDetailsVO._type;
        this._bonusValidity = bonusAccountDetailsVO._bonusValidity;
        this._bonusValue = bonusAccountDetailsVO._bonusValue;
        this._multFactor = bonusAccountDetailsVO._multFactor;
        this._multFactor = bonusAccountDetailsVO._bonusName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("CardGroupSetVO Data ");
        sb.append("_cardGroupSetID=" + _cardGroupSetID + ",");
        sb.append("_version=" + _version + ",");
        sb.append("_cardGroupID=" + _cardGroupID + ",");
        sb.append("_bundleID=" + _bundleID + ",");
        sb.append("_type=" + _type + ",");
        sb.append("_bonusValidity=" + _bonusValidity + ",");
        sb.append("_bonusValue=" + _bonusValue + ",");
        sb.append("_multFactor=" + _multFactor + ",");
        sb.append("_bonusName=" + _bonusName + ",");
        sb.append("_bundleType=" + _bundleType + ",");
        sb.append("_restrictedOnIN=" + _restrictedOnIN + ",");

        return sb.toString();
    }

    /**
     * @return Returns the bonusValidity.
     */
    public String getBonusValidity() {
        return _bonusValidity;
    }

    /**
     * @param bonusValidity
     *            The bonusValidity to set.
     */
    public void setBonusValidity(String bonusValidity) {
        _bonusValidity = bonusValidity;
    }

    /**
     * @return Returns the bonusValue.
     */
    public String getBonusValue() {
        return _bonusValue;
    }

    /**
     * @param bonusValue
     *            The bonusValue to set.
     */
    public void setBonusValue(String bonusValue) {
        _bonusValue = bonusValue;
    }

    /**
     * @return Returns the bundleCode.
     */
    public String getBundleID() {
        return _bundleID;
    }

    /**
     * @param bundleCode
     *            The bundleCode to set.
     */
    public void setBundleID(String bundleID) {
        _bundleID = bundleID;
    }

    /**
     * @return Returns the cardGroupID.
     */
    public String getCardGroupID() {
        return _cardGroupID;
    }

    /**
     * @param cardGroupID
     *            The cardGroupID to set.
     */
    public void setCardGroupID(String cardGroupID) {
        _cardGroupID = cardGroupID;
    }

    /**
     * @return Returns the cardGroupSetID.
     */
    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    /**
     * @param cardGroupSetID
     *            The cardGroupSetID to set.
     */
    public void setCardGroupSetID(String cardGroupSetID) {
        _cardGroupSetID = cardGroupSetID;
    }

    /**
     * @return Returns the multFactor.
     */
    public String getMultFactor() {
        return _multFactor;
    }

    /**
     * @param multFactor
     *            The multFactor to set.
     */
    public void setMultFactor(String multFactor) {
        _multFactor = multFactor;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
     * @return Returns the bonusAccDetailList.
     */
    public ArrayList getBonusAccDetailList() {
        return _bonusAccDetailList;
    }

    /**
     * @param bonusAccDetailList
     *            The bonusAccDetailList to set.
     */
    public void setBonusAccDetailList(ArrayList bonusAccDetailList) {
        _bonusAccDetailList = bonusAccDetailList;
    }

    /**
     * @return Returns the bonusName.
     */
    public String getBonusName() {
        return _bonusName;
    }

    /**
     * @param bonusName
     *            The bonusName to set.
     */
    public void setBonusName(String bonusName) {
        _bonusName = bonusName;
    }

    /**
     * @return Returns the bundleType.
     */
    public String getBundleType() {
        return _bundleType;
    }

    /**
     * @param bundleType
     *            The bundleType to set.
     */
    public void setBundleType(String bundleType) {
        _bundleType = bundleType;
    }

    /**
     * @return Returns the restrictedOnIN.
     */
    public String getRestrictedOnIN() {
        return _restrictedOnIN;
    }

    /**
     * @param restrictedOnIN
     *            The restrictedOnIN to set.
     */
    public void setRestrictedOnIN(String restrictedOnIN) {
        _restrictedOnIN = restrictedOnIN;
    }

    public String getBonusCode() {
        return _bonusCode;
    }

    public void setBonusCode(String code) {
        _bonusCode = code;
    }
}
