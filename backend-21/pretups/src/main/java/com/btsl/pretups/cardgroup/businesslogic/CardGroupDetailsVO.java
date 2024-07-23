package com.btsl.pretups.cardgroup.businesslogic;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/*
 * CardGroupDetailsVO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 28/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Card Group Details object for interaction with the database
 */

public class CardGroupDetailsVO implements Serializable {
    private String _cardGroupSetID;
    private String _version;
    private String _cardGroupID;
    private String _cardGroupCode;
    private long _startRange;
    private long _endRange;
    private String _validityPeriodType;
    private int _validityPeriod;
    private long _gracePeriod;
    private String _senderTax1Name;
    private String _senderTax1Type;
    private double _senderTax1Rate;
    private String _senderTax2Name;
    private String _senderTax2Type;
    private double _senderTax2Rate;
    private String _receiverTax1Name;
    private String _receiverTax1Type;
    private double _receiverTax1Rate;
    private String _receiverTax2Name;
    private String _receiverTax2Type;
    private double _receiverTax2Rate;
    
//    private Log log = LogFactory.getLog(CardGroupDetailsVO.class.getName());
//    public static final Log log = LogFactory.getLog(CardGroupDetailsVO.class.getName());

    private String _senderAccessFeeType;
    private double _senderAccessFeeRate;
    private long _minSenderAccessFee;
    private long _maxSenderAccessFee;
    private String _receiverAccessFeeType;
    private double _receiverAccessFeeRate;
    private long _minReceiverAccessFee;
    private long _maxReceiverAccessFee;
    private long _multipleOf;
    // the fields below is used to show card_group_set_name and sub_service on
    // testCardGroupcommon.jsp
    private String _cardGroupSetName;
    private String _cardGroupSubServiceId;
    private String _cardGroupSubServiceIdDesc;

    private long _transferValue;
    private String _serviceTypeId;
    private String _serviceTypeDesc;
    private String _setType;
    private String _setTypeName;

    // added for card group slab suspend/resume
    private String _status;

    private int _rowIndex = 0;
    private String _editDetail = null;
    private long _bonusValidityValue;

    // Online/Offline feature
    private String _online;
    private String _both;
    // added for card group slab copy
    private String[] _cardGroupList = null;
    private String _networkCode;
    private Date _applicableFrom;
    private long _oldApplicableFrom;
    private String _LastVersion;

    // Following variables are for selector bonus fields
    private String _bonusTalkTimeType;
    private double _bonusTalkTimeRate;
    private long _bonusTalkTimeValue;
    private String _bonusTalkTimeValidity;
    private double _bonusTalkTimeConvFactor;
    private String _bonusTalkTimeBundleType;

    // Added for bonus bundles
    private String _senderConvFactor = null;
    private String _receiverConvFactor = null;
    private ArrayList<BonusAccountDetailsVO> _bonusAccList = null;
    private String _serviceTypeSelector = null;

    private double _bonus1;
    private double _bonus2;

    private long _bonus1validity;
    private long _bonus2validity;
    private long _bonusTalktimevalidity;
    // added for cos
    private String _cosRequired;

    private double _inPromo;
    // 6.6
    private String _reversalPermitted;
    private String _cardName;
    private Date _reversalModifiedDate;
    private String _reversalModifiedDateAsString;
    //Added for TAX3 and TAX4
    private String _receiverTax3Name;
    private String _receiverTax3Type;
    private double _receiverTax3Rate;
    private String _receiverTax4Name;
    private String _receiverTax4Type;
    private double _receiverTax4Rate;
    private static final float EPSILON=0.0000001f;
    
    private String voucherType;
    private String voucherTypeDesc;
    private String voucherSegment;
    private String voucherSegmentDesc;
    private String voucherProductId;
    private String voucherProductName;
    private String cardGroupType;
    private String voucherDenomination;
    private String productName;
    
    private String receiverTax3RateAsString;
    private String receiverTax4RateAsString;
    private String maxReceiverAccessFeeAsString;
    private String startRangeAsString;
    private String maxSenderAccessFeeAsString;
    private String minSenderAccessFeeAsString;
    private String validityPeriodAsString;
    private String reversalModifiedDateAsString;
    private String endRangeAsString;
    private String minReceiverAccessFeeAsString;
    private String senderTax2RateAsString;
    private String applicableFromAsString;
    private String multipleOfAsString;
    private String inPromoAsString;
    private String senderAccessFeeRateAsString;
    private String senderTax1RateAsString;
    private String receiverAccessFeeRateAsString;
    private String receiverTax1RateAsString;
    private String receiverTax2RateAsString;
    private String validityPeriodTypeDesc;
    private String availableVouchers;
    
    
    public String getValidityPeriodTypeDesc() {
		return validityPeriodTypeDesc;
	}

	public void setValidityPeriodTypeDesc(String validityPeriodTypeDesc) {
		this.validityPeriodTypeDesc = validityPeriodTypeDesc;
	}

	public String getAvailableVouchers() {
		return availableVouchers;
	}

	public void setAvailableVouchers(String availableVouchers) {
		this.availableVouchers = availableVouchers;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	public String getVoucherProductId() {
		return voucherProductId;
	}

	public void setVoucherProductId(String voucherProductId) {
		this.voucherProductId = voucherProductId;
	}
	
    /**
     * @return Returns the cardGroupList.
     */
    public String[] getCardGroupList() {
        return _cardGroupList;
    }

    /**
     * @param cardGroupList
     *            The cardGroupList to set.
     */
    public void setCardGroupList(String[] cardGroupList) {
        this._cardGroupList = cardGroupList;
    }

    /**
     * @return Returns the editDetail.
     */
    public String getEditDetail() {
        return _editDetail;
    }

    /**
     * @param editDetail
     *            The editDetail to set.
     */
    public void setEditDetail(String editDetail) {
        _editDetail = editDetail;
    }

    /**
     * @return Returns the rowIndex.
     */
    public int getRowIndex() {
        return _rowIndex;
    }

    /**
     * @param rowIndex
     *            The rowIndex to set.
     */
    public void setRowIndex(int rowIndex) {
        _rowIndex = rowIndex;
    }

    public String getCardGroupID() {
        return _cardGroupID;
    }

    public void setCardGroupID(String cardGroupID) {
        _cardGroupID = cardGroupID;
    }

    public String getCardGroupSetID() {
        return _cardGroupSetID;
    }

    public void setCardGroupSetID(String cardGroupSetID) {
        _cardGroupSetID = cardGroupSetID;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long endRange) {
        _endRange = endRange;
    }

    public String getEndRangeAsString() {
        if (_endRange == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_endRange);
        }
    }

    public void setEndRangeAsString(String endRange) {
        final String METHOD_NAME = "setEndRangeAsString";
        try {
            _endRange = PretupsBL.getSystemAmount(endRange);
        } catch (BTSLBaseException e) {
            _endRange = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if _endRange is blank then we put it as -1 to recongnise in getter.
    }

    public long getGracePeriod() {
        return _gracePeriod;
    }

    public void setGracePeriod(long gracePeriod) {
        _gracePeriod = gracePeriod;
    }

    public void setGracePeriod(String gracePeriod) {
        try {
            _gracePeriod = Long.parseLong(gracePeriod);
        } catch (NumberFormatException e) {
            _gracePeriod = 0;
        }
    }

    public long getMaxReceiverAccessFee() {
        return _maxReceiverAccessFee;
    }

    public void setMaxReceiverAccessFee(long maxReceiverAccessFee) {
        _maxReceiverAccessFee = maxReceiverAccessFee;
    }

    public String getMaxReceiverAccessFeeAsString() {
        if (_maxReceiverAccessFee == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_maxReceiverAccessFee);
        }
    }

    public void setMaxReceiverAccessFeeAsString(String maxReceiverAccessFee) {
        final String METHOD_NAME = "setMaxReceiverAccessFeeAsString";
        try {
            _maxReceiverAccessFee = PretupsBL.getSystemAmount(maxReceiverAccessFee);
        } catch (BTSLBaseException e) {
            _maxReceiverAccessFee = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if _maxReceiverAccessFee is blank then we put it as -1 to recongnise
         // in getter.
    }

    public long getMaxSenderAccessFee() {
        return _maxSenderAccessFee;
    }

    public void setMaxSenderAccessFee(long maxSenderAccessFee) {
        _maxSenderAccessFee = maxSenderAccessFee;
    }

    public String getMaxSenderAccessFeeAsString() {
        if (_maxSenderAccessFee == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_maxSenderAccessFee);
        }
    }

    public void setMaxSenderAccessFeeAsString(String maxSenderAccessFee) {
        final String METHOD_NAME = "setMaxSenderAccessFeeAsString";
        try {
            _maxSenderAccessFee = PretupsBL.getSystemAmount(maxSenderAccessFee);
        } catch (BTSLBaseException e) {
            _maxSenderAccessFee = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if _maxSenderAccessFee is blank then we put it as -1 to recongnise
         // in getter.
    }

    public long getMinReceiverAccessFee() {
        return _minReceiverAccessFee;
    }

    public void setMinReceiverAccessFee(long minReceiverAccessFee) {
        _minReceiverAccessFee = minReceiverAccessFee;
    }

    public String getMinReceiverAccessFeeAsString() {
        if (_minReceiverAccessFee == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_minReceiverAccessFee);
        }
    }

    public void setMinReceiverAccessFeeAsString(String minReceiverAccessFee) {
        final String METHOD_NAME = "setMinReceiverAccessFeeAsString";
        try {
            _minReceiverAccessFee = PretupsBL.getSystemAmount(minReceiverAccessFee);
        } catch (BTSLBaseException e) {
            _minReceiverAccessFee = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if _minReceiverAccessFee is blank then we put it as -1 to recongnise
         // in getter.
    }

    public long getMinSenderAccessFee() {
        return _minSenderAccessFee;
    }

    public void setMinSenderAccessFee(long minSenderAccessFee) {
        _minSenderAccessFee = minSenderAccessFee;
    }

    public String getMinSenderAccessFeeAsString() {
        if (_minSenderAccessFee == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_minSenderAccessFee);
        }
    }

    public void setMinSenderAccessFeeAsString(String minSenderAccessFee) {
        final String METHOD_NAME = "setMinSenderAccessFeeAsString";
        try {
            _minSenderAccessFee = PretupsBL.getSystemAmount(minSenderAccessFee);
        } catch (BTSLBaseException e) {
            _minSenderAccessFee = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if _minSenderAccessFee is blank then we put it as -1 to recongnise
         // in getter.
    }

    public String getReceiverAccessFeeType() {
        return _receiverAccessFeeType;
    }

    public void setReceiverAccessFeeType(String receiverAccessFeeType) {
        _receiverAccessFeeType = receiverAccessFeeType;
    }

    public String getSenderAccessFeeType() {
        return _senderAccessFeeType;
    }

    public void setSenderAccessFeeType(String senderAccessFeeType) {
        _senderAccessFeeType = senderAccessFeeType;
    }

    public long getStartRange() {
        return _startRange;
    }

    public String getStartRangeAsString() {
        if (_startRange == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_startRange);
        }
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public void setStartRangeAsString(String startRange) {
        final String METHOD_NAME = "setStartRangeAsString";
        try {
            _startRange = PretupsBL.getSystemAmount(startRange);
        } catch (BTSLBaseException e) {
            _startRange = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if __startRange is blank then we put it as -1 to recongnise in
         // getter.
    }

    /**
     * @return Returns the receiverTax1Name.
     */
    public String getReceiverTax1Name() {
        return _receiverTax1Name;
    }

    /**
     * @param receiverTax1Name
     *            The receiverTax1Name to set.
     */
    public void setReceiverTax1Name(String receiverTax1Name) {
        _receiverTax1Name = receiverTax1Name;
    }

    /**
     * @return Returns the receiverTax1Type.
     */
    public String getReceiverTax1Type() {
        return _receiverTax1Type;
    }

    /**
     * @param receiverTax1Type
     *            The receiverTax1Type to set.
     */
    public void setReceiverTax1Type(String receiverTax1Type) {
        _receiverTax1Type = receiverTax1Type;
    }

    /**
     * @return Returns the receiverTax2Name.
     */
    public String getReceiverTax2Name() {
        return _receiverTax2Name;
    }

    /**
     * @param receiverTax2Name
     *            The receiverTax2Name to set.
     */
    public void setReceiverTax2Name(String receiverTax2Name) {
        _receiverTax2Name = receiverTax2Name;
    }

    /**
     * @return Returns the receiverTax2Type.
     */
    public String getReceiverTax2Type() {
        return _receiverTax2Type;
    }

    /**
     * @param receiverTax2Type
     *            The receiverTax2Type to set.
     */
    public void setReceiverTax2Type(String receiverTax2Type) {
        _receiverTax2Type = receiverTax2Type;
    }

    /**
     * @return Returns the senderTax1Name.
     */
    public String getSenderTax1Name() {
        return _senderTax1Name;
    }

    /**
     * @param senderTax1Name
     *            The senderTax1Name to set.
     */
    public void setSenderTax1Name(String senderTax1Name) {
        _senderTax1Name = senderTax1Name;
    }

    /**
     * @return Returns the senderTax1Type.
     */
    public String getSenderTax1Type() {
        return _senderTax1Type;
    }

    /**
     * @param senderTax1Type
     *            The senderTax1Type to set.
     */
    public void setSenderTax1Type(String senderTax1Type) {
        _senderTax1Type = senderTax1Type;
    }

    /**
     * @return Returns the senderTax2Name.
     */
    public String getSenderTax2Name() {
        return _senderTax2Name;
    }

    /**
     * @param senderTax2Name
     *            The senderTax2Name to set.
     */
    public void setSenderTax2Name(String senderTax2Name) {
        _senderTax2Name = senderTax2Name;
    }

    /**
     * @return Returns the senderTax2Type.
     */
    public String getSenderTax2Type() {
        return _senderTax2Type;
    }

    /**
     * @param senderTax2Type
     *            The senderTax2Type to set.
     */
    public void setSenderTax2Type(String senderTax2Type) {
        _senderTax2Type = senderTax2Type;
    }

    public String getValidityPeriodType() {
        return _validityPeriodType;
    }

    public void setValidityPeriodType(String validityPeriodType) {
        _validityPeriodType = validityPeriodType;
    }

    public int getValidityPeriod() {
        return _validityPeriod;
    }

    public String getValidityPeriodAsString() {
        if (_validityPeriod > 0) {
            return String.valueOf(_validityPeriod);
        } else {
            return "";
        }
    }

    public void setValidityPeriod(int validityPeriod) {
        _validityPeriod = validityPeriod;
    }

    public void setValidityPeriod(String validityPeriod) {
        try {
            _validityPeriod = Integer.parseInt(validityPeriod);
        } catch (NumberFormatException e) {
            _validityPeriod = 0;
        }
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public String getCardGroupCode() {
        return _cardGroupCode;
    }

    public void setCardGroupCode(String cardGroupCode) {
        _cardGroupCode = cardGroupCode;
    }

    public String getReversalPermitted() {
        return _reversalPermitted;
    }

    public void setReversalPermitted(String reversalPermitted) {
        _reversalPermitted = reversalPermitted;
    }

    public String getCardName() {
        return _cardName;
    }

    public void setCardName(String cardName) {
        _cardName = cardName;
    }

    public Date getReversalModifiedDate() {
        return _reversalModifiedDate;
    }

    public void setReversalModifiedDate(Date lastModifiedDate) {
        _reversalModifiedDate = lastModifiedDate;
    }

    public String getReversalModifiedDateAsString() {
        return _reversalModifiedDateAsString;
    }

    public void setReversalModifiedDateAsString(String lastModifiedDateAsString) {
        _reversalModifiedDateAsString = lastModifiedDateAsString;
    }

    /**
     * Returns a comma delimited list of the name/value pairs.
     */
    public String toString() {

        final StringBuilder sbd = new StringBuilder("CardGroupDetailsVO ");
        sbd.append("cardGroupSetID=").append(_cardGroupSetID).append(",");
        sbd.append("version=").append(_version).append(",");
        sbd.append("cardGroupID=").append(_cardGroupID).append(",");
        sbd.append("cardGroupCode=").append(_cardGroupCode).append(",");
        sbd.append("startRange=").append(_startRange).append(",");
        sbd.append("endRange=").append(_endRange).append(",");
        sbd.append("validityPeriodType=").append(_validityPeriodType).append(",");
        sbd.append("validityPeriod=").append(_validityPeriod).append(",");
        sbd.append("gracePeriod=").append(_gracePeriod).append(",");
        sbd.append("multipleOff=").append(_multipleOf).append(",");
        sbd.append("senderTax1Name=").append(_senderTax1Name).append(",");
        sbd.append("senderTax1Type=").append(_senderTax1Type).append(",");
        sbd.append("senderTax1Rate=").append(_senderTax1Rate).append(",");
        sbd.append("senderTax2Name=").append(_senderTax2Name).append(",");
        sbd.append("senderTax2Type=").append(_senderTax2Type).append(",");
        sbd.append("senderTax2Rate=").append(_senderTax2Rate).append(",");
        sbd.append("receiverTax1Name=").append(_receiverTax1Name).append(",");
        sbd.append("receiverTax1Type=").append(_receiverTax1Type).append(",");
        sbd.append("receiverTax1Rate=").append(_receiverTax1Rate).append(",");
        sbd.append("receiverTax2Name=").append(_receiverTax2Name).append(",");
        sbd.append("receiverTax2Type=").append(_receiverTax2Type).append(",");
        sbd.append("receiverTax2Rate=").append(_receiverTax2Rate).append(",");
        sbd.append("receiverTax3Name=" ).append( _receiverTax3Name ).append( ",");
        sbd.append("receiverTax3Type=" ).append( _receiverTax3Type ).append( ",");
        sbd.append("receiverTax3Rate=" ).append( _receiverTax3Rate ).append( ",");
        sbd.append("receiverTax4Name=" ).append( _receiverTax4Name ).append( ",");
        sbd.append("receiverTax4Type=" ).append( _receiverTax4Type ).append( ",");
        sbd.append("receiverTax4Rate=" ).append( _receiverTax4Rate ).append( ",");
        sbd.append("senderAccessFeeType=").append(_senderAccessFeeType).append(",");
        sbd.append("senderAccessFeeRate=").append(_senderAccessFeeRate).append(",");
        sbd.append("minSenderAccessFee=").append(_minSenderAccessFee).append(",");
        sbd.append("maxSenderAccessFee=").append(_maxSenderAccessFee).append(",");
        sbd.append("receiverAccessFeeType=").append(_receiverAccessFeeType).append(",");
        sbd.append("receiverAccessFeeRate=").append(_receiverAccessFeeRate).append(",");
        sbd.append("minReceiverAccessFee=").append(_minReceiverAccessFee).append(",");
        sbd.append("maxReceiverAccessFee=").append(_maxReceiverAccessFee).append(",");
        sbd.append("_serviceTypeId=").append(_serviceTypeId).append(",");
        sbd.append("_onLine=").append(_online).append(",");
        sbd.append("_both=").append(_both).append(",");
        sbd.append("_senderConvFactor=").append(_senderConvFactor).append(",");
        sbd.append("_receiverConvFactor=").append(_receiverConvFactor).append(",");
        sbd.append("_serviceTypeSelector=").append(_serviceTypeSelector).append(",");
        sbd.append("_bonusAccList=").append(_bonusAccList).append(",");
        sbd.append("voucherType=").append(voucherType).append(",");
        sbd.append("voucherSegment=").append(voucherSegment).append(",");
        sbd.append("voucherProductId=").append(voucherProductId).append(",");
        sbd.append("cardGroupType=").append(cardGroupType).append(",");
        sbd.append("cosRequired=").append(_cosRequired);
        return sbd.toString();
    }

    public double getReceiverTax1Rate() {
        return _receiverTax1Rate;
    }

    public void setReceiverTax1Rate(double receiverTax1Rate) {
        _receiverTax1Rate = receiverTax1Rate;
    }

    public String getReceiverTax1RateAsString() {
        if (Math.abs( _receiverTax1Rate-(-1))<EPSILON) {
            return null;
        }
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax1Type)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong( _receiverTax1Rate));
        } else {
            return String.valueOf(_receiverTax1Rate);
        }
    }

    public void setReceiverTax1RateAsString(double receiverTax1Rate) {
        final String METHOD_NAME = "setReceiverTax1RateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax1Type)) {
            try {
                _receiverTax1Rate = PretupsBL.getSystemAmount(receiverTax1Rate);
            } catch (BTSLBaseException e) {
               // _log.errorTrace("METHOD_NAME", e);
//            	log.errorTrace("setReceiverTax1RateAsString", e);
            }
        } else {
            _receiverTax1Rate = receiverTax1Rate;
        }
    }

    public void setReceiverTax1RateAsString(String receiverTax1Rate) {
        try {
            final double conv = Double.parseDouble(receiverTax1Rate);
            setReceiverTax1RateAsString(conv);
        } catch (NumberFormatException e) {
            _receiverTax1Rate = -1;
        }

    }

    public double getReceiverTax2Rate() {
        return _receiverTax2Rate;
    }

    public void setReceiverTax2Rate(double receiverTax2Rate) {
        _receiverTax2Rate = receiverTax2Rate;
    }

    public String getReceiverTax2RateAsString() {
        if (Math.abs(_receiverTax2Rate-(-1))<EPSILON ) {
            return null;
        }
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax2Type)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong( _receiverTax2Rate));
        } else {
            return String.valueOf(_receiverTax2Rate);
        }
    }

    public void setReceiverTax2RateAsString(double receiverTax2Rate) {
        final String METHOD_NAME = "setReceiverTax2RateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax2Type)) {

            try {
                _receiverTax2Rate = PretupsBL.getSystemAmount(receiverTax2Rate);
            } catch (BTSLBaseException e) {
               // _log.errorTrace(METHOD_NAME, e);
//            	log.errorTrace("setReceiverTax2RateAsString", e);
            }
        } else {
            _receiverTax2Rate = receiverTax2Rate;
        }
    }

    public void setReceiverTax2RateAsString(String receiverTax2Rate) {
        try {
            final double conv = Double.parseDouble(receiverTax2Rate);
            setReceiverTax2RateAsString(conv);
        } catch (NumberFormatException e) {
            _receiverTax2Rate = -1;
        }

    }

    public double getSenderTax1Rate() {
        return _senderTax1Rate;
    }

    public void setSenderTax1Rate(double senderTax1Rate) {
        _senderTax1Rate = senderTax1Rate;
    }

    public String getSenderTax1RateAsString() {
        if (Math.abs(_senderTax1Rate-(-1))<EPSILON) {
            return null;
        }
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax1Type)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_senderTax1Rate));
        } else {
            return String.valueOf(_senderTax1Rate);
        }
    }

    public void setSenderTax1RateAsString(double senderTax1Rate) {
        final String METHOD_NAME = "setSenderTax1RateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax1Type)) {
            try {
                _senderTax1Rate = PretupsBL.getSystemAmount(senderTax1Rate);
            } catch (BTSLBaseException e) {
               // _log.errorTrace(METHOD_NAME, e);
//            	log.errorTrace("setSenderTax1RateAsString", e);
            }
        } else {
            _senderTax1Rate = senderTax1Rate;
        }
    }

    public void setSenderTax1RateAsString(String senderTax1Rate) {
        try {
            final double conv = Double.parseDouble(senderTax1Rate);
            setSenderTax1RateAsString(conv);
        } catch (NumberFormatException e) {
            _senderTax1Rate = -1;
        }

    }

    public double getSenderTax2Rate() {
        return _senderTax2Rate;
    }

    public void setSenderTax2Rate(double senderTax2Rate) {
        _senderTax2Rate = senderTax2Rate;
    }

    public String getSenderTax2RateAsString() {
        if (Math.abs(_senderTax1Rate-(-1))<EPSILON) {
            return null;
        }
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax2Type)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_senderTax2Rate));
        } else {
            return String.valueOf(_senderTax2Rate);
        }
    }

    public void setSenderTax2RateAsString(double senderTax2Rate) {
        final String METHOD_NAME = "setSenderTax2RateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax2Type)) {
            try {
                _senderTax2Rate = PretupsBL.getSystemAmount(senderTax2Rate);
            } catch (BTSLBaseException e) {
               // _log.errorTrace(METHOD_NAME, e);
//            	log.errorTrace("setSenderTax2RateAsString", e);
            }
        } else {
            _senderTax2Rate = senderTax2Rate;
        }
    }

    public void setSenderTax2RateAsString(String senderTax2Rate) {
        try {
            final double conv = Double.parseDouble(senderTax2Rate);
            setSenderTax2RateAsString(conv);
        } catch (NumberFormatException e) {
            _senderTax2Rate = -1;
        }

    }

    public double getReceiverAccessFeeRate() {
        return _receiverAccessFeeRate;
    }

    public void setReceiverAccessFeeRate(double receiverAccessFeeRate) {
        _receiverAccessFeeRate = receiverAccessFeeRate;
    }

    public String getReceiverAccessFeeRateAsString() {
        if (Math.abs(_receiverAccessFeeRate-(-1))<EPSILON ) {
            return null;
        }
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverAccessFeeType)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_receiverAccessFeeRate));
        } else {
            return String.valueOf(_receiverAccessFeeRate);
        }
    }

    public void setReceiverAccessFeeRateAsString(double receiverAccessFeeRate) {
        final String METHOD_NAME = "setReceiverAccessFeeRateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverAccessFeeType)) {
            try {
                _receiverAccessFeeRate = PretupsBL.getSystemAmount(receiverAccessFeeRate);
            } catch (BTSLBaseException e) {
               // _log.errorTrace(METHOD_NAME, e);
//            	log.errorTrace("setReceiverAccessFeeRateAsString", e);
            }
        } else {
            _receiverAccessFeeRate = receiverAccessFeeRate;
        }
    }

    public void setReceiverAccessFeeRateAsString(String receiverAccessFeeRate) {
        try {
            final double conv = Double.parseDouble(receiverAccessFeeRate);
            setReceiverAccessFeeRateAsString(conv);
        } catch (NumberFormatException e) {
            _receiverAccessFeeRate = -1;
        }

    }

    public double getSenderAccessFeeRate() {
        return _senderAccessFeeRate;
    }

    public void setSenderAccessFeeRate(double senderAccessFeeRate) {
        _senderAccessFeeRate = senderAccessFeeRate;
    }

    public String getSenderAccessFeeRateAsString() {
        if (Math.abs(_senderAccessFeeRate-(-1))<EPSILON) {
            return null;
        }
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderAccessFeeType)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_senderAccessFeeRate));
        } else {
            return String.valueOf(_senderAccessFeeRate);
        }
    }

    public void setSenderAccessFeeRateAsString(double senderAccessFeeRate) {
        final String METHOD_NAME = "setSenderAccessFeeRateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderAccessFeeType)) {
            try {
                _senderAccessFeeRate = PretupsBL.getSystemAmount(senderAccessFeeRate);
            } catch (BTSLBaseException e) {
               // _log.errorTrace(METHOD_NAME, e);
//            	log.errorTrace("setSenderAccessFeeRateAsString", e);
            }
        } else {
            _senderAccessFeeRate = senderAccessFeeRate;
        }
    }

    public void setSenderAccessFeeRateAsString(String senderAccessFeeRate) {
        final String METHOD_NAME = "setSenderAccessFeeRateAsString";
        try {
            final double conv = Double.parseDouble(senderAccessFeeRate);
            setSenderAccessFeeRateAsString(conv);
        } catch (NumberFormatException e) {
            _senderAccessFeeRate = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }

    }

    /**
     * @return Returns the multipleOff.
     */
    public long getMultipleOf() {
        return _multipleOf;
    }

    /**
     * @param multipleOff
     *            The multipleOff to set.
     */
    public void setMultipleOf(long multipleOff) {
        _multipleOf = multipleOff;
    }

    public String getMultipleOfAsString() {
        if (_multipleOf == -1) {
            return null;
        } else {
            return PretupsBL.getDisplayAmount(_multipleOf);
        }
    }

    public void setMultipleOfAsString(String multipleOf) {
        final String METHOD_NAME = "setMultipleOfAsString";
        try {
            _multipleOf = PretupsBL.getSystemAmount(multipleOf);
        } catch (BTSLBaseException e) {
            _multipleOf = -1;
           // _log.errorTrace(METHOD_NAME, e);
        }// if _multipleOf is blank then we put it as -1 to recongnise in
         // getter.
    }

    /**
     * @return Returns the cardGroupSetName.
     */
    public String getCardGroupSetName() {
        return _cardGroupSetName;
    }

    /**
     * @param cardGroupSetName
     *            The cardGroupSetName to set.
     */
    public void setCardGroupSetName(String cardGroupSetName) {
        _cardGroupSetName = cardGroupSetName;
    }

    /**
     * @return Returns the cardGroupSubServiceId.
     */
    public String getCardGroupSubServiceId() {
        return _cardGroupSubServiceId;
    }

    /**
     * @param cardGroupSubServiceId
     *            The cardGroupSubServiceId to set.
     */
    public void setCardGroupSubServiceId(String cardGroupSubServiceId) {
        _cardGroupSubServiceId = cardGroupSubServiceId;
    }

    /**
     * @return Returns the cardGroupSubServiceIdDesc.
     */
    public String getCardGroupSubServiceIdDesc() {
        return _cardGroupSubServiceIdDesc;
    }

    /**
     * @param cardGroupSubServiceIdDesc
     *            The cardGroupSubServiceIdDesc to set.
     */
    public void setCardGroupSubServiceIdDesc(String cardGroupSubServiceIdDesc) {
        _cardGroupSubServiceIdDesc = cardGroupSubServiceIdDesc;
    }

    /**
     * @return Returns the bonusTalkTimeValue.
     */
    public long getBonusTalkTimeValue() {
        return _bonusTalkTimeValue;
    }

    /**
     * @param bonusTalkTimeValue
     *            The bonusTalkTimeValue to set.
     */
    public void setBonusTalkTimeValue(long bonusTalkTimeValue) {
        _bonusTalkTimeValue = bonusTalkTimeValue;
    }

    /**
     * @return Returns the transferValue.
     */
    public long getTransferValue() {
        return _transferValue;
    }

    /**
     * @param transferValue
     *            The transferValue to set.
     */
    public void setTransferValue(long transferValue) {
        _transferValue = transferValue;
    }

    /**
     * @return Returns the serviceTypeDesc.
     */
    public String getServiceTypeDesc() {
        return _serviceTypeDesc;
    }

    /**
     * @param serviceTypeDesc
     *            The serviceTypeDesc to set.
     */
    public void setServiceTypeDesc(String serviceTypeDesc) {
        _serviceTypeDesc = serviceTypeDesc;
    }

    /**
     * @return Returns the serviceTypeId.
     */
    public String getServiceTypeId() {
        return _serviceTypeId;
    }

    /**
     * @param serviceTypeId
     *            The serviceTypeId to set.
     */
    public void setServiceTypeId(String serviceTypeId) {
        _serviceTypeId = serviceTypeId;
    }

    /**
     * @return Returns the setType.
     */
    public String getSetType() {
        return _setType;
    }

    /**
     * @param setType
     *            The setType to set.
     */
    public void setSetType(String setType) {
        _setType = setType;
    }

    /**
     * @return Returns the setTypeName.
     */
    public String getSetTypeName() {
        return _setTypeName;
    }

    /**
     * @param setTypeName
     *            The setTypeName to set.
     */
    public void setSetTypeName(String setTypeName) {
        _setTypeName = setTypeName;
    }

    /**
     * @return Returns the _status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param _status
     *            The _status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the bonusTalktimevalidity.
     */
    public long getBonusValidityValue() {
        return _bonusValidityValue;
    }

    /**
     * @param bonusTalktimevalidity
     *            The bonusTalktimevalidity to set.
     */
    public void setBonusValidityValue(long bonusTalktimevalidity) {
        _bonusValidityValue = bonusTalktimevalidity;
    }

    /**
     * @return Returns the online.
     */
    public String getOnline() {
        return _online;
    }

    /**
     * @param online
     *            The online to set.
     */
    public void setOnline(String online) {
        _online = online;
    }

    /**
     * @return Returns the combine.
     */
    public String getBoth() {
        return _both;
    }

    /**
     * @param combine
     *            The combine to set.
     */
    public void setBoth(String both) {
        _both = both;
    }

    /**
     * @return Returns the networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     *            The networkCode to set.
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return Returns the applicableFrom.
     */
    public Date getApplicableFrom() {
        return _applicableFrom;
    }

    /**
     * @param applicableFrom
     *            The applicableFrom to set.
     */
    public void setApplicableFrom(Date applicableFrom) {
        _applicableFrom = applicableFrom;
    }

    public String getApplicableFromAsString() {
        if (_applicableFrom != null) {
            try {
                return BTSLUtil.getDateTimeStringFromDate(_applicableFrom);
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * @return Returns the oldApplicableFrom.
     */
    public long getOldApplicableFrom() {
        return _oldApplicableFrom;
    }

    /**
     * @param oldApplicableFrom
     *            The oldApplicableFrom to set.
     */
    public void setOldApplicableFrom(long oldApplicableFrom) {
        _oldApplicableFrom = oldApplicableFrom;
    }

    /**
     * @return Returns the lastVersion.
     */
    public String getLastVersion() {
        return _LastVersion;
    }

    /**
     * @param lastVersion
     *            The lastVersion to set.
     */
    public void setLastVersion(String lastVersion) {
        _LastVersion = lastVersion;
    }

    /**
     * @return Returns the receiverConvFactor.
     */
    public String getReceiverConvFactor() {
        return _receiverConvFactor;
    }

    /**
     * @param receiverConvFactor
     *            The receiverConvFactor to set.
     */
    public void setReceiverConvFactor(String receiverConvFactor) {
        _receiverConvFactor = receiverConvFactor;
    }

    /**
     * @return Returns the _bonusAccList.
     */
    public ArrayList getBonusAccList() {
        return _bonusAccList;
    }

    /**
     * @param accList
     *            The _bonusAccList to set.
     */
    public void setBonusAccList(ArrayList accList) {
        _bonusAccList = accList;
    }

    /**
     * @return Returns the senderConvFactor.
     */
    public String getSenderConvFactor() {
        return _senderConvFactor;
    }

    /**
     * @param senderConvFactor
     *            The senderConvFactor to set.
     */
    public void setSenderConvFactor(String senderConvFactor) {
        _senderConvFactor = senderConvFactor;
    }

    /**
     * @return Returns the bonusTalkTimeRate.
     */
    public double getBonusTalkTimeRate() {
        return _bonusTalkTimeRate;
    }

    /**
     * @param bonusTalkTimeRate
     *            The bonusTalkTimeRate to set.
     */
    public void setBonusTalkTimeRate(double bonusTalkTimeRate) {
        _bonusTalkTimeRate = bonusTalkTimeRate;
    }

    /**
     * @return Returns the bonusTalkTimeType.
     */
    public String getBonusTalkTimeType() {
        return _bonusTalkTimeType;
    }

    /**
     * @param bonusTalkTimeType
     *            The bonusTalkTimeType to set.
     */
    public void setBonusTalkTimeType(String bonusTalkTimeType) {
        _bonusTalkTimeType = bonusTalkTimeType;
    }

    /**
     * @return Returns the bonusTalkTimeConvFactor.
     */
    public double getBonusTalkTimeConvFactor() {
        return _bonusTalkTimeConvFactor;
    }

    /**
     * @param bonusTalkTimeConvFactor
     *            The bonusTalkTimeConvFactor to set.
     */
    public void setBonusTalkTimeConvFactor(double bonusTalkTimeConvFactor) {
        _bonusTalkTimeConvFactor = bonusTalkTimeConvFactor;
    }

    /**
     * @return Returns the bonusBundleType.
     */
    public String getBonusTalkTimeBundleType() {
        return _bonusTalkTimeBundleType;
    }

    /**
     * @param bonusBundleType
     *            The bonusBundleType to set.
     */
    public void setBonusTalkTimeBundleType(String bonusBundleType) {
        _bonusTalkTimeBundleType = bonusBundleType;
    }

    /**
     * @return Returns the bonusTalkTimeValidity.
     */
    public String getBonusTalkTimeValidity() {
        return _bonusTalkTimeValidity;
    }

    /**
     * @param bonusTalkTimeValidity
     *            The bonusTalkTimeValidity to set.
     */
    public void setBonusTalkTimeValidity(String bonusTalkTimeValidity) {
        _bonusTalkTimeValidity = bonusTalkTimeValidity;
    }

    /**
     * @return Returns the serviceTypeSelector.
     */
    public String getServiceTypeSelector() {
        return _serviceTypeSelector;
    }

    /**
     * @param serviceTypeSelector
     *            The serviceTypeSelector to set.
     */
    public void setServiceTypeSelector(String serviceTypeSelector) {
        _serviceTypeSelector = serviceTypeSelector;
    }

    /**
     * @return Returns the bonus1.
     */
    public double getBonus1() {
        return _bonus1;
    }

    /**
     * @param bonus1
     *            The bonus1 to set.
     */
    public void setBonus1(double bonus1) {
        _bonus1 = bonus1;
    }

    /**
     * @return Returns the bonus2.
     */
    public double getBonus2() {
        return _bonus2;
    }

    /**
     * @param Bonus2
     *            The bonus2 to set.
     */
    public void setBonus2(double bonus2) {
        _bonus2 = bonus2;
    }

    public long getBonus1validity() {
        return _bonus1validity;
    }

    /**
     * @param bonus1validity
     *            The bonus1validity to set.
     */
    public void setBonus1validity(long bonus1validity) {
        _bonus1validity = bonus1validity;
    }

    /**
     * @return Returns the bonus2validity.
     */
    public long getBonus2validity() {
        return _bonus2validity;
    }

    /**
     * @param bonus2validity
     *            The bonus2validity to set.
     */
    public void setBonus2validity(long bonus2validity) {
        _bonus2validity = bonus2validity;
    }

    /**
     * @return Returns the bonusTalktimevalidity.
     */
    public long getBonusTalktimevalidity() {
        return _bonusTalktimevalidity;
    }

    /**
     * @param bonusTalktimevalidity
     *            The bonusTalktimevalidity to set.
     */
    public void setBonusTalktimevalidity(long bonusTalktimevalidity) {
        _bonusTalktimevalidity = bonusTalktimevalidity;
    }

    /**
     * @return the _cosRequired
     */
    public String getCosRequired() {
        return _cosRequired;
    }

    /**
     * @param required
     *            the _cosRequired to set
     */
    public void setCosRequired(String required) {
        _cosRequired = required;
    }

    /**
     * @return the _inPromo
     */
    public double getInPromo() {
        return _inPromo;
    }

    /**
     * @param promo
     *            the _inPromo to set
     */
    public void setInPromo(double promo) {
        _inPromo = promo;
    }

    public void setInPromoAsString(double promo) {
        final String METHOD_NAME = "setInPromoAsString";
        try {
            _inPromo = PretupsBL.getSystemAmount(promo);
        } catch (BTSLBaseException e) {
           // _log.errorTrace(METHOD_NAME, e);
//        	log.errorTrace("setInPromoAsString", e);
        }
    }

    public void setInPromoAsString(String promo) {
        try {
            final double conv = Double.parseDouble(promo);
            setInPromoAsString(conv);
        } catch (NumberFormatException e) {
            _inPromo = -1;
        }

    }

    public String getInPromoAsString() {
        if (Math.abs(_inPromo-(-1))<EPSILON ) {
            return null;
        } else {
            // return PretupsBL.getDisplayAmount((long) _inPromo);
            return PretupsBL.getDisplayAmount( BTSLUtil.parseDoubleToLong(_inPromo));
        }

    }
    public String getReceiverTax3Name() {
        return _receiverTax3Name;
}
public void setReceiverTax3Name(String tax3Name) {
        _receiverTax3Name = tax3Name;
}
public String getReceiverTax3Type() {
        return _receiverTax3Type;
}
public void setReceiverTax3Type(String tax3Type) {
        _receiverTax3Type = tax3Type;
}
public double getReceiverTax3Rate() {
        return _receiverTax3Rate;
}
public void setReceiverTax3Rate(double tax3Rate) {
        _receiverTax3Rate = tax3Rate;
}
public String getReceiverTax4Name() {
        return _receiverTax4Name;
}
public void setReceiverTax4Name(String tax4Name) {
        _receiverTax4Name = tax4Name;
}
public String getReceiverTax4Type() {
        return _receiverTax4Type;
}
public void setReceiverTax4Type(String tax4Type) {
        _receiverTax4Type = tax4Type;
}
public double getReceiverTax4Rate() {
        return _receiverTax4Rate;
}
public void setReceiverTax4Rate(double tax4Rate) {
        _receiverTax4Rate = tax4Rate;
}
public String getReceiverTax3RateAsString()
{
        if(Math.abs(_receiverTax3Rate-(-1))<EPSILON)
                return null;
    if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax3Type))
        return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_receiverTax3Rate));
    else
    {
        return String.valueOf(_receiverTax3Rate);
    }
}
public String getReceiverTax4RateAsString()
{
        if(Math.abs(_receiverTax4Rate-(-1))<EPSILON)
                return null;
    if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax4Type))
        return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_receiverTax4Rate));
    else
    {
        return String.valueOf(_receiverTax4Rate);
    }
}
public void setReceiverTax3RateAsString(double receiverTax3Rate)
{
        final String METHOD_NAME="setReceiverTax3RateAsString";
    if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax3Type))
    {
        try {
        _receiverTax3Rate = PretupsBL.getSystemAmount(receiverTax3Rate);
    } catch (BTSLBaseException e) {
       // _log.errorTrace("METHOD_NAME",e);
//    	log.errorTrace("setReceiverTax3RateAsString", e);
    }
    }
    else
        _receiverTax3Rate = receiverTax3Rate;
}
public void setReceiverTax3RateAsString(String receiverTax3Rate)
{
    try{
        double conv= Double.parseDouble(receiverTax3Rate);
        setReceiverTax3RateAsString(conv);
    }catch (NumberFormatException e) {_receiverTax3Rate=-1;}
}
public void setReceiverTax4RateAsString(double receiverTax4Rate)
{
    final String METHOD_NAME="setReceiverTax4RateAsString";
    if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax4Type))
    {
        try {
        _receiverTax4Rate = PretupsBL.getSystemAmount(receiverTax4Rate);
    } catch (BTSLBaseException e) {
       // _log.errorTrace(METHOD_NAME,e);
//    	log.errorTrace("setReceiverTax4RateAsString", e);
    }
    }
    else
        _receiverTax4Rate = receiverTax4Rate;
}
public void setReceiverTax4RateAsString(String receiverTax4Rate)
{
    try{
        double conv= Double.parseDouble(receiverTax4Rate);
        setReceiverTax4RateAsString(conv);
    }catch (NumberFormatException e) {_receiverTax4Rate=-1;}

}

public String getCardGroupType() {
	return cardGroupType;
}

public void setCardGroupType(String cardGroupType) {
	this.cardGroupType = cardGroupType;
}

public String getVoucherDenomination() {
	return voucherDenomination;
}

public void setVoucherDenomination(String voucherDenomination) {
	this.voucherDenomination = voucherDenomination;
}

public String getVoucherTypeDesc() {
	return voucherTypeDesc;
}

public void setVoucherTypeDesc(String voucherTypeDesc) {
	this.voucherTypeDesc = voucherTypeDesc;
}

public String getVoucherSegmentDesc() {
	return voucherSegmentDesc;
}

public void setVoucherSegmentDesc(String voucherSegmentDesc) {
	this.voucherSegmentDesc = voucherSegmentDesc;
}

public String getProductName() {
	return productName;
}

public void setProductName(String productName) {
	this.productName = productName;
}
}


