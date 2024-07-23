package restassuredapi.api.channeluserservices;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.viewselfcommenquiryrequestpojo.ViewSelfCommEnquiryRequestPojo;
import restassuredapi.pojo.viewselfcommenquiryresponsepojo.ViewSelfCommEnquiryResponsePojo;

public class ViewSelfCommEnquiryAPI extends BaseAPI{

	
	String apiPath="/user/viewSelfCommEnquiry";
	String contentType;
	ViewSelfCommEnquiryRequestPojo viewSelfCommEnquiryRequestPojo = new ViewSelfCommEnquiryRequestPojo();
	ViewSelfCommEnquiryResponsePojo viewSelfCommEnquiryResponsePojo = new ViewSelfCommEnquiryResponsePojo();
	
	public ViewSelfCommEnquiryAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(ViewSelfCommEnquiryRequestPojo viewSelfCommEnquiryRequestPojo) {
			this.viewSelfCommEnquiryRequestPojo =viewSelfCommEnquiryRequestPojo;
			}
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(viewSelfCommEnquiryRequestPojo);
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
