package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLDateUtil;

public class OtfProfileVO implements Serializable {
    private Log _log = LogFactory.getLog(OtfProfileVO.class.getName());
    private String _commProfileOtfID;
    private String _commProfileSetID;
    private String _commProfileSetVersion;
    private String _productCode;
    private String _productCodeDesc;
    private String _addtnlComStatus;
    private String _addtnlComStatusName;
    private String _otfTimeSlab;
    private String _otfApplicableFrom;
    private String _otfApplicableTo;
    /*private String otfApplicableFromStr;
    private String otfApplicableToStr;*/
    private List<OTFDetailsVO> otfDetails;
    private int otfDetailsSize;

    public OtfProfileVO() {};

    public OtfProfileVO(OtfProfileVO otfProfileVO) {
        this._commProfileOtfID = otfProfileVO._commProfileOtfID;
        this._commProfileSetID = otfProfileVO._commProfileSetID;
        this._commProfileSetVersion = otfProfileVO._commProfileSetVersion;
        this._productCode = otfProfileVO._productCode;
        this._productCodeDesc = otfProfileVO._productCodeDesc;
        this._addtnlComStatus = otfProfileVO._addtnlComStatus;
        this._addtnlComStatusName = otfProfileVO._addtnlComStatusName;
        this._otfTimeSlab = otfProfileVO._otfTimeSlab;
        this._otfApplicableFrom = otfProfileVO._otfApplicableFrom;
        this._otfApplicableTo = otfProfileVO._otfApplicableTo;
        /*this.otfApplicableFromStr=otfProfileVO.otfApplicableFromStr;
        this.otfApplicableToStr=otfProfileVO.otfApplicableToStr;*/
        List<OTFDetailsVO> otf = new ArrayList<>(); 
        int number=Integer.parseInt(otfProfileVO.getOtfDetailsSize());
        for(int i=0;i<number;i++){
        	otf.add(new OTFDetailsVO(otfProfileVO.getOtfDetails().get(i)));
        }
        this.otfDetails=otf;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommissionProfileProductsVO Data ");

        sb.append("_commProfileOtfID=" + _commProfileOtfID + ",");
        sb.append("_commProfileSetID=" + _commProfileSetID + ",");
        sb.append("_commProfileSetVersion=" + _commProfileSetVersion + ",");
        sb.append("_productCode=" + _productCode + ",");
        sb.append("_productCodeDesc=" + _productCodeDesc + ",");
        sb.append("_addtnlComStatus=" + _addtnlComStatus + ",");
        sb.append("_addtnlComStatusName=" + _addtnlComStatusName + ",");
        sb.append("_otfTimeSlab=" + _otfTimeSlab + ",");
        sb.append("_otfApplicableFrom=" + _otfApplicableFrom + ",");
        sb.append("_otfApplicableTo=" + _otfApplicableTo + ",");
        /*sb.append("otfApplicableFromStr=" + otfApplicableFromStr + ",");
        sb.append("otfApplicableToStr=" + otfApplicableToStr + ",");*/
        return sb.toString();
    }

	public String getCommProfileOtfID() {
	    return _commProfileOtfID;
	}
	public void setCommProfileOtfID(String commProfileServiceTypeID) {
	    _commProfileOtfID = commProfileServiceTypeID;
	}
	
	public String getCommProfileSetID() {
        return _commProfileSetID;
    }
    public void setCommProfileSetID(String commProfileSetID) {
        _commProfileSetID = commProfileSetID;
    }

    public String getCommProfileSetVersion() {
        return _commProfileSetVersion;
    }
    public void setCommProfileSetVersion(String commProfileSetVersion) {
        _commProfileSetVersion = commProfileSetVersion;
    }

    public String getProductCode() {
        return _productCode;
    }
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getProductCodeDesc() {
        return _productCodeDesc;
    }
    public void setProductCodeDesc(String productCodeDesc) {
        _productCodeDesc = productCodeDesc;
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

    public String getOtfTimeSlab() {
        return _otfTimeSlab;
    }
    public void setOtfTimeSlab(String time) {
        _otfTimeSlab = time;
    }

    public String getOtfApplicableFrom() {
        return _otfApplicableFrom;
    }
    public void setOtfApplicableFrom(String todate) {
        _otfApplicableFrom = todate;
    }

    public String getOtfApplicableTo() {
        return _otfApplicableTo;
    }
    public void setOtfApplicableTo(String todate) {
        _otfApplicableTo = todate;
    }
    
    public Date getOtfApplicableFromDate() {
    	Date otfDate = null;
		try {
			otfDate = BTSLDateUtil.getGregorianDate(_otfApplicableFrom);
		} catch (ParseException e) {
			_log.errorTrace("getOtfApplicableToDate", e);
		}
		return otfDate;
	}

	public Date getOtfApplicableToDate() {
		Date otfDate = null;
		try {
			otfDate = BTSLDateUtil.getGregorianDate(_otfApplicableTo);
		} catch (ParseException e) {
			_log.errorTrace("getOtfApplicableToDate", e);
		}
		return otfDate;
	}
    
    public List<OTFDetailsVO> getOtfDetails() {
		return otfDetails;
	}
    public void setOtfDetails(List<OTFDetailsVO> otfDetails) {
		this.otfDetails = otfDetails;
	}
    
    public void setOtfDetailsSize(int otfDetailsSize) {
  		this.otfDetailsSize = otfDetailsSize;
  	}
    public String getOtfDetailsSize() {
		return Integer.toString(otfDetailsSize);
	}
}
