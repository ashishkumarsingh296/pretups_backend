package restassuredapi.api.vouchercardgroupAPI;

import static io.restassured.RestAssured.given;

import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.vouchercardgroupchangestatusrequestpojo.VoucherCardGroupChangeStatusRequestPojo;

public class VoucherCardGroupChangeStatusApi extends BaseAPI {

	String apiPath="/p2p/card-group/view/card-group-details";
	String contentType;
	VoucherCardGroupChangeStatusRequestPojo voucherCardGroupChangeStatusRequestPojo= new VoucherCardGroupChangeStatusRequestPojo();

	
	public VoucherCardGroupChangeStatusApi(String baseURI) {
		super(baseURI);	
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBodyParam(VoucherCardGroupChangeStatusRequestPojo voucherCardGroupChangeStatusRequestPojo) {
		this.voucherCardGroupChangeStatusRequestPojo =voucherCardGroupChangeStatusRequestPojo;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		//requestSpecBuilder.setBody(bodyParam.toString());
		requestSpecBuilder.setBody(voucherCardGroupChangeStatusRequestPojo);
		//requestSpecBuilder.setBody(Joiner.on("&").withKeyValueSeparator("=").join(bodyParams));
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