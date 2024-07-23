package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApprovalBarredForDltRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovalBarredForDltRequestVO}
     *   <li>{@link ApprovalBarredForDltRequestVO#setAction(String)}
     *   <li>{@link ApprovalBarredForDltRequestVO#setLoginId(String)}
     *   <li>{@link ApprovalBarredForDltRequestVO#setRemarks(String)}
     *   <li>{@link ApprovalBarredForDltRequestVO#setRequestType(String)}
     *   <li>{@link ApprovalBarredForDltRequestVO#getAction()}
     *   <li>{@link ApprovalBarredForDltRequestVO#getLoginId()}
     *   <li>{@link ApprovalBarredForDltRequestVO#getRemarks()}
     *   <li>{@link ApprovalBarredForDltRequestVO#getRequestType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovalBarredForDltRequestVO actualApprovalBarredForDltRequestVO = new ApprovalBarredForDltRequestVO();
        actualApprovalBarredForDltRequestVO.setAction("Action");
        actualApprovalBarredForDltRequestVO.setLoginId("42");
        actualApprovalBarredForDltRequestVO.setRemarks("Remarks");
        actualApprovalBarredForDltRequestVO.setRequestType("Request Type");
        assertEquals("Action", actualApprovalBarredForDltRequestVO.getAction());
        assertEquals("42", actualApprovalBarredForDltRequestVO.getLoginId());
        assertEquals("Remarks", actualApprovalBarredForDltRequestVO.getRemarks());
        assertEquals("Request Type", actualApprovalBarredForDltRequestVO.getRequestType());
    }
}

