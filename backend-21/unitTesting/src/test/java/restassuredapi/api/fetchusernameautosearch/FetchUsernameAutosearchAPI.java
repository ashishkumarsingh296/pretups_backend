package restassuredapi.api.fetchusernameautosearch;

import static io.restassured.RestAssured.given;
import java.util.HashMap;
import com.utils.Log;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;

public class FetchUsernameAutosearchAPI extends BaseAPI {
	
	String apiPath = "/v1/pretupsUIReports/fetchUserNameAutoSearch";
	String contentType;
	String accessToken;
	String categoryCode;
	String domainCode;
	String geography;
	String userName;
		
	EncoderConfig encoderconfig = new EncoderConfig();

	public FetchUsernameAutosearchAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
		
	public FetchUsernameAutosearchAPI(String baseURI) {
		super(baseURI);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	protected void createRequest() {
	requestSpecBuilder.setBaseUri(baseURI);
	requestSpecBuilder.setBasePath(apiPath);
	requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
	requestSpecBuilder.setContentType(ContentType.JSON);
	requestSpecBuilder.addHeader("Authorization", "");
	requestSpecBuilder.addQueryParam("categoryCode", categoryCode);
	requestSpecBuilder.addQueryParam("domainCode", domainCode);
	requestSpecBuilder.addQueryParam("geography", geography);
	requestSpecBuilder.addQueryParam("userName", userName);
		
	requestSpecification = requestSpecBuilder.build();
	HashMap<String, String> queryParams = new HashMap<String, String>();
	HashMap<String, String> pathParams = new HashMap<String, String>();
		
	queryParams.put("categoryCode", categoryCode);
	queryParams.put("domainCode", domainCode);
	queryParams.put("geography", geography);
	queryParams.put("userName", userName);
	logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
		String response = apiResponse.asString();
		Log.info(response);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}
}