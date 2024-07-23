package restassuredapi.pojo.channelAdminBulkDeleteResponsePojo;

import java.util.ArrayList;

public class ErrorMap {

	
	private ArrayList<MasterErrorList> masterErrorList;
	private Object rowErrorMsgLists;
	
	public ArrayList<MasterErrorList> getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(ArrayList<MasterErrorList> masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	public Object getRowErrorMsgLists() {
		return rowErrorMsgLists;
	}
	public void setRowErrorMsgLists(Object rowErrorMsgLists) {
		this.rowErrorMsgLists = rowErrorMsgLists;
	}
	
    
}
