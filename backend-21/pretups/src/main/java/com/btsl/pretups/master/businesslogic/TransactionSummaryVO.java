package com.btsl.pretups.master.businesslogic;

public class TransactionSummaryVO {
	private String _type;
	private String _networkName;
	private String _gatewayCode;
	private String _category;
	private String _service;
	private String _subService;
	private String _interfaceId;
	private long _succesfulRecharges;
	private double _rechargesDenoms;
	private double _accessFee;
	private double _serviceTax;
	private double _failAmt; 
	private long _failCount;
	private double _talkTimeAmt;
	private double _totalRechargeAmt;
	private String _transDate; 
	private int _transTime; 
	private String _transMonth;
	private String _timeDateMonth;
	private int _transYear;
	
	public String getNetworkName() {
		return _networkName;
	}
	public void setNetworkName(String name) {
		_networkName = name;
	}
	public String getGatewayCode() {
		return _gatewayCode;
	}
	public void setGatewayCode(String _gatewayCode) {
		this._gatewayCode = _gatewayCode;
	}
	public String getCategory() {
		return _category;
	}
	public void setCategory(String _category) {
		this._category = _category;
	}
	public String getService() {
		return _service;
	}
	public void setService(String _service) {
		this._service = _service;
	}
	public String getSubService() {
		return _subService;
	}
	public void setSubService(String service) {
		_subService = service;
	}
	public String getInterfaceId() {
		return _interfaceId;
	}
	public void setInterfaceId(String id) {
		_interfaceId = id;
	}
	public String getType() {
		return _type;
	}
	public void setType(String type) {
		this._type = type;
	}
	public long getSuccesfulRecharges() {
		return _succesfulRecharges;
	}
	public void setSuccesfulRecharges(long recharges) {
		_succesfulRecharges = recharges;
	}
	public double getRechargesDenoms() {
		return _rechargesDenoms;
	}
	public void setRechargesDenoms(double denoms) {
		_rechargesDenoms = denoms;
	}
	public double getAccessFee() {
		return _accessFee;
	}
	public void setAccessFee(double fee) {
		_accessFee = fee;
	}
	public double getServiceTax() {
		return _serviceTax;
	}
	public void setServiceTax(double tax) {
		_serviceTax = tax;
	}
	public double getFailAmt() {
		return _failAmt;
	}
	public void setFailAmt(double amt) {
		_failAmt = amt;
	}
	public long getFailCount() {
		return _failCount;
	}
	public void setFailCount(long count) {
		_failCount = count;
	}
	public double getTalkTimeAmt() {
		return _talkTimeAmt;
	}
	public void setTalkTimeAmt(double timeAmt) {
		_talkTimeAmt = timeAmt;
	}
	public double getTotalRechargeAmt() {
		return _totalRechargeAmt;
	}
	public void setTotalRechargeAmt(double rechargeAmt) {
		_totalRechargeAmt = rechargeAmt;
	}
	public String getTransDate() {
		return _transDate;
	}
	public void setTransDate(String date) {
		_transDate = date;
	}
	public int getTransTime() {
		return _transTime;
	}
	public void setTransTime(int time) {
		_transTime = time;
	}
	public String getTransMonth() {
		return _transMonth;
	}
	public void setTransMonth(String month) {
		_transMonth = month;
	}
	
	public String getTimeDateMonth() {
		return _timeDateMonth;
	}
	public void setTimeDateMonth(String timeDateMonth) {
		_timeDateMonth = timeDateMonth;
	}
	
	public int getTransYear() {
		return _transYear;
	}
	public void setTransYear(int transYear) {
		_transYear = transYear;
	}
	
	
}
