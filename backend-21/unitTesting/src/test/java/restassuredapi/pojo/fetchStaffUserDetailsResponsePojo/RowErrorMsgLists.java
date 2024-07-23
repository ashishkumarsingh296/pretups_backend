package restassuredapi.pojo.fetchStaffUserDetailsResponsePojo;

import java.util.List;

public class RowErrorMsgLists {
	
	private List<MasterErrorList1>masterErrorList;
	private String rowName;
	private String rowValue;
	
	public List<MasterErrorList1> getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(List<MasterErrorList1> masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	public List<RowErrorMsgList> getRowErrorMsgList() {
		return rowErrorMsgList;
	}
	public void setRowErrorMsgList(List<RowErrorMsgList> rowErrorMsgList) {
		this.rowErrorMsgList = rowErrorMsgList;
	}
	public String getRowName() {
		return rowName;
	}
	public void setRowName(String rowName) {
		this.rowName = rowName;
	}
	public String getRowValue() {
		return rowValue;
	}
	public void setRowValue(String rowValue) {
		this.rowValue = rowValue;
	}
	private List<RowErrorMsgList>rowErrorMsgList;


}
