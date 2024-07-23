package restassuredapi.pojo.regexresponsepojo;


import java.util.List;


public class ErrorMap {
 
    private List<MasterErrorList> masterErrorList = null;
    private List<RowErrorMsgLists> rowErrorMsgLists = null;

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
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("rowErrorMsgList = ").append(rowErrorMsgLists)
        		.append("masterErrorList").append( masterErrorList)
        		).toString();
    }


   

}
