package restassuredapi.api.xrayjira;

import static io.restassured.RestAssured.given;

import java.util.HashMap;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.AddVoucherCardGroupRequestPojo;

public class XrayJiraAPI extends BaseAPI{


	String apiPath = "/rest/raven/1.0/import/execution";

	String contentType;
	String body;
	
	public XrayJiraAPI(String baseURI) {
		super(baseURI);
	}
	
	 public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	public void addBody(String body) {
		this.body =body;
		}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(body);
		//requestSpecBuilder.setBody(Joiner.on("&").withKeyValueSeparator("=").join(bodyParams));
		requestSpecification = requestSpecBuilder.build();
	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().preemptive().basic("Test.execution", "Jira@te1").post();
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
