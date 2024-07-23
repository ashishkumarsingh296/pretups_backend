package com.restapi.superadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class MessageModifyResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link MessageModifyResponseVO}
     *   <li>{@link MessageModifyResponseVO#setRequestGatewayList(ArrayList)}
     *   <li>{@link MessageModifyResponseVO#setResponseGatewayList(ArrayList)}
     *   <li>{@link MessageModifyResponseVO#getRequestGatewayList()}
     *   <li>{@link MessageModifyResponseVO#getResponseGatewayList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        MessageModifyResponseVO actualMessageModifyResponseVO = new MessageModifyResponseVO();
        ArrayList requestGatewayList = new ArrayList();
        actualMessageModifyResponseVO.setRequestGatewayList(requestGatewayList);
        ArrayList responseGatewayList = new ArrayList();
        actualMessageModifyResponseVO.setResponseGatewayList(responseGatewayList);
        assertSame(requestGatewayList, actualMessageModifyResponseVO.getRequestGatewayList());
        assertSame(responseGatewayList, actualMessageModifyResponseVO.getResponseGatewayList());
    }
}

