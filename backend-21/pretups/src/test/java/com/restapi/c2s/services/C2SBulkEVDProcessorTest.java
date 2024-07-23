package com.restapi.c2s.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;
import com.btsl.user.businesslogic.OAuthUserData;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

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

@ContextConfiguration(classes = {C2SBulkEVDProcessor.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2SBulkEVDProcessorTest {
    @Autowired
    private C2SBulkEVDProcessor c2SBulkEVDProcessor;

    /**
     * Method under test: {@link C2SBulkEVDProcessor#addBatch(Connection, C2SBulkEvdRechargeRequestVO, ChannelUserVO, String, int)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddBatch() throws BTSLBaseException, SQLException, ParseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        //Connection con = mock(Connection.class);

        C2SBulkEvdRechargeRequestVO requestVO = new C2SBulkEvdRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        requestVO.setData(data);

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("2");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("01/01/24");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");

        ChannelUserVO channelUserVO = ChannelUserVO.getInstance() ;
        CategoryVO categoryVO =  new CategoryVO() ;
        categoryVO.setDomainCodeforCategory("TEST");

        channelUserVO.setCategoryVO(categoryVO);

        c2SBulkEVDProcessor.addBatch(JUnitConfig.getConnection(), requestVO, channelUserVO, "Servicekeyword", 2);
    }

    /**
     * Method under test: {@link C2SBulkEVDProcessor#addBatch(Connection, C2SBulkEvdRechargeRequestVO, ChannelUserVO, String, int)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddBatch2() throws BTSLBaseException, SQLException, ParseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.text.ParseException: dd/MM/yy
        //       at com.btsl.util.BTSLUtil.getDateFromDateString(BTSLUtil.java:1015)
        //       at com.restapi.c2s.services.C2SBulkEVDProcessor.addBatch(C2SBulkEVDProcessor.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("2");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("01/01/24");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");

        C2SBulkEvdRechargeRequestVO requestVO = new C2SBulkEvdRechargeRequestVO();
        requestVO.setData(data);
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance() ;
        CategoryVO categoryVO =  new CategoryVO() ;
        categoryVO.setDomainCodeforCategory("TEST");

        channelUserVO.setCategoryVO(categoryVO);

        c2SBulkEVDProcessor.addBatch(JUnitConfig.getConnection(), requestVO, channelUserVO, "Servicekeyword", 2);
    }

    /**
     * Method under test: {@link C2SBulkEVDProcessor#addBatch(Connection, C2SBulkEvdRechargeRequestVO, ChannelUserVO, String, int)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddBatch3() throws BTSLBaseException, SQLException, ParseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("2");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("01/01/24");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");

        C2SBulkEvdRechargeRequestVO requestVO = new C2SBulkEvdRechargeRequestVO();
        requestVO.setData(data);
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance() ;
        CategoryVO categoryVO =  new CategoryVO() ;
        categoryVO.setDomainCodeforCategory("TEST");

        channelUserVO.setCategoryVO(categoryVO);

        c2SBulkEVDProcessor.addBatch(JUnitConfig.getConnection(), requestVO, channelUserVO, "Servicekeyword", 2);
    }

    /**
     * Method under test: {@link C2SBulkEVDProcessor#addBatch(Connection, C2SBulkEvdRechargeRequestVO, ChannelUserVO, String, int)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddBatch4() throws BTSLBaseException, SQLException, ParseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.text.ParseException: dd/MM/yy
        //       at com.btsl.util.BTSLUtil.getDateFromDateString(BTSLUtil.java:1015)
        //       at com.restapi.c2s.services.C2SBulkEVDProcessor.addBatch(C2SBulkEVDProcessor.java:88)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("2");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("01/01/24");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");

        C2SBulkRechargeDetails c2sBulkRechargeDetails = new C2SBulkRechargeDetails();
        c2sBulkRechargeDetails.setBatchType("Batch Type");
        c2sBulkRechargeDetails.setExtcode("Extcode");
        c2sBulkRechargeDetails.setExtnwcode("Extnwcode");
        c2sBulkRechargeDetails.setFile("File");
        c2sBulkRechargeDetails.setFileName("foo.txt");
        c2sBulkRechargeDetails.setFileType("File Type");
        c2sBulkRechargeDetails.setLoginid("Loginid");
        c2sBulkRechargeDetails.setMsisdn("Msisdn");
        c2sBulkRechargeDetails.setNoOfDays("2");
        c2sBulkRechargeDetails.setOccurence("Occurence");
        c2sBulkRechargeDetails.setPassword("iloveyou");
        c2sBulkRechargeDetails.setPin("Pin");
        c2sBulkRechargeDetails.setScheduleDate("01/01/24");
        c2sBulkRechargeDetails.setScheduleNow("Schedule Now");
        c2sBulkRechargeDetails.setUserid("Userid");
        C2SBulkEvdRechargeRequestVO requestVO = mock(C2SBulkEvdRechargeRequestVO.class);
        when(requestVO.getData()).thenReturn(c2sBulkRechargeDetails);
        doNothing().when(requestVO).setData(Mockito.<OAuthUserData>any());
        doNothing().when(requestVO).setLoginId(Mockito.<String>any());
        doNothing().when(requestVO).setMsisdn(Mockito.<String>any());
        doNothing().when(requestVO).setPassword(Mockito.<String>any());
        doNothing().when(requestVO).setPin(Mockito.<String>any());
        doNothing().when(requestVO).setReqGatewayCode(Mockito.<String>any());
        doNothing().when(requestVO).setReqGatewayLoginId(Mockito.<String>any());
        doNothing().when(requestVO).setReqGatewayPassword(Mockito.<String>any());
        doNothing().when(requestVO).setReqGatewayType(Mockito.<String>any());
        doNothing().when(requestVO).setServicePort(Mockito.<String>any());
        doNothing().when(requestVO).setSourceType(Mockito.<String>any());
        doNothing().when(requestVO).setData(Mockito.<C2SBulkRechargeDetails>any());
        requestVO.setData(data);
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance() ;
        CategoryVO categoryVO =  new CategoryVO() ;
        categoryVO.setDomainCodeforCategory("TEST");

        channelUserVO.setCategoryVO(categoryVO);

        c2SBulkEVDProcessor.addBatch(JUnitConfig.getConnection(), requestVO, channelUserVO, "Servicekeyword", 2);
    }

    /**
     * Method under test: {@link C2SBulkEVDProcessor#processRequestBulkEVD(C2SBulkEvdRechargeRequestVO, String, String, String, MultiValueMap, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessRequestBulkEVD() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        C2SBulkEvdRechargeRequestVO requestVO = new C2SBulkEvdRechargeRequestVO();

        OAuthUserData data = new OAuthUserData();
        data.setExtcode("Extcode");
        data.setLoginid("Loginid");
        data.setMsisdn("Msisdn");
        data.setPassword("iloveyou");
        data.setPin("Pin");
        data.setUserid("Userid");
        requestVO.setData(data);

        C2SBulkRechargeDetails data2 = new C2SBulkRechargeDetails();
        data2.setBatchType("Batch Type");
        data2.setExtcode("Extcode");
        data2.setExtnwcode("Extnwcode");
        data2.setFile("File");
        data2.setFileName("foo.txt");
        data2.setFileType("File Type");
        data2.setLoginid("Loginid");
        data2.setMsisdn("Msisdn");
        data2.setNoOfDays("2");
        data2.setOccurence("Occurence");
        data2.setPassword("iloveyou");
        data2.setPin("Pin");
        data2.setScheduleDate("01/01/24");
        data2.setScheduleNow("Schedule Now");
        data2.setUserid("Userid");
        requestVO.setData(data2);
        requestVO.setLoginId("42");
        requestVO.setMsisdn("Msisdn");
        requestVO.setPassword("iloveyou");
        requestVO.setPin("Pin");
        requestVO.setReqGatewayCode("Req Gateway Code");
        requestVO.setReqGatewayLoginId("42");
        requestVO.setReqGatewayPassword("iloveyou");
        requestVO.setReqGatewayType("Req Gateway Type");
        requestVO.setServicePort("Service Port");
        requestVO.setSourceType("Source Type");
        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper response1 = org.mockito.Mockito.mock(CustomResponseWrapper.class); org.mockito.Mockito.doNothing().when(response1).setStatus(org.mockito.Mockito.anyInt());


        c2SBulkEVDProcessor.processRequestBulkEVD(requestVO, "Service Keyword", "Request IDStr", "Request For", headers,
                response1);
    }

    /**
     * Method under test: {@link C2SBulkEVDProcessor#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testStaffUserDetails() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: Could not load TestContextBootstrapper [null]. Specify @BootstrapWith's 'value' attribute or make the default bootstrapper class available.
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:147)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   java.lang.NoClassDefFoundError: Could not initialize class org.springframework.test.context.TestContextAnnotationUtils
        //       at org.springframework.test.context.BootstrapUtils.resolveExplicitTestContextBootstrapper(BootstrapUtils.java:157)
        //       at org.springframework.test.context.BootstrapUtils.resolveTestContextBootstrapper(BootstrapUtils.java:130)
        //       at org.springframework.test.context.TestContextManager.<init>(TestContextManager.java:122)
        //       at java.util.Optional.map(Optional.java:215)
        //   See https://diff.blue/R026 to resolve this issue.

        ChannelUserVO channelUserVO = ChannelUserVO.getInstance() ;
        CategoryVO categoryVO =  new CategoryVO() ;
        categoryVO.setDomainCodeforCategory("TEST");

        channelUserVO.setCategoryVO(categoryVO);

        c2SBulkEVDProcessor.staffUserDetails(channelUserVO, channelUserVO);
    }

    /**
     * Method under test: {@link C2SBulkEVDProcessor#writeFileCSV(List, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testWriteFileCSV() throws IOException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

        c2SBulkEVDProcessor.writeFileCSV(new ArrayList<>(), "/data1/pretupsapp/foo.txt");
    }
}

