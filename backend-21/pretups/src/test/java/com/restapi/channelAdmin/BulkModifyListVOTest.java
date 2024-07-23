package com.restapi.channelAdmin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BulkModifyListVOTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link BulkModifyListVO}
     *   <li>{@link BulkModifyListVO#setAddress1(String)}
     *   <li>{@link BulkModifyListVO#setAddress2(String)}
     *   <li>{@link BulkModifyListVO#setAllowLowBalAlert(String)}
     *   <li>{@link BulkModifyListVO#setCity(String)}
     *   <li>{@link BulkModifyListVO#setCommProfile(String)}
     *   <li>{@link BulkModifyListVO#setCompany(String)}
     *   <li>{@link BulkModifyListVO#setContactNumber(String)}
     *   <li>{@link BulkModifyListVO#setContactPerson(String)}
     *   <li>{@link BulkModifyListVO#setCountry(String)}
     *   <li>{@link BulkModifyListVO#setDesignation(String)}
     *   <li>{@link BulkModifyListVO#setDocumentNo(String)}
     *   <li>{@link BulkModifyListVO#setDocumentType(String)}
     *   <li>{@link BulkModifyListVO#setEmail(String)}
     *   <li>{@link BulkModifyListVO#setExternalCode(String)}
     *   <li>{@link BulkModifyListVO#setFax(String)}
     *   <li>{@link BulkModifyListVO#setFirstName(String)}
     *   <li>{@link BulkModifyListVO#setGeoDomainCode(String)}
     *   <li>{@link BulkModifyListVO#setGrade(String)}
     *   <li>{@link BulkModifyListVO#setGroupRoleAllowed(String)}
     *   <li>{@link BulkModifyListVO#setInSuspend(String)}
     *   <li>{@link BulkModifyListVO#setLanguage(String)}
     *   <li>{@link BulkModifyListVO#setLastName(String)}
     *   <li>{@link BulkModifyListVO#setLatitude(String)}
     *   <li>{@link BulkModifyListVO#setLongitude(String)}
     *   <li>{@link BulkModifyListVO#setMobileNumber(String)}
     *   <li>{@link BulkModifyListVO#setOutSuspend(String)}
     *   <li>{@link BulkModifyListVO#setPaymentType(String)}
     *   <li>{@link BulkModifyListVO#setPin(String)}
     *   <li>{@link BulkModifyListVO#setRoleCode(String)}
     *   <li>{@link BulkModifyListVO#setRsaSecureID(String)}
     *   <li>{@link BulkModifyListVO#setServices(String)}
     *   <li>{@link BulkModifyListVO#setShortName(String)}
     *   <li>{@link BulkModifyListVO#setState(String)}
     *   <li>{@link BulkModifyListVO#setSubscriberCode(String)}
     *   <li>{@link BulkModifyListVO#setTrfProfile(String)}
     *   <li>{@link BulkModifyListVO#setTrfRuleCode(String)}
     *   <li>{@link BulkModifyListVO#setUserID(String)}
     *   <li>{@link BulkModifyListVO#setUserNamePrefix(String)}
     *   <li>{@link BulkModifyListVO#setVoucherType(String)}
     *   <li>{@link BulkModifyListVO#setWebLoginID(String)}
     *   <li>{@link BulkModifyListVO#setWebLoginPassword(String)}
     *   <li>{@link BulkModifyListVO#getAddress1()}
     *   <li>{@link BulkModifyListVO#getAddress2()}
     *   <li>{@link BulkModifyListVO#getAllowLowBalAlert()}
     *   <li>{@link BulkModifyListVO#getCity()}
     *   <li>{@link BulkModifyListVO#getCommProfile()}
     *   <li>{@link BulkModifyListVO#getCompany()}
     *   <li>{@link BulkModifyListVO#getContactNumber()}
     *   <li>{@link BulkModifyListVO#getContactPerson()}
     *   <li>{@link BulkModifyListVO#getCountry()}
     *   <li>{@link BulkModifyListVO#getDesignation()}
     *   <li>{@link BulkModifyListVO#getDocumentNo()}
     *   <li>{@link BulkModifyListVO#getDocumentType()}
     *   <li>{@link BulkModifyListVO#getEmail()}
     *   <li>{@link BulkModifyListVO#getExternalCode()}
     *   <li>{@link BulkModifyListVO#getFax()}
     *   <li>{@link BulkModifyListVO#getFirstName()}
     *   <li>{@link BulkModifyListVO#getGeoDomainCode()}
     *   <li>{@link BulkModifyListVO#getGrade()}
     *   <li>{@link BulkModifyListVO#getGroupRoleAllowed()}
     *   <li>{@link BulkModifyListVO#getInSuspend()}
     *   <li>{@link BulkModifyListVO#getLanguage()}
     *   <li>{@link BulkModifyListVO#getLastName()}
     *   <li>{@link BulkModifyListVO#getLatitude()}
     *   <li>{@link BulkModifyListVO#getLongitde()}
     *   <li>{@link BulkModifyListVO#getMobileNumber()}
     *   <li>{@link BulkModifyListVO#getOutSuspend()}
     *   <li>{@link BulkModifyListVO#getPaymentType()}
     *   <li>{@link BulkModifyListVO#getPin()}
     *   <li>{@link BulkModifyListVO#getRoleCode()}
     *   <li>{@link BulkModifyListVO#getRsaSecureID()}
     *   <li>{@link BulkModifyListVO#getServices()}
     *   <li>{@link BulkModifyListVO#getShortName()}
     *   <li>{@link BulkModifyListVO#getState()}
     *   <li>{@link BulkModifyListVO#getSubscriberCode()}
     *   <li>{@link BulkModifyListVO#getTrfProfile()}
     *   <li>{@link BulkModifyListVO#getTrfRuleCode()}
     *   <li>{@link BulkModifyListVO#getUserID()}
     *   <li>{@link BulkModifyListVO#getUserNamePrefix()}
     *   <li>{@link BulkModifyListVO#getVoucherType()}
     *   <li>{@link BulkModifyListVO#getWebLoginID()}
     *   <li>{@link BulkModifyListVO#getWebLoginPassword()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        BulkModifyListVO actualBulkModifyListVO = new BulkModifyListVO();
        actualBulkModifyListVO.setAddress1("42 Main St");
        actualBulkModifyListVO.setAddress2("42 Main St");
        actualBulkModifyListVO.setAllowLowBalAlert("Allow Low Bal Alert");
        actualBulkModifyListVO.setCity("Oxford");
        actualBulkModifyListVO.setCommProfile("Comm Profile");
        actualBulkModifyListVO.setCompany("Company");
        actualBulkModifyListVO.setContactNumber("42");
        actualBulkModifyListVO.setContactPerson("Contact Person");
        actualBulkModifyListVO.setCountry("GB");
        actualBulkModifyListVO.setDesignation("Designation");
        actualBulkModifyListVO.setDocumentNo("Document No");
        actualBulkModifyListVO.setDocumentType("Document Type");
        actualBulkModifyListVO.setEmail("jane.doe@example.org");
        actualBulkModifyListVO.setExternalCode("External Code");
        actualBulkModifyListVO.setFax("Fax");
        actualBulkModifyListVO.setFirstName("Jane");
        actualBulkModifyListVO.setGeoDomainCode("Geo Domain Code");
        actualBulkModifyListVO.setGrade("Grade");
        actualBulkModifyListVO.setGroupRoleAllowed("Group Role Allowed");
        actualBulkModifyListVO.setInSuspend("In Suspend");
        actualBulkModifyListVO.setLanguage("en");
        actualBulkModifyListVO.setLastName("Doe");
        actualBulkModifyListVO.setLatitude("Latitude");
        actualBulkModifyListVO.setLongitude("Longitde");
        actualBulkModifyListVO.setMobileNumber("42");
        actualBulkModifyListVO.setOutSuspend("Out Suspend");
        actualBulkModifyListVO.setPaymentType("Payment Type");
        actualBulkModifyListVO.setPin("Pin");
        actualBulkModifyListVO.setRoleCode("Role Code");
        actualBulkModifyListVO.setRsaSecureID("Rsa Secure ID");
        actualBulkModifyListVO.setServices("Services");
        actualBulkModifyListVO.setShortName("Short Name");
        actualBulkModifyListVO.setState("MD");
        actualBulkModifyListVO.setSubscriberCode("Subscriber Code");
        actualBulkModifyListVO.setTrfProfile("Trf Profile");
        actualBulkModifyListVO.setTrfRuleCode("Trf Rule Code");
        actualBulkModifyListVO.setUserID("User ID");
        actualBulkModifyListVO.setUserNamePrefix("janedoe");
        actualBulkModifyListVO.setVoucherType("Voucher Type");
        actualBulkModifyListVO.setWebLoginID("Web Login ID");
        actualBulkModifyListVO.setWebLoginPassword("iloveyou");
        assertEquals("42 Main St", actualBulkModifyListVO.getAddress1());
        assertEquals("42 Main St", actualBulkModifyListVO.getAddress2());
        assertEquals("Allow Low Bal Alert", actualBulkModifyListVO.getAllowLowBalAlert());
        assertEquals("Oxford", actualBulkModifyListVO.getCity());
        assertEquals("Comm Profile", actualBulkModifyListVO.getCommProfile());
        assertEquals("Company", actualBulkModifyListVO.getCompany());
        assertEquals("42", actualBulkModifyListVO.getContactNumber());
        assertEquals("Contact Person", actualBulkModifyListVO.getContactPerson());
        assertEquals("GB", actualBulkModifyListVO.getCountry());
        assertEquals("Designation", actualBulkModifyListVO.getDesignation());
        assertEquals("Document No", actualBulkModifyListVO.getDocumentNo());
        assertEquals("Document Type", actualBulkModifyListVO.getDocumentType());
        assertEquals("jane.doe@example.org", actualBulkModifyListVO.getEmail());
        assertEquals("External Code", actualBulkModifyListVO.getExternalCode());
        assertEquals("Fax", actualBulkModifyListVO.getFax());
        assertEquals("Jane", actualBulkModifyListVO.getFirstName());
        assertEquals("Geo Domain Code", actualBulkModifyListVO.getGeoDomainCode());
        assertEquals("Grade", actualBulkModifyListVO.getGrade());
        assertEquals("Group Role Allowed", actualBulkModifyListVO.getGroupRoleAllowed());
        assertEquals("In Suspend", actualBulkModifyListVO.getInSuspend());
        assertEquals("en", actualBulkModifyListVO.getLanguage());
        assertEquals("Doe", actualBulkModifyListVO.getLastName());
        assertEquals("Latitude", actualBulkModifyListVO.getLatitude());
        assertEquals("Longitde", actualBulkModifyListVO.getLongitde());
        assertEquals("42", actualBulkModifyListVO.getMobileNumber());
        assertEquals("Out Suspend", actualBulkModifyListVO.getOutSuspend());
        assertEquals("Payment Type", actualBulkModifyListVO.getPaymentType());
        assertEquals("Pin", actualBulkModifyListVO.getPin());
        assertEquals("Role Code", actualBulkModifyListVO.getRoleCode());
        assertEquals("Rsa Secure ID", actualBulkModifyListVO.getRsaSecureID());
        assertEquals("Services", actualBulkModifyListVO.getServices());
        assertEquals("Short Name", actualBulkModifyListVO.getShortName());
        assertEquals("MD", actualBulkModifyListVO.getState());
        assertEquals("Subscriber Code", actualBulkModifyListVO.getSubscriberCode());
        assertEquals("Trf Profile", actualBulkModifyListVO.getTrfProfile());
        assertEquals("Trf Rule Code", actualBulkModifyListVO.getTrfRuleCode());
        assertEquals("User ID", actualBulkModifyListVO.getUserID());
        assertEquals("janedoe", actualBulkModifyListVO.getUserNamePrefix());
        assertEquals("Voucher Type", actualBulkModifyListVO.getVoucherType());
        assertEquals("Web Login ID", actualBulkModifyListVO.getWebLoginID());
        assertEquals("iloveyou", actualBulkModifyListVO.getWebLoginPassword());
    }
}

