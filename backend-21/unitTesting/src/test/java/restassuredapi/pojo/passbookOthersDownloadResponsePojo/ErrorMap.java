package restassuredapi.pojo.passbookOthersDownloadResponsePojo;

import java.util.List;

public class ErrorMap {

	private List<MasterErrorList> masterErrorList;
	private String rowErrorMsgLists = null;
	
	public String getRowErrorMsgLists() {
		return rowErrorMsgLists;
	}
	public void setRowErrorMsgLists(String rowErrorMsgLists) {
		this.rowErrorMsgLists = rowErrorMsgLists;
	}
	public List<MasterErrorList> getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(List<MasterErrorList> masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	
}
