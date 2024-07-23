package com.restapi.c2cbulk;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.XssWrapper;
import com.btsl.db.util.MComConnection;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.security.CustomResponseWrapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {C2CBulkServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2CBulkServiceImplTest {
    @Autowired
    private C2CBulkServiceImpl c2CBulkServiceImpl;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link C2CBulkServiceImpl#processC2cBulkTrfAppProcess(MultiValueMap, HttpServletResponse, C2CProcessBulkRequestVO, HttpServletRequest)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testProcessC2cBulkTrfAppProcess() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
      //  mockStatic(OAuthenticationUtil.class) ;

       // doNothing().when(OAuthenticationUtil.class).validateTokenApi(Mockito.any(), Mockito.any()) ;


        HttpHeaders headers = new HttpHeaders();
        CustomResponseWrapper responseSwag = mock(CustomResponseWrapper.class) ;

        doNothing().when(responseSwag).setStatus(Mockito.anyInt());

        C2CProcessBulkRequestVO req = new C2CProcessBulkRequestVO();
        req.setBatchId("42");
        req.setExtcode("Extcode");
        req.setFile("File");
        req.setFileName("foo.txt");
        req.setFileType("File Type");
        req.setLanguage1("en");
        req.setLanguage2("en");
        req.setLoginid("Loginid");
        req.setMsisdn("Msisdn");
        req.setPassword("iloveyou");
        req.setPin("Pin");
        req.setUserid("Userid");

        HttpServletRequest request = mock(HttpServletRequest.class);
        c2CBulkServiceImpl.processC2cBulkTrfAppProcess(headers, responseSwag, req,
                request);
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#decodeFile(String)}
     */
    @Test
    public void testDecodeFile() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        byte[] actualDecodeFileResult = (new C2CBulkServiceImpl()).decodeFile("42");
        assertEquals(1, actualDecodeFileResult.length);
        assertEquals((byte) -29, actualDecodeFileResult[0]);
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#decodeFile(String)}
     */
    @Test
    public void testDecodeFile2() throws BTSLBaseException {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        thrown.expect(BTSLBaseException.class);
        (new C2CBulkServiceImpl()).decodeFile("APPRV");
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#writeFileCSV(List, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testWriteFileCSV() throws IOException {
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


        FileWriter csvWriter = mock(FileWriter.class);
        //when(csvWriter.append(Mockito.anyString())).thenReturn(csvWriter);

        c2CBulkServiceImpl.writeFileCSV(new ArrayList<>(), "\\data1\\pretupsapp\\tomcat_trunk_dev\\logs/foo.txt");
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    public void testStaffUserDetails() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R002 Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     C2CBulkServiceImpl.log

        C2CBulkServiceImpl c2cBulkServiceImpl = new C2CBulkServiceImpl();
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
        c2cBulkServiceImpl.staffUserDetails(channelUserVO, ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testStaffUserDetails2() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2cbulk.C2CBulkServiceImpl.staffUserDetails(C2CBulkServiceImpl.java:898)
        //   See https://diff.blue/R013 to resolve this issue.

        C2CBulkServiceImpl c2cBulkServiceImpl = new C2CBulkServiceImpl();
        ChannelUserVO channelUserVO = mock(ChannelUserVO.class);

        c2cBulkServiceImpl.staffUserDetails(channelUserVO, ChannelUserVO.getInstance());
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    public void testStaffUserDetails3() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        C2CBulkServiceImpl c2cBulkServiceImpl = new C2CBulkServiceImpl();
        ChannelUserVO channelUserVO = mock(ChannelUserVO.class);
        when(channelUserVO.getParentID()).thenReturn("Parent ID");
        doNothing().when(channelUserVO).setParentLoginID(Mockito.<String>any());
        doNothing().when(channelUserVO).setPinRequired(Mockito.<String>any());
        doNothing().when(channelUserVO).setSmsPin(Mockito.<String>any());
        doNothing().when(channelUserVO).setMsisdn(Mockito.<String>any());
        doNothing().when(channelUserVO).setOwnerID(Mockito.<String>any());
        doNothing().when(channelUserVO).setParentID(Mockito.<String>any());
        doNothing().when(channelUserVO).setStaffUser(anyBoolean());
        doNothing().when(channelUserVO).setStatus(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserID(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserType(Mockito.<String>any());
        ChannelUserVO parentChannelUserVO = ChannelUserVO.getInstance();
        c2cBulkServiceImpl.staffUserDetails(channelUserVO, parentChannelUserVO);
        verify(channelUserVO).getParentID();
        verify(channelUserVO).setParentLoginID(Mockito.<String>any());
        verify(channelUserVO).setPinRequired(Mockito.<String>any());
        verify(channelUserVO).setSmsPin(Mockito.<String>any());
        verify(channelUserVO).setMsisdn(Mockito.<String>any());
        verify(channelUserVO).setOwnerID(Mockito.<String>any());
        verify(channelUserVO).setParentID(Mockito.<String>any());
        verify(channelUserVO).setStaffUser(anyBoolean());
        verify(channelUserVO).setStatus(Mockito.<String>any());
        verify(channelUserVO).setUserID(Mockito.<String>any());
        verify(channelUserVO).setUserType(Mockito.<String>any());
        assertEquals(0L, parentChannelUserVO.getBalance());
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testStaffUserDetails4() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2cbulk.C2CBulkServiceImpl.staffUserDetails(C2CBulkServiceImpl.java:899)
        //   See https://diff.blue/R013 to resolve this issue.

        C2CBulkServiceImpl c2cBulkServiceImpl = new C2CBulkServiceImpl();
        ChannelUserVO channelUserVO = mock(ChannelUserVO.class);
        when(channelUserVO.getParentID()).thenReturn("Parent ID");
        doNothing().when(channelUserVO).setParentLoginID(Mockito.<String>any());
        doNothing().when(channelUserVO).setPinRequired(Mockito.<String>any());
        doNothing().when(channelUserVO).setSmsPin(Mockito.<String>any());
        doNothing().when(channelUserVO).setMsisdn(Mockito.<String>any());
        doNothing().when(channelUserVO).setOwnerID(Mockito.<String>any());
        doNothing().when(channelUserVO).setParentID(Mockito.<String>any());
        doNothing().when(channelUserVO).setStaffUser(anyBoolean());
        doNothing().when(channelUserVO).setStatus(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserID(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserType(Mockito.<String>any());

        ChannelUserVO parentChannelUserVO = mock(ChannelUserVO.class);


        c2cBulkServiceImpl.staffUserDetails(channelUserVO, parentChannelUserVO);
    }

    /**
     * Method under test: {@link C2CBulkServiceImpl#staffUserDetails(ChannelUserVO, ChannelUserVO)}
     */
    @Test
    public void testStaffUserDetails5() {
        //   Diffblue Cover was unable to write a Spring test,
        //   so wrote a non-Spring test instead.
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

        C2CBulkServiceImpl c2cBulkServiceImpl = new C2CBulkServiceImpl();
        ChannelUserVO channelUserVO = mock(ChannelUserVO.class);
        when(channelUserVO.getParentID()).thenReturn("Parent ID");
        doNothing().when(channelUserVO).setParentLoginID(Mockito.<String>any());
        doNothing().when(channelUserVO).setPinRequired(Mockito.<String>any());
        doNothing().when(channelUserVO).setSmsPin(Mockito.<String>any());
        doNothing().when(channelUserVO).setMsisdn(Mockito.<String>any());
        doNothing().when(channelUserVO).setOwnerID(Mockito.<String>any());
        doNothing().when(channelUserVO).setParentID(Mockito.<String>any());
        doNothing().when(channelUserVO).setStaffUser(anyBoolean());
        doNothing().when(channelUserVO).setStatus(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserID(Mockito.<String>any());
        doNothing().when(channelUserVO).setUserType(Mockito.<String>any());
        ChannelUserVO parentChannelUserVO = mock(ChannelUserVO.class);
        when(parentChannelUserVO.getPinRequired()).thenReturn("Pin Required");
        when(parentChannelUserVO.getSmsPin()).thenReturn("Sms Pin");
        when(parentChannelUserVO.getLoginID()).thenReturn("Login ID");
        when(parentChannelUserVO.getMsisdn()).thenReturn("Msisdn");
        when(parentChannelUserVO.getOwnerID()).thenReturn("Owner ID");
        when(parentChannelUserVO.getParentID()).thenReturn("Parent ID");
        when(parentChannelUserVO.getStatus()).thenReturn("Status");
        when(parentChannelUserVO.getUserType()).thenReturn("User Type");
        c2cBulkServiceImpl.staffUserDetails(channelUserVO, parentChannelUserVO);
        verify(channelUserVO).getParentID();
        verify(channelUserVO).setParentLoginID(Mockito.<String>any());
        verify(channelUserVO).setPinRequired(Mockito.<String>any());
        verify(channelUserVO).setSmsPin(Mockito.<String>any());
        verify(channelUserVO).setMsisdn(Mockito.<String>any());
        verify(channelUserVO).setOwnerID(Mockito.<String>any());
        verify(channelUserVO).setParentID(Mockito.<String>any());
        verify(channelUserVO).setStaffUser(anyBoolean());
        verify(channelUserVO).setStatus(Mockito.<String>any());
        verify(channelUserVO).setUserID(Mockito.<String>any());
        verify(channelUserVO).setUserType(Mockito.<String>any());
        verify(parentChannelUserVO).getPinRequired();
        verify(parentChannelUserVO).getSmsPin();
        verify(parentChannelUserVO).getLoginID();
        verify(parentChannelUserVO).getMsisdn();
        verify(parentChannelUserVO).getOwnerID();
        verify(parentChannelUserVO).getParentID();
        verify(parentChannelUserVO).getStatus();
        verify(parentChannelUserVO).getUserType();
    }
}

