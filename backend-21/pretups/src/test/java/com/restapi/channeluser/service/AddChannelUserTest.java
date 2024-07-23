package com.restapi.channeluser.service;

import com.btsl.pretups.channeluser.businesslogic.AddChannelUserRequestVO;
import com.btsl.pretups.channeluser.businesslogic.ChannelUserDetails;
import com.btsl.pretups.channeluser.businesslogic.Msisdn;
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

@ContextConfiguration(classes = {AddChannelUser.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AddChannelUserTest {
    @Autowired
    private AddChannelUser addChannelUser;

    /**
     * Method under test: {@link AddChannelUser#addChannelUser(HttpServletResponse, MultiValueMap, AddChannelUserRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddChannelUser() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R034 Diffblue Cover can't complete test.
        //   Diffblue Cover was unable to complete the test.
        //   Try to increase the number of fuzzing iterations if non-default
        //   value is used.
        //   See https://diff.blue/R034 for further troubleshooting of this issue.

        // Arrange
        // TODO: Populate arranged inputs
        Object[] uriVars = new Object[]{};
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/v1/channelUsers/", uriVars)
                .contentType(MediaType.APPLICATION_JSON);

        AddChannelUserRequestVO addChannelUserRequestVO = new AddChannelUserRequestVO();

        ChannelUserDetails data = new ChannelUserDetails();
        data.setAddress1("42 Main St");
        data.setAddress2("42 Main St");
        data.setAllowedTimeFrom("jane.doe@example.org");
        data.setAllowedTimeTo("alice.liddell@example.org");
        data.setAlloweddays("Alloweddays");
        data.setAllowedip("127.0.0.1");
        data.setAppointmentdate("2020-03-01");
        data.setCity("Oxford");
        data.setCommissionProfileID("Commission Profile ID");
        data.setCompany("Company");
        data.setConfirmwebpassword("iloveyou");
        data.setContactNumber("42");
        data.setContactPerson("Contact Person");
        data.setControlGroup("Control Group");
        data.setCountry("GB");
        data.setDepartmentCode("Department Code");
        data.setDesignation("Designation");
        data.setDivisionCode("Division Code");
        data.setDocumentNo("Document No");
        data.setDocumentType("Document Type");
        data.setDomain("Domain");
        data.setEmailid("jane.doe@example.org");
        data.setEmpcode("Empcode");
        data.setExternalCode("External Code");
        data.setExtnwcode("Extnwcode");
        data.setFax("Fax");
        data.setFirstName("Jane");
        data.setGeographicalDomain("Geographical Domain");
        data.setGeographyCode("Geography Code");
        data.setGrouprole("Roles");
        data.setInsuspend("Insuspend");
        data.setLanguage("en");
        data.setLastName("Doe");
        data.setLatitude("Latitude");
        data.setLmsProfileId("42");
        data.setLongitude("Longitude");
        data.setLowbalalertother("Lowbalalertother");
        data.setLowbalalertparent("Lowbalalertparent");
        data.setLowbalalertself("Lowbalalertself");
        data.setMobileNumber("42");

        Msisdn msisdn = new Msisdn();
        msisdn.setConfirmPin("Confirm Pin");
        msisdn.setDescription("The characteristics of someone or something");
        msisdn.setIsprimary("Isprimary");
        msisdn.setPhoneNo("6625550144");
        msisdn.setPin("Pin");
        msisdn.setStkProfile("Stk Profile");
        Msisdn[] msisdn2 = new Msisdn[]{msisdn};
        data.setMsisdn(msisdn2);
        data.setMultipleGeographyLoc("Multiple Geography Loc");
        data.setOldLogin("Old Login");
        data.setOtherEmail("jane.doe@example.org");
        data.setOutletCode("Outlet Code");
        data.setOutsuspend("Outsuspend");
        data.setOwnerUser("Owner User");
        data.setParentCategory("Parent Category");
        data.setParentUser("Parent User");
        data.setPaymentType("Payment Type");
        data.setRoleType("Role Type");
        data.setServices("Services");
        data.setShortName("Short Name");
        data.setSsn("123-45-678");
        data.setState("MD");
        data.setSubOutletCode("Sub Outlet Code");
        data.setSubscriberCode("Subscriber Code");
        data.setTransferProfile("Transfer Profile");
        data.setTransferRuleType("Transfer Rule Type");
        data.setUserCatCode("User Cat Code");
        data.setUserCode("User Code");
        data.setUserDomainCodes("User Domain Codes");
        data.setUserName("janedoe");
        data.setUserNamePrefix("janedoe");
        data.setUserProducts("User Products");
        data.setUsergrade("Usergrade");
        data.setVoucherSegments("Voucher Segments");
        data.setVoucherTypes("Voucher Types");
        data.setWebloginid("Webloginid");
        data.setWebpassword("iloveyou");
        addChannelUserRequestVO.setData(data);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(addChannelUserRequestVO));
        Object[] controllers = new Object[]{addChannelUser};
        MockMvc buildResult = MockMvcBuilders.standaloneSetup(controllers).build();

        // Act
        ResultActions actualPerformResult = buildResult.perform(requestBuilder);

        // Assert
        // TODO: Add assertions on result
    }
}

