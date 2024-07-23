package restassuredapi.api.selfvoucherenquirysubscriber;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.selfvoucherenquirysubscriberrequestpojo.SelfVoucherEnquirySubscriberRequestPojo;
import restassuredapi.pojo.selfvoucherenquirysubscriberresponsepojo.SelfVoucherEnquirySubscriberResponsePojo;

public class SelfVoucherEnquirySubscriberAPI extends BaseAPI {
	String apiPath="/p2p-rest-receiver/selfvcrenq";
	String contentType;
	SelfVoucherEnquirySubscriberRequestPojo selfVoucherEnquirySubscriberRequestPojo = new SelfVoucherEnquirySubscriberRequestPojo();
	SelfVoucherEnquirySubscriberResponsePojo selfVoucherEnquirySubscriberResponsePojo = new SelfVoucherEnquirySubscriberResponsePojo();
	
	public SelfVoucherEnquirySubscriberAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(SelfVoucherEnquirySubscriberRequestPojo selfVoucherEnquirySubscriberRequestPojo) {
			this.selfVoucherEnquirySubscriberRequestPojo =selfVoucherEnquirySubscriberRequestPojo;
			}
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		//requestSpecBuilder.setBody(bodyParam.toString());
		requestSpecBuilder.setBody(selfVoucherEnquirySubscriberRequestPojo);
		//requestSpecBuilder.setBody(Joiner.on("&").withKeyValueSeparator("=").join(bodyParams));
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
