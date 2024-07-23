
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonDownloadReqDTO extends BaseRequestdata {
	protected String fileType;
	protected boolean offline;
	protected String fileName;
	protected String offlineReportTaskID;
	protected List<DispHeaderColumn> dispHeaderColumnList;
 
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


		public boolean isOffline() {
			return offline;
		}

		public void setOffline(boolean offline) {
			this.offline = offline;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getOfflineReportTaskID() {
			return offlineReportTaskID;
		}

		public void setOfflineReportTaskID(String offlineReportTaskID) {
			this.offlineReportTaskID = offlineReportTaskID;
		}

		@Override
		public String toString() {
			return "CommmonDownloadReq [  fileType=" + fileType + "]";
		}


    

}
