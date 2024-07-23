/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Entity of CATEGORIES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "USER_BALANCES")
public class UserBalances implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "NETWORK_CODE")
    private String networkCode;

    @Column(name = "NETWORK_CODE_FOR")
    private String networkCodeFor;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "BALANCE")
    private long balance;

    @Column(name = "PREV_BALANCE")
    private long prevBalance;

    @Column(name = "LAST_TRANSFER_TYPE")
    private String lastTransferType;

    @Column(name = "LAST_TRANSFER_NO")
    private String lastTransferNo;

    @Column(name = "LAST_TRANSFER_ON")
    private Date lastTransferOn;

    @Column(name = "DAILY_BALANCE_UPDATED_ON")
    private Date dailyBalanceUpdatedOn;

    @Column(name = "BALANCE_TYPE")
    private String balanceType;

    @Column(name = "Transfer")
    private Date transfer;



    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserBalances other = (UserBalances) obj;
        return Objects.equals(this.getUserId(), other.getUserId());

    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getNetworkCodeFor() {
		return networkCodeFor;
	}

	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	public long getPrevBalance() {
		return prevBalance;
	}

	public void setPrevBalance(long prevBalance) {
		this.prevBalance = prevBalance;
	}

	public String getLastTransferType() {
		return lastTransferType;
	}

	public void setLastTransferType(String lastTransferType) {
		this.lastTransferType = lastTransferType;
	}

	public String getLastTransferNo() {
		return lastTransferNo;
	}

	public void setLastTransferNo(String lastTransferNo) {
		this.lastTransferNo = lastTransferNo;
	}

	public Date getLastTransferOn() {
		return lastTransferOn;
	}

	public void setLastTransferOn(Date lastTransferOn) {
		this.lastTransferOn = lastTransferOn;
	}

	public Date getDailyBalanceUpdatedOn() {
		return dailyBalanceUpdatedOn;
	}

	public void setDailyBalanceUpdatedOn(Date dailyBalanceUpdatedOn) {
		this.dailyBalanceUpdatedOn = dailyBalanceUpdatedOn;
	}

	public String getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(String balanceType) {
		this.balanceType = balanceType;
	}

	public Date getTransfer() {
		return transfer;
	}

	public void setTransfer(Date transfer) {
		this.transfer = transfer;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
