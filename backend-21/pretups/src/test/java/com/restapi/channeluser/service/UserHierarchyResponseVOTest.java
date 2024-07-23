package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;

import java.util.ArrayList;

import org.junit.Test;

public class UserHierarchyResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierarchyResponseVO}
     *   <li>{@link UserHierarchyResponseVO#setChanerUserVO(ChannelUserVO)}
     *   <li>{@link UserHierarchyResponseVO#setFileName(String)}
     *   <li>{@link UserHierarchyResponseVO#setFileType(String)}
     *   <li>{@link UserHierarchyResponseVO#setFileattachment(String)}
     *   <li>{@link UserHierarchyResponseVO#setUserHierarchyList(ArrayList)}
     *   <li>{@link UserHierarchyResponseVO#toString()}
     *   <li>{@link UserHierarchyResponseVO#getChanerUserVO()}
     *   <li>{@link UserHierarchyResponseVO#getFileName()}
     *   <li>{@link UserHierarchyResponseVO#getFileType()}
     *   <li>{@link UserHierarchyResponseVO#getFileattachment()}
     *   <li>{@link UserHierarchyResponseVO#getUserHierarchyList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierarchyResponseVO actualUserHierarchyResponseVO = new UserHierarchyResponseVO();
        ChannelUserVO chanerUserVO = ChannelUserVO.getInstance();
        actualUserHierarchyResponseVO.setChanerUserVO(chanerUserVO);
        actualUserHierarchyResponseVO.setFileName("foo.txt");
        actualUserHierarchyResponseVO.setFileType("File Type");
        actualUserHierarchyResponseVO.setFileattachment("Fileattachment");
        ArrayList<ChannelUserVO> userHierarchyList = new ArrayList<>();
        actualUserHierarchyResponseVO.setUserHierarchyList(userHierarchyList);
        String actualToStringResult = actualUserHierarchyResponseVO.toString();
        assertSame(chanerUserVO, actualUserHierarchyResponseVO.getChanerUserVO());
        assertEquals("foo.txt", actualUserHierarchyResponseVO.getFileName());
        assertEquals("File Type", actualUserHierarchyResponseVO.getFileType());
        assertEquals("Fileattachment", actualUserHierarchyResponseVO.getFileattachment());
        assertSame(userHierarchyList, actualUserHierarchyResponseVO.getUserHierarchyList());
        assertEquals("UserHierarchyResponseVO [userHierarchyList=[]]", actualToStringResult);
    }
}

