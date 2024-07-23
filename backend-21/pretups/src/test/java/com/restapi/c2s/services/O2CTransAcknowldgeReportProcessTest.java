package com.restapi.c2s.services;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAcknowledgeResp;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfAckDownloadReqDTO;
import com.btsl.util.JUnitConfig;
import com.btsl.util.OracleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {O2CTransAcknowldgeReportProcess.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CTransAcknowldgeReportProcessTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private O2CTransAcknowldgeReportProcess o2CTransAcknowldgeReportProcess;

    /**
     * Method under test: {@link O2CTransAcknowldgeReportProcess#getO2CTransAckReportSearch(O2CTransfAckDownloadReqDTO, GetO2CTransferAcknowledgeResp)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetO2CTransAckReportSearch() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init(); //Auto replace

     /*   Connection con = com.btsl.util.JUnitConfig.getConnection() ;
        try (MockedConstruction<MComConnection> mComm = Mockito.mockConstruction(MComConnection.class,
                (mock, context) -> {
                    // further stubbings ...
                    when(mock.getConnection()).thenReturn(JUnitConfig.getConnection());
                })) {
            MComConnection MComm = new MComConnection();
            mockStatic(OracleUtil.class);
          //  JUnitConfig.initConnections();
            when(OracleUtil.getConnection()).thenReturn(JUnitConfig.getConnection());


        }

     */   //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.O2CTransAcknowldgeReportProcess.getO2CTransAckReportSearch(O2CTransAcknowldgeReportProcess.java:82)

        // Arrange
        // TODO: Populate arranged inputs
        O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO = new O2CTransfAckDownloadReqDTO();
        getO2CTransfAcknReqVO.setDistributionType("TEST");
        getO2CTransfAcknReqVO.setFileType("XLS");
        getO2CTransfAcknReqVO.setFileName("Test");
        getO2CTransfAcknReqVO.setTransactionID("123");
        getO2CTransfAcknReqVO.setExtnwcode("1234");
        GetO2CTransferAcknowledgeResp response = Mockito.mock(GetO2CTransferAcknowledgeResp.class);

        Mockito.doNothing().when(response).setStatus(Mockito.anyString());
        // Act
        this.o2CTransAcknowldgeReportProcess.getO2CTransAckReportSearch(getO2CTransfAcknReqVO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2CTransAcknowldgeReportProcess#validateInputs(Connection, O2CTransfAckDownloadReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException {
        Connection con = mock(Connection.class);

        O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO = new O2CTransfAckDownloadReqDTO();
        getO2CTransfAcknReqVO.setDispHeaderColumnList(new ArrayList<>());
        getO2CTransfAcknReqVO.setDistributionType("Distribution Type");
        getO2CTransfAcknReqVO.setExtnwcode("Extnwcode");
        getO2CTransfAcknReqVO.setFileName("foo.txt");
        getO2CTransfAcknReqVO.setFileType("File Type");
        getO2CTransfAcknReqVO.setFromDate("2020-03-01");
        getO2CTransfAcknReqVO.setLocale(Locale.getDefault());
        getO2CTransfAcknReqVO.setMsisdn("Msisdn");
        getO2CTransfAcknReqVO.setOffline(true);
        getO2CTransfAcknReqVO.setOfflineReportTaskID("Offline Report Task ID");
        getO2CTransfAcknReqVO.setProductCode("Product Code");
        getO2CTransfAcknReqVO.setToDate("2020-03-01");
        getO2CTransfAcknReqVO.setTransactionID("Transaction ID");
        getO2CTransfAcknReqVO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransAcknowldgeReportProcess.validateInputs (com.btsl.util.JUnitConfig.getConnection(), getO2CTransfAcknReqVO);
    }

    /**
     * Method under test: {@link O2CTransAcknowldgeReportProcess#validateInputs(Connection, O2CTransfAckDownloadReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException {
        Connection con = mock(Connection.class);
        O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO = mock(O2CTransfAckDownloadReqDTO.class);
        when(getO2CTransfAcknReqVO.getDistributionType()).thenReturn("Distribution Type");
        doNothing().when(getO2CTransfAcknReqVO).setExtnwcode(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setFromDate(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setLocale(Mockito.<Locale>any());
        doNothing().when(getO2CTransfAcknReqVO).setMsisdn(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setProductCode(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setToDate(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setUserId(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(getO2CTransfAcknReqVO).setFileName(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setFileType(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setOffline(anyBoolean());
        doNothing().when(getO2CTransfAcknReqVO).setOfflineReportTaskID(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setDistributionType(Mockito.<String>any());
        doNothing().when(getO2CTransfAcknReqVO).setTransactionID(Mockito.<String>any());
        getO2CTransfAcknReqVO.setDispHeaderColumnList(new ArrayList<>());
        getO2CTransfAcknReqVO.setDistributionType("Distribution Type");
        getO2CTransfAcknReqVO.setExtnwcode("Extnwcode");
        getO2CTransfAcknReqVO.setFileName("foo.txt");
        getO2CTransfAcknReqVO.setFileType("File Type");
        getO2CTransfAcknReqVO.setFromDate("2020-03-01");
        getO2CTransfAcknReqVO.setLocale(Locale.getDefault());
        getO2CTransfAcknReqVO.setMsisdn("Msisdn");
        getO2CTransfAcknReqVO.setOffline(true);
        getO2CTransfAcknReqVO.setOfflineReportTaskID("Offline Report Task ID");
        getO2CTransfAcknReqVO.setProductCode("Product Code");
        getO2CTransfAcknReqVO.setToDate("2020-03-01");
        getO2CTransfAcknReqVO.setTransactionID("Transaction ID");
        getO2CTransfAcknReqVO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransAcknowldgeReportProcess.validateInputs (com.btsl.util.JUnitConfig.getConnection(), getO2CTransfAcknReqVO);
        verify(getO2CTransfAcknReqVO, atLeast(1)).getDistributionType();
        verify(getO2CTransfAcknReqVO).setExtnwcode(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setFromDate(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setLocale(Mockito.<Locale>any());
        verify(getO2CTransfAcknReqVO).setMsisdn(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setProductCode(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setToDate(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setUserId(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(getO2CTransfAcknReqVO).setFileName(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setFileType(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setOffline(anyBoolean());
        verify(getO2CTransfAcknReqVO).setOfflineReportTaskID(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setDistributionType(Mockito.<String>any());
        verify(getO2CTransfAcknReqVO).setTransactionID(Mockito.<String>any());
    }

    /**
     * Method under test: {@link O2CTransAcknowldgeReportProcess#execute(O2CTransfAckDownloadReqDTO, GetO2CTransferAckDownloadResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testExecute() throws BTSLBaseException {
        com.btsl.util.JUnitConfig.init();
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.c2s.services.O2CTransAcknowldgeReportProcess.execute(O2CTransAcknowldgeReportProcess.java:526)

        // Arrange
        // TODO: Populate arranged inputs
        O2CTransfAckDownloadReqDTO o2CTransfAckDownloadReqDTO = null;
        GetO2CTransferAckDownloadResp response = null;

        // Act
        this.o2CTransAcknowldgeReportProcess.execute(o2CTransfAckDownloadReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }
}

