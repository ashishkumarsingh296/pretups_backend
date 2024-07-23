package restassuredapi.api.userpinmanagement;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

//import com.github.dzieciou.testing.curl.CurlLoggingRestAssuredConfigFactory;
import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userpinmanagementrequestpojo.UserPinManagementRequestPojo;
public class UserPinManagementAPI extends BaseAPI {
	String apiPath = "/v1/userServices/pinManagement";
	String contentType;
	String resetPin;
	
	String accessToken;
	

	UserPinManagementRequestPojo userPinManagementRequestPojo = new UserPinManagementRequestPojo();

	
	EncoderConfig encoderconfig = new EncoderConfig();

	public UserPinManagementAPI(String baseURI,  String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setResetPin(String resetPin) {
		this.resetPin = resetPin;
	}
	public String getResetPin() {
		return resetPin;
	}

	
	public void addBodyParam(UserPinManagementRequestPojo userPinManagementRequestPojo) {
		this.userPinManagementRequestPojo = userPinManagementRequestPojo;
	}

	@Override
	protected void createRequest() {
//	config = CurlLoggingRestAssuredConfigFactory.createConfig();  
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(userPinManagementRequestPojo);
		requestSpecBuilder.addQueryParam("resetPin", resetPin);
		requestSpecification = requestSpecBuilder.build();
	
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("resetPin", resetPin);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);

	}

	@Override
	protected void executeRequest() {
	//	apiResponse = given().spec(requestSpecification).config(config).headers(headerMap).post();
		
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s = apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}

}
