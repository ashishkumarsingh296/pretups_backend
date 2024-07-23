package restassuredapi.api.c2creversal;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;


public class C2cReversalApi extends BaseAPI {
   //rstapi/v1/admTxn/reverseC2CTxn
	String apiPath = "/v1/admTxn/reverseC2CTxn";
	String contentType;
	String accessToken;
	String networkCode;
	String networkCodeFor;
	String remark;
	String txnID;
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2cReversalApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
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

	public String getNetworkCodeFor() {
		return networkCodeFor;
	}

	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	public void addBodyParam() {
	
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.addQueryParam("networkCode",networkCode);
		requestSpecBuilder.addQueryParam("networkCodeFor", networkCodeFor);
		requestSpecBuilder.addQueryParam("remark",remark);
		requestSpecBuilder.addQueryParam("txnID", txnID);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("networkCode",networkCode );
		queryParams.put("networkCodeFor", networkCodeFor);
		queryParams.put("remark",remark );
		queryParams.put("txnID", txnID);
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken).get();
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
