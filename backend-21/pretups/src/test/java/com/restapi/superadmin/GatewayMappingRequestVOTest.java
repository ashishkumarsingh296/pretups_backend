package com.restapi.superadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class GatewayMappingRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GatewayMappingRequestVO}
     *   <li>{@link GatewayMappingRequestVO#setGatewayList(ArrayList)}
     *   <li>{@link GatewayMappingRequestVO#getGatewayList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GatewayMappingRequestVO actualGatewayMappingRequestVO = new GatewayMappingRequestVO();
        ArrayList<GatewayMappingVO> gatewayList = new ArrayList<>();
        actualGatewayMappingRequestVO.setGatewayList(gatewayList);
        assertSame(gatewayList, actualGatewayMappingRequestVO.getGatewayList());
    }
}

