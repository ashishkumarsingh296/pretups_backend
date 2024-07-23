package com.restapi.c2s.services;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.util.JUnitConfig;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PinpassHistServiceProcessor.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PinpassHistServiceProcessorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private PinpassHistServiceProcessor pinpassHistServiceProcessor;

    /**
     * Method under test: {@link PinpassHistServiceProcessor#searchPinPassHist(Connection, PinPassHistoryReqDTO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSearchPinPassHist() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.PinpassHistServiceProcessor.searchPinPassHist(PinpassHistServiceProcessor.java:92)

        // Arrange
        // TODO: Populate arranged inputs
        Connection con = null;
        PinPassHistoryReqDTO pinPassHistoryReqDTO = null;

        // Act
        List<PinPassHistSearchRecordVO> actualSearchPinPassHistResult = this.pinpassHistServiceProcessor
                .searchPinPassHist(JUnitConfig.getConnection(), pinPassHistoryReqDTO);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PinpassHistServiceProcessor#validateInputs(Connection, PinPassHistoryReqDTO)}
     */
    @Test
    public void testValidateInputs() throws BTSLBaseException {
        //Connection con = mock(Connection.class);
        JUnitConfig.init();
        PinPassHistoryReqDTO pinPassHistoryReqDTO = new PinPassHistoryReqDTO();
        pinPassHistoryReqDTO.setCategoryCode("Category Code");
        pinPassHistoryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        pinPassHistoryReqDTO.setDomain("Domain");
        pinPassHistoryReqDTO.setExtnwcode("Extnwcode");
        pinPassHistoryReqDTO.setFileName("foo.txt");
        pinPassHistoryReqDTO.setFileType("File Type");
        pinPassHistoryReqDTO.setFromDate("2020-03-01");
        pinPassHistoryReqDTO.setGeography("Geography");
        pinPassHistoryReqDTO.setLocale(Locale.getDefault());
        pinPassHistoryReqDTO.setMsisdn("Msisdn");
        pinPassHistoryReqDTO.setOffline(true);
        pinPassHistoryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        pinPassHistoryReqDTO.setProductCode("Product Code");
        pinPassHistoryReqDTO.setReqType("Req Type");
        pinPassHistoryReqDTO.setToDate("2020-03-01");
        pinPassHistoryReqDTO.setUserId("42");
        pinPassHistoryReqDTO.setUserType("User Type");
        thrown.expect(BTSLBaseException.class);
        pinpassHistServiceProcessor.validateInputs(JUnitConfig.getConnection(), pinPassHistoryReqDTO);
    }

    /**
     * Method under test: {@link PinpassHistServiceProcessor#validateInputs(Connection, PinPassHistoryReqDTO)}
     */
    @Test
    public void testValidateInputs2() throws BTSLBaseException {

        JUnitConfig.init();
        oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy con = mock(
                oracle$1jdbc$1replay$1driver$1NonTxnReplayableConnection$2java$1sql$1Connection$$$Proxy.class);

        PinPassHistoryReqDTO pinPassHistoryReqDTO = new PinPassHistoryReqDTO();
        pinPassHistoryReqDTO.setCategoryCode("Category Code");
        pinPassHistoryReqDTO.setDispHeaderColumnList(new ArrayList<>());
        pinPassHistoryReqDTO.setDomain("Domain");
        pinPassHistoryReqDTO.setExtnwcode("Extnwcode");
        pinPassHistoryReqDTO.setFileName("foo.txt");
        pinPassHistoryReqDTO.setFileType("File Type");
        pinPassHistoryReqDTO.setFromDate("2020-03-01");
        pinPassHistoryReqDTO.setGeography("Geography");
        pinPassHistoryReqDTO.setLocale(Locale.getDefault());
        pinPassHistoryReqDTO.setMsisdn("Msisdn");
        pinPassHistoryReqDTO.setOffline(true);
        pinPassHistoryReqDTO.setOfflineReportTaskID("Offline Report Task ID");
        pinPassHistoryReqDTO.setProductCode("Product Code");
        pinPassHistoryReqDTO.setReqType("Req Type");
        pinPassHistoryReqDTO.setToDate("2020-03-01");
        pinPassHistoryReqDTO.setUserId("42");
        pinPassHistoryReqDTO.setUserType("User Type");
        thrown.expect(BTSLBaseException.class);
        pinpassHistServiceProcessor.validateInputs(JUnitConfig.getConnection(), pinPassHistoryReqDTO);
    }
}

