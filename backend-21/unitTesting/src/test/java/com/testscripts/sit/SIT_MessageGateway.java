package com.testscripts.sit;

import java.text.MessageFormat;

import org.testng.annotations.Test;

import com.Features.messageGateway;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.PretupsI;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

public class SIT_MessageGateway extends BaseTest{
	
	
	static boolean TestCaseCounter = false;
	String gatewayCode = null;
	
	
	@Test
	public void a_AddGateway() throws InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITMSGGTW1");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		messageGateway messageGateway = new messageGateway(driver);
		
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),PretupsI.GATEWAY_TYPE_WEB)) ;
		//currentNode=test.createNode("To verify that Super Admin is able to a Message Gateway for: " +(PretupsI.GATEWAY_TYPE_WEB));
		currentNode.assignCategory("SIT");
		
		String [] result = messageGateway.addmessageGateway();
		gatewayCode = result[0];
		Log.info("Message Gateway created as:" +result[0]);
		
        String expected = MessagesDAO.prepareMessageByKey("gateway.operation.msg.addsuccess");
		
		Validator.messageCompare(result[1], expected);
		
	}
	
	
	
	@Test
	public void b_ModifyGateway() throws InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITMSGGTW2");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		messageGateway messageGateway = new messageGateway(driver);
		
		currentNode=test.createNode(CaseMaster1.getExtentCase()) ;
		//currentNode=test.createNode("To verify that Super Admin is able to modify a message Gateway");
		currentNode.assignCategory("SIT");
		String result = messageGateway.ModifyMessageGateway(gatewayCode);
		
		Log.info("Message Gateway modified as:" +gatewayCode);
		
        String expected = MessagesDAO.prepareMessageByKey("gateway.operation.msg.updatesuccess");
		
		Validator.messageCompare(result, expected);
		
	}
	
	@Test
	public void c_AssociateGatewayMapping() throws InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITMSGGTW3");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		messageGateway messageGateway = new messageGateway(driver);
		
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to associate message gateway Mapping");
		currentNode.assignCategory("SIT");
		String actual = messageGateway.associateMapping(gatewayCode);
		
        String expected = MessagesDAO.prepareMessageByKey("gateway.messagegatewaymapping.recordsaved");
		
		Validator.messageCompare(actual, expected);
		
	}	
	
	@Test
	public void d_ModifyGatewayStatusSuspend() throws InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITMSGGTW4");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		messageGateway messageGateway = new messageGateway(driver);
		
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to suspend a message Gateway");
		currentNode.assignCategory("SIT");
		String actual = messageGateway.ModifyMessageGatewayStatusSuspend(gatewayCode);
		
		Log.info("Message Gateway modified as:" +gatewayCode);
		
        String expected = MessagesDAO.prepareMessageByKey("gateway.operation.msg.updatesuccess");
		
		Validator.messageCompare(actual, expected);
		
	}
	
	
	
	@Test
	public void e_DeleteGatewayMapping() throws InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITMSGGTW5");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		messageGateway messageGateway = new messageGateway(driver);
		
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to delete message gateway Mapping");
		currentNode.assignCategory("SIT");
		String actual = messageGateway.deleteGatewayMapping(gatewayCode);
		
        String expected = MessagesDAO.prepareMessageByKey("gateway.messagegatewaymapping.recorddeleted");
		
		Validator.messageCompare(actual, expected);
		
	}
	
	
	
	@Test
	public void f_DeleteGateway() throws InterruptedException{
		
		Log.startTestCase(this.getClass().getName());

		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("SITMSGGTW6");

		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+ CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}

		messageGateway messageGateway = new messageGateway(driver);
		
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		//currentNode=test.createNode("To verify that Super Admin is able to delete message gateway");
		currentNode.assignCategory("SIT");
		String actual = messageGateway.DeleteMessageGateway(gatewayCode);
		
        String expected = MessagesDAO.prepareMessageByKey("gateway.deleteoperation.msg.success");
		
		Validator.messageCompare(actual, expected);
		
	}

}
