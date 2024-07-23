package com.restapi.channeluser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GradeListTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link GradeList}
     *   <li>{@link GradeList#setCommisionProfileList(List)}
     *   <li>{@link GradeList#setGradeName(String)}
     *   <li>{@link GradeList#setGradecode(String)}
     *   <li>{@link GradeList#getCommisionProfileList()}
     *   <li>{@link GradeList#getGradecode()}
     *   <li>{@link GradeList#getGradename()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        GradeList actualGradeList = new GradeList();
        ArrayList<CommisionProfileList> commisionProfileList = new ArrayList<>();
        actualGradeList.setCommisionProfileList(commisionProfileList);
        actualGradeList.setGradeName("Gradename");
        actualGradeList.setGradecode("Gradecode");
        assertSame(commisionProfileList, actualGradeList.getCommisionProfileList());
        assertEquals("Gradecode", actualGradeList.getGradecode());
        assertEquals("Gradename", actualGradeList.getGradename());
    }
}

