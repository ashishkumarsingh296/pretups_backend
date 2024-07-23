package com.restapi.o2c.service;


import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CBatchApprovalDetailsRequestVO {

	@JsonProperty("data")
	private O2CBatchApprovalDetails data = null;

	public O2CBatchApprovalDetails getData() {
		return data;
	}

	public void setData(O2CBatchApprovalDetails data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CBatchApprovalDetailsRequestVO [data=").append(data).append("]");
		return builder.toString();
	}
	
}

	class O2CBatchApprovalDetails{
		@JsonProperty("approvalType")
		private String approvalType;
		@JsonProperty("batchId")
		private String batchId;
		@JsonProperty("approvalLevel")
		private String approvalLevel;
		@JsonProperty("approvalSubType")
		private String approvalSubType;
		
		
		/**
		 * @return the approvalSubType
		 */
		public String getApprovalSubType() {
			return approvalSubType;
		}



		/**
		 * @param approvalSubType the approvalSubType to set
		 */
		public void setApprovalSubType(String approvalSubType) {
			this.approvalSubType = approvalSubType;
		}



		public String getApprovalType() {
			return approvalType;
		}



		public void setApprovalType(String approvalType) {
			this.approvalType = approvalType;
		}



		public String getBatchId() {
			return batchId;
		}



		public void setBatchId(String batchId) {
			this.batchId = batchId;
		}



		public String getApprovalLevel() {
			return approvalLevel;
		}



		public void setApprovalLevel(String approvalLevel) {
			this.approvalLevel = approvalLevel;
		}



		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("O2CBatchApprovalDetails [approvalType=").append(approvalType).append(", batchId=")
					.append(batchId).append(", approvalLevel=").append(approvalLevel).append(", approvalSubType=")
					.append(approvalSubType).append("]");
			return builder.toString();
		}



		
		
	}
