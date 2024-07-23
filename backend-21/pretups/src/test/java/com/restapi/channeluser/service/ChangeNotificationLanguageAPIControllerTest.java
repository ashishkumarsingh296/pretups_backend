package com.restapi.channeluser.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
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

@ContextConfiguration(classes = {ChangeNotificationLanguageAPIController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChangeNotificationLanguageAPIControllerTest {
    @Autowired
    private ChangeNotificationLanguageAPIController changeNotificationLanguageAPIController;

    @MockBean
    private ChangeNotificationLanguageAPIService changeNotificationLanguageAPIService;

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIController#loadUserPhoneDetails(MultiValueMap, String, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadUserPhoneDetails() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        String[] values = new String[]{"foo"};
        String[] values2 = new String[]{"foo"};
        String[] values3 = new String[]{"foo"};
        String[] values4 = new String[]{"foo"};
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/v1/changeNotificationLang/loadUserPhoneList", uriVars)
                .param("categoryCode", values)
                .param("msisdn", values2)
                .param("searchBy", values3)
                .param("userName", values4);
        Object[] controllers = new Object[]{changeNotificationLanguageAPIController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIController#loadUsers(MultiValueMap, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadUsers() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        String[] values = new String[]{"foo"};
        String[] values2 = new String[]{"foo"};
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/v1/changeNotificationLang/loadAllUsers", uriVars)
                .param("categoryCode", values)
                .param("userName", values2);
        Object[] controllers = new Object[]{changeNotificationLanguageAPIController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIController#updateNotificationLanguage(MultiValueMap, NotificationLanguageRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testUpdateNotificationLanguage() throws Exception {
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
                .post("/v1/changeNotificationLang/updateNotificationLang", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        NotificationLanguageRequestVO notificationLanguageRequestVO = new NotificationLanguageRequestVO();
        ArrayList<ChangePhoneLanguage> changedPhoneLanguageList = new ArrayList<>();
        notificationLanguageRequestVO.setChangedPhoneLanguageList(changedPhoneLanguageList);
        notificationLanguageRequestVO.setUserLoginID("User Login ID");

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(notificationLanguageRequestVO));
        Object[] controllers = new Object[]{changeNotificationLanguageAPIController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

