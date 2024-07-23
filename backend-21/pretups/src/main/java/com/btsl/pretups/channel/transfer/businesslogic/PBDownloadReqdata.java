
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PBDownloadReqdata extends BaseRequestdata {

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


		@Override
		public String toString() {
			return "PassbookDownloadReqData [tnwcode=" + extnwcode + ",productCode=" + productCode + "fileType=" + fileType + "]";
		}
       

    

}
