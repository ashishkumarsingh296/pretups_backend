package restassuredapi.api.fetchStaffUserDetails;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;

public class FetchStaffUserDetailsAPI extends BaseAPI{
	
	String apiPath = "/v1/pretupsUIReports/fetchStaffUserDetails";

	String contentType;
	String accessToken;
	String categoryCode;
	String channlUserIDOrMSISDN;
	String domainCode;
	String geography;
	String reqTab;
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getChannlUserIDOrMSISDN() {
		return channlUserIDOrMSISDN;
	}

	public void setChannlUserIDOrMSISDN(String channlUserIDOrMSISDN) {
		this.channlUserIDOrMSISDN = channlUserIDOrMSISDN;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getReqTab() {
		return reqTab;
	}

	public void setReqTab(String reqTab) {
		this.reqTab = reqTab;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}

	
	
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public FetchStaffUserDetailsAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;
	}





	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(ContentType.JSON);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecBuilder.addQueryParam("categoryCode", categoryCode);
		requestSpecBuilder.addQueryParam("channlUserIDOrMSISDN", channlUserIDOrMSISDN);
		requestSpecBuilder.addQueryParam("domainCode", domainCode);
		requestSpecBuilder.addQueryParam("geography", geography);
		requestSpecBuilder.addQueryParam("reqTab", reqTab);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("categoryCode", categoryCode);
		queryParams.put("channlUserIDOrMSISDN", channlUserIDOrMSISDN);
		queryParams.put("domainCode", domainCode);
		queryParams.put("geography", geography);
		queryParams.put("reqTab", reqTab);
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();    /*.auth().oauth2(accessToken, OAuthSignature.HEADER)*/
	
		String s = apiResponse.asString();
		System.out.println(s);
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}


}
