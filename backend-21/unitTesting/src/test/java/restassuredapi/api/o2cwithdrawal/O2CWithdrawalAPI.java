package restassuredapi.api.o2cwithdrawal;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2cwithdrawalrequestpojo.O2CWithdrawalRequestPojo;

import com.utils.Log;

public class O2CWithdrawalAPI extends BaseAPI{

	String apiPath = "/v1/o2c/o2cWithdrawl";
	String contentType;
	O2CWithdrawalRequestPojo o2CWithdrawalRequestPojo = new O2CWithdrawalRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2CWithdrawalAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(O2CWithdrawalRequestPojo o2CWithdrawalRequestPojo) {
		this.o2CWithdrawalRequestPojo = o2CWithdrawalRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2CWithdrawalRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
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
	}

}
