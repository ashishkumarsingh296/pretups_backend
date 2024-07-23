package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelAdminTransferVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelAdminTransferVO}
     *   <li>{@link ChannelAdminTransferVO#setToParentUser(String)}
     *   <li>{@link ChannelAdminTransferVO#setUserId(String)}
     *   <li>{@link ChannelAdminTransferVO#toString()}
     *   <li>{@link ChannelAdminTransferVO#getToParentUser()}
     *   <li>{@link ChannelAdminTransferVO#getUserId()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelAdminTransferVO actualChannelAdminTransferVO = new ChannelAdminTransferVO();
        actualChannelAdminTransferVO.setToParentUser("To Parent User");
        actualChannelAdminTransferVO.setUserId("42");
        String actualToStringResult = actualChannelAdminTransferVO.toString();
        assertEquals("To Parent User", actualChannelAdminTransferVO.getToParentUser());
        assertEquals("42", actualChannelAdminTransferVO.getUserId());
        assertEquals("ChannelAdminTransferVO [fromParentUserId=42, toParentUserId=To Parent User]", actualToStringResult);
    }
}

