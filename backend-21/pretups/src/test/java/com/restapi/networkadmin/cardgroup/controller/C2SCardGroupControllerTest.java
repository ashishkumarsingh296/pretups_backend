package com.restapi.networkadmin.cardgroup.controller;

import static org.mockito.Mockito.mock;

import com.btsl.common.XssWrapper;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.security.CustomResponseWrapper;
import com.restapi.networkadmin.cardgroup.requestVO.CardGroupSetVersionListRequestVO;

import java.util.ArrayList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;

public class C2SCardGroupControllerTest {
    /**
     * Method under test: {@link C2SCardGroupController#viewC2SCardGroup(MultiValueMap, HttpServletResponse, HttpServletRequest, CardGroupSetVersionListRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewC2SCardGroup() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Diffblue AI was unable to find a test

        C2SCardGroupController c2sCardGroupController = new C2SCardGroupController();
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = new CustomResponseWrapper(new Response());
        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));

        CardGroupSetVersionListRequestVO requestVO = new CardGroupSetVersionListRequestVO();
       //jdk21 requestVO.setCardGroupSetVersionList(new ArrayList<>());
        requestVO.setSelectCardGroupSetId("42");
        requestVO.setSelectCardGroupSetVersionId("42");
    //    c2sCardGroupController.viewC2SCardGroup(headers, response1, httpServletRequest, requestVO);
    }

    /**
     * Method under test: {@link C2SCardGroupController#viewC2SCardGroup(MultiValueMap, HttpServletResponse, HttpServletRequest, CardGroupSetVersionListRequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewC2SCardGroup2() throws Exception {
       com.btsl.util.JUnitConfig.init();
        //   Diffblue AI was unable to find a test

        C2SCardGroupController c2sCardGroupController = new C2SCardGroupController();
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = new CustomResponseWrapper(mock(CustomResponseWrapper.class));
        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));

        CardGroupSetVersionListRequestVO requestVO = new CardGroupSetVersionListRequestVO();
      //jdk21  requestVO.setCardGroupSetVersionList(new ArrayList<>());
        requestVO.setSelectCardGroupSetId("42");
        requestVO.setSelectCardGroupSetVersionId("42");
        c2sCardGroupController.viewC2SCardGroup(headers, response1, httpServletRequest, "SetID", "");
    }
}

