package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelUserTransferResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserTransferResponseVO}
     *   <li>{@link ChannelUserTransferResponseVO#setCategory(String)}
     *   <li>{@link ChannelUserTransferResponseVO#setDomain(String)}
     *   <li>{@link ChannelUserTransferResponseVO#setGeography(String)}
     *   <li>{@link ChannelUserTransferResponseVO#setParentName(String)}
     *   <li>{@link ChannelUserTransferResponseVO#setUserName(String)}
     *   <li>{@link ChannelUserTransferResponseVO#toString()}
     *   <li>{@link ChannelUserTransferResponseVO#getCategory()}
     *   <li>{@link ChannelUserTransferResponseVO#getDomain()}
     *   <li>{@link ChannelUserTransferResponseVO#getGeography()}
     *   <li>{@link ChannelUserTransferResponseVO#getParentName()}
     *   <li>{@link ChannelUserTransferResponseVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserTransferResponseVO actualChannelUserTransferResponseVO = new ChannelUserTransferResponseVO();
        actualChannelUserTransferResponseVO.setCategory("Category");
        actualChannelUserTransferResponseVO.setDomain("Domain");
        actualChannelUserTransferResponseVO.setGeography("Geography");
        actualChannelUserTransferResponseVO.setParentName("Parent Name");
        actualChannelUserTransferResponseVO.setUserName("janedoe");
        String actualToStringResult = actualChannelUserTransferResponseVO.toString();
        assertEquals("Category", actualChannelUserTransferResponseVO.getCategory());
        assertEquals("Domain", actualChannelUserTransferResponseVO.getDomain());
        assertEquals("Geography", actualChannelUserTransferResponseVO.getGeography());
        assertEquals("Parent Name", actualChannelUserTransferResponseVO.getParentName());
        assertEquals("janedoe", actualChannelUserTransferResponseVO.getUserName());
        assertEquals(
                "ChannelUserTransferResponseVO [domain=Domain, category=Category, geography=Geography, parentName=Parent"
                        + " Name, userName=janedoe]",
                actualToStringResult);
    }
}

