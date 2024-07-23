package restassuredapi.pojo.downloadOfflineReportResponsePojo;

import java.util.List;

public class RowErrorMsgLists2 {
	
	private List<MasterErrorList3>masterErrorList;
	private String rowName;
	private String rowValue;
	private List<RowErrorMsgList1>rowErrorMsgList;
	
	public List<MasterErrorList3> getMasterErrorList() {
		return masterErrorList;
	}
	public void setMasterErrorList(List<MasterErrorList3> masterErrorList) {
		this.masterErrorList = masterErrorList;
	}
	public List<RowErrorMsgList1> getRowErrorMsgList() {
		return rowErrorMsgList;
	}
	public void setRowErrorMsgList(List<RowErrorMsgList1> rowErrorMsgList) {
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
	
	

}
