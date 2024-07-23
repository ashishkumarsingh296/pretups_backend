package restassuredapi.api.downloadOfflineReport;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;

public class DownloadOfflineReportAPI extends BaseAPI{

	String apiPath = "/v1/pretupsUIReports/downloadOfflineReportByTaskID";

	String contentType;
	String accessToken;
	String reportTaskID;
	EncoderConfig encoderconfig = new EncoderConfig();
	
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

	public String getReportTaskID() {
		return reportTaskID;
	}

	public void setReportTaskID(String reportTaskID) {
		this.reportTaskID = reportTaskID;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}

	
	public DownloadOfflineReportAPI(String baseURI , String accessToken) {
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
		requestSpecBuilder.addQueryParam("reportTaskID", reportTaskID);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("reportTaskID", reportTaskID);
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
