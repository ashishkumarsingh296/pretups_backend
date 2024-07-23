package restassuredapi.api.channelAdmin_ModifyChannelUser;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.channelAdminModifyChannelUserRequestPojo.ChannelAdminModifyChannelUserReqPojo;

public class ChannelAdmin_ModifyChannelUserAPI extends BaseAPI {

	String apiPath = "/v1/channelUsers/modify";
	String accessToken;
	String idType;
	String idValue;

	public String getIdType() {
		return idType;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

	EncoderConfig encoderconfig = new EncoderConfig();
	ChannelAdminModifyChannelUserReqPojo channelAdminModifyChannelUserReqPojo = new ChannelAdminModifyChannelUserReqPojo();

	public ChannelAdmin_ModifyChannelUserAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}

	public void addBodyParam(ChannelAdminModifyChannelUserReqPojo channelAdminModifyChannelUserReqPojo) {
		this.channelAdminModifyChannelUserReqPojo = channelAdminModifyChannelUserReqPojo;
	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(ContentType.JSON);
		requestSpecBuilder.setBody(channelAdminModifyChannelUserReqPojo);
		// requestSpecBuilder.addHeader("Authorization", "");
		requestSpecBuilder.addQueryParam("idType", idType);
		requestSpecBuilder.addQueryParam("idValue", idValue);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("idType", idType);
		queryParams.put("idValue", idValue);
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
