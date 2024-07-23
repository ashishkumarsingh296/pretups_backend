package com.restapi.channelAdmin.serviceI;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.security.CustomResponseWrapper;
import com.restapi.channelAdmin.requestVO.BulkCUStatusChangeRequestVO;
import com.restapi.channelAdmin.responseVO.BulkCUStatusChangeResponseVO;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import jakarta.servlet.http.HttpServletResponse;

import jxl.read.biff.BiffException;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ChangeUserStatusByAdminImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChangeUserStatusByAdminImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ChangeUserStatusByAdminImpl changeUserStatusByAdminImpl;

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#changeUserStatusAdmin(Connection, String, String, HttpServletResponse, String, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChangeUserStatusAdmin() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.serviceI.ChangeUserStatusByAdminImpl.changeUserStatusAdmin(ChangeUserStatusByAdminImpl.java:80)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.changeUserStatusAdmin(com.btsl.util.JUnitConfig.getConnection(), "42", "Msisdn", response1,
                "Status", "Remarks");
    }

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO, String, BulkCUStatusChangeResponseVO, HttpServletResponse)}
     */
    @Test
    public void testChannelUserBulkStatusChangeImpl()
            throws BTSLBaseException, IOException, SQLException, BiffException {
        BulkCUStatusChangeRequestVO requestVO = new BulkCUStatusChangeRequestVO();
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");

        BulkCUStatusChangeResponseVO responseVO = new BulkCUStatusChangeResponseVO();
        responseVO.setErrorLogs(new ArrayList<>());
        responseVO.setFileAttachment("File Attachment");
        responseVO.setFileName("foo.txt");
        responseVO.setMessage("Not all who wander are lost");
        responseVO.setMessageCode("Message Code");
        responseVO.setNumberOfRecords(1L);
        responseVO.setStatus("Txnstatus");
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO, "Session Msisdn", responseVO,
                response1);
    }

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO, String, BulkCUStatusChangeResponseVO, HttpServletResponse)}
     */
    @Test
    public void testChannelUserBulkStatusChangeImpl2()
            throws BTSLBaseException, IOException, SQLException, BiffException {
        BulkCUStatusChangeRequestVO requestVO = mock(BulkCUStatusChangeRequestVO.class);
        when(requestVO.getFileType()).thenReturn("File Type");
        when(requestVO.getFileName()).thenReturn("foo.txt");
        when(requestVO.getFile()).thenReturn("File");
        doNothing().when(requestVO).setFile(Mockito.<String>any());
        doNothing().when(requestVO).setFileName(Mockito.<String>any());
        doNothing().when(requestVO).setFileType(Mockito.<String>any());
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");

        BulkCUStatusChangeResponseVO responseVO = new BulkCUStatusChangeResponseVO();
        responseVO.setErrorLogs(new ArrayList<>());
        responseVO.setFileAttachment("File Attachment");
        responseVO.setFileName("foo.txt");
        responseVO.setMessage("Not all who wander are lost");
        responseVO.setMessageCode("Message Code");
        responseVO.setNumberOfRecords(1L);
        responseVO.setStatus("Txnstatus");
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO, "Session Msisdn", responseVO,
                response1);
        verify(requestVO).getFile();
        verify(requestVO).getFileName();
        verify(requestVO, atLeast(1)).getFileType();
        verify(requestVO).setFile(Mockito.<String>any());
        verify(requestVO).setFileName(Mockito.<String>any());
        verify(requestVO).setFileType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO, String, BulkCUStatusChangeResponseVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChannelUserBulkStatusChangeImpl3()
            throws BTSLBaseException, IOException, SQLException, BiffException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.serviceI.ChangeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(ChangeUserStatusByAdminImpl.java:494)
        //   See https://diff.blue/R013 to resolve this issue.

        BulkCUStatusChangeRequestVO requestVO = mock(BulkCUStatusChangeRequestVO.class);
        when(requestVO.getFileType()).thenReturn("XLS");
        when(requestVO.getFileName()).thenReturn("foo.txt");
        when(requestVO.getFile()).thenReturn("File");
        doNothing().when(requestVO).setFile(Mockito.<String>any());
        doNothing().when(requestVO).setFileName(Mockito.<String>any());
        doNothing().when(requestVO).setFileType(Mockito.<String>any());
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");

        BulkCUStatusChangeResponseVO responseVO = new BulkCUStatusChangeResponseVO();
        responseVO.setErrorLogs(new ArrayList<>());
        responseVO.setFileAttachment("File Attachment");
        responseVO.setFileName("foo.txt");
        responseVO.setMessage("Not all who wander are lost");
        responseVO.setMessageCode("Message Code");
        responseVO.setNumberOfRecords(1L);
        responseVO.setStatus("Txnstatus");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO, "Session Msisdn", responseVO,
                response1);
    }

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO, String, BulkCUStatusChangeResponseVO, HttpServletResponse)}
     */
    @Test
    public void testChannelUserBulkStatusChangeImpl4()
            throws BTSLBaseException, IOException, SQLException, BiffException {
        BulkCUStatusChangeRequestVO requestVO = mock(BulkCUStatusChangeRequestVO.class);
        when(requestVO.getFileType()).thenReturn("");
        when(requestVO.getFileName()).thenReturn("foo.txt");
        when(requestVO.getFile()).thenReturn("File");
        doNothing().when(requestVO).setFile(Mockito.<String>any());
        doNothing().when(requestVO).setFileName(Mockito.<String>any());
        doNothing().when(requestVO).setFileType(Mockito.<String>any());
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");

        BulkCUStatusChangeResponseVO responseVO = new BulkCUStatusChangeResponseVO();
        responseVO.setErrorLogs(new ArrayList<>());
        responseVO.setFileAttachment("File Attachment");
        responseVO.setFileName("foo.txt");
        responseVO.setMessage("Not all who wander are lost");
        responseVO.setMessageCode("Message Code");
        responseVO.setNumberOfRecords(1L);
        responseVO.setStatus("Txnstatus");
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO, "Session Msisdn", responseVO,
                response1);
        verify(requestVO).getFile();
        verify(requestVO).getFileName();
        verify(requestVO).getFileType();
        verify(requestVO).setFile(Mockito.<String>any());
        verify(requestVO).setFileName(Mockito.<String>any());
        verify(requestVO).setFileType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO, String, BulkCUStatusChangeResponseVO, HttpServletResponse)}
     */
    @Test
    public void testChannelUserBulkStatusChangeImpl5()
            throws BTSLBaseException, IOException, SQLException, BiffException {
        BulkCUStatusChangeRequestVO requestVO = mock(BulkCUStatusChangeRequestVO.class);
        when(requestVO.getFileName()).thenReturn("");
        when(requestVO.getFile()).thenReturn("File");
        doNothing().when(requestVO).setFile(Mockito.<String>any());
        doNothing().when(requestVO).setFileName(Mockito.<String>any());
        doNothing().when(requestVO).setFileType(Mockito.<String>any());
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");

        BulkCUStatusChangeResponseVO responseVO = new BulkCUStatusChangeResponseVO();
        responseVO.setErrorLogs(new ArrayList<>());
        responseVO.setFileAttachment("File Attachment");
        responseVO.setFileName("foo.txt");
        responseVO.setMessage("Not all who wander are lost");
        responseVO.setMessageCode("Message Code");
        responseVO.setNumberOfRecords(1L);
        responseVO.setStatus("Txnstatus");
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO, "Session Msisdn", responseVO,
                response1);
        verify(requestVO).getFile();
        verify(requestVO).getFileName();
        verify(requestVO).setFile(Mockito.<String>any());
        verify(requestVO).setFileName(Mockito.<String>any());
        verify(requestVO).setFileType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ChangeUserStatusByAdminImpl#channelUserBulkStatusChangeImpl(BulkCUStatusChangeRequestVO, String, BulkCUStatusChangeResponseVO, HttpServletResponse)}
     */
    @Test
    public void testChannelUserBulkStatusChangeImpl6()
            throws BTSLBaseException, IOException, SQLException, BiffException {
        BulkCUStatusChangeRequestVO requestVO = mock(BulkCUStatusChangeRequestVO.class);
        when(requestVO.getFile()).thenReturn("");
        doNothing().when(requestVO).setFile(Mockito.<String>any());
        doNothing().when(requestVO).setFileName(Mockito.<String>any());
        doNothing().when(requestVO).setFileType(Mockito.<String>any());
        requestVO.setFile("File");
        requestVO.setFileName("foo.txt");
        requestVO.setFileType("File Type");

        BulkCUStatusChangeResponseVO responseVO = new BulkCUStatusChangeResponseVO();
        responseVO.setErrorLogs(new ArrayList<>());
        responseVO.setFileAttachment("File Attachment");
        responseVO.setFileName("foo.txt");
        responseVO.setMessage("Not all who wander are lost");
        responseVO.setMessageCode("Message Code");
        responseVO.setNumberOfRecords(1L);
        responseVO.setStatus("Txnstatus");
        thrown.expect(BTSLBaseException.class);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        changeUserStatusByAdminImpl.channelUserBulkStatusChangeImpl(requestVO, "Session Msisdn", responseVO,
                response1);
        verify(requestVO).getFile();
        verify(requestVO).setFile(Mockito.<String>any());
        verify(requestVO).setFileName(Mockito.<String>any());
        verify(requestVO).setFileType(Mockito.<String>any());
    }
}

