package com.restapi.reporting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.CommonReportRequest;
import com.btsl.user.businesslogic.Param;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Ignore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class ReportServiceImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ReportServiceImpl#fetchCommonReport(Connection, CommonReportRequest)}
     */
    @Test
    public void testFetchCommonReport() throws BTSLBaseException, SQLException {
        ReportServiceImpl reportServiceImpl = new ReportServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CommonReportRequest request = new CommonReportRequest();
        request.setActivePanelId("42");
        request.setDownload("Download");
        request.setFileType("File Type");
        request.setParams(new ArrayList<>());
        request.setReport_template("Report template");
        thrown.expect(BTSLBaseException.class);
        reportServiceImpl.fetchCommonReport(p_con, request);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ReportServiceImpl#fetchCommonReport(Connection, CommonReportRequest)}
     */
    @Test
    public void testFetchCommonReport2() throws BTSLBaseException, SQLException {
        ReportServiceImpl reportServiceImpl = new ReportServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CommonReportRequest request = new CommonReportRequest();
        request.setActivePanelId("42");
        request.setDownload("Download");
        request.setFileType("File Type");
        request.setParams(new ArrayList<>());
        request.setReport_template("Report template");
        thrown.expect(BTSLBaseException.class);
        reportServiceImpl.fetchCommonReport(p_con, request);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ReportServiceImpl#fetchCommonReport(Connection, CommonReportRequest)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testFetchCommonReport3() throws BTSLBaseException, SQLException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.btsl.common.BTSLBaseException: error.general.processing
        //       at com.restapi.reporting.ReportServiceImpl.fetchCommonReport(ReportServiceImpl.java:264)
        //   java.lang.NullPointerException
        //       at java.util.Hashtable.put(Hashtable.java:461)
        //       at com.restapi.reporting.ReportDataSourceConnectionUtil.getConnection(ReportDataSourceConnectionUtil.java:32)
        //       at com.restapi.reporting.ReportServiceImpl.fetchCommonReport(ReportServiceImpl.java:128)
        //   See https://diff.blue/R013 to resolve this issue.

        ReportServiceImpl reportServiceImpl = new ReportServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn(null);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CommonReportRequest request = new CommonReportRequest();
        request.setActivePanelId("42");
        request.setDownload("Download");
        request.setFileType("File Type");
        request.setParams(new ArrayList<>());
        request.setReport_template("Report template");
        reportServiceImpl.fetchCommonReport(p_con, request);
    }

    /**
     * Method under test: {@link ReportServiceImpl#fetchCommonReport(Connection, CommonReportRequest)}
     */
    @Test
    public void testFetchCommonReport4() throws BTSLBaseException, SQLException {
        ReportServiceImpl reportServiceImpl = new ReportServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        CommonReportRequest request = new CommonReportRequest();
        request.setActivePanelId("42");
        request.setDownload("Download");
        request.setFileType("File Type");
        request.setParams(new ArrayList<>());
        request.setReport_template("Report template");
        assertEquals("", reportServiceImpl.fetchCommonReport(p_con, request));
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ReportServiceImpl#fetchCommonReport(Connection, CommonReportRequest)}
     */
    @Test
    public void testFetchCommonReport5() throws BTSLBaseException, SQLException {
        ReportServiceImpl reportServiceImpl = new ReportServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        Param param = new Param();
        param.setParam("fetchCommonReport");
        param.setType("fetchCommonReport");
        param.setValue("42");

        ArrayList<Param> params = new ArrayList<>();
        params.add(param);

        CommonReportRequest request = new CommonReportRequest();
        request.setActivePanelId("42");
        request.setDownload("Download");
        request.setFileType("File Type");
        request.setParams(params);
        request.setReport_template("Report template");
        thrown.expect(BTSLBaseException.class);
        reportServiceImpl.fetchCommonReport(p_con, request);
        verify(p_con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet).close();
    }

    /**
     * Method under test: {@link ReportServiceImpl#fetchCommonReport(Connection, CommonReportRequest)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testFetchCommonReport6() throws BTSLBaseException, SQLException {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.ArrayIndexOutOfBoundsException: 0
        //       at com.restapi.reporting.ReportServiceImpl.fetchCommonReport(ReportServiceImpl.java:46)
        //   See https://diff.blue/R013 to resolve this issue.

        ReportServiceImpl reportServiceImpl = new ReportServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        Connection p_con = mock(Connection.class);
        when(p_con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        Param param = new Param();
        param.setParam("fetchCommonReport");
        param.setType("fetchCommonReport");
        param.setValue("42");

        Param param2 = new Param();
        param2.setParam("-");
        param2.setType("-");
        param2.setValue("fetchCommonReport");

        ArrayList<Param> params = new ArrayList<>();
        params.add(param2);
        params.add(param);

        CommonReportRequest request = new CommonReportRequest();
        request.setActivePanelId("42");
        request.setDownload("Download");
        request.setFileType("File Type");
        request.setParams(params);
        request.setReport_template("Report template");
        reportServiceImpl.fetchCommonReport(p_con, request);
    }
}

