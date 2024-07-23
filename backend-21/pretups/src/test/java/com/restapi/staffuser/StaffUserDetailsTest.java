package com.restapi.staffuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StaffUserDetailsTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link StaffUserDetails}
     *   <li>{@link StaffUserDetails#setAddress1(String)}
     *   <li>{@link StaffUserDetails#setAddress2(String)}
     *   <li>{@link StaffUserDetails#setAllowedTimeFrom(String)}
     *   <li>{@link StaffUserDetails#setAllowedTimeTo(String)}
     *   <li>{@link StaffUserDetails#setAlloweddays(String)}
     *   <li>{@link StaffUserDetails#setAllowedip(String)}
     *   <li>{@link StaffUserDetails#setAppointmentdate(String)}
     *   <li>{@link StaffUserDetails#setCity(String)}
     *   <li>{@link StaffUserDetails#setConfirmwebpassword(String)}
     *   <li>{@link StaffUserDetails#setContactNumber(String)}
     *   <li>{@link StaffUserDetails#setCountry(String)}
     *   <li>{@link StaffUserDetails#setDesignation(String)}
     *   <li>{@link StaffUserDetails#setEmailid(String)}
     *   <li>{@link StaffUserDetails#setFirstName(String)}
     *   <li>{@link StaffUserDetails#setLanguage(String)}
     *   <li>{@link StaffUserDetails#setLastName(String)}
     *   <li>{@link StaffUserDetails#setMsisdn(Msisdn[])}
     *   <li>{@link StaffUserDetails#setRoles(String)}
     *   <li>{@link StaffUserDetails#setServices(String)}
     *   <li>{@link StaffUserDetails#setShortName(String)}
     *   <li>{@link StaffUserDetails#setState(String)}
     *   <li>{@link StaffUserDetails#setSubscriberCode(String)}
     *   <li>{@link StaffUserDetails#setUserName(String)}
     *   <li>{@link StaffUserDetails#setUserNamePrefix(String)}
     *   <li>{@link StaffUserDetails#setWebloginid(String)}
     *   <li>{@link StaffUserDetails#setWebpassword(String)}
     *   <li>{@link StaffUserDetails#toString()}
     *   <li>{@link StaffUserDetails#getAddress1()}
     *   <li>{@link StaffUserDetails#getAddress2()}
     *   <li>{@link StaffUserDetails#getAllowedTimeFrom()}
     *   <li>{@link StaffUserDetails#getAllowedTimeTo()}
     *   <li>{@link StaffUserDetails#getAlloweddays()}
     *   <li>{@link StaffUserDetails#getAppointmentdate()}
     *   <li>{@link StaffUserDetails#getCity()}
     *   <li>{@link StaffUserDetails#getConfirmwebpassword()}
     *   <li>{@link StaffUserDetails#getContactNumber()}
     *   <li>{@link StaffUserDetails#getCountry()}
     *   <li>{@link StaffUserDetails#getDesignation()}
     *   <li>{@link StaffUserDetails#getEmailid()}
     *   <li>{@link StaffUserDetails#getFirstName()}
     *   <li>{@link StaffUserDetails#getLanguage()}
     *   <li>{@link StaffUserDetails#getLastName()}
     *   <li>{@link StaffUserDetails#getMsisdn()}
     *   <li>{@link StaffUserDetails#getRoles()}
     *   <li>{@link StaffUserDetails#getServices()}
     *   <li>{@link StaffUserDetails#getShortName()}
     *   <li>{@link StaffUserDetails#getState()}
     *   <li>{@link StaffUserDetails#getSubscriberCode()}
     *   <li>{@link StaffUserDetails#getUserName()}
     *   <li>{@link StaffUserDetails#getUserNamePrefix()}
     *   <li>{@link StaffUserDetails#getWebloginid()}
     *   <li>{@link StaffUserDetails#getWebpassword()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        StaffUserDetails actualStaffUserDetails = new StaffUserDetails();
        actualStaffUserDetails.setAddress1("42 Main St");
        actualStaffUserDetails.setAddress2("42 Main St");
        actualStaffUserDetails.setAllowedTimeFrom("jane.doe@example.org");
        actualStaffUserDetails.setAllowedTimeTo("alice.liddell@example.org");
        actualStaffUserDetails.setAlloweddays("Alloweddays");
        actualStaffUserDetails.setAllowedip("127.0.0.1");
        actualStaffUserDetails.setAppointmentdate("2020-03-01");
        actualStaffUserDetails.setCity("Oxford");
        actualStaffUserDetails.setConfirmwebpassword("iloveyou");
        actualStaffUserDetails.setContactNumber("42");
        actualStaffUserDetails.setCountry("GB");
        actualStaffUserDetails.setDesignation("Designation");
        actualStaffUserDetails.setEmailid("jane.doe@example.org");
        actualStaffUserDetails.setFirstName("Jane");
        actualStaffUserDetails.setLanguage("en");
        actualStaffUserDetails.setLastName("Doe");
        Msisdn[] msisdn = new Msisdn[]{new Msisdn()};
        actualStaffUserDetails.setMsisdn(msisdn);
        actualStaffUserDetails.setRoles("Roles");
        actualStaffUserDetails.setServices("Services");
        actualStaffUserDetails.setShortName("Short Name");
        actualStaffUserDetails.setState("MD");
        actualStaffUserDetails.setSubscriberCode("Subscriber Code");
        actualStaffUserDetails.setUserName("janedoe");
        actualStaffUserDetails.setUserNamePrefix("janedoe");
        actualStaffUserDetails.setWebloginid("Webloginid");
        actualStaffUserDetails.setWebpassword("iloveyou");
        actualStaffUserDetails.toString();
        assertEquals("42 Main St", actualStaffUserDetails.getAddress1());
        assertEquals("42 Main St", actualStaffUserDetails.getAddress2());
        assertEquals("jane.doe@example.org", actualStaffUserDetails.getAllowedTimeFrom());
        assertEquals("alice.liddell@example.org", actualStaffUserDetails.getAllowedTimeTo());
        assertEquals("Alloweddays", actualStaffUserDetails.getAlloweddays());
        assertEquals("2020-03-01", actualStaffUserDetails.getAppointmentdate());
        assertEquals("Oxford", actualStaffUserDetails.getCity());
        assertEquals("iloveyou", actualStaffUserDetails.getConfirmwebpassword());
        assertEquals("42", actualStaffUserDetails.getContactNumber());
        assertEquals("GB", actualStaffUserDetails.getCountry());
        assertEquals("Designation", actualStaffUserDetails.getDesignation());
        assertEquals("jane.doe@example.org", actualStaffUserDetails.getEmailid());
        assertEquals("Jane", actualStaffUserDetails.getFirstName());
        assertEquals("en", actualStaffUserDetails.getLanguage());
        assertEquals("Doe", actualStaffUserDetails.getLastName());
        assertSame(msisdn, actualStaffUserDetails.getMsisdn());
        assertEquals("Roles", actualStaffUserDetails.getRoles());
        assertEquals("Services", actualStaffUserDetails.getServices());
        assertEquals("Short Name", actualStaffUserDetails.getShortName());
        assertEquals("MD", actualStaffUserDetails.getState());
        assertEquals("Subscriber Code", actualStaffUserDetails.getSubscriberCode());
        assertEquals("janedoe", actualStaffUserDetails.getUserName());
        assertEquals("janedoe", actualStaffUserDetails.getUserNamePrefix());
        assertEquals("Webloginid", actualStaffUserDetails.getWebloginid());
        assertEquals("iloveyou", actualStaffUserDetails.getWebpassword());
    }
}

