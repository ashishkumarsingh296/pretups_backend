package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReconcileServiceListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ReconcileServiceListResponseVO}
     *   <li>{@link ReconcileServiceListResponseVO#setServicesList(List)}
     *   <li>{@link ReconcileServiceListResponseVO#toString()}
     *   <li>{@link ReconcileServiceListResponseVO#getServicesList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ReconcileServiceListResponseVO actualReconcileServiceListResponseVO = new ReconcileServiceListResponseVO();
        ArrayList<ServiceListFilter> servicesList = new ArrayList<>();
        actualReconcileServiceListResponseVO.setServicesList(servicesList);
        String actualToStringResult = actualReconcileServiceListResponseVO.toString();
        assertSame(servicesList, actualReconcileServiceListResponseVO.getServicesList());
        assertEquals("ServiceListVO [servicesList=[]]", actualToStringResult);
    }
}

