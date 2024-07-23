package com.btsl.pretups.transfer.businesslogic.errorfilerequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.RowErrorMsgLists;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"file",
"filetype",
"rowErrorMsgLists"
})
public class ErrorFileRequestVO {

@JsonProperty("file")
private String file;
@JsonProperty("filetype")
private String filetype;
@JsonProperty("rowErrorMsgLists")
private List<RowErrorMsgLists> rowErrorMsgLists = null;
@JsonProperty("partialFailure")
private boolean partialFailure;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("file")
public String getFile() {
return file;
}

@JsonProperty("file")
public void setFile(String file) {
this.file = file;
}

@JsonProperty("filetype")
public String getFiletype() {
return filetype;
}

@JsonProperty("filetype")
public void setFiletype(String filetype) {
this.filetype = filetype;
}

@JsonProperty("rowErrorMsgLists")
public List<RowErrorMsgLists> getRowErrorMsgLists() {
return rowErrorMsgLists;
}

@JsonProperty("rowErrorMsgLists")
public void setRowErrorMsgLists(List<RowErrorMsgLists> rowErrorMsgLists) {
this.rowErrorMsgLists = rowErrorMsgLists;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}


public boolean getPartialFailure() {
	return partialFailure;
}

public void setPartialFailure(boolean partialFailure) {
	this.partialFailure = partialFailure;
}

@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("ErrorFileRequestVO [file=").append(file).append(", filetype=").append(filetype)
			.append(", rowErrorMsgLists=").append(rowErrorMsgLists).append(", partialFailure=").append(partialFailure)
			.append(", additionalProperties=").append(additionalProperties).append("]");
	return builder.toString();
}



}

