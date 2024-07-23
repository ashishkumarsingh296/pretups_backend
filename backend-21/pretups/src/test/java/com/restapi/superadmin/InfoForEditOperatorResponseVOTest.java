package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class InfoForEditOperatorResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link InfoForEditOperatorResponseVO}
     *   <li>{@link InfoForEditOperatorResponseVO#setDomainList(ArrayList)}
     *   <li>{@link InfoForEditOperatorResponseVO#setProductList(ArrayList)}
     *   <li>{@link InfoForEditOperatorResponseVO#setSerivesList(ArrayList)}
     *   <li>{@link InfoForEditOperatorResponseVO#setVoucherSegmentList(ArrayList)}
     *   <li>{@link InfoForEditOperatorResponseVO#setVoucherTypeList(ArrayList)}
     *   <li>{@link InfoForEditOperatorResponseVO#toString()}
     *   <li>{@link InfoForEditOperatorResponseVO#getDomainList()}
     *   <li>{@link InfoForEditOperatorResponseVO#getProductList()}
     *   <li>{@link InfoForEditOperatorResponseVO#getSerivesList()}
     *   <li>{@link InfoForEditOperatorResponseVO#getVoucherSegmentList()}
     *   <li>{@link InfoForEditOperatorResponseVO#getVoucherTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        InfoForEditOperatorResponseVO actualInfoForEditOperatorResponseVO = new InfoForEditOperatorResponseVO();
        ArrayList domainList = new ArrayList();
        actualInfoForEditOperatorResponseVO.setDomainList(domainList);
        ArrayList productList = new ArrayList();
        actualInfoForEditOperatorResponseVO.setProductList(productList);
        ArrayList serivesList = new ArrayList();
        actualInfoForEditOperatorResponseVO.setSerivesList(serivesList);
        ArrayList voucherSegmentList = new ArrayList();
        actualInfoForEditOperatorResponseVO.setVoucherSegmentList(voucherSegmentList);
        ArrayList voucherTypeList = new ArrayList();
        actualInfoForEditOperatorResponseVO.setVoucherTypeList(voucherTypeList);
        String actualToStringResult = actualInfoForEditOperatorResponseVO.toString();
        assertSame(domainList, actualInfoForEditOperatorResponseVO.getDomainList());
        assertSame(productList, actualInfoForEditOperatorResponseVO.getProductList());
        assertSame(serivesList, actualInfoForEditOperatorResponseVO.getSerivesList());
        assertSame(voucherSegmentList, actualInfoForEditOperatorResponseVO.getVoucherSegmentList());
        assertSame(voucherTypeList, actualInfoForEditOperatorResponseVO.getVoucherTypeList());
        assertEquals("InfoForEditOperatorResponseVO [domainList=[], serivesList=[], productList=[], voucherTypeList=[],"
                + " voucherSegmentList=[]]", actualToStringResult);
    }
}

