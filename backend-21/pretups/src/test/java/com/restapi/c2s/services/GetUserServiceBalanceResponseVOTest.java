package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class GetUserServiceBalanceResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetUserServiceBalanceResponseVO}
     *   <li>{@link GetUserServiceBalanceResponseVO#setList(ArrayList)}
     *   <li>{@link GetUserServiceBalanceResponseVO#setMessage(String)}
     *   <li>{@link GetUserServiceBalanceResponseVO#setMessageCode(String)}
     *   <li>{@link GetUserServiceBalanceResponseVO#setServiceList(ArrayList)}
     *   <li>{@link GetUserServiceBalanceResponseVO#setStatus(String)}
     *   <li>{@link GetUserServiceBalanceResponseVO#toString()}
     *   <li>{@link GetUserServiceBalanceResponseVO#getList()}
     *   <li>{@link GetUserServiceBalanceResponseVO#getMessage()}
     *   <li>{@link GetUserServiceBalanceResponseVO#getMessageCode()}
     *   <li>{@link GetUserServiceBalanceResponseVO#getServiceList()}
     *   <li>{@link GetUserServiceBalanceResponseVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetUserServiceBalanceResponseVO actualGetUserServiceBalanceResponseVO = new GetUserServiceBalanceResponseVO();
        ArrayList<UserBalanceVO> list = new ArrayList<>();
        actualGetUserServiceBalanceResponseVO.setList(list);
        actualGetUserServiceBalanceResponseVO.setMessage("Not all who wander are lost");
        actualGetUserServiceBalanceResponseVO.setMessageCode("Message Code");
        ArrayList<ServiceViceBalanceVO> serviceList = new ArrayList<>();
        actualGetUserServiceBalanceResponseVO.setServiceList(serviceList);
        actualGetUserServiceBalanceResponseVO.setStatus("Status");
        String actualToStringResult = actualGetUserServiceBalanceResponseVO.toString();
        assertSame(list, actualGetUserServiceBalanceResponseVO.getList());
        assertEquals("Not all who wander are lost", actualGetUserServiceBalanceResponseVO.getMessage());
        assertEquals("Message Code", actualGetUserServiceBalanceResponseVO.getMessageCode());
        assertSame(serviceList, actualGetUserServiceBalanceResponseVO.getServiceList());
        assertEquals("Status", actualGetUserServiceBalanceResponseVO.getStatus());
        assertEquals("GetUserServiceBalanceResponseVO [messageCode=Message Code, message=Not all who wander are lost,"
                + " status=Status, serviceList=[]], balanceList=[]]", actualToStringResult);
    }
}

