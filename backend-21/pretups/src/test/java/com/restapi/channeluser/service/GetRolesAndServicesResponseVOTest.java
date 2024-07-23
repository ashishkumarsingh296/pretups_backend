package com.restapi.channeluser.service;

import static org.junit.Assert.assertSame;

import com.btsl.pretups.master.businesslogic.LocaleMasterVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class GetRolesAndServicesResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GetRolesAndServicesResponseVO}
     *   <li>{@link GetRolesAndServicesResponseVO#setGroupRole(Map)}
     *   <li>{@link GetRolesAndServicesResponseVO#setLanguagesList(List)}
     *   <li>{@link GetRolesAndServicesResponseVO#setProfileList(ArrayList)}
     *   <li>{@link GetRolesAndServicesResponseVO#setServicesList(List)}
     *   <li>{@link GetRolesAndServicesResponseVO#setSystemRole(Map)}
     *   <li>{@link GetRolesAndServicesResponseVO#setVoucherList(List)}
     *   <li>{@link GetRolesAndServicesResponseVO#getGroupRole()}
     *   <li>{@link GetRolesAndServicesResponseVO#getLanguagesList()}
     *   <li>{@link GetRolesAndServicesResponseVO#getProfileList()}
     *   <li>{@link GetRolesAndServicesResponseVO#getServicesList()}
     *   <li>{@link GetRolesAndServicesResponseVO#getSystemRole()}
     *   <li>{@link GetRolesAndServicesResponseVO#getVoucherList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GetRolesAndServicesResponseVO actualGetRolesAndServicesResponseVO = new GetRolesAndServicesResponseVO();
        HashMap<Object, Object> groupRole = new HashMap<>();
        actualGetRolesAndServicesResponseVO.setGroupRole(groupRole);
        ArrayList<LocaleMasterVO> languagesList = new ArrayList<>();
        actualGetRolesAndServicesResponseVO.setLanguagesList(languagesList);
        ArrayList profileList = new ArrayList();
        actualGetRolesAndServicesResponseVO.setProfileList(profileList);
        ArrayList<String> serviceTypeList = new ArrayList<>();
        actualGetRolesAndServicesResponseVO.setServicesList(serviceTypeList);
        HashMap<Object, Object> systemRole = new HashMap<>();
        actualGetRolesAndServicesResponseVO.setSystemRole(systemRole);
        ArrayList<String> voucherList = new ArrayList<>();
        actualGetRolesAndServicesResponseVO.setVoucherList(voucherList);
        assertSame(groupRole, actualGetRolesAndServicesResponseVO.getGroupRole());
        assertSame(languagesList, actualGetRolesAndServicesResponseVO.getLanguagesList());
        assertSame(profileList, actualGetRolesAndServicesResponseVO.getProfileList());
        assertSame(serviceTypeList, actualGetRolesAndServicesResponseVO.getServicesList());
        assertSame(systemRole, actualGetRolesAndServicesResponseVO.getSystemRole());
        assertSame(voucherList, actualGetRolesAndServicesResponseVO.getVoucherList());
    }
}

