package restassuredapi.api.bulkSusResCUAPI;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.bulkSusResCURequestPojo.BulkSusResCURequestPojo;
import restassuredapi.pojo.bulkgiftrechargerequestpojo.C2CBulkGiftRechargeRequestPojo;

public class BulkSusResCUAPI extends BaseAPI{
	
	String apiPath = "/v1/channeladmin/bulksusrescu";
	String Type;
	String accessToken;
	String contentType;
	
	BulkSusResCURequestPojo bulkSusResCURequestPojo = new BulkSusResCURequestPojo();
	EncoderConfig encoderconfig = new EncoderConfig();


	public BulkSusResCUAPI(String baseURI,String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}
	
	public String getApiPath() {
		return apiPath;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
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
	
	 public void addBodyParam(BulkSusResCURequestPojo bulkSusResCURequestPojo) {
			this.bulkSusResCURequestPojo =bulkSusResCURequestPojo;
	}
	
	@Override
	protected void createRequest() {
		// TODO Auto-generated method stub
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("Type", Type);
		requestSpecBuilder.setBody(bulkSusResCURequestPojo);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		
	}

	@Override
	protected void executeRequest() {
		// TODO Auto-generated method stub
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).post();
		String s = apiResponse.asString();
		Log.info(s);
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		// TODO Auto-generated method stub
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();

	}

}
