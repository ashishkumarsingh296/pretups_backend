package com.restapi.c2s.services;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.channel.transfer.businesslogic.AdditionalCommissionSummryC2SResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtlnCommSummryDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {AddtnlCommSummryReportProcess.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AddtnlCommSummryReportProcessTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private AddtnlCommSummryReportProcess addtnlCommSummryReportProcess;

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#getAddtnlCommSummryDetails(AddtnlCommSummryReqDTO, AdditionalCommissionSummryC2SResp)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetAddtnlCommSummryDetails() throws BTSLBaseException {
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

        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = new AddtnlCommSummryReqDTO();
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");

        AdditionalCommissionSummryC2SResp response = new AdditionalCommissionSummryC2SResp();
        response.setAdditionalProperties(new HashMap<>());
        response.setAddtnlcommissionSummaryList(new ArrayList<>());

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
        response.setTotalDiffAmount("10");
        response.setTotalTransactionCount("3");
        addtnlCommSummryReportProcess.getAddtnlCommSummryDetails(addtnlCommSummryReqDTO, response);
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException {

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);

        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = new AddtnlCommSummryReqDTO();
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException {

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = new AddtnlCommSummryReqDTO();
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs3() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("Daily Ormonthly Option");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs4() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("DAILY");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs5() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getFromMonthYear()).thenReturn("jane.doe@example.org");
        when(addtnlCommSummryReqDTO.getToMonthYear()).thenReturn("To Month Year");
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("MONTHLY");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getFromMonthYear();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getToMonthYear();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs6() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getFromMonthYear()).thenReturn("dd/MM/yy HH:mm:ss");
        when(addtnlCommSummryReqDTO.getToMonthYear()).thenReturn("To Month Year");
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("MONTHLY");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getFromMonthYear();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getToMonthYear();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs7() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getFromMonthYear()).thenReturn(null);
        when(addtnlCommSummryReqDTO.getToMonthYear()).thenReturn("To Month Year");
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("MONTHLY");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getFromMonthYear();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getToMonthYear();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs8() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getFromMonthYear()).thenReturn("jane.doe@example.org");
        when(addtnlCommSummryReqDTO.getToMonthYear()).thenReturn("dd/MM/yy HH:mm:ss");
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("MONTHLY");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getFromMonthYear();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getToMonthYear();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    public void testValidateInputs9() throws BTSLBaseException {
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

        JUnitConfig.init();
        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getFromMonthYear()).thenReturn("jane.doe@example.org");
        when(addtnlCommSummryReqDTO.getToMonthYear()).thenReturn(null);
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn("MONTHLY");
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
        verify(addtnlCommSummryReqDTO, atLeast(1)).getDailyOrmonthlyOption();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getFromMonthYear();
        verify(addtnlCommSummryReqDTO, atLeast(1)).getToMonthYear();
        verify(addtnlCommSummryReqDTO).getExtnwcode();
        verify(addtnlCommSummryReqDTO).getFromDate();
        verify(addtnlCommSummryReqDTO).getToDate();
        verify(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        verify(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        verify(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        verify(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#validateInputs(Connection, AddtnlCommSummryReqDTO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateInputs10() throws BTSLBaseException {
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.AddtnlCommSummryReportProcess.validateInputs(AddtnlCommSummryReportProcess.java:165)
        //   See https://diff.blue/R013 to resolve this issue.

        AddtnlCommSummryReportProcess addtnlCommSummryReportProcess = new AddtnlCommSummryReportProcess();
        //Connection con = mock(Connection.class);
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = mock(AddtnlCommSummryReqDTO.class);
        when(addtnlCommSummryReqDTO.getFromMonthYear()).thenReturn("jane.doe@example.org");
        when(addtnlCommSummryReqDTO.getToMonthYear()).thenReturn("To Month Year");
        when(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()).thenReturn(null);
        when(addtnlCommSummryReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(addtnlCommSummryReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(addtnlCommSummryReqDTO.getToDate()).thenReturn("2020-03-01");
        doNothing().when(addtnlCommSummryReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDailyOrmonthlyOption(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setService(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToMonthYear(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(addtnlCommSummryReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(addtnlCommSummryReqDTO).setOffline(anyBoolean());
        doNothing().when(addtnlCommSummryReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");
        addtnlCommSummryReportProcess.validateInputs(JUnitConfig.getConnection(), addtnlCommSummryReqDTO);
    }

    /**
     * Method under test: {@link AddtnlCommSummryReportProcess#execute(AddtnlCommSummryReqDTO, AddtlnCommSummryDownloadResp)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
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

        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = new AddtnlCommSummryReqDTO();
        addtnlCommSummryReqDTO.setCategoryCode("Category Code");
        addtnlCommSummryReqDTO.setDailyOrmonthlyOption("Daily Ormonthly Option");
        addtnlCommSummryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        addtnlCommSummryReqDTO.setDomain("Domain");
        addtnlCommSummryReqDTO.setExtnwcode("Extnwcode");
        addtnlCommSummryReqDTO.setFileName("foo.txt");
        addtnlCommSummryReqDTO.setFileType("File Type");
        addtnlCommSummryReqDTO.setFromDate("2020-03-01");
        addtnlCommSummryReqDTO.setFromMonthYear("jane.doe@example.org");
        addtnlCommSummryReqDTO.setGeography("Geography");
        addtnlCommSummryReqDTO.setLocale(Locale.getDefault());
        addtnlCommSummryReqDTO.setMsisdn("Msisdn");
        addtnlCommSummryReqDTO.setOffline(true);
        addtnlCommSummryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        addtnlCommSummryReqDTO.setProductCode("Product Code");
        addtnlCommSummryReqDTO.setService("Service");
        addtnlCommSummryReqDTO.setToDate("2020-03-01");
        addtnlCommSummryReqDTO.setToMonthYear("To Month Year");
        addtnlCommSummryReqDTO.setUserId("42");

        AddtlnCommSummryDownloadResp response = new AddtlnCommSummryDownloadResp();
        response.setAdditionalProperties(new HashMap<>());

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        response.setErrorMap(errorMap);
        response.setFileData("File Data");
        response.setFileName("foo.txt");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        addtnlCommSummryReportProcess.execute(addtnlCommSummryReqDTO, response);
    }
}

