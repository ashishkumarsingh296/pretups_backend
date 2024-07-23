package restassuredapi.api.otpforusertransferapi;

import java.util.HashMap;

import com.utils.Log;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.otpforusertransferrequestpojo.OtpForUserTransferRequestpojo;

public class OtpForUserTransferApi  extends BaseAPI{
	String apiPath = "/v1/channelUsers/sendOTPUserTransfer";
	String contentType;
	String accessToken;
	OtpForUserTransferRequestpojo requestpojo = new OtpForUserTransferRequestpojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setBodyParam(OtpForUserTransferRequestpojo requestpojo) {
		this.requestpojo = requestpojo;
	}
	
	public OtpForUserTransferApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(requestpojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
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
