package com.btsl.common;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.pretups.common.SchemaConstants;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ToStringBuilder;


public class ErrorMap {
    @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(description = "Master Error List", additionalProperties = Schema.AdditionalPropertiesValue.FALSE))
    private List<MasterErrorList> masterErrorList = null;
    @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(description = "Row Error Message List", additionalProperties = Schema.AdditionalPropertiesValue.FALSE))
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
