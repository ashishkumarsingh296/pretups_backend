package restassuredapi.api.o2cpurchaseorwithdrawuserlistapi;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

public class O2cPurchaseOrWithdrawUserListApi extends BaseAPI {

	String apiPath = "/v1/c2cFileServices/downloadO2cPuchaseOrWithdrawUserList";
	String contentType;
	String accessToken;
	String purchaseOrWithdraw;
	String geoDomainCode;
	String domainCode;
	String categoryCode;
	String productCode;
	String walletTypeOpt;
	EncoderConfig encoderconfig = new EncoderConfig();

	public O2cPurchaseOrWithdrawUserListApi(String baseURI, String accessToken) {
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


	public String getAccessToken() {
		return accessToken;
	}


	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}


	public String getPurchaseOrWithdraw() {
		return purchaseOrWithdraw;
	}


	public void setPurchaseOrWithdraw(String purchaseOrWithdraw) {
		this.purchaseOrWithdraw = purchaseOrWithdraw;
	}


	public String getGeoDomainCode() {
		return geoDomainCode;
	}


	public void setGeoDomainCode(String geoDomainCode) {
		this.geoDomainCode = geoDomainCode;
	}


	public String getDomainCode() {
		return domainCode;
	}


	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}


	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public String getWalletTypeOpt() {
		return walletTypeOpt;
	}


	public void setWalletTypeOpt(String walletTypeOpt) {
		this.walletTypeOpt = walletTypeOpt;
	}


	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}


	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}


	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		
		requestSpecBuilder.addQueryParam("categoryCode", categoryCode );
		requestSpecBuilder.addQueryParam("domainCode", domainCode );
		requestSpecBuilder.addQueryParam("geoDomainCode", geoDomainCode );
		requestSpecBuilder.addQueryParam("productCode", productCode );
		requestSpecBuilder.addQueryParam("purchaseOrWithdraw", purchaseOrWithdraw);
		requestSpecBuilder.addQueryParam("walletType", walletTypeOpt );
		requestSpecification = requestSpecBuilder.build();
		
		HashMap<String, String> queryParams = new HashMap<String, String>();
		HashMap<String, String> pathParams = new HashMap<String, String>();
		queryParams.put("categoryCode", categoryCode );
		queryParams.put("domainCode", domainCode );
		queryParams.put("geoDomainCode", geoDomainCode );
		queryParams.put("productCode", productCode );
		queryParams.put("purchaseOrWithdraw", purchaseOrWithdraw);
		queryParams.put("walletType", walletTypeOpt );
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);
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
		//apiResponse.then().spec(responseSpecification);
	}

}
