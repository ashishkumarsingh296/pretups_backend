package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.TypesI;

/**
 * @(#)UserPhoneVO.java
 *                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Mohit Goel 24/06/2005 Initial Creation
 * 
 *                      This class is used for User Phone Info
 * 
 */
public class UserPhoneVO implements Serializable {

    private String _userPhonesId = null;
    private String _msisdn = null;
    private String _userId = null;
    private String _description = null;
    private String _primaryNumber = null;
    private String _smsPin = null;
    private String _confirmSmsPin = null;
    private String _pinRequired = null;
    private String _phoneProfile = null;
    private String _phoneProfileDesc;
    private String _phoneLanguage = null;
    private String _country = null;
    private int _invalidPinCount;
    private String _lastTransactionStatus;
    private Date _lastTransactionOn;
    private Date _pinModifiedOn;
    private String _createdBy = null;
    private Date _createdOn;
    private String _modifiedBy = null;
    private Date _modifiedOn;
    private String _lastTransferID;
    private String _lastTransferType = null;

    // defined to know the row number
    private int rowIndex;
    private String showSmsPin;
    private String oldSmsPin;
    private String multiBox;

    private long _prefixID;
    private String _tempTransferID;
    private String _encryptDecryptKey;
    private String _simProfileID;
    private boolean _registered = true;
    private boolean _pinRequiredBool = true;
    private Date _firstInvalidPinTime;
    private Locale _locale;

    private boolean _forcePinCheckReqd = true;
    private boolean _barUserForInvalidPin = false;
    private boolean _pinModifyFlag = true;

    private String _operationType; // when user phone modify then which type
                                   // action taken like Insert/Delete/Update
    private boolean _isIdGenerate = false;
    private String _pinReset = null;

    private Date _lastAccessOn = null;
    private boolean _isAccessOn = false;
    // Add for auto generating password
    private String _pinGenerateAllow;
    private Date _currentModifiedOn;

    // For self TPIN reset
    private String _otp = null;

    private String networkCode=null;
    private String _ownerTempTransferId;
    private String _requestGatewayCode;
    public String getMhash() {
		return mhash;
	}

	public void setMhash(String mhash) {
		this.mhash = mhash;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}
	private String mhash;
    private String imei;
	
    public UserPhoneVO() {
    };

    public UserPhoneVO(UserPhoneVO userphoneVO) {
        this._userPhonesId = userphoneVO._userPhonesId;
        this._msisdn = userphoneVO._msisdn;
        this._userId = userphoneVO._userId;
        this._description = userphoneVO._description;
        this._primaryNumber = userphoneVO._primaryNumber;
        this._smsPin = userphoneVO._smsPin;
        this._confirmSmsPin = userphoneVO._confirmSmsPin;
        this._pinRequired = userphoneVO._pinRequired;
        this._phoneProfile = userphoneVO._phoneProfile;
        this._phoneProfileDesc = userphoneVO._phoneProfileDesc;
        this._phoneLanguage = userphoneVO._phoneLanguage;
        this._country = userphoneVO._country;
        this._invalidPinCount = userphoneVO._invalidPinCount;
        this._lastTransactionStatus = userphoneVO._lastTransactionStatus;
        this._lastTransactionOn = userphoneVO._lastTransactionOn;
        this._pinModifiedOn = userphoneVO._pinModifiedOn;
        this._createdBy = userphoneVO._createdBy;
        this._createdOn = userphoneVO._createdOn;
        this._modifiedBy = userphoneVO._modifiedBy;
        this._modifiedOn = userphoneVO._modifiedOn;
        this._lastTransferID = userphoneVO._lastTransferID;
        this._lastTransferType = userphoneVO._lastTransferType;

        // defined to know the row number
        this.rowIndex = userphoneVO.rowIndex;
        this.showSmsPin = userphoneVO.showSmsPin;
        this.oldSmsPin = userphoneVO.oldSmsPin;
        this.multiBox = userphoneVO.multiBox;

        this._prefixID = userphoneVO._prefixID;
        this._tempTransferID = userphoneVO._tempTransferID;
        this._encryptDecryptKey = userphoneVO._encryptDecryptKey;
        this._simProfileID = userphoneVO._simProfileID;
        this._registered = userphoneVO._registered;
        this._pinRequiredBool = userphoneVO._pinRequiredBool;
        this._firstInvalidPinTime = userphoneVO._firstInvalidPinTime;
        this._locale = userphoneVO._locale;

        this._forcePinCheckReqd = userphoneVO._forcePinCheckReqd;
        this._barUserForInvalidPin = userphoneVO._barUserForInvalidPin;
        this._pinModifyFlag = userphoneVO._pinModifyFlag;
        this._operationType = userphoneVO._operationType;
        this._isIdGenerate = userphoneVO._isIdGenerate;
        this._pinReset = userphoneVO._pinReset;
        this._lastAccessOn = userphoneVO._lastAccessOn;
        this._isAccessOn = userphoneVO._isAccessOn;
        this._pinGenerateAllow = userphoneVO._pinGenerateAllow;
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCurrentModifiedOn() {
        return _currentModifiedOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCurrentModifiedOn(Date currentModifiedOn) {
        _currentModifiedOn = currentModifiedOn;
    }

    /**
     * @return Returns the pinModifyFlag.
     */
    public boolean isPinModifyFlag() {
        return _pinModifyFlag;
    }

    /**
     * @param pinModifyFlag
     *            The pinModifyFlag to set.
     */
    public void setPinModifyFlag(boolean pinModifyFlag) {
        _pinModifyFlag = pinModifyFlag;
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

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        if (country != null) {
            _country = country.trim();
        }
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdBy to set.
     */
    public void setCreatedBy(String createdBy) {
        if (createdBy != null) {
            _createdBy = createdBy.trim();
        }
    }

    /**
     * @return Returns the createdOn.
     */
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     * @param createdOn
     *            The createdOn to set.
     */
    public void setCreatedOn(Date createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        if (description != null) {
            _description = description.trim();
        }
    }

    /**
     * @return Returns the language.
     */
    public String getPhoneLanguage() {
        return _phoneLanguage;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setPhoneLanguage(String language) {
        if (language != null) {
            _phoneLanguage = language.trim();
        }
    }

    /**
     * @return Returns the modifiedBy.
     */
    public String getModifiedBy() {
        return _modifiedBy;
    }

    /**
     * @param modifiedBy
     *            The modifiedBy to set.
     */
    public void setModifiedBy(String modifiedBy) {
        if (modifiedBy != null) {
            _modifiedBy = modifiedBy.trim();
        }
    }

    /**
     * @return Returns the modifiedOn.
     */
    public Date getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(Date modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * @param msisdn
     *            The msisdn to set.
     */
    public void setMsisdn(String msisdn) {
        if (msisdn != null) {
            _msisdn = msisdn.trim();
        }
    }

    /**
     * @return Returns the pinRequired.
     */
    public String getPinRequired() {
        return _pinRequired;
    }

    /**
     * @param pinRequired
     *            The pinRequired to set.
     */
    public void setPinRequired(String pinRequired) {
        if (TypesI.NO.equalsIgnoreCase(pinRequired)) {
            _pinRequiredBool = false;
        } else {
            _pinRequiredBool = true;
        }
        if (pinRequired != null) {
            _pinRequired = pinRequired.trim();
        }
    }

    /**
     * @return Returns the primaryNumber.
     */
    public String getPrimaryNumber() {
        return _primaryNumber;
    }

    /**
     * @param primaryNumber
     *            The primaryNumber to set.
     */
    public void setPrimaryNumber(String primaryNumber) {
        if (primaryNumber != null) {
            _primaryNumber = primaryNumber.trim();
        }
    }

    /**
     * @return Returns the profile.
     */
    public String getPhoneProfile() {
        return _phoneProfile;
    }

    /**
     * @param profile
     *            The profile to set.
     */
    public void setPhoneProfile(String profile) {
        if (profile != null) {
            _phoneProfile = profile.trim();
        }
    }

    /**
     * @return Returns the smsPin.
     */
    public String getSmsPin() {
        return _smsPin;
    }

    /**
     * @param smsPin
     *            The smsPin to set.
     */
    public void setSmsPin(String smsPin) {
        if (smsPin != null) {
            _smsPin = smsPin.trim();
        }
    }

    public int getInvalidPinCount() {
        return _invalidPinCount;
    }

    public void setInvalidPinCount(int invalidPinCount) {
        _invalidPinCount = invalidPinCount;
    }

    /**
     * @return Returns the userId.
     */
    public String getUserId() {
        return _userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(String userId) {
        if (userId != null) {
            _userId = userId.trim();
        }
    }

    /**
     * @return Returns the confirmSmsPin.
     */
    public String getConfirmSmsPin() {
        return _confirmSmsPin;
    }

    /**
     * @param confirmSmsPin
     *            The confirmSmsPin to set.
     */
    public void setConfirmSmsPin(String confirmSmsPin) {
        if (confirmSmsPin != null) {
            _confirmSmsPin = confirmSmsPin.trim();
        }
    }

    /**
     * @return Returns the showSmsPin.
     */
    public String getShowSmsPin() {
        return showSmsPin;
    }

    /**
     * @param showSmsPin
     *            The showSmsPin to set.
     */
    public void setShowSmsPin(String showSmsPin) {
        if (showSmsPin != null) {
            this.showSmsPin = showSmsPin.trim();
        } else {
            this.showSmsPin = showSmsPin;
        }
    }

    /**
     * @return Returns the lastTransferType.
     */
    public String getLastTransferType() {
        return _lastTransferType;
    }

    /**
     * @param lastTransferType
     *            The lastTransferType to set.
     */
    public void setLastTransferType(String lastTransferType) {
        if (lastTransferType != null) {
            _lastTransferType = lastTransferType.trim();
        }
    }

    /**
     * @return Returns the pinModifiedOn.
     */
    public Date getPinModifiedOn() {
        return _pinModifiedOn;
    }

    /**
     * @param pinModifiedOn
     *            The pinModifiedOn to set.
     */
    public void setPinModifiedOn(Date pinModifiedOn) {
        _pinModifiedOn = pinModifiedOn;
    }

    /**
     * @return Returns the userPhoneId.
     */
    public String getUserPhonesId() {
        return _userPhonesId;
    }

    /**
     * @param userPhoneId
     *            The userPhoneId to set.
     */
    public void setUserPhonesId(String userPhoneId) {
        if (userPhoneId != null) {
            _userPhonesId = userPhoneId.trim();
        }
    }

    public Date getLastTransactionOn() {
        return _lastTransactionOn;
    }

    public void setLastTransactionOn(Date lastTransactionOn) {
        _lastTransactionOn = lastTransactionOn;
    }

    public String getLastTransactionStatus() {
        return _lastTransactionStatus;
    }

    public void setLastTransactionStatus(String lastTransactionStatus) {
        _lastTransactionStatus = lastTransactionStatus;
    }

    public String getLastTransferID() {
        return _lastTransferID;
    }

    public void setLastTransferID(String lastTransferID) {
        _lastTransferID = lastTransferID;
    }

    public long getPrefixID() {
        return _prefixID;
    }

    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    public String getEncryptDecryptKey() {
        return _encryptDecryptKey;
    }

    public void setEncryptDecryptKey(String encryptDecryptKey) {
        _encryptDecryptKey = encryptDecryptKey;
    }

    public String getSimProfileID() {
        return _simProfileID;
    }

    public void setSimProfileID(String simProfileID) {
        _simProfileID = simProfileID;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public String getTempTransferID() {
        return _tempTransferID;
    }

    public void setTempTransferID(String tempTransferID) {
        _tempTransferID = tempTransferID;
    }

    public boolean isRegistered() {
        return _registered;
    }

    public void setRegistered(boolean registered) {
        _registered = registered;
    }

    public boolean isPinRequiredBool() {
        return _pinRequiredBool;
    }

    public void setPinRequiredBool(boolean pinRequiredBool) {
        _pinRequiredBool = pinRequiredBool;
    }

    public Date getFirstInvalidPinTime() {
        return _firstInvalidPinTime;
    }

    public void setFirstInvalidPinTime(Date firstInvalidPinTime) {
        _firstInvalidPinTime = firstInvalidPinTime;
    }

    /**
     * @return Returns the phoneProfileDesc.
     */
    public String getPhoneProfileDesc() {
        return _phoneProfileDesc;
    }

    /**
     * @param phoneProfileDesc
     *            The phoneProfileDesc to set.
     */
    public void setPhoneProfileDesc(String phoneProfileDesc) {
        _phoneProfileDesc = phoneProfileDesc;
    }

    public boolean isForcePinCheckReqd() {
        return _forcePinCheckReqd;
    }

    public void setForcePinCheckReqd(boolean forcePinCheckReqd) {
        _forcePinCheckReqd = forcePinCheckReqd;
    }

    public boolean isBarUserForInvalidPin() {
        return _barUserForInvalidPin;
    }

    public void setBarUserForInvalidPin(boolean barUserForInvalidPin) {
        _barUserForInvalidPin = barUserForInvalidPin;
    }

    /**
     * @return Returns the multiBox.
     */
    public String getMultiBox() {
        return multiBox;
    }

    /**
     * @param multiBox
     *            The multiBox to set.
     */
    public void setMultiBox(String multiBox) {
        this.multiBox = multiBox;
    }

    /**
     * @return Returns the oldSmsPin.
     */
    public String getOldSmsPin() {
        return oldSmsPin;
    }

    /**
     * @param oldSmsPin
     *            The oldSmsPin to set.
     */
    public void setOldSmsPin(String oldSmsPin) {
        this.oldSmsPin = oldSmsPin;
    }

    /**
     * @return Returns the operationOn.
     */
    public String getOperationType() {
        return this._operationType;
    }

    /**
     * @param operationOn
     *            The operationOn to set.
     */
    public void setOperationType(String operationType) {
        this._operationType = operationType;
    }

    /**
     * @return Returns the isIdGenerate.
     */
    public boolean isIdGenerate() {
        return this._isIdGenerate;
    }

    /**
     * @param isIdGenerate
     *            The isIdGenerate to set.
     */
    public void setIdGenerate(boolean isIdGenerate) {
        this._isIdGenerate = isIdGenerate;
    }

    public String getPinReset() {
        return _pinReset;
    }

    public void setPinReset(String pinReset) {
        _pinReset = pinReset;
    }

    @Override
	public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("_userPhonesId=").append(_userPhonesId);
        strBuild.append(",_msisdn=").append(_msisdn);
        strBuild.append(",_userId=").append(_userId);
        strBuild.append(",_primaryNumber=").append(_primaryNumber);
        strBuild.append(",_operationType=").append(_operationType);
        strBuild.append(",_isIdGenerate=").append(_isIdGenerate);
        strBuild.append(",_lastAccessOn=").append(_lastAccessOn);
        return strBuild.toString();
    }

    /**
     * @return Returns the isAccessOn.
     */
    public boolean isAccessOn() {
        return _isAccessOn;
    }

    /**
     * @return Returns the lastAccessOn.
     */
    public Date getLastAccessOn() {
        return _lastAccessOn;
    }

    /**
     * @param isAccessOn
     *            The isAccessOn to set.
     */
    public void setAccessOn(boolean isAccessOn) {
        _isAccessOn = isAccessOn;
    }

    /**
     * @param lastAccessOn
     *            The lastAccessOn to set.
     */
    public void setLastAccessOn(Date lastAccessOn) {
        _lastAccessOn = lastAccessOn;
    }

    /**
     * @return Returns the _pinGenerateAllow.
     */
    public String getPinGenerateAllow() {
        return _pinGenerateAllow;
    }

    /**
     * @param generateAllow
     *            The _pinGenerateAllow to set.
     */
    public void setPinGenerateAllow(String generateAllow) {
        _pinGenerateAllow = generateAllow;
    }

    public String getOTP() {
        return _otp;
    }

    public void setOTP(String _otp) {
        this._otp = _otp;
    }
    
    public String getNetworkCode(){
    	return networkCode;
    }
    
    public void setNetworkCode(String networkCode){
    	this.networkCode=networkCode;
    }
       //added by Ashutosh to clear the frequently used instance variables of the current instance
    
    /** START: Birendra: CLARO: 19-MAR-2015 */
   	
   	public String getOwnerTempTransferId() {
   		return _ownerTempTransferId;
   	}

   	public void setOwnerTempTransferId(String tempTransferId) {
   		_ownerTempTransferId = tempTransferId;
   	}

   	public String getRequestGatewayCode() {
   		return _requestGatewayCode;
   	}

   	public void setRequestGatewayCode(String p_requestGatewayCode) {
   		this._requestGatewayCode = p_requestGatewayCode;
   	}
   	
   	public static UserPhoneVO getInstance(){
   		return new UserPhoneVO();
   	}
   	/** STOP: Birendra: CLARO: 19-MAR-2015 */
    
    public void clearInstance() {
        this._userPhonesId = null;
        this._msisdn = null;
        this._userId = null;
        this._description = null;
        this._primaryNumber = null;
        this._smsPin = null;
        this._confirmSmsPin = null;
        this._pinRequired = null;
        this._phoneProfile = null;
        this._phoneProfileDesc = null;
        this._phoneLanguage = null;
        this._country = null;
        this._invalidPinCount = 0;
        this._lastTransactionStatus = null;
        this._lastTransactionOn = null;
        this._pinModifiedOn = null;
        this._createdBy = null;
        this._createdOn = null;
        this._modifiedBy = null;
        this._modifiedOn = null;
        this._lastTransferID = null;
        this._lastTransferType = null;
        this.rowIndex = 0;
        this.showSmsPin = null;
        this.oldSmsPin = null;
        this.multiBox = null;
        this._prefixID = 0;
        this._tempTransferID = null;
        this._encryptDecryptKey = null;
        this._simProfileID = null;
        this._registered = true;
        this._pinRequiredBool = true;
        this._firstInvalidPinTime = null;
        this._locale = null;
        this._forcePinCheckReqd = true;
        this._barUserForInvalidPin = false;
        this._pinModifyFlag = true;
        this._operationType = null;
        this._isIdGenerate = false;
        this._pinReset = null;
        this._lastAccessOn = null;
        this._isAccessOn = false;
        this._pinGenerateAllow = null;
    }
}
