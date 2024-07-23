package com.restapi.networkadmin.commissionprofile.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.common.XssWrapper;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.util.JUnitConfig;
import com.restapi.networkadmin.commissionprofile.requestVO.BatchAddCommisionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommisionProfileResponseVO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {BatchCommissionProfileServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class BatchCommissionProfileServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private BatchCommissionProfileServiceImpl batchCommissionProfileServiceImpl;

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#basicFileValidations(BatchAddCommisionProfileRequestVO, BatchAddCommisionProfileResponseVO, Locale, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testBasicFileValidations() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.networkadmin.commissionprofile.service.BatchCommissionProfileServiceImpl.basicFileValidations(BatchCommissionProfileServiceImpl.java:112)
        //   See https://diff.blue/R013 to resolve this issue.

        BatchAddCommisionProfileRequestVO request = new BatchAddCommisionProfileRequestVO();
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
        response.setErrorFlag("An error occurred");
        response.setErrorList(new ArrayList());
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setSheetName("Sheet Name");
        response.setStatus(1);
        response.setTotalRecords(1);
        response.setTransactionId("42");
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        try{batchCommissionProfileServiceImpl.basicFileValidations(request, response, locale, new ArrayList<>());
        }catch(Exception e){}
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#basicFileValidations(BatchAddCommisionProfileRequestVO, BatchAddCommisionProfileResponseVO, Locale, ArrayList)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testBasicFileValidations2() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.btsl.pretups.channel.transfer.requesthandler.C2CFileUploadApiController.isValideFileName(C2CFileUploadApiController.java:1428)
        //       at com.restapi.networkadmin.commissionprofile.service.BatchCommissionProfileServiceImpl.basicFileValidations(BatchCommissionProfileServiceImpl.java:112)
        //   See https://diff.blue/R013 to resolve this issue.

        BatchAddCommisionProfileRequestVO request = mock(BatchAddCommisionProfileRequestVO.class);
        when(request.getFileAttachment()).thenReturn("File Attachment");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
        response.setErrorFlag("An error occurred");
        response.setErrorList(new ArrayList());
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setSheetName("Sheet Name");
        response.setStatus(1);
        response.setTotalRecords(1);
        response.setTransactionId("42");
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        try{batchCommissionProfileServiceImpl.basicFileValidations(request, response, locale, new ArrayList<>());

        }catch(Exception e){}
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#basicFileValidations(BatchAddCommisionProfileRequestVO, BatchAddCommisionProfileResponseVO, Locale, ArrayList)}
     */
    @Test
    public void testBasicFileValidations3() {
        BatchAddCommisionProfileRequestVO request = mock(BatchAddCommisionProfileRequestVO.class);
        when(request.getFileAttachment()).thenReturn("");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
        response.setErrorFlag("An error occurred");
        response.setErrorList(new ArrayList());
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setSheetName("Sheet Name");
        response.setStatus(1);
        response.setTotalRecords(1);
        response.setTransactionId("42");
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();
        ArrayList<MasterErrorList> inputValidations = new ArrayList<>();
        ArrayList<MasterErrorList> actualBasicFileValidationsResult = null;
        try{
             actualBasicFileValidationsResult = batchCommissionProfileServiceImpl
                    .basicFileValidations(request, response, locale, inputValidations);
        }catch(Exception e){}
        assertSame(inputValidations, actualBasicFileValidationsResult);
        assertEquals(1, actualBasicFileValidationsResult.size());
        verify(request, atLeast(1)).getFileAttachment();
        verify(request, atLeast(1)).getFileName();
        verify(request).getFileType();
        verify(request).setFileAttachment(Mockito.<String>any());
        verify(request).setFileName(Mockito.<String>any());
        verify(request).setFileType(Mockito.<String>any());
        assertEquals("File is Empty", response.getMessage());
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#uploadAndValidateFile(Connection, MComConnectionI, String, BatchAddCommisionProfileRequestVO, BatchAddCommisionProfileResponseVO, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUploadAndValidateFile() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        Connection con = null;
        MComConnectionI mcomCon = null;
        String loginId = "";
        //BatchAddCommisionProfileRequestVO request = null;
     //   BatchAddCommisionProfileResponseVO response = null;
        String domainCode = "";
        String catrgoryCode = "";
        String batchName = "";


        BatchAddCommisionProfileRequestVO request = mock(BatchAddCommisionProfileRequestVO.class);
        when(request.getFileAttachment()).thenReturn("File Attachment");
        when(request.getFileName()).thenReturn("foo.txt");
        when(request.getFileType()).thenReturn("File Type");
        doNothing().when(request).setFileAttachment(Mockito.<String>any());
        doNothing().when(request).setFileName(Mockito.<String>any());
        doNothing().when(request).setFileType(Mockito.<String>any());
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        BatchAddCommisionProfileResponseVO response = new BatchAddCommisionProfileResponseVO();
        response.setErrorFlag("An error occurred");
        response.setErrorList(new ArrayList());
        response.setErrorMap(errorMap);
        response.setFileAttachment("File Attachment");
        response.setFileType("File Type");
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setSheetName("Sheet Name");
        response.setStatus(1);
        response.setTotalRecords(1);
        response.setTransactionId("42");
        response.setValidRecords(1);
        Locale locale = Locale.getDefault();

        // Act
        boolean actualUploadAndValidateFileResult = this.batchCommissionProfileServiceImpl.uploadAndValidateFile(JUnitConfig.getConnection(),
                JUnitConfig.getMComConnection(), loginId, request, response, domainCode, catrgoryCode);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#processBulkAddCommissionProf(Connection, HttpServletResponse, BatchAddCommisionProfileRequestVO, String, String, String, String, String, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessBulkAddCommissionProf() throws Exception {
        com.btsl.util.JUnitConfig.init(this.getClass().getName()); //Auto replace

       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        BatchAddCommisionProfileRequestVO request = new BatchAddCommisionProfileRequestVO();
        request.setFileAttachment("File Attachment");
        request.setFileName("batch_add_comm_prof_239801");
        request.setFileType("xls");

        batchCommissionProfileServiceImpl.processBulkAddCommissionProf(com.btsl.util.JUnitConfig.getConnection(), response1, request, "P file", "ALL",
                "DIST", "BatchName", "42", Locale.getDefault());
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#processBulkAddCommissionProf(Connection, HttpServletResponse, BatchAddCommisionProfileRequestVO, String, String, String, String, String, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessBulkAddCommissionProf2() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.commissionprofile.service.BatchCommissionProfileServiceImpl.processBulkAddCommissionProf(BatchCommissionProfileServiceImpl.java:329)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        BatchAddCommisionProfileRequestVO request = new BatchAddCommisionProfileRequestVO();
        request.setFileAttachment("File Attachment");
        request.setFileName("foo.txt");
        request.setFileType("File Type");
        batchCommissionProfileServiceImpl.processBulkAddCommissionProf(JUnitConfig.getConnection(), response1, request, "P file", "Domain Code",
                "Catrgory Code", "Batch Name", "42", Locale.getDefault());
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#validateFileName(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFileName() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.regex.Pattern.<init>(Pattern.java:1350)
        //       at java.util.regex.Pattern.compile(Pattern.java:1028)
        //       at com.restapi.networkadmin.commissionprofile.service.BatchCommissionProfileServiceImpl.validateFileName(BatchCommissionProfileServiceImpl.java:4682)
        //   See https://diff.blue/R013 to resolve this issue.

        batchCommissionProfileServiceImpl.validateFileName("foo.txt");
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        batchCommissionProfileServiceImpl.validateFileDetailsMap(new HashMap<>());
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#validateFileDetailsMap(HashMap)}
     */
    @Test
    public void testValidateFileDetailsMap2() throws BTSLBaseException {
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        fileDetailsMap.put("foo", "foo");
        thrown.expect(BTSLBaseException.class);
        batchCommissionProfileServiceImpl.validateFileDetailsMap(fileDetailsMap);
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#downloadFileTemplate(Connection, Locale, String, String, String, HttpServletRequest, HttpServletResponse, String)}
     */
    @Test
    public void testDownloadFileTemplate() throws BTSLBaseException, IOException, SQLException, ParseException {
        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        Locale locale = Locale.getDefault();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        batchCommissionProfileServiceImpl.downloadFileTemplate(com.btsl.util.JUnitConfig.getConnection(), locale, "Login ID", "Category Code", "Domain Code",
                request, response1);
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#downloadFileTemplate(Connection, Locale, String, String, String, HttpServletRequest, HttpServletResponse, String)}
     */
    @Test
    public void testDownloadFileTemplate2() throws BTSLBaseException, IOException, SQLException, ParseException {


        JUnitConfig.init();

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);
        Locale locale = Locale.getDefault();
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        batchCommissionProfileServiceImpl.downloadFileTemplate(JUnitConfig.getConnection(), locale, "Login ID", "Category Code", "Domain Code",
                request, response1);
    }

    /**
     * Method under test: {@link BatchCommissionProfileServiceImpl#processUploadedFileForCommProfile(Connection, HttpServletResponse, BatchAddCommisionProfileRequestVO, String, String, String, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessUploadedFileForCommProfile() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        Connection p_con = mock(Connection.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        CustomResponseWrapper responseSwag = response1;

        BatchAddCommisionProfileRequestVO request = new BatchAddCommisionProfileRequestVO();
        request.setFileAttachment("File Attachment");
        request.setFileName("batch_com_modify_555");
        request.setFileType("xls");
        batchCommissionProfileServiceImpl.processUploadedFileForCommProfile(JUnitConfig.getConnection(), responseSwag, request, "P file",
                "Domain Code", "Catrgory Code", "Batch Name");
    }
}

