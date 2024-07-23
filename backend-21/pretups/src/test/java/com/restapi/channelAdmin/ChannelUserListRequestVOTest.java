package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelUserListRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserListRequestVO}
     *   <li>{@link ChannelUserListRequestVO#setDomain(String)}
     *   <li>{@link ChannelUserListRequestVO#setGeography(String)}
     *   <li>{@link ChannelUserListRequestVO#setLoginID(String)}
     *   <li>{@link ChannelUserListRequestVO#setMsisdn(String)}
     *   <li>{@link ChannelUserListRequestVO#setParentUserID(String)}
     *   <li>{@link ChannelUserListRequestVO#setStatus(String)}
     *   <li>{@link ChannelUserListRequestVO#setUserCategory(String)}
     *   <li>{@link ChannelUserListRequestVO#toString()}
     *   <li>{@link ChannelUserListRequestVO#getDomain()}
     *   <li>{@link ChannelUserListRequestVO#getGeography()}
     *   <li>{@link ChannelUserListRequestVO#getLoginID()}
     *   <li>{@link ChannelUserListRequestVO#getMsisdn()}
     *   <li>{@link ChannelUserListRequestVO#getParentUserID()}
     *   <li>{@link ChannelUserListRequestVO#getStatus()}
     *   <li>{@link ChannelUserListRequestVO#getUserCategory()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserListRequestVO actualChannelUserListRequestVO = new ChannelUserListRequestVO();
        actualChannelUserListRequestVO.setDomain("Domain");
        actualChannelUserListRequestVO.setGeography("Geography");
        actualChannelUserListRequestVO.setLoginID("Login ID");
        actualChannelUserListRequestVO.setMsisdn("Msisdn");
        actualChannelUserListRequestVO.setParentUserID("Parent User ID");
        actualChannelUserListRequestVO.setStatus("Status");
        actualChannelUserListRequestVO.setUserCategory("User Category");
        String actualToStringResult = actualChannelUserListRequestVO.toString();
        assertEquals("Domain", actualChannelUserListRequestVO.getDomain());
        assertEquals("Geography", actualChannelUserListRequestVO.getGeography());
        assertEquals("Login ID", actualChannelUserListRequestVO.getLoginID());
        assertEquals("Msisdn", actualChannelUserListRequestVO.getMsisdn());
        assertEquals("Parent User ID", actualChannelUserListRequestVO.getParentUserID());
        assertEquals("Status", actualChannelUserListRequestVO.getStatus());
        assertEquals("User Category", actualChannelUserListRequestVO.getUserCategory());
        assertEquals("ChannelUserListRequestVO [msisdn=Msisdn, loginID=Login ID, domain=Domain, userCategory=User Category,"
                + " parentUserID=Parent User ID, geography=Geography, status=Status]", actualToStringResult);
    }
}

