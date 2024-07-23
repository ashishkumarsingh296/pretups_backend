package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class SuspendResumeUserVoTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SuspendResumeUserVo}
     *   <li>{@link SuspendResumeUserVo#setSuspendResumeUserDetailsData(SuspendResumeUserDetailsData)}
     *   <li>{@link SuspendResumeUserVo#toString()}
     *   <li>{@link SuspendResumeUserVo#getSuspendResumeUserDetailsData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SuspendResumeUserVo actualSuspendResumeUserVo = new SuspendResumeUserVo();
        SuspendResumeUserDetailsData suspendResumeUserDetailsData = new SuspendResumeUserDetailsData();
        actualSuspendResumeUserVo.setSuspendResumeUserDetailsData(suspendResumeUserDetailsData);
        String actualToStringResult = actualSuspendResumeUserVo.toString();
        assertSame(suspendResumeUserDetailsData, actualSuspendResumeUserVo.getSuspendResumeUserDetailsData());
        assertEquals("SuspendResumeUserVo [suspendResumeUserDetailsData=SuspendResumeUserVo [msisdn=null, loginid=null,"
                + " remarks=null]]", actualToStringResult);
    }
}

