package restassuredapi.pojo.userhierarchyrequestpojo;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * User Hierarchy Request Wrapper class
 * @author akhilesh.mittal1
 *
 */


@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen")
public class UserHierarchyRequestParentVO {

	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	@JsonProperty("data")
	UserHierarchyRequestMessage data;
	
	@JsonProperty("sourceType")
	private String sourceType;
	
	@JsonProperty("reqGatewayType")
	private String reqGatewayType;
	
	@JsonProperty("reqGatewayPassword")
	private String reqGatewayPassword;
	
	@JsonProperty("servicePort")
	private String servicePort;
	
	@JsonProperty("reqGatewayCode")
	private String reqGatewayCode;

	
	
	@JsonProperty("reqGatewayLoginId")
	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	
	@JsonProperty("data")
	public UserHierarchyRequestMessage getData() {
		return data;
	}

	public void setData(UserHierarchyRequestMessage data) {
		this.data = data;
	}

	
	@JsonProperty("sourceType")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	@JsonProperty("reqGatewayType")
	public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	
	@JsonProperty("reqGatewayPassword")
	public String getReqGatewayPassword() {
		return reqGatewayPassword;
	}

	public void setReqGatewayPassword(String reqGatewayPassword) {
		this.reqGatewayPassword = reqGatewayPassword;
	}

	
	@JsonProperty("servicePort")
	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	
	@JsonProperty("reqGatewayCode")
	public String getReqGatewayCode() {
		return reqGatewayCode;
	}

	public void setReqGatewayCode(String reqGatewayCode) {
		this.reqGatewayCode = reqGatewayCode;
	}

	@Override
	public String toString() {
		return "UserHierarchyRequestParentVO [reqGatewayLoginId=" + reqGatewayLoginId + ", data=" + data
				+ ", sourceType=" + sourceType + ", reqGatewayType=" + reqGatewayType + ", reqGatewayPassword="
				+ reqGatewayPassword + ", servicePort=" + servicePort + ", reqGatewayCode=" + reqGatewayCode + "]";
	}

}
