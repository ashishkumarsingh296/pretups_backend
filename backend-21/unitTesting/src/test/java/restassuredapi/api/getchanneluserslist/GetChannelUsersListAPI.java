package restassuredapi.api.getchanneluserslist;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.getchanneluserslistresponsepojo.GetChannelUsersListResponsePojo;
import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;
public class GetChannelUsersListAPI extends BaseAPI {
	
	String apiPath = "/v1/channelUsers/channelUsersList";

	String contentType;
	String networkCode;
	String msisdn;
	String loginId;
	String userName;
	String category;
	String geography;
	String domain;
	String pageNumber;
	String entriesPerPage;
	String accessToken;
	
	GetChannelUsersListResponsePojo getChannelUsersListResponsePojo = new GetChannelUsersListResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	

	public GetChannelUsersListAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}




	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getEntriesPerPage() {
		return entriesPerPage;
	}

	public void setEntriesPerPage(String entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}

	public GetChannelUsersListResponsePojo getGetChannelUsersListResponsePojo() {
		return getChannelUsersListResponsePojo;
	}

	public void setGetChannelUsersListResponsePojo(GetChannelUsersListResponsePojo getChannelUsersListResponsePojo) {
		this.getChannelUsersListResponsePojo = getChannelUsersListResponsePojo;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}
	
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("networkCode", networkCode);
		requestSpecBuilder.addQueryParam("msisdn", msisdn);
		requestSpecBuilder.addQueryParam("loginId", loginId);
		requestSpecBuilder.addQueryParam("userName", userName);
		requestSpecBuilder.addQueryParam("category", category);
		requestSpecBuilder.addQueryParam("geography", geography);
		requestSpecBuilder.addQueryParam("domain", domain);
		requestSpecBuilder.addQueryParam("pageNumber", pageNumber);
		requestSpecBuilder.addQueryParam("entriesPerPage", entriesPerPage);
		requestSpecBuilder.addHeader("Authorization", "");
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("networkCode", networkCode);
		queryParams.put("msisdn", msisdn);
		queryParams.put("loginId", loginId);
		queryParams.put("userName", userName);
		queryParams.put("category", category);
		queryParams.put("geography", geography);
		queryParams.put("domain", domain);
		queryParams.put("pageNumber", pageNumber);
		queryParams.put("entriesPerPage", entriesPerPage);
		
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
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
