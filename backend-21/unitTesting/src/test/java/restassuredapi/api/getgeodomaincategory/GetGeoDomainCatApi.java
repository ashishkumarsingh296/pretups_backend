package restassuredapi.api.getgeodomaincategory;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class GetGeoDomainCatApi extends BaseAPI{

	String apiPath="v1/o2c/getGeoDomainCatDetails";
	String contentType;
	String accessToken;
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public GetGeoDomainCatApi(String baseURI,String accessToken) {
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
	}
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}


	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}
}
