package restassuredapi.api.channelvoucherenquiry;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.channelvoucherenquiryrequestpojo.ChannelVoucherEnquiryRequestPojo;
import restassuredapi.pojo.channelvoucherenquiryresponsepojo.ChannelVoucherEnquiryResponsePojo;

public class ChannelVoucherEnquiryAPI extends BaseAPI {
	
	String apiPath="/c2s-receiver/vcavlblreq";
	String contentType;
	ChannelVoucherEnquiryRequestPojo channelVoucherEnquiryRequestPojo = new ChannelVoucherEnquiryRequestPojo();
	ChannelVoucherEnquiryResponsePojo channelVoucherEnquiryResponsePojo = new ChannelVoucherEnquiryResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	public ChannelVoucherEnquiryAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(ChannelVoucherEnquiryRequestPojo channelVoucherEnquiryRequestPojo) {
			this.channelVoucherEnquiryRequestPojo =channelVoucherEnquiryRequestPojo;
			}
	
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(channelVoucherEnquiryRequestPojo);
		
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
