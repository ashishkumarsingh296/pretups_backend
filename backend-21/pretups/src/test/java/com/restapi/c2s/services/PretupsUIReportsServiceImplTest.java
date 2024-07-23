package com.restapi.c2s.services;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.EventProcessorFactory;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVersionVO;
import com.btsl.pretups.channel.transfer.businesslogic.AddtlnCommSummryDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommisionResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetCommissionSlabReqVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetCommissionSlabResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAcknowledgeResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetParentOwnerProfileReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfAckDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
import com.btsl.pretups.channel.transfer.requesthandler.GetParentOwnerProfileRespVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PretupsUIReportsServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PretupsUIReportsServiceImplTest {
    @MockBean(name = "AddtnlCommSummryReportProcess")
    private AddtnlCommSummryReportProcess addtnlCommSummryReportProcess;

    @MockBean(name = "BulkUserAddRptProcess")
    private BulkUserAddRptProcess bulkUserAddRptProcess;

    @MockBean(name = "C2CTransferCommReportProcessor")
    private C2CTransferCommReportProcessor c2CTransferCommReportProcessor;

    @MockBean(name = "C2STransferCommReportProcessor")
    private C2STransferCommReportProcessor c2STransferCommReportProcessor;

    @MockBean
    private EventProcessorFactory eventProcessorFactory;

    @MockBean(name = "O2CTransAcknowldgeReportProcess")
    private O2CTransAcknowldgeReportProcess o2CTransAcknowldgeReportProcess;

    @MockBean(name = "O2CTransfDetailReportProcess")
    private O2CTransfDetailReportProcess o2CTransfDetailReportProcess;

    @MockBean(name = "PassbookOthersProcessor")
    private PassbookOthersProcessor passbookOthersProcessor;

    @MockBean(name = "PinpassHistServiceProcessor")
    private PinpassHistServiceProcessor pinpassHistServiceProcessor;

    @Autowired
    private PretupsUIReportsServiceImpl pretupsUIReportsServiceImpl;

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#downloadPinPassHistData(PinPassHistoryReqDTO, PinPassHistDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadPinPassHistData() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.downloadPinPassHistData(PretupsUIReportsServiceImpl.java:354)

        // Arrange
        // TODO: Populate arranged inputs
        PinPassHistoryReqDTO pinPassHistoryReqDTO = null;
        PinPassHistDownloadResp response = null;

        // Act
        this.pretupsUIReportsServiceImpl.downloadPinPassHistData(pinPassHistoryReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#getC2StransferCommissionInfo(C2STransferCommReqDTO, C2StransferCommisionResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetC2StransferCommissionInfo() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.getC2StransferCommissionInfo(PretupsUIReportsServiceImpl.java:416)

        // Arrange
        // TODO: Populate arranged inputs
        C2STransferCommReqDTO c2STransferCommReqDTO = null;
        C2StransferCommisionResp response = null;

        // Act
        this.pretupsUIReportsServiceImpl.getC2StransferCommissionInfo(c2STransferCommReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#downloadC2StransferCommData(C2STransferCommReqDTO, C2STransferCommDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadC2StransferCommData() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.downloadC2StransferCommData(PretupsUIReportsServiceImpl.java:486)

        // Arrange
        // TODO: Populate arranged inputs
        C2STransferCommReqDTO c2sTransferCommReqDTO = null;
        C2STransferCommDownloadResp response = null;

        // Act
        Map<String, String> actualDownloadC2StransferCommDataResult = this.pretupsUIReportsServiceImpl
                .downloadC2StransferCommData(c2sTransferCommReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#precheckOfflineValidations(Connection, String, String)}
     */
    @Test
    public void testPrecheckOfflineValidations() throws BTSLBaseException {
        // Arrange
        // TODO: Populate arranged inputs
        //Connection con = null;
        JUnitConfig.init();
        String loggedInUserID = "TEST";
        String reportID = "TEST";

        // Act
        this.pretupsUIReportsServiceImpl.precheckOfflineValidations(JUnitConfig.getConnection(), loggedInUserID, reportID);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#downloadC2CtransferCommData(C2CTransferCommReqDTO, C2CTransferCommDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadC2CtransferCommData() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.downloadC2CtransferCommData(PretupsUIReportsServiceImpl.java:719)

        // Arrange
        // TODO: Populate arranged inputs
        C2CTransferCommReqDTO c2cTransferCommReqDTO = null;
        C2CTransferCommDownloadResp response = null;

        // Act
        Map<String, String> actualDownloadC2CtransferCommDataResult = this.pretupsUIReportsServiceImpl
                .downloadC2CtransferCommData(c2cTransferCommReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#getParentOwnerProfileInfo(GetParentOwnerProfileReq, GetParentOwnerProfileRespVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetParentOwnerProfileInfo() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.getParentOwnerProfileInfo(PretupsUIReportsServiceImpl.java:808)

        // Arrange
        // TODO: Populate arranged inputs
        GetParentOwnerProfileReq getParentOwnerProfileReq = null;
        GetParentOwnerProfileRespVO response = null;

        // Act
        this.pretupsUIReportsServiceImpl.getParentOwnerProfileInfo(getParentOwnerProfileReq, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#getCommissionSlabDetails(GetCommissionSlabReqVO, GetCommissionSlabResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetCommissionSlabDetails() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.getCommissionSlabDetails(PretupsUIReportsServiceImpl.java:904)

        // Arrange
        // TODO: Populate arranged inputs
        GetCommissionSlabReqVO getCommissionSlabReqVO = null;
        GetCommissionSlabResp response = null;

        // Act
        this.pretupsUIReportsServiceImpl.getCommissionSlabDetails(getCommissionSlabReqVO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#setcommissionSlabSectionUI(Connection, GetCommissionSlabReqVO, CommissionProfileSetVO, CommissionProfileSetVersionVO, GetCommissionSlabResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSetcommissionSlabSectionUI() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.util.PretupsBL.getDisplayAmount(PretupsBL.java:785)
        //       at com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO.getTransferMultipleOffAsString(CommissionProfileProductsVO.java:245)
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.setcommissionSlabSectionUI(PretupsUIReportsServiceImpl.java:1005)
        //   See https://diff.blue/R013 to resolve this issue.

        PretupsUIReportsServiceImpl pretupsUIReportsServiceImpl = new PretupsUIReportsServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDouble(Mockito.<String>any())).thenReturn(10.0d);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        GetCommissionSlabReqVO getCommissionSlabReqVO = mock(GetCommissionSlabReqVO.class);
        CommissionProfileSetVO commissionProfileSetVO = mock(CommissionProfileSetVO.class);
        when(commissionProfileSetVO.getCommProfileSetId()).thenReturn("42");
        CommissionProfileSetVersionVO commissionProfileSetVersionVO = mock(CommissionProfileSetVersionVO.class);
        when(commissionProfileSetVersionVO.getCommProfileSetVersion()).thenReturn("1.0.2");
        pretupsUIReportsServiceImpl.setcommissionSlabSectionUI(JUnitConfig.getConnection(), getCommissionSlabReqVO, commissionProfileSetVO,
                commissionProfileSetVersionVO, mock(GetCommissionSlabResp.class));
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#setcommissionSlabSectionUI(Connection, GetCommissionSlabReqVO, CommissionProfileSetVO, CommissionProfileSetVersionVO, GetCommissionSlabResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSetcommissionSlabSectionUI2() throws BTSLBaseException, SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.btsl.pretups.util.PretupsBL.getDisplayAmount(PretupsBL.java:785)
        //       at com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO.getTransferMultipleOffAsString(CommissionProfileProductsVO.java:245)
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.setcommissionSlabSectionUI(PretupsUIReportsServiceImpl.java:1005)
        //   See https://diff.blue/R013 to resolve this issue.

        PretupsUIReportsServiceImpl pretupsUIReportsServiceImpl = new PretupsUIReportsServiceImpl();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDouble(Mockito.<String>any())).thenReturn(10.0d);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();

        JUnitConfig.init();
        //Connection con = mock(Connection.class);
     //   when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        GetCommissionSlabReqVO getCommissionSlabReqVO = mock(GetCommissionSlabReqVO.class);
        CommissionProfileSetVO commissionProfileSetVO = mock(CommissionProfileSetVO.class);
        when(commissionProfileSetVO.getCommProfileSetId()).thenReturn("42");
        CommissionProfileSetVersionVO commissionProfileSetVersionVO = mock(CommissionProfileSetVersionVO.class);
        when(commissionProfileSetVersionVO.getCommProfileSetVersion()).thenReturn("1.0.2");
        pretupsUIReportsServiceImpl.setcommissionSlabSectionUI(JUnitConfig.getConnection(), getCommissionSlabReqVO, commissionProfileSetVO,
                commissionProfileSetVersionVO, mock(GetCommissionSlabResp.class));
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#setCBCSlabSectionUI(Connection, GetCommissionSlabReqVO, CommissionProfileSetVO, CommissionProfileSetVersionVO, GetCommissionSlabResp)}
     */
    @Test
    public void testSetCBCSlabSectionUI() throws BTSLBaseException, SQLException {
        /*java.sql.Date date = mock(java.sql.Date.class);
        when(date.getTime()).thenReturn(10L);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.<String>any())).thenReturn("String");
        when(resultSet.getDate(Mockito.<String>any())).thenReturn(date);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();
        */
        //Connection con = mock(Connection.class);
//        when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);
        JUnitConfig.init();
        GetCommissionSlabReqVO getCommissionSlabReqVO = new GetCommissionSlabReqVO();
        getCommissionSlabReqVO.setCategoryCode("Category Code");
        getCommissionSlabReqVO.setDomainCode("Domain Code");
        getCommissionSlabReqVO.setExtnwcode("Extnwcode");
        getCommissionSlabReqVO.setFromDate("2020-03-01");
        getCommissionSlabReqVO.setGeography("Geography");
        getCommissionSlabReqVO.setLocale(Locale.getDefault());
        getCommissionSlabReqVO.setLoggedInUserID("Logged In User ID");
        getCommissionSlabReqVO.setMsisdn("Msisdn");
        getCommissionSlabReqVO.setProductCode("Product Code");
        getCommissionSlabReqVO.setToDate("2020-03-01");
        getCommissionSlabReqVO.setUserId("42");
        CommissionProfileSetVO commissionProfileSetVO = CommissionProfileSetVO.getInstance();

        CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
        commissionProfileSetVersionVO.setApplicableFrom(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        commissionProfileSetVersionVO.setCommProfileSetId("42");
        commissionProfileSetVersionVO.setCommProfileSetVersion("1.0.2");
        commissionProfileSetVersionVO.setComm_set_name("Comm set name");
        commissionProfileSetVersionVO.setCommissionType(" commission Type");
        commissionProfileSetVersionVO.setCommissionTypeValue("42");
        commissionProfileSetVersionVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        commissionProfileSetVersionVO.setCreatedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        commissionProfileSetVersionVO.setCreated_on("Jan 1, 2020 8:00am GMT+0100");
        commissionProfileSetVersionVO.setDualCommissionType("Dual Commission Type");
        commissionProfileSetVersionVO.setDualCommissionTypeDesc("Dual Commission Type Desc");
        commissionProfileSetVersionVO.setGeoCode("Geo Code");
        commissionProfileSetVersionVO.setIsDefault("Is Default");
        commissionProfileSetVersionVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        commissionProfileSetVersionVO.setModifiedOn(
                java.util.Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        commissionProfileSetVersionVO.setOldApplicableFrom(1L);
        commissionProfileSetVersionVO.setOtherCommissionName(" other Commission Name");
        commissionProfileSetVersionVO.setOtherCommissionProfileSetID("Other Commission Profile Set ID");
        commissionProfileSetVersionVO.setRowId("42");
        commissionProfileSetVersionVO.setServicesAllowed("Services Allowed");
        commissionProfileSetVersionVO.setSource(" source");
        commissionProfileSetVersionVO.setStatus("Status");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        GetCommissionSlabResp response = new GetCommissionSlabResp();
        response.setApplicableFrom("jane.doe@example.org");
        response.setCommissionType("Commission Type");
        response.setErrorMap(errorMap);
        response.setListAdditionalCommSlabVO(new ArrayList<>());
        response.setListcBCcommSlabDetVO(new ArrayList<>());
        response.setListcommissionSlabDetVO(new ArrayList<>());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        pretupsUIReportsServiceImpl.setCBCSlabSectionUI(JUnitConfig.getConnection(), getCommissionSlabReqVO, commissionProfileSetVO,
                commissionProfileSetVersionVO, response);
       /* verify(JUnitConfig.getConnection(), atLeast(1)).prepareStatement(Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement, atLeast(1)).close();
        verify(resultSet, atLeast(1)).next();
        verify(resultSet, atLeast(1)).getString(Mockito.<String>any());
        verify(resultSet, atLeast(1)).getDate(Mockito.<String>any());
        verify(resultSet, atLeast(1)).close();
        verify(date, atLeast(1)).getTime();*/
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#setCBCSlabSectionUI(Connection, GetCommissionSlabReqVO, CommissionProfileSetVO, CommissionProfileSetVersionVO, GetCommissionSlabResp)}
     */
    @Test
    public void testSetCBCSlabSectionUI2() throws BTSLBaseException, SQLException {
        /*ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), Mockito.<String>any());
        doNothing().when(preparedStatement).close();*/
        //Connection con = mock(Connection.class);
      //  when(con.prepareStatement(Mockito.<String>any())).thenReturn(preparedStatement);

        JUnitConfig.init();
        GetCommissionSlabReqVO getCommissionSlabReqVO = new GetCommissionSlabReqVO();
        getCommissionSlabReqVO.setCategoryCode("Category Code");
        getCommissionSlabReqVO.setDomainCode("Domain Code");
        getCommissionSlabReqVO.setExtnwcode("Extnwcode");
        getCommissionSlabReqVO.setFromDate("2020-03-01");
        getCommissionSlabReqVO.setGeography("Geography");
        getCommissionSlabReqVO.setLocale(Locale.getDefault());
        getCommissionSlabReqVO.setLoggedInUserID("Logged In User ID");
        getCommissionSlabReqVO.setMsisdn("Msisdn");
        getCommissionSlabReqVO.setProductCode("Product Code");
        getCommissionSlabReqVO.setToDate("2020-03-01");
        getCommissionSlabReqVO.setUserId("42");
        CommissionProfileSetVO commissionProfileSetVO = CommissionProfileSetVO.getInstance();

        CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
        commissionProfileSetVersionVO
                .setApplicableFrom(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        commissionProfileSetVersionVO.setCommProfileSetId("42");
        commissionProfileSetVersionVO.setCommProfileSetVersion("1.0.2");
        commissionProfileSetVersionVO.setComm_set_name("Comm set name");
        commissionProfileSetVersionVO.setCommissionType(" commission Type");
        commissionProfileSetVersionVO.setCommissionTypeValue("42");
        commissionProfileSetVersionVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        commissionProfileSetVersionVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        commissionProfileSetVersionVO.setCreated_on("Jan 1, 2020 8:00am GMT+0100");
        commissionProfileSetVersionVO.setDualCommissionType("Dual Commission Type");
        commissionProfileSetVersionVO.setDualCommissionTypeDesc("Dual Commission Type Desc");
        commissionProfileSetVersionVO.setGeoCode("Geo Code");
        commissionProfileSetVersionVO.setIsDefault("Is Default");
        commissionProfileSetVersionVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        commissionProfileSetVersionVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        commissionProfileSetVersionVO.setOldApplicableFrom(1L);
        commissionProfileSetVersionVO.setOtherCommissionName(" other Commission Name");
        commissionProfileSetVersionVO.setOtherCommissionProfileSetID("Other Commission Profile Set ID");
        commissionProfileSetVersionVO.setRowId("42");
        commissionProfileSetVersionVO.setServicesAllowed("Services Allowed");
        commissionProfileSetVersionVO.setSource(" source");
        commissionProfileSetVersionVO.setStatus("Status");

        ErrorMap errorMap = new ErrorMap();
        errorMap.setMasterErrorList(new ArrayList<>());
        errorMap.setRowErrorMsgLists(new ArrayList<>());

        GetCommissionSlabResp response = new GetCommissionSlabResp();
        response.setApplicableFrom("jane.doe@example.org");
        response.setCommissionType("Commission Type");
        response.setErrorMap(errorMap);
        response.setListAdditionalCommSlabVO(new ArrayList<>());
        response.setListcBCcommSlabDetVO(new ArrayList<>());
        response.setListcommissionSlabDetVO(new ArrayList<>());
        response.setMessage("Not all who wander are lost");
        response.setMessageCode("Message Code");
        response.setReferenceId(1);
        response.setService("Service");
        response.setStatus("Status");
        response.setSuccessList(new ArrayList<>());
        pretupsUIReportsServiceImpl.setCBCSlabSectionUI(JUnitConfig.getConnection(), getCommissionSlabReqVO, commissionProfileSetVO,
                commissionProfileSetVersionVO, response);
       /* verify(con).prepareStatement(Mockito.<String>any());
        verify(preparedStatement).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(anyInt(), Mockito.<String>any());
        verify(preparedStatement).close();
        verify(resultSet).next();
        verify(resultSet).close();*/
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#getO2cTransferAcknowledgement(O2CTransfAckDownloadReqDTO, GetO2CTransferAcknowledgeResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetO2cTransferAcknowledgement() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.getO2cTransferAcknowledgement(PretupsUIReportsServiceImpl.java:1126)

        // Arrange
        // TODO: Populate arranged inputs
        O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO = null;
        GetO2CTransferAcknowledgeResp response = null;

        // Act
        this.pretupsUIReportsServiceImpl.getO2cTransferAcknowledgement(getO2CTransfAcknReqVO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#downloadO2CTransferAcknowlege(O2CTransfAckDownloadReqDTO, GetO2CTransferAckDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadO2CTransferAcknowlege() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.downloadO2CTransferAcknowlege(PretupsUIReportsServiceImpl.java:1187)

        // Arrange
        // TODO: Populate arranged inputs
        O2CTransfAckDownloadReqDTO o2cTransfAckDownloadReqDTO = null;
        GetO2CTransferAckDownloadResp response = null;

        // Act
        this.pretupsUIReportsServiceImpl.downloadO2CTransferAcknowlege(o2cTransfAckDownloadReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#downloadAddntlCommSummry(AddtnlCommSummryReqDTO, AddtlnCommSummryDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadAddntlCommSummry() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.downloadAddntlCommSummry(PretupsUIReportsServiceImpl.java:1415)

        // Arrange
        // TODO: Populate arranged inputs
        AddtnlCommSummryReqDTO addtnlCommSummryReqDTO = null;
        AddtlnCommSummryDownloadResp response = null;

        // Act
        this.pretupsUIReportsServiceImpl.downloadAddntlCommSummry(addtnlCommSummryReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PretupsUIReportsServiceImpl#downloadPassbookOthersData(PassbookOthersReqDTO, PassbookOthersDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testDownloadPassbookOthersData() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PretupsUIReportsServiceImpl.downloadPassbookOthersData(PretupsUIReportsServiceImpl.java:1660)

        // Arrange
        // TODO: Populate arranged inputs
        PassbookOthersReqDTO passbookOthersReqDTO = null;
        PassbookOthersDownloadResp passbookOthersDownloadResp = null;

        // Act
        this.pretupsUIReportsServiceImpl.downloadPassbookOthersData(passbookOthersReqDTO, passbookOthersDownloadResp);

        // Assert
        // TODO: Add assertions on result
    }
}

