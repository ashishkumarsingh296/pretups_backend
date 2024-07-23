/**
 * @(#)ChannelTransferItemsVO.java
 *                                 Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                 All Rights Reserved
 * 
 *                                 <description>
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 avinash.kamthan Aug 3, 2005 Initital Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 * 
 */

package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;
import java.util.Date;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.util.PretupsBL;

/**
 * @author avinash.kamthan
 * 
 */
public class ChannelTransferItemsVO extends NetworkProductVO implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	//public static final Log logger = LogFactory.getLog(ChannelTransferItemsVO.class.getName());
	private int _serialNum;
    private String _transferID;
    private long _requiredQuantity;
    private long _approvedQuantity;
    private String _commProfileDetailID;
    private String _commProfileProductID;
    private String commType;
    private double _commRate;
    private long _commValue;
    private String _tax1Type;
    private double _tax1Rate;
    private long _tax1Value;
    private String _tax2Type;
    private double _tax2Rate;
    private long _tax2Value;
    private String _tax3Type;
    private double _tax3Rate;
    private long _tax3Value;
    private long _productTotalMRP;
    private long _payableAmount;
    private long _netPayableAmount;
    private long _senderPreviousStock;
    private long _receiverPreviousStock;
  
    private long _afterTransSenderPreviousStock;
    private long _afterTransReceiverPreviousStock;

    private long _previousBalance;

    private boolean _isSlabDefine;

    private String _productMrpStr;
    private String _taxOnC2CTransfer;
    // For mali- +ve commision apply
    private long _commQuantity = 0;
    private long _senderDebitQty = 0;
    private long _receiverCreditQty = 0;
    // afeter debiting commision set the network stock
    private long _afterTransCommisionSenderPreviousStock = 0;

    private String _firstApprovedQuantity;
    private String _secondApprovedQuantity;
    private String _thirdApprovedQuantity;
    private long initialRequestedQuantity;
    private String initialRequestedQuantityStr;
    private long productFirstApprovedQty;
	private long productSecondApprovedQty;
    private long productThirdApprovedQty;
    
    public String getProductFirstApprovedQty() {
		return PretupsBL.getDisplayAmount(productFirstApprovedQty);
	}

	public void setProductFirstApprovedQty(long productFirstApprovedQty) {
		this.productFirstApprovedQty = productFirstApprovedQty;
	}

	public String getProductSecondApprovedQty() {
		return PretupsBL.getDisplayAmount(productSecondApprovedQty);
	}

	public void setProductSecondApprovedQty(long productSecondApprovedQty) {
		this.productSecondApprovedQty = productSecondApprovedQty;
	}

	public String getProductThirdApprovedQty() {
		return PretupsBL.getDisplayAmount(productThirdApprovedQty);
	}

	public void setProductThirdApprovedQty(long productThirdApprovedQty) {
		this.productThirdApprovedQty = productThirdApprovedQty;
	}

    // added by AshishT for Mobinil5.7 BUG Fix
    private long _senderPostStock;
    private long _receiverPostStock;

    // added by Amit Raheja for reverse transactions
    private long _senderBalance;
    // added by gaurav pandey
    private String _cellId;

    private long _networkStockAfterReversal = 0;
    private long _reversalQty = 0;
    private String _loginId = null;
    private String _msisdn = null;
    private String _userCategory = null;
    private String _gradeName;
    private String _paymentType;
    private String _initiatorRemarks = null;
    private String _extTxnNo = null;
    private java.util.Date _extTxnDate;
    private String _externalCode = null;
    private String _commissionProfileVer = null;
    private String _commissionProfileSetId = null;

    private long _bonusBalance = 0;
    private String _balanceType = null;
    private long _previousBonusBalance;
    private long _afterTransSenderPreviousBonusStock;
    // commission profile cache
    private long _startRange = 0;
    private long _endRange = 0;

    private long _bonusDebtQty = 0;
    private long _mainDebitQty = 0;
    /** START:Birendra:28JAN2015 */
    private String userWallet;
    
    private long totalReceiverBalance;
    private long totalSenderBalance;
    
    private Date otfApplicableFrom;
	private Date otfApplicableTo;
    private String otfTimeSlab;
    private String otfTypePctOrAMt;
    private double otfRate;
    private long otfAmount;
    private boolean otfCountsUpdated = false;
    private boolean reversalRequest;
    private boolean targetAchieved;
    private String otfApplicable = "N";
    private UserOTFCountsVO userOTFCountsVO=null;
    
	public UserOTFCountsVO getUserOTFCountsVO() {
		return userOTFCountsVO;
	}


	public void setUserOTFCountsVO(UserOTFCountsVO userOTFCountsVO) {
		this.userOTFCountsVO = userOTFCountsVO;
	}

	private String _othCommType;
	private double _othCommRate;
	private long _othCommValue;
	private String _othCommProfType;
	private String _othCommProfValue;
	private String _othCommSetId;
   	private boolean _isOthSlabDefine;
   	private long payableAmountApproval;
   	private long netPayableAmountApproval;
   	private long voucherQuantity;
   	private String bundleID; 

	public long getCommissionValuePosi() {
		return commissionValuePosi;
	}


	public void setCommissionValuePosi(long commissionValuePosi) {
		this.commissionValuePosi = commissionValuePosi;
	}

	 public String getCommissionValuePosiAsString() {
	        return PretupsBL.getDisplayAmount(commissionValuePosi);
	    }

	private long commissionValuePosi;
    public long getVoucherQuantity() {
		return voucherQuantity;
	}


	public void setVoucherQuantity(long voucherQuantity) {
		this.voucherQuantity = voucherQuantity;
	}


	public long getPayableAmountApproval() {
		return payableAmountApproval;
	}


	public void setPayableAmountApproval(long payableAmountApproval) {
		this.payableAmountApproval = payableAmountApproval;
	}


	public long getNetPayableAmountApproval() {
		return netPayableAmountApproval;
	}


	public void setNetPayableAmountApproval(long netPayableAmountApproval) {
		this.netPayableAmountApproval = netPayableAmountApproval;
	}
	
    
    
    @Override
	public String toString() {
		return "ChannelTransferItemsVO [_serialNum=" + _serialNum
				+ ", _transferID=" + _transferID + ", _requiredQuantity="
				+ _requiredQuantity + ", _approvedQuantity="
				+ _approvedQuantity + ", _commProfileDetailID="
				+ _commProfileDetailID + ", _commProfileProductID="
				+ _commProfileProductID + ", _commType=" + commType
				+ ", _commRate=" + _commRate + ", _commValue=" + _commValue
				+ ", _tax1Type=" + _tax1Type + ", _tax1Rate=" + _tax1Rate
				+ ", _tax1Value=" + _tax1Value + ", _tax2Type=" + _tax2Type
				+ ", _tax2Rate=" + _tax2Rate + ", _tax2Value=" + _tax2Value
				+ ", _tax3Type=" + _tax3Type + ", _tax3Rate=" + _tax3Rate
				+ ", _tax3Value=" + _tax3Value + ", _productTotalMRP="
				+ _productTotalMRP + ", _payableAmount=" + _payableAmount
				+ ", _netPayableAmount=" + _netPayableAmount
				+ ", _senderPreviousStock=" + _senderPreviousStock
				+ ", _receiverPreviousStock=" + _receiverPreviousStock
				+ ", _afterTransSenderPreviousStock="
				+ _afterTransSenderPreviousStock
				+ ", _afterTransReceiverPreviousStock="
				+ _afterTransReceiverPreviousStock + ", _previousBalance="
				+ _previousBalance + ", _isSlabDefine=" + _isSlabDefine
				+ ", _productMrpStr=" + _productMrpStr + ", _taxOnC2CTransfer="
				+ _taxOnC2CTransfer + ", _commQuantity=" + _commQuantity
				+ ", _senderDebitQty=" + _senderDebitQty
				+ ", _receiverCreditQty=" + _receiverCreditQty
				+ ", _afterTransCommisionSenderPreviousStock="
				+ _afterTransCommisionSenderPreviousStock
				+ ", _firstApprovedQuantity=" + _firstApprovedQuantity
				+ ", _secondApprovedQuantity=" + _secondApprovedQuantity
				+ ", _thirdApprovedQuantity=" + _thirdApprovedQuantity
				+ ", _senderPostStock=" + _senderPostStock
				+ ", _receiverPostStock=" + _receiverPostStock
				+ ", _senderBalance=" + _senderBalance + ", _cellId=" + _cellId
				+ ", _networkStockAfterReversal=" + _networkStockAfterReversal
				+ ", _reversalQty=" + _reversalQty + ", _loginId=" + _loginId
				+ ", _msisdn=" + _msisdn + ", _userCategory=" + _userCategory
				+ ", _gradeName=" + _gradeName + ", _paymentType="
				+ _paymentType + ", _initiatorRemarks=" + _initiatorRemarks
				+ ", _extTxnNo=" + _extTxnNo + ", _extTxnDate=" + _extTxnDate
				+ ", _externalCode=" + _externalCode
				+ ", _commissionProfileVer=" + _commissionProfileVer
				+ ", _commissionProfileSetId=" + _commissionProfileSetId
				+ ", _bonusBalance=" + _bonusBalance + ", _balanceType="
				+ _balanceType + ", _previousBonusBalance="
				+ _previousBonusBalance
				+ ", _afterTransSenderPreviousBonusStock="
				+ _afterTransSenderPreviousBonusStock + ", _startRange="
				+ _startRange + ", _endRange=" + _endRange + ", _bonusDebtQty="
				+ _bonusDebtQty + ", _mainDebitQty=" + _mainDebitQty
				+ ", userWallet=" + userWallet + ", totalReceiverBalance="
				+ totalReceiverBalance + ", totalSenderBalance="
				+ totalSenderBalance + ", otfApplicableFrom="
				+ otfApplicableFrom + ", otfApplicableTo=" + otfApplicableTo
				+ ", otfTimeSlab=" + otfTimeSlab + ", otfTypePctOrAMt="
				+ otfTypePctOrAMt + ", otfRate=" + otfRate + ", otfAmount="
				+ otfAmount + ", otfCountsUpdated=" + otfCountsUpdated
				+ ", reversalRequest=" + reversalRequest + ", targetAchieved="
				+ targetAchieved + ", otfApplicable=" + otfApplicable
				+ ", _othCommType=" + _othCommType + ", _othCommRate="
				+ _othCommRate + ", _othCommValue=" + _othCommValue
				+ ", _othCommProfType=" + _othCommProfType
				+ ", _othCommProfValue=" + _othCommProfValue
				+ ", _othCommSetId=" + _othCommSetId + ", _isOthSlabDefine="
				+ _isOthSlabDefine + "]";
	}


	public String isOtfApplicable() {
		return otfApplicable;
	}


	public void setOtfApplicable(String otfApplicable) {
		this.otfApplicable = otfApplicable;
	}


	public boolean isTargetAchieved() {
		return targetAchieved;
	}


	public void setTargetAchieved(boolean targetAchieved) {
		this.targetAchieved = targetAchieved;
	}


	public boolean isReversalRequest() {
		return reversalRequest;
	}


	public void setReversalRequest(boolean reversalRequest) {
		this.reversalRequest = reversalRequest;
	}


	public boolean isOtfCountsUpdated() {
		return otfCountsUpdated;
	}


	public void setOtfCountsUpdated(boolean otfCountsUpdated) {
		this.otfCountsUpdated = otfCountsUpdated;
	}


	public String getOtfRateAsString() {
        String otfRateAsString = Double.toString(otfRate);
        if (PretupsI.SYSTEM_AMOUNT.equals(otfTypePctOrAMt)) {
        	otfRateAsString = PretupsBL.getDisplayAmount( getOtfRate());
        }
        return otfRateAsString;
    }

    
    public String getOtfAsString() {
        return PretupsBL.getDisplayAmount(otfAmount);
    }

    
    public double getOtfRate() {
		return otfRate;
	}

	public void setOtfRate(double otfRate) {
		this.otfRate = otfRate;
	}

	public long getOtfAmount() {
		return otfAmount;
	}

	public void setOtfAmount(long otfAmount) {
		this.otfAmount = otfAmount;
	}
    
    public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}

	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		this.otfTypePctOrAMt = otfTypePctOrAMt;
	}

	public Date getOtfApplicableFrom() {
		return otfApplicableFrom;
	}

	public void setOtfApplicableFrom(Date otfApplicableFrom) {
		this.otfApplicableFrom = otfApplicableFrom;
	}

	public Date getOtfApplicableTo() {
		return otfApplicableTo;
	}

	public void setOtfApplicableTo(Date otfApplicableTo) {
		this.otfApplicableTo = otfApplicableTo;
	}

	public String getOtfTimeSlab() {
		return otfTimeSlab;
	}

	public void setOtfTimeSlab(String otfTimeSlab) {
		this.otfTimeSlab = otfTimeSlab;
	}



    
    

    public long getTotalSenderBalance() {
		return totalSenderBalance;
	}

	public void setTotalSenderBalance(long totalSenderBalance) {
		this.totalSenderBalance = totalSenderBalance;
	}

	public long getTotalReceiverBalance() {
		return totalReceiverBalance;
	}

	public void setTotalReceiverBalance(long totalBalance) {
		this.totalReceiverBalance = totalBalance;
	}

    public String getUserWallet() {
        return userWallet;
    }

    public void setUserWallet(String userWallet) {
        this.userWallet = userWallet;
    }

    /** STOP:Birendra:28JAN2015 */
    public long getNetworkStockAfterReversal() {
        return _networkStockAfterReversal;
    }

    public void setNetworkStockAfterReversal(long networkStockAfterReversal) {
        _networkStockAfterReversal = networkStockAfterReversal;
    }

    public long getReversalQty() {
        return _reversalQty;
    }

    public void setReversalQty(long reversalQty) {
        _reversalQty = reversalQty;
    }

    public String getLoginId() {
        return _loginId;
    }

    public void setLoginId(String loginId) {
        _loginId = loginId;
    }

    public String getMsisdn() {
        return _msisdn;
    }

    public void setMsisdn(String msisdn) {
        _msisdn = msisdn;
    }

    public String getUserCategory() {
        return _userCategory;
    }

    public void setUserCategory(String userCategory) {
        _userCategory = userCategory;
    }

    public String getGradeName() {
        return _gradeName;
    }

    public void setGradeName(String gradeName) {
        _gradeName = gradeName;
    }

    public String getPaymentType() {
        return _paymentType;
    }

    public void setPaymentType(String paymentType) {
        _paymentType = paymentType;
    }

    public String getInitiatorRemarks() {
        return _initiatorRemarks;
    }

    public void setInitiatorRemarks(String initiatorRemarks) {
        _initiatorRemarks = initiatorRemarks;
    }

    public String getExtTxnNo() {
        return _extTxnNo;
    }

    public void setExtTxnNo(String extTxnNo) {
        _extTxnNo = extTxnNo;
    }

    public java.util.Date getExtTxnDate() {
        return _extTxnDate;
    }

    public void setExtTxnDate(java.util.Date extTxnDate) {
        _extTxnDate = extTxnDate;
    }

    public String getExternalCode() {
        return _externalCode;
    }

    public void setExternalCode(String externalCode) {
        _externalCode = externalCode;
    }

    public String getCommissionProfileVer() {
        return _commissionProfileVer;
    }

    public void setCommissionProfileVer(String commissionProfileVer) {
        _commissionProfileVer = commissionProfileVer;
    }

    public String getCommissionProfileSetId() {
        return _commissionProfileSetId;
    }

    public void setCommissionProfileSetId(String commissionProfileSetId) {
        _commissionProfileSetId = commissionProfileSetId;
    }

    public String getCellId() {
        return _cellId;
    }

    /**
     * @param id
     *            the _cellId to set
     */
    public void setCellId(String id) {
        _cellId = id;
    }

    public String getSenderBalanceAsString() {
        return PretupsBL.getDisplayAmount(_senderBalance);
    }

    public String getPayableAmountAsString() {
        return PretupsBL.getDisplayAmount(_payableAmount);
    }
    public String getPayableAmountAsStringApproval() {
        return PretupsBL.getDisplayAmount(payableAmountApproval);
    }

    @Override
	public String getProductCost() {
        return PretupsBL.getDisplayAmount(_productTotalMRP);
    }

    public long getApprovedQuantity() {
        return _approvedQuantity;
    }

    public void setApprovedQuantity(long approvedQuantity) {
        _approvedQuantity = approvedQuantity;
    }

    public String getCommProfileDetailID() {
        return _commProfileDetailID;
    }

    public void setCommProfileDetailID(String commProfileDetailID) {
        _commProfileDetailID = commProfileDetailID;
    }

    public String getCommProfileProductID() {
        return _commProfileProductID;
    }

    public void setCommProfileProductID(String commProfileProductID) {
    	_commProfileProductID = commProfileProductID;
    }

    public double getCommRate() {
        return _commRate;
    }

    public void setCommRate(double commRate) {
        _commRate = commRate;
    }

    @Override
	public String getCommType() {
        return commType;
    }

    @Override
	public void setCommType(String commType) {
        this.commType = commType;
    }

    public long getCommValue() {
        return _commValue;
    }

    public void setCommValue(long commValue) {
        _commValue = commValue;
    }

    public long getNetPayableAmount() {
        return _netPayableAmount;
    }

    public void setNetPayableAmount(long netPayableAmount) {
        _netPayableAmount = netPayableAmount;
    }

    public long getPayableAmount() {
        return _payableAmount;
    }

    public void setPayableAmount(long payableAmount) {
        _payableAmount = payableAmount;
    }

    public long getReceiverPreviousStock() {
        return _receiverPreviousStock;
    }

    public void setReceiverPreviousStock(long receiverPreviousStock) {
        _receiverPreviousStock = receiverPreviousStock;
    }

    /**
     * because on the form requested qty is STring but in this it is
     * long so there is need to convert it from String to long
     */
    public long getRequiredQuantity() {
        return _requiredQuantity;
    }

    public void setRequiredQuantity(long requiredQuantity) {
        _requiredQuantity = requiredQuantity;
    }

    public long getSenderPreviousStock() {
        return _senderPreviousStock;
    }

    public void setSenderPreviousStock(long senderPreviousStock) {
        _senderPreviousStock = senderPreviousStock;
    }

    public int getSerialNum() {
        return _serialNum;
    }

    public void setSerialNum(int serialNum) {
        _serialNum = serialNum;
    }

    public double getTax1Rate() {
        return _tax1Rate;
    }

    public void setTax1Rate(double tax1Rate) {
        _tax1Rate = tax1Rate;
    }

    public String getTax1Type() {
        return _tax1Type;
    }

    public void setTax1Type(String tax1Type) {
        _tax1Type = tax1Type;
    }

    public long getTax1Value() {
        return _tax1Value;
    }

    public void setTax1Value(long tax1Value) {
        _tax1Value = tax1Value;
    }

    public double getTax2Rate() {
        return _tax2Rate;
    }

    public void setTax2Rate(double tax2Rate) {
        _tax2Rate = tax2Rate;
    }

    public String getTax2Type() {
        return _tax2Type;
    }

    public void setTax2Type(String tax2Type) {
        _tax2Type = tax2Type;
    }

    public long getTax2Value() {
        return _tax2Value;
    }

    public void setTax2Value(long tax2Value) {
        _tax2Value = tax2Value;
    }

    public double getTax3Rate() {
        return _tax3Rate;
    }

    public void setTax3Rate(double tax3Rate) {
        _tax3Rate = tax3Rate;
    }

    public String getTax3Type() {
        return _tax3Type;
    }

    public void setTax3Type(String tax3Type) {
        _tax3Type = tax3Type;
    }

    public long getTax3Value() {
        return _tax3Value;
    }

    public void setTax3Value(long tax3Value) {
        _tax3Value = tax3Value;
    }

    public String getTransferID() {
        return _transferID;
    }

    public void setTransferID(String transferID) {
        _transferID = transferID;
    }

    public String getCommAsString() {
        return PretupsBL.getDisplayAmount(_commValue);
    }

    public String getTax1ValueAsString() {
        return PretupsBL.getDisplayAmount(_tax1Value);
    }

    public String getTax2ValueAsString() {
        return PretupsBL.getDisplayAmount(_tax2Value);
    }

    public String getTax3ValueAsString() {
        return PretupsBL.getDisplayAmount(_tax3Value);
    }

    public String getNetPayableAmountAsString() {
        return PretupsBL.getDisplayAmount(_netPayableAmount);
    }
    public String getNetPayableAmountAsStringApproval() {
        return PretupsBL.getDisplayAmount(netPayableAmountApproval);
    }
    public boolean isSlabDefine() {
        return _isSlabDefine;
    }

    public void setSlabDefine(boolean isSlabDefine) {
        this._isSlabDefine = isSlabDefine;
    }

    public long getAfterTransReceiverPreviousStock() {
        return _afterTransReceiverPreviousStock;
    }

    public void setAfterTransReceiverPreviousStock(long afterTransReceiverPreviousStock) {
        _afterTransReceiverPreviousStock = afterTransReceiverPreviousStock;
    }

    public long getAfterTransSenderPreviousStock() {
        return _afterTransSenderPreviousStock;
    }

    public void setAfterTransSenderPreviousStock(long afterTransSenderPreviousStock) {
        _afterTransSenderPreviousStock = afterTransSenderPreviousStock;
    }

    public long getPreviousBalance() {
        return _previousBalance;
    }

    public void setPreviousBalance(long previousBalance) {
        _previousBalance = previousBalance;
    }

    public long getProductTotalMRP() {
        return _productTotalMRP;
    }

    public void setProductTotalMRP(long productTotalMRP) {
        _productTotalMRP = productTotalMRP;
    }

    public String getTax1RateAsString() {
        String tax1Rate = Double.toString(_tax1Rate);
        if (PretupsI.SYSTEM_AMOUNT.equals(_tax1Type)) {
            tax1Rate = PretupsBL.getDisplayAmount(Double.valueOf(getTax1Rate()));
        }
        return tax1Rate;
    }

    public String getTax2RateAsString() {
        String tax2Rate = Double.toString(_tax2Rate);
        if (PretupsI.SYSTEM_AMOUNT.equals(_tax2Type)) {
            tax2Rate = PretupsBL.getDisplayAmount(Double.valueOf(getTax2Rate()));
        }
        return tax2Rate;
    }

    public String getTax3RateAsString() {
        String tax3Rate = Double.toString(_tax3Rate);
        if (PretupsI.SYSTEM_AMOUNT.equals(_tax3Type)) {
            tax3Rate = PretupsBL.getDisplayAmount(Double.valueOf(getTax3Rate()));
        }
        return tax3Rate;
    }

    public String getCommRateAsString() {
        String commRate = Double.toString(_commRate);
        if (PretupsI.SYSTEM_AMOUNT.equals(commType)) {
            commRate = PretupsBL.getDisplayAmount(Double.valueOf(getCommRate()));
        }
        return commRate;
    }

    public String getProductMrpStr() {
        return _productMrpStr;
    }

    public void setProductMrpStr(String productMrpStr) {
        _productMrpStr = productMrpStr;
    }

    public String getTaxOnC2CTransfer() {
        return _taxOnC2CTransfer;
    }

    public void setTaxOnC2CTransfer(String taxOnC2CTransfer) {
        _taxOnC2CTransfer = taxOnC2CTransfer;
    }

    /**
     * @return Returns the commQuantity.
     */
    public String getCommQuantityAsString() {
        return PretupsBL.getDisplayAmount(_commQuantity);
    }

    public long getCommQuantity() {
        return _commQuantity;
    }

    /**
     * @param commQuantity
     *            The commQuantity to set.
     */
    public void setCommQuantity(long commQuantity) {
        _commQuantity = commQuantity;
    }

    /**
     * @return Returns the receiverCreditQty.
     */
    public String getReceiverCreditQtyAsString() {
        return PretupsBL.getDisplayAmount(_receiverCreditQty);
    }

    /**
     * @return Returns the senderDebitQty.
     */

    public String getSenderDebitQtyAsString() {
        return PretupsBL.getDisplayAmount(_senderDebitQty);
    }

    /**
     * @param receiverCreditQty
     *            The receiverCreditQty to set.
     */
    public void setReceiverCreditQty(long receiverCreditQty) {
        _receiverCreditQty = receiverCreditQty;
    }

    public long getSenderDebitQty() {
        return _senderDebitQty;
    }

    public long getReceiverCreditQty() {
        return _receiverCreditQty;
    }

    /**
     * @param senderDebitQty
     *            The senderDebitQty to set.
     */
    public void setSenderDebitQty(long senderDebitQty) {
        _senderDebitQty = senderDebitQty;
    }

    /**
     * @return Returns the afterTransCommisionSenderPreviousStock.
     */
    public long getAfterTransCommisionSenderPreviousStock() {
        return _afterTransCommisionSenderPreviousStock;
    }

    /**
     * @param afterTransCommisionSenderPreviousStock
     *            The afterTransCommisionSenderPreviousStock to set.
     */
    public void setAfterTransCommisionSenderPreviousStock(long afterTransCommisionSenderPreviousStock) {
        _afterTransCommisionSenderPreviousStock = afterTransCommisionSenderPreviousStock;
    }

    public String getFirstApprovedQuantity() {
        return _firstApprovedQuantity;
    }

    public void setFirstApprovedQuantity(String firstApprovedQuantity) {
        _firstApprovedQuantity = firstApprovedQuantity;
    }

    public String getSecondApprovedQuantity() {
        return _secondApprovedQuantity;
    }

    public void setSecondApprovedQuantity(String secondApprovedQuantity) {
        _secondApprovedQuantity = secondApprovedQuantity;
    }

    public String getThirdApprovedQuantity() {
        return _thirdApprovedQuantity;
    }

    public void setThirdApprovedQuantity(String thirdApprovedQuantity) {
        _thirdApprovedQuantity = thirdApprovedQuantity;
    }// BUG FIX by AshishT for Mobinil5.7

    public long getSenderPostStock() {
        return _senderPostStock;
    }

    public void setSenderPostStock(long postStock) {
        _senderPostStock = postStock;
    }

    public long getReceiverPostStock() {
        return _receiverPostStock;
    }

    public void setReceiverPostStock(long postStock) {
        _receiverPostStock = postStock;
    }

    public long getSenderBalance() {
        return _senderBalance;
    }

    public void setSenderBalance(long balance) {
        _senderBalance = balance;
    }

    @Override
	public Object clone() {
    	final String methodName="clone";
    	try {
        	
            final ChannelTransferItemsVO cloned = (ChannelTransferItemsVO) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
        //	logger.error(methodName,  e);
            return null;
        }
    }

    public long getStartRange() {
        return _startRange;
    }

    public void setStartRange(long range) {
        _startRange = range;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long range) {
        _endRange = range;
    }

    public long getBonusBalance() {
        return _bonusBalance;
    }

    public void setBonusBalance(long balance) {
        _bonusBalance = balance;
    }

    public String getBalanceType() {
        return _balanceType;
    }

    public void setBalanceType(String type) {
        _balanceType = type;
    }

    public long getPreviousBonusBalance() {
        return _previousBonusBalance;
    }

    public void setPreviousBonusBalance(long bonusBalance) {
        _previousBonusBalance = bonusBalance;
    }

    public long getAfterTransSenderPreviousBonusStock() {
        return _afterTransSenderPreviousBonusStock;
    }

    public void setAfterTransSenderPreviousBonusStock(long transSenderPreviousBonusStock) {
        _afterTransSenderPreviousBonusStock = transSenderPreviousBonusStock;
    }

    public long getBonusDebtQty() {
        return _bonusDebtQty;
    }

    public void setBonusDebtQty(long debtQty) {
        _bonusDebtQty = debtQty;
    }

    public long getMainDebitQty() {
        return _mainDebitQty;
    }

    public void setMainDebitQty(long debitQty) {
        _mainDebitQty = debitQty;
    }

    
	@Override
	public String getNetworkStockAsString() {
        return PretupsBL.getDisplayAmount(_afterTransSenderPreviousStock);
    }
	
     public String getSenderPreviousBalAsString(){
    	 return PretupsBL.getDisplayAmount(_senderPreviousStock);
     }
     
     public String getSenderPostBalAsString(){
    	 return PretupsBL.getDisplayAmount(_senderPostStock);
     }
     
     public String getReceiverPreviousBalAsString(){
    	 return PretupsBL.getDisplayAmount(_receiverPreviousStock);
     }
     
     public String getReceiverPostBalAsString(){
    	 return PretupsBL.getDisplayAmount(_receiverPostStock);
     }
	
	 public double getOthCommRate()
	{
		return _othCommRate;
	}
	public void setOthCommRate(double othCommRate)
	{
		_othCommRate = othCommRate;
	}
	public String getOthCommType()
	{
		return _othCommType;
	}
	public void setOthCommType(String othCommType)
	{
		_othCommType = othCommType;
	}
	public long getOthCommValue()
	{
		return _othCommValue;
	}
	public void setOthCommValue(long othCommValue)
	{
		_othCommValue = othCommValue;
	}
	public String getOthCommProfType()
	{
			return _othCommProfType;
	}
	public void setOthCommProfType(String othCommProfType)
	{
			_othCommProfType = othCommProfType;
	}
	public String getOthCommProfValue()
	{
			return _othCommProfValue;
	}
	public void setOthCommProfValue(String othCommProfValue)
	{
			_othCommProfValue = othCommProfValue;
	}
	public String getOthCommRateAsString()
	{
		String othCommRate = Double.toString(_othCommRate);
		if(PretupsI.SYSTEM_AMOUNT.equals(_othCommType))
		{
			othCommRate = PretupsBL.getDisplayAmount(getOthCommRate());
		}
			return othCommRate;
	}
	public String getOthCommAsString()
	{
			return PretupsBL.getDisplayAmount(_othCommValue);
	}
	public String getOthCommSetId()
	{
			return _othCommSetId;
	}
	public void setOthCommSetId(String othCommSetId)
	{
			_othCommSetId = othCommSetId;
	}
	public boolean isOthSlabDefine()
	{
			return _isOthSlabDefine;
	}
	public void setOthSlabDefine(boolean isOthSlabDefine)
	{
			this._isOthSlabDefine = isOthSlabDefine;
	}


	public long getInitialRequestedQuantity() {
		return initialRequestedQuantity;
	}


	public void setInitialRequestedQuantity(long initialRequestedQuantity) {
		this.initialRequestedQuantity = initialRequestedQuantity;
	}


	public String getInitialRequestedQuantityStr() {
		return initialRequestedQuantityStr;
	}


	public void setInitialRequestedQuantityStr(String initialRequestedQuantityStr) {
		this.initialRequestedQuantityStr = initialRequestedQuantityStr;
	}
	
	private String transactionType;
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getBundleID() {
		return bundleID;
	}

	public void setBundleID(String bundleID) {
		this.bundleID = bundleID;
	}

	public String getApprovedQuantityAsString() {
        return PretupsBL.getDisplayAmount(_approvedQuantity);
    }
}
