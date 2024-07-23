package com.restapi.c2s.services;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.btsl.common.BTSLBaseException;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.jdbc.proxy.oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy;
import org.junit.Ignore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PassbookOthersProcessor.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PassbookOthersProcessorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private PassbookOthersProcessor passbookOthersProcessor;

    /**
     * Method under test: {@link PassbookOthersProcessor#validateInputs(Connection, PassbookOthersReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException {
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        PassbookOthersReqDTO passbookOthersReqDTO = new PassbookOthersReqDTO();
        passbookOthersReqDTO.setCategoryCode("Category Code");
        passbookOthersReqDTO.setDispHeaderColumnList(new ArrayList<>());
        passbookOthersReqDTO.setDomain("Domain");
        passbookOthersReqDTO.setExtnwcode("Extnwcode");
        passbookOthersReqDTO.setFileName("foo.txt");
        passbookOthersReqDTO.setFileType("File Type");
        passbookOthersReqDTO.setFromDate("2020-03-01");
        passbookOthersReqDTO.setGeography("Geography");
        passbookOthersReqDTO.setLocale(Locale.getDefault());
        passbookOthersReqDTO.setMsisdn("Msisdn");
        passbookOthersReqDTO.setNetworkCode("Network Code");
        passbookOthersReqDTO.setOffline(true);
        passbookOthersReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        passbookOthersReqDTO.setProduct("Product");
        passbookOthersReqDTO.setProductCode("Product Code");
        passbookOthersReqDTO.setToDate("2020-03-01");
        passbookOthersReqDTO.setUser("User");
        passbookOthersReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        passbookOthersProcessor.validateInputs(JUnitConfig.getConnection(), passbookOthersReqDTO);
    }

    /**
     * Method under test: {@link PassbookOthersProcessor#validateInputs(Connection, PassbookOthersReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException {
       JUnitConfig.init();

        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        PassbookOthersReqDTO passbookOthersReqDTO = new PassbookOthersReqDTO();
        passbookOthersReqDTO.setCategoryCode("Category Code");
        passbookOthersReqDTO.setDispHeaderColumnList(new ArrayList<>());
        passbookOthersReqDTO.setDomain("Domain");
        passbookOthersReqDTO.setExtnwcode("Extnwcode");
        passbookOthersReqDTO.setFileName("foo.txt");
        passbookOthersReqDTO.setFileType("File Type");
        passbookOthersReqDTO.setFromDate("2020-03-01");
        passbookOthersReqDTO.setGeography("Geography");
        passbookOthersReqDTO.setLocale(Locale.getDefault());
        passbookOthersReqDTO.setMsisdn("Msisdn");
        passbookOthersReqDTO.setNetworkCode("Network Code");
        passbookOthersReqDTO.setOffline(true);
        passbookOthersReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        passbookOthersReqDTO.setProduct("Product");
        passbookOthersReqDTO.setProductCode("Product Code");
        passbookOthersReqDTO.setToDate("2020-03-01");
        passbookOthersReqDTO.setUser("User");
        passbookOthersReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        passbookOthersProcessor.validateInputs(JUnitConfig.getConnection(), passbookOthersReqDTO);
    }

    /**
     * Method under test: {@link PassbookOthersProcessor#validateInputs(Connection, PassbookOthersReqDTO)}
     */
    @Test
    public void testValidateInputs3() throws BTSLBaseException {
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        PassbookOthersReqDTO passbookOthersReqDTO = mock(PassbookOthersReqDTO.class);
        when(passbookOthersReqDTO.getFromDate()).thenReturn("2020-03-01");
        when(passbookOthersReqDTO.getToDate()).thenReturn("2020-03-01");
        when(passbookOthersReqDTO.getNetworkCode()).thenReturn("Network Code");
        doNothing().when(passbookOthersReqDTO).setExtnwcode(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setFromDate(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setLocale(Mockito.<Locale>any());
        doNothing().when(passbookOthersReqDTO).setMsisdn(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setProductCode(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setToDate(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setUserId(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        doNothing().when(passbookOthersReqDTO).setFileName(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setFileType(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setOffline(anyBoolean());
        doNothing().when(passbookOthersReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setCategoryCode(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setDomain(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setGeography(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setNetworkCode(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setProduct(Mockito.<String>any());
        doNothing().when(passbookOthersReqDTO).setUser(Mockito.<String>any());
        passbookOthersReqDTO.setCategoryCode("Category Code");
        passbookOthersReqDTO.setDispHeaderColumnList(new ArrayList<>());
        passbookOthersReqDTO.setDomain("Domain");
        passbookOthersReqDTO.setExtnwcode("Extnwcode");
        passbookOthersReqDTO.setFileName("foo.txt");
        passbookOthersReqDTO.setFileType("File Type");
        passbookOthersReqDTO.setFromDate("2020-03-01");
        passbookOthersReqDTO.setGeography("Geography");
        passbookOthersReqDTO.setLocale(Locale.getDefault());
        passbookOthersReqDTO.setMsisdn("Msisdn");
        passbookOthersReqDTO.setNetworkCode("Network Code");
        passbookOthersReqDTO.setOffline(true);
        passbookOthersReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        passbookOthersReqDTO.setProduct("Product");
        passbookOthersReqDTO.setProductCode("Product Code");
        passbookOthersReqDTO.setToDate("2020-03-01");
        passbookOthersReqDTO.setUser("User");
        passbookOthersReqDTO.setUserId("42");
        thrown.expect(BTSLBaseException.class);
        passbookOthersProcessor.validateInputs(JUnitConfig.getConnection(), passbookOthersReqDTO);
        verify(passbookOthersReqDTO).getFromDate();
        verify(passbookOthersReqDTO).getToDate();
        verify(passbookOthersReqDTO).getNetworkCode();
        verify(passbookOthersReqDTO).setExtnwcode(Mockito.<String>any());
        verify(passbookOthersReqDTO).setFromDate(Mockito.<String>any());
        verify(passbookOthersReqDTO).setLocale(Mockito.<Locale>any());
        verify(passbookOthersReqDTO).setMsisdn(Mockito.<String>any());
        verify(passbookOthersReqDTO).setProductCode(Mockito.<String>any());
        verify(passbookOthersReqDTO).setToDate(Mockito.<String>any());
        verify(passbookOthersReqDTO).setUserId(Mockito.<String>any());
        verify(passbookOthersReqDTO).setDispHeaderColumnList(Mockito.<List<DispHeaderColumn>>any());
        verify(passbookOthersReqDTO).setFileName(Mockito.<String>any());
        verify(passbookOthersReqDTO).setFileType(Mockito.<String>any());
        verify(passbookOthersReqDTO).setOffline(anyBoolean());
        verify(passbookOthersReqDTO).setOfflineReportTaskID(Mockito.<String>any());
        verify(passbookOthersReqDTO).setCategoryCode(Mockito.<String>any());
        verify(passbookOthersReqDTO).setDomain(Mockito.<String>any());
        verify(passbookOthersReqDTO).setGeography(Mockito.<String>any());
        verify(passbookOthersReqDTO).setNetworkCode(Mockito.<String>any());
        verify(passbookOthersReqDTO).setProduct(Mockito.<String>any());
        verify(passbookOthersReqDTO).setUser(Mockito.<String>any());
    }

    /**
     * Method under test: {@link PassbookOthersProcessor#execute(PassbookOthersReqDTO, PassbookOthersDownloadResp)}
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
        //       at com.restapi.c2s.services.PassbookOthersProcessor.execute(PassbookOthersProcessor.java:784)

        // Arrange
        // TODO: Populate arranged inputs
        PassbookOthersReqDTO passbookOthersReqDTO = null;
        PassbookOthersDownloadResp response = null;

        // Act
        this.passbookOthersProcessor.execute(passbookOthersReqDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PassbookOthersProcessor#executeOffineService(EventObjectData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testExecuteOffineService() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.PassbookOthersProcessor.executeOffineService(PassbookOthersProcessor.java:963)
        //   See https://diff.blue/R013 to resolve this issue.

        passbookOthersProcessor.executeOffineService(new EventObjectData());
    }

    /**
     * Method under test: {@link PassbookOthersProcessor#executeOffineService(EventObjectData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testExecuteOffineService2() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.PassbookOthersProcessor.executeOffineService(PassbookOthersProcessor.java:961)
        //   See https://diff.blue/R013 to resolve this issue.

        passbookOthersProcessor.executeOffineService(null);
    }

    /**
     * Method under test: {@link PassbookOthersProcessor#executeOffineService(EventObjectData)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testExecuteOffineService3() throws BTSLBaseException {
       com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.c2s.services.PassbookOthersProcessor.executeOffineService(PassbookOthersProcessor.java:963)
        //   See https://diff.blue/R013 to resolve this issue.

        EventObjectData srcObj = mock(EventObjectData.class);
        when(srcObj.getRequestData()).thenReturn(null);
        when(srcObj.getFileName()).thenReturn("foo.txt");
        passbookOthersProcessor.executeOffineService(srcObj);
    }
}

