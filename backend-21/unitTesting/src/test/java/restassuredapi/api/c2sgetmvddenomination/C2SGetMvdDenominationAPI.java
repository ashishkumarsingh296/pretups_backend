package restassuredapi.api.c2sgetmvddenomination;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class C2SGetMvdDenominationAPI extends BaseAPI{
	
	String apiPath = "/v1/c2sServices/getDenomination";
	String contentType;
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2SGetMvdDenominationAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
		//requestSpecBuilder.addPathParam("transferType", transferType);
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		//pathParams.put("transferType", transferType);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}
	
	
	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
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
