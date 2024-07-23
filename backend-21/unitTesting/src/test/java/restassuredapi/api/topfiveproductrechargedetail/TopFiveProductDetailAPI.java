package restassuredapi.api.topfiveproductrechargedetail;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2sgettransactiondetailresponsepojo.C2SGetTransactionDetailsResponsePojo;
import restassuredapi.pojo.topfiveproductrechargedetailrequestpojo.TopFiveProdductRechargeDetailRequestPojo;

public class TopFiveProductDetailAPI extends BaseAPI {
	

	
	
	String apiPath="/c2s-rest-receiver/c2snprodtxndetails";
	
	String contentType;
	TopFiveProdductRechargeDetailRequestPojo topFiveProdductRechargeDetailRequestPojo  = new TopFiveProdductRechargeDetailRequestPojo();
	C2SGetTransactionDetailsResponsePojo c2SGetTransactionDetailsResponsePojo = new C2SGetTransactionDetailsResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	
	public TopFiveProductDetailAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(TopFiveProdductRechargeDetailRequestPojo topFiveProdductRechargeDetailRequestPojo) 
	 {
			this.topFiveProdductRechargeDetailRequestPojo =topFiveProdductRechargeDetailRequestPojo;
	 }
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(topFiveProdductRechargeDetailRequestPojo);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
		String s=apiResponse.asString();
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}



}



