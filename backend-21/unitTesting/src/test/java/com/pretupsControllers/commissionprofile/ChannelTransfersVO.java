package com.pretupsControllers.commissionprofile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._parser;

public class ChannelTransfersVO {
	
	private static String _requestedQuantity;
	private static long _unitValue;
	private static boolean _isweb = true;
	private static String _commissionProfileDetailID;
	private static Date _OtfApplicableFrom;
	private static Date _OtfApplicableTo;
	private static String _OtfTimeSlab;
	private static String _BaseCommProfileDetailID;
	private static String _tax1Type;
	private static double _tax1Rate;
	private static long _tax1Value;
	private static String _tax2Type;
	private static double _tax2Rate;
	private static long _tax2Value;
	private static String _tax3Type;
	private static double _tax3Rate;
	private static long _tax3Value;
	private static String _commType;
	private static double _commRate;
	private static long _commValue;
	private static String _commProfileDetailID;
	private static String _userID = null;
	private static String _baseCommProfileOTFDetailID;
	private static String _baseCommProfileDetailID;
	private static long _otfValue;
	private static String _otfTypePctOrAMt;
	private static double _OtfRate;
	private static long _discountValue;
	private static String _discountType;
	private static double _discountRate;
	private static String _taxOnChannelTransfer;
	private static String _taxOnFOCTransfer;
	private static long _commQuantity;
	private static long _senderDebitQty;
	private static long _receiverCreditQty;
	private static long _payableAmount;
	private static long _netPayableAmount;
	private static double _productTotalMRP;
	private static long _requiredQuantity;
	private static boolean _isOtfFlag;
	private static long _otfAmount;
	private static Double _otherCommissionValue;
	private static Double _otherCommissionRate;
	private static String _otherCommisssionType;
	private static String _otherCommissionProfileType;
	
	public String getCommProfileDetailID() {
		return _commProfileDetailID;
	}
	
	public void setRequestedQuantity(String requestedQty) {
		_requestedQuantity = requestedQty;	
	}
	
    public void setRequiredQuantity(long requiredQuantity) {
        _requiredQuantity = requiredQuantity;
    }
	
	public String getRequestedQuantity() {
		return _requestedQuantity;	
	}
	
	public long getUnitValue() {
		return _unitValue;
	}
	
	public boolean isWeb() {
		return _isweb;
	}
	
	public String getBaseCommProfileDetailID() {
		return _BaseCommProfileDetailID;
	}
	
	public String getUserID() {
		return _userID;
	}
	
	public Date getOtfApplicableFrom() {
		return _OtfApplicableFrom;
	}
	
	public Date getOtfApplicableTo() {
		return _OtfApplicableTo;
	}
	
	public String getOtfTimeSlab() {
		return _OtfTimeSlab;
	}
	
	public String getCommType() {
		return _commType;
	}
	
	public double getCommRate() {
		return _commRate;
	}
	
	public void setCommValue(long commValue) {
		_commValue = commValue;
	}
	
	public long getOtfValue() {
		return _otfValue;
	}
	
	public void setOtfFlag(boolean flag) {
		_isOtfFlag = flag;
	}
	
	public boolean isOtfFlag() {
		return _isOtfFlag;
	}
	
	public String getBaseCommProfileOTFDetailID() {
		return _baseCommProfileOTFDetailID;
	}
	
	public void setBaseCommProfileOTFDetailID(String baseCommProfileOTFDetailID) {
		_baseCommProfileOTFDetailID = baseCommProfileOTFDetailID;
	}
	
	public void setBaseCommProfileDetailID(String baseCommProfileDetailID) {
		_baseCommProfileDetailID = baseCommProfileDetailID;
	}
	
	public void setOtfValue(long otfValue) {
		_otfValue = otfValue;
	}
	
    public String getOtfTypePctOrAMt() {
		return _otfTypePctOrAMt;
	}
    
	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		_otfTypePctOrAMt = otfTypePctOrAMt;
	}
	
	public double getOtfRate() {
		return _OtfRate;
	}
	
	public void setOtfRate(Double otfRate) {
		_OtfRate = otfRate;
	}
	
    public void setDiscountValue(long discountValue) {
        _discountValue = discountValue;
    }
    
    public String getDiscountType() {
    	return _discountType;
    }
    
    public double getDiscountRate() {
    	return _discountRate;
    }
	
    public String getTaxOnChannelTransfer() {
    	return _taxOnChannelTransfer;
    }
    
    public String getTaxOnFOCTransfer() {
    	return _taxOnFOCTransfer;
    }
    
    public String getTax1Type() {
    	return _tax1Type;
    }
    
    public double getTax1Rate() {
    	return _tax1Rate;
    }
    
    public String getTax2Type() {
    	return _tax2Type;
    }
    
    public double getTax2Rate() {
    	return _tax2Rate;
    }
    
    public String getTax3Type() {
    	return _tax3Type;
    }
    
    public double getTax3Rate() {
    	return _tax3Rate;
    }
    
    public void setTax1Value(long tax1Value) {
    	_tax1Value = tax1Value;
    }
    
    public long getTax1Value() {
    	return _tax1Value;
    }
    
    public void setTax2Value(long tax2Value) {
    	_tax2Value = tax2Value;
    }
    
    public long getTax2Value() {
    	return _tax2Value;
    }
    
    public void setTax3Value(long tax3Value) {
    	_tax3Value = tax3Value;
    }
    
    public long getTax3Value() {
    	return _tax3Value;
    }
    
    public long getCommValue() {
    	return _commValue;
    }
    
    public void setCommQuantity(long commQuantity) {
    	_commQuantity = commQuantity;
    }
    
    public long getCommQuantity() {
    	return _commQuantity;
    }
    
    public void setSenderDebitQty(long senderDebitQty) {
        _senderDebitQty = senderDebitQty;
    }
    
    public long getSenderDebitQty() {
    	return _senderDebitQty;
    }
    
    public void setReceiverCreditQty(long receiverCreditQty) {
    	_receiverCreditQty = receiverCreditQty;
    }
    
    public void setPayableAmount(long payableAmount) {
        _payableAmount = payableAmount;
    }
    
    public long getPayableAmount() {
    	return _payableAmount;
    }
    
    public void setNetPayableAmount(long netPayableAmount) {
        _netPayableAmount = netPayableAmount;
    }
    
    public long getNetPayableAmount () {
    	return _netPayableAmount;
    }
    
    public void setOtfAmount(long otfAmount) {
    	_otfAmount = otfAmount;
    }
    
    public long getOtfAmount() {
    	return _otfAmount;
    }
    
    public long getReceiverCreditQty() {
    	return _receiverCreditQty;
    }
    
    public void setProductTotalMRP(double d) {
        _productTotalMRP = d;
    }
    
    public double getProductTotalMRP() {
        return _productTotalMRP;
    }
    
    public long getDiscountValue() {
    	return _discountValue;
    }
    
    public long getRequiredQuantity() {
    	return _requiredQuantity;
    }
    
	public static Double getOtherCommissionValue() {
		return _otherCommissionValue;
	}

	public static void setOtherCommissionValue(Double _otherCommissionValue) {
		ChannelTransfersVO._otherCommissionValue = _otherCommissionValue;
	}
    
	public static String getOtherCommisssionType() {
		return _otherCommisssionType;
	}

	public static void setOtherCommisssionType(String _otherCommisssionType) {
		ChannelTransfersVO._otherCommisssionType = _otherCommisssionType;
	}

	public static Double getOtherCommissionRate() {
		return _otherCommissionRate;
	}

	public static void setOtherCommissionRate(Double _otherCommissionRate) {
		ChannelTransfersVO._otherCommissionRate = _otherCommissionRate;
	}

	public static String get_otherCommissionProfileType() {
		return _otherCommissionProfileType;
	}

	public static void set_otherCommissionProfileType(String _otherCommissionProfileType) {
		ChannelTransfersVO._otherCommissionProfileType = _otherCommissionProfileType;
	}

	public void loadChannelTransfersDAO(String ReceiverMSISDN, String ProductCode, String requestedQuantity) throws SQLException {
		final String methodname = "loadChannelTransfersDAO";
		Log.debug("Entered " + methodname + "(" + ReceiverMSISDN + ", " + ProductCode + ", " + requestedQuantity + ")");
		ResultSet CommissionProfileDetails = DBHandler.AccessHandler.getCommissionProfileDetails(ReceiverMSISDN, ProductCode, requestedQuantity);
		CommissionProfileDetails.beforeFirst();
		if (CommissionProfileDetails.next()) {
			_tax1Type = CommissionProfileDetails.getString("tax1_type");
			_tax1Rate = CommissionProfileDetails.getDouble("tax1_rate");
			_tax2Type = CommissionProfileDetails.getString("tax2_type");
			_tax2Rate = CommissionProfileDetails.getDouble("tax2_rate");
			_tax3Type = CommissionProfileDetails.getString("tax3_type");
			_tax3Rate = CommissionProfileDetails.getDouble("tax3_rate");
			_commType = CommissionProfileDetails.getString("commission_type");
			_commRate = CommissionProfileDetails.getDouble("commission_rate");
			_commProfileDetailID = CommissionProfileDetails.getString("comm_profile_detail_id");
			_discountType = CommissionProfileDetails.getString("discount_type");
			_discountRate = CommissionProfileDetails.getDouble("discount_rate");
			_taxOnChannelTransfer = CommissionProfileDetails.getString("TAXES_ON_CHANNEL_TRANSFER");
			_taxOnFOCTransfer = CommissionProfileDetails.getString("TAXES_ON_FOC_APPLICABLE");
			_requiredQuantity = _parser.getSystemAmount(requestedQuantity);
		}
		
		_requestedQuantity = requestedQuantity;
		_unitValue = DBHandler.AccessHandler.getProductUnitValue(ProductCode); 
		// _isweb can be called in future for Non WEB transactions
		
		if(SystemPreferences.OTH_COM_ENABLED) {
			ResultSet otherCommissionDetails = DBHandler.AccessHandler.getOtherCommissionProfileDetails(ReceiverMSISDN, ProductCode, requestedQuantity);
			if(otherCommissionDetails.next()) {
				_otherCommissionRate = otherCommissionDetails.getDouble("oth_commission_rate");
				_otherCommisssionType = otherCommissionDetails.getString("oth_commission_type");
				_otherCommissionProfileType = otherCommissionDetails.getString("oth_comm_prf_type").trim();
			}
		}
		Log.debug("Exited " + methodname + "()");
	}
}
