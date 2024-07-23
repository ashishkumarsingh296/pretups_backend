package restassuredapi.pojo.c2cbulkapprovallistresponsepojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;



public class RowErrorMsgLists {
    
	@JsonProperty("rowValue")
    private String rowValue;

	@JsonProperty("rowName")
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

	@JsonProperty("masterErrorList")
	private List<MasterErrorList> masterErrorList = null;
	
	@JsonProperty("rowErrorMsgList")
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
