package com.restapi.networkadmin.service;

import com.btsl.common.BaseResponse;
import com.restapi.networkadmin.requestVO.UpdateControlPreferenceVO;
import com.restapi.networkadmin.responseVO.ControlPreferenceListsResponseVO;

import java.sql.SQLException;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ControlPreferenceServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ControlPreferenceServiceImplTest {
    @Autowired
    private ControlPreferenceServiceImpl controlPreferenceServiceImpl;

    /**
     * Method under test: {@link ControlPreferenceServiceImpl#fetchCtrlPreferenceLists(Locale, String, String, String, String, HttpServletResponse)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testFetchCtrlPreferenceLists() throws SQLException {
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
        //       at com.restapi.networkadmin.service.ControlPreferenceServiceImpl.fetchCtrlPreferenceLists(ControlPreferenceServiceImpl.java:56)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        String moduleCodeString = "";
        String controlCodeString = "";
        String preferenceCodeString = "";
        String loginId = "";
        HttpServletResponse responseSwag = null;

        // Act
        ControlPreferenceListsResponseVO actualFetchCtrlPreferenceListsResult = this.controlPreferenceServiceImpl
                .fetchCtrlPreferenceLists(locale, moduleCodeString, controlCodeString, preferenceCodeString, loginId,
                        responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link ControlPreferenceServiceImpl#updateControlPreference(Locale, HttpServletResponse, UpdateControlPreferenceVO, String)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testUpdateControlPreference() throws SQLException {
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
        //       at com.restapi.networkadmin.service.ControlPreferenceServiceImpl.updateControlPreference(ControlPreferenceServiceImpl.java:121)

        // Arrange
        // TODO: Populate arranged inputs
        Locale locale = null;
        HttpServletResponse responseSwag = null;
        UpdateControlPreferenceVO updateControlPreferenceVO = null;
        String msisdn = "";

        // Act
        BaseResponse actualUpdateControlPreferenceResult = this.controlPreferenceServiceImpl
                .updateControlPreference(locale, responseSwag, updateControlPreferenceVO, msisdn);

        // Assert
        // TODO: Add assertions on result
    }
}

