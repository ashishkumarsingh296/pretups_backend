package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UpdateServiceClassPreferenceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateServiceClassPreferenceVO}
     *   <li>{@link UpdateServiceClassPreferenceVO#setAllowAction(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setLastModifiedTime(Long)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setModuleCode(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setNetworkCode(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setPreferenceCode(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setPreferenceValue(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setPreferenceValueType(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#setServiceCode(String)}
     *   <li>{@link UpdateServiceClassPreferenceVO#getAllowAction()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getLastModifiedTime()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getModuleCode()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getNetworkCode()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getPreferenceCode()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getPreferenceValue()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getPreferenceValueType()}
     *   <li>{@link UpdateServiceClassPreferenceVO#getServiceCode()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateServiceClassPreferenceVO actualUpdateServiceClassPreferenceVO = new UpdateServiceClassPreferenceVO();
        actualUpdateServiceClassPreferenceVO.setAllowAction("Allow Action");
        actualUpdateServiceClassPreferenceVO.setLastModifiedTime(1L);
        actualUpdateServiceClassPreferenceVO.setModuleCode("Module Code");
        actualUpdateServiceClassPreferenceVO.setNetworkCode("Network Code");
        actualUpdateServiceClassPreferenceVO.setPreferenceCode("Preference Code");
        actualUpdateServiceClassPreferenceVO.setPreferenceValue("42");
        actualUpdateServiceClassPreferenceVO.setPreferenceValueType("42");
        actualUpdateServiceClassPreferenceVO.setServiceCode("Service Code");
        assertEquals("Allow Action", actualUpdateServiceClassPreferenceVO.getAllowAction());
        assertEquals(1L, actualUpdateServiceClassPreferenceVO.getLastModifiedTime().longValue());
        assertEquals("Module Code", actualUpdateServiceClassPreferenceVO.getModuleCode());
        assertEquals("Network Code", actualUpdateServiceClassPreferenceVO.getNetworkCode());
        assertEquals("Preference Code", actualUpdateServiceClassPreferenceVO.getPreferenceCode());
        assertEquals("42", actualUpdateServiceClassPreferenceVO.getPreferenceValue());
        assertEquals("42", actualUpdateServiceClassPreferenceVO.getPreferenceValueType());
        assertEquals("Service Code", actualUpdateServiceClassPreferenceVO.getServiceCode());
    }
}

