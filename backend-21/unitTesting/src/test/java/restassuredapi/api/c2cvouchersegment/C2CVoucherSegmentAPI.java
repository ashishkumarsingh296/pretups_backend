package restassuredapi.api.c2cvouchersegment;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.c2cbuyvouchersegmentinforequestpojo.C2CBuyVoucherSegmentInfoRequestPojo;
import restassuredapi.pojo.c2cbuyvouchertypeinforequestpojo.C2CBuyVoucherTypeInfoRequestPojo;


public class C2CVoucherSegmentAPI extends BaseAPI {

	String apiPath = "/voucher/getvouchersegments";
	String contentType;
	C2CBuyVoucherSegmentInfoRequestPojo c2cBuyVoucherSegmentInfoRequestPojo = new C2CBuyVoucherSegmentInfoRequestPojo();
	
	EncoderConfig encoderconfig = new EncoderConfig();

	public C2CVoucherSegmentAPI(String baseURI) {
		super(baseURI);
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void addBodyParam(C2CBuyVoucherSegmentInfoRequestPojo c2cBuyVoucherSegmentInfoRequestPojo) {
		this.c2cBuyVoucherSegmentInfoRequestPojo = c2cBuyVoucherSegmentInfoRequestPojo;
	}

	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(c2cBuyVoucherSegmentInfoRequestPojo);
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
