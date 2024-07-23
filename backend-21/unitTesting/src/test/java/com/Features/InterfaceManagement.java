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
import com.pageobjects.superadminpages.interfaceManagement.AddInterfaceConfirmPage;
import com.pageobjects.superadminpages.interfaceManagement.AddInterfacePage;
import com.pageobjects.superadminpages.interfaceManagement.InterfaceListPage;
import com.pageobjects.superadminpages.interfaceManagement.InterfacemanagementPage;
import com.pageobjects.superadminpages.interfaceManagement.ModifyInterfacePage;
import com.pageobjects.superadminpages.interfaceManagement.SetUpIPNodesDetails;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.SwitchWindow;
import com.utils._masterVO;

public class InterfaceManagement {



	WebDriver driver = null;
	Login login;
	RandomGeneration randomNum;
	SuperAdminHomePage SuperAdminHomePage;
	MastersSubCategories MastersSubCategories;
	InterfacemanagementPage InterfacePage;
	InterfaceListPage interfaceList;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	SelectNetworkPage networkPage;
	AddInterfacePage AddInterfacePage;
	SetUpIPNodesDetails SetUpIPNodesDetails;
	AddInterfaceConfirmPage AddInterfaceConfirmPage;
	ModifyInterfacePage ModifyInterfacePage;
	int INTERFACE_ClientVer = Integer.parseInt(_masterVO.getClientDetail("INTERFACE_VER"));

	public InterfaceManagement(WebDriver driver){
		
		this.driver = driver;	
		login = new Login();
		randomNum = new RandomGeneration();
		SuperAdminHomePage = new SuperAdminHomePage(driver);
		MastersSubCategories = new MastersSubCategories(driver);
		InterfacePage = new InterfacemanagementPage(driver);
		networkPage = new SelectNetworkPage(driver);
		interfaceList = new InterfaceListPage(driver);
		AddInterfacePage = new AddInterfacePage(driver);
		AddInterfaceConfirmPage = new AddInterfaceConfirmPage(driver);
		SetUpIPNodesDetails = new SetUpIPNodesDetails(driver);
		ModifyInterfacePage = new ModifyInterfacePage(driver);
		
	}


	public String [] addInterface() throws InterruptedException {

		userAccessMap = UserAccess.getUserWithAccess(RolesI.INTERFACE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		String [] result = new String[5];

		String InterfaceName = UniqueChecker.UC_InterfaceName();
		result[0] = InterfaceName;

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickInterfaceManagement();
		result[3]= InterfacePage.selectInterfaceCatergory1(1);
		System.out.println("The selected category is : " +result[3]);
		
		InterfacePage.clickSubmit();
		interfaceList.clickAdd();
		result[4] = AddInterfacePage.selectInterfaceType(1);
		AddInterfacePage.enterInterfaceName(InterfaceName);
		String extId = UniqueChecker.UC_InterfaceExtID();
		AddInterfacePage.enterexternalId(extId);
		if(INTERFACE_ClientVer==1){
		AddInterfacePage.enternoOfNodes();
		}
		AddInterfacePage.entervalExpiryTime();
		AddInterfacePage.entertopUpExpiryTime();
		AddInterfacePage.enterlanguage1Message();
		AddInterfacePage.enterlanguage2Message();
		AddInterfacePage.selectStatus(PretupsI.STATUS_ACTIVE_LOOKUPS);
		if(INTERFACE_ClientVer==1){
		AddInterfacePage.clicksetUpIPNodes();
		
		SwitchWindow.switchwindow(driver);

		SetUpIPNodesDetails.enterNode1IP();
		SetUpIPNodesDetails.enterNode1Port();
		SetUpIPNodesDetails.enterNode1URI();
		SetUpIPNodesDetails.selectNode1Status(PretupsI.STATUS_ACTIVE_LOOKUPS);
		SetUpIPNodesDetails.clickAddNodes();
		SwitchWindow.backwindow(driver);
		}
		AddInterfacePage.clickaddInterfaceButton();
		AddInterfaceConfirmPage.clickConfirm();

		result[1] = InterfacePage.getMsg();
		result[2] = DBHandler.AccessHandler.getInterfaceID(extId, InterfaceName);
		return result;


	}




	public String modifyInterface(String interfaceID){

		Log.info("The Interface ID for Modification is" +interfaceID);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.INTERFACE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickInterfaceManagement();
		InterfacePage.selectInterfaceCatergory1(1);
		InterfacePage.clickSubmit();

		interfaceList.SelectInterfaceID(interfaceID);

		interfaceList.clickModify();
		ModifyInterfacePage.enterlanguage1Message();
		ModifyInterfacePage.enterlanguage2Message();
		ModifyInterfacePage.clicksubmitButton();
		ModifyInterfacePage.clickConfirmButton();
		String actual = InterfacePage.getMsg();
		return actual;



	}


	public String DeleteInterface(String interfaceID){

		Log.info("The Interface ID for Modification is" +interfaceID);

		userAccessMap = UserAccess.getUserWithAccess(RolesI.INTERFACE); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

		networkPage.selectNetwork();
		SuperAdminHomePage.clickMasters();
		MastersSubCategories.clickInterfaceManagement();
		InterfacePage.selectInterfaceCatergory1(1);
		InterfacePage.clickSubmit();

		interfaceList.SelectInterfaceID(interfaceID);

		interfaceList.clickDelete();
		driver.switchTo().alert().accept();
		String actual = InterfacePage.getMsg();
		return actual;



	}



}
