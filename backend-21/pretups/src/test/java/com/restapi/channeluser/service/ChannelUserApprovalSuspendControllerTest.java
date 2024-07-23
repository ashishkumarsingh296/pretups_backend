package com.restapi.channeluser.service;

import com.fasterxml.jackson.databind.ObjectMapper;

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

@ContextConfiguration(classes = {ChannelUserApprovalSuspendController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChannelUserApprovalSuspendControllerTest {
    @Autowired
    private ChannelUserApprovalSuspendController channelUserApprovalSuspendController;

    @MockBean
    private ChannelUserServicesI channelUserServicesI;

    /**
     * Method under test: {@link ChannelUserApprovalSuspendController#getSuspendReqOrDelReqChannelUsers(HttpServletResponse, MultiValueMap, ChannelUserSearchReqVo)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetSuspendReqOrDelReqChannelUsers() throws Exception {
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
                .post("/v1/channelUsers/UserListByStatus", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        ChannelUserSearchReqVo channelUserSearchReqVo = new ChannelUserSearchReqVo();
        channelUserSearchReqVo.setCategory("Category");
        channelUserSearchReqVo.setDomain("Domain");
        channelUserSearchReqVo.setGeography("Geography");
        channelUserSearchReqVo.setLoggedInUserUserid("Logged In User Userid");
        channelUserSearchReqVo.setLoggedUserNeworkCode("Logged User Nework Code");
        channelUserSearchReqVo.setLoginID("Login ID");
        channelUserSearchReqVo.setMobileNumber("42");
        channelUserSearchReqVo.setSearchType("Search Type");
        channelUserSearchReqVo.setUserStatus("User Status");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(channelUserSearchReqVo));
        Object[] controllers = new Object[]{channelUserApprovalSuspendController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChannelUserApprovalSuspendController#saveDeleteOrSuspend(HttpServletResponse, MultiValueMap, ActionOnUserReqVo)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSaveDeleteOrSuspend() throws Exception {
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
                .post("/v1/channelUsers/actionOnUser", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        ActionOnUserReqVo actionOnUserReqVo = new ActionOnUserReqVo();
        actionOnUserReqVo.setAction("Action");
        actionOnUserReqVo.setLoginId("42");
        actionOnUserReqVo.setRemarks("Remarks");
        actionOnUserReqVo.setRequestType("Request Type");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(actionOnUserReqVo));
        Object[] controllers = new Object[]{channelUserApprovalSuspendController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

