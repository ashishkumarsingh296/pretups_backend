package com.restapi.networkadmin.commissionprofile.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommissionProfileViewDetailsRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileViewDetailsRequestVO}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setCategoryCode(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setCommProfileSetId(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setCommProfileSetVersionId(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setCommissionType(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setDomainCode(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setGradeCode(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setGrphDomainCode(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#setNetworkCode(String)}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getCategoryCode()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getCommProfileSetId()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getCommProfileSetVersionId()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getCommissionType()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getDomainCode()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getGradeCode()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getGrphDomainCode()}
     *   <li>{@link CommissionProfileViewDetailsRequestVO#getNetworkCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileViewDetailsRequestVO actualCommissionProfileViewDetailsRequestVO = new CommissionProfileViewDetailsRequestVO();
        actualCommissionProfileViewDetailsRequestVO.setCategoryCode("Category Code");
        actualCommissionProfileViewDetailsRequestVO.setCommProfileSetId("42");
        actualCommissionProfileViewDetailsRequestVO.setCommProfileSetVersionId("42");
        actualCommissionProfileViewDetailsRequestVO.setCommissionType("Commission Type");
        actualCommissionProfileViewDetailsRequestVO.setDomainCode("Domain Code");
        actualCommissionProfileViewDetailsRequestVO.setGradeCode("Grade Code");
        actualCommissionProfileViewDetailsRequestVO.setGrphDomainCode("Grph Domain Code");
        actualCommissionProfileViewDetailsRequestVO.setNetworkCode("Network Code");
        assertEquals("Category Code", actualCommissionProfileViewDetailsRequestVO.getCategoryCode());
        assertEquals("42", actualCommissionProfileViewDetailsRequestVO.getCommProfileSetId());
        assertEquals("42", actualCommissionProfileViewDetailsRequestVO.getCommProfileSetVersionId());
        assertEquals("Commission Type", actualCommissionProfileViewDetailsRequestVO.getCommissionType());
        assertEquals("Domain Code", actualCommissionProfileViewDetailsRequestVO.getDomainCode());
        assertEquals("Grade Code", actualCommissionProfileViewDetailsRequestVO.getGradeCode());
        assertEquals("Grph Domain Code", actualCommissionProfileViewDetailsRequestVO.getGrphDomainCode());
        assertEquals("Network Code", actualCommissionProfileViewDetailsRequestVO.getNetworkCode());
    }
}

