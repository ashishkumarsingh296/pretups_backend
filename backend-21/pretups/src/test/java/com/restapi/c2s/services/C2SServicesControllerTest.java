package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.util.RawValue;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {C2SServicesController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SServicesControllerTest {
    @MockBean
    private C2SServiceI c2SServiceI;

    @Autowired
    private C2SServicesController c2SServicesController;

    /**
     * Method under test: {@link C2SServicesController#getDenominationsMvd(MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetDenominationsMvd() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/c2sServices/getDenomination",
                uriVars);
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#prepareJsonResponse(RequestVO)}
     */
    @Test
    public void testPrepareJsonResponse() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        C2SServicesController c2sServicesController = new C2SServicesController();
        RequestVO prequestVO = mock(RequestVO.class);
        when(prequestVO.isSuccessTxn()).thenReturn(true);
        when(prequestVO.getRequestIDStr()).thenReturn("Request IDStr");
        when(prequestVO.getMessageArguments()).thenReturn(new String[]{"Message Arguments"});
        when(prequestVO.getMessageCode()).thenReturn("Message Code");
        c2sServicesController.prepareJsonResponse(prequestVO);
        verify(prequestVO).isSuccessTxn();
        verify(prequestVO, atLeast(1)).getMessageCode();
        verify(prequestVO).getRequestIDStr();
        verify(prequestVO).getMessageArguments();
    }

    /**
     * Method under test: {@link C2SServicesController#prepareJsonResponse(RequestVO)}
     */
    @Test
    public void testPrepareJsonResponse2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R031 Method may be time-sensitive.
        //   Diffblue Cover was only able to write tests which were time-sensitive.
        //   The assertions no longer passed when run at an alternate date, time and
        //   timezone. Try refactoring the method to take a java.time.Clock instance so
        //   that the time can be parameterized during testing.
        //   Please see https://diff.blue/R031

        C2SServicesController c2sServicesController = new C2SServicesController();
        RequestVO prequestVO = mock(RequestVO.class);
        when(prequestVO.getMessageCode()).thenReturn("");
        doNothing().when(prequestVO).setJsonReponse(Mockito.<PretupsResponse<JsonNode>>any());
        doNothing().when(prequestVO).setSenderReturnMessage(Mockito.<String>any());
        c2sServicesController.prepareJsonResponse(prequestVO);
        verify(prequestVO).getMessageCode();
        verify(prequestVO).setJsonReponse(Mockito.<PretupsResponse<JsonNode>>any());
        verify(prequestVO).setSenderReturnMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServicesController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServicesController.parseRequestForMessage(C2SServicesController.java:944)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServicesController c2sServicesController = new C2SServicesController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(MissingNode.getInstance());
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sServicesController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServicesController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage2() throws BTSLBaseException, UnsupportedEncodingException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServicesController.parseRequestForMessage(C2SServicesController.java:944)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServicesController c2sServicesController = new C2SServicesController();
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(new BinaryNode("AXAXAXAX".getBytes("UTF-8")));
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sServicesController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServicesController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage3() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServicesController.parseRequestForMessage(C2SServicesController.java:944)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServicesController c2sServicesController = new C2SServicesController();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("42");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setRequestMSISDN(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sServicesController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode).textValue();
        verify(prequestVO).setRequestMSISDN(Mockito.<String>any());
        verify(prequestVO).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServicesController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage4() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServicesController.parseRequestForMessage(C2SServicesController.java:944)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServicesController c2sServicesController = new C2SServicesController();
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.textValue()).thenReturn("");
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sServicesController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(arrayNode).textValue();
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServicesController#parseRequestForMessage(JsonNode, RequestVO)}
     */
    @Test
    public void testParseRequestForMessage5() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.C2SServicesController.parseRequestForMessage(C2SServicesController.java:944)
        //   See https://diff.blue/R013 to resolve this issue.

        C2SServicesController c2sServicesController = new C2SServicesController();

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.withExactBigDecimals(true));
        arrayNode.addRawValue(new RawValue("foo"));
        JsonNode request = mock(JsonNode.class);
        when(request.get(Mockito.<String>any())).thenReturn(arrayNode);
        RequestVO prequestVO = mock(RequestVO.class);
        doNothing().when(prequestVO).setReqContentType(Mockito.<String>any());
        doNothing().when(prequestVO).setRequestMessage(Mockito.<String>any());
        PretupsResponse<JsonNode> actualParseRequestForMessageResult = c2sServicesController
                .parseRequestForMessage(request, prequestVO);
        assertTrue(actualParseRequestForMessageResult.getStatus());
        assertNull(actualParseRequestForMessageResult.getFieldError());
        verify(request, atLeast(1)).get(Mockito.<String>any());
        verify(prequestVO).setReqContentType(Mockito.<String>any());
        verify(prequestVO, atLeast(1)).setRequestMessage(Mockito.<String>any());
    }

    /**
     * Method under test: {@link C2SServicesController#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    public void testStaffUserDetails() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Diffblue AI was unable to find a test

        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
        c2SServicesController.staffUserDetails(channelUserVO, ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link C2SServicesController#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    public void testStaffUserDetails2() {
        ChannelUserVO channelUserVO = mock(ChannelUserVO.class);
        when(channelUserVO.getParentID()).thenReturn("Parent ID");
        doNothing().when(channelUserVO).setParentLoginID(Mockito.<String>any());
        doNothing().when(channelUserVO).setPinRequired(Mockito.<String>any());
        doNothing().when(channelUserVO).setSmsPin(Mockito.<String>any());
        doNothing().when(channelUserVO).setMsisdn(Mockito.<String>any());
        doNothing().when(channelUserVO).setOwnerID(Mockito.<String>any());
        doNothing().when(channelUserVO).setParentID(Mockito.<String>any());
        doNothing().when(channelUserVO).setStaffUser(anyBoolean());
        doNothing().when(channelUserVO).setStatus(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserID(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserType(Mockito.<String>any());
        ChannelUserVO parentChannelUserVO = ChannelUserVO.getInstance();
        c2SServicesController.staffUserDetails(channelUserVO, parentChannelUserVO);
        verify(channelUserVO).getParentID();
        verify(channelUserVO).setParentLoginID(Mockito.<String>any());
        verify(channelUserVO).setPinRequired(Mockito.<String>any());
        verify(channelUserVO).setSmsPin(Mockito.<String>any());
        verify(channelUserVO).setMsisdn(Mockito.<String>any());
        verify(channelUserVO).setOwnerID(Mockito.<String>any());
        verify(channelUserVO).setParentID(Mockito.<String>any());
        verify(channelUserVO).setStaffUser(anyBoolean());
        verify(channelUserVO).setStatus(Mockito.<String>any());
        verify(channelUserVO).setUserID(Mockito.<String>any());
        verify(channelUserVO).setUserType(Mockito.<String>any());
        assertEquals(0L, parentChannelUserVO.getBalance());
    }

    /**
     * Method under test: {@link C2SServicesController#processEVDRequest(MultiValueMap, C2SRechargeRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessEVDRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/c2sServices/evd", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SRechargeRequestVO c2sRechargeRequestVO = new C2SRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sRechargeRequestVO.setData(data);

        C2SRechargeDetails data2 = new C2SRechargeDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setGifterLang("Gifter Lang");
        data2.setGifterMsisdn("Gifter Msisdn");
        data2.setGifterName("Gifter Name");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setNotifMsisdn("Notif Msisdn");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQty("Qty");
        data2.setSelector("Selector");
        data2.setUserid("Userid");
        c2sRechargeRequestVO.setData(data2);
        c2sRechargeRequestVO.setLoginId("42");
        c2sRechargeRequestVO.setMsisdn("Msisdn");
        c2sRechargeRequestVO.setPassword("iloveyou");
        c2sRechargeRequestVO.setPin("Pin");
        c2sRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sRechargeRequestVO.setReqGatewayLoginId("42");
        c2sRechargeRequestVO.setReqGatewayPassword("iloveyou");
        c2sRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        c2sRechargeRequestVO.setServicePort("Service Port");
        c2sRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sRechargeRequestVO));
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#processGetUserServiceBalanceRequest(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessGetUserServiceBalanceRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{"Service Name"};
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/v1/c2sServices/userservicebal/{serviceName}", uriVars);
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#processGiftRechargeRequest(MultiValueMap, GiftRechargeRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessGiftRechargeRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/c2sServices/grc", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        GiftRechargeRequestVO giftRechargeRequestVO = new GiftRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        giftRechargeRequestVO.setData(data);

        GiftRechargeDetails data2 = new GiftRechargeDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setGifterLang("Gifter Lang");
        data2.setGifterMsisdn("Gifter Msisdn");
        data2.setGifterName("Gifter Name");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setSelector("Selector");
        data2.setUserid("Userid");
        giftRechargeRequestVO.setData(data2);
        giftRechargeRequestVO.setLoginId("42");
        giftRechargeRequestVO.setMsisdn("Msisdn");
        giftRechargeRequestVO.setPassword("iloveyou");
        giftRechargeRequestVO.setPin("Pin");
        giftRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        giftRechargeRequestVO.setReqGatewayLoginId("42");
        giftRechargeRequestVO.setReqGatewayPassword("iloveyou");
        giftRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        giftRechargeRequestVO.setServicePort("Service Port");
        giftRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(giftRechargeRequestVO));
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#processInternetRechargeRequest(MultiValueMap, InternetRechargeRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessInternetRechargeRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                .post("/v1/c2sServices/c2sintrrc", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        InternetRechargeRequestVO internetRechargeRequestVO = new InternetRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        internetRechargeRequestVO.setData(data);

        InternetRechargeDetails data2 = new InternetRechargeDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setNotifMsisdn("Notif Msisdn");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setSelector("Selector");
        data2.setUserid("Userid");
        internetRechargeRequestVO.setData(data2);
        internetRechargeRequestVO.setLoginId("42");
        internetRechargeRequestVO.setMsisdn("Msisdn");
        internetRechargeRequestVO.setPassword("iloveyou");
        internetRechargeRequestVO.setPin("Pin");
        internetRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        internetRechargeRequestVO.setReqGatewayLoginId("42");
        internetRechargeRequestVO.setReqGatewayPassword("iloveyou");
        internetRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        internetRechargeRequestVO.setServicePort("Service Port");
        internetRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(internetRechargeRequestVO));
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#processMVDRequest(MultiValueMap, MvdRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessMVDRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/c2sServices/mvd", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        MvdRequestVO mvdRequestVO = new MvdRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        mvdRequestVO.setData(data);

        MvdDetails data2 = new MvdDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQty("Qty");
        data2.setSelector("Selector");
        data2.setUserid("Userid");
        mvdRequestVO.setData(data2);
        mvdRequestVO.setLoginId("42");
        mvdRequestVO.setMsisdn("Msisdn");
        mvdRequestVO.setPassword("iloveyou");
        mvdRequestVO.setPin("Pin");
        mvdRequestVO.setReqGatewayCode("Req Gateway Code");
        mvdRequestVO.setReqGatewayLoginId("42");
        mvdRequestVO.setReqGatewayPassword("iloveyou");
        mvdRequestVO.setReqGatewayType("Req Gateway Type");
        mvdRequestVO.setServicePort("Service Port");
        mvdRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(mvdRequestVO));
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#processPostPaidBillRequest(MultiValueMap, C2SRechargeRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessPostPaidBillRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/c2sServices/postpaid", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SRechargeRequestVO c2sRechargeRequestVO = new C2SRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sRechargeRequestVO.setData(data);

        C2SRechargeDetails data2 = new C2SRechargeDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setGifterLang("Gifter Lang");
        data2.setGifterMsisdn("Gifter Msisdn");
        data2.setGifterName("Gifter Name");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setNotifMsisdn("Notif Msisdn");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQty("Qty");
        data2.setSelector("Selector");
        data2.setUserid("Userid");
        c2sRechargeRequestVO.setData(data2);
        c2sRechargeRequestVO.setLoginId("42");
        c2sRechargeRequestVO.setMsisdn("Msisdn");
        c2sRechargeRequestVO.setPassword("iloveyou");
        c2sRechargeRequestVO.setPin("Pin");
        c2sRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sRechargeRequestVO.setReqGatewayLoginId("42");
        c2sRechargeRequestVO.setReqGatewayPassword("iloveyou");
        c2sRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        c2sRechargeRequestVO.setServicePort("Service Port");
        c2sRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sRechargeRequestVO));
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SServicesController#processRechargeRequest(MultiValueMap, C2SRechargeRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRechargeRequest() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/c2sServices/rctrf", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SRechargeRequestVO c2sRechargeRequestVO = new C2SRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sRechargeRequestVO.setData(data);

        C2SRechargeDetails data2 = new C2SRechargeDetails();
        data2.setAmount("10");
        data2.setDate("2020-03-01");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setExtrefnum("Extrefnum");
        data2.setGifterLang("Gifter Lang");
        data2.setGifterMsisdn("Gifter Msisdn");
        data2.setGifterName("Gifter Name");
        data2.setLanguage1("en");
        data2.setLanguage2("en");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setMsisdn2("Msisdn2");
        data2.setNotifMsisdn("Notif Msisdn");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setQty("Qty");
        data2.setSelector("Selector");
        data2.setUserid("Userid");
        c2sRechargeRequestVO.setData(data2);
        c2sRechargeRequestVO.setLoginId("42");
        c2sRechargeRequestVO.setMsisdn("Msisdn");
        c2sRechargeRequestVO.setPassword("iloveyou");
        c2sRechargeRequestVO.setPin("Pin");
        c2sRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sRechargeRequestVO.setReqGatewayLoginId("42");
        c2sRechargeRequestVO.setReqGatewayPassword("iloveyou");
        c2sRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        c2sRechargeRequestVO.setServicePort("Service Port");
        c2sRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sRechargeRequestVO));
        Object[] controllers = new Object[]{c2SServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

