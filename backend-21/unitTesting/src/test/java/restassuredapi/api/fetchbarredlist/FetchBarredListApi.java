package restassuredapi.api.fetchbarredlist;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;

import java.util.HashMap;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.barunbarchanneluserrequestpojo.BarUnbarChannelUserRequestPojo;
import restassuredapi.pojo.fetchbarreduserlistrequestpojo.FetchBarredListRequestPojo;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;

import com.utils.Log;


public class FetchBarredListApi extends BaseAPI {

	String apiPath = "/v1/userServices/barredUserList";
	String contentType;
	FetchBarredListRequestPojo fetchBarredListRequestPojo = new FetchBarredListRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public FetchBarredListApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(FetchBarredListRequestPojo fetchBarredListRequestPojo) {
		this.fetchBarredListRequestPojo = fetchBarredListRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(fetchBarredListRequestPojo);
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
