package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class GetUserWidgetResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetUserWidgetResponseVO}
     *   <li>{@link GetUserWidgetResponseVO#setWidgetList(ArrayList)}
     *   <li>{@link GetUserWidgetResponseVO#toString()}
     *   <li>{@link GetUserWidgetResponseVO#getWidgetList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetUserWidgetResponseVO actualGetUserWidgetResponseVO = new GetUserWidgetResponseVO();
        ArrayList<String> widgetList = new ArrayList<>();
        actualGetUserWidgetResponseVO.setWidgetList(widgetList);
        String actualToStringResult = actualGetUserWidgetResponseVO.toString();
        assertSame(widgetList, actualGetUserWidgetResponseVO.getWidgetList());
        assertEquals("GetUserWidgetResponseVO [widgetList=[]]", actualToStringResult);
    }
}

