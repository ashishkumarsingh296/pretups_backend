package com.restapi.c2s.services;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.PBDownloadReqdata;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookDownloadResp;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PassBookDownloadService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PassBookDownloadServiceTest {
    @Autowired
    private PassBookDownloadService passBookDownloadService;

    /**
     * Method under test: {@link PassBookDownloadService#execute(PBDownloadReqdata, PassbookDownloadResp)}
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
        //       at com.restapi.c2s.services.PassBookDownloadService.execute(PassBookDownloadService.java:408)

        // Arrange
        // TODO: Populate arranged inputs
        PBDownloadReqdata pbDownloadReqdata = null;
        PassbookDownloadResp response = null;

        // Act
        this.passBookDownloadService.execute(pbDownloadReqdata, response);

        // Assert
        // TODO: Add assertions on result
    }
}

