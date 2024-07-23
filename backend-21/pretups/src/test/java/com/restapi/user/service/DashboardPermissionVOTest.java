package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class DashboardPermissionVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DashboardPermissionVO}
     *   <li>{@link DashboardPermissionVO#setId(String)}
     *   <li>{@link DashboardPermissionVO#setItems(ArrayList)}
     *   <li>{@link DashboardPermissionVO#getId()}
     *   <li>{@link DashboardPermissionVO#getItems()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DashboardPermissionVO actualDashboardPermissionVO = new DashboardPermissionVO();
        actualDashboardPermissionVO.setId("42");
        ArrayList<Item> items = new ArrayList<>();
        actualDashboardPermissionVO.setItems(items);
        assertEquals("42", actualDashboardPermissionVO.getId());
        assertSame(items, actualDashboardPermissionVO.getItems());
    }
}

