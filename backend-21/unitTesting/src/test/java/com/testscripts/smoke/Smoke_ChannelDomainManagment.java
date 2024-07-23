package com.testscripts.smoke;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.ChannelDomain;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_CHANNEL_DOMAIN_MANAGEMENT)
public class Smoke_ChannelDomainManagment extends BaseTest {
   
    private HashMap<String, String> channelDomainMap;
    boolean isRoleExists;

    @Test
    @TestManager(TestKey = "PRETUPS-521") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void a_addDomain() {
        final String methodName = "Test_addDomain";
        Log.startTestCase(methodName);
        isRoleExists = ExcelUtility.isRoleExists(RolesI.CHANNELDOMAINMGMT);
        
        ChannelDomain channelDomain = new ChannelDomain(driver);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SCHNLDOMMGMT1");

        currentNode = test.createNode(CaseMaster.getExtentCase()).assignCategory(TestCategory.SMOKE);//"To verify that Super Admin is able to create Channel Domain in the system.");
        if(isRoleExists)
        {
        channelDomainMap = channelDomain.add_domain();
        String ChnlDomainCrMsg = MessagesDAO.prepareMessageByKey("domain.add.success", "");
        Assertion.assertEquals(channelDomainMap.get("ChannelDomainCreationMsg"), ChnlDomainCrMsg);
        }
        else
        {
        	Assertion.assertSkip("Channel Domain Management Module not available in system.") ; 
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test
    @TestManager(TestKey = "PRETUPS-523") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void b_ModifyDomain() {
        final String methodName = "Test_ModifyDomain";
        Log.startTestCase(methodName);
        isRoleExists = ExcelUtility.isRoleExists(RolesI.CHANNELDOMAINMGMT);
        ChannelDomain channelDomain = new ChannelDomain(driver);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SCHNLDOMMGMT2");
        currentNode = test.createNode(CaseMaster.getExtentCase()).assignCategory(TestCategory.SMOKE);
        if(isRoleExists)
        {
        channelDomainMap = channelDomain.modify_domain();
        String ChnlDomainModMsg = MessagesDAO.prepareMessageByKey("domains.modifydomain.success", "");
        Assertion.assertEquals(channelDomainMap.get("ChannelDomainModifyMsg"), ChnlDomainModMsg);
        }
        else
        {
        	Assertion.assertSkip("Channel Domain Management Module not available in system.") ; 
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test
    @TestManager(TestKey = "PRETUPS-1845") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void c_ModifyDomainStatus() {
        final String methodName = "c_ModifyDomainStatus";
        Log.startTestCase(methodName);
        isRoleExists = ExcelUtility.isRoleExists(RolesI.CHANNELDOMAINMGMT);
        ChannelDomain channelDomain = new ChannelDomain(driver);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SCHNLDOMMGMT3");
        currentNode = test.createNode(CaseMaster.getExtentCase()).assignCategory(TestCategory.SMOKE);
        if(isRoleExists)
        {
        channelDomainMap = channelDomain.add_domain();
        channelDomainMap = channelDomain.modify_Status(channelDomainMap.get("DomainName"));
        String ChnlDomainModMsg = MessagesDAO.prepareMessageByKey("domains.modifydomain.success", "");
        Assertion.assertEquals(channelDomainMap.get("ChannelDomainModifyMsg"), ChnlDomainModMsg);
        }
        else
        {
        	Assertion.assertSkip("Channel Domain Management Module not available in system.") ; 
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
}