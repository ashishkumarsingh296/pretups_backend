package com.restapi.channelAdmin.serviceI;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.security.CustomResponseWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalConfirmVO;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalListRequestVO;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {CAC2STransferReversalServiceI.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class CAC2STransferReversalServiceITest {
    @Autowired
    private CAC2STransferReversalServiceI cAC2STransferReversalServiceI;

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#getTransferReversalList(Connection, String, CAC2STransferReversalListRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetTransferReversalList() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init();

        CAC2STransferReversalListRequestVO requestVO = new CAC2STransferReversalListRequestVO();
        requestVO.setSenderMsisdn("Sender Msisdn");
        requestVO.setServiceType("Service Type");
        requestVO.setTransactionID("Transaction ID");
        requestVO.setUserMsisdn("User Msisdn");
        cAC2STransferReversalServiceI.getTransferReversalList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#getTransferReversalList(Connection, String, CAC2STransferReversalListRequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetTransferReversalList2() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.getTransferReversalList(CAC2STransferReversalServiceI.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        com.btsl.util.JUnitConfig.init();

        CAC2STransferReversalListRequestVO requestVO = mock(CAC2STransferReversalListRequestVO.class);
        doNothing().when(requestVO).setSenderMsisdn(Mockito.<String>any());
        doNothing().when(requestVO).setServiceType(Mockito.<String>any());
        doNothing().when(requestVO).setTransactionID(Mockito.<String>any());
        doNothing().when(requestVO).setUserMsisdn(Mockito.<String>any());
        requestVO.setSenderMsisdn("Sender Msisdn");
        requestVO.setServiceType("Service Type");
        requestVO.setTransactionID("Transaction ID");
        requestVO.setUserMsisdn("User Msisdn");
        cAC2STransferReversalServiceI.getTransferReversalList(com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#confirmTransferReversal(Connection, CAC2STransferReversalConfirmVO, MultiValueMap, String, String, HttpServletResponse)}
     */
    @Test
    public void testConfirmTransferReversal() {
        Connection con = mock(Connection.class);

        CAC2STransferReversalConfirmVO requestVO = new CAC2STransferReversalConfirmVO();
        requestVO.setSenderMsisdn("Sender Msisdn");
        requestVO.setTransactionID("Transaction ID");
        requestVO.setUserMsisdn("User Msisdn");
        HttpHeaders headers = new HttpHeaders();
        assertTrue(
                cAC2STransferReversalServiceI
                        .confirmTransferReversal (com.btsl.util.JUnitConfig.getConnection(), requestVO, headers, "Login ID", "Request IDStr",
                                new CustomResponseWrapper(new Response()))
                        .getSuccessList()
                        .isEmpty());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#confirmTransferReversal(Connection, CAC2STransferReversalConfirmVO, MultiValueMap, String, String, HttpServletResponse)}
     */
    @Test
    public void testConfirmTransferReversal2() {
        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        CAC2STransferReversalConfirmVO requestVO = new CAC2STransferReversalConfirmVO();
        requestVO.setSenderMsisdn("Sender Msisdn");
        requestVO.setTransactionID("Transaction ID");
        requestVO.setUserMsisdn("User Msisdn");
        HttpHeaders headers = new HttpHeaders();
        assertTrue(
                cAC2STransferReversalServiceI
                        .confirmTransferReversal (com.btsl.util.JUnitConfig.getConnection(), requestVO, headers, "Login ID", "Request IDStr",
                                new CustomResponseWrapper(new Response()))
                        .getSuccessList()
                        .isEmpty());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = cac2sTransferReversalServiceI
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestForMessage2() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.RuntimeException: parseRequestForMessage
        //       at com.btsl.pretups.receiver.RequestVO.setRequestMessage(RequestVO.java:556)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:518)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO prequestVO = mock(RequestVO.class);
        doThrow(new RuntimeException("parseRequestForMessage")).when(prequestVO).setReqContentType(Mockito.<String>any());
        doThrow(new RuntimeException("parseRequestForMessage")).when(prequestVO).setRequestMessage(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestForMessage(request, prequestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestForMessage3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(null);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestForMessage(request, prequestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage4() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(new BinaryNode(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1}));
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = cac2sTransferReversalServiceI
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage5() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setRequestMSISDN(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = cac2sTransferReversalServiceI
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode).textValue();
        verify(prequestVO).setRequestMSISDN(Mockito.<String>any());
        verify(prequestVO).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestForMessage6() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.RuntimeException: parseRequestForMessage
        //       at com.btsl.pretups.receiver.RequestVO.setRequestMSISDN(RequestVO.java:586)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:523)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doThrow(new RuntimeException("parseRequestForMessage")).when(prequestVO).setRequestMSISDN(Mockito.<String>any());
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestForMessage(request, prequestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage7() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestForMessage(CAC2STransferReversalServiceI.java:508)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = cac2sTransferReversalServiceI
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode).textValue();
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestfromJson(request, requestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson2() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.RuntimeException: parseRequestfromJson
        //       at com.btsl.pretups.receiver.RequestVO.getReqContentType(RequestVO.java:1063)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:545)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenThrow(new RuntimeException("parseRequestfromJson"));
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO).setReqContentType(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestfromJson(request, requestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(null);
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestfromJson(request, requestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestfromJson4() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO requestVO = mock(RequestVO.class);
        doNothing().when(requestVO).setLogin(Mockito.<String>any());
        doNothing().when(requestVO).setPassword(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayType(Mockito.<String>any());
        doNothing().when(requestVO).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO).setSourceType(Mockito.<String>any());
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        PretupsResponse<JsonNode> actualParseRequestfromJsonResult = cac2sTransferReversalServiceI
                .parseRequestfromJson(request, requestVO);
        assertTrue(actualParseRequestfromJsonResult.getStatus());
        assertNull(actualParseRequestfromJsonResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode, atLeast(1)).textValue();
        verify(requestVO).getReqContentType();
        verify(requestVO).setLogin(Mockito.<String>any());
        verify(requestVO).setPassword(Mockito.<String>any());
        verify(requestVO).setRequestGatewayCode(Mockito.<String>any());
        verify(requestVO).setRequestGatewayType(Mockito.<String>any());
        verify(requestVO).setServicePort(Mockito.<String>any());
        verify(requestVO).setSourceType(Mockito.<String>any());
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testParseRequestfromJson5() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.RuntimeException: parseRequestfromJson
        //       at com.btsl.pretups.receiver.RequestVO.setRequestGatewayCode(RequestVO.java:996)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:558)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO requestVO = mock(RequestVO.class);
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO).setLogin(Mockito.<String>any());
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO).setPassword(Mockito.<String>any());
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO)
                .setRequestGatewayCode(Mockito.<String>any());
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO)
                .setRequestGatewayType(Mockito.<String>any());
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO).setServicePort(Mockito.<String>any());
        doThrow(new RuntimeException("parseRequestfromJson")).when(requestVO).setSourceType(Mockito.<String>any());
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        cac2sTransferReversalServiceI.parseRequestfromJson(request, requestVO);
    }

    /**
     * Method under test: {@link CAC2STransferReversalServiceI#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestfromJson6() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.channelAdmin.serviceI.CAC2STransferReversalServiceI.parseRequestfromJson(CAC2STransferReversalServiceI.java:555)
        //   See https://diff.blue/R013 to resolve this issue.

        CAC2STransferReversalServiceI cac2sTransferReversalServiceI = new CAC2STransferReversalServiceI();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO requestVO = mock(RequestVO.class);
        doNothing().when(requestVO).setLogin(Mockito.<String>any());
        doNothing().when(requestVO).setPassword(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO).setRequestGatewayType(Mockito.<String>any());
        doNothing().when(requestVO).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO).setSourceType(Mockito.<String>any());
        when(requestVO.getReqContentType()).thenReturn(null);
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestfromJsonResult = cac2sTransferReversalServiceI
                .parseRequestfromJson(request, requestVO);
        assertTrue(actualParseRequestfromJsonResult.getStatus());
        assertNull(actualParseRequestfromJsonResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode, atLeast(1)).textValue();
        verify(requestVO).getReqContentType();
        verify(requestVO).setLogin(Mockito.<String>any());
        verify(requestVO).setPassword(Mockito.<String>any());
        verify(requestVO).setReqContentType(Mockito.<String>any());
        verify(requestVO).setRequestGatewayCode(Mockito.<String>any());
        verify(requestVO).setRequestGatewayType(Mockito.<String>any());
        verify(requestVO).setServicePort(Mockito.<String>any());
        verify(requestVO).setSourceType(Mockito.<String>any());
    }
}

