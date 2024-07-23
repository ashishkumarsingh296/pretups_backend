package com.restapi.oauth.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.XssWrapper;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.security.CustomResponseWrapper;

import jakarta.servlet.DispatcherType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

@ContextConfiguration(classes = {NonceInterceptor.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NonceInterceptorTest {
    @Autowired
    private NonceInterceptor nonceInterceptor;

    @MockBean
    private NonceValidatorService nonceValidatorService;

    /**
     * Method under test: {@link NonceInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
     */
    @Test
    public void testPreHandle() {
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        assertTrue(nonceInterceptor.preHandle(request, response1, "Handler"));
    }

    /**
     * Method under test: {@link NonceInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
     */
    @Test
    public void testPreHandle2() {
        DefaultMultipartHttpServletRequest req = mock(DefaultMultipartHttpServletRequest.class);
        when(req.getMethod()).thenReturn("https://example.org/example");
        when(req.getDispatcherType()).thenReturn(DispatcherType.FORWARD);
        XssWrapper request = new XssWrapper(new OwnHttpRequestWrapper(req));
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        assertTrue(nonceInterceptor.preHandle(request, response1, "Handler"));
        verify(req).getMethod();
        verify(req).getDispatcherType();
    }

    /**
     * Method under test: {@link NonceInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPreHandle3() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.oauth.services.NonceInterceptor.preHandle(NonceInterceptor.java:36)
        //   See https://diff.blue/R013 to resolve this issue.
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        nonceInterceptor.preHandle(null, response1, "Handler");
    }

    /**
     * Method under test: {@link NonceInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testPreHandle4() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.RuntimeException: REQUEST
        //       at jakarta.servlet.http.HttpServletRequestWrapper.getRequestURI(HttpServletRequestWrapper.java:217)
        //       at com.restapi.oauth.services.NonceInterceptor.preHandle(NonceInterceptor.java:44)
        //   See https://diff.blue/R013 to resolve this issue.

        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenThrow(new RuntimeException("REQUEST"));
        when(request.getRequestURI()).thenThrow(new RuntimeException("REQUEST"));
        when(request.getMethod()).thenReturn("https://example.org/example");
        when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());

        nonceInterceptor.preHandle(request, response1, "Handler");
    }
}

