package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChangePhoneLanguageTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link ChangePhoneLanguage}
     *   <li>{@link ChangePhoneLanguage#setCountry(String)}
     *   <li>{@link ChangePhoneLanguage#setLanguageCode(String)}
     *   <li>{@link ChangePhoneLanguage#setUserMsisdn(String)}
     *   <li>{@link ChangePhoneLanguage#toString()}
     *   <li>{@link ChangePhoneLanguage#getCountry()}
     *   <li>{@link ChangePhoneLanguage#getLanguageCode()}
     *   <li>{@link ChangePhoneLanguage#getUserMsisdn()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        ChangePhoneLanguage actualChangePhoneLanguage = new ChangePhoneLanguage();
        actualChangePhoneLanguage.setCountry("GB");
        actualChangePhoneLanguage.setLanguageCode("en");
        actualChangePhoneLanguage.setUserMsisdn("User Msisdn");
        String actualToStringResult = actualChangePhoneLanguage.toString();
        assertEquals("GB", actualChangePhoneLanguage.getCountry());
        assertEquals("en", actualChangePhoneLanguage.getLanguageCode());
        assertEquals("User Msisdn", actualChangePhoneLanguage.getUserMsisdn());
        assertEquals("ChangePhoneLanguage [userMsisdn=User Msisdn, languageCode=en, country=GB]", actualToStringResult);
    }
}

