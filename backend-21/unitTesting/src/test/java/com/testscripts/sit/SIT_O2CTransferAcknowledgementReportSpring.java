package com.testscripts.sit;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportO2C.O2CTransferAcknowledgementSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

public class SIT_O2CTransferAcknowledgementReportSpring extends BaseTest{

	static boolean TestCaseCounter = false;
	static String domainCode;
		
		@Test //Spring module or not
		public void a_O2CTrfAck(){
			final String methodname = "a_O2CTrfAck";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Acknowledgement");
				TestCaseCounter = true;
			}	
			O2CTransferAcknowledgementSpring o2cTrfdetail = new O2CTransferAcknowledgementSpring(driver);
			OperatorToChannelMap o2cmap = new OperatorToChannelMap();
			Login login1 = new Login();
			boolean spring=false;
			String domainName = o2cmap.getOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode")).get("TO_DOMAIN");
			domainCode = DBHandler.AccessHandler.getDomainCode(domainName);
			currentNode = test.createNode("To verify that O2C transfer Acknowledgement screen visible in spring.");
			currentNode.assignCategory("SIT");	
			
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.O2C_TRANSFER_ACKNOWLEDGEMENT,domainCode);  // wht O2C_TRANSFER_ACKNOWLEDGEMENT
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
			new ChannelAdminHomePage(driver).clickChannelTrfO2CReport();
			o2cTrfdetail.clickO2CTransferDetailslink();
			try{
			spring = driver.findElement(By.xpath("//span[@id='servertime']")).isDisplayed();
			if(spring)currentNode.log(Status.PASS, "The module is spring.");
			else if(!spring) currentNode.log(Status.FAIL, "Module is not spring.");}
			catch(Exception e){Log.info("Error while looking for spring module.");
			ExtentI.attachScreenShot();
			ExtentI.attachCatalinaLogs();}
		}	
		
		
	/*	@Test
		public void b_O2CTrfAck(){
			final String methodname = "b_O2CTrfAck";
			Log.startTestCase(methodname);
			
			if (TestCaseCounter == false) {
				test=extent.createTest("[SIT]O2C Transfer Acknowledgement");
				TestCaseCounter = true;
			}	
			O2CTransferAcknowledgementSpring o2cTrfdetail = new O2CTransferAcknowledgementSpring(driver);
			currentNode = test.createNode("To verify that proper error message appears when Category is not selected.");
			currentNode.assignCategory("SIT");	
			String[] data={"","Enter Transfer Number","","","","","",""};
			String expected = MessagesDAO.getLabelByKey("pretups.O2Ctransferacknowledgement.msisdn.required");
			new UserClosingBalanceSpringFeatures(driver).checkUserClosingBalanceReport("channel",domainCode, data);
				String actual = o2cTrfdetail.fetcherrormessage("transferNum");
				Validator.messageCompare(actual, expected);
			
		}
		*/
		
		
		
		
	
		
		
	}
	

