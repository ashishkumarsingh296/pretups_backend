package com.btsl.voms.voucher.businesslogic;

import java.util.Date;

/**
 * @(#)VomsPrintBatchVO.java Copyright(c) 2012, Comviva Technologies Ltd. All
 *                           Rights Reserved
 * 
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Rahul Dutt 16/05/2012 Initial Creation
 * 
 *                           VO class for VOMS_PRINT_BATCHES table i/o ops
 */
public class VomsPrintBatchVO implements java.io.Serializable {
    private String _printbatchID;
    private String _startSerialNo;
    private String _endSerialNo;
    private String _userID;
    private String _isDownloaded;
    private String _productID;
    private String _productName;
    private String _vomsDecryKey;
    private long _totNoOfVOuchers;
    private Date _createdOn;
    private Date _modifiedOn;
    private String _createdBy;
    private String _modifiedBy;
    private String _mrp;
    private String _voucherType;
    private String voucherName;
    private String voucherSegmentDesc;
    private String voucherSegment;
    private String network;

    public String getVoucherSegmentDesc() {
		return voucherSegmentDesc;
	}

	public void setVoucherSegmentDesc(String voucherSegmentDesc) {
		this.voucherSegmentDesc = voucherSegmentDesc;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("_printbatchID" + _printbatchID);
        buffer.append(",_startSerialNo" + _startSerialNo);
        buffer.append(",_endSerialNo" + _endSerialNo);
        buffer.append(",_userID" + _userID);
        buffer.append(",_isDownloaded" + _isDownloaded);
        buffer.append(",_productID" + _productID);
        buffer.append(",_productName" + _productName);
        buffer.append(",_totNoOfVOuchers" + _totNoOfVOuchers);
        buffer.append(",_createdOn" + _createdOn);
        buffer.append(",_modifiedOn" + _modifiedOn);
        buffer.append(",_createdBy" + _createdBy);
        buffer.append(",_modifiedBy" + _modifiedBy);
        buffer.append(",_mrp" + _mrp);
        buffer.append(",voucherName=" + voucherName);
        buffer.append(",voucherSegment=" + voucherSegment);
        buffer.append(",voucherSegmentDesc=" + voucherSegmentDesc);
        return buffer.toString();
    }

    /**
     * @return the endSerialNo
     */
    public String getEndSerialNo() {
        return _endSerialNo;
    }

    /**
     * @param endSerialNo
     *            the endSerialNo to set
     */
    public void setEndSerialNo(String endSerialNo) {
        _endSerialNo = endSerialNo;
    }

    /**
     * @return the isDownloaded
     */
    public String getIsDownloaded() {
        return _isDownloaded;
    }

    /**
     * @param isDownloaded
     *            the isDownloaded to set
     */
    public void setIsDownloaded(String isDownloaded) {
        _isDownloaded = isDownloaded;
    }

    /**
     * @return the printbatchID
     */
    public String getPrintbatchID() {
        return _printbatchID;
    }

    /**
     * @param printbatchID
     *            the printbatchID to set
     */
    public void setPrintbatchID(String printbatchID) {
        _printbatchID = printbatchID;
    }

    /**
     * @return the productID
     */
    public String getProductID() {
        return _productID;
    }

    /**
     * @param productID
     *            the productID to set
     */
    public void setProductID(String productID) {
        _productID = productID;
    }

    /**
     * @return the startSerialNo
     */
    public String getStartSerialNo() {
        return _startSerialNo;
    }

    /**
     * @param startSerialNo
     *            the startSerialNo to set
     */
    public void setStartSerialNo(String startSerialNo) {
        _startSerialNo = startSerialNo;
    }

    /**
     * @return the totNoOfVOuchers
     */
    public long getTotNoOfVOuchers() {
        return _totNoOfVOuchers;
    }

    /**
     * @param totNoOfVOuchers
     *            the totNoOfVOuchers to set
     */
    public void setTotNoOfVOuchers(long totNoOfVOuchers) {
        _totNoOfVOuchers = totNoOfVOuchers;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            the userID to set
     */
    public void setUserID(String userID) {
        _userID = userID;
    }

    /**
     * @return the vomsDecryKey
     */
    public String getVomsDecryKey() {
        return _vomsDecryKey;
    }

    /**
     * @param vomsDecryKey
     *            the vomsDecryKey to set
     */
    public void setVomsDecryKey(String vomsDecryKey) {
        _vomsDecryKey = vomsDecryKey;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @return the createdOn
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            the createdOn to set
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            the modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

    /**
     * @return the modifiedOn
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            the modifiedOn to set
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return the mrp
     */
    public String getMrp() {
        return _mrp;
    }

    /**
     * @param mrp
     *            the mrp to set
     */
    public void setMrp(String mrp) {
        _mrp = mrp;
    }

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String vtype) {
        _voucherType = vtype;
    }

	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	public String getVoucherName() {
		return voucherName;
	}

	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}
}
