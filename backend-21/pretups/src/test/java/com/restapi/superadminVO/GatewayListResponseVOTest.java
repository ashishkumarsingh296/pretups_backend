package com.restapi.superadminVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class GatewayListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GatewayListResponseVO}
     *   <li>{@link GatewayListResponseVO#setClassHandler(String)}
     *   <li>{@link GatewayListResponseVO#setGatewaySubTypeList(ArrayList)}
     *   <li>{@link GatewayListResponseVO#setGatewayTypeList(ArrayList)}
     *   <li>{@link GatewayListResponseVO#getClassHandler()}
     *   <li>{@link GatewayListResponseVO#getGatewaySubTypeList()}
     *   <li>{@link GatewayListResponseVO#getGatewayTypeList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GatewayListResponseVO actualGatewayListResponseVO = new GatewayListResponseVO();
        actualGatewayListResponseVO.setClassHandler("Class Handler");
        ArrayList gatewaySubTypeList = new ArrayList();
        actualGatewayListResponseVO.setGatewaySubTypeList(gatewaySubTypeList);
        ArrayList gatewayTypeList = new ArrayList();
        actualGatewayListResponseVO.setGatewayTypeList(gatewayTypeList);
        assertEquals("Class Handler", actualGatewayListResponseVO.getClassHandler());
        assertSame(gatewaySubTypeList, actualGatewayListResponseVO.getGatewaySubTypeList());
        assertSame(gatewayTypeList, actualGatewayListResponseVO.getGatewayTypeList());
    }
}

