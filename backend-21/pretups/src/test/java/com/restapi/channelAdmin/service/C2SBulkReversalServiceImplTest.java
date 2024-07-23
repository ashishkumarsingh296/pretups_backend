package com.restapi.channelAdmin.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.c2s.services.C2SRechargeReversalDetails;
import com.restapi.channelAdmin.requestVO.C2SBulkReversalRequestVO;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import com.btsl.util.MessageResources;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {C2SBulkReversalServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SBulkReversalServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private C2SBulkReversalServiceImpl c2SBulkReversalServiceImpl;

    /**
     * Method under test: {@link C2SBulkReversalServiceImpl#confirmUploadRequest(C2SBulkReversalRequestVO)}
     */
    @Test
    public void testConfirmUploadRequest() throws BTSLBaseException {
        C2SBulkReversalRequestVO req = new C2SBulkReversalRequestVO();
        req.setAttachment("Attachment");
        req.setBatchName("Batch Name");
        req.setFileName("foo.txt");
        req.setFileType("File Type");
        thrown.expect(BTSLBaseException.class);
        c2SBulkReversalServiceImpl.confirmUploadRequest(req);
    }

    /**
     * Method under test: {@link C2SBulkReversalServiceImpl#confirmUploadRequest(C2SBulkReversalRequestVO)}
     */
    @Test
    public void testConfirmUploadRequest2() throws BTSLBaseException {
        C2SBulkReversalRequestVO req = mock(C2SBulkReversalRequestVO.class);
        doNothing().when(req).setAttachment(Mockito.<String>any());
        doNothing().when(req).setBatchName(Mockito.<String>any());
        doNothing().when(req).setFileName(Mockito.<String>any());
        doNothing().when(req).setFileType(Mockito.<String>any());
        req.setAttachment("Attachment");
        req.setBatchName("Batch Name");
        req.setFileName("foo.txt");
        req.setFileType("File Type");
        thrown.expect(BTSLBaseException.class);
        c2SBulkReversalServiceImpl.confirmUploadRequest(req);
        verify(req).setAttachment(Mockito.<String>any());
        verify(req).setBatchName(Mockito.<String>any());
        verify(req).setFileName(Mockito.<String>any());
        verify(req).setFileType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SBulkReversalServiceImpl#uploadFileToServer(String, String, String, String, Long, String)}
     */
    @Test
    public void testUploadFileToServer() throws Exception {
        thrown.expect(BTSLBaseException.class);
        c2SBulkReversalServiceImpl.uploadFileToServer("foo.txt", "P dir Path", "text/plain", "Name", 3L, "P attachment");
    }

    /**
     * Method under test: {@link C2SBulkReversalServiceImpl#uploadFileToServer(String, String, String, String, Long, String)}
     */
    @Test
    public void testUploadFileToServer2() throws Exception {
        thrown.expect(BTSLBaseException.class);
        c2SBulkReversalServiceImpl.uploadFileToServer("uploadFileToServer", "P dir Path", "text/plain", "Name", 3L,
                "P attachment");
    }

    /**
     * Method under test: {@link C2SBulkReversalServiceImpl#uploadFileToServer(String, String, String, String, Long, String)}
     */
    @Test
    public void testUploadFileToServer3() throws Exception {
        thrown.expect(BTSLBaseException.class);
        c2SBulkReversalServiceImpl.uploadFileToServer("foo.txt", null, "text/plain", "Name", 3L, "P attachment");
    }

    /**
     * Method under test: {@link C2SBulkReversalServiceImpl#processUploadedFile(Connection, ChannelUserVO, Locale, C2SBulkReversalRequestVO, MessageResources)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessUploadedFile() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.channelAdmin.service.C2SBulkReversalServiceImpl.processUploadedFile(C2SBulkReversalServiceImpl.java:192)

        // Arrange
        // TODO: Populate arranged inputs
        Connection conn = null;
        ChannelUserVO loginUserVO = null;
        Locale senderLanguage = null;
        C2SBulkReversalRequestVO req = null;
        MessageResources messageResources = null;

        // Act
        List<C2SRechargeReversalDetails> actualProcessUploadedFileResult = this.c2SBulkReversalServiceImpl
                .processUploadedFile(conn, loginUserVO, senderLanguage, req, messageResources);

        // Assert
        // TODO: Add assertions on result
    }
}

