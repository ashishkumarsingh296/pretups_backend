package restassuredapi.api.userhierarchy;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.userhierarchyrequestpojo.UserHierarchyRequestParentVO;


public class UserHierarchyAPI extends BaseAPI {

	String apiPath = "/c2s-rest-receiver/userhierarchy";
	String contentType;
	UserHierarchyRequestParentVO userHierarchyRequestParentVO = new UserHierarchyRequestParentVO();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public UserHierarchyAPI(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(UserHierarchyRequestParentVO userHierarchyRequestParentVO) {
		this.userHierarchyRequestParentVO = userHierarchyRequestParentVO;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(userHierarchyRequestParentVO);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
