package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoadC2SCardGroupListResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoadC2SCardGroupListResponseVO}
     *   <li>{@link LoadC2SCardGroupListResponseVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        assertEquals(
                "LoadC2SCardGroupListResponseVO [reversalModifiedDateAsString=null, reversalModifiedDate=null,"
                        + " amountTypeList=null, validityTypeList=null, bonusBundleList=null, locationIndex=null, setStatus=null,"
                        + " tempAccList=null, cardGroupSubServiceName=null, serviceTypedesc=null, setTypeName=null]",
                (new LoadC2SCardGroupListResponseVO()).toString());
    }
}

