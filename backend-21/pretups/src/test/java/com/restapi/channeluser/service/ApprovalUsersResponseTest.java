package com.restapi.channeluser.service;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ApprovalUsersResponseTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ApprovalUsersResponse}
     *   <li>{@link ApprovalUsersResponse#setUsersList(List)}
     *   <li>{@link ApprovalUsersResponse#getUsersList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ApprovalUsersResponse actualApprovalUsersResponse = new ApprovalUsersResponse();
        ArrayList<ChannelUserVO> usersList = new ArrayList<>();
        actualApprovalUsersResponse.setUsersList(usersList);
        assertSame(usersList, actualApprovalUsersResponse.getUsersList());
    }
}

