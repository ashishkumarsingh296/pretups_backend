package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.changenotificationlang.ChangeNotificationLangAPI;
import restassuredapi.api.changenotificationlang.UpdateNotificationLanguageAPI;
import restassuredapi.api.changenotificationlang.UserPhoneDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.changenotificationlangresponsepojo.ChangeNotificationLangResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.phonedetailsresponsepojo.Language;
import restassuredapi.pojo.phonedetailsresponsepojo.PhoneDetailsResponsePojo;
import restassuredapi.pojo.updatenotificationlangrequestpojo.ChangedPhoneLanguage;
import restassuredapi.pojo.updatenotificationlangrequestpojo.UpdateNotificationlangRequestPojo;
import restassuredapi.pojo.updatenotificationlangresponsepojo.UpdateNotificationlangResponsePojo;

@ModuleManager(name = Module.CHANGE_NOTIFICATION_LANG)
public class ChangeNotificationLangTest extends BaseTest {

	static String moduleCode;

	ChangeNotificationLangResponsePojo changeNotificationLangResponsePojo = new ChangeNotificationLangResponsePojo();
	PhoneDetailsResponsePojo phoneDetailsResponsePojo = new PhoneDetailsResponsePojo();

	UpdateNotificationlangRequestPojo updatenotificationlangRequestPojo = new UpdateNotificationlangRequestPojo();
	ArrayList<ChangedPhoneLanguage> changedPhoneLanguageList = null;

	UpdateNotificationlangResponsePojo updatenotificationlangResponsePojo = new UpdateNotificationlangResponsePojo();

	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	HashMap<String, String> transfer_Details = new HashMap<String, String>();

	HashMap<String, String> returnMap = new HashMap<String, String>();

	String searchBy = "MSISDN";

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][10];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			Data[j][8] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
			Data[j][9] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			j++;
		}
		return Data;
	}

	Map<String, Object> headerMap = new HashMap<String, Object>();

	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	protected static String accessToken;

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {

		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));

		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-15208")
	public void A_01_Test_Success_UserDetails(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode, String userName,
			String domainName) throws Exception {

		final String methodName = "A_01_Test_Success_UserDetails";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANGENOTIFICATIONLANG01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		ChangeNotificationLangAPI changeNotificationLangAPI = new ChangeNotificationLangAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		changeNotificationLangAPI.setContentType(_masterVO.getProperty("contentType"));
		changeNotificationLangAPI.setCategoryCode(categorCode);
		changeNotificationLangAPI.setUserName(""); // all users
		changeNotificationLangAPI.setExpectedStatusCode(200);
		changeNotificationLangAPI.perform();

		changeNotificationLangResponsePojo = changeNotificationLangAPI
				.getAPIResponseAsPOJO(ChangeNotificationLangResponsePojo.class);

		long statusCode = changeNotificationLangResponsePojo.getStatus();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-15208")
	public void A_02_Test_Success_PhoneDetails(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode, String userName,
			String domainName) throws Exception {

		final String methodName = "A_02_Test_Success_PhoneDetails";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANGENOTIFICATIONLANG02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		UserPhoneDetailsAPI userPhoneDetailsAPI = new UserPhoneDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPhoneDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		userPhoneDetailsAPI.setCategoryCode("");
		userPhoneDetailsAPI.setUserName("");
		userPhoneDetailsAPI.setMsisdn(msisdn);
		userPhoneDetailsAPI.setSearchBy(searchBy);
		userPhoneDetailsAPI.setExpectedStatusCode(200);
		userPhoneDetailsAPI.perform();

		phoneDetailsResponsePojo = userPhoneDetailsAPI.getAPIResponseAsPOJO(PhoneDetailsResponsePojo.class);

		long statusCode = phoneDetailsResponsePojo.getStatus();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	public Language getNewLanguageToUpdate(List<Language> languageList, String assignedLanguageCode) {

		return languageList.stream()
				.filter(language -> !assignedLanguageCode.equals(language.getLanguageCode())).findAny().orElse(null);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15208")
	public void A_03_Test_Success_UpdateNotificationLang(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_03_Test_Success_UpdateNotificationLang";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANGENOTIFICATIONLANG03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		// loading channel user phone details to change language
		A_02_Test_Success_PhoneDetails(loginID, password, msisdn, PIN, parentName, categoryName, categorCode, externalCode, userName, domainName);
		
		UpdateNotificationLanguageAPI updateNotificationLanguageAPI = new UpdateNotificationLanguageAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		
		List<Language> languageList = phoneDetailsResponsePojo.getLanguageList();
		ChangedPhoneLanguage changedPhoneLanguage = new ChangedPhoneLanguage();
		if(phoneDetailsResponsePojo.getUserList().size() > 0 && languageList.size() > 1 ) {
			
			String assignedLanguageCode = phoneDetailsResponsePojo.getUserList().get(0).getLanguageCode();
			Language newLanguage = getNewLanguageToUpdate(languageList, assignedLanguageCode);
			
			changedPhoneLanguage.setUserMsisdn(phoneDetailsResponsePojo.getMsisdn());
			changedPhoneLanguage.setLanguageCode(newLanguage.getLanguageCode());
			changedPhoneLanguage.setCountry(newLanguage.getCountry());
		}
		
		
		changedPhoneLanguageList = new ArrayList<ChangedPhoneLanguage>();
		changedPhoneLanguageList.add(changedPhoneLanguage);
		updatenotificationlangRequestPojo.setChangedPhoneLanguageList(changedPhoneLanguageList);
		updatenotificationlangRequestPojo.setUserLoginID(phoneDetailsResponsePojo.getLoginID());
		
		updateNotificationLanguageAPI.setContentType(_masterVO.getProperty("contentType"));
		updateNotificationLanguageAPI.addBodyParam(updatenotificationlangRequestPojo);
		updateNotificationLanguageAPI.setExpectedStatusCode(200);
		updateNotificationLanguageAPI.perform();

		updatenotificationlangResponsePojo = updateNotificationLanguageAPI.getAPIResponseAsPOJO(UpdateNotificationlangResponsePojo.class);

		long statusCode = updatenotificationlangResponsePojo.getStatus();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

}
