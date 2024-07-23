package com.restapi.channelAdmin;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.StaffUserDTO;

import java.util.ArrayList;

import org.junit.Test;

public class StaffUserListByParentResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link StaffUserListByParentResponseVO}
     *   <li>{@link StaffUserListByParentResponseVO#setStaffuserList(ArrayList)}
     *   <li>{@link StaffUserListByParentResponseVO#getStaffuserList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        StaffUserListByParentResponseVO actualStaffUserListByParentResponseVO = new StaffUserListByParentResponseVO();
        ArrayList<StaffUserDTO> staffuserList = new ArrayList<>();
        actualStaffUserListByParentResponseVO.setStaffuserList(staffuserList);
        assertSame(staffuserList, actualStaffUserListByParentResponseVO.getStaffuserList());
    }
}

