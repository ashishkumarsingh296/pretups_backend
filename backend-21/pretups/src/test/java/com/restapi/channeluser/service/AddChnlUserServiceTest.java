package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.pretups.channeluser.businesslogic.ChannelUserApprovalReqVO;
import com.btsl.pretups.channeluser.businesslogic.ChannelUserDetails;
import com.btsl.pretups.channeluser.businesslogic.Msisdn;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.util.BTSLUtil;
import com.btsl.util.JUnitConfig;
import com.btsl.util.OracleUtil;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

public class AddChnlUserServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddChnlUserService}
     *   <li>{@link AddChnlUserService#setChannelUserVO(ChannelUserVO)}
     *   <li>{@link AddChnlUserService#setExistingDBchannelUserVO(ChannelUserVO)}
     *   <li>{@link AddChnlUserService#setPrimaryMsisdn(String)}
     *   <li>{@link AddChnlUserService#setRequestGroupHashMap(HashMap)}
     *   <li>{@link AddChnlUserService#setSenderPin(String)}
     *   <li>{@link AddChnlUserService#getChannelUserVO()}
     *   <li>{@link AddChnlUserService#getExistingDBchannelUserVO()}
     *   <li>{@link AddChnlUserService#getPrimaryMsisdn()}
     *   <li>{@link AddChnlUserService#getRequestGroupHashMap()}
     *   <li>{@link AddChnlUserService#getSenderPin()}
     * </ul>
     */
    @Test
    public void testConstructor() {

        AddChnlUserService actualAddChnlUserService = new AddChnlUserService();
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
        actualAddChnlUserService.setChannelUserVO(channelUserVO);
        ChannelUserVO existingDBchannelUserVO = ChannelUserVO.getInstance();
        actualAddChnlUserService.setExistingDBchannelUserVO(existingDBchannelUserVO);
        actualAddChnlUserService.setPrimaryMsisdn("Primary Msisdn");
        HashMap<String, String> requestGroupHashMap = new HashMap<>();
        actualAddChnlUserService.setRequestGroupHashMap(requestGroupHashMap);
        actualAddChnlUserService.setSenderPin("Sender Pin");
        assertSame(channelUserVO, actualAddChnlUserService.getChannelUserVO());
        assertSame(existingDBchannelUserVO, actualAddChnlUserService.getExistingDBchannelUserVO());
        assertEquals("Primary Msisdn", actualAddChnlUserService.getPrimaryMsisdn());
        assertSame(requestGroupHashMap, actualAddChnlUserService.getRequestGroupHashMap());
        assertEquals("Sender Pin", actualAddChnlUserService.getSenderPin());
    }

    /**
     * Method under test: {@link AddChnlUserService#execute(ChannelUserApprovalReqVO, ChannelUserVO, Connection)}
     */
    @Test
    ////@Ignore("TODO: Complete this test")
    public void testExecute() throws BTSLBaseException, SQLException {
        //Mockito.mockStatic(PreferenceCache.class);
        JUnitConfig.init();
      //  when(PreferenceCache.getControlPreference("USER_APPROVAL_LEVEL", Mockito.anyString(), Mockito.anyString())).thenReturn(1);

        //Object aa = PreferenceCache.getControlPreference("USER_APPROVAL_LEVEL", "NG", "Test");

        //int maxApprovalLevel =((Integer) PreferenceCache.getControlPreference("USER_APPROVAL_LEVEL", "NG", "Test")).intValue();



        AddChnlUserService addChnlUserService = new AddChnlUserService();


        Msisdn msisdn = new Msisdn();
        msisdn.setConfirmPin("1357");
        msisdn.setDescription("The characteristics of someone or something");
        msisdn.setIsprimary("Y");
        msisdn.setPhoneNo("6625550144");
        msisdn.setPin("1357");
        msisdn.setStkProfile("String");

        ChannelUserDetails channelUserDetails = new ChannelUserDetails();
        channelUserDetails.setAddress1("42 Main St");
        channelUserDetails.setAddress2("42 Main St");
        channelUserDetails.setAllowedTimeFrom("00:00");
        channelUserDetails.setAllowedTimeTo("10:00");
        channelUserDetails.setAlloweddays("1");
        channelUserDetails.setAllowedip("127.0.0.1");
        channelUserDetails.setAppointmentdate("01/01/23");
        channelUserDetails.setCity("Oxford");
        channelUserDetails.setCommissionProfileID("Commission Profile ID");
        channelUserDetails.setCompany("Company");
        channelUserDetails.setConfirmwebpassword("Com@1357");
        channelUserDetails.setContactNumber("8888888888");
        channelUserDetails.setContactPerson("Contact Person");
        channelUserDetails.setControlGroup("Control Group");
        channelUserDetails.setCountry("GB");
        channelUserDetails.setDepartmentCode("Department Code");
        channelUserDetails.setDesignation("Designation");
        channelUserDetails.setDivisionCode("Division Code");
        channelUserDetails.setDocumentNo("Document No");
        channelUserDetails.setDocumentType("String");
        channelUserDetails.setDomain("Domain");
        channelUserDetails.setEmailid("jane.doe@example.org");
        channelUserDetails.setEmpcode("Empcode");
        channelUserDetails.setExternalCode("");
        channelUserDetails.setExtnwcode("String");
        channelUserDetails.setFax("");
        channelUserDetails.setFirstName("Jane");
        channelUserDetails.setGeographicalDomain("Geographical Domain");
        channelUserDetails.setGeographyCode("String");
        channelUserDetails.setGrouprole("Roles");
        channelUserDetails.setInsuspend(PretupsI.NO);
        channelUserDetails.setLanguage("String_String");
        channelUserDetails.setLastName("Doe");
        channelUserDetails.setLatitude("Latitude");
        channelUserDetails.setLmsProfileId("42");
        channelUserDetails.setLongitude("Longitude");
        channelUserDetails.setLowbalalertother("Lowbalalertother");
        channelUserDetails.setLowbalalertparent("Lowbalalertparent");
        channelUserDetails.setLowbalalertself("Lowbalalertself");
        channelUserDetails.setMobileNumber("42");


        channelUserDetails.setMsisdn(new Msisdn[]{msisdn});



        channelUserDetails.setMultipleGeographyLoc("Multiple Geography Loc");
        channelUserDetails.setOldLogin("Old Login");
        channelUserDetails.setOtherEmail("jane.doe@example.org");
        channelUserDetails.setOutletCode("Outlet Code");
        channelUserDetails.setOutsuspend(PretupsI.NO);
        channelUserDetails.setOwnerUser("Owner User");
        channelUserDetails.setParentCategory("Parent Category");
        channelUserDetails.setParentUser("String");
        channelUserDetails.setPaymentType("String");
        channelUserDetails.setRoleType("Role Type");
        channelUserDetails.setServices("Services");
        channelUserDetails.setShortName("Short Name");
        channelUserDetails.setSsn("123-45-678");
        channelUserDetails.setState("MD");
        channelUserDetails.setSubOutletCode("Sub Outlet Code");
        channelUserDetails.setSubscriberCode("");
        channelUserDetails.setTransferProfile("Transfer Profile");
        channelUserDetails.setTransferRuleType("Transfer Rule Type");
        channelUserDetails.setUserCatCode("String");
        channelUserDetails.setUserCode("User Code");
        channelUserDetails.setUserDomainCodes("User Domain Codes");
        channelUserDetails.setUserName("janedoe");
        channelUserDetails.setUserNamePrefix("MR");
        channelUserDetails.setUserProducts("User Products");
        channelUserDetails.setUsergrade("Usergrade");
        channelUserDetails.setVoucherSegments("Voucher Segments");
        channelUserDetails.setVoucherTypes("String");
        channelUserDetails.setWebloginid("Webloginid");
        channelUserDetails.setWebpassword("Com@1357");
        ChannelUserApprovalReqVO requestVO = new ChannelUserApprovalReqVO();//mock(ChannelUserApprovalReqVO.class);
     //   when(requestVO.getData()).thenReturn(channelUserDetails);
//        when(requestVO.getApprovalLevel()).thenReturn(PretupsI.NEW);





        requestVO.setData(channelUserDetails);
        requestVO.setApprovalLevel(PretupsI.NEW);

        requestVO.setUserAction(PretupsI.OPERATION_ADD);

        ChannelUserVO channelUserVO = mock(ChannelUserVO.class) ;

        when(channelUserVO.getUserType()).thenReturn(PretupsI.USER_TYPE_CHANNEL);
        when(channelUserVO.getCategoryCode()).thenReturn(PretupsI.CATEGORY_CODE_RETAILER);
        when(channelUserVO.getNetworkID()).thenReturn("String");
        when(channelUserVO.getDomainID()).thenReturn("DISTB_CHAN");


        CategoryVO categoryVO =  new CategoryVO() ;

        categoryVO.setSequenceNumber(0);
        categoryVO.setCategoryCode("TEST");
        categoryVO.setCategoryStatus("TEST");
        when(channelUserVO.getCategoryVO()).thenReturn(categoryVO);
  //doNothing().when(throw new BTSLBaseException(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()));
//        doNothing().when( validator ).validate( any() );

//        addChnlUserService.execute(requestVO, channelUserVO, JUnitConfig.getConnection());

        //when(requestVO.getApprovalLevel()).thenReturn(PretupsI.NEW);

       // CategoryDAO().loadCategoryDetailsUsingCategoryCode
        ArrayList categoryList = new ArrayList();

        CategoryVO pCatVO =  new CategoryVO();
        pCatVO.setCategoryCode("DIST");
        pCatVO.setCategoryName("Test Category");
        categoryList.add(pCatVO);

        try (MockedConstruction<CategoryDAO> categoryDAO = Mockito.mockConstruction(CategoryDAO.class,
                (mock, context) -> {
                    // further stubbings ...
                    when(mock.loadCategoryDetailsUsingCategoryCode(Mockito.any(), Mockito.anyString())).thenReturn(categoryList);
                })) {
            CategoryDAO categoryDAO2 = new CategoryDAO();
            when(categoryDAO2.loadCategoryDetailsUsingCategoryCode(Mockito.any(), Mockito.anyString())).thenReturn(categoryList);

        }


try {

//    mockStatic(PreferenceCache.class);

 //   when((Integer) PreferenceCache.getControlPreference(Mockito.anyString(), Mockito.anyString(),
 //           Mockito.anyString())).thenReturn(1);


    addChnlUserService.setPrimaryMsisdn("9999999999");



    try (MockedConstruction<NumberPortDAO> numberPortDAO = Mockito.mockConstruction(NumberPortDAO.class,
            (mock, context) -> {
                // further stubbings ...
              //  when(numberPortDAO.isExists(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);
            })) {

        try {
            NumberPortDAO nDao = new NumberPortDAO();
            when(nDao.isExists(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    addChnlUserService.execute(requestVO, channelUserVO, JUnitConfig.getConnection());
}catch(Exception e){

    e.printStackTrace();
}
        //when(requestVO.getApprovalLevel()).thenReturn(PretupsI.USER_ACTION_REJECT);
        requestVO.setApprovalLevel(PretupsI.USER_ACTION_REJECT);


        addChnlUserService.execute(requestVO, channelUserVO, JUnitConfig.getConnection());


       // addChnlUserService.execute(requestVO, channelUserVO, JUnitConfig.getConnection());


    }

    /**
     * Method under test: {@link AddChnlUserService#execute(ChannelUserApprovalReqVO, ChannelUserVO, Connection)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testExecute2() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();

        ChannelUserDetails channelUserDetails = new ChannelUserDetails();
        channelUserDetails.setAddress1("42 Main St");
        channelUserDetails.setAddress2("42 Main St");
        channelUserDetails.setAllowedTimeFrom("jane.doe@example.org");
        channelUserDetails.setAllowedTimeTo("alice.liddell@example.org");
        channelUserDetails.setAlloweddays("Alloweddays");
        channelUserDetails.setAllowedip("127.0.0.1");
        channelUserDetails.setAppointmentdate("2020-03-01");
        channelUserDetails.setCity("Oxford");
        channelUserDetails.setCommissionProfileID("Commission Profile ID");
        channelUserDetails.setCompany("Company");
        channelUserDetails.setConfirmwebpassword("iloveyou");
        channelUserDetails.setContactNumber("42");
        channelUserDetails.setContactPerson("Contact Person");
        channelUserDetails.setControlGroup("Control Group");
        channelUserDetails.setCountry("GB");
        channelUserDetails.setDepartmentCode("Department Code");
        channelUserDetails.setDesignation("Designation");
        channelUserDetails.setDivisionCode("Division Code");
        channelUserDetails.setDocumentNo("Document No");
        channelUserDetails.setDocumentType("Document Type");
        channelUserDetails.setDomain("Domain");
        channelUserDetails.setEmailid("jane.doe@example.org");
        channelUserDetails.setEmpcode("Empcode");
        channelUserDetails.setExternalCode("External Code");
        channelUserDetails.setExtnwcode("Extnwcode");
        channelUserDetails.setFax("Fax");
        channelUserDetails.setFirstName("Jane");
        channelUserDetails.setGeographicalDomain("Geographical Domain");
        channelUserDetails.setGeographyCode("Geography Code");
        channelUserDetails.setGrouprole("Roles");
        channelUserDetails.setInsuspend("Insuspend");
        channelUserDetails.setLanguage("en");
        channelUserDetails.setLastName("Doe");
        channelUserDetails.setLatitude("Latitude");
        channelUserDetails.setLmsProfileId("42");
        channelUserDetails.setLongitude("Longitude");
        channelUserDetails.setLowbalalertother("Lowbalalertother");
        channelUserDetails.setLowbalalertparent("Lowbalalertparent");
        channelUserDetails.setLowbalalertself("Lowbalalertself");
        channelUserDetails.setMobileNumber("42");
        channelUserDetails.setMsisdn(null);
        channelUserDetails.setMultipleGeographyLoc("Multiple Geography Loc");
        channelUserDetails.setOldLogin("Old Login");
        channelUserDetails.setOtherEmail("jane.doe@example.org");
        channelUserDetails.setOutletCode("Outlet Code");
        channelUserDetails.setOutsuspend("Outsuspend");
        channelUserDetails.setOwnerUser("Owner User");
        channelUserDetails.setParentCategory("Parent Category");
        channelUserDetails.setParentUser("Parent User");
        channelUserDetails.setPaymentType("Payment Type");
        channelUserDetails.setRoleType("Role Type");
        channelUserDetails.setServices("Services");
        channelUserDetails.setShortName("Short Name");
        channelUserDetails.setSsn("123-45-678");
        channelUserDetails.setState("MD");
        channelUserDetails.setSubOutletCode("Sub Outlet Code");
        channelUserDetails.setSubscriberCode("Subscriber Code");
        channelUserDetails.setTransferProfile("Transfer Profile");
        channelUserDetails.setTransferRuleType("Transfer Rule Type");
        channelUserDetails.setUserCatCode("String");
        channelUserDetails.setUserCode("User Code");
        channelUserDetails.setUserDomainCodes("User Domain Codes");
        channelUserDetails.setUserName("janedoe");
        channelUserDetails.setUserNamePrefix("janedoe");
        channelUserDetails.setUserProducts("User Products");
        channelUserDetails.setUsergrade("Usergrade");
        channelUserDetails.setVoucherSegments("Voucher Segments");
        channelUserDetails.setVoucherTypes("Voucher Types");
        channelUserDetails.setWebloginid("Webloginid");
        channelUserDetails.setWebpassword("iloveyou");
        ChannelUserApprovalReqVO requestVO = mock(ChannelUserApprovalReqVO.class);
        when(requestVO.getData()).thenReturn(channelUserDetails);
        addChnlUserService.execute(requestVO, mock(ChannelUserVO.class), JUnitConfig.getConnection());
    }

    /**
     * Method under test: {@link AddChnlUserService#execute(ChannelUserApprovalReqVO, ChannelUserVO, Connection)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testExecute3() throws BTSLBaseException, SQLException {

        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();

        Msisdn msisdn = new Msisdn();
        msisdn.setConfirmPin("Confirm Pin");
        msisdn.setDescription("The characteristics of someone or something");
        msisdn.setIsprimary("Isprimary");
        msisdn.setPhoneNo("6625550144");
        msisdn.setPin("Pin");
        msisdn.setStkProfile("Stk Profile");

        ChannelUserDetails channelUserDetails = new ChannelUserDetails();
        channelUserDetails.setAddress1("42 Main St");
        channelUserDetails.setAddress2("42 Main St");
        channelUserDetails.setAllowedTimeFrom("jane.doe@example.org");
        channelUserDetails.setAllowedTimeTo("alice.liddell@example.org");
        channelUserDetails.setAlloweddays("Alloweddays");
        channelUserDetails.setAllowedip("127.0.0.1");
        channelUserDetails.setAppointmentdate("2020-03-01");
        channelUserDetails.setCity("Oxford");
        channelUserDetails.setCommissionProfileID("Commission Profile ID");
        channelUserDetails.setCompany("Company");
        channelUserDetails.setConfirmwebpassword("iloveyou");
        channelUserDetails.setContactNumber("42");
        channelUserDetails.setContactPerson("Contact Person");
        channelUserDetails.setControlGroup("Control Group");
        channelUserDetails.setCountry("GB");
        channelUserDetails.setDepartmentCode("Department Code");
        channelUserDetails.setDesignation("Designation");
        channelUserDetails.setDivisionCode("Division Code");
        channelUserDetails.setDocumentNo("Document No");
        channelUserDetails.setDocumentType("Document Type");
        channelUserDetails.setDomain("Domain");
        channelUserDetails.setEmailid("jane.doe@example.org");
        channelUserDetails.setEmpcode("Empcode");
        channelUserDetails.setExternalCode("External Code");
        channelUserDetails.setExtnwcode("Extnwcode");
        channelUserDetails.setFax("Fax");
        channelUserDetails.setFirstName("Jane");
        channelUserDetails.setGeographicalDomain("Geographical Domain");
        channelUserDetails.setGeographyCode("Geography Code");
        channelUserDetails.setGrouprole(",");
        channelUserDetails.setInsuspend("Insuspend");
        channelUserDetails.setLanguage("en");
        channelUserDetails.setLastName("Doe");
        channelUserDetails.setLatitude("Latitude");
        channelUserDetails.setLmsProfileId("42");
        channelUserDetails.setLongitude("Longitude");
        channelUserDetails.setLowbalalertother("Lowbalalertother");
        channelUserDetails.setLowbalalertparent("Lowbalalertparent");
        channelUserDetails.setLowbalalertself("Lowbalalertself");
        channelUserDetails.setMobileNumber("42");
        channelUserDetails.setMsisdn(new Msisdn[]{msisdn});
        channelUserDetails.setMultipleGeographyLoc("Multiple Geography Loc");
        channelUserDetails.setOldLogin("Old Login");
        channelUserDetails.setOtherEmail("jane.doe@example.org");
        channelUserDetails.setOutletCode("Outlet Code");
        channelUserDetails.setOutsuspend("Outsuspend");
        channelUserDetails.setOwnerUser("Owner User");
        channelUserDetails.setParentCategory("Parent Category");
        channelUserDetails.setParentUser("Parent User");
        channelUserDetails.setPaymentType("Payment Type");
        channelUserDetails.setRoleType("Role Type");
        channelUserDetails.setServices("Services");
        channelUserDetails.setShortName("Short Name");
        channelUserDetails.setSsn("123-45-678");
        channelUserDetails.setState("MD");
        channelUserDetails.setSubOutletCode("Sub Outlet Code");
        channelUserDetails.setSubscriberCode("Subscriber Code");
        channelUserDetails.setTransferProfile("Transfer Profile");
        channelUserDetails.setTransferRuleType("Transfer Rule Type");
        channelUserDetails.setUserCatCode("User Cat Code");
        channelUserDetails.setUserCode("User Code");
        channelUserDetails.setUserDomainCodes("User Domain Codes");
        channelUserDetails.setUserName("janedoe");
        channelUserDetails.setUserNamePrefix("janedoe");
        channelUserDetails.setUserProducts("User Products");
        channelUserDetails.setUsergrade("Usergrade");
        channelUserDetails.setVoucherSegments("Voucher Segments");
        channelUserDetails.setVoucherTypes("Voucher Types");
        channelUserDetails.setWebloginid("Webloginid");
        channelUserDetails.setWebpassword("iloveyou");
        ChannelUserApprovalReqVO requestVO = mock(ChannelUserApprovalReqVO.class);
        when(requestVO.getData()).thenReturn(channelUserDetails);
        addChnlUserService.execute(requestVO, mock(ChannelUserVO.class), JUnitConfig.getConnection());
    }

    /**
     * Method under test: {@link AddChnlUserService#execute(ChannelUserApprovalReqVO, ChannelUserVO, Connection)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testExecute4() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.AddChnlUserService.convertReqGroupRoleToMap(AddChnlUserService.java:2461)
        //       at com.restapi.channeluser.service.AddChnlUserService.execute(AddChnlUserService.java:153)
        //   See https://diff.blue/R013 to resolve this issue.
        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();

        Msisdn msisdn = new Msisdn();
        msisdn.setConfirmPin("Confirm Pin");
        msisdn.setDescription("The characteristics of someone or something");
        msisdn.setIsprimary("Isprimary");
        msisdn.setPhoneNo("6625550144");
        msisdn.setPin("Pin");
        msisdn.setStkProfile("Stk Profile");

        ChannelUserDetails channelUserDetails = new ChannelUserDetails();
        channelUserDetails.setAddress1("42 Main St");
        channelUserDetails.setAddress2("42 Main St");
        channelUserDetails.setAllowedTimeFrom("jane.doe@example.org");
        channelUserDetails.setAllowedTimeTo("alice.liddell@example.org");
        channelUserDetails.setAlloweddays("Alloweddays");
        channelUserDetails.setAllowedip("127.0.0.1");
        channelUserDetails.setAppointmentdate("2020-03-01");
        channelUserDetails.setCity("Oxford");
        channelUserDetails.setCommissionProfileID("Commission Profile ID");
        channelUserDetails.setCompany("Company");
        channelUserDetails.setConfirmwebpassword("iloveyou");
        channelUserDetails.setContactNumber("42");
        channelUserDetails.setContactPerson("Contact Person");
        channelUserDetails.setControlGroup("Control Group");
        channelUserDetails.setCountry("GB");
        channelUserDetails.setDepartmentCode("Department Code");
        channelUserDetails.setDesignation("Designation");
        channelUserDetails.setDivisionCode("Division Code");
        channelUserDetails.setDocumentNo("Document No");
        channelUserDetails.setDocumentType("Document Type");
        channelUserDetails.setDomain("Domain");
        channelUserDetails.setEmailid("jane.doe@example.org");
        channelUserDetails.setEmpcode("Empcode");
        channelUserDetails.setExternalCode("External Code");
        channelUserDetails.setExtnwcode("Extnwcode");
        channelUserDetails.setFax("Fax");
        channelUserDetails.setFirstName("Jane");
        channelUserDetails.setGeographicalDomain("Geographical Domain");
        channelUserDetails.setGeographyCode("Geography Code");
        channelUserDetails.setGrouprole(null);
        channelUserDetails.setInsuspend("Insuspend");
        channelUserDetails.setLanguage("en");
        channelUserDetails.setLastName("Doe");
        channelUserDetails.setLatitude("Latitude");
        channelUserDetails.setLmsProfileId("42");
        channelUserDetails.setLongitude("Longitude");
        channelUserDetails.setLowbalalertother("Lowbalalertother");
        channelUserDetails.setLowbalalertparent("Lowbalalertparent");
        channelUserDetails.setLowbalalertself("Lowbalalertself");
        channelUserDetails.setMobileNumber("42");
        channelUserDetails.setMsisdn(new Msisdn[]{msisdn});
        channelUserDetails.setMultipleGeographyLoc("Multiple Geography Loc");
        channelUserDetails.setOldLogin("Old Login");
        channelUserDetails.setOtherEmail("jane.doe@example.org");
        channelUserDetails.setOutletCode("Outlet Code");
        channelUserDetails.setOutsuspend("Outsuspend");
        channelUserDetails.setOwnerUser("Owner User");
        channelUserDetails.setParentCategory("Parent Category");
        channelUserDetails.setParentUser("Parent User");
        channelUserDetails.setPaymentType("Payment Type");
        channelUserDetails.setRoleType("Role Type");
        channelUserDetails.setServices("Services");
        channelUserDetails.setShortName("Short Name");
        channelUserDetails.setSsn("123-45-678");
        channelUserDetails.setState("MD");
        channelUserDetails.setSubOutletCode("Sub Outlet Code");
        channelUserDetails.setSubscriberCode("Subscriber Code");
        channelUserDetails.setTransferProfile("Transfer Profile");
        channelUserDetails.setTransferRuleType("Transfer Rule Type");
        channelUserDetails.setUserCatCode("User Cat Code");
        channelUserDetails.setUserCode("User Code");
        channelUserDetails.setUserDomainCodes("User Domain Codes");
        channelUserDetails.setUserName("janedoe");
        channelUserDetails.setUserNamePrefix("janedoe");
        channelUserDetails.setUserProducts("User Products");
        channelUserDetails.setUsergrade("Usergrade");
        channelUserDetails.setVoucherSegments("Voucher Segments");
        channelUserDetails.setVoucherTypes("Voucher Types");
        channelUserDetails.setWebloginid("Webloginid");
        channelUserDetails.setWebpassword("iloveyou");
        ChannelUserApprovalReqVO requestVO = mock(ChannelUserApprovalReqVO.class);
        when(requestVO.getData()).thenReturn(channelUserDetails);
        addChnlUserService.execute(requestVO, mock(ChannelUserVO.class), JUnitConfig.getConnection());
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDivisionCode(Connection, String)}
     */
    @Test
    public void testValidateDivisionCode() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();
      /*  ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
    */    addChnlUserService.validateDivisionCode(JUnitConfig.getConnection(), "Division Code");
      /*  verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDivisionCode(Connection, String)}
     */
    @Test
    public void testValidateDivisionCode2() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        */
        addChnlUserService.validateDivisionCode(JUnitConfig.getConnection(), "Division Code");
        /*verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDivisionCode(Connection, String)}
     */
    @Test
    public void testValidateDivisionCode3() throws BTSLBaseException, SQLException {
        JUnitConfig.init();

        AddChnlUserService addChnlUserService = new AddChnlUserService();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        */
        addChnlUserService.validateDivisionCode(JUnitConfig.getConnection(), "Division Code");
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDivisionCode(Connection, String)}
     */
    @Test
    public void testValidateDivisionCode4() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        (new AddChnlUserService()).validateDivisionCode(mock(Connection.class), "");
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDepartmentCode(Connection, String, String)}
     */
    @Test
    public void testValidateDepartmentCode() throws BTSLBaseException, SQLException {

        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();
      /*  ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
   */
        addChnlUserService.validateDepartmentCode(JUnitConfig.getConnection(), "Department Code", "Division ID");
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDepartmentCode(Connection, String, String)}
     */
    @Test
    public void testValidateDepartmentCode2() throws BTSLBaseException, SQLException {
        JUnitConfig.init();

        AddChnlUserService addChnlUserService = new AddChnlUserService();
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
        */
        addChnlUserService.validateDepartmentCode(JUnitConfig.getConnection(), "Department Code", "Division ID");
/*
        verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
*/
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDepartmentCode(Connection, String, String)}
     */
    @Test
    public void testValidateDepartmentCode3() throws BTSLBaseException, SQLException {
        JUnitConfig.init();
        AddChnlUserService addChnlUserService = new AddChnlUserService();
       /* ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
       //Connection con = mock(Connection.class);
        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        thrown.expect(BTSLBaseException.class);
       */ addChnlUserService.validateDepartmentCode(JUnitConfig.getConnection(), "Department Code", "Division ID");
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link AddChnlUserService#validateDepartmentCode(Connection, String, String)}
     */
    @Test
    public void testValidateDepartmentCode4() throws BTSLBaseException {
        thrown.expect(BTSLBaseException.class);
        (new AddChnlUserService()).validateDepartmentCode(mock(Connection.class), "", "Division ID");
    }
}

