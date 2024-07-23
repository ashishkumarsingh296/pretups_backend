package restassuredapi.api.ViewVoucherC2CO2CTrfDetails;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.viewvoucherC2cO2ctrfdetails.ViewVoucherC2cO2cTrfDetailsReqPojo;

public class ViewVoucherC2CO2CTrfDetailsAPI extends BaseAPI {

	String apiPath = "/v1/c2cReceiver/vtxncalviw";
	String contentType;
	String accessToken;
	ViewVoucherC2cO2cTrfDetailsReqPojo viewVoucherC2cO2cTrfDetailsReqPojo = new ViewVoucherC2cO2cTrfDetailsReqPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public ViewVoucherC2CO2CTrfDetailsAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken=accessToken;

	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(ViewVoucherC2cO2cTrfDetailsReqPojo viewVoucherC2cO2cTrfDetailsReqPojo) {
		this.viewVoucherC2cO2cTrfDetailsReqPojo = viewVoucherC2cO2cTrfDetailsReqPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(viewVoucherC2cO2cTrfDetailsReqPojo);
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
