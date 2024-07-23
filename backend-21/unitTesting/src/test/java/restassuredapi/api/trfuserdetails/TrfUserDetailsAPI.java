package restassuredapi.api.trfuserdetails;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.trfuserdetailsrequestpojo.TrfUserDetailsRequestPojo;
import restassuredapi.pojo.trfuserdetailsresponsepojo.TrfUserDetailsResponsePojo;


public class TrfUserDetailsAPI extends BaseAPI {

	String apiPath = "v1/c2sReceiver/userinfo";
	String contentType;
	String accessToken;
	TrfUserDetailsRequestPojo trfUserDetailsRequestPojo = new TrfUserDetailsRequestPojo();
	TrfUserDetailsResponsePojo trfUserDetailsResponsePojo = new TrfUserDetailsResponsePojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public TrfUserDetailsAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(TrfUserDetailsRequestPojo trfUserDetailsRequestPojo) {
		this.trfUserDetailsRequestPojo = trfUserDetailsRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(trfUserDetailsRequestPojo);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();

		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
