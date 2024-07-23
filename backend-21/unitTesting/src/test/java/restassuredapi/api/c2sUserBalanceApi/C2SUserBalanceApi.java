package restassuredapi.api.c2sUserBalanceApi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2sUserBalancerequestpojo.C2SUserBalanceRequestPojo;
import restassuredapi.pojo.totaltransactiondetailedviewrequestpojo.TotalTransactionDetailedViewRequestPojo;

public class C2SUserBalanceApi extends BaseAPI {

	String apiPath = "/v1/c2sReceiver/c2sUserBalance";

	String accessToken;
	String contentType;

	C2SUserBalanceRequestPojo c2sUserBalanceRequestPojo = new C2SUserBalanceRequestPojo();
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2SUserBalanceApi(String baseURI, String accessToken) {
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
	public void addBodyParam(C2SUserBalanceRequestPojo c2sUserBalanceRequestPojo) {
		this.c2sUserBalanceRequestPojo = c2sUserBalanceRequestPojo;
	}
	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2sUserBalanceRequestPojo);
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
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}

}
