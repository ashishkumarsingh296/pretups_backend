package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UserWidgetRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserWidgetRequestVO}
     *   <li>{@link UserWidgetRequestVO#setWigetList(List)}
     *   <li>{@link UserWidgetRequestVO#toString()}
     *   <li>{@link UserWidgetRequestVO#getWigetList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserWidgetRequestVO actualUserWidgetRequestVO = new UserWidgetRequestVO();
        ArrayList<String> wigetList = new ArrayList<>();
        actualUserWidgetRequestVO.setWigetList(wigetList);
        String actualToStringResult = actualUserWidgetRequestVO.toString();
        assertSame(wigetList, actualUserWidgetRequestVO.getWigetList());
        assertEquals("UserWidgetRequestVO [WigetList=[]]", actualToStringResult);
    }
}

