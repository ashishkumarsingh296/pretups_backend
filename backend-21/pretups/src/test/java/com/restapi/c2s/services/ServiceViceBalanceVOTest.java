package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ServiceViceBalanceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ServiceViceBalanceVO}
     *   <li>{@link ServiceViceBalanceVO#setBalanceAssociated(String)}
     *   <li>{@link ServiceViceBalanceVO#setServiceCode(String)}
     *   <li>{@link ServiceViceBalanceVO#setServiceName(String)}
     *   <li>{@link ServiceViceBalanceVO#toString()}
     *   <li>{@link ServiceViceBalanceVO#getBalanceAssociated()}
     *   <li>{@link ServiceViceBalanceVO#getServiceCode()}
     *   <li>{@link ServiceViceBalanceVO#getServiceName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ServiceViceBalanceVO actualServiceViceBalanceVO = new ServiceViceBalanceVO();
        actualServiceViceBalanceVO.setBalanceAssociated("Balance Associated");
        actualServiceViceBalanceVO.setServiceCode("Service Code");
        actualServiceViceBalanceVO.setServiceName("Service Name");
        String actualToStringResult = actualServiceViceBalanceVO.toString();
        assertEquals("Balance Associated", actualServiceViceBalanceVO.getBalanceAssociated());
        assertEquals("Service Code", actualServiceViceBalanceVO.getServiceCode());
        assertEquals("Service Name", actualServiceViceBalanceVO.getServiceName());
        assertEquals("ServiceViceBalanceVO [serviceCode=Service Code, serviceName=Service Name, balanceAssociated=Balance"
                + " Associated]", actualToStringResult);
    }
}

