/*
 * Created on Jul 18, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.vomsreport.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;

/**
 * @(#)VomsReportForm.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Vikas yadav 18/07/2006 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         This class is used for report(Voucher Availability
 *                         Report,Available Vouchers Report,Voucher status
 *                         report) of VOMS
 * 
 */
public class VomsReportForm  {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    private ArrayList _productList = null;
    private String _productID = null;
    private String _ProductIDName = null;
    private ArrayList _categoryList = null;
    private ArrayList _typeList = null;
    private String _voucherType = null;
    private String _radioNetCode = null;
    private String _statusString = null;
    private String _categoryCodeForProduct = null;
    private String _statusName = null;
    private String _fromSerial;
    private String _toSerial;
    private String _networkName;
    private String _networkCode;
    private String _reportHeaderName;
    private String _productName;
    private String _categoryName;
    private String _status;
    private String _prevStatus;
    private String _validity;
    private String _talkTime;
    private String _mrp;
    private String _serialNo;
    private String _prodCircle;
    private String _consumptionCircle;
    private String _profile;
    private String _denomination;
    private String _newStatus;
    private String _preStatus;
    private String _enabldOn;
    private String _consumdOn;
    private String _msisdn;
    private String _senderMsisdn;// To add Sender MSISDN in Voucher
                                 // enquiry[added by Vipul on 06/12/07]
    private String _createdOn;

    // added by siddhartha
    private String _toDate = null;
    private String _fromDate = null;
    private ArrayList _voucherList = null;
    private String _lastConsumedOn = null;
    private String _transactionID = null;
    private String applicableFromFormat = null;

    // ADDED BY VIKRAM
    private ArrayList _selectorList;
    private String _selector;
    private int _selectorListSize;
    private String _selectorName;

    // voucher Type by Akanksha
    private ArrayList _voucherTypeList = null;
    private int _voucherTypeListSize;
    private String _name = null;
    private String _serviceTypeMapping = null;
    private String _VoucherTypeDesc = null;
    private int _productListSize;
    private String _type;
	private String distributedToDate = null;
    private String distributedFromDate = null;
    private String consumedToDate = null;
    private String consumedFromDate = null;
    private String burnRate;
    private String optionalMSISDN = null;
	private String optionalCategoryCodeForProduct = null;
    private String optionalProductID = null;
    private String totalDistributed;
    private String totalConsumed;
    
    // added for load user data
    
    private ArrayList _statusList = null;
    private ArrayList _terminalTypeList = null;
    private String _categoryID;
    private long _time;
    private int _userListSize;
    private ArrayList _userList = null; 
    private String _userID;
    private String _loggedInUserCategoryCode;
    private String _loggedInUserName;
    private String _userType;
    private ArrayList _zoneList = null;
    private String _zoneCode;
    private String _zoneCodeDesc;
    private String _zoneName = null;
    private String _domainCode;
    private String _domainCodeDesc;
    private ArrayList _domainList = null;
    private String _domainName = null;
    private String _fromtransferCategoryCode;
    private ArrayList _fromCategoryList = null;
    private String _userName;	 
    
    
    private String _voucherStatus = null;
    private String bankName=null;
    private String productMRP=null;
    private ArrayList bankTypeList = null;
	
	private String _fromTime = null;
	private String _toTime = null;
	private Date _fromDateTime = null;
	private Date _toDateTime = null;
	private String _domainListString;
	
	private String _fromMonth;
    private String _toMonth;
	
    private String _tempfromDate;
    private String _temptoDate;
    
    //added by Vishwajeet Singh
    private String _voucherSegment=null;
    private String _expiryDate=null;
    
	
    /**
     * @param fromMonth
     *            The fromMonth to set.
     */
    public void setFromMonth(String fromMonth) {
        _fromMonth = fromMonth;
    }
	
	
	 /**
     * @return Returns the toMonth.
     */
    public String getToMonth() {
        return _toMonth;
    }

    /**
     * @param toMonth
     *            The toMonth to set.
     */
    public void setToMonth(String toMonth) {
        _toMonth = toMonth;
    }
	
	
    public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	
    
    public ArrayList getBankTypeList() {
		return bankTypeList;
	}

	public void setBankTypeList(ArrayList bankTypeList) {
		this.bankTypeList = bankTypeList;
	}

	 public String getProductMRP() {
			return productMRP;
		}

		public void setProductMRP(String productMRP) {
			this.productMRP = productMRP;
		}
		
		 /**
	     * @return Returns the voucherStatus.
	     */
	    public String getVoucherStatus() {
	        return _voucherStatus;
	    }

    /**
     * @param voucherStatus
     *            The voucherStatus to set.
     */
    public void setVoucherStatus(String voucherStatus) {
        _voucherStatus = voucherStatus;
    }

    
    
    
	 /**
    * @return Returns the statusList.
    */
   public ArrayList getStatusList() {
       return _statusList;
   }

   /**
    * @param statusList
    *            The statusList to set.
    */
   public void setStatusList(ArrayList statusList) {
       _statusList = statusList;
   }
    
    

			 /**
		* @return Returns the terminalTypeList.
		*/
		public ArrayList getTerminalTypeList() {
		return _terminalTypeList;
		}
		
		/**
		* @param terminalTypeList
		*            The terminalTypeList to set.
		*/
		public void setTerminalTypeList(ArrayList terminalTypeList) {
		_terminalTypeList = terminalTypeList;
		}
    

   /**
        * @return Returns the categoryListSize.
        */
       public int getCategoryListSize() {
           if (_categoryList != null) {
               return _categoryList.size();
           } else {
               return 0;
           }
       }
   	
     

   /**
        * @return Returns the categoryID.
        */
       public String getCategoryID() {
           return _categoryID;
       }

       /**
        * @param categoryID
        *            The categoryID to set.
        */
       public void setCategoryID(String categoryID) {
           _categoryID = categoryID;
       }	
    
    
       public long getTime() {
		       return _time;
		   }
		
		  
		   public void setTime(long p_time) {
		       _time = p_time;
		   }
			
			
	
	 public void setUserListSize(int userListSize) {
       _userListSize = userListSize;
   }
	
	
	 /**
    * @param userListSize
    *            The userListSize to set.
    */
   public void setUserListSize(ArrayList userList) {
       if (userList != null) {
           _userListSize = userList.size();
       } else {
           _userListSize = 0;
       }
   }
	
	 
	
	  public ArrayList getUserList() {
       return _userList;
   }

   public void setUserList(ArrayList userList) {
       _userList = userList;
   }
	
	
	public int getUserListSize() {
       if (_userList != null) {
           return _userList.size();
       }
       return 0;
   }
    
	public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }
    
    

/**
     * @return Returns the loggedInUserCategoryCode.
     */
    public String getLoggedInUserCategoryCode() {
        return _loggedInUserCategoryCode;
    }

    /**
     * @param loggedInUserCategoryCode
     *            The loggedInUserCategoryCode to set.
     */
    public void setLoggedInUserCategoryCode(String loggedInUserCategoryCode) {
        _loggedInUserCategoryCode = loggedInUserCategoryCode;
    }
	
	
	/**
     * @return Returns the loggedInUserName.
     */
    public String getLoggedInUserName() {
        return _loggedInUserName;
    }

    /**
     * @param loggedInUserName
     *            The loggedInUserName to set.
     */
    public void setLoggedInUserName(String loggedInUserName) {
        _loggedInUserName = loggedInUserName;
		
    }
	    private String _loginUserID = null;
		
	/**
     * @return Returns the loginUserID.
     */
    public String getLoginUserID() {
        return _loginUserID;
    }

    /**
     * @param loginUserID
     *            The loginUserID to set.
     */
    public void setLoginUserID(String loginUserID) {
        if (loginUserID != null) {
            _loginUserID = loginUserID.trim();
        }
    }	
    
    /**
     * @return Returns the userType.
     */
    public String getUserType() {
        return _userType;
    }

    /**
     * @param userType
     *            The userType to set.
     */
    public void setUserType(String userType) {
        _userType = userType;
    }
    
    public int getZoneListSize() {
        if (_zoneList != null) {
            return _zoneList.size();
        }
        return 0;
    }
	
/**
     * @return Returns the zoneList.
     */
    public ArrayList getZoneList() {
        return _zoneList;
    }

    /**
     * @param zoneList
     *            The zoneList to set.
     */
    public void setZoneList(ArrayList zoneList) {
        _zoneList = zoneList;
    }	
	
	
	
	
	
	 public String getZoneCode() {
        return _zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        _zoneCode = zoneCode;
    }
	
	
	public String getZoneCodeDesc() {
        return _zoneCodeDesc;
    }

    public void setZoneCodeDesc(String zoneCodeDesc) {
        _zoneCodeDesc = zoneCodeDesc;
    }
	
	
	
	
    /**
     * @return Returns the zoneName.
     */
    public String getZoneName() {
        return _zoneName;
    }

    /**
     * @param zoneName
     *            The zoneName to set.
     */
    public void setZoneName(String zoneName) {
        if (zoneName != null) {
            _zoneName = zoneName.trim();
        }
    }
	
	   
		
		 public int getDomainListSize() {
        if (_domainList != null) {
            return _domainList.size();
        }
        return 0;
    }
	
	
	public String getDomainCode() {
        return _domainCode;
    }

    public void setDomainCode(String domainCode) {
        _domainCode = domainCode;
    }

    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    public void setDomainCodeDesc(String domainCodeDesc) {
        _domainCodeDesc = domainCodeDesc;
    }
	
	
	
	
	  public ArrayList getDomainList() {
        return _domainList;
    }

    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }
	
	/**
     * @return Returns the domainName.
     */
    public String getDomainName() {
        return _domainName;
    }

    /**
     * @param domainName
     *            The domainName to set.
     */
    public void setDomainName(String domainName) {
        if (domainName != null) {
            _domainName = domainName.trim();
        }
    }
	
	/**
     * @return Returns the fromtransferCategoryCode.
     */
    public String getFromtransferCategoryCode() {
        return _fromtransferCategoryCode;
    }

    /**
     * @param fromtransferCategoryCode
     *            The fromtransferCategoryCode to set.
     */
    public void setFromtransferCategoryCode(String fromtransferCategoryCode) {
        _fromtransferCategoryCode = fromtransferCategoryCode;
    }
	
	 /**
     * @return Returns the fromCategoryList.
     */
    public ArrayList getFromCategoryList() {
        return _fromCategoryList;
    }

    /**
     * @param fromCategoryList
     *            The fromCategoryList to set.
     */
    public void setFromCategoryList(ArrayList fromCategoryList) {
        _fromCategoryList = fromCategoryList;
    }

	 public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        _userName = userName;
    }
    
    //end 
    
    public String getTotalDistributed() {
		return totalDistributed;
	}

	public void setTotalDistributed(String totalDistributed) {
		this.totalDistributed = totalDistributed;
	}

	public String getTotalConsumed() {
		return totalConsumed;
	}

	public void setTotalConsumed(String totalConsumed) {
		this.totalConsumed = totalConsumed;
	}

	
    public String getOptionalMSISDN() {
		return optionalMSISDN;
	}

	public void setOptionalMSISDN(String optionalMSISDN) {
		this.optionalMSISDN = optionalMSISDN;
	}

	public String getOptionalCategoryCodeForProduct() {
		return optionalCategoryCodeForProduct;
	}

	public void setOptionalCategoryCodeForProduct(
			String optionalCategoryCodeForProduct) {
		this.optionalCategoryCodeForProduct = optionalCategoryCodeForProduct;
	}

	public String getOptionalProductID() {
		return optionalProductID;
	}

	public void setOptionalProductID(String optionalProductID) {
		this.optionalProductID = optionalProductID;
	}

    
    
    public String getBurnRate() {
		return burnRate;
	}

	public void setBurnRate(String burnRate) {
		this.burnRate = burnRate;
	}

	public String getDistributedToDate() {
		return distributedToDate;
	}

	public void setDistributedToDate(String distributedToDate) {
		this.distributedToDate = distributedToDate;
	}

	public String getDistributedFromDate() {
		return distributedFromDate;
	}

	public void setDistributedFromDate(String distributedFromDate) {
		this.distributedFromDate = distributedFromDate;
	}

	public String getConsumedToDate() {
		return consumedToDate;
	}

	public void setConsumedToDate(String consumedToDate) {
		this.consumedToDate = consumedToDate;
	}

	public String getConsumedFromDate() {
		return consumedFromDate;
	}

	public void setConsumedFromDate(String consumedFromDate) {
		this.consumedFromDate = consumedFromDate;
	}



    public ArrayList getVoucherTypeList() {
        return _voucherTypeList;
    }

    public void setVoucherTypeList(ArrayList type) {
        _voucherTypeList = type;
    }

    public int getVoucherTypeListSize() {
        if (_voucherTypeList != null) {
            return _voucherTypeList.size();
        } else {
            return 0;
        }
    }

    public void setVoucherTypeListSize(int typeListSize) {
        _voucherTypeListSize = typeListSize;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getServiceTypeMapping() {
        return _serviceTypeMapping;
    }

    public void setServiceTypeMapping(String typeMapping) {
        _serviceTypeMapping = typeMapping;
    }

    //
    /**
     * Method flush.
     * Flush the contents of the form bean
     */
    public void flush() {
        _mrp = null;
        _productName = null;
        _serialNo = null;
        _productID = null;
        _fromSerial = null;
        _toSerial = null;
        _productList = null;
        _talkTime = null;
        _validity = null;
        _categoryList = null;
        _fromSerial = null;
        _voucherType = null;
        _categoryCodeForProduct = null;
        _selectorList = null;
        _selector = null;
        bankTypeList=null;

    }// end of flush
    
    
    public void semiFlush() {
        
        setUserID(null);
        
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getSerialNo() {
        return _serialNo;
    }

    public void setSerialNo(String serialNo) {
        _serialNo = serialNo;
    }

    /**
     * @return Returns the log.
     */
    public Log getLog() {
        return _log;
    }

    /**
     * @param log
     *            The log to set.
     */
    public void setLog(Log log) {
        _log = log;
    }

    /**
     * @return Returns the productID.
     */
    public String getProductID() {
        return _productID;
    }

    /**
     * @param productID
     *            The productID to set.
     */
    public void setProductID(String productID) {
        _productID = productID;
    }

    /**
     * @return Returns the productIDName.
     */
    public String getProductIDName() {
        return _ProductIDName;
    }

    /**
     * @param productIDName
     *            The productIDName to set.
     */
    public void setProductIDName(String productIDName) {
        _ProductIDName = productIDName;
    }

    /**
     * @return Returns the productList.
     */
    public ArrayList getProductList() {
        return _productList;
    }

    /**
     * @param productList
     *            The productList to set.
     */
    public void setProductList(ArrayList productList) {
        _productList = productList;
    }

    /**
     * @return Returns the categoryList.
     */
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    /**
     * @param categoryList
     *            The categoryList to set.
     */
    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    /**
     * @return Returns the categoryCodeForProduct.
     */
    public String getCategoryCodeForProduct() {
        return _categoryCodeForProduct;
    }

    /**
     * @param categoryCodeForProduct
     *            The categoryCodeForProduct to set.
     */
    public void setCategoryCodeForProduct(String categoryCodeForProduct) {
        _categoryCodeForProduct = categoryCodeForProduct;
    }

    /**
     * @return Returns the fromSerial.
     */
    public String getFromSerial() {
        return _fromSerial;
    }

    /**
     * @param fromSerial
     *            The fromSerial to set.
     */
    public void setFromSerial(String fromSerial) {
        _fromSerial = fromSerial;
    }

    /**
     * @return Returns the toSerial.
     */
    public String getToSerial() {
        return _toSerial;
    }

    /**
     * @param toSerial
     *            The toSerial to set.
     */
    public void setToSerial(String toSerial) {
        _toSerial = toSerial;
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
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * @return Returns the reportHeaderName.
     */
    public String getReportHeaderName() {
        return _reportHeaderName;
    }

    /**
     * @param reportHeaderName
     *            The reportHeaderName to set.
     */
    public void setReportHeaderName(String reportHeaderName) {
        _reportHeaderName = reportHeaderName;
    }

    /**
     * @return Returns the categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            The productName to set.
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getTalkTime() {
        return _talkTime;
    }

    public void setTalkTime(String talkTime) {
        _talkTime = talkTime;
    }

    public String getValidity() {
        return _validity;
    }

    public void setValidity(String validity) {
        _validity = validity;
    }

    public String getMrp() {
        return _mrp;
    }

    public void setMrp(String mrp) {
        _mrp = mrp;
    }

    public String getConsumdOn() {
        return _consumdOn;
    }

    public void setConsumdOn(String consumdOn) {
        _consumdOn = consumdOn;
    }

    public String getConsumptionCircle() {
        return _consumptionCircle;
    }

    public void setConsumptionCircle(String consumptionCircle) {
        _consumptionCircle = consumptionCircle;
    }

    public String getDenomination() {
        return _denomination;
    }

    public void setDenomination(String denomination) {
        _denomination = denomination;
    }

    public String getEnabldOn() {
        return _enabldOn;
    }

    public void setEnabldOn(String enabldOn) {
        _enabldOn = enabldOn;
    }

    public String getNewStatus() {
        return _newStatus;
    }

    public void setNewStatus(String newStatus) {
        _newStatus = newStatus;
    }

    public String getPreStatus() {
        return _preStatus;
    }

    public void setPreStatus(String preStatus) {
        _preStatus = preStatus;
    }

    public String getProdCircle() {
        return _prodCircle;
    }

    public void setProdCircle(String prodCircle) {
        _prodCircle = prodCircle;
    }

    public String getProfile() {
        return _profile;
    }

    public void setProfile(String profile) {
        _profile = profile;
    }

    public String getPrevStatus() {
        return _prevStatus;
    }

    public void setPrevStatus(String prevStatus) {
        _prevStatus = prevStatus;
    }

    public String getCreatedOn() {
        return _createdOn;
    }

    public void setCreatedOn(String createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the typeList.
     */
    public ArrayList getTypeList() {
        return _typeList;
    }

    /**
     * @param typeList
     *            The typeList to set.
     */
    public void setTypeList(ArrayList typeList) {
        _typeList = typeList;
    }

    /**
     * @return Returns the voucherType.
     */
    public String getVoucherType() {
        return _voucherType;
    }

    /**
     * @param voucherType
     *            The voucherType to set.
     */
    public void setVoucherType(String voucherType) {
        _voucherType = voucherType;
    }

    /**
     * @return Returns the radioNetCode.
     */
    public String getRadioNetCode() {
        return _radioNetCode;
    }

    /**
     * @param radioNetCode
     *            The radioNetCode to set.
     */
    public void setRadioNetCode(String radioNetCode) {
        _radioNetCode = radioNetCode;
    }

    /**
     * @return Returns the statusString.
     */
    public String getStatusString() {
        return _statusString;
    }

    /**
     * @param statusString
     *            The statusString to set.
     */
    public void setStatusString(String statusString) {
        _statusString = statusString;
    }

    /**
     * @return Returns the statusName.
     */
    public String getStatusName() {
        return _statusName;
    }

    /**
     * @param statusName
     *            The statusName to set.
     */
    public void setStatusName(String statusName) {
        _statusName = statusName;
    }

    // added by siddhartha

    /**
     * This method returns the fromDate
     */

    public String getFromDate() {
        return _fromDate;
    }

    public void setFromDate(String fromDate) {
        this._fromDate = fromDate;
    }

    /**
     * This method returns the toDate entered for searching the reconciled
     * vouchers
     * 
     * @return string
     */

    public String getToDate() {
        return _toDate;
    }

    public void setToDate(String toDate) {
        this._toDate = toDate;
    }

    /**
     * This method returns the list of reconciled vouchers
     * 
     * @return arraylist
     */

    public ArrayList getVoucherList() {
        return _voucherList;
    }

    public void setVoucherList(ArrayList voucherList) {
        this._voucherList = voucherList;
    }

    /**
     * This method returns the last consumed date of the voucher
     * 
     * @return string
     */

    public String getLastConsumedOn() {
        return _lastConsumedOn;
    }

    public void setLastConsumedOn(String lastConsumedOn) {
        this._lastConsumedOn = lastConsumedOn;
    }

    /**
     * This method returns the transaction id for the voucher when it was
     * consumed
     * 
     * @return string
     */

    public String getTransactionID() {
        return _transactionID;
    }

    public void setTransactionID(String transactionID) {
        this._transactionID = transactionID;
    }

    /**
     * This method returns the applicable from format
     * 
     * @return string
     */

    public String getApplicableFromFormat() {
        return applicableFromFormat;
    }

    public void setApplicableFromFormat(String applicableFromFormat) {
        this.applicableFromFormat = applicableFromFormat;
    }

    public String getSenderMsisdn() {
        return _senderMsisdn;
    }

    public void setSenderMsisdn(String senderMsisdn) {
        _senderMsisdn = senderMsisdn;
    }

    /**
     * @return the selectorList
     */
    public ArrayList getSelectorList() {
        return _selectorList;
    }

    /**
     * @param selectorList
     *            the selectorList to set
     */
    public void setSelectorList(ArrayList selectorList) {
        _selectorList = selectorList;
    }

    /**
     * @return the selectorListSize
     */
    public int getSelectorListSize() {
        return _selectorList.size();
    }

    /**
     * @param selectorListSize
     *            the selectorListSize to set
     */
    public void setSelectorListSize(int selectorListSize) {
        _selectorListSize = selectorListSize;
    }

    /**
     * @return the selector
     */
    public String getSelector() {
        return _selector;
    }

    /**
     * @param selector
     *            the selector to set
     */
    public void setSelector(String selector) {
        _selector = selector;
    }

    /**
     * @return the selectorName
     */
    public String getSelectorName() {
        return _selectorName;
    }

    /**
     * @param selectorName
     *            the selectorName to set
     */
    public void setSelectorName(String selectorName) {
        _selectorName = selectorName;
    }

    public String getVoucherTypeDesc() {
        return _VoucherTypeDesc;
    }

    public void setVoucherTypeDesc(String voucherTypeDesc) {
        _VoucherTypeDesc = voucherTypeDesc;
    }

    public int getProductListSize() {
        if (_productList != null) {
            return _productList.size();
        } else {
            return 0;
        }
    }

    public void setProductListSize(int productListSize) {
        _productListSize = productListSize;
    }

    public String getType() {
        return _type;
    }

    public void setType(String statusType) {
        _type = statusType;
    }

    public String getFromTime() {
        return _fromTime;
    }
    
    /**
     * @return Returns the toTime.
     */
    public String getToTime() {
        return _toTime;
    }

    /**
     * @param toTime
     *            The toTime to set.
     */
    public void setToTime(String toTime) {
        _toTime = toTime;
    }


    /**
     * @param fromTime
     *            The fromTime to set.
     */
    public void setFromTime(String fromTime) {
        _fromTime = fromTime;
    }
    
    public Date getFromDateTime() {
        return _fromDateTime;
    }

    /**
     * @param fromDateTime
     *            The fromDateTime to set.
     */
    public void setFromDateTime(Date fromDateTime) {
        _fromDateTime = fromDateTime;
    }

    /**
     * @return Returns the toDateTime.
     */
    public Date getToDateTime() {
        return _toDateTime;
    }

    /**
     * @param toDateTime
     *            The toDateTime to set.
     */
    public void setToDateTime(Date toDateTime) {
        _toDateTime = toDateTime;
    }
    
    /**
     * @return Returns the domainListString.
     */
    public String getDomainListString() {
        return _domainListString;
    }

    /**
     * @param domainListString
     *            The domainListString to set.
     */
    public void setDomainListString(String domainListString) {
        _domainListString = domainListString;
    }
    

    /**
     * @param dailyDate
     *            The dailyDate to set.
     */
   /* public void setDailyDate(String dailyDate) {
        _dailyDate = dailyDate;
    }*/

    /**
     * @return Returns the fromMonth.
     */
    public String getFromMonth() {
        return _fromMonth;
    }

    /**
     * @return Returns the tempfromDate.
     */
    public String getTempfromDate() {
        return _tempfromDate;
    }

    /**
     * @param tempfromDate
     *            The tempfromDate to set.
     */
    public void setTempfromDate(String tempfromDate) {
        _tempfromDate = tempfromDate;
    }

    
    public String getTemptoDate() {
        return _temptoDate;
    }

    public void setTemptoDate(String temptoDate) {
        _temptoDate = temptoDate;
    }


	public String getVoucherSegment() {
		return _voucherSegment;
	}


	public void setVoucherSegment(String _voucherSegment) {
		this._voucherSegment = _voucherSegment;
	}


	public String getExpiryDate() {
		return _expiryDate;
	}


	public void setExpiryDate(String _expiryDate) {
		this._expiryDate = _expiryDate;
	}


}
