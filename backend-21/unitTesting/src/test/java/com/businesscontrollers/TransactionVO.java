package com.businesscontrollers;

import java.util.Arrays;
import java.util.HashMap;

import com.pretupsControllers.BTSLUtil;
import com.pretupsControllers.commissionprofile.CommissionVO;

public class TransactionVO extends NetworkStockVO {

	private String _txntype;
	private String _multiWalletStatus;
	private HashMap<String, CommissionVO> _commissionVO;
	private HashMap<String, String> _initiatedQty;
	
	private String _fromMSISDN;
	private HashMap<String, String> _fromUserPreBalances;
	private String _toMSISDN;
	private HashMap<String, String> _toUserPreBalances;
	private HashMap<String, String> _fromUserPostBalances;
	private HashMap<String, String> _toUserPostBalances;
	private HashMap<String, String> salePreBalances;
	
	private String _autoNetworkStockSystemStatus;
	private String autoNetworkStockSystemThreshold;
	private String _dualCommType;
	private String gatewayType;
	
	public HashMap<String, String> getSalePreBalances() {
		return salePreBalances;
	}
	public void setSalePreBalances(HashMap<String, String> salePreBalances) {
		this.salePreBalances = salePreBalances;
	}
	
	public String get_multiWalletStatus() {
		return _multiWalletStatus;
	}
	public void set_multiWalletStatus(String _multiWalletStatus) {
		this._multiWalletStatus = _multiWalletStatus;
	}
	public String get_txntype() {
		return _txntype;
	}
	public void set_txntype(String _txntype) {
		this._txntype = _txntype;
	}
	public HashMap<String, String> get_fromUserPreBalances() {
		return _fromUserPreBalances;
	}
	public void set_fromUserPreBalances(HashMap<String, String> _fromUserPreBalances) {
		this._fromUserPreBalances = _fromUserPreBalances;
	}
	public HashMap<String, String> get_toUserPreBalances() {
		return _toUserPreBalances;
	}
	public void set_toUserPreBalances(HashMap<String, String> _toUserPreBalances) {
		this._toUserPreBalances = _toUserPreBalances;
	}
	public HashMap<String, String> get_fromUserPostBalances() {
		return _fromUserPostBalances;
	}
	public void set_fromUserPostBalances(HashMap<String, String> _fromUserPostBalances) {
		this._fromUserPostBalances = _fromUserPostBalances;
	}
	public HashMap<String, String> get_toUserPostBalances() {
		return _toUserPostBalances;
	}
	public void set_toUserPostBalances(HashMap<String, String> _toUserPostBalances) {
		this._toUserPostBalances = _toUserPostBalances;
	}
	public HashMap<String, String> getInitiatedQty() {
		return _initiatedQty;
	}
	public void setInitiatedQty(HashMap<String, String> _initiatedQty) {
		this._initiatedQty = _initiatedQty;
	}
	public String get_fromMSISDN() {
		return _fromMSISDN;
	}
	public void set_fromMSISDN(String _fromMSISDN) {
		this._fromMSISDN = _fromMSISDN;
	}
	public String get_toMSISDN() {
		return _toMSISDN;
	}
	public void set_toMSISDN(String _toMSISDN) {
		this._toMSISDN = _toMSISDN;
	}
	public HashMap<String, CommissionVO> getCommissionVO() {
		return _commissionVO;
	}
	public void setCommissionVO(HashMap<String, CommissionVO> _commissionVO) {
		this._commissionVO = _commissionVO;
	}
	public String getautoNetworkStockSystemStatus() {
		return _autoNetworkStockSystemStatus;
	}
	public void setautoNetworkStockSystemStatus(String _autoNetworkStockSystemStatus) {
		this._autoNetworkStockSystemStatus = _autoNetworkStockSystemStatus;
	}
	public void setAutoNetworkStockSystemThreshold(String autoNetworkStockSystemThreshold) {
		this.autoNetworkStockSystemThreshold = autoNetworkStockSystemThreshold;
	}
	public void setDualCommissioningType(String dualCommType) {
		_dualCommType = dualCommType;
	}
	public String getDualCommissioningType() {
		return _dualCommType;
	}
	public String getGatewayType() {
		return gatewayType;
	}
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}
	
	public HashMap<String, Object[]> getAutoNetworkStockThresholds(String wallet) {
		HashMap<String, Object[]> Thresholds = new HashMap<String, Object[]>();
		if (!BTSLUtil.isNullString(autoNetworkStockSystemThreshold)) {
			String[] walletBreakdown = autoNetworkStockSystemThreshold.split(",");
			
			for (int i = 0; i < walletBreakdown.length; i++) {
				String[] productBreakDown = walletBreakdown[i].split(":");
				if (productBreakDown[0].equals(wallet)) {
					Object[] thresholdObj = new Object[2];
					thresholdObj[0] = productBreakDown[2];
					thresholdObj[1] = productBreakDown[3];
					Thresholds.put(productBreakDown[1], thresholdObj);
				}
			}

		}
		return Thresholds;
	}
	
	public String toString() {
		 final StringBuilder sbd = new StringBuilder("TransactionVO ");
	        sbd.append("txntype=").append(_txntype).append(",");
	        sbd.append("multiWalletStatus=").append(_multiWalletStatus).append(",");
	        sbd.append("autoNetworkStockSystemStatus").append(_autoNetworkStockSystemStatus).append(",");
	        sbd.append("autoNetworkStockSystemThreshold").append(autoNetworkStockSystemThreshold).append(",");
	        sbd.append("salePreBalances=").append(Arrays.asList(getSalePreBalances())).append(",");
	        sbd.append("focPreBalances=").append(getFocPreBalances()).append(",");
	        sbd.append("incentivePreBalances=").append(getIncentivePreBalances()).append(",");
	        sbd.append("salePostBalances=").append(getSalePostBalances()).append(",");
	        sbd.append("focPostBalances=").append(getFocPostBalances()).append(",");
	        sbd.append("incentivePostBalances=").append(getIncentivePostBalances()).append(",");
	        sbd.append("initiatedQty=").append(Arrays.asList(_initiatedQty)).append(",");
	        sbd.append("fromMSISDN=").append(_fromMSISDN).append(",");
	        sbd.append("fromUserPreBalances=").append(Arrays.asList(_fromUserPreBalances)).append(",");
	        sbd.append("toMSISDN=").append(_toMSISDN).append(",");
	        sbd.append("toUserPreBalances=").append(Arrays.asList(_toUserPreBalances)).append(",");
	        sbd.append("fromUserPostBalances=").append(Arrays.asList(_fromUserPostBalances)).append(",");
	        sbd.append("toUserPostBalances=").append(Arrays.asList(_toUserPostBalances)).append(",");
	        return sbd.toString();
	}
}
