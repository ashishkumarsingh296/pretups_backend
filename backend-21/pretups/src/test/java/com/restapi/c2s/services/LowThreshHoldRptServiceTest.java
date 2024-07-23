package com.restapi.c2s.services;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportDTO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.LowThresholdDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThresholdDownloadResp;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {LowThreshHoldRptService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class LowThreshHoldRptServiceTest {
    @Autowired
    private LowThreshHoldRptService lowThreshHoldRptService;

    /**
     * Method under test: {@link LowThreshHoldRptService#getLowThreshHoldReport(LowThreshHoldReportDTO, LowThreshHoldRptResp)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGetLowThreshHoldReport() throws BTSLBaseException {
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
        //       at com.restapi.c2s.services.LowThreshHoldRptService.getLowThreshHoldReport(LowThreshHoldRptService.java:94)

        // Arrange
        // TODO: Populate arranged inputs
        LowThreshHoldReportDTO lowThreshHoldReportDTO = null;
        LowThreshHoldRptResp response = null;

        // Act
        this.lowThreshHoldRptService.getLowThreshHoldReport(lowThreshHoldReportDTO, response);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link LowThreshHoldRptService#execute(LowThresholdDownloadReqDTO, LowThresholdDownloadResp)}
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
        //       at com.restapi.c2s.services.LowThreshHoldRptService.execute(LowThreshHoldRptService.java:643)

        // Arrange
        // TODO: Populate arranged inputs
        LowThresholdDownloadReqDTO lhtDownloadReqdata = null;
        LowThresholdDownloadResp response = null;

        // Act
        this.lowThreshHoldRptService.execute(lhtDownloadReqdata, response);

        // Assert
        // TODO: Add assertions on result
    }
}

