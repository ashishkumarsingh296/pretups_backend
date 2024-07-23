package com.restapi.user.service;

import com.btsl.pretups.receiver.RequestVO;
import org.junit.Ignore;
import org.junit.Test;

public class VoucherInfoTest {
    /**
     * Method under test: {@link VoucherInfo#process(RequestVO)}
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
        //       at com.restapi.user.service.VoucherInfo.process(VoucherInfo.java:60)

        // Arrange
        // TODO: Populate arranged inputs
        VoucherInfo voucherInfo = null;
        RequestVO p_requestVO = null;

        // Act
        voucherInfo.process(p_requestVO);

        // Assert
        // TODO: Add assertions on result
    }
}

