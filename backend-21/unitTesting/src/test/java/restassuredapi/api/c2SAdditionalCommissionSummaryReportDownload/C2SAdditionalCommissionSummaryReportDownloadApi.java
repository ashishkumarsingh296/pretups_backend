package restassuredapi.api.c2SAdditionalCommissionSummaryReportDownload;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2Sadditionalcommissionsummaryreportdownloadrequestpojo.C2SAdditionalCommissionSummaryReportDownloadRequestPojo;

public class C2SAdditionalCommissionSummaryReportDownloadApi extends BaseAPI {

	String apiPath = "/v1/pretupsUIReports/downloadAddtnlCommSummryRpt";
	String contentType;
	C2SAdditionalCommissionSummaryReportDownloadRequestPojo c2SAdditionalCommissionSummaryReportDownloadRequestPojo  = new C2SAdditionalCommissionSummaryReportDownloadRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2SAdditionalCommissionSummaryReportDownloadApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2SAdditionalCommissionSummaryReportDownloadRequestPojo c2SAdditionalCommissionSummaryReportDownloadRequestPojo) {
		this.c2SAdditionalCommissionSummaryReportDownloadRequestPojo = c2SAdditionalCommissionSummaryReportDownloadRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
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