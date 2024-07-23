package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserHierachyCARequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierachyCARequestVO}
     *   <li>{@link UserHierachyCARequestVO#setAdvancedSearch(boolean)}
     *   <li>{@link UserHierachyCARequestVO#setLoginID(String)}
     *   <li>{@link UserHierachyCARequestVO#setMsisdn(String)}
     *   <li>{@link UserHierachyCARequestVO#setOwnerName(String)}
     *   <li>{@link UserHierachyCARequestVO#setParentCategory(String)}
     *   <li>{@link UserHierachyCARequestVO#setParentUserId(String)}
     *   <li>{@link UserHierachyCARequestVO#setUserCategory(String)}
     *   <li>{@link UserHierachyCARequestVO#setUserStatus(String)}
     *   <li>{@link UserHierachyCARequestVO#toString()}
     *   <li>{@link UserHierachyCARequestVO#getLoginID()}
     *   <li>{@link UserHierachyCARequestVO#getMsisdn()}
     *   <li>{@link UserHierachyCARequestVO#getOwnerName()}
     *   <li>{@link UserHierachyCARequestVO#getParentCategory()}
     *   <li>{@link UserHierachyCARequestVO#getParentUserId()}
     *   <li>{@link UserHierachyCARequestVO#getUserCategory()}
     *   <li>{@link UserHierachyCARequestVO#getUserStatus()}
     *   <li>{@link UserHierachyCARequestVO#isAdvancedSearch()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierachyCARequestVO actualUserHierachyCARequestVO = new UserHierachyCARequestVO();
        actualUserHierachyCARequestVO.setAdvancedSearch(true);
        actualUserHierachyCARequestVO.setLoginID("Login ID");
        actualUserHierachyCARequestVO.setMsisdn("Msisdn");
        actualUserHierachyCARequestVO.setOwnerName("Owner Name");
        actualUserHierachyCARequestVO.setParentCategory("Parent Category");
        actualUserHierachyCARequestVO.setParentUserId("42");
        actualUserHierachyCARequestVO.setUserCategory("User Category");
        actualUserHierachyCARequestVO.setUserStatus("User Status");
        String actualToStringResult = actualUserHierachyCARequestVO.toString();
        assertEquals("Login ID", actualUserHierachyCARequestVO.getLoginID());
        assertEquals("Msisdn", actualUserHierachyCARequestVO.getMsisdn());
        assertEquals("Owner Name", actualUserHierachyCARequestVO.getOwnerName());
        assertEquals("Parent Category", actualUserHierachyCARequestVO.getParentCategory());
        assertEquals("42", actualUserHierachyCARequestVO.getParentUserId());
        assertEquals("User Category", actualUserHierachyCARequestVO.getUserCategory());
        assertEquals("User Status", actualUserHierachyCARequestVO.getUserStatus());
        assertTrue(actualUserHierachyCARequestVO.isAdvancedSearch());
        assertEquals("UserHierachyCARequestVO [loginID=Login ID, msisdn=Msisdn, ownerName=Owner Name, parentCategory=Parent"
                        + " Category, parentUserId=42, userCategory=User Category, userStatus=User Status, advancedSearch" + "=true]",
                actualToStringResult);
    }
}

