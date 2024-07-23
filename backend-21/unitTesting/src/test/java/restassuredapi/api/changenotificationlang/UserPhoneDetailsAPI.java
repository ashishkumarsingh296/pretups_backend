package restassuredapi.api.changenotificationlang;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class UserPhoneDetailsAPI extends BaseAPI{
	
	String apiPath = "/v1/changeNotificationLang/loadUserPhoneList";

	String contentType;
	String accessToken;

	String searchBy;
	String categoryCode;
	String msisdn;
	String userName;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public UserPhoneDetailsAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("categoryCode", categoryCode);
		requestSpecBuilder.addQueryParam("userName", userName);
		requestSpecBuilder.addQueryParam("msisdn", msisdn);
		requestSpecBuilder.addQueryParam("searchBy", searchBy);
		//requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		
		queryParams.put("categoryCode", categoryCode);	
		queryParams.put("userName", userName);
		queryParams.put("msisdn", msisdn);
		queryParams.put("searchBy", searchBy);
		
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
