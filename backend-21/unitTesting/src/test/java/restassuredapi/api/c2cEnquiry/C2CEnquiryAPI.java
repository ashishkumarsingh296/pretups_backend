package restassuredapi.api.c2cEnquiry;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cenquiryrequestpojo.C2cAndO2cEnquiryRequestVO;

public class C2CEnquiryAPI extends BaseAPI {

	String apiPath = "/v1/channelEnquiry/enquiryO2cAndC2c";
	String contentType;
	String accessToken;
	String enquiryType;
	String searchBy;

	public void setEnquiryType(String enquiryType) {
		this.enquiryType = enquiryType;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	C2cAndO2cEnquiryRequestVO requestpojo = new C2cAndO2cEnquiryRequestVO();

	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CEnquiryAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setBodyParam(C2cAndO2cEnquiryRequestVO requestpojo) {
		this.requestpojo = requestpojo;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(requestpojo);
		requestSpecBuilder.addQueryParam("enquiryType", enquiryType);
		requestSpecBuilder.addQueryParam("searchBy", searchBy);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("enquiryType", enquiryType);
		queryParams.put("searchBy", searchBy);
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);

	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
	}

}
