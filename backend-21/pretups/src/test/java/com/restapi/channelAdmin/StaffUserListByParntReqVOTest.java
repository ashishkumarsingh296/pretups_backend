package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StaffUserListByParntReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link StaffUserListByParntReqVO}
     *   <li>{@link StaffUserListByParntReqVO#setDomain(String)}
     *   <li>{@link StaffUserListByParntReqVO#setGeography(String)}
     *   <li>{@link StaffUserListByParntReqVO#setLoginID(String)}
     *   <li>{@link StaffUserListByParntReqVO#setMsisdn(String)}
     *   <li>{@link StaffUserListByParntReqVO#setOwnerUserID(String)}
     *   <li>{@link StaffUserListByParntReqVO#setParentUserID(String)}
     *   <li>{@link StaffUserListByParntReqVO#setStatus(String)}
     *   <li>{@link StaffUserListByParntReqVO#setUserCategory(String)}
     *   <li>{@link StaffUserListByParntReqVO#setUserName(String)}
     *   <li>{@link StaffUserListByParntReqVO#toString()}
     *   <li>{@link StaffUserListByParntReqVO#getDomain()}
     *   <li>{@link StaffUserListByParntReqVO#getGeography()}
     *   <li>{@link StaffUserListByParntReqVO#getLoginID()}
     *   <li>{@link StaffUserListByParntReqVO#getMsisdn()}
     *   <li>{@link StaffUserListByParntReqVO#getOwnerUserID()}
     *   <li>{@link StaffUserListByParntReqVO#getParentUserID()}
     *   <li>{@link StaffUserListByParntReqVO#getStatus()}
     *   <li>{@link StaffUserListByParntReqVO#getUserCategory()}
     *   <li>{@link StaffUserListByParntReqVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        StaffUserListByParntReqVO actualStaffUserListByParntReqVO = new StaffUserListByParntReqVO();
        actualStaffUserListByParntReqVO.setDomain("Domain");
        actualStaffUserListByParntReqVO.setGeography("Geography");
        actualStaffUserListByParntReqVO.setLoginID("Login ID");
        actualStaffUserListByParntReqVO.setMsisdn("Msisdn");
        actualStaffUserListByParntReqVO.setOwnerUserID("Owner User ID");
        actualStaffUserListByParntReqVO.setParentUserID("Parent User ID");
        actualStaffUserListByParntReqVO.setStatus("Status");
        actualStaffUserListByParntReqVO.setUserCategory("User Category");
        actualStaffUserListByParntReqVO.setUserName("janedoe");
        String actualToStringResult = actualStaffUserListByParntReqVO.toString();
        assertEquals("Domain", actualStaffUserListByParntReqVO.getDomain());
        assertEquals("Geography", actualStaffUserListByParntReqVO.getGeography());
        assertEquals("Login ID", actualStaffUserListByParntReqVO.getLoginID());
        assertEquals("Msisdn", actualStaffUserListByParntReqVO.getMsisdn());
        assertEquals("Owner User ID", actualStaffUserListByParntReqVO.getOwnerUserID());
        assertEquals("Parent User ID", actualStaffUserListByParntReqVO.getParentUserID());
        assertEquals("Status", actualStaffUserListByParntReqVO.getStatus());
        assertEquals("User Category", actualStaffUserListByParntReqVO.getUserCategory());
        assertEquals("janedoe", actualStaffUserListByParntReqVO.getUserName());
        assertEquals("ChannelUserListRequestVO [, domain=Domain, userCategory=User Category, parentUserID=Parent User ID,"
                + " geography=Geography, status=]", actualToStringResult);
    }
}

