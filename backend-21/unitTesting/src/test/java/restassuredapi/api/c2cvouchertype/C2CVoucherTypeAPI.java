package restassuredapi.api.c2cvouchertype;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cbuyvouchertypeinforequestpojo.C2CBuyVoucherTypeInfoRequestPojo;


public class C2CVoucherTypeAPI extends BaseAPI {

	String apiPath = "/voucher/getvouchertypes";
	String contentType;
	C2CBuyVoucherTypeInfoRequestPojo c2cBuyVoucherTypeInfoRequestPojo = new C2CBuyVoucherTypeInfoRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CVoucherTypeAPI(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2CBuyVoucherTypeInfoRequestPojo c2cBuyVoucherTypeInfoRequestPojo) {
		this.c2cBuyVoucherTypeInfoRequestPojo = c2cBuyVoucherTypeInfoRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2cBuyVoucherTypeInfoRequestPojo);
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
