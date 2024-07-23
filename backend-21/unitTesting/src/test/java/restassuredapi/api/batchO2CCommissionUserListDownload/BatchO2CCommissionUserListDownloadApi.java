package restassuredapi.api.batchO2CCommissionUserListDownload;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo.BatchO2CApprovalDetailRequestpojo;
import restassuredapi.pojo.batchO2CCommissionUserListDownloadRequestpojo.BatchO2CCommissionUserListDownloadRequestpojo;

public class BatchO2CCommissionUserListDownloadApi extends BaseAPI{
	
	String apiPath = "/v1/o2c/batchCommissionUserListDownload";
	String contentType;
	String accessToken;

	BatchO2CCommissionUserListDownloadRequestpojo batchO2CCommissionUserListDownloadRequestpojo;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public BatchO2CCommissionUserListDownloadApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void addBodyParam(BatchO2CCommissionUserListDownloadRequestpojo batchO2CCommissionUserListDownloadRequestpojo) {
		this.batchO2CCommissionUserListDownloadRequestpojo = batchO2CCommissionUserListDownloadRequestpojo;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(batchO2CCommissionUserListDownloadRequestpojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String response = apiResponse.asString();
		Log.info(response);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		
	}
	
}
