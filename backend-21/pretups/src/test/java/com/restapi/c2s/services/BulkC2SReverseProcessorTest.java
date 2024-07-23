package com.restapi.c2s.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.util.RawValue;

import java.util.ArrayList;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

public class BulkC2SReverseProcessorTest {
    /**
     * Method under test: {@link BulkC2SReverseProcessor#processRequestChannel(C2SRechargeReversalRequestVO, String, String, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessRequestChannel() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.BulkC2SReverseProcessor.processRequestChannel(BulkC2SReverseProcessor.java:344)
        //   See https://diff.blue/R013 to resolve this issue.

        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        C2SRechargeReversalRequestVO requestVOX = new C2SRechargeReversalRequestVO();
        requestVOX.setData(data);
        requestVOX.setDataRev(new ArrayList<>());
        requestVOX.setLoginId("42");
        requestVOX.setMsisdn("Msisdn");
        requestVOX.setPassword("iloveyou");
        requestVOX.setPin("Pin");
        requestVOX.setPin1("Sender Pin");
        requestVOX.setReqGatewayCode("Req Gateway Code");
        requestVOX.setReqGatewayLoginId("42");
        requestVOX.setReqGatewayPassword("iloveyou");
        requestVOX.setReqGatewayType("Req Gateway Type");
        requestVOX.setServicePort("Service Port");
        requestVOX.setSourceType("Source Type");
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        bulkC2SReverseProcessor.processRequestChannel(requestVOX, "Service Keyword", "Request Id Channel", headers,
                response1);
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#processRequestChannel(C2SRechargeReversalRequestVO, String, String, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessRequestChannel2() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.BulkC2SReverseProcessor.processRequestChannel(BulkC2SReverseProcessor.java:344)
        //   See https://diff.blue/R013 to resolve this issue.

        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        C2SRechargeReversalDetails c2sRechargeReversalDetails = new C2SRechargeReversalDetails();
        c2sRechargeReversalDetails.setExtNwCode("processRequestChannel");
        c2sRechargeReversalDetails.setExtcode("processRequestChannel");
        c2sRechargeReversalDetails.setLoginid("processRequestChannel");
        c2sRechargeReversalDetails.setMsisdn("processRequestChannel");
        c2sRechargeReversalDetails.setPassword("iloveyou");
        c2sRechargeReversalDetails.setPin("processRequestChannel");
        c2sRechargeReversalDetails.setTxnid("processRequestChannel");
        c2sRechargeReversalDetails.setUserid("processRequestChannel");

        ArrayList<C2SRechargeReversalDetails> data2 = new ArrayList<>();
        data2.add(c2sRechargeReversalDetails);

        C2SRechargeReversalRequestVO requestVOX = new C2SRechargeReversalRequestVO();
        requestVOX.setData(data);
        requestVOX.setDataRev(data2);
        requestVOX.setLoginId("42");
        requestVOX.setMsisdn("Msisdn");
        requestVOX.setPassword("iloveyou");
        requestVOX.setPin("Pin");
        requestVOX.setPin1("Sender Pin");
        requestVOX.setReqGatewayCode("Req Gateway Code");
        requestVOX.setReqGatewayLoginId("42");
        requestVOX.setReqGatewayPassword("iloveyou");
        requestVOX.setReqGatewayType("Req Gateway Type");
        requestVOX.setServicePort("Service Port");
        requestVOX.setSourceType("Source Type");
        HttpHeaders headers = new HttpHeaders();

        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        bulkC2SReverseProcessor.processRequestChannel(requestVOX, "Service Keyword", "Request Id Channel", headers,
                response1);
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage() throws BTSLBaseException {
        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = bulkC2SReverseProcessor
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testParseRequestForMessage2() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.BulkC2SReverseProcessor.parseRequestForMessage(BulkC2SReverseProcessor.java:368)
        //   See https://diff.blue/R013 to resolve this issue.

        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(null);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        bulkC2SReverseProcessor.parseRequestForMessage(request, prequestVO);
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage3() throws BTSLBaseException {
        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(new BinaryNode(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1}));
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = bulkC2SReverseProcessor
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage4() throws BTSLBaseException {
        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("foo"));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = bulkC2SReverseProcessor
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage5() throws BTSLBaseException {
        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("parseRequestForMessage"));
        arrayNode.addRawValue(new RawValue("foo"));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = bulkC2SReverseProcessor
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link BulkC2SReverseProcessor#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage6() throws BTSLBaseException {
        BulkC2SReverseProcessor bulkC2SReverseProcessor = new BulkC2SReverseProcessor();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("parseRequestForMessage"));
        arrayNode.addRawValue(new RawValue((String) null));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = bulkC2SReverseProcessor
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }
}

