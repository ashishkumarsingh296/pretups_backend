package com.restapi.networkadmin;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

public class C2STransferListAddResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2STransferListAddResponseVO}
     *   <li>{@link C2STransferListAddResponseVO#setErrorList(ArrayList)}
     *   <li>{@link C2STransferListAddResponseVO#getErrorList()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        C2STransferListAddResponseVO actualC2sTransferListAddResponseVO = new C2STransferListAddResponseVO();
        ArrayList errorList = new ArrayList();
        actualC2sTransferListAddResponseVO.setErrorList(errorList);
        assertSame(errorList, actualC2sTransferListAddResponseVO.getErrorList());
    }
}

