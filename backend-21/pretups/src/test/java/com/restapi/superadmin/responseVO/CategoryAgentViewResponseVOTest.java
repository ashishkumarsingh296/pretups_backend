package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class CategoryAgentViewResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link CategoryAgentViewResponseVO}
     *   <li>{@link CategoryAgentViewResponseVO#setAgentAllowedSource(ArrayList)}
     *   <li>{@link CategoryAgentViewResponseVO#setAgentGeoList(ArrayList)}
     *   <li>{@link CategoryAgentViewResponseVO#setAgentRoleMap(HashMap)}
     *   <li>{@link CategoryAgentViewResponseVO#getAgentAllowedSource()}
     *   <li>{@link CategoryAgentViewResponseVO#getAgentGeoList()}
     *   <li>{@link CategoryAgentViewResponseVO#getAgentRoleMap()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        CategoryAgentViewResponseVO actualCategoryAgentViewResponseVO = new CategoryAgentViewResponseVO();
        ArrayList agentAllowedSource = new ArrayList();
        actualCategoryAgentViewResponseVO.setAgentAllowedSource(agentAllowedSource);
        ArrayList agentGeoList = new ArrayList();
        actualCategoryAgentViewResponseVO.setAgentGeoList(agentGeoList);
        HashMap agentRoleMap = new HashMap();
        actualCategoryAgentViewResponseVO.setAgentRoleMap(agentRoleMap);
        assertSame(agentAllowedSource, actualCategoryAgentViewResponseVO.getAgentAllowedSource());
        assertSame(agentGeoList, actualCategoryAgentViewResponseVO.getAgentGeoList());
        assertSame(agentRoleMap, actualCategoryAgentViewResponseVO.getAgentRoleMap());
    }
}

