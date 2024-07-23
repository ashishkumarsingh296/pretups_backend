
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
public class C2STransferCommissionReqVO {

    @JsonProperty("data")
    private C2STransferCommissionReqData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public C2STransferCommissionReqData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(C2STransferCommissionReqData data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("data", data).append("additionalProperties", additionalProperties).toString();
    }
    
    public class C2STransferCommissionReqData {
        @JsonProperty("reportDate")
        private String reportDate;
        @JsonProperty("allowedTimeFrom")
        private String allowedTimeFrom;
        @JsonProperty("allowedTimeTo")
        private String allowedTimeTo;
        @JsonProperty("extnwcode")
        private String extnwcode;
        @JsonProperty("categoryCode")
        private String categoryCode;
        @JsonProperty("domain")
        private String domain;
        @JsonProperty("geography")
        private String geography;
        @JsonProperty("service")
        private String service;
        @JsonProperty("transStatus")
        private String transStatus;
        @JsonProperty("user")
        private String user;
        @JsonProperty("tabReq")
        private String tabReq;
        @JsonProperty("includeStaffUser")
        private String includeStaffUser;
        @JsonProperty("mobileNumber")
        private String mobileNumber; // Receiver Mobile Number
        
 
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();


		public Map<String, Object> getAdditionalProperties() {
			return additionalProperties;
		}

		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
		}



	

	

		public String getExtnwcode() {
			return extnwcode;
		}

		public void setExtnwcode(String extnwcode) {
			this.extnwcode = extnwcode;
		}

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getGeography() {
			return geography;
		}

		public void setGeography(String geography) {
			this.geography = geography;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}

		public String getTransStatus() {
			return transStatus;
		}

		public void setTransStatus(String transStatus) {
			this.transStatus = transStatus;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getTabReq() {
			return tabReq;
		}

		public void setTabReq(String tabReq) {
			this.tabReq = tabReq;
		}

		public String getIncludeStaffUser() {
			return includeStaffUser;
		}

		public void setIncludeStaffUser(String includeStaffUser) {
			this.includeStaffUser = includeStaffUser;
		}
		
		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		public String getReportDate() {
			return reportDate;
		}

		public void setReportDate(String reportDate) {
			this.reportDate = reportDate;
		}

		public String getAllowedTimeFrom() {
			return allowedTimeFrom;
		}

		public void setAllowedTimeFrom(String allowedTimeFrom) {
			this.allowedTimeFrom = allowedTimeFrom;
		}

		public String getAllowedTimeTo() {
			return allowedTimeTo;
		}

		public void setAllowedTimeTo(String allowedTimeTo) {
			this.allowedTimeTo = allowedTimeTo;
		}

		@Override
		public String toString() {
			return "C2STransferCommissionReqData [ReportDate=" + reportDate + ", extnwcode=" + extnwcode + ",categoryCode=" + categoryCode +  ",domain =" + domain  + ", additionalProperties=" + additionalProperties + "]";
			}
   

    }

}
