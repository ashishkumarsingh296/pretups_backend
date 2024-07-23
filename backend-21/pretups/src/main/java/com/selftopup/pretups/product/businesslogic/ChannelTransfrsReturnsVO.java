package com.selftopup.pretups.product.businesslogic;

import java.io.Serializable;

/**
 * @(#)ChannelTransfrsReturnsVO.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   <description>
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   manoj kumar sep23, 2005 Initital Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 * 
 */

public class ChannelTransfrsReturnsVO implements Serializable {
    private String _productType;
    private String _productCode;
    private long _productShortCode;
    private String _moduleCode;
    private String _productName;
    private String _shortName;
    private long _transfes;
    private long _returns;
    private String _serviceName;

    /**
     * @return Returns the productShortCode.
     */
    public long getProductShortCode() {
        return _productShortCode;
    }

    /**
     * @param productShortCode
     *            The productShortCode to set.
     */
    public void setProductShortCode(long productShortCode) {
        _productShortCode = productShortCode;
    }

    /**
     * @return Returns the serviceName.
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * @param serviceName
     *            The serviceName to set.
     */
    public void setServiceName(String serviceName) {
        _serviceName = serviceName;
    }

    /**
     * @return Returns the moduleCode.
     */
    public String getModuleCode() {
        return _moduleCode;
    }

    /**
     * @param moduleCode
     *            The moduleCode to set.
     */
    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
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
        _productCode = productCode;
    }

    /**
     * @return Returns the productName.
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * @param productName
     *            The productName to set.
     */
    public void setProductName(String productName) {
        _productName = productName;
    }

    /**
     * @return Returns the productType.
     */
    public String getProductType() {
        return _productType;
    }

    /**
     * @param productType
     *            The productType to set.
     */
    public void setProductType(String productType) {
        _productType = productType;
    }

    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return _shortName;
    }

    /**
     * @param shortName
     *            The shortName to set.
     */
    public void setShortName(String shortName) {
        _shortName = shortName;
    }

    /**
     * @return Returns the returns.
     */
    public long getReturns() {
        return _returns;
    }

    /**
     * @param returns
     *            The returns to set.
     */
    public void setReturns(long returns) {
        _returns = returns;
    }

    /**
     * @return Returns the transfes.
     */
    public long getTransfes() {
        return _transfes;
    }

    /**
     * @param transfes
     *            The transfes to set.
     */
    public void setTransfes(long transfes) {
        _transfes = transfes;
    }
}
