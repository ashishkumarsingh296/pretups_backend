package restassuredapi.api;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

public class ChUserPaymentMethodAndRangeAPI extends BaseAPI{

	String apiPath = "v1/o2c/chUserPaymentTypes&Ranges";
	String contentType;
	String channelUserId;
	String transferType;
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public ChUserPaymentMethodAndRangeAPI(String baseURI, String accessToken) {
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
	
	public String getchannelUserId() {
		return channelUserId;
	}

	public void setchannelUserId(String channelUserId) {
		this.channelUserId = channelUserId;
	}
	
	public String gettransferTyped() {
		return transferType;
	}

	public void settransferType(String transferType) {
		this.transferType = transferType;
	}
	

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecBuilder.addQueryParam("channelUserId", channelUserId);
		requestSpecBuilder.addQueryParam("transferType", transferType);
		
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		
		queryParams.put("channelUserId", channelUserId);
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
