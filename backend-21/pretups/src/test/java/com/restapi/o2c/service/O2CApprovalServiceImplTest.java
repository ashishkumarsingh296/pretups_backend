package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.util.JUnitConfig;
import com.web.pretups.channel.transfer.web.ChannelTransferApprovalForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {O2CApprovalServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CApprovalServiceImplTest {
    @Autowired
    private O2CApprovalServiceImpl o2CApprovalServiceImpl;

    /**
     * Method under test: {@link O2CApprovalServiceImpl#confirmOptToChannelTransfer(ChannelTransferApprovalForm, String, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConfirmOptToChannelTransfer() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.cancelOrder(O2CApprovalServiceImpl.java:2119)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.confirmOptToChannelTransfer(O2CApprovalServiceImpl.java:1416)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelTransferApprovalForm theForm = new ChannelTransferApprovalForm();
        o2CApprovalServiceImpl.confirmOptToChannelTransfer(theForm, "Approve Or Reject", ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#confirmOptToChannelTransfer(ChannelTransferApprovalForm, String, ChannelUserVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testConfirmOptToChannelTransfer2() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.confirmOptToChannelTransfer(O2CApprovalServiceImpl.java:1407)
        //   See https://diff.blue/R013 to resolve this issue.

        o2CApprovalServiceImpl.confirmOptToChannelTransfer(null, "Approve Or Reject", ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#getMessage(Locale, String, String[])}
     */
    @Test
    public void testGetMessage() {
        assertNull(o2CApprovalServiceImpl.getMessage(Locale.getDefault(), "An error occurred", new String[]{"Args"}));
        assertNull(o2CApprovalServiceImpl.getMessage(Locale.getDefault(Locale.Category.DISPLAY), "An error occurred",
                new String[]{"Args"}));
        assertNull(o2CApprovalServiceImpl.getMessage(null, "An error occurred", new String[]{"Args"}));
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processO2CStockApproval(O2CStockAppRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CStockApproval() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();

        O2CStockAppRequestVO o2cStockAppRequestVO = new O2CStockAppRequestVO();

        List<O2CDataStApp> listt =  new ArrayList<>();
        O2CDataStApp o2cData = new O2CDataStApp();

        //PretupsI.CHANNEL_TRANSFER_ORDER_NEW
        //PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1
        //PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2
        o2cData.setCurrentStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);

        o2cData.setExtNwCode("1234");
        o2cData.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        o2cData.setRemarks("String");
        o2cData.setPin("1357");
        o2cData.setExtTxnNumber("1234");
        o2cData.setExtNwCode("String");
        O2CPaymentdetailAppr paymentDetails = new O2CPaymentdetailAppr();
        paymentDetails.setPaymentDate("01/01/23");
        paymentDetails.setPaymentType("CASH");
        paymentDetails.setPaymentInstNumber("1234");

        o2cData.setPaymentDetails(paymentDetails);
        List<O2CProductAppr> products = new ArrayList<>();

        O2CProductAppr prod = new O2CProductAppr();
        prod.setProductCode("String");
        prod.setAppQuantity("10");

        products.add(prod) ;
        o2cData.setProducts(products);
        o2cData.setRefNumber("1234");
        o2cData.setToMsisdn("9999999999");
        o2cData.setTxnId("1234");

        listt.add(o2cData) ;


        o2cStockAppRequestVO.setO2cStockAppRequests(listt);
        HttpHeaders headers = new HttpHeaders();

        CustomResponseWrapper response1 = mock(CustomResponseWrapper.class) ;

        doNothing().when(response1).setStatus(Mockito.anyInt());
      try {
          when(JUnitConfig.getConnection().prepareStatement(Mockito.anyString()).executeQuery().getString("status")).thenReturn(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
      }catch (Exception e){
e.printStackTrace();
      }

        try {
            when(JUnitConfig.getMComConnection().getConnection().prepareStatement(Mockito.anyString()).executeQuery().getString("status")).thenReturn("TESTER");
        }catch (Exception e){
            e.printStackTrace();
        }

        o2CApprovalServiceImpl.processO2CStockApproval(o2cStockAppRequestVO, headers,
                response1);


        //PretupsI.CHANNEL_TRANSFER_ORDER_NEW
        //PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1
        //PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2
        o2cData.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
        o2cData.setCurrentStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
        o2CApprovalServiceImpl.processO2CStockApproval(o2cStockAppRequestVO, headers,
                response1);


        o2cData.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
        o2cData.setCurrentStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
        o2CApprovalServiceImpl.processO2CStockApproval(o2cStockAppRequestVO, headers,
                response1);
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processO2CStockApproval(O2CStockAppRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CStockApproval2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.processO2CStockApproval(O2CApprovalServiceImpl.java:267)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CStockAppRequestVO o2cStockAppRequestVO = mock(O2CStockAppRequestVO.class);
        doNothing().when(o2cStockAppRequestVO).setO2cStockAppRequests(Mockito.<List<O2CDataStApp>>any());
        o2cStockAppRequestVO.setO2cStockAppRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        o2CApprovalServiceImpl.processO2CStockApproval(o2cStockAppRequestVO, headers,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processO2CStockApproval(O2CStockAppRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
    public void testProcessO2CStockApproval3() throws BTSLBaseException {
        O2CStockAppRequestVO o2cStockAppRequestVO = mock(O2CStockAppRequestVO.class);
        doNothing().when(o2cStockAppRequestVO).setO2cStockAppRequests(Mockito.<List<O2CDataStApp>>any());
        o2cStockAppRequestVO.setO2cStockAppRequests(new ArrayList<>());
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        BaseResponseMultiple actualProcessO2CStockApprovalResult = o2CApprovalServiceImpl
                .processO2CStockApproval(o2cStockAppRequestVO, headers, responseSwag);
        assertTrue(actualProcessO2CStockApprovalResult.getSuccessList().isEmpty());
        assertEquals("400", actualProcessO2CStockApprovalResult.getStatus());
        assertEquals("stockApproval", actualProcessO2CStockApprovalResult.getService());
        assertEquals("java.lang.NullPointerException", actualProcessO2CStockApprovalResult.getMessageCode());
        assertEquals("java.lang.NullPointerException : null", actualProcessO2CStockApprovalResult.getMessage());
        verify(o2cStockAppRequestVO).setO2cStockAppRequests(Mockito.<List<O2CDataStApp>>any());
        assertEquals(400, ((MockHttpServletResponse) responseSwag.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processO2CStockApproval(O2CStockAppRequestVO, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessO2CStockApproval4() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.processO2CStockApproval(O2CApprovalServiceImpl.java:267)
        //   See https://diff.blue/R013 to resolve this issue.

        O2CStockAppRequestVO o2cStockAppRequestVO = mock(O2CStockAppRequestVO.class);
        doNothing().when(o2cStockAppRequestVO).setO2cStockAppRequests(Mockito.<List<O2CDataStApp>>any());
        o2cStockAppRequestVO.setO2cStockAppRequests(new ArrayList<>());
        o2CApprovalServiceImpl.processO2CStockApproval(o2cStockAppRequestVO, new HttpHeaders(), null);
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processOptToChannelTransfer(ChannelTransferApprovalForm, O2CDataStApp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessOptToChannelTransfer() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.processOptToChannelTransfer(O2CApprovalServiceImpl.java:971)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelTransferApprovalForm theForm = new ChannelTransferApprovalForm();
        o2CApprovalServiceImpl.processOptToChannelTransfer(theForm, new O2CDataStApp());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processOptToChannelTransfer(ChannelTransferApprovalForm, O2CDataStApp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessOptToChannelTransfer2() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.processOptToChannelTransfer(O2CApprovalServiceImpl.java:971)
        //   See https://diff.blue/R013 to resolve this issue.

        o2CApprovalServiceImpl.processOptToChannelTransfer(null, new O2CDataStApp());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#processOptToChannelTransfer(ChannelTransferApprovalForm, O2CDataStApp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessOptToChannelTransfer3() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.processOptToChannelTransfer(O2CApprovalServiceImpl.java:971)
        //   See https://diff.blue/R013 to resolve this issue.

        ChannelTransferApprovalForm theForm = mock(ChannelTransferApprovalForm.class);
        o2CApprovalServiceImpl.processOptToChannelTransfer(theForm, new O2CDataStApp());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#transferApprovalConfirmation(ChannelTransferApprovalForm, String, O2CDataStApp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testTransferApprovalConfirmation() throws Exception {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.transferApprovalConfirmation(O2CApprovalServiceImpl.java:772)

        // Arrange
        // TODO: Populate arranged inputs
        ChannelTransferApprovalForm theForm = null;
        String transferId = "";
        O2CDataStApp o2CDataAppRequest = null;

        // Act
        this.o2CApprovalServiceImpl.transferApprovalConfirmation(theForm, transferId, o2CDataAppRequest);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#transferApprovalDetails(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testTransferApprovalDetails() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.transferApprovalDetails(O2CApprovalServiceImpl.java:2815)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        o2CApprovalServiceImpl.transferApprovalDetails(headers, "Transaction ID",
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#transferApprovalDetails(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testTransferApprovalDetails2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.transferApprovalDetails(O2CApprovalServiceImpl.java:2815)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        headers.add("https://example.org/example", "https://example.org/example");
        o2CApprovalServiceImpl.transferApprovalDetails(headers, "Transaction ID",
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#transferApprovalDetails(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
    public void testTransferApprovalDetails3() {
        HttpHeaders headers = new HttpHeaders();
        O2CApprovalTxnDetailsResponseVO actualTransferApprovalDetailsResult = o2CApprovalServiceImpl
                .transferApprovalDetails(headers, "Transaction ID", new CustomResponseWrapper(new MockHttpServletResponse()));
        assertNull(actualTransferApprovalDetailsResult.getAddress());
        assertNull(actualTransferApprovalDetailsResult.getGeographicDomainName());
        assertNull(actualTransferApprovalDetailsResult.getGeographicDomainList());
        assertNull(actualTransferApprovalDetailsResult.getGeographicDomainCode());
        assertNull(actualTransferApprovalDetailsResult.getGeoDomainNameForUser());
        assertNull(actualTransferApprovalDetailsResult.getGeoDomainCodeForUser());
        assertNull(actualTransferApprovalDetailsResult.getGardeDesc());
        assertNull(actualTransferApprovalDetailsResult.getFirstLevelApprovedQuantity());
        assertNull(actualTransferApprovalDetailsResult.getFirstApprovalLimit());
        assertNull(actualTransferApprovalDetailsResult.getExternalTxnNum());
        assertNull(actualTransferApprovalDetailsResult.getExternalTxnMandatory());
        assertNull(actualTransferApprovalDetailsResult.getExternalTxnExist());
        assertNull(actualTransferApprovalDetailsResult.getExternalTxnDate());
        assertNull(actualTransferApprovalDetailsResult.getErrorList());
        assertNull(actualTransferApprovalDetailsResult.getErpCode());
        assertNull(actualTransferApprovalDetailsResult.getDomainTypeCode());
        assertNull(actualTransferApprovalDetailsResult.getDomainNameForUserCode());
        assertNull(actualTransferApprovalDetailsResult.getDomainName());
        assertNull(actualTransferApprovalDetailsResult.getDomainList());
        assertNull(actualTransferApprovalDetailsResult.getDomainCode());
        assertNull(actualTransferApprovalDetailsResult.getDistributorName());
        assertNull(actualTransferApprovalDetailsResult.getCurrentApprovalLevel());
        assertNull(actualTransferApprovalDetailsResult.getCommissionQuantity());
        assertNull(actualTransferApprovalDetailsResult.getCommissionProfileName());
        assertNull(actualTransferApprovalDetailsResult.getChannelUserStatus());
        assertNull(actualTransferApprovalDetailsResult.getChannelTransferList());
        assertNull(actualTransferApprovalDetailsResult.getChannelOwnerCategoryUserName());
        assertNull(actualTransferApprovalDetailsResult.getChannelOwnerCategoryUserID());
        assertNull(actualTransferApprovalDetailsResult.getChannelOwnerCategoryDesc());
        assertNull(actualTransferApprovalDetailsResult.getChannelOwnerCategory());
        assertNull(actualTransferApprovalDetailsResult.getCategoryName());
        assertNull(actualTransferApprovalDetailsResult.getCategoryList());
        assertNull(actualTransferApprovalDetailsResult.getCategoryCodeForUserCode());
        assertNull(actualTransferApprovalDetailsResult.getCategoryCode());
        assertNull(actualTransferApprovalDetailsResult.getApprove3Remark());
        assertNull(actualTransferApprovalDetailsResult.getApprove2Remark());
        assertNull(actualTransferApprovalDetailsResult.getApprove1Remark());
        assertEquals(0, actualTransferApprovalDetailsResult.getApprovalLevel());
        assertNull(actualTransferApprovalDetailsResult.getAllUser());
        assertNull(actualTransferApprovalDetailsResult.getAllOrder());
    }

    /**
     * Method under test: {@link O2CApprovalServiceImpl#transferApprovalDetails(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testTransferApprovalDetails4() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.o2c.service.O2CApprovalServiceImpl.transferApprovalDetails(O2CApprovalServiceImpl.java:2815)
        //   See https://diff.blue/R013 to resolve this issue.

        o2CApprovalServiceImpl.transferApprovalDetails(new HttpHeaders(), "Transaction ID", null);
    }
}

