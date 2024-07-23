package restassuredapi.api.O2CDirectApproval;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;

import com.utils.Log;

public class O2CDirectApprovalAPI extends BaseAPI{
	String apiPath="C2SReceiver";
	String contentType;
	public String getRequestGateCode() {
		return requestGateCode;
	}

	public void setRequestGateCode(String requestGateCode) {
		this.requestGateCode = requestGateCode;
	}

	public String getRequestGateType() {
		return requestGateType;
	}

	public void setRequestGateType(String requestGateType) {
		this.requestGateType = requestGateType;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}


	String requestGateCode;
	String requestGateType;
	String login;
	String password;
	String sourceType;
	String servicePort;
	HashMap<String, String> bodyParam = new HashMap<String, String>();
	


	


	public HashMap<String, String> getBodyParam() {
		return bodyParam;
	}

	public void setBodyParam(HashMap<String, String> bodyParam) {
		this.bodyParam = bodyParam;
	}


	EncoderConfig encoderconfig = new EncoderConfig();
	public O2CDirectApprovalAPI(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	 
	

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
		String s=apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("REQUEST_GATEWAY_CODE", requestGateCode);
		requestSpecBuilder.addQueryParam("REQUEST_GATEWAY_TYPE", requestGateType);
		requestSpecBuilder.addQueryParam("LOGIN", login);
		requestSpecBuilder.addQueryParam("PASSWORD", password);
		requestSpecBuilder.addQueryParam("SOURCE_TYPE", sourceType);
		requestSpecBuilder.addQueryParam("SERVICE_PORT", servicePort);
		requestSpecBuilder.setBody(com.google.common.base.Joiner.on("&").withKeyValueSeparator("=").join(bodyParam));
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("REQUEST_GATEWAY_CODE", requestGateCode);
		queryParams.put("REQUEST_GATEWAY_TYPE", requestGateType);
		queryParams.put("LOGIN", login);
		queryParams.put("PASSWORD", password);
		queryParams.put("SOURCE_TYPE", sourceType);
		queryParams.put("SERVICE_PORT", servicePort);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}


	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}


}
