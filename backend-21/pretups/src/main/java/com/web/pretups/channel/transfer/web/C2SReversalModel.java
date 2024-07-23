package com.web.pretups.channel.transfer.web;

import java.util.List;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;

public class C2SReversalModel {

	private String moduleType;

	private String serviceType;

	private String dispalyMsisdn;

	private String pinRequired;

	private List<ServiceKeywordCacheVO> serviceKeywordList;

	private List<ListValueVO> serviceTypeList;

	private List<ListValueVO> moduleTypeList;

	private String showBalance;

	private Long currentBalance;

	private String pin;

	private String senderMsisdn;

	private String subscriberMsisdn;

	private String txID;

	private String selectIndex;

	private String j_captcha_response;

	private String txnid;

	private String c2sReverseResponseMessage;

	private Boolean c2sReverseResponseStatus;

	private String countryCode;

	private String displayTransferMRP;

	public String getDisplayTransferMRP() {
		return displayTransferMRP;
	}

	public void setDisplayTransferMRP(String transferMRP) {
		displayTransferMRP = transferMRP;
	}


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getC2sReverseResponseMessage() {
		return c2sReverseResponseMessage;
	}

	public void setC2sReverseResponseMessage(String c2sReverseResponseMessage) {
		this.c2sReverseResponseMessage = c2sReverseResponseMessage;
	}

	public Boolean getC2sReverseResponseStatus() {
		return c2sReverseResponseStatus;
	}

	public void setC2sReverseResponseStatus(Boolean c2sReverseResponseStatus) {
		this.c2sReverseResponseStatus = c2sReverseResponseStatus;
	}

	public String getTxnid() {
		return txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

	public String getJ_captcha_response() {
		return j_captcha_response;
	}

	public void setJ_captcha_response(String j_captcha_response) {
		this.j_captcha_response = j_captcha_response;
	}

	public String getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(String selectIndex) {
		this.selectIndex = selectIndex;
	}

	private List<ChannelTransferVO> userRevlist;

	private C2STransferVO transferVO;

	private String _tempSubMsisdn;

	private String _subSid;

	public List<ChannelTransferVO> getUserRevlist() {
		return userRevlist;
	}

	public C2STransferVO getTransferVO() {
		return transferVO;
	}

	public void setTransferVO(C2STransferVO transferVO) {
		this.transferVO = transferVO;
	}

	public void setUserRevlist(List<ChannelTransferVO> userRevlist) {
		this.userRevlist = userRevlist;
	}

	public String getTxID() {
		return txID;
	}

	public void setTxID(String txID) {
		this.txID = txID;
	}

	public String getSubscriberMsisdn() {
		return subscriberMsisdn;
	}

	public void setSubscriberMsisdn(String subscriberMsisdn) {
		this.subscriberMsisdn = subscriberMsisdn;
	}

	public String getSenderMsisdn() {
		return senderMsisdn;
	}

	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getDispalyMsisdn() {
		return dispalyMsisdn;
	}

	public void setDispalyMsisdn(String dispalyMsisdn) {
		this.dispalyMsisdn = dispalyMsisdn;
	}

	public String getPinRequired() {
		return pinRequired;
	}

	public void setPinRequired(String pinRequired) {
		this.pinRequired = pinRequired;
	}

	public List<ServiceKeywordCacheVO> getServiceKeywordList() {
		return serviceKeywordList;
	}

	public void setServiceKeywordList(List<ServiceKeywordCacheVO> serviceKeywordList) {
		this.serviceKeywordList = serviceKeywordList;
	}

	public List<ListValueVO> getServiceTypeList() {
		return serviceTypeList;
	}

	public void setServiceTypeList(List<ListValueVO> serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}

	public List<ListValueVO> getModuleTypeList() {
		return moduleTypeList;
	}

	public void setModuleTypeList(List<ListValueVO> moduleTypeList) {
		this.moduleTypeList = moduleTypeList;
	}

	public String getShowBalance() {
		return showBalance;
	}

	public void setShowBalance(String showBalance) {
		this.showBalance = showBalance;
	}

	public Long getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Long currentBalance) {
		this.currentBalance = currentBalance;
	}

	public void setSubSid(String subSid){
    	_subSid = subSid;
	}
	
    public String getSubSid(){
    	return _subSid;
    }

	public void setTempSubMsisdn(String tempSubMsisdn)
    {
    	_tempSubMsisdn=tempSubMsisdn;
	}

    public String getTempSubMsisdn()
    {
    	return _tempSubMsisdn;
    }
}
