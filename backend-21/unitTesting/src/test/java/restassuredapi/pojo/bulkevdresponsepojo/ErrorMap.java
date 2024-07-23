package restassuredapi.pojo.bulkevdresponsepojo;

import java.util.ArrayList;

public class ErrorMap {
	
	private Object masterErrorList;
	private ArrayList<RowErrorMsgList> rowErrorMsgLists;
	
	public Object getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(Object masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	public ArrayList<RowErrorMsgList> getRowErrorMsgLists() {
		return rowErrorMsgLists;
	}
	public void setRowErrorMsgLists(ArrayList<RowErrorMsgList> rowErrorMsgLists) {
		this.rowErrorMsgLists = rowErrorMsgLists;
	}


}
