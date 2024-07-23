package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class AddO2CTransferRuleReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddO2CTransferRuleReqVO}
     *   <li>{@link AddO2CTransferRuleReqVO#setDomainCode(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#setDpAllowed(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#setFirstApprovalLimit(Long)}
     *   <li>{@link AddO2CTransferRuleReqVO#setFocAllowed(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#setProductArray(String[])}
     *   <li>{@link AddO2CTransferRuleReqVO#setReturnAllowed(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#setSecondApprovalLimit(Long)}
     *   <li>{@link AddO2CTransferRuleReqVO#setToCategory(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#setTransferAllowed(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#setWithdrawAllowed(String)}
     *   <li>{@link AddO2CTransferRuleReqVO#getDomainCode()}
     *   <li>{@link AddO2CTransferRuleReqVO#getDpAllowed()}
     *   <li>{@link AddO2CTransferRuleReqVO#getFirstApprovalLimit()}
     *   <li>{@link AddO2CTransferRuleReqVO#getFocAllowed()}
     *   <li>{@link AddO2CTransferRuleReqVO#getProductArray()}
     *   <li>{@link AddO2CTransferRuleReqVO#getReturnAllowed()}
     *   <li>{@link AddO2CTransferRuleReqVO#getSecondApprovalLimit()}
     *   <li>{@link AddO2CTransferRuleReqVO#getToCategory()}
     *   <li>{@link AddO2CTransferRuleReqVO#getTransferAllowed()}
     *   <li>{@link AddO2CTransferRuleReqVO#getWithdrawAllowed()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddO2CTransferRuleReqVO actualAddO2CTransferRuleReqVO = new AddO2CTransferRuleReqVO();
        actualAddO2CTransferRuleReqVO.setDomainCode("Domain Code");
        actualAddO2CTransferRuleReqVO.setDpAllowed("Dp Allowed");
        actualAddO2CTransferRuleReqVO.setFirstApprovalLimit(42L);
        actualAddO2CTransferRuleReqVO.setFocAllowed("Foc Allowed");
        String[] productArray = new String[]{"Product Array"};
        actualAddO2CTransferRuleReqVO.setProductArray(productArray);
        actualAddO2CTransferRuleReqVO.setReturnAllowed("Return Allowed");
        actualAddO2CTransferRuleReqVO.setSecondApprovalLimit(42L);
        actualAddO2CTransferRuleReqVO.setToCategory("To Category");
        actualAddO2CTransferRuleReqVO.setTransferAllowed("Transfer Allowed");
        actualAddO2CTransferRuleReqVO.setWithdrawAllowed("Withdraw Allowed");
        assertEquals("Domain Code", actualAddO2CTransferRuleReqVO.getDomainCode());
        assertEquals("Dp Allowed", actualAddO2CTransferRuleReqVO.getDpAllowed());
        assertEquals(42L, actualAddO2CTransferRuleReqVO.getFirstApprovalLimit().longValue());
        assertEquals("Foc Allowed", actualAddO2CTransferRuleReqVO.getFocAllowed());
        assertSame(productArray, actualAddO2CTransferRuleReqVO.getProductArray());
        assertEquals("Return Allowed", actualAddO2CTransferRuleReqVO.getReturnAllowed());
        assertEquals(42L, actualAddO2CTransferRuleReqVO.getSecondApprovalLimit().longValue());
        assertEquals("To Category", actualAddO2CTransferRuleReqVO.getToCategory());
        assertEquals("Transfer Allowed", actualAddO2CTransferRuleReqVO.getTransferAllowed());
        assertEquals("Withdraw Allowed", actualAddO2CTransferRuleReqVO.getWithdrawAllowed());
    }
}

