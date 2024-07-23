package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class UserHierarchyUIResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierarchyUIResponseVO}
     *   <li>{@link UserHierarchyUIResponseVO#setLevel(int)}
     *   <li>{@link UserHierarchyUIResponseVO#setUserHierarchyUIResponseData(UserHierarchyUIResponseData)}
     *   <li>{@link UserHierarchyUIResponseVO#getLevel()}
     *   <li>{@link UserHierarchyUIResponseVO#getUserHierarchyUIResponseData()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierarchyUIResponseVO actualUserHierarchyUIResponseVO = new UserHierarchyUIResponseVO();
        actualUserHierarchyUIResponseVO.setLevel(1);
        UserHierarchyUIResponseData userHierarchyUIResponseData = new UserHierarchyUIResponseData();
        userHierarchyUIResponseData.setBalanceList(new ArrayList<>());
        userHierarchyUIResponseData.setCategory("Category");
        userHierarchyUIResponseData.setCategoryCode("Category Code");
        userHierarchyUIResponseData.setChildList(new ArrayList<>());
        userHierarchyUIResponseData.setLevel(1);
        userHierarchyUIResponseData.setLoginId("42");
        userHierarchyUIResponseData.setMsisdn("Msisdn");
        userHierarchyUIResponseData.setParentID("Parent ID");
        userHierarchyUIResponseData.setStatus("Status");
        userHierarchyUIResponseData.setStatusCode("Status Code");
        userHierarchyUIResponseData.setUserID("User ID");
        userHierarchyUIResponseData.setUserType("User Type");
        userHierarchyUIResponseData.setUsername("janedoe");
        actualUserHierarchyUIResponseVO.setUserHierarchyUIResponseData(userHierarchyUIResponseData);
        assertEquals(1, actualUserHierarchyUIResponseVO.getLevel());
        assertSame(userHierarchyUIResponseData, actualUserHierarchyUIResponseVO.getUserHierarchyUIResponseData());
    }
}

