package com.restapi.superadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;


public class ChannelTransferRuleResponseVO extends BaseResponse{
	
	public String domainName;
	public String toDomainName;
	public ArrayList categoryDomainList;
	public String userCategory;
	public ArrayList productList;
	public ArrayList categoryList;
	public ArrayList toCategoryList;
	public boolean acrossDomain;
	public ArrayList transferTypeList;
	public ArrayList productionVOList;
	public ArrayList uncontrollTxnLevelList;
	public ArrayList controllTxnLevelList;
	public ArrayList fixedTransferLevelList;
	public String transferTypeDesc;
	public String uncntrlTransferLevelDesc;
	public String uncntrlReturnLevelDesc;
	public String uncntrlWithdrawLevelDesc;
	public String cntrlTransferLevelDesc;
	public String ctrlReturnLevelDesc;
	public String cntrlWithdrawLevelDesc;
	public String fixedTransferLevelDesc;
	public String fixedReturnLevelDesc;
	public String fixedWithdrawLevelDesc;
	public String cntrlReturnLevelDesc;
	public ChannelTransferRuleVO channelTransferRuleVO;
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getToDomainName() {
		return toDomainName;
	}
	public void setToDomainName(String toDomainName) {
		this.toDomainName = toDomainName;
	}
	public ArrayList getCategoryDomainList() {
		return categoryDomainList;
	}
	public void setCategoryDomainList(ArrayList categoryDomainList) {
		this.categoryDomainList = categoryDomainList;
	}
	public String getUserCategory() {
		return userCategory;
	}
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}
	public ArrayList getProductList() {
		return productList;
	}
	public void setProductList(ArrayList productList) {
		this.productList = productList;
	}
	public ArrayList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList getToCategoryList() {
		return toCategoryList;
	}
	public void setToCategoryList(ArrayList toCategoryList) {
		this.toCategoryList = toCategoryList;
	}
	public boolean isAcrossDomain() {
		return acrossDomain;
	}
	public void setAcrossDomain(boolean acrossDomain) {
		this.acrossDomain = acrossDomain;
	}
	public ArrayList getTransferTypeList() {
		return transferTypeList;
	}
	public void setTransferTypeList(ArrayList transferTypeList) {
		this.transferTypeList = transferTypeList;
	}
	
	public ArrayList getProductionVOList() {
		return productionVOList;
	}
	public void setProductionVOList(ArrayList productionVOList) {
		this.productionVOList = productionVOList;
	}
	public ArrayList getUncontrollTxnLevelList() {
		return uncontrollTxnLevelList;
	}
	public void setUncontrollTxnLevelList(ArrayList uncontrollTxnLevelList) {
		this.uncontrollTxnLevelList = uncontrollTxnLevelList;
	}
	public ArrayList getControllTxnLevelList() {
		return controllTxnLevelList;
	}
	public void setControllTxnLevelList(ArrayList controllTxnLevelList) {
		this.controllTxnLevelList = controllTxnLevelList;
	}
	
	public String getTransferTypeDesc() {
		return transferTypeDesc;
	}
	public void setTransferTypeDesc(String transferTypeDesc) {
		this.transferTypeDesc = transferTypeDesc;
	}
	public String getUncntrlTransferLevelDesc() {
		return uncntrlTransferLevelDesc;
	}
	public void setUncntrlTransferLevelDesc(String uncntrlTransferLevelDesc) {
		this.uncntrlTransferLevelDesc = uncntrlTransferLevelDesc;
	}
	public String getUncntrlReturnLevelDesc() {
		return uncntrlReturnLevelDesc;
	}
	public void setUncntrlReturnLevelDesc(String uncntrlReturnLevelDesc) {
		this.uncntrlReturnLevelDesc = uncntrlReturnLevelDesc;
	}
	public String getUncntrlWithdrawLevelDesc() {
		return uncntrlWithdrawLevelDesc;
	}
	public ArrayList getFixedTransferLevelList() {
		return fixedTransferLevelList;
	}
	public void setFixedTransferLevelList(ArrayList fixedTransferLevelList) {
		this.fixedTransferLevelList = fixedTransferLevelList;
	}
	public void setUncntrlWithdrawLevelDesc(String uncntrlWithdrawLevelDesc) {
		this.uncntrlWithdrawLevelDesc = uncntrlWithdrawLevelDesc;
	}
	public String getCntrlTransferLevelDesc() {
		return cntrlTransferLevelDesc;
	}
	public void setCntrlTransferLevelDesc(String cntrlTransferLevelDesc) {
		this.cntrlTransferLevelDesc = cntrlTransferLevelDesc;
	}
	public String getCtrlReturnLevelDesc() {
		return ctrlReturnLevelDesc;
	}
	public void setCtrlReturnLevelDesc(String ctrlReturnLevelDesc) {
		this.ctrlReturnLevelDesc = ctrlReturnLevelDesc;
	}
	public String getCntrlWithdrawLevelDesc() {
		return cntrlWithdrawLevelDesc;
	}
	public void setCntrlWithdrawLevelDesc(String cntrlWithdrawLevelDesc) {
		this.cntrlWithdrawLevelDesc = cntrlWithdrawLevelDesc;
	}
	public String getFixedTransferLevelDesc() {
		return fixedTransferLevelDesc;
	}
	public void setFixedTransferLevelDesc(String fixedTransferLevelDesc) {
		this.fixedTransferLevelDesc = fixedTransferLevelDesc;
	}
	public String getFixedReturnLevelDesc() {
		return fixedReturnLevelDesc;
	}
	public void setFixedReturnLevelDesc(String fixedReturnLevelDesc) {
		this.fixedReturnLevelDesc = fixedReturnLevelDesc;
	}
	public String getFixedWithdrawLevelDesc() {
		return fixedWithdrawLevelDesc;
	}
	public void setFixedWithdrawLevelDesc(String fixedWithdrawLevelDesc) {
		this.fixedWithdrawLevelDesc = fixedWithdrawLevelDesc;
	}
	public String getCntrlReturnLevelDesc() {
		return cntrlReturnLevelDesc;
	}
	public void setCntrlReturnLevelDesc(String cntrlReturnLevelDesc) {
		this.cntrlReturnLevelDesc = cntrlReturnLevelDesc;
	}
	public ChannelTransferRuleVO getChannelTransferRuleVO() {
		return channelTransferRuleVO;
	}
	public void setChannelTransferRuleVO(ChannelTransferRuleVO channelTransferRuleVO) {
		this.channelTransferRuleVO = channelTransferRuleVO;
	}
	
	
		

}
