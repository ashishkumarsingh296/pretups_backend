package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class TransferProfileFormVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link TransferProfileFormVO}
     *   <li>{@link TransferProfileFormVO#setCategory(String)}
     *   <li>{@link TransferProfileFormVO#setCategoryList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setCategoryName(String)}
     *   <li>{@link TransferProfileFormVO#setCode(String)}
     *   <li>{@link TransferProfileFormVO#setDailyInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailyInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailyInCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailyInValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailyOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailyOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailyOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailyOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberInCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberInValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setDailySubscriberOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setDefaultCommProfile(String)}
     *   <li>{@link TransferProfileFormVO#setDefaultProfileModified(boolean)}
     *   <li>{@link TransferProfileFormVO#setDescription(String)}
     *   <li>{@link TransferProfileFormVO#setDomainCodeforCategory(String)}
     *   <li>{@link TransferProfileFormVO#setDomainList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setDomainName(String)}
     *   <li>{@link TransferProfileFormVO#setDomainTypeCode(String)}
     *   <li>{@link TransferProfileFormVO#setDomainTypeList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setIsDefault(String)}
     *   <li>{@link TransferProfileFormVO#setLastModifiedTime(long)}
     *   <li>{@link TransferProfileFormVO#setListSizeFlag(boolean)}
     *   <li>{@link TransferProfileFormVO#setLookupStatusList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setMonthlyInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyInCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyInValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlyOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberInCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberInValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setMonthlySubscriberOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setNetworkCode(String)}
     *   <li>{@link TransferProfileFormVO#setNetworkName(String)}
     *   <li>{@link TransferProfileFormVO#setProductBalanceList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setProductCode(String)}
     *   <li>{@link TransferProfileFormVO#setProfileId(String)}
     *   <li>{@link TransferProfileFormVO#setProfileName(String)}
     *   <li>{@link TransferProfileFormVO#setProfileStatusName(String)}
     *   <li>{@link TransferProfileFormVO#setSearchDomainList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setShortName(String)}
     *   <li>{@link TransferProfileFormVO#setStatus(String)}
     *   <li>{@link TransferProfileFormVO#setSubscriberOutCountFlag(boolean)}
     *   <li>{@link TransferProfileFormVO#setTansferProfileList(ArrayList)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyInCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyInValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlDailyOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyInCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyInValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlMonthlyOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlTransferFlag(boolean)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyInCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyInValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setUnctrlWeeklyOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyInCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyInValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklyOutValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberInAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberInAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberInCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberInValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberOutAltCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberOutAltValue(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberOutCount(String)}
     *   <li>{@link TransferProfileFormVO#setWeeklySubscriberOutValue(String)}
     *   <li>{@link TransferProfileFormVO#getCategory()}
     *   <li>{@link TransferProfileFormVO#getCategoryList()}
     *   <li>{@link TransferProfileFormVO#getCategoryName()}
     *   <li>{@link TransferProfileFormVO#getCode()}
     *   <li>{@link TransferProfileFormVO#getDailyInAltCount()}
     *   <li>{@link TransferProfileFormVO#getDailyInAltValue()}
     *   <li>{@link TransferProfileFormVO#getDailyInCount()}
     *   <li>{@link TransferProfileFormVO#getDailyInValue()}
     *   <li>{@link TransferProfileFormVO#getDailyOutAltCount()}
     *   <li>{@link TransferProfileFormVO#getDailyOutAltValue()}
     *   <li>{@link TransferProfileFormVO#getDailyOutCount()}
     *   <li>{@link TransferProfileFormVO#getDailyOutValue()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberInAltCount()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberInAltValue()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberInCount()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberInValue()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberOutAltCount()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberOutAltValue()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberOutCount()}
     *   <li>{@link TransferProfileFormVO#getDailySubscriberOutValue()}
     *   <li>{@link TransferProfileFormVO#getDefaultCommProfile()}
     *   <li>{@link TransferProfileFormVO#getDescription()}
     *   <li>{@link TransferProfileFormVO#getDomainCodeforCategory()}
     *   <li>{@link TransferProfileFormVO#getDomainList()}
     *   <li>{@link TransferProfileFormVO#getDomainName()}
     *   <li>{@link TransferProfileFormVO#getDomainTypeCode()}
     *   <li>{@link TransferProfileFormVO#getDomainTypeList()}
     *   <li>{@link TransferProfileFormVO#getIsDefault()}
     *   <li>{@link TransferProfileFormVO#getLastModifiedTime()}
     *   <li>{@link TransferProfileFormVO#getLookupStatusList()}
     *   <li>{@link TransferProfileFormVO#getMonthlyInAltCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlyInAltValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlyInCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlyInValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlyOutAltCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlyOutAltValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlyOutCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlyOutValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberInAltCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberInAltValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberInCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberInValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberOutAltCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberOutAltValue()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberOutCount()}
     *   <li>{@link TransferProfileFormVO#getMonthlySubscriberOutValue()}
     *   <li>{@link TransferProfileFormVO#getNetworkCode()}
     *   <li>{@link TransferProfileFormVO#getNetworkName()}
     *   <li>{@link TransferProfileFormVO#getProductBalanceList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        TransferProfileFormVO actualTransferProfileFormVO = new TransferProfileFormVO();
        actualTransferProfileFormVO.setCategory("Category");
        ArrayList categoryList = new ArrayList();
        actualTransferProfileFormVO.setCategoryList(categoryList);
        actualTransferProfileFormVO.setCategoryName("Category Name");
        actualTransferProfileFormVO.setCode("Code");
        actualTransferProfileFormVO.setDailyInAltCount("3");
        actualTransferProfileFormVO.setDailyInAltValue("42");
        actualTransferProfileFormVO.setDailyInCount("3");
        actualTransferProfileFormVO.setDailyInValue("42");
        actualTransferProfileFormVO.setDailyOutAltCount("3");
        actualTransferProfileFormVO.setDailyOutAltValue("42");
        actualTransferProfileFormVO.setDailyOutCount("3");
        actualTransferProfileFormVO.setDailyOutValue("42");
        actualTransferProfileFormVO.setDailySubscriberInAltCount("3");
        actualTransferProfileFormVO.setDailySubscriberInAltValue("42");
        actualTransferProfileFormVO.setDailySubscriberInCount("3");
        actualTransferProfileFormVO.setDailySubscriberInValue("42");
        actualTransferProfileFormVO.setDailySubscriberOutAltCount("3");
        actualTransferProfileFormVO.setDailySubscriberOutAltValue("42");
        actualTransferProfileFormVO.setDailySubscriberOutCount("3");
        actualTransferProfileFormVO.setDailySubscriberOutValue("42");
        actualTransferProfileFormVO.setDefaultCommProfile("Default Comm Profile");
        actualTransferProfileFormVO.setDefaultProfileModified(true);
        actualTransferProfileFormVO.setDescription("The characteristics of someone or something");
        actualTransferProfileFormVO.setDomainCodeforCategory("Domain Codefor Category");
        ArrayList domainList = new ArrayList();
        actualTransferProfileFormVO.setDomainList(domainList);
        actualTransferProfileFormVO.setDomainName("Domain Name");
        actualTransferProfileFormVO.setDomainTypeCode("Domain Type Code");
        ArrayList domainTypeList = new ArrayList();
        actualTransferProfileFormVO.setDomainTypeList(domainTypeList);
        actualTransferProfileFormVO.setIsDefault("Is Default");
        actualTransferProfileFormVO.setLastModifiedTime(1L);
        actualTransferProfileFormVO.setListSizeFlag(true);
        ArrayList lookupStatusList = new ArrayList();
        actualTransferProfileFormVO.setLookupStatusList(lookupStatusList);
        actualTransferProfileFormVO.setMonthlyInAltCount("3");
        actualTransferProfileFormVO.setMonthlyInAltValue("42");
        actualTransferProfileFormVO.setMonthlyInCount("3");
        actualTransferProfileFormVO.setMonthlyInValue("42");
        actualTransferProfileFormVO.setMonthlyOutAltCount("3");
        actualTransferProfileFormVO.setMonthlyOutAltValue("42");
        actualTransferProfileFormVO.setMonthlyOutCount("3");
        actualTransferProfileFormVO.setMonthlyOutValue("42");
        actualTransferProfileFormVO.setMonthlySubscriberInAltCount("3");
        actualTransferProfileFormVO.setMonthlySubscriberInAltValue("42");
        actualTransferProfileFormVO.setMonthlySubscriberInCount("3");
        actualTransferProfileFormVO.setMonthlySubscriberInValue("42");
        actualTransferProfileFormVO.setMonthlySubscriberOutAltCount("3");
        actualTransferProfileFormVO.setMonthlySubscriberOutAltValue("42");
        actualTransferProfileFormVO.setMonthlySubscriberOutCount("3");
        actualTransferProfileFormVO.setMonthlySubscriberOutValue("42");
        actualTransferProfileFormVO.setNetworkCode("Network Code");
        actualTransferProfileFormVO.setNetworkName("Network Name");
        ArrayList productBalanceList = new ArrayList();
        actualTransferProfileFormVO.setProductBalanceList(productBalanceList);
        actualTransferProfileFormVO.setProductCode("Product Code");
        actualTransferProfileFormVO.setProfileId("42");
        actualTransferProfileFormVO.setProfileName("foo.txt");
        actualTransferProfileFormVO.setProfileStatusName("Profile Status Name");
        actualTransferProfileFormVO.setSearchDomainList(new ArrayList());
        actualTransferProfileFormVO.setShortName("Short Name");
        actualTransferProfileFormVO.setStatus("Status");
        actualTransferProfileFormVO.setSubscriberOutCountFlag(true);
        actualTransferProfileFormVO.setTansferProfileList(new ArrayList());
        actualTransferProfileFormVO.setUnctrlDailyInAltCount("3");
        actualTransferProfileFormVO.setUnctrlDailyInAltValue("42");
        actualTransferProfileFormVO.setUnctrlDailyInCount("3");
        actualTransferProfileFormVO.setUnctrlDailyInValue("42");
        actualTransferProfileFormVO.setUnctrlDailyOutAltCount("3");
        actualTransferProfileFormVO.setUnctrlDailyOutAltValue("42");
        actualTransferProfileFormVO.setUnctrlDailyOutCount("3");
        actualTransferProfileFormVO.setUnctrlDailyOutValue("42");
        actualTransferProfileFormVO.setUnctrlMonthlyInAltCount("3");
        actualTransferProfileFormVO.setUnctrlMonthlyInAltValue("42");
        actualTransferProfileFormVO.setUnctrlMonthlyInCount("3");
        actualTransferProfileFormVO.setUnctrlMonthlyInValue("42");
        actualTransferProfileFormVO.setUnctrlMonthlyOutAltCount("3");
        actualTransferProfileFormVO.setUnctrlMonthlyOutAltValue("42");
        actualTransferProfileFormVO.setUnctrlMonthlyOutCount("3");
        actualTransferProfileFormVO.setUnctrlMonthlyOutValue("42");
        actualTransferProfileFormVO.setUnctrlTransferFlag(true);
        actualTransferProfileFormVO.setUnctrlWeeklyInAltCount("3");
        actualTransferProfileFormVO.setUnctrlWeeklyInAltValue("42");
        actualTransferProfileFormVO.setUnctrlWeeklyInCount("3");
        actualTransferProfileFormVO.setUnctrlWeeklyInValue("42");
        actualTransferProfileFormVO.setUnctrlWeeklyOutAltCount("3");
        actualTransferProfileFormVO.setUnctrlWeeklyOutAltValue("42");
        actualTransferProfileFormVO.setUnctrlWeeklyOutCount("3");
        actualTransferProfileFormVO.setUnctrlWeeklyOutValue("42");
        actualTransferProfileFormVO.setWeeklyInAltCount("3");
        actualTransferProfileFormVO.setWeeklyInAltValue("42");
        actualTransferProfileFormVO.setWeeklyInCount("3");
        actualTransferProfileFormVO.setWeeklyInValue("42");
        actualTransferProfileFormVO.setWeeklyOutAltCount("3");
        actualTransferProfileFormVO.setWeeklyOutAltValue("42");
        actualTransferProfileFormVO.setWeeklyOutCount("3");
        actualTransferProfileFormVO.setWeeklyOutValue("42");
        actualTransferProfileFormVO.setWeeklySubscriberInAltCount("3");
        actualTransferProfileFormVO.setWeeklySubscriberInAltValue("42");
        actualTransferProfileFormVO.setWeeklySubscriberInCount("3");
        actualTransferProfileFormVO.setWeeklySubscriberInValue("42");
        actualTransferProfileFormVO.setWeeklySubscriberOutAltCount("3");
        actualTransferProfileFormVO.setWeeklySubscriberOutAltValue("42");
        actualTransferProfileFormVO.setWeeklySubscriberOutCount("3");
        actualTransferProfileFormVO.setWeeklySubscriberOutValue("42");
        assertEquals("Category", actualTransferProfileFormVO.getCategory());
        assertSame(categoryList, actualTransferProfileFormVO.getCategoryList());
        assertEquals("Category Name", actualTransferProfileFormVO.getCategoryName());
        assertEquals("Code", actualTransferProfileFormVO.getCode());
        assertEquals("3", actualTransferProfileFormVO.getDailyInAltCount());
        assertEquals("42", actualTransferProfileFormVO.getDailyInAltValue());
        assertEquals("3", actualTransferProfileFormVO.getDailyInCount());
        assertEquals("42", actualTransferProfileFormVO.getDailyInValue());
        assertEquals("3", actualTransferProfileFormVO.getDailyOutAltCount());
        assertEquals("42", actualTransferProfileFormVO.getDailyOutAltValue());
        assertEquals("3", actualTransferProfileFormVO.getDailyOutCount());
        assertEquals("42", actualTransferProfileFormVO.getDailyOutValue());
        assertEquals("3", actualTransferProfileFormVO.getDailySubscriberInAltCount());
        assertEquals("42", actualTransferProfileFormVO.getDailySubscriberInAltValue());
        assertEquals("3", actualTransferProfileFormVO.getDailySubscriberInCount());
        assertEquals("42", actualTransferProfileFormVO.getDailySubscriberInValue());
        assertEquals("3", actualTransferProfileFormVO.getDailySubscriberOutAltCount());
        assertEquals("42", actualTransferProfileFormVO.getDailySubscriberOutAltValue());
        assertEquals("3", actualTransferProfileFormVO.getDailySubscriberOutCount());
        assertEquals("42", actualTransferProfileFormVO.getDailySubscriberOutValue());
        assertEquals("Default Comm Profile", actualTransferProfileFormVO.getDefaultCommProfile());
        assertEquals("The characteristics of someone or something", actualTransferProfileFormVO.getDescription());
        assertEquals("Domain Codefor Category", actualTransferProfileFormVO.getDomainCodeforCategory());
        assertSame(domainList, actualTransferProfileFormVO.getDomainList());
        assertEquals("Domain Name", actualTransferProfileFormVO.getDomainName());
        assertEquals("Domain Type Code", actualTransferProfileFormVO.getDomainTypeCode());
        assertSame(domainTypeList, actualTransferProfileFormVO.getDomainTypeList());
        assertEquals("Is Default", actualTransferProfileFormVO.getIsDefault());
        assertEquals(1L, actualTransferProfileFormVO.getLastModifiedTime());
        assertSame(lookupStatusList, actualTransferProfileFormVO.getLookupStatusList());
        assertEquals("3", actualTransferProfileFormVO.getMonthlyInAltCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlyInAltValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlyInCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlyInValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlyOutAltCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlyOutAltValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlyOutCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlyOutValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlySubscriberInAltCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlySubscriberInAltValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlySubscriberInCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlySubscriberInValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlySubscriberOutAltCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlySubscriberOutAltValue());
        assertEquals("3", actualTransferProfileFormVO.getMonthlySubscriberOutCount());
        assertEquals("42", actualTransferProfileFormVO.getMonthlySubscriberOutValue());
        assertEquals("Network Code", actualTransferProfileFormVO.getNetworkCode());
        assertEquals("Network Name", actualTransferProfileFormVO.getNetworkName());
        assertSame(productBalanceList, actualTransferProfileFormVO.getProductBalanceList());
    }
}

