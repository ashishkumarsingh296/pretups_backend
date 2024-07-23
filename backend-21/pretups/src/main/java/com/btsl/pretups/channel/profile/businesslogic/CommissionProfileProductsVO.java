/**
 * @(#)CommissionProfileProductsVO.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 * 
 *                                      <description>
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      avinash.kamthan Aug 3, 2005 Initital
 *                                      Creation
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 * 
 */

package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author avinash.kamthan
 */
public class CommissionProfileProductsVO implements Serializable {
    private String _commProfileProductID;
    private String _commProfileSetID;
    private String _version;
    private long _minTransferValue;
    private long _maxTransferValue;
    private long _transferMultipleOff;
    private String _taxOnChannelTransfer;
    private String _taxOnFOCApplicable;
    private String _productCode;
    private String _productCodeDesc;
    private String _discountType;
    private double _discountRate;
    private double _transferMultipleOffInDouble;
    private String paymentMode;
    private String transactionType;
    public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	private String transactionTypeDesc;
    public String getTransactionTypeDesc() {
		return transactionTypeDesc;
	}
	public void setTransactionTypeDesc(String transactionTypeDesc) {
		this.transactionTypeDesc = transactionTypeDesc;
	}
	
	private String paymentModeDesc;
    public String getPaymentModeDesc() {
		return paymentModeDesc;
	}
	public void setPaymentModeDesc(String paymentModeDesc) {
		this.paymentModeDesc = paymentModeDesc;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	private Log _log = LogFactory.getLog(CommissionProfileProductsVO.class.getName());

    public CommissionProfileProductsVO() {
    };

    public CommissionProfileProductsVO(
                    CommissionProfileProductsVO commissionProfileProductsVO) {
        this._commProfileProductID = commissionProfileProductsVO._commProfileProductID;
        this._commProfileSetID = commissionProfileProductsVO._commProfileSetID;
        this._version = commissionProfileProductsVO._version;
        this._minTransferValue = commissionProfileProductsVO._minTransferValue;
        this._maxTransferValue = commissionProfileProductsVO._maxTransferValue;
        this._transferMultipleOff = commissionProfileProductsVO._transferMultipleOff;
        this._taxOnChannelTransfer = commissionProfileProductsVO._taxOnChannelTransfer;
        this._taxOnFOCApplicable = commissionProfileProductsVO._taxOnFOCApplicable;
        this._productCode = commissionProfileProductsVO._productCode;
        this._productCodeDesc = commissionProfileProductsVO._productCodeDesc;
        this._discountType = commissionProfileProductsVO._discountType;
        this._discountRate = commissionProfileProductsVO._discountRate;
        this._transferMultipleOffInDouble = commissionProfileProductsVO._transferMultipleOffInDouble;
        this.paymentMode = commissionProfileProductsVO.paymentMode;
        this.paymentModeDesc = commissionProfileProductsVO.paymentModeDesc;
        this.transactionType = commissionProfileProductsVO.transactionType;
        this.transactionTypeDesc = commissionProfileProductsVO.transactionTypeDesc;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer("CommissionProfileProductsVO Data ");

        sb.append("commProfileProductID=" + _commProfileProductID + ",");
        sb.append("commProfileSetID=" + _commProfileSetID + ",");
        sb.append("version=" + _version + ",");
        sb.append("minTransferValue=" + _minTransferValue + ",");
        sb.append("maxTransferValue=" + _maxTransferValue + ",");
        sb.append("transferMultipleOff=" + _transferMultipleOff + ",");
        sb.append("taxOnFOCApplicable=" + _taxOnFOCApplicable + ",");
        sb.append("maxTransferValue=" + _maxTransferValue + ",");
        sb.append("productCode=" + _productCode + ",");
        sb.append("productCodeDesc=" + _productCodeDesc + ",");
        sb.append("discountType=" + _discountType + ",");
        sb.append("_discountRate=" + _discountRate + ",");
        sb.append("paymentMode=" + paymentMode + ",");
        sb.append("paymentModeDesc=" + paymentModeDesc + ",");
        sb.append("transactionType=" + transactionType + ",");
        sb.append("transactionTypeDesc=" + transactionTypeDesc );

        return sb.toString();
    }

    public String getCommProfileProductID() {
        return _commProfileProductID;
    }

    public void setCommProfileProductID(String commProfileProductID) {
        _commProfileProductID = commProfileProductID;
    }

    public double getDiscountRate() {
        return _discountRate;
    }

    public void setDiscountRate(double discountRate) {
        _discountRate = discountRate;
    }

    public String getDiscountRateAsString() {
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_discountType)) {
            return PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(_discountRate));
        } else {
            return String.valueOf(_discountRate);
        }

    }

    public void setDiscountRateAsString(double discountRate) {
        final String METHOD_NAME = "setDiscountRateAsString";
        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(_discountType)) {
            try {
                _discountRate = PretupsBL.getSystemAmount(discountRate);
            } catch (BTSLBaseException e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        } else {
            _discountRate = discountRate;
        }
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

    public String getMaxTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_maxTransferValue);
    }

    public void setMaxTransferValueAsString(String maxTransferValue) {
        final String METHOD_NAME = "setMaxTransferValueAsString";
        try {
            _maxTransferValue = PretupsBL.getSystemAmount(maxTransferValue);
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public long getMinTransferValue() {
        return _minTransferValue;
    }

    public void setMinTransferValue(long minTransferValue) {
        _minTransferValue = minTransferValue;
    }

    public String getMinTransferValueAsString() {
        return PretupsBL.getDisplayAmount(_minTransferValue);
    }

    public void setMinTransferValueAsString(String minTransferValue) {
        final String METHOD_NAME = "setMinTransferValueAsString";
        try {
            _minTransferValue = PretupsBL.getSystemAmount(minTransferValue);
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String productCode) {
        _productCode = productCode;
    }

    public String getTaxOnChannelTransfer() {
        return _taxOnChannelTransfer;
    }

    public void setTaxOnChannelTransfer(String taxOnChannelTransfer) {
        _taxOnChannelTransfer = taxOnChannelTransfer;
    }

    public long getTransferMultipleOff() {
        return _transferMultipleOff;
    }

    public void setTransferMultipleOff(long transferMultipleOff) {
        _transferMultipleOff = transferMultipleOff;
    }

    public String getTransferMultipleOffAsString() {
        return PretupsBL.getDisplayAmount(_transferMultipleOff);
    }

    public void setTransferMultipleOffAsString(String transferMultipleOff) {
        final String METHOD_NAME = "setTransferMultipleOffAsString";
        try {
            _transferMultipleOff = PretupsBL.getSystemAmount(transferMultipleOff);
        } catch (BTSLBaseException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * @return Returns the productCodeDesc.
     */
    public String getProductCodeDesc() {
        return _productCodeDesc;
    }

    /**
     * @param productCodeDesc
     *            The productCodeDesc to set.
     */
    public void setProductCodeDesc(String productCodeDesc) {
        _productCodeDesc = productCodeDesc;
    }

    /**
     * @return Returns the commProfileSetID.
     */
    public String getCommProfileSetID() {
        return _commProfileSetID;
    }

    /**
     * @param commProfileSetID
     *            The commProfileSetID to set.
     */
    public void setCommProfileSetID(String commProfileSetID) {
        _commProfileSetID = commProfileSetID;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return _version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion(String version) {
        _version = version;
    }

    /**
     * @return Returns the taxOnFOCApplicable.
     */
    public String getTaxOnFOCApplicable() {
        return _taxOnFOCApplicable;
    }

    /**
     * @param taxOnFOCApplicable
     *            The taxOnFOCApplicable to set.
     */
    public void setTaxOnFOCApplicable(String taxOnFOCApplicable) {
        _taxOnFOCApplicable = taxOnFOCApplicable;
    }
    
	/**
	 * @return the transferMultipleOffInDouble
	 */
	public double getTransferMultipleOffInDouble() {
		return _transferMultipleOffInDouble;
	}

	/**
	 * @param transferMultipleOffInDouble the transferMultipleOffInDouble to set
	 */
	public void setTransferMultipleOffInDouble(double transferMultipleOffInDouble) {
		_transferMultipleOffInDouble = transferMultipleOffInDouble;
	}
    
}
