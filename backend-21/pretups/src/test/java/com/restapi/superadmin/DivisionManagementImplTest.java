package com.restapi.superadmin;

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

@ContextConfiguration(classes = {DivisionManagementImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class DivisionManagementImplTest {
    @Autowired
    private DivisionManagementImpl divisionManagementImpl;

    /**
     * Method under test: {@link DivisionManagementImpl#viewDivisionList(Connection, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewDivisionList() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.viewDivisionList(DivisionManagementImpl.java:47)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.viewDivisionList (com.btsl.util.JUnitConfig.getConnection(), "42", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#ModifyDivisionAdmin(Connection, String, ModifyDivisionRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testModifyDivisionAdmin() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.ModifyDivisionAdmin(DivisionManagementImpl.java:117)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ModifyDivisionRequestVO requestVO = new ModifyDivisionRequestVO();
        requestVO.setDivDeptId("42");
        requestVO.setDivDeptName("Div Dept Name");
        requestVO.setDivDeptShortCode("Div Dept Short Code");
        requestVO.setDivDeptType("Div Dept Type");
        requestVO.setParentId("42");
        requestVO.setStatus("Status");
        requestVO.setStatusName("Status Name");
        divisionManagementImpl.ModifyDivisionAdmin (com.btsl.util.JUnitConfig.getConnection(), "42", requestVO, new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#AddDivision(Connection, String, String, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddDivision() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.AddDivision(DivisionManagementImpl.java:204)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.AddDivision (com.btsl.util.JUnitConfig.getConnection(), "42", "Div Dept Name", "Div Dept Short Code", "Div Dept Type", "Status",
                new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#viewDivDepList(Connection, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testViewDivDepList() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.viewDivDepList(DivisionManagementImpl.java:306)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.viewDivDepList (com.btsl.util.JUnitConfig.getConnection(), new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#deleteDivision(Connection, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteDivision() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.deleteDivision(DivisionManagementImpl.java:366)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.deleteDivision (com.btsl.util.JUnitConfig.getConnection(), "Login ID", "42", "42", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#getCategoryList(Connection, HttpServletResponse, String)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetCategoryList() throws BTSLBaseException, SQLException {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.getCategoryList(DivisionManagementImpl.java:441)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.getCategoryList (com.btsl.util.JUnitConfig.getConnection(), new CustomResponseWrapper(new Response()), "Category Code");
    }

    /**
     * Method under test: {@link DivisionManagementImpl#departmentListAdmin(Connection, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDepartmentListAdmin() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.departmentListAdmin(DivisionManagementImpl.java:557)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.departmentListAdmin (com.btsl.util.JUnitConfig.getConnection(), "Login ID", "42", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#deleteDepartment(Connection, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDeleteDepartment() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.deleteDepartment(DivisionManagementImpl.java:620)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.deleteDepartment (com.btsl.util.JUnitConfig.getConnection(), "Login ID", "42", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#AddDepartment(Connection, String, String, String, String, String, String, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testAddDepartment() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.AddDepartment(DivisionManagementImpl.java:694)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);
        divisionManagementImpl.AddDepartment (com.btsl.util.JUnitConfig.getConnection(), "Login ID", "42", "Div Dept Name", "Div Dept Short Code",
                "Div Dept Type", "Status", new CustomResponseWrapper(new Response()));
    }

    /**
     * Method under test: {@link DivisionManagementImpl#ModifyDepartmentAdmin(Connection, String, ModifyDepartmentRequestVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testModifyDepartmentAdmin() {
      com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.superadmin.DivisionManagementImpl.ModifyDepartmentAdmin(DivisionManagementImpl.java:796)
        //   See https://diff.blue/R013 to resolve this issue.

        Connection con = mock(Connection.class);

        ModifyDepartmentRequestVO requestVO = new ModifyDepartmentRequestVO();
        requestVO.setDivDeptId("42");
        requestVO.setDivDeptName("Div Dept Name");
        requestVO.setDivDeptShortCode("Div Dept Short Code");
        requestVO.setDivDeptType("Div Dept Type");
        requestVO.setParentId("42");
        requestVO.setStatus("Status");
        divisionManagementImpl.ModifyDepartmentAdmin (com.btsl.util.JUnitConfig.getConnection(), "Login ID", requestVO,
                new CustomResponseWrapper(new Response()));
    }
}

