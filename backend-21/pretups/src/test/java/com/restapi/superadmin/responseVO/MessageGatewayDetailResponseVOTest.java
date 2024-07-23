package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class MessageGatewayDetailResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MessageGatewayDetailResponseVO}
     *   <li>{@link MessageGatewayDetailResponseVO#setHandlerClassDescription(String)}
     *   <li>{@link MessageGatewayDetailResponseVO#setHandlerClassList(ArrayList)}
     *   <li>{@link MessageGatewayDetailResponseVO#getHandlerClassDescription()}
     *   <li>{@link MessageGatewayDetailResponseVO#getHandlerClassList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MessageGatewayDetailResponseVO actualMessageGatewayDetailResponseVO = new MessageGatewayDetailResponseVO();
        actualMessageGatewayDetailResponseVO.setHandlerClassDescription("Handler Class Description");
        ArrayList handlerClassList = new ArrayList();
        actualMessageGatewayDetailResponseVO.setHandlerClassList(handlerClassList);
        assertEquals("Handler Class Description", actualMessageGatewayDetailResponseVO.getHandlerClassDescription());
        assertSame(handlerClassList, actualMessageGatewayDetailResponseVO.getHandlerClassList());
    }
}

