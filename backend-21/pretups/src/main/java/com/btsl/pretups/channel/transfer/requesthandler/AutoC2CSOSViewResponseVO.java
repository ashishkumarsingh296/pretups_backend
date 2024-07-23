package com.btsl.pretups.channel.transfer.requesthandler;

import com.btsl.common.BaseResponse;

public class AutoC2CSOSViewResponseVO extends BaseResponse {
	

	    private String  msisdn;
	    private String  subscriberID;
	    private String  employeeCode;
	    private String  employeeName;
	    private String  minTxnAmount;
	    private String  maxTxnAmount;
	    private String  monthlyLimit;
	    private String  processedRecs;

	    private String  subscriberTypeID;
	    private String  subscriberTypeIDDesc;
	    
	    private String  languageID;
	    private String  languageIDDesc;
	    private boolean  selfAllow = false;
	    private String  loginUserID = null;
	    private String  loginUserName = null;
	    private String  loginUserCatCode = null;
	    private String  loginUserCatName = null;
	    private String  loginUserType;
	    private String  geoDomainName;
	    private String  geoDomainCode;
	    private String  domainCode;
	    private String  domainName;
	    private boolean  ownerOnly = false;
	    private String  categoryCode;
	    private long  time = 0;
	    private String  userName;
	    private String  userID;
	    private String  ownerID = null;
	    private String  ownerName = null;
	    private String  ownerCategoryName;
	    private String  categoryName;
	    private String  geographicalDomainCodeDesc;
	    private String  geographicalDomainCode;
	    private String  autoc2callowed;
	    private String  errorFlag;
	    private String sosAllowed = null;
	    private String sosAllowedAmount = null;
	    private String sosThresholdLimit = null;
		private String lrAllowed = null;
		private String lrMaxAmount = null;
	    private String autoo2callowed;
		private String autoO2CTxnAmunt = null;
		private String autoO2CThresholdLimit = null;
		
		public String getMsisdn() {
			return msisdn;
		}
		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}
		public String getSubscriberID() {
			return subscriberID;
		}
		public void setSubscriberID(String subscriberID) {
			this.subscriberID = subscriberID;
		}
		public String getEmployeeCode() {
			return employeeCode;
		}
		public void setEmployeeCode(String employeeCode) {
			this.employeeCode = employeeCode;
		}
		public String getEmployeeName() {
			return employeeName;
		}
		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}
		public String getMinTxnAmount() {
			return minTxnAmount;
		}
		public void setMinTxnAmount(String minTxnAmount) {
			this.minTxnAmount = minTxnAmount;
		}
		public String getMaxTxnAmount() {
			return maxTxnAmount;
		}
		public void setMaxTxnAmount(String maxTxnAmount) {
			this.maxTxnAmount = maxTxnAmount;
		}
		public String getMonthlyLimit() {
			return monthlyLimit;
		}
		public void setMonthlyLimit(String monthlyLimit) {
			this.monthlyLimit = monthlyLimit;
		}
		public String getProcessedRecs() {
			return processedRecs;
		}
		public void setProcessedRecs(String processedRecs) {
			this.processedRecs = processedRecs;
		}
		public String getSubscriberTypeID() {
			return subscriberTypeID;
		}
		public void setSubscriberTypeID(String subscriberTypeID) {
			this.subscriberTypeID = subscriberTypeID;
		}
		public String getSubscriberTypeIDDesc() {
			return subscriberTypeIDDesc;
		}
		public void setSubscriberTypeIDDesc(String subscriberTypeIDDesc) {
			this.subscriberTypeIDDesc = subscriberTypeIDDesc;
		}
		public String getLanguageID() {
			return languageID;
		}
		public void setLanguageID(String languageID) {
			this.languageID = languageID;
		}
		public String getLanguageIDDesc() {
			return languageIDDesc;
		}
		public void setLanguageIDDesc(String languageIDDesc) {
			this.languageIDDesc = languageIDDesc;
		}
		public boolean isSelfAllow() {
			return selfAllow;
		}
		public void setSelfAllow(boolean selfAllow) {
			this.selfAllow = selfAllow;
		}
		public String getLoginUserID() {
			return loginUserID;
		}
		public void setLoginUserID(String loginUserID) {
			this.loginUserID = loginUserID;
		}
		public String getLoginUserName() {
			return loginUserName;
		}
		public void setLoginUserName(String loginUserName) {
			this.loginUserName = loginUserName;
		}
		public String getLoginUserCatCode() {
			return loginUserCatCode;
		}
		public void setLoginUserCatCode(String loginUserCatCode) {
			this.loginUserCatCode = loginUserCatCode;
		}
		public String getLoginUserCatName() {
			return loginUserCatName;
		}
		public void setLoginUserCatName(String loginUserCatName) {
			this.loginUserCatName = loginUserCatName;
		}
		public String getLoginUserType() {
			return loginUserType;
		}
		public void setLoginUserType(String loginUserType) {
			this.loginUserType = loginUserType;
		}
		public String getGeoDomainName() {
			return geoDomainName;
		}
		public void setGeoDomainName(String geoDomainName) {
			this.geoDomainName = geoDomainName;
		}
		public String getGeoDomainCode() {
			return geoDomainCode;
		}
		public void setGeoDomainCode(String geoDomainCode) {
			this.geoDomainCode = geoDomainCode;
		}
		public String getDomainCode() {
			return domainCode;
		}
		public void setDomainCode(String domainCode) {
			this.domainCode = domainCode;
		}
		public String getDomainName() {
			return domainName;
		}
		public void setDomainName(String domainName) {
			this.domainName = domainName;
		}
		public boolean isOwnerOnly() {
			return ownerOnly;
		}
		public void setOwnerOnly(boolean ownerOnly) {
			this.ownerOnly = ownerOnly;
		}
		public String getCategoryCode() {
			return categoryCode;
		}
		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}
		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getUserID() {
			return userID;
		}
		public void setUserID(String userID) {
			this.userID = userID;
		}
		public String getOwnerID() {
			return ownerID;
		}
		public void setOwnerID(String ownerID) {
			this.ownerID = ownerID;
		}
		public String getOwnerName() {
			return ownerName;
		}
		public void setOwnerName(String ownerName) {
			this.ownerName = ownerName;
		}
		public String getOwnerCategoryName() {
			return ownerCategoryName;
		}
		public void setOwnerCategoryName(String ownerCategoryName) {
			this.ownerCategoryName = ownerCategoryName;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getGeographicalDomainCodeDesc() {
			return geographicalDomainCodeDesc;
		}
		public void setGeographicalDomainCodeDesc(String geographicalDomainCodeDesc) {
			this.geographicalDomainCodeDesc = geographicalDomainCodeDesc;
		}
		public String getGeographicalDomainCode() {
			return geographicalDomainCode;
		}
		public void setGeographicalDomainCode(String geographicalDomainCode) {
			this.geographicalDomainCode = geographicalDomainCode;
		}
		public String getAutoc2callowed() {
			return autoc2callowed;
		}
		public void setAutoc2callowed(String autoc2callowed) {
			this.autoc2callowed = autoc2callowed;
		}
		public String getErrorFlag() {
			return errorFlag;
		}
		public void setErrorFlag(String errorFlag) {
			this.errorFlag = errorFlag;
		}
		public String getSosAllowed() {
			return sosAllowed;
		}
		public void setSosAllowed(String sosAllowed) {
			this.sosAllowed = sosAllowed;
		}
		public String getSosAllowedAmount() {
			return sosAllowedAmount;
		}
		public void setSosAllowedAmount(String sosAllowedAmount) {
			this.sosAllowedAmount = sosAllowedAmount;
		}
		public String getSosThresholdLimit() {
			return sosThresholdLimit;
		}
		public void setSosThresholdLimit(String sosThresholdLimit) {
			this.sosThresholdLimit = sosThresholdLimit;
		}
		
		
		public String getLrAllowed() {
			return lrAllowed;
		}
		public void setLrAllowed(String lrAllowed) {
			this.lrAllowed = lrAllowed;
		}
		public String getLrMaxAmount() {
			return lrMaxAmount;
		}
		public void setLrMaxAmount(String lrMaxAmount) {
			this.lrMaxAmount = lrMaxAmount;
		}
		
		
		
		public String getAutoo2callowed() {
			return autoo2callowed;
		}
		public void setAutoo2callowed(String autoo2callowed) {
			this.autoo2callowed = autoo2callowed;
		}
		public String getAutoO2CTxnAmunt() {
			return autoO2CTxnAmunt;
		}
		public void setAutoO2CTxnAmunt(String autoO2CTxnAmunt) {
			this.autoO2CTxnAmunt = autoO2CTxnAmunt;
		}
		public String getAutoO2CThresholdLimit() {
			return autoO2CThresholdLimit;
		}
		public void setAutoO2CThresholdLimit(String autoO2CThresholdLimit) {
			this.autoO2CThresholdLimit = autoO2CThresholdLimit;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AutoC2cSosResponseVO [msisdn=");
			builder.append(msisdn);
			builder.append(", subscriberID=");
			builder.append(subscriberID);
			builder.append(", employeeCode=");
			builder.append(employeeCode);
			builder.append(", employeeName=");
			builder.append(employeeName);
			builder.append(", minTxnAmount=");
			builder.append(minTxnAmount);
			builder.append(", maxTxnAmount=");
			builder.append(maxTxnAmount);
			builder.append(", monthlyLimit=");
			builder.append(monthlyLimit);
			builder.append(", processedRecs=");
			builder.append(processedRecs);
			builder.append(", subscriberTypeID=");
			builder.append(subscriberTypeID);
			builder.append(", subscriberTypeIDDesc=");
			builder.append(subscriberTypeIDDesc);
			builder.append(", languageID=");
			builder.append(languageID);
			builder.append(", languageIDDesc=");
			builder.append(languageIDDesc);
			builder.append(", selfAllow=");
			builder.append(selfAllow);
			builder.append(", loginUserID=");
			builder.append(loginUserID);
			builder.append(", loginUserName=");
			builder.append(loginUserName);
			builder.append(", loginUserCatCode=");
			builder.append(loginUserCatCode);
			builder.append(", loginUserCatName=");
			builder.append(loginUserCatName);
			builder.append(", loginUserType=");
			builder.append(loginUserType);
			builder.append(", geoDomainName=");
			builder.append(geoDomainName);
			builder.append(", geoDomainCode=");
			builder.append(geoDomainCode);
			builder.append(", domainCode=");
			builder.append(domainCode);
			builder.append(", domainName=");
			builder.append(domainName);
			builder.append(", ownerOnly=");
			builder.append(ownerOnly);
			builder.append(", categoryCode=");
			builder.append(categoryCode);
			builder.append(", time=");
			builder.append(time);
			builder.append(", userName=");
			builder.append(userName);
			builder.append(", userID=");
			builder.append(userID);
			builder.append(", ownerID=");
			builder.append(ownerID);
			builder.append(", ownerName=");
			builder.append(ownerName);
			builder.append(", ownerCategoryName=");
			builder.append(ownerCategoryName);
			builder.append(", categoryName=");
			builder.append(categoryName);
			builder.append(", geographicalDomainCodeDesc=");
			builder.append(geographicalDomainCodeDesc);
			builder.append(", geographicalDomainCode=");
			builder.append(geographicalDomainCode);
			builder.append(", autoc2callowed=");
			builder.append(autoc2callowed);
			builder.append(", errorFlag=");
			builder.append(errorFlag);
			builder.append(", sosAllowed=");
			builder.append(sosAllowed);
			builder.append(", sosAllowedAmount=");
			builder.append(sosAllowedAmount);
			builder.append(", sosThresholdLimit=");
			builder.append(sosThresholdLimit);
			builder.append(", lrAllowed=");
			builder.append(lrAllowed);
			builder.append(", lrMaxAmount=");
			builder.append(lrMaxAmount);
			builder.append("]");
			return builder.toString();
		}
	    
	    
		
		
	    
	    


}
