package restassuredapi.api.userpropertieslists;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userpropertieslistsresponsepojo.UserPropertiesListsResponsePojo;

import com.utils.Log;

public class UserPropertiesListsAPI extends BaseAPI {
	String apiPath = "/v1/channelUsers/selectionLists/{userCategory}/{parentCategory}/{parentGeography}";
	String contentType;
	String networkCode;
	String parentUserId;
	String userCategory;
	String parentCategory;
	String parentGeography;
	String accessToken;
	UserPropertiesListsResponsePojo userAssociateProfileResponsePojo = new UserPropertiesListsResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();

	public UserPropertiesListsAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public void setParentUserId(String parentUserId) {
		this.parentUserId = parentUserId;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public void setParentGeography(String parentGeography) {
		this.parentGeography = parentGeography;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("networkCode", networkCode);
		requestSpecBuilder.addQueryParam("parentUserId", parentUserId);
		requestSpecBuilder.addPathParam("userCategory", userCategory);
		requestSpecBuilder.addPathParam("parentCategory", parentCategory);
		requestSpecBuilder.addPathParam("parentGeography", parentGeography);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("networkCode", networkCode);
		queryParams.put("parentUserId", parentUserId);
		
		pathParams.put("userCategory", userCategory);
		pathParams.put("parentCategory", parentCategory);
		pathParams.put("parentGeography", parentGeography);

		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
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
