package com.restapi.superadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;

import java.util.ArrayList;

import org.junit.Test;

public class CatTrfProfileRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CatTrfProfileRequestVO}
     *   <li>{@link CatTrfProfileRequestVO#setCategory(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailyOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDailySubscriberOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDefaultCommProfile(String)}
     *   <li>{@link CatTrfProfileRequestVO#setDefaultProfileModified(boolean)}
     *   <li>{@link CatTrfProfileRequestVO#setDescription(String)}
     *   <li>{@link CatTrfProfileRequestVO#setIsDefault(String)}
     *   <li>{@link CatTrfProfileRequestVO#setLastModifiedTime(long)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlyOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setMonthlySubscriberOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setNetworkCode(String)}
     *   <li>{@link CatTrfProfileRequestVO#setNetworkName(String)}
     *   <li>{@link CatTrfProfileRequestVO#setParentProfileID(String)}
     *   <li>{@link CatTrfProfileRequestVO#setProductBalanceList(ArrayList)}
     *   <li>{@link CatTrfProfileRequestVO#setProfileId(String)}
     *   <li>{@link CatTrfProfileRequestVO#setProfileName(String)}
     *   <li>{@link CatTrfProfileRequestVO#setShortName(String)}
     *   <li>{@link CatTrfProfileRequestVO#setStatus(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlDailyOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlMonthlyOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setUnctrlWeeklyOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklyOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberInAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberInAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberInCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberInValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberOutAltCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberOutAltValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberOutCount(String)}
     *   <li>{@link CatTrfProfileRequestVO#setWeeklySubscriberOutValue(String)}
     *   <li>{@link CatTrfProfileRequestVO#toString()}
     *   <li>{@link CatTrfProfileRequestVO#getCategory()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyInAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyInAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyInCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyInValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyOutAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyOutAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyOutCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailyOutValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberInAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberInAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberInCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberInValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberOutAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberOutAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberOutCount()}
     *   <li>{@link CatTrfProfileRequestVO#getDailySubscriberOutValue()}
     *   <li>{@link CatTrfProfileRequestVO#getDefaultCommProfile()}
     *   <li>{@link CatTrfProfileRequestVO#getDescription()}
     *   <li>{@link CatTrfProfileRequestVO#getIsDefault()}
     *   <li>{@link CatTrfProfileRequestVO#getLastModifiedTime()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyInAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyInAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyInCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyInValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyOutAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyOutAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyOutCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlyOutValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberInAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberInAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberInCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberInValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberOutAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberOutAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberOutCount()}
     *   <li>{@link CatTrfProfileRequestVO#getMonthlySubscriberOutValue()}
     *   <li>{@link CatTrfProfileRequestVO#getNetworkCode()}
     *   <li>{@link CatTrfProfileRequestVO#getNetworkName()}
     *   <li>{@link CatTrfProfileRequestVO#getParentProfileID()}
     *   <li>{@link CatTrfProfileRequestVO#getProductBalanceList()}
     *   <li>{@link CatTrfProfileRequestVO#getProfileId()}
     *   <li>{@link CatTrfProfileRequestVO#getProfileName()}
     *   <li>{@link CatTrfProfileRequestVO#getShortName()}
     *   <li>{@link CatTrfProfileRequestVO#getStatus()}
     *   <li>{@link CatTrfProfileRequestVO#getUnctrlDailyInAltCount()}
     *   <li>{@link CatTrfProfileRequestVO#getUnctrlDailyInAltValue()}
     *   <li>{@link CatTrfProfileRequestVO#getUnctrlDailyInCount()}
     *   <li>{@link CatTrfProfileRequestVO#getUnctrlDailyInValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CatTrfProfileRequestVO actualCatTrfProfileRequestVO = new CatTrfProfileRequestVO();
        actualCatTrfProfileRequestVO.setCategory("Category");
        actualCatTrfProfileRequestVO.setDailyInAltCount("3");
        actualCatTrfProfileRequestVO.setDailyInAltValue("42");
        actualCatTrfProfileRequestVO.setDailyInCount("3");
        actualCatTrfProfileRequestVO.setDailyInValue("42");
        actualCatTrfProfileRequestVO.setDailyOutAltCount("3");
        actualCatTrfProfileRequestVO.setDailyOutAltValue("42");
        actualCatTrfProfileRequestVO.setDailyOutCount("3");
        actualCatTrfProfileRequestVO.setDailyOutValue("42");
        actualCatTrfProfileRequestVO.setDailySubscriberInAltCount("3");
        actualCatTrfProfileRequestVO.setDailySubscriberInAltValue("42");
        actualCatTrfProfileRequestVO.setDailySubscriberInCount("3");
        actualCatTrfProfileRequestVO.setDailySubscriberInValue("42");
        actualCatTrfProfileRequestVO.setDailySubscriberOutAltCount("3");
        actualCatTrfProfileRequestVO.setDailySubscriberOutAltValue("42");
        actualCatTrfProfileRequestVO.setDailySubscriberOutCount("3");
        actualCatTrfProfileRequestVO.setDailySubscriberOutValue("42");
        actualCatTrfProfileRequestVO.setDefaultCommProfile("Default Comm Profile");
        actualCatTrfProfileRequestVO.setDefaultProfileModified(true);
        actualCatTrfProfileRequestVO.setDescription("The characteristics of someone or something");
        actualCatTrfProfileRequestVO.setIsDefault("Is Default");
        actualCatTrfProfileRequestVO.setLastModifiedTime(1L);
        actualCatTrfProfileRequestVO.setMonthlyInAltCount("3");
        actualCatTrfProfileRequestVO.setMonthlyInAltValue("42");
        actualCatTrfProfileRequestVO.setMonthlyInCount("3");
        actualCatTrfProfileRequestVO.setMonthlyInValue("42");
        actualCatTrfProfileRequestVO.setMonthlyOutAltCount("3");
        actualCatTrfProfileRequestVO.setMonthlyOutAltValue("42");
        actualCatTrfProfileRequestVO.setMonthlyOutCount("3");
        actualCatTrfProfileRequestVO.setMonthlyOutValue("42");
        actualCatTrfProfileRequestVO.setMonthlySubscriberInAltCount("3");
        actualCatTrfProfileRequestVO.setMonthlySubscriberInAltValue("42");
        actualCatTrfProfileRequestVO.setMonthlySubscriberInCount("3");
        actualCatTrfProfileRequestVO.setMonthlySubscriberInValue("42");
        actualCatTrfProfileRequestVO.setMonthlySubscriberOutAltCount("3");
        actualCatTrfProfileRequestVO.setMonthlySubscriberOutAltValue("42");
        actualCatTrfProfileRequestVO.setMonthlySubscriberOutCount("3");
        actualCatTrfProfileRequestVO.setMonthlySubscriberOutValue("42");
        actualCatTrfProfileRequestVO.setNetworkCode("Network Code");
        actualCatTrfProfileRequestVO.setNetworkName("Network Name");
        actualCatTrfProfileRequestVO.setParentProfileID("Parent Profile ID");
        ArrayList<TransferProfileProductVO> productBalanceList = new ArrayList<>();
        actualCatTrfProfileRequestVO.setProductBalanceList(productBalanceList);
        actualCatTrfProfileRequestVO.setProfileId("42");
        actualCatTrfProfileRequestVO.setProfileName("foo.txt");
        actualCatTrfProfileRequestVO.setShortName("Short Name");
        actualCatTrfProfileRequestVO.setStatus("Status");
        actualCatTrfProfileRequestVO.setUnctrlDailyInAltCount("3");
        actualCatTrfProfileRequestVO.setUnctrlDailyInAltValue("42");
        actualCatTrfProfileRequestVO.setUnctrlDailyInCount("3");
        actualCatTrfProfileRequestVO.setUnctrlDailyInValue("42");
        actualCatTrfProfileRequestVO.setUnctrlDailyOutAltCount("3");
        actualCatTrfProfileRequestVO.setUnctrlDailyOutAltValue("42");
        actualCatTrfProfileRequestVO.setUnctrlDailyOutCount("3");
        actualCatTrfProfileRequestVO.setUnctrlDailyOutValue("42");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyInAltCount("3");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyInAltValue("42");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyInCount("3");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyInValue("42");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyOutAltCount("3");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyOutAltValue("42");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyOutCount("3");
        actualCatTrfProfileRequestVO.setUnctrlMonthlyOutValue("42");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyInAltCount("3");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyInAltValue("42");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyInCount("3");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyInValue("42");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyOutAltCount("3");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyOutAltValue("42");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyOutCount("3");
        actualCatTrfProfileRequestVO.setUnctrlWeeklyOutValue("42");
        actualCatTrfProfileRequestVO.setWeeklyInAltCount("3");
        actualCatTrfProfileRequestVO.setWeeklyInAltValue("42");
        actualCatTrfProfileRequestVO.setWeeklyInCount("3");
        actualCatTrfProfileRequestVO.setWeeklyInValue("42");
        actualCatTrfProfileRequestVO.setWeeklyOutAltCount("3");
        actualCatTrfProfileRequestVO.setWeeklyOutAltValue("42");
        actualCatTrfProfileRequestVO.setWeeklyOutCount("3");
        actualCatTrfProfileRequestVO.setWeeklyOutValue("42");
        actualCatTrfProfileRequestVO.setWeeklySubscriberInAltCount("3");
        actualCatTrfProfileRequestVO.setWeeklySubscriberInAltValue("42");
        actualCatTrfProfileRequestVO.setWeeklySubscriberInCount("3");
        actualCatTrfProfileRequestVO.setWeeklySubscriberInValue("42");
        actualCatTrfProfileRequestVO.setWeeklySubscriberOutAltCount("3");
        actualCatTrfProfileRequestVO.setWeeklySubscriberOutAltValue("42");
        actualCatTrfProfileRequestVO.setWeeklySubscriberOutCount("3");
        actualCatTrfProfileRequestVO.setWeeklySubscriberOutValue("42");
        String actualToStringResult = actualCatTrfProfileRequestVO.toString();
        assertEquals("Category", actualCatTrfProfileRequestVO.getCategory());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailyInAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailyInAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailyInCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailyInValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailyOutAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailyOutAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailyOutCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailyOutValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailySubscriberInAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailySubscriberInAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailySubscriberInCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailySubscriberInValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailySubscriberOutAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailySubscriberOutAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getDailySubscriberOutCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getDailySubscriberOutValue());
        assertEquals("Default Comm Profile", actualCatTrfProfileRequestVO.getDefaultCommProfile());
        assertEquals("The characteristics of someone or something", actualCatTrfProfileRequestVO.getDescription());
        assertEquals("Is Default", actualCatTrfProfileRequestVO.getIsDefault());
        assertEquals(1L, actualCatTrfProfileRequestVO.getLastModifiedTime());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlyInAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlyInAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlyInCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlyInValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlyOutAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlyOutAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlyOutCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlyOutValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlySubscriberInAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlySubscriberInAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlySubscriberInCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlySubscriberInValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlySubscriberOutAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlySubscriberOutAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getMonthlySubscriberOutCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getMonthlySubscriberOutValue());
        assertEquals("Network Code", actualCatTrfProfileRequestVO.getNetworkCode());
        assertEquals("Network Name", actualCatTrfProfileRequestVO.getNetworkName());
        assertEquals("Parent Profile ID", actualCatTrfProfileRequestVO.getParentProfileID());
        assertSame(productBalanceList, actualCatTrfProfileRequestVO.getProductBalanceList());
        assertEquals("42", actualCatTrfProfileRequestVO.getProfileId());
        assertEquals("foo.txt", actualCatTrfProfileRequestVO.getProfileName());
        assertEquals("Short Name", actualCatTrfProfileRequestVO.getShortName());
        assertEquals("Status", actualCatTrfProfileRequestVO.getStatus());
        assertEquals("3", actualCatTrfProfileRequestVO.getUnctrlDailyInAltCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getUnctrlDailyInAltValue());
        assertEquals("3", actualCatTrfProfileRequestVO.getUnctrlDailyInCount());
        assertEquals("42", actualCatTrfProfileRequestVO.getUnctrlDailyInValue());
        assertEquals(
                "CatTrfProfileRequestVO [productBalanceList=[], profileId=42, networkName=Network Name, profileName=foo.txt,"
                        + " shortName=Short Name, description=The characteristics of someone or something, status=Status,"
                        + " dailyInCount=3, dailyInValue=42, dailyOutCount=3, dailyOutValue=42, networkCode=Network Code,"
                        + " category=Category, weeklyInCount=3, weeklyInValue=42, weeklyOutCount=3, weeklyOutValue=42,"
                        + " dailySubscriberOutCount=3, weeklySubscriberOutCount=3, monthlySubscriberOutCount=3, dailySubscriberOutValue"
                        + "=42, weeklySubscriberOutValue=42, monthlySubscriberOutValue=42, monthlyInCount=3, monthlyInValue=42,"
                        + " monthlyOutCount=3, monthlyOutValue=42, dailyInAltCount=3, dailyInAltValue=42, dailyOutAltCount=3,"
                        + " dailyOutAltValue=42, weeklyInAltCount=3, weeklyInAltValue=42, weeklyOutAltCount=3, weeklyOutAltValue=42,"
                        + " monthlyInAltCount=3, monthlyInAltValue=42, monthlyOutAltCount=3, monthlyOutAltValue=42, dailySubscr"
                        + "iberOutAltCount=3, weeklySubscriberOutAltCount=3, monthlySubscriberOutAltCount=3, dailySubscriberOutAltValue"
                        + "=42, weeklySubscriberOutAltValue=42, monthlySubscriberOutAltValue=42, dailySubscriberInCount=3,"
                        + " weeklySubscriberInCount=3, monthlySubscriberInCount=3, dailySubscriberInValue=42, weeklySubscriberInValue"
                        + "=42, monthlySubscriberInValue=42, dailySubscriberInAltCount=3, weeklySubscriberInAltCount=3,"
                        + " monthlySubscriberInAltCount=3, dailySubscriberInAltValue=42, weeklySubscriberInAltValue=42,"
                        + " monthlySubscriberInAltValue=42, unctrlDailyInAltCount=3, unctrlDailyInAltValue=42, unctrlDailyOutAltCount=3,"
                        + " unctrlDailyOutAltValue=42, unctrlWeeklyInAltCount=3, unctrlWeeklyInAltValue=42, unctrlWeeklyOutAltCount=3,"
                        + " unctrlWeeklyOutAltValue=42, unctrlMonthlyInAltCount=3, unctrlMonthlyInAltValue=42, unctrlMonthlyOutAltCount"
                        + "=3, unctrlMonthlyOutAltValue=42, lastModifiedTime=1, parentProfileID=Parent Profile ID, isDefaultPro"
                        + "fileModified=true, defaultCommProfile=Default Comm Profile, isDefault=Is Default, unctrlDailyInCount=3,"
                        + " unctrlDailyInValue=42, unctrlDailyOutCount=3, unctrlDailyOutValue=42, unctrlWeeklyInCount=3,"
                        + " unctrlWeeklyInValue=42, unctrlWeeklyOutCount=3, unctrlWeeklyOutValue=42, unctrlMonthlyInCount=3,"
                        + " unctrlMonthlyInValue=42, unctrlMonthlyOutCount=3, unctrlMonthlyOutValue=42]",
                actualToStringResult);
    }
}

