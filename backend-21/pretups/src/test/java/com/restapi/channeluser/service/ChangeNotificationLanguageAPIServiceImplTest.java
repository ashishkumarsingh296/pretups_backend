package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.security.CustomResponseWrapper;

import java.sql.Connection;

import java.util.ArrayList;

import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {ChangeNotificationLanguageAPIServiceImpl.class,
        ChannelUserDAO.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChangeNotificationLanguageAPIServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private ChannelUserDAO channelUserDAO;

    @Autowired
    private ChangeNotificationLanguageAPIServiceImpl changeNotificationLanguageAPIServiceImpl;

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadUsersDetails(MultiValueMap, HashMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadUsersDetails() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.loadUsersDetails(ChangeNotificationLanguageAPIServiceImpl.java:125)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        HashMap<String, String> requestMap = new HashMap<>();
        changeNotificationLanguageAPIServiceImpl.loadUsersDetails(headers, requestMap,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadUsersDetails(MultiValueMap, HashMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadUsersDetails2() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.loadUsersDetails(ChangeNotificationLanguageAPIServiceImpl.java:125)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        headers.add("https://example.org/example", "https://example.org/example");
        HashMap<String, String> requestMap = new HashMap<>();
        changeNotificationLanguageAPIServiceImpl.loadUsersDetails(headers, requestMap,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadUserPhoneDetailsByMsisdn(MultiValueMap, HashMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadUserPhoneDetailsByMsisdn() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.loadUserPhoneDetailsByMsisdn(ChangeNotificationLanguageAPIServiceImpl.java:312)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        HashMap<String, String> requestMap = new HashMap<>();
        changeNotificationLanguageAPIServiceImpl.loadUserPhoneDetailsByMsisdn(headers, requestMap,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadUserPhoneDetailsByMsisdn(MultiValueMap, HashMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadUserPhoneDetailsByMsisdn2() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.loadUserPhoneDetailsByMsisdn(ChangeNotificationLanguageAPIServiceImpl.java:312)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        headers.add("https://example.org/example", "https://example.org/example");
        HashMap<String, String> requestMap = new HashMap<>();
        changeNotificationLanguageAPIServiceImpl.loadUserPhoneDetailsByMsisdn(headers, requestMap,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#changeNotificationLanguage(MultiValueMap, NotificationLanguageRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChangeNotificationLanguage() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.changeNotificationLanguage(ChangeNotificationLanguageAPIServiceImpl.java:430)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();

        NotificationLanguageRequestVO requestVO = new NotificationLanguageRequestVO();
        requestVO.setChangedPhoneLanguageList(new ArrayList<>());
        requestVO.setUserLoginID("User Login ID");
        changeNotificationLanguageAPIServiceImpl.changeNotificationLanguage(headers, requestVO,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#changeNotificationLanguage(MultiValueMap, NotificationLanguageRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChangeNotificationLanguage2() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.changeNotificationLanguage(ChangeNotificationLanguageAPIServiceImpl.java:430)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        headers.add("https://example.org/example", "https://example.org/example");

        NotificationLanguageRequestVO requestVO = new NotificationLanguageRequestVO();
        requestVO.setChangedPhoneLanguageList(new ArrayList<>());
        requestVO.setUserLoginID("User Login ID");
        changeNotificationLanguageAPIServiceImpl.changeNotificationLanguage(headers, requestVO,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#changeNotificationLanguage(MultiValueMap, NotificationLanguageRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testChangeNotificationLanguage3() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.changeNotificationLanguage(ChangeNotificationLanguageAPIServiceImpl.java:430)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        NotificationLanguageRequestVO requestVO = mock(NotificationLanguageRequestVO.class);
        doNothing().when(requestVO).setChangedPhoneLanguageList(Mockito.<ArrayList<ChangePhoneLanguage>>any());
        doNothing().when(requestVO).setUserLoginID(Mockito.<String>any());
        requestVO.setChangedPhoneLanguageList(new ArrayList<>());
        requestVO.setUserLoginID("User Login ID");
        changeNotificationLanguageAPIServiceImpl.changeNotificationLanguage(headers, requestVO,
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadLanguageList(Connection, ChannelUserDAO)}
     */
    @Test
    public void testLoadLanguageList() throws BTSLBaseException {
       //Connection con = mock(Connection.class);
        ChannelUserDAO channelUserDAO2 = mock(ChannelUserDAO.class);
        when(channelUserDAO2.loadLanguageListForUser(Mockito.<Connection>any())).thenReturn(new ArrayList());
        thrown.expect(BTSLBaseException.class);
        changeNotificationLanguageAPIServiceImpl.loadLanguageList(JUnitConfig.getConnection(), channelUserDAO2);
        verify(channelUserDAO2).loadLanguageListForUser(Mockito.<Connection>any());
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadLanguageList(Connection, ChannelUserDAO)}
     */
    @Test
    public void testLoadLanguageList2() throws BTSLBaseException {
       //Connection con = mock(Connection.class);

        ArrayList arrayList = new ArrayList();
        arrayList.add("42");
        ChannelUserDAO channelUserDAO2 = mock(ChannelUserDAO.class);
        when(channelUserDAO2.loadLanguageListForUser(Mockito.<Connection>any())).thenReturn(arrayList);
        ArrayList actualLoadLanguageListResult = changeNotificationLanguageAPIServiceImpl.loadLanguageList(JUnitConfig.getConnection(),
                channelUserDAO2);
        assertSame(arrayList, actualLoadLanguageListResult);
        assertEquals(1, actualLoadLanguageListResult.size());
        assertEquals("42", actualLoadLanguageListResult.get(0));
        verify(channelUserDAO2).loadLanguageListForUser(Mockito.<Connection>any());
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#loadLanguageList(Connection, ChannelUserDAO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testLoadLanguageList3() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: An error occurred
        //       at com.btsl.pretups.user.businesslogic.ChannelUserDAO.loadLanguageListForUser(ChannelUserDAO.java:4507)
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.loadLanguageList(ChangeNotificationLanguageAPIServiceImpl.java:653)
        //   See https://diff.blue/R013 to resolve this issue.

       //Connection con = mock(Connection.class);
        ChannelUserDAO channelUserDAO2 = mock(ChannelUserDAO.class);
        when(channelUserDAO2.loadLanguageListForUser(Mockito.<Connection>any()))
                .thenThrow(new BTSLBaseException("An error occurred"));
        changeNotificationLanguageAPIServiceImpl.loadLanguageList(JUnitConfig.getConnection(), channelUserDAO2);
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#validateMsisdn(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.util.PretupsBL.getFilteredMSISDN(PretupsBL.java:347)
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.validateMsisdn(ChangeNotificationLanguageAPIServiceImpl.java:682)
        //   See https://diff.blue/R013 to resolve this issue.

        changeNotificationLanguageAPIServiceImpl.validateMsisdn("Msisdn");
    }

    /**
     * Method under test: {@link ChangeNotificationLanguageAPIServiceImpl#validateMsisdn(String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateMsisdn2() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.util.PretupsBL.getFilteredMSISDN(PretupsBL.java:347)
        //       at com.restapi.channeluser.service.ChangeNotificationLanguageAPIServiceImpl.validateMsisdn(ChangeNotificationLanguageAPIServiceImpl.java:682)
        //   See https://diff.blue/R013 to resolve this issue.

        changeNotificationLanguageAPIServiceImpl.validateMsisdn("validateMsisdn");
    }
}

