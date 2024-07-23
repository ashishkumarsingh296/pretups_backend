package com.restapi.channelAdmin;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.security.CustomResponseWrapper;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {CreateBatchForVoucherDownloadImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateBatchForVoucherDownloadImplTest {
    @Autowired
    private CreateBatchForVoucherDownloadImpl createBatchForVoucherDownloadImpl;

    /**
     * Method under test: {@link CreateBatchForVoucherDownloadImpl#getMrpList(Connection, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetMrpList() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.CreateBatchForVoucherDownloadImpl.getMrpList(CreateBatchForVoucherDownloadImpl.java:47)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        createBatchForVoucherDownloadImpl.getMrpList(com.btsl.util.JUnitConfig.getConnection(), "42", "Voucher Type", "Voucher Segment",
                response1);
    }

    /**
     * Method under test: {@link CreateBatchForVoucherDownloadImpl#getBatchIdDetails(Connection, String, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetBatchIdDetails() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.CreateBatchForVoucherDownloadImpl.getBatchIdDetails(CreateBatchForVoucherDownloadImpl.java:129)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        createBatchForVoucherDownloadImpl.getBatchIdDetails(com.btsl.util.JUnitConfig.getConnection(), "42", "Denomination", "Voucher Type", "Voucher Segment",
                response1);
    }
}

