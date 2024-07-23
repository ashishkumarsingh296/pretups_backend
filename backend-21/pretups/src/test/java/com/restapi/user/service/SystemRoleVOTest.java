package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SystemRoleVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SystemRoleVO}
     *   <li>{@link SystemRoleVO#setDefaultType(String)}
     *   <li>{@link SystemRoleVO#setDomainType(String)}
     *   <li>{@link SystemRoleVO#setRoleCode(String)}
     *   <li>{@link SystemRoleVO#setRoleName(String)}
     *   <li>{@link SystemRoleVO#setRoleType(String)}
     *   <li>{@link SystemRoleVO#setStatus(String)}
     *   <li>{@link SystemRoleVO#toString()}
     *   <li>{@link SystemRoleVO#getDefaultType()}
     *   <li>{@link SystemRoleVO#getDomainType()}
     *   <li>{@link SystemRoleVO#getRoleCode()}
     *   <li>{@link SystemRoleVO#getRoleName()}
     *   <li>{@link SystemRoleVO#getRoleType()}
     *   <li>{@link SystemRoleVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SystemRoleVO actualSystemRoleVO = new SystemRoleVO();
        actualSystemRoleVO.setDefaultType("Default Type");
        actualSystemRoleVO.setDomainType("Domain Type");
        actualSystemRoleVO.setRoleCode("Role Code");
        actualSystemRoleVO.setRoleName("Role Name");
        actualSystemRoleVO.setRoleType("Role Type");
        actualSystemRoleVO.setStatus("Status");
        actualSystemRoleVO.toString();
        assertEquals("Default Type", actualSystemRoleVO.getDefaultType());
        assertEquals("Domain Type", actualSystemRoleVO.getDomainType());
        assertEquals("Role Code", actualSystemRoleVO.getRoleCode());
        assertEquals("Role Name", actualSystemRoleVO.getRoleName());
        assertEquals("Role Type", actualSystemRoleVO.getRoleType());
        assertEquals("Status", actualSystemRoleVO.getStatus());
    }
}

