package com.restapi.preferences.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UpdateSystemPreferenceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateSystemPreferenceVO}
     *   <li>{@link UpdateSystemPreferenceVO#setLastModifiedTime(Long)}
     *   <li>{@link UpdateSystemPreferenceVO#setPreferenceCode(String)}
     *   <li>{@link UpdateSystemPreferenceVO#setPreferenceValue(String)}
     *   <li>{@link UpdateSystemPreferenceVO#setPreferenceValueType(String)}
     *   <li>{@link UpdateSystemPreferenceVO#toString()}
     *   <li>{@link UpdateSystemPreferenceVO#getLastModifiedTime()}
     *   <li>{@link UpdateSystemPreferenceVO#getPreferenceCode()}
     *   <li>{@link UpdateSystemPreferenceVO#getPreferenceValue()}
     *   <li>{@link UpdateSystemPreferenceVO#getPreferenceValueType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateSystemPreferenceVO actualUpdateSystemPreferenceVO = new UpdateSystemPreferenceVO();
        actualUpdateSystemPreferenceVO.setLastModifiedTime(1L);
        actualUpdateSystemPreferenceVO.setPreferenceCode("Preference Code");
        actualUpdateSystemPreferenceVO.setPreferenceValue("42");
        actualUpdateSystemPreferenceVO.setPreferenceValueType("42");
        String actualToStringResult = actualUpdateSystemPreferenceVO.toString();
        assertEquals(1L, actualUpdateSystemPreferenceVO.getLastModifiedTime().longValue());
        assertEquals("Preference Code", actualUpdateSystemPreferenceVO.getPreferenceCode());
        assertEquals("42", actualUpdateSystemPreferenceVO.getPreferenceValue());
        assertEquals("42", actualUpdateSystemPreferenceVO.getPreferenceValueType());
        assertEquals("UpdateSystemPreferenceVO(preferenceCode=Preference Code, preferenceValue=42, preferenceValueType=42,"
                + " lastModifiedTime=1)", actualToStringResult);
    }
}

