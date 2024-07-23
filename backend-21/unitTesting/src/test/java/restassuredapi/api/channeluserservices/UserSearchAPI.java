package restassuredapi.api.channeluserservices;

import static io.restassured.RestAssured.given;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.usersearchrequestpojo.UserSearchRequestPojo;
import restassuredapi.pojo.usersearchresponsepojo.UserSearchResponsePojo;

public class UserSearchAPI extends BaseAPI {
	String apiPath="/user/searchuser";
	String contentType;
	UserSearchRequestPojo userSearchRequestPojo = new UserSearchRequestPojo();
	UserSearchResponsePojo userSearchResponsePojo = new UserSearchResponsePojo();
	
	public UserSearchAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(UserSearchRequestPojo userSearchRequestPojo) {
			this.userSearchRequestPojo = userSearchRequestPojo;
			}
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(userSearchRequestPojo);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
		String s=apiResponse.asString();
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
