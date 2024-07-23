/** 
 * This software is the sole property of Comviva
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
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
 * This Enum constants used for System.
 * 
 * @author SubeshKCV
 */

/**
 * Gets the int value.
 *
 * @return the int value
 */
@Getter
public enum Constants {

	/** The default lang. */
	DEFAULT_LANG(0),
	/** The active status. */
	ACTIVE_STATUS("Y"),

	key("981AFA8CDEB2A0F7E0A011B557BB08CF"),

	SUSPEND("S"),
	/** The inactive status. */
	INACTIVE_STATUS("N"),
	/** The all. */
	ALL("ALL"),
	/** The yes. */
	YES("Y"),
	/** The no. */
	NO("N"),

	/** The sms. */
	SMS("SMS"),

	/** The email. */
	EMAIL("EMAIL"),
	/** The space. */
	SPACE(" "),
	/** The slash. */
	SLASH("/"),
	/** The minus. */
	MINUS("-"),
	/** The true. */
	TRUE("TRUE"),
	/** The false. */
	FALSE("FALSE"),
	/** The login id. */
	LOGIN_ID("loginId"),
	/** The web. */
	WEB("WEB"),
	/** The mobile. */
	MOBILE("MOBILE"),
	/** The bank. */
	BANK("BANK"),

	/** The wallet. */
	WALLET("WALLET"),

	/** The user type operator. */
	USER_TYPE_OPERATOR("OPERATOR"),

	/** The initiator. */
	INITIATOR("INITIATOR"),

	/** The rollback. */
	ROLLBACK("ROLLBACK"),

	/** The domaincode wholesaler. */
	DOMAINCODE_WHOLESALER("DISTWS"),

	/** The domaincode wholesale. */
	DOMAINCODE_WHOLESALE("WHS"),

	/** The api key header. */
	API_KEY_HEADER("Authorization"),

	/** The add initiate status. */
	ADD_INITIATE_STATUS("AI"),

	/** The update initiate status. */
	UPDATE_INITIATE_STATUS("UI"),

	/** The delete initiate status. */
	DELETE_INITIATE_STATUS("DI"),

	/** The generic error message. */
	GENERIC_ERROR_MESSAGE("GENERIC ERROR IN MDS"),

	/** The success response. */
	SUCCESS_RESPONSE("200"),
	
	
	/** Internal server error*/
	INTERNAL_SERVER_ERROR("500"),

	/** The bad request. */
	BAD_REQUEST("400"),

	/** The success message. */
	SUCCESS_MESSAGE("Success"),

	/** The action. */
	ACTION_APPROVE("APPROVE"),

	/** The action reject. */
	ACTION_REJECT("REJECT"),

	/** The bearer code. */
	BEARER_CODE("BEARER_CODE"),

	/** The application code value. */
	APPLICATION_CODE_VALUE("2"),

	/** The domain seqno value. */
	DOMAIN_SEQNO_VALUE(5),

	/** The kafka json length. */
	KAFKA_JSON_LENGTH(2),

	/** The custom json layout length. */
	CUSTOM_JSON_LAYOUT_LENGTH(2),

	/** The group level. */
	GROUP_LEVEL(2),

	/** The web group role. */
	WEB_GROUP_ROLE(1),

	/** The user code length. */
	USER_CODE_LENGTH(10),
	/** The mobile group role. */
	MOBILE_GROUP_ROLE(2),

	/** The domain name length. */
	DOMAIN_NAME_LENGTH(50),

	/** The domain code length. */
	DOMAIN_CODE_LENGTH(10),

	/** The number of cat length. */
	NUMBER_OF_CAT_LENGTH(5),

	/** The grade code length. */
	GRADE_CODE_LENGTH(10),

	/** The grade name length. */
	GRADE_NAME_LENGTH(40),

	/** The category code length. */
	CATEGORY_CODE_LENGTH(10),

	/** The category name length. */
	CATEGORY_NAME_LENGTH(50),

	/** The grouprole code length. */
	GROUPROLE_CODE_LENGTH(35),

	/** The grouprole name length. */
	GROUPROLE_NAME_LENGTH(50),

	/** The wallet name length. */
	WALLET_NAME_LENGTH(20),

	/** The userid. */
	USERID("User Id"),

	/** The userid. */
	PROFILEID("Profile Id"),

	/** The fromserialno. */
	FROMSERIALNO("From Serial No"),

	/** The toserialno. */
	TOSERIALNO("To Serial No"),

	/** The voucherType. */
	VOUCHERTYPE("voucher Type"),
	
	/** The voucherType. */
    VOUCHERUPLOAD("voucher Upload"),
    
    /** The voucherType. */
    FILETYPE("File Type"),
    
    /** The voucherType. */
    FILENAME("File Name"),

	/** The voucherType. */
	VOUCHERSEGMENT("voucher Segment"),

	/** The voucherType. */
	VOUCHEREXPIRY_PERIOD("voucher expiry period"),
	/** The voucherType. */
	VOUCHEREXPIRY_DATE("voucher expiry Date"),

	/** The invalid request. */
	INVALID_REQUEST("Request Params"),

	/** The grade code. */
	GRADE_CODE("Grade Code"),

	/** The grade name. */
	GRADE_NAME("Grade Name"),

	/** The category code. */
	CATEGORY_CODE("Category Code"),

	/** The category name. */
	CATEGORY_NAME("Category Name"),

	/** The parent category code. */
	PARENT_CATEGORY_CODE("Parent Category Code"),

	/** The pseudo category code. */
	PSEUDO_CATEGORY_CODE("Pseudo Category Code"),

	/** The pseudo category name. */
	PSEUDO_CATEGORY_NAME("Pseudo Category Name"),

	/** The action code. */
	ACTION_CODE("Action"),

	/** The domain code. */
	DOMAIN_CODE("Domain Code"),

	/** The domain name. */
	DOMAIN_NAME("Domain Name"),

	/** The number of cat. */
	NUMBER_OF_CAT("Number of Cat"),

	/** The initiate status. */
	INITIATE_STATUS("InitiateStatus"),

	/** The page number. */
	PAGE_NUMBER("Page Number"),

	/** The per page. */
	PER_PAGE("Per Page"),

	/** The identifier type. */
	IDENTIFIER_TYPE("identifierType"),

	/** The bearer code value. */
	BEARER_CODE_VALUE("bearerCode"),

	/** The identifier value. */
	IDENTIFIER_VALUE("identifierValue"),

	/** The identifier value lenght. */
	IDENTIFIER_VALUE_LENGHT(20),

	/** The is approve. */
	IS_APPROVE("isApprovalRequired"),

	/** The bank id. */
	BANK_ID("bankId"),

	/** The remarks. */
	REMARKS("remarks"),

	/** The group role code. */
	GROUP_ROLE_CODE("Group Role Code"),

	/** The group role name. */
	GROUP_ROLE_NAME("Group Role Name"),

	/** The group role type. */
	GROUP_ROLE_TYPE("Group Role Type"),

	/** The role type. */
	ROLE_TYPE("Role Type"),

	/** The linked banks. */
	LINKED_BANKS("Linked Banks"),

	/** The role list. */
	ROLE_LIST("Role List"),

	/** The service list. */
	SERVICE_LIST("Service List"),

	/** The service type. */
	SERVICE_TYPE("Service Type"),
	
	SUBSERVICE("Sub Service"),

	/** The service type code. */
	SERVICE_TYPE_CODE("serviceType"),

	/** The service pymt id. */
	SERVICE_PYMT_ID("Service Pymt Id"),

	/** The servie name. */
	SERVIE_NAME("Service Name"),

	/** The service status. */
	SERVICE_STATUS("Service Status"),

	/** The status. */
	STATUS("Status"),

	/** The service code. */
	SERVICE_CODE("Service Code"),

	/** The initiate type. */
	INITIATE_TYPE("Initiate type"),

	/** The entry type. */
	ENTRY_TYPE("Entry type"),

	/** The module code. */
	MODULE_CODE("moduleCode"),

	/** The role name. */
	ROLE_NAME("roleName"),

	/** The role code. */
	ROLE_CODE("roleCode"),

	/** The role status. */
	ROLE_STATUS("roleStatus"),

	/** The service paymt id. */
	SERVICE_PAYMT_ID("servicePymtId"),

	/** The application code. */
	APPLICATION_CODE("Application Code"),

	/** The payment instrument id. */
	PAYMENT_INSTRUMENT_ID("Payment Instrument Id"),

	/** The payment method type id. */
	PAYMENT_METHOD_TYPE_ID("Payment Method Type Id"),

	/** The category details. */
	CATEGORY_DETAILS("Category Details"),

	/** The payer selection wallet service. */
	PAYER_SELECTION_WALLET_SERVICE("Please select altleast one payer Wallet Service"),

	/** The payee selection wallet service. */
	PAYEE_SELECTION_WALLET_SERVICE("Please select altleast one payee Wallet Service"),

	/** The initiator selection wallet service. */
	INITIATOR_SELECTION_WALLET_SERVICE("Please select altleast one initiator Wallet Service"),

	/** The payer selection bank service. */
	PAYER_SELECTION_BANK_SERVICE("Please select altleast one payer Bank Service"),

	/** The payee selection bank service. */
	PAYEE_SELECTION_BANK_SERVICE("Please select altleast one payee Bank Service"),

	/** The initiator selection bank service. */
	INITIATOR_SELECTION_BANK_SERVICE("Please select altleast one initiator Bank Service"),

	/** The service bearer code. */
	SERVICE_BEARER_CODE("bearerCode"),

	/** The selected. */
	SELECTED("Selected"),

	/** The financial services. */
	FINANCIAL_SERVICES("Financial Services"),

	/** The user type payer. */
	USER_TYPE_PAYER("PAYER"),

	/** The user type payee. */
	USER_TYPE_PAYEE("PAYEE"),

	/** The distributed channel. */
	DISTRIBUTED_CHANNEL("DISTB_CHAN"),

	/** The application id. */
	APPLICATION_ID("2"),

	/** The user id. */
	USER_ID("SU001"),

	/** The gateway type. */
	GATEWAY_TYPE("WEB"),

	/** The owner. */
	OWNER("OWNER"),

	/** The parent. */
	PARENT("PARENT"),

	/** The bnkadm. */
	BNKADM("BNKADM"),

	/** The provider id. */
	// OC2 Constant
	PROVIDER_ID("Provider Id"),

	/** The wallet id. */
	WALLET_ID("Wallet Id"),

	/** The no of instance. */
	NO_OF_INSTANCE("No Of Instance"),

	/** The payment instrument type. */
	PAYMENT_INSTRUMENT_TYPE("Pyament Type"),

	/** The payment instrument type id. */
	PAYMENT_INSTRUMENT_TYPE_ID("Pyament Type Id"),

	/** The approval limit1. */
	APPROVAL_LIMIT1("First Approval Limit"),

	/** The currency factor. */
	CURRENCY_FACTOR("CURRENCY_FACTOR"),

	/** The first level limit approval. */
	FIRST_LEVEL_LIMIT_APPROVAL("A1"),

	/** The transfer rule approve req. */
	TRANSFER_RULE_APPROVE_REQ("Approval Required"),

	/** The un controlled transfer level. */
	UN_CONTROLLED_TRANSFER_LEVEL("UnControlledTransferLevel"),

	/** The fixed transfer category. */
	FIXED_TRANSFER_CATEGORY("FixedTransferCategory"),

	/** The o2c service. */
	O2C_SERVICE("O2C"),

	/** The operator. */
	OPERATOR("OPT"),

	/** The transfer type sale. */
	TRANSFER_TYPE_SALE("TR_SALE"),

	/** The multiple pmt instrmt. */
	// Transfer Rule Constants
	MULTIPLE_PMT_INSTRMT("MULTIPLE_PMT_INSTRMT"),

	/** The multiple wallets. */
	MULTIPLE_WALLETS("MULTIPLE_WALLETS"),

	/** The commission wallet identifier. */
	COMMISSION_WALLET_IDENTIFIER("COMMISSION_WALLET_IDENTIFIER"),

	/** The is commission wallet required. */
	IS_COMMISSION_WALLET_REQUIRED("IS_COMMISSION_WALLET_REQUIRED"),

	/** The is remittance wallet required. */
	IS_REMITTANCE_WALLET_REQUIRED("IS_REMITTANCE_WALLET_REQUIRED"),

	/** The remittance wallet identifier. */
	REMITTANCE_WALLET_IDENTIFIER("REMITTANCE_WALLET_IDENTIFIER"),

	/** The protelecom remittance wallet identifier. */
	PROTELECOM_REMITTANCE_WALLET_IDENTIFIER("PROTELECOM_REMITTANCE_WALLET_IDENTIFIER"),

	/** The is protelecom remittance wallet required. */
	IS_PROTELECOM_REMITTANCE_WALLET_REQUIRED("IS_PROTELECOM_REMITTANCE_WALLET_REQUIRED"),

	/** The bonussva wallet identifier. */
	BONUSSVA_WALLET_IDENTIFIER("BONUSSVA_WALLET_IDENTIFIER"),

	/** The transfer rule id not found. */
	TRANSFER_RULE_ID_NOT_FOUND("Transfer Rule Id not Found"),

	/** The transfer rule id. */
	TRANSFER_RULE_ID("Transfer Rule Id"),

	/** The transfer type enum. */
	TRANSFER_TYPE_ENUM("TRANSFER_TYPE"),

	/** The fixed transfer level. */
	FIXED_TRANSFER_LEVEL("Fixed Transfer Level"),

	/** The controlled transfer level. */
	CONTROLLED_TRANSFER_LEVEL("Controlled Transfer Level"),

	/** The transfer rule type. */
	TRANSFER_RULE_TYPE("Transfer Rule Type"),

	/** The grph domain code. */
	GRPH_DOMAIN_CODE("Grph Domain Code"),

	/** The grph domaincode. */
	GRPH_DOMAINCODE("GrphDomainCode"),

	/** The payer domain code. */
	PAYER_DOMAIN_CODE("PayerDomainCode"),

	/** The payee domain code. */
	PAYEE_DOMAIN_CODE("PayeeDomainCode"),

	/** The payer category code. */
	PAYER_CATEGORY_CODE("PayerCategoryCode"),

	/** The payee category code. */
	PAYEE_CATEGORY_CODE("PayeeCategoryCode"),

	/** The foc allowed. */
	FOC_ALLOWED("focAllowed"),

	/** The payment methodtype. */
	PAYMENT_METHODTYPE("paymentMethodType"),

	/** The payer payment methodtype. */
	PAYER_PAYMENT_METHODTYPE("payerpaymentMethodType"),

	/** The payee payment methodtype. */
	PAYEE_PAYMENT_METHODTYPE("payeepaymentMethodType"),

	/** The payer payment instrument id. */
	PAYER_PAYMENT_INSTRUMENT_ID("PayerPaymentInstrumentId"),

	/** The payee payment instrument id. */
	PAYEE_PAYMENT_INSTRUMENT_ID("PayeePaymentInstrumentId"),

	/** The payer bankid. */
	PAYER_BANKID("Payer Bank Id"),

	/** The payee bankid. */
	PAYEE_BANKID("Payee Bank Id"),

	/** The payer providerid. */
	PAYER_PROVIDERID("Payer Provider Id"),

	/** The payee providerid. */
	PAYEE_PROVIDERID("Payee Provider Id"),

	/** The payer linkedwallet bankid. */
	PAYER_LINKEDWALLET_BANKID("PayerLinkedWalletBankId"),

	/** The payee linkedwallet bankid. */
	PAYEE_LINKEDWALLET_BANKID("PayeeLinkedWalletBankId"),

	/** The payer paymenttypeid. */
	PAYER_PAYMENTTYPEID("Payer Payment Type Id"),

	/** The payee paymenttypeid. */
	PAYEE_PAYMENTTYPEID("Payee Payment Type Id"),

	/** The payer gradecode. */
	PAYER_GRADECODE("Payer Grade Code"),

	/** The payee gradecode. */
	PAYEE_GRADECODE("Payee Grade Code"),

	/** The parent assocallowed. */
	PARENT_ASSOCALLOWED("Parentassoc Allowed"),

	/** The linked bankwallet id. */
	LINKED_BANKWALLET_ID("linkedBankWalletId"),

	/** The profile id. */
	PROFILE_ID("profileId"),

	/** The res msisdn allowed. */
	RES_MSISDN_ALLOWED("ResMsisdn allowed"),

	/** The bypass allowed. */
	BYPASS_ALLOWED("Bypass Allowed"),

	/** The direct transfer allowed. */
	DIRECT_TRANSFER_ALLOWED("Direct Transfer Allowed"),

	/** The uncontroled transfer allowed. */
	UNCONTROLED_TRANSFER_ALLOWED("Un Controled Transfer Allowed"),

	/** The transfer category. */
	TRANSFER_CATEGORY("Transfer Category"),

	/** The transfer type. */
	TRANSFER_TYPE("TransferType"),

	/** The domain subscriber. */
	DOMAIN_SUBSCRIBER("SUBS"),

	/** The service type ccashout. */
	SERVICE_TYPE_CCASHOUT("CCASHOUT"),

	/** The enterprise domain. */
	ENTERPRISE_DOMAIN("Enterprise"),

	/** The user type operator code. */
	USER_TYPE_OPERATOR_CODE("OPT"),

	/** The vault domain. */
	VAULT_DOMAIN("VAULT"),

	/** The servicetype auto. */
	SERVICETYPE_AUTO("AUTOO2C"),

	/** The servicetype stock. */
	SERVICETYPE_STOCK("STOCK"),

	/** The servicetype optw. */
	SERVICETYPE_OPTW("OPTW"),

	/** The transfer funds between self wallets service type. */
	TRANSFER_FUNDS_BETWEEN_SELF_WALLETS_SERVICE_TYPE("FTBOA"),

	/** The transfer funds between related parties service type. */
	TRANSFER_FUNDS_BETWEEN_RELATED_PARTIES_SERVICE_TYPE("FTBRP"),

	/** The bearer for all service tcp. */
	BEARER_FOR_ALL_SERVICE_TCP("J2ME:PAYEE:PAYER,WEB:PAYEE:PAYER,IVR:PAYEE:PAYER,API:PAYEE:PAYER,"
			+ "WEBSERVICE:PAYEE:PAYER,GATEWAY:PAYEE:PAYER,USSD:PAYEE:PAYER,SYSTEM:PAYEE:PAYER"),

	/** The control profile cum service type all. */
	CONTROL_PROFILE_CUM_SERVICE_TYPE_ALL("ALLSERV"),

	/** The control profile cum service name. */
	CONTROL_PROFILE_CUM_SERVICE_NAME("All Service"),

	/** The default registration type. */
	DEFAULT_REGISTRATION_TYPE("DEFAULT_REGISTRATION_TYPE"),

	/** The o2c not include domains. */
	O2C_NOT_INCLUDE_DOMAINS("OPT,SUBS,MERCHANT,MTOPT"),

	/** The bulk payer admin category code. */
	BULK_PAYER_ADMIN_CATEGORY_CODE("ENTADM"),

	/** The kyc enum id. */
	KYC_ENUM_ID("KYC_MODE"),

	/** The thresh hold names. */
	THRESH_HOLD_NAMES("DAY,WEEK,MONTH"),

	/** The network code. */
	// GeoGrapghical
	NETWORK_CODE("NETWORK CODE"),

	/** The operation type error. */
	OPERATION_TYPE_ERROR("Operation Type"),

	/** The parent domain code. */
	PARENT_DOMAIN_CODE("PARENT DOMAIN"),

	/** The grph domain type. */
	GRPH_DOMAIN_TYPE("GRPH DOMAIN TYPE"),

	/** The walet name. */
	// walletmanagement
	WALET_NAME("Wallet Name"),

	/** The wallet rec. */
	WALLET_REC("Wallet"),

	/** The walet type. */
	WALET_TYPE("Wallet Type"),

	/** The walet id. */
	WALET_ID("Wallet Id"),

	/** The max wlt add perday. */
	MAX_WLT_ADD_PERDAY("MAX_WLT_ADD_PERDAY"),

	/** The master wallet type id. */
	MASTER_WALLET_TYPE_ID("MASTER_WALLET_TYPE_ID"),

	/** The preference paymentinstrument. */
	PREFERENCE_PAYMENTINSTRUMENT("payment Instrument Id"),

	/** The preference paymentinstrument type. */
	PREFERENCE_PAYMENTINSTRUMENT_TYPE("payment Instrument type Id"),

	/** The preference payment type id. */
	PREFERENCE_PAYMENT_TYPE_ID("payment type id"),

	/** The preference kyc mode. */
	PREFERENCE_KYC_MODE("Kyc Mode"),

	/** The preference provider id. */
	PREFERENCE_PROVIDER_ID("provider id"),

	/** The preference grade. */
	PREFERENCE_GRADE("channel grades"),

	/** The preference profileid. */
	PREFERENCE_PROFILEID("profile id"),

	/** The preference categorycode. */
	PREFERENCE_CATEGORYCODE("category code"),

	/** The preference type. */
	PREFERENCE_TYPE("preference type"),

	/** The preference isprimary. */
	PREFERENCE_ISPRIMARY("is primary"),

	/** The sav club creation. */
	SAV_CLUB_CREATION("SAV_CLUB_CREATION"),

	/** The user registration. */
	USER_REGISTRATION("USER_REGISTRATION"),

	/** The bank account linking. */
	BANK_ACCOUNT_LINKING("BANK_ACCOUNT_LINKING"),

	/** The automatic grade change. */
	AUTOMATIC_GRADE_CHANGE("AUTOMATIC_GRADE_CHANGE"),

	/** The preference bankid. */
	PREFERENCE_BANKID("Bank Id"),

	/** The distribution domain code. */
	DISTRIBUTION_DOMAIN_CODE("DISTB_CHAN"),

	/** The corporate. */
	CORPORATE("CORPORATE"),

	/** The default provider id. */
	// Mfs Wallet Type
	DEFAULT_PROVIDER_ID("Default Provider Id"),

	/** The payment type. */
	PAYMENT_TYPE_STR("Payment Type"),
	/** The payment type id. */
	PAYMENT_TYPE_ID("Payment Type Id"),

	/** The service payment id. */
	SERVICE_PAYMENT_ID("Service Paymentt Id"),

	/** The wallet type. */
	WALLET_TYPE("Select At least One Wallet Type"),

	/** The serivice type. */
	SERIVICE_TYPE("Select At least One Service"),

	/** The provider name. */
	PROVIDER_NAME("Provider Name"),

	/** The grph domain name. */
	GRPH_DOMAIN_NAME("GRPH DOMAIN NAME"),

	/** The parent grph domain code. */
	PARENT_GRPH_DOMAIN_CODE("PARENT GRPH DOMAIN CODE"),

	/** The grph domain shortname. */
	GRPH_DOMAIN_SHORTNAME("GRPH DOMAIN SHORT NAME"),

	/** The description. */
	DESCRIPTION("Description"),

	/** The message code. */
	MESSAGE_CODE("MESSAGE CODE"),

	/** The preference code. */
	PREFERENCE_CODE("preferenceCode"),

	/** The default value. */
	DEFAULT_VALUE("defaultValue"),

	/** The grph domain type zone. */
	GRPH_DOMAIN_TYPE_ZONE("ZO"),

	/** The grph domain type area. */
	GRPH_DOMAIN_TYPE_AREA("AR"),

	/** The mobiquity network code. */
	MOBIQUITY_NETWORK_CODE("SN"),

	/** The multiple wallet allowed. */
	MULTIPLE_WALLET_ALLOWED("MULTIPLEWALL_TYPE"),

	/** The is topup opt multi wallet req. */
	IS_TOPUP_OPT_MULTI_WALLET_REQ("IS_TOPUP_OPT_MULTI_WALLET_REQ"),

	/** The is biller multi wallet req. */
	IS_BILLER_MULTI_WALLET_REQ("IS_BILLER_MULTI_WALLET_REQ"),

	/** The merchant domain category code. */
	MERCHANT_DOMAIN_CATEGORY_CODE("MERCHANT"),

	/** The biller category code. */
	BILLER_CATEGORY_CODE("WBILLMER"),

	/** The ui txn amount tax wallet. */
	UI_TXN_AMOUNT_TAX_WALLET("IND3WALLET"),

	/** The ui amount tax wallet. */
	UI_AMOUNT_TAX_WALLET("IND3AMT"),

	/** The gui sc amount tax wallet. */
	GUI_SC_AMOUNT_TAX_WALLET("IND3SC"),

	/** The txn amount tax wallet. */
	TXN_AMOUNT_TAX_WALLET("INDTAX01"),

	/** The sc amount tax wallet. */
	SC_AMOUNT_TAX_WALLET("INDTAX02"),

	/** The gui com amount tax wallet. */
	GUI_COM_AMOUNT_TAX_WALLET("IND3TAX"),

	/** The com amount tax wallet. */
	COM_AMOUNT_TAX_WALLET("INDTAX03"),

	/** The operator wallet no for service charge. */
	OPERATOR_WALLET_NO_FOR_SERVICE_CHARGE("IND03"),

	/** The gui expense wallet. */
	GUI_EXPENSE_WALLET("IND3EXP"),

	/** The operator wallet no for expense. */
	OPERATOR_WALLET_NO_FOR_EXPENSE("IND03B"),

	/** The mtx sequence. */
	MTX_SEQUENCE("MTX"),

	/** The append to seq. */
	APPEND_TO_SEQ("_"),

	/** The mtx sequence alter. */
	MTX_SEQUENCE_ALTER("SEQ"),

	/** The checked. */
	CHECKED("checked"),

	/** The party name prefix. */
	PARTY_NAME_PREFIX("NAME_PREFIX"),

	/** The user name length. */
	USER_NAME_LENGTH(80),

	/** The short name length. */
	SHORT_NAME_LENGTH(15),

	/** The contact no length. */
	CONTACT_NO_LENGTH(10),

	/** The proof type. */
	PROOF_TYPE("PROOF_TYPE"),

	/** The party gender. */
	PARTY_GENDER("GENDER_TYPE"),

	/** The marital status type. */
	MARITAL_STATUS_TYPE("MARITAL_STATUS"),

	/** The is agent id. */
	IS_AGENT_ID("ISAGENT"),

	/** The is merchant id. */
	IS_MERCHANT_ID("ISMERCHANT"),

	/** The username prefix id. */
	USERNAME_PREFIX_ID("userNamePrefixId"),

	/** The user name. */
	USER_NAME("userName"),

	/** The last name. */
	LAST_NAME("lastName"),

	/** The external code. */
	EXTERNAL_CODE("externalCode"),

	/** The short name. */
	SHORT_NAME("shortName"),

	/** The id type. */
	ID_TYPE("idType"),

	/** The city. */
	CITY("city"),

	/** The state. */
	STATE("state"),

	/** The country. */
	COUNTRY("country"),

	/** The designation. */
	DESIGNATION("designation"),

	/** The contact person. */
	CONTACT_PERSON("contactPerson"),

	/** The contact no. */
	CONTACT_NO("contactNo"),

	/** The contact id type. */
	CONTACT_ID_TYPE("contactIdType"),

	/** The contact id no. */
	CONTACT_ID_NO("contactIdNo"),

	/** The gender id. */
	GENDER_ID("genderId"),

	/** The marital status. */
	MARITAL_STATUS("maritalStatus"),

	/** The relationship. */
	RELATIONSHIP("relationship"),

	/** The date of birth. */
	DATE_OF_BIRTH("dateOfBirth"),

	/** The regformnum. */
	REGFORMNUM("regFormNum"),

	/** The nationality. */
	NATIONALITY("nationality"),

	/** The idissue country. */
	IDISSUE_COUNTRY("idIssueCountry"),

	/** The residence country. */
	RESIDENCE_COUNTRY("residenceCountry"),

	/** The idissue date. */
	IDISSUE_DATE("idIssueDate"),

	/** The is idexpires. */
	IS_IDEXPIRES("isIdExpires"),

	/** The idexpirydate. */
	IDEXPIRYDATE("idExpiryDate"),

	/** The postal code. */
	POSTAL_CODE("postalCode"),

	/** The employer name. */
	EMPLOYER_NAME("employerName"),

	/** The registration type. */
	REGISTRATION_TYPE("registrationType"),

	/** The merchant type. */
	MERCHANT_TYPE("merchantType"),

	/** The merchant type enum. */
	MERCHANT_TYPE_ENUM("MERCHANT_TYPE"),

	/** The supports online transaction reversalrow. */
	SUPPORTS_ONLINE_TRANSACTION_REVERSALROW("supportsOnlineTransactionReversalRow"),

	/** The is merchant. */
	IS_MERCHANT("isMerchant"),

	/** The is agent. */
	IS_AGENT("isAgent"),

	/** The merchant category. */
	MERCHANT_CATEGORY("merchantCategory"),

	/** The agent category. */
	AGENT_CATEGORY("agentCategory"),

	/** The merchant description. */
	MERCHANT_DESCRIPTION("merchantDescription"),

	/** The agent description. */
	AGENT_DESCRIPTION("agentDescription"),

	/** The show on merchant locator. */
	SHOW_ON_MERCHANT_LOCATOR("showOnMerchantLocator"),

	/** The show on agent locator. */
	SHOW_ON_AGENT_LOCATOR("showOnAgentLocator"),

	/** The latitude. */
	LATITUDE("latitude"),

	/** The longitude. */
	LONGITUDE("longitude"),

	/** The auto sweep allowed. */
	AUTO_SWEEP_ALLOWED("autoSweepAllowed"),

	/** The web login id. */
	WEB_LOGIN_ID("webLoginId"),

	/** The web pword. */
	WEB_PWORD("webPassword"),

	/** The is random pword allowd. */
	IS_RANDOM_PWORD_ALLOWD("isRandomPasswordAllowed"),

	/** The web confirm pword. */
	WEB_CONFIRM_PWORD("webConfrimPassword"),

	/** The external code length. */
	EXTERNAL_CODE_LENGTH("EXTERNAL_CODE_LENGTH"),

	/** The min external code length. */
	MIN_EXTERNAL_CODE_LENGTH("MIN_EXT_CODE_LENGTH"),

	/** The is external code required. */
	IS_EXTERNAL_CODE_REQUIRED("IS_ID_REQUIRED"),

	/** The min contactno length. */
	MIN_CONTACTNO_LENGTH("MIN_CONTACTNO_LENGTH"),

	/** The max contactno length. */
	MAX_CONTACTNO_LENGTH("MAX_CONTACTNO_LENGTH"),

	/** The app id mmoney. */
	APP_ID_MMONEY("2"),

	/** The user relations owner. */
	USER_RELATIONS_OWNER("OWNER"),

	/** The user relations parent. */
	USER_RELATIONS_PARENT("PARENT"),

	/** The normal. */
	NORMAL("NORMAL"),

	/** The commission. */
	COMMISSION("COMMISSION"),

	/** The na. */
	NA("NA"),

	/** The ntrust. */
	NTRUST("NTRUST"),

	/** The optional values length. */
	OPTIONAL_VALUES_LENGTH(50),

	/** The optional values placeid length. */
	OPTIONAL_VALUES_PLACEID_LENGTH(30),

	/** The optional values comfield length. */
	OPTIONAL_VALUES_COMFIELD_LENGTH(80),

	/** The optional values channel length. */
	OPTIONAL_VALUES_CHANNEL_LENGTH(5),

	/** The optional values dmsid length. */
	OPTIONAL_VALUES_DMSID_LENGTH(10),

	/** The optional values empcode length. */
	OPTIONAL_VALUES_EMPCODE_LENGTH(12),

	/** The optional values ssn length. */
	OPTIONAL_VALUES_SSN_LENGTH(15),

	/** The source incom. */
	SOURCE_INCOM("Source of Income"),

	/** The monthly turnover. */
	MONTHLY_TURNOVER("Monthly TurnOver"),

	/** The employee code. */
	EMPLOYEE_CODE("Employee code"),

	/** The distinct. */
	DISTINCT("Distinct"),

	/** The address1. */
	ADDRESS1("Address1"),

	/** The address2. */
	ADDRESS2("Address2"),

	/** The ssn. */
	SSN("Ssn"),

	/** The lastname2. */
	LASTNAME2("Lastname2"),

	/** The hus lastname. */
	HUS_LASTNAME("Husband Lastname"),

	/** The citizanship. */
	CITIZANSHIP("citizenship"),

	/** The placeid. */
	PLACEID("PlaceId"),

	/** The pob. */
	POB("pob"),

	/** The comfield1. */
	COMFIELD1("comField1"),

	/** The comfield2. */
	COMFIELD2("comField2"),

	/** The comfield3. */
	COMFIELD3("comField3"),

	/** The comfield4. */
	COMFIELD4("comField4"),

	/** The comfield5. */
	COMFIELD5("comField5"),

	/** The comfield6. */
	COMFIELD6("comField6"),

	/** The comfield7. */
	COMFIELD7("comField7"),

	/** The comfield8. */
	COMFIELD8("comField8"),

	/** The comfield9. */
	COMFIELD9("comField9"),

	/** The comfield10. */
	COMFIELD10("comField10"),

	/** The comfield11. */
	COMFIELD11("comField11"),

	/** The custgeo. */
	CUSTGEO("custGeo"),

	/** The supgeo. */
	SUPGEO("supGeo"),

	/** The comname. */
	COMNAME("comName"),

	/** The comname2. */
	COMNAME2("comName2"),

	/** The shopname. */
	SHOPNAME("shopName"),

	/** The avgincome. */
	AVGINCOME("AvgIncome"),

	/** The avgexp. */
	AVGEXP("avgExpense"),

	/** The avgemp. */
	AVGEMP("avgEmp"),

	/** The scmname1. */
	SCMNAME1("scmName1"),

	/** The scmname2. */
	SCMNAME2("scmName2"),

	/** The scmname3. */
	SCMNAME3("scmName3"),

	/** The scmname4. */
	SCMNAME4("scmName4"),

	/** The bankname. */
	BANKNAME("Bank Name"),

	/** The bankacco. */
	BANKACCO("bankAccno"),

	/** The channelid. */
	CHANNELID("channelId"),

	/** The dmsid. */
	DMSID("dmsId"),

	/** The comtaxid. */
	COMTAXID("comTaxId"),

	/** The personal1. */
	PERSONAL1("personal1"),

	/** The personal2. */
	PERSONAL2("personal2"),

	/** The personal3. */
	PERSONAL3("personal3"),

	/** The personal4. */
	PERSONAL4("personal4"),

	/** The contaxid. */
	CONTAXID("ContactTaxId"),

	/** The comact. */
	COMACT("ComAct"),

	/** The comtype. */
	COMTYPE("comType"),

	/** The preference codes. */
	// Identical Transaction
	PREFERENCE_CODES("Preference Codes"),

	/** The txn amt. */
	TXN_AMT("TXN_AMT"),

	/** The payee provider. */
	PAYEE_PROVIDER("PAYEE_PROVIDER"),

	/** The payer provider. */
	PAYER_PROVIDER("PAYER_PROVIDER"),

	/** The payer wallet id. */
	PAYER_WALLET_ID("PAYER_WALLET_ID"),

	/** The payee wallet id. */
	PAYEE_WALLET_ID("PAYEE_WALLET_ID"),

	/** The serv type. */
	SERV_TYPE("SERV_TYPE"),

	/** The time intrvl. */
	TIME_INTRVL("TIME_INTRVL"),

	/** The txn amt field name. */
	TXN_AMT_FIELD_NAME("Transaction Amount"),

	/** The payee msisdn field name. */
	PAYEE_MSISDN_FIELD_NAME("Payee MSISDN"),

	/** The payer msisdn field name. */
	PAYER_MSISDN_FIELD_NAME("Payer MSISDN"),

	/** The payee provider field name. */
	PAYEE_PROVIDER_FIELD_NAME("Payee Provider"),

	/** The payer provider field name. */
	PAYER_PROVIDER_FIELD_NAME("Payer Provider"),

	/** The payer wallet id field name. */
	PAYER_WALLET_ID_FIELD_NAME("Payer Wallet Id"),

	/** The payee wallet id field name. */
	PAYEE_WALLET_ID_FIELD_NAME("Payee Wallet Id"),

	/** The time intrvl field name. */
	TIME_INTRVL_FIELD_NAME("Time Interval"),

	/** The min loginid length. */
	MIN_LOGINID_LENGTH("MIN_LOGINID_LENGTH"),

	/** The max loginid length. */
	MAX_LOGINID_LENGTH("MAX_LOGINID_LENGTH"),

	/** The msisdn. */
	MSISDN("msisdn"),

	/** The min msisdn length. */
	MIN_MSISDN_LENGTH("MIN_MSISDN_LENGTH"),

	/** The max msisdn length. */
	MAX_MSISDN_LENGTH("MAX_MSISDN_LENGTH"),

	/** The preferred langauge. */
	PREFERRED_LANGAUGE("PreferredLangauge"),

	/** The allowed ip. */
	ALLOWED_IP("allowedIp"),

	/** The url. */
	URL("url"),

	/** The transaction sms. */
	TRANSACTION_SMS("Transaction SMS"),

	/** The admin email. */
	ADMIN_EMAIL("Admin Email"),

	/** The admin sms. */
	ADMIN_SMS("Admin SMS"),

	/** The allowed special chars designation or name. */
	ALLOWED_SPECIAL_CHARS_DESIGNATION_OR_NAME("' @-"),

	/** The allowed days. */
	ALLOWED_DAYS("allowedDays"),

	/** The allowed from time. */
	ALLOWED_FROM_TIME("allowedFromtime"),

	/** The allowed to time. */
	ALLOWED_TO_TIME("allowedTotime"),

	/** The network prefix length. */
	NETWORK_PREFIX_LENGTH("NETWORK_PREFIX_LENGT"),

	/** The opt prefix value. */
	OPT_PREFIX_VALUE("OPT_PREFIX_VALUE"),

	/** The opt pref transition over. */
	OPT_PREF_TRANSITION_OVER("OPT_PREFIX_TRANSITION_OVER"),

	/** The web pass min length. */
	WEB_PASS_MIN_LENGTH("WEB_PWD_MIN_LENGTH"),

	/** The web pass max length. */
	WEB_PASS_MAX_LENGTH("WEB_PWD_MAX_LENGTH"),

	/** The channel user as subs allowed. */
	CHANNEL_USER_AS_SUBS_ALLOWED("CHANNEL_USER_AS_SUBS_ALLOWED"),

	/** The is consumer portal required. */
	IS_CONSUMER_PORTAL_REQUIRED("IS_CONSUMER_PORTAL_REQUIRED"),

	/** The identical preference type. */
	IDENTICAL_PREFERENCE_TYPE("IDTXNPARAM"),

	/** The party status suspend. */
	PARTY_STATUS_SUSPEND("S"),

	/** The party status addinitiated. */
	PARTY_STATUS_ADDINITIATED("AI"),

	/** The party status updateinitiated. */
	PARTY_STATUS_UPDATEINITIATED("UI"),

	/** The party status deleteinitiated. */
	PARTY_STATUS_DELETEINITIATED("DI"),

	/** The party status suspendinitiated. */
	PARTY_STATUS_SUSPENDINITIATED("SI"),

	/** The party status resumeinitiated. */
	PARTY_STATUS_RESUMEINITIATED("RI"),

	/** The operation type add. */
	OPERATION_TYPE_ADD("add"),

	/** The operation type update. */
	OPERATION_TYPE_UPDATE("update"),

	/** The operation type delete. */
	OPERATION_TYPE_DELETE("delete"),

	/** The operation type suspend. */
	OPERATION_TYPE_SUSPEND("suspend"),

	/** The operation type resume. */
	OPERATION_TYPE_RESUME("resume"),

	/** The operation type addapproval. */
	OPERATION_TYPE_ADDAPPROVAL("mchecker"),

	/** The operation type modifyapproval. */
	OPERATION_TYPE_MODIFYAPPROVAL("machecker"),

	/** The operation type deleteapproval. */
	OPERATION_TYPE_DELETEAPPROVAL("dachecker"),

	/** The operation type suspendapproval. */
	OPERATION_TYPE_SUSPENDAPPROVAL("sachecker"),

	/** The operation type resumeapproval. */
	OPERATION_TYPE_RESUMEAPPROVAL("rachecker"),

	/** The approval limit. */
	APPROVAL_LIMIT("Approval Limit"),

	/** The zone. */
	ZONE("zone"),

	/** The user type channel. */
	USER_TYPE_CHANNEL("CHANNEL"),

	/** The id gen party. */
	ID_GEN_PARTY("PT"),

	/** The financial year start index. */
	FINANCIAL_YEAR_START_INDEX("FIN_YR_ST_INDX"),

	/** The party creation type batch. */
	PARTY_CREATION_TYPE_BATCH("B"),

	/** The saltnumber length. */
	SALTNUMBER_LENGTH(6),

	/** The default currency code. */
	DEFAULT_CURRENCY_CODE("CURRENCY_CODE"),

	/** The merchant process type. */
	MERCHANT_PROCESS_TYPE("MERPROCESSTYPE"),

	/** The is loyalty enabled. */
	IS_LOYALTY_ENABLED("IS_LOYALTY_ENABLED"),

	/** The access type phone. */
	ACCESS_TYPE_PHONE("PHONE"),

	/** The enable diff tpin and mpin. */
	ENABLE_DIFF_TPIN_AND_MPIN("ENABLE_DIFF_TPIN_AND_MPIN"),

	/** The enable mpin across bearer. */
	ENABLE_MPIN_ACROSS_BEARER("ENABLE_MPIN_ACROSS_BEARER"),

	/** The use default pin. */
	USE_DEFAULT_PIN("USE_DEFAULT_PIN"),

	/** The default reset pin. */
	DEFAULT_RESET_PIN("DEFAULT_RESET_PIN"),

	/** The enable tpin authentication. */
	ENABLE_TPIN_AUTHENTICATION("ENABLE_TPIN_AUTHENTICATION"),

	/** The pin length. */
	PIN_LENGTH("PIN_LENGTH"),

	/** The pin start index. */
	PIN_START_INDEX(2),

	/** The pin pass max length. */
	PIN_PASS_MAX_LENGTH("PIN_PWD_MAX_LENGTH"),

	/** The pin pass min length. */
	PIN_PASS_MIN_LENGTH("PIN_PWD_MIN_LENGTH"),

	/** The is random pin allowed. */
	IS_RANDOM_PIN_ALLOWED("IS_RANDOM_PIN_ALLOW"),

	/** The zero wallet balance. */
	ZERO_WALLET_BALANCE(0),

	/** The account no length. */
	ACCOUNT_NO_LENGTH("ACCOUNT_NO_LENGTH"),

	/** The min account no length. */
	MIN_ACCOUNT_NO_LENGTH("MIN_ACCOUNT_NO_LENGTH"),

	/** The account number. */
	ACCOUNT_NUMBER("Account Number"),

	/** The first name. */
	FIRST_NAME("First Name"),

	/** The agent code. */
	AGENT_CODE("agentCode"),

	/** The subscriber status. */
	SUBSCRIBER_STATUS("subscriberStatus"),

	/** The banking services for trust bank. */
	BANKING_SERVICES_FOR_TRUST_BANK("BANKING_SERVICES_FOR_TRUST_BANK"),

	/** The multiple mfs provider. */
	MULTIPLE_MFS_PROVIDER("MULTIPLE_MFS_PROVIDER"),

	/** The tcp profile id. */
	// TCP
	TCP_PROFILE_ID("Tcp ProfileId "),

	/** The default account number. */
	DEFAULT_ACCOUNT_NUMBER("12232323566"),

	/** The bank saving account code. */
	BANK_SAVING_ACCOUNT_CODE("02"),

	/** The identiy proof. */
	IDENTIY_PROOF("Identity Proof"),

	/** The identiy proof type. */
	IDENTIY_PROOF_TYPE("Identity Proof Type"),

	/** The address proof. */
	ADDRESS_PROOF("Address Proof"),

	/** The address proof type. */
	ADDRESS_PROOF_TYPE("Address Proof Type"),

	/** The photo proof. */
	PHOTO_PROOF("Photo Proof"),

	/** The photo proof type. */
	PHOTO_PROOF_TYPE("Photo Proof Type"),

	/** The file name identity. */
	FILE_NAME_IDENTITY("identity"),

	/** The file name address. */
	FILE_NAME_ADDRESS("address"),

	/** The file name photo. */
	FILE_NAME_PHOTO("photo"),

	/** The file name logo. */
	FILE_NAME_LOGO("logo"),

	/** The nick name. */
	NICK_NAME("Nick Name"),

	/** The max nickname length. */
	MAX_NICKNAME_LENGTH("MAX_NICKNAME_LENGTH"),

	/** The appointment date. */
	APPOINTMENT_DATE("Appointment Date"),

	/** The is logo upload allowed. */
	IS_LOGO_UPLOAD_ALLOWED("IS_LOGO_UPLOAD_ALLOWED"),

	/** The company logo. */
	COMPANY_LOGO("CompanyLogo"),

	/** The customer id. */
	CUSTOMER_ID("CustomerId"),

	/** The is primary account. */
	IS_PRIMARY_ACCOUNT("Is primaryAccount"),

	/** The account type. */
	ACCOUNT_TYPE("Account type"),

	/** The liquidation bank account number. */
	LIQUIDATION_BANK_ACCOUNT_NUMBER("LiquidationBankAccountNumber"),

	/** The liquidation bank account holder name. */
	LIQUIDATION_BANK_ACCOUNT_HOLDER_NAME("LiquidationBankAccountHolderName"),

	/** The liquidation bank branch name. */
	LIQUIDATION_BANK_BRANCH_NAME("LiquidationBankBranchName"),

	/** The liquidation bank id. */
	LIQUIDATION_BANK_ID("LiquidationBankId"),

	/** The frequency. */
	FREQUENCY("frequency"),

	/** The frequency trading name. */
	FREQUENCY_TRADING_NAME("frequencyTradingName"),

	/** The day ofthe week. */
	DAY_OFTHE_WEEK("dayOfTheWeek"),

	/** The month ofthe day. */
	MONTH_OFTHE_DAY("MonthOfTheDay"),

	/** The liquidation account no length. */
	LIQUIDATION_ACCOUNT_NO_LENGTH("LIQUIDATION_ACCOUNT_NO_LENGTH"),

	/** The account no postfix. */
	ACCOUNT_NO_POSTFIX("ACCOUNTPOSTFIX"),

	/** The value true. */
	VALUE_TRUE("true"),

	/** The value false. */
	VALUE_FALSE("false"),

	/** The stock via bank service. */
	STOCK_VIA_BANK_SERVICE("STCKVIABNK"),

	/** The stock o2c service. */
	STOCK_O2C_SERVICE("STKTR2OCA"),

	/** The agent code length. */
	AGENT_CODE_LENGTH("AGENT_CODE_LENGTH"),

	/** The bank assign or not. */
	BANK_ASSIGN_OR_NOT("BANK_ASSIGN_OR_NOT"),

	/** The channel user. */
	CHANNEL_USER("CHUSER"),

	/** The parent id. */
	PARENT_ID("Parent Id"),

	/** The owner id. */
	OWNER_ID("Owner Id"),

	/** The child owner id. */
	CHILD_OWNER_ID("Child Owner Id"),

	/** The child parent id. */
	CHILD_PARENT_ID("Child Parent Id"),

	/** The enterprise category code. */
	ENTERPRISE_CATEGORY_CODE("EnterPriseCategoryCode"),

	/** The bulk payment reg required. */
	BULK_PAYMENT_REG_REQUIRED("BulkPaymentegRequired"),

	/** The enterprise limit. */
	ENTERPRISE_LIMIT("Enterpriselimit"),

	/** The company code. */
	COMPANY_CODE("CompanyCode"),

	/** The bulk payer type. */
	BULK_PAYER_TYPE("bulkPayerType"),

	/** The enterprise unreg bp mode. */
	ENTERPRISE_UNREG_BP_MODE("unRegBpMode"),

	/** The web group role id. */
	WEB_GROUP_ROLE_ID("webGroupRoleId"),

	/** The enterprise category wallet. */
	ENTERPRISE_CATEGORY_WALLET("enterpriseCategoryWallet"),

	/** The id gen bp. */
	ID_GEN_BP("BP"),

	/** The min user length not in pass. */
	MIN_USER_LENGTH_NOT_IN_PASS("MIN_USER_LENGTH_NOT_IN_PASSWORD"),

	/** The liquidation bank type. */
	LIQUIDATION_BANK_TYPE("LIQUIDATION"),

	/** The user type. */
	USER_TYPE("User Type"),

	/** The superadmin category. */
	SUPERADMIN_CATEGORY("SUADM"),

	/** The netadmin. */
	NETADMIN("NWADM"),

	/** The special character password validation. */
	SPECIAL_CHARACTER_PASSWORD_VALIDATION("%,?,&,@,\\=,\\#,+,\\:,$,{,},.,^,_,,/,-,[,],*"),

	/** The contact person regexp. */
	CONTACT_PERSON_REGEXP("' @-"),

	/** The tcp type applicable. */
	TCP_TYPE_APPLICABLE("TCP_TYPE_APPLICABLE"),

	/** The tcp type rule1. */
	TCP_TYPE_RULE1("RULE1"),

	/** The tcp type rule2. */
	TCP_TYPE_RULE2("RULE2"),

	/** The tcp type rule3. */
	TCP_TYPE_RULE3("RULE3"),

	/** The party creation type individual. */
	PARTY_CREATION_TYPE_INDIVIDUAL("M"),

	/** The pass creation type. */
	PASS_CREATION_TYPE("Y"),

	/** The user code. */
	// bill pay
	USER_CODE("Bill Company Code"),

	/** The zip code. */
	ZIP_CODE("ZipCode"),

	/** The service level. */
	SERVICE_LEVEL("ServiceLevel"),

	/** The registration confirm interval. */
	REGISTRATION_CONFIRM_INTERVAL("RegistrationConfirmInterval"),

	/** The paid bill notification frequency. */
	PAID_BILL_NOTIFICATION_FREQUENCY("PaidBillNotifcationFrequency"),

	/** The auto bill deletion frequency. */
	AUTO_BILL_DELETION_FREQUENCY("AutoBillDeletionFrequency"),

	/** The process type. */
	PROCESS_TYPE("ProcessType"),

	/** The biller type str. */
	BILLER_TYPE_STR("BillerType"),

	/** The biller amount. */
	BILLER_AMOUNT("BillAmount"),

	/** The sub type. */
	SUB_TYPE("SubType"),

	/** The mobiquity id. */
	MOBIQUITY_ID("MOBIQUITY_ID"),

	/** The bill process type. */
	BILL_PROCESS_TYPE("PROCESSTYPE"),

	/** The biller type. */
	BILLER_TYPE("BILLERTYPE"),

	/** The bill amt enum typeid. */
	BILL_AMT_ENUM_TYPEID("BILLAMT"),

	/** The service levels. */
	SERVICE_LEVELS("SERVICE_LEVELS"),

	/** The biller type presentment. */
	BILLER_TYPE_PRESENTMENT("BILL_PRE"),

	/** The bill amount amt same. */
	BILL_AMOUNT_AMT_SAME("AMT_SAME"),

	/** The bill amount amt part. */
	BILL_AMOUNT_AMT_PART("AMT_PART"),

	/** The payment effected to. */
	PAYMENT_EFFECTED_TO("PaymentEffectedTo"),

	/** The payment type. */
	PAYMENT_TYPE("PAYMENT_TYPE"),

	/** The bank pay info. */
	BANK_PAY_INFO("BANK_INFO"),

	/** The merchant category code. */
	MERCHANT_CATEGORY_CODE("MerCategoryCode"),

	/** The ussd info. */
	USSD_INFO("USSD_INFO"),

	/** The is nickname required. */
	IS_NICKNAME_REQUIRED("IS_NICKNAME_REQUIRED"),

	/** The merchant domain code. */
	MERCHANT_DOMAIN_CODE("MERCHANT"),

	/** The trf cntrl profile type role. */
	TRF_CNTRL_PROFILE_TYPE_ROLE("ROLE"),

	/** The biller id. */
	// Biller
	BILLER_ID("Biller Id"),

	/** The biller valid serial no. */
	BILLER_VALID_SERIAL_NO("Biller Validation Serail Number"),

	/** The biller valid label. */
	BILLER_VALID_LABEL("Biller Validation Label"),

	/** The biller valid type. */
	BILLER_VALID_TYPE("Biller Validation Type"),

	/** The biller valid min. */
	BILLER_VALID_MIN("Biller Validation min value"),

	/** The biller valid max. */
	BILLER_VALID_MAX("Biller Validation max value"),

	/** The biller valid startwith. */
	BILLER_VALID_STARTWITH("Biller Validation start with value"),

	/** The biller valid contains. */
	BILLER_VALID_CONTAINS("Biller Validation contains"),

	/** The biller valid ref type. */
	BILLER_VALID_REF_TYPE("Biller Validation Ref Type"),

	/** The value type alpha. */
	VALUE_TYPE_ALPHA("A"),

	/** The value type nuemeric. */
	VALUE_TYPE_NUEMERIC("NM"),

	/** The value type alphaonly. */
	VALUE_TYPE_ALPHAONLY("AL"),

	/** The value type rupees. */
	VALUE_TYPE_RUPEES("RS"),

	/** The value type num special chars. */
	VALUE_TYPE_NUM_SPECIAL_CHARS("NS"),

	/** The multiplication factor for amount. */
	MULTIPLICATION_FACTOR_FOR_AMOUNT("CURRENCY_FACTOR"),

	/** The service type billpay. */
	SERVICE_TYPE_BILLPAY("BILLPAY"),

	/** The service type rc. */
	SERVICE_TYPE_RC("RC"),

	/** The user code ptups. */
	USER_CODE_PTUPS("PTUPS"),

	/** The bill not paid. */
	BILL_NOT_PAID("N"),

	/** The from date. */
	FROM_DATE("from date"),

	/** The to date. */
	TO_DATE("to date"),

	/** The page no. */
	PAGE_NO("Page No"),

	/** The page size. */
	PAGE_SIZE("Page Size"),

	/** The user type subscriber. */
	USER_TYPE_SUBSCRIBER("SUBSCRIBER"),

	/** The empty. */
	EMPTY("empty"),

	/** The invlid date. */
	INVLID_DATE("invaliddate"),

	/** The invlid amount. */
	INVLID_AMOUNT("invalidamount"),

	/** The category code subs. */
	CATEGORY_CODE_SUBS("SUBS"),

	/** The bill archive file name. */
	BILL_ARCHIVE_FILE_NAME("BILL_ARCHIVE"),

	/** The export directory path. */
	EXPORT_DIRECTORY_PATH("CORE_CONFIG_EXPORT_DIRECTORY_PATH"),

	/** The bill archive on deletion directory name. */
	BILL_ARCHIVE_ON_DELETION_DIRECTORY_NAME("BILL_ARCHIVE_ON_DELETION"),

	/** The default archive file extension. */
	DEFAULT_ARCHIVE_FILE_EXTENSION(".csv"),

	/** The file name separator. */
	FILE_NAME_SEPARATOR("_"),

	/** The flag directory name. */
	FLAG_DIRECTORY_NAME("FLAG"),

	/** The premium servicelevel. */
	PREMIUM_SERVICELEVEL("SL1"),

	/** The standard servicelevel. */
	STANDARD_SERVICELEVEL("SL2"),

	/** The both servicelevel. */
	BOTH_SERVICELEVEL("SL3"),

	/** The adhoc servicelevel. */
	ADHOC_SERVICELEVEL("SL4"),

	/** The process type real. */
	PROCESS_TYPE_REAL("REAL"),

	/** The allowed acc no expression. */
	ALLOWED_ACC_NO_EXPRESSION("^[a-zA-Z0-9.\\\\-@\\\\\\\\ ]*$"),

	/** The supports online transaction reversal. */
	SUPPORTS_ONLINE_TRANSACTION_REVERSAL("supportsOnlineTransactionReversal"),

	/** The division. */
	DIVISION("Division"),

	/** The department. */
	DEPARTMENT("Department"),
	/** The department. */
	ENTERPRISE_BP_CATEGORY_CODE("SUBS"),
	/** The ENTERPRISE_SALARY_WALLET_TYPE. */
	ENTERPRISE_SALARY_WALLET_TYPE("12"),
	/** The ENTERPRISE_SALARY_WALLET_TYPE. */
	SERVICE_ROLE_CODE("SERVICE_ROLE_CODE"),
	/** The ENTERPRISE_SALARY_WALLET_TYPE. */
	SMS_MESSAGE("smsMessage"),
	/** The ENTERPRISE_SALARY_WALLET_TYPE. */
	SMS_MESSAGE_CODE("smsMessageCode"),
	/** The ENTERPRISE_SALARY_WALLET_TYPE. */
	EMAIL_MESSAGE("emailMessage"),
	/** The ENTERPRISE_SALARY_WALLET_TYPE. */
	EMAIL_SUBJECT("emailSubject"), FROM_MAIL_ID("FROM_MAIL_ID"),

	FORMAT("HTML/TEXT"),

	INITIAL_RETRY(0), SYSTEM("SYSTEM"),

	GATEWAY_TYPE_SMSC("SMSC"),

	GATEWAY_CODE("gatewayCode"),

	VMS_SYSTEM_PREFERENCES("system_preferences"),

	VMS_SYSTEM_PREFERENCES_CODE("system preferences Code"),

	VMS_RESPONSE_MSG("RESPONSE_MSG"),

	VMS_LOCAL_MASTER_LIST("LOCAL_MASTER_LIST"),

	VMS_LOCAL_MESSAGEGATEWAY("LOCAL_MESSAGEGATEWAY"),
	
	VMS_LOCAL_MESSAGEGATEWAYMAPPING("LOCAL_MESSAGEGATEWAYMAPPING"),

	VMS_LOCAL_LOADLOOKUPS("LOCAL_LOADLOOKUPS"),

	/** User Login Validations */
	USER_LOGIN_ID("User LoginId"),

	VALIDATION_TYPE("validation Type"),

	MRP("mrp"),

	INPUT_VALIDATIONPARAM("inputValidation Param"),

	USER_PASSWORD("User Password"),

	USER_LANGUAGE("Language"),

	USER_FIRSTLOGINJTI("Fisrt login jti"),

	REQUEST_GATEWAY_TYPE("Request Gateway Type"),

	REQUEST_GATEWAY_CODE("Request Gateway Code"),
	
	REQUEST_GATEWAY_LOGING_ID("Request Gateway Login Id"),
	
	REQUEST_GATEWAY_PASSWORD("Request Gateway Password"),
	
	REQUEST_GATEWAY_SERVERPORT("Server Post"),

	AUTHENDICATION_ERROR("Authentication Failure"),

	PASSWORD_BLOCKED("Your password has been blocked"),

	WEB_INTERFACE_NOT_ALLOWED("can't access web interface"),

	PARTY_STATUS_SUSPEND_NETWORK("Network Suspended"),

	USER_STATUS_CANCELED("C"),

	USER_STATUS_DELETED("N"),

	ROOT_PARENT_ID("ROOT"),

	STAFF_USER_TYPE("STAFF"),

	USER_STATUS_TYPE("URTYP"),

	LOG_TYPE_LOGIN("LOGIN"),

	USER_STATUS_NEW("W"),

	USER_STATUS_APPROVED("A"),

	DOMAIN_SUSPENDED("User domain is suspended"),

	USER_CATEGORY_SUSPENDED("User category is suspended"),

	USER_APPROVAL_PENDING("User approval is still pending"),

	USER_STATUS_SUSPEND("S"),

	PARENT_USER_REQUESTED_SUSPEND("User status is Suspend request (Parent user has sent a request for suspension)"),

	USER_STATUS_BLOCK("User status is blocked"),

	USER_STATUS_BLOCKED("B"),

	USER_STATUS_DEREGISTERED("D"),

	USER_STATUS_DEREGISTER("User status is de-registered"),

	USER_STATUS_SUSPEND_REQUEST("SR"),

	USER_STATUS_SUSPEND_REQUESTED("SR"),

	MENUBL_ROLE_ASSIGNMENT_FIXED("F"), MENUBL_ROLE_ASSIGNMENT_ASSIGNED("A"),

	PERSIAN("persian"),

	LOCALE_PERSIAN("fa_IR@calendar=persian"),

	USER_PASSWORD_BLOCKED("Your password has been blocked, please contact customer care"),

	SUPER_ADMIN("SUADM"),

	SUPER_NETWORK_ADMIN("SUNADM"), SUPER_CHANNEL_ADMIN("SUBCU"),

	SUPER_CUSTOMER_CARE("SUCCE"), STATUS_DELETE("N"),

	STATUS_SUSPEND("S"), NETWORK_NAME_DEFAULT("Not Applicable"),

	VMS_LOCAL_MASTER_BYCNTRYLANG("VMS_LOCAL_MASTER_BYCNTRYLANG"),

	DEFAULT_LANGUAGE("en"),

	DEFAULT_COUNTRY("US"),

	LANG1_MESSAGE("LANG1"),

	ROLE_ACCESS_TYPE_BOTH("B"),

	PRODUCT_TYPE("PDTYP"),

	LOOKUP_VOUCHER_STATUS("VSTAT"),

	FIXED("F"),

	ASSIGNED("A"),

	KEY("981AFA8CDEB2A0F7E0A011B557BB08CF"),
	

	CREDENTIALSFILE("Credentials.txt"),

	AES_ERROR_CODES(3002),

	INVALID_JWT_TOKEN("INVJWT001"),

	CIPHER_ENCRYPT("ENCRYPT"),

	CIPHER_DECRYPT("DECRYPT"),

	PONG("PONG"),

	ONE("1"),

	NAME_EXIST("NAME"),

	SHORTNAME_EXIST("SHORTNAME"),

	MRP_EXIST("MRP"),

	USERLOGINIDPARAM("userLoginId"),

	TWO("2"),

	TYPE("Type"),

	Authorization("Authorization"),

	extractUserID("extractUserID"),

	DENOMINATION_VALUE("Denomination"),

	PAYABLEAMOUNT_VALUE("Payable Amount"),

	FREQUENCY_MINUTS("MINUTES"),

	FREQUENCY_HOUR("HOUR"),

	FREQUENCY_DAY("DAY"),

	FREQUENCY_MONTH("MONTH"),

	FREQUENCY_YEAR("YEAR"),

	CATEGORY_ID_TYPE("EVDCATID"),

	PRODUCT_ID_TYPE("VOMSPRID"),

	CATEGORY_ID("Category Id"),

	EVD_CATEGORY_TYPE_FIXED("CFIX"),

	CIPHER_INSTANCE("AES/CBC/PKCS5Padding"),
	
	PRODUCTID("productId"),

	UTF_8("UTF-8"),

	ENGLISH("en"),

	FRENCH("fr"),

	ARABIC("ar"),

	NetowkLevel("NL"),

	CategoryLevel("CL"),

	DENOMINATION_ID("Denomination ID"),

	USERINPUT_ID("User ID"),

	DENOMINATION_NAME("Denomination Name"),

	DENOM_SHORT_NAME("Denomination short Name"),


    VOUCHER_BATCH_LIST_EMPTY("VOUCHER_BATCH_LIST_EMPTY"),
    
    VOMS_BATCH_ID_PAD_LENGTH(4),
    REVOKETOKEN("accessToken"),
    PRNOTFOUND("PRNOTFOUND"),
    STATUS_COMPLETE("C"),
    STATUS_UNDERPROCESS("U"),
    PROCESS_ERROR_UPDATE_STATUS("PROCESS_ERROR_UPDATE_STATUS"),
    PROCESS_UNDER_PROCESS("PROCESS_UNDER_PROCESS"),
    PROCESS_ALREADY_RUNNING("PROCESS_ALREADY_RUNNING"),
    VOUCHER_SEGMENT_NATIONAL("NL"),
    
    VOUCHER_TYPE("VOUCHERTYPE"),
    
    VOUCHER_SEGMENT("VOUCHERSEGMENT"),
    
    PROFILE_NAME("Profile Name"),
    
    PROFILE_SHORT_NAME("Profile Short Name"),
    
    AUTO_GENERATE("Auto generate"),
    BATCH_INVALID_COMBINATION ("BATCH_INVALID_COMBINATION"),
    
    BASIC("Basic "),
    
    APPLICATION_URLENCODE("application/x-www-form-urlencoded"),
    
    CONTENT_TYPE("Content-Type"),
    postgresDialect("org.hibernate.dialect.PostgreSQLDialect"),
    oracleDialect("org.hibernate.dialect.Oracle10gDialect"),
    
    
    SCHEDULED("SC"),
    BATCH_PROCESS_CHANGE("CHANGESTATUS"),
    
    VOMS_BATCHES_DOC_TYPE("VMBTCHUD"), 

    NOTAPPLICABLE("NA "),
    VMSSEG("VMSSEG"),
    
	KEY_PASS("KEY_PASS"),

	KEY_STORE_NAME("KEY_STORE_NAME"),

	STORE_PASS("STORE_PASS"),

	SECRET_KEY_NAME("SECRET_KEY_NAME"),

	S_NO("S_NO"),

	IS_SPRING_ENABLE("IS_SPRING_ENABLE"),

	EXPIRY_PERIOD_EXPIRY_DATE("expiry period or expiry date"),

	EXPIRY_DATE("expiry date"),

	PROFILE_DESCRIPTION("255"),

	VOUCHER_THRESHOLD("Voucher Threshold Value"),

	MAX_REQ_QUANTITY("Maximum Required Quantity Exceeds defined Limit"),

	DATE_FORMAT("dd/MM/yy"),

	SERVICE_ID("Service id"),

	VOUCHER_TYPE_TEST_DIGITAL("DT"),

	VOUCHER_TYPE_TEST_ELECTRONIC("ET"),

	VOUCHER_TYPE_TEST_PHYSICAL("PT"),

	VOUCHER_TYPE_DIGITAL("D"),

	VOUCHER_TYPE_ELECTRONIC("E"),

	VOUCHER_TYPE_PHYSICAL("P"),

	 APPROVE("APPROVE"), REJECT("REJECT"),
	 
	 
	 BATCH_PROCESS_ENABLE("ENABLE"),
	 
	 VOMS_MAX_ERROR_COUNTEN(0),

	 VOMS_MAX_ERROR_COUNTOTH(0),
	 
CHANGESTATUS("ChangeStatus"),
ROWNO("rowNo"),
QUANTITY("Quantity"),
FILEAttachment("File Attachment"),
NetworkCode("Network Code"),
BATCHNO("Batch no"),
LEVELAPPROVER("Level approver"),
ApproverLevel("Approvel level"),
SYSPREF001("SYSPREF001"),
SYSPREFKEYS("SYSPREFKEYS"),
VMS_MODULE_PREFMAP("VMS_MODULE_PREFMAP"),
SERIALNO("SERIALNO"),
NOATTCHMNT01("NOATTCHMNT01"),
FILEATTACHMENT("FILEATTACHEMENT"),

LANGUAGES_RTL_DIRECTION("LANGUAGES_RTL_DIRECTION"),
RIGHT_CLICK_ENABLE("RIGHT_CLICK_ENABLE"),

TALK_TIME("Talk Time"),
VALIDITY("Validity"),

VOUCHER_GENERATE_QTY("Voucher Generate Quantity"),
MAILSENDROLE("APP1VOMS"),
MAILSUBJECTKEY("voucher.generation.notification.subject.approve"),
VMS_NETWORK_PREFERENCES("VMS_NETWORK_PREFERENCES"),
POSTGRES_DRIVER("org.postgresql.Driver"),

MULTI_VALIDATION_ERROR("MULTI_VALIDATION_ERROR"),

VOUCHERBATCHRECORD("VoucherBatchRecord"),
VOUCHERAPROVALRECORD("VoucherApprovalRecord"),
APPROVALLEVEL("Approval level"),
QUANTITY_VAL("Quantity"),

DEFAULT_THREAD_POOL_COUNT(30),

STRING_VALUE("String Value"),

VOUCHERENQUIRYREQ("VoucherEnquiryReq"),
MANDATORY_FIELD("M"),
LABEL_NAME("lableName"),
PERSISTENCE_LOCK_TIMEOUT(500),

ADDDENOMINATIONVO("AddDenominationVO"),

DENOMINATIONNAME("denominationName"),

DENOMINATIONSHORTNAME("shortName"),

DENOMINATIONVALUE("denomination"),


PAYABLEAMOUNT("payableAmount"),
NUMBER_OF_RECORDS_ON_PAGE("NUMBER_OF_RECORDS_ON_PAGE"),


DENOMINATIONVALUE1("denominationValue"),



DENOMINATIONID("denominationID"),

MODIFYDENOMINATION("ModifyDenomination"),


ADDPROFILEVO("AddProfileVO"),

PROFILENAME("profileName"),

PROFILES_SHORT_NAME("profileShortName"),


VOUCHERUPLOADREQUEST("VoucherUploadRequest"),
	 
MODIFYPROFILEVO("ModifyProfileVO"),

THREADPOOLEXE_POLLSIZE("THREADPOOLEXE_POLLSIZE"),

VOUCHER_GENERATE_QUANTITY("Voucher Generate Quantity"),
VMSPATH("/v1/**"),
JWT_KEYSTORE_KEY("981AFA8CDEB2A0F7E0A011B557BB08CF"),
JWT_KEYPAIR_KEY("981AFA8CDEB2A0F7E0A011B557BB08CF"),
CHECKSUM_KEY("checkSum"),
CHECKSUM_FIELD("CheckSum"),
FILE_NAME("File name"),
SECURITYCONSTANTSPROPS("SecurityConstants"),
UNDERSCORE("_"),
DOTPROPS(".props"),
Parameter_USERLANG("language"),
VMS_REGEX_LANGUAGE_PROPS("VMS_REGEX_LANGUAGE_PROPS"),
LOGIN_URL("login"),

IDENTIFIER_PASS("identifier Password"),

IDENTIFIER_TYPE_LOGINID("loginid"),

IDENTIFIER_TYPE_MSISDN("msisdn"),

IDENTIFIER_TYPE_EXTGW("externalCode"),

PARAMETER_CHECKSUM("checkSum"),

VMS_CONTROL_PREFERENCES("VMS_CONTROL_PREFERENCES"),

GETLOCALELIST_URL("getLocaleList"),

RENEWTOKEN_URL("renewtoken"),

ENCRYPT_API_URL("encrypt"),

GENERATETOKEN_URL("generateTokenApi"),

VALIDATIONRULEPROPS("ValidationRule.props"),

SECRET_KEYNAME("SecretKeyName"),

KEYPASS("KeyPass"),

STOREPASS("StorePass"),

APPROVAL2ROLE("R_VOMSGENVOUAPPR2"),
APPROVAL3ROLE("R_VOMSGENVOUAPPR3"),
FAILED("FAILED"),
APPROVED("APPROVED"), REJECTED("REJECTED"),
STARTEXECUTION("sequence.number.generation.execution.time"),
/** The endexecution. */
ENDEXECUTION("sequence.number.generation.requests"),
LOOKUPTYPE_BSTAT("BSTAT"),
MODIFY_REQUEST("Modify Request data "),
DOUBLE_QUOTES("''"),
SINGLE_QUOTES( "'"),

DOU_QUO(""),
SHA("SHA"),
KEY_STORE_FILENAME("KeyStoreName"),
AES("AES"),
BLACKLIST_JWT_TOKEN("BLACKLIST_JWT_TOKEN"),

PINNUMBER("pinNumer"),
SERVER_HOME("SERVER_HOME"),
PESTATUS("pestatus"),
	GROUPROLE_SUSPENDED("No grouprole is assigned"),
VMS_LOCAL_MASTER_BYLANGCODE("VMS_LOCAL_MASTER_BYLANGCODE")

	

;

	/** The str value. */
	private String strValue;

	/** The int value. */
	private int intValue;

	/**
	 * Construct String Constant.
	 *
	 * @param strValue - strValue
	 */
	Constants(String strValue) {
		this.strValue = strValue;
	}

	/**
	 * Construct Intger Constant.
	 *
	 * @param intValue - intValue
	 */
	Constants(int intValue) {
		this.intValue = intValue;
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
