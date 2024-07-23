package com.restapi.networkadmin.commissionprofile.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SusResCommProfileSetRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link SusResCommProfileSetRequestVO}
     *   <li>{@link SusResCommProfileSetRequestVO#setCommProfileSetId(String)}
     *   <li>{@link SusResCommProfileSetRequestVO#setDefaultProfile(String)}
     *   <li>{@link SusResCommProfileSetRequestVO#setLanguage1Message(String)}
     *   <li>{@link SusResCommProfileSetRequestVO#setLanguage2Message(String)}
     *   <li>{@link SusResCommProfileSetRequestVO#setStatus(String)}
     *   <li>{@link SusResCommProfileSetRequestVO#toString()}
     *   <li>{@link SusResCommProfileSetRequestVO#getCommProfileSetId()}
     *   <li>{@link SusResCommProfileSetRequestVO#getDefaultProfile()}
     *   <li>{@link SusResCommProfileSetRequestVO#getLanguage1Message()}
     *   <li>{@link SusResCommProfileSetRequestVO#getLanguage2Message()}
     *   <li>{@link SusResCommProfileSetRequestVO#getStatus()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        SusResCommProfileSetRequestVO actualSusResCommProfileSetRequestVO = new SusResCommProfileSetRequestVO();
        actualSusResCommProfileSetRequestVO.setCommProfileSetId("42");
        actualSusResCommProfileSetRequestVO.setDefaultProfile("Default Profile");
        actualSusResCommProfileSetRequestVO.setLanguage1Message("en");
        actualSusResCommProfileSetRequestVO.setLanguage2Message("en");
        actualSusResCommProfileSetRequestVO.setStatus("Status");
        String actualToStringResult = actualSusResCommProfileSetRequestVO.toString();
        assertEquals("42", actualSusResCommProfileSetRequestVO.getCommProfileSetId());
        assertEquals("Default Profile", actualSusResCommProfileSetRequestVO.getDefaultProfile());
        assertEquals("en", actualSusResCommProfileSetRequestVO.getLanguage1Message());
        assertEquals("en", actualSusResCommProfileSetRequestVO.getLanguage2Message());
        assertEquals("Status", actualSusResCommProfileSetRequestVO.getStatus());
        assertEquals(
                "SusResCommProfileSetRequestVO(status=Status, commProfileSetId=42, language1Message=en, language2Message=en,"
                        + " defaultProfile=Default Profile)",
                actualToStringResult);
    }
}

