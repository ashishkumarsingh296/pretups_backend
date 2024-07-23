package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmNotificationPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeNotificationDisplayedPage;
import com.pageobjects.channeluserspages.c2srecharge.C2STransferPage;
import com.pageobjects.channeluserspages.homepages.C2STransferSubCategoriesPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.PreferenceSubCategories;
import com.pageobjects.networkadminpages.homepage.ServiceClassPreference;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.ServicePreferencePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

public class C2STransfer extends BaseTest{
	
	public WebDriver driver;
	
	ChannelUserHomePage CHhomePage;
	Login login1;
	C2STransferPage C2STransferPage;
	C2SRechargeConfirmPage C2SRechargeConfirmPage;
	C2SRechargeConfirmNotificationPage C2SRechargeConfirmNotificationPage;
	C2SRechargeNotificationDisplayedPage C2SRechargeNotificationDisplayedPage;
	C2STransferSubCategoriesPage C2STransferSubCategoriesPage;
	C2STransactionStatus C2STransactionStatus;
	RandomGeneration randomNum;
	SystemPreferencePage sysPref;
	ServicePreferencePage servPref;
	SuperAdminHomePage suHomepage;
	NetworkAdminHomePage naHomepage;
	PreferenceSubCategories naPref;
	ServiceClassPreference naServPref;
	SelectNetworkPage networkPage;
	
	public C2STransfer(WebDriver driver) {
		this.driver = driver;
		
		CHhomePage = new ChannelUserHomePage(driver);
		login1 = new Login();
		C2STransferPage =new C2STransferPage(driver);
		C2SRechargeConfirmPage =new C2SRechargeConfirmPage(driver);
		C2SRechargeConfirmNotificationPage =new C2SRechargeConfirmNotificationPage(driver);
		C2SRechargeNotificationDisplayedPage =new C2SRechargeNotificationDisplayedPage(driver);
		C2STransferSubCategoriesPage =new C2STransferSubCategoriesPage(driver);
		C2STransactionStatus=new C2STransactionStatus();
		randomNum = new RandomGeneration();
		sysPref = new SystemPreferencePage(driver);
		servPref = new ServicePreferencePage(driver);
		suHomepage = new SuperAdminHomePage(driver);
		naHomepage = new NetworkAdminHomePage(driver);
		naPref = new PreferenceSubCategories(driver);
		naServPref = new ServiceClassPreference(driver);
		networkPage = new SelectNetworkPage(driver);
	}

	
	public void performC2STransfer(String ParentCategory, String FromCategory,String PIN,String service) {
		final String methodname = "performC2STransfer";
		Log.methodEntry(methodname, ParentCategory, FromCategory, PIN, service);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login1.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		/*C2S Transfer Page*/
		CHhomePage.clickC2STransfer();
		C2STransferSubCategoriesPage.clickC2SRecharge();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch servicename from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();

		for(int i=1;i<=totalRow;i++){
		String []values={ExcelUtility.getCellData(0,ExcelI.NAME,i),ExcelUtility.getCellData(0,ExcelI.SELECTOR_NAME,i)};
			serviceMap.put(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i),values);
		}

		String serviceName= serviceMap.get(service)[0];
		C2STransferPage.selectService(serviceName);
		if(service.equals("PPB"))
		{
			String SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Postpaid");
			C2STransferPage.enterSubMSISDN(SubMSISDN);
		}
		else{
			String SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
			C2STransferPage.enterSubMSISDN(SubMSISDN);
		}
		double b=C2STransferPage.getCurrentBalance();
		int a= (int) (b*0.05);
		int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		if(minCardSlab < a && a < midCardSlab);
		else if(a > midCardSlab){a = midCardSlab;}
		else if(a < minCardSlab){a = minCardSlab+1;}
		
		
		C2STransferPage.enterAmount(""+a);
			C2STransferPage.selectSubService(serviceMap.get(service)[1]);	
			C2STransferPage.enterPin(PIN);

			C2STransferPage.clickSubmitButton();
			C2SRechargeConfirmPage.clickSubmitButton();
			String transferID,transferStatus;
			String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
			boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
			if (notificationMsgLink==true){
				//C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
				transferID = C2SRechargeConfirmNotificationPage.transferID();
				transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
				int i=0,timetowait=150,t=0;
				while(!transferStatus.equals(successStatus)){
					try {
						Thread.sleep(timetowait);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t=timetowait+t;
					C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
					transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
					i++;
					ExtentI.Markup(ExtentColor.BLUE,"No. of times the Final notification link is clicked : "+i);
					if(i==3){Log.info("No more clicks, now leaving as the click counter reached to "+i+" | Totalwait time : "+t);break;}
				}
			}
			transferID = C2SRechargeConfirmNotificationPage.transferID();
			String trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
			transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
			
			if(transferStatus.equals(successStatus)){
				ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: "+transferID+", hence Transaction Successful");
				ExtentI.getChannelRequestDailyLogs(transferID);
				ExtentI.getOneLineTXNLogsC2S(transferID);
				ExtentI.attachCatalinaLogsForSuccess();}
			else 
				{currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status on WEB: "+transferStatus+" | TXN ID: "+transferID+" | DB TXN Status: "+trf_status);
				ExtentI.getChannelRequestDailyLogs(transferID);
				ExtentI.getOneLineTXNLogsC2S(transferID);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();}
			
			Log.methodExit(methodname);
	}
		
	
	/**
	 * MRP block time allowed and successive block time
	 */
	public void modifyMRPPreference(String trueOrfalse, boolean setduration, String duration){
		Map<String,String> usermap=UserAccess.getUserWithAccess(RolesI.MODIFY_SYSTEM_PRF);
		login1.LoginAsUser(driver, usermap.get("LOGIN_ID"), usermap.get("PASSWORD"));
		String mrpprefernce = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network code"), CONSTANT.MRP_BLOCK_TIME_ALLOWED);
		String preferenceCode1 = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.MRP_BLOCK_TIME_ALLOWED);
		boolean updateCache = false;
		new SelectNetworkPage(driver).selectNetwork();
		suHomepage.clickPreferences();
		sysPref.clickSystemPrefernce();
		
		
		if(!mrpprefernce.toUpperCase().equals(trueOrfalse.toUpperCase())){
		sysPref.selectModule("C2S");
		sysPref.selectSystemPreference();
		sysPref.clickSubmitButton();
		sysPref.setValueofSystemPreference(preferenceCode1, trueOrfalse);
		sysPref.clickModifyBtn();
		sysPref.clickConfirmBtn();
		updateCache=true;
		}
		else{Log.info("Preference for MRP_BLOCK_TIME_ALLOWED is already set as: "+trueOrfalse);}
		if(setduration){
		String preferenceCode2 = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.SUCC_BLOCK_TIME);
		String valuespreftype[] = DBHandler.AccessHandler.getTypeOFPreference("", _masterVO.getMasterValue("Network Code"), CONSTANT.SUCC_BLOCK_TIME);
		suHomepage.clickPreferences();
		sysPref.clickSystemPrefernce();
		sysPref.selectModule("C2S");
		sysPref.selectPreferenceType(valuespreftype[1]);
		sysPref.clickSubmitButton();
		servPref.setValueofServicePreference(preferenceCode2, duration);
		servPref.clickModifyBtn();
		servPref.clickConfirmBtn();
		if(valuespreftype[1].equals(PretupsI.SERVICE_CLASS_PREFERENCE_TYPE)){
			Object[][] serClassIDs = DBHandler.AccessHandler.getServiceClassID("ALL","ALL");
			Map<String,String> usermapO=UserAccess.getUserWithAccess(RolesI.MODSERVICEPREF);
			login1.LoginAsUser(driver, usermapO.get("LOGIN_ID"), usermapO.get("PASSWORD"));
			for(int i=0;i<serClassIDs.length;i++){
				networkPage.selectNetwork();
				naHomepage.clickPreferences();
				naPref.clickServiceClassPreferencelink();
				naServPref.selectServiceClass(serClassIDs[i][0].toString());
				naServPref.clicksubmitBtn();
				servPref.setValueofServicePreference(preferenceCode2, duration);
				servPref.clickModifyBtn();
				servPref.clickConfirmBtn();}
		}
		updateCache = true;}
		if(updateCache){
		new UpdateCache().updateCache();}
	}
	
	public String performC2STransfer(String ParentCategory, String FromCategory,String PIN,String service, String amount, String subMSISDN) throws IOException, InterruptedException {
		String transferID = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login1.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
		/*C2S Transfer Page*/
		CHhomePage.clickC2STransfer();
		C2STransferSubCategoriesPage.clickC2SRecharge();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();
		//HashMap to fetch service name from Excel
		Map<String,String[]> serviceMap=new HashMap<String,String[]>();
		for(int i=1;i<=totalRow;i++){
			String []values={ExcelUtility.getCellData(0,ExcelI.NAME,i),ExcelUtility.getCellData(0,ExcelI.SELECTOR_NAME,i)};
				serviceMap.put(ExcelUtility.getCellData(0,ExcelI.SERVICE_TYPE,i),values);
			}

		String serviceName= serviceMap.get(service)[0];
		C2STransferPage.selectService(serviceName);
		
		C2STransferPage.enterSubMSISDN(subMSISDN);

		C2STransferPage.enterAmount(amount);
		C2STransferPage.selectSubService(serviceMap.get(service)[1]);	
			
			if(serviceName.contains("Gift")){
				C2STransferPage.enterGifterMSISDN(UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
				if(C2STransferPage.gifterNameVisibility()){
				C2STransferPage.enterGifterName(randomNum.randomAlphabets(7));}
			}

			C2STransferPage.enterPin(PIN);
			C2STransferPage.clickSubmitButton();
			String msg = new AddChannelUserDetailsPage(driver).getActualMessage(); 
			
			if(msg!=null){
			ExtentI.Markup(ExtentColor.RED, msg);
			ExtentI.attachScreenShot();}
			else{
/*				C2SRechargeConfirmPage.clickSubmitButton();
	
				boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
				if (notificationMsgLink==true){
					C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
				}
				String transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				transferID = C2SRechargeConfirmNotificationPage.transferID();
				if(transferStatus.equals(successStatus)){
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: "+transferID+", hence Transaction Successful");
					ExtentI.attachCatalinaLogsForSuccess();}
				else 
					{currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status : "+transferStatus+" . TXN ID: "+transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();
					}*/
				C2SRechargeConfirmPage.clickSubmitButton();
				String transferStatus;
				String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
				boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
				if (notificationMsgLink==true){
					//C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
					transferID = C2SRechargeConfirmNotificationPage.transferID();
					transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
					int i=0,timetowait=150,t=0;
					while(!transferStatus.equals(successStatus)){
						try {
							Thread.sleep(timetowait);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						t=timetowait+t;
						C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
						transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
						i++;
						ExtentI.Markup(ExtentColor.BLUE,"No. of times the Final notification link is clicked : "+i);
						if(i==3){Log.info("No more clicks, now leaving as the click counter reached to "+i+" | Totalwait time : "+t);break;}
					}
				}
				transferID = C2SRechargeConfirmNotificationPage.transferID();
				String trf_status = DBHandler.AccessHandler.fetchTransferStatus(transferID);
				transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
				
				if(transferStatus.equals(successStatus)){
					ExtentI.Markup(ExtentColor.GREEN, "Transaction Status Found as: " + transferStatus + " with TXN ID: "+transferID+", hence Transaction Successful");
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogsForSuccess();}
				else 
					{
					Assertion.assertFail("Transaction is not successful. Transfer Status on WEB: "+transferStatus+" | TXN ID: "+transferID+" | DB TXN Status: "+trf_status);
					ExtentI.getChannelRequestDailyLogs(transferID);
					ExtentI.getOneLineTXNLogsC2S(transferID);
					ExtentI.attachCatalinaLogs();
					ExtentI.attachScreenShot();}
				
			}
			return transferID;
	}
		
}
