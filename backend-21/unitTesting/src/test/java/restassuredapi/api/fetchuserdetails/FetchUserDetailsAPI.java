package restassuredapi.api.fetchuserdetails;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class FetchUserDetailsAPI extends BaseAPI{


	String apiPath = "/v1/channelUsers/fetchUserDetails/{idValue}";

	String contentType;
	String idType;
	String idValue;
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public FetchUserDetailsAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public void setIdType(String idType) {
		this.idType = idType;
	}

	public void setIdType(int idType) {
		this.idType = Integer.toString(idType) ;
	}

	public void setidValue(String idValue) {
		this.idValue = idValue;
	}

	public void setidValue(int idValue) {
		this.idValue = Integer.toString(idValue) ;
	}



	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("idType", idType);
		requestSpecBuilder.addPathParam("idValue", idValue);
		//requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("idType", idType);
		
		pathParams.put("idValue", idValue);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
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
