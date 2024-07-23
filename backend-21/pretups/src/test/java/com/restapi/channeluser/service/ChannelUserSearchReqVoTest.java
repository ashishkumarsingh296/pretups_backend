package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelUserSearchReqVoTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserSearchReqVo}
     *   <li>{@link ChannelUserSearchReqVo#setCategory(String)}
     *   <li>{@link ChannelUserSearchReqVo#setDomain(String)}
     *   <li>{@link ChannelUserSearchReqVo#setGeography(String)}
     *   <li>{@link ChannelUserSearchReqVo#setLoggedInUserUserid(String)}
     *   <li>{@link ChannelUserSearchReqVo#setLoggedUserNeworkCode(String)}
     *   <li>{@link ChannelUserSearchReqVo#setLoginID(String)}
     *   <li>{@link ChannelUserSearchReqVo#setMobileNumber(String)}
     *   <li>{@link ChannelUserSearchReqVo#setSearchType(String)}
     *   <li>{@link ChannelUserSearchReqVo#setUserStatus(String)}
     *   <li>{@link ChannelUserSearchReqVo#getCategory()}
     *   <li>{@link ChannelUserSearchReqVo#getDomain()}
     *   <li>{@link ChannelUserSearchReqVo#getGeography()}
     *   <li>{@link ChannelUserSearchReqVo#getLoggedInUserUserid()}
     *   <li>{@link ChannelUserSearchReqVo#getLoggedUserNeworkCode()}
     *   <li>{@link ChannelUserSearchReqVo#getLoginID()}
     *   <li>{@link ChannelUserSearchReqVo#getMobileNumber()}
     *   <li>{@link ChannelUserSearchReqVo#getSearchType()}
     *   <li>{@link ChannelUserSearchReqVo#getUserStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserSearchReqVo actualChannelUserSearchReqVo = new ChannelUserSearchReqVo();
        actualChannelUserSearchReqVo.setCategory("Category");
        actualChannelUserSearchReqVo.setDomain("Domain");
        actualChannelUserSearchReqVo.setGeography("Geography");
        actualChannelUserSearchReqVo.setLoggedInUserUserid("Logged In User Userid");
        actualChannelUserSearchReqVo.setLoggedUserNeworkCode("Logged User Nework Code");
        actualChannelUserSearchReqVo.setLoginID("Login ID");
        actualChannelUserSearchReqVo.setMobileNumber("42");
        actualChannelUserSearchReqVo.setSearchType("Search Type");
        actualChannelUserSearchReqVo.setUserStatus("User Status");
        assertEquals("Category", actualChannelUserSearchReqVo.getCategory());
        assertEquals("Domain", actualChannelUserSearchReqVo.getDomain());
        assertEquals("Geography", actualChannelUserSearchReqVo.getGeography());
        assertEquals("Logged In User Userid", actualChannelUserSearchReqVo.getLoggedInUserUserid());
        assertEquals("Logged User Nework Code", actualChannelUserSearchReqVo.getLoggedUserNeworkCode());
        assertEquals("Login ID", actualChannelUserSearchReqVo.getLoginID());
        assertEquals("42", actualChannelUserSearchReqVo.getMobileNumber());
        assertEquals("Search Type", actualChannelUserSearchReqVo.getSearchType());
        assertEquals("User Status", actualChannelUserSearchReqVo.getUserStatus());
    }
}

