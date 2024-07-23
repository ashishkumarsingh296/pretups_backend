
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;
import java.util.Locale;

public class LowThreshHoldReportDTO {

        private String userID;
	    private String fromDate;
        private String toDate;
        private String extnwcode;
        private String category;
    	private String geography;
    	private String threshhold;
    	private String domain;
    	private String msisdn;
    	private Locale locale;
    	
    	
    	
		public String getFromDate() {
			return fromDate;
		}
		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}
		public String getToDate() {
			return toDate;
		}
		public void setToDate(String toDate) {
			this.toDate = toDate;
		}
		public String getExtnwcode() {
			return extnwcode;
		}
		public void setExtnwcode(String extnwcode) {
			this.extnwcode = extnwcode;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getGeography() {
			return geography;
		}
		public void setGeography(String geography) {
			this.geography = geography;
		}
		public String getThreshhold() {
			return threshhold;
		}
		public void setThreshhold(String threshhold) {
			this.threshhold = threshhold;
		}
		public String getDomain() {
			return domain;
		}
		public void setDomain(String domain) {
			this.domain = domain;
		}
		public String getUserID() {
			return userID;
		}
		public void setUserID(String userID) {
			this.userID = userID;
		}
		public String getMsisdn() {
			return msisdn;
		}
		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}
		public Locale getLocale() {
			return locale;
		}
		public void setLocale(Locale locale) {
			this.locale = locale;
		}


}
