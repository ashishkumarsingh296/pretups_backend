package restassuredapi.api.redispreferencecacheapi;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class PreferenceCacheApi extends BaseAPI {
	

	public PreferenceCacheApi(String baseURI) {
		super(baseURI);
		
	}
	
	String apiPath = "/v1/redis/getPreferenceCache";
	
    String contentType;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).get();
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
