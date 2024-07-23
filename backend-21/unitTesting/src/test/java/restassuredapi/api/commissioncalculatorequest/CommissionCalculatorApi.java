package restassuredapi.api.commissioncalculatorequest;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.commissioncalculatorequestpojo.CommissionCalculatorRequestPojo;

public class CommissionCalculatorApi extends BaseAPI {


	String apiPath = "/c2s-receiver/commissioncalculator";
	String contentType;
	CommissionCalculatorRequestPojo commissionCalculatorRequestPojo = new CommissionCalculatorRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public CommissionCalculatorApi(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(CommissionCalculatorRequestPojo commissionCalculatorRequestPojo) {
		this.commissionCalculatorRequestPojo = commissionCalculatorRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(commissionCalculatorRequestPojo);
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).post();
		String s = apiResponse.asString();
		System.out.println(s);
	}

	@Override
	protected void validateResponse() {
		responseSpecBuilder.expectStatusCode(expectedStatusCode);
		responseSpecification = responseSpecBuilder.build();
		apiResponse.then().spec(responseSpecification);
	}



}
