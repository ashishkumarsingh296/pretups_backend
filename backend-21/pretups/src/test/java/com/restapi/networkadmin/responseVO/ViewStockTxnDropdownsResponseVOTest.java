package com.restapi.networkadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ViewStockTxnDropdownsResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ViewStockTxnDropdownsResponseVO}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setFromDateStr(String)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setNetworkCode(String)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setRoamNetworkList(ArrayList)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setStatusList(ArrayList)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setStockTypeList(ArrayList)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setToDateStr(String)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#setUserID(String)}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getFromDateStr()}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getNetworkCode()}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getRoamNetworkList()}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getStatusList()}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getStockTypeList()}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getToDateStr()}
     *   <li>{@link ViewStockTxnDropdownsResponseVO#getUserID()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ViewStockTxnDropdownsResponseVO actualViewStockTxnDropdownsResponseVO = new ViewStockTxnDropdownsResponseVO();
        actualViewStockTxnDropdownsResponseVO.setFromDateStr("2020-03-01");
        actualViewStockTxnDropdownsResponseVO.setNetworkCode("Network Code");
        ArrayList roamNetworkList = new ArrayList();
        actualViewStockTxnDropdownsResponseVO.setRoamNetworkList(roamNetworkList);
        ArrayList statusList = new ArrayList();
        actualViewStockTxnDropdownsResponseVO.setStatusList(statusList);
        ArrayList stockTypeList = new ArrayList();
        actualViewStockTxnDropdownsResponseVO.setStockTypeList(stockTypeList);
        actualViewStockTxnDropdownsResponseVO.setToDateStr("2020-03-01");
        actualViewStockTxnDropdownsResponseVO.setUserID("User ID");
        assertEquals("2020-03-01", actualViewStockTxnDropdownsResponseVO.getFromDateStr());
        assertEquals("Network Code", actualViewStockTxnDropdownsResponseVO.getNetworkCode());
        assertSame(roamNetworkList, actualViewStockTxnDropdownsResponseVO.getRoamNetworkList());
        assertSame(statusList, actualViewStockTxnDropdownsResponseVO.getStatusList());
        assertSame(stockTypeList, actualViewStockTxnDropdownsResponseVO.getStockTypeList());
        assertEquals("2020-03-01", actualViewStockTxnDropdownsResponseVO.getToDateStr());
        assertEquals("User ID", actualViewStockTxnDropdownsResponseVO.getUserID());
    }
}

