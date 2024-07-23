package com.restapi.superadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import org.junit.Test;

public class PasswordManagementVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PasswordManagementVO}
     *   <li>{@link PasswordManagementVO#setChannelUserVO(ChannelUserVO)}
     *   <li>{@link PasswordManagementVO#setLoginID(String)}
     *   <li>{@link PasswordManagementVO#setMsisdn(String)}
     *   <li>{@link PasswordManagementVO#setRemarks(String)}
     *   <li>{@link PasswordManagementVO#setResetPassword(String)}
     *   <li>{@link PasswordManagementVO#setResetPin(String)}
     *   <li>{@link PasswordManagementVO#getChannelUserVO()}
     *   <li>{@link PasswordManagementVO#getLoginID()}
     *   <li>{@link PasswordManagementVO#getMsisdn()}
     *   <li>{@link PasswordManagementVO#getRemarks()}
     *   <li>{@link PasswordManagementVO#getResetPassword()}
     *   <li>{@link PasswordManagementVO#getResetPin()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PasswordManagementVO actualPasswordManagementVO = new PasswordManagementVO();
        ChannelUserVO channelUserVO = ChannelUserVO.getInstance();
        actualPasswordManagementVO.setChannelUserVO(channelUserVO);
        actualPasswordManagementVO.setLoginID("Login ID");
        actualPasswordManagementVO.setMsisdn("Msisdn");
        actualPasswordManagementVO.setRemarks("Remarks");
        actualPasswordManagementVO.setResetPassword("iloveyou");
        actualPasswordManagementVO.setResetPin("Resert Pin");
        assertSame(channelUserVO, actualPasswordManagementVO.getChannelUserVO());
        assertEquals("Login ID", actualPasswordManagementVO.getLoginID());
        assertEquals("Msisdn", actualPasswordManagementVO.getMsisdn());
        assertEquals("Remarks", actualPasswordManagementVO.getRemarks());
        assertEquals("iloveyou", actualPasswordManagementVO.getResetPassword());
        assertEquals("Resert Pin", actualPasswordManagementVO.getResetPin());
    }
}

