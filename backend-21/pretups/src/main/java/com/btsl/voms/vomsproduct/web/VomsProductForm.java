package com.btsl.voms.vomsproduct.web;

import java.text.ParseException;
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
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsActiveProductItemVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsActiveProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.ibm.icu.util.Calendar;

/**
 * @(#)VomsProductForm.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Amit Singh 22/06/2006 Initial Creation
 * 
 *                          This form bean is used for Product(profile)
 *                          information of the EVD system.
 */

public class VomsProductForm {

    
	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(VomsProductForm.class.getName());
    private ArrayList _categoryTypeList;
    private String _mrp;
    private String _productName;
    private String _productID;
    private String _minQty;
    private String _maxQty;
    private String _productDescription;
    private String _talkTime;
    private String _time;
    private String _validity;
    private ArrayList _categoryList;// for storing the MRP list
    private String _categoryID;
    private String _networkName;
    private String _applicableFrom;
    private String _applicableFromFormat;
    private String _applicableFromLength;
    private Date _applicableFromDate;
    private String _status;
    private String _statusDesc;
    private String _requestType;
    private long _modifiedOn;
    private String _categoryName;
    private ArrayList _productList;
    private ArrayList _activeProductList;
    private ArrayList _mrpWithProductsList;
    private ArrayList _mrpProductConfirmList;
    private ArrayList _statusList;
    private int _radioIndex;
    private String _radioVal;
    private String _showBack;
    private String _currentServerTime;
    private String _activeProductID;
    private VomsActiveProductVO _vomsActiveProductVO;
    private String _productShortName;
    private String expiryPeriodStr = null;
    private String voucherAutoGenerate = null;
    private String serviceName = null;
    public String getVoucherAutoGenerate() {
		return voucherAutoGenerate;
	}

	public void setVoucherAutoGenerate(String voucherAutoGenerate) {
		this.voucherAutoGenerate = voucherAutoGenerate;
	}

	private String voucherGenerateQuantity = null;
    public String getVoucherGenerateQuantity() {
		return voucherGenerateQuantity;
	}

	public void setVoucherGenerateQuantity(String voucherGenerateQuantity) {
		this.voucherGenerateQuantity = voucherGenerateQuantity;
	}

	private String voucherThreshold = null;
	private ArrayList segmentList;
	private String segment;
	private String segmentDesc;

	// Added by Anjali 07/07/2010
    private String _type = null;
    private ArrayList _typeList = null;
    private String _typeDesc = null;
    private int _typeListSize = 0;
    public String getExpiryPeriodStr() {
		return expiryPeriodStr;
	}

	public void setExpiryPeriodStr(String expiryPeriodStr) {
		this.expiryPeriodStr = expiryPeriodStr;
	}

	private String _typeCode = null;
    private int _loadTypeListSize;
    private long expiryPeriod;
    // voms: akanksha
    private String _voucherType = null;
    private ArrayList<VomsCategoryVO> _voucherTypeList;
    private String _voucherTypeDesc = null;
    private ArrayList<String> _serviceList = null;
    private int _serviceListSize;
    private String _service = null;
    private int _serviceID;
    private Date _expiryDate=null;
    private String expiryDateString=null;
	//Added for voms_hcpt by niharika
	private String _itemCode;
    private String _secondaryPrefixCode;
    // End

    /**
     * Method flush.
     * Flush the contents of the form bean
     */
    public void flush() {
        if (logger.isDebugEnabled()) {
            logger.debug("Flush", "Entered");
        }
        _mrp = null;
        _productName = null;
        _categoryID = null;
        _productID = null;
        _minQty = null;
        _maxQty = null;
        _productDescription = null;
        _talkTime = null;
        _validity = null;
        _applicableFrom = null;
        _showBack = null;
        _activeProductID = null;
        _activeProductList = null;
        _categoryList = null;
        _mrpWithProductsList = null;
        _mrpProductConfirmList = null;
        _requestType = null;
        _productShortName = null;
        _productList = null;
        // Added by Anjali 07/07/2010
        _type = null;
        _typeDesc = null;
        _typeList = null;
        voucherAutoGenerate=null;
        voucherGenerateQuantity=null;
        voucherThreshold=null;
        _voucherType = null;
        _voucherName = null;
        _serviceTypeMapping = null;
        _statusName = null;
        _statusName = null;
        _modifyFlag = null;
        _networkCode = null;
        _servicesList = null;
        _loginUserName = null;
        expiryPeriodStr = null;
        expiryDateString = null;
        _userId = null;
		_itemCode=null;
    	_secondaryPrefixCode=null;
        if (logger.isDebugEnabled()) {
            logger.debug("Flush", "Exited");
            // End
        }
    }// end of flush

    /**
     * @return Returns the vomsActiveProductVO.
     */
    public VomsActiveProductVO getVomsActiveProductVO() {
        return _vomsActiveProductVO;
    }

    /**
     * @param vomsActiveProductVO
     *            The vomsActiveProductVO to set.
     */
    public void setVomsActiveProductVO(VomsActiveProductVO vomsActiveProductVO) {
        _vomsActiveProductVO = vomsActiveProductVO;
    }

    /**
     * @return Returns the activeProductID.
     */
    public String getActiveProductID() {
        return _activeProductID;
    }

    /**
     * @param activeProductID
     *            The activeProductID to set.
     */
    public void setActiveProductID(String activeProductID) {
        _activeProductID = activeProductID;
    }

    /**
     * @return Returns the activeProductList.
     */
    public ArrayList getActiveProductList() {
        return _activeProductList;
    }

    /**
     * @param activeProductList
     *            The activeProductList to set.
     */
    public void setActiveProductList(ArrayList activeProductList) {
        _activeProductList = activeProductList;
    }

    /**
     * @return Returns the applicableFromLength.
     */
    public String getApplicableFromLength() {
        return _applicableFromLength;
    }

    /**
     * @param applicableFromLength
     *            The applicableFromLength to set.
     */
    public void setApplicableFromLength(String applicableFromLength) {
        _applicableFromLength = applicableFromLength;
    }

    /**
     * @return Returns the applicableFromFormat.
     */
    public String getApplicableFromFormat() {
        return _applicableFromFormat;
    }

    /**
     * @param applicableFromFormat
     *            The applicableFromFormat to set.
     */
    public void setApplicableFromFormat(String applicableFromFormat) {
        _applicableFromFormat = applicableFromFormat;
    }

    /**
     * @return Returns the applicableFromDate.
     */
    public Date getApplicableFromDate() {
        return _applicableFromDate;
    }

    /**
     * @param applicableFromDate
     *            The applicableFromDate to set.
     */
    public void setApplicableFromDate(Date applicableFromDate) {
        _applicableFromDate = applicableFromDate;
    }

    /**
     * @return Returns the currentServerTime.
     */
    public String getCurrentServerTime() {
        return _currentServerTime;
    }

    /**
     * @param currentServerTime
     *            The currentServerTime to set.
     */
    public void setCurrentServerTime(String currentServerTime) {
        _currentServerTime = currentServerTime;
    }

    /**
     * @return Returns the statusDesc.
     */
    public String getStatusDesc() {
        return _statusDesc;
    }

    /**
     * @param statusDesc
     *            The statusDesc to set.
     */
    public void setStatusDesc(String statusDesc) {
        _statusDesc = statusDesc;
    }

    /**
     * @return Returns the showBack.
     */
    public String getShowBack() {
        return _showBack;
    }

    /**
     * @param showBack
     *            The showBack to set.
     */
    public void setShowBack(String showBack) {
        _showBack = showBack;
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
     * @return Returns the radioVal.
     */
    public String getRadioVal() {
        return _radioVal;
    }

    /**
     * @param radioVal
     *            The radioVal to set.
     */
    public void setRadioVal(String radioVal) {
        _radioVal = radioVal;
    }

    /**
     * @return Returns the modifiedOn.
     */
    public long getModifiedOn() {
        return _modifiedOn;
    }

    /**
     * @param modifiedOn
     *            The modifiedOn to set.
     */
    public void setModifiedOn(long modifiedOn) {
        _modifiedOn = modifiedOn;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the mrpProductConfirmList.
     */
    public ArrayList getMrpProductConfirmList() {
        return _mrpProductConfirmList;
    }

    /**
     * @param mrpProductConfirmList
     *            The mrpProductConfirmList to set.
     */
    public void setMrpProductConfirmList(ArrayList mrpProductConfirmList) {
        _mrpProductConfirmList = mrpProductConfirmList;
    }

    /**
     * @return Returns the mrpWithProductsList.
     */
    public VomsActiveProductItemVO getVomsActPrdItemVOForAddIndexed(int i) {
        if (this._mrpWithProductsList != null) {
            return (VomsActiveProductItemVO) this._mrpWithProductsList.get(i);
        }
        return null;
    }

    /**
     * @param mrpWithProductsList
     *            The mrpWithProductsList to set.
     */
    public void setVomsActPrdItemVOForAddIndexed(int i, VomsActiveProductItemVO vomsActiveProductItemVO) {
        if (this._mrpWithProductsList != null) {
            this._mrpWithProductsList.set(i, vomsActiveProductItemVO);
        }
    }

    /**
     * @return Returns the mrpWithProductsList.
     */
    public VomsActiveProductItemVO getVomsActPrdItemVOIndexed(int i) {
        if (this._mrpWithProductsList != null) {
            return (VomsActiveProductItemVO) this._mrpWithProductsList.get(i);
        }
        return null;
    }

    /**
     * @param mrpWithProductsList
     *            The mrpWithProductsList to set.
     */
    public void setVomsActPrdItemVOIndexed(int i, VomsActiveProductItemVO vomsActiveProductItemVO) {
        if (this._mrpWithProductsList != null) {
            this._mrpWithProductsList.set(i, vomsActiveProductItemVO);
        }
    }

    /**
     * @param mrpWithProductsList
     *            The mrpWithProductsList to set.
     */
    public void setVomsActPrdItemVOForDelIndexed(int i, VomsActiveProductItemVO vomsActiveProductItemVO) {
        if (this._mrpWithProductsList != null) {
            this._mrpWithProductsList.set(i, vomsActiveProductItemVO);
        }
    }

    public VomsActiveProductItemVO getVomsActPrdItemVOForDelIndexed(int i) {
        if (this._mrpWithProductsList != null) {
            return (VomsActiveProductItemVO) this._mrpWithProductsList.get(i);
        }
        return null;
    }

    /**
     * @return Returns the mrpWithProductsList.
     */
    public ArrayList getMrpWithProductsList() {
        return _mrpWithProductsList;
    }

    /**
     * @param mrpWithProductsList
     *            The mrpWithProductsList to set.
     */
    public void setMrpWithProductsList(ArrayList mrpWithProductsList) {
        _mrpWithProductsList = mrpWithProductsList;
    }

    /**
     * @return Returns the applicableFrom.
     */
    public String getApplicableFrom() {
        return _applicableFrom;
    }

    /**
     * @param applicableFrom
     *            The applicableFrom to set.
     */
    public void setApplicableFrom(String applicableFrom) {
        _applicableFrom = applicableFrom;
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

    /**
     * @return Returns the maxQty.
     */
    public String getMaxQty() {
        return _maxQty;
    }

    /**
     * @param maxQty
     *            The maxQty to set.
     */
    public void setMaxQty(String maxQty) {
        _maxQty = maxQty;
    }

    /**
     * @return Returns the minQty.
     */
    public String getMinQty() {
        return _minQty;
    }

    /**
     * @param minQty
     *            The minQty to set.
     */
    public void setMinQty(String minQty) {
        _minQty = minQty;
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
     * @return Returns the categoryListSize.
     */
    public int getProductListSize() {
        if (_productList != null) {
            return _productList.size();
        } else {
            return 0;
        }
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
     * @return Returns the mrp.
     */
    public String getMrp() {
        return _mrp;
    }

    /**
     * @param mrp
     *            The mrp to set.
     */
    public void setMrp(String mrp) {
        _mrp = mrp;
    }

    /**
     * @return Returns the productDescription.
     */
    public String getProductDescription() {
        return _productDescription;
    }

    /**
     * @param productDescription
     *            The productDescription to set.
     */
    public void setProductDescription(String productDescription) {
        _productDescription = productDescription;
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

    /**
     * @return Returns the talkTime.
     */
    public String getTalkTime() {
        return _talkTime;
    }

    /**
     * @param talkTime
     *            The talkTime to set.
     */
    public void setTalkTime(String talkTime) {
        _talkTime = talkTime;
    }

    /**
     * @return Returns the validity.
     */
    public String getValidity() {
        return _validity;
    }

    /**
     * @param validity
     *            The validity to set.
     */
    public void setValidity(String validity) {
        _validity = validity;
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

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    public int getRadioIndex() {
        return _radioIndex;
    }

    public void setRadioIndex(int radioIndex) {
        _radioIndex = radioIndex;
    }

    public String getTime() {
        return _time;
    }

    public void setTime(String time) {
        _time = time;
    }

    public String getProductShortName() {
        return _productShortName;
    }

    public void setProductShortName(String productShortName) {
        _productShortName = productShortName;
    }

    /**
     * @return the typeListSize
     */
    public int getTypeListSize() {
        return _typeList.size();
    }

    /**
     * @param typeListSize
     *            the typeListSize to set
     */
    public void setTypeListSize(int typeListSize) {
        _typeListSize = typeListSize;
    }

    /**
     * @return the typeList
     */
    public ArrayList getTypeList() {
        return _typeList;
    }

    /**
     * @param typeList
     *            the typeList to set
     */
    public void setTypeList(ArrayList typeList) {
        _typeList = typeList;
    }

    /**
     * @return the type
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return the typeDesc
     */
    public String getTypeDesc() {
        return _typeDesc;
    }

    /**
     * @param typeDesc
     *            the typeDesc to set
     */
    public void setTypeDesc(String typeDesc) {
        _typeDesc = typeDesc;
    }

    public ArrayList getCategoryTypeList() {
        return _categoryTypeList;
    }

    /**
     * @param categoryTypeList
     *            The categoryTypeList to set.
     */
    public void setCategoryTypeList(ArrayList categoryTypeList) {
        _categoryTypeList = categoryTypeList;
    }

    /**
     * @return the typeCode
     */
    public String getTypeCode() {
        return _typeCode;
    }

    /**
     * @param typeCode
     *            the typeCode to set
     */
    public void setTypeCode(String typeCode) {
        _typeCode = typeCode;
    }

    /**
     * @return the loadTypeListSize
     */
    public int getLoadTypeListSize() {
        return _loadTypeListSize;
    }

    /**
     * @param loadTypeListSize
     *            the loadTypeListSize to set
     */
    public void setLoadTypeListSize(int loadTypeListSize) {
        _loadTypeListSize = loadTypeListSize;
    }

    /**
     * @return the expiryPeriod
     */
    public long getExpiryPeriod() {
        return expiryPeriod;
    }

    /**
     * @param expiryPeriod
     *            the expiryPeriod to set
     */
    public void setExpiryPeriod(long expiryPeriod) {
        this.expiryPeriod = expiryPeriod;
    }

    /******************************** added by Ashutosh for reinventing the voucher management module ******************************************************/

    private String _voucherName;
    private String _serviceTypeMapping;
    private String _statusName;
    private String _modifyFlag;
    private String _networkCode;
    private long _lastModified;
    private ArrayList _servicesList;
    private String _loginUserName;
    private String _userId;
    private int _allowedServicesListSize;

    private ArrayList _voucherList;
    private ArrayList _allowedServicesList;
    private String[] _selectedServices = {};

    public String getVoucherType() {
        return _voucherType;
    }

    public void setVoucherType(String type) {
        _voucherType = type;
    }

    public String getVoucherName() {
        return _voucherName;
    }

    public ArrayList<VomsCategoryVO> getVoucherTypeList() {
        return _voucherTypeList;
    }

    public void setVoucherTypeList(ArrayList<VomsCategoryVO> typeList) {
        _voucherTypeList = typeList;
    }

    public void setVoucherName(String name) {
        _voucherName = name;
    }

    public String getServiceTypeMapping() {
        return _serviceTypeMapping;
    }

    public void setServiceTypeMapping(String typeMapping) {
        _serviceTypeMapping = typeMapping;
    }

    public String getStatusName() {
        return _statusName;
    }

    public void setStatusName(String name) {
        _statusName = name;
    }

    public ArrayList getVoucherList() {
        return _voucherList;
    }

    public String getVoucherTypeDesc() {
        return _voucherTypeDesc;
    }

    public void setVoucherTypeDesc(String typeDesc) {
        _voucherTypeDesc = typeDesc;
    }

    public void setVoucherList(ArrayList list) {
        _voucherList = list;
    }

    public int getVoucherTypeListSize() {
        if (_voucherTypeList != null) {
            return _voucherTypeList.size();
        } else {
            return 0;
        }
    }

    public int getVoucherListSize() {
        if (_voucherList != null && _voucherList.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getSegmentListSize() {
        if (segmentList != null) {
            return segmentList.size();
        } else {
            return 0;
        }
    }
    
    public String getModifyFlag() {
        return _modifyFlag;
    }

    /**
     * To set the value of modifyFlag field
     */
    public void setModifyFlag(String modifyFlag) {
        _modifyFlag = modifyFlag;
    }

    public String getLoginUserName() {
        return _loginUserName;
    }

    /**
     * To set the value of loginUserName field
     */
    public void setLoginUserName(String loginUserName) {
        _loginUserName = loginUserName;
    }

    public String getUserId() {
        return _userId;
    }

    /**
     * To set the value of userId field
     */
    public void setUserId(String userId) {
        _userId = userId;
    }

    /**
     * To get the value of statusList field
     * 
     * @return statusList.
     */

    public void semiFlush() {
        if (logger.isDebugEnabled()) {
            logger.debug("semiFlush", "Entered");
        }
        _voucherType = null;
        _voucherName = null;
        _serviceTypeMapping = null;
        _statusName = null;
        
        _type = null;
        if (logger.isDebugEnabled()) {
            logger.debug("semiFlush", "Exited");
        }

    }

    /**
     * To get the value of networkCode field
     * 
     * @return networkCode.
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * To set the value of networkCode field
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public long getLastModified() {
        return _lastModified;
    }

    /**
     * To set the value of lastModified field
     */
    public void setLastModified(long lastModified) {
        _lastModified = lastModified;
    }

    public ArrayList getServicesList() {
        return _servicesList;
    }

    public void setServicesList(ArrayList list) {
        _servicesList = list;
    }

    public ArrayList getAllowedServicesList() {
        return _allowedServicesList;
    }

    public void setAllowedServicesList(ArrayList list) {
        _allowedServicesList = list;
    }

    public String[] getSelectedServices() {
        return _selectedServices;
    }

    public void setSelectedServices(String[] gates) {
        _selectedServices = gates;
    }

    public int getAllowedServicesListSize() {
        return _allowedServicesListSize;
    }

    public void setAllowedServicesListSize(int len) {
        _allowedServicesListSize = len;
    }

    public ArrayList getServiceList() {
        return _serviceList;
    }

    public void setServiceList(ArrayList list) {
        _serviceList = list;
    }

    public int getServiceListSize() {
        if (_serviceList != null) {
            return _serviceList.size();
        } else {
            return 0;
        }
    }

    public void setServiceListSize(int serviceListSize) {
        _serviceListSize = serviceListSize;
    }

    /**
     * @return the service
     */
    public String getService() {
        return _service;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(String service) {
        _service = service;
    }

    public int getServiceID() {
        return _serviceID;
    }

    public void setServiceID(int _serviceid) {
        _serviceID = _serviceid;
    }
    public Date getExpiryDate() {
  		return _expiryDate;
  	}

  	public void setExpiryDate(Date expiryDate) {
  		this._expiryDate = expiryDate;
  	}
  	
  	public String getExpiryDateString() {
  		return expiryDateString;
  	}

  	public void setExpiryDateString(String expiryDateString) {
  		this.expiryDateString = expiryDateString;
  	}
  
	

	
    
	public String getVoucherThreshold() {
		return voucherThreshold;
	}

	public void setVoucherThreshold(String voucherThreshold) {
		this.voucherThreshold = voucherThreshold;
	}

	public ArrayList getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(ArrayList segmentList) {
		this.segmentList = segmentList;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getSegmentDesc() {
		return segmentDesc;
	}

	public void setSegmentDesc(String segmentDesc) {
		this.segmentDesc = segmentDesc;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	//Added for voms_hcpt by niharika
    public String getItemCode() {
		return _itemCode;
	}

	public void setItemCode(String itemCode) {
		this._itemCode = itemCode;
	}

	public String getSecondaryPrefixCode() {
		return _secondaryPrefixCode;
	}

	public void setSecondaryPrefixCode(String secondaryPrefixCode) {
		this._secondaryPrefixCode = secondaryPrefixCode;
	}
}
