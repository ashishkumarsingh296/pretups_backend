package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddTempCardGroupListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link AddTempCardGroupListResponseVO}
     *   <li>{@link AddTempCardGroupListResponseVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        assertEquals("AddTempCardGroupListResponseVO AddTempCardGroupListResponseVO [tempCardGroupList=null]",
                (new AddTempCardGroupListResponseVO()).toString());
    }
}

