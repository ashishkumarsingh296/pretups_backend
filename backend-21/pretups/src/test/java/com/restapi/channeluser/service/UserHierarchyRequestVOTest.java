package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserHierarchyRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UserHierarchyRequestVO}
     *   <li>{@link UserHierarchyRequestVO#setGeography(String)}
     *   <li>{@link UserHierarchyRequestVO#setLoginId(String)}
     *   <li>{@link UserHierarchyRequestVO#setMsisdn(String)}
     *   <li>{@link UserHierarchyRequestVO#setParentCategory(String)}
     *   <li>{@link UserHierarchyRequestVO#setStatus(String)}
     *   <li>{@link UserHierarchyRequestVO#setUserCategory(String)}
     *   <li>{@link UserHierarchyRequestVO#setUserDomain(String)}
     *   <li>{@link UserHierarchyRequestVO#toString()}
     *   <li>{@link UserHierarchyRequestVO#getGeography()}
     *   <li>{@link UserHierarchyRequestVO#getLoginId()}
     *   <li>{@link UserHierarchyRequestVO#getMsisdn()}
     *   <li>{@link UserHierarchyRequestVO#getParentCategory()}
     *   <li>{@link UserHierarchyRequestVO#getStatus()}
     *   <li>{@link UserHierarchyRequestVO#getUserCategory()}
     *   <li>{@link UserHierarchyRequestVO#getUserDomain()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UserHierarchyRequestVO actualUserHierarchyRequestVO = new UserHierarchyRequestVO();
        actualUserHierarchyRequestVO.setGeography("Geography");
        actualUserHierarchyRequestVO.setLoginId("42");
        actualUserHierarchyRequestVO.setMsisdn("Msisdn");
        actualUserHierarchyRequestVO.setParentCategory("Parent Category");
        actualUserHierarchyRequestVO.setStatus("Status");
        actualUserHierarchyRequestVO.setUserCategory("User Category");
        actualUserHierarchyRequestVO.setUserDomain("User Domain");
        String actualToStringResult = actualUserHierarchyRequestVO.toString();
        assertEquals("Geography", actualUserHierarchyRequestVO.getGeography());
        assertEquals("42", actualUserHierarchyRequestVO.getLoginId());
        assertEquals("Msisdn", actualUserHierarchyRequestVO.getMsisdn());
        assertEquals("Parent Category", actualUserHierarchyRequestVO.getParentCategory());
        assertEquals("Status", actualUserHierarchyRequestVO.getStatus());
        assertEquals("User Category", actualUserHierarchyRequestVO.getUserCategory());
        assertEquals("User Domain", actualUserHierarchyRequestVO.getUserDomain());
        assertEquals("UserHierarchyRequestVO [userDomain=User Domain, parentCategory=Parent Category, geography=Geography,"
                + " userCategory=User Category, status=Status, msisdn=Msisdn, loginId=42]", actualToStringResult);
    }
}

