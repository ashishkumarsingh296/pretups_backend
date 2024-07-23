package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ApprovalOperatorUsersListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovalOperatorUsersListResponseVO}
     *   <li>{@link ApprovalOperatorUsersListResponseVO#setApprovalOperatorUsersList(ArrayList)}
     *   <li>{@link ApprovalOperatorUsersListResponseVO#getApprovalOperatorUsersList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovalOperatorUsersListResponseVO actualApprovalOperatorUsersListResponseVO = new ApprovalOperatorUsersListResponseVO();
        ArrayList approvalOperatorUsersList = new ArrayList();
        actualApprovalOperatorUsersListResponseVO.setApprovalOperatorUsersList(approvalOperatorUsersList);
        assertSame(approvalOperatorUsersList, actualApprovalOperatorUsersListResponseVO.getApprovalOperatorUsersList());
    }
}

