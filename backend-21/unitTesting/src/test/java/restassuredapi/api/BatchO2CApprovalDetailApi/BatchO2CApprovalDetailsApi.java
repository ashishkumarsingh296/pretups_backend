package restassuredapi.api.BatchO2CApprovalDetailApi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo.BatchO2CApprovalDetailRequestpojo;

public class BatchO2CApprovalDetailsApi extends BaseAPI {

	String apiPath = "/v1/o2c/getBulkApprDetail";
	String contentType;
	String accessToken;
	BatchO2CApprovalDetailRequestpojo batchO2CApprovalDetailRequestpojo = new BatchO2CApprovalDetailRequestpojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public BatchO2CApprovalDetailsApi (String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(BatchO2CApprovalDetailRequestpojo batchO2CApprovalDetailRequestpojo) {
		this.batchO2CApprovalDetailRequestpojo = batchO2CApprovalDetailRequestpojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(batchO2CApprovalDetailRequestpojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
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
