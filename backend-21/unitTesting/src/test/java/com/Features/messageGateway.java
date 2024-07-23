package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.messageGateway.AddMessageGateway;
import com.pageobjects.superadminpages.messageGateway.AddMessageGatewayConfirmPage;
import com.pageobjects.superadminpages.messageGateway.AddMessageGatewayDetailsPage;
import com.pageobjects.superadminpages.messageGateway.GatewayMappingPage;
import com.pageobjects.superadminpages.messageGateway.MessageGatewaySubCategories;
import com.pageobjects.superadminpages.messageGateway.ModifyMessageGateway;
import com.utils.CacheUpdate;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class messageGateway {


	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	AddMessageGateway AddMessageGateway;
	AddMessageGatewayConfirmPage AddMessageGatewayConfirmPage;
	AddMessageGatewayDetailsPage AddMessageGatewayDetailsPage;
	ModifyMessageGateway ModifyMessageGateway;
	SelectNetworkPage networkPage;
	MessageGatewaySubCategories MessageGatewaySubCategories;
	GatewayMappingPage GatewayMappingPage;
	CacheUpdate CacheUpdate ;


	public messageGateway(WebDriver driver){

		this.driver = driver;	
		login = new Login();
		randomNum = new RandomGeneration();
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		MastersSubCategories = new MastersSubCategories(driver);
		AddMessageGateway = new AddMessageGateway(driver);
		AddMessageGatewayConfirmPage = new AddMessageGatewayConfirmPage(driver);
		AddMessageGatewayDetailsPage = new AddMessageGatewayDetailsPage(driver);
		ModifyMessageGateway = new ModifyMessageGateway(driver);
		networkPage = new SelectNetworkPage(driver);
		MessageGatewaySubCategories = new MessageGatewaySubCategories(driver);
		GatewayMappingPage = new GatewayMappingPage(driver);
		CacheUpdate CacheUpdate = new CacheUpdate(driver);
	}


	public String [] addmessageGateway() throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.MESSAGEGATEWAY); //Getting User with Access to Add Message Gateway
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[2];

		String GatewayName = UniqueChecker.UC_GatewayCode();
		result[0] = GatewayName;

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMessageGateway();
		MessageGatewaySubCategories.clickAddgateway();
		AddMessageGateway.enterGatewayCode(GatewayName);
		AddMessageGateway.enterGatewayName(GatewayName);
		AddMessageGateway.selectGatewayType(PretupsI.GATEWAY_TYPE_WEB);
		AddMessageGateway.selectGatewaySubType(PretupsI.GATEWAY_SUB_TYPE_WEB);
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
/*		
		int totalRow1 = ExcelUtility.getRowCount();

		int i=1;
		for( i=1; i<=totalRow1;i++)

		{			if((ExcelUtility.getCellData(0, ExcelI.MASTER_DETAILS, i).equalsIgnoreCase("Superadmin LoginID")));
			break;
		}
		System.out.println(i);
		String loginId = ExcelUtility.getCellData(0, ExcelI.VALUES, i);
		
		int j=1;
		for( j=1; i<=totalRow1;j++)

		{			if((ExcelUtility.getCellData(0, ExcelI.MASTER_DETAILS, j).equalsIgnoreCase("Superadmin Password")));
			break;
		}
		System.out.println(j);
		String Pwd = ExcelUtility.getCellData(0, ExcelI.VALUES, j);
		
		int k=1;
		for( k=1; k<=totalRow1;k++)

		{			if((ExcelUtility.getCellData(0, ExcelI.MASTER_DETAILS, k).equalsIgnoreCase("Putty IP")));
			break;
		}
		String host = ExcelUtility.getCellData(0, ExcelI.VALUES, k);
		System.out.println(k);
		*/
		
		String loginId = _masterVO.getMasterValue("Superadmin LoginID");
		String Pwd = _masterVO.getMasterValue("Superadmin Password");
		String host = _masterVO.getMasterValue("Putty IP");
		
		AddMessageGateway.enterHost(host);
		AddMessageGateway.selectProtocol(PretupsI.HTTP_PROTOCOL);
		AddMessageGateway.selectReqChkBox();
		AddMessageGateway.selectPushChkBox();
		AddMessageGateway.clickSubmit();
		AddMessageGatewayDetailsPage.enterServicePort("8080");
		AddMessageGatewayDetailsPage.SelectUnderProcessY();
		AddMessageGatewayDetailsPage.selectAuthType();
		AddMessageGatewayDetailsPage.enterReqLoginID(loginId);
		AddMessageGatewayDetailsPage.enterReqPwd(Pwd);
		AddMessageGatewayDetailsPage.enterReqConfirmPwd(Pwd);
		AddMessageGatewayDetailsPage.selectStatus(PretupsI.STATUS_ACTIVE);
		AddMessageGatewayDetailsPage.selectContentType();
		AddMessageGatewayDetailsPage.selectEncryptionLevel();
		AddMessageGatewayDetailsPage.enterPort("8080");
		AddMessageGatewayDetailsPage.selectRespStatus(PretupsI.STATUS_ACTIVE);
		AddMessageGatewayDetailsPage.enterLoginID(loginId);
		AddMessageGatewayDetailsPage.enterPwd(Pwd);
		AddMessageGatewayDetailsPage.enterConfirmPwd(Pwd);
		AddMessageGatewayDetailsPage.enterDestinationNo();
		AddMessageGatewayDetailsPage.entertimeOuts();
		AddMessageGatewayDetailsPage.clickSubmit();
		AddMessageGatewayConfirmPage.clickConfirm();

		result[1] = AddMessageGateway.getMsg();

		return result;
		
	}
	
	
	
	public String  ModifyMessageGateway(String gatewayCode) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODMESSAGEGATEWAY); //Getting User with Access to Add Message Gateway
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		

		//String GatewayName = UniqueChecker.UC_GatewayCode();
		//result[0] = GatewayName;

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMessageGateway();
		MessageGatewaySubCategories.clickModifygateway();
		ModifyMessageGateway.selectGatewayCode(gatewayCode);
		ModifyMessageGateway.clickModify();
		AddMessageGateway.clickSubmit();
		AddMessageGatewayDetailsPage.modifyDestinationNo();
		AddMessageGatewayDetailsPage.clickSubmit();
		AddMessageGatewayConfirmPage.clickModifyConfirm();
		String result = AddMessageGateway.getMsg();

		return result;
	}
	
	
	
	
	
	
	public String ModifyMessageGatewayStatusSuspend(String gatewayCode) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODMESSAGEGATEWAY); //Getting User with Access to Add Message Gateway
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMessageGateway();
		MessageGatewaySubCategories.clickModifygateway();
		ModifyMessageGateway.selectGatewayCode(gatewayCode);
		ModifyMessageGateway.clickModify();
		AddMessageGateway.clickSubmit();
		AddMessageGatewayDetailsPage.modifyStatusSuspend(PretupsI.STATUS_SUSPENDED_LOOKUPS);
		AddMessageGatewayDetailsPage.modifyRespStatusSuspend(PretupsI.STATUS_SUSPENDED_LOOKUPS);
		AddMessageGatewayDetailsPage.clickSubmit();
		AddMessageGatewayConfirmPage.clickModifyConfirm();
		String result = AddMessageGateway.getMsg();
		
		//CacheUpdate.updateCache(CacheController.CacheI.MessageGatewayCache());

		return result;
	}
	
	
	public String deleteGatewayMapping(String gatewayCode){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MESSAGEGATMAPPING); //Getting User with Access to Add Message Gateway
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMessageGateway();
		MessageGatewaySubCategories.clickMessageGatewayMapping();
		GatewayMappingPage.SelectMapping(gatewayCode);
		GatewayMappingPage.clickDelete();
		driver.switchTo().alert().accept();
		String actual = GatewayMappingPage.getMsg();

		return actual;
		
	}
	
	public String associateMapping(String gatewayCode){
		
		userAccessMap = UserAccess.getUserWithAccess(RolesI.MESSAGEGATMAPPING); //Getting User with Access to Add Message Gateway
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMessageGateway();
		MessageGatewaySubCategories.clickMessageGatewayMapping();
		GatewayMappingPage.SelectResponseGateway(gatewayCode);
		GatewayMappingPage.SelectMapping(gatewayCode);
		GatewayMappingPage.clickModify();
		GatewayMappingPage.clickConfirm();
		
		String actual = GatewayMappingPage.getMsg();

		return actual;
		
	}
	
	
	
	
	public String DeleteMessageGateway(String gatewayCode) throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.MODMESSAGEGATEWAY); //Getting User with Access to Add Message Gateway
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMessageGateway();
		MessageGatewaySubCategories.clickModifygateway();
		ModifyMessageGateway.selectGatewayCode(gatewayCode);
		ModifyMessageGateway.clickDelete();
		driver.switchTo().alert().accept();
		String actual = AddMessageGateway.getMsg();

		return actual;
	}
	
	
	
	
	
	
	
	}
