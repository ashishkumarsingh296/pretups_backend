package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SaveTransferProfileDataReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SaveTransferProfileDataReqVO}
     *   <li>{@link SaveTransferProfileDataReqVO#setAction(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setCategoryCode(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDailySubscriberOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDefaultProfile(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDescription(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setDomainCode(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setMonthlySubscriberOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setNetworkCode(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setProductBalancelist(List)}
     *   <li>{@link SaveTransferProfileDataReqVO#setProfileID(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setProfileName(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setShortName(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setStatus(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlDailyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlMonthlyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setUnctrlWeeklyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberInCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberInValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberOutCount(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#setWeeklySubscriberOutValue(String)}
     *   <li>{@link SaveTransferProfileDataReqVO#getAction()}
     *   <li>{@link SaveTransferProfileDataReqVO#getCategoryCode()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyInAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyInAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyInCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyInValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyOutAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyOutAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyOutCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailyOutValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberInAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberInAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberInCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberInValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberOutAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberOutAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberOutCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDailySubscriberOutValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDefaultProfile()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDescription()}
     *   <li>{@link SaveTransferProfileDataReqVO#getDomainCode()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyInAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyInAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyInCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyInValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyOutAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyOutAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyOutCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlyOutValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberInAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberInAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberInCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberInValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberOutAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberOutAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberOutCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getMonthlySubscriberOutValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getNetworkCode()}
     *   <li>{@link SaveTransferProfileDataReqVO#getProductBalancelist()}
     *   <li>{@link SaveTransferProfileDataReqVO#getProfileID()}
     *   <li>{@link SaveTransferProfileDataReqVO#getProfileName()}
     *   <li>{@link SaveTransferProfileDataReqVO#getShortName()}
     *   <li>{@link SaveTransferProfileDataReqVO#getStatus()}
     *   <li>{@link SaveTransferProfileDataReqVO#getUnctrlDailyInAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getUnctrlDailyInAltValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getUnctrlDailyInCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getUnctrlDailyInValue()}
     *   <li>{@link SaveTransferProfileDataReqVO#getUnctrlDailyOutAltCount()}
     *   <li>{@link SaveTransferProfileDataReqVO#getUnctrlDailyOutAltValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SaveTransferProfileDataReqVO actualSaveTransferProfileDataReqVO = new SaveTransferProfileDataReqVO();
        actualSaveTransferProfileDataReqVO.setAction("Action");
        actualSaveTransferProfileDataReqVO.setCategoryCode("Category Code");
        actualSaveTransferProfileDataReqVO.setDailyInAltCount("3");
        actualSaveTransferProfileDataReqVO.setDailyInAltValue("42");
        actualSaveTransferProfileDataReqVO.setDailyInCount("3");
        actualSaveTransferProfileDataReqVO.setDailyInValue("42");
        actualSaveTransferProfileDataReqVO.setDailyOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setDailyOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setDailyOutCount("3");
        actualSaveTransferProfileDataReqVO.setDailyOutValue("42");
        actualSaveTransferProfileDataReqVO.setDailySubscriberInAltCount("3");
        actualSaveTransferProfileDataReqVO.setDailySubscriberInAltValue("42");
        actualSaveTransferProfileDataReqVO.setDailySubscriberInCount("3");
        actualSaveTransferProfileDataReqVO.setDailySubscriberInValue("42");
        actualSaveTransferProfileDataReqVO.setDailySubscriberOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setDailySubscriberOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setDailySubscriberOutCount("3");
        actualSaveTransferProfileDataReqVO.setDailySubscriberOutValue("42");
        actualSaveTransferProfileDataReqVO.setDefaultProfile("Default Profile");
        actualSaveTransferProfileDataReqVO.setDescription("The characteristics of someone or something");
        actualSaveTransferProfileDataReqVO.setDomainCode("Domain Code");
        actualSaveTransferProfileDataReqVO.setMonthlyInAltCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlyInAltValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlyInCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlyInValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlyOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlyOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlyOutCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlyOutValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberInAltCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberInAltValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberInCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberInValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberOutCount("3");
        actualSaveTransferProfileDataReqVO.setMonthlySubscriberOutValue("42");
        actualSaveTransferProfileDataReqVO.setNetworkCode("Network Code");
        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        actualSaveTransferProfileDataReqVO.setProductBalancelist(productBalancelist);
        actualSaveTransferProfileDataReqVO.setProfileID("Profile ID");
        actualSaveTransferProfileDataReqVO.setProfileName("foo.txt");
        actualSaveTransferProfileDataReqVO.setShortName("Short Name");
        actualSaveTransferProfileDataReqVO.setStatus("Status");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyInAltCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyInAltValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyInCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyInValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyOutCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlDailyOutValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyInAltCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyInAltValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyInCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyInValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyOutCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlMonthlyOutValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyInAltCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyInAltValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyInCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyInValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyOutCount("3");
        actualSaveTransferProfileDataReqVO.setUnctrlWeeklyOutValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklyInAltCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklyInAltValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklyInCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklyInValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklyOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklyOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklyOutCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklyOutValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberInAltCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberInAltValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberInCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberInValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberOutAltCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberOutAltValue("42");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberOutCount("3");
        actualSaveTransferProfileDataReqVO.setWeeklySubscriberOutValue("42");
        assertEquals("Action", actualSaveTransferProfileDataReqVO.getAction());
        assertEquals("Category Code", actualSaveTransferProfileDataReqVO.getCategoryCode());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailyInAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailyInAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailyInCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailyInValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailyOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailyOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailyOutCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailyOutValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailySubscriberInAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailySubscriberInAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailySubscriberInCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailySubscriberInValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailySubscriberOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailySubscriberOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getDailySubscriberOutCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getDailySubscriberOutValue());
        assertEquals("Default Profile", actualSaveTransferProfileDataReqVO.getDefaultProfile());
        assertEquals("The characteristics of someone or something", actualSaveTransferProfileDataReqVO.getDescription());
        assertEquals("Domain Code", actualSaveTransferProfileDataReqVO.getDomainCode());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlyInAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlyInAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlyInCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlyInValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlyOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlyOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlyOutCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlyOutValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlySubscriberInAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlySubscriberInAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlySubscriberInCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlySubscriberInValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlySubscriberOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlySubscriberOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getMonthlySubscriberOutCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getMonthlySubscriberOutValue());
        assertEquals("Network Code", actualSaveTransferProfileDataReqVO.getNetworkCode());
        assertSame(productBalancelist, actualSaveTransferProfileDataReqVO.getProductBalancelist());
        assertEquals("Profile ID", actualSaveTransferProfileDataReqVO.getProfileID());
        assertEquals("foo.txt", actualSaveTransferProfileDataReqVO.getProfileName());
        assertEquals("Short Name", actualSaveTransferProfileDataReqVO.getShortName());
        assertEquals("Status", actualSaveTransferProfileDataReqVO.getStatus());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getUnctrlDailyInAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getUnctrlDailyInAltValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getUnctrlDailyInCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getUnctrlDailyInValue());
        assertEquals("3", actualSaveTransferProfileDataReqVO.getUnctrlDailyOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataReqVO.getUnctrlDailyOutAltValue());
    }
}

