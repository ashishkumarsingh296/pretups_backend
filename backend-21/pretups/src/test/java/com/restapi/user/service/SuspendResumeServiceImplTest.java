package com.restapi.user.service;

import com.btsl.common.BTSLBaseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

@ContextConfiguration(classes = {SuspendResumeServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SuspendResumeServiceImplTest {
    @Autowired
    private SuspendResumeServiceImpl suspendResumeServiceImpl;

    /**
     * Method under test: {@link SuspendResumeServiceImpl#processRequestStaff(SuspendResumeUserVo, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequestStaff() throws BTSLBaseException {
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
        //       at com.restapi.user.service.SuspendResumeServiceImpl.processRequestStaff(SuspendResumeServiceImpl.java:64)

        // Arrange
        // TODO: Populate arranged inputs
        SuspendResumeUserVo requestVO = null;
        HttpServletRequest httprequest = null;
        MultiValueMap<String, String> headers = null;
        HttpServletResponse responseSwag = null;

        // Act
        SuspendResumeResponse actualProcessRequestStaffResult = this.suspendResumeServiceImpl.processRequestStaff(requestVO,
                httprequest, headers, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link SuspendResumeServiceImpl#processRequest(SuspendResumeUserVo, HttpServletRequest, MultiValueMap, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testProcessRequest() throws BTSLBaseException {
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
        //       at com.restapi.user.service.SuspendResumeServiceImpl.processRequest(SuspendResumeServiceImpl.java:310)

        // Arrange
        // TODO: Populate arranged inputs
        SuspendResumeUserVo requestVO = null;
        HttpServletRequest httprequest = null;
        MultiValueMap<String, String> headers = null;
        HttpServletResponse responseSwag = null;

        // Act
        SuspendResumeResponse actualProcessRequestResult = this.suspendResumeServiceImpl.processRequest(requestVO,
                httprequest, headers, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }
}

