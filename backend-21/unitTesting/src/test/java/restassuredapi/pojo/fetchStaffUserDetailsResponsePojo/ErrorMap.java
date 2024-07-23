package restassuredapi.pojo.fetchStaffUserDetailsResponsePojo;

import java.util.List;

public class ErrorMap {

	private List<MasterErrorList> masterErrorList;
	private List<RowErrorMsgLists> rowErrorMsgLists;
	
	public List<MasterErrorList> getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(List<MasterErrorList> masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	public List<RowErrorMsgLists> getRowErrorMsgLists() {
		return rowErrorMsgLists;
	}
	public void setRowErrorMsgLists(List<RowErrorMsgLists> rowErrorMsgLists) {
		this.rowErrorMsgLists = rowErrorMsgLists;
	}

}
