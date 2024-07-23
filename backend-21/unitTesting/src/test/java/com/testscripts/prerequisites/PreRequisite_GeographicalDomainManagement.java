package com.testscripts.prerequisites;

import java.text.MessageFormat;

import org.testng.annotations.Test;

import com.Features.GeogaphicalDomainManagement;
import com.classes.BaseTest;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

/**
 * @author Ayush Abhijeet This class creates Geographical Domains. Dependency:
 * Database Connectivity for Geographical Domains Types.
 **/
@ModuleManager(name = Module.PREREQUISITE_GEOGRAPHICAL_DOMAIN_MANAGEMENT)
public class PreRequisite_GeographicalDomainManagement extends BaseTest {

    @Test
    @TestManager(TestKey = "PRETUPS-410") //TO BE UNCOMMMENTED WITH JIRA TEST CASE ID
    public void Test_AddGeographicalDomain() {
        final String methodName = "Test_AddGeographicalDomain";
        Log.startTestCase(methodName);

        // Initializing the Division Department Feature Class with current Driver
        GeogaphicalDomainManagement geogaphicalDomainManagement = new GeogaphicalDomainManagement(driver);
        boolean CLIENTFEATURE = Boolean.parseBoolean(_masterVO.getClientDetail("DEFAULTGRADEFEATURE"));

        //Creating WorkSheet in DataProvider
        geogaphicalDomainManagement.createGeographicalSheetHeader();
        String geographicalDomainTypeName[] = geogaphicalDomainManagement.getGeographyTypes();
        int size = geographicalDomainTypeName.length;
        String domainTypeName;
        String parentGeography = geographicalDomainTypeName[0];
        for (int i = 0; i < size; i++) {

            if (!CLIENTFEATURE) {
                // Cases for Grade Creation as the Client Library DEFAULTGRADEFEATURE is false
                currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGEODOMAINMGMT1").getExtentCase(), geographicalDomainTypeName[i])).assignCategory(TestCategory.PREREQUISITE);
                domainTypeName = geographicalDomainTypeName[i];
                String[] domainData = geogaphicalDomainManagement.addGeographicalDomain(parentGeography, domainTypeName);
                geogaphicalDomainManagement.writeGeographicalData(domainData,i);
            } else {
                // Cases for validating Default Grades as the Client Library DEFAULTGRADEFEATURE is true
                currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PGEODOMAINMGMT2").getExtentCase(), geographicalDomainTypeName[i])).assignCategory(TestCategory.PREREQUISITE);
                domainTypeName = geographicalDomainTypeName[i];
                String[] domainData = geogaphicalDomainManagement.validateDefaultGeographicalDomain(_masterVO.getMasterValue(MasterI.NETWORK_CODE), domainTypeName);
                geogaphicalDomainManagement.writeGeographicalData(domainData,i);
            }

        }

        Log.endTestCase(methodName);
    }
}