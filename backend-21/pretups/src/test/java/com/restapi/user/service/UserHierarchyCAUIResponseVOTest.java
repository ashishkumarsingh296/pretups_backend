package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UserHierarchyCAUIResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierarchyCAUIResponseVO}
     *   <li>{@link UserHierarchyCAUIResponseVO#setLevel(int)}
     *   <li>{@link UserHierarchyCAUIResponseVO#setUserHierarchyUIResponseData(List)}
     *   <li>{@link UserHierarchyCAUIResponseVO#getLevel()}
     *   <li>{@link UserHierarchyCAUIResponseVO#getUserHierarchyUIResponseData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierarchyCAUIResponseVO actualUserHierarchyCAUIResponseVO = new UserHierarchyCAUIResponseVO();
        actualUserHierarchyCAUIResponseVO.setLevel(1);
        ArrayList<UserHierarchyUIResponseData> userHierarchyUIResponseData = new ArrayList<>();
        actualUserHierarchyCAUIResponseVO.setUserHierarchyUIResponseData(userHierarchyUIResponseData);
        assertEquals(1, actualUserHierarchyCAUIResponseVO.getLevel());
        assertSame(userHierarchyUIResponseData, actualUserHierarchyCAUIResponseVO.getUserHierarchyUIResponseData());
    }
}

