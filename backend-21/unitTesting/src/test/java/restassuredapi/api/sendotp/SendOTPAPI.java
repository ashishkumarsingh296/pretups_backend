package restassuredapi.api.sendotp;



import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.sendotprequestpojo.SendOTPRequestPojo;
import restassuredapi.pojo.sendotpresponsepojo.SendOTPResponsePojo;

public class SendOTPAPI extends BaseAPI {
	

	
	
	String apiPath="/c2s-rest-receiver/otpforforgotpin";
	
	String contentType;
	SendOTPRequestPojo sendOTPRequestPojo = new SendOTPRequestPojo();
	SendOTPResponsePojo sendOTPResponsePojo = new SendOTPResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public SendOTPAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(SendOTPRequestPojo sendOTPRequestPojo) 
	 {
			this.sendOTPRequestPojo =sendOTPRequestPojo;
	 }
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(sendOTPRequestPojo);
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



