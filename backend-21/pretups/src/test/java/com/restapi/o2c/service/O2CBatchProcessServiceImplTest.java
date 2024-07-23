package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.XssWrapper;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.iat.util.IATCommonUtil;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.security.CustomResponseWrapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {O2CBatchProcessServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CBatchProcessServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private O2CBatchProcessServiceImpl o2CBatchProcessServiceImpl;

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#processRequest(String, O2CBatchWithdrawFileRequest, String, OperatorUtilI, Locale, Connection, String, String, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processRequest(O2CBatchProcessServiceImpl.java:466)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CBatchProcessServiceImpl o2cBatchProcessServiceImpl = new O2CBatchProcessServiceImpl();

        O2CBatchWithdrawFileRequest o2CFileUploadApiRequest = new O2CBatchWithdrawFileRequest();
        o2CFileUploadApiRequest.setBatchName("Batch Name");
        o2CFileUploadApiRequest.setFileAttachment("File Attachment");
        o2CFileUploadApiRequest.setFileName("foo.txt");
        o2CFileUploadApiRequest.setFileType("File Type");
        o2CFileUploadApiRequest.setLanguage1("en");
        o2CFileUploadApiRequest.setLanguage2("en");
        o2CFileUploadApiRequest.setPin("Pin");
        IATCommonUtil calculatorI = new IATCommonUtil();
        Locale locale = Locale.getDefault();
        Timestamp timestamp = mock(Timestamp.class);
        when(timestamp.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        doNothing().when(con).close();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2cBatchProcessServiceImpl.processRequest("Service Typee", o2CFileUploadApiRequest, "Msisdn", calculatorI, locale,
                JUnitConfig.getConnection(), "Request Type", "Batch ID", "Service Type", "Request IDStr", httprequest, headers,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#validateFilePathCons(String)}
     */
    @Test
    public void testValidateFilePathCons() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        o2CBatchProcessServiceImpl.validateFilePathCons("");
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#createDirectory(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCreateDirectory() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file '\directory\foo.txt', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        o2CBatchProcessServiceImpl.createDirectory("/directory/foo.txt");
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#createDirectory(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testCreateDirectory2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file 'createDirectory', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        o2CBatchProcessServiceImpl.createDirectory("createDirectory");
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#writeCSV(List, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testWriteCSV() throws IOException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R011 Sandboxing policy violation.
        //   Diffblue Cover ran code in your project that tried
        //     to access files (file '\directory\foo.txt', permission 'write').
        //   Diffblue Cover's default sandboxing policy disallows this in order to prevent
        //   your code from damaging your system environment.
        //   See https://diff.blue/R011 to resolve this issue.

        o2CBatchProcessServiceImpl.writeCSV(new ArrayList<>(), "/directory/foo.txt");
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#processBulkComBatchProcessRequest(O2CBatchWithdrawFileRequest, String, OperatorUtilI, Locale, Connection, String, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkComBatchProcessRequest() throws SQLException {

        com.btsl.util.JUnitConfig.init();
        O2CBatchProcessServiceImpl o2cBatchProcessServiceImpl = new O2CBatchProcessServiceImpl();

        O2CBatchWithdrawFileRequest bulkComProcessApiRequest = new O2CBatchWithdrawFileRequest();
        bulkComProcessApiRequest.setBatchName("Batch Name");
        bulkComProcessApiRequest.setFileAttachment("File Attachment");
        bulkComProcessApiRequest.setFileName("foo.txt");
        bulkComProcessApiRequest.setFileType("csv");
        bulkComProcessApiRequest.setLanguage1("en");
        bulkComProcessApiRequest.setLanguage2("en");
        bulkComProcessApiRequest.setPin("Pin");
        IATCommonUtil calculatorI = new IATCommonUtil();
        Locale locale = Locale.getDefault();
        //Connection con = mock(Connection.class);
        //   doNothing().when(con).close();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2cBatchProcessServiceImpl.processBulkComBatchProcessRequest(bulkComProcessApiRequest, "Msisdn", calculatorI,
                locale, JUnitConfig.getConnection(), "Request Type", "Batch ID", "Request IDStr", httprequest, headers,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#processBulkComBatchProcessRequest(O2CBatchWithdrawFileRequest, String, OperatorUtilI, Locale, Connection, String, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
    public void testProcessBulkComBatchProcessRequest2() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processBulkComBatchProcessRequest(O2CBatchProcessServiceImpl.java:1515)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CBatchProcessServiceImpl o2cBatchProcessServiceImpl = new O2CBatchProcessServiceImpl();

        O2CBatchWithdrawFileRequest bulkComProcessApiRequest = new O2CBatchWithdrawFileRequest();
        bulkComProcessApiRequest.setBatchName("Batch Name");
        bulkComProcessApiRequest.setFileAttachment("File Attachment");
        bulkComProcessApiRequest.setFileName("foo.txt");
        bulkComProcessApiRequest.setFileType("File Type");
        bulkComProcessApiRequest.setLanguage1("en");
        bulkComProcessApiRequest.setLanguage2("en");
        bulkComProcessApiRequest.setPin("Pin");
        IATCommonUtil calculatorI = new IATCommonUtil();
        Locale locale = Locale.getDefault();
        //Connection con = mock(Connection.class);
        //doNothing().when(con).close();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        O2CBatchWithdrawFileResponse actualProcessBulkComBatchProcessRequestResult = o2cBatchProcessServiceImpl
                .processBulkComBatchProcessRequest(bulkComProcessApiRequest, "Msisdn", calculatorI, locale, JUnitConfig.getConnection(),
                        "Request Type", "Batch ID", "Request IDStr", httprequest, headers,
                        new CustomResponseWrapper(new MockHttpServletResponse()));
        assertSame(o2cBatchProcessServiceImpl.response, actualProcessBulkComBatchProcessRequestResult);
        assertTrue(actualProcessBulkComBatchProcessRequestResult.getSuccessList().isEmpty());
        assertEquals("400", actualProcessBulkComBatchProcessRequestResult.getStatus());
        assertEquals("error.general.processing", actualProcessBulkComBatchProcessRequestResult.getMessageCode());
        assertEquals("Check File Type supplied.", actualProcessBulkComBatchProcessRequestResult.getMessage());
        //verify(con).close();
        assertSame(actualProcessBulkComBatchProcessRequestResult, o2cBatchProcessServiceImpl.response);
        assertTrue(o2cBatchProcessServiceImpl.processRunning);
        assertFalse(o2cBatchProcessServiceImpl.fileExist);
        assertEquals(0, o2cBatchProcessServiceImpl.errorSize);
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#processBulkComBatchProcessRequest(O2CBatchWithdrawFileRequest, String, OperatorUtilI, Locale, Connection, String, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkComBatchProcessRequest3() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processBulkComBatchProcessRequest(O2CBatchProcessServiceImpl.java:1515)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processBulkComBatchProcessRequest(O2CBatchProcessServiceImpl.java:1515)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CBatchProcessServiceImpl o2cBatchProcessServiceImpl = new O2CBatchProcessServiceImpl();

        O2CBatchWithdrawFileRequest bulkComProcessApiRequest = new O2CBatchWithdrawFileRequest();
        bulkComProcessApiRequest.setBatchName("Batch Name");
        bulkComProcessApiRequest.setFileAttachment("File Attachment");
        bulkComProcessApiRequest.setFileName("foo.txt");
        bulkComProcessApiRequest.setFileType("File Type");
        bulkComProcessApiRequest.setLanguage1("en");
        bulkComProcessApiRequest.setLanguage2("en");
        bulkComProcessApiRequest.setPin("Pin");
        IATCommonUtil calculatorI = new IATCommonUtil();
        Locale locale = Locale.getDefault();
        //Connection con = mock(Connection.class);
        //doNothing().when(con).close();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        o2cBatchProcessServiceImpl.processBulkComBatchProcessRequest(bulkComProcessApiRequest, "Msisdn", calculatorI,
                locale, JUnitConfig.getConnection(), "Request Type", "Batch ID", "Request IDStr", httprequest, new HttpHeaders(), null);
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#processBulkComBatchProcessRequest(O2CBatchWithdrawFileRequest, String, OperatorUtilI, Locale, Connection, String, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkComBatchProcessRequest4() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processBulkComBatchProcessRequest(O2CBatchProcessServiceImpl.java:1515)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processBulkComBatchProcessRequest(O2CBatchProcessServiceImpl.java:1515)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CBatchProcessServiceImpl o2cBatchProcessServiceImpl = new O2CBatchProcessServiceImpl();

        O2CBatchWithdrawFileRequest bulkComProcessApiRequest = new O2CBatchWithdrawFileRequest();
        bulkComProcessApiRequest.setBatchName("Batch Name");
        bulkComProcessApiRequest.setFileAttachment("File Attachment");
        bulkComProcessApiRequest.setFileName("foo.txt");
        bulkComProcessApiRequest.setFileType("File Type");
        bulkComProcessApiRequest.setLanguage1("en");
        bulkComProcessApiRequest.setLanguage2("en");
        bulkComProcessApiRequest.setPin("Pin");
        IATCommonUtil calculatorI = new IATCommonUtil();
        Locale locale = Locale.getDefault();
        //Connection con = mock(Connection.class);
//        doThrow(new SQLException()).when(con).close();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        o2cBatchProcessServiceImpl.processBulkComBatchProcessRequest(bulkComProcessApiRequest, "Msisdn", calculatorI,
                locale, JUnitConfig.getConnection(), "Request Type", "Batch ID", "Request IDStr", httprequest, headers,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CBatchProcessServiceImpl#processBulkComBatchProcessRequest(O2CBatchWithdrawFileRequest, String, OperatorUtilI, Locale, Connection, String, String, String, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
    public void testProcessBulkComBatchProcessRequest5() throws SQLException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CBatchProcessServiceImpl.processBulkComBatchProcessRequest(O2CBatchProcessServiceImpl.java:1515)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CBatchProcessServiceImpl o2cBatchProcessServiceImpl = new O2CBatchProcessServiceImpl();

        O2CBatchWithdrawFileRequest bulkComProcessApiRequest = new O2CBatchWithdrawFileRequest();
        bulkComProcessApiRequest.setBatchName("Batch Name");
        bulkComProcessApiRequest.setFileAttachment("File Attachment");
        bulkComProcessApiRequest.setFileName("foo.txt");
        bulkComProcessApiRequest.setFileType("File Type");
        bulkComProcessApiRequest.setLanguage1("en");
        bulkComProcessApiRequest.setLanguage2("en");
        bulkComProcessApiRequest.setPin("Pin");
        IATCommonUtil calculatorI = new IATCommonUtil();
        Locale locale = Locale.getDefault();
        //Connection con = mock(Connection.class);
        //    doThrow(new SQLException()).when(con).close();
        XssWrapper httprequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        HttpHeaders headers = new HttpHeaders();
        O2CBatchWithdrawFileResponse actualProcessBulkComBatchProcessRequestResult = o2cBatchProcessServiceImpl
                .processBulkComBatchProcessRequest(bulkComProcessApiRequest, "Msisdn", calculatorI, locale, JUnitConfig.getConnection(),
                        "Request Type", "Batch ID", "Request IDStr", httprequest, headers,
                        new CustomResponseWrapper(new MockHttpServletResponse()));
        assertSame(o2cBatchProcessServiceImpl.response, actualProcessBulkComBatchProcessRequestResult);
        assertTrue(actualProcessBulkComBatchProcessRequestResult.getSuccessList().isEmpty());
        assertEquals("400", actualProcessBulkComBatchProcessRequestResult.getStatus());
        assertEquals("error.general.processing", actualProcessBulkComBatchProcessRequestResult.getMessageCode());
        assertEquals("Check File Type supplied.", actualProcessBulkComBatchProcessRequestResult.getMessage());
//        verify(con).close();
        assertSame(actualProcessBulkComBatchProcessRequestResult, o2cBatchProcessServiceImpl.response);
        assertTrue(o2cBatchProcessServiceImpl.processRunning);
        assertFalse(o2cBatchProcessServiceImpl.fileExist);
        assertEquals(0, o2cBatchProcessServiceImpl.errorSize);
    }
}

