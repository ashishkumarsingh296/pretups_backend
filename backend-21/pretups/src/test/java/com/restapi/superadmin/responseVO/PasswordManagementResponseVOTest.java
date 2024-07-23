package com.restapi.superadmin.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PasswordManagementResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link PasswordManagementResponseVO}
     *   <li>{@link PasswordManagementResponseVO#setAddress(String)}
     *   <li>{@link PasswordManagementResponseVO#setCategory(String)}
     *   <li>{@link PasswordManagementResponseVO#setCity(String)}
     *   <li>{@link PasswordManagementResponseVO#setContactPerson(String)}
     *   <li>{@link PasswordManagementResponseVO#setCountry(String)}
     *   <li>{@link PasswordManagementResponseVO#setDesignation(String)}
     *   <li>{@link PasswordManagementResponseVO#setInvalidPasswordCount(Integer)}
     *   <li>{@link PasswordManagementResponseVO#setLoginID(String)}
     *   <li>{@link PasswordManagementResponseVO#setMobileNumber(String)}
     *   <li>{@link PasswordManagementResponseVO#setNetworkCode(String)}
     *   <li>{@link PasswordManagementResponseVO#setNetworkName(String)}
     *   <li>{@link PasswordManagementResponseVO#setOwnerID(String)}
     *   <li>{@link PasswordManagementResponseVO#setOwnerName(String)}
     *   <li>{@link PasswordManagementResponseVO#setParentID(String)}
     *   <li>{@link PasswordManagementResponseVO#setParentName(String)}
     *   <li>{@link PasswordManagementResponseVO#setPasswordStatus(String)}
     *   <li>{@link PasswordManagementResponseVO#setRemarks(String)}
     *   <li>{@link PasswordManagementResponseVO#setShortName(String)}
     *   <li>{@link PasswordManagementResponseVO#setSsn(String)}
     *   <li>{@link PasswordManagementResponseVO#setState(String)}
     *   <li>{@link PasswordManagementResponseVO#setUserGrade(String)}
     *   <li>{@link PasswordManagementResponseVO#setUserID(String)}
     *   <li>{@link PasswordManagementResponseVO#setUserName(String)}
     *   <li>{@link PasswordManagementResponseVO#getAddress()}
     *   <li>{@link PasswordManagementResponseVO#getCategory()}
     *   <li>{@link PasswordManagementResponseVO#getCity()}
     *   <li>{@link PasswordManagementResponseVO#getContactPerson()}
     *   <li>{@link PasswordManagementResponseVO#getCountry()}
     *   <li>{@link PasswordManagementResponseVO#getDesignation()}
     *   <li>{@link PasswordManagementResponseVO#getInvalidPasswordCount()}
     *   <li>{@link PasswordManagementResponseVO#getLoginID()}
     *   <li>{@link PasswordManagementResponseVO#getMobileNumber()}
     *   <li>{@link PasswordManagementResponseVO#getNetworkCode()}
     *   <li>{@link PasswordManagementResponseVO#getNetworkName()}
     *   <li>{@link PasswordManagementResponseVO#getOwnerID()}
     *   <li>{@link PasswordManagementResponseVO#getOwnerName()}
     *   <li>{@link PasswordManagementResponseVO#getParentID()}
     *   <li>{@link PasswordManagementResponseVO#getParentName()}
     *   <li>{@link PasswordManagementResponseVO#getPasswordStatus()}
     *   <li>{@link PasswordManagementResponseVO#getRemarks()}
     *   <li>{@link PasswordManagementResponseVO#getShortName()}
     *   <li>{@link PasswordManagementResponseVO#getSsn()}
     *   <li>{@link PasswordManagementResponseVO#getState()}
     *   <li>{@link PasswordManagementResponseVO#getUserGrade()}
     *   <li>{@link PasswordManagementResponseVO#getUserID()}
     *   <li>{@link PasswordManagementResponseVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        PasswordManagementResponseVO actualPasswordManagementResponseVO = new PasswordManagementResponseVO();
        actualPasswordManagementResponseVO.setAddress("42 Main St");
        actualPasswordManagementResponseVO.setCategory("Category");
        actualPasswordManagementResponseVO.setCity("Oxford");
        actualPasswordManagementResponseVO.setContactPerson("Contact Person");
        actualPasswordManagementResponseVO.setCountry("GB");
        actualPasswordManagementResponseVO.setDesignation("Designation");
        actualPasswordManagementResponseVO.setInvalidPasswordCount(1);
        actualPasswordManagementResponseVO.setLoginID("Login ID");
        actualPasswordManagementResponseVO.setMobileNumber("42");
        actualPasswordManagementResponseVO.setNetworkCode("Network Code");
        actualPasswordManagementResponseVO.setNetworkName("Network Name");
        actualPasswordManagementResponseVO.setOwnerID("Owner ID");
        actualPasswordManagementResponseVO.setOwnerName("Owner Name");
        actualPasswordManagementResponseVO.setParentID("Parent ID");
        actualPasswordManagementResponseVO.setParentName("Parent Name");
        actualPasswordManagementResponseVO.setPasswordStatus("Password Status");
        actualPasswordManagementResponseVO.setRemarks("Remarks");
        actualPasswordManagementResponseVO.setShortName("Short Name");
        actualPasswordManagementResponseVO.setSsn("123-45-678");
        actualPasswordManagementResponseVO.setState("MD");
        actualPasswordManagementResponseVO.setUserGrade("User Grade");
        actualPasswordManagementResponseVO.setUserID("User ID");
        actualPasswordManagementResponseVO.setUserName("janedoe");
        assertEquals("42 Main St", actualPasswordManagementResponseVO.getAddress());
        assertEquals("Category", actualPasswordManagementResponseVO.getCategory());
        assertEquals("Oxford", actualPasswordManagementResponseVO.getCity());
        assertEquals("Contact Person", actualPasswordManagementResponseVO.getContactPerson());
        assertEquals("GB", actualPasswordManagementResponseVO.getCountry());
        assertEquals("Designation", actualPasswordManagementResponseVO.getDesignation());
        assertEquals(1, actualPasswordManagementResponseVO.getInvalidPasswordCount().intValue());
        assertEquals("Login ID", actualPasswordManagementResponseVO.getLoginID());
        assertEquals("42", actualPasswordManagementResponseVO.getMobileNumber());
        assertEquals("Network Code", actualPasswordManagementResponseVO.getNetworkCode());
        assertEquals("Network Name", actualPasswordManagementResponseVO.getNetworkName());
        assertEquals("Owner ID", actualPasswordManagementResponseVO.getOwnerID());
        assertEquals("Owner Name", actualPasswordManagementResponseVO.getOwnerName());
        assertEquals("Parent ID", actualPasswordManagementResponseVO.getParentID());
        assertEquals("Parent Name", actualPasswordManagementResponseVO.getParentName());
        assertEquals("Password Status", actualPasswordManagementResponseVO.getPasswordStatus());
        assertEquals("Remarks", actualPasswordManagementResponseVO.getRemarks());
        assertEquals("Short Name", actualPasswordManagementResponseVO.getShortName());
        assertEquals("123-45-678", actualPasswordManagementResponseVO.getSsn());
        assertEquals("MD", actualPasswordManagementResponseVO.getState());
        assertEquals("User Grade", actualPasswordManagementResponseVO.getUserGrade());
        assertEquals("User ID", actualPasswordManagementResponseVO.getUserID());
        assertEquals("janedoe", actualPasswordManagementResponseVO.getUserName());
    }
}

