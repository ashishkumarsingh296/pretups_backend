/*
 * Created on Aug 9, 2005
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.master.businesslogic;

import java.io.Serializable;

/**
 * @(#)GeographicalDomainTypeVO.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Mohit Goel 11/08/2005 Initial Creation
 * 
 * 
 * 
 */
public class GeographicalDomainTypeVO implements Serializable {

    private String _grphDomainType;
    private String _grphDomainTypeName;
    private String _grphDomainParent;
    private String _controllingUnit;
    private String _grphDomainSequenceNo;
    private String _categoryCode;

    public String toString() {

        StringBuffer strBuff = new StringBuffer("\n grphDomainType=" + _grphDomainType);
        strBuff.append("\n grphDomainTypeName=" + _grphDomainTypeName);
        strBuff.append("\n grphDomainParent=" + _grphDomainParent);
        strBuff.append("\n controllingUnit=" + _controllingUnit);
        strBuff.append("\n grphDomainSequenceNo=" + _grphDomainSequenceNo);

        return strBuff.toString();
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the controllingUnit.
     */
    public String getControllingUnit() {
        return _controllingUnit;
    }

    /**
     * @param controllingUnit
     *            The controllingUnit to set.
     */
    public void setControllingUnit(String controllingUnit) {
        _controllingUnit = controllingUnit;
    }

    /**
     * @return Returns the grphDomainParent.
     */
    public String getGrphDomainParent() {
        return _grphDomainParent;
    }

    /**
     * @param grphDomainParent
     *            The grphDomainParent to set.
     */
    public void setGrphDomainParent(String grphDomainParent) {
        _grphDomainParent = grphDomainParent;
    }

    /**
     * @return Returns the grphDomainSequenceNo.
     */
    public String getGrphDomainSequenceNo() {
        return _grphDomainSequenceNo;
    }

    /**
     * @param grphDomainSequenceNo
     *            The grphDomainSequenceNo to set.
     */
    public void setGrphDomainSequenceNo(String grphDomainSequenceNo) {
        _grphDomainSequenceNo = grphDomainSequenceNo;
    }

    /**
     * @return Returns the grphDomainType.
     */
    public String getGrphDomainType() {
        return _grphDomainType;
    }

    /**
     * @param grphDomainType
     *            The grphDomainType to set.
     */
    public void setGrphDomainType(String grphDomainType) {
        _grphDomainType = grphDomainType;
    }

    /**
     * @return Returns the grphDomainTypeName.
     */
    public String getGrphDomainTypeName() {
        return _grphDomainTypeName;
    }

    /**
     * @param grphDomainTypeName
     *            The grphDomainTypeName to set.
     */
    public void setGrphDomainTypeName(String grphDomainTypeName) {
        _grphDomainTypeName = grphDomainTypeName;
    }
}
