
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
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
public class PassbookSearchOthersRequestVO {

    @JsonProperty("data")
    private PassbookSearchOtherDataReq data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public PassbookSearchOtherDataReq getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(PassbookSearchOtherDataReq data) {
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
    
    public class PassbookSearchOtherDataReq {

        
        @JsonProperty("domain")
        private String domain;
        @JsonProperty("category")
        private String category;
        @JsonProperty("geography")
        private String geography;
        @JsonProperty("user")
        private String user;
        @JsonProperty("product")
        private String product;
        @JsonProperty("fromDate")
        private String fromDate;
        @JsonProperty("toDate")
        private String toDate;
        @JsonProperty("fileType")
        private String fileType;
        @JsonProperty("dispHeaderColumnList")
		private List<DispHeaderColumn> dispHeaderColumnList;
        
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public String getDomain() {
			return domain;
		}


		public void setDomain(String domain) {
			this.domain = domain;
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


		public String getUser() {
			return user;
		}


		public void setUser(String user) {
			this.user = user;
		}


		public String getProduct() {
			return product;
		}


		public void setProduct(String product) {
			this.product = product;
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


		public Map<String, Object> getAdditionalProperties() {
			return additionalProperties;
		}


		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
		}

		@Override
		public String toString() {
			return "PassbookSearchOthersRequestVO [inputDateRange=" + fromDate + ", toDate=" + toDate  + "productCode=" + product + ", additionalProperties=" + additionalProperties + "]";
		}


		public List<DispHeaderColumn> getDispHeaderColumnList() {
			return dispHeaderColumnList;
		}


		public void setDispHeaderColumnList(List<DispHeaderColumn> dispHeaderColumnList) {
			this.dispHeaderColumnList = dispHeaderColumnList;
		}


		public String getFileType() {
			return fileType;
		}


		public void setFileType(String fileType) {
			this.fileType = fileType;
		}


       

    }

}
