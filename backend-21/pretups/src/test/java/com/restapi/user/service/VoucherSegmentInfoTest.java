package com.restapi.user.service;

import com.btsl.pretups.receiver.RequestVO;
import org.junit.Ignore;
import org.junit.Test;

public class VoucherSegmentInfoTest {
    /**
     * Method under test: {@link VoucherSegmentInfo#process(RequestVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcess() {
        // TODO: Complete this test.
        //   Reason: R006 Static initializer failed.
        //   The static initializer of
        //   com.btsl.util.OracleUtil
        //   threw java.lang.NullPointerException while trying to load it.
        //   Make sure the static initializer of OracleUtil
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NullPointerException
        //       at com.btsl.util.OracleUtil.<clinit>(OracleUtil.java:256)
        //       at com.btsl.db.util.MComConnection.<init>(MComConnection.java:22)
        //       at com.restapi.user.service.VoucherSegmentInfo.process(VoucherSegmentInfo.java:55)

        // Arrange
        // TODO: Populate arranged inputs
        VoucherSegmentInfo voucherSegmentInfo = null;
        RequestVO p_requestVO = null;

        // Act
        voucherSegmentInfo.process(p_requestVO);

        // Assert
        // TODO: Add assertions on result
    }
}

