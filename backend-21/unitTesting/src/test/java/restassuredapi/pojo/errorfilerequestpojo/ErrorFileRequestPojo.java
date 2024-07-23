package restassuredapi.pojo.errorfilerequestpojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"file",
"filetype",
"rowErrorMsgLists"
})
public class ErrorFileRequestPojo {

@JsonProperty("file")
private String file;
@JsonProperty("filetype")
private String filetype;
@JsonProperty("rowErrorMsgLists")
private List<RowErrorMsgList> rowErrorMsgLists = null;
@JsonProperty("partialError")
private boolean partialError;
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
public List<RowErrorMsgList> getRowErrorMsgLists() {
return rowErrorMsgLists;
}

@JsonProperty("rowErrorMsgLists")
public void setRowErrorMsgLists(List<RowErrorMsgList> rowErrorMsgLists) {
this.rowErrorMsgLists = rowErrorMsgLists;
}

public boolean getPartialError() {
	return partialError;
}

public void setPartialError(boolean partialError) {
	this.partialError = partialError;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}


}

