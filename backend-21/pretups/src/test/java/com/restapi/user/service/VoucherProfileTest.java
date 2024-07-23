package com.restapi.user.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VoucherProfileTest {
    /**
     * Methods under test:
     *
     * <ul>
     *   <li>default or parameterless constructor of {@link VoucherProfile}
     *   <li>{@link VoucherProfile#setDenomination(String)}
     *   <li>{@link VoucherProfile#setNoOfVouchersAvailable(String)}
     *   <li>{@link VoucherProfile#setTotalAmount(String)}
     *   <li>{@link VoucherProfile#setVoucherProfileID(String)}
     *   <li>{@link VoucherProfile#setVoucherProfileName(String)}
     *   <li>{@link VoucherProfile#toString()}
     *   <li>{@link VoucherProfile#getDenomination()}
     *   <li>{@link VoucherProfile#getNoOfVouchersAvailable()}
     *   <li>{@link VoucherProfile#getTotalAmount()}
     *   <li>{@link VoucherProfile#getVoucherProfileID()}
     *   <li>{@link VoucherProfile#getVoucherProfileName()}
     * </ul>
     */
    @Test
    public void testConstructor() {
        VoucherProfile actualVoucherProfile = new VoucherProfile();
        actualVoucherProfile.setDenomination("Denomination");
        actualVoucherProfile.setNoOfVouchersAvailable("No Of Vouchers Available");
        actualVoucherProfile.setTotalAmount("10");
        actualVoucherProfile.setVoucherProfileID("Voucher Profile ID");
        actualVoucherProfile.setVoucherProfileName("foo.txt");
        String actualToStringResult = actualVoucherProfile.toString();
        assertEquals("Denomination", actualVoucherProfile.getDenomination());
        assertEquals("No Of Vouchers Available", actualVoucherProfile.getNoOfVouchersAvailable());
        assertEquals("10", actualVoucherProfile.getTotalAmount());
        assertEquals("Voucher Profile ID", actualVoucherProfile.getVoucherProfileID());
        assertEquals("foo.txt", actualVoucherProfile.getVoucherProfileName());
        assertEquals("VoucherProfile [voucherProfileName=foo.txt, denomination=Denomination, noOfVouchersAvailable=No Of"
                + " Vouchers Available, voucherProfileID=Voucher Profile ID, totalAmount=10]", actualToStringResult);
    }
}

