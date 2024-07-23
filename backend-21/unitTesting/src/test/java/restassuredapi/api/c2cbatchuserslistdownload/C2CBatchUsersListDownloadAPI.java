package restassuredapi.api.c2cbatchuserslistdownload;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cbatchuserslistdownloadresponsepojo.C2CBatchUsersListDownloadResponsePojo;

import com.utils.Log;

public class C2CBatchUsersListDownloadAPI  extends BaseAPI  {
	
	String apiPath = "/v1/c2cFileServices/downloadUsersList";

	String contentType;
	
	String category;
	String operationType;
	
	
	String accessToken;
	
	C2CBatchUsersListDownloadResponsePojo c2CBatchUsersListDownloadResponsePojo = new C2CBatchUsersListDownloadResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	

	public C2CBatchUsersListDownloadAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	
	

	public C2CBatchUsersListDownloadResponsePojo getC2CBatchUsersListDownloadResponsePojo() {
		return c2CBatchUsersListDownloadResponsePojo;
	}

	public void setC2CBatchUsersListDownloadResponsePojo(
			C2CBatchUsersListDownloadResponsePojo c2cBatchUsersListDownloadResponsePojo) {
		c2CBatchUsersListDownloadResponsePojo = c2cBatchUsersListDownloadResponsePojo;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}
	
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);

		requestSpecBuilder.addQueryParam("category", category);
		requestSpecBuilder.addQueryParam("operationType", operationType);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("category", category);
		queryParams.put("operationType", operationType);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String s = apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}






}
