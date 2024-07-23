package restassuredapi.pojo;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;


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
