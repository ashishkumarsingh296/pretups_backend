package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.domain.businesslogic.DomainVO;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

public class DomainManagmentResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link DomainManagmentResponseVO}
     *   <li>{@link DomainManagmentResponseVO#setAllowdSource(ArrayList)}
     *   <li>{@link DomainManagmentResponseVO#setDomainList(ArrayList)}
     *   <li>{@link DomainManagmentResponseVO#setGeoList(ArrayList)}
     *   <li>{@link DomainManagmentResponseVO#setRoleList(HashMap)}
     *   <li>{@link DomainManagmentResponseVO#toString()}
     *   <li>{@link DomainManagmentResponseVO#getAllowdSource()}
     *   <li>{@link DomainManagmentResponseVO#getDomainList()}
     *   <li>{@link DomainManagmentResponseVO#getGeoList()}
     *   <li>{@link DomainManagmentResponseVO#getRoleList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        DomainManagmentResponseVO actualDomainManagmentResponseVO = new DomainManagmentResponseVO();
        ArrayList allowdSource = new ArrayList();
        actualDomainManagmentResponseVO.setAllowdSource(allowdSource);
        ArrayList<DomainVO> domainList = new ArrayList<>();
        actualDomainManagmentResponseVO.setDomainList(domainList);
        ArrayList geoList = new ArrayList();
        actualDomainManagmentResponseVO.setGeoList(geoList);
        HashMap roleList = new HashMap();
        actualDomainManagmentResponseVO.setRoleList(roleList);
        String actualToStringResult = actualDomainManagmentResponseVO.toString();
        assertSame(allowdSource, actualDomainManagmentResponseVO.getAllowdSource());
        assertSame(domainList, actualDomainManagmentResponseVO.getDomainList());
        assertSame(geoList, actualDomainManagmentResponseVO.getGeoList());
        assertSame(roleList, actualDomainManagmentResponseVO.getRoleList());
        assertEquals("DomainManagmentResponseVO [domainList=[], geoList=[], allowdSource=[], roleList={}]",
                actualToStringResult);
    }
}

