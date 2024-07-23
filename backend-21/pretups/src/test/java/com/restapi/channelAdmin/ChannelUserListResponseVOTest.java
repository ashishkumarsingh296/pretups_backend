package com.restapi.channelAdmin;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;

import java.util.ArrayList;

import org.junit.Test;

public class ChannelUserListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserListResponseVO}
     *   <li>{@link ChannelUserListResponseVO#setChannelUsersList(ArrayList)}
     *   <li>{@link ChannelUserListResponseVO#getChannelUsersList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserListResponseVO actualChannelUserListResponseVO = new ChannelUserListResponseVO();
        ArrayList<GetChannelUsersMsg> channelUsersList = new ArrayList<>();
        actualChannelUserListResponseVO.setChannelUsersList(channelUsersList);
        assertSame(channelUsersList, actualChannelUserListResponseVO.getChannelUsersList());
    }
}

