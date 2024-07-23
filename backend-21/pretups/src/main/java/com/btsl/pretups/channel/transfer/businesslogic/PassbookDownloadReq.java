
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
public class PassbookDownloadReq {

    @JsonProperty("data")
    private PassbookDownloadReqData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("data")
    public PassbookDownloadReqData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(PassbookDownloadReqData data) {
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
    
    public class PassbookDownloadReqData {

        @JsonProperty("fromDate")
        private String fromDate;
        @JsonProperty("toDate")
        private String toDate;
        @JsonProperty("extnwcode")
        private String extnwcode;
        @JsonProperty("productCode")
        private String productCode;
    	@JsonProperty("fileType")
    	private String fileType;
    	@JsonProperty("dispHeaderColumnList")
    	private List<DispHeaderColumn> dispHeaderColumnList;
 
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
        
        @JsonProperty("productCode")
        public String getProductCode() {
			return productCode;
		}

        @JsonProperty("productCode")
		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}

      
	

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public List<DispHeaderColumn> getDispHeaderColumnList() {
			return dispHeaderColumnList;
		}

		public void setDispHeaderColumnList(List<DispHeaderColumn> dispHeaderColumnList) {
			this.dispHeaderColumnList = dispHeaderColumnList;
		}


		@Override
		public String toString() {
			return "PassbookDownloadReqData [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode + ",productCode=" + productCode + "fileType=" + fileType + ", additionalProperties=" + additionalProperties + "]";
		}
       

    }

}
