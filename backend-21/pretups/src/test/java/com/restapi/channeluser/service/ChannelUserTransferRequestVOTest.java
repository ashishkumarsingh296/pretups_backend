package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelUserTransferRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserTransferRequestVO}
     *   <li>{@link ChannelUserTransferRequestVO#setCategory(String)}
     *   <li>{@link ChannelUserTransferRequestVO#setDomain(String)}
     *   <li>{@link ChannelUserTransferRequestVO#setGeography(String)}
     *   <li>{@link ChannelUserTransferRequestVO#setMsisdn(String)}
     *   <li>{@link ChannelUserTransferRequestVO#setOwnerId(String)}
     *   <li>{@link ChannelUserTransferRequestVO#toString()}
     *   <li>{@link ChannelUserTransferRequestVO#getCategory()}
     *   <li>{@link ChannelUserTransferRequestVO#getDomain()}
     *   <li>{@link ChannelUserTransferRequestVO#getGeography()}
     *   <li>{@link ChannelUserTransferRequestVO#getMsisdn()}
     *   <li>{@link ChannelUserTransferRequestVO#getOwnerId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserTransferRequestVO actualChannelUserTransferRequestVO = new ChannelUserTransferRequestVO();
        actualChannelUserTransferRequestVO.setCategory("Category");
        actualChannelUserTransferRequestVO.setDomain("Domain");
        actualChannelUserTransferRequestVO.setGeography("Geography");
        actualChannelUserTransferRequestVO.setMsisdn("Msisdn");
        actualChannelUserTransferRequestVO.setOwnerId("42");
        String actualToStringResult = actualChannelUserTransferRequestVO.toString();
        assertEquals("Category", actualChannelUserTransferRequestVO.getCategory());
        assertEquals("Domain", actualChannelUserTransferRequestVO.getDomain());
        assertEquals("Geography", actualChannelUserTransferRequestVO.getGeography());
        assertEquals("Msisdn", actualChannelUserTransferRequestVO.getMsisdn());
        assertEquals("42", actualChannelUserTransferRequestVO.getOwnerId());
        assertEquals("ChannelUserTransferRequestVO [msisdn=Msisdn, domain=Domain, category=Category, geography=Geography,"
                + " ownerId=42]", actualToStringResult);
    }
}

