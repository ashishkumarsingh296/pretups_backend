package restassuredapi.api.userdetails;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userdetirequestpojo.UserDetailsRequestPojo;


public class UserDetailsApi extends BaseAPI {

	String apiPath = "/c2s-rest-receiver/usrdetails";
	String contentType;
	UserDetailsRequestPojo userDetailsRequestPojo = new UserDetailsRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public UserDetailsApi(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(UserDetailsRequestPojo userDetailsRequestPojo) {
		this.userDetailsRequestPojo = userDetailsRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(userDetailsRequestPojo);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
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
