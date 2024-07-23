package com.restapi.superadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GatewayMappingVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GatewayMappingVO}
     *   <li>{@link GatewayMappingVO#setModifyFlag(String)}
     *   <li>{@link GatewayMappingVO#set_altresponseGatewayCode(String)}
     *   <li>{@link GatewayMappingVO#set_requestGatewayCode(String)}
     *   <li>{@link GatewayMappingVO#set_responseGatewayCode(String)}
     *   <li>{@link GatewayMappingVO#getModifyFlag()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GatewayMappingVO actualGatewayMappingVO = new GatewayMappingVO();
        actualGatewayMappingVO.setModifyFlag("Modify Flag");
        actualGatewayMappingVO.set_altresponseGatewayCode(" altresponse Gateway Code");
        actualGatewayMappingVO.set_requestGatewayCode(" request Gateway Code");
        actualGatewayMappingVO.set_responseGatewayCode(" response Gateway Code");
        assertEquals("Modify Flag", actualGatewayMappingVO.getModifyFlag());
    }

    /**
     * Method under test: {@link GatewayMappingVO#get_altresponseGatewayCode()}
     */
    @Test
    public void testGet_altresponseGatewayCode() {
        assertNull((new GatewayMappingVO()).get_altresponseGatewayCode());
    }

    /**
     * Method under test: {@link GatewayMappingVO#get_requestGatewayCode()}
     */
    @Test
    public void testGet_requestGatewayCode() {
        assertNull((new GatewayMappingVO()).get_requestGatewayCode());
    }

    /**
     * Method under test: {@link GatewayMappingVO#get_responseGatewayCode()}
     */
    @Test
    public void testGet_responseGatewayCode() {
        assertNull((new GatewayMappingVO()).get_responseGatewayCode());
    }
}

