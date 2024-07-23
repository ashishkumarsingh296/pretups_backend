package restassuredapi.api.channelAdminAddStaffUser;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.ChannelAdminAddStaffUserRequestPojo;

public final class ChannelAdminAddStaffUserAPI extends BaseAPI {

	String apiPath = "/v1/staffUsers/addStaffUser";
	String accessToken;
	EncoderConfig encoderconfig = new EncoderConfig();
	ChannelAdminAddStaffUserRequestPojo channelAdminCreateStaffUserReqPojo = new ChannelAdminAddStaffUserRequestPojo();
	
	
	public ChannelAdminAddStaffUserAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}
	
	public void addBodyParam(ChannelAdminAddStaffUserRequestPojo channelAdminCreateStaffUserReqPojo) {
		this.channelAdminCreateStaffUserReqPojo = channelAdminCreateStaffUserReqPojo;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(ContentType.JSON);
		requestSpecBuilder.setBody(channelAdminCreateStaffUserReqPojo);
		//requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);

	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken)
				.post(); /* .auth().oauth2(accessToken, OAuthSignature.HEADER) */

		String s = apiResponse.asString();
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
