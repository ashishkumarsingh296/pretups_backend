package com.restapi.superadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class ChannelTransferRuleVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelTransferRuleVO}
     *   <li>{@link ChannelTransferRuleVO#setApprovalRequired(String)}
     *   <li>{@link ChannelTransferRuleVO#setCntrlReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setCntrlTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setCntrlWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setCreatedBy(String)}
     *   <li>{@link ChannelTransferRuleVO#setCreatedOn(Date)}
     *   <li>{@link ChannelTransferRuleVO#setDirectTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setDomainCode(String)}
     *   <li>{@link ChannelTransferRuleVO#setDpAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setFirstApprovalLimit(Long)}
     *   <li>{@link ChannelTransferRuleVO#setFixedReturnCategory(String)}
     *   <li>{@link ChannelTransferRuleVO#setFixedReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setFixedTransferCategory(String)}
     *   <li>{@link ChannelTransferRuleVO#setFixedTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setFixedWithdrawCategory(String)}
     *   <li>{@link ChannelTransferRuleVO#setFixedWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setFocAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setFocTransferType(String)}
     *   <li>{@link ChannelTransferRuleVO#setFromCategory(String)}
     *   <li>{@link ChannelTransferRuleVO#setFromCategoryDes(String)}
     *   <li>{@link ChannelTransferRuleVO#setFromDomainDes(String)}
     *   <li>{@link ChannelTransferRuleVO#setFromSeqNo(Integer)}
     *   <li>{@link ChannelTransferRuleVO#setLastModifiedTime(Long)}
     *   <li>{@link ChannelTransferRuleVO#setModifiedBy(String)}
     *   <li>{@link ChannelTransferRuleVO#setModifiedOn(Date)}
     *   <li>{@link ChannelTransferRuleVO#setNetworkCode(String)}
     *   <li>{@link ChannelTransferRuleVO#setParentAssocationAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setPreviousStatus(String)}
     *   <li>{@link ChannelTransferRuleVO#setProductArray(String[])}
     *   <li>{@link ChannelTransferRuleVO#setProductVOList(ArrayList)}
     *   <li>{@link ChannelTransferRuleVO#setRestrictedMsisdnAccess(String)}
     *   <li>{@link ChannelTransferRuleVO#setRestrictedRechargeAccess(String)}
     *   <li>{@link ChannelTransferRuleVO#setReturnAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setReturnChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setRuleType(String)}
     *   <li>{@link ChannelTransferRuleVO#setSecondApprovalLimit(Long)}
     *   <li>{@link ChannelTransferRuleVO#setStatus(String)}
     *   <li>{@link ChannelTransferRuleVO#setStatusDesc(String)}
     *   <li>{@link ChannelTransferRuleVO#setToCategory(String)}
     *   <li>{@link ChannelTransferRuleVO#setToCategoryDes(String)}
     *   <li>{@link ChannelTransferRuleVO#setToDomainCode(String)}
     *   <li>{@link ChannelTransferRuleVO#setToDomainDes(String)}
     *   <li>{@link ChannelTransferRuleVO#setToSeqNo(Integer)}
     *   <li>{@link ChannelTransferRuleVO#setTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setTransferChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setTransferRuleID(String)}
     *   <li>{@link ChannelTransferRuleVO#setTransferRuleType(String)}
     *   <li>{@link ChannelTransferRuleVO#setTransferType(String)}
     *   <li>{@link ChannelTransferRuleVO#setType(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlReturnAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlReturnLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlTransferAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlTransferAllowedTmp(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlTransferLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlWithdrawAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setUncntrlWithdrawLevel(String)}
     *   <li>{@link ChannelTransferRuleVO#setWithdrawAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#setWithdrawChnlBypassAllowed(String)}
     *   <li>{@link ChannelTransferRuleVO#toString()}
     *   <li>{@link ChannelTransferRuleVO#getApprovalRequired()}
     *   <li>{@link ChannelTransferRuleVO#getCntrlReturnLevel()}
     *   <li>{@link ChannelTransferRuleVO#getCntrlTransferLevel()}
     *   <li>{@link ChannelTransferRuleVO#getCntrlWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleVO#getCreatedBy()}
     *   <li>{@link ChannelTransferRuleVO#getCreatedOn()}
     *   <li>{@link ChannelTransferRuleVO#getDirectTransferAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getDomainCode()}
     *   <li>{@link ChannelTransferRuleVO#getDpAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getFirstApprovalLimit()}
     *   <li>{@link ChannelTransferRuleVO#getFixedReturnCategory()}
     *   <li>{@link ChannelTransferRuleVO#getFixedReturnLevel()}
     *   <li>{@link ChannelTransferRuleVO#getFixedTransferCategory()}
     *   <li>{@link ChannelTransferRuleVO#getFixedTransferLevel()}
     *   <li>{@link ChannelTransferRuleVO#getFixedWithdrawCategory()}
     *   <li>{@link ChannelTransferRuleVO#getFixedWithdrawLevel()}
     *   <li>{@link ChannelTransferRuleVO#getFocAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getFocTransferType()}
     *   <li>{@link ChannelTransferRuleVO#getFromCategory()}
     *   <li>{@link ChannelTransferRuleVO#getFromCategoryDes()}
     *   <li>{@link ChannelTransferRuleVO#getFromDomainDes()}
     *   <li>{@link ChannelTransferRuleVO#getFromSeqNo()}
     *   <li>{@link ChannelTransferRuleVO#getLastModifiedTime()}
     *   <li>{@link ChannelTransferRuleVO#getModifiedBy()}
     *   <li>{@link ChannelTransferRuleVO#getModifiedOn()}
     *   <li>{@link ChannelTransferRuleVO#getNetworkCode()}
     *   <li>{@link ChannelTransferRuleVO#getParentAssocationAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getPreviousStatus()}
     *   <li>{@link ChannelTransferRuleVO#getProductArray()}
     *   <li>{@link ChannelTransferRuleVO#getProductVOList()}
     *   <li>{@link ChannelTransferRuleVO#getRestrictedMsisdnAccess()}
     *   <li>{@link ChannelTransferRuleVO#getRestrictedRechargeAccess()}
     *   <li>{@link ChannelTransferRuleVO#getReturnAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getReturnChnlBypassAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getRuleType()}
     *   <li>{@link ChannelTransferRuleVO#getSecondApprovalLimit()}
     *   <li>{@link ChannelTransferRuleVO#getStatus()}
     *   <li>{@link ChannelTransferRuleVO#getStatusDesc()}
     *   <li>{@link ChannelTransferRuleVO#getToCategory()}
     *   <li>{@link ChannelTransferRuleVO#getToCategoryDes()}
     *   <li>{@link ChannelTransferRuleVO#getToDomainCode()}
     *   <li>{@link ChannelTransferRuleVO#getToDomainDes()}
     *   <li>{@link ChannelTransferRuleVO#getToSeqNo()}
     *   <li>{@link ChannelTransferRuleVO#getTransferAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getTransferChnlBypassAllowed()}
     *   <li>{@link ChannelTransferRuleVO#getTransferRuleID()}
     *   <li>{@link ChannelTransferRuleVO#getTransferRuleType()}
     *   <li>{@link ChannelTransferRuleVO#getTransferType()}
     *   <li>{@link ChannelTransferRuleVO#getType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelTransferRuleVO actualChannelTransferRuleVO = new ChannelTransferRuleVO();
        actualChannelTransferRuleVO.setApprovalRequired("Approval Required");
        actualChannelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        actualChannelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        actualChannelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        actualChannelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualChannelTransferRuleVO.setCreatedOn(createdOn);
        actualChannelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        actualChannelTransferRuleVO.setDomainCode("Domain Code");
        actualChannelTransferRuleVO.setDpAllowed("Dp Allowed");
        actualChannelTransferRuleVO.setFirstApprovalLimit(42L);
        actualChannelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        actualChannelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        actualChannelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        actualChannelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        actualChannelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        actualChannelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        actualChannelTransferRuleVO.setFocAllowed("Foc Allowed");
        actualChannelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        actualChannelTransferRuleVO.setFromCategory("jane.doe@example.org");
        actualChannelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        actualChannelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        actualChannelTransferRuleVO.setFromSeqNo(1);
        actualChannelTransferRuleVO.setLastModifiedTime(1L);
        actualChannelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualChannelTransferRuleVO.setModifiedOn(modifiedOn);
        actualChannelTransferRuleVO.setNetworkCode("Network Code");
        actualChannelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        actualChannelTransferRuleVO.setPreviousStatus("Previous Status");
        String[] productArray = new String[]{"Product Array"};
        actualChannelTransferRuleVO.setProductArray(productArray);
        ArrayList productVOList = new ArrayList();
        actualChannelTransferRuleVO.setProductVOList(productVOList);
        actualChannelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        actualChannelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        actualChannelTransferRuleVO.setReturnAllowed("Return Allowed");
        actualChannelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        actualChannelTransferRuleVO.setRuleType("Rule Type");
        actualChannelTransferRuleVO.setSecondApprovalLimit(42L);
        actualChannelTransferRuleVO.setStatus("Status");
        actualChannelTransferRuleVO.setStatusDesc("Status Desc");
        actualChannelTransferRuleVO.setToCategory("To Category");
        actualChannelTransferRuleVO.setToCategoryDes("To Category Des");
        actualChannelTransferRuleVO.setToDomainCode("To Domain Code");
        actualChannelTransferRuleVO.setToDomainDes("To Domain Des");
        actualChannelTransferRuleVO.setToSeqNo(1);
        actualChannelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        actualChannelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        actualChannelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        actualChannelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        actualChannelTransferRuleVO.setTransferType("Transfer Type");
        actualChannelTransferRuleVO.setType("Type");
        actualChannelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        actualChannelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        actualChannelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        actualChannelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        actualChannelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        actualChannelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        actualChannelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        actualChannelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        actualChannelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        actualChannelTransferRuleVO.toString();
        assertEquals("Approval Required", actualChannelTransferRuleVO.getApprovalRequired());
        assertEquals("Cntrl Return Level", actualChannelTransferRuleVO.getCntrlReturnLevel());
        assertEquals("Cntrl Transfer Level", actualChannelTransferRuleVO.getCntrlTransferLevel());
        assertEquals("Cntrl Withdraw Level", actualChannelTransferRuleVO.getCntrlWithdrawLevel());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualChannelTransferRuleVO.getCreatedBy());
        assertSame(createdOn, actualChannelTransferRuleVO.getCreatedOn());
        assertEquals("Direct Transfer Allowed", actualChannelTransferRuleVO.getDirectTransferAllowed());
        assertEquals("Domain Code", actualChannelTransferRuleVO.getDomainCode());
        assertEquals("Dp Allowed", actualChannelTransferRuleVO.getDpAllowed());
        assertEquals(42L, actualChannelTransferRuleVO.getFirstApprovalLimit().longValue());
        assertEquals("Fixed Return Category", actualChannelTransferRuleVO.getFixedReturnCategory());
        assertEquals("Fixed Return Level", actualChannelTransferRuleVO.getFixedReturnLevel());
        assertEquals("Fixed Transfer Category", actualChannelTransferRuleVO.getFixedTransferCategory());
        assertEquals("Fixed Transfer Level", actualChannelTransferRuleVO.getFixedTransferLevel());
        assertEquals("Fixed Withdraw Category", actualChannelTransferRuleVO.getFixedWithdrawCategory());
        assertEquals("Fixed Withdraw Level", actualChannelTransferRuleVO.getFixedWithdrawLevel());
        assertEquals("Foc Allowed", actualChannelTransferRuleVO.getFocAllowed());
        assertEquals("Foc Transfer Type", actualChannelTransferRuleVO.getFocTransferType());
        assertEquals("jane.doe@example.org", actualChannelTransferRuleVO.getFromCategory());
        assertEquals("jane.doe@example.org", actualChannelTransferRuleVO.getFromCategoryDes());
        assertEquals("jane.doe@example.org", actualChannelTransferRuleVO.getFromDomainDes());
        assertEquals(1, actualChannelTransferRuleVO.getFromSeqNo().intValue());
        assertEquals(1L, actualChannelTransferRuleVO.getLastModifiedTime().longValue());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualChannelTransferRuleVO.getModifiedBy());
        assertSame(modifiedOn, actualChannelTransferRuleVO.getModifiedOn());
        assertEquals("Network Code", actualChannelTransferRuleVO.getNetworkCode());
        assertEquals("Parent Assocation Allowed", actualChannelTransferRuleVO.getParentAssocationAllowed());
        assertEquals("Previous Status", actualChannelTransferRuleVO.getPreviousStatus());
        assertSame(productArray, actualChannelTransferRuleVO.getProductArray());
        assertSame(productVOList, actualChannelTransferRuleVO.getProductVOList());
        assertEquals("Restricted Msisdn Access", actualChannelTransferRuleVO.getRestrictedMsisdnAccess());
        assertEquals("Restricted Recharge Access", actualChannelTransferRuleVO.getRestrictedRechargeAccess());
        assertEquals("Return Allowed", actualChannelTransferRuleVO.getReturnAllowed());
        assertEquals("Return Chnl Bypass Allowed", actualChannelTransferRuleVO.getReturnChnlBypassAllowed());
        assertEquals("Rule Type", actualChannelTransferRuleVO.getRuleType());
        assertEquals(42L, actualChannelTransferRuleVO.getSecondApprovalLimit().longValue());
        assertEquals("Status", actualChannelTransferRuleVO.getStatus());
        assertEquals("Status Desc", actualChannelTransferRuleVO.getStatusDesc());
        assertEquals("To Category", actualChannelTransferRuleVO.getToCategory());
        assertEquals("To Category Des", actualChannelTransferRuleVO.getToCategoryDes());
        assertEquals("To Domain Code", actualChannelTransferRuleVO.getToDomainCode());
        assertEquals("To Domain Des", actualChannelTransferRuleVO.getToDomainDes());
        assertEquals(1, actualChannelTransferRuleVO.getToSeqNo().intValue());
        assertEquals("Transfer Allowed", actualChannelTransferRuleVO.getTransferAllowed());
        assertEquals("Transfer Chnl Bypass Allowed", actualChannelTransferRuleVO.getTransferChnlBypassAllowed());
        assertEquals("Transfer Rule ID", actualChannelTransferRuleVO.getTransferRuleID());
        assertEquals("Transfer Rule Type", actualChannelTransferRuleVO.getTransferRuleType());
        assertEquals("Transfer Type", actualChannelTransferRuleVO.getTransferType());
        assertEquals("Type", actualChannelTransferRuleVO.getType());
    }
}

