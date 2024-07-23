package com.restapi.channelAdmin;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelUserDTO;

import java.util.ArrayList;

import org.junit.Test;

public class ChannelUserListByParentResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserListByParentResponseVO}
     *   <li>{@link ChannelUserListByParentResponseVO#setChannelUsersList(ArrayList)}
     *   <li>{@link ChannelUserListByParentResponseVO#getChannelUsersList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserListByParentResponseVO actualChannelUserListByParentResponseVO = new ChannelUserListByParentResponseVO();
        ArrayList<ChannelUserDTO> channelUsersList = new ArrayList<>();
        actualChannelUserListByParentResponseVO.setChannelUsersList(channelUsersList);
        assertSame(channelUsersList, actualChannelUserListByParentResponseVO.getChannelUsersList());
    }
}

