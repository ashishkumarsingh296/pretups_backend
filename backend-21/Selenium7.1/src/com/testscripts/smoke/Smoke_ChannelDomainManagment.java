package com.testscripts.smoke;

import org.testng.annotations.Test;
import java.util.HashMap;

import com.Features.ChannelDomain;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;

/**
 * @author lokesh.kontey
 *
 */
public class Smoke_ChannelDomainManagment extends BaseTest{
	
	String chnldomainCode;
	String chnldomainName;
	String chnlcatCode;
	String chnlcatName;
	HashMap<String, String> channelDomainMap;
	
	@Test//(priority=1)
	public void a_addDomain(){
		ChannelDomain channelDomain = new ChannelDomain(driver);	
		test = extent.createTest("[Smoke]Channel Domain Management");		
		
		currentNode=test.createNode("To verify that Super Admin is able to create Channel Domain in the system.");
		currentNode.assignCategory("Smoke");
		channelDomainMap=channelDomain.add_domain();
		String ChnlDomainCrMsg = MessagesDAO.prepareMessageByKey("domain.add.success","");
				if (channelDomainMap.get("ChannelDomainCreationMsg").equals(ChnlDomainCrMsg))
				currentNode.log(Status.PASS, "Message Validation Successful");
				else {
				currentNode.log(Status.FAIL, "Expected [" + ChnlDomainCrMsg + "] but found [" + channelDomainMap.get("ChannelDomainCreationMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
				}
	
	}
	
	@Test//(priority=2)
	public void b_modifyDomain(){
		ChannelDomain channelDomain = new ChannelDomain(driver);	
		currentNode=test.createNode("To verify that Super Admin is able to Modify Channel Domain existing in the system.");
		currentNode.assignCategory("Smoke");
		channelDomainMap= channelDomain.modify_domain();
		String ChnlDomainModMsg = MessagesDAO.prepareMessageByKey("domains.modifydomain.success","");
				if (channelDomainMap.get("ChannelDomainModifyMsg").equals(ChnlDomainModMsg))
				currentNode.log(Status.PASS, "Message Validation Successful");
				else {
				currentNode.log(Status.FAIL, "Expected [" + ChnlDomainModMsg + "] but found [" + channelDomainMap.get("ChannelDomainModifyMsg") + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
				}
	}
}
