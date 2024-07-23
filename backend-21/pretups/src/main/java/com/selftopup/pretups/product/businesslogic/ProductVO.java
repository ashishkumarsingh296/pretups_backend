/**
 * @(#)ProductsVO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     avinash.kamthan Aug 4, 2005 Initital Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 * 
 */

package com.selftopup.pretups.product.businesslogic;

import java.io.Serializable;

import com.selftopup.pretups.util.PretupsBL;

/**
 * @author avinash.kamthan
 * 
 */
public class ProductVO implements Serializable {
    private String _productType;
    private String _productCode;
    private String _moduleCode;
    private String _productName;
    private String _shortName;
    private long _productShortCode;
    private String _productCategory;
    private String _erpProductCode;
    private String _status;
    private String _productStatusName;
    private long _unitValue;
    private long _networkStock;
    private long _minTransferValue;
    private long _maxTransferValue;
    private long _transferMultipleOf;
    private String _discountType;
    private double _discountRate;
    private long _discountValue;
    private long _balance;
    private String _productUsageName; // usege for displaying purpose
    private String _taxOnChannelTransfer;// to check whether tax are applicable
                                         // on channel transfer defined in
                                         // product commission rpofile
    private String _taxOnFOCTransfer;// to check whether tax are applicable on
                                     // FOC transfer defined in product
                                     // commission rpofile

    private String _productUsage;// from network product mapping. to check
                                 // whehre this product is for distribution or
                                 // for consumprion

    private String _requestedQuantity;
    // added by nilesh
    private long _networkFOCStock;
    private long _networkINCStock;

    // Added by Amit Raheja for reverse transactions
    private String _reversalRequestedQuantity;

    public String getBalanceAsString() {
        return PretupsBL.getDisplayAmount(_balance);
    }

    /**
     * @return Returns the productUsageName.
     */
    public String getProductUsageName() {
        return _productUsageName;
    }

    /**
     * @param productUsageName
     *            The productUsageName to set.
     */
    public void setProductUsageName(String productUsageName) {
        _productUsageName = productUsageName;
    }

    public String getProductCost() {
        if (_requestedQuantity != null)
            return PretupsBL.getDisplayAmount((long) (Double.parseDouble(_requestedQuantity) * _unitValue));
        else
            return "";
    }

    /**
     * @return Returns the productStatusName.
     */
    public String getProductStatusName() {
        return _productStatusName;
    }

    /**
     * @param productStatusName
     *            The productStatusName to set.
     */
    public void setProductStatusName(String productStatusName) {
        _productStatusName = productStatusName;
    }

    public long getNetworkStock() {
        return _networkStock;
    }

    public void setNetworkStock(long networkStock) {
        _networkStock = networkStock;
    }

    public String getErpProductCode() {
        return _erpProductCode;
    }

    public void setErpProductCode(String erpProductCode) {
        _erpProductCode = erpProductCode;
    }

    public String getModuleCode() {
        return _moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        _moduleCode = moduleCode;
    }

    public String getProductCategory() {
        return _productCategory;
    }

    public void setProductCategory(String productCategory) {
        _productCategory = productCategory;
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String productName) {
        _productName = productName;
    }

    public long getProductShortCode() {
        return _productShortCode;
    }

    public void setProductShortCode(long productShortCode) {
        _productShortCode = productShortCode;
    }

    public String getProductType() {
        return _productType;
    }

    public void setProductType(String productType) {
        _productType = productType;
    }

    public String getShortName() {
        return _shortName;
    }

    public void setShortName(String shortName) {
        _shortName = shortName;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public long getUnitValue() {
        return _unitValue;
    }

    public void setUnitValue(long unitValue) {
        _unitValue = unitValue;
    }

    public double getDiscountRate() {
        return _discountRate;
    }

    public void setDiscountRate(double discountRate) {
        _discountRate = discountRate;
    }

    public String getDiscountType() {
        return _discountType;
    }

    public void setDiscountType(String discountType) {
        _discountType = discountType;
    }

    public long getMaxTransferValue() {
        return _maxTransferValue;
    }

    public void setMaxTransferValue(long maxTransferValue) {
        _maxTransferValue = maxTransferValue;
    }

    public long getMinTransferValue() {
        return _minTransferValue;
    }

    public void setMinTransferValue(long minTransferValue) {
        _minTransferValue = minTransferValue;
    }

    public long getTransferMultipleOf() {
        return _transferMultipleOf;
    }

    public void setTransferMultipleOf(long transferMultipleOff) {
        _transferMultipleOf = transferMultipleOff;
    }

    public String getMaxTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_maxTransferValue);
    }

    public String getMinTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_minTransferValue);
    }

    public String getNetworkStockAsString() {
        return PretupsBL.getDisplayAmount(_networkStock);
    }

    // public void setNetworkStockAsString(String networkStockAsString)
    // {
    // _networkStockAsString = networkStockAsString;
    // }
    public String getRequestedQuantity() {
        return _requestedQuantity;
    }

    public void setRequestedQuantity(String requstedQty) {
        _requestedQuantity = requstedQty;
    }

    public String getUnitValueAsString() {
        return PretupsBL.getDisplayAmount(_unitValue);
    }

    public long getDiscountValue() {
        return _discountValue;
    }

    public void setDiscountValue(long discountValue) {
        _discountValue = discountValue;
    }

    public long getBalance() {
        return _balance;
    }

    public void setBalance(long balance) {
        _balance = balance;
    }

    public String getTaxOnChannelTransfer() {
        return _taxOnChannelTransfer;
    }

    public void setTaxOnChannelTransfer(String taxOnChannelTransfer) {
        _taxOnChannelTransfer = taxOnChannelTransfer;
    }

    public String getProductUsage() {
        return _productUsage;
    }

    public void setProductUsage(String productUsage) {
        _productUsage = productUsage;
    }

    public String getTaxOnFOCTransfer() {
        return _taxOnFOCTransfer;
    }

    public void setTaxOnFOCTransfer(String taxOnFOCTransfer) {
        _taxOnFOCTransfer = taxOnFOCTransfer;
    }

    // added by nilesh
    public long getNetworkFOCStock() {
        return _networkFOCStock;
    }

    public void setNetworkFOCStock(long networkFOCStock) {
        _networkFOCStock = networkFOCStock;
    }

    public long getNetworkINCStock() {
        return _networkINCStock;
    }

    public void setNetworkINCStock(long networkINCStock) {
        _networkINCStock = networkINCStock;
    }

    public String getNetworkFOCStockAsString() {
        return PretupsBL.getDisplayAmount(_networkFOCStock);
    }

    public String getReversalRequestedQuantity() {
        return _reversalRequestedQuantity;
    }

    public void setReversalRequestedQuantity(String requestedQuantity) {
        _reversalRequestedQuantity = requestedQuantity;
    }
}