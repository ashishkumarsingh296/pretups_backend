package com.restapi.phoneApi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherProfileTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherProfile}
     *   <li>{@link VoucherProfile#setDenomination(String)}
     *   <li>{@link VoucherProfile#setVoucherProfileID(String)}
     *   <li>{@link VoucherProfile#setVoucherProfileName(String)}
     *   <li>{@link VoucherProfile#toString()}
     *   <li>{@link VoucherProfile#getDenomination()}
     *   <li>{@link VoucherProfile#getVoucherProfileID()}
     *   <li>{@link VoucherProfile#getVoucherProfileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherProfile actualVoucherProfile = new VoucherProfile();
        actualVoucherProfile.setDenomination("Denomination");
        actualVoucherProfile.setVoucherProfileID("Voucher Profile ID");
        actualVoucherProfile.setVoucherProfileName("foo.txt");
        String actualToStringResult = actualVoucherProfile.toString();
        assertEquals("Denomination", actualVoucherProfile.getDenomination());
        assertEquals("Voucher Profile ID", actualVoucherProfile.getVoucherProfileID());
        assertEquals("foo.txt", actualVoucherProfile.getVoucherProfileName());
        assertEquals("VoucherProfile [voucherProfileName=foo.txt, denomination=Denomination, voucherProfileID=Voucher"
                + " Profile ID", actualToStringResult);
    }
}

