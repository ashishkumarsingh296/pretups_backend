package restassuredapi.test;

import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.userdetails.UserDetailsApi;
import restassuredapi.pojo.userdetirequestpojo.Data;
import restassuredapi.pojo.userdetirequestpojo.UserDetailsRequestPojo;
import restassuredapi.pojo.userdetiresponsepojo.UserDetailsResponsePojo;
@ModuleManager(name = Module.REST_GET_CHANNEL_USER_DETAILS)
public class UserDetails extends BaseTest  {
	static String moduleCode;
	UserDetailsRequestPojo userDetailsRequestPojo = new UserDetailsRequestPojo();
	HashMap<String, String> transfer_Details = new HashMap<String, String>();

	Data data = new Data();

	UserDetailsResponsePojo userDetailsResponsePojo = new UserDetailsResponsePojo();

	HashMap<String, String> returnMap = new HashMap<String, String>();
	public void setupData() {

		userDetailsRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		userDetailsRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		userDetailsRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		userDetailsRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		userDetailsRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		userDetailsRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));

		data.setExtcode("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		//data.setLanguage2(_masterVO.getProperty("languageCode0"));
		data.setLoginid("");
		data.setMsisdn("");
		data.setPassword("");
		data.setPin("");

		userDetailsRequestPojo.setData(data);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int OperatorRowCount = ExcelUtility.getRowCount();
		for (int i = 1; i < OperatorRowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			if (CategoryName.equals("DIST") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				returnMap.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				returnMap.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
				returnMap.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				returnMap.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
			} 
			if (CategoryName.equals("SE") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				returnMap.put("MSISDN2", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				break;
			}
		}

	}

	public void setupDataLoginId() {

		setupData();

		data.setLoginid(returnMap.get("LOGIN_ID"));
		data.setMsisdn("");
		data.setPassword(returnMap.get("PASSWORD"));
		data.setPin(returnMap.get("PIN"));
		data.setMsisdn2(returnMap.get("MSISDN2"));
		userDetailsRequestPojo.setData(data);

	}

	public void setupDataMsisdn() {

		setupData();

		data.setLoginid("");
		data.setMsisdn(returnMap.get("MSISDN"));
		data.setPassword("");
		data.setPin(returnMap.get("PIN"));
		data.setMsisdn2(returnMap.get("MSISDN2"));
		userDetailsRequestPojo.setData(data);

	}
	
	// Successful data with valid data.using login id and password
		@Test
		public void A_01_Test_success_loginId() throws Exception {
			final String methodName = "A_01_Test_success_loginId";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS001");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupDataLoginId();

			UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

			userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
			userDetailsApi.addBodyParam(userDetailsRequestPojo);
			
			userDetailsApi.setExpectedStatusCode(200);
			userDetailsApi.perform();

			userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);
			
			int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());

			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		// Successful data with valid data.using msisdn and pin
				@Test
				public void A_02_Test_success_msisdn() throws Exception {
					final String methodName = "A_02_Test_success_msisdn";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS002");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataMsisdn();
					
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());

					Assert.assertEquals(statusCode, 200);
					Assertion.assertEquals(Integer.toString(statusCode), "200");
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
			
				// blank msisdn 2
				@Test
			public void A_03_Test_Negative_blank_msisdn2() throws Exception {
					final String methodName = "A_03_Test_Negative_blank_msisdn2";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS003");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataMsisdn();
					userDetailsRequestPojo.getData().setMsisdn2("");
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());
                    String message = userDetailsResponsePojo.getDataObject().getMessage();
					Assert.assertEquals(statusCode, 206);
					Assertion.assertEquals("msisdn2 field is invaild or blank.", message);
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				// Invalid msisdn 2
				@Test
				public void A_04_Test_Negative_invalid_msisdn2() throws Exception {
					final String methodName = "A_04_Test_Negative_invalid_msisdn2";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS004");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataMsisdn();
					userDetailsRequestPojo.getData().setMsisdn2("2332222232");
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());
                    String message = userDetailsResponsePojo.getDataObject().getMessage();
					Assert.assertEquals(statusCode, 206);
					Assertion.assertEquals("User details not found or msisdn enterd is not vaild.", message);
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				// extnwcode code blank
				@Test
				public void A_05_Test_Negative_blank_extnwcode() throws Exception {
					final String methodName = "A_05_Test_Negative_blank_extnwcode";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS005");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataMsisdn();
					userDetailsRequestPojo.getData().setExtnwcode("");
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());
                    String message = userDetailsResponsePojo.getDataObject().getMessage();
					Assert.assertEquals(statusCode, 206);
					Assertion.assertEquals("External network code value is blank.", message);
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				
				// Invalid sender msisdn 
				@Test
				public void A_06_Test_Negative_Invalid_msisdn() throws Exception {
					final String methodName = "A_06_Test_Negative_Invalid_msisdn";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS006");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataMsisdn();
					String newmsisdn=returnMap.get("MSISDN");
					newmsisdn = newmsisdn.substring(0,newmsisdn.length()-3);
					userDetailsRequestPojo.getData().setMsisdn(newmsisdn);
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());
                    String message = userDetailsResponsePojo.getDataObject().getMessage();
					Assert.assertEquals(statusCode, 206);
					Assertion.assertEquals("Invalid user", message);
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				// Blank Pin and msisdn not blank 
				@Test
				public void A_07_Test_Negative_Blank_Pin() throws Exception {
					final String methodName = "A_07_Test_Negative_Blank_Pin";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS007");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataMsisdn();
					userDetailsRequestPojo.getData().setPin("");
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());
                    String message = userDetailsResponsePojo.getDataObject().getMessage();
					Assert.assertEquals(statusCode, 206);
					Assertion.assertEquals("PIN can not be blank.", message);
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				//Successful data with valid data.using login id and password combination does not match
				@Test
				public void A_08_Test_Negative_LoginID_Pass() throws Exception {
					final String methodName = "A_08_Test_Negative_LoginID_Pass";
					Log.startTestCase(methodName);

					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDTAILS008");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(CaseMaster.getExtentCase());
					currentNode.assignCategory("REST");
					setupDataLoginId();
					userDetailsRequestPojo.getData().setPassword("9988");
					UserDetailsApi userDetailsApi = new UserDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));

					userDetailsApi.setContentType(_masterVO.getProperty("contentType"));
					userDetailsApi.addBodyParam(userDetailsRequestPojo);
					userDetailsApi.setExpectedStatusCode(200);
					userDetailsApi.perform();

					userDetailsResponsePojo = userDetailsApi.getAPIResponseAsPOJO(UserDetailsResponsePojo.class);

					int statusCode = Integer.parseInt(userDetailsResponsePojo.getDataObject().getTxnstatus());

					Assert.assertEquals(statusCode, 206);
					Assertion.assertEquals(userDetailsResponsePojo.getDataObject().getMessage(),"No such user exists, password is invalid.");
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}

}
