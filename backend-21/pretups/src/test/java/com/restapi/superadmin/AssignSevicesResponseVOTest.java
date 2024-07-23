package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class AssignSevicesResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AssignSevicesResponseVO}
     *   <li>{@link AssignSevicesResponseVO#setDomainList(ArrayList)}
     *   <li>{@link AssignSevicesResponseVO#setProductList(ArrayList)}
     *   <li>{@link AssignSevicesResponseVO#setServicesList(ArrayList)}
     *   <li>{@link AssignSevicesResponseVO#setVoucherSegmentList(ArrayList)}
     *   <li>{@link AssignSevicesResponseVO#setVoucherTypeList(ArrayList)}
     *   <li>{@link AssignSevicesResponseVO#toString()}
     *   <li>{@link AssignSevicesResponseVO#getDomainList()}
     *   <li>{@link AssignSevicesResponseVO#getProductList()}
     *   <li>{@link AssignSevicesResponseVO#getServicesList()}
     *   <li>{@link AssignSevicesResponseVO#getVoucherSegmentList()}
     *   <li>{@link AssignSevicesResponseVO#getVoucherTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        AssignSevicesResponseVO actualAssignSevicesResponseVO = new AssignSevicesResponseVO();
        ArrayList domainList = new ArrayList();
        actualAssignSevicesResponseVO.setDomainList(domainList);
        ArrayList productList = new ArrayList();
        actualAssignSevicesResponseVO.setProductList(productList);
        ArrayList servicesList = new ArrayList();
        actualAssignSevicesResponseVO.setServicesList(servicesList);
        ArrayList voucherSegmentList = new ArrayList();
        actualAssignSevicesResponseVO.setVoucherSegmentList(voucherSegmentList);
        ArrayList voucherTypeList = new ArrayList();
        actualAssignSevicesResponseVO.setVoucherTypeList(voucherTypeList);
        String actualToStringResult = actualAssignSevicesResponseVO.toString();
        assertSame(domainList, actualAssignSevicesResponseVO.getDomainList());
        assertSame(productList, actualAssignSevicesResponseVO.getProductList());
        assertSame(servicesList, actualAssignSevicesResponseVO.getServicesList());
        assertSame(voucherSegmentList, actualAssignSevicesResponseVO.getVoucherSegmentList());
        assertSame(voucherTypeList, actualAssignSevicesResponseVO.getVoucherTypeList());
        assertEquals("AssignSevicesResponseVO [domainList=[], productList=[], voucherTypeList=[], voucherSegmentList=[],"
                + " servicesList=[]]", actualToStringResult);
    }
}

