
package com.btsl.pretups.channel.transfer.businesslogic;


import java.util.Locale;

/**
 * 
 * @author Subesh KCV
 *This class is only for DTO and extended DTO classes , not to be used for Requests comming from rest controller class
 * As per Sonar , we should not use requests comming from controller to service and dao Classes, Instead transform origin request to DTO classes 
 */

public class BaseRequestdata {
	
	
	protected String fromDate;
	protected String toDate;
	protected String extnwcode;
	protected String productCode;
	protected String msisdn;
	protected Locale locale;
	protected String userId;
	
	
	


		@Override
		public String toString() {
			return "BaseReqdata [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode + ",productCode=" + productCode +  "]";
		}


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


		public String getProductCode() {
			return productCode;
		}


		public void setProductCode(String productCode) {
			this.productCode = productCode;
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


		public String getUserId() {
			return userId;
		}


		public void setUserId(String userId) {
			this.userId = userId;
		}


	      

    

}
