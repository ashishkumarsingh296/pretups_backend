package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class CategoryListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CategoryListResponseVO}
     *   <li>{@link CategoryListResponseVO#setCategoryList(ArrayList)}
     *   <li>{@link CategoryListResponseVO#setDomainName(String)}
     *   <li>{@link CategoryListResponseVO#setDomainTypeCode(String)}
     *   <li>{@link CategoryListResponseVO#setGroupRoleMap(HashMap)}
     *   <li>{@link CategoryListResponseVO#setHideAddButton(boolean)}
     *   <li>{@link CategoryListResponseVO#setMessageGatewayList(ArrayList)}
     *   <li>{@link CategoryListResponseVO#setSystemRoleMap(HashMap)}
     *   <li>{@link CategoryListResponseVO#setUserPrefixIdDisableinModify(boolean)}
     *   <li>{@link CategoryListResponseVO#setUserType(String)}
     *   <li>{@link CategoryListResponseVO#getCategoryList()}
     *   <li>{@link CategoryListResponseVO#getDomainName()}
     *   <li>{@link CategoryListResponseVO#getDomainTypeCode()}
     *   <li>{@link CategoryListResponseVO#getGroupRoleMap()}
     *   <li>{@link CategoryListResponseVO#getMessageGatewayList()}
     *   <li>{@link CategoryListResponseVO#getSystemRoleMap()}
     *   <li>{@link CategoryListResponseVO#getUserType()}
     *   <li>{@link CategoryListResponseVO#isHideAddButton()}
     *   <li>{@link CategoryListResponseVO#isUserPrefixIdDisableinModify()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CategoryListResponseVO actualCategoryListResponseVO = new CategoryListResponseVO();
        ArrayList categoryList = new ArrayList();
        actualCategoryListResponseVO.setCategoryList(categoryList);
        actualCategoryListResponseVO.setDomainName("Domain Name");
        actualCategoryListResponseVO.setDomainTypeCode("Domain Type Code");
        HashMap<String, ArrayList> groupRoleMap = new HashMap<>();
        actualCategoryListResponseVO.setGroupRoleMap(groupRoleMap);
        actualCategoryListResponseVO.setHideAddButton(true);
        ArrayList messageGatewayList = new ArrayList();
        actualCategoryListResponseVO.setMessageGatewayList(messageGatewayList);
        HashMap<String, ArrayList> systemRoleMap = new HashMap<>();
        actualCategoryListResponseVO.setSystemRoleMap(systemRoleMap);
        actualCategoryListResponseVO.setUserPrefixIdDisableinModify(true);
        actualCategoryListResponseVO.setUserType("User Type");
        assertSame(categoryList, actualCategoryListResponseVO.getCategoryList());
        assertEquals("Domain Name", actualCategoryListResponseVO.getDomainName());
        assertEquals("Domain Type Code", actualCategoryListResponseVO.getDomainTypeCode());
        assertSame(groupRoleMap, actualCategoryListResponseVO.getGroupRoleMap());
        assertSame(messageGatewayList, actualCategoryListResponseVO.getMessageGatewayList());
        assertSame(systemRoleMap, actualCategoryListResponseVO.getSystemRoleMap());
        assertEquals("User Type", actualCategoryListResponseVO.getUserType());
        assertTrue(actualCategoryListResponseVO.isHideAddButton());
        assertTrue(actualCategoryListResponseVO.isUserPrefixIdDisableinModify());
    }
}

