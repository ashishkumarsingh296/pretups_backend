package com.restapi.o2c.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class O2CStockAppRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link O2CStockAppRequestVO}
     *   <li>{@link O2CStockAppRequestVO#setO2cStockAppRequests(List)}
     *   <li>{@link O2CStockAppRequestVO#toString()}
     *   <li>{@link O2CStockAppRequestVO#getO2cStockAppRequests()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        O2CStockAppRequestVO actualO2cStockAppRequestVO = new O2CStockAppRequestVO();
        ArrayList<O2CDataStApp> o2cStockAppRequests = new ArrayList<>();
        actualO2cStockAppRequestVO.setO2cStockAppRequests(o2cStockAppRequests);
        String actualToStringResult = actualO2cStockAppRequestVO.toString();
        assertSame(o2cStockAppRequests, actualO2cStockAppRequestVO.getO2cStockAppRequests());
        assertEquals("O2CStockAppRequestVO [o2cStockAppRequests=[]]", actualToStringResult);
    }
}

