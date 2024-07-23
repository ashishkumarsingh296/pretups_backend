package restassuredapi.api.channeluserservices;
import static io.restassured.RestAssured.given;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.selfprofilethersholdrequest.SelfProfileThresholdRequestPojo;
import restassuredapi.pojo.selfprofilethresholdresponsepojo.SelfProfileThresholdResponsePojo;


public class SelfProfileThresholdAPI extends BaseAPI{
	String apiPath="/user/selfthreshold";
	String contentType;
	SelfProfileThresholdRequestPojo selfProfileThresholdRequestPojo = new SelfProfileThresholdRequestPojo();
	SelfProfileThresholdResponsePojo selfProfileThresholdResponsePojo= new SelfProfileThresholdResponsePojo();
	
	public SelfProfileThresholdAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(SelfProfileThresholdRequestPojo selfProfileThresholdRequestPojo) {
			this.selfProfileThresholdRequestPojo =selfProfileThresholdRequestPojo;
			}
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(selfProfileThresholdRequestPojo);
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
