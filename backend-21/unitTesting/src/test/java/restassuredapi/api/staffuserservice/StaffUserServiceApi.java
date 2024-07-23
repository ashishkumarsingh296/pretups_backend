package restassuredapi.api.staffuserservice;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class StaffUserServiceApi extends BaseAPI{


	String apiPath = "/v1/staffUsers/getServices";

	String contentType;
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public StaffUserServiceApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

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
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
//		HashMap<String, String> queryParams = new HashMap<String, String>();
//		HashMap<String, String> pathParams = new HashMap<String, String>();
//		queryParams.put("idType", idType);
//		
//		pathParams.put("idValue", idValue);
//		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();    /*.auth().oauth2(accessToken, OAuthSignature.HEADER)*/
	
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
