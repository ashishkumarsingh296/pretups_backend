package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class C2CBulkApprovalRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2CBulkApprovalRequestVO}
     *   <li>{@link C2CBulkApprovalRequestVO#setApprovalList(List)}
     *   <li>{@link C2CBulkApprovalRequestVO#toString()}
     *   <li>{@link C2CBulkApprovalRequestVO#getApprovalList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2CBulkApprovalRequestVO actualC2cBulkApprovalRequestVO = new C2CBulkApprovalRequestVO();
        ArrayList<C2CBulkApprovalData> approvalList = new ArrayList<>();
        actualC2cBulkApprovalRequestVO.setApprovalList(approvalList);
        String actualToStringResult = actualC2cBulkApprovalRequestVO.toString();
        assertSame(approvalList, actualC2cBulkApprovalRequestVO.getApprovalList());
        assertEquals("C2CBulkApprovalRequestVO [approvalList=[]]", actualToStringResult);
    }
}

