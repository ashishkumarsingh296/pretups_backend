/**
 * 
 */
package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmNotificationPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeConfirmPage;
import com.pageobjects.channeluserspages.c2srecharge.C2SRechargeNotificationDisplayedPage;
import com.pageobjects.channeluserspages.c2srecharge.C2STransferPage;
import com.pageobjects.channeluserspages.homepages.C2STransferSubCategoriesPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.customercarepages.homepage.CustomerCarehomepage;
import com.pageobjects.customercarepages.privaterecharge.PrivateRecahrgeDeactivationpage;
import com.pageobjects.customercarepages.privaterecharge.PrivateRecahrgeEnquirypage;
import com.pageobjects.customercarepages.privaterecharge.PrivateRecahrgeModificationpage;
import com.pageobjects.customercarepages.privaterecharge.PrivateRecahrgeRegistrationpage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class PrivateRecharge extends BaseTest{

	CustomerCarehomepage ccehomepage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	PrivateRecahrgeRegistrationpage pvtRcRegistration;
	PrivateRecahrgeModificationpage pvtRcModification;
	PrivateRecahrgeEnquirypage pvtRcEnquiry;
	PrivateRecahrgeDeactivationpage pvtRcDeactivation;
	
	ChannelUserHomePage CHhomePage;
	C2STransferPage C2STransferPage;
	C2SRechargeConfirmPage C2SRechargeConfirmPage;
	C2SRechargeConfirmNotificationPage C2SRechargeConfirmNotificationPage;
	C2SRechargeNotificationDisplayedPage C2SRechargeNotificationDisplayedPage;
	C2STransferSubCategoriesPage C2STransferSubCategoriesPage;
	C2STransactionStatus C2STransactionStatus;
	
	Map<String, String> userAccessMap;
	WebDriver driver =null;
	String SubMSISDN;
	
	public PrivateRecharge(WebDriver driver){
		this.driver=driver;
		ccehomepage = new CustomerCarehomepage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		pvtRcRegistration = new PrivateRecahrgeRegistrationpage(driver);
		pvtRcModification = new PrivateRecahrgeModificationpage(driver);
		pvtRcEnquiry = new PrivateRecahrgeEnquirypage(driver);
		pvtRcDeactivation = new PrivateRecahrgeDeactivationpage(driver);
		
		CHhomePage = new ChannelUserHomePage(driver);
		C2STransferPage =new C2STransferPage(driver);
		C2SRechargeConfirmPage =new C2SRechargeConfirmPage(driver);
		C2SRechargeConfirmNotificationPage =new C2SRechargeConfirmNotificationPage(driver);
		C2SRechargeNotificationDisplayedPage =new C2SRechargeNotificationDisplayedPage(driver);
		C2STransferSubCategoriesPage =new C2STransferSubCategoriesPage(driver);
		C2STransactionStatus=new C2STransactionStatus();
		
	}
	
	public String privateRechargeRegistration(boolean gentype){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.PRIVATE_RECH_REG); 
		SubMSISDN=UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		ccehomepage.clickPrivateRecharge();
		ccehomepage.clickPrivateRcRegistration();
		pvtRcRegistration.enterSubscriberMSISDN(SubMSISDN);
		pvtRcRegistration.enterSubsriberName("AUT"+randStr.randomAlphabets(4));
		pvtRcRegistration.selectTypeOfGeneration(gentype);
		if(gentype){pvtRcRegistration.enterSubsriberSID(UniqueChecker.UC_SubsSID());}// if true(i.e. MANUAL) then enter SID else not.
		pvtRcRegistration.clickSubmitBtn();
		pvtRcRegistration.clickRegisterBtn();
		String subscriberSID = DBHandler.AccessHandler.getsubscriberSIDviaMSISDN(SubMSISDN);
		Log.info("SID fetched from database: "+subscriberSID);
		return subscriberSID;
	}
	
	public void privateRechargemodification(boolean gentype,String msisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.PRIVATE_RECH_MOD); 
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		ccehomepage.clickPrivateRecharge();
		ccehomepage.clickPrivateRcModification();
		pvtRcModification.enterSubscriberMSISDN(msisdn);
		pvtRcModification.clickSubmitBtn();
		pvtRcModification.enterSubsriberName("AUT"+randStr.randomAlphabets(4));
		pvtRcModification.selectTypeOfGeneration(gentype);
		if(gentype){pvtRcModification.enterSubsriberSID(UniqueChecker.UC_SubsSID());}// if true(i.e. MANUAL) then enter SID else not.
		pvtRcModification.clickSubmitnextBtn();
		pvtRcModification.clickModifyBtn();
		String subscriberSID = DBHandler.AccessHandler.getsubscriberSIDviaMSISDN(msisdn);
		Log.info("SID fetched from database: "+subscriberSID);
	}
	
	public void privateRechargeEnquiry(String msisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.PRIVATE_RECH_ENQUIRY); 
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		ccehomepage.clickPrivateRecharge();
		ccehomepage.clickPrivateRcEnquiry();
		pvtRcEnquiry.enterSubscriberMSISDN(msisdn);
		pvtRcEnquiry.clicksubmitbtn();
		pvtRcEnquiry.compareSubsMSISDN(msisdn);
		pvtRcEnquiry.compareSubsSID(msisdn);
	}
	
	public void privateRechargeDeactivation(String msisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.PRIVATE_RECH_DEACTIVATION); 
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		ccehomepage.clickPrivateRecharge();
		ccehomepage.clickPrivateRcDeactivation();
		pvtRcDeactivation.enterSubscriberMSISDN(msisdn);
		pvtRcDeactivation.enterSubscriberSID(msisdn);
		pvtRcDeactivation.clickdeactivatebtn();
		pvtRcDeactivation.clickconfirmdeactivatebtn();
	}
	
	public void privateRechargeRegistration(boolean gentype, String msisdn, String subscriberSID){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.PRIVATE_RECH_REG); 
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		ccehomepage.clickPrivateRecharge();
		ccehomepage.clickPrivateRcRegistration();
		pvtRcRegistration.enterSubscriberMSISDN(msisdn);
		pvtRcRegistration.enterSubsriberName("AUT"+randStr.randomAlphabets(4));
		pvtRcRegistration.selectTypeOfGeneration(gentype);
		if(gentype){pvtRcRegistration.enterSubsriberSID(subscriberSID);}// if true(i.e. MANUAL) then enter SID else not.
		pvtRcRegistration.clickSubmitBtn();
		pvtRcRegistration.clickRegisterBtn();
	}
	
	public void performC2STransferToSID(String ParentCategory, String FromCategory,String PIN,String service, String SID) throws IOException, InterruptedException
	{

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		login.UserLogin(driver,"ChannelUser",ParentCategory, FromCategory);
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
		
		//C2STransferPage.enterSubMSISDN(DBHandler.AccessHandler.getsubscriberSIDviaMSISDN(DBHandler.AccessHandler.fetchSubscriberMSISDNRandomAlias("PRE")));
		C2STransferPage.enterSubMSISDN(SID);
		double b=C2STransferPage.getCurrentBalance();
		int a= (int) (b*0.05);
		int minCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab1"));
		int midCardSlab = Integer.parseInt(_masterVO.getProperty("CardGroupSlab3"));
		if(minCardSlab < a && a < midCardSlab);
		else if(a > midCardSlab){a = midCardSlab;}
		else if(a < minCardSlab){a = minCardSlab;}
		
		C2STransferPage.enterAmount(""+a);
			C2STransferPage.selectSubService(serviceMap.get(service)[1]);	
			C2STransferPage.enterPin(PIN);

			C2STransferPage.clickSubmitButton();
			C2SRechargeConfirmPage.clickSubmitButton();

			/*boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
			if (notificationMsgLink==true){
				C2SRechargeConfirmNotificationPage.clickNotificationMsgLink();
			}
			String transferStatus = C2SRechargeConfirmNotificationPage.transferStatus();
			if(transferStatus.equals("SUCCESS")){
				Log.info("Transaction is successful.");
			}
			else{
				BaseTest.currentNode.log(Status.FAIL, "Transaction is not successful. Transfer Status : "+transferStatus);
				ExtentI.attachCatalinaLogs();
				ExtentI.attachScreenShot();
			}*/
			String transferID,transferStatus;
			String successStatus = DBHandler.AccessHandler.getTransactionStatusByKey("200", "C2S_STATUS");
			boolean notificationMsgLink=C2SRechargeConfirmNotificationPage.notificationMsgLinkVisibility(); 
			if (notificationMsgLink==true){
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
	}
	
	
}
