package restassuredapi.pojo.approvalSuspendReqpojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "loginId",
    "remarks",
    "requestType"
})
public class ApprovalSuspendReqpojo {
	@JsonProperty("action")
	public String action;
	@JsonProperty("loginId")
    public String loginId;
	@JsonProperty("remarks")
    public String remarks;
	@JsonProperty("requestType")
    public String requestType;
	
	
	@JsonProperty("action")
	public String getAction() {
		return action;
	}
	@JsonProperty("action")
	public void setAction(String action) {
		this.action = action;
	}
	@JsonProperty("loginId")
	public String getLoginId() {
		return loginId;
	}
	@JsonProperty("loginId")
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	@JsonProperty("remarks")
	public String getRemarks() {
		return remarks;
	}
	@JsonProperty("remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@JsonProperty("requestType")
	public String getRequestType() {
		return requestType;
		
	}
	@JsonProperty("requestType")
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
    
}
