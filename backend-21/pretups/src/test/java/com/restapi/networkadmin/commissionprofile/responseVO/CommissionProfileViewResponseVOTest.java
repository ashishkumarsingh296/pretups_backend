package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class CommissionProfileViewResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileViewResponseVO}
     *   <li>{@link CommissionProfileViewResponseVO#setAdditionalProfileList(ArrayList)}
     *   <li>{@link CommissionProfileViewResponseVO#setAddtnlComStatus(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setAddtnlComStatusName(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setApplicableFromDate(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setApplicableFromHour(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setCommProfileSetVersionId(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setCommissionProfileList(ArrayList)}
     *   <li>{@link CommissionProfileViewResponseVO#setCommissionType(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setCommissionTypeAsString(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setCommissionTypeValue(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setCommissionTypeValueAsString(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setDefaultProfile(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setDeleteAllowed(boolean)}
     *   <li>{@link CommissionProfileViewResponseVO#setDualCommType(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setDualCommTypeDesc(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setGatewayCode(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setGradeCode(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setOldApplicableFromDate(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setOldApplicableFromHour(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setOtfProfileList(ArrayList)}
     *   <li>{@link CommissionProfileViewResponseVO#setOtherCategoryCode(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setOtherCommissionProfile(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setOtherCommissionProfileAsString(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setProfileName(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setRoamRecharge(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setSequenceNo(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setShortCode(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setSlabList(ArrayList)}
     *   <li>{@link CommissionProfileViewResponseVO#setSubServiceCode(String)}
     *   <li>{@link CommissionProfileViewResponseVO#setVersion(String)}
     *   <li>{@link CommissionProfileViewResponseVO#getAdditionalProfileList()}
     *   <li>{@link CommissionProfileViewResponseVO#getAddtnlComStatus()}
     *   <li>{@link CommissionProfileViewResponseVO#getAddtnlComStatusName()}
     *   <li>{@link CommissionProfileViewResponseVO#getApplicableFromDate()}
     *   <li>{@link CommissionProfileViewResponseVO#getApplicableFromHour()}
     *   <li>{@link CommissionProfileViewResponseVO#getCommProfileSetVersionId()}
     *   <li>{@link CommissionProfileViewResponseVO#getCommissionProfileList()}
     *   <li>{@link CommissionProfileViewResponseVO#getCommissionType()}
     *   <li>{@link CommissionProfileViewResponseVO#getCommissionTypeAsString()}
     *   <li>{@link CommissionProfileViewResponseVO#getCommissionTypeValue()}
     *   <li>{@link CommissionProfileViewResponseVO#getCommissionTypeValueAsString()}
     *   <li>{@link CommissionProfileViewResponseVO#getDefaultProfile()}
     *   <li>{@link CommissionProfileViewResponseVO#getDualCommType()}
     *   <li>{@link CommissionProfileViewResponseVO#getDualCommTypeDesc()}
     *   <li>{@link CommissionProfileViewResponseVO#getGatewayCode()}
     *   <li>{@link CommissionProfileViewResponseVO#getGradeCode()}
     *   <li>{@link CommissionProfileViewResponseVO#getOldApplicableFromDate()}
     *   <li>{@link CommissionProfileViewResponseVO#getOldApplicableFromHour()}
     *   <li>{@link CommissionProfileViewResponseVO#getOtfProfileList()}
     *   <li>{@link CommissionProfileViewResponseVO#getOtherCategoryCode()}
     *   <li>{@link CommissionProfileViewResponseVO#getOtherCommissionProfile()}
     *   <li>{@link CommissionProfileViewResponseVO#getOtherCommissionProfileAsString()}
     *   <li>{@link CommissionProfileViewResponseVO#getProfileName()}
     *   <li>{@link CommissionProfileViewResponseVO#getRoamRecharge()}
     *   <li>{@link CommissionProfileViewResponseVO#getSequenceNo()}
     *   <li>{@link CommissionProfileViewResponseVO#getShortCode()}
     *   <li>{@link CommissionProfileViewResponseVO#getSlabList()}
     *   <li>{@link CommissionProfileViewResponseVO#getSubServiceCode()}
     *   <li>{@link CommissionProfileViewResponseVO#getVersion()}
     *   <li>{@link CommissionProfileViewResponseVO#isDeleteAllowed()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileViewResponseVO actualCommissionProfileViewResponseVO = new CommissionProfileViewResponseVO();
        ArrayList additionalProfileList = new ArrayList();
        actualCommissionProfileViewResponseVO.setAdditionalProfileList(additionalProfileList);
        actualCommissionProfileViewResponseVO.setAddtnlComStatus("Addtnl Com Status");
        actualCommissionProfileViewResponseVO.setAddtnlComStatusName("Addtnl Com Status Name");
        actualCommissionProfileViewResponseVO.setApplicableFromDate("2020-03-01");
        actualCommissionProfileViewResponseVO.setApplicableFromHour("jane.doe@example.org");
        actualCommissionProfileViewResponseVO.setCommProfileSetVersionId("42");
        ArrayList commissionProfileList = new ArrayList();
        actualCommissionProfileViewResponseVO.setCommissionProfileList(commissionProfileList);
        actualCommissionProfileViewResponseVO.setCommissionType("Commission Type");
        actualCommissionProfileViewResponseVO.setCommissionTypeAsString("Commission Type As String");
        actualCommissionProfileViewResponseVO.setCommissionTypeValue("42");
        actualCommissionProfileViewResponseVO.setCommissionTypeValueAsString("42");
        actualCommissionProfileViewResponseVO.setDefaultProfile("Default Profile");
        actualCommissionProfileViewResponseVO.setDeleteAllowed(true);
        actualCommissionProfileViewResponseVO.setDualCommType("Dual Comm Type");
        actualCommissionProfileViewResponseVO.setDualCommTypeDesc("Dual Comm Type Desc");
        actualCommissionProfileViewResponseVO.setGatewayCode("Gateway Code");
        actualCommissionProfileViewResponseVO.setGradeCode("Grade Code");
        actualCommissionProfileViewResponseVO.setOldApplicableFromDate("2020-03-01");
        actualCommissionProfileViewResponseVO.setOldApplicableFromHour("jane.doe@example.org");
        ArrayList otfProfileList = new ArrayList();
        actualCommissionProfileViewResponseVO.setOtfProfileList(otfProfileList);
        actualCommissionProfileViewResponseVO.setOtherCategoryCode("Other Category Code");
        actualCommissionProfileViewResponseVO.setOtherCommissionProfile("Other Commission Profile");
        actualCommissionProfileViewResponseVO.setOtherCommissionProfileAsString("Other Commission Profile As String");
        actualCommissionProfileViewResponseVO.setProfileName("foo.txt");
        actualCommissionProfileViewResponseVO.setRoamRecharge("Roam Recharge");
        actualCommissionProfileViewResponseVO.setSequenceNo("Sequence No");
        actualCommissionProfileViewResponseVO.setShortCode("Short Code");
        ArrayList slabList = new ArrayList();
        actualCommissionProfileViewResponseVO.setSlabList(slabList);
        actualCommissionProfileViewResponseVO.setSubServiceCode("Sub Service Code");
        actualCommissionProfileViewResponseVO.setVersion("1.0.2");
        assertSame(additionalProfileList, actualCommissionProfileViewResponseVO.getAdditionalProfileList());
        assertEquals("Addtnl Com Status", actualCommissionProfileViewResponseVO.getAddtnlComStatus());
        assertEquals("Addtnl Com Status Name", actualCommissionProfileViewResponseVO.getAddtnlComStatusName());
        assertEquals("2020-03-01", actualCommissionProfileViewResponseVO.getApplicableFromDate());
        assertEquals("jane.doe@example.org", actualCommissionProfileViewResponseVO.getApplicableFromHour());
        assertEquals("42", actualCommissionProfileViewResponseVO.getCommProfileSetVersionId());
        assertSame(commissionProfileList, actualCommissionProfileViewResponseVO.getCommissionProfileList());
        assertEquals("Commission Type", actualCommissionProfileViewResponseVO.getCommissionType());
        assertEquals("Commission Type As String", actualCommissionProfileViewResponseVO.getCommissionTypeAsString());
        assertEquals("42", actualCommissionProfileViewResponseVO.getCommissionTypeValue());
        assertEquals("42", actualCommissionProfileViewResponseVO.getCommissionTypeValueAsString());
        assertEquals("Default Profile", actualCommissionProfileViewResponseVO.getDefaultProfile());
        assertEquals("Dual Comm Type", actualCommissionProfileViewResponseVO.getDualCommType());
        assertEquals("Dual Comm Type Desc", actualCommissionProfileViewResponseVO.getDualCommTypeDesc());
        assertEquals("Gateway Code", actualCommissionProfileViewResponseVO.getGatewayCode());
        assertEquals("Grade Code", actualCommissionProfileViewResponseVO.getGradeCode());
        assertEquals("2020-03-01", actualCommissionProfileViewResponseVO.getOldApplicableFromDate());
        assertEquals("jane.doe@example.org", actualCommissionProfileViewResponseVO.getOldApplicableFromHour());
        assertSame(otfProfileList, actualCommissionProfileViewResponseVO.getOtfProfileList());
        assertEquals("Other Category Code", actualCommissionProfileViewResponseVO.getOtherCategoryCode());
        assertEquals("Other Commission Profile", actualCommissionProfileViewResponseVO.getOtherCommissionProfile());
        assertEquals("Other Commission Profile As String",
                actualCommissionProfileViewResponseVO.getOtherCommissionProfileAsString());
        assertEquals("foo.txt", actualCommissionProfileViewResponseVO.getProfileName());
        assertEquals("Roam Recharge", actualCommissionProfileViewResponseVO.getRoamRecharge());
        assertEquals("Sequence No", actualCommissionProfileViewResponseVO.getSequenceNo());
        assertEquals("Short Code", actualCommissionProfileViewResponseVO.getShortCode());
        assertSame(slabList, actualCommissionProfileViewResponseVO.getSlabList());
        assertEquals("Sub Service Code", actualCommissionProfileViewResponseVO.getSubServiceCode());
        assertEquals("1.0.2", actualCommissionProfileViewResponseVO.getVersion());
        assertTrue(actualCommissionProfileViewResponseVO.isDeleteAllowed());
    }
}

