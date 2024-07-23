package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class ChannelTransferPaymentVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(ChannelTransferPaymentVO.class.getName());
	
    private String _transferId;
    private Date _transferDate;
    private Date _transferDateTime;
    private String _paymentId;
    private String _paymentStatus;
    private long _paymentAmount;
    
    public String getTransferId() {
		return _transferId;
	}
	public void setTransferId(String _transferId) {
		this._transferId = _transferId;
	}
	public Date getTransferDate() {
		return _transferDate;
	}
	public void setTransferDate(Date _transferDate) {
		this._transferDate = _transferDate;
	}
	public Date getTransferDateTime() {
		return _transferDateTime;
	}
	public void setTransferDateTime(Date _transferDateTime) {
		this._transferDateTime = _transferDateTime;
	}
	public String getPaymentId() {
		return _paymentId;
	}
	public void setPaymentId(String paymentId) {
		this._paymentId = paymentId;
	}
	public long getPaymentAmount() {
		return _paymentAmount;
	}
	public void setPaymentAmount(long _paymentAmount) {
		this._paymentAmount = _paymentAmount;
	}
	public String getPaymentStatus() {
		return _paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this._paymentStatus = paymentStatus;
	}
	
	@Override
	public String toString() {
        final StringBuffer sbf = new StringBuffer(" transferID : " + _transferId);
        sbf.append(", transferDate : " + _transferDate);
        sbf.append(", transferDateTime : " + _transferDateTime);
        sbf.append(", paymentId : " + _paymentId);
        sbf.append(", paymentStatus : " + _paymentStatus);
        sbf.append(", paymentAmount : " + _paymentAmount);
        return sbf.toString();
	}
}
