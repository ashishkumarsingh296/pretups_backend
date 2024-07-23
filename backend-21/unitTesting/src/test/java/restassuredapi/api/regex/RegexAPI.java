package restassuredapi.api.regex;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class RegexAPI extends BaseAPI {
	String apiPath="v1/regex";
	String contentType;
	String accessToken;
    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}


	String language;
    String country;

	EncoderConfig encoderconfig = new EncoderConfig();
	public RegexAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	 
	

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String s=apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("language", language);
		requestSpecBuilder.addQueryParam("country", country);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("language", language);
		queryParams.put("country", country);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}


	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}
}
