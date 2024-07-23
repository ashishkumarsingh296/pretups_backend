/*
 * @# C2SRechargeModel.java
 *  parul.nagpal
 */
package com.web.pretups.channel.transfer.web;

import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.transfer.businesslogic.EnquiryVO;

public class C2SRechargeModel implements Serializable  {
    
    private String _serviceType;
    private String _subscriberMsisdn;
    private String _amount;
    private String _subServiceType;
    private String _pin;
    private String _loginUserMsisdn;
    private String _serviceTypeDes;
    private String _subServiceTypeDes;
    private String _languageCode;
    private String _languageCodeDesc;

    private ArrayList _serviceTypeList = null;
    private ArrayList _subServiceTypeList = null;
    private ArrayList _serviceKeywordList = null;
    private ArrayList _languageList = null;
    private String _displayPin;// for displaying the pin at confirmation page
    private String _pinRequired;// set at the form
    private String _txnID;
    private boolean _finalMesasge = false;
    private C2STransferVO _transferVO;

    // add for gift Recharge
    private String _gifterLanguageCode;
    private String _gifterLanguageCodeDesc;
    private boolean _giftServiceExist = false;
    private String _gifterMsisdn;
    private String _gifterName;
    private String _gifterServiceCode;
    // add for PSTN and Internate Recharge Service
    private boolean _pstnRechargeExist = false;
    private String _notificationMsisdn = null;
    private boolean _pstnOrIntrServiceCode = false;
    private boolean _iatIntRechargeExist = false;
    private boolean _iatIntRecharge = false;
    private boolean _iatRoamRecharge = false;
    private boolean _iatRecLangRecharge = false;

    // added by vikram c2s web recharge screen
    private String _multipleEntry = "S";
    private String _confirmAmount;
    private String _confirmSubscriberMSISDN;
    private String _dispalyMsisdn = null;
    private ArrayList _balanceList = null;
    private long _currentBalance = 0;
    private String _showBalance = "N";
    private String _countryCode = null;
    private String _printVoucherPin;

    // added for multiple voucher download
    private String _noOfVouchers;
    private boolean _multipleVoucherDownloadExist = false;
    private ArrayList _denominationList = null;
    private ArrayList _voucherSerialAndPinList = null;
    private String _denomination = null;
    private String _decryptionKey;
    private String _firstSerialNo;
    private String _lastSerialNo;
    private String _saleBatchNo;

    // add for the SID
    private String _subscriberSid;
    private String _subscriberTmpMsisdn;
    // For CAPTCHA
    private String j_captcha_response = null;
    private int _invoiceSize;
    private String _invoiceno;
    private String _xMontoDeudaTotal;
    private String _xOpcionRecaudacion;
    private String _xCodTipoServicio;
    private ArrayList _enquiryItemList; // List of the Items in the transfer
    // request



	private boolean _invoicetag = false;
    private String _billPaymentTxnID;
    private String _serialNo;
    // added for ppbal enquiry
    private String _totAmtdue;
    

	private String _MinAmtDue;
    private ArrayList _currencyList = null;
    private boolean multiCurrencyRechargeExist = false;
    private String currencyCode = "";
    private String currencyCodeDesc="";
    private String _currencyServiceCode="";
	private boolean multiCurrencyRecharge = false;
	private String _finalResponse="";
	private String _txnstatus;
	private static final long serialVersionUID = 1L;
    
    public String getSubscriberTmpMsisdn() {
        return _subscriberTmpMsisdn;
    }

    public void setSubscriberTmpMsisdn(String subscriberTmpMsisdn) {
        _subscriberTmpMsisdn = subscriberTmpMsisdn;
    }

    public String getSubscriberSid() {
        return _subscriberSid;
    }

    public void setSubscriberSid(String subscriberSid) {
        _subscriberSid = subscriberSid;
    }

    public String getDenomination() {
        return _denomination;
    }

    public void setDenomination(String denomination) {
        _denomination = denomination;
    }

    public ArrayList getVoucherSerialAndPinList() {
        return _voucherSerialAndPinList;
    }

    public void setVoucherSerialAndPinList(ArrayList voucherSerialAndPinList) {
        _voucherSerialAndPinList = voucherSerialAndPinList;
    }

    public boolean isMultipleVoucherDownloadExist() {
        return _multipleVoucherDownloadExist;
    }

    public void setMultipleVoucherDownloadExist(boolean multipleVoucherDownloadExist) {
        _multipleVoucherDownloadExist = multipleVoucherDownloadExist;
    }

    public String getNoOfVouchers() {
        return _noOfVouchers;
    }

    public void setNoOfVouchers(String noOfVouchers) {
        _noOfVouchers = noOfVouchers;
    }

    public String getDecryptionKey() {
        return _decryptionKey;
    }

    public void setDecryptionKey(String decryptionKey) {
        _decryptionKey = decryptionKey;
    }

    public String getFirstSerialNo() {
        return _firstSerialNo;
    }

    public void setFirstSerialNo(String firstSerialNo) {
        _firstSerialNo = firstSerialNo;
    }

    public String getLastSerialNo() {
        return _lastSerialNo;
    }

    public void setLastSerialNo(String lastSerialNo) {
        _lastSerialNo = lastSerialNo;
    }

    public String getSaleBatchNo() {
        return _saleBatchNo;
    }

    public void setSaleBatchNo(String saleBatchNo) {
        _saleBatchNo = saleBatchNo;
    }

    /**
     * @return the printVoucherPin
     */
    public String getPrintVoucherPin() {
        return _printVoucherPin;
    }

    /**
     * @param printVoucherPin
     *            the printVoucherPin to set
     */
    public void setPrintVoucherPin(String printVoucherPin) {
        _printVoucherPin = printVoucherPin;
    }

    /**
     * constructor of C2SRechargeForm
     */
    public C2SRechargeModel() {
    }

    public int getServiceTypeListSize() {
        if (_serviceTypeList != null) {
            return _serviceTypeList.size();
        }
        return 0;
    }

    public int getSubServiceTypeListSize() {
        if (_subServiceTypeList != null) {
            return _subServiceTypeList.size();
        }
        return 0;
    }

   

    
    
    public String getAmount() {
        return _amount;
    }

    public void setAmount(String amount) {
        _amount = amount;
    }

    public String getLoginUserMsisdn() {
        return _loginUserMsisdn;
    }

    public void setLoginUserMsisdn(String loginUserMsisdn) {
        _loginUserMsisdn = loginUserMsisdn;
    }

    public String getPin() {
        return _pin;
    }

    public void setPin(String pin) {
        _pin = pin;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    public ArrayList getServiceTypeList() {
        return _serviceTypeList;
    }

    public void setServiceTypeList(ArrayList serviceTypeList) {
        _serviceTypeList = serviceTypeList;
    }

    
    
    
    public String getSubscriberMsisdn() {
        return _subscriberMsisdn;
    }

    public void setSubscriberMsisdn(String subscriberMsisdn) {
        _subscriberMsisdn = subscriberMsisdn;
    }

    public String getSubServiceType() {
        return _subServiceType;
    }

    public void setSubServiceType(String subServiceType) {
        _subServiceType = subServiceType;
    }

    public ArrayList getSubServiceTypeList() {
        return _subServiceTypeList;
    }

    public void setSubServiceTypeList(ArrayList subServiceTypeList) {
        _subServiceTypeList = subServiceTypeList;
    }

    public String getServiceTypeDes() {
        return _serviceTypeDes;
    }

    public void setServiceTypeDes(String serviceTypeDes) {
        _serviceTypeDes = serviceTypeDes;
    }

    public String getSubServiceTypeDes() {
        return _subServiceTypeDes;
    }

    public void setSubServiceTypeDes(String subServiceTypeDes) {
        _subServiceTypeDes = subServiceTypeDes;
    }

    public ArrayList getServiceKeywordList() {
        return _serviceKeywordList;
    }

    public void setServiceKeywordList(ArrayList serviceKeywordList) {
        _serviceKeywordList = serviceKeywordList;
    }

    public String getPinRequired() {
        return _pinRequired;
    }

    public void setPinRequired(String pinRequired) {
        _pinRequired = pinRequired;
    }

    public String getDisplayPin() {
        return _displayPin;
    }

    public void setDisplayPin(String displayPin) {
        _displayPin = displayPin;
    }

    public String getLanguageCode() {
        return _languageCode;
    }

    public void setLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }

    public String getLanguageCodeDesc() {
        return _languageCodeDesc;
    }

    public void setLanguageCodeDesc(String languageCodeDesc) {
        _languageCodeDesc = languageCodeDesc;
    }

    public ArrayList getLanguageList() {
        return _languageList;
    }

    public void setLanguageList(ArrayList languageList) {
        _languageList = languageList;
    }

    public int getLanguageListSize() {
        if (_languageList != null && !_languageList.isEmpty()) {
            return _languageList.size();
        } else {
            return 0;
        }
    }



    /**
     * @return Returns the txnStatus.
     */
    public String getTxnID() {
        return _txnID;
    }

    /**
     * @param txnStatus
     *            The txnStatus to set.
     */
    public void setTxnID(String txnStatus) {
        _txnID = txnStatus;
    }

    /**
     * @return Returns the finalMesasge.
     */
    public boolean isFinalMesasge() {
        return _finalMesasge;
    }

    /**
     * @param finalMesasge
     *            The finalMesasge to set.
     */
    public void setFinalMesasge(boolean finalMesasge) {
        _finalMesasge = finalMesasge;
    }

    /**
     * @return Returns the transferVO.
     */
    public C2STransferVO getTransferVO() {
        return _transferVO;
    }

    /**
     * @param transferVO
     *            The transferVO to set.
     */
    public void setTransferVO(C2STransferVO transferVO) {
        _transferVO = transferVO;
    }

    /**
     * @return Returns the _gifterLanguageCode.
     */
    public String getGifterLanguageCode() {
        return _gifterLanguageCode;
    }

    /**
     * @param languageCode
     *            The _gifterLanguageCode to set.
     */
    public void setGifterLanguageCode(String languageCode) {
        _gifterLanguageCode = languageCode;
    }

    /**
     * @return Returns the _giftServiceExist.
     */
    public boolean isGiftServiceExist() {
        return _giftServiceExist;
    }

    /**
     * @param serviceExist
     *            The _giftServiceExist to set.
     */
    public void setGiftServiceExist(boolean serviceExist) {
        _giftServiceExist = serviceExist;
    }

    /**
     * @return Returns the _gifterMsisdn.
     */
    public String getGifterMsisdn() {
        return _gifterMsisdn;
    }

    /**
     * @param msisdn
     *            The _gifterMsisdn to set.
     */
    public void setGifterMsisdn(String msisdn) {
        _gifterMsisdn = msisdn;
    }

    /**
     * @return Returns the _gifterName.
     */
    public String getGifterName() {
        return _gifterName;
    }

    /**
     * @param name
     *            The _gifterName to set.
     */
    public void setGifterName(String name) {
        _gifterName = name;
    }

    /**
     * @return Returns the _gifterLanguageCodeDesc.
     */
    public String getGifterLanguageCodeDesc() {
        return _gifterLanguageCodeDesc;
    }

    /**
     * @param languageCodeDesc
     *            The _gifterLanguageCodeDesc to set.
     */
    public void setGifterLanguageCodeDesc(String languageCodeDesc) {
        _gifterLanguageCodeDesc = languageCodeDesc;
    }

    /**
     * @return Returns the _gifterServiceCode.
     */
    public String getGifterServiceCode() {
        return _gifterServiceCode;
    }

    /**
     * @param serviceCode
     *            The _gifterServiceCode to set.
     */
    public void setGifterServiceCode(String serviceCode) {
        _gifterServiceCode = serviceCode;
    }

    /**
     * @return Returns the pstnRechargeExist.
     */
    public boolean isPstnRechargeExist() {
        return _pstnRechargeExist;
    }

    /**
     * @param pstnRechargeExist
     *            The pstnRechargeExist to set.
     */
    public void setPstnRechargeExist(boolean pstnRechargeExist) {
        _pstnRechargeExist = pstnRechargeExist;
    }

    /**
     * @return Returns the notificationMSISDN.
     */
    public String getNotificationMsisdn() {
        return _notificationMsisdn;
    }

    /**
     * @param notificationMSISDN
     *            The notificationMSISDN to set.
     */
    public void setNotificationMsisdn(String notificationMsisdn) {
        _notificationMsisdn = notificationMsisdn;
    }

    /**
     * @return Returns the pstnOrIntrServiceCode.
     */
    public boolean getPstnOrIntrServiceCode() {
        return _pstnOrIntrServiceCode;
    }

    /**
     * @param pstnOrIntrServiceCode
     *            The pstnOrIntrServiceCode to set.
     */
    public void setPstnOrIntrServiceCode(boolean pstnOrIntrServiceCode) {
        _pstnOrIntrServiceCode = pstnOrIntrServiceCode;
    }

    public boolean isIatIntRechargeExist() {
        return _iatIntRechargeExist;
    }

    public void setIatIntRechargeExist(boolean iatIntRechargeExist) {
        _iatIntRechargeExist = iatIntRechargeExist;
    }

    public boolean isIatIntRecharge() {
        return _iatIntRecharge;
    }

    public void setIatIntRecharge(boolean iatIntRecharge) {
        _iatIntRecharge = iatIntRecharge;
    }

    public boolean isIatRoamRecharge() {
        return _iatRoamRecharge;
    }

    public void setIatRoamRecharge(boolean iatRoamRecharge) {
        _iatRoamRecharge = iatRoamRecharge;
    }

    /**
     * @return Returns the iatRecLangRecharge.
     */
    public boolean isIatRecLangRecharge() {
        return _iatRecLangRecharge;
    }

    /**
     * @param iatRecLangRecharge
     *            The iatRecLangRecharge to set.
     */
    public void setIatRecLangRecharge(boolean iatRecLangRecharge) {
        _iatRecLangRecharge = iatRecLangRecharge;
    }

    /**
     * @return the multipleEntry
     */
    public String getMultipleEntry() {
        return _multipleEntry;
    }

    /**
     * @param multipleEntry
     *            the multipleEntry to set
     */
    public void setMultipleEntry(String multipleEntry) {
        this._multipleEntry = multipleEntry;
    }

    /**
     * @return the confirmAmount
     */
    public String getConfirmAmount() {
        return _confirmAmount;
    }

    /**
     * @param confirmAmount
     *            the confirmAmount to set
     */
    public void setConfirmAmount(String confirmAmount) {
        this._confirmAmount = confirmAmount;
    }

    /**
     * @return the confirmSubscriberMSISDN
     */
    public String getConfirmSubscriberMSISDN() {
        return _confirmSubscriberMSISDN;
    }

    /**
     * @param confirmSubscriberMSISDN
     *            the confirmSubscriberMSISDN to set
     */
    public void setConfirmSubscriberMSISDN(String confirmSubscriberMSISDN) {
        this._confirmSubscriberMSISDN = confirmSubscriberMSISDN;
    }

    /**
     * @return Returns the dispalyMsisdn.
     */
    public String getDispalyMsisdn() {
        return _dispalyMsisdn;
    }

    /**
     * @param dispalyMsisdn
     *            The dispalyMsisdn to set.
     */
    public void setDispalyMsisdn(String dispalyMsisdn) {
        _dispalyMsisdn = dispalyMsisdn;
    }

    /**
     * @return Returns the currentBalance.
     */

    /**
     * @return Returns the balanceList.
     */
    public ArrayList getBalanceList() {
        return _balanceList;
    }

    /**
     * @param balanceList
     *            The balanceList to set.
     */
    public void setBalanceList(ArrayList balanceList) {
        _balanceList = balanceList;
    }

    public int getBalanceListSize() {
        if (_balanceList == null) {
            return 0;
        } else {
            return _balanceList.size();
        }
    }

    /**
     * @return Returns the currentBalance.
     */
    public long getCurrentBalance() {
        return _currentBalance;
    }

    /**
     * @param currentBalance
     *            The currentBalance to set.
     */
    public void setCurrentBalance(long currentBalance) {
        _currentBalance = currentBalance;
    }

    /**
     * @return Returns the showBalance.
     */
    public String getShowBalance() {
        return _showBalance;
    }

    /**
     * @param showBalance
     *            The showBalance to set.
     */
    public void setShowBalance(String showBalance) {
        _showBalance = showBalance;
    }

    public String getCountryCode() {
        return _countryCode;
    }

    public void setCountryCode(String code) {
        _countryCode = code;
    }

    public ArrayList getDenominationList() {
        return _denominationList;
    }

    public void setDenominationList(ArrayList denominationList) {
        _denominationList = denominationList;
    }

    public String getJ_captcha_response() {
        return j_captcha_response;
    }

    public void setJ_captcha_response(String j_captcha_response) {
        this.j_captcha_response = j_captcha_response;
    }

    /**
     * @return Returns the invoiceSize.
     */

    public int getInvoiceSize() {
        return _invoiceSize;
    }

    /**
     * @param size
     *            The invoiceSize to set.
     */
    public void setInvoiceSize(int size) {
        _invoiceSize = size;
    }

    /**
     * @return Returns the xMontoDeudaTotal.
     */
    public String getxMontoDeudaTotal() {
        return _xMontoDeudaTotal;
    }

    /**
     * @param montoDeudaTotal
     *            The xMontoDeudaTotal to set.
     */
    public void setxMontoDeudaTotal(String montoDeudaTotal) {
        _xMontoDeudaTotal = montoDeudaTotal;
    }

    /**
     * @return Returns the xOpcionRecaudacion.
     */
    public String getxOpcionRecaudacion() {
        return _xOpcionRecaudacion;
    }

    /**
     * @param opcionRecaudacion
     *            The xOpcionRecaudacion to set.
     */
    public void setxOpcionRecaudacion(String opcionRecaudacion) {
        _xOpcionRecaudacion = opcionRecaudacion;
    }

    /**
     * @return Returns the xCodTipoServicio.
     */
    public String getxCodTipoServicio() {
        return _xCodTipoServicio;
    }

    /**
     * @param codTipoServicio
     *            The codTipoServicio to set.
     */
    public void setxCodTipoServicio(String codTipoServicio) {
        _xCodTipoServicio = codTipoServicio;
    }

    /**
     * @return Returns the enquiryItemList.
     */
    public ArrayList getEnquiryItemList() {
        return _enquiryItemList;
    }

    /**
     * @param itemList
     *            The EnquiryItemList to set.
     */
    public void setEnquiryItemList(ArrayList itemList) {
        _enquiryItemList = itemList;
    }

    /**
     * @param index
     * @return Returns the EnquiryVO at index i.
     */
    public EnquiryVO getEnquiryIndexed(int i) {
        return (EnquiryVO) _enquiryItemList.get(i);
    }

    /**
     * @param index
     * @param EnquiryVO
     *            The EnquiryIndex to set.
     */
    public void setEnquiryIndexed(int i, EnquiryVO enquiryVO) {
        _enquiryItemList.set(i, enquiryVO);
    }

    /**
     * @return Returns the invoiceno.
     */

    public String getInvoiceno() {
        return _invoiceno;
    }

    /**
     * @param size
     *            The invoiceno to set.
     */
    public void setInvoiceno(String size) {
        _invoiceno = size;
    }

    public boolean isInvoicetag() {
        return _invoicetag;
    }

    public void setInvoicetag(boolean invoicetag) {
        _invoicetag = invoicetag;
    }

    /**
     * @return Returns the txnStatus.
     */
    public String getBillPaymentTxnID() {
        return _billPaymentTxnID;
    }

    /**
     * @param txnStatus
     *            The txnStatus to set.
     */
    public void setBillPaymentTxnID(String txnid) {
        _billPaymentTxnID = txnid;
    }

    /**
     * @return the minAmtDue
     */
    public String getMinAmtDue() {
        return _MinAmtDue;
    }

    /**
     * @param minAmtDue
     *            the minAmtDue to set
     */
    public void setMinAmtDue(String minAmtDue) {
        _MinAmtDue = minAmtDue;
    }

    /**
     * @return the totAmtdue
     */
    public String getTotAmtdue() {
        return _totAmtdue;
    }

    /**
     * @param totAmtdue
     *            the totAmtdue to set
     */
    public void setTotAmtdue(String totAmtdue) {
        _totAmtdue = totAmtdue;

    }

    public String getSerialNo() {
        return _serialNo;
    }

    public void setSerialNo(String serialNo) {
        _serialNo = serialNo;
    }
   
    
    public boolean isMultiCurrencyRecharge() {
		return multiCurrencyRecharge;
	}

	public void setMultiCurrencyRecharge(boolean multiCurrencyRecharge) {
		this.multiCurrencyRecharge = multiCurrencyRecharge;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	public String getCurrencyCodeDesc() {
		return currencyCodeDesc;
	}

	public void setCurrencyCodeDesc(String currencyCodeDesc) {
		this.currencyCodeDesc = currencyCodeDesc;
	}
	
	public boolean isMultiCurrencyRechargeExist() {
		return multiCurrencyRechargeExist;
	}

	public void setMultiCurrencyRechargeExist(boolean multiCurrencyRechargeExist) {
		this.multiCurrencyRechargeExist = multiCurrencyRechargeExist;
	}

	
    public ArrayList Currency() {
        return _currencyList;
    }

    public void setCurrencyList(ArrayList currencyList) {
        _currencyList = currencyList;
    }

    public int getCurrencyListSize() {
        if (_currencyList != null && !_currencyList.isEmpty()) {
            return _currencyList.size();
        } else {
            return 0;
        }
    }

    public ArrayList getCurrencyList() {
        return _currencyList;
    }
	/**
     * @return Returns the _currencyServiceCode.
     */
    public String getCurrencyServiceCode() {
        return _currencyServiceCode;
    }

    /**
     * @param serviceCode
     *            The _currencyServiceCodeto set.
     */
    public void setCurrencyServiceCode(String currencyServiceCode) {
        _currencyServiceCode= currencyServiceCode;
    }

    public String getFinalResponse(){
    	return _finalResponse;
    }
	
    public void setFinalResponse(String finalResponse){
    	_finalResponse = finalResponse;
    }
    
    public String getTxnStatus(){
    	return _txnstatus;
    }
    
    public void setTxnStatus(String txnstatus){
    	_txnstatus=txnstatus;
    }
    
}
