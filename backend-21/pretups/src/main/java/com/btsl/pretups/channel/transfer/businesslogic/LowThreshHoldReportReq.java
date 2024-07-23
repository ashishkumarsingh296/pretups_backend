
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data"
})
public class LowThreshHoldReportReq {

    @JsonProperty("data")
    private LowThreshHoldRepoReqData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public LowThreshHoldRepoReqData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(LowThreshHoldRepoReqData data) {
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
    
    public class LowThreshHoldRepoReqData {

        @JsonProperty("fromDate")
        private String fromDate;
        @JsonProperty("toDate")
        private String toDate;
        @JsonProperty("extnwcode")
        private String extnwcode;
        @JsonProperty("category")
        private String category;
    	@JsonProperty("geography")
    	private String geography;
    	@JsonProperty("threshhold")
    	private String threshhold;
       	@JsonProperty("domain")
    	private String domain;	
        
        @JsonProperty("fromDate")
        public String getFromDate() {
            return fromDate;
        }

        @JsonProperty("fromDate")
        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        @JsonProperty("toDate")
        public String getToDate() {
            return toDate;
        }

        @JsonProperty("toDate")
        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        @JsonProperty("extnwcode")
        public String getExtnwcode() {
            return extnwcode;
        }

        @JsonProperty("extnwcode")
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
		
		@Override
		public String toString() {
			return "LowThreshHoldRepoReqData [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode + ",categoryCode=" + category + ",domain= " + domain + ", additionalProperties=" + additionalProperties + "]";
		}

       

    }

}
