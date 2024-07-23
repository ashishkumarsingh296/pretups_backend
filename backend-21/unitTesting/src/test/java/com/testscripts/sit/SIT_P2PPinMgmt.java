package com.testscripts.sit;

import org.testng.annotations.Test;

import com.Features.P2PSubscribers;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.sshmanager.SSHService;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_P2PPinMgmt extends BaseTest{

	static boolean TestCaseCounter = false;
	String assignCategory = "SIT";
	
	@Test
	public void _01_PinMgmt(){
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPINMGMT1").getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPINMGMT1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String subsMsisdn = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE", "Y");
		
		SSHService.startMessageSentLogMonitor();
		new P2PSubscribers(driver).p2pSendPin(subsMsisdn);
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected = MessagesDAO.prepareMessageByKey("p2psubscriber.unblockpin.msg.sendmsg.success", "");
		Validator.messageCompare(actual, expected);
		SSHService.stopMessageSentLogMonitor();
	}

	@Test
	public void _02_PinMgmt(){
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PPINMGMT2").getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PPINMGMT2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String subsMsisdn = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE", "Y");
		SSHService.startMessageSentLogMonitor();
		new P2PSubscribers(driver).p2pResetPin(subsMsisdn);
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected = MessagesDAO.prepareMessageByKey("p2psubscribe.unblockpin.msg.resetsuccess", "");
		Validator.messageCompare(actual, expected);
		SSHService.stopMessageSentLogMonitor();
	}
	
	
	@Test
	public void _03_PinMgmt(){
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PDEREGSUBS1").getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PDEREGSUBS1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String subsMsisdn = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE", "Y");
		
		SSHService.startMessageSentLogMonitor();
		new P2PSubscribers(driver).p2pderegistersubscriber(subsMsisdn);
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected = MessagesDAO.prepareMessageByKey("p2psubscriber.deletesubscriber.msg.success", "");
		Validator.messageCompare(actual, expected);
		SSHService.stopMessageSentLogMonitor();
	}
	
	@Test
	public void _04_PinMgmt(){
		Log.startTestCase(this.getClass().getName());

		if (TestCaseCounter == false) { 
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITP2PSUSRESSERV1").getModuleCode());
			TestCaseCounter = true;
		}

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PSUSRESSERV1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String subsMsisdn = DBHandler.AccessHandler.getP2PSubscriberMSISDN("PRE", "Y");
		SSHService.startMessageSentLogMonitor();
		new P2PSubscribers(driver).suspendService(subsMsisdn);
		String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected = MessagesDAO.prepareMessageByKey("p2psubscriber.suspendsubscriberservice.msg.success", "");
		Validator.messageCompare(actual, expected);
		SSHService.stopMessageSentLogMonitor();
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITP2PSUSRESSERV2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		SSHService.startMessageSentLogMonitor();
		new P2PSubscribers(driver).resumeService(subsMsisdn);
		String actual1 = new AddChannelUserDetailsPage(driver).getActualMessage();
		String expected1 = MessagesDAO.prepareMessageByKey("p2psubscriber.resumesubscriberservice.msg.success", "");
		Validator.messageCompare(actual1, expected1);
		SSHService.stopMessageSentLogMonitor();
	}
	
}
