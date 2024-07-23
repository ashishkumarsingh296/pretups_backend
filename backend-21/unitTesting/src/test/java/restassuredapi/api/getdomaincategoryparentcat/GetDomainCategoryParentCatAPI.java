package restassuredapi.api.getdomaincategoryparentcat;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.getchanneluserslistresponsepojo.GetChannelUsersListResponsePojo;
import restassuredapi.pojo.getdomaincategoryparentcategoryresponsepojo.GetDomainCategoryParentCategoryResponsePojo;

public class GetDomainCategoryParentCatAPI  extends BaseAPI {
	
	String apiPath = "/v1/channelUsers/domainCategoryParentCat";

	String contentType;
//	String networkCode;
//	String identifierType;
//	String identifierValue;
	String accessToken;
	
	
	GetDomainCategoryParentCategoryResponsePojo getDomainCategoryParentCategoryResponsePojo = new GetDomainCategoryParentCategoryResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public GetDomainCategoryParentCatAPI(String baseURI, String accessToken) {
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

//	public String getNetworkCode() {
//		return networkCode;
//	}
//
//	public void setNetworkCode(String networkCode) {
//		this.networkCode = networkCode;
//	}
//
//	public String getIdentifierType() {
//		return identifierType;
//	}
//
//	public void setIdentifierType(String identifierType) {
//		this.identifierType = identifierType;
//	}
//
//	public String getIdentifierValue() {
//		return identifierValue;
//	}
//
//	public void setIdentifierValue(String identifierValue) {
//		this.identifierValue = identifierValue;
//	}

	public GetDomainCategoryParentCategoryResponsePojo getGetDomainCategoryParentCategoryResponsePojo() {
		return getDomainCategoryParentCategoryResponsePojo;
	}

	public void setGetDomainCategoryParentCategoryResponsePojo(
			GetDomainCategoryParentCategoryResponsePojo getDomainCategoryParentCategoryResponsePojo) {
		this.getDomainCategoryParentCategoryResponsePojo = getDomainCategoryParentCategoryResponsePojo;
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
		requestSpecBuilder.addHeader("Authorization", "");
		
		
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
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