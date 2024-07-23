package restassuredapi.api.validateotp;



import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.validateotprequestpojo.ValidateOtpRequestPojo;
import restassuredapi.pojo.validateotpresponsepojo.ValidateOtpResponsePojo;

public class ValidateOTPAPI extends BaseAPI {
	

	
	
	String apiPath="/c2s-rest-receiver/otpvdpinrst";
	
	String contentType;
	ValidateOtpRequestPojo validateOTPRequestPojo = new ValidateOtpRequestPojo();
	ValidateOtpResponsePojo validateOTPResponsePojo = new ValidateOtpResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public ValidateOTPAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(ValidateOtpRequestPojo validateOtpRequestPojo) 
	 {
			this.validateOTPRequestPojo =validateOtpRequestPojo;
	 }
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(validateOTPRequestPojo);
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



