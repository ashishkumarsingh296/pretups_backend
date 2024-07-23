package com.testscripts.smoke;

import java.text.MessageFormat;

import org.testng.annotations.Test;

import com.Features.GeogaphicalDomainManagement;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SMOKE_GEOGRAPHICAL_DOMAIN_MANAGEMENT)
public class Smoke_AddGeographicalDomain extends BaseTest {

    String MasterSheetPath;

    @Test
    @TestManager(TestKey = "PRETUPS-283") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void Test_AddGeographicalDomain() {
        final String methodName = "Test_AddGeographicalDomain";
        Log.startTestCase(methodName);

        GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
        String geographicalDomainTypeName[] = geogaphicalDomainManagement.getGeographyTypes();
        int size = geographicalDomainTypeName.length;
        String domainTypeName;
        String parentGeography = geographicalDomainTypeName[0];

        for (int i = 0; i < size; i++) {

            // Test Case Number 1: To verify Operator is able to create Zone / Area / Sub Area
            domainTypeName = geographicalDomainTypeName[i];
            currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGEODOMAINMGMT1").getExtentCase(), domainTypeName)).assignCategory(TestCategory.SMOKE);
            String[] domainData = geogaphicalDomainManagement.addGeographicalDomain(parentGeography, domainTypeName);

            // Case Number 2: Message Validation
            currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SGEODOMAINMGMT1").getExtentCase(), domainTypeName)).assignCategory(TestCategory.SMOKE);
            String addGeographySuccessMsg = MessagesDAO.prepareMessageByKey("grphdomain.addgrphdomain.msg.addsuccess");
            Assertion.assertEquals(domainData[4], addGeographySuccessMsg);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
}