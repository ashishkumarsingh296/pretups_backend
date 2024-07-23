package com.restapi.channelAdmin.serviceI;

import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.JUnitConfig;
import com.restapi.channelAdmin.requestVO.SuspendResumeStaffRequestVO;
import com.restapi.channelAdmin.responseVO.SuspendResumeStaffResponseVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {SuspendResumeStaffServiceImpl.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SuspendResumeStaffServiceImplTest {
    @Autowired
    private SuspendResumeStaffServiceImpl suspendResumeStaffServiceImpl;

    /**
     * Method under test: {@link SuspendResumeStaffServiceImpl#suspendResumeStaffUser(Connection, MComConnectionI, Locale, String, ChannelUserVO, SuspendResumeStaffRequestVO, SuspendResumeStaffResponseVO, HttpServletResponse)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testSuspendResumeStaffUser() throws SQLException {
        com.btsl.util.JUnitConfig.init(); //Auto replace
       // Connection con = null;
       // MComConnectionI mcomCon = null;
        Locale locale = null;
        String operationType = "";
        ChannelUserVO sessionUserVO = JUnitConfig.getChannelUserVO();



        SuspendResumeStaffRequestVO request = new SuspendResumeStaffRequestVO();

        request.setMsisdn("9999999999");
        request.setLoginID("Test");

        SuspendResumeStaffResponseVO response = new SuspendResumeStaffResponseVO();



        HttpServletResponse responseSwag = Mockito.mock(HttpServletResponse.class) ;

        Mockito.doNothing().when(responseSwag).setStatus(Mockito.anyInt());

        // Act
        SuspendResumeStaffResponseVO actualSuspendResumeStaffUserResult = this.suspendResumeStaffServiceImpl
                .suspendResumeStaffUser(JUnitConfig.getConnection(), JUnitConfig.getMComConnection(), locale, operationType, sessionUserVO, request, response, responseSwag);

        // Assert
        // TODO: Add assertions on result
    }
}

