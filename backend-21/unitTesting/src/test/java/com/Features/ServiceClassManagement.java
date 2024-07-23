package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.superadminpages.homepage.MastersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.serviceClassManagement.AddServiceClassPage;
import com.pageobjects.superadminpages.serviceClassManagement.serviceClassConfirmPage;
import com.pageobjects.superadminpages.serviceClassManagement.serviceClassDetailsPage;
import com.pageobjects.superadminpages.serviceClassManagement.serviceClassMgmtPage;
import com.utils.RandomGeneration;

public class ServiceClassManagement {



	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage networkPage;
	serviceClassMgmtPage serviceClassMgmtPage;
	serviceClassDetailsPage serviceClassDetailsPage;
	AddServiceClassPage AddServiceClassPage;
	serviceClassConfirmPage serviceClassConfirmPage;



	public ServiceClassManagement(WebDriver driver){
		this.driver = driver;	
		login = new Login();
		randomNum = new RandomGeneration();
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		MastersSubCategories = new MastersSubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		serviceClassMgmtPage =new serviceClassMgmtPage(driver);
		serviceClassDetailsPage = new serviceClassDetailsPage(driver);
		AddServiceClassPage = new AddServiceClassPage(driver);
		serviceClassConfirmPage = new serviceClassConfirmPage(driver);
	}



	public String[] addServiceClass(String Interface,String intCategory) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADDSERVICECLASS); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[3];
		String ServiceClassName = UniqueChecker.UC_ServiceClassName(); 
		result[0] = ServiceClassName;
		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickServiceClassManagement();
		serviceClassMgmtPage.selectInterfaceCatergory(intCategory);
		serviceClassMgmtPage.selectInterfaceType(1);
		serviceClassMgmtPage.selectInterface(Interface);
		serviceClassMgmtPage.clickSubmit();
		serviceClassDetailsPage.clickAdd();
		AddServiceClassPage.enterserviceClassName(ServiceClassName);
		AddServiceClassPage.enterServiceClassCode(ServiceClassName);
		AddServiceClassPage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		
		AddServiceClassPage.enterp2pSenderAllowedStatus();
		
		AddServiceClassPage.enterp2pReceiverAllowedStatus();
		
		AddServiceClassPage.enterc2sReceiverAllowedStatus();
		
		AddServiceClassPage.clickAdd();
		serviceClassConfirmPage.clickConfirm();
		String msg = serviceClassMgmtPage.getMsg();
		result[1] = msg;
		
		result[2] = DBHandler.AccessHandler.getServiceClassID(ServiceClassName);

		return result;

	}



	public String modifyServiceClass(String Interface, String ServiceClassID , String intCategory){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADDSERVICECLASS); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickServiceClassManagement();
		serviceClassMgmtPage.selectInterfaceCatergory(intCategory);
		serviceClassMgmtPage.selectInterfaceType(1);
		serviceClassMgmtPage.selectInterface(Interface);
		serviceClassMgmtPage.clickSubmit();
		serviceClassDetailsPage.SelectServiceClass(ServiceClassID);
		serviceClassDetailsPage.ClickModify();
		boolean chkBoxvisible = AddServiceClassPage.checkVisibilityp2pReceiverSuspendCheckbox();
		if( chkBoxvisible== true){
		AddServiceClassPage.clickP2PReceiverChkbox();
		}
		AddServiceClassPage.enterc2sReceiverAllowedStatusUpdate();
		AddServiceClassPage.clickModify();
		serviceClassConfirmPage.clickModifyConfirm();
		String actual = serviceClassMgmtPage.getMsg();
		return actual;
	}
	
	
	public String deleteServiceClass(String Interface, String ServiceClassID, String intCategory){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.ADDSERVICECLASS); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickServiceClassManagement();
		serviceClassMgmtPage.selectInterfaceCatergory(intCategory);
		serviceClassMgmtPage.selectInterfaceType(1);
		serviceClassMgmtPage.selectInterface(Interface);
		serviceClassMgmtPage.clickSubmit();
		serviceClassDetailsPage.SelectServiceClass(ServiceClassID);
		serviceClassDetailsPage.clickDelete();
		driver.switchTo().alert().accept();
		String actual = serviceClassMgmtPage.getMsg();
		
		return actual;
		
	}

}
