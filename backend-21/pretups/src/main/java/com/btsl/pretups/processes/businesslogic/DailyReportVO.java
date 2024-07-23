package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)DailyReportVO.java
 *                        Copyright(c) 2006, Bharti Telesoft Ltd. All
 *                        Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ved Prakash Sharma 21/09/2006 Initial Creation
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 * 
 */
import java.io.Serializable;
import java.util.ArrayList;

import com.btsl.util.BTSLUtil;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DailyReportVO implements Serializable, Comparable {
    private String _networkCode;
    private String _productCode;
    private int _productCount;
    private String _amount;
    private String _count;
    private String _serviceType;
    private String _serviceTypeName;
    private String _productName;
    private String _networkName;
    private String _trnfrSubType;
    private String _stock;

    private String _monthFailAmountStr = "0";
    private long _monthFailAmount;
    private long _monthFailCount;
    private long _monthTotalCount;
    private long _monthSuccessCount;
    private long _monthTotalAmount;
    private long _monthSuccessAmount;
    private long _monthUnderProcessCount;
    private long _monthAmbigousCount;
    private long _monthUnderProcessAmount;
    private long _monthAmbigousAmount;
    private String _monthTotalAmountStr = "0";
    private String _monthSuccessAmountStr = "0";
    private String _monthAmbigousAmountStr = "0";
    private String _monthUnderProcessAmountStr = "0";

    private String _dailyFailAmountStr = "0";
    private long _dailyFailAmount;
    private long _dailyFailCount;
    private long _dailyTotalCount;
    private long _dailySuccessCount;
    private long _dailyUnderProcessCount;
    private long _dailyAmbigousCount;
    private long _dailyUnderProcessAmount;
    private long _dailyAmbigousAmount;
    private long _dailyTotalAmount;
    private long _dailySuccessAmount;
    private String _dailyTotalAmountStr = "0";
    private String _dailySuccessAmountStr = "0";
    private String _dailyAmbigousAmountStr = "0";
    private String _dailyUnderProcessAmountStr = "0";

    private String _errorDesc;
    private ArrayList _productCodeList = null;

    private long[] dailyTotalCount = new long[24];
    private long[] dailySuccessCount = new long[24];
    private long _prodAmount;
    private long _prodCount;

    // added for mgt summary report
    // main report
    private String _categoryCode;
    private String _categoryName;
    private String _senderInterfaceCode;
    private String _senderInterfaceName;
    private String _receiverInterfaceCode;
    private String _receiverInterfaceName;

    // interface wise report
    private long _dailyTotalSenderValCount;
    private long _dailyTotalDebitCount;
    private long _dailyTotalReceiverValCount;
    private long _dailyTotalCreditCount;
    private long _monthlyTotalSenderValCount;
    private long _monthlyTotalDebitCount;
    private long _monthlyTotalReceiverValCount;
    private long _monthlyTotalCreditCount;
    private long _dailyFailSenderValCount;
    private long _dailyFailDebitCount;
    private long _dailyFailReceiverValCount;
    private long _dailyFailCreditCount;
    private long _monthlyFailSenderValCount;
    private long _monthlyFailDebitCount;
    private long _monthlyFailReceiverValCount;
    private long _monthlyFailCreditCount;

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
        _categoryCode = categoryCode;
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
     * @return Returns the receiverInterfaceCode.
     */
    public String getReceiverInterfaceCode() {
        return _receiverInterfaceCode;
    }

    /**
     * @param receiverInterfaceCode
     *            The receiverInterfaceCode to set.
     */
    public void setReceiverInterfaceCode(String receiverInterfaceCode) {
        _receiverInterfaceCode = receiverInterfaceCode;
    }

    /**
     * @return Returns the receiverInterfaceName.
     */
    public String getReceiverInterfaceName() {
        return _receiverInterfaceName;
    }

    /**
     * @param receiverInterfaceName
     *            The receiverInterfaceName to set.
     */
    public void setReceiverInterfaceName(String receiverInterfaceName) {
        _receiverInterfaceName = receiverInterfaceName;
    }

    /**
     * @return Returns the senderInterfaceCode.
     */
    public String getSenderInterfaceCode() {
        return _senderInterfaceCode;
    }

    /**
     * @param senderInterfaceCode
     *            The senderInterfaceCode to set.
     */
    public void setSenderInterfaceCode(String senderInterfaceCode) {
        _senderInterfaceCode = senderInterfaceCode;
    }

    /**
     * @return Returns the senderInterfaceName.
     */
    public String getSenderInterfaceName() {
        return _senderInterfaceName;
    }

    /**
     * @param senderInterfaceName
     *            The senderInterfaceName to set.
     */
    public void setSenderInterfaceName(String senderInterfaceName) {
        _senderInterfaceName = senderInterfaceName;
    }

    /**
     * @param index
     * @param dailySuccessCount
     */
    public void setDailySuccessCount(int index, long dailySuccessCount) {
        this.dailySuccessCount[index] = dailySuccessCount;
    }

    /**
     * @param index
     * @param dailyTotalCount
     */
    public void setDailyTotalCount(int index, long dailyTotalCount) {
        this.dailyTotalCount[index] = dailyTotalCount;
    }

    /**
     * @param index
     * @return
     */
    public long getDailySuccessCount(int index) {
        return this.dailySuccessCount[index];
    }

    /**
     * @param index
     * @return
     */
    public long getDailyTotalCount(int index) {
        return this.dailyTotalCount[index];
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        DailyReportVO obj = (DailyReportVO) arg0;
        if (BTSLUtil.isNullString(this._networkCode))
            return 1;
        else if (BTSLUtil.isNullString(obj._networkCode))
            return -1;
        else if (this._networkCode.compareTo(obj._networkCode) > 0)
            return 1;
        else
            return -1;
    }

    public String getServiceTypeName() {
        return this._serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this._serviceTypeName = serviceTypeName;
    }

    public String getStock() {
        return this._stock;
    }

    public void setStock(String stock) {
        this._stock = stock;
    }

    public long getDailyAmbigousAmount() {
        return this._dailyAmbigousAmount;
    }

    public void setDailyAmbigousAmount(long dailyAmbigousAmount) {
        this._dailyAmbigousAmount = dailyAmbigousAmount;
    }

    public String getDailyAmbigousAmountStr() {
        return this._dailyAmbigousAmountStr;
    }

    public void setDailyAmbigousAmountStr(String dailyAmbigousAmountStr) {
        this._dailyAmbigousAmountStr = dailyAmbigousAmountStr;
    }

    public long getDailyAmbigousCount() {
        return this._dailyAmbigousCount;
    }

    public void setDailyAmbigousCount(long dailyAmbigousCount) {
        this._dailyAmbigousCount = dailyAmbigousCount;
    }

    public long getDailySuccessAmount() {
        return this._dailySuccessAmount;
    }

    public void setDailySuccessAmount(long dailySuccessAmount) {
        this._dailySuccessAmount = dailySuccessAmount;
    }

    public String getDailySuccessAmountStr() {
        return this._dailySuccessAmountStr;
    }

    public void setDailySuccessAmountStr(String dailySuccessAmountStr) {
        this._dailySuccessAmountStr = dailySuccessAmountStr;
    }

    public long getDailyTotalAmount() {
        return this._dailyTotalAmount;
    }

    public void setDailyTotalAmount(long dailyTotalAmount) {
        this._dailyTotalAmount = dailyTotalAmount;
    }

    public String getDailyTotalAmountStr() {
        return this._dailyTotalAmountStr;
    }

    public void setDailyTotalAmountStr(String dailyTotalAmountStr) {
        this._dailyTotalAmountStr = dailyTotalAmountStr;
    }

    public long getDailyUnderProcessAmount() {
        return this._dailyUnderProcessAmount;
    }

    public void setDailyUnderProcessAmount(long dailyUnderProcessAmount) {
        this._dailyUnderProcessAmount = dailyUnderProcessAmount;
    }

    public String getDailyUnderProcessAmountStr() {
        return this._dailyUnderProcessAmountStr;
    }

    public void setDailyUnderProcessAmountStr(String dailyUnderProcessAmountStr) {
        this._dailyUnderProcessAmountStr = dailyUnderProcessAmountStr;
    }

    public long getDailyUnderProcessCount() {
        return this._dailyUnderProcessCount;
    }

    public void setDailyUnderProcessCount(long dailyUnderProcessCount) {
        this._dailyUnderProcessCount = dailyUnderProcessCount;
    }

    public long getMonthAmbigousAmount() {
        return this._monthAmbigousAmount;
    }

    public void setMonthAmbigousAmount(long monthAmbigousAmount) {
        this._monthAmbigousAmount = monthAmbigousAmount;
    }

    public String getMonthAmbigousAmountStr() {
        return this._monthAmbigousAmountStr;
    }

    public void setMonthAmbigousAmountStr(String monthAmbigousAmountStr) {
        this._monthAmbigousAmountStr = monthAmbigousAmountStr;
    }

    public long getMonthAmbigousCount() {
        return this._monthAmbigousCount;
    }

    public void setMonthAmbigousCount(long monthAmbigousCount) {
        this._monthAmbigousCount = monthAmbigousCount;
    }

    public long getMonthSuccessAmount() {
        return this._monthSuccessAmount;
    }

    public void setMonthSuccessAmount(long monthSuccessAmount) {
        this._monthSuccessAmount = monthSuccessAmount;
    }

    public String getMonthSuccessAmountStr() {
        return this._monthSuccessAmountStr;
    }

    public void setMonthSuccessAmountStr(String monthSuccessAmountStr) {
        this._monthSuccessAmountStr = monthSuccessAmountStr;
    }

    public long getMonthTotalAmount() {
        return this._monthTotalAmount;
    }

    public void setMonthTotalAmount(long monthTotalAmount) {
        this._monthTotalAmount = monthTotalAmount;
    }

    public String getMonthTotalAmountStr() {
        return this._monthTotalAmountStr;
    }

    public void setMonthTotalAmountStr(String monthTotalAmountStr) {
        this._monthTotalAmountStr = monthTotalAmountStr;
    }

    public long getMonthUnderProcessAmount() {
        return this._monthUnderProcessAmount;
    }

    public void setMonthUnderProcessAmount(long monthUnderProcessAmount) {
        this._monthUnderProcessAmount = monthUnderProcessAmount;
    }

    public String getMonthUnderProcessAmountStr() {
        return this._monthUnderProcessAmountStr;
    }

    public void setMonthUnderProcessAmountStr(String monthUnderProcessAmountStr) {
        this._monthUnderProcessAmountStr = monthUnderProcessAmountStr;
    }

    public long getMonthUnderProcessCount() {
        return this._monthUnderProcessCount;
    }

    public void setMonthUnderProcessCount(long monthUnderProcessCount) {
        this._monthUnderProcessCount = monthUnderProcessCount;
    }

    /**
     * @return
     */
    public long getDailySuccessCount() {
        return this._dailySuccessCount;
    }

    /**
     * @param dailySuccessCount
     */
    public void setDailySuccessCount(long dailySuccessCount) {
        this._dailySuccessCount = dailySuccessCount;
    }

    /**
     * @return
     */
    public long getDailyTotalCount() {
        return this._dailyTotalCount;
    }

    /**
     * @param dailyTotalCount
     */
    public void setDailyTotalCount(long dailyTotalCount) {
        this._dailyTotalCount = dailyTotalCount;
    }

    /**
     * @return
     */
    public long getMonthSuccessCount() {
        return this._monthSuccessCount;
    }

    /**
     * @param monthSuccessCount
     */
    public void setMonthSuccessCount(long monthSuccessCount) {
        this._monthSuccessCount = monthSuccessCount;
    }

    /**
     * @return
     */
    public long getMonthTotalCount() {
        return this._monthTotalCount;
    }

    /**
     * @param monthTotalCount
     */
    public void setMonthTotalCount(long monthTotalCount) {
        this._monthTotalCount = monthTotalCount;
    }

    /**
     * @return
     */
    public ArrayList getProductCodeList() {
        return this._productCodeList;
    }

    /**
     * @param productCodeList
     */
    public void setProductCodeList(ArrayList productCodeList) {
        this._productCodeList = productCodeList;
    }

    /**
     * @return
     */
    public int getProductCount() {
        return this._productCount;
    }

    /**
     * @param productCount
     */
    public void setProductCount(int productCount) {
        this._productCount = productCount;
    }

    /**
     * @return
     */
    public String getDailyFailAmountStr() {
        return this._dailyFailAmountStr;
    }

    /**
     * @param dailyAmountStr
     */
    public void setDailyFailAmountStr(String dailyAmountStr) {
        this._dailyFailAmountStr = dailyAmountStr;
    }

    /**
     * @return
     */
    public String getMonthFailAmountStr() {
        return this._monthFailAmountStr;
    }

    /**
     * @param monthAmountStr
     */
    public void setMonthFailAmountStr(String monthAmountStr) {
        this._monthFailAmountStr = monthAmountStr;
    }

    /**
     * @return
     */
    public String getAmount() {
        return _amount;
    }

    /**
     * @param amount
     */
    public void setAmount(String amount) {
        _amount = amount;
    }

    /**
     * @return
     */
    public String getCount() {
        return _count;
    }

    /**
     * @param count
     */
    public void setCount(String count) {
        _count = count;
    }

    /**
     * @return
     */
    public String getNetworkCode() {
        return _networkCode;
    }

    /**
     * @param networkCode
     */
    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    /**
     * @return
     */
    public String getProductCode() {
        return _productCode;
    }

    /**
     * @param productCode
     */
    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    /**
     * @return
     */
    public String getServiceType() {
        return _serviceType;
    }

    /**
     * @param serviceType
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }

    /**
     * @return
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * @return
     */
    public String getNetworkName() {
        return _networkName;
    }

    /**
     * @param networkName
     */
    public void setNetworkName(String networkName) {
        _networkName = networkName;
    }

    /**
     * @return
     */
    public String getTrnfrSubType() {
        return _trnfrSubType;
    }

    /**
     * @param trnfrSubType
     */
    /**
     * @param trnfrSubType
     */
    public void setTrnfrSubType(String trnfrSubType) {
        _trnfrSubType = trnfrSubType;
    }

    /**
     * @return
     */
    public long getDailyFailAmount() {
        return this._dailyFailAmount;
    }

    /**
     * @param dailyAmount
     */
    public void setDailyFailAmount(long dailyAmount) {
        this._dailyFailAmount = dailyAmount;
    }

    /**
     * @return
     */
    public long getDailyFailCount() {
        return this._dailyFailCount;
    }

    /**
     * @param dailyCount
     */
    public void setDailyFailCount(long dailyCount) {
        this._dailyFailCount = dailyCount;
    }

    /**
     * @return
     */
    public String getErrorDesc() {
        return this._errorDesc;
    }

    /**
     * @param errorDesc
     */
    public void setErrorDesc(String errorDesc) {
        this._errorDesc = errorDesc;
    }

    /**
     * @return
     */
    public long getMonthFailAmount() {
        return this._monthFailAmount;
    }

    /**
     * @param monthAmount
     */
    public void setMonthFailAmount(long monthAmount) {
        this._monthFailAmount = monthAmount;
    }

    /**
     * @return
     */
    public long getMonthFailCount() {
        return this._monthFailCount;
    }

    /**
     * @param monthCount
     */
    public void setMonthFailCount(long monthCount) {
        this._monthFailCount = monthCount;
    }

    public long getProdAmount() {
        return _prodAmount;
    }

    public void setProdAmount(long prodAmount) {
        _prodAmount = prodAmount;
    }

    public long getProdCount() {
        return _prodCount;
    }

    public void setProdCount(long prodCount) {
        _prodCount = prodCount;
    }

    /**
     * @return Returns the dailyFailCreditCount.
     */
    public long getDailyFailCreditCount() {
        return _dailyFailCreditCount;
    }

    /**
     * @param dailyFailCreditCount
     *            The dailyFailCreditCount to set.
     */
    public void setDailyFailCreditCount(long dailyFailCreditCount) {
        _dailyFailCreditCount = dailyFailCreditCount;
    }

    /**
     * @return Returns the dailyFailDebitCount.
     */
    public long getDailyFailDebitCount() {
        return _dailyFailDebitCount;
    }

    /**
     * @param dailyFailDebitCount
     *            The dailyFailDebitCount to set.
     */
    public void setDailyFailDebitCount(long dailyFailDebitCount) {
        _dailyFailDebitCount = dailyFailDebitCount;
    }

    /**
     * @return Returns the dailyFailReceiverValCount.
     */
    public long getDailyFailReceiverValCount() {
        return _dailyFailReceiverValCount;
    }

    /**
     * @param dailyFailReceiverValCount
     *            The dailyFailReceiverValCount to set.
     */
    public void setDailyFailReceiverValCount(long dailyFailReceiverValCount) {
        _dailyFailReceiverValCount = dailyFailReceiverValCount;
    }

    /**
     * @return Returns the dailyFailSenderValCount.
     */
    public long getDailyFailSenderValCount() {
        return _dailyFailSenderValCount;
    }

    /**
     * @param dailyFailSenderValCount
     *            The dailyFailSenderValCount to set.
     */
    public void setDailyFailSenderValCount(long dailyFailSenderValCount) {
        _dailyFailSenderValCount = dailyFailSenderValCount;
    }

    /**
     * @return Returns the dailyTotalCreditCount.
     */
    public long getDailyTotalCreditCount() {
        return _dailyTotalCreditCount;
    }

    /**
     * @param dailyTotalCreditCount
     *            The dailyTotalCreditCount to set.
     */
    public void setDailyTotalCreditCount(long dailyTotalCreditCount) {
        _dailyTotalCreditCount = dailyTotalCreditCount;
    }

    /**
     * @return Returns the dailyTotalDebitCount.
     */
    public long getDailyTotalDebitCount() {
        return _dailyTotalDebitCount;
    }

    /**
     * @param dailyTotalDebitCount
     *            The dailyTotalDebitCount to set.
     */
    public void setDailyTotalDebitCount(long dailyTotalDebitCount) {
        _dailyTotalDebitCount = dailyTotalDebitCount;
    }

    /**
     * @return Returns the dailyTotalReceiverValCount.
     */
    public long getDailyTotalReceiverValCount() {
        return _dailyTotalReceiverValCount;
    }

    /**
     * @param dailyTotalReceiverValCount
     *            The dailyTotalReceiverValCount to set.
     */
    public void setDailyTotalReceiverValCount(long dailyTotalReceiverValCount) {
        _dailyTotalReceiverValCount = dailyTotalReceiverValCount;
    }

    /**
     * @return Returns the dailyTotalSenderValCount.
     */
    public long getDailyTotalSenderValCount() {
        return _dailyTotalSenderValCount;
    }

    /**
     * @param dailyTotalSenderValCount
     *            The dailyTotalSenderValCount to set.
     */
    public void setDailyTotalSenderValCount(long dailyTotalSenderValCount) {
        _dailyTotalSenderValCount = dailyTotalSenderValCount;
    }

    /**
     * @return Returns the monthlyFailCreditCount.
     */
    public long getMonthlyFailCreditCount() {
        return _monthlyFailCreditCount;
    }

    /**
     * @param monthlyFailCreditCount
     *            The monthlyFailCreditCount to set.
     */
    public void setMonthlyFailCreditCount(long monthlyFailCreditCount) {
        _monthlyFailCreditCount = monthlyFailCreditCount;
    }

    /**
     * @return Returns the monthlyFailDebitCount.
     */
    public long getMonthlyFailDebitCount() {
        return _monthlyFailDebitCount;
    }

    /**
     * @param monthlyFailDebitCount
     *            The monthlyFailDebitCount to set.
     */
    public void setMonthlyFailDebitCount(long monthlyFailDebitCount) {
        _monthlyFailDebitCount = monthlyFailDebitCount;
    }

    /**
     * @return Returns the monthlyFailReceiverValCount.
     */
    public long getMonthlyFailReceiverValCount() {
        return _monthlyFailReceiverValCount;
    }

    /**
     * @param monthlyFailReceiverValCount
     *            The monthlyFailReceiverValCount to set.
     */
    public void setMonthlyFailReceiverValCount(long monthlyFailReceiverValCount) {
        _monthlyFailReceiverValCount = monthlyFailReceiverValCount;
    }

    /**
     * @return Returns the monthlyFailSenderValCount.
     */
    public long getMonthlyFailSenderValCount() {
        return _monthlyFailSenderValCount;
    }

    /**
     * @param monthlyFailSenderValCount
     *            The monthlyFailSenderValCount to set.
     */
    public void setMonthlyFailSenderValCount(long monthlyFailSenderValCount) {
        _monthlyFailSenderValCount = monthlyFailSenderValCount;
    }

    /**
     * @return Returns the monthlyTotalCreditCount.
     */
    public long getMonthlyTotalCreditCount() {
        return _monthlyTotalCreditCount;
    }

    /**
     * @param monthlyTotalCreditCount
     *            The monthlyTotalCreditCount to set.
     */
    public void setMonthlyTotalCreditCount(long monthlyTotalCreditCount) {
        _monthlyTotalCreditCount = monthlyTotalCreditCount;
    }

    /**
     * @return Returns the monthlyTotalDebitCount.
     */
    public long getMonthlyTotalDebitCount() {
        return _monthlyTotalDebitCount;
    }

    /**
     * @param monthlyTotalDebitCount
     *            The monthlyTotalDebitCount to set.
     */
    public void setMonthlyTotalDebitCount(long monthlyTotalDebitCount) {
        _monthlyTotalDebitCount = monthlyTotalDebitCount;
    }

    /**
     * @return Returns the monthlyTotalReceiverValCount.
     */
    public long getMonthlyTotalReceiverValCount() {
        return _monthlyTotalReceiverValCount;
    }

    /**
     * @param monthlyTotalReceiverValCount
     *            The monthlyTotalReceiverValCount to set.
     */
    public void setMonthlyTotalReceiverValCount(long monthlyTotalReceiverValCount) {
        _monthlyTotalReceiverValCount = monthlyTotalReceiverValCount;
    }

    /**
     * @return Returns the monthlyTotalSenderValCount.
     */
    public long getMonthlyTotalSenderValCount() {
        return _monthlyTotalSenderValCount;
    }

    /**
     * @param monthlyTotalSenderValCount
     *            The monthlyTotalSenderValCount to set.
     */
    public void setMonthlyTotalSenderValCount(long monthlyTotalSenderValCount) {
        _monthlyTotalSenderValCount = monthlyTotalSenderValCount;
    }
}
