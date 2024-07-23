package restassuredapi.api.channelAdminBulkDeleteChannelUsers;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.utils.Log;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import restassuredapi.api.BaseAPI;

public class ChannelAdminBulkDeleteChannelUsersAPI extends BaseAPI {

	String apiPath = "/v1/BulkOperations/bulkUploadOperations";
	String accessToken;
	String fileAttachment;
	String fileName;
	String fileType;
	String idType;
	String userAction;

	public String getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	EncoderConfig encoderconfig = new EncoderConfig();

	public ChannelAdminBulkDeleteChannelUsersAPI(String baseURI, String accessToken) {
		super(baseURI);
		this.accessToken = accessToken;
	}

	public EncoderConfig getEncoderconfig() {
		return encoderconfig;
	}

	public void setEncoderconfig(EncoderConfig encoderconfig) {
		this.encoderconfig = encoderconfig;
	}

	public void createTxtFileWithChannelUsers() {
		
	}

	public void deleteCreatedTxtFile() {

	}

	@Override
	protected void createRequest() {
		requestSpecBuilder.setBaseUri(baseURI);
		requestSpecBuilder.setBasePath(apiPath);
		requestSpecBuilder.setConfig(RestAssured.config()
				.encoderConfig(encoderconfig.appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		requestSpecBuilder.setContentType(ContentType.JSON);
		requestSpecBuilder.addQueryParam("fileAttachment", fileAttachment);
		requestSpecBuilder.addQueryParam("fileName", fileName);
		requestSpecBuilder.addQueryParam("fileType", fileType);
		requestSpecBuilder.addQueryParam("idType", idType);
		requestSpecBuilder.addQueryParam("userAction", userAction);
		requestSpecification = requestSpecBuilder.build();
		HashMap<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("fileAttachment", fileAttachment);
		queryParams.put("fileName", fileName);
		queryParams.put("fileType", fileType);
		queryParams.put("idType", idType);
		queryParams.put("userAction", userAction);
		HashMap<String, String> pathParams = new HashMap<String, String>();
		logApiUrlAndParameters(baseURI, apiPath, queryParams, pathParams);

	}

	@Override
	protected void executeRequest() {
		apiResponse = given().spec(requestSpecification).auth().oauth2(accessToken)
				.post(); /* .auth().oauth2(accessToken, OAuthSignature.HEADER) */

		String s = apiResponse.asString();
		Log.info(s);
	}

	@Override
	protected void validateResponse() {
//		responseSpecBuilder.expectStatusCode(expectedStatusCode);
//		responseSpecification = responseSpecBuilder.build();
//		apiResponse.then().spec(responseSpecification);
	}

}
