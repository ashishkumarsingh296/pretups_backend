package restassuredapi.api.o2cbulktrfwidprocessapprovalapi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2cbulktrfwidappprocess.O2CBulkTrfWidProcessApproval;

public class O2CBulkTrfWidProcessApprovalApi extends BaseAPI{

	String apiPath = "/v1/o2c/o2CBatchProcess";
	String contentType;
	String accessToken;
	EncoderConfig encoderconfig = new EncoderConfig();
	HashMap<String, String> queryParams = new HashMap<String, String>();
	public O2CBulkTrfWidProcessApprovalApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	O2CBulkTrfWidProcessApproval batchO2CApprovalDetailRequestpojo = new O2CBulkTrfWidProcessApproval();
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void addBodyParam(O2CBulkTrfWidProcessApproval batchO2CApprovalDetailRequestpojo) {
		this.batchO2CApprovalDetailRequestpojo = batchO2CApprovalDetailRequestpojo;
	}
	
	public void addQueryParam(String batchID,String requestType,String serviceType) {
		queryParams.put("batchID",batchID);
		queryParams.put("requestType",requestType);
		queryParams.put("serviceType",serviceType);
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(batchO2CApprovalDetailRequestpojo);
		requestSpecBuilder.addQueryParam("batchID", queryParams.get("batchID"));
		requestSpecBuilder.addQueryParam("requestType", queryParams.get("requestType"));
		requestSpecBuilder.addQueryParam("serviceType", queryParams.get("serviceType"));
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();

	}
}
