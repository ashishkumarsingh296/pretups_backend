package com.restapi.c2s.services;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.JUnitConfig;
import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {C2CTransferCommReportProcessor.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2CTransferCommReportProcessorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private C2CTransferCommReportProcessor c2CTransferCommReportProcessor;

    /**
     * Method under test: {@link C2CTransferCommReportProcessor#searchC2CTransferCommission(C2CTransferCommReqDTO)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testSearchC2CTransferCommission() throws BTSLBaseException {
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

        JUnitConfig.init();

        C2CTransferCommReqDTO c2CTransferCommReqDTO = new C2CTransferCommReqDTO();
        c2CTransferCommReqDTO.setCategoryCode("Category Code");
        c2CTransferCommReqDTO.setDispHeaderColumnList(new ArrayList<>());
        c2CTransferCommReqDTO.setDistributionType("Distribution Type");
        c2CTransferCommReqDTO.setDomain("Domain");
        c2CTransferCommReqDTO.setExtnwcode("Extnwcode");
        c2CTransferCommReqDTO.setFileName("foo.txt");
        c2CTransferCommReqDTO.setFileType("File Type");
        c2CTransferCommReqDTO.setFromDate("2020-03-01");
        c2CTransferCommReqDTO.setGeography("Geography");
        c2CTransferCommReqDTO.setIncludeStaffUserDetails("Include Staff User Details");
        c2CTransferCommReqDTO.setLocale(Locale.getDefault());
        c2CTransferCommReqDTO.setMsisdn("Msisdn");
        c2CTransferCommReqDTO.setOffline(true);
        c2CTransferCommReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        c2CTransferCommReqDTO.setProductCode("Product Code");
        c2CTransferCommReqDTO.setReceiverMobileNumber("42");
        c2CTransferCommReqDTO.setReqTab("Req Tab");
        c2CTransferCommReqDTO.setSenderMobileNumber("42");
        c2CTransferCommReqDTO.setToDate("2020-03-01");
        c2CTransferCommReqDTO.setTransferCategory("Transfer Category");
        c2CTransferCommReqDTO.setTransferInout("Transfer Inout");
        c2CTransferCommReqDTO.setTransferSubType("Transfer Sub Type");
        c2CTransferCommReqDTO.setTransferUser("Transfer User");
        c2CTransferCommReqDTO.setTransferUserCategory("Transfer User Category");
        c2CTransferCommReqDTO.setUser("User");
        c2CTransferCommReqDTO.setUserId("42");
        c2CTransferCommReportProcessor.searchC2CTransferCommission(c2CTransferCommReqDTO);
    }

    /**
     * Method under test: {@link C2CTransferCommReportProcessor#validateInputs(Connection, C2CTransferCommReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException, SQLException {
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

        C2CTransferCommReportProcessor c2cTransferCommReportProcessor = new C2CTransferCommReportProcessor();
       ////Connection con = mock(Connection.class);

        JUnitConfig.init();
        C2CTransferCommReqDTO c2CTransferCommReqDTO = new C2CTransferCommReqDTO();
        c2CTransferCommReqDTO.setCategoryCode("Category Code");
        c2CTransferCommReqDTO.setDispHeaderColumnList(new ArrayList<>());
        c2CTransferCommReqDTO.setDistributionType("Distribution Type");
        c2CTransferCommReqDTO.setDomain("Domain");
        c2CTransferCommReqDTO.setExtnwcode("Extnwcode");
        c2CTransferCommReqDTO.setFileName("foo.txt");
        c2CTransferCommReqDTO.setFileType("File Type");
        c2CTransferCommReqDTO.setFromDate("2020-03-01");
        c2CTransferCommReqDTO.setGeography("Geography");
        c2CTransferCommReqDTO.setIncludeStaffUserDetails("Include Staff User Details");
        c2CTransferCommReqDTO.setLocale(Locale.getDefault());
        c2CTransferCommReqDTO.setMsisdn("Msisdn");
        c2CTransferCommReqDTO.setOffline(true);
        c2CTransferCommReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        c2CTransferCommReqDTO.setProductCode("Product Code");
        c2CTransferCommReqDTO.setReceiverMobileNumber("42");
        c2CTransferCommReqDTO.setReqTab("Req Tab");
        c2CTransferCommReqDTO.setSenderMobileNumber("42");
        c2CTransferCommReqDTO.setToDate("2020-03-01");
        c2CTransferCommReqDTO.setTransferCategory("Transfer Category");
        c2CTransferCommReqDTO.setTransferInout("Transfer Inout");
        c2CTransferCommReqDTO.setTransferSubType("Transfer Sub Type");
        c2CTransferCommReqDTO.setTransferUser("Transfer User");
        c2CTransferCommReqDTO.setTransferUserCategory("Transfer User Category");
        c2CTransferCommReqDTO.setUser("User");
        c2CTransferCommReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        c2cTransferCommReportProcessor.validateInputs(JUnitConfig.getConnection(), c2CTransferCommReqDTO);
    }

    /**
     * Method under test: {@link C2CTransferCommReportProcessor#validateInputs(Connection, C2CTransferCommReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException, SQLException {
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

        C2CTransferCommReportProcessor c2cTransferCommReportProcessor = new C2CTransferCommReportProcessor();
       /* oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
*/
        JUnitConfig.init();
        C2CTransferCommReqDTO c2CTransferCommReqDTO = new C2CTransferCommReqDTO();
        c2CTransferCommReqDTO.setCategoryCode("Category Code");
        c2CTransferCommReqDTO.setDispHeaderColumnList(new ArrayList<>());
        c2CTransferCommReqDTO.setDistributionType("Distribution Type");
        c2CTransferCommReqDTO.setDomain("Domain");
        c2CTransferCommReqDTO.setExtnwcode("Extnwcode");
        c2CTransferCommReqDTO.setFileName("foo.txt");
        c2CTransferCommReqDTO.setFileType("File Type");
        c2CTransferCommReqDTO.setFromDate("2020-03-01");
        c2CTransferCommReqDTO.setGeography("Geography");
        c2CTransferCommReqDTO.setIncludeStaffUserDetails("Include Staff User Details");
        c2CTransferCommReqDTO.setLocale(Locale.getDefault());
        c2CTransferCommReqDTO.setMsisdn("Msisdn");
        c2CTransferCommReqDTO.setOffline(true);
        c2CTransferCommReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        c2CTransferCommReqDTO.setProductCode("Product Code");
        c2CTransferCommReqDTO.setReceiverMobileNumber("42");
        c2CTransferCommReqDTO.setReqTab("Req Tab");
        c2CTransferCommReqDTO.setSenderMobileNumber("42");
        c2CTransferCommReqDTO.setToDate("2020-03-01");
        c2CTransferCommReqDTO.setTransferCategory("Transfer Category");
        c2CTransferCommReqDTO.setTransferInout("Transfer Inout");
        c2CTransferCommReqDTO.setTransferSubType("Transfer Sub Type");
        c2CTransferCommReqDTO.setTransferUser("Transfer User");
        c2CTransferCommReqDTO.setTransferUserCategory("Transfer User Category");
        c2CTransferCommReqDTO.setUser("User");
        c2CTransferCommReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        c2cTransferCommReportProcessor.validateInputs(JUnitConfig.getConnection(), c2CTransferCommReqDTO);
    }

    /**
     * Method under test: {@link C2CTransferCommReportProcessor#execute(C2CTransferCommReqDTO, C2CTransferCommDownloadResp)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testExecute() throws BTSLBaseException {
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

        JUnitConfig.init();
        C2CTransferCommReqDTO c2CTransferCommReqDTO = new C2CTransferCommReqDTO();
        c2CTransferCommReqDTO.setCategoryCode("Category Code");
        c2CTransferCommReqDTO.setDispHeaderColumnList(new ArrayList<>());
        c2CTransferCommReqDTO.setDistributionType("Distribution Type");
        c2CTransferCommReqDTO.setDomain("Domain");
        c2CTransferCommReqDTO.setExtnwcode("Extnwcode");
        c2CTransferCommReqDTO.setFileName("foo.txt");
        c2CTransferCommReqDTO.setFileType("File Type");
        c2CTransferCommReqDTO.setFromDate("2020-03-01");
        c2CTransferCommReqDTO.setGeography("Geography");
        c2CTransferCommReqDTO.setIncludeStaffUserDetails("Include Staff User Details");
        c2CTransferCommReqDTO.setLocale(Locale.getDefault());
        c2CTransferCommReqDTO.setMsisdn("Msisdn");
        c2CTransferCommReqDTO.setOffline(true);
        c2CTransferCommReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        c2CTransferCommReqDTO.setProductCode("Product Code");
        c2CTransferCommReqDTO.setReceiverMobileNumber("42");
        c2CTransferCommReqDTO.setReqTab("Req Tab");
        c2CTransferCommReqDTO.setSenderMobileNumber("42");
        c2CTransferCommReqDTO.setToDate("2020-03-01");
        c2CTransferCommReqDTO.setTransferCategory("Transfer Category");
        c2CTransferCommReqDTO.setTransferInout("Transfer Inout");
        c2CTransferCommReqDTO.setTransferSubType("Transfer Sub Type");
        c2CTransferCommReqDTO.setTransferUser("Transfer User");
        c2CTransferCommReqDTO.setTransferUserCategory("Transfer User Category");
        c2CTransferCommReqDTO.setUser("User");
        c2CTransferCommReqDTO.setUserId("42");

        C2CTransferCommDownloadResp response = new C2CTransferCommDownloadResp();
        response.setAdditionalProperties(new HashMap<>());

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        response.setErrorMap(errorMap);
        response.setFileData("File Data");
        response.setFileName("foo.txt");
        response.setFilePath("/directory/foo.txt");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        response.setTotalRecords("Total Records");
        c2CTransferCommReportProcessor.execute(c2CTransferCommReqDTO, response);
    }

    /**
     * Method under test: {@link C2CTransferCommReportProcessor#executeOffineService(EventObjectData)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testExecuteOffineService() throws BTSLBaseException {
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

        c2CTransferCommReportProcessor.executeOffineService(new EventObjectData());
    }
}

