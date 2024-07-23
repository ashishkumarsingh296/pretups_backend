package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChannelUserListByParntReqVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChannelUserListByParntReqVO}
     *   <li>{@link ChannelUserListByParntReqVO#setDomain(String)}
     *   <li>{@link ChannelUserListByParntReqVO#setGeography(String)}
     *   <li>{@link ChannelUserListByParntReqVO#setOwnerUserID(String)}
     *   <li>{@link ChannelUserListByParntReqVO#setParentUserID(String)}
     *   <li>{@link ChannelUserListByParntReqVO#setUserCategory(String)}
     *   <li>{@link ChannelUserListByParntReqVO#setUserName(String)}
     *   <li>{@link ChannelUserListByParntReqVO#toString()}
     *   <li>{@link ChannelUserListByParntReqVO#getDomain()}
     *   <li>{@link ChannelUserListByParntReqVO#getGeography()}
     *   <li>{@link ChannelUserListByParntReqVO#getOwnerUserID()}
     *   <li>{@link ChannelUserListByParntReqVO#getParentUserID()}
     *   <li>{@link ChannelUserListByParntReqVO#getUserCategory()}
     *   <li>{@link ChannelUserListByParntReqVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChannelUserListByParntReqVO actualChannelUserListByParntReqVO = new ChannelUserListByParntReqVO();
        actualChannelUserListByParntReqVO.setDomain("Domain");
        actualChannelUserListByParntReqVO.setGeography("Geography");
        actualChannelUserListByParntReqVO.setOwnerUserID("Owner User ID");
        actualChannelUserListByParntReqVO.setParentUserID("Parent User ID");
        actualChannelUserListByParntReqVO.setUserCategory("User Category");
        actualChannelUserListByParntReqVO.setUserName("janedoe");
        String actualToStringResult = actualChannelUserListByParntReqVO.toString();
        assertEquals("Domain", actualChannelUserListByParntReqVO.getDomain());
        assertEquals("Geography", actualChannelUserListByParntReqVO.getGeography());
        assertEquals("Owner User ID", actualChannelUserListByParntReqVO.getOwnerUserID());
        assertEquals("Parent User ID", actualChannelUserListByParntReqVO.getParentUserID());
        assertEquals("User Category", actualChannelUserListByParntReqVO.getUserCategory());
        assertEquals("janedoe", actualChannelUserListByParntReqVO.getUserName());
        assertEquals("ChannelUserListRequestVO [, domain=Domain, userCategory=User Category, parentUserID=Parent User ID,"
                + " geography=Geography, status=]", actualToStringResult);
    }
}

