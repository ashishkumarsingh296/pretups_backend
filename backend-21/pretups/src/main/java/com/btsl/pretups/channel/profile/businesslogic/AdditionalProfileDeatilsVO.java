package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

/**
 * @(#)AdditionalProfileDeatilsVO.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 * 
 *                                     <description>
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Mohit.Goel Aug 27, 2005 Initital Creation
 *                                     Samna Soin Oct 19, 2011 Modified
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 * 
 */

public class AdditionalProfileDeatilsVO implements Serializable {
    private String _addCommProfileDetailID;
    private String _commProfileServiceTypeID;
    private long _startRange;
    private String _startRangeAsString = null;
    private long _endRange;
    private String _endRangeAsString = null;
    private String _addCommType;
    private double _addCommRate;
    private String _addCommRateAsString = "0";
    private String _addRoamCommType;
    private double _addRoamCommRate;
    private String _addRoamCommRateAsString = "0";
    private double _diffrentialFactor;
    private String _diffrentialFactorAsString = "1";
    private String _tax1Type;
    private double _tax1Rate;
    private String _tax1RateAsString = "0";
    private String _tax2Type;
    private double _tax2Rate;
    private String _tax2RateAsString = "0";

    private int rowIndex;
	private String _addOwnerCommRateAsString ="0";
	private String _addOwnerCommType;
	private double _addOwnerCommRate;
	private String _ownerTax1Type;
	private double _ownerTax1Rate;
	private String _ownerTax1RateAsString = "0";
	private String _ownerTax2Type;
	private double _ownerTax2Rate;
	private String _ownerTax2RateAsString = "0";

    private String _addtnlComStatus;
    private String _addtnlComStatusName;
    // added for batch modify commission profile
    private long _minTransferValue;
    private String _minTrasferValueAsString;
    private long _maxTransferValue;
    private String _maxTransferValueAsString;
    private String ServiceType;
    private String _profileName;
    private String _setVersion;
    private String _setID;
    private Timestamp _applicableFrom;
    private String _serviceID;
    private String _shortCode;
    private String _subServiceCode;
    private String _gatewayCode;
    private String _additionalCommissionTimeSlab;
    private String _applicableFromAdditional;
    private String _applicableToAdditional;
    private String otfApplicableFromStr;
    private String otfApplicableToStr;
    private Date otfApplicableFrom;
    private Date otfApplicableTo;
    private String otfType;
    private List<OTFDetailsVO> otfDetails;
    private int otfDetailsSize;
    private String otfTimeSlab;
    private String otfValueAsString;
	private String otfRateAsString;
	private String otfTypePctOrAMt;

	private boolean otfmodify;

	private long otfValue;
	private double otfRate;
	private String addCommProfileOTFDetailID;

	public boolean isOtfmodify() {
		return otfmodify;
	}
	public void setOtfmodify(boolean otfmodify) {
		this.otfmodify = otfmodify;
	}

		//added for owner commission
		private String _sequenceNo;
		public AdditionalProfileDeatilsVO() {
	    };
	    public AdditionalProfileDeatilsVO(
                AdditionalProfileDeatilsVO additionalProfileDeatilsVO) {
    this._addCommProfileDetailID = additionalProfileDeatilsVO._addCommProfileDetailID;
    this._commProfileServiceTypeID = additionalProfileDeatilsVO._commProfileServiceTypeID;
    this._startRange = additionalProfileDeatilsVO._startRange;
    this._startRangeAsString = additionalProfileDeatilsVO._startRangeAsString;
    this._endRange = additionalProfileDeatilsVO._endRange;
    this._endRangeAsString = additionalProfileDeatilsVO._endRangeAsString;
    this._addCommType = additionalProfileDeatilsVO._addCommType;
    this._addCommRate = additionalProfileDeatilsVO._addCommRate;
    this._addCommRateAsString = additionalProfileDeatilsVO._addCommRateAsString;
    this._addRoamCommType = additionalProfileDeatilsVO._addRoamCommType;
    this._addRoamCommRate = additionalProfileDeatilsVO._addRoamCommRate;
    this._addRoamCommRateAsString = additionalProfileDeatilsVO._addRoamCommRateAsString;
    this._diffrentialFactor = additionalProfileDeatilsVO._diffrentialFactor;
    this._diffrentialFactorAsString = additionalProfileDeatilsVO._diffrentialFactorAsString;
    this._tax1Type = additionalProfileDeatilsVO._tax1Type;
    this._tax1Rate = additionalProfileDeatilsVO._tax1Rate;
    this._tax1RateAsString = additionalProfileDeatilsVO._tax1RateAsString;
    this._tax2Type = additionalProfileDeatilsVO._tax2Type;
    this._tax2Rate = additionalProfileDeatilsVO._tax2Rate;
    this._tax2RateAsString = additionalProfileDeatilsVO._tax2RateAsString;
    //Mearged from diwakar commit in 6.7 by lalit
    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue()) {
   	 this._addOwnerCommType = additionalProfileDeatilsVO._addOwnerCommType;
        this._addOwnerCommRate = additionalProfileDeatilsVO._addOwnerCommRate;
        this._addOwnerCommRateAsString = additionalProfileDeatilsVO._addOwnerCommRateAsString;
        this._ownerTax1Type = additionalProfileDeatilsVO._ownerTax1Type;
        this._ownerTax1Rate = additionalProfileDeatilsVO._ownerTax1Rate;
        this._ownerTax1RateAsString = additionalProfileDeatilsVO._ownerTax1RateAsString;
        this._ownerTax2Type = additionalProfileDeatilsVO._ownerTax2Type;
        this._ownerTax2Rate = additionalProfileDeatilsVO._ownerTax2Rate;
        this._ownerTax2RateAsString = additionalProfileDeatilsVO._ownerTax2RateAsString;
   }
    this.rowIndex = additionalProfileDeatilsVO.rowIndex;
    this._addtnlComStatus = additionalProfileDeatilsVO._addtnlComStatus;
    this._addtnlComStatusName = additionalProfileDeatilsVO._addtnlComStatusName;
    this._gatewayCode = additionalProfileDeatilsVO._gatewayCode;
    this._additionalCommissionTimeSlab = additionalProfileDeatilsVO._additionalCommissionTimeSlab;
    this._applicableFromAdditional = additionalProfileDeatilsVO._applicableFromAdditional;
    this._applicableToAdditional = additionalProfileDeatilsVO._applicableToAdditional;
    
    this.otfTimeSlab=additionalProfileDeatilsVO.otfTimeSlab;
    this.otfApplicableFromStr=additionalProfileDeatilsVO.otfApplicableFromStr;
    this.otfApplicableToStr=additionalProfileDeatilsVO.otfApplicableToStr;
    List<OTFDetailsVO> otf = new ArrayList<>(); 
    for(int i=0;i<Integer.parseInt(additionalProfileDeatilsVO.getOtfDetailsSize());i++){
    	otf.add(new OTFDetailsVO(additionalProfileDeatilsVO.getOtfDetails().get(i)));
    }
    this.otfDetails=otf;
    this.otfType=additionalProfileDeatilsVO.otfType;
    this.addCommProfileOTFDetailID=additionalProfileDeatilsVO.addCommProfileOTFDetailID;
    this.origOtfApplicableToStr=additionalProfileDeatilsVO.origOtfApplicableToStr;
    
}
	    public String getOtfTypePctOrAMt() {
			return otfTypePctOrAMt;
		}
		public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
			this.otfTypePctOrAMt = otfTypePctOrAMt;
		}
    public String getOtfValueAsString() {
		return otfValueAsString;
	}

	public void setOtfValueAsString(String otfValue) {
		this.otfValueAsString = otfValue;
	}

	public String getOtfRateAsString() {
		return otfRateAsString;
	}

	public void setOtfRateAsString(String otfRate) {
		this.otfRateAsString = otfRate;
	}
  
  public void setOtfDetailsSize(int otfDetailsSize) {
		this.otfDetailsSize = otfDetailsSize;
	}

public String getOtfDetailsSize() {
		return Integer.toString(otfDetailsSize);
	}

	public String getSequenceNo() {
		return _sequenceNo;
	}

	public void setSequenceNo(String no) {
		_sequenceNo = no;
	}
    public String getShortCode() {
        return _shortCode;
    }

    public void setShortCode(String shortCode) {
        _shortCode = shortCode;
    }

    public String getSetID() {
        return _setID;
    }

    public void setSetID(String setID) {
        _setID = setID;
    }

    public Timestamp getApplicableFrom() {
        return _applicableFrom;
    }

    public void setApplicableFrom(Timestamp applicableFrom) {
        _applicableFrom = applicableFrom;
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

    public String toString() {
        final StringBuffer sb = new StringBuffer("CommissionProfileDeatilsVO Data ");
        sb.append("_addCommProfileDetailID=" + _addCommProfileDetailID + ",");
        sb.append("_commProfileServiceTypeID=" + _commProfileServiceTypeID + ",");
        sb.append("_startRange=" + _startRange + ",");
        sb.append("_endRange=" + _endRange + ",");
        sb.append("_addCommType=" + _addCommType + ",");
        sb.append("_addCommRate=" + _addCommRate + ",");
        sb.append("_addRoamCommType=" + _addRoamCommType + ",");
        sb.append("_addRoamCommRate=" + _addRoamCommRate + ",");
        sb.append("_diffrentialFactor=" + _diffrentialFactor + ",");
        sb.append("_tax1Type=" + _tax1Type + ",");
        sb.append("_tax1Rate=" + _tax1Rate + ",");
        sb.append("_tax2Type=" + _tax2Type + ",");
        sb.append("_tax2Rate=" + _tax2Rate + ",");

        sb.append("_addtnlComStatus=" + _addtnlComStatus + ",");
        sb.append("_addtnlComStatusName=" + _addtnlComStatusName + ",");
        sb.append("_gatewayCode=" + _gatewayCode + ",");
        sb.append("_additionalCommissionTimeSlab=" + _additionalCommissionTimeSlab + ",");
        sb.append("_applicableFromAdditional=" + _applicableFromAdditional + ",");
        sb.append("_applicableToAdditional=" + _applicableToAdditional + ",");
        sb.append("otfApplicableFromStr=" + otfApplicableFromStr + ",");
        sb.append("otfApplicableToStr=" + otfApplicableToStr + ",");
        sb.append("otfType=" + otfType + ","); 
        return sb.toString();
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

    /**
     * @return Returns the addCommRate.
     */
    public double getAddCommRate() {
        return _addCommRate;
    }

    /**
     * @param addCommRate
     *            The addCommRate to set.
     */
    public void setAddCommRate(double addCommRate) {
        _addCommRate = addCommRate;
    }

    public String getAddCommRateAsString() {
        return _addCommRateAsString;
    }

    public void setAddCommRateAsString(String addCommRateAsString) {
        if (addCommRateAsString != null) {
            _addCommRateAsString = addCommRateAsString.trim();
        }
    }

    /**
     * @return Returns the addCommType.
     */
    public String getAddCommType() {
        return _addCommType;
    }

    /**
     * @param addCommType
     *            The addCommType to set.
     */
    public void setAddCommType(String addCommType) {
        _addCommType = addCommType;
    }

    /**
     * @return Returns the addCommProfileDetailID.
     */
    public String getAddCommProfileDetailID() {
        return _addCommProfileDetailID;
    }

    /**
     * @param addCommProfileDetailID
     *            The addCommProfileDetailID to set.
     */
    public void setAddCommProfileDetailID(String addCommProfileDetailID) {
        _addCommProfileDetailID = addCommProfileDetailID;
    }

    /**
     * @return Returns the commProfileServiceTypeID.
     */
    public String getCommProfileServiceTypeID() {
        return _commProfileServiceTypeID;
    }

    /**
     * @param commProfileServiceTypeID
     *            The commProfileServiceTypeID to set.
     */
    public void setCommProfileServiceTypeID(String commProfileServiceTypeID) {
        _commProfileServiceTypeID = commProfileServiceTypeID;
    }

    /**
     * @return Returns the diffrentialFactor.
     */
    public double getDiffrentialFactor() {
        return _diffrentialFactor;
    }

    /**
     * @param diffrentialFactor
     *            The diffrentialFactor to set.
     */
    public void setDiffrentialFactor(double diffrentialFactor) {
        _diffrentialFactor = diffrentialFactor;
    }

    /**
     * @return Returns the diffrentialFactorAsString.
     */
    public String getDiffrentialFactorAsString() {
        return _diffrentialFactorAsString;
    }

    /**
     * @param diffrentialFactorAsString
     *            The diffrentialFactorAsString to set.
     */
    public void setDiffrentialFactorAsString(String diffrentialFactorAsString) {
        if (diffrentialFactorAsString != null) {
            _diffrentialFactorAsString = diffrentialFactorAsString.trim();
        }
    }

    public String getAddtnlComStatus() {
        return _addtnlComStatus;
    }

    public void setAddtnlComStatus(String addtnlComStatus) {
        _addtnlComStatus = addtnlComStatus;
    }

    public String getAddtnlComStatusName() {
        return _addtnlComStatusName;
    }

    public void setAddtnlComStatusName(String addtnlComStatusName) {
        _addtnlComStatusName = addtnlComStatusName;
    }

    // added by gaurav pandey for roam recharge

    public double getAddRoamCommRate() {
        return _addRoamCommRate;
    }

    /**
     * @param addRoamCommRate
     *            The addRoamCommRate to set.
     */
    public void setAddRoamCommRate(double addRoamCommRate) {
        _addRoamCommRate = addRoamCommRate;
    }

    public String getAddRoamCommRateAsString() {
        return _addRoamCommRateAsString;
    }

    public void setAddRoamCommRateAsString(String addRoamCommRateAsString) {
        if (addRoamCommRateAsString != null) {
            _addRoamCommRateAsString = addRoamCommRateAsString.trim();
        }
    }

    /**
     * @return Returns the addRoamCommType.
     */
    public String getAddRoamCommType() {
        return _addRoamCommType;
    }

    /**
     * @param addRoamCommType
     *            The addRoamCommType to set.
     */
    public void setAddRoamCommType(String addRoamCommType) {
        _addRoamCommType = addRoamCommType;
    }

    public long getMinTransferValue() {
        return _minTransferValue;
    }

    public void setMinTransferValue(long minTransferValue) {
        _minTransferValue = minTransferValue;
    }

    public String getMinTrasferValueAsString() {
        return _minTrasferValueAsString;
    }

    public void setMinTrasferValueAsString(String minTrasferValueAsString) {
        _minTrasferValueAsString = minTrasferValueAsString;
    }

    public long getMaxTransferValue() {
        return _maxTransferValue;
    }

    public void setMaxTransferValue(long maxTransferValue) {
        _maxTransferValue = maxTransferValue;
    }

    public String getMaxTransferValueAsString() {
        return _maxTransferValueAsString;
    }

    public void setMaxTransferValueAsString(String maxTransferValueAsString) {
        _maxTransferValueAsString = maxTransferValueAsString;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public String getProfileName() {
        return _profileName;
    }

    public void setProfileName(String profileName) {
        _profileName = profileName;
    }

    public String getSetVersion() {
        return _setVersion;
    }

    public void setSetVersion(String setVersion) {
        _setVersion = setVersion;
    }

    public String getServiceID() {
        return _serviceID;
    }

    public void setServiceID(String serviceID) {
        _serviceID = serviceID;
    }

    public void setSubServiceCode(String subServiceCode) {
        _subServiceCode = subServiceCode;
    }

    public String getSubServiceCode() {
        return _subServiceCode;
    }

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        _gatewayCode = gatewayCode;
    }

    public String getAdditionalCommissionTimeSlab() {
        return _additionalCommissionTimeSlab;
    }

    public void setAdditionalCommissionTimeSlab(String time) {
        _additionalCommissionTimeSlab = time;
    }

    public String getApplicableFromAdditional() {
        return _applicableFromAdditional;
    }

    public void setApplicableFromAdditional(String fromDate) {
        _applicableFromAdditional = fromDate;
    }

    public String getApplicableToAdditional() {
        return _applicableToAdditional;
    }

    public void setApplicableToAdditional(String toDate) {
        _applicableToAdditional = toDate;
    }
		    public String getAddOwnerCommRateAsString() {
		        return _addOwnerCommRateAsString;
		    }
		    public void setAddOwnerCommRateAsString(String addOwnerCommRateAsString) {
		        if(addOwnerCommRateAsString!=null)
		            _addOwnerCommRateAsString = addOwnerCommRateAsString.trim(); 
			    else
			        _addOwnerCommRateAsString = addOwnerCommRateAsString; 
		    }
		    public double getAddOwnerCommRate() {
		        return _addOwnerCommRate;
		    }
		    public void setAddOwnerCommRate(double addOwnerCommRate) {
		        _addOwnerCommRate = addOwnerCommRate;
		    }
		    public String getAddOwnerCommType() {
		        return _addOwnerCommType;
		    }
		    public void setAddOwnerCommType(String addOwnerCommType) {
		        _addOwnerCommType = addOwnerCommType;
		    }
		    public double getOwnerTax1Rate()
			{
				return _ownerTax1Rate;
			}
			public void setOwnerTax1Rate(double tax1Rate)
			{
				_ownerTax1Rate = tax1Rate;
			}
			public String getOwnerTax1RateAsString()
			{
			    return _ownerTax1RateAsString;
			}
			public void setOwnerTax1RateAsString(String ownerTax1RateAsString)
			{
			    if(ownerTax1RateAsString!=null)
			        _ownerTax1RateAsString = ownerTax1RateAsString.trim();
			    else
			        _ownerTax1RateAsString = ownerTax1RateAsString;
			}
			public String getOwnerTax1Type()
			{
				return _ownerTax1Type;
			}
			public void setOwnerTax1Type(String tax1Type)
			{
				_ownerTax1Type = tax1Type;
			}
			public double getOwnerTax2Rate()
			{
				return _ownerTax2Rate;
			}
			public void setOwnerTax2Rate(double tax2Rate)
			{
				_ownerTax2Rate = tax2Rate;
			}
			public String getOwnerTax2RateAsString()
			{
			    return _ownerTax2RateAsString;
			}
			public void setOwnerTax2RateAsString(String ownerTax2RateAsString)
			{
			    if(ownerTax2RateAsString!=null)
			        _ownerTax2RateAsString = ownerTax2RateAsString.trim();
			    else
			        _ownerTax2RateAsString = ownerTax2RateAsString;
			}
			public String getOwnerTax2Type()
			{
				return _ownerTax2Type;
			}
			public void setOwnerTax2Type(String tax2Type)
			{
				_ownerTax2Type = tax2Type;
			}

			public String getOtfApplicableFromStr() {
				return otfApplicableFromStr;
			}

			public void setOtfApplicableFromStr(String otfApplicableFromStr) {
				this.otfApplicableFromStr = otfApplicableFromStr;
			}

			public String getOtfApplicableToStr() {
				return otfApplicableToStr;
			}

			public void setOtfApplicableToStr(String otfApplicableToStr) {
				this.otfApplicableToStr = otfApplicableToStr;
			}

			public Date getOtfApplicableFrom() {
				return otfApplicableFrom;
			}

			public void setOtfApplicableFrom(Date otfApplicableFrom) {
				this.otfApplicableFrom = otfApplicableFrom;
			}

			public Date getOtfApplicableTo() {
				return otfApplicableTo;
			}

			public void setOtfApplicableTo(Date otfApplicableTo) {
				this.otfApplicableTo = otfApplicableTo;
			}

			public String getOtfType() {
				return otfType;
			}

			public void setOtfType(String otfType) {
				this.otfType = otfType;
			}

			public List<OTFDetailsVO> getOtfDetails() {
				return otfDetails;
			}

			public void setOtfDetails(List<OTFDetailsVO> otfDetails) {
				this.otfDetails = otfDetails;
			}

			public String getOtfTimeSlab() {
				return otfTimeSlab;
			}

			public void setOtfTimeSlab(String otfTimeSlab) {
				this.otfTimeSlab = otfTimeSlab;
			}
			
			public long getOtfValue() {
				return otfValue;
			}

			public void setOtfValue(long otfValue) {
				this.otfValue = otfValue;
			}

			public double getOtfRate() {
				return otfRate;
			}

			public void setOtfRate(Double otfRate) {
				this.otfRate = otfRate;
			}
			
			public String getAddCommProfileOTFDetailID() {
				return addCommProfileOTFDetailID;
			}

			public void setAddCommProfileOTFDetailID(String addCommProfileOTFDetailID) {
				this.addCommProfileOTFDetailID = addCommProfileOTFDetailID;
			}
			
			private String origOtfApplicableToStr;
			public String getOrigOtfApplicableToStr() {
				return origOtfApplicableToStr;
			}

			public void setOrigOtfApplicableToStr(String origOtfApplicableToStr) {
				this.origOtfApplicableToStr = origOtfApplicableToStr;
			}
}
