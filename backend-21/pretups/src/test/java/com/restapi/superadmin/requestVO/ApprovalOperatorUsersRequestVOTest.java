package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApprovalOperatorUsersRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovalOperatorUsersRequestVO}
     *   <li>{@link ApprovalOperatorUsersRequestVO#setCategory(String)}
     *   <li>{@link ApprovalOperatorUsersRequestVO#setLoginID(String)}
     *   <li>{@link ApprovalOperatorUsersRequestVO#getCategory()}
     *   <li>{@link ApprovalOperatorUsersRequestVO#getLoginID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovalOperatorUsersRequestVO actualApprovalOperatorUsersRequestVO = new ApprovalOperatorUsersRequestVO();
        actualApprovalOperatorUsersRequestVO.setCategory("Category");
        actualApprovalOperatorUsersRequestVO.setLoginID("Login ID");
        assertEquals("Category", actualApprovalOperatorUsersRequestVO.getCategory());
        assertEquals("Login ID", actualApprovalOperatorUsersRequestVO.getLoginID());
    }
}

