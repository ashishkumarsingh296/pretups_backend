package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class SuspendResumeUserHierarchyRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SuspendResumeUserHierarchyRequestVO}
     *   <li>{@link SuspendResumeUserHierarchyRequestVO#setLoginIdList(ArrayList)}
     *   <li>{@link SuspendResumeUserHierarchyRequestVO#setRequestType(String)}
     *   <li>{@link SuspendResumeUserHierarchyRequestVO#toString()}
     *   <li>{@link SuspendResumeUserHierarchyRequestVO#getLoginIdList()}
     *   <li>{@link SuspendResumeUserHierarchyRequestVO#getRequestType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SuspendResumeUserHierarchyRequestVO actualSuspendResumeUserHierarchyRequestVO = new SuspendResumeUserHierarchyRequestVO();
        ArrayList<String> loginIdList = new ArrayList<>();
        actualSuspendResumeUserHierarchyRequestVO.setLoginIdList(loginIdList);
        actualSuspendResumeUserHierarchyRequestVO.setRequestType("Request Type");
        String actualToStringResult = actualSuspendResumeUserHierarchyRequestVO.toString();
        assertSame(loginIdList, actualSuspendResumeUserHierarchyRequestVO.getLoginIdList());
        assertEquals("Request Type", actualSuspendResumeUserHierarchyRequestVO.getRequestType());
        assertEquals("suspendResumeUserHierarchyRequestVO [loginIdList=[], requestType=Request Type]",
                actualToStringResult);
    }
}

