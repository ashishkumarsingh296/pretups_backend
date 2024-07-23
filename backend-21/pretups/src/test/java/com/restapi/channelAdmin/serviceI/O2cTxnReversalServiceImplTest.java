package com.restapi.channelAdmin.serviceI;

import com.btsl.pretups.common.PretupsI;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalListRequestVO;
import com.restapi.channelAdmin.requestVO.O2CTxnReversalRequestVO;
import com.restapi.channelAdmin.responseVO.O2CTxnReversalListResponseVO;
import com.restapi.channelAdmin.responseVO.ParentCategoryListResponseVO;

import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {O2cTxnReversalServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2cTxnReversalServiceImplTest {
    @Autowired
    private O2cTxnReversalServiceImpl o2cTxnReversalServiceImpl;

    /**
     * Method under test: {@link O2cTxnReversalServiceImpl#getO2CTxnReversalList(O2CTxnReversalListRequestVO, O2CTxnReversalListResponseVO, HttpServletResponse, OAuthUser, Locale, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetO2CTxnReversalList() throws Exception {
        com.btsl.util.JUnitConfig.init(); //Auto replace


        O2CTxnReversalListRequestVO requestVO = new O2CTxnReversalListRequestVO();
        requestVO.setTransactionID("1234");
        requestVO.setMsisdn("9999999999");
        O2CTxnReversalListResponseVO response = new O2CTxnReversalListResponseVO();
        HttpServletResponse responseSwag = Mockito.mock(HttpServletResponse.class);

        OAuthUser oAuthUserData = new OAuthUser();

        OAuthUserData data = new OAuthUserData();
        data.setLoginid("123");
        data.setMsisdn("9999999999");
        oAuthUserData.setData(data);
        Locale locale = null;
        String searchBy = PretupsI.SEARCH_BY_TRANSACTIONID;
        this.o2cTxnReversalServiceImpl.getO2CTxnReversalList(requestVO, response, responseSwag, oAuthUserData, locale,
                searchBy);

        searchBy = PretupsI.SEARCH_BY_MSISDN;
        this.o2cTxnReversalServiceImpl.getO2CTxnReversalList(requestVO, response, responseSwag, oAuthUserData, locale,
                searchBy);

        searchBy = PretupsI.SEARCH_BY_ADVANCE;
        this.o2cTxnReversalServiceImpl.getO2CTxnReversalList(requestVO, response, responseSwag, oAuthUserData, locale,
                searchBy);


        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2cTxnReversalServiceImpl#enquiryDetail(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testEnquiryDetail() {
        com.btsl.util.JUnitConfig.init(this.getClass().toString()); //Auto replace

        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        o2cTxnReversalServiceImpl.enquiryDetail(headers, "Transaction ID", response1);
    }

    /**
     * Method under test: {@link O2cTxnReversalServiceImpl#enquiryDetail(MultiValueMap, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testEnquiryDetail2() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.O2cTxnReversalServiceImpl.enquiryDetail(O2cTxnReversalServiceImpl.java:909)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        headers.add("https://example.org/example", "https://example.org/example");
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class);
        org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        o2cTxnReversalServiceImpl.enquiryDetail(headers, "Transaction ID", response1);
    }

    /**
     * Method under test: {@link O2cTxnReversalServiceImpl#reverseO2CTxn(MultiValueMap, O2CTxnReversalRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testReverseO2CTxn() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.O2cTxnReversalServiceImpl.reverseO2CTxn(O2cTxnReversalServiceImpl.java:1241)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();

        O2CTxnReversalRequestVO requestVO = new O2CTxnReversalRequestVO();
        requestVO.setRemarks("Remarks");
        requestVO.setTransactionID("Transaction ID");
        o2cTxnReversalServiceImpl.reverseO2CTxn(headers, requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2cTxnReversalServiceImpl#reverseO2CTxn(MultiValueMap, O2CTxnReversalRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testReverseO2CTxn2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.channelAdmin.serviceI.O2cTxnReversalServiceImpl.reverseO2CTxn(O2cTxnReversalServiceImpl.java:1241)
        //   See https://diff.blue/R013 to resolve this issue.

        HttpHeaders headers = new HttpHeaders();
        headers.add("https://example.org/example", "https://example.org/example");

        O2CTxnReversalRequestVO requestVO = new O2CTxnReversalRequestVO();
        requestVO.setRemarks("Remarks");
        requestVO.setTransactionID("Transaction ID");
        o2cTxnReversalServiceImpl.reverseO2CTxn(headers, requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link O2cTxnReversalServiceImpl#getParentCategoryList(ParentCategoryListResponseVO, HttpServletResponse, OAuthUser, Locale, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetParentCategoryList() throws Exception {
        com.btsl.util.JUnitConfig.init(this.getClass().toString()); //Auto replace


        ParentCategoryListResponseVO response = null;
        HttpServletResponse responseSwag = null;
        OAuthUser oAuthUserData = new OAuthUser();


       // oAuthUserData.setData(data);

        Locale locale = null;
        String categoryCode = "";

        // Act
        this.o2cTxnReversalServiceImpl.getParentCategoryList(response, responseSwag, oAuthUserData, locale, categoryCode);

        // Assert
        // TODO: Add assertions on result
    }
}

