package com.btsl.voms.vomsproduct.businesslogic;

import java.util.ArrayList;

/*
 * @(#)VomsActiveProductItemVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Singh 10/07/2006 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */

public class VomsActiveProductItemVO extends VomsProductVO {

    // Instanse variables
    private String _activeProductID;
    private String _checkBoxVal;
    private String _newProductID;
    private String _newProductName;
    private ArrayList _itemsList;
    private String _voucherType;
    private String _mrpString;
    private String type;

	/**
     * Method toString.
     * This method is used to display all of the information of
     * the object of the VomsActiveProductItemVO class.
     * 
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(" _activeProductID=" + _activeProductID);

        return sb.toString();
    }

    /**
     * @return Returns the newProductID.
     */
    public String getNewProductID() {
        return _newProductID;
    }

    /**
     * @param newProductID
     *            The newProductID to set.
     */
    public void setNewProductID(String newProductID) {
        _newProductID = newProductID;
    }

    /**
     * @return Returns the newProductName.
     */
    public String getNewProductName() {
        return _newProductName;
    }

    /**
     * @param newProductName
     *            The newProductName to set.
     */
    public void setNewProductName(String newProductName) {
        _newProductName = newProductName;
    }

    /**
     * @return Returns the checkBoxVal.
     */
    public String getCheckBoxVal() {
        return _checkBoxVal;
    }

    /**
     * @param checkBoxVal
     *            The checkBoxVal to set.
     */
    public void setCheckBoxVal(String checkBoxVal) {
        _checkBoxVal = checkBoxVal;
    }

    /**
     * @return Returns the activeProductID.
     */
    public String getActiveProductID() {
        return _activeProductID;
    }

    /**
     * @param activeProductID
     *            The activeProductID to set.
     */
    public void setActiveProductID(String activeProductID) {
        _activeProductID = activeProductID;
    }

    /**
     * @return Returns the itemsList.
     */
    public ArrayList getItemsList() {
        return _itemsList;
    }

    /**
     * @param itemsList
     *            The itemsList to set.
     */
    public void setItemsList(ArrayList itemsList) {
        _itemsList = itemsList;
    }

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public String getMrpString() {
        return _mrpString;
    }

    public void setMrpString(String _mrp) {
        this._mrpString = _mrp;
    }
    
    public String getType() {
  		return type;
  	}

  	public void setType(String type) {
  		this.type = type;
  	}

}
