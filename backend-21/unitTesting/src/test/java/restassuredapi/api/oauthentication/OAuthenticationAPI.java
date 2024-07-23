package restassuredapi.api.oauthentication;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
//import com.github.dzieciou.testing.curl.CurlLoggingRestAssuredConfigFactory;
public class OAuthenticationAPI extends BaseAPI{



	String apiPath = "/v1/generateTokenAPI";
	String contentType;
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	Map<String, Object> headerMap;
	RestAssuredConfig config = null;
	EncoderConfig encoderconfig = new EncoderConfig();

	public OAuthenticationAPI(String baseURI, Map<String, Object> headerMap) {
		super(baseURI);
		this.headerMap=headerMap;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(OAuthenticationRequestPojo oAuthenticationRequestPojo) {
		this.oAuthenticationRequestPojo = oAuthenticationRequestPojo;
		}

	protected void createRequest() {
	//	config = CurlLoggingRestAssuredConfigFactory.createConfig();
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(oAuthenticationRequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);

	}

	@Override
	protected void executeRequest() {
		//apiResponse = given().spec(requestSpecification).config(config).headers(headerMap).post();
		apiResponse = given().spec(requestSpecification).relaxedHTTPSValidation().headers(headerMap).post();

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
