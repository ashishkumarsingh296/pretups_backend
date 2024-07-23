package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LanguageVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LanguageVO}
     *   <li>{@link LanguageVO#setLanguageCode(String)}
     *   <li>{@link LanguageVO#setLanguageName(String)}
     *   <li>{@link LanguageVO#toString()}
     *   <li>{@link LanguageVO#getLanguageCode()}
     *   <li>{@link LanguageVO#getLanguageName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        LanguageVO actualLanguageVO = new LanguageVO();
        actualLanguageVO.setLanguageCode("en");
        actualLanguageVO.setLanguageName("en");
        actualLanguageVO.toString();
        assertEquals("en", actualLanguageVO.getLanguageCode());
        assertEquals("en", actualLanguageVO.getLanguageName());
    }
}

