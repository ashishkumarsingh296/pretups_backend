package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StaffUserEditDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link StaffUserEditDetails}
     *   <li>{@link StaffUserEditDetails#setAddress1(String)}
     *   <li>{@link StaffUserEditDetails#setAddress2(String)}
     *   <li>{@link StaffUserEditDetails#setAllowedTimeFrom(String)}
     *   <li>{@link StaffUserEditDetails#setAllowedTimeTo(String)}
     *   <li>{@link StaffUserEditDetails#setAlloweddays(String)}
     *   <li>{@link StaffUserEditDetails#setAllowedip(String)}
     *   <li>{@link StaffUserEditDetails#setAppointmentdate(String)}
     *   <li>{@link StaffUserEditDetails#setCity(String)}
     *   <li>{@link StaffUserEditDetails#setContactNumber(String)}
     *   <li>{@link StaffUserEditDetails#setCountry(String)}
     *   <li>{@link StaffUserEditDetails#setDesignation(String)}
     *   <li>{@link StaffUserEditDetails#setEmailid(String)}
     *   <li>{@link StaffUserEditDetails#setFirstName(String)}
     *   <li>{@link StaffUserEditDetails#setLanguage(String)}
     *   <li>{@link StaffUserEditDetails#setLastName(String)}
     *   <li>{@link StaffUserEditDetails#setMsisdn(EditMsisdn[])}
     *   <li>{@link StaffUserEditDetails#setOldWebloginid(String)}
     *   <li>{@link StaffUserEditDetails#setRoles(String)}
     *   <li>{@link StaffUserEditDetails#setServices(String)}
     *   <li>{@link StaffUserEditDetails#setShortName(String)}
     *   <li>{@link StaffUserEditDetails#setState(String)}
     *   <li>{@link StaffUserEditDetails#setSubscriberCode(String)}
     *   <li>{@link StaffUserEditDetails#setUserName(String)}
     *   <li>{@link StaffUserEditDetails#setUserNamePrefix(String)}
     *   <li>{@link StaffUserEditDetails#setWebloginid(String)}
     *   <li>{@link StaffUserEditDetails#toString()}
     *   <li>{@link StaffUserEditDetails#getAddress1()}
     *   <li>{@link StaffUserEditDetails#getAddress2()}
     *   <li>{@link StaffUserEditDetails#getAllowedTimeFrom()}
     *   <li>{@link StaffUserEditDetails#getAllowedTimeTo()}
     *   <li>{@link StaffUserEditDetails#getAlloweddays()}
     *   <li>{@link StaffUserEditDetails#getAppointmentdate()}
     *   <li>{@link StaffUserEditDetails#getCity()}
     *   <li>{@link StaffUserEditDetails#getContactNumber()}
     *   <li>{@link StaffUserEditDetails#getCountry()}
     *   <li>{@link StaffUserEditDetails#getDesignation()}
     *   <li>{@link StaffUserEditDetails#getEmailid()}
     *   <li>{@link StaffUserEditDetails#getFirstName()}
     *   <li>{@link StaffUserEditDetails#getLanguage()}
     *   <li>{@link StaffUserEditDetails#getLastName()}
     *   <li>{@link StaffUserEditDetails#getMsisdn()}
     *   <li>{@link StaffUserEditDetails#getOldWebloginid()}
     *   <li>{@link StaffUserEditDetails#getRoles()}
     *   <li>{@link StaffUserEditDetails#getServices()}
     *   <li>{@link StaffUserEditDetails#getShortName()}
     *   <li>{@link StaffUserEditDetails#getState()}
     *   <li>{@link StaffUserEditDetails#getSubscriberCode()}
     *   <li>{@link StaffUserEditDetails#getUserName()}
     *   <li>{@link StaffUserEditDetails#getUserNamePrefix()}
     *   <li>{@link StaffUserEditDetails#getWebloginid()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        StaffUserEditDetails actualStaffUserEditDetails = new StaffUserEditDetails();
        actualStaffUserEditDetails.setAddress1("42 Main St");
        actualStaffUserEditDetails.setAddress2("42 Main St");
        actualStaffUserEditDetails.setAllowedTimeFrom("jane.doe@example.org");
        actualStaffUserEditDetails.setAllowedTimeTo("alice.liddell@example.org");
        actualStaffUserEditDetails.setAlloweddays("Alloweddays");
        actualStaffUserEditDetails.setAllowedip("127.0.0.1");
        actualStaffUserEditDetails.setAppointmentdate("2020-03-01");
        actualStaffUserEditDetails.setCity("Oxford");
        actualStaffUserEditDetails.setContactNumber("42");
        actualStaffUserEditDetails.setCountry("GB");
        actualStaffUserEditDetails.setDesignation("Designation");
        actualStaffUserEditDetails.setEmailid("jane.doe@example.org");
        actualStaffUserEditDetails.setFirstName("Jane");
        actualStaffUserEditDetails.setLanguage("en");
        actualStaffUserEditDetails.setLastName("Doe");
        EditMsisdn[] msisdn = new EditMsisdn[]{new EditMsisdn()};
        actualStaffUserEditDetails.setMsisdn(msisdn);
        actualStaffUserEditDetails.setOldWebloginid("Old Webloginid");
        actualStaffUserEditDetails.setRoles("Roles");
        actualStaffUserEditDetails.setServices("Services");
        actualStaffUserEditDetails.setShortName("Short Name");
        actualStaffUserEditDetails.setState("MD");
        actualStaffUserEditDetails.setSubscriberCode("Subscriber Code");
        actualStaffUserEditDetails.setUserName("janedoe");
        actualStaffUserEditDetails.setUserNamePrefix("janedoe");
        actualStaffUserEditDetails.setWebloginid("Webloginid");
        actualStaffUserEditDetails.toString();
        assertEquals("42 Main St", actualStaffUserEditDetails.getAddress1());
        assertEquals("42 Main St", actualStaffUserEditDetails.getAddress2());
        assertEquals("jane.doe@example.org", actualStaffUserEditDetails.getAllowedTimeFrom());
        assertEquals("alice.liddell@example.org", actualStaffUserEditDetails.getAllowedTimeTo());
        assertEquals("Alloweddays", actualStaffUserEditDetails.getAlloweddays());
        assertEquals("2020-03-01", actualStaffUserEditDetails.getAppointmentdate());
        assertEquals("Oxford", actualStaffUserEditDetails.getCity());
        assertEquals("42", actualStaffUserEditDetails.getContactNumber());
        assertEquals("GB", actualStaffUserEditDetails.getCountry());
        assertEquals("Designation", actualStaffUserEditDetails.getDesignation());
        assertEquals("jane.doe@example.org", actualStaffUserEditDetails.getEmailid());
        assertEquals("Jane", actualStaffUserEditDetails.getFirstName());
        assertEquals("en", actualStaffUserEditDetails.getLanguage());
        assertEquals("Doe", actualStaffUserEditDetails.getLastName());
        assertSame(msisdn, actualStaffUserEditDetails.getMsisdn());
        assertEquals("Old Webloginid", actualStaffUserEditDetails.getOldWebloginid());
        assertEquals("Roles", actualStaffUserEditDetails.getRoles());
        assertEquals("Services", actualStaffUserEditDetails.getServices());
        assertEquals("Short Name", actualStaffUserEditDetails.getShortName());
        assertEquals("MD", actualStaffUserEditDetails.getState());
        assertEquals("Subscriber Code", actualStaffUserEditDetails.getSubscriberCode());
        assertEquals("janedoe", actualStaffUserEditDetails.getUserName());
        assertEquals("janedoe", actualStaffUserEditDetails.getUserNamePrefix());
        assertEquals("Webloginid", actualStaffUserEditDetails.getWebloginid());
    }
}

