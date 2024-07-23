package com.btsl.pretups.channel.user.businesslogic.wallet;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserProductWalletMappingVO implements Serializable, Comparable<UserProductWalletMappingVO> {

	 @Override
		public String toString() {
			return "UserProductWalletMappingVO [networkCode=" + networkCode + ", networkName=" + networkName
					+ ", productCode=" + productCode + ", productName=" + productName + ", productType=" + productType
					+ ", accountCode=" + accountCode + ", accountName=" + accountName + ", accountPriority="
					+ accountPriority + ", addnlComAlwd=" + addnlComAlwd + ", lmsPoint=" + lmsPoint + ", partialDedAlwd="
					+ partialDedAlwd + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", modifiedBy="
					+ modifiedBy + ", modifiedOn=" + modifiedOn + ", balance=" + balance + ", previousBalance="
					+ previousBalance + ", debitBalance=" + debitBalance + ", creditBalance=" + creditBalance
					+ ", balanceType=" + balanceType + ", penaltyAccountPriority=" + penaltyAccountPriority
					+ ", productNameToShow=" + productNameToShow + ", priorityList=" + priorityList + "]";
		}
	 
	
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String networkCode;
    private String networkName;
    private String productCode;
    private String productName;
    private String productType;
    private String accountCode;
    private String accountName;
    private int accountPriority;
    private String addnlComAlwd;
    private String lmsPoint;
    private String partialDedAlwd;
    private String createdBy;
    private Date createdOn;
    private String modifiedBy;
    private Date modifiedOn;

    private long balance;
    private long previousBalance;
    private long debitBalance;
    private long creditBalance;
    private String balanceType;
	private int penaltyAccountPriority;

    public int getPenaltyAccountPriority() {
		return penaltyAccountPriority;
	}

	public void setPenaltyAccountPriority(int penaltyAccountPriority) {
		this.penaltyAccountPriority = penaltyAccountPriority;
	}

    /*
     * Added to show product name on JSP and to populate priority drop down
     * dynamically
     */
    private String productNameToShow;
    private List<Integer> priorityList = null;

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * @return
     */
    public long getDebitBalance() {
        return debitBalance;
    }

    /**
     * @param debitBalance
     */
    public void setDebitBalance(long debitBalance) {
        this.debitBalance = debitBalance;
    }

    /**
     * @return
     */
    public long getCreditBalance() {
        return creditBalance;
    }

    /**
     * @param creditBalance
     */
    public void setCreditBalance(long creditBalance) {
        this.creditBalance = creditBalance;
    }

    /**
     * @return
     */
    public String getNetworkCode() {
        return networkCode;
    }

    /**
     * @param networkCode
     */
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    /**
     * @return
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * @param networkName
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * @return
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return
     */
    public String getAccountCode() {
        return accountCode;
    }

    /**
     * @param accountCode
     */
    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    /**
     * @return
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @param accountName
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * @return
     */
    public int getAccountPriority() {
        return accountPriority;
    }

    /**
     * @param accountPriority
     */
    public void setAccountPriority(int accountPriority) {
        this.accountPriority = accountPriority;
    }

    /**
     * @return
     */
    public String getAddnlComAlwd() {
        return addnlComAlwd;
    }

    /**
     * @param addnlComAlwd
     */
    public void setAddnlComAlwd(String addnlComAlwd) {
        this.addnlComAlwd = addnlComAlwd;
    }

    /**
     * @return
     */
    public String getLmsPoint() {
        return lmsPoint;
    }

    /**
     * @param lmsPoint
     */
    public void setLmsPoint(String lmsPoint) {
        this.lmsPoint = lmsPoint;
    }

    /**
     * @return
     */
    public String getPartialDedAlwd() {
        return partialDedAlwd;
    }

    /**
     * @param partialDedAlwd
     */
    public void setPartialDedAlwd(String partialDedAlwd) {
        this.partialDedAlwd = partialDedAlwd;
    }

    /**
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return
     */
    public Date getCreatedOn() {
        return new Date(createdOn.getTime());
    }

    /**
     * @param createdOn
     */
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return
     */
    public Date getModifiedOn() {
        return new Date(modifiedOn.getTime());
    }

    /**
     * @param modifiedOn
     */
    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    /**
     * @return
     */
    public List<Integer> getPriorityList() {
        return priorityList;
    }

    /**
     * @param priorityList
     */
    public void setPriorityList(List<Integer> priorityList) {
        this.priorityList = priorityList;
    }

    /**
     * @return
     */
    public String getProductNameToShow() {
        return productNameToShow;
    }

    /**
     * @param productNameToShow
     */
    public void setProductNameToShow(String productNameToShow) {
        this.productNameToShow = productNameToShow;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(UserProductWalletMappingVO object) {
        if (this.getAccountPriority() > object.getAccountPriority()) {
            return 1;
        } else if (this.getAccountPriority() < object.getAccountPriority()) {
            return -1;
        } else {
            return 0;
        }

    }

    @Override
    public boolean equals(Object arg0) {
        return super.equals(arg0);
    }

    @Override
	public int hashCode(){
    	return super.hashCode();
    }
    
    /**
     * @return
     */
    public long getBalance() {
        return balance;
    }

	/**
     * @param balance
     */
    public void setBalance(long balance) {
        this.balance = balance;
    }

    /**
     * @return
     */
    public long getPreviousBalance() {
        return previousBalance;
    }

    /**
     * @param previousBalance
     */
    public void setPreviousBalance(long previousBalance) {
        this.previousBalance = previousBalance;
    }

    /**
     * @return
     */
    public String getProductType() {
        return productType;
    }

    /**
     * @param productType
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

}
