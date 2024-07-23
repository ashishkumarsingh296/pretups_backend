package restassuredapi.pojo.c2ctransferstockresponsepojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class RowErrorMsgLists {

    private String rowValue;


	private String rowName;
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


	private List<MasterErrorList> masterErrorList = null;
    private List<RowErrorMsgList> rowErrorMsgList = null;


    public List<MasterErrorList> getMasterErrorList() {
        return masterErrorList;
    }

    public void setMasterErrorList(List<MasterErrorList> masterErrorList) {
        this.masterErrorList = masterErrorList;
    }

    public List<RowErrorMsgList> getRowErrorMsgList() {
        return rowErrorMsgList;
    }

    public void setRowErrorMsgList(List<RowErrorMsgList> rowErrorMsgList) {
        this.rowErrorMsgList = rowErrorMsgList;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("rowValue = ").append(rowValue)
        		.append("rowName").append( rowName).append("masterErrorList = ").append(masterErrorList)
        		.append("rowErrorMsgList").append( rowErrorMsgList)
        		).toString();
    }

}
