package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuspendResumeStaffRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SuspendResumeStaffRequestVO}
     *   <li>{@link SuspendResumeStaffRequestVO#setLoginID(String)}
     *   <li>{@link SuspendResumeStaffRequestVO#setMsisdn(String)}
     *   <li>{@link SuspendResumeStaffRequestVO#toString()}
     *   <li>{@link SuspendResumeStaffRequestVO#getLoginID()}
     *   <li>{@link SuspendResumeStaffRequestVO#getMsisdn()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SuspendResumeStaffRequestVO actualSuspendResumeStaffRequestVO = new SuspendResumeStaffRequestVO();
        actualSuspendResumeStaffRequestVO.setLoginID("Login ID");
        actualSuspendResumeStaffRequestVO.setMsisdn("Msisdn");
        String actualToStringResult = actualSuspendResumeStaffRequestVO.toString();
        assertEquals("Login ID", actualSuspendResumeStaffRequestVO.getLoginID());
        assertEquals("Msisdn", actualSuspendResumeStaffRequestVO.getMsisdn());
        assertEquals("SuspendResumeStaffRequestVO [msisdn=Msisdn, loginID=Login ID]", actualToStringResult);
    }
}

