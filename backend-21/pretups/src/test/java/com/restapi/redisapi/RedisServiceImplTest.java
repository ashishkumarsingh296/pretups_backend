package com.restapi.redisapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.btsl.common.ErrorMap;
import com.btsl.security.CustomResponseWrapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {RedisServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisServiceImplTest {
    @Autowired
    private RedisServiceImpl redisServiceImpl;

    /**
     * Method under test: {@link RedisServiceImpl#sublookupsCache(Connection, SublookupsCacheResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSublookupsCache() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.redisapi.RedisServiceImpl.sublookupsCache(RedisServiceImpl.java:113)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        SublookupsCacheResponse response = new SublookupsCacheResponse();
        response.setErrorMap(errorMap);
        response.setMap(new HashMap());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        redisServiceImpl.sublookupsCache (com.btsl.util.JUnitConfig.getConnection(), response, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link RedisServiceImpl#sublookupsCache(Connection, SublookupsCacheResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSublookupsCache2() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.redisapi.RedisServiceImpl.sublookupsCache(RedisServiceImpl.java:113)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        SublookupsCacheResponse response = mock(SublookupsCacheResponse.class);
        doNothing().when(response).setErrorMap(Mockito.<ErrorMap>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setTransactionId(Mockito.<String>any());
        doNothing().when(response).setMap(Mockito.<HashMap<Object, Object>>any());
        response.setErrorMap(errorMap);
        response.setMap(new HashMap());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        redisServiceImpl.sublookupsCache (com.btsl.util.JUnitConfig.getConnection(), response, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link RedisServiceImpl#sublookupsCache(Connection, SublookupsCacheResponse, HttpServletResponse)}
     */
    @Test
    public void testSublookupsCache3() {
        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        SublookupsCacheResponse response = mock(SublookupsCacheResponse.class);
        doNothing().when(response).setErrorMap(Mockito.<ErrorMap>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setTransactionId(Mockito.<String>any());
        doNothing().when(response).setMap(Mockito.<HashMap<Object, Object>>any());
        response.setErrorMap(errorMap);
        response.setMap(new HashMap());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        assertSame(response, redisServiceImpl.sublookupsCache (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag));
        verify(response).setErrorMap(Mockito.<ErrorMap>any());
        verify(response).setMessage(Mockito.<String>any());
        verify(response, atLeast(1)).setMessageCode(Mockito.<String>any());
        verify(response, atLeast(1)).setStatus(anyInt());
        verify(response).setTransactionId(Mockito.<String>any());
        verify(response, atLeast(1)).setMap(Mockito.<HashMap<Object, Object>>any());
        assertEquals(400, ((MockHttpServletResponse) responseSwag.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link RedisServiceImpl#sublookupsCache(Connection, SublookupsCacheResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSublookupsCache4() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.redisapi.RedisServiceImpl.sublookupsCache(RedisServiceImpl.java:113)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        SublookupsCacheResponse response = mock(SublookupsCacheResponse.class);
        doNothing().when(response).setErrorMap(Mockito.<ErrorMap>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setTransactionId(Mockito.<String>any());
        doNothing().when(response).setMap(Mockito.<HashMap<Object, Object>>any());
        response.setErrorMap(errorMap);
        response.setMap(new HashMap());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        redisServiceImpl.sublookupsCache (com.btsl.util.JUnitConfig.getConnection(), response, null);
    }

    /**
     * Method under test: {@link RedisServiceImpl#preferenceCache(Connection, PreferenceCacheResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPreferenceCache() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.redisapi.RedisServiceImpl.preferenceCache(RedisServiceImpl.java:160)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        PreferenceCacheResponse response = new PreferenceCacheResponse();
        response.setErrorMap(errorMap);
        response.setMap(new HashMap<>());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        redisServiceImpl.preferenceCache (com.btsl.util.JUnitConfig.getConnection(), response, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link RedisServiceImpl#preferenceCache(Connection, PreferenceCacheResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPreferenceCache2() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at org.apache.catalina.connector.Response.isCommitted(Response.java:619)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1494)
        //       at org.apache.catalina.connector.Response.setStatus(Response.java:1476)
        //       at jakarta.servlet.http.HttpServletResponseWrapper.setStatus(HttpServletResponseWrapper.java:201)
        //       at com.restapi.redisapi.RedisServiceImpl.preferenceCache(RedisServiceImpl.java:160)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        PreferenceCacheResponse response = mock(PreferenceCacheResponse.class);
        doNothing().when(response).setErrorMap(Mockito.<ErrorMap>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setTransactionId(Mockito.<String>any());
        doNothing().when(response).setMap(Mockito.<Map<Object, Object>>any());
        response.setErrorMap(errorMap);
        response.setMap(new HashMap<>());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        redisServiceImpl.preferenceCache (com.btsl.util.JUnitConfig.getConnection(), response, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link RedisServiceImpl#preferenceCache(Connection, PreferenceCacheResponse, HttpServletResponse)}
     */
    @Test
    public void testPreferenceCache3() {
        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        PreferenceCacheResponse response = mock(PreferenceCacheResponse.class);
        doNothing().when(response).setErrorMap(Mockito.<ErrorMap>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setTransactionId(Mockito.<String>any());
        doNothing().when(response).setMap(Mockito.<Map<Object, Object>>any());
        response.setErrorMap(errorMap);
        response.setMap(new HashMap<>());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        CustomResponseWrapper responseSwag = new CustomResponseWrapper(new MockHttpServletResponse());
        assertSame(response, redisServiceImpl.preferenceCache (com.btsl.util.JUnitConfig.getConnection(), response, responseSwag));
        verify(response).setErrorMap(Mockito.<ErrorMap>any());
        verify(response).setMessage(Mockito.<String>any());
        verify(response, atLeast(1)).setMessageCode(Mockito.<String>any());
        verify(response, atLeast(1)).setStatus(anyInt());
        verify(response).setTransactionId(Mockito.<String>any());
        verify(response, atLeast(1)).setMap(Mockito.<Map<Object, Object>>any());
        assertEquals(400, ((MockHttpServletResponse) responseSwag.getResponse()).getStatus());
    }

    /**
     * Method under test: {@link RedisServiceImpl#preferenceCache(Connection, PreferenceCacheResponse, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPreferenceCache4() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.redisapi.RedisServiceImpl.preferenceCache(RedisServiceImpl.java:160)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());
        PreferenceCacheResponse response = mock(PreferenceCacheResponse.class);
        doNothing().when(response).setErrorMap(Mockito.<ErrorMap>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setMessageCode(Mockito.<String>any());
        doNothing().when(response).setStatus(anyInt());
        doNothing().when(response).setTransactionId(Mockito.<String>any());
        doNothing().when(response).setMap(Mockito.<Map<Object, Object>>any());
        response.setErrorMap(errorMap);
        response.setMap(new HashMap<>());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setStatus(1);
        response.setTransactionId("42");
        redisServiceImpl.preferenceCache (com.btsl.util.JUnitConfig.getConnection(), response, null);
    }
}

