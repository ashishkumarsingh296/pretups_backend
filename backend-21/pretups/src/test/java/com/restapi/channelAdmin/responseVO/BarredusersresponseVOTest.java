package com.restapi.channelAdmin.responseVO;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BarredusersresponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BarredusersresponseVO}
     *   <li>{@link BarredusersresponseVO#setUsersList(List)}
     *   <li>{@link BarredusersresponseVO#getUsersList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BarredusersresponseVO actualBarredusersresponseVO = new BarredusersresponseVO();
        ArrayList<ChannelUserVO> usersList = new ArrayList<>();
        actualBarredusersresponseVO.setUsersList(usersList);
        assertSame(usersList, actualBarredusersresponseVO.getUsersList());
    }
}

