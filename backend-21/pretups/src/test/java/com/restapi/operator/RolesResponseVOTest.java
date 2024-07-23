package com.restapi.operator;

import static org.junit.Assert.assertSame;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class RolesResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link RolesResponseVO}
     *   <li>{@link RolesResponseVO#setDomainList(List)}
     *   <li>{@link RolesResponseVO#setGroupRole(Map)}
     *   <li>{@link RolesResponseVO#setLanguagesList(List)}
     *   <li>{@link RolesResponseVO#setServicesList(List)}
     *   <li>{@link RolesResponseVO#setSystemRole(Map)}
     *   <li>{@link RolesResponseVO#setVoucherList(List)}
     *   <li>{@link RolesResponseVO#getDomainList()}
     *   <li>{@link RolesResponseVO#getGroupRole()}
     *   <li>{@link RolesResponseVO#getLanguagesList()}
     *   <li>{@link RolesResponseVO#getServicesList()}
     *   <li>{@link RolesResponseVO#getSystemRole()}
     *   <li>{@link RolesResponseVO#getVoucherList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        RolesResponseVO actualRolesResponseVO = new RolesResponseVO();
        ArrayList<ListValueVO> domainList = new ArrayList<>();
        actualRolesResponseVO.setDomainList(domainList);
        HashMap<Object, Object> groupRole = new HashMap<>();
        actualRolesResponseVO.setGroupRole(groupRole);
        ArrayList<LocaleMasterVO> languagesList = new ArrayList<>();
        actualRolesResponseVO.setLanguagesList(languagesList);
        ArrayList<String> serviceTypeList = new ArrayList<>();
        actualRolesResponseVO.setServicesList(serviceTypeList);
        HashMap<Object, Object> systemRole = new HashMap<>();
        actualRolesResponseVO.setSystemRole(systemRole);
        ArrayList<ListValueVO> voucherList = new ArrayList<>();
        actualRolesResponseVO.setVoucherList(voucherList);
        assertSame(domainList, actualRolesResponseVO.getDomainList());
        assertSame(groupRole, actualRolesResponseVO.getGroupRole());
        assertSame(languagesList, actualRolesResponseVO.getLanguagesList());
        assertSame(serviceTypeList, actualRolesResponseVO.getServicesList());
        assertSame(systemRole, actualRolesResponseVO.getSystemRole());
        assertSame(voucherList, actualRolesResponseVO.getVoucherList());
    }
}

