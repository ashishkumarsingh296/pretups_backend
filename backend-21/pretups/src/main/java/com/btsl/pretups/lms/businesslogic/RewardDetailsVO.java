package com.btsl.pretups.lms.businesslogic;

import java.io.Serializable;

public class RewardDetailsVO implements Serializable {
    private String _commProfileDetailID;
    private String _commProfileProductsID;
    private long _startRange;
    private String _startRangeAsString = null;
    private long _endRange;
    private String _endRangeAsString = null;
    private String _commType;
    private double _commRate;
    private String _commRateAsString = "0";
    private String _tax1Type;
    private double _tax1Rate;
    private String _tax1RateAsString = "0";
    private String _tax2Type;
    private double _tax2Rate;
    private String _tax2RateAsString = "0";
    private String _tax3Type;
    private double _tax3Rate;
    private String _tax3RateAsString = "0";
    private String _rewardedTo = null;

    private int rowIndex;
    private static final long serialVersionUID = 1L;
    public RewardDetailsVO() {
    };

    public RewardDetailsVO(RewardDetailsVO rewardDetailsVO) {
        this._commProfileDetailID = rewardDetailsVO._commProfileDetailID;
        this._commProfileProductsID = rewardDetailsVO._commProfileProductsID;
        this._startRange = rewardDetailsVO._startRange;
        this._startRangeAsString = rewardDetailsVO._startRangeAsString;
        this._endRange = rewardDetailsVO._endRange;
        this._endRangeAsString = rewardDetailsVO._endRangeAsString;
        this._commType = rewardDetailsVO._commType;
        this._commRate = rewardDetailsVO._commRate;
        this._commRateAsString = rewardDetailsVO._commRateAsString;
        this._tax1Type = rewardDetailsVO._tax1Type;
        this._tax1Rate = rewardDetailsVO._tax1Rate;
        this._tax1RateAsString = rewardDetailsVO._tax1RateAsString;
        this._tax2Type = rewardDetailsVO._tax2Type;
        this._tax2Rate = rewardDetailsVO._tax2Rate;
        this._tax2RateAsString = rewardDetailsVO._tax2RateAsString;
        this._tax3Type = rewardDetailsVO._tax3Type;
        this._tax3Rate = rewardDetailsVO._tax3Rate;
        this._tax3RateAsString = rewardDetailsVO._tax3RateAsString;

        this.rowIndex = rewardDetailsVO.rowIndex;
    }

    /**
     * @return Returns the rowIndex.
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *            The rowIndex to set.
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
@Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RewardDetailsVO Data ");

        sb.append("_commProfileDetailID=" + _commProfileDetailID + ",");
        sb.append("_commProfileProductsID=" + _commProfileProductsID + ",");
        sb.append("_startRange=" + _startRange + ",");
        sb.append("_endRange=" + _endRange + ",");
        sb.append("_commType=" + _commType + ",");
        sb.append("_commRate=" + _commRate + ",");
        sb.append("_tax1Type=" + _tax1Type + ",");
        sb.append("_tax1Rate=" + _tax1Rate + ",");
        sb.append("_tax2Type=" + _tax2Type + ",");
        sb.append("_tax2Rate=" + _tax2Rate + ",");
        sb.append("_tax3Type=" + _tax3Type + ",");
        sb.append("_tax3Rate=" + _tax3Rate + ",");

        return sb.toString();
    }

    public double getCommRate() {
        return _commRate;
    }

    public void setCommRate(double commRate) {
        _commRate = commRate;
    }

    public String getCommRateAsString() {
        return _commRateAsString;
    }

    public void setCommRateAsString(String commRateAsString) {
        if (commRateAsString != null) {
            _commRateAsString = commRateAsString.trim();
        }
    }

    public String getCommType() {
        return _commType;
    }

    public void setCommType(String commType) {
        _commType = commType;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long endRange) {
        _endRange = endRange;
    }

    public String getEndRangeAsString() {
        return _endRangeAsString;
    }

    public void setEndRangeAsString(String endRangeAsString) {
        if (endRangeAsString != null) {
            _endRangeAsString = endRangeAsString.trim();
        }
    }

    public long getStartRange() {
        return _startRange;
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public String getStartRangeAsString() {
        return _startRangeAsString;
    }

    public void setStartRangeAsString(String startRangeAsString) {
        if (startRangeAsString != null) {
            _startRangeAsString = startRangeAsString.trim();
        }
    }

    public double getTax1Rate() {
        return _tax1Rate;
    }

    public void setTax1Rate(double tax1Rate) {
        _tax1Rate = tax1Rate;
    }

    public String getTax1RateAsString() {
        return _tax1RateAsString;
    }

    public void setTax1RateAsString(String tax1RateAsString) {
        if (tax1RateAsString != null) {
            _tax1RateAsString = tax1RateAsString.trim();
        }
    }

    public String getTax1Type() {
        return _tax1Type;
    }

    public void setTax1Type(String tax1Type) {
        _tax1Type = tax1Type;
    }

    public double getTax2Rate() {
        return _tax2Rate;
    }

    public void setTax2Rate(double tax2Rate) {
        _tax2Rate = tax2Rate;
    }

    public String getTax2RateAsString() {
        return _tax2RateAsString;
    }

    public void setTax2RateAsString(String tax2RateAsString) {
        if (tax2RateAsString != null) {
            _tax2RateAsString = tax2RateAsString.trim();
        }
    }

    public String getTax2Type() {
        return _tax2Type;
    }

    public void setTax2Type(String tax2Type) {
        _tax2Type = tax2Type;
    }

    public double getTax3Rate() {
        return _tax3Rate;
    }

    public void setTax3Rate(double tax3Rate) {
        _tax3Rate = tax3Rate;
    }

    public String getTax3RateAsString() {
        return _tax3RateAsString;
    }

    public void setTax3RateAsString(String tax3RateAsString) {
        if (tax3RateAsString != null) {
            _tax3RateAsString = tax3RateAsString.trim();
        }
    }

    public String getTax3Type() {
        return _tax3Type;
    }

    public void setTax3Type(String tax3Type) {
        _tax3Type = tax3Type;
    }

    public String getCommProfileDetailID() {
        return _commProfileDetailID;
    }

    /**
     * @param commProfileDetailID
     *            The commProfileDetailID to set.
     */
    public void setCommProfileDetailID(String commProfileDetailID) {
        _commProfileDetailID = commProfileDetailID;
    }

    /**
     * @return Returns the commProfileProductsID.
     */
    public String getCommProfileProductsID() {
        return _commProfileProductsID;
    }

    /**
     * @param commProfileProductsID
     *            The commProfileProductsID to set.
     */
    public void setCommProfileProductsID(String commProfileProductsID) {
        _commProfileProductsID = commProfileProductsID;
    }

    public String getRewardedTo() {
        return _rewardedTo;
    }

    public void setRewardedTo(String rewardedTo) {
        _rewardedTo = rewardedTo;
    }
}
