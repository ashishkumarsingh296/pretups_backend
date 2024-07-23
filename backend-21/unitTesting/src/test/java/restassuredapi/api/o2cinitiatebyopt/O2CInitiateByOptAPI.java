package restassuredapi.api.o2cinitiatebyopt;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2cinitiateoptreqpojo.O2CInitiateByOptRequest;
import restassuredapi.pojo.o2creturnrequestpojo.O2CReturnRequest;

import com.utils.Log;

public class O2CInitiateByOptAPI extends BaseAPI{

	String apiPath = "/v1/o2c/stockInitiateByOpt";
	String contentType;
	O2CInitiateByOptRequest o2CInitiateByOptRequest = new O2CInitiateByOptRequest();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2CInitiateByOptAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(O2CInitiateByOptRequest o2CInitiateByOptRequest) {
		this.o2CInitiateByOptRequest = o2CInitiateByOptRequest;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(o2CInitiateByOptRequest);
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
