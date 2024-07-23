package com.btsl.voms.vomsreport.businesslogic;

/**
 * @(#)VomsVoucherResendPinVO.java
 *                                 Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 * 
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Siddhartha Srivastava 27/09/2006 Initial
 *                                 Creation
 *                                 Shishupal Singh 02/05/2007 Modification
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 This class is used to transfer the details
 *                                 entered in the first jsp to the DAO
 * 
 */
import java.io.Serializable;

public class VomsVoucherResendPinVO implements Serializable {
    private String serviceType; // service type of the transaction
    private String transferID; // transfer id of the transaction
    private String transferDate; // date of transaction
    private String customerMSISDN; // msisdn of the customer/end user
    private String retailerMSISDN;// msisdn of the retailer
    
	private String _lineNumber;
	private String _errorCode;
	private String _serialNo;

    /**
     * This method return the string representation of the object
     */
	@Override
    public String toString() {
    	StringBuilder sbf = new StringBuilder();
    	 sbf.append(" customerMSISDN ").append(customerMSISDN);
    	 sbf.append(" retailerMSISDN ").append(retailerMSISDN);
    	 sbf.append(" transferDate ").append(transferDate);
    	 sbf.append(" transferID ").append(transferID);
    	 sbf.append(" lineNumber").append(_lineNumber);
    	 sbf.append(" errorCode").append(_errorCode);
        return sbf.toString();
       
    }

    public String getCustomerMSISDN() {
        return customerMSISDN;
    }

    public void setCustomerMSISDN(String customerMSISDN) {
        this.customerMSISDN = customerMSISDN;
    }

    public String getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(String transferDate) {
        this.transferDate = transferDate;
    }

    public String getTransferID() {
        return transferID;
    }

    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }

    public String getRetailerMSISDN() {
        return retailerMSISDN;
    }

    public void setRetailerMSISDN(String receiverMSISDN) {
        this.retailerMSISDN = receiverMSISDN;
    }

    /**
     * @return Returns the serviceType.
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType
     *            The serviceType to set.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
		public String getLineNumber() {
		return _lineNumber;
	}

	public void setLineNumber(String number) {
		_lineNumber = number;
	}

	public String getErrorCode() {
		return _errorCode;
	}

	public void setErrorCode(String code) {
		_errorCode = code;
	}

	public String getSerialNo() {
		return _serialNo;
	}

	public void setSerialNo(String no) {
		_serialNo = no;
	}
	
}
