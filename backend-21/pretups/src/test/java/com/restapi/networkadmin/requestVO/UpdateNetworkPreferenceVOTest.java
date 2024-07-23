package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UpdateNetworkPreferenceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateNetworkPreferenceVO}
     *   <li>{@link UpdateNetworkPreferenceVO#setAllowAction(String)}
     *   <li>{@link UpdateNetworkPreferenceVO#setLastModifiedTime(Long)}
     *   <li>{@link UpdateNetworkPreferenceVO#setNetworkCode(String)}
     *   <li>{@link UpdateNetworkPreferenceVO#setPreferenceCode(String)}
     *   <li>{@link UpdateNetworkPreferenceVO#setPreferenceValue(String)}
     *   <li>{@link UpdateNetworkPreferenceVO#setPreferenceValueType(String)}
     *   <li>{@link UpdateNetworkPreferenceVO#getAllowAction()}
     *   <li>{@link UpdateNetworkPreferenceVO#getLastModifiedTime()}
     *   <li>{@link UpdateNetworkPreferenceVO#getNetworkCode()}
     *   <li>{@link UpdateNetworkPreferenceVO#getPreferenceCode()}
     *   <li>{@link UpdateNetworkPreferenceVO#getPreferenceValue()}
     *   <li>{@link UpdateNetworkPreferenceVO#getPreferenceValueType()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateNetworkPreferenceVO actualUpdateNetworkPreferenceVO = new UpdateNetworkPreferenceVO();
        actualUpdateNetworkPreferenceVO.setAllowAction("Allow Action");
        actualUpdateNetworkPreferenceVO.setLastModifiedTime(1L);
        actualUpdateNetworkPreferenceVO.setNetworkCode("Network Code");
        actualUpdateNetworkPreferenceVO.setPreferenceCode("Preference Code");
        actualUpdateNetworkPreferenceVO.setPreferenceValue("42");
        actualUpdateNetworkPreferenceVO.setPreferenceValueType("42");
        assertEquals("Allow Action", actualUpdateNetworkPreferenceVO.getAllowAction());
        assertEquals(1L, actualUpdateNetworkPreferenceVO.getLastModifiedTime().longValue());
        assertEquals("Network Code", actualUpdateNetworkPreferenceVO.getNetworkCode());
        assertEquals("Preference Code", actualUpdateNetworkPreferenceVO.getPreferenceCode());
        assertEquals("42", actualUpdateNetworkPreferenceVO.getPreferenceValue());
        assertEquals("42", actualUpdateNetworkPreferenceVO.getPreferenceValueType());
    }
}

