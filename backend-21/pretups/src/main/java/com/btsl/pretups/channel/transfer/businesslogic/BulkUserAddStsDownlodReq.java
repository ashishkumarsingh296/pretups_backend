
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
@JsonPropertyOrder({ "data" })
public class BulkUserAddStsDownlodReq {

	@JsonProperty("data")
	private BulkUserDownloadReqData data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("data")
	public BulkUserDownloadReqData getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(BulkUserDownloadReqData data) {
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
		return new ToStringBuilder(this).append("data", data).append("additionalProperties", additionalProperties)
				.toString();
	}

	public class BulkUserDownloadReqData {
		

    	@JsonProperty("batchNo")
        private String batchNo;
        @JsonProperty("fileType")
        private String fileType;
		

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@Override
		public String toString() {
			return "BulkUserDownloadReqData [batchNo=" + batchNo + ", fileType=" + fileType + ","
					+ "fileType=" + fileType + ", additionalProperties=" + additionalProperties + "]";
		}

		public String getBatchNo() {
			return batchNo;
		}

		public void setBatchNo(String batchNo) {
			this.batchNo = batchNo;
		}

		
		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

	

		public Map<String, Object> getAdditionalProperties() {
			return additionalProperties;
		}

		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
		}

		

		
	

	}

}
