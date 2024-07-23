package com.btsl.pretups.loyalitystock.businesslogic;

/**
 * @(#)LoyalityStockVO.java
 *                          Copyright(c) 2005, Mahindra Comviva Technologies
 *                          Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Vikas Jauhari Nov 25, 2013 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 * 
 */
import java.io.Serializable;

public class LoyalityStockVO implements Serializable {

    private String _itemCode;
    private String _itemName;
    private int _itemStockAvailable;
    private int _perItemPoints;
    private int _stockItemBuffer;
    private String _status;

    public String getItemCode() {
        return _itemCode;
    }

    public void setItemCode(String code) {
        _itemCode = code;
    }

    public String getItemName() {
        return _itemName;
    }

    public void setItemName(String name) {
        _itemName = name;
    }

    public int getItemStockAvailable() {
        return _itemStockAvailable;
    }

    public void setItemStockAvailable(int stockAvailable) {
        _itemStockAvailable = stockAvailable;
    }

    public int getPerItemPoints() {
        return _perItemPoints;
    }

    public void setPerItemPoints(int itemPoints) {
        _perItemPoints = itemPoints;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String _status) {
        this._status = _status;
    }

    public int getStockItemBuffer() {
        return _stockItemBuffer;
    }

    public void setStockItemBuffer(int itemBuffer) {
        _stockItemBuffer = itemBuffer;
    }
}
