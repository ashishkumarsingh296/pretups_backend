package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class NotificationLanguageResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link NotificationLanguageResponseVO}
     *   <li>{@link NotificationLanguageResponseVO#setLanguageList(ArrayList)}
     *   <li>{@link NotificationLanguageResponseVO#setLoginID(String)}
     *   <li>{@link NotificationLanguageResponseVO#setMsisdn(String)}
     *   <li>{@link NotificationLanguageResponseVO#setUserID(String)}
     *   <li>{@link NotificationLanguageResponseVO#setUserList(ArrayList)}
     *   <li>{@link NotificationLanguageResponseVO#setUserListSize(int)}
     *   <li>{@link NotificationLanguageResponseVO#setUserName(String)}
     *   <li>{@link NotificationLanguageResponseVO#toString()}
     *   <li>{@link NotificationLanguageResponseVO#getLanguageList()}
     *   <li>{@link NotificationLanguageResponseVO#getLoginID()}
     *   <li>{@link NotificationLanguageResponseVO#getMsisdn()}
     *   <li>{@link NotificationLanguageResponseVO#getUserID()}
     *   <li>{@link NotificationLanguageResponseVO#getUserList()}
     *   <li>{@link NotificationLanguageResponseVO#getUserListSize()}
     *   <li>{@link NotificationLanguageResponseVO#getUserName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        NotificationLanguageResponseVO actualNotificationLanguageResponseVO = new NotificationLanguageResponseVO();
        ArrayList languageList = new ArrayList();
        actualNotificationLanguageResponseVO.setLanguageList(languageList);
        actualNotificationLanguageResponseVO.setLoginID("Login ID");
        actualNotificationLanguageResponseVO.setMsisdn("Msisdn");
        actualNotificationLanguageResponseVO.setUserID("User ID");
        ArrayList userList = new ArrayList();
        actualNotificationLanguageResponseVO.setUserList(userList);
        actualNotificationLanguageResponseVO.setUserListSize(3);
        actualNotificationLanguageResponseVO.setUserName("janedoe");
        String actualToStringResult = actualNotificationLanguageResponseVO.toString();
        assertSame(languageList, actualNotificationLanguageResponseVO.getLanguageList());
        assertEquals("Login ID", actualNotificationLanguageResponseVO.getLoginID());
        assertEquals("Msisdn", actualNotificationLanguageResponseVO.getMsisdn());
        assertEquals("User ID", actualNotificationLanguageResponseVO.getUserID());
        assertSame(userList, actualNotificationLanguageResponseVO.getUserList());
        assertEquals(3, actualNotificationLanguageResponseVO.getUserListSize());
        assertEquals("janedoe", actualNotificationLanguageResponseVO.getUserName());
        assertEquals("NotificationLanguageResponseVO [msisdn=Msisdn, loginID=Login ID, userName=janedoe, userID=User ID,"
                + " userList=[], userListSize=3, languageList=[]]", actualToStringResult);
    }
}

