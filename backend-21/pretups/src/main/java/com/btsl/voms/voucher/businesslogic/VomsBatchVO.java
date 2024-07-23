/*
 * Created on Jun 26, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.voucher.businesslogic;

import java.util.ArrayList;
import java.util.Date;

//import org.apache.struts.upload.FormFile;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.util.BTSLUtil;

/**
 * @author vikas.yadav
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */

public class VomsBatchVO implements java.io.Serializable {

    private String _batchNo;
    private String _oneTimeUsage;
    private String _productID;
    private String _batchType;
    private String _batchTypeDesc;
    private String _referenceNo;
    private String _referenceType;
    private long _noOfVoucher;
    private int _expiryPeriod;
    private String _fromSerialNo;
    private String _toSerialNo;
    private long _successCount;
    private long _failCount;
    private String _locationCode;
    private String _createdBy;
    private java.util.Date _createdDate;
    private java.util.Date _createdOn;
    private String _modifiedBy;
    private java.util.Date _modifiedDate;
    private java.util.Date _modifiedOn;
    protected String _status;
    protected String _statusDesc;
    protected long _totalVoucherPerOrder;
    protected String _startSerialNo = null;;
    private String _productName;
    private String _mrp;
    protected java.util.ArrayList _ItemList = null;
    private java.util.Date _downloadDate;
    private int _downloadCount = 0;
    private String _createdOnStr = null;
    private String _process = null;
    private String _message = null;
    private String _downloadOnStr = null;
    private int _executeCount = 0;
    private int _scheduleCount = 0;
    private int _failureCount = 0;
    private int _rcAdminDaysAllowed = 0;
    private String _denomination;
    private String _quantity;
    private String _remarks;
    private int rowIndex;
    private ArrayList _productlist;
    private ArrayList vcrTypeProductlist;
    private String _productid;
    private int _talktime;
    private int _validity;
    
    public ArrayList getVcrTypeProductlist() {
		return vcrTypeProductlist;
	}

	public void setVcrTypeProductlist(ArrayList vcrTypeProductlist) {
		this.vcrTypeProductlist = vcrTypeProductlist;
	}

	private int _apprvLvl = 0;

    private long _unUsedVouchers = 0l;
    private String _unUsedToserialNO = null;
    private String _unUsedFromserialNO = null;
    private ArrayList _unUsedBatchList = null;
    private String _unUsedBatchExists = null;

    private String _usedFromSerialNo = null;
    private String _usedToSerialNo = null;

    private String _NetworkCode = null;

    private ChannelTransferItemsVO _channelTransferItemsVO;
    private String _toUSERID = null; // gaurav
    private String userName = null;
    private String userMsisdn = null;
    private Date _expiryDate;
    private Date modifiedTime;
    private int seq_id;
    private int processScreen;
    //private FormFile _file;
    private String _fileName = null;
    private Boolean _filePresent = false;
    
    private String extTxnNo;
    private String batchID;
    private String segment;
    private String masterBatchNo;
    long quantityLong = (long) 0.0;
    long preQuantityLong = (long) 0.0;
    long preFromSerialNoLong = (long) 0.0;
    long preToSerialNoLong  = (long) 0.0;
    private String initiatedQuantity;
    private String approver1Quantity;
    private String approver2Quantity;
    private String transferId;
    
    
    
    public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getInitiatedQuantity() {
		return initiatedQuantity;
	}

	public void setInitiatedQuantity(String initiatedQuantity) {
		this.initiatedQuantity = initiatedQuantity;
	}

	public String getApprover1Quantity() {
		return approver1Quantity;
	}

	public void setApprover1Quantity(String approver1Quantity) {
		this.approver1Quantity = approver1Quantity;
	}

	public String getApprover2Quantity() {
		return approver2Quantity;
	}

	public void setApprover2Quantity(String approver2Quantity) {
		this.approver2Quantity = approver2Quantity;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getBatchID() {
		return batchID;
	}

	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}

	public void setFilePresent(Boolean filePresent) {
		this._filePresent = filePresent;
	}

	public Boolean getFilePresent() {
		return _filePresent;
	}
    
    public String getExtTxnNo() {
		return extTxnNo;
	}

	public void setExtTxnNo(String extTxnNo) {
		this.extTxnNo = extTxnNo;
	}

	public int getProcessScreen() {
		return processScreen;
	}
	
	

	public void setProcessScreen(int processScreen) {
		this.processScreen = processScreen;
	}

	/**
   *
   *
   */
    public VomsBatchVO() {
        super();
    }

    /**
     * @return Returns the batchTypeDesc.
     */
    public String getBatchTypeDesc() {
        return _batchTypeDesc;
    }

    /**
     * @param batchTypeDesc
     *            The batchTypeDesc to set.
     */
    public void setBatchTypeDesc(String batchTypeDesc) {
        _batchTypeDesc = batchTypeDesc;
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
 	*
 	*
 	*/

    public java.lang.String getBatchNo() {
        return _batchNo;
    }

    public java.lang.String getProductID() {
        return _productID;
    }

    public java.lang.String getBatchType() {
        return _batchType;
    }

    public java.lang.String getReferenceNo() {
        return _referenceNo;
    }

    public java.lang.String getReferenceType() {
        return _referenceType;
    }

    public long getNoOfVoucher() {
        return _noOfVoucher;
    }

    public java.lang.String getFromSerialNo() {
        return _fromSerialNo;
    }

    public java.lang.String getToSerialNo() {
        return _toSerialNo;
    }

    public long getSuccessCount() {
        return _successCount;
    }

    public long getFailCount() {
        return _failCount;
    }

    public java.lang.String getLocationCode() {
        return _locationCode;
    }

    public java.util.Date getCreatedDate() {
        return _createdDate;
    }

    public java.util.Date getCreatedOn() {
        return _createdOn;
    }

    public java.lang.String getCreatedBy() {
        return _createdBy;
    }

    public java.util.Date getModifiedOn() {
        return _modifiedOn;
    }

    public java.util.Date getModifiedTime() {
        return _modifiedOn;
    }

    public java.lang.String getModifiedBy() {
        return _modifiedBy;
    }

    public java.lang.String getStatus() {
        return _status;
    }

    public java.lang.String getProductName() {
        return _productName;
    }

    public void setBatchNo(java.lang.String p_batchNo) {
        _batchNo = p_batchNo;
    }

    public void setProductID(java.lang.String p_productID) {
        _productID = p_productID;
    }

    public void setBatchType(java.lang.String p_batchType) {
        _batchType = p_batchType;
    }

    public void setReferenceNo(java.lang.String p_referenceNo) {
        _referenceNo = p_referenceNo;
    }

    public void setReferenceType(java.lang.String p_referenceType) {
        _referenceType = p_referenceType;
    }

    public void setNoOfVoucher(long p_noOfVoucher) {
        _noOfVoucher = p_noOfVoucher;
    }

    public void setFromSerialNo(java.lang.String p_fromSerialNo) {
        _fromSerialNo = p_fromSerialNo;
    }

    public void setToSerialNo(java.lang.String p_toSerialNo) {
        _toSerialNo = p_toSerialNo;
    }

    public void setSuccessCount(long p_successCount) {
        _successCount = p_successCount;
    }

    public void setFailCount(long p_failCount) {
        _failCount = p_failCount;
    }

    public void setLocationCode(java.lang.String p_locationCode) {
        _locationCode = p_locationCode;
    }

    public void setCreatedDate(java.util.Date p_createdDate) {
        _createdDate = p_createdDate;
    }

    public void setCreatedOn(java.util.Date p_createdTime) {
        _createdOn = p_createdTime;
    }

    public void setCreatedBy(java.lang.String p_createdBy) {
        _createdBy = p_createdBy;
    }

    public void setModifiedDate(java.util.Date p_modifiedDate) {
        _modifiedDate = p_modifiedDate;
    }

    public void setModifiedOn(java.util.Date p_modifiedTime) {
        _modifiedOn = p_modifiedTime;
    }

    public void setModifiedBy(java.lang.String p_modifiedBy) {
        _modifiedBy = p_modifiedBy;
    }

    public void setStatus(java.lang.String p_status) {
        _status = p_status;
    }

    public void setProductName(java.lang.String p_productName) {
        _productName = p_productName;
    }

    public java.util.ArrayList getItemList() {
        return _ItemList;
    }

    public void setItemList(java.util.ArrayList list) {
        _ItemList = list;
    }

    public long getTotalVoucherPerOrder() {
        return _totalVoucherPerOrder;
    }

    public void setTotalVoucherPerOrder(long long1) {
        _totalVoucherPerOrder = long1;
    }

    public String getStartSerialNo() {
        return _startSerialNo;
    }

    public void setStartSerialNo(String string) {
        _startSerialNo = string;
    }

    public java.util.Date getDownloadDate() {
        return _downloadDate;
    }

    public void setDownloadDate(java.util.Date date) {
        _downloadDate = date;
    }

    public int getDownloadCount() {
        return _downloadCount;
    }

    public void setDownloadCount(int i) {
        _downloadCount = i;
    }

    /**
     * @return
     */
    public String getCreatedOnStr() {
        return _createdOnStr;
    }

    /**
     * @param string
     */
    public void setCreatedOnStr(String string) {
        _createdOnStr = string;
    }

    /**
     * @return
     */
    public String getProcess() {
        return _process;
    }

    /**
     * @param string
     */
    public void setProcess(String string) {
        _process = string;
    }

    /**
     * @return
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @param string
     */
    public void setMessage(String string) {
        _message = string;
    }

    /**
     * @return
     */
    public String getDownloadOnStr() {
        return _downloadOnStr;
    }

    /**
     * @param string
     */
    public void setDownloadOnStr(String string) {
        _downloadOnStr = string;
    }

    // Added By Gurjeet For Daily reports
    /**
     * @return
     */
    public int getExecuteCount() {
        return _executeCount;
    }

    /**
     * @return
     */
    public int getFailureCount() {
        return _failureCount;
    }

    /**
     * @return
     */
    public int getScheduleCount() {
        return _scheduleCount;
    }

    /**
     * @param i
     */
    public void setExecuteCount(int i) {
        _executeCount = i;
    }

    /**
     * @param i
     */
    public void setFailureCount(int i) {
        _failureCount = i;
    }

    /**
     * @param i
     */
    public void setScheduleCount(int i) {
        _scheduleCount = i;
    }

    // Additions end By Gurjeet on 09/03/04
    /**
     * @return
     */
    public int getRcAdminDaysAllowed() {
        return _rcAdminDaysAllowed;
    }

    /**
     * @param i
     */
    public void setRcAdminDaysAllowed(int i) {
        _rcAdminDaysAllowed = i;
    }

    /**
     * @return
     */
    public int getExpiryPeriod() {
        return _expiryPeriod;
    }

    /**
     * @param p_l
     */
    public void setExpiryPeriod(int p_l) {
        _expiryPeriod = p_l;
    }

    /**
     * @return
     */
    public String getOneTimeUsage() {
        return _oneTimeUsage;
    }

    /**
     * @param p_string
     */
    public void setOneTimeUsage(String p_string) {
        _oneTimeUsage = p_string;
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
     * @return the denomination
     */
    public String getDenomination() {
        return _denomination;
    }

    /**
     * @param denomination
     *            the denomination to set
     */
    public void setDenomination(String denomination) {
        _denomination = denomination;
    }

    /**
     * @return the quantity
     */
    public String getQuantity() {
        return _quantity;
    }

    /**
     * @param quantity
     *            the quantity to set
     */
    public void setQuantity(String quantity) {
        _quantity = quantity;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return _remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        this._remarks = remarks;
    }

    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *            the rowIndex to set
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * @return the productlist
     */
    public ArrayList getProductlist() {
        return _productlist;
    }

    /**
     * @param productlist
     *            the productlist to set
     */
    public void setProductlist(ArrayList productlist) {
        _productlist = productlist;
    }

    /**
     * @return the productid
     */
    public String getProductid() {
        return _productid;
    }

    /**
     * @param productid
     *            the productid to set
     */
    public void setProductid(String productid) {
        _productid = productid;
    }

    /**
     * @return the talktime
     */
    public int getTalktime() {
        return _talktime;
    }

    /**
     * @param talktime
     *            the talktime to set
     */
    public void setTalktime(int talktime) {
        _talktime = talktime;
    }

    /**
     * @return the validity
     */
    public int getValidity() {
        return _validity;
    }

    /**
     * @param validity
     *            the validity to set
     */
    public void setValidity(int validity) {
        _validity = validity;
    }

    /**
     * @return the apprvLvl
     */
    public int getApprvLvl() {
        return _apprvLvl;
    }

    /**
     * @param apprvLvl
     *            the apprvLvl to set
     */
    public void setApprvLvl(int apprvLvl) {
        _apprvLvl = apprvLvl;
    }

    public ChannelTransferItemsVO getChannelTransferItemsVO() {
        return _channelTransferItemsVO;
    }

    public void setChannelTransferItemsVO(ChannelTransferItemsVO channelTransferItemsVO) {
        _channelTransferItemsVO = new ChannelTransferItemsVO();
        _channelTransferItemsVO = channelTransferItemsVO;
    }

    
    public String get_NetworkCode() {
        return _NetworkCode;
    }

    public void set_NetworkCode(String networkCode) {
        _NetworkCode = networkCode;
    }

    public long getUnUsedVouchers() {
        return _unUsedVouchers;
    }

    public void setUnUsedVouchers(long usedVouchers) {
        _unUsedVouchers = usedVouchers;
    }

    public String getUnUsedToserialNO() {
        return _unUsedToserialNO;
    }

    public void setUnUsedToserialNO(String usedToserialNO) {
        _unUsedToserialNO = usedToserialNO;
    }

    public String getUnUsedFromserialNO() {
        return _unUsedFromserialNO;
    }

    public void setUnUsedFromserialNO(String usedFromserialNO) {
        _unUsedFromserialNO = usedFromserialNO;
    }

    public ArrayList getUnUsedBatchList() {
        return _unUsedBatchList;
    }

    public void setUnUsedBatchList(ArrayList usedBatchList) {
        _unUsedBatchList = usedBatchList;
    }

    public String getUnUsedBatchExists() {
        return _unUsedBatchExists;
    }

    public void setUnUsedBatchExists(String usedBatchExists) {
        _unUsedBatchExists = usedBatchExists;
    }

    // ////////////////////////voucher generation enhancement by Ashutosh
    // //////////////////////////////////
    private String _voucherType = null;

	public String getVouchersegment() {
		return vouchersegment;
	}

	public void setVouchersegment(String vouchersegment) {
		this.vouchersegment = vouchersegment;
	}

	private String  vouchersegment = null;

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

    public String getToUserID() {
        return _toUSERID;
    }

    public void setToUserID(String string) {
        _toUSERID = string;
    }

    public String getUsedFromSerialNo() {
        return _usedFromSerialNo;
    }

    public void setUsedFromSerialNo(String fromSerialNo) {
        _usedFromSerialNo = fromSerialNo;
    }

    public String getUsedToSerialNo() {
        return _usedToSerialNo;
    }

    public void setUsedToSerialNo(String toSerialNo) {
        _usedToSerialNo = toSerialNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMsisdn() {
        return userMsisdn;
    }

    public void setUserMsisdn(String userMsisdn) {
        this.userMsisdn = userMsisdn;
    }

    public Date getExpiryDate() {
        return _expiryDate;
    }

    public String getRemarksLevel1() {
		return remarksLevel1;
	}

	public void setRemarksLevel1(String remarksLevel1) {
		this.remarksLevel1 = remarksLevel1;
	}

	public String getRemarksLevel2() {
		return remarksLevel2;
	}

	public void setRemarksLevel2(String remarksLevel2) {
		this.remarksLevel2 = remarksLevel2;
	}

	public String getRemarksLevel3() {
		return remarksLevel3;
	}

	public void setRemarksLevel3(String remarksLevel3) {
		this.remarksLevel3 = remarksLevel3;
	}

	public void setExpiryDate(Date date) {
        _expiryDate = date;
    }

	public int getSeq_id() {
		return seq_id;
	}

	public void setSeq_id(int seq_id) {
		this.seq_id = seq_id;
	}

	private String remarksLevel1;
    private String remarksLevel2;
    public String getFirstApprovedOn() {
		return firstApprovedOn;
	}

	public void setFirstApprovedOn(String firstApprovedOn) {
		this.firstApprovedOn = firstApprovedOn;
	}

	public String getSecondApprovedOn() {
		return secondApprovedOn;
	}

	public void setSecondApprovedOn(String secondApprovedOn) {
		this.secondApprovedOn = secondApprovedOn;
	}

	public String getThirdApprovedOn() {
		return thirdApprovedOn;
	}

	public void setThirdApprovedOn(String thirdApprovedOn) {
		this.thirdApprovedOn = thirdApprovedOn;
	}

	public String getFirstApprovedBy() {
		return firstApprovedBy;
	}

	public void setFirstApprovedBy(String firstApprovedBy) {
		this.firstApprovedBy = firstApprovedBy;
	}

	public String getSecondApprovedBy() {
		return secondApprovedBy;
	}

	public void setSecondApprovedBy(String secondApprovedBy) {
		this.secondApprovedBy = secondApprovedBy;
	}

	public String getThirdApprovedBy() {
		return thirdApprovedBy;
	}

	public void setThirdApprovedBy(String thirdApprovedBy) {
		this.thirdApprovedBy = thirdApprovedBy;
	}

	private String remarksLevel3;
    
    private String firstApprovedOn;
    private String secondApprovedOn;
    private String thirdApprovedOn;

    private String firstApprovedBy;
    private String secondApprovedBy;
    private String thirdApprovedBy;
    
    private String _preFromSerialNo;
    private String _preToSerialNo;
    private String _preQuantity;
    private String _preProductId;
    public void setPreFromSerialNo(java.lang.String p_fromSerialNo) {
    	_preFromSerialNo = p_fromSerialNo;
    }
    public java.lang.String getPreFromSerialNo() {
        return _preFromSerialNo;
    }
    public void setPreToSerialNo(java.lang.String p_toSerialNo) {
    	_preToSerialNo = p_toSerialNo;
    }
    public java.lang.String getPreToSerialNo() {
        return _preToSerialNo;
    }
    public void setPreQuantity(String quantity) {
    	_preQuantity = quantity;
    }
    public String getPreQuantity() {
        return _preQuantity;
    }
    public String getPreProductId() {
        return _preProductId;
    }
    public void setPreProductId(String productid) {
        _preProductId = productid;
    }
    
    public long getPreFromSerialNoLong() {
    	if(!BTSLUtil.isNullString(_preFromSerialNo))
    		return Long.parseLong(_preFromSerialNo);
    	else
    		return 0;
    }
    public long getPreToSerialNoLong() {
    	if(!BTSLUtil.isNullString(_preToSerialNo))
    		return Long.parseLong(_preToSerialNo);
    	else
    		return 0;
    }
    public long getPreQuantityLong() {
    	if(!BTSLUtil.isNullString(_preQuantity))
    		return Long.parseLong(_preQuantity);
    	else
    		return 0;
    }
   
    public long getQuantityLong() {
    	if(!BTSLUtil.isNullString(_quantity))
    		return Long.parseLong(_quantity);
    	else
    		return 0;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return _fileName;
    }

    /**
     * @param fileName
     *            The fileName to set.
     */
    public void setFileName(String fileName) {
        _fileName = fileName;
    }
    /**
     * @return Returns the file.
     */
   /* public FormFile getFile() {
        return _file;
    }

    *//**
     * @param file
     *            The file to set.
     *//*
    public void setFile(FormFile file) {
        _file = file;
    }*/

	public String toString() {
		StringBuffer sb = new StringBuffer();
        sb.append(" _batchNo=" + _batchNo).append( ", _oneTimeUsage=" + _oneTimeUsage).append(", _productID=" + _productID);
        sb.append( ", _batchType=" + _batchType).append( ", _batchTypeDesc=" + _batchTypeDesc).append( ", _referenceNo=" + _referenceNo);
        sb.append( ", _referenceType=" + _referenceType).append( ", _noOfVoucher=" + _noOfVoucher).append( ", _expiryPeriod="+_expiryPeriod);
        sb.append( ", _fromSerialNo=" + _fromSerialNo).append( ", _toSerialNo=" + _toSerialNo);
        sb.append( ", _successCount=" + _successCount).append( ", _failCount=" + _failCount).append( ", _locationCode=" + _locationCode);
        sb.append( ", _createdBy=" + _createdBy).append( ", _createdDate=" + _createdDate).append( ", _createdOn=" + _createdOn);
        sb.append( ", _modifiedBy=" + _modifiedBy).append( ", _modifiedDate=" + _modifiedDate).append( ", _modifiedOn=" + _modifiedOn);
        sb.append( ", _status=" + _status).append( ", _statusDesc=" + _statusDesc).append( ", _totalVoucherPerOrder="+_totalVoucherPerOrder);
        sb.append( ", _startSerialNo=" + _startSerialNo).append( ", _productName=" + _productName);
        sb.append( ", _mrp=" + _mrp).append( ", _ItemList=" + _ItemList).append( ", _downloadDate=" + _downloadDate);
        sb.append( ", _downloadCount=" + _downloadCount).append( ", _createdOnStr=" + _createdOnStr).append( ", _process=" + _process);
        sb.append( ", _message=" + _message).append( ", _downloadOnStr=" + _downloadOnStr).append( ", _executeCount=" + _executeCount);
        sb.append( ", _scheduleCount=" + _scheduleCount).append( ", _failureCount=" + _failureCount).append( ", _rcAdminDaysAllowed=");
        sb.append( _rcAdminDaysAllowed).append( ", _denomination=" + _denomination).append( ", _quantity=" + _quantity).append( ", _remarks=");
        sb.append( _remarks).append( ", rowIndex=" + rowIndex).append( ", _productlist=" + _productlist).append( ", _productid=" + _productid);
        sb.append( ", _talktime=" + _talktime).append( ", _validity=" + _validity).append( ", _apprvLvl=" + _apprvLvl);
        sb.append( ", _unUsedVouchers=" + _unUsedVouchers).append( ", _unUsedToserialNO=" + _unUsedToserialNO);
        sb.append( ", _unUsedFromserialNO=" + _unUsedFromserialNO).append( ", _unUsedBatchList=" + _unUsedBatchList);
        sb.append( ", _unUsedBatchExists=" + _unUsedBatchExists).append( ", _usedFromSerialNo=" + _usedFromSerialNo);
        sb.append( ", _usedToSerialNo=" + _usedToSerialNo).append( ", _NetworkCode=" + _NetworkCode);
        sb.append( ", _channelTransferItemsVO=" + _channelTransferItemsVO).append( ", _toUSERID=" + _toUSERID).append( ", userName="+userName);
		sb.append( ", userMsisdn=" + userMsisdn).append( ", _expiryDate=" + _expiryDate).append( ", seq_id=" + seq_id);
		sb.append( ", processScreen=" + processScreen).append( ", _fileName=" + _fileName);
		sb.append( ", _filePresent=" + _filePresent).append( ", extTxnNo=" + extTxnNo).append( ", batchID=" + batchID).append( ", segment="+ segment);
		sb.append( ", _voucherType=" + _voucherType).append( ", remarksLevel1=" + remarksLevel1).append( ", remarksLevel2="+remarksLevel2);
		sb.append( ", remarksLevel3=" + remarksLevel3).append( ", firstApprovedOn=" + firstApprovedOn);
		sb.append( ", secondApprovedOn=" + secondApprovedOn).append( ", thirdApprovedOn=" + thirdApprovedOn);
		sb.append( ", firstApprovedBy=" + firstApprovedBy).append( ", secondApprovedBy=" + secondApprovedBy);
		sb.append( ", thirdApprovedBy=" + thirdApprovedBy).append( ", _preFromSerialNo=" + _preFromSerialNo);
		sb.append( ", _preToSerialNo=" + _preToSerialNo).append( ", _preQuantity=" + _preQuantity).append( ", _preProductId="+_preProductId);
		sb.append( ", _masterBatchId=" + masterBatchNo);
		sb.append( ", transferId=" + transferId);
		sb.append( ", approver1Quantity=" + approver1Quantity).append( ", approver2Quantity=" + approver2Quantity);
		return sb.toString();
	}

	public String getMasterBatchNo() {
		return masterBatchNo;
	}

	public void setMasterBatchNo(String masterBatchNo) {
		this.masterBatchNo = masterBatchNo;
	}
    
	public static VomsBatchVO getInstance(){
		return new VomsBatchVO();
	}
    
}
