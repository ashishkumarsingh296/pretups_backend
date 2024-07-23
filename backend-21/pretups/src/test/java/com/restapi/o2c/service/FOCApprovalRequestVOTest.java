package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FOCApprovalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link FOCApprovalRequestVO}
     *   <li>{@link FOCApprovalRequestVO#setFocApprovalRequests(List)}
     *   <li>{@link FOCApprovalRequestVO#toString()}
     *   <li>{@link FOCApprovalRequestVO#getFocApprovalRequests()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        FOCApprovalRequestVO actualFocApprovalRequestVO = new FOCApprovalRequestVO();
        ArrayList<FOCApprovalData> focApprovalRequests = new ArrayList<>();
        actualFocApprovalRequestVO.setFocApprovalRequests(focApprovalRequests);
        String actualToStringResult = actualFocApprovalRequestVO.toString();
        assertSame(focApprovalRequests, actualFocApprovalRequestVO.getFocApprovalRequests());
        assertEquals("FOCApprovalRequestVO [focApprovalRequests=[]]", actualToStringResult);
    }
}

