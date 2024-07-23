package com.restapi.superadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class MessageGatewayResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MessageGatewayResponseVO}
     *   <li>{@link MessageGatewayResponseVO#setMessageGatewayList(ArrayList)}
     *   <li>{@link MessageGatewayResponseVO#getMessageGatewayList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MessageGatewayResponseVO actualMessageGatewayResponseVO = new MessageGatewayResponseVO();
        ArrayList messageGatewayList = new ArrayList();
        actualMessageGatewayResponseVO.setMessageGatewayList(messageGatewayList);
        assertSame(messageGatewayList, actualMessageGatewayResponseVO.getMessageGatewayList());
    }
}

