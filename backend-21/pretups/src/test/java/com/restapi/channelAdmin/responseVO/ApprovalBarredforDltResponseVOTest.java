package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApprovalBarredforDltResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovalBarredforDltResponseVO}
     *   <li>{@link ApprovalBarredforDltResponseVO#setChangeStatus(boolean)}
     *   <li>{@link ApprovalBarredforDltResponseVO#isChangeStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovalBarredforDltResponseVO actualApprovalBarredforDltResponseVO = new ApprovalBarredforDltResponseVO();
        actualApprovalBarredforDltResponseVO.setChangeStatus(true);
        assertTrue(actualApprovalBarredforDltResponseVO.isChangeStatus());
    }
}

