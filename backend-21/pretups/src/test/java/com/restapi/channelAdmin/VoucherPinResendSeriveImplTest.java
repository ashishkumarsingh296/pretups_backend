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

@ContextConfiguration(classes = {VoucherPinResendSeriveImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class VoucherPinResendSeriveImplTest {
    @Autowired
    private VoucherPinResendSeriveImpl voucherPinResendSeriveImpl;

    /**
     * Method under test: {@link VoucherPinResendSeriveImpl#viewVoucherList(Connection, String, HttpServletResponse, VoucherPinResendRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewVoucherList() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.VoucherPinResendSeriveImpl.viewVoucherList(VoucherPinResendSeriveImpl.java:53)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();
       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        VoucherPinResendRequestVO requestVO = new VoucherPinResendRequestVO();
        requestVO.setCustomerMsisdn("Customer Msisdn");
        requestVO.setDate("2020-03-01");
        requestVO.setRemarks("Remarks");
        requestVO.setRequestGatewayCode("Request Gateway Code");
        requestVO.setSerialNo("Serial No");
        requestVO.setSubscriberMsisdn("Subscriber Msisdn");
        requestVO.setTransactionid("Transactionid");
        voucherPinResendSeriveImpl.viewVoucherList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, requestVO);
    }

    /**
     * Method under test: {@link VoucherPinResendSeriveImpl#viewVoucherDetailList(Connection, String, HttpServletResponse, VoucherPinResendRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewVoucherDetailList() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.VoucherPinResendSeriveImpl.viewVoucherDetailList(VoucherPinResendSeriveImpl.java:186)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        VoucherPinResendRequestVO requestVO = new VoucherPinResendRequestVO();
        requestVO.setCustomerMsisdn("Customer Msisdn");
        requestVO.setDate("2020-03-01");
        requestVO.setRemarks("Remarks");
        requestVO.setRequestGatewayCode("Request Gateway Code");
        requestVO.setSerialNo("Serial No");
        requestVO.setSubscriberMsisdn("Subscriber Msisdn");
        requestVO.setTransactionid("Transactionid");
        voucherPinResendSeriveImpl.viewVoucherDetailList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, requestVO);
    }

    /**
     * Method under test: {@link VoucherPinResendSeriveImpl#sendPin(Connection, String, HttpServletResponse, VoucherPinResendRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSendPin() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.VoucherPinResendSeriveImpl.sendPin(VoucherPinResendSeriveImpl.java:247)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);
        com.btsl.util.JUnitConfig.init();

       CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        VoucherPinResendRequestVO request = new VoucherPinResendRequestVO();
        request.setCustomerMsisdn("Customer Msisdn");
        request.setDate("2020-03-01");
        request.setRemarks("Remarks");
        request.setRequestGatewayCode("Request Gateway Code");
        request.setSerialNo("Serial No");
        request.setSubscriberMsisdn("Subscriber Msisdn");
        request.setTransactionid("Transactionid");
        voucherPinResendSeriveImpl.sendPin(com.btsl.util.JUnitConfig.getConnection(), "Login ID", response1, request);
    }
}

