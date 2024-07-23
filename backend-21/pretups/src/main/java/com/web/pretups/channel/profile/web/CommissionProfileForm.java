package com.web.pretups.channel.profile.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)CommissionProfileForm.java Copyright(c) 2005, Bharti Telesoft Ltd. All
 *                                Rights Reserved
 * 
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Mohit Goel 24/08/2005 Initial Creation Samna
 *                                Soin 19/10/2011 Modified
 * 
 *                                This class is used for Commission Profile
 *                                Insertion/Updation
 * 
 */
public class CommissionProfileForm {

    private Log _log = LogFactory.getLog(CommissionProfileForm.class.getName());
    private String _requestType = null;
    private int _locationIndex;
    public static final String CAC="CAC";
    public static final String CBC="CBC";
    private String _networkName = null;
    private String _domainCode = null;
    private String _domainCodeDesc = null;
    private ArrayList _domainList;

    private String _gatewayCode = null;
    private ArrayList _gatewayList;
    private String[] _gatewayArray;
    private String[] _selectedGateways = {};
    private String _gatewayType;
    private long _time;

    private String _categoryCode = null;
    private String _categoryCodeDesc = null;
    private ArrayList _categoryList;

    private String _grphDomainCodeDesc = null;
    private String _gradeCodeDesc = null;
    private ArrayList _geographyList;
    private String _grphDomainCode;
    private String _grphDomainName;
    private String _grphDomainType;

    private String _version;
    private String _profileName = null;
    private String _shortCode = null;
    private String _applicableFromDate = null;
    
    private String _applicableFromHour = null;
    private String _oldApplicableFromDate;
    private String _oldApplicableFromHour;
    private ArrayList _commissionProfileList;
    private String _showAdditionalCommissionFlag;
    private ArrayList _additionalProfileList;
    private ArrayList _otfProfileList;
    private String _productCode = null;
    private String _productCodeDesc = null;
    private ArrayList _productList;
    private String _minTransferValue = null;
    private String _maxTransferValue = null;
    private String _multipleOf = null;
    private String _taxCalculatedOnFlag = null;
    private String _taxOnFOCFlag = null;
    private String _maxDiscountAllowed = null;
    private String _maxDiscountType = null;
    private String _serviceCode = null;
    private String _serviceCodeDesc = null;
    private ArrayList _serviceList;

    private ArrayList _amountTypeList;
    private ArrayList _slabsList;// contains VO's of
    private String _selectCommProfileSetID;
    private ArrayList _selectCommProfileSetList;
    private String _selectCommProifleVersionID = null;
    private ArrayList _selectCommProfileVersionList;

    private String _numberOfDays;

    private boolean _deleteAllowed = false;
    private String _code;
    private String _oldCode;

    private String _domainName;
    private String _categoryName;
    private String _networkCode = null;
    private ArrayList _domainAllList = null;
    private ArrayList _parentCategoryList = null;
    private String _parentCategoryCode;
    private String _fromDate;
    private String _toDate;
    private String _addtnlComStatus;
    private String _addtnlComStatusName;

    private String _roamRecharge;

   // private FormFile _file;
    private String _fileName = null;
    private String _date;
    private ArrayList _errorList = null;
    private String _errorFlag;
    private String sheetName;
    private String _setID;
    private String _batchName;
    private int _domainListSize;
    private int length;

    private ArrayList _subServiceList;
    private String _serviceType;
    private String _subServiceCode = null;
    private String _subServiceDesc = null;

    private String _gradeCode;
    private String _gradeName;
    private ArrayList _gradeList;
    private String _additionalCommissionTimeSlab;
    private String _applicableFromAdditional;
    private String _applicableToAdditional;
	private String _sequenceNo;
	private ArrayList amountCountTypeList;
	private String searchMsisdn;
	private String searchLoginId;
	private String showBackButton;
	private String dualCommType;
	private String dualCommTypeDesc;
	private List<ListValueVO> dualCommTypeList;
	private String lastDualCommissionTypeDesc;
	private ArrayList _otherCommissionTypeList;
    private ArrayList _otherCategoryList;
    private String _otherCategoryCode;
	private String _commissionTypeValue;
	private String _otherCommissionProfile;	
	private String _commissionType;
	private ArrayList _otherCommissionProfileList;
	private String _commissionTypeValueAsString;
    private String _otherCommissionProfileAsString;
    private String _commissionTypeAsString;
    private String _otherGradeCode;
	private ArrayList _gradeListOth;
	private ArrayList _gatewayListOth;
    private static final float EPSILON=0.0000001f;
    private ArrayList paymentModeList;
	private String paymentMode;
	private String transactionType;
	private ArrayList transactionTypeList;
	private String paymentModeDesc = null;
	private String transactionTypeDesc = null;
	private String otfTimeSlab;
	private String otfApplicableFrom;
	private String otfApplicableTo;
	private String otfApplicableFromStr;
	private int otfDetailVOSize;
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
	private String otfApplicableToStr;
	
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public ArrayList getPaymentModeList() {
		return paymentModeList;
	}
	public void setPaymentModeList(ArrayList paymentModeList) {
		this.paymentModeList = paymentModeList;
	}
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public ArrayList getTransactionTypeList() {
		return transactionTypeList;
	}
	public void setTransactionTypeList(ArrayList transactionTypeList) {
		this.transactionTypeList = transactionTypeList;
	}

	public String getLastDualCommissionTypeDesc() {
		return lastDualCommissionTypeDesc;
	}

	public void setLastDualCommissionTypeDesc(String lastDualCommissionTypeDesc) {
		this.lastDualCommissionTypeDesc = lastDualCommissionTypeDesc;
	}

	public String getDualCommTypeDesc() {
		return dualCommTypeDesc;
	}

	public void setDualCommTypeDesc(String dualCommTypeDesc) {
		this.dualCommTypeDesc = dualCommTypeDesc;
	}

	public List<ListValueVO> getDualCommTypeList() {
		return dualCommTypeList;
	}

	public void setDualCommTypeList(ArrayList<ListValueVO> dualCommTypeList) {
		this.dualCommTypeList = dualCommTypeList;
	}

	public String getDualCommType() {
		return dualCommType;
	}

	public int getDualCommTypeListSize() {
		 if (dualCommTypeList != null) {
	            return dualCommTypeList.size();
	        }
	        return 0;
	} 
	
	public void setDualCommType(String dualCommType) {
		this.dualCommType = dualCommType;
	}

	public String getShowBackButton() {
		return showBackButton;
	}

	public void setShowBackButton(String showViewButton) {
		this.showBackButton = showViewButton;
	}

	public String getSearchMsisdn() {
		return searchMsisdn;
	}

	public void setSearchMsisdn(String searchMsisdn) {
		this.searchMsisdn = searchMsisdn;
	}

	public String getSearchLoginId() {
		return searchLoginId;
	}

	public void setSearchLoginId(String searchLoginId) {
		this.searchLoginId = searchLoginId;
	}

	public ArrayList getAmountCountTypeList() {
		return amountCountTypeList;
	}

	public void setAmountCountTypeList(ArrayList amountCountTypeList) {
		this.amountCountTypeList = amountCountTypeList;
	}

	public String getSequenceNo() {
		return _sequenceNo;
	}

	public void setSequenceNo(String no) {
		_sequenceNo = no;
	}
    public String getGradeCode() {
        return _gradeCode;
    }

    /**
     * To set the value of gradeCode field
     */
    public void setGradeCode(String gradeCode) {
        _gradeCode = gradeCode;
    }

    public String getGradeName() {
        return _gradeName;
    }

    /**
     * To set the value of gradeName field
     */
    public void setGradeName(String gradeName) {
        _gradeName = gradeName;
    }

    public ArrayList getGradeList() {
        return _gradeList;
    }

    public void setGradeList(ArrayList gList) {
        _gradeList = gList;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDomainListSize() {
        return _domainListSize;
    }

    public void setDomainListSize(int domainListSize) {
        _domainListSize = domainListSize;
    }

    public String getBatchName() {
        return _batchName;
    }

    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    public String getErrorFlag() {
        return _errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    public ArrayList getErrorList() {
        return _errorList;
    }

    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        _code = code;
    }

    public void flush() {
        _requestType = null;
        _locationIndex = -1;

        // for selectDomainForCommission.jsp
        _networkName = null;
        _domainCode = null;
        _domainCodeDesc = null;
        _domainList = null;
        _categoryCode = null;
        _categoryCodeDesc = null;
        _categoryList = null;
        _version = null;
        _geographyList = null;
        _grphDomainCodeDesc = null;
        _gradeList = null;
        _gradeCodeDesc = null;
        // for commissionProfileDetail.jsp
        _profileName = null;
        _shortCode = null;
        _applicableFromDate = null;
        _applicableFromHour = null;
        _oldApplicableFromDate = null;
        _oldApplicableFromHour = null;
        _showAdditionalCommissionFlag = null;

        _commissionProfileList = null;
        _additionalProfileList = null;
        _otfProfileList = null;

        // for addCommissionProfile.jsp
        _productCode = null;
        _productCodeDesc = null;
        _productList = null;
        _minTransferValue = null;
        _maxTransferValue = null;
        _multipleOf = null;
        _taxCalculatedOnFlag = null;
        _taxOnFOCFlag = null;
        _maxDiscountAllowed = null;
        _maxDiscountType = null;

        // for addAdditionalProfile.jsp
        _serviceCode = null;
        _serviceCodeDesc = null;
        _serviceList = null;

        _amountTypeList = null;
        _slabsList = null;

        // for selectCommissionProfileName.jsp
        _selectCommProfileSetID = null;
        _selectCommProfileSetList = null;
        _selectCommProifleVersionID = null;
        _selectCommProfileVersionList = null;

        // for selectCommissionProfileNameForView.jsp
        _numberOfDays = null;

        // For Commission Profile Details
        _domainName = null;
        _categoryName = null;
        _networkCode = null;
        _domainAllList = null;
        _parentCategoryList = null;
        _parentCategoryCode = null;
        _fromDate = null;
        _toDate = null;

        _addtnlComStatus = null;
        _addtnlComStatusName = null;

        // for roam recharge
        _roamRecharge = null;
        _subServiceList = null;
        _subServiceCode = null;
        _subServiceDesc = null;
        // _gatewayCode=null;
        // _additionalCommissionTimeSlab=null;
		_commissionTypeValue = null;
		_commissionType = null;
		_otherCommissionProfile = null;
		paymentMode = null;
		paymentModeDesc = null;
        transactionType = null;
        transactionTypeDesc = null;
        otfTimeSlab = null;
    	otfApplicableFrom = null;
    	otfApplicableTo = null;
    }

    public void semiFlush() {
        // for commissionProfileDetail.jsp
        _profileName = null;
        _shortCode = null;
        _applicableFromDate = null;
        _applicableFromHour = null;
        _oldApplicableFromDate = null;
        _oldApplicableFromHour = null;
        _showAdditionalCommissionFlag = null;

        _commissionProfileList = null;
        _additionalProfileList = null;
        _otfProfileList = null;
		_commissionTypeValue = null;
        _commissionType = null;
        _otherCommissionProfile = null;
    }

    public void semiFlush1()// not flush the _showAdditionalCommissionFlag
    {
        // for commissionProfileDetail.jsp
        _profileName = null;
        _shortCode = null;
        _applicableFromDate = null;
        _applicableFromHour = null;
        _oldApplicableFromDate = null;
        _oldApplicableFromHour = null;

        _commissionProfileList = null;
        _additionalProfileList = null;
        _otfProfileList = null;
		_otherCommissionProfile = null;
    }

    public void semiFlushCommission() {
        // for addCommissionProfile.jsp
        _productCode = null;
        _productCodeDesc = null;
        paymentMode = null;
        paymentModeDesc = null;
        transactionType = null;
        transactionTypeDesc = null;
        otfTimeSlab = null;
    	otfApplicableFrom = null;
    	otfApplicableTo = null;
        _productList = null;
        _minTransferValue = null;
        _maxTransferValue = null;
        _multipleOf = null;
        _taxCalculatedOnFlag = null;
        _taxOnFOCFlag = null;
        _maxDiscountAllowed = null;
        _maxDiscountType = null;

        _slabsList = null;
    }
    
    public void semiFlushOtf() {
        _productCode = null;
        _productCodeDesc = null;
        otfTimeSlab = null;
    	otfApplicableFrom = null;
    	otfApplicableTo = null;
    	otfApplicableFromStr = null;
    	otfApplicableToStr = null;
        _productList = null;
        _slabsList = null;
    }

    public void semiFlushAdditional() {
        // for addAdditionalProfile.jsp
        _serviceCode = null;
        _serviceCodeDesc = null;
        _serviceList = null;
        _minTransferValue = null;
        _maxTransferValue = null;

       _slabsList = null;
        _subServiceList = null;
        _subServiceCode = null;
        _subServiceDesc = null;

        // _gatewayCode=null;
        // _additionalCommissionTimeSlab=null;
    }




    /**
     * Set date in locale format in form
     */
	public void setDateInLocaleFormatInForm(String callerType, String index) {
		_applicableFromDate = BTSLDateUtil.getSystemLocaleDate(_applicableFromDate);
     	
      	if (_slabsList != null && !_slabsList.isEmpty() && !"edit".equalsIgnoreCase(callerType)) {
      		_applicableFromAdditional=BTSLDateUtil.getSystemLocaleDate(_applicableFromAdditional);
    		_applicableToAdditional=BTSLDateUtil.getSystemLocaleDate(_applicableToAdditional);
    		otfApplicableFrom=BTSLDateUtil.getSystemLocaleDate(otfApplicableFrom);
    		otfApplicableTo=BTSLDateUtil.getSystemLocaleDate(otfApplicableTo);
		if(_slabsList!=null && _slabsList.get(0) instanceof AdditionalProfileDeatilsVO) {
				ArrayList<AdditionalProfileDeatilsVO> slabsListArr = _slabsList;
			    int slabListSize = slabsListArr.size();
			    for(int i = 0; i< slabListSize; i++) {
			    	AdditionalProfileDeatilsVO commissionProfileDeatilsVO = slabsListArr.get(i);
			    	commissionProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getSystemLocaleDate(commissionProfileDeatilsVO.getOtfApplicableFromStr()));
			    	commissionProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getSystemLocaleDate(commissionProfileDeatilsVO.getOtfApplicableToStr()));
			    }
			}
         }
      	
      	if (_commissionProfileList != null && !_commissionProfileList.isEmpty()) {
        	if (_commissionProfileList.get(0) instanceof CommissionProfileCombinedVO)
        	{
        		ArrayList<CommissionProfileCombinedVO> commissionProfileCombinedVOList = _commissionProfileList;
        		int arraySize = commissionProfileCombinedVOList.size();
        		int size = 0;
        		if("edit".equalsIgnoreCase(callerType) || _locationIndex > -1) {
        			size = arraySize;
        		} else if(_locationIndex < 0){
        			if(_slabsList!=null && _slabsList.get(0) instanceof CommissionProfileDeatilsVO )  {
            			size = arraySize - 1;
            		} else if(_slabsList!=null && (_slabsList.get(0) instanceof AdditionalProfileDeatilsVO || _slabsList.get(0) instanceof OTFDetailsVO)) {
            			size = arraySize;
            		}
        		}
        	}
      	}
      	
      	if (_additionalProfileList != null && !_additionalProfileList.isEmpty()) {
        	if (_additionalProfileList.get(0) instanceof AdditionalProfileCombinedVO)
        	{
        		ArrayList<AdditionalProfileCombinedVO> additionalProfileCombinedVOList = _additionalProfileList;
        		int arraySize = additionalProfileCombinedVOList.size();
        		int size = 0;
        		if("edit".equalsIgnoreCase(callerType) || _locationIndex > -1) {
        			size = arraySize;
        		} else if(_locationIndex < 0){
        			if(_slabsList!=null && (_slabsList.get(0) instanceof CommissionProfileDeatilsVO || _slabsList.get(0) instanceof OTFDetailsVO)) {
            			size = arraySize;
            		} else if(_slabsList!=null && _slabsList.get(0) instanceof AdditionalProfileDeatilsVO) {
            			size = arraySize - 1;
            		}	
        		}
        		for(int k = 0; k<arraySize; k++) {
        			AdditionalProfileServicesVO additionalProfileServicesVO = additionalProfileCombinedVOList.get(k).getAdditionalProfileServicesVO();
        			additionalProfileServicesVO.setApplicableFromAdditional(BTSLDateUtil.getSystemLocaleDate(additionalProfileServicesVO.getApplicableFromAdditional()));
        			additionalProfileServicesVO.setApplicableToAdditional(BTSLDateUtil.getSystemLocaleDate(additionalProfileServicesVO.getApplicableToAdditional()));
        		}
        		for(int i = 0; i< size ; i++) {
        			boolean modify = true;
        			if(!"edit".equalsIgnoreCase(callerType) && (_slabsList.get(0) instanceof AdditionalProfileDeatilsVO) 
        					&& _locationIndex > -1 && i == _locationIndex) {
        				modify = false;
            		}
        			if(modify) {
	        			ArrayList slabsListArr = additionalProfileCombinedVOList.get(i).getSlabsList();
				        int slabListSize = slabsListArr.size();
			            for(int j = 0; j< slabListSize; j++) {
			            	AdditionalProfileDeatilsVO additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) slabsListArr.get(j);
			            	additionalProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getSystemLocaleDate(additionalProfileDeatilsVO.getOtfApplicableFromStr()));
			            	additionalProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getSystemLocaleDate(additionalProfileDeatilsVO.getOtfApplicableToStr()));
			            }
        			}
        		}
        	}
        }
      	
      	if (_otfProfileList != null && !_otfProfileList.isEmpty()) {
			if(_otfProfileList.get(0) instanceof OtfProfileCombinedVO) {
				ArrayList<OtfProfileCombinedVO> otfProfileCombinedVOList = _otfProfileList;
        		int arraySize = _otfProfileList.size();
        		int size = 0;
        		if("edit".equalsIgnoreCase(callerType) || _locationIndex > -1) {
        			size = arraySize;
        		} else if(_locationIndex < 0){
        			if(_slabsList != null && (_slabsList.get(0) instanceof CommissionProfileDeatilsVO || _slabsList.get(0) instanceof AdditionalProfileDeatilsVO)) {
            			size = arraySize;
            		} else if(_slabsList != null && _slabsList.get(0) instanceof OTFDetailsVO ) {
            			size = arraySize - 1;
            		}
        		}
        		for(int k = 0; k<arraySize; k++) {
        			OtfProfileVO otfProfileVO = otfProfileCombinedVOList.get(k).getOtfProfileVO();
        			otfProfileVO.setOtfApplicableFrom(BTSLDateUtil.getSystemLocaleDate(otfProfileVO.getOtfApplicableFrom()));
        			otfProfileVO.setOtfApplicableTo(BTSLDateUtil.getSystemLocaleDate(otfProfileVO.getOtfApplicableTo()));
        		}
			}
         }
        }


	private void setDateInGregorianInForm(String callerType, String index) {
		_applicableFromDate = BTSLDateUtil.getGregorianDateInString(_applicableFromDate);
        
        if (_slabsList != null && !_slabsList.isEmpty() && !"edit".equalsIgnoreCase(callerType)) {
        	_applicableFromAdditional=BTSLDateUtil.getGregorianDateInString(_applicableFromAdditional);
            _applicableToAdditional=BTSLDateUtil.getGregorianDateInString(_applicableToAdditional); 
            otfApplicableFrom=BTSLDateUtil.getGregorianDateInString(otfApplicableFrom);
    		otfApplicableTo=BTSLDateUtil.getGregorianDateInString(otfApplicableTo);
			 if(_slabsList!=null && _slabsList.get(0) instanceof AdditionalProfileDeatilsVO) {
				ArrayList<AdditionalProfileDeatilsVO> slabsListArr = _slabsList;
			    int slabListSize = slabsListArr.size();
			    for(int i = 0; i< slabListSize; i++) {
			    	AdditionalProfileDeatilsVO commissionProfileDeatilsVO = slabsListArr.get(i);
			    	commissionProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getGregorianDateInString(commissionProfileDeatilsVO.getOtfApplicableFromStr()));
			    	commissionProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getGregorianDateInString(commissionProfileDeatilsVO.getOtfApplicableToStr()));
			    }
			}
         }
        
        if (_commissionProfileList != null && !_commissionProfileList.isEmpty()) {
        	if (_commissionProfileList.get(0) instanceof CommissionProfileCombinedVO)
        	{
        		ArrayList<CommissionProfileCombinedVO> commissionProfileCombinedVOList = _commissionProfileList;
        		int arraySize = commissionProfileCombinedVOList.size();
        		int size = 0;
        		if("edit".equalsIgnoreCase(callerType) || _locationIndex > -1) {
        			size = arraySize;
        		} else if(_locationIndex < 0){
        			if(_slabsList!=null && _slabsList.get(0) instanceof CommissionProfileDeatilsVO) {
            			size = arraySize - 1;
            		} else if(_slabsList!=null && (_slabsList.get(0) instanceof AdditionalProfileDeatilsVO || _slabsList.get(0) instanceof OTFDetailsVO)) {
            			size = arraySize;
            		} else {
            			size = _commissionProfileList.size();
            		}
        		}
        	}
        }
        
        if (_additionalProfileList != null && !_additionalProfileList.isEmpty()) {
        	if (_additionalProfileList.get(0) instanceof AdditionalProfileCombinedVO)
        	{
        		ArrayList<AdditionalProfileCombinedVO> additionalProfileCombinedVOList = _additionalProfileList;
        		int arraySize = additionalProfileCombinedVOList.size();
        		int size = 0;
        		if("edit".equalsIgnoreCase(callerType) || _locationIndex > -1) {
        			size = arraySize;
        		} else if(_locationIndex < 0){
        			if(_slabsList!=null && (_slabsList.get(0) instanceof CommissionProfileDeatilsVO || _slabsList.get(0) instanceof OTFDetailsVO)) {
            			size = arraySize;
            		} else if(_slabsList!=null && _slabsList.get(0) instanceof AdditionalProfileDeatilsVO) {
            			size = arraySize - 1;
            		}
        		}
        		for(int k = 0; k<arraySize; k++) {
        			AdditionalProfileServicesVO additionalProfileServicesVO = additionalProfileCombinedVOList.get(k).getAdditionalProfileServicesVO();
        			additionalProfileServicesVO.setApplicableFromAdditional(BTSLDateUtil.getGregorianDateInString(additionalProfileServicesVO.getApplicableFromAdditional()));
        			additionalProfileServicesVO.setApplicableToAdditional(BTSLDateUtil.getGregorianDateInString(additionalProfileServicesVO.getApplicableToAdditional()));
        		}
        		for(int i = 0; i< size ; i++) {
        			boolean modify = true;
        			if(!"edit".equalsIgnoreCase(callerType) && (_slabsList!=null && _slabsList.get(0) instanceof AdditionalProfileDeatilsVO) 
        					&& _locationIndex > -1 && i == _locationIndex) {
        				modify = false;
            		}
        			if(modify) {
	        			ArrayList slabsListArr = additionalProfileCombinedVOList.get(i).getSlabsList();
				        int slabListSize = slabsListArr.size();
			            for(int j = 0; j< slabListSize; j++) {
			            	AdditionalProfileDeatilsVO additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) slabsListArr.get(j);
			            	additionalProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableFromStr()));
			            	additionalProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getGregorianDateInString(additionalProfileDeatilsVO.getOtfApplicableToStr()));
			            }
        			}
        		}
        	}
        }
        
    	if (_otfProfileList != null && !_otfProfileList.isEmpty()) {
			if(_otfProfileList.get(0) instanceof OtfProfileCombinedVO) {
				ArrayList<OtfProfileCombinedVO> otfProfileCombinedVOList = _otfProfileList;
        		int arraySize = _otfProfileList.size();
        		int size = 0;
        		if("edit".equalsIgnoreCase(callerType) || _locationIndex > -1) {
        			size = arraySize;
        		} else if(_locationIndex < 0){
        			if(_slabsList != null && (_slabsList.get(0) instanceof CommissionProfileDeatilsVO || _slabsList.get(0) instanceof AdditionalProfileDeatilsVO)) {
            			size = arraySize;
            		} else if(_slabsList != null && _slabsList.get(0) instanceof OTFDetailsVO ) {
            			size = arraySize - 1;
            		}
        		}
        		for(int k = 0; k<arraySize; k++) {
        			OtfProfileVO otfProfileVO = otfProfileCombinedVOList.get(k).getOtfProfileVO();
        			otfProfileVO.setOtfApplicableFrom(BTSLDateUtil.getGregorianDateInString(otfProfileVO.getOtfApplicableFrom()));
        			otfProfileVO.setOtfApplicableTo(BTSLDateUtil.getGregorianDateInString(otfProfileVO.getOtfApplicableTo()));
        		}
			}
         }
	}

    private String[] validateOTFDetails(List<OTFDetailsVO> otfDetails, int list){
    
    	String arr[] = new String[4]; 
    	arr[3]=Boolean.toString(false);
    	int index=0;
    	
    	OTFDetailsVO currentotfdetail;
    
    	OTFDetailsVO nextotfdetail;
    	for(int i=0;i<list-1;i++){
    		currentotfdetail= otfDetails.get(i);
    		
    		index=i+1;
    		nextotfdetail= otfDetails.get(index);

    		if(Integer.parseInt(currentotfdetail.getOtfValue())>=Integer.parseInt(nextotfdetail.getOtfValue())){
    			arr[0]=currentotfdetail.getOtfValue();
    			arr[1]=nextotfdetail.getOtfValue();
    			arr[2]=Integer.toString(index);
    			arr[3]=Boolean.toString(true);
    			break;
    		}

    	}
    	
    	return arr;
    }
    
    
    /**
     * @return Returns the applicableFromDate.
     */
    public String getApplicableFromDate() {
        return _applicableFromDate;
    }
    public void setApplicableFromDate(String applicableFromDate) {
        if (applicableFromDate != null) {
            _applicableFromDate = applicableFromDate.trim();
        }
    }
   
    /**
     * @return Returns the applicableFromHour.
     */
    public String getApplicableFromHour() {
        return _applicableFromHour;
    }

    /**
     * @param applicableFromHour
     *            The applicableFromHour to set.
     */
    public void setApplicableFromHour(String applicableFromHour) {
        if (applicableFromHour != null) {
            _applicableFromHour = applicableFromHour.trim();
        }
    }

    /**
     * @return Returns the oldApplicableFromDate.
     */
    public String getOldApplicableFromDate() {
        return _oldApplicableFromDate;
    }

    /**
     * @param oldApplicableFromDate
     *            The oldApplicableFromDate to set.
     */
    public void setOldApplicableFromDate(String oldApplicableFromDate) {
        _oldApplicableFromDate = oldApplicableFromDate;
    }

    /**
     * @return Returns the oldApplicableFromHour.
     */
    public String getOldApplicableFromHour() {
        return _oldApplicableFromHour;
    }

    /**
     * @param oldApplicableFromHour
     *            The oldApplicableFromHour to set.
     */
    public void setOldApplicableFromHour(String oldApplicableFromHour) {
        _oldApplicableFromHour = oldApplicableFromHour;
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        if (categoryCode != null) {
            _categoryCode = categoryCode.trim();
        }
    }

    /**
     * @return Returns the categoryCodeDesc.
     */
    public String getCategoryCodeDesc() {
        return _categoryCodeDesc;
    }

    public String getGrphDomainCodeDesc() {
        return _grphDomainCodeDesc;
    }

    public String getGradeCodeDesc() {
        return _gradeCodeDesc;
    }

    /**
     * @param categoryCodeDesc
     *            The categoryCodeDesc to set.
     */
    public void setCategoryCodeDesc(String categoryCodeDesc) {
        if (categoryCodeDesc != null) {
            _categoryCodeDesc = categoryCodeDesc.trim();
        }
    }

    public void setGrphDomainCodeDesc(String grphDomainCodeDesc) {
        if (grphDomainCodeDesc != null) {
            _grphDomainCodeDesc = grphDomainCodeDesc.trim();
        }
    }

    public void setGradeCodeDesc(String gradeCodeDesc) {
        if (gradeCodeDesc != null) {
            _gradeCodeDesc = gradeCodeDesc.trim();
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
     * @return Returns the domainCode.
     */
    public String getDomainCode() {
        return _domainCode;
    }

    /**
     * @param domainCode
     *            The domainCode to set.
     */
    public void setGatewayCode(String gatewayCode) {
        if (gatewayCode != null) {
            _gatewayCode = gatewayCode.trim();
        }
    }

    public String getGatewayCode() {
        return _gatewayCode;
    }

    public void setGatewayType(String gatewayType) {
        _gatewayType = gatewayType;
    }

    public String getGatewayType() {
        return _gatewayType;
    }

    /**
     * @param domainCode
     *            The domainCode to set.
     */
    public void setDomainCode(String domainCode) {
        if (domainCode != null) {
            _domainCode = domainCode.trim();
        }
    }

    /**
     * @return Returns the domainCodeDesc.
     */
    public String getDomainCodeDesc() {
        return _domainCodeDesc;
    }

    /**
     * @param domainCodeDesc
     *            The domainCodeDesc to set.
     */
    public void setDomainCodeDesc(String domainCodeDesc) {
        if (domainCodeDesc != null) {
            _domainCodeDesc = domainCodeDesc.trim();
        }
    }

    /**
     * @return Returns the domainList.
     */
    public ArrayList getDomainList() {
        return _domainList;
    }

    /**
     * @param domainList
     *            The domainList to set.
     */
    public void setDomainList(ArrayList domainList) {
        _domainList = domainList;
    }

    public ArrayList getGatewayList() {
        return _gatewayList;
    }

    /**
     * @param domainList
     *            The domainList to set.
     */
    public void setGatewayList(ArrayList gatewayList) {
        _gatewayList = gatewayList;
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return _networkName;
    }

    public ArrayList getGeographyList() {
        return _geographyList;
    }

    public void setGeographyList(ArrayList geoglist) {
        _geographyList = geoglist;
    }

    public String getGrphDomainName() {
        return _grphDomainName;
    }

    public void getGrphDomainName(String gName) {
        _grphDomainName = gName;
    }

    public String getGrphDomainCode() {
        return _grphDomainCode;
    }

    public void setGrphDomainCode(String grphDomainCode) {
        _grphDomainCode = grphDomainCode;
    }

    public String getGrphDomainType() {
        return _grphDomainType;
    }

    public void setGrphDomainType(String grphDomainType) {
        _grphDomainType = grphDomainType;
    }

    /**
     * @param networkName
     *            The networkName to set.
     */
    public void setNetworkName(String networkName) {
        if (networkName != null) {
            _networkName = networkName.trim();
        }
    }

    /**
     * @return Returns the profileName.
     */
    public String getProfileName() {
        return _profileName;
    }

    /**
     * @param profileName
     *            The profileName to set.
     */
    public void setProfileName(String profileName) {
        if (profileName != null) {
            _profileName = profileName.trim();
        }
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        _version = version;
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
        if (requestType != null) {
            _requestType = requestType.trim();
        }
    }

    /**
     * @return Returns the amountTypeList.
     */
    public ArrayList getAmountTypeList() {
        return _amountTypeList;
    }

    /**
     * @param amountTypeList
     *            The amountTypeList to set.
     */
    public void setAmountTypeList(ArrayList amountTypeList) {
        _amountTypeList = amountTypeList;
    }

    /**
     * @return Returns the maxDiscountAllowed.
     */
    public String getMaxDiscountAllowed() {
        return _maxDiscountAllowed;
    }

    /**
     * @param maxDiscountAllowed
     *            The maxDiscountAllowed to set.
     */
    public void setMaxDiscountAllowed(String maxDiscountAllowed) {
        if (maxDiscountAllowed != null) {
            _maxDiscountAllowed = maxDiscountAllowed.trim();
        }
    }

    /**
     * @return Returns the maxDiscountType.
     */
    public String getMaxDiscountType() {
        return _maxDiscountType;
    }

    /**
     * @param maxDiscountType
     *            The maxDiscountType to set.
     */
    public void setMaxDiscountType(String maxDiscountType) {
        if (maxDiscountType != null) {
            _maxDiscountType = maxDiscountType.trim();
        }
    }

    /**
     * @return Returns the maxTransferValue.
     */
    public String getMaxTransferValue() {
        return _maxTransferValue;
    }

    /**
     * @param maxTransferValue
     *            The maxTransferValue to set.
     */
    public void setMaxTransferValue(String maxTransferValue) {
        if (maxTransferValue != null) {
            _maxTransferValue = maxTransferValue.trim();
        }
    }

    /**
     * @return Returns the minTransferValue.
     */
    public String getMinTransferValue() {
        return _minTransferValue;
    }

    /**
     * @param minTransferValue
     *            The minTransferValue to set.
     */
    public void setMinTransferValue(String minTransferValue) {
        if (minTransferValue != null) {
            _minTransferValue = minTransferValue.trim();
        }
    }

    /**
     * @return Returns the multipleOf.
     */
    public String getMultipleOf() {
        return _multipleOf;
    }

    /**
     * @param multipleOf
     *            The multipleOf to set.
     */
    public void setMultipleOf(String multipleOf) {
        if (multipleOf != null) {
            _multipleOf = multipleOf.trim();
        }
    }

    /**
     * @return Returns the taxCalculatedOnFlag.
     */
    public String getTaxCalculatedOnFlag() {
        return _taxCalculatedOnFlag;
    }

    /**
     * @param taxCalculatedOnFlag
     *            The taxCalculatedOnFlag to set.
     */
    public void setTaxCalculatedOnFlag(String taxCalculatedOnFlag) {
        if (taxCalculatedOnFlag != null) {
            _taxCalculatedOnFlag = taxCalculatedOnFlag.trim();
        }
    }

    /**
     * @return Returns the taxOnFOCFlag.
     */
    public String getTaxOnFOCFlag() {
        return _taxOnFOCFlag;
    }

    /**
     * @param taxOnFOCFlag
     *            The taxOnFOCFlag to set.
     */
    public void setTaxOnFOCFlag(String taxOnFOCFlag) {
        if (taxOnFOCFlag != null) {
            _taxOnFOCFlag = taxOnFOCFlag.trim();
        }
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        if (productCode != null) {
            _productCode = productCode.trim();
        }
    }

    /**
     * @return Returns the productCodeDesc.
     */
    public String getProductCodeDesc() {
        return _productCodeDesc;
    }

    /**
     * @param productCodeDesc
     *            The productCodeDesc to set.
     */
    public void setProductCodeDesc(String productCodeDesc) {
        if (productCodeDesc != null) {
            _productCodeDesc = productCodeDesc.trim();
        }
    }

    /**
     * @return Returns the commissionProfileList.
     */
    public ArrayList getCommissionProfileList() {
        return _commissionProfileList;
    }

    /**
     * @param commissionProfileList
     *            The commissionProfileList to set.
     */
    public void setCommissionProfileList(ArrayList commissionProfileList) {
        _commissionProfileList = commissionProfileList;
    }

    /**
     * @return Returns the slabsList.
     */
    public ArrayList getSlabsList() {
        return _slabsList;
    }

    /**
     * @param slabsList
     *            The slabsList to set.
     */
    public void setSlabsList(ArrayList slabsList) {
        _slabsList = slabsList;
    }

    public CommissionProfileDeatilsVO getCommSlabsListIndexed(int i) {
        return (CommissionProfileDeatilsVO) _slabsList.get(i);
    }

    public void setCommSlabsListIndexed(int i, CommissionProfileDeatilsVO commissionProfileDeatilsVO) {
        _slabsList.set(i, commissionProfileDeatilsVO);
    }

    public OTFDetailsVO getOtfSlabsListIndexed(int i) {
        return (OTFDetailsVO) _slabsList.get(i);
    }
    public void setOtfSlabsListIndexed(int i, OTFDetailsVO otfDetailsVO) {
        _slabsList.set(i, otfDetailsVO);
    }
    
    public AdditionalProfileDeatilsVO getAddSlabsListIndexed(int i) {
        return (AdditionalProfileDeatilsVO) _slabsList.get(i);
    }


    public void setAddSlabsListIndexed(int i, AdditionalProfileDeatilsVO additionalProfileDeatilsVO) {
        _slabsList.set(i, additionalProfileDeatilsVO);
    }

    /**
     * @return Returns the productList.
     */
    public ArrayList getProductList() {
        return _productList;
    }

    public int getProductListSize() {
        if (_productList != null) {
            return _productList.size();
        }
        return 0;
    }

    /**
     * @param productList
     *            The productList to set.
     */
    public void setProductList(ArrayList productList) {
        _productList = productList;
    }

    /**
     * @return Returns the locationIndex.
     */
    public int getLocationIndex() {
        return _locationIndex;
    }

    /**
     * @param locationIndex
     *            The locationIndex to set.
     */
    public void setLocationIndex(int locationIndex) {
        _locationIndex = locationIndex;
    }

    /**
     * @return Returns the additionalProfileList.
     */
    public ArrayList getAdditionalProfileList() {
        return _additionalProfileList;
    }

    /**
     * @param additionalProfileList
     *            The additionalProfileList to set.
     */
    public void setAdditionalProfileList(ArrayList additionalProfileList) {
        _additionalProfileList = additionalProfileList;
    }

    /**
     * @return Returns the serviceCode.
     */
    public String getServiceCode() {
        return _serviceCode;
    }

    /**
     * @param serviceCode
     *            The serviceCode to set.
     */
    public void setServiceCode(String serviceCode) {
        if (serviceCode != null) {
            _serviceCode = serviceCode.trim();
        }
    }

    /**
     * @return Returns the serviceCodeDesc.
     */
    public String getServiceCodeDesc() {
        return _serviceCodeDesc;
    }

    /**
     * @param serviceCodeDesc
     *            The serviceCodeDesc to set.
     */
    public void setServiceCodeDesc(String serviceCodeDesc) {
        if (serviceCodeDesc != null) {
            _serviceCodeDesc = serviceCodeDesc.trim();
        }
    }

    /**
     * @return Returns the serviceList.
     */
    public ArrayList getServiceList() {
        return _serviceList;
    }

    /**
     * @param serviceList
     *            The serviceList to set.
     */
    public void setServiceList(ArrayList serviceList) {
        _serviceList = serviceList;
    }

    /**
     * @return Returns the showAdditionalCommissionFlag.
     */
    public String getShowAdditionalCommissionFlag() {
        return _showAdditionalCommissionFlag;
    }

    /**
     * @param showAdditionalCommissionFlag
     *            The showAdditionalCommissionFlag to set.
     */
    public void setShowAdditionalCommissionFlag(String showAdditionalCommissionFlag) {
        _showAdditionalCommissionFlag = showAdditionalCommissionFlag;
    }

    /**
     * @return Returns the shortCode.
     */
    public String getShortCode() {
        return _shortCode;
    }

    /**
     * @param shortCode
     *            The shortCode to set.
     */
    public void setShortCode(String shortCode) {
        if (shortCode != null) {
            _shortCode = shortCode.trim();
        }
    }

    /**
     * @return Returns the selectCommProfileSetID.
     */
    public String getSelectCommProfileSetID() {
        return _selectCommProfileSetID;
    }

    /**
     * @param selectCommProfileSetID
     *            The selectCommProfileSetID to set.
     */
    public void setSelectCommProfileSetID(String selectCommProfileSetID) {
        if (selectCommProfileSetID != null) {
            this._selectCommProfileSetID = selectCommProfileSetID.trim();
        } else {
            this._selectCommProfileSetID = selectCommProfileSetID;
        }
    }

    /**
     * @return Returns the selectCommProfileSetList.
     */
    public ArrayList getSelectCommProfileSetList() {
        return _selectCommProfileSetList;
    }

    /**
     * @param selectCommProfileSetList
     *            The selectCommProfileSetList to set.
     */
    public void setSelectCommProfileSetList(ArrayList selectCommProfileSetList) {
        this._selectCommProfileSetList = selectCommProfileSetList;
    }

    public CommissionProfileSetVO getSelectCommProfileSetListIndexed(int i) {
        return (CommissionProfileSetVO) _selectCommProfileSetList.get(i);
    }

    /**
     * @param selectCommProfileSetList
     *            The selectCommProfileSetList to set.
     */
    public void setSelectCommProfileSetListIndexed(int i, CommissionProfileSetVO commissionProfileSetVO) {
        this._selectCommProfileSetList.set(i, commissionProfileSetVO);
    }

    public int getResultCount() {
        if (_selectCommProfileSetList != null && !_selectCommProfileSetList.isEmpty()) {
            return _selectCommProfileSetList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the selectCommProfileVersionList.
     */
    public ArrayList getSelectCommProfileVersionList() {
        return _selectCommProfileVersionList;
    }

    /**
     * @param selectCommProfileVersionList
     *            The selectCommProfileVersionList to set.
     */
    public void setSelectCommProfileVersionList(ArrayList selectCommProfileVersionList) {
        this._selectCommProfileVersionList = selectCommProfileVersionList;
    }

    public int getVersionListCount() {
        if (_selectCommProfileVersionList != null && !_selectCommProfileVersionList.isEmpty()) {
            return _selectCommProfileVersionList.size();
        } else {
            return 0;
        }
    }

    public int getNameListCount() {
        if (_selectCommProfileSetList != null && !_selectCommProfileSetList.isEmpty()) {
            return _selectCommProfileSetList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the selectCommProifleVersionID.
     */
    public String getselectCommProifleVersionID() {
        return _selectCommProifleVersionID;
    }

    /**
     * @param selectCommProifleVersionID
     *            The selectCommProifleVersionID to set.
     */
    public void setselectCommProifleVersionID(String selectCommProifleVersionID) {
        if (selectCommProifleVersionID != null) {
            _selectCommProifleVersionID = selectCommProifleVersionID.trim();
        }
    }

    /**
     * @return Returns the selectCommProifleVersionID.
     */
    public String getSelectCommProifleVersionID() {
        return _selectCommProifleVersionID;
    }

    /**
     * @param selectCommProifleVersionID
     *            The selectCommProifleVersionID to set.
     */
    public void setSelectCommProifleVersionID(String selectCommProifleVersionID) {
        _selectCommProifleVersionID = selectCommProifleVersionID;
    }

    /**
     * @return Returns the numberOfDays.
     */
    public String getNumberOfDays() {
        return _numberOfDays;
    }

    /**
     * @param numberOfDays
     *            The numberOfDays to set.
     */
    public void setNumberOfDays(String numberOfDays) {
        _numberOfDays = numberOfDays;
    }

    /**
     * @return Returns the deleteAllowed.
     */
    public boolean isDeleteAllowed() {
        return _deleteAllowed;
    }

    /**
     * @param deleteAllowed
     *            The deleteAllowed to set.
     */
    public void setDeleteAllowed(boolean deleteAllowed) {
        _deleteAllowed = deleteAllowed;
    }

    public String getOldCode() {
        return _oldCode;
    }

    public void setOldCode(String oldCode) {
        _oldCode = oldCode;
    }

    public String getCategoryName() {
        return _categoryName;
    }

    public void setCategoryName(String cat_name) {
        _categoryName = cat_name;
    }

    public String getDomainName() {
        return _domainName;
    }

    public void setDomainName(String domain_name) {
        _domainName = domain_name;
    }

    public ArrayList getDomainAllList() {
        return _domainAllList;
    }

    public void setDomainAllList(ArrayList domainAllList) {
        _domainAllList = domainAllList;
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        if (networkCode != null) {
            _networkCode = networkCode.trim();
        }
    }

    public ArrayList getParentCategoryList() {
        return _parentCategoryList;
    }

    public void setParentCategoryList(ArrayList parentCategoryList) {
        _parentCategoryList = parentCategoryList;
    }

    public String getParentCategoryCode() {
        return _parentCategoryCode;
    }

    public void setParentCategoryCode(String parentCategoryCode) {
        _parentCategoryCode = parentCategoryCode;
    }

    public String getFromDate() {
        return _fromDate;
    }

    public void setFromDate(String from_date) {
        _fromDate = from_date;
    }

    public String getToDate() {
        return _toDate;
    }

    public void setToDate(String to_date) {
        _toDate = to_date;
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

    public String getRoamRecharge() {
        return _roamRecharge;
    }

    public void setRoamRecharge(String roamRecharge) {
        _roamRecharge = roamRecharge;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public String getDate() {
        return _date;
    }

    /**
     * @param date
     *            The date to set.
     */
    public void setDate(String date) {
        _date = date;
    }

  /*  public FormFile getFile() {
        return _file;
    }

    public void setFile(FormFile file) {
        _file = file;
    }
*/
    public String getSetID() {
        return _setID;
    }

    public void setSetID(String setID) {
        _setID = setID;
    }

    public ArrayList getSubServiceList() {
        return _subServiceList;
    }

    public void setSubServiceList(ArrayList subServiceList) {
        _subServiceList = subServiceList;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public String getSubServiceCode() {
        return _subServiceCode;
    }

    public void setSubServiceCode(String subServiceCode) {
        if (subServiceCode != null) {
            _subServiceCode = subServiceCode.trim();
        }
    }

    public String getSubServiceDesc() {
        return _subServiceDesc;
    }

    public void setSubServiceDesc(String subServiceDesc) {
        if (subServiceDesc != null) {
            _subServiceDesc = subServiceDesc.trim();
        }
    }

    public long getTime() {
        return _time;
    }

    public void setTime(long time) {
        _time = time;
    }

    public String[] getSelectedGateways() {
        return _selectedGateways;
    }

    public void setSelectedGateways(String[] gates) {
        _selectedGateways = gates;
    }

    public String getAdditionalCommissionTimeSlab() {
        return _additionalCommissionTimeSlab;
    }

    public void setAdditionalCommissionTimeSlab(String time) {
        _additionalCommissionTimeSlab = time;
    }

    public void additionalFlushAdditional() {
        _gatewayCode = null;
        _additionalCommissionTimeSlab = null;
        _applicableFromAdditional = null;
        _applicableToAdditional = null;
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
	public ArrayList getOtherCommissionTypeList() {
        return _otherCommissionTypeList;
	}
	public void setOtherCommissionTypeList(ArrayList _otherCommissionTypeList) {
        this._otherCommissionTypeList = _otherCommissionTypeList;
    }
	public ArrayList getOtherCategoryList() {
        return _otherCategoryList;
    }
	public void setOtherCategoryList(ArrayList _otherCategoryList) {
        this._otherCategoryList = _otherCategoryList;
    }
	public String getOtherCategoryCode() {
        return _otherCategoryCode;
    }
	public void setOtherCategoryCode(String _otherCategoryCode) {
        this._otherCategoryCode = _otherCategoryCode;
    }
	public String getCommissionTypeValue() {
		return _commissionTypeValue;
    }
	public void setCommissionTypeValue(String _commissionTypeValue) {
		this._commissionTypeValue = _commissionTypeValue;
    }
	public String getOtherCommissionProfile() {
		return _otherCommissionProfile;
    }
    public void setOtherCommissionProfile(String _otherCommissionProfile) {
		this._otherCommissionProfile = _otherCommissionProfile;
    }	
	public String getCommissionType() {
		return _commissionType;
    }
	public void setCommissionType(String _commissionType) {
		this._commissionType = _commissionType;
    }
	public ArrayList getOtherCommissionProfileList() {
		return _otherCommissionProfileList;
    }
    public void setOtherCommissionProfileList(ArrayList _otherCommissionProfileList) {
		this._otherCommissionProfileList = _otherCommissionProfileList;
    }
	public String getCommissionTypeAsString() {
		return _commissionTypeAsString;
    }
	public void setCommissionTypeAsString(String _commissionTypeAsString) {
		this._commissionTypeAsString = _commissionTypeAsString;
    }
	public String getOtherCommissionProfileAsString() {
		return _otherCommissionProfileAsString;
    }
	public void setOtherCommissionProfileAsString(String _otherCommissionProfileAsString) {
		this._otherCommissionProfileAsString = _otherCommissionProfileAsString;
    }
	public String getCommissionTypeValueAsString() {
		return _commissionTypeValueAsString;
    }
	public void setCommissionTypeValueAsString(String _commissionTypeValueAsString) {
		this._commissionTypeValueAsString = _commissionTypeValueAsString;
    }
	
	public String getPaymentModeDesc() {
        return paymentModeDesc;
    }
    public void setPaymentModeDesc(String paymentModeDesc) {
        if (paymentModeDesc != null) {
        	this.paymentModeDesc = paymentModeDesc.trim();
        }
    }
    public String getTransactionTypeDesc() {
        return transactionTypeDesc;
    }
    public void setTransactionTypeDesc(String transactionTypeDesc) {
        if (transactionTypeDesc != null) {
        	this.transactionTypeDesc = transactionTypeDesc.trim();
        }
    }
    public String getOtfTimeSlab() {
        return otfTimeSlab;
    }
    public void setOtfTimeSlab(String time) {
        otfTimeSlab = time;
    }

    public String getOtfApplicableFrom() {
        return otfApplicableFrom;
    }
    public void setOtfApplicableFrom(String todate) {
        otfApplicableFrom = todate;
    }

    public String getOtfApplicableTo() {
        return otfApplicableTo;
    }
    public void setOtfApplicableTo(String todate) {
        otfApplicableTo = todate;
    }
    
    public ArrayList getOtfProfileList() {
        return _otfProfileList;
    }
    public void setOtfProfileList(ArrayList otfProfileList) {
        _otfProfileList = otfProfileList;
    }
    
    public int getOtfDetailVOSize() {
		return otfDetailVOSize;
	}
	public void setOtfDetailVOSize(int otfDetailVOSize) {
		this.otfDetailVOSize = otfDetailVOSize;
	}
	
	  public List<String[]> validateTotCom(ArrayList<OTFDetailsVO> otfSlabList, ArrayList<CommissionProfileDeatilsVO>comSlabList) throws BTSLBaseException{
	    	 final String methodName = "validateTotCom";
	         LogFactory.printLog(methodName, " Entered ", _log);
	         List<String[]> list =  new ArrayList<String[]>();
	    	int indexCom =0;
	    	for(CommissionProfileDeatilsVO comSlab : comSlabList)
	    	 {
	    		 int index =0;
	    		 String arr[] = new String[3]; 
	    		 arr[2]=Boolean.toString(false);
	    		for(OTFDetailsVO otfSlab : otfSlabList)
	    		{
	    			double calculatedOTFValue = 0.0,commValue = 0.0,total=0.0;
	    		        if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(comSlab.getCommType())) {
	    		            commValue = (Double.parseDouble(comSlab.getCommRateAsString()) / 100) * (Double.parseDouble(comSlab.getStartRangeAsString()));
	    		        } else if (PretupsI.SYSTEM_AMOUNT.equals(comSlab.getCommType())) {
	    		            commValue = Double.parseDouble(comSlab.getCommRateAsString());
	    		        }
			        if(!BTSLUtil.isNullString(otfSlab.getOtfType())){
			        	if (PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otfSlab.getOtfType())) {
			        		calculatedOTFValue = otfSlab.getOtfRateDouble();
			        	} else if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(otfSlab.getOtfType())) {
			        	calculatedOTFValue = (Double.parseDouble(comSlab.getStartRangeAsString()) * ((otfSlab.getOtfRateDouble()) / 100));
			        	}
			        }
	    			
	    			total = calculatedOTFValue+commValue;
	    			if(total >= Double.parseDouble(comSlab.getStartRangeAsString()))
	    			{
	    				arr[0]=Integer.toString(indexCom+1);
	        			arr[1]=Integer.toString(index+1);
	        			arr[2]=Boolean.toString(true);
	        			index ++;
	        			break;
	    			}
	    			index ++;
	    		}
	    		list.add(arr);
	    		indexCom++;
	    	 }
	    	return list;
	    }
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	 public ArrayList getGradeListOth() {
		return _gradeListOth;
	}
	public void setGradeListOth(ArrayList gListOth) {
		_gradeListOth = gListOth;
	}
	public ArrayList getGatewayListOth() {
		return _gatewayListOth;
	}
	public void setGatewayListOth(ArrayList gatewayListOth) {
		_gatewayListOth = gatewayListOth;
	}
	public String getOtherGradeCode() {
        return _otherGradeCode;
    }
    public void setOtherGradeCode(String otherGradeCode) {
        _otherGradeCode = otherGradeCode;
    } 
	  
	
}
