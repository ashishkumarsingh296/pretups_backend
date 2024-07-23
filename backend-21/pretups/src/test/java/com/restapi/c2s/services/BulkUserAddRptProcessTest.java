package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStatusRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {BulkUserAddRptProcess.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class BulkUserAddRptProcessTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private BulkUserAddRptProcess bulkUserAddRptProcess;

    /**
     * Method under test: {@link BulkUserAddRptProcess#searchBulkUserAddRpt(BulkUserAddRptReqDTO, ChannelUserVO, BulkUserAddStatusRptResp)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testSearchBulkUserAddRpt() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        BulkUserAddRptReqDTO bulkUserAddRptReqDTO = new BulkUserAddRptReqDTO();
        bulkUserAddRptReqDTO.setBatchNo("Batch No");
        bulkUserAddRptReqDTO.setDispHeaderColumnList(new ArrayList<>());
        bulkUserAddRptReqDTO.setDomain("Domain");
        bulkUserAddRptReqDTO.setExtnwcode("Extnwcode");
        bulkUserAddRptReqDTO.setFileName("foo.txt");
        bulkUserAddRptReqDTO.setFileType("File Type");
        bulkUserAddRptReqDTO.setFromDate("2020-03-01");
        bulkUserAddRptReqDTO.setGeography("Geography");
        bulkUserAddRptReqDTO.setLocale(Locale.getDefault());
        bulkUserAddRptReqDTO.setMsisdn("Msisdn");
        bulkUserAddRptReqDTO.setOffline(true);
        bulkUserAddRptReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        bulkUserAddRptReqDTO.setProductCode("Product Code");
        bulkUserAddRptReqDTO.setReqTab("Req Tab");
        bulkUserAddRptReqDTO.setToDate("2020-03-01");
        bulkUserAddRptReqDTO.setUserId("42");
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();

        BulkUserAddStatusRptResp response = new BulkUserAddStatusRptResp();
        response.setAdditionalProperties(new HashMap<>());
        response.setBulkUserAddStatusRptList(new ArrayList<>());

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        response.setErrorMap(errorMap);
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        bulkUserAddRptProcess.searchBulkUserAddRpt(bulkUserAddRptReqDTO, channelUserVO, response);
    }

    /**
     * Method under test: {@link BulkUserAddRptProcess#validateInputs(Connection, BulkUserAddRptReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        BulkUserAddRptProcess bulkUserAddRptProcess = new BulkUserAddRptProcess();
        ////Connection con = mock(Connection.class);
        JUnitConfig.init();
        BulkUserAddRptReqDTO bulkUserAddRptReqDTO = new BulkUserAddRptReqDTO();
        bulkUserAddRptReqDTO.setBatchNo("Batch No");
        bulkUserAddRptReqDTO.setDispHeaderColumnList(new ArrayList<>());
        bulkUserAddRptReqDTO.setDomain("Domain");
        bulkUserAddRptReqDTO.setExtnwcode("Extnwcode");
        bulkUserAddRptReqDTO.setFileName("foo.txt");
        bulkUserAddRptReqDTO.setFileType("File Type");
        bulkUserAddRptReqDTO.setFromDate("2020-03-01");
        bulkUserAddRptReqDTO.setGeography("Geography");
        bulkUserAddRptReqDTO.setLocale(Locale.getDefault());
        bulkUserAddRptReqDTO.setMsisdn("Msisdn");
        bulkUserAddRptReqDTO.setOffline(true);
        bulkUserAddRptReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        bulkUserAddRptReqDTO.setProductCode("Product Code");
        bulkUserAddRptReqDTO.setReqTab("Req Tab");
        bulkUserAddRptReqDTO.setToDate("2020-03-01");
        bulkUserAddRptReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        bulkUserAddRptProcess.validateInputs(JUnitConfig.getConnection(), bulkUserAddRptReqDTO);
    }

    /**
     * Method under test: {@link BulkUserAddRptProcess#validateInputs(Connection, BulkUserAddRptReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        BulkUserAddRptProcess bulkUserAddRptProcess = new BulkUserAddRptProcess();

        BulkUserAddRptReqDTO bulkUserAddRptReqDTO = new BulkUserAddRptReqDTO();
        bulkUserAddRptReqDTO.setBatchNo("Batch No");
        bulkUserAddRptReqDTO.setDispHeaderColumnList(new ArrayList<>());
        bulkUserAddRptReqDTO.setDomain("Domain");
        bulkUserAddRptReqDTO.setExtnwcode("Extnwcode");
        bulkUserAddRptReqDTO.setFileName("foo.txt");
        bulkUserAddRptReqDTO.setFileType("File Type");
        bulkUserAddRptReqDTO.setFromDate("2020-03-01");
        bulkUserAddRptReqDTO.setGeography("Geography");
        bulkUserAddRptReqDTO.setLocale(Locale.getDefault());
        bulkUserAddRptReqDTO.setMsisdn("Msisdn");
        bulkUserAddRptReqDTO.setOffline(true);
        bulkUserAddRptReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        bulkUserAddRptReqDTO.setProductCode("Product Code");
        bulkUserAddRptReqDTO.setReqTab("Req Tab");
        bulkUserAddRptReqDTO.setToDate("2020-03-01");
        bulkUserAddRptReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        bulkUserAddRptProcess.validateInputs(null, bulkUserAddRptReqDTO);
    }

    /**
     * Method under test: {@link BulkUserAddRptProcess#getDisplayListColumns(Locale)}
     */
    @Test
    public void testGetDisplayListColumns() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        BulkUserAddRptProcess bulkUserAddRptProcess = new BulkUserAddRptProcess();
        List actualDisplayListColumns = bulkUserAddRptProcess.getDisplayListColumns(Locale.getDefault());
        assertEquals(31, actualDisplayListColumns.size());
        Object getResult = actualDisplayListColumns.get(5);
        assertNull(((DispHeaderColumn) getResult).getDisplayName());
        Object getResult2 = actualDisplayListColumns.get(25);
        assertNull(((DispHeaderColumn) getResult2).getDisplayName());
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.grouprolecode",
                ((DispHeaderColumn) getResult2).getColumnName());
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.shortname", ((DispHeaderColumn) getResult).getColumnName());
        Object getResult3 = actualDisplayListColumns.get(2);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.usernameprefix",
                ((DispHeaderColumn) getResult3).getColumnName());
        Object getResult4 = actualDisplayListColumns.get(29);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.lowbalalertallow",
                ((DispHeaderColumn) getResult4).getColumnName());
        Object getResult5 = actualDisplayListColumns.get(28);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.mpayprofileid",
                ((DispHeaderColumn) getResult5).getColumnName());
        Object getResult6 = actualDisplayListColumns.get(4);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.lastname", ((DispHeaderColumn) getResult6).getColumnName());
        Object getResult7 = actualDisplayListColumns.get(27);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.mcomorceflag",
                ((DispHeaderColumn) getResult7).getColumnName());
        Object getResult8 = actualDisplayListColumns.get(26);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.grade", ((DispHeaderColumn) getResult8).getColumnName());
        Object getResult9 = actualDisplayListColumns.get(3);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.firstname",
                ((DispHeaderColumn) getResult9).getColumnName());
        Object getResult10 = actualDisplayListColumns.get(1);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.parentmsisdn",
                ((DispHeaderColumn) getResult10).getColumnName());
        Object getResult11 = actualDisplayListColumns.get(30);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.remarks", ((DispHeaderColumn) getResult11).getColumnName());
        Object getResult12 = actualDisplayListColumns.get(0);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.parentloginid",
                ((DispHeaderColumn) getResult12).getColumnName());
        assertNull(((DispHeaderColumn) getResult8).getDisplayName());
        assertNull(((DispHeaderColumn) getResult9).getDisplayName());
        assertNull(((DispHeaderColumn) getResult11).getDisplayName());
        assertNull(((DispHeaderColumn) getResult12).getDisplayName());
        assertNull(((DispHeaderColumn) getResult4).getDisplayName());
        assertNull(((DispHeaderColumn) getResult5).getDisplayName());
        assertNull(((DispHeaderColumn) getResult7).getDisplayName());
        assertNull(((DispHeaderColumn) getResult10).getDisplayName());
        assertNull(((DispHeaderColumn) getResult3).getDisplayName());
        assertNull(((DispHeaderColumn) getResult6).getDisplayName());
    }

    /**
     * Method under test: {@link BulkUserAddRptProcess#getDisplayListColumns(Locale)}
     */
    @Test
    public void testGetDisplayListColumns2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        BulkUserAddRptProcess bulkUserAddRptProcess = new BulkUserAddRptProcess();
        List actualDisplayListColumns = bulkUserAddRptProcess
                .getDisplayListColumns(Locale.getDefault(Locale.Category.DISPLAY));
        assertEquals(31, actualDisplayListColumns.size());
        Object getResult = actualDisplayListColumns.get(5);
        assertNull(((DispHeaderColumn) getResult).getDisplayName());
        Object getResult2 = actualDisplayListColumns.get(25);
        assertNull(((DispHeaderColumn) getResult2).getDisplayName());
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.grouprolecode",
                ((DispHeaderColumn) getResult2).getColumnName());
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.shortname", ((DispHeaderColumn) getResult).getColumnName());
        Object getResult3 = actualDisplayListColumns.get(2);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.usernameprefix",
                ((DispHeaderColumn) getResult3).getColumnName());
        Object getResult4 = actualDisplayListColumns.get(29);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.lowbalalertallow",
                ((DispHeaderColumn) getResult4).getColumnName());
        Object getResult5 = actualDisplayListColumns.get(28);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.mpayprofileid",
                ((DispHeaderColumn) getResult5).getColumnName());
        Object getResult6 = actualDisplayListColumns.get(4);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.lastname", ((DispHeaderColumn) getResult6).getColumnName());
        Object getResult7 = actualDisplayListColumns.get(27);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.mcomorceflag",
                ((DispHeaderColumn) getResult7).getColumnName());
        Object getResult8 = actualDisplayListColumns.get(26);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.grade", ((DispHeaderColumn) getResult8).getColumnName());
        Object getResult9 = actualDisplayListColumns.get(3);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.firstname",
                ((DispHeaderColumn) getResult9).getColumnName());
        Object getResult10 = actualDisplayListColumns.get(1);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.parentmsisdn",
                ((DispHeaderColumn) getResult10).getColumnName());
        Object getResult11 = actualDisplayListColumns.get(30);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.remarks", ((DispHeaderColumn) getResult11).getColumnName());
        Object getResult12 = actualDisplayListColumns.get(0);
        assertEquals("bulkuser.xlsfile.bulkuserenquirydetails.parentloginid",
                ((DispHeaderColumn) getResult12).getColumnName());
        assertNull(((DispHeaderColumn) getResult8).getDisplayName());
        assertNull(((DispHeaderColumn) getResult9).getDisplayName());
        assertNull(((DispHeaderColumn) getResult11).getDisplayName());
        assertNull(((DispHeaderColumn) getResult12).getDisplayName());
        assertNull(((DispHeaderColumn) getResult4).getDisplayName());
        assertNull(((DispHeaderColumn) getResult5).getDisplayName());
        assertNull(((DispHeaderColumn) getResult7).getDisplayName());
        assertNull(((DispHeaderColumn) getResult10).getDisplayName());
        assertNull(((DispHeaderColumn) getResult3).getDisplayName());
        assertNull(((DispHeaderColumn) getResult6).getDisplayName());
    }
}

