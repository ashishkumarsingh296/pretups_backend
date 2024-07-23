
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
public class BulkUserAddRptReqVO {

    @JsonProperty("data")
    private BulkUserAddRptReqData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public BulkUserAddRptReqData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(BulkUserAddRptReqData data) {
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
    
    public class BulkUserAddRptReqData {

    	@JsonProperty("batchNo")
        private String batchNo;
    	@JsonProperty("domain")
        private String domain;
    	@JsonProperty("geography")
        private String geography;
        @JsonProperty("fromDate")
        private String fromDate;
        @JsonProperty("toDate")
        private String toDate;
        @JsonProperty("reqTab")
        private String reqTab;

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


		public String getBatchNo() {
			return batchNo;
		}
		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}
		public String getReqTab() {
			return reqTab;
		}
		public void setReqTab(String reqTab) {
			this.reqTab = reqTab;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("O2CTransferdetSearchReqData[ fromDate=" ).append(fromDate);
			sb.append(",toDate=" ).append(toDate);
			sb.append(",domain=" ).append(domain);
			sb.append(",geography=" ).append(geography);
			sb.append("]");
			return sb.toString();
			}
   

    }

}
