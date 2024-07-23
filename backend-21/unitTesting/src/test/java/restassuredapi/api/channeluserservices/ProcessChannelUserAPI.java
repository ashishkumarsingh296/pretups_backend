package restassuredapi.api.channeluserservices;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import restassuredapi.api.BaseAPI;
import restassuredapi.pojo.processchanneluserrequestpojo.ProcessChannelUserRequestPojo;
import restassuredapi.pojo.processchanneluserresponsepojo.ProcessChannelUserResponsePojo;

public class ProcessChannelUserAPI extends BaseAPI {

	String apiPath="/c2s-receiver/dvd";
	String contentType;
	
	ProcessChannelUserRequestPojo processChannelUserRequestPojo = new ProcessChannelUserRequestPojo();
	ProcessChannelUserResponsePojo processChannelUserResponsePojo = new ProcessChannelUserResponsePojo();
	EncoderConfig encoderconfig = new EncoderConfig();
	public ProcessChannelUserAPI(String baseURI) {
		super(baseURI);	
	}
	
	public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public void addBodyParam(ProcessChannelUserRequestPojo processChannelUserRequestPojo) {
	this.processChannelUserRequestPojo =processChannelUserRequestPojo;
	}
	

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
                .encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(contentType);
		requestSpecBuilder.setBody(processChannelUserRequestPojo);
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
