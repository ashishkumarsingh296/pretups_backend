package com.restapi.channeluser.service;

import static org.mockito.Mockito.mock;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;

import java.sql.Connection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ChannelApprvListServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Method under test: {@link ChannelApprvListService#execute(ApplistReqVO, Connection)}
     */
    @Test
    public void testExecute() throws BTSLBaseException {
        ChannelApprvListService channelApprvListService = new ChannelApprvListService();

        ApplistReqVO applistReqVO = new ApplistReqVO();
        applistReqVO.setApprovalLevel("Approval Level");
        applistReqVO.setCategory("Category");
        applistReqVO.setDomain("Domain");
        applistReqVO.setGeography("Geography");
        applistReqVO.setLoggedInUserUserid("Logged In User Userid");
        applistReqVO.setLoginID("Login ID");
        applistReqVO.setMobileNumber("42");
        applistReqVO.setReqTab("Req Tab");
        applistReqVO.setStatus("Status");
        thrown.expect(BTSLBaseException.class);
        channelApprvListService.execute(applistReqVO, mock(Connection.class));
    }
}

