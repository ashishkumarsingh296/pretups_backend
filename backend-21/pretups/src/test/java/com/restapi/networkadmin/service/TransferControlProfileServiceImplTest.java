package com.restapi.networkadmin.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.XssWrapper;
import com.btsl.db.util.MComConnection;
import com.btsl.pretups.filters.OwnHttpRequestWrapper;
import com.btsl.util.JUnitConfig;
import com.restapi.networkadmin.requestVO.DeleteTransferProfileDataReqVO;
import com.restapi.networkadmin.requestVO.ModifyTransferProfileDataCloneReqVO;
import com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO;
import com.restapi.networkadmin.responseVO.DeleteTransferProfileRespVO;
import com.restapi.networkadmin.responseVO.ModifyTransferProfileRespVO;
import com.restapi.networkadmin.responseVO.SaveTransferProfileRespVO;
import com.restapi.superadmin.requestVO.TransferProfileLoadReqVO;
import com.restapi.superadmin.requestVO.TransferProfileSearchReqVO;
import com.restapi.superadmin.responseVO.TransferProfileLoadRespVO;
import com.restapi.superadmin.responseVO.TransferProfileSearchRespVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {TransferControlProfileServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class TransferControlProfileServiceImplTest {
    @Autowired
    private TransferControlProfileServiceImpl transferControlProfileServiceImpl;

    /**
     * Method under test: {@link TransferControlProfileServiceImpl#searchTransferProfileList(TransferProfileSearchReqVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testSearchTransferProfileList() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.TransferControlProfileServiceImpl.searchTransferProfileList(TransferControlProfileServiceImpl.java:68)

        // Arrange
        // TODO: Populate arranged inputs
        TransferProfileSearchReqVO request = null;
        String msisdn = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        TransferProfileSearchRespVO actualSearchTransferProfileListResult = this.transferControlProfileServiceImpl
                .searchTransferProfileList(request, msisdn, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link TransferControlProfileServiceImpl#loadTransferProfilebyCat(TransferProfileLoadReqVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testLoadTransferProfilebyCat() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.TransferControlProfileServiceImpl.loadTransferProfilebyCat(TransferControlProfileServiceImpl.java:126)

        // Arrange
        // TODO: Populate arranged inputs
        TransferProfileLoadReqVO request = null;
        String msisdn = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        TransferProfileLoadRespVO actualLoadTransferProfilebyCatResult = this.transferControlProfileServiceImpl
                .loadTransferProfilebyCat(request, msisdn, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link TransferControlProfileServiceImpl#saveTransferControlProfile(SaveTransferProfileDataCloneReqVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testSaveTransferControlProfile() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.TransferControlProfileServiceImpl.saveTransferControlProfile(TransferControlProfileServiceImpl.java:363)

        // Arrange
        // TODO: Populate arranged inputs
        SaveTransferProfileDataCloneReqVO request = null;
        String msisdn = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        SaveTransferProfileRespVO actualSaveTransferControlProfileResult = this.transferControlProfileServiceImpl
                .saveTransferControlProfile(request, msisdn, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    private static void initConnections() {
        try {
            ResultSet resultSet = mock(ResultSet.class);
            //PreparedStatement psmtInsert = mock(PreparedStatement.class);
            //when(psmtInsert.executeUpdate()).thenReturn(1);


            when(resultSet.getInt(Mockito.<String>any())).thenReturn(1);
            Timestamp ts = Timestamp.from(Instant.now());
            when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(ts);

            java.util.Date date = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            when(resultSet.getDate(Mockito.<String>any())).thenReturn(sqlDate);


            when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
            when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
            //when(resultSet.getTimestamp(Mockito.<String>any())).thenReturn(timestamp);

            when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false)
                    .thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);



            doNothing().when(resultSet).close();
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(preparedStatement.executeUpdate()).thenReturn(1);//TODO: Aded
            doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
            doNothing().when(preparedStatement).close();
            //Connection con = mock(Connection.class);
         Connection   con = mock(Connection.class);
            when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

            MComConnection mcomCon = mock(MComConnection.class);

            when(mcomCon.getConnection()).thenReturn(con);

        } catch (Exception e) {
        }
    }


    /**
     * Method under test: {@link TransferControlProfileServiceImpl#modifyTransferControlProfile(ModifyTransferProfileDataCloneReqVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testModifyTransferControlProfile() throws BTSLBaseException {


        //JUnitConfig.initConnections();
        JUnitConfig.init();

        ModifyTransferProfileDataCloneReqVO request = new ModifyTransferProfileDataCloneReqVO();


        String msisdn = "";
        HttpServletRequest httpServletRequest2 = null;
        XssWrapper httpServletRequest = new XssWrapper(new OwnHttpRequestWrapper(new MockHttpServletRequest()));


        HttpServletResponse responseSwag1 = null;
        HttpServletResponse responseSwag = Mockito.mock(HttpServletResponse.class);
        doNothing().when(responseSwag).setStatus(Mockito.anyInt());



        Locale locale = null;


        // Act
        ModifyTransferProfileRespVO actualModifyTransferControlProfileResult = this.transferControlProfileServiceImpl
                .modifyTransferControlProfile(request, msisdn, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link TransferControlProfileServiceImpl#deleteTransferControlProfile(DeleteTransferProfileDataReqVO, String, HttpServletRequest, HttpServletResponse, Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testDeleteTransferControlProfile() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.networkadmin.service.TransferControlProfileServiceImpl.deleteTransferControlProfile(TransferControlProfileServiceImpl.java:921)

        // Arrange
        // TODO: Populate arranged inputs
        DeleteTransferProfileDataReqVO request = null;
        String msisdn = "";
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse responseSwag = null;
        Locale locale = null;

        // Act
        DeleteTransferProfileRespVO actualDeleteTransferControlProfileResult = this.transferControlProfileServiceImpl
                .deleteTransferControlProfile(request, msisdn, httpServletRequest, responseSwag, locale);

        // Assert
        // TODO: Add assertions on result
    }
}

