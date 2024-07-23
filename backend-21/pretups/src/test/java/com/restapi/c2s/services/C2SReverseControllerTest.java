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
import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.util.RawValue;

import java.util.ArrayList;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {C2SReverseController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SReverseControllerTest {
    @Autowired
    private C2SReverseController c2SReverseController;

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
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
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(null);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        c2sReverseController.parseRequestForMessage(request, prequestVO);
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(new BinaryNode(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1}));
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage4() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setRequestMSISDN(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode).textValue();
        verify(prequestVO).setRequestMSISDN(Mockito.<String>any());
        verify(prequestVO).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage5() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode).textValue();
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage6() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("foo"));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage7() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("parseRequestForMessage"));
        arrayNode.addRawValue(new RawValue("foo"));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage8() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestForMessage(C2SReverseController.java:411)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("parseRequestForMessage"));
        arrayNode.addRawValue(new RawValue((String) null));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sReverseController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestfromJson(JsonNode, RequestVO)}
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
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestfromJson(C2SReverseController.java:458)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestfromJson(C2SReverseController.java:458)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        c2sReverseController.parseRequestfromJson(request, requestVO);
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestfromJson(JsonNode, RequestVO)}
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
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestfromJson(C2SReverseController.java:458)
        //   See https://diff.blue/R013 to resolve this issue.

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestfromJson(C2SReverseController.java:458)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(null);
        RequestVO requestVO = mock(RequestVO.class);
        when(requestVO.getReqContentType()).thenReturn("text/plain");
        doNothing().when(requestVO).setReqContentType(Mockito.<String>any());
        c2sReverseController.parseRequestfromJson(request, requestVO);
    }

    /**
     * Method under test: {@link C2SReverseController#parseRequestfromJson(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestfromJson3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.common.PretupsRestUtil.getMessageString(PretupsRestUtil.java:138)
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestfromJson(C2SReverseController.java:458)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
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
        PretupsResponse<JsonNode> actualParseRequestfromJsonResult = c2sReverseController.parseRequestfromJson(request,
                requestVO);
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
     * Method under test: {@link C2SReverseController#parseRequestfromJson(JsonNode, RequestVO)}
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
        //       at com.restapi.c2s.services.C2SReverseController.parseRequestfromJson(C2SReverseController.java:458)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SReverseController c2sReverseController = new C2SReverseController();
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
        PretupsResponse<JsonNode> actualParseRequestfromJsonResult = c2sReverseController.parseRequestfromJson(request,
                requestVO);
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

    /**
     * Method under test: {@link C2SReverseController#processRechargeReversalRequest(MultiValueMap, C2SRechargeReversalRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRechargeReversalRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/c2sServices/rcrev", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SRechargeReversalRequestVO c2sRechargeReversalRequestVO = new C2SRechargeReversalRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sRechargeReversalRequestVO.setData(data);
        ArrayList<C2SRechargeReversalDetails> data2 = new ArrayList<>();
        c2sRechargeReversalRequestVO.setDataRev(data2);
        c2sRechargeReversalRequestVO.setLoginId("42");
        c2sRechargeReversalRequestVO.setMsisdn("Msisdn");
        c2sRechargeReversalRequestVO.setPassword("iloveyou");
        c2sRechargeReversalRequestVO.setPin("Pin");
        c2sRechargeReversalRequestVO.setPin1("Sender Pin");
        c2sRechargeReversalRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sRechargeReversalRequestVO.setReqGatewayLoginId("42");
        c2sRechargeReversalRequestVO.setReqGatewayPassword("iloveyou");
        c2sRechargeReversalRequestVO.setReqGatewayType("Req Gateway Type");
        c2sRechargeReversalRequestVO.setServicePort("Service Port");
        c2sRechargeReversalRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sRechargeReversalRequestVO));
        Object[] controllers = new Object[]{c2SReverseController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

