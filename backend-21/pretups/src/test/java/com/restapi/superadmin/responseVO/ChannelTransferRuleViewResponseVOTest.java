package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.restapi.superadmin.requestVO.ChannelTransferRuleRequestVO;
import org.junit.Test;

public class ChannelTransferRuleViewResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelTransferRuleViewResponseVO}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setChannelTransferRuleRequestVO(ChannelTransferRuleRequestVO)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setChnlByPassFlag(boolean)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setCntrlReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setCntrlReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setCntrlTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setCntrlTransferLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setCntrlWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setCntrlWithdrawLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setDirectTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setDomainCode(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setDomainCodeDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedReturnCategory(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedReturnCategoryDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedTransferCategory(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedTransferCategoryDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedTransferLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedWithdrawCategory(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedWithdrawCategoryDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFixedWithdrawLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFromCategory(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFromCategoryDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setFromCategorySeqNumber(Integer)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setParentAssocationAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setParentAssocationAllowedDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setParentAssociationAllowedFlag(boolean)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setProductArray(String[])}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setProductArrayDesc(String[])}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setRestrictedMsisdnAccess(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setRestrictedMsisdnAccessFlag(boolean)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setRestrictedRechargeAccess(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setRestrictedRechargeFlag(boolean)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setReturnAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setReturnChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setToCategory(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setToCategoryDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setToCategorySeqNumber(Integer)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setToDomainCode(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setToDomainCodeDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setTransferChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setTransferType(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setTransferTypeDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlReturnAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlTransferAllowedFlag(boolean)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlTransferLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlWithdrawAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setUncntrlWithdrawLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setWithdrawAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#setWithdrawChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleViewResponseVO#toString()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getChannelTransferRuleRequestVO()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getCntrlReturnLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getCntrlReturnLevelDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getCntrlTransferLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getCntrlTransferLevelDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getCntrlWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getCntrlWithdrawLevelDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getDirectTransferAllowed()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getDomainCode()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getDomainCodeDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedReturnCategory()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedReturnCategoryDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedReturnLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedReturnLevelDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedTransferCategory()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedTransferCategoryDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedTransferLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedTransferLevelDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedWithdrawCategory()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedWithdrawCategoryDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFixedWithdrawLevelDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFromCategory()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFromCategoryDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getFromCategorySeqNumber()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getParentAssocationAllowed()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getParentAssocationAllowedDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getProductArray()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getProductArrayDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getRestrictedMsisdnAccess()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getRestrictedRechargeAccess()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getReturnAllowed()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getReturnChnlBypassAllowed()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getToCategory()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getToCategoryDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getToCategorySeqNumber()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getToDomainCode()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getToDomainCodeDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getTransferChnlBypassAllowed()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getTransferType()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getTransferTypeDesc()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getUncntrlReturnAllowed()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getUncntrlReturnLevel()}
     *   <li>{@link ChannelTransferRuleViewResponseVO#getUncntrlReturnLevelDesc()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelTransferRuleViewResponseVO actualChannelTransferRuleViewResponseVO = new ChannelTransferRuleViewResponseVO();
        ChannelTransferRuleRequestVO channelTransferRuleRequestVO = new ChannelTransferRuleRequestVO();
        channelTransferRuleRequestVO.setCntrlReturnLevel("Cntrl Return Level");
        channelTransferRuleRequestVO.setCntrlTransferLevel("Cntrl Transfer Level");
        channelTransferRuleRequestVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        channelTransferRuleRequestVO.setDirectTransferAllowed("Direct Transfer Allowed");
        channelTransferRuleRequestVO.setDomainCode("Domain Code");
        channelTransferRuleRequestVO.setFixedReturnCategory("Fixed Return Category");
        channelTransferRuleRequestVO.setFixedReturnLevel("Fixed Return Level");
        channelTransferRuleRequestVO.setFixedTransferCategory("Fixed Transfer Category");
        channelTransferRuleRequestVO.setFixedTransferLevel("Fixed Transfer Level");
        channelTransferRuleRequestVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        channelTransferRuleRequestVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        channelTransferRuleRequestVO.setFromCategory("jane.doe@example.org");
        channelTransferRuleRequestVO.setParentAssocationAllowed("Parent Assocation Allowed");
        channelTransferRuleRequestVO.setProductArray(new String[]{"Product Array"});
        channelTransferRuleRequestVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        channelTransferRuleRequestVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        channelTransferRuleRequestVO.setReturnAllowed("Return Allowed");
        channelTransferRuleRequestVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        channelTransferRuleRequestVO.setToCategory("To Category");
        channelTransferRuleRequestVO.setToDomainCode("To Domain Code");
        channelTransferRuleRequestVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        channelTransferRuleRequestVO.setTransferType("Transfer Type");
        channelTransferRuleRequestVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        channelTransferRuleRequestVO.setUncntrlReturnLevel("Uncntrl Return Level");
        channelTransferRuleRequestVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        channelTransferRuleRequestVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        channelTransferRuleRequestVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        channelTransferRuleRequestVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        channelTransferRuleRequestVO.setWithdrawAllowed("Withdraw Allowed");
        channelTransferRuleRequestVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        actualChannelTransferRuleViewResponseVO.setChannelTransferRuleRequestVO(channelTransferRuleRequestVO);
        actualChannelTransferRuleViewResponseVO.setChnlByPassFlag(true);
        actualChannelTransferRuleViewResponseVO.setCntrlReturnLevel("Cntrl Return Level");
        actualChannelTransferRuleViewResponseVO.setCntrlReturnLevelDesc("Cntrl Return Level Desc");
        actualChannelTransferRuleViewResponseVO.setCntrlTransferLevel("Cntrl Transfer Level");
        actualChannelTransferRuleViewResponseVO.setCntrlTransferLevelDesc("Cntrl Transfer Level Desc");
        actualChannelTransferRuleViewResponseVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        actualChannelTransferRuleViewResponseVO.setCntrlWithdrawLevelDesc("Cntrl Withdraw Level Desc");
        actualChannelTransferRuleViewResponseVO.setDirectTransferAllowed("Direct Transfer Allowed");
        actualChannelTransferRuleViewResponseVO.setDomainCode("Domain Code");
        actualChannelTransferRuleViewResponseVO.setDomainCodeDesc("Domain Code Desc");
        actualChannelTransferRuleViewResponseVO.setFixedReturnCategory("Fixed Return Category");
        actualChannelTransferRuleViewResponseVO.setFixedReturnCategoryDesc("Fixed Return Category Desc");
        actualChannelTransferRuleViewResponseVO.setFixedReturnLevel("Fixed Return Level");
        actualChannelTransferRuleViewResponseVO.setFixedReturnLevelDesc("Fixed Return Level Desc");
        actualChannelTransferRuleViewResponseVO.setFixedTransferCategory("Fixed Transfer Category");
        actualChannelTransferRuleViewResponseVO.setFixedTransferCategoryDesc("Fixed Transfer Category Desc");
        actualChannelTransferRuleViewResponseVO.setFixedTransferLevel("Fixed Transfer Level");
        actualChannelTransferRuleViewResponseVO.setFixedTransferLevelDesc("Fixed Transfer Level Desc");
        actualChannelTransferRuleViewResponseVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        actualChannelTransferRuleViewResponseVO.setFixedWithdrawCategoryDesc("Fixed Withdraw Category Desc");
        actualChannelTransferRuleViewResponseVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        actualChannelTransferRuleViewResponseVO.setFixedWithdrawLevelDesc("Fixed Withdraw Level Desc");
        actualChannelTransferRuleViewResponseVO.setFromCategory("jane.doe@example.org");
        actualChannelTransferRuleViewResponseVO.setFromCategoryDesc("jane.doe@example.org");
        actualChannelTransferRuleViewResponseVO.setFromCategorySeqNumber(10);
        actualChannelTransferRuleViewResponseVO.setParentAssocationAllowed("Parent Assocation Allowed");
        actualChannelTransferRuleViewResponseVO.setParentAssocationAllowedDesc("Parent Assocation Allowed Desc");
        actualChannelTransferRuleViewResponseVO.setParentAssociationAllowedFlag(true);
        String[] productArray = new String[]{"Product Array"};
        actualChannelTransferRuleViewResponseVO.setProductArray(productArray);
        String[] productArrayDesc = new String[]{"Product Array Desc"};
        actualChannelTransferRuleViewResponseVO.setProductArrayDesc(productArrayDesc);
        actualChannelTransferRuleViewResponseVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        actualChannelTransferRuleViewResponseVO.setRestrictedMsisdnAccessFlag(true);
        actualChannelTransferRuleViewResponseVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        actualChannelTransferRuleViewResponseVO.setRestrictedRechargeFlag(true);
        actualChannelTransferRuleViewResponseVO.setReturnAllowed("Return Allowed");
        actualChannelTransferRuleViewResponseVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        actualChannelTransferRuleViewResponseVO.setToCategory("To Category");
        actualChannelTransferRuleViewResponseVO.setToCategoryDesc("To Category Desc");
        actualChannelTransferRuleViewResponseVO.setToCategorySeqNumber(10);
        actualChannelTransferRuleViewResponseVO.setToDomainCode("To Domain Code");
        actualChannelTransferRuleViewResponseVO.setToDomainCodeDesc("To Domain Code Desc");
        actualChannelTransferRuleViewResponseVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        actualChannelTransferRuleViewResponseVO.setTransferType("Transfer Type");
        actualChannelTransferRuleViewResponseVO.setTransferTypeDesc("Transfer Type Desc");
        actualChannelTransferRuleViewResponseVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        actualChannelTransferRuleViewResponseVO.setUncntrlReturnLevel("Uncntrl Return Level");
        actualChannelTransferRuleViewResponseVO.setUncntrlReturnLevelDesc("Uncntrl Return Level Desc");
        actualChannelTransferRuleViewResponseVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        actualChannelTransferRuleViewResponseVO.setUncntrlTransferAllowedFlag(true);
        actualChannelTransferRuleViewResponseVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        actualChannelTransferRuleViewResponseVO.setUncntrlTransferLevelDesc("Uncntrl Transfer Level Desc");
        actualChannelTransferRuleViewResponseVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        actualChannelTransferRuleViewResponseVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        actualChannelTransferRuleViewResponseVO.setUncntrlWithdrawLevelDesc("Uncntrl Withdraw Level Desc");
        actualChannelTransferRuleViewResponseVO.setWithdrawAllowed("Withdraw Allowed");
        actualChannelTransferRuleViewResponseVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        actualChannelTransferRuleViewResponseVO.toString();
        assertSame(channelTransferRuleRequestVO, actualChannelTransferRuleViewResponseVO.getChannelTransferRuleRequestVO());
        assertEquals("Cntrl Return Level", actualChannelTransferRuleViewResponseVO.getCntrlReturnLevel());
        assertEquals("Cntrl Return Level Desc", actualChannelTransferRuleViewResponseVO.getCntrlReturnLevelDesc());
        assertEquals("Cntrl Transfer Level", actualChannelTransferRuleViewResponseVO.getCntrlTransferLevel());
        assertEquals("Cntrl Transfer Level Desc", actualChannelTransferRuleViewResponseVO.getCntrlTransferLevelDesc());
        assertEquals("Cntrl Withdraw Level", actualChannelTransferRuleViewResponseVO.getCntrlWithdrawLevel());
        assertEquals("Cntrl Withdraw Level Desc", actualChannelTransferRuleViewResponseVO.getCntrlWithdrawLevelDesc());
        assertEquals("Direct Transfer Allowed", actualChannelTransferRuleViewResponseVO.getDirectTransferAllowed());
        assertEquals("Domain Code", actualChannelTransferRuleViewResponseVO.getDomainCode());
        assertEquals("Domain Code Desc", actualChannelTransferRuleViewResponseVO.getDomainCodeDesc());
        assertEquals("Fixed Return Category", actualChannelTransferRuleViewResponseVO.getFixedReturnCategory());
        assertEquals("Fixed Return Category Desc", actualChannelTransferRuleViewResponseVO.getFixedReturnCategoryDesc());
        assertEquals("Fixed Return Level", actualChannelTransferRuleViewResponseVO.getFixedReturnLevel());
        assertEquals("Fixed Return Level Desc", actualChannelTransferRuleViewResponseVO.getFixedReturnLevelDesc());
        assertEquals("Fixed Transfer Category", actualChannelTransferRuleViewResponseVO.getFixedTransferCategory());
        assertEquals("Fixed Transfer Category Desc",
                actualChannelTransferRuleViewResponseVO.getFixedTransferCategoryDesc());
        assertEquals("Fixed Transfer Level", actualChannelTransferRuleViewResponseVO.getFixedTransferLevel());
        assertEquals("Fixed Transfer Level Desc", actualChannelTransferRuleViewResponseVO.getFixedTransferLevelDesc());
        assertEquals("Fixed Withdraw Category", actualChannelTransferRuleViewResponseVO.getFixedWithdrawCategory());
        assertEquals("Fixed Withdraw Category Desc",
                actualChannelTransferRuleViewResponseVO.getFixedWithdrawCategoryDesc());
        assertEquals("Fixed Withdraw Level", actualChannelTransferRuleViewResponseVO.getFixedWithdrawLevel());
        assertEquals("Fixed Withdraw Level Desc", actualChannelTransferRuleViewResponseVO.getFixedWithdrawLevelDesc());
        assertEquals("jane.doe@example.org", actualChannelTransferRuleViewResponseVO.getFromCategory());
        assertEquals("jane.doe@example.org", actualChannelTransferRuleViewResponseVO.getFromCategoryDesc());
        assertEquals(10, actualChannelTransferRuleViewResponseVO.getFromCategorySeqNumber().intValue());
        assertEquals("Parent Assocation Allowed", actualChannelTransferRuleViewResponseVO.getParentAssocationAllowed());
        assertEquals("Parent Assocation Allowed Desc",
                actualChannelTransferRuleViewResponseVO.getParentAssocationAllowedDesc());
        assertSame(productArray, actualChannelTransferRuleViewResponseVO.getProductArray());
        assertSame(productArrayDesc, actualChannelTransferRuleViewResponseVO.getProductArrayDesc());
        assertEquals("Restricted Msisdn Access", actualChannelTransferRuleViewResponseVO.getRestrictedMsisdnAccess());
        assertEquals("Restricted Recharge Access", actualChannelTransferRuleViewResponseVO.getRestrictedRechargeAccess());
        assertEquals("Return Allowed", actualChannelTransferRuleViewResponseVO.getReturnAllowed());
        assertEquals("Return Chnl Bypass Allowed", actualChannelTransferRuleViewResponseVO.getReturnChnlBypassAllowed());
        assertEquals("To Category", actualChannelTransferRuleViewResponseVO.getToCategory());
        assertEquals("To Category Desc", actualChannelTransferRuleViewResponseVO.getToCategoryDesc());
        assertEquals(10, actualChannelTransferRuleViewResponseVO.getToCategorySeqNumber().intValue());
        assertEquals("To Domain Code", actualChannelTransferRuleViewResponseVO.getToDomainCode());
        assertEquals("To Domain Code Desc", actualChannelTransferRuleViewResponseVO.getToDomainCodeDesc());
        assertEquals("Transfer Chnl Bypass Allowed",
                actualChannelTransferRuleViewResponseVO.getTransferChnlBypassAllowed());
        assertEquals("Transfer Type", actualChannelTransferRuleViewResponseVO.getTransferType());
        assertEquals("Transfer Type Desc", actualChannelTransferRuleViewResponseVO.getTransferTypeDesc());
        assertEquals("Uncntrl Return Allowed", actualChannelTransferRuleViewResponseVO.getUncntrlReturnAllowed());
        assertEquals("Uncntrl Return Level", actualChannelTransferRuleViewResponseVO.getUncntrlReturnLevel());
        assertEquals("Uncntrl Return Level Desc", actualChannelTransferRuleViewResponseVO.getUncntrlReturnLevelDesc());
    }
}

