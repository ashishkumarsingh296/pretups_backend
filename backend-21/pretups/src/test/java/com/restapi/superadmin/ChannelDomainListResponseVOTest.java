package com.restapi.superadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class ChannelDomainListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelDomainListResponseVO}
     *   <li>{@link ChannelDomainListResponseVO#setChannelDomainList(ArrayList)}
     *   <li>{@link ChannelDomainListResponseVO#getChannelDomainList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelDomainListResponseVO actualChannelDomainListResponseVO = new ChannelDomainListResponseVO();
        ArrayList channelDomainList = new ArrayList();
        actualChannelDomainListResponseVO.setChannelDomainList(channelDomainList);
        assertSame(channelDomainList, actualChannelDomainListResponseVO.getChannelDomainList());
    }
}

