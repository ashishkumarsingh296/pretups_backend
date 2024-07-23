/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */

package com.btsl.pretups.channel.query.businesslogic;

import java.io.Serializable;

public class C2sBalanceQueryVO implements Serializable {

    private long balance;
    private String productName;
    private String productType;
    public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	private String productShortCode;
    private String grphDomainName;
    private String domainName;
    private String categoryName;

    private String userName;
    private String address;
    private String primaryNumber;
    private String msisdn;
    private String productCode;
    private String unitValue;

    public String getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(String unitValue) {
		this.unitValue = unitValue;
	}

	/**
     * To get the value of msisdn field
     * 
     * @return msisdn.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * To set the value of msisdn field
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * To get the value of address field
     * 
     * @return address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * To set the value of address field
     */
    public void setAddress(String address) {
    	 this.address = address;
    }

    /**
     * To get the value of primaryNumber field
     * 
     * @return primaryNumber.
     */
    public String getPrimaryNumber() {
        return primaryNumber;
    }

    /**
     * To set the value of primaryNumber field
     */
    public void setPrimaryNumber(String primaryNumber) {
    	 this.primaryNumber = primaryNumber;
    }

    /**
     * To get the value of userName field
     * 
     * @return userName.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * To set the value of userName field
     */
    public void setUserName(String userName) {
    	 this.userName = userName;
    }

    /**
     * To get the value of categoryName field
     * 
     * @return categoryName.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * To set the value of categoryName field
     */
    public void setCategoryName(String categoryName) {
    	 this.categoryName = categoryName;
    }

    /**
     * To get the value of domainName field
     * 
     * @return domainName.
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * To set the value of domainName field
     */
    public void setDomainName(String domainName) {
    	 this.domainName = domainName;
    }

    /**
     * To get the value of grphDomainName field
     * 
     * @return grphDomainName.
     */
    public String getGrphDomainName() {
        return grphDomainName;
    }

    /**
     * To set the value of grphDomainName field
     */
    public void setGrphDomainName(String grphDomainName) {
    	 this.grphDomainName = grphDomainName;
    }

    /**
     * 
     */
    public C2sBalanceQueryVO() {
        super();
    }

    /**
     * To get the value of balance field
     * 
     * @return balance.
     */
    public long getBalance() {
        return balance;
    }

    /**
     * To set the value of balance field
     */
    public void setBalance(long balance) {
    	 this.balance = balance;
    }

    /**
     * To get the value of productName field
     * 
     * @return productName.
     */
    public String getProductName() {
        return productName;
    }

    /**
     * To set the value of productName field
     */
    public void setProductName(String productName) {
    	 this.productName = productName;
    }

    /**
     * To get the value of productShortCode field
     * 
     * @return productShortCode.
     */
    public String getProductShortCode() {
        return productShortCode;
    }

    /**
     * To set the value of productShortCode field
     */
    public void setProductShortCode(String productShortCode) {
    	 this.productShortCode = productShortCode;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
    	 this.productCode = productCode;
    }
}
