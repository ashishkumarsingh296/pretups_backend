/**
 * 
 */
package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.superadminpages.channeldomainmanagement.ChannelCategoryMgmtPage;
import com.pageobjects.superadminpages.channeldomainmanagement.ChannelDomainManagementPage1;
import com.pageobjects.superadminpages.channeldomainmanagement.ChannelDomainManagementPage2;
import com.pageobjects.superadminpages.homepage.ChannelDomainSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.utils.ExcelUtility;
import com.utils.RandomGeneration;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class ChannelDomain extends BaseTest{
	static String chnldomainCode;
	static String chnldomainName;
	static String chnlcatCode;
	static String chnlcatName;
	static String supercategoryname;
	static String domain_type_name;
	WebDriver driver=null;
	HashMap<String, String> channelDomainMap;
	
	SuperAdminHomePage homepage;
	ChannelDomainManagementPage1 chnlDomainPage1;
	ChannelDomainSubCategories chnlDomainSub;
	Login login;
	SelectNetworkPage selectNetwork;
	ChannelDomainManagementPage2 chnlDomainPage2;
	RandomGeneration randomgen;
	Map<String, String> userAccessMap = new HashMap<String, String>();
	ChannelCategoryMgmtPage ChannelCategoryMgmtPage1;
	
	public ChannelDomain(WebDriver driver){
	this.driver=driver;
	
	homepage= new SuperAdminHomePage(driver);
	chnlDomainPage1= new ChannelDomainManagementPage1(driver);
	chnlDomainSub= new ChannelDomainSubCategories(driver);
	login = new Login();
	selectNetwork= new SelectNetworkPage(driver);
	chnlDomainPage2= new ChannelDomainManagementPage2(driver);
	randomgen= new RandomGeneration();
	channelDomainMap= new HashMap<String, String>();
	ChannelCategoryMgmtPage1 = new ChannelCategoryMgmtPage(driver);
	supercategoryname  = DBHandler.AccessHandler.getCategoryName(PretupsI.SUPERADMIN_CATCODE);
	}
	
	
	public HashMap<String, String> add_domain(){
		chnldomainCode= "AUT"+randomgen.randomAlphabets(4).toUpperCase();
		chnldomainName= "AUT"+randomgen.randomAlphabets(6).toUpperCase();
		channelDomainMap.put("DomainName", chnldomainName);
		chnlcatCode= "AUT"+randomgen.randomAlphabets(4).toUpperCase();
		chnlcatName= "AUT"+randomgen.randomAlphabets(6).toUpperCase();
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String domainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		domain_type_name = DBHandler.AccessHandler.fetchdomainTypeName(domainName);
		
		channelDomainMap.put("categoryCode", chnlcatCode);
		
		String uIDPrefix= UniqueChecker.UC_UserIDPrefix();
		
		login.UserLogin(driver, "Operator", supercategoryname);
		selectNetwork.selectNetwork();
		homepage.clickChannelDomain();
		chnlDomainSub.clickChannelDomainMgmt();
		chnlDomainPage1.clickAddButton();
		
		chnlDomainPage1.enterChannelDomainCode(chnldomainCode);
		chnlDomainPage1.enterChannelDomainName(chnldomainName);
		chnlDomainPage1.selectChannelDomainType(domain_type_name);
		chnlDomainPage1.enterNumberOfDomainCategories("1");
		chnlDomainPage1.clickSubmitButton();
		
		chnlDomainPage2.enterChannelCategoryCode(chnlcatCode);
		chnlDomainPage2.enterChannelCategoryName(chnlcatName);
		chnlDomainPage2.selectGeographicalDomain();
		chnlDomainPage2.selectRoleType();
		chnlDomainPage2.enterUserIDPrefix(uIDPrefix);
		chnlDomainPage2.enterMaxTxnMsisdn();
		chnlDomainPage2.clickSubmitButton();
		chnlDomainPage2.clickConfirmButton();
		
		channelDomainMap.put("ChannelDomainCreationMsg",chnlDomainSub.getMessage());
		
		return channelDomainMap;
	}
	
	public HashMap<String, String> modify_domain(){
		
		login.UserLogin(driver, "Operator", supercategoryname);
		selectNetwork.selectNetwork();
		homepage.clickChannelDomain();
		chnlDomainSub.clickChannelDomainMgmt();
		chnlDomainPage1.selectDomain(chnldomainCode);
		chnlDomainPage1.clickModifyButton();
		chnlDomainPage1.enterNumberOfDomainCategories("2");
		chnlDomainPage1.clickSubmitButton1();
		chnlDomainPage1.clickConfirmButton();
		
		channelDomainMap.put("ChannelDomainModifyMsg",chnlDomainSub.getMessage());
		return channelDomainMap;
		
	}
	
	
	public HashMap<String, String> add_domain_WEB(){
		chnldomainCode= "AUT"+randomgen.randomAlphabets(4).toUpperCase();
		chnldomainName= "AUT"+randomgen.randomAlphabets(6).toUpperCase();
		channelDomainMap.put("DomainName", chnldomainName);
		chnlcatCode= "AUT"+randomgen.randomAlphabets(4).toUpperCase();
		chnlcatName= "AUT"+randomgen.randomAlphabets(6).toUpperCase();
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String domainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		domain_type_name = DBHandler.AccessHandler.fetchdomainTypeName(domainName);
		
		channelDomainMap.put("categoryName", chnlcatName);
		
		String uIDPrefix= randomgen.randomAlphabets(2).toUpperCase();
		
		login.UserLogin(driver, "Operator", supercategoryname);
		selectNetwork.selectNetwork();
		homepage.clickChannelDomain();
		chnlDomainSub.clickChannelDomainMgmt();
		chnlDomainPage1.clickAddButton();
		
		chnlDomainPage1.enterChannelDomainCode(chnldomainCode);
		chnlDomainPage1.enterChannelDomainName(chnldomainName);
		chnlDomainPage1.selectChannelDomainType(domain_type_name);
		chnlDomainPage1.enterNumberOfDomainCategories("1");
		chnlDomainPage1.clickSubmitButton();
		
		chnlDomainPage2.enterChannelCategoryCode(chnlcatCode);
		chnlDomainPage2.enterChannelCategoryName(chnlcatName);
		chnlDomainPage2.selectGeographicalDomain();
		chnlDomainPage2.selectRoleType();
		chnlDomainPage2.selectSourceGateway();
		
		chnlDomainPage2.enterUserIDPrefix(uIDPrefix);
		chnlDomainPage2.enterMaxTxnMsisdn();
		chnlDomainPage2.clickSubmitButton();
		chnlDomainPage2.clickConfirmButton();
		
		channelDomainMap.put("ChannelDomainCreationMsg",chnlDomainSub.getMessage());
		
		return channelDomainMap;
	}
	
	
	
	public String deleteCategory(String domain,String catCode) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNELCATGRYMGMT); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homepage.clickChannelDomain();
		chnlDomainSub.clickChannelCategoryMgmt();
		ChannelCategoryMgmtPage1.SelectDomain(domain);
		ChannelCategoryMgmtPage1.clickSubmit();
		ChannelCategoryMgmtPage1.SelectCategory(catCode);
		ChannelCategoryMgmtPage1.clickDelete();
		driver.switchTo().alert().accept();
		String message = ChannelCategoryMgmtPage1.getMsg();
		
	return message;
	
	}
	
	public String deleteDomain(String domain) throws InterruptedException{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNELDOMAINMGMT); //Getting User with Access to Add Interface
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		selectNetwork.selectNetwork();
		homepage.clickChannelDomain();
		chnlDomainSub.clickChannelDomainMgmt();
		chnlDomainPage1.selectDomain(domain);
		chnlDomainPage1.clickDeleteButton();
		driver.switchTo().alert().accept();
		String message  = chnlDomainSub.getMessage();
		
	return message;
	
	}
	
	
	public HashMap<String, String> modify_Status(String domain){
		login.UserLogin(driver, "Operator", supercategoryname);
		selectNetwork.selectNetwork();
		homepage.clickChannelDomain();
		chnlDomainSub.clickChannelDomainMgmt();
		chnlDomainPage1.selectDomain(domain);
		chnlDomainPage1.clickModifyButton();
		chnlDomainPage1.enterNumberOfDomainCategories("2");
		chnlDomainPage1.selectStatus("Suspended");
		chnlDomainPage1.clickSubmitButton1();
		chnlDomainPage1.clickConfirmButton();
		
		channelDomainMap.put("ChannelDomainModifyMsg",chnlDomainSub.getMessage());
		return channelDomainMap;
		
	}
	
}
