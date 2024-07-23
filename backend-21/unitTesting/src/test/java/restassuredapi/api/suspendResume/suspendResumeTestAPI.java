package restassuredapi.api.suspendResume;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.suspendResumerequestpojo.SuspendResumeRequestPojo;


public class suspendResumeTestAPI extends BaseAPI {
	String apiPath = "/v1/userServices/suspendResume";

	String accessToken;
	String contentType;

	SuspendResumeRequestPojo suspendResumeResponsePojo = new SuspendResumeRequestPojo();
	EncoderConfig encoderconfig = new EncoderConfig();

	public  suspendResumeTestAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void addBodyParam(SuspendResumeRequestPojo suspendResumeResponsePojo) {
		this.suspendResumeResponsePojo = suspendResumeResponsePojo;
	}
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(suspendResumeResponsePojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s = apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}
	@Override
	protected void validateResponse() {
		// TODO Auto-generated method stub
		
	}
}
