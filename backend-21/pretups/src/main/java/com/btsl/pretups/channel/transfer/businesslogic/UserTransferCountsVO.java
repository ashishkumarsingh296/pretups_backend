/**
 * @(#)UserTransferCountsVO.java
 *                               Copyright(c) 2005, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 * 
 *                               <description>
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               avinash.kamthan Aug 3, 2005 Initital Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;

/**
 * @author avinash.kamthan
 */
public class UserTransferCountsVO extends TransferProfileVO implements Serializable {
    private String _userID;
    private Date _outsideLastInTime;
    private Date _outsideLastOutTime;
    private Date _lastInTime;
    private Date _lastOutTime;
    private Date _lastTransferDate;
    private String _lastTransferID;
    private String lastSOSTxnID;
    private String lastSOSTxnStatus;
    private String lastLrStatus;
    public String getLastLrStatus() {
		return lastLrStatus;
	}

	public void setLastLrStatus(String lastLrStatus) {
		this.lastLrStatus = lastLrStatus;
	}

	public String getLastLRTxnID() {
		return lastLRTxnID;
	}

	public void setLastLRTxnID(String lastLRTxnID) {
		this.lastLRTxnID = lastLRTxnID;
	}

	private String lastLRTxnID;

    private long sosAllowedAmount = 0;
    
	public long getSosAllowedAmount() {
		return sosAllowedAmount;
	}

    private String lastSOSProductCode;


	public void setSosAllowedAmount(long sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}

	public String getLastTransferID() {
        return _lastTransferID;
    }

    public void setLastTransferID(String lastTransferID) {
        _lastTransferID = lastTransferID;
    }

    public Date getLastInTime() {
        return _lastInTime;
    }

    public void setLastInTime(Date lastInTime) {
        _lastInTime = lastInTime;
    }

    public Date getLastOutTime() {
        return _lastOutTime;
    }

    public void setLastOutTime(Date lastOutTime) {
        _lastOutTime = lastOutTime;
    }

    public Date getOutsideLastInTime() {
        return _outsideLastInTime;
    }

    public void setOutsideLastInTime(Date outsideLastInTime) {
        _outsideLastInTime = outsideLastInTime;
    }

    public String getUserID() {
        return _userID;
    }

    public void setUserID(String userID) {
        _userID = userID;
    }

    public Date getOutsideLastOutTime() {
        return _outsideLastOutTime;
    }

    public void setOutsideLastOutTime(Date outsideLastOutTime) {
        _outsideLastOutTime = outsideLastOutTime;
    }

    public Date getLastTransferDate() {
        return _lastTransferDate;
    }

    public void setLastTransferDate(Date lastTransferDate) {
        _lastTransferDate = lastTransferDate;
    }
    
    public String getLastSOSTxnID() {
		return lastSOSTxnID;
	}
	public void setLastSOSTxnID(String lastSOSTxn) {
		lastSOSTxnID = lastSOSTxn ;
	}
	
	public String getLastSOSTxnStatus() {
		return lastSOSTxnStatus;
	}
	public void setLastSOSTxnStatus(String lastSOSStatus) {
		lastSOSTxnStatus = lastSOSStatus ;
	}
	
	public String getLastSOSProductCode() {
		return lastSOSProductCode;
	}
	public void setLastSOSProductCode(String lastSOSProduct) {
		lastSOSProductCode = lastSOSProduct ;
	}
	/**
     * Create new object of this class
     * @return UserTransferCountsVO new object of this class
     */
	public static UserTransferCountsVO getInstance() {
		return new UserTransferCountsVO();
	}
	
	

}
