package restassuredapi.pojo.c2sBulkReversalResponsePojo;

import java.util.ArrayList;

public final class RowErrorMsgList {

	private String rowValue;
	private String rowName;
	private ArrayList<MasterErrorList> masterErrorList;
	private Object rowErrorMsgList;
	
	public String getRowValue() {
		return rowValue;
	}
	public void setRowValue(String rowValue) {
		this.rowValue = rowValue;
	}
	public String getRowName() {
		return rowName;
	}
	public void setRowName(String rowName) {
		this.rowName = rowName;
	}
	public ArrayList<MasterErrorList> getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(ArrayList<MasterErrorList> masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	public Object getRowErrorMsgList() {
		return rowErrorMsgList;
	}
	public void setRowErrorMsgList(Object rowErrorMsgList) {
		this.rowErrorMsgList = rowErrorMsgList;
	}
}
