
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
public class O2CTransferAckDownloadReq {

	@JsonProperty("data")
	private O2CTransferAckDownloadReqData data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("data")
	public O2CTransferAckDownloadReqData getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(O2CTransferAckDownloadReqData data) {
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

	public class O2CTransferAckDownloadReqData {

		
		@JsonProperty("transactionID")
		private String transactionID;
		@JsonProperty("fileType")
		private String fileType;
		@JsonProperty("dispHeaderColumnList")
		private List<DispHeaderColumn> dispHeaderColumnList;
		
		@JsonProperty("distributionType")
		private String distributionType;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@Override
		public String toString() {
			return "PassbookDownloadReqData [transactionID=" + transactionID +  
					"fileType=" + fileType + ", additionalProperties=" + additionalProperties + "]";
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

		public Map<String, Object> getAdditionalProperties() {
			return additionalProperties;
		}

		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
		}


		public String getTransactionID() {
			return transactionID;
		}


		public void setTransactionID(String transactionID) {
			this.transactionID = transactionID;
		}


		public String getDistributionType() {
			return distributionType;
		}


		public void setDistributionType(String distributionType) {
			this.distributionType = distributionType;
		}

        


	
	

	}

}
