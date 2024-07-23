package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

public class ChannelTransferRuleResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelTransferRuleResponseVO}
     *   <li>{@link ChannelTransferRuleResponseVO#setAcrossDomain(boolean)}
     *   <li>{@link ChannelTransferRuleResponseVO#setCategoryDomainList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setChannelTransferRuleVO(ChannelTransferRuleVO)}
     *   <li>{@link ChannelTransferRuleResponseVO#setCntrlReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setCntrlTransferLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setCntrlWithdrawLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setControllTxnLevelList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setCtrlReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setDomainName(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setFixedReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setFixedTransferLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setFixedTransferLevelList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setFixedWithdrawLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setProductList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setProductionVOList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setToCategoryList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setToDomainName(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setTransferTypeDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setTransferTypeList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setUncntrlReturnLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setUncntrlTransferLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setUncntrlWithdrawLevelDesc(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#setUncontrollTxnLevelList(ArrayList)}
     *   <li>{@link ChannelTransferRuleResponseVO#setUserCategory(String)}
     *   <li>{@link ChannelTransferRuleResponseVO#getCategoryDomainList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getCategoryList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getChannelTransferRuleVO()}
     *   <li>{@link ChannelTransferRuleResponseVO#getCntrlReturnLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getCntrlTransferLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getCntrlWithdrawLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getControllTxnLevelList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getCtrlReturnLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getDomainName()}
     *   <li>{@link ChannelTransferRuleResponseVO#getFixedReturnLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getFixedTransferLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getFixedTransferLevelList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getFixedWithdrawLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getProductList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getProductionVOList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getToCategoryList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getToDomainName()}
     *   <li>{@link ChannelTransferRuleResponseVO#getTransferTypeDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getTransferTypeList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getUncntrlReturnLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getUncntrlTransferLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getUncntrlWithdrawLevelDesc()}
     *   <li>{@link ChannelTransferRuleResponseVO#getUncontrollTxnLevelList()}
     *   <li>{@link ChannelTransferRuleResponseVO#getUserCategory()}
     *   <li>{@link ChannelTransferRuleResponseVO#isAcrossDomain()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelTransferRuleResponseVO actualChannelTransferRuleResponseVO = new ChannelTransferRuleResponseVO();
        actualChannelTransferRuleResponseVO.setAcrossDomain(true);
        ArrayList categoryDomainList = new ArrayList();
        actualChannelTransferRuleResponseVO.setCategoryDomainList(categoryDomainList);
        ArrayList categoryList = new ArrayList();
        actualChannelTransferRuleResponseVO.setCategoryList(categoryList);
        ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleVO();
        channelTransferRuleVO.setApprovalRequired("Approval Required");
        channelTransferRuleVO.setCntrlReturnLevel("Cntrl Return Level");
        channelTransferRuleVO.setCntrlTransferLevel("Cntrl Transfer Level");
        channelTransferRuleVO.setCntrlWithdrawLevel("Cntrl Withdraw Level");
        channelTransferRuleVO.setCreatedBy("Jan 1, 2020 8:00am GMT+0100");
        channelTransferRuleVO
                .setCreatedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        channelTransferRuleVO.setDirectTransferAllowed("Direct Transfer Allowed");
        channelTransferRuleVO.setDomainCode("Domain Code");
        channelTransferRuleVO.setDpAllowed("Dp Allowed");
        channelTransferRuleVO.setFirstApprovalLimit(42L);
        channelTransferRuleVO.setFixedReturnCategory("Fixed Return Category");
        channelTransferRuleVO.setFixedReturnLevel("Fixed Return Level");
        channelTransferRuleVO.setFixedTransferCategory("Fixed Transfer Category");
        channelTransferRuleVO.setFixedTransferLevel("Fixed Transfer Level");
        channelTransferRuleVO.setFixedWithdrawCategory("Fixed Withdraw Category");
        channelTransferRuleVO.setFixedWithdrawLevel("Fixed Withdraw Level");
        channelTransferRuleVO.setFocAllowed("Foc Allowed");
        channelTransferRuleVO.setFocTransferType("Foc Transfer Type");
        channelTransferRuleVO.setFromCategory("jane.doe@example.org");
        channelTransferRuleVO.setFromCategoryDes("jane.doe@example.org");
        channelTransferRuleVO.setFromDomainDes("jane.doe@example.org");
        channelTransferRuleVO.setFromSeqNo(1);
        channelTransferRuleVO.setLastModifiedTime(1L);
        channelTransferRuleVO.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
        channelTransferRuleVO
                .setModifiedOn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
        channelTransferRuleVO.setNetworkCode("Network Code");
        channelTransferRuleVO.setOwnerCategoryName("Owner Category Name");
        channelTransferRuleVO.setParentAssocationAllowed("Parent Assocation Allowed");
        channelTransferRuleVO.setPreviousStatus("Previous Status");
        channelTransferRuleVO.setProductArray(new String[]{"Product Array"});
        channelTransferRuleVO.setProductVOList(new ArrayList());
        channelTransferRuleVO.setRestrictedMsisdnAccess("Restricted Msisdn Access");
        channelTransferRuleVO.setRestrictedRechargeAccess("Restricted Recharge Access");
        channelTransferRuleVO.setReturnAllowed("Return Allowed");
        channelTransferRuleVO.setReturnChnlBypassAllowed("Return Chnl Bypass Allowed");
        channelTransferRuleVO.setRuleType("Rule Type");
        channelTransferRuleVO.setSecondApprovalLimit(42L);
        channelTransferRuleVO.setStatus("Status");
        channelTransferRuleVO.setStatusDesc("Status Desc");
        channelTransferRuleVO.setToCategory("To Category");
        channelTransferRuleVO.setToCategoryDes("To Category Des");
        channelTransferRuleVO.setToDomainCode("To Domain Code");
        channelTransferRuleVO.setToDomainDes("To Domain Des");
        channelTransferRuleVO.setToSeqNo(1);
        channelTransferRuleVO.setTransferAllowed("Transfer Allowed");
        channelTransferRuleVO.setTransferChnlBypassAllowed("Transfer Chnl Bypass Allowed");
        channelTransferRuleVO.setTransferRuleID("Transfer Rule ID");
        channelTransferRuleVO.setTransferRuleType("Transfer Rule Type");
        channelTransferRuleVO.setTransferType("Transfer Type");
        channelTransferRuleVO.setTransferTypeDesc("Transfer Type Desc");
        channelTransferRuleVO.setType("Type");
        channelTransferRuleVO.setUncntrlReturnAllowed("Uncntrl Return Allowed");
        channelTransferRuleVO.setUncntrlReturnLevel("Uncntrl Return Level");
        channelTransferRuleVO.setUncntrlTransferAllowed("Uncntrl Transfer Allowed");
        channelTransferRuleVO.setUncntrlTransferAllowedTmp("Uncntrl Transfer Allowed Tmp");
        channelTransferRuleVO.setUncntrlTransferLevel("Uncntrl Transfer Level");
        channelTransferRuleVO.setUncntrlWithdrawAllowed("Uncntrl Withdraw Allowed");
        channelTransferRuleVO.setUncntrlWithdrawLevel("Uncntrl Withdraw Level");
        channelTransferRuleVO.setWithdrawAllowed("Withdraw Allowed");
        channelTransferRuleVO.setWithdrawChnlBypassAllowed("Withdraw Chnl Bypass Allowed");
        actualChannelTransferRuleResponseVO.setChannelTransferRuleVO(channelTransferRuleVO);
        actualChannelTransferRuleResponseVO.setCntrlReturnLevelDesc("Cntrl Return Level Desc");
        actualChannelTransferRuleResponseVO.setCntrlTransferLevelDesc("Cntrl Transfer Level Desc");
        actualChannelTransferRuleResponseVO.setCntrlWithdrawLevelDesc("Cntrl Withdraw Level Desc");
        ArrayList controllTxnLevelList = new ArrayList();
        actualChannelTransferRuleResponseVO.setControllTxnLevelList(controllTxnLevelList);
        actualChannelTransferRuleResponseVO.setCtrlReturnLevelDesc("Ctrl Return Level Desc");
        actualChannelTransferRuleResponseVO.setDomainName("Domain Name");
        actualChannelTransferRuleResponseVO.setFixedReturnLevelDesc("Fixed Return Level Desc");
        actualChannelTransferRuleResponseVO.setFixedTransferLevelDesc("Fixed Transfer Level Desc");
        ArrayList fixedTransferLevelList = new ArrayList();
        actualChannelTransferRuleResponseVO.setFixedTransferLevelList(fixedTransferLevelList);
        actualChannelTransferRuleResponseVO.setFixedWithdrawLevelDesc("Fixed Withdraw Level Desc");
        ArrayList productList = new ArrayList();
        actualChannelTransferRuleResponseVO.setProductList(productList);
        ArrayList productionVOList = new ArrayList();
        actualChannelTransferRuleResponseVO.setProductionVOList(productionVOList);
        ArrayList toCategoryList = new ArrayList();
        actualChannelTransferRuleResponseVO.setToCategoryList(toCategoryList);
        actualChannelTransferRuleResponseVO.setToDomainName("To Domain Name");
        actualChannelTransferRuleResponseVO.setTransferTypeDesc("Transfer Type Desc");
        ArrayList transferTypeList = new ArrayList();
        actualChannelTransferRuleResponseVO.setTransferTypeList(transferTypeList);
        actualChannelTransferRuleResponseVO.setUncntrlReturnLevelDesc("Uncntrl Return Level Desc");
        actualChannelTransferRuleResponseVO.setUncntrlTransferLevelDesc("Uncntrl Transfer Level Desc");
        actualChannelTransferRuleResponseVO.setUncntrlWithdrawLevelDesc("Uncntrl Withdraw Level Desc");
        ArrayList uncontrollTxnLevelList = new ArrayList();
        actualChannelTransferRuleResponseVO.setUncontrollTxnLevelList(uncontrollTxnLevelList);
        actualChannelTransferRuleResponseVO.setUserCategory("User Category");
        assertSame(categoryDomainList, actualChannelTransferRuleResponseVO.getCategoryDomainList());
        assertSame(categoryList, actualChannelTransferRuleResponseVO.getCategoryList());
        assertSame(channelTransferRuleVO, actualChannelTransferRuleResponseVO.getChannelTransferRuleVO());
        assertEquals("Cntrl Return Level Desc", actualChannelTransferRuleResponseVO.getCntrlReturnLevelDesc());
        assertEquals("Cntrl Transfer Level Desc", actualChannelTransferRuleResponseVO.getCntrlTransferLevelDesc());
        assertEquals("Cntrl Withdraw Level Desc", actualChannelTransferRuleResponseVO.getCntrlWithdrawLevelDesc());
        assertSame(controllTxnLevelList, actualChannelTransferRuleResponseVO.getControllTxnLevelList());
        assertEquals("Ctrl Return Level Desc", actualChannelTransferRuleResponseVO.getCtrlReturnLevelDesc());
        assertEquals("Domain Name", actualChannelTransferRuleResponseVO.getDomainName());
        assertEquals("Fixed Return Level Desc", actualChannelTransferRuleResponseVO.getFixedReturnLevelDesc());
        assertEquals("Fixed Transfer Level Desc", actualChannelTransferRuleResponseVO.getFixedTransferLevelDesc());
        assertSame(fixedTransferLevelList, actualChannelTransferRuleResponseVO.getFixedTransferLevelList());
        assertEquals("Fixed Withdraw Level Desc", actualChannelTransferRuleResponseVO.getFixedWithdrawLevelDesc());
        assertSame(productList, actualChannelTransferRuleResponseVO.getProductList());
        assertSame(productionVOList, actualChannelTransferRuleResponseVO.getProductionVOList());
        assertSame(toCategoryList, actualChannelTransferRuleResponseVO.getToCategoryList());
        assertEquals("To Domain Name", actualChannelTransferRuleResponseVO.getToDomainName());
        assertEquals("Transfer Type Desc", actualChannelTransferRuleResponseVO.getTransferTypeDesc());
        assertSame(transferTypeList, actualChannelTransferRuleResponseVO.getTransferTypeList());
        assertEquals("Uncntrl Return Level Desc", actualChannelTransferRuleResponseVO.getUncntrlReturnLevelDesc());
        assertEquals("Uncntrl Transfer Level Desc", actualChannelTransferRuleResponseVO.getUncntrlTransferLevelDesc());
        assertEquals("Uncntrl Withdraw Level Desc", actualChannelTransferRuleResponseVO.getUncntrlWithdrawLevelDesc());
        assertSame(uncontrollTxnLevelList, actualChannelTransferRuleResponseVO.getUncontrollTxnLevelList());
        assertEquals("User Category", actualChannelTransferRuleResponseVO.getUserCategory());
        assertTrue(actualChannelTransferRuleResponseVO.isAcrossDomain());
    }
}

