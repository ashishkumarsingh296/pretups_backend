package com.restapi.channelAdmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BarredusersrequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BarredusersrequestVO}
     *   <li>{@link BarredusersrequestVO#setApprovalLevel(String)}
     *   <li>{@link BarredusersrequestVO#setCategory(String)}
     *   <li>{@link BarredusersrequestVO#setDomain(String)}
     *   <li>{@link BarredusersrequestVO#setExternalcode(String)}
     *   <li>{@link BarredusersrequestVO#setGeography(String)}
     *   <li>{@link BarredusersrequestVO#setLoggedInUserUserid(String)}
     *   <li>{@link BarredusersrequestVO#setLoggedUserNeworkCode(String)}
     *   <li>{@link BarredusersrequestVO#setLoginID(String)}
     *   <li>{@link BarredusersrequestVO#setMobileNumber(String)}
     *   <li>{@link BarredusersrequestVO#setSearchType(String)}
     *   <li>{@link BarredusersrequestVO#setUserStatus(String)}
     *   <li>{@link BarredusersrequestVO#getApprovalLevel()}
     *   <li>{@link BarredusersrequestVO#getCategory()}
     *   <li>{@link BarredusersrequestVO#getDomain()}
     *   <li>{@link BarredusersrequestVO#getExternalcode()}
     *   <li>{@link BarredusersrequestVO#getGeography()}
     *   <li>{@link BarredusersrequestVO#getLoggedInUserUserid()}
     *   <li>{@link BarredusersrequestVO#getLoggedUserNeworkCode()}
     *   <li>{@link BarredusersrequestVO#getLoginID()}
     *   <li>{@link BarredusersrequestVO#getMobileNumber()}
     *   <li>{@link BarredusersrequestVO#getSearchType()}
     *   <li>{@link BarredusersrequestVO#getUserStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BarredusersrequestVO actualBarredusersrequestVO = new BarredusersrequestVO();
        actualBarredusersrequestVO.setApprovalLevel("Approval Level");
        actualBarredusersrequestVO.setCategory("Category");
        actualBarredusersrequestVO.setDomain("Domain");
        actualBarredusersrequestVO.setExternalcode("Externalcode");
        actualBarredusersrequestVO.setGeography("Geography");
        actualBarredusersrequestVO.setLoggedInUserUserid("Logged In User Userid");
        actualBarredusersrequestVO.setLoggedUserNeworkCode("Logged User Nework Code");
        actualBarredusersrequestVO.setLoginID("Login ID");
        actualBarredusersrequestVO.setMobileNumber("42");
        actualBarredusersrequestVO.setSearchType("Search Type");
        actualBarredusersrequestVO.setUserStatus("User Status");
        assertEquals("Approval Level", actualBarredusersrequestVO.getApprovalLevel());
        assertEquals("Category", actualBarredusersrequestVO.getCategory());
        assertEquals("Domain", actualBarredusersrequestVO.getDomain());
        assertEquals("Externalcode", actualBarredusersrequestVO.getExternalcode());
        assertEquals("Geography", actualBarredusersrequestVO.getGeography());
        assertEquals("Logged In User Userid", actualBarredusersrequestVO.getLoggedInUserUserid());
        assertEquals("Logged User Nework Code", actualBarredusersrequestVO.getLoggedUserNeworkCode());
        assertEquals("Login ID", actualBarredusersrequestVO.getLoginID());
        assertEquals("42", actualBarredusersrequestVO.getMobileNumber());
        assertEquals("Search Type", actualBarredusersrequestVO.getSearchType());
        assertEquals("User Status", actualBarredusersrequestVO.getUserStatus());
    }
}

