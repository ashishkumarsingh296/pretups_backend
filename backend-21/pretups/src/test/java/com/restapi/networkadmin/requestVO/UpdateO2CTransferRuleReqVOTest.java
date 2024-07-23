package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class UpdateO2CTransferRuleReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateO2CTransferRuleReqVO}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setDomainCode(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setDpAllowed(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setFirstApprovalLimit(Long)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setFocAllowed(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setLastModifiedTime(long)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setParentAssocationAllowed(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setProductArray(String[])}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setReturnAllowed(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setSecondApprovalLimit(Long)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setToCategory(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setToCategoryDes(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setTransferAllowed(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setTransferRuleId(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#setWithdrawAllowed(String)}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getDomainCode()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getDpAllowed()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getFirstApprovalLimit()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getFocAllowed()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getLastModifiedTime()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getParentAssocationAllowed()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getProductArray()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getReturnAllowed()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getSecondApprovalLimit()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getToCategory()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getToCategoryDes()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getTransferAllowed()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getTransferRuleId()}
     *   <li>{@link UpdateO2CTransferRuleReqVO#getWithdrawAllowed()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateO2CTransferRuleReqVO actualUpdateO2CTransferRuleReqVO = new UpdateO2CTransferRuleReqVO();
        actualUpdateO2CTransferRuleReqVO.setDomainCode("Domain Code");
        actualUpdateO2CTransferRuleReqVO.setDpAllowed("Dp Allowed");
        actualUpdateO2CTransferRuleReqVO.setFirstApprovalLimit(42L);
        actualUpdateO2CTransferRuleReqVO.setFocAllowed("Foc Allowed");
        actualUpdateO2CTransferRuleReqVO.setLastModifiedTime(1L);
        actualUpdateO2CTransferRuleReqVO.setParentAssocationAllowed("Parent Assocation Allowed");
        String[] productArray = new String[]{"Product Array"};
        actualUpdateO2CTransferRuleReqVO.setProductArray(productArray);
        actualUpdateO2CTransferRuleReqVO.setReturnAllowed("Return Allowed");
        actualUpdateO2CTransferRuleReqVO.setSecondApprovalLimit(42L);
        actualUpdateO2CTransferRuleReqVO.setToCategory("To Category");
        actualUpdateO2CTransferRuleReqVO.setToCategoryDes("To Category Des");
        actualUpdateO2CTransferRuleReqVO.setTransferAllowed("Transfer Allowed");
        actualUpdateO2CTransferRuleReqVO.setTransferRuleId("42");
        actualUpdateO2CTransferRuleReqVO.setWithdrawAllowed("Withdraw Allowed");
        assertEquals("Domain Code", actualUpdateO2CTransferRuleReqVO.getDomainCode());
        assertEquals("Dp Allowed", actualUpdateO2CTransferRuleReqVO.getDpAllowed());
        assertEquals(42L, actualUpdateO2CTransferRuleReqVO.getFirstApprovalLimit().longValue());
        assertEquals("Foc Allowed", actualUpdateO2CTransferRuleReqVO.getFocAllowed());
        assertEquals(1L, actualUpdateO2CTransferRuleReqVO.getLastModifiedTime());
        assertEquals("Parent Assocation Allowed", actualUpdateO2CTransferRuleReqVO.getParentAssocationAllowed());
        assertSame(productArray, actualUpdateO2CTransferRuleReqVO.getProductArray());
        assertEquals("Return Allowed", actualUpdateO2CTransferRuleReqVO.getReturnAllowed());
        assertEquals(42L, actualUpdateO2CTransferRuleReqVO.getSecondApprovalLimit().longValue());
        assertEquals("To Category", actualUpdateO2CTransferRuleReqVO.getToCategory());
        assertEquals("To Category Des", actualUpdateO2CTransferRuleReqVO.getToCategoryDes());
        assertEquals("Transfer Allowed", actualUpdateO2CTransferRuleReqVO.getTransferAllowed());
        assertEquals("42", actualUpdateO2CTransferRuleReqVO.getTransferRuleId());
        assertEquals("Withdraw Allowed", actualUpdateO2CTransferRuleReqVO.getWithdrawAllowed());
    }
}

