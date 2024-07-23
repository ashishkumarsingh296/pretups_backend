package restassuredapi.api.categorylist;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;


public class CategoryListAPI extends BaseAPI {
	
	String apiPath = "v1/channelUsers/getCategoryListC2C";
	String contentType;
	String categoryCode;
	String networkCode;
	String transferType;
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	
	public CategoryListAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	
	public CategoryListAPI(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecBuilder.addQueryParam("categoryCode", categoryCode);
		requestSpecBuilder.addQueryParam("networkCode", networkCode);
		requestSpecBuilder.addQueryParam("transferType", transferType);
		
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		
		queryParams.put("categoryCode", categoryCode);
		queryParams.put("networkCode", networkCode);
		queryParams.put("transferType", transferType);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String response = apiResponse.asString();
		Log.info(response);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}



}
