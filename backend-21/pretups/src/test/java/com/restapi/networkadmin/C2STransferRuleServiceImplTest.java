package com.restapi.networkadmin;

import static org.mockito.Mockito.mock;

import com.btsl.security.CustomResponseWrapper;
import com.restapi.networkadmin.requestVO.ChannelTransferDeleteRequestVO;

import java.sql.Connection;
import java.util.ArrayList;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.util.JUnitConfig;
import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {C2STransferRuleServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class C2STransferRuleServiceImplTest {
    @Autowired
    private C2STransferRuleServiceImpl c2STransferRuleServiceImpl;

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#viewC2SList(Connection, String, String, String, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewC2SList() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.viewC2SList(C2STransferRuleServiceImpl.java:49)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        c2STransferRuleServiceImpl.viewC2SList (com.btsl.util.JUnitConfig.getConnection(), "Login ID", "Domain Code", "Category Code", "Grade Code",
                "Status Code", "Gateway Code", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#viewC2SDropdownList(Connection, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewC2SDropdownList() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.viewC2SDropdownList(C2STransferRuleServiceImpl.java:116)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        c2STransferRuleServiceImpl.viewC2SDropdownList (com.btsl.util.JUnitConfig.getConnection(), "Login ID", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#addTransfer(Connection, String, C2STransferRuleRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddTransfer() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.addTransfer(C2STransferRuleServiceImpl.java:213)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        C2STransferRuleRequestVO requestVO = new C2STransferRuleRequestVO();
        requestVO.setTransferList(new ArrayList<>());
        c2STransferRuleServiceImpl.addTransfer(JUnitConfig.getConnection(), "Login ID", requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#addTransfer(Connection, String, C2STransferRuleRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddTransfer2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.addTransfer(C2STransferRuleServiceImpl.java:213)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        C2STransferRuleRequestVO requestVO = new C2STransferRuleRequestVO();
        requestVO.setTransferList(new ArrayList<>());
        c2STransferRuleServiceImpl.addTransfer(JUnitConfig.getConnection(), "Login ID", requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#modifyTransfer(Connection, String, ChannelTransferModifyRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testModifyTransfer() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.modifyTransfer(C2STransferRuleServiceImpl.java:328)
        //   See https://diff.blue/R013 to resolve this issue.

        //Connection con = mock(Connection.class);

        ChannelTransferModifyRequestVO requestVO = new ChannelTransferModifyRequestVO();
        requestVO.setTransferList(new ArrayList<>());
        c2STransferRuleServiceImpl.modifyTransfer(JUnitConfig.getConnection(), "Login ID", requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#modifyTransfer(Connection, String, ChannelTransferModifyRequestVO, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testModifyTransfer2() {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.modifyTransfer(C2STransferRuleServiceImpl.java:328)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        ChannelTransferModifyRequestVO requestVO = new ChannelTransferModifyRequestVO();
        requestVO.setTransferList(new ArrayList<>());
        c2STransferRuleServiceImpl.modifyTransfer(JUnitConfig.getConnection(), "Login ID", requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#deleteTransfer(Connection, String, ChannelTransferDeleteRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteTransfer() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.deleteTransfer(C2STransferRuleServiceImpl.java:420)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ChannelTransferDeleteRequestVO requestVO = new ChannelTransferDeleteRequestVO();
        requestVO.setTransferList(new ArrayList<>());
        c2STransferRuleServiceImpl.deleteTransfer (com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link C2STransferRuleServiceImpl#deleteTransfer(Connection, String, ChannelTransferDeleteRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteTransfer2() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.networkadmin.C2STransferRuleServiceImpl.deleteTransfer(C2STransferRuleServiceImpl.java:420)
        //   See https://diff.blue/R013 to resolve this issue.

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        ChannelTransferDeleteRequestVO requestVO = new ChannelTransferDeleteRequestVO();
        requestVO.setTransferList(new ArrayList<>());
        c2STransferRuleServiceImpl.deleteTransfer (com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO, new CustomResponseWrapper(new Response()));
    }
}

