package com.restapi.channeluser.service;

import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@ContextConfiguration(classes = {ChannelUserApprvListController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelUserApprvListControllerTest {
    @Autowired
    private ChannelUserApprvListController channelUserApprvListController;

    /**
     * Method under test: {@link ChannelUserApprvListController#getApprovalList(HttpServletResponse, MultiValueMap, ApplistReqVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetApprovalList() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                .post("/v1/channelUsers/channelUserApprovalList", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        ApplistReqVO applistReqVO = new ApplistReqVO();
        applistReqVO.setApprovalLevel("Approval Level");
        applistReqVO.setCategory("Category");
        applistReqVO.setDomain("Domain");
        applistReqVO.setGeography("Geography");
        applistReqVO.setLoggedInUserUserid("Logged In User Userid");
        applistReqVO.setLoginID("Login ID");
        applistReqVO.setMobileNumber("42");
        applistReqVO.setReqTab("Req Tab");
        applistReqVO.setStatus("Status");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(applistReqVO));
        Object[] controllers = new Object[]{channelUserApprvListController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

