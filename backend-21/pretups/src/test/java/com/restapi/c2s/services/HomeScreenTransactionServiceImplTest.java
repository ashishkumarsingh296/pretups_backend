package com.restapi.c2s.services;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewResponse;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoResponse;
import com.btsl.pretups.channel.transfer.businesslogic.TotalIncomeDetailsViewVO;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalDataResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalanceRequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.restapi.user.service.PendingTxnListResponseVO;
import com.restapi.user.service.TotalUserIncomeDetailViewResponse;
import com.restapi.user.service.UserBalanceResponseVO;
import com.restapi.user.service.UserHierachyRequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import java.util.ArrayList;

import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {HomeScreenTransactionServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class HomeScreenTransactionServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private HomeScreenTransactionServiceImpl homeScreenTransactionServiceImpl;

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getUserIncomeDetails(String, TotalIncomeDetailsViewVO, Locale, TotalUserIncomeDetailViewResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserIncomeDetails() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";
        TotalIncomeDetailsViewVO totalIncomeDetailsViewVO = new TotalIncomeDetailsViewVO();

        TotalIncomeDetailsViewVO.TotalIncomeDetailsViewData data =  new TotalIncomeDetailsViewVO().getData();

        data.setToDate("01/01/24");

        totalIncomeDetailsViewVO.setData(data);

        Locale locale = null;
        TotalUserIncomeDetailViewResponse response = null;
        HttpServletResponse responseSwag = null;

        // Act
        this.homeScreenTransactionServiceImpl.getUserIncomeDetails(msisdn, totalIncomeDetailsViewVO, locale, response,
                responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getPercentage(Object, Object)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetPercentage() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "Current Data"
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at java.lang.Double.valueOf(Double.java:502)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getPercentage(HomeScreenTransactionServiceImpl.java:289)
        //   See https://diff.blue/R013 to resolve this issue.

        homeScreenTransactionServiceImpl.getPercentage("Current Data", "Previous Data");
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getPercentage(Object, Object)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetPercentage2() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "Previous Data"
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at java.lang.Double.valueOf(Double.java:502)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getPercentage(HomeScreenTransactionServiceImpl.java:290)
        //   See https://diff.blue/R013 to resolve this issue.

        homeScreenTransactionServiceImpl.getPercentage(42, "Previous Data");
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getC2SAllTransaction(String, C2SAllTransactionDetailViewRequestVO, Locale, C2SAllTransactionDetailViewResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetC2SAllTransaction() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getC2SAllTransaction(HomeScreenTransactionServiceImpl.java:320)

        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";
        C2SAllTransactionDetailViewRequestVO c2SAllTransactionDetailViewRequestVO = null;
        Locale locale = null;
        C2SAllTransactionDetailViewResponse response = null;

        // Act
        this.homeScreenTransactionServiceImpl.getC2SAllTransaction(msisdn, c2SAllTransactionDetailViewRequestVO, locale,
                response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#validateData(String, ArrayList, String, String)}
     */
    @Test
    public void testValidateData() throws BTSLBaseException, ParseException {
        thrown.expect(BTSLBaseException.class);
        homeScreenTransactionServiceImpl.validateData("Service Type", new ArrayList(), "2020-03-01", "2020-03-01");
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#validateData(String, ArrayList, String, String)}
     */
    @Test
    public void testValidateData2() throws BTSLBaseException, ParseException {
        thrown.expect(BTSLBaseException.class);
        homeScreenTransactionServiceImpl.validateData("C2S", new ArrayList(), "2020-03-01", "2020-03-01");
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getPassBookView(String, PassbookViewInfoRequestVO, Locale, PassbookViewInfoResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetPassBookView() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getPassBookView(HomeScreenTransactionServiceImpl.java:534)

        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";
        PassbookViewInfoRequestVO passbookViewInfoRequestVO = null;
        Locale locale = null;
        PassbookViewInfoResponse response = null;

        // Act
        this.homeScreenTransactionServiceImpl.getPassBookView(msisdn, passbookViewInfoRequestVO, locale, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getChannelUserTxnDetail(String, String, String, String, String, Locale, TransactionalDataResponseVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetChannelUserTxnDetail() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getChannelUserTxnDetail(HomeScreenTransactionServiceImpl.java:655)

        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";
        String fromDate = "";
        String toDate = "";
        String transferType = "";
        String transferSubType = "";
        Locale locale = null;
        TransactionalDataResponseVO response = null;

        // Act
        this.homeScreenTransactionServiceImpl.getChannelUserTxnDetail(msisdn, fromDate, toDate, transferType,
                transferSubType, locale, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getPendingtxnList(String, String, PendingTxnListResponseVO, Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetPendingtxnList() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getPendingtxnList(HomeScreenTransactionServiceImpl.java:731)

        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";
        String transferType = "";
        PendingTxnListResponseVO pendingTxnListResponseVO = null;
        Locale locale = null;

        // Act
        this.homeScreenTransactionServiceImpl.getPendingtxnList(msisdn, transferType, pendingTxnListResponseVO, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getUserWidgetList(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserWidgetList() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getUserWidgetList(HomeScreenTransactionServiceImpl.java:842)

        // Arrange
        // TODO: Populate arranged inputs
        String msisdn = "";

        // Act
        ArrayList<String> actualUserWidgetList = this.homeScreenTransactionServiceImpl.getUserWidgetList(msisdn);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getUserBalances(ChannelUserVO, UserBalanceRequestVO, Locale, UserBalanceResponseVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserBalances() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getUserBalances(HomeScreenTransactionServiceImpl.java:911)

        // Arrange
        // TODO: Populate arranged inputs
        ChannelUserVO channelUserVO = JUnitConfig.getChannelUserVO();
        UserBalanceRequestVO c2sTotalTransactionCountRequestVO = null;
        Locale locale = null;
        UserBalanceResponseVO response = null;

        // Act
        this.homeScreenTransactionServiceImpl.getUserBalances(channelUserVO, c2sTotalTransactionCountRequestVO, locale,
                response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getUserHierarchyList(Connection, String, UserHierachyRequestVO, UserHierarchyUIResponseData, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserHierarchyList() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getUserHierarchyList(HomeScreenTransactionServiceImpl.java:967)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        JUnitConfig.init();
        UserHierachyRequestVO requestVO = new UserHierachyRequestVO();
        requestVO.setAdvancedSearch(true);
        requestVO.setLoginID("Login ID");
        requestVO.setMsisdn("Msisdn");
        requestVO.setOwnerName("Owner Name");
        requestVO.setParentCategory("Parent Category");
        requestVO.setSimpleSearch(true);
        requestVO.setUserCategory("User Category");
        requestVO.setUserStatus("User Status");

        UserHierarchyUIResponseData responseVO = new UserHierarchyUIResponseData();
        responseVO.setBalanceList(new ArrayList<>());
        responseVO.setCategory("Category");
        responseVO.setCategoryCode("Category Code");
        responseVO.setChildList(new ArrayList<>());
        responseVO.setLevel(1);
        responseVO.setLoginId("42");
        responseVO.setMsisdn("Msisdn");
        responseVO.setParentID("Parent ID");
        responseVO.setStatus("Status");
        responseVO.setStatusCode("Status Code");
        responseVO.setUserID("User ID");
        responseVO.setUserType("User Type");
        responseVO.setUsername("janedoe");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        homeScreenTransactionServiceImpl.getUserHierarchyList(JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
                response1);
    }

    /**
     * Method under test: {@link HomeScreenTransactionServiceImpl#getUserHierarchyList(Connection, String, UserHierachyRequestVO, UserHierarchyUIResponseData, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetUserHierarchyList2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.user.businesslogic.UserDAO.loadAllUserDetailsByLoginID(UserDAO.java:4514)
        //       at com.restapi.c2s.services.HomeScreenTransactionServiceImpl.getUserHierarchyList(HomeScreenTransactionServiceImpl.java:967)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        UserHierachyRequestVO requestVO = new UserHierachyRequestVO();
        requestVO.setAdvancedSearch(true);
        requestVO.setLoginID("Login ID");
        requestVO.setMsisdn("Msisdn");
        requestVO.setOwnerName("Owner Name");
        requestVO.setParentCategory("Parent Category");
        requestVO.setSimpleSearch(true);
        requestVO.setUserCategory("User Category");
        requestVO.setUserStatus("User Status");

        UserHierarchyUIResponseData responseVO = new UserHierarchyUIResponseData();
        responseVO.setBalanceList(new ArrayList<>());
        responseVO.setCategory("Category");
        responseVO.setCategoryCode("Category Code");
        responseVO.setChildList(new ArrayList<>());
        responseVO.setLevel(1);
        responseVO.setLoginId("42");
        responseVO.setMsisdn("Msisdn");
        responseVO.setParentID("Parent ID");
        responseVO.setStatus("Status");
        responseVO.setStatusCode("Status Code");
        responseVO.setUserID("User ID");
        responseVO.setUserType("User Type");
        responseVO.setUsername("janedoe");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        homeScreenTransactionServiceImpl.getUserHierarchyList(JUnitConfig.getConnection(), "Login ID", requestVO, responseVO,
                response1);
    }
}

