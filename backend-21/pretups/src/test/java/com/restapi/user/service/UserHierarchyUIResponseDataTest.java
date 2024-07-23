package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.channel.transfer.businesslogic.BalanceVO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UserHierarchyUIResponseDataTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierarchyUIResponseData}
     *   <li>{@link UserHierarchyUIResponseData#setBalanceList(List)}
     *   <li>{@link UserHierarchyUIResponseData#setCategory(String)}
     *   <li>{@link UserHierarchyUIResponseData#setCategoryCode(String)}
     *   <li>{@link UserHierarchyUIResponseData#setChildList(List)}
     *   <li>{@link UserHierarchyUIResponseData#setLevel(int)}
     *   <li>{@link UserHierarchyUIResponseData#setLoginId(String)}
     *   <li>{@link UserHierarchyUIResponseData#setMsisdn(String)}
     *   <li>{@link UserHierarchyUIResponseData#setParentID(String)}
     *   <li>{@link UserHierarchyUIResponseData#setStatus(String)}
     *   <li>{@link UserHierarchyUIResponseData#setStatusCode(String)}
     *   <li>{@link UserHierarchyUIResponseData#setUserID(String)}
     *   <li>{@link UserHierarchyUIResponseData#setUserType(String)}
     *   <li>{@link UserHierarchyUIResponseData#setUsername(String)}
     *   <li>{@link UserHierarchyUIResponseData#toString()}
     *   <li>{@link UserHierarchyUIResponseData#getBalanceList()}
     *   <li>{@link UserHierarchyUIResponseData#getCategory()}
     *   <li>{@link UserHierarchyUIResponseData#getCategoryCode()}
     *   <li>{@link UserHierarchyUIResponseData#getChildList()}
     *   <li>{@link UserHierarchyUIResponseData#getLevel()}
     *   <li>{@link UserHierarchyUIResponseData#getLoginId()}
     *   <li>{@link UserHierarchyUIResponseData#getMsisdn()}
     *   <li>{@link UserHierarchyUIResponseData#getParentID()}
     *   <li>{@link UserHierarchyUIResponseData#getStatus()}
     *   <li>{@link UserHierarchyUIResponseData#getStatusCode()}
     *   <li>{@link UserHierarchyUIResponseData#getUserID()}
     *   <li>{@link UserHierarchyUIResponseData#getUserType()}
     *   <li>{@link UserHierarchyUIResponseData#getUsername()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierarchyUIResponseData actualUserHierarchyUIResponseData = new UserHierarchyUIResponseData();
        ArrayList<BalanceVO> balanceList = new ArrayList<>();
        actualUserHierarchyUIResponseData.setBalanceList(balanceList);
        actualUserHierarchyUIResponseData.setCategory("Category");
        actualUserHierarchyUIResponseData.setCategoryCode("Category Code");
        ArrayList<UserHierarchyUIResponseData> childList = new ArrayList<>();
        actualUserHierarchyUIResponseData.setChildList(childList);
        actualUserHierarchyUIResponseData.setLevel(1);
        actualUserHierarchyUIResponseData.setLoginId("42");
        actualUserHierarchyUIResponseData.setMsisdn("Msisdn");
        actualUserHierarchyUIResponseData.setParentID("Parent ID");
        actualUserHierarchyUIResponseData.setStatus("Status");
        actualUserHierarchyUIResponseData.setStatusCode("Status Code");
        actualUserHierarchyUIResponseData.setUserID("User ID");
        actualUserHierarchyUIResponseData.setUserType("User Type");
        actualUserHierarchyUIResponseData.setUsername("janedoe");
        String actualToStringResult = actualUserHierarchyUIResponseData.toString();
        assertSame(balanceList, actualUserHierarchyUIResponseData.getBalanceList());
        assertEquals("Category", actualUserHierarchyUIResponseData.getCategory());
        assertEquals("Category Code", actualUserHierarchyUIResponseData.getCategoryCode());
        assertSame(childList, actualUserHierarchyUIResponseData.getChildList());
        assertEquals(1, actualUserHierarchyUIResponseData.getLevel());
        assertEquals("42", actualUserHierarchyUIResponseData.getLoginId());
        assertEquals("Msisdn", actualUserHierarchyUIResponseData.getMsisdn());
        assertEquals("Parent ID", actualUserHierarchyUIResponseData.getParentID());
        assertEquals("Status", actualUserHierarchyUIResponseData.getStatus());
        assertEquals("Status Code", actualUserHierarchyUIResponseData.getStatusCode());
        assertEquals("User ID", actualUserHierarchyUIResponseData.getUserID());
        assertEquals("User Type", actualUserHierarchyUIResponseData.getUserType());
        assertEquals("janedoe", actualUserHierarchyUIResponseData.getUsername());
        assertEquals("UserHierarchyUIResponseData [username=janedoe, msisdn=Msisdn, balanceList=[], parentID=Parent ID,"
                + " userID=User ID, status=Status, statusCode=Status Code, category=Category, categoryCode=Category Code,"
                + " loginId=42, level=1, userType=User Type, childList=[]]", actualToStringResult);
    }
}

