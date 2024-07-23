package restassuredapi.api.c2crecentbuyenquiry;

import static io.restassured.RestAssured.given;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2crecentbuyenquiryrequestpojo.C2CRecentBuyEnquiryRequestPojo;


public class C2CRecentBuyEnquiryAPI extends BaseAPI {

	String apiPath = "/c2s-rest-receiver/c2cbuyusenq";
	String contentType;
	C2CRecentBuyEnquiryRequestPojo c2CRecentBuyEnquiryRequestPojo = new C2CRecentBuyEnquiryRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CRecentBuyEnquiryAPI(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2CRecentBuyEnquiryRequestPojo c2CRecentBuyEnquiryRequestPojo) {
		this.c2CRecentBuyEnquiryRequestPojo = c2CRecentBuyEnquiryRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2CRecentBuyEnquiryRequestPojo);
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
