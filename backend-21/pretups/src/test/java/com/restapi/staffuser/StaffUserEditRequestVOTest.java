package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StaffUserEditRequestVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link StaffUserEditRequestVO}
     *   <li>{@link StaffUserEditRequestVO#setStaffUserEditDetailsdata(StaffUserEditDetails)}
     *   <li>{@link StaffUserEditRequestVO#toString()}
     *   <li>{@link StaffUserEditRequestVO#getStaffUserEditDetailsdata()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        StaffUserEditRequestVO actualStaffUserEditRequestVO = new StaffUserEditRequestVO();
        StaffUserEditDetails StaffUserEditDetailsdata = new StaffUserEditDetails();
        actualStaffUserEditRequestVO.setStaffUserEditDetailsdata(StaffUserEditDetailsdata);
        String actualToStringResult = actualStaffUserEditRequestVO.toString();
        assertSame(StaffUserEditDetailsdata, actualStaffUserEditRequestVO.getStaffUserEditDetailsdata());
        assertEquals(
                "StaffUserRequestVO [StaffUserEditDetailsdata=StaffUserEditDetails [language=null, allowedip=null,"
                        + " designation=null, firstName=null, lastName=null, userName=null, shortName=null, userNamePrefix=null,"
                        + " subscriberCode=null, contactNumber=null, address1=null, address2=null, city=null, state=null,"
                        + " country=null, emailid=null, oldWebloginid=null, webloginid=null, msisdn=null, appointmentdate=null,"
                        + " alloweddays=null, services=null, roles=null, allowedTimeFrom=null, allowedTimeTo=null]]",
                actualToStringResult);
    }
}

