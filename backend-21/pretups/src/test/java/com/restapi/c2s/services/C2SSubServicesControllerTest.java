package com.restapi.c2s.services;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {C2SSubServicesController.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SSubServicesControllerTest {
    @Autowired
    private C2SSubServicesController c2SSubServicesController;

    /**
     * Method under test: {@link C2SSubServicesController#subServicesList(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSubServicesList() throws Exception {
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
                .get("/v1/c2sServices/subServices/{serviceName}", uriVars);
        Object[] controllers = new Object[]{c2SSubServicesController};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

