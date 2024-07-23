package com.restapi.c2s.services;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailsReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetSearchResp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {O2CTransfDetailReportProcess.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CTransfDetailReportProcessTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private O2CTransfDetailReportProcess o2CTransfDetailReportProcess;

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#getO2CTransfDetailReportSearch(O2CTransferDetailsReqDTO, O2CtransferDetSearchResp)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testGetO2CTransfDetailReportSearch() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.O2CTransfDetailReportProcess.getO2CTransfDetailReportSearch(O2CTransfDetailReportProcess.java:98)

        // Arrange
        // TODO: Populate arranged inputs
        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = null;
        O2CtransferDetSearchResp response = null;

        // Act
        this.o2CTransfDetailReportProcess.getO2CTransfDetailReportSearch(o2CTransferDetailsReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#validateInputs(Connection, O2CTransferDetailsReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException {
        Connection con = mock(Connection.class);

        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = new O2CTransferDetailsReqDTO();
        o2CTransferDetailsReqDTO.setCategoryCode("Category Code");
        o2CTransferDetailsReqDTO.setDispHeaderColumnList(new ArrayList<>());
        o2CTransferDetailsReqDTO.setDistributionType("Distribution Type");
        o2CTransferDetailsReqDTO.setDomain("Domain");
        o2CTransferDetailsReqDTO.setExtnwcode("Extnwcode");
        o2CTransferDetailsReqDTO.setFileName("foo.txt");
        o2CTransferDetailsReqDTO.setFileType("File Type");
        o2CTransferDetailsReqDTO.setFromDate("2020-03-01");
        o2CTransferDetailsReqDTO.setGeography("Geography");
        o2CTransferDetailsReqDTO.setLocale(Locale.getDefault());
        o2CTransferDetailsReqDTO.setMsisdn("Msisdn");
        o2CTransferDetailsReqDTO.setOffline(true);
        o2CTransferDetailsReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        o2CTransferDetailsReqDTO.setProductCode("Product Code");
        o2CTransferDetailsReqDTO.setToDate("2020-03-01");
        o2CTransferDetailsReqDTO.setTransferCategory("Transfer Category");
        o2CTransferDetailsReqDTO.setTransferSubType("Transfer Sub Type");
        o2CTransferDetailsReqDTO.setUser("User");
        o2CTransferDetailsReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransfDetailReportProcess.validateInputs(con, o2CTransferDetailsReqDTO);
    }

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#validateInputs(Connection, O2CTransferDetailsReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException {
        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = new O2CTransferDetailsReqDTO();
        o2CTransferDetailsReqDTO.setCategoryCode("Category Code");
        o2CTransferDetailsReqDTO.setDispHeaderColumnList(new ArrayList<>());
        o2CTransferDetailsReqDTO.setDistributionType("Distribution Type");
        o2CTransferDetailsReqDTO.setDomain("Domain");
        o2CTransferDetailsReqDTO.setExtnwcode("Extnwcode");
        o2CTransferDetailsReqDTO.setFileName("foo.txt");
        o2CTransferDetailsReqDTO.setFileType("File Type");
        o2CTransferDetailsReqDTO.setFromDate("2020-03-01");
        o2CTransferDetailsReqDTO.setGeography("Geography");
        o2CTransferDetailsReqDTO.setLocale(Locale.getDefault());
        o2CTransferDetailsReqDTO.setMsisdn("Msisdn");
        o2CTransferDetailsReqDTO.setOffline(true);
        o2CTransferDetailsReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        o2CTransferDetailsReqDTO.setProductCode("Product Code");
        o2CTransferDetailsReqDTO.setToDate("2020-03-01");
        o2CTransferDetailsReqDTO.setTransferCategory("Transfer Category");
        o2CTransferDetailsReqDTO.setTransferSubType("Transfer Sub Type");
        o2CTransferDetailsReqDTO.setUser("User");
        o2CTransferDetailsReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransfDetailReportProcess.validateInputs(null, o2CTransferDetailsReqDTO);
    }

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#validateInputs(Connection, O2CTransferDetailsReqDTO)}
     */
    @Test
    public void testValidateInputs3() throws BTSLBaseException {
        Connection con = mock(Connection.class);
        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = mock(O2CTransferDetailsReqDTO.class);
        when(o2CTransferDetailsReqDTO.getTransferCategory()).thenReturn("Transfer Category");
        when(o2CTransferDetailsReqDTO.getGeography()).thenReturn("Geography");
        when(o2CTransferDetailsReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(o2CTransferDetailsReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(o2CTransferDetailsReqDTO.getToDate()).thenReturn("2020-03-01");
        when(o2CTransferDetailsReqDTO.getTransferSubType()).thenReturn("Transfer Sub Type");
        doNothing().when(o2CTransferDetailsReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(o2CTransferDetailsReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setOffline(anyBoolean());
        doNothing().when(o2CTransferDetailsReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDistributionType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setTransferCategory(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setTransferSubType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setUser(Mockito.<String>any());
        o2CTransferDetailsReqDTO.setCategoryCode("Category Code");
        o2CTransferDetailsReqDTO.setDispHeaderColumnList(new ArrayList<>());
        o2CTransferDetailsReqDTO.setDistributionType("Distribution Type");
        o2CTransferDetailsReqDTO.setDomain("Domain");
        o2CTransferDetailsReqDTO.setExtnwcode("Extnwcode");
        o2CTransferDetailsReqDTO.setFileName("foo.txt");
        o2CTransferDetailsReqDTO.setFileType("File Type");
        o2CTransferDetailsReqDTO.setFromDate("2020-03-01");
        o2CTransferDetailsReqDTO.setGeography("Geography");
        o2CTransferDetailsReqDTO.setLocale(Locale.getDefault());
        o2CTransferDetailsReqDTO.setMsisdn("Msisdn");
        o2CTransferDetailsReqDTO.setOffline(true);
        o2CTransferDetailsReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        o2CTransferDetailsReqDTO.setProductCode("Product Code");
        o2CTransferDetailsReqDTO.setToDate("2020-03-01");
        o2CTransferDetailsReqDTO.setTransferCategory("Transfer Category");
        o2CTransferDetailsReqDTO.setTransferSubType("Transfer Sub Type");
        o2CTransferDetailsReqDTO.setUser("User");
        o2CTransferDetailsReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransfDetailReportProcess.validateInputs(con, o2CTransferDetailsReqDTO);
        verify(o2CTransferDetailsReqDTO).getExtnwcode();
        verify(o2CTransferDetailsReqDTO).getFromDate();
        verify(o2CTransferDetailsReqDTO).getToDate();
        verify(o2CTransferDetailsReqDTO).getGeography();
        verify(o2CTransferDetailsReqDTO, atLeast(1)).getTransferCategory();
        verify(o2CTransferDetailsReqDTO, atLeast(1)).getTransferSubType();
        verify(o2CTransferDetailsReqDTO).setExtnwcode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setFromDate(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setLocale(Mockito.<Locale>any());
        verify(o2CTransferDetailsReqDTO).setMsisdn(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setProductCode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setToDate(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setUserId(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(o2CTransferDetailsReqDTO).setFileName(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setFileType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setOffline(anyBoolean());
        verify(o2CTransferDetailsReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setCategoryCode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDistributionType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDomain(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setGeography(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setTransferCategory(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setTransferSubType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setUser(Mockito.<String>any());
    }

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#validateInputs(Connection, O2CTransferDetailsReqDTO)}
     */
    @Test
    public void testValidateInputs4() throws BTSLBaseException {
        Connection con = mock(Connection.class);
        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = mock(O2CTransferDetailsReqDTO.class);
        when(o2CTransferDetailsReqDTO.getTransferCategory()).thenReturn("ALL");
        when(o2CTransferDetailsReqDTO.getGeography()).thenReturn("Geography");
        when(o2CTransferDetailsReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(o2CTransferDetailsReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(o2CTransferDetailsReqDTO.getToDate()).thenReturn("2020-03-01");
        when(o2CTransferDetailsReqDTO.getTransferSubType()).thenReturn("Transfer Sub Type");
        doNothing().when(o2CTransferDetailsReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(o2CTransferDetailsReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setOffline(anyBoolean());
        doNothing().when(o2CTransferDetailsReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDistributionType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setTransferCategory(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setTransferSubType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setUser(Mockito.<String>any());
        o2CTransferDetailsReqDTO.setCategoryCode("Category Code");
        o2CTransferDetailsReqDTO.setDispHeaderColumnList(new ArrayList<>());
        o2CTransferDetailsReqDTO.setDistributionType("Distribution Type");
        o2CTransferDetailsReqDTO.setDomain("Domain");
        o2CTransferDetailsReqDTO.setExtnwcode("Extnwcode");
        o2CTransferDetailsReqDTO.setFileName("foo.txt");
        o2CTransferDetailsReqDTO.setFileType("File Type");
        o2CTransferDetailsReqDTO.setFromDate("2020-03-01");
        o2CTransferDetailsReqDTO.setGeography("Geography");
        o2CTransferDetailsReqDTO.setLocale(Locale.getDefault());
        o2CTransferDetailsReqDTO.setMsisdn("Msisdn");
        o2CTransferDetailsReqDTO.setOffline(true);
        o2CTransferDetailsReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        o2CTransferDetailsReqDTO.setProductCode("Product Code");
        o2CTransferDetailsReqDTO.setToDate("2020-03-01");
        o2CTransferDetailsReqDTO.setTransferCategory("Transfer Category");
        o2CTransferDetailsReqDTO.setTransferSubType("Transfer Sub Type");
        o2CTransferDetailsReqDTO.setUser("User");
        o2CTransferDetailsReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransfDetailReportProcess.validateInputs(con, o2CTransferDetailsReqDTO);
        verify(o2CTransferDetailsReqDTO).getExtnwcode();
        verify(o2CTransferDetailsReqDTO).getFromDate();
        verify(o2CTransferDetailsReqDTO).getToDate();
        verify(o2CTransferDetailsReqDTO).getGeography();
        verify(o2CTransferDetailsReqDTO, atLeast(1)).getTransferCategory();
        verify(o2CTransferDetailsReqDTO, atLeast(1)).getTransferSubType();
        verify(o2CTransferDetailsReqDTO).setExtnwcode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setFromDate(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setLocale(Mockito.<Locale>any());
        verify(o2CTransferDetailsReqDTO).setMsisdn(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setProductCode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setToDate(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setUserId(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(o2CTransferDetailsReqDTO).setFileName(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setFileType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setOffline(anyBoolean());
        verify(o2CTransferDetailsReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setCategoryCode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDistributionType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDomain(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setGeography(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setTransferCategory(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setTransferSubType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setUser(Mockito.<String>any());
    }

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#validateInputs(Connection, O2CTransferDetailsReqDTO)}
     */
    @Test
    public void testValidateInputs5() throws BTSLBaseException {
        Connection con = mock(Connection.class);
        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = mock(O2CTransferDetailsReqDTO.class);
        when(o2CTransferDetailsReqDTO.getTransferCategory()).thenReturn(null);
        when(o2CTransferDetailsReqDTO.getGeography()).thenReturn("Geography");
        when(o2CTransferDetailsReqDTO.getExtnwcode()).thenReturn("Extnwcode");
        when(o2CTransferDetailsReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(o2CTransferDetailsReqDTO.getToDate()).thenReturn("2020-03-01");
        when(o2CTransferDetailsReqDTO.getTransferSubType()).thenReturn("Transfer Sub Type");
        doNothing().when(o2CTransferDetailsReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(o2CTransferDetailsReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setOffline(anyBoolean());
        doNothing().when(o2CTransferDetailsReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDistributionType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setTransferCategory(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setTransferSubType(Mockito.<String>any());
        doNothing().when(o2CTransferDetailsReqDTO).setUser(Mockito.<String>any());
        o2CTransferDetailsReqDTO.setCategoryCode("Category Code");
        o2CTransferDetailsReqDTO.setDispHeaderColumnList(new ArrayList<>());
        o2CTransferDetailsReqDTO.setDistributionType("Distribution Type");
        o2CTransferDetailsReqDTO.setDomain("Domain");
        o2CTransferDetailsReqDTO.setExtnwcode("Extnwcode");
        o2CTransferDetailsReqDTO.setFileName("foo.txt");
        o2CTransferDetailsReqDTO.setFileType("File Type");
        o2CTransferDetailsReqDTO.setFromDate("2020-03-01");
        o2CTransferDetailsReqDTO.setGeography("Geography");
        o2CTransferDetailsReqDTO.setLocale(Locale.getDefault());
        o2CTransferDetailsReqDTO.setMsisdn("Msisdn");
        o2CTransferDetailsReqDTO.setOffline(true);
        o2CTransferDetailsReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        o2CTransferDetailsReqDTO.setProductCode("Product Code");
        o2CTransferDetailsReqDTO.setToDate("2020-03-01");
        o2CTransferDetailsReqDTO.setTransferCategory("Transfer Category");
        o2CTransferDetailsReqDTO.setTransferSubType("Transfer Sub Type");
        o2CTransferDetailsReqDTO.setUser("User");
        o2CTransferDetailsReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        o2CTransfDetailReportProcess.validateInputs(con, o2CTransferDetailsReqDTO);
        verify(o2CTransferDetailsReqDTO).getExtnwcode();
        verify(o2CTransferDetailsReqDTO).getFromDate();
        verify(o2CTransferDetailsReqDTO).getToDate();
        verify(o2CTransferDetailsReqDTO).getGeography();
        verify(o2CTransferDetailsReqDTO).getTransferCategory();
        verify(o2CTransferDetailsReqDTO, atLeast(1)).getTransferSubType();
        verify(o2CTransferDetailsReqDTO).setExtnwcode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setFromDate(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setLocale(Mockito.<Locale>any());
        verify(o2CTransferDetailsReqDTO).setMsisdn(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setProductCode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setToDate(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setUserId(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(o2CTransferDetailsReqDTO).setFileName(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setFileType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setOffline(anyBoolean());
        verify(o2CTransferDetailsReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setCategoryCode(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDistributionType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setDomain(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setGeography(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setTransferCategory(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setTransferSubType(Mockito.<String>any());
        verify(o2CTransferDetailsReqDTO).setUser(Mockito.<String>any());
    }

    /**
     * Method under test: {@link O2CTransfDetailReportProcess#execute(O2CTransferDetailsReqDTO, O2CTransferDetailDownloadResp)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testExecute() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.O2CTransfDetailReportProcess.execute(O2CTransfDetailReportProcess.java:761)

        // Arrange
        // TODO: Populate arranged inputs
        O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO = null;
        O2CTransferDetailDownloadResp response = null;

        // Act
        this.o2CTransfDetailReportProcess.execute(o2CTransferDetailsReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }
}

