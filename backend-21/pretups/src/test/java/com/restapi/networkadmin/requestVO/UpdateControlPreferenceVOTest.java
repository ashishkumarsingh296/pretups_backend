package com.restapi.networkadmin.requestVO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.btsl.pretups.preference.businesslogic.ControlPreferenceVO;

import java.util.ArrayList;

import org.junit.Test;

public class UpdateControlPreferenceVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link UpdateControlPreferenceVO}
     *   <li>{@link UpdateControlPreferenceVO#setCtrlPreferenceList(ArrayList)}
     *   <li>{@link UpdateControlPreferenceVO#toString()}
     *   <li>{@link UpdateControlPreferenceVO#getCtrlPreferenceList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        UpdateControlPreferenceVO actualUpdateControlPreferenceVO = new UpdateControlPreferenceVO();
        ArrayList<ControlPreferenceVO> ctrlPreferenceList = new ArrayList<>();
        actualUpdateControlPreferenceVO.setCtrlPreferenceList(ctrlPreferenceList);
        String actualToStringResult = actualUpdateControlPreferenceVO.toString();
        assertSame(ctrlPreferenceList, actualUpdateControlPreferenceVO.getCtrlPreferenceList());
        assertEquals("UpdateControlPreferenceVO [ctrlPreferenceList=[]]", actualToStringResult);
    }
}

