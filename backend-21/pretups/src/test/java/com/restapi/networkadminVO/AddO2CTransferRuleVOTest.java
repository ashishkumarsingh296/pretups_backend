package com.restapi.networkadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class AddO2CTransferRuleVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddO2CTransferRuleVO}
     *   <li>{@link AddO2CTransferRuleVO#setApprovalRequired(String)}
     *   <li>{@link AddO2CTransferRuleVO#setCntrlReturnLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setCntrlTransferLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setCntrlWithdrawLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setCreatedBy(String)}
     *   <li>{@link AddO2CTransferRuleVO#setCreatedOn(Date)}
     *   <li>{@link AddO2CTransferRuleVO#setDirectTransferAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setDomainCode(String)}
     *   <li>{@link AddO2CTransferRuleVO#setDpAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFirstApprovalLimit(long)}
     *   <li>{@link AddO2CTransferRuleVO#setFixedReturnCategory(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFixedReturnLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFixedTransferCategory(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFixedTransferLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFixedWithdrawCategory(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFixedWithdrawLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFocAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFocTransferType(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFromCategory(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFromCategoryDes(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFromDomainDes(String)}
     *   <li>{@link AddO2CTransferRuleVO#setFromSeqNo(int)}
     *   <li>{@link AddO2CTransferRuleVO#setLastModifiedTime(long)}
     *   <li>{@link AddO2CTransferRuleVO#setModifiedBy(String)}
     *   <li>{@link AddO2CTransferRuleVO#setModifiedOn(Date)}
     *   <li>{@link AddO2CTransferRuleVO#setNetworkCode(String)}
     *   <li>{@link AddO2CTransferRuleVO#setOwnerCategoryName(String)}
     *   <li>{@link AddO2CTransferRuleVO#setParentAssocationAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setPreviousStatus(String)}
     *   <li>{@link AddO2CTransferRuleVO#setProductArray(String[])}
     *   <li>{@link AddO2CTransferRuleVO#setProductVOList(ArrayList)}
     *   <li>{@link AddO2CTransferRuleVO#setRestrictedMsisdnAccess(String)}
     *   <li>{@link AddO2CTransferRuleVO#setRestrictedRechargeAccess(String)}
     *   <li>{@link AddO2CTransferRuleVO#setReturnAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setReturnChnlBypassAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setRuleType(String)}
     *   <li>{@link AddO2CTransferRuleVO#setSecondApprovalLimit(long)}
     *   <li>{@link AddO2CTransferRuleVO#setStatus(String)}
     *   <li>{@link AddO2CTransferRuleVO#setStatusDesc(String)}
     *   <li>{@link AddO2CTransferRuleVO#setToCategory(String)}
     *   <li>{@link AddO2CTransferRuleVO#setToCategoryDes(String)}
     *   <li>{@link AddO2CTransferRuleVO#setToDomainCode(String)}
     *   <li>{@link AddO2CTransferRuleVO#setToDomainDes(String)}
     *   <li>{@link AddO2CTransferRuleVO#setToSeqNo(int)}
     *   <li>{@link AddO2CTransferRuleVO#setTransferAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setTransferChnlBypassAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setTransferRuleID(String)}
     *   <li>{@link AddO2CTransferRuleVO#setTransferRuleType(String)}
     *   <li>{@link AddO2CTransferRuleVO#setTransferType(String)}
     *   <li>{@link AddO2CTransferRuleVO#setTransferTypeDesc(String)}
     *   <li>{@link AddO2CTransferRuleVO#setType(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlReturnAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlReturnLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlTransferAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlTransferAllowedTmp(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlTransferLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlWithdrawAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setUncntrlWithdrawLevel(String)}
     *   <li>{@link AddO2CTransferRuleVO#setWithdrawAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#setWithdrawChnlBypassAllowed(String)}
     *   <li>{@link AddO2CTransferRuleVO#getApprovalRequired()}
     *   <li>{@link AddO2CTransferRuleVO#getCntrlReturnLevel()}
     *   <li>{@link AddO2CTransferRuleVO#getCntrlTransferLevel()}
     *   <li>{@link AddO2CTransferRuleVO#getCntrlWithdrawLevel()}
     *   <li>{@link AddO2CTransferRuleVO#getCreatedBy()}
     *   <li>{@link AddO2CTransferRuleVO#getCreatedOn()}
     *   <li>{@link AddO2CTransferRuleVO#getDirectTransferAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getDomainCode()}
     *   <li>{@link AddO2CTransferRuleVO#getDpAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getFirstApprovalLimit()}
     *   <li>{@link AddO2CTransferRuleVO#getFixedReturnCategory()}
     *   <li>{@link AddO2CTransferRuleVO#getFixedReturnLevel()}
     *   <li>{@link AddO2CTransferRuleVO#getFixedTransferCategory()}
     *   <li>{@link AddO2CTransferRuleVO#getFixedTransferLevel()}
     *   <li>{@link AddO2CTransferRuleVO#getFixedWithdrawCategory()}
     *   <li>{@link AddO2CTransferRuleVO#getFixedWithdrawLevel()}
     *   <li>{@link AddO2CTransferRuleVO#getFocAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getFocTransferType()}
     *   <li>{@link AddO2CTransferRuleVO#getFromCategory()}
     *   <li>{@link AddO2CTransferRuleVO#getFromCategoryDes()}
     *   <li>{@link AddO2CTransferRuleVO#getFromDomainDes()}
     *   <li>{@link AddO2CTransferRuleVO#getFromSeqNo()}
     *   <li>{@link AddO2CTransferRuleVO#getLastModifiedTime()}
     *   <li>{@link AddO2CTransferRuleVO#getModifiedBy()}
     *   <li>{@link AddO2CTransferRuleVO#getModifiedOn()}
     *   <li>{@link AddO2CTransferRuleVO#getNetworkCode()}
     *   <li>{@link AddO2CTransferRuleVO#getOwnerCategoryName()}
     *   <li>{@link AddO2CTransferRuleVO#getParentAssocationAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getPreviousStatus()}
     *   <li>{@link AddO2CTransferRuleVO#getProductArray()}
     *   <li>{@link AddO2CTransferRuleVO#getProductVOList()}
     *   <li>{@link AddO2CTransferRuleVO#getRestrictedMsisdnAccess()}
     *   <li>{@link AddO2CTransferRuleVO#getRestrictedRechargeAccess()}
     *   <li>{@link AddO2CTransferRuleVO#getReturnAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getReturnChnlBypassAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getRuleType()}
     *   <li>{@link AddO2CTransferRuleVO#getSecondApprovalLimit()}
     *   <li>{@link AddO2CTransferRuleVO#getStatus()}
     *   <li>{@link AddO2CTransferRuleVO#getStatusDesc()}
     *   <li>{@link AddO2CTransferRuleVO#getToCategory()}
     *   <li>{@link AddO2CTransferRuleVO#getToCategoryDes()}
     *   <li>{@link AddO2CTransferRuleVO#getToDomainCode()}
     *   <li>{@link AddO2CTransferRuleVO#getToDomainDes()}
     *   <li>{@link AddO2CTransferRuleVO#getToSeqNo()}
     *   <li>{@link AddO2CTransferRuleVO#getTransferAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getTransferChnlBypassAllowed()}
     *   <li>{@link AddO2CTransferRuleVO#getTransferRuleID()}
     *   <li>{@link AddO2CTransferRuleVO#getTransferRuleType()}
     *   <li>{@link AddO2CTransferRuleVO#getTransferType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AddO2CTransferRuleVO actualAddO2CTransferRuleVO = new AddO2CTransferRuleVO();
        actualAddO2CTransferRuleVO.setApprovalRequired("Approval Required");
        actualAddO2CTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        actualAddO2CTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        actualAddO2CTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        actualAddO2CTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        Date createdOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualAddO2CTransferRuleVO.setCreatedOn(createdOn);
        actualAddO2CTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        actualAddO2CTransferRuleVO.setDomainCode("Domain Code");
        actualAddO2CTransferRuleVO.setDpAllowed("Dp Allowed");
        actualAddO2CTransferRuleVO.setFirstApprovalLimit(42L);
        actualAddO2CTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        actualAddO2CTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        actualAddO2CTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        actualAddO2CTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        actualAddO2CTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        actualAddO2CTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        actualAddO2CTransferRuleVO.setFocAllowed("Foc Allowed");
        actualAddO2CTransferRuleVO.setFocTransferType("Foc Transfer Type");
        actualAddO2CTransferRuleVO.setFromCategory("jane.doe@example.org");
        actualAddO2CTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        actualAddO2CTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        actualAddO2CTransferRuleVO.setFromSeqNo(1);
        actualAddO2CTransferRuleVO.setLastModifiedTime(1L);
        actualAddO2CTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        Date modifiedOn = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        actualAddO2CTransferRuleVO.setModifiedOn(modifiedOn);
        actualAddO2CTransferRuleVO.setNetworkCode("Network Code");
        actualAddO2CTransferRuleVO.setOwnerCategoryName("Owner Category Name");
        actualAddO2CTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        actualAddO2CTransferRuleVO.setPreviousStatus("Previous Status");
        String[] productArray = new String[]{"Product Array"};
        actualAddO2CTransferRuleVO.setProductArray(productArray);
        ArrayList productVOList = new ArrayList();
        actualAddO2CTransferRuleVO.setProductVOList(productVOList);
        actualAddO2CTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        actualAddO2CTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        actualAddO2CTransferRuleVO.setReturnAllowed("Return Allowed");
        actualAddO2CTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        actualAddO2CTransferRuleVO.setRuleType("Rule Type");
        actualAddO2CTransferRuleVO.setSecondApprovalLimit(42L);
        actualAddO2CTransferRuleVO.setStatus("Status");
        actualAddO2CTransferRuleVO.setStatusDesc("Status Desc");
        actualAddO2CTransferRuleVO.setToCategory("To Category");
        actualAddO2CTransferRuleVO.setToCategoryDes("To Category Des");
        actualAddO2CTransferRuleVO.setToDomainCode("To Domain Code");
        actualAddO2CTransferRuleVO.setToDomainDes("To Domain Des");
        actualAddO2CTransferRuleVO.setToSeqNo(1);
        actualAddO2CTransferRuleVO.setTransferAllowed("Transfer Allowed");
        actualAddO2CTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        actualAddO2CTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        actualAddO2CTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        actualAddO2CTransferRuleVO.setTransferType("Transfer Type");
        actualAddO2CTransferRuleVO.setTransferTypeDesc("Transfer Type Desc");
        actualAddO2CTransferRuleVO.setType("Type");
        actualAddO2CTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        actualAddO2CTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        actualAddO2CTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        actualAddO2CTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        actualAddO2CTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        actualAddO2CTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        actualAddO2CTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        actualAddO2CTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        actualAddO2CTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        assertEquals("Approval Required", actualAddO2CTransferRuleVO.getApprovalRequired());
        assertEquals("Cntrl Return Level", actualAddO2CTransferRuleVO.getCntrlReturnLevel());
        assertEquals("Cntrl Transfer Level", actualAddO2CTransferRuleVO.getCntrlTransferLevel());
        assertEquals("Cntrl Withdraw Level", actualAddO2CTransferRuleVO.getCntrlWithdrawLevel());
        assertEquals("Jan 1, 2020 8:00am GMT+0100", actualAddO2CTransferRuleVO.getCreatedBy());
        assertSame(createdOn, actualAddO2CTransferRuleVO.getCreatedOn());
        assertEquals("Direct Transfer Allowed", actualAddO2CTransferRuleVO.getDirectTransferAllowed());
        assertEquals("Domain Code", actualAddO2CTransferRuleVO.getDomainCode());
        assertEquals("Dp Allowed", actualAddO2CTransferRuleVO.getDpAllowed());
        assertEquals(42L, actualAddO2CTransferRuleVO.getFirstApprovalLimit());
        assertEquals("Fixed Return Category", actualAddO2CTransferRuleVO.getFixedReturnCategory());
        assertEquals("Fixed Return Level", actualAddO2CTransferRuleVO.getFixedReturnLevel());
        assertEquals("Fixed Transfer Category", actualAddO2CTransferRuleVO.getFixedTransferCategory());
        assertEquals("Fixed Transfer Level", actualAddO2CTransferRuleVO.getFixedTransferLevel());
        assertEquals("Fixed Withdraw Category", actualAddO2CTransferRuleVO.getFixedWithdrawCategory());
        assertEquals("Fixed Withdraw Level", actualAddO2CTransferRuleVO.getFixedWithdrawLevel());
        assertEquals("Foc Allowed", actualAddO2CTransferRuleVO.getFocAllowed());
        assertEquals("Foc Transfer Type", actualAddO2CTransferRuleVO.getFocTransferType());
        assertEquals("jane.doe@example.org", actualAddO2CTransferRuleVO.getFromCategory());
        assertEquals("jane.doe@example.org", actualAddO2CTransferRuleVO.getFromCategoryDes());
        assertEquals("jane.doe@example.org", actualAddO2CTransferRuleVO.getFromDomainDes());
        assertEquals(1, actualAddO2CTransferRuleVO.getFromSeqNo());
        assertEquals(1L, actualAddO2CTransferRuleVO.getLastModifiedTime());
        assertEquals("Jan 1, 2020 9:00am GMT+0100", actualAddO2CTransferRuleVO.getModifiedBy());
        assertSame(modifiedOn, actualAddO2CTransferRuleVO.getModifiedOn());
        assertEquals("Network Code", actualAddO2CTransferRuleVO.getNetworkCode());
        assertEquals("Owner Category Name", actualAddO2CTransferRuleVO.getOwnerCategoryName());
        assertEquals("Parent Assocation Allowed", actualAddO2CTransferRuleVO.getParentAssocationAllowed());
        assertEquals("Previous Status", actualAddO2CTransferRuleVO.getPreviousStatus());
        assertSame(productArray, actualAddO2CTransferRuleVO.getProductArray());
        assertSame(productVOList, actualAddO2CTransferRuleVO.getProductVOList());
        assertEquals("Restricted Msisdn Access", actualAddO2CTransferRuleVO.getRestrictedMsisdnAccess());
        assertEquals("Restricted Recharge Access", actualAddO2CTransferRuleVO.getRestrictedRechargeAccess());
        assertEquals("Return Allowed", actualAddO2CTransferRuleVO.getReturnAllowed());
        assertEquals("Return Chnl Bypass Allowed", actualAddO2CTransferRuleVO.getReturnChnlBypassAllowed());
        assertEquals("Rule Type", actualAddO2CTransferRuleVO.getRuleType());
        assertEquals(42L, actualAddO2CTransferRuleVO.getSecondApprovalLimit());
        assertEquals("Status", actualAddO2CTransferRuleVO.getStatus());
        assertEquals("Status Desc", actualAddO2CTransferRuleVO.getStatusDesc());
        assertEquals("To Category", actualAddO2CTransferRuleVO.getToCategory());
        assertEquals("To Category Des", actualAddO2CTransferRuleVO.getToCategoryDes());
        assertEquals("To Domain Code", actualAddO2CTransferRuleVO.getToDomainCode());
        assertEquals("To Domain Des", actualAddO2CTransferRuleVO.getToDomainDes());
        assertEquals(1, actualAddO2CTransferRuleVO.getToSeqNo());
        assertEquals("Transfer Allowed", actualAddO2CTransferRuleVO.getTransferAllowed());
        assertEquals("Transfer Chnl Bypass Allowed", actualAddO2CTransferRuleVO.getTransferChnlBypassAllowed());
        assertEquals("Transfer Rule ID", actualAddO2CTransferRuleVO.getTransferRuleID());
        assertEquals("Transfer Rule Type", actualAddO2CTransferRuleVO.getTransferRuleType());
        assertEquals("Transfer Type", actualAddO2CTransferRuleVO.getTransferType());
    }
}

