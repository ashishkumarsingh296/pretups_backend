package com.restapi.superadmin.serviceI;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.security.CustomResponseWrapper;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {InfoForEditOperatorServiceI.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class InfoForEditOperatorServiceITest {
    @Autowired
    private InfoForEditOperatorServiceI infoForEditOperatorServiceI;

    /**
     * Method under test: {@link InfoForEditOperatorServiceI#assignList(Connection, String, HttpServletResponse, String, String, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAssignList() throws BTSLBaseException, SQLException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.serviceI.InfoForEditOperatorServiceI.assignList(InfoForEditOperatorServiceI.java:160)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        infoForEditOperatorServiceI.assignList(con, "42", new CustomResponseWrapper(new Response()), "Category Code",
                "42", "42");
    }

    /**
     * Method under test: {@link InfoForEditOperatorServiceI#assignGeography(Connection, String, HttpServletResponse, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAssignGeography() throws BTSLBaseException, SQLException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.serviceI.InfoForEditOperatorServiceI.assignGeography(InfoForEditOperatorServiceI.java:251)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        infoForEditOperatorServiceI.assignGeography(con, "42", new CustomResponseWrapper(new Response()),
                "Category Code");
    }

    /**
     * Method under test: {@link InfoForEditOperatorServiceI#getSMSCInfo(Connection, String, HttpServletResponse, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetSMSCInfo() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.serviceI.InfoForEditOperatorServiceI.getSMSCInfo(InfoForEditOperatorServiceI.java:401)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        infoForEditOperatorServiceI.getSMSCInfo(com.btsl.util.JUnitConfig.getConnection(), "42", new CustomResponseWrapper(new Response()), "Category Code");
    }

    /**
     * Method under test: {@link InfoForEditOperatorServiceI#getDepartement(Connection, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetDepartement() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.serviceI.InfoForEditOperatorServiceI.getDepartement(InfoForEditOperatorServiceI.java:457)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        infoForEditOperatorServiceI.getDepartement(com.btsl.util.JUnitConfig.getConnection(), new CustomResponseWrapper(new Response()));
    }
}

