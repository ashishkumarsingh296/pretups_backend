package com.restapi.c2sservices.controller;

import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.c2s.services.C2SBulkRcServiceI;
import com.restapi.c2s.services.C2SBulkRechargeDetails;
import com.restapi.c2s.services.C2SBulkRechargeRequestVO;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@ContextConfiguration(classes = {C2SBulkRechargeController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SBulkRechargeControllerTest {
    @MockBean
    private C2SBulkRcServiceI c2SBulkRcServiceI;

    @Autowired
    private C2SBulkRechargeController c2SBulkRechargeController;

    /**
     * Method under test: {@link C2SBulkRechargeController#processBulkGiftRechargeRequest(MultiValueMap, C2SBulkRechargeRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkGiftRechargeRequest() throws Exception {
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
                .post("/v1/c2sServices/c2sbulkgrc", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SBulkRechargeRequestVO c2sBulkRechargeRequestVO = new C2SBulkRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sBulkRechargeRequestVO.setData(data);

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("No Of Days");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("2020-03-01");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");
        c2sBulkRechargeRequestVO.setData(data2);
        c2sBulkRechargeRequestVO.setLoginId("42");
        c2sBulkRechargeRequestVO.setMsisdn("Msisdn");
        c2sBulkRechargeRequestVO.setPassword("iloveyou");
        c2sBulkRechargeRequestVO.setPin("Pin");
        c2sBulkRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sBulkRechargeRequestVO.setReqGatewayLoginId("42");
        c2sBulkRechargeRequestVO.setReqGatewayPassword("iloveyou");
        c2sBulkRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        c2sBulkRechargeRequestVO.setServicePort("Service Port");
        c2sBulkRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sBulkRechargeRequestVO));
        Object[] controllers = new Object[]{c2SBulkRechargeController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRechargeController#processBulkInternetRechargeRequest(MultiValueMap, C2SBulkRechargeRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkInternetRechargeRequest() throws Exception {
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
                .post("/v1/c2sServices/c2sbulkintrrc", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SBulkRechargeRequestVO c2sBulkRechargeRequestVO = new C2SBulkRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sBulkRechargeRequestVO.setData(data);

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("No Of Days");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("2020-03-01");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");
        c2sBulkRechargeRequestVO.setData(data2);
        c2sBulkRechargeRequestVO.setLoginId("42");
        c2sBulkRechargeRequestVO.setMsisdn("Msisdn");
        c2sBulkRechargeRequestVO.setPassword("iloveyou");
        c2sBulkRechargeRequestVO.setPin("Pin");
        c2sBulkRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sBulkRechargeRequestVO.setReqGatewayLoginId("42");
        c2sBulkRechargeRequestVO.setReqGatewayPassword("iloveyou");
        c2sBulkRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        c2sBulkRechargeRequestVO.setServicePort("Service Port");
        c2sBulkRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sBulkRechargeRequestVO));
        Object[] controllers = new Object[]{c2SBulkRechargeController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRechargeController#processBulkPrepaidRechargeRequest(MultiValueMap, C2SBulkRechargeRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessBulkPrepaidRechargeRequest() throws Exception {
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
                .post("/v1/c2sServices/c2sbulkprc", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        C2SBulkRechargeRequestVO c2sBulkRechargeRequestVO = new C2SBulkRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        c2sBulkRechargeRequestVO.setData(data);

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("No Of Days");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("2020-03-01");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");
        c2sBulkRechargeRequestVO.setData(data2);
        c2sBulkRechargeRequestVO.setLoginId("42");
        c2sBulkRechargeRequestVO.setMsisdn("Msisdn");
        c2sBulkRechargeRequestVO.setPassword("iloveyou");
        c2sBulkRechargeRequestVO.setPin("Pin");
        c2sBulkRechargeRequestVO.setReqGatewayCode("Req Gateway Code");
        c2sBulkRechargeRequestVO.setReqGatewayLoginId("42");
        c2sBulkRechargeRequestVO.setReqGatewayPassword("iloveyou");
        c2sBulkRechargeRequestVO.setReqGatewayType("Req Gateway Type");
        c2sBulkRechargeRequestVO.setServicePort("Service Port");
        c2sBulkRechargeRequestVO.setSourceType("Source Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(c2sBulkRechargeRequestVO));
        Object[] controllers = new Object[]{c2SBulkRechargeController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link C2SBulkRechargeController#processViewBulkPrepaidRecharge(String, String, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessViewBulkPrepaidRecharge() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{"xxx", "Msisdn"};
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/v1/c2sServices/viewc2sbulk/{batchId:.+}/{msisdn}", uriVars);
        Object[] controllers = new Object[]{c2SBulkRechargeController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

