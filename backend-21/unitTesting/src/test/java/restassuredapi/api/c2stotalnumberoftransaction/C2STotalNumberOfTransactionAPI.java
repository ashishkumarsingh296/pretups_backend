package restassuredapi.api.c2stotalnumberoftransaction;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;

import restassuredapi.pojo.c2stotalnumberoftransactionrequestepojo.C2STotalNumberOfTransactionRequestPojo;
import restassuredapi.pojo.c2stotalnumberoftransactionresponsepojo.C2STotalNumberOfTransactionResponsePojo;

public class C2STotalNumberOfTransactionAPI extends BaseAPI {
String apiPath="/c2s-rest-receiver/c2stotaltrans";
	
	String contentType;
	C2STotalNumberOfTransactionRequestPojo c2STotalNumberOfTransactionRequestPojo = new C2STotalNumberOfTransactionRequestPojo();
	C2STotalNumberOfTransactionResponsePojo c2STotalNumberOfTransactionResponsePojo = new C2STotalNumberOfTransactionResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	public C2STotalNumberOfTransactionAPI(String baseURI) {
		super(baseURI);
	}
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }
	 
	 public void addBodyParam(C2STotalNumberOfTransactionRequestPojo c2STotalNumberOfTransactionRequestPojo) {
			this.c2STotalNumberOfTransactionRequestPojo =c2STotalNumberOfTransactionRequestPojo;
			}
	 @Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2STotalNumberOfTransactionRequestPojo);
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
