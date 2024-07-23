
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
public class PassbookViewInfoRequestVO {

    @JsonProperty("data")
    private PassbookDetailsViewData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public PassbookDetailsViewData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(PassbookDetailsViewData data) {
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
    
    public class PassbookDetailsViewData {

        @JsonProperty("fromDate")
        private String fromDate;
        @JsonProperty("toDate")
        private String toDate;
        @JsonProperty("extnwcode")
        private String extnwcode;
 
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
			return "PassbookDetailsViewData [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode
					+ ", additionalProperties=" + additionalProperties + "]";
		}

       

    }

}
