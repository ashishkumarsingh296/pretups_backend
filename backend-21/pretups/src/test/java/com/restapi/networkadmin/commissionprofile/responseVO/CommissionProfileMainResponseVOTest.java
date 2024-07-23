package com.restapi.networkadmin.commissionprofile.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class CommissionProfileMainResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CommissionProfileMainResponseVO}
     *   <li>{@link CommissionProfileMainResponseVO#setGeoList(ArrayList)}
     *   <li>{@link CommissionProfileMainResponseVO#setGradeList(ArrayList)}
     *   <li>{@link CommissionProfileMainResponseVO#getGeoList()}
     *   <li>{@link CommissionProfileMainResponseVO#getGradeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CommissionProfileMainResponseVO actualCommissionProfileMainResponseVO = new CommissionProfileMainResponseVO();
        ArrayList geoList = new ArrayList();
        actualCommissionProfileMainResponseVO.setGeoList(geoList);
        ArrayList gradeList = new ArrayList();
        actualCommissionProfileMainResponseVO.setGradeList(gradeList);
        assertSame(geoList, actualCommissionProfileMainResponseVO.getGeoList());
        assertSame(gradeList, actualCommissionProfileMainResponseVO.getGradeList());
    }
}

