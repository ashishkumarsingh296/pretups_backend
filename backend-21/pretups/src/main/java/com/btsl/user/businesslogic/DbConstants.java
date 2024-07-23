/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import lombok.Getter;

/**
 * This Enum constants used for DB based constants.
 * 
 * @author SubeshKCV
 */
@Getter
public enum DbConstants {

    /** The status. */
    // Query Parameters
    STATUS("status"),

    /** The status id. */
    STATUS_ID("statusId"),

    /** The application id. */
    APPLICATION_ID("applicationId"),

    /** The releation type. */
    RELEATION_TYPE("relationType"),

    /** The user name. */
    USER_NAME("userName"),

    /** The network code. */
    NETWORK_CODE("networkCode"),

    /** The domain code. */
    // Domain parameters
    DOMAIN_CODE("domainCode"),

    /** The domain name. */
    DOMAIN_NAME("domainName"),

    /** The category code. */
    // Category parameters
    CATEGORY_CODE("categoryCode"),

    /** The category name. */
    CATEGORY_NAME("categoryName"),

    /** The owner cat. */
    OWNER_CAT("ownerCat"),

    /** The grph domain code. */
    GRPH_DOMAIN_CODE("grphDomainCode"),

    /** The grade code. */
    // Grade parameters
    GRADE_CODE("gradeCode"),

    /** The grade name. */
    GRADE_NAME("gradeName"),

    /** The group role type id. */
    // Group Role parameters
    GROUP_ROLE_TYPE_ID("GID"),

    /** The grouprole code. */
    GROUPROLE_CODE("groupRoleCode"),

    /** The login id. */
    // General parameters
    LOGIN_ID("loginId"),

    /** The msisdn. */
    MSISDN("msisdn"),

    /** The user id. */
    USER_ID("userId"),

    /** The agentcode. */
    AGENTCODE("agentCode"),

    /** The user type. */
    USER_TYPE("userType"),

    /** The users table. */
    USERS_TABLE("users"),

    /** The users m table. */
    USERS_M_TABLE("users_m"),

    /** The user phones table. */
    USER_PHONES_TABLE("user_phones"),

    /** The user phones m table. */
    USER_PHONES_M_TABLE("user_phones_m"),

    /** The channel users table. */
    CHANNEL_USERS_TABLE("channel_users"),

    /** The channel users m table. */
    CHANNEL_USERS_M_TABLE("channel_users_m"),

    /** The mtx wallet table. */
    MTX_WALLET_TABLE("mtx_wallet"),

    /** The mtx payment methods. */
    MTX_PAYMENT_METHODS("mtx_payment_methods"),

    /** The owner user id. */
    OWNER_USER_ID("ownerUserId"),

    /** The wallet id. */
    WALLET_ID("walletId"),

    /** The wallet number. */
    WALLET_NUMBER("walletNumber"),

    /** The profile id. */
    PROFILE_ID("profileId"),

    /** The owner profile id. */
    OWNER_PROFILE_ID("ownerProfileId"),

    /** The profile type. */
    PROFILE_TYPE("profileType"),

    /** The bank id. */
    BANK_ID("bankId"),

    /** The provider id. */
    PROVIDER_ID("providerId"),

    /** The protelecom remitt id. */
    PROTELECOM_REMITT_ID("protelecomRemittId"),

    /** The commissionwallet type id. */
    COMMISSIONWALLET_TYPE_ID("commissionWalletTypeId"),

    /** The remittancewallet type id. */
    REMITTANCEWALLET_TYPE_ID("remittanceWalletTypeId"),

    /** The bonus wallet type id. */
    BONUS_WALLET_TYPE_ID("bonusWalletTypeId"),

    /** The payment method type bank. */
    // system preference
    PAYMENT_METHOD_TYPE_BANK("BANK"),

    /** The multiple lnkd banks. */
    MULTIPLE_LNKD_BANKS("MULTIPLE_LNKD_BANKS"),

    /** The remittance wallet identifier. */
    REMITTANCE_WALLET_IDENTIFIER("REMITTANCE_WALLET_IDENTIFIER"),

    /** The multiple wallets. */
    MULTIPLE_WALLETS("MULTIPLE_WALLETS"),

    /** The is remittance wallet required. */
    IS_REMITTANCE_WALLET_REQUIRED("IS_REMITTANCE_WALLET_REQUIRED"),

    /** The inactive status. */
    // Default values
    INACTIVE_STATUS("N"),

    /** The application id value. */
    APPLICATION_ID_VALUE("2"),

    /** The payment type id. */
    PAYMENT_TYPE_ID("22"),

    /** The imtsmsr 9022. */
    IMTSMSR_9022("9022"),

    /** The imtsmsr 9024. */
    IMTSMSR_9024("9024"),

    /** The pseudo. */
    PSEUDO("PSEUDO"),

    /** The paymentmethod type id. */
    PAYMENTMETHOD_TYPE_ID("paymentMethodTypeId"),
    /** The zero. */
    // Result positions
    ZERO(0),
    /** The one. */
    ONE(1),
    /** The two. */
    TWO(2),
    /** The three. */
    THREE(3),
    /** The four. */
    FOUR(4),
    /** The six. */
    SIX(6),

    LAST_NAME("lastName"), PROCESS_ID("processid"),
    /** The created on. */
    CREATED_ON("createdOn"),

    /** The modified on. */
    MODIFIED_ON("modifiedOn"),

    /** The transfer date. */
    TRANSFER_DATE("transferDate"),

    /** The last login on. */
    LAST_LOGIN_ON("lastLoginOn"),

    /** The idno. */
    IDNO("idNo"),

    /** The wnop id. */
    WNOP_ID("wnopid"),
    /** The company code. */
    COMPANY_CODE("companyCode"),

    /** The code user type. */
    CODE_USER_TYPE("CodeuserType"),

    /** The paid status. */
    PAID_STATUS("paidStatus"),
    
    GATEWAY_CODE("gatewayCode"),
    USER_LOGIN_ID("userLoginID"),
    USER_STATUS1("userStatus1"),
    USER_STATUS2("userStatus2"),
    CAT_STATUS("catStatus"),
    PSTATUS("pstatus"),
    PTOTALNOVOUCHERS("ptotalNoVouchers"),
    BATCH_ACCEPTED("AC"),
    PNETWORKCODE("pNetworkCode"),
    PBATCHNO("pbatchNo"),
    PNETWORKCODE2("pNetworkCode2"),
    PPRODUCTID("pProductID"),
    PDENOMID("pDenomID"),
    PVOUCHERTYPE("pVoucherType"),
    POUCHERSEGMENT("pVoucherSegment")
    ;
    

    /** The str value. */
    // DB String Constant
    private String strValue;

    /** The int value. */
    // DB Integer Constant
    private int intValue;

    /**
     * Construct Integer Constant.
     *
     * @param intValue
     *            - intValue
     */
    DbConstants(int intValue) {
        this.intValue = intValue;
    }

    /**
     * Construct String Constant.
     *
     * @param strValue
     *            - strValue
     */
    DbConstants(String strValue) {
        this.strValue = strValue;
    }

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

    
    
    
}
