package com.restapi.voucherbundle.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.util.JUnitConfig;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

public class AddVoucherBundleTest {
    /**
     * Method under test: {@link AddVoucherBundle#addVoucherBundle(String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testAddVoucherBundle() throws BTSLBaseException, IOException, SQLException {
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
        //       at com.restapi.voucherbundle.service.AddVoucherBundle.addVoucherBundle(AddVoucherBundle.java:60)

        // Arrange
        // TODO: Populate arranged inputs
        JUnitConfig.init();
        AddVoucherBundle addVoucherBundle = null;
        String requestData = "";

        // Act
        PretupsResponse<JsonNode> actualAddVoucherBundleResult = addVoucherBundle.addVoucherBundle(requestData);

        // Assert
        // TODO: Add assertions on result
    }
}

