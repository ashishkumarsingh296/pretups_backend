package restassuredapi.api.changenotificationlang;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.updatenotificationlangrequestpojo.UpdateNotificationlangRequestPojo;

public class UpdateNotificationLanguageAPI extends BaseAPI {

	String apiPath = "/v1/changeNotificationLang/updateNotificationLang";
	String contentType;
	
	UpdateNotificationlangRequestPojo updatenotificationlangRequestPojo = new UpdateNotificationlangRequestPojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	String accessToken;
	
	public UpdateNotificationLanguageAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public void setContentType(String contentType) {
	        this.contentType = contentType;
	}
	 
	 public void addBodyParam(UpdateNotificationlangRequestPojo updatenotificationlangRequestPojo ) {
			this.updatenotificationlangRequestPojo =updatenotificationlangRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecification = requestSpecBuilder.build();
		requestSpecBuilder.setBody(updatenotificationlangRequestPojo );
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
