package com.btsl.pretups.currencyconversion.businesslogic;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;

public class CurrencyConversionVO implements Serializable {

	private static final Log LOG = LogFactory.getLog(CurrencyConversionVO.class.getName());
    private String _sourceCurrencyCode = null;
    private String _targetCurrencyCode = null;
    private String _sourceCurrencyName = null;
    private String _targetCurrencyName = null;
    private String _country = null;
    private double _conversion;
    private long _multFactor;
    private String _description = null; 
	private Date _createdOn = null; 
	private String _createdBy = null; 
	private Date _modifiedOn = null; 
	private String _modifiedBy = null; 
	private String _action = "N";
	private long _id;
	private List<CurrencyConversionVO> _mDataList;
	private String _errorCode = ""; 
	private String _errorMsg = "";
	private String _externalRefNumber = "";
	  

	
	
    public String toString() {
        final StringBuilder sbf = new StringBuilder();
        sbf.append(super.toString());
        sbf.append("_sourceCurrencyCode =").append(_sourceCurrencyCode);
        sbf.append(",_targetCurrencyCode =").append(_targetCurrencyCode);
        sbf.append(",_sourceCurrencyName =").append(_sourceCurrencyName);
        sbf.append(",_targetCurrencyName=").append(_targetCurrencyName);
        sbf.append(",_country =").append(_country);
        sbf.append(",_conversion=").append(_conversion);
        sbf.append(",_multFactor =").append(_multFactor);
        sbf.append(",_description =").append(_description);
        sbf.append(",_createdOn=").append(_createdOn);
        sbf.append(",_modifiedOn=").append(_modifiedOn);
        sbf.append(",_modifiedBy=").append(_modifiedBy);
        sbf.append(",_action=").append(_action);
        sbf.append(",_id=").append(_id);
        sbf.append(",_errorCode=").append(_errorCode);
        sbf.append(",_errorMsg=").append(_errorMsg);
        sbf.append(",_externalRefNumber=").append(_externalRefNumber);
           return sbf.toString();
    }

	public String getSourceCurrencyCode() {
        return _sourceCurrencyCode;
    }

    public void setSourceCurrencyCode(String sCode) {
        _sourceCurrencyCode = sCode;
    }

    public String getTargetCurrencyCode() {
        return _targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String tCode) {
        _targetCurrencyCode = tCode;
    }

    public String getSourceCurrencyName() {
        return _sourceCurrencyName;
    }

    public void setSourceCurrencyName(String sName) {
        _sourceCurrencyName = sName;
    }

    public String getTargetCurrencyName() {
        return _targetCurrencyName;
    }

    public void setTargetCurrencyName(String tName) {
        _targetCurrencyName = tName;
    }

    public String getCountry() {
        return _country;
    }

    public void setCountry(String country) {
        _country = country;
    }

    public double getConversion() {
        return _conversion;
    }

    public void setConversion(double conversion) {
        _conversion = conversion;
    }

    public long getMultFactor() {
        return _multFactor;
    }

    public void setMultFactor(long multFactor) {
        _multFactor = multFactor;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }
	
	public Date getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }
	
	public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }
	
	public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }
	
	public String getModifiedBy() {
        return _modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        _modifiedBy = modifiedBy;
    }

	public String getAction() {
		return _action;
	}

	public void setAction(String _action) {
		this._action = _action;
	}

	public long getId() {
		return _id;
	}

	public void setId(long _id) {
		this._id = _id;
	}

	public double getDisplayAmount() {

        // if(LOG.isDebugEnabled())LOG.debug("getDisplayAmount","Entered p_amount:"+p_amount)
        final String METHOD_NAME = "getDisplayAmount";
        final long multiplicationFactor = _multFactor;
        final double amount = (double) _conversion / (double) multiplicationFactor;
        if(LOG.isDebugEnabled())LOG.debug("getDisplayAmount","Exiting display amount:"+amount);
        return amount;		
	}

	public long getSystemAmount() {
		final long multiplicationFactor = _multFactor;
		long amount = 0;
		amount = BTSLUtil.parseDoubleToLong(_conversion * multiplicationFactor);
		return amount;}
		
	public List<CurrencyConversionVO> getmDataList() {
		return _mDataList;
	}

	public void setmDataList(List<CurrencyConversionVO> dataList) {
		_mDataList = dataList;
	}	
	
	  /**
		 * @return the _errorCode
		 */
		public String getErrorCode() {
			return _errorCode;
		}

		/**
		 * @param _errorCode the _errorCode to set
		 */
		public void setErrorCode(String _errorCode) {
			this._errorCode = _errorCode;
		}

		/**
		 * @return the _errorMsg
		 */
		public String getErrorMsg() {
			return _errorMsg;
		}

		/**
		 * @param _errorMsg the _errorMsg to set
		 */
		public void setErrorMsg(String _errorMsg) {
			this._errorMsg = _errorMsg;
		}
		
		/**
		 * @return the _externalRefNumber
		 */
		public String getExternalRefNumber() {
			return _externalRefNumber;
		}

		/**
		 * @param _externalRefNumber the _externalRefNumber to set
		 */
		public void setExternalRefNumber(String _externalRefNumber) {
			this._externalRefNumber = _externalRefNumber;
		}

}
