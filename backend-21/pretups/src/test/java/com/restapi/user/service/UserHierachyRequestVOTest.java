package com.restapi.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserHierachyRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierachyRequestVO}
     *   <li>{@link UserHierachyRequestVO#setAdvancedSearch(boolean)}
     *   <li>{@link UserHierachyRequestVO#setLoginID(String)}
     *   <li>{@link UserHierachyRequestVO#setMsisdn(String)}
     *   <li>{@link UserHierachyRequestVO#setOwnerName(String)}
     *   <li>{@link UserHierachyRequestVO#setParentCategory(String)}
     *   <li>{@link UserHierachyRequestVO#setSimpleSearch(boolean)}
     *   <li>{@link UserHierachyRequestVO#setUserCategory(String)}
     *   <li>{@link UserHierachyRequestVO#setUserStatus(String)}
     *   <li>{@link UserHierachyRequestVO#toString()}
     *   <li>{@link UserHierachyRequestVO#getLoginID()}
     *   <li>{@link UserHierachyRequestVO#getMsisdn()}
     *   <li>{@link UserHierachyRequestVO#getOwnerName()}
     *   <li>{@link UserHierachyRequestVO#getParentCategory()}
     *   <li>{@link UserHierachyRequestVO#getUserCategory()}
     *   <li>{@link UserHierachyRequestVO#getUserStatus()}
     *   <li>{@link UserHierachyRequestVO#isAdvancedSearch()}
     *   <li>{@link UserHierachyRequestVO#isSimpleSearch()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierachyRequestVO actualUserHierachyRequestVO = new UserHierachyRequestVO();
        actualUserHierachyRequestVO.setAdvancedSearch(true);
        actualUserHierachyRequestVO.setLoginID("Login ID");
        actualUserHierachyRequestVO.setMsisdn("Msisdn");
        actualUserHierachyRequestVO.setOwnerName("Owner Name");
        actualUserHierachyRequestVO.setParentCategory("Parent Category");
        actualUserHierachyRequestVO.setSimpleSearch(true);
        actualUserHierachyRequestVO.setUserCategory("User Category");
        actualUserHierachyRequestVO.setUserStatus("User Status");
        String actualToStringResult = actualUserHierachyRequestVO.toString();
        assertEquals("Login ID", actualUserHierachyRequestVO.getLoginID());
        assertEquals("Msisdn", actualUserHierachyRequestVO.getMsisdn());
        assertEquals("Owner Name", actualUserHierachyRequestVO.getOwnerName());
        assertEquals("Parent Category", actualUserHierachyRequestVO.getParentCategory());
        assertEquals("User Category", actualUserHierachyRequestVO.getUserCategory());
        assertEquals("User Status", actualUserHierachyRequestVO.getUserStatus());
        assertTrue(actualUserHierachyRequestVO.isAdvancedSearch());
        assertTrue(actualUserHierachyRequestVO.isSimpleSearch());
        assertEquals("UserHierachyRequestVO [loginID=Login ID, msisdn=Msisdn, ownerName=Owner Name, parentCategory=Parent"
                        + " Category, userCategory=User Category, userStatus=User Status, advancedSearch=true, simpleSearch" + "=true]",
                actualToStringResult);
    }
}

