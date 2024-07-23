package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

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
public class C2STotalTransactionCountRequestVO {
	
	   @JsonProperty("data")
	   private TotalTransactionCountVO data;
	   @JsonIgnore
	   private Map<String, Object> additionalProperties = new HashMap<String, Object>();
 
	   
	   @JsonAnyGetter
	    public Map<String, Object> getAdditionalProperties() {
	        return this.additionalProperties;
	    }

	    @JsonAnySetter
	    public void setAdditionalProperty(String name, Object value) {
	        this.additionalProperties.put(name, value);
	    }

	   
	   public TotalTransactionCountVO getData() {
			return data;
		}

		public void setData(TotalTransactionCountVO data) {
			this.data = data;
		}
		


	@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("C2STotalTransactionCountRequestVO [data=").append(data).append(", additionalProperties=")
					.append(additionalProperties).append("]");
			return builder.toString();
		}



	public class TotalTransactionCountVO{
		    @JsonProperty("fromDate")
	        private String fromDate;
	        @JsonProperty("toDate")
	        private String toDate;
	        @JsonProperty("extnwcode")
	        private String extnwcode;
	        @JsonProperty("language1")
	        private String language1;
	        @JsonProperty("language2")
	        private String language2;
	        @JsonIgnore
	        private Map<String, Object> additionalProperties = new HashMap<String, Object>();
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
			public String getLanguage1() {
				return language1;
			}
			public void setLanguage1(String language1) {
				this.language1 = language1;
			}
			public String getLanguage2() {
				return language2;
			}
			public void setLanguage2(String language2) {
				this.language2 = language2;
			}
			public Map<String, Object> getAdditionalProperties() {
				return additionalProperties;
			}
			public void setAdditionalProperties(Map<String, Object> additionalProperties) {
				this.additionalProperties = additionalProperties;
			}
			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder();
				builder.append("TotalTransactionCountVO [fromDate=").append(fromDate).append(", toDate=").append(toDate)
						.append(", extnwcode=").append(extnwcode).append(", language1=").append(language1)
						.append(", language2=").append(language2).append(", additionalProperties=")
						.append(additionalProperties).append("]");
				return builder.toString();
			}

	   }

}
