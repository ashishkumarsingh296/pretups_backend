package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuspendResumeStaffResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SuspendResumeStaffResponseVO}
     *   <li>{@link SuspendResumeStaffResponseVO#setMessage(String)}
     *   <li>{@link SuspendResumeStaffResponseVO#setMessageCode(String)}
     *   <li>{@link SuspendResumeStaffResponseVO#setStatus(int)}
     *   <li>{@link SuspendResumeStaffResponseVO#toString()}
     *   <li>{@link SuspendResumeStaffResponseVO#getMessage()}
     *   <li>{@link SuspendResumeStaffResponseVO#getMessageCode()}
     *   <li>{@link SuspendResumeStaffResponseVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SuspendResumeStaffResponseVO actualSuspendResumeStaffResponseVO = new SuspendResumeStaffResponseVO();
        actualSuspendResumeStaffResponseVO.setMessage("Not all who wander are lost");
        actualSuspendResumeStaffResponseVO.setMessageCode("Message Code");
        actualSuspendResumeStaffResponseVO.setStatus(1);
        actualSuspendResumeStaffResponseVO.toString();
        assertEquals("Not all who wander are lost", actualSuspendResumeStaffResponseVO.getMessage());
        assertEquals("Message Code", actualSuspendResumeStaffResponseVO.getMessageCode());
        assertEquals(1, actualSuspendResumeStaffResponseVO.getStatus());
    }
}

