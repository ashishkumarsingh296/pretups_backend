package com.selftopup.pretups.cardgroup.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.ServiceClassDAO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;

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
    String[] _cardGroupList = null;
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
    private ArrayList _bonusAccList = null;
    private String _serviceTypeSelector = null;

    private double _bonus1;
    private double _bonus2;

    private long _bonus1validity;
    private long _bonus2validity;
    private long _bonusTalktimevalidity;
    // added for cos
    private String _cosRequired;
    private Log _log = LogFactory.getFactory().getInstance(CardGroupDetailsVO.class.getName());
    private double _inPromo;

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
        if (_endRange == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_endRange);
    }

    public void setEndRangeAsString(String endRange) {
        try {
            _endRange = PretupsBL.getSystemAmount(endRange);
        } catch (BTSLBaseException e) {
            _endRange = -1;
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
        if (_maxReceiverAccessFee == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_maxReceiverAccessFee);
    }

    public void setMaxReceiverAccessFeeAsString(String maxReceiverAccessFee) {
        try {
            _maxReceiverAccessFee = PretupsBL.getSystemAmount(maxReceiverAccessFee);
        } catch (BTSLBaseException e) {
            _maxReceiverAccessFee = -1;
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
        if (_maxSenderAccessFee == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_maxSenderAccessFee);
    }

    public void setMaxSenderAccessFeeAsString(String maxSenderAccessFee) {
        try {
            _maxSenderAccessFee = PretupsBL.getSystemAmount(maxSenderAccessFee);
        } catch (BTSLBaseException e) {
            _maxSenderAccessFee = -1;
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
        if (_minReceiverAccessFee == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_minReceiverAccessFee);
    }

    public void setMinReceiverAccessFeeAsString(String minReceiverAccessFee) {
        try {
            _minReceiverAccessFee = PretupsBL.getSystemAmount(minReceiverAccessFee);
        } catch (BTSLBaseException e) {
            _minReceiverAccessFee = -1;
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
        if (_minSenderAccessFee == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_minSenderAccessFee);
    }

    public void setMinSenderAccessFeeAsString(String minSenderAccessFee) {
        try {
            _minSenderAccessFee = PretupsBL.getSystemAmount(minSenderAccessFee);
        } catch (BTSLBaseException e) {
            _minSenderAccessFee = -1;
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
        if (_startRange == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_startRange);
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public void setStartRangeAsString(String startRange) {
        try {
            _startRange = PretupsBL.getSystemAmount(startRange);
        } catch (BTSLBaseException e) {
            _startRange = -1;
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
        if (_validityPeriod > 0)
            return String.valueOf(_validityPeriod);
        else
            return "";
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

    /**
     * Returns a comma delimited list of the name/value pairs.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("CardGroupDetailsVO ");

        sb.append("cardGroupSetID=" + _cardGroupSetID + ",");
        sb.append("version=" + _version + ",");
        sb.append("cardGroupID=" + _cardGroupID + ",");
        sb.append("cardGroupCode=" + _cardGroupCode + ",");
        sb.append("startRange=" + _startRange + ",");
        sb.append("endRange=" + _endRange + ",");
        sb.append("validityPeriodType=" + _validityPeriodType + ",");
        sb.append("validityPeriod=" + _validityPeriod + ",");
        sb.append("gracePeriod=" + _gracePeriod + ",");
        sb.append("multipleOff=" + _multipleOf + ",");
        sb.append("senderTax1Name=" + _senderTax1Name + ",");
        sb.append("senderTax1Type=" + _senderTax1Type + ",");
        sb.append("senderTax1Rate=" + _senderTax1Rate + ",");
        sb.append("senderTax2Name=" + _senderTax2Name + ",");
        sb.append("senderTax2Type=" + _senderTax2Type + ",");
        sb.append("senderTax2Rate=" + _senderTax2Rate + ",");
        sb.append("receiverTax1Name=" + _receiverTax1Name + ",");
        sb.append("receiverTax1Type=" + _receiverTax1Type + ",");
        sb.append("receiverTax1Rate=" + _receiverTax1Rate + ",");
        sb.append("receiverTax2Name=" + _receiverTax2Name + ",");
        sb.append("receiverTax2Type=" + _receiverTax2Type + ",");
        sb.append("receiverTax2Rate=" + _receiverTax2Rate + ",");
        sb.append("senderAccessFeeType=" + _senderAccessFeeType + ",");
        sb.append("senderAccessFeeRate=" + _senderAccessFeeRate + ",");
        sb.append("minSenderAccessFee=" + _minSenderAccessFee + ",");
        sb.append("maxSenderAccessFee=" + _maxSenderAccessFee + ",");
        sb.append("receiverAccessFeeType=" + _receiverAccessFeeType + ",");
        sb.append("receiverAccessFeeRate=" + _receiverAccessFeeRate + ",");
        sb.append("minReceiverAccessFee=" + _minReceiverAccessFee + ",");
        sb.append("maxReceiverAccessFee=" + _maxReceiverAccessFee + ",");
        sb.append("_serviceTypeId=" + _serviceTypeId + ",");
        sb.append("_onLine=" + _online + ",");
        sb.append("_both=" + _both + ",");
        sb.append("_senderConvFactor=" + _senderConvFactor + ",");
        sb.append("_receiverConvFactor=" + _receiverConvFactor + ",");
        sb.append("_serviceTypeSelector=" + _serviceTypeSelector + ",");
        sb.append("_bonusAccList=" + _bonusAccList + ",");

        return sb.toString();
    }

    public double getReceiverTax1Rate() {
        return _receiverTax1Rate;
    }

    public void setReceiverTax1Rate(double receiverTax1Rate) {
        _receiverTax1Rate = receiverTax1Rate;
    }

    public String getReceiverTax1RateAsString() {
        if (_receiverTax1Rate == -1)
            return null;
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax1Type))
            return PretupsBL.getDisplayAmount((long) _receiverTax1Rate);
        else {
            return String.valueOf(_receiverTax1Rate);
        }
    }

    public void setReceiverTax1RateAsString(double receiverTax1Rate) {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax1Type)) {
            try {
                _receiverTax1Rate = PretupsBL.getSystemAmount(receiverTax1Rate);
            } catch (BTSLBaseException e) {
            	 _log.error("setReceiverTax1RateAsString", "Exception " + e.getMessage());
            }
        } else
            _receiverTax1Rate = receiverTax1Rate;
    }

    public void setReceiverTax1RateAsString(String receiverTax1Rate) {
        try {
            double conv = Double.parseDouble(receiverTax1Rate);
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
        if (_receiverTax2Rate == -1)
            return null;
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax2Type))
            return PretupsBL.getDisplayAmount((long) _receiverTax2Rate);
        else {
            return String.valueOf(_receiverTax2Rate);
        }
    }

    public void setReceiverTax2RateAsString(double receiverTax2Rate) {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverTax2Type)) {
            try {
                _receiverTax2Rate = PretupsBL.getSystemAmount(receiverTax2Rate);
            } catch (BTSLBaseException e) {
            	 _log.error("setReceiverTax2RateAsString", "Exception " + e.getMessage());
            }
        } else
            _receiverTax2Rate = receiverTax2Rate;
    }

    public void setReceiverTax2RateAsString(String receiverTax2Rate) {
        try {
            double conv = Double.parseDouble(receiverTax2Rate);
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
        if (_senderTax1Rate == -1)
            return null;
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax1Type))
            return PretupsBL.getDisplayAmount((long) _senderTax1Rate);
        else {
            return String.valueOf(_senderTax1Rate);
        }
    }

    public void setSenderTax1RateAsString(double senderTax1Rate) {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax1Type)) {
            try {
                _senderTax1Rate = PretupsBL.getSystemAmount(senderTax1Rate);
            } catch (BTSLBaseException e) {
            	_log.error("setSenderTax1RateAsString", "Exception " + e.getMessage());
            }
        } else
            _senderTax1Rate = senderTax1Rate;
    }

    public void setSenderTax1RateAsString(String senderTax1Rate) {
        try {
            double conv = Double.parseDouble(senderTax1Rate);
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
        if (_senderTax2Rate == -1)
            return null;
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax2Type))
            return PretupsBL.getDisplayAmount((long) _senderTax2Rate);
        else
            return String.valueOf(_senderTax2Rate);
    }

    public void setSenderTax2RateAsString(double senderTax2Rate) {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderTax2Type)) {
            try {
                _senderTax2Rate = PretupsBL.getSystemAmount(senderTax2Rate);
            } catch (BTSLBaseException e) {
            	_log.error("setSenderTax2RateAsString", "Exception " + e.getMessage());
            }
        } else
            _senderTax2Rate = senderTax2Rate;
    }

    public void setSenderTax2RateAsString(String senderTax2Rate) {
        try {
            double conv = Double.parseDouble(senderTax2Rate);
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
        if (_receiverAccessFeeRate == -1)
            return null;
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverAccessFeeType))
            return PretupsBL.getDisplayAmount((long) _receiverAccessFeeRate);
        else
            return String.valueOf(_receiverAccessFeeRate);
    }

    public void setReceiverAccessFeeRateAsString(double receiverAccessFeeRate) {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_receiverAccessFeeType)) {
            try {
                _receiverAccessFeeRate = PretupsBL.getSystemAmount(receiverAccessFeeRate);
            } catch (BTSLBaseException e) {
            	_log.error("setReceiverAccessFeeRateAsString", "Exception " + e.getMessage());
            }
        } else
            _receiverAccessFeeRate = receiverAccessFeeRate;
    }

    public void setReceiverAccessFeeRateAsString(String receiverAccessFeeRate) {
        try {
            double conv = Double.parseDouble(receiverAccessFeeRate);
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
        if (_senderAccessFeeRate == -1)
            return null;
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderAccessFeeType))
            return PretupsBL.getDisplayAmount((long) _senderAccessFeeRate);
        else
            return String.valueOf(_senderAccessFeeRate);
    }

    public void setSenderAccessFeeRateAsString(double senderAccessFeeRate) {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_senderAccessFeeType)) {
            try {
                _senderAccessFeeRate = PretupsBL.getSystemAmount(senderAccessFeeRate);
            } catch (BTSLBaseException e) {
            	_log.error("setSenderAccessFeeRateAsString", "Exception " + e.getMessage());
            	            }
        } else
            _senderAccessFeeRate = senderAccessFeeRate;
    }

    public void setSenderAccessFeeRateAsString(String senderAccessFeeRate) {
        try {
            double conv = Double.parseDouble(senderAccessFeeRate);
            setSenderAccessFeeRateAsString(conv);
        } catch (NumberFormatException e) {
            _senderAccessFeeRate = -1;
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
        if (_multipleOf == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount(_multipleOf);
    }

    public void setMultipleOfAsString(String multipleOf) {
        try {
            _multipleOf = PretupsBL.getSystemAmount(multipleOf);
        } catch (BTSLBaseException e) {
            _multipleOf = -1;
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
        if (_applicableFrom != null)
            try {
                return BTSLUtil.getDateTimeStringFromDate(_applicableFrom);
            } catch (ParseException e) {
                return "";
            }
        else
            return "";
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
        try {
            _inPromo = PretupsBL.getSystemAmount(promo);
        } catch (BTSLBaseException e) {
        	  _log.error("setInPromoAsString", "Exception " + e.getMessage());
        }
    }

    public void setInPromoAsString(String promo) {
        try {
            double conv = Double.parseDouble(promo);
            setInPromoAsString(conv);
        } catch (NumberFormatException e) {
            _inPromo = -1;
        }

    }

    public String getInPromoAsString() {
        if (_inPromo == -1)
            return null;
        else
            return PretupsBL.getDisplayAmount((long) _inPromo);

    }
}
