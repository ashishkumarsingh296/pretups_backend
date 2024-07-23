package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;

import org.junit.Test;
import org.mockito.Mockito;

public class SaveTransferProfileDataCloneReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SaveTransferProfileDataCloneReqVO}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setAction(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setCategoryCode(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDailySubscriberOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDefaultProfile(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDescription(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setDomainCode(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setMonthlySubscriberOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setNetworkCode(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setProductBalancelist(List)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setProfileID(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setProfileName(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setShortName(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setStatus(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setSubscriberOutCountFlag(boolean)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlDailyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlMonthlyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlTransferFlag(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setUnctrlWeeklyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklyOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberInAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberInAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberInCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberInValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberOutAltCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberOutAltValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberOutCount(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#setWeeklySubscriberOutValue(String)}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getAction()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getCategoryCode()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyInAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyInAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyInCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyInValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyOutAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyOutAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyOutCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailyOutValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberInAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberInAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberInCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberInValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberOutAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberOutAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberOutCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDailySubscriberOutValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDefaultProfile()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDescription()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getDomainCode()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyInAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyInAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyInCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyInValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyOutAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyOutAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyOutCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlyOutValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberInAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberInAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberInCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberInValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberOutAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberOutAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberOutCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getMonthlySubscriberOutValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getNetworkCode()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getProductBalancelist()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getProfileID()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getProfileName()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getShortName()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getStatus()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getUnctrlDailyInAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getUnctrlDailyInAltValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getUnctrlDailyInCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getUnctrlDailyInValue()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getUnctrlDailyOutAltCount()}
     *   <li>{@link SaveTransferProfileDataCloneReqVO#getUnctrlDailyOutAltValue()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SaveTransferProfileDataCloneReqVO actualSaveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        actualSaveTransferProfileDataCloneReqVO.setAction("Action");
        actualSaveTransferProfileDataCloneReqVO.setCategoryCode("Category Code");
        actualSaveTransferProfileDataCloneReqVO.setDailyInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailyInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailyInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailyInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailyOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailyOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailyOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailyOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setDailySubscriberOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setDefaultProfile("Default Profile");
        actualSaveTransferProfileDataCloneReqVO.setDescription("The characteristics of someone or something");
        actualSaveTransferProfileDataCloneReqVO.setDomainCode("Domain Code");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlyOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setMonthlySubscriberOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setNetworkCode("Network Code");
        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        actualSaveTransferProfileDataCloneReqVO.setProductBalancelist(productBalancelist);
        actualSaveTransferProfileDataCloneReqVO.setProfileID("Profile ID");
        actualSaveTransferProfileDataCloneReqVO.setProfileName("foo.txt");
        actualSaveTransferProfileDataCloneReqVO.setShortName("Short Name");
        actualSaveTransferProfileDataCloneReqVO.setStatus("Status");
        actualSaveTransferProfileDataCloneReqVO.setSubscriberOutCountFlag(true);
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlDailyOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlMonthlyOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("Unctrl Transfer Flag");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setUnctrlWeeklyOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklyOutValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberInAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberInAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberInCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberInValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberOutAltCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberOutAltValue("42");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberOutCount("3");
        actualSaveTransferProfileDataCloneReqVO.setWeeklySubscriberOutValue("42");
        assertEquals("Action", actualSaveTransferProfileDataCloneReqVO.getAction());
        assertEquals("Category Code", actualSaveTransferProfileDataCloneReqVO.getCategoryCode());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailyInAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailyInAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailyInCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailyInValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailyOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailyOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailyOutCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailyOutValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberInAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberInAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberInCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberInValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberOutCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getDailySubscriberOutValue());
        assertEquals("Default Profile", actualSaveTransferProfileDataCloneReqVO.getDefaultProfile());
        assertEquals("The characteristics of someone or something",
                actualSaveTransferProfileDataCloneReqVO.getDescription());
        assertEquals("Domain Code", actualSaveTransferProfileDataCloneReqVO.getDomainCode());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlyInAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlyInAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlyInCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlyInValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlyOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlyOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlyOutCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlyOutValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberInAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberInAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberInCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberInValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberOutAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberOutCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getMonthlySubscriberOutValue());
        assertEquals("Network Code", actualSaveTransferProfileDataCloneReqVO.getNetworkCode());
        assertSame(productBalancelist, actualSaveTransferProfileDataCloneReqVO.getProductBalancelist());
        assertEquals("Profile ID", actualSaveTransferProfileDataCloneReqVO.getProfileID());
        assertEquals("foo.txt", actualSaveTransferProfileDataCloneReqVO.getProfileName());
        assertEquals("Short Name", actualSaveTransferProfileDataCloneReqVO.getShortName());
        assertEquals("Status", actualSaveTransferProfileDataCloneReqVO.getStatus());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getUnctrlDailyInAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getUnctrlDailyInAltValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getUnctrlDailyInCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getUnctrlDailyInValue());
        assertEquals("3", actualSaveTransferProfileDataCloneReqVO.getUnctrlDailyOutAltCount());
        assertEquals("42", actualSaveTransferProfileDataCloneReqVO.getUnctrlDailyOutAltValue());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:784)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setShortName("TEST");
        saveTransferProfileDataCloneReqVO.setProfileID("TEST");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData2() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setShortName("Short Name");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData3() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    public void testValidateFormData4() {
        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag(" ");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        assertTrue(saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault()).isEmpty());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData5() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(new ArrayList<>());
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData6() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData7() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData8() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData9() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData10() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData11() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData12() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData13() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData14() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData15() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData16() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData17() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData18() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData19() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData20() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("TEST");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData21() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    public void testValidateFormData22() {
        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setUnctrlTransferFlag("Y");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        assertTrue(saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault()).isEmpty());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData23() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: empty String
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:1842)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateProductBalancelist(SaveTransferProfileDataCloneReqVO.java:821)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:794)
        //   See https://diff.blue/R013 to resolve this issue.

        TransferProfileProductReqVO transferProfileProductReqVO = new TransferProfileProductReqVO();
        transferProfileProductReqVO.setAllowedMaxPercentage(" ");
        transferProfileProductReqVO.setAllowedMaxPercentageInt(-1);
        transferProfileProductReqVO.setAltBalance(" ");
        transferProfileProductReqVO.setAltBalanceLong(42L);
        transferProfileProductReqVO.setC2sMaxTxnAmt(" ");
        transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setC2sMinTxnAmt(" ");
        transferProfileProductReqVO.setC2sMinTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setCurrentBalance(" ");
        transferProfileProductReqVO.setMaxBalance(" ");
        transferProfileProductReqVO.setMaxBalanceAsLong(42L);
        transferProfileProductReqVO.setMinBalance(" ");
        transferProfileProductReqVO.setMinResidualBalanceAsLong(1L);
        transferProfileProductReqVO.setProductCode(" ");
        transferProfileProductReqVO.setProductName(" ");
        transferProfileProductReqVO.setProductShortCode(" ");

        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        productBalancelist.add(transferProfileProductReqVO);

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(productBalancelist);
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData24() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "Min Balance"
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateProductBalancelist(SaveTransferProfileDataCloneReqVO.java:821)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:794)
        //   See https://diff.blue/R013 to resolve this issue.

        TransferProfileProductReqVO transferProfileProductReqVO = mock(TransferProfileProductReqVO.class);
        when(transferProfileProductReqVO.getMinBalance()).thenReturn("1");
        doNothing().when(transferProfileProductReqVO).setAllowedMaxPercentage(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setAllowedMaxPercentageInt(anyInt());
        doNothing().when(transferProfileProductReqVO).setAltBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setAltBalanceLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setC2sMaxTxnAmt(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setC2sMaxTxnAmtAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setC2sMinTxnAmt(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setC2sMinTxnAmtAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setCurrentBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMaxBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMaxBalanceAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setMinBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMinResidualBalanceAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setProductCode(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setProductName(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setProductShortCode(Mockito.<String>any());
        transferProfileProductReqVO.setAllowedMaxPercentage(" ");
        transferProfileProductReqVO.setAllowedMaxPercentageInt(-1);
        transferProfileProductReqVO.setAltBalance(" ");
        transferProfileProductReqVO.setAltBalanceLong(42L);
        transferProfileProductReqVO.setC2sMaxTxnAmt(" ");
        transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setC2sMinTxnAmt(" ");
        transferProfileProductReqVO.setC2sMinTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setCurrentBalance(" ");
        transferProfileProductReqVO.setMaxBalance(" ");
        transferProfileProductReqVO.setMaxBalanceAsLong(42L);
        transferProfileProductReqVO.setMinBalance(" ");
        transferProfileProductReqVO.setMinResidualBalanceAsLong(1L);
        transferProfileProductReqVO.setProductCode(" ");
        transferProfileProductReqVO.setProductName(" ");
        transferProfileProductReqVO.setProductShortCode(" ");

        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        productBalancelist.add(transferProfileProductReqVO);

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(productBalancelist);
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData25() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "Min Balance"
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateProductBalancelist(SaveTransferProfileDataCloneReqVO.java:821)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:794)
        //   See https://diff.blue/R013 to resolve this issue.

        TransferProfileProductReqVO transferProfileProductReqVO = mock(TransferProfileProductReqVO.class);
        when(transferProfileProductReqVO.getMinBalance()).thenReturn("Min Balance");
        doNothing().when(transferProfileProductReqVO).setAllowedMaxPercentage(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setAllowedMaxPercentageInt(anyInt());
        doNothing().when(transferProfileProductReqVO).setAltBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setAltBalanceLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setC2sMaxTxnAmt(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setC2sMaxTxnAmtAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setC2sMinTxnAmt(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setC2sMinTxnAmtAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setCurrentBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMaxBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMaxBalanceAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setMinBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMinResidualBalanceAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setProductCode(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setProductName(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setProductShortCode(Mockito.<String>any());
        transferProfileProductReqVO.setAllowedMaxPercentage(" ");
        transferProfileProductReqVO.setAllowedMaxPercentageInt(-1);
        transferProfileProductReqVO.setAltBalance(" ");
        transferProfileProductReqVO.setAltBalanceLong(42L);
        transferProfileProductReqVO.setC2sMaxTxnAmt(" ");
        transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setC2sMinTxnAmt(" ");
        transferProfileProductReqVO.setC2sMinTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setCurrentBalance(" ");
        transferProfileProductReqVO.setMaxBalance(" ");
        transferProfileProductReqVO.setMaxBalanceAsLong(42L);
        transferProfileProductReqVO.setMinBalance(" ");
        transferProfileProductReqVO.setMinResidualBalanceAsLong(1L);
        transferProfileProductReqVO.setProductCode(" ");
        transferProfileProductReqVO.setProductName(" ");
        transferProfileProductReqVO.setProductShortCode(" ");

        TransferProfileProductReqVO transferProfileProductReqVO2 = new TransferProfileProductReqVO();
        transferProfileProductReqVO2.setAllowedMaxPercentage("42");
        transferProfileProductReqVO2.setAllowedMaxPercentageInt(0);
        transferProfileProductReqVO2.setAltBalance("42");
        transferProfileProductReqVO2.setAltBalanceLong(1L);
        transferProfileProductReqVO2.setC2sMaxTxnAmt("42");
        transferProfileProductReqVO2.setC2sMaxTxnAmtAsLong(0L);
        transferProfileProductReqVO2.setC2sMinTxnAmt("42");
        transferProfileProductReqVO2.setC2sMinTxnAmtAsLong(0L);
        transferProfileProductReqVO2.setCurrentBalance("42");
        transferProfileProductReqVO2.setMaxBalance("42");
        transferProfileProductReqVO2.setMaxBalanceAsLong(1L);
        transferProfileProductReqVO2.setMinBalance("42");
        transferProfileProductReqVO2.setMinResidualBalanceAsLong(3L);
        transferProfileProductReqVO2.setProductCode("42");
        transferProfileProductReqVO2.setProductName("42");
        transferProfileProductReqVO2.setProductShortCode("42");

        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        productBalancelist.add(transferProfileProductReqVO2);
        productBalancelist.add(transferProfileProductReqVO);

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(productBalancelist);
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
    //@Ignore("TODO: Complete this test")
    public void testValidateFormData26() {
        com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: For input string: "Max Balance"
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateProductBalancelist(SaveTransferProfileDataCloneReqVO.java:822)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:794)
        //   See https://diff.blue/R013 to resolve this issue.

        TransferProfileProductReqVO transferProfileProductReqVO = mock(TransferProfileProductReqVO.class);
        when(transferProfileProductReqVO.getMaxBalance()).thenReturn("100");
        when(transferProfileProductReqVO.getMinBalance()).thenReturn("42");
        doNothing().when(transferProfileProductReqVO).setAllowedMaxPercentage(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setAllowedMaxPercentageInt(anyInt());
        doNothing().when(transferProfileProductReqVO).setAltBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setAltBalanceLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setC2sMaxTxnAmt(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setC2sMaxTxnAmtAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setC2sMinTxnAmt(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setC2sMinTxnAmtAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setCurrentBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMaxBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMaxBalanceAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setMinBalance(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setMinResidualBalanceAsLong(anyLong());
        doNothing().when(transferProfileProductReqVO).setProductCode(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setProductName(Mockito.<String>any());
        doNothing().when(transferProfileProductReqVO).setProductShortCode(Mockito.<String>any());
        transferProfileProductReqVO.setAllowedMaxPercentage(" ");
        transferProfileProductReqVO.setAllowedMaxPercentageInt(-1);
        transferProfileProductReqVO.setAltBalance(" ");
        transferProfileProductReqVO.setAltBalanceLong(42L);
        transferProfileProductReqVO.setC2sMaxTxnAmt(" ");
        transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setC2sMinTxnAmt(" ");
        transferProfileProductReqVO.setC2sMinTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setCurrentBalance(" ");
        transferProfileProductReqVO.setMaxBalance(" ");
        transferProfileProductReqVO.setMaxBalanceAsLong(42L);
        transferProfileProductReqVO.setMinBalance(" ");
        transferProfileProductReqVO.setMinResidualBalanceAsLong(1L);
        transferProfileProductReqVO.setProductCode(" ");
        transferProfileProductReqVO.setProductName(" ");
        transferProfileProductReqVO.setProductShortCode(" ");

        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        productBalancelist.add(transferProfileProductReqVO);

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(productBalancelist);
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData27() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:784)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData28() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setShortName("Short Name");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData29() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData30() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(new ArrayList<>());
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData31() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData32() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData33() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData34() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData35() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData36() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData37() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData38() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyInAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData39() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData40() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailyOutAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData41() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData42() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberOutAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData43() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData44() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData45() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInAltCount("3");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData46() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validationStepTen(SaveTransferProfileDataCloneReqVO.java:1744)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:804)
        //   See https://diff.blue/R013 to resolve this issue.

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setDailySubscriberInAltValue("42");
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }

    /**
     * Method under test: {@link SaveTransferProfileDataCloneReqVO#validateFormData(Locale)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testValidateFormData47() {
       com.btsl.util.JUnitConfig.init();
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NumberFormatException: empty String
        //       at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:1842)
        //       at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
        //       at java.lang.Double.parseDouble(Double.java:538)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateProductBalancelist(SaveTransferProfileDataCloneReqVO.java:821)
        //       at com.restapi.networkadmin.requestVO.SaveTransferProfileDataCloneReqVO.validateFormData(SaveTransferProfileDataCloneReqVO.java:794)
        //   See https://diff.blue/R013 to resolve this issue.

        TransferProfileProductReqVO transferProfileProductReqVO = new TransferProfileProductReqVO();
        transferProfileProductReqVO.setAllowedMaxPercentage(" ");
        transferProfileProductReqVO.setAllowedMaxPercentageInt(-1);
        transferProfileProductReqVO.setAltBalance(" ");
        transferProfileProductReqVO.setAltBalanceLong(42L);
        transferProfileProductReqVO.setC2sMaxTxnAmt(" ");
        transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setC2sMinTxnAmt(" ");
        transferProfileProductReqVO.setC2sMinTxnAmtAsLong(-1L);
        transferProfileProductReqVO.setCurrentBalance(" ");
        transferProfileProductReqVO.setMaxBalance(" ");
        transferProfileProductReqVO.setMaxBalanceAsLong(42L);
        transferProfileProductReqVO.setMinBalance(" ");
        transferProfileProductReqVO.setMinResidualBalanceAsLong(1L);
        transferProfileProductReqVO.setProductCode(" ");
        transferProfileProductReqVO.setProductName(" ");
        transferProfileProductReqVO.setProductShortCode(" ");

        ArrayList<TransferProfileProductReqVO> productBalancelist = new ArrayList<>();
        productBalancelist.add(transferProfileProductReqVO);

        SaveTransferProfileDataCloneReqVO saveTransferProfileDataCloneReqVO = new SaveTransferProfileDataCloneReqVO();
        saveTransferProfileDataCloneReqVO.setProductBalancelist(productBalancelist);
        saveTransferProfileDataCloneReqVO.setShortName(" ");
        saveTransferProfileDataCloneReqVO.validateFormData(Locale.getDefault());
    }
}

