package restassuredapi.api.o2cbatchwithdrawfileupload;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.o2cbatchwithdrawfileuploadrequestpojo.O2CBatchWFileUploadRequestPojo;

public class O2CBatchWFileUploadApi extends BaseAPI {

	String apiPath = "/v1/o2c/o2CBatchWithdraw";
	String contentType;
	String category;
	String channelDomain;
	public String getChannelDomain() {
		return channelDomain;
	}

	public void setChannelDomain(String channelDomain) {
		this.channelDomain = channelDomain;
	}

	public String getGeoDomain() {
		return geoDomain;
	}

	public void setGeoDomain(String geoDomain) {
		this.geoDomain = geoDomain;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getWalletType() {
		return walletType;
	}

	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}

	String geoDomain;
	String product;
	String walletType;
	O2CBatchWFileUploadRequestPojo c2CFileUploadApiRequestPojo = new O2CBatchWFileUploadRequestPojo();
	String accessToken;
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2CBatchWFileUploadApi(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	

	public void setCategory(String category) {
		this.category = category;
	}

	

	public void addBodyParam(O2CBatchWFileUploadRequestPojo c2CFileUploadApiRequestPojo) {
		this.c2CFileUploadApiRequestPojo = c2CFileUploadApiRequestPojo;
	}
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2CFileUploadApiRequestPojo);
		requestSpecBuilder.addQueryParam("category", category);
		requestSpecBuilder.addQueryParam("channelDomain", channelDomain);
		requestSpecBuilder.addQueryParam("geoDomain", geoDomain);
		requestSpecBuilder.addQueryParam("product", product);
		requestSpecBuilder.addQueryParam("walletType", walletType);
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
