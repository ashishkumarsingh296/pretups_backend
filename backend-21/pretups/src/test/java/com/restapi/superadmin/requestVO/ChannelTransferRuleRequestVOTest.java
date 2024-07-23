package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ChannelTransferRuleRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelTransferRuleRequestVO}
     *   <li>{@link ChannelTransferRuleRequestVO#setCntrlReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setCntrlTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setCntrlWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setDirectTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setDomainCode(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFixedReturnCategory(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFixedReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFixedTransferCategory(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFixedTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFixedWithdrawCategory(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFixedWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setFromCategory(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setParentAssocationAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setProductArray(String[])}
     *   <li>{@link ChannelTransferRuleRequestVO#setRestrictedMsisdnAccess(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setRestrictedRechargeAccess(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setReturnAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setReturnChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setToCategory(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setToDomainCode(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setTransferChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setTransferType(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setUncntrlReturnAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setUncntrlReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setUncntrlTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setUncntrlTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setUncntrlWithdrawAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setUncntrlWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setWithdrawAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#setWithdrawChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleRequestVO#getCntrlReturnLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getCntrlTransferLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getCntrlWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getDirectTransferAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getDomainCode()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFixedReturnCategory()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFixedReturnLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFixedTransferCategory()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFixedTransferLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFixedWithdrawCategory()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFixedWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getFromCategory()}
     *   <li>{@link ChannelTransferRuleRequestVO#getParentAssocationAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getProductArray()}
     *   <li>{@link ChannelTransferRuleRequestVO#getRestrictedMsisdnAccess()}
     *   <li>{@link ChannelTransferRuleRequestVO#getRestrictedRechargeAccess()}
     *   <li>{@link ChannelTransferRuleRequestVO#getReturnAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getReturnChnlBypassAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getToCategory()}
     *   <li>{@link ChannelTransferRuleRequestVO#getToDomainCode()}
     *   <li>{@link ChannelTransferRuleRequestVO#getTransferChnlBypassAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getTransferType()}
     *   <li>{@link ChannelTransferRuleRequestVO#getUncntrlReturnAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getUncntrlReturnLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getUncntrlTransferAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getUncntrlTransferLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getUncntrlWithdrawAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getUncntrlWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleRequestVO#getWithdrawAllowed()}
     *   <li>{@link ChannelTransferRuleRequestVO#getWithdrawChnlBypassAllowed()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelTransferRuleRequestVO actualChannelTransferRuleRequestVO = new ChannelTransferRuleRequestVO();
        actualChannelTransferRuleRequestVO.setCntrlReturnLevel("Cntrl Return Level");
        actualChannelTransferRuleRequestVO.setCntrlTransferLevel("Cntrl Transfer Level");
        actualChannelTransferRuleRequestVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        actualChannelTransferRuleRequestVO.setDirectTransferAllowed("Direct Transfer Allowed");
        actualChannelTransferRuleRequestVO.setDomainCode("Domain Code");
        actualChannelTransferRuleRequestVO.setFixedReturnCategory("Fixed Return Category");
        actualChannelTransferRuleRequestVO.setFixedReturnLevel("Fixed Return Level");
        actualChannelTransferRuleRequestVO.setFixedTransferCategory("Fixed Transfer Category");
        actualChannelTransferRuleRequestVO.setFixedTransferLevel("Fixed Transfer Level");
        actualChannelTransferRuleRequestVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        actualChannelTransferRuleRequestVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        actualChannelTransferRuleRequestVO.setFromCategory("jane.doe@example.org");
        actualChannelTransferRuleRequestVO.setParentAssocationAllowed("Parent Assocation Allowed");
        String[] productArray = new String[]{"Product Array"};
        actualChannelTransferRuleRequestVO.setProductArray(productArray);
        actualChannelTransferRuleRequestVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        actualChannelTransferRuleRequestVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        actualChannelTransferRuleRequestVO.setReturnAllowed("Return Allowed");
        actualChannelTransferRuleRequestVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        actualChannelTransferRuleRequestVO.setToCategory("To Category");
        actualChannelTransferRuleRequestVO.setToDomainCode("To Domain Code");
        actualChannelTransferRuleRequestVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        actualChannelTransferRuleRequestVO.setTransferType("Transfer Type");
        actualChannelTransferRuleRequestVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        actualChannelTransferRuleRequestVO.setUncntrlReturnLevel("Uncntrl Return Level");
        actualChannelTransferRuleRequestVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        actualChannelTransferRuleRequestVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        actualChannelTransferRuleRequestVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        actualChannelTransferRuleRequestVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        actualChannelTransferRuleRequestVO.setWithdrawAllowed("Withdraw Allowed");
        actualChannelTransferRuleRequestVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals("Cntrl Return Level", actualChannelTransferRuleRequestVO.getCntrlReturnLevel());
        assertEquals("Cntrl Transfer Level", actualChannelTransferRuleRequestVO.getCntrlTransferLevel());
        assertEquals("Cntrl Withdraw Level", actualChannelTransferRuleRequestVO.getCntrlWithdrawLevel());
        assertEquals("Direct Transfer Allowed", actualChannelTransferRuleRequestVO.getDirectTransferAllowed());
        assertEquals("Domain Code", actualChannelTransferRuleRequestVO.getDomainCode());
        assertEquals("Fixed Return Category", actualChannelTransferRuleRequestVO.getFixedReturnCategory());
        assertEquals("Fixed Return Level", actualChannelTransferRuleRequestVO.getFixedReturnLevel());
        assertEquals("Fixed Transfer Category", actualChannelTransferRuleRequestVO.getFixedTransferCategory());
        assertEquals("Fixed Transfer Level", actualChannelTransferRuleRequestVO.getFixedTransferLevel());
        assertEquals("Fixed Withdraw Category", actualChannelTransferRuleRequestVO.getFixedWithdrawCategory());
        assertEquals("Fixed Withdraw Level", actualChannelTransferRuleRequestVO.getFixedWithdrawLevel());
        assertEquals("jane.doe@example.org", actualChannelTransferRuleRequestVO.getFromCategory());
        assertEquals("Parent Assocation Allowed", actualChannelTransferRuleRequestVO.getParentAssocationAllowed());
        assertSame(productArray, actualChannelTransferRuleRequestVO.getProductArray());
        assertEquals("Restricted Msisdn Access", actualChannelTransferRuleRequestVO.getRestrictedMsisdnAccess());
        assertEquals("Restricted Recharge Access", actualChannelTransferRuleRequestVO.getRestrictedRechargeAccess());
        assertEquals("Return Allowed", actualChannelTransferRuleRequestVO.getReturnAllowed());
        assertEquals("Return Chnl Bypass Allowed", actualChannelTransferRuleRequestVO.getReturnChnlBypassAllowed());
        assertEquals("To Category", actualChannelTransferRuleRequestVO.getToCategory());
        assertEquals("To Domain Code", actualChannelTransferRuleRequestVO.getToDomainCode());
        assertEquals("Transfer Chnl Bypass Allowed", actualChannelTransferRuleRequestVO.getTransferChnlBypassAllowed());
        assertEquals("Transfer Type", actualChannelTransferRuleRequestVO.getTransferType());
        assertEquals("Uncntrl Return Allowed", actualChannelTransferRuleRequestVO.getUncntrlReturnAllowed());
        assertEquals("Uncntrl Return Level", actualChannelTransferRuleRequestVO.getUncntrlReturnLevel());
        assertEquals("Uncntrl Transfer Allowed", actualChannelTransferRuleRequestVO.getUncntrlTransferAllowed());
        assertEquals("Uncntrl Transfer Level", actualChannelTransferRuleRequestVO.getUncntrlTransferLevel());
        assertEquals("Uncntrl Withdraw Allowed", actualChannelTransferRuleRequestVO.getUncntrlWithdrawAllowed());
        assertEquals("Uncntrl Withdraw Level", actualChannelTransferRuleRequestVO.getUncntrlWithdrawLevel());
        assertEquals("Withdraw Allowed", actualChannelTransferRuleRequestVO.getWithdrawAllowed());
        assertEquals("Withdraw Chnl Bypass Allowed", actualChannelTransferRuleRequestVO.getWithdrawChnlBypassAllowed());
    }
}

