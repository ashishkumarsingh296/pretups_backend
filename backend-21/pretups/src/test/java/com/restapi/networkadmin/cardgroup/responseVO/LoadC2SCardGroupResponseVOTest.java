package com.restapi.networkadmin.cardgroup.responseVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LoadC2SCardGroupResponseVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link LoadC2SCardGroupResponseVO}
     *   <li>{@link LoadC2SCardGroupResponseVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        assertEquals(
                "LoadC2SCardGroupResponseVO [viewSlabCopy=null, viewCopy=null, cardGroupSubServiceList=null,"
                        + " cardGroupSubServiceID=null, cardGroupSubServiceName=null, serviceTypeList=null, serviceTypeId=null,"
                        + " serviceTypedesc=null, setTypeList=null, setType=null, setTypeName=null]",
                (new LoadC2SCardGroupResponseVO()).toString());
    }
}

