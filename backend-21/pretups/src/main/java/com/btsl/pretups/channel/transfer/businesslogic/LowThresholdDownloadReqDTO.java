
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LowThresholdDownloadReqDTO extends BaseRequestdata {

	private String domain;
	private String categoryCode;
	private String threshold;
	private String geography;
	private String fileType;
	private List<DispHeaderColumn> dispHeaderColumnList;
 
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


		
		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		public String getThreshold() {
			return threshold;
		}

		public void setThreshold(String threshold) {
			this.threshold = threshold;
		}

		public String getGeography() {
			return geography;
		}

		public void setGeography(String geography) {
			this.geography = geography;
		}
       
		@Override
		public String toString() {
			return "LowThresholdDownloadReqDTO [tnwcode=" + extnwcode + ",productCode=" + productCode + "fileType=" + fileType + "]";
		}

    

}
