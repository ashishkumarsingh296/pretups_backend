package com.restapi.networkadmin.cardgroup.requestVO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class C2SAddCardGroupSaveRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link C2SAddCardGroupSaveRequestVO}
     *   <li>{@link C2SAddCardGroupSaveRequestVO#toString()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        assertEquals(
                "C2SAddCardGroupSaveRequestVO [cardGroupSetID=null, cardGroupSetName=null, createdOn=null, createdBy=null,"
                        + " version=null, modifiedOn=null, modifiedBy=null, moduleCode=null, status=null, subServiceType=null,"
                        + " serviceTypeID=null, setType=null, defaultCardGroup=null, applicableFromHour=null, applicableFromDate=null,"
                        + " origCardGroupSetNameList=null, cardGroupSetNameList=null, cardGroupSetVersionList=null, selectCardGroupSetId"
                        + "=null, cardGroupList=null, cardGroupSubServiceID=null]",
                (new C2SAddCardGroupSaveRequestVO()).toString());
    }
}

