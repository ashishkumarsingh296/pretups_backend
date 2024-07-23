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

// TODO: Auto-generated Javadoc
/**
 * This Enum constants used for Message codes define in MFS system.
 * 
 * @author sudharshans
 */

/**
 * Gets the str value.
 *
 * @return the str value
 */

/**
 * Gets the str value.
 *
 * @return the str value
 */
@Getter
public enum MessageCodes {

	/** The success. */
	SUCCESS("200"),

	/** The bad request. */
	BAD_REQUEST("400"),

	/** The generic error. */
	GENERIC_ERROR("1035"),

	IDGEN_ERROR("IDGENERR001"),

	/** The wrong bearer code. */
	WRONG_BEARER_CODE("3000"),

	/** The requester not found. */
	REQUESTER_NOT_FOUND("99442"),

	/** The p dec exception. */
	P_DEC_EXCEPTION("01037"),

	/** The user suspended. */
	USER_SUSPENDED("99018"),

	/** The invalid p. */
	INVALID_P("00412"),

	/** The user not found. */
	USER_NOT_FOUND("2100"),

	/** The user not authorized. */
	USER_NOT_AUTHORIZED("01013"),

	/** The kafka unable send. */
	KAFKA_UNABLE_SEND("5999"),

	/** The field mandatory. */
	FIELD_MANDATORY("FV0001"),

	/** The field alphanumeric. */
	FIELD_ALPHANUMERIC("FV0002"),

	/** The no approval pending. */
	NO_APPROVAL_PENDING("FV0003"),

	/** The under approval. */
	UNDER_APPROVAL("FV0004"),

	/** The field invalid lenghth. */
	FIELD_INVALID_LENGHTH("FV0005"),

	/** The field not unique. */
	FIELD_NOT_UNIQUE("FV0006"),

	/** The field not found. */
	FIELD_NOT_FOUND("FV0007"),

	/** The field positive integer. */
	FIELD_POSITIVE_INTEGER("FV0008"),

	/** The field numeric. */
	FIELD_NUMERIC("FV0009"),

	/** The field invalid. */
	FIELD_INVALID("FV0010"),

	/** The field invalid nemeric. */
	FIELD_INVALID_NEMERIC("FV0011"),

	/** The field invalid alphanemeric. */
	FIELD_INVALID_ALPHANEMERIC("FV0012"),

	/** The list empty. */
	LIST_EMPTY("FV0013"),

	/** The field invalid alphabets. */
	FIELD_INVALID_ALPHABETS("FV0014"),

	/** The field alphanumeric with some specialchar. */
	FIELD_ALPHANUMERIC_WITH_SOME_SPECIALCHAR("FV0015"),

	/** The field uppercase. */
	FIELD_UPPERCASE("FV0016"),

	/** The value in delete initiated state. */
	VALUE_IN_DELETE_INITIATED_STATE("FV0017"),

	/** The field range invalid. */
	FIELD_RANGE_INVALID("FV00018"),

	/** The externalid exception. */
	EXTERNALID_EXCEPTION("EXT001"),

	/** The category add init success. */
	CATEGORY_ADD_INIT_SUCCESS("CAT001"),

	/** The category add appr success. */
	CATEGORY_ADD_APPR_SUCCESS("CAT002"),

	/** The category add created success. */
	CATEGORY_ADD_CREATED_SUCCESS("CAT012"),

	/** The category update init success. */
	CATEGORY_UPDATE_INIT_SUCCESS("CAT003"),

	/** The category update appr success. */
	CATEGORY_UPDATE_APPR_SUCCESS("CAT004"),

	/** The category delete init success. */
	CATEGORY_DELETE_INIT_SUCCESS("CAT005"),

	/** The category delete appr success. */
	CATEGORY_DELETE_APPR_SUCCESS("CAT006"),

	/** The category reject success. */
	CATEGORY_REJECT_SUCCESS("CAT007"),

	/** The category already exists. */
	CATEGORY_ALREADY_EXISTS("CAT008"),

	/** The category code not found. */
	CATEGORY_CODE_NOT_FOUND("CAT009"),

	/** The category code domain code limit. */
	CATEGORY_CODE_DOMAIN_CODE_LIMIT("CAT010"),

	/** The category initiate limit exceeed. */
	CATEGORY_INITIATE_LIMIT_EXCEEED("CAT011"),

	/** The category and parent category same. */
	CATEGORY_AND_PARENT_CATEGORY_SAME("CAT016"),

	/** The grade add init success. */
	GRADE_ADD_INIT_SUCCESS("GRA001"),

	/** The grade add appr success. */
	GRADE_ADD_APPR_SUCCESS("GRA002"),

	/** The grade add created success. */
	GRADE_ADD_CREATED_SUCCESS("GRA014"),

	/** The grade update init success. */
	GRADE_UPDATE_INIT_SUCCESS("GRA003"),

	/** The grade update appr success. */
	GRADE_UPDATE_APPR_SUCCESS("GRA004"),

	/** The grade delete init success. */
	GRADE_DELETE_INIT_SUCCESS("GRA005"),

	/** The grade delete appr success. */
	GRADE_DELETE_APPR_SUCCESS("GRA006"),

	/** The grade reject success. */
	GRADE_REJECT_SUCCESS("GRA007"),

	/** The grade already exists. */
	GRADE_ALREADY_EXISTS("GRA008"),

	/** The grade code not found. */
	GRADE_CODE_NOT_FOUND("GRA009"),

	/** The no grades found with category. */
	NO_GRADES_FOUND_WITH_CATEGORY("GRA010"),

	/** The grade name already exists. */
	GRADE_NAME_ALREADY_EXISTS("GRA011"),

	/** The grade under aproval. */
	GRADE_UNDER_APROVAL("GRA012"),

	/** The gradecode categorycode not match. */
	GRADECODE_CATEGORYCODE_NOT_MATCH("GRA013"),

	/** The no active grade found. */
	NO_ACTIVE_GRADE_FOUND("GRA015"),

	/** The domain name alphanumeric. */
	DOMAIN_NAME_ALPHANUMERIC("DOM04"),

	/** The num of cat numeric. */
	NUM_OF_CAT_NUMERIC("DOM06"),

	/** The domain code already exists. */
	DOMAIN_CODE_ALREADY_EXISTS("DOM07"),

	/** The cat num not valid. */
	CAT_NUM_NOT_VALID("DOM08"),

	/** The domain name not unique. */
	DOMAIN_NAME_NOT_UNIQUE("DOM09"),

	/** The domain not found. */
	DOMAIN_NOT_FOUND("DOM10"),

	/** The domain alreday init. */
	DOMAIN_ALREDAY_INIT("DOM12"),

	/** The domain code not delete initiated. */
	DOMAIN_CODE_NOT_DELETE_INITIATED("DOM13"),

	/** The domain code not update initiated. */
	DOMAIN_CODE_NOT_UPDATE_INITIATED("DOM14"),

	/** The no active domain found. */
	NO_ACTIVE_DOMAIN_FOUND("DOM15"),

	/** The domain have assigned categories. */
	DOMAIN_HAVE_ASSIGNED_CATEGORIES("DOM16"),

	/** The domain name length invalid. */
	DOMAIN_NAME_LENGTH_INVALID("DOM18"),

	/** The domain add init success. */
	DOMAIN_ADD_INIT_SUCCESS("DOM19"),

	/** The domain approve success. */
	DOMAIN_APPROVE_SUCCESS("DOM20"),

	/** The domain reject success. */
	DOMAIN_REJECT_SUCCESS("DOM21"),

	/** The domain delete init success. */
	DOMAIN_DELETE_INIT_SUCCESS("DOM23"),

	/** The domain delete appr success. */
	DOMAIN_DELETE_APPR_SUCCESS("DOM24"),

	/** The domain update init success. */
	DOMAIN_UPDATE_INIT_SUCCESS("DOM25"),

	/** The domain update appr success. */
	DOMAIN_UPDATE_APPR_SUCCESS("DOM26"),

	/** The domain no of cat invalid. */
	DOMAIN_NO_OF_CAT_INVALID("DOM27"),

	/** The domain created success. */
	DOMAIN_CREATED_SUCCESS("DOM28"),

	/** The group role init success. */
	GROUP_ROLE_INIT_SUCCESS("GRPR001"),

	/** The group role add appr success. */
	GROUP_ROLE_ADD_APPR_SUCCESS("GRPR002"),

	/** The group role update init success. */
	GROUP_ROLE_UPDATE_INIT_SUCCESS("GRPR003"),

	/** The group role delete init success. */
	GROUP_ROLE_DELETE_INIT_SUCCESS("GRPR005"),

	/** The group role delete appr success. */
	GROUP_ROLE_DELETE_APPR_SUCCESS("GRPR006"),

	/** The group role reject success. */
	GROUP_ROLE_REJECT_SUCCESS("GRPR007"),

	/** The group role already exists. */
	GROUP_ROLE_ALREADY_EXISTS("GRPR008"),

	/** The group role code not found. */
	GROUP_ROLE_CODE_NOT_FOUND("GRPR009"),

	/** The group role update appr success. */
	GROUP_ROLE_UPDATE_APPR_SUCCESS("GRPR010"),

	/** The group role under aproval. */
	GROUP_ROLE_UNDER_APROVAL("GRPR012"),

	/** The group role code user association. */
	GROUP_ROLE_CODE_USER_ASSOCIATION("GRPR013"),

	/** The group role add created success. */
	GROUP_ROLE_ADD_CREATED_SUCCESS("GRPR014"),

	/** The group role name already exists. */
	GROUP_ROLE_NAME_ALREADY_EXISTS("GRPR015"),

	/** The group role delete success. */
	GROUP_ROLE_DELETE_SUCCESS("GRPR016"),

	/** The no records found. */
	NO_RECORDS_FOUND("GEN001"),

	/** The database exception. */
	DATABASE_EXCEPTION("DB001"),

	/** The transfer rule add init success. */
	// O2C messages
	TRANSFER_RULE_ADD_INIT_SUCCESS("TR001"),

	/** The transfer rule add appr success. */
	TRANSFER_RULE_ADD_APPR_SUCCESS("TR002"),

	/** The transfer rule update init success. */
	TRANSFER_RULE_UPDATE_INIT_SUCCESS("TR003"),

	/** The transfer rule update appr success. */
	TRANSFER_RULE_UPDATE_APPR_SUCCESS("TR004"),

	/** The transfer rule delete init success. */
	TRANSFER_RULE_DELETE_INIT_SUCCESS("TR005"),

	/** The transfer rule delete appr success. */
	TRANSFER_RULE_DELETE_APPR_SUCCESS("TR006"),

	/** The transfer rule reject success. */
	TRANSFER_RULE_REJECT_SUCCESS("TR007"),

	/** The transfer rule already exists. */
	TRANSFER_RULE_ALREADY_EXISTS("TR008"),

	/** The transfer rule under aproval. */
	TRANSFER_RULE_UNDER_APROVAL("TR009"),

	/** The transfer rule not found. */
	TRANSFER_RULE_NOT_FOUND("TR010"),

	/** The transfer rule add create success. */
	TRANSFER_RULE_ADD_CREATE_SUCCESS("TR011"),

	/** The transfer rule update success. */
	TRANSFER_RULE_UPDATE_SUCCESS("TR012"),

	/** The transfer rule add create success. */
	TRANSFER_RULE_SUSPEND_ERROR("TR013"),

	/** The transfer rule add create success. */
	TRANSFER_RULE_RESUME_ERROR("TR014"),

	/** The tcp add init success. */
	TCP_ADD_INIT_SUCCESS("TCP001"),

	/** The tcp add appr success. */
	TCP_ADD_APPR_SUCCESS("TCP002"),

	/** The tcp update init success. */
	TCP_UPDATE_INIT_SUCCESS("TCP003"),

	/** The tcp update appr success. */
	TCP_UPDATE_APPR_SUCCESS("TCP004"),

	/** The tcp delete init success. */
	TCP_DELETE_INIT_SUCCESS("TCP005"),

	/** The tcp delete appr success. */
	TCP_DELETE_APPR_SUCCESS("TCP006"),

	/** The tcp reject success. */
	TCP_REJECT_SUCCESS("TCP007"),

	/** The tcp already exists. */
	TCP_ALREADY_EXISTS("TCP008"),

	/** The tcp under aproval. */
	TCP_UNDER_APROVAL("TCP009"),

	/** The tcp not found. */
	TCP_NOT_FOUND("TCP010"),

	/** The no active instrument tcp found. */
	NO_ACTIVE_INSTRUMENT_TCP_FOUND("TCP011"),

	/** The tcp customer already exists. */
	TCP_CUSTOMER_ALREADY_EXISTS("TCP012"),

	/** The tcp category no sysgroup. */
	TCP_CATEGORY_NO_SYSGROUP("TCP013"),

	/** The tcp profilename found. */
	TCP_PROFILENAME_FOUND("TCP014"),

	/** The no registration type found. */
	NO_REGISTRATION_TYPE_FOUND("TCP015"),

	/** The no customer tcp category. */
	NO_CUSTOMER_TCP_CATEGORY("TCP016"),

	/** The weekly amount greater than monthly. */
	WEEKLY_AMOUNT_GREATER_THAN_MONTHLY("TCP017"),

	/** The daily count greater than weekly. */
	DAILY_COUNT_GREATER_THAN_WEEKLY("TCP018"),

	/** The weekly count greater than monthly. */
	WEEKLY_COUNT_GREATER_THAN_MONTHLY("TCP019"),

	/** The daily amount greater than weekly. */
	DAILY_AMOUNT_GREATER_THAN_WEEKLY("TCP021"),

	/** The cust tcp all. */
	CUST_TCP_ALL("TCP020"),

	/** The no active instrumenttcp not founded. */
	NO_ACTIVE_INSTRUMENTTCP_NOT_FOUNDED("TCP022"),

	/** The instrumenttcp threshhold size invalid. */
	INSTRUMENTTCP_THRESHHOLD_SIZE_INVALID("TCP023"),

	/** The tcp customer nogrouprole exists. */
	TCP_CUSTOMER_NOGROUPROLE_EXISTS("TCP024"),

	/** The tcp add success. */
	TCP_ADD_SUCCESS("TCP0025"),

	/** The wallet add success. */
	// WALLET
	WALLET_ADD_SUCCESS("WAL001"),

	/** The wallet add limi exist. */
	WALLET_ADD_LIMI_EXIST("WAL002"),

	/** The wallet add name exist. */
	WALLET_ADD_NAME_EXIST("WAL003"),

	/** The wallet update success. */
	WALLET_UPDATE_SUCCESS("WAL004"),

	/** The wallet delete associate provider error. */
	WALLET_DELETE_ASSOCIATE_PROVIDER_ERROR("WAL005"),

	/** The wallet delete master data error. */
	WALLET_DELETE_MASTER_DATA_ERROR("WAL006"),

	/** The wallet default delete error. */
	WALLET_DEFAULT_DELETE_ERROR("WAL007"),

	/** The wallet error delete. */
	WALLET_ERROR_DELETE("WAL008"),

	/** The wallet delete success. */
	WALLET_DELETE_SUCCESS("WAL009"),

	/** The wallet preference add success. */
	// Wallet Preference
	WALLET_PREFERENCE_ADD_SUCCESS("WALP01"),

	/** The pseudo categories add init success. */
	// PSEUDO CATEGORIES
	PSEUDO_CATEGORIES_ADD_INIT_SUCCESS("PCAT001"),

	/** The pseudo categories add appr success. */
	PSEUDO_CATEGORIES_ADD_APPR_SUCCESS("PCAT002"),

	/** The pseudo categories added success. */
	PSEUDO_CATEGORIES_ADDED_SUCCESS("PCAT003"),

	/** The pseudo categories update init success. */
	PSEUDO_CATEGORIES_UPDATE_INIT_SUCCESS("PCAT004"),

	/** The pseudo categories update appr success. */
	PSEUDO_CATEGORIES_UPDATE_APPR_SUCCESS("PCAT005"),

	/** The pseudo categories delete init success. */
	PSEUDO_CATEGORIES_DELETE_INIT_SUCCESS("PCAT006"),

	/** The pseudo categories delete appr success. */
	PSEUDO_CATEGORIES_DELETE_APPR_SUCCESS("PCAT007"),

	/** The pseudo categories reject success. */
	PSEUDO_CATEGORIES_REJECT_SUCCESS("PCAT008"),

	/** The pseudo categories under approval. */
	PSEUDO_CATEGORIES_UNDER_APPROVAL("PCAT009"),

	/** The pseudo categories code already exist. */
	PSEUDO_CATEGORIES_CODE_ALREADY_EXIST("PCAT010"),

	/** The pseudo categories name already exist. */
	PSEUDO_CATEGORIES_NAME_ALREADY_EXIST("PCAT011"),

	/** The pseudo categories update success. */
	PSEUDO_CATEGORIES_UPDATE_SUCCESS("PCAT012"),

	/** The pseudo categories deleted success. */
	PSEUDO_CATEGORIES_DELETED_SUCCESS("PCAT013"),

	/** The parent category in valid. */
	PARENT_CATEGORY_IN_VALID("PCAT014"),

	/** The mfs wallet already mapped. */
	// MFS WALLET MAPPING
	MFS_WALLET_ALREADY_MAPPED("MFS001"),

	/** The commission wallet not default. */
	COMMISSION_WALLET_NOT_DEFAULT("MFS002"),

	/** The remittance wallet not default. */
	REMITTANCE_WALLET_NOT_DEFAULT("MFS003"),

	/** The pro remittance not default. */
	PRO_REMITTANCE_NOT_DEFAULT("MFS004"),

	/** The bonus sva wallet not default. */
	BONUS_SVA_WALLET_NOT_DEFAULT("MFS005"),

	/** The invalid payment service id. */
	INVALID_PAYMENT_SERVICE_ID("MFS006"),

	/** The invalid payment service type. */
	INVALID_PAYMENT_SERVICE_TYPE("MFS007"),

	/** The invalid provider id. */
	INVALID_PROVIDER_ID("MFS008"),

	/** The mfs unable delete wallet grole. */
	MFS_UNABLE_DELETE_WALLET_GROLE("MFS009"),

	/** The mfs unable delete wallet tcp. */
	MFS_UNABLE_DELETE_WALLET_TCP("MFS010"),

	/** The mfs unable delete wallet user. */
	MFS_UNABLE_DELETE_WALLET_USER("MFS011"),

	/** The mfs wallet map add success. */
	MFS_WALLET_MAP_ADD_SUCCESS("MFS012"),

	/** The mfs wallet map update success. */
	MFS_WALLET_MAP_UPDATE_SUCCESS("MFS013"),

	/** The mfs wallet map delete success. */
	MFS_WALLET_MAP_DELETE_SUCCESS("MFS014"),

	/** The grph domain add success. */
	// Geo Management
	GRPH_DOMAIN_ADD_SUCCESS("GEO001"),

	/** The grph domain code already exists. */
	GRPH_DOMAIN_CODE_ALREADY_EXISTS("GEO002"),

	/** The grph domain name already exists. */
	GRPH_DOMAIN_NAME_ALREADY_EXISTS("GEO003"),

	/** The grph domain shortname already exists. */
	GRPH_DOMAIN_SHORTNAME_ALREADY_EXISTS("GEO004"),

	/** The grph domain update success. */
	GRPH_DOMAIN_UPDATE_SUCCESS("GEO005"),

	/** The grph domain delete success. */
	GRPH_DOMAIN_DELETE_SUCCESS("GEO006"),

	/** The grph domain child rcords exists. */
	GRPH_DOMAIN_CHILD_RCORDS_EXISTS("GEO007"),

	/** The grph domain user active. */
	GRPH_DOMAIN_USER_ACTIVE("GEO008"),

	/** The grph domain not exists. */
	GRPH_DOMAIN_NOT_EXISTS("GEO009"),

	/** The grph domain not available. */
	GRPH_DOMAIN_NOT_AVAILABLE("GEO010"),

	/** The grph domain suspended. */
	GRPH_DOMAIN_SUSPENDED("GEO011"),

	/** The parent grph domain not exists. */
	PARENT_GRPH_DOMAIN_NOT_EXISTS("GEO012"),

	/** The grph domain type not available. */
	GRPH_DOMAIN_TYPE_NOT_AVAILABLE("GEO013"),

	/** The sys pref update not allowed. */
	SYS_PREF_UPDATE_NOT_ALLOWED("SPC001"),

	/** The value sys pref update not allowed. */
	VALUE_SYS_PREF_UPDATE_NOT_ALLOWED("SPC002"),

	/** The sys pref update success. */
	SYS_PREF_UPDATE_SUCCESS("SPC003"),

	/** The wallet instance success. */
	WALLET_INSTANCE_SUCCESS("WI001"),

	/** The wallet instance failed. */
	WALLET_INSTANCE_FAILED("WI002"),

	/** The chuser add approve. */
	// Channel User
	CHUSER_ADD_APPROVE("CHU001"),

	/** The chuser approve error. */
	CHUSER_APPROVE_ERROR("CHU002"),

	/** The chuser approve reject. */
	CHUSER_APPROVE_REJECT("CHU003"),

	/** The chuser enterprise no categoryies. */
	CHUSER_ENTERPRISE_NO_CATEGORYIES("CHU004"),

	/** The chuser wallet balance notzero. */
	CHUSER_WALLET_BALANCE_NOTZERO("CHU005"),

	/** The chuser child exist. */
	CHUSER_CHILD_EXIST("CHU006"),

	/** The chuser pending child exist. */
	CHUSER_PENDING_CHILD_EXIST("CHU007"),

	/** The chuser pending transaction exist. */
	CHUSER_PENDING_TRANSACTION_EXIST("CHU008"),

	/** The chuser add morethen oneuser exists. */
	CHUSER_ADD_MORETHEN_ONEUSER_EXISTS("CHU009"),

	/** The chuser add makerdata corrupted. */
	CHUSER_ADD_MAKERDATA_CORRUPTED("CHU010"),

	/** The chuser add nouser exists. */
	CHUSER_ADD_NOUSER_EXISTS("CHU011"),

	/** The chuser generic exception occurred. */
	CHUSER_GENERIC_EXCEPTION_OCCURRED("CHU012"),

	/** The chuser already associated with user. */
	CHUSER_ALREADY_ASSOCIATED_WITH_USER("CHU013"),

	/** The chuser churn mgmt user error. */
	CHUSER_CHURN_MGMT_USER_ERROR("CHU014"),

	/** The chuser suspend delete alreadyinitiated. */
	CHUSER_SUSPEND_DELETE_ALREADYINITIATED("CHU015"),

	/** The chuser already modifiy initiated. */
	CHUSER_ALREADY_MODIFIY_INITIATED("CHU016"),

	/** The chuser suspend initiated success. */
	CHUSER_SUSPEND_INITIATED_SUCCESS("CHU017"),

	/** The chuser delete fic balance nonzero. */
	CHUSER_DELETE_FIC_BALANCE_NONZERO("CHU018"),

	/** The chuser delete wallets have balance. */
	CHUSER_DELETE_WALLETS_HAVE_BALANCE("CHU019"),

	/** The chuser delete channel existing txn. */
	CHUSER_DELETE_CHANNEL_EXISTING_TXN("CHU020"),

	/** The chuser suspend approval done. */
	CHUSER_SUSPEND_APPROVAL_DONE("CHU021"),

	/** The chuser delete associatedbank with user. */
	CHUSER_DELETE_ASSOCIATEDBANK_WITH_USER("CHU022"),

	/** The chuser delete initiated success. */
	CHUSER_DELETE_INITIATED_SUCCESS("CHU023"),

	/** The chuser delete approve success. */
	CHUSER_DELETE_APPROVE_SUCCESS("CHU024"),

	/** The chuser resume initiated success. */
	CHUSER_RESUME_INITIATED_SUCCESS("CHU025"),

	/** The chuser delete already initiated. */
	CHUSER_DELETE_ALREADY_INITIATED("CHU026"),

	/** The chuser resume approval done. */
	CHUSER_RESUME_APPROVAL_DONE("CHU027"),

	/** The chuser add initiated success. */
	CHUSER_ADD_INITIATED_SUCCESS("CHU028"),

	/** The chuser update approve. */
	CHUSER_UPDATE_APPROVE("CHU029"),

	/** The chuser resume not suspend. */
	CHUSER_RESUME_NOT_SUSPEND("CHU030"),

	/** The chuser update initiated success. */
	CHUSER_UPDATE_INITIATED_SUCCESS("CHU031"),

	/** The chuser update already initiated. */
	CHUSER_UPDATE_ALREADY_INITIATED("CHU032"),

	/** The password complecity. */
	PASSWORD_COMPLECITY("CHU033"),

	/** The channel user time diff error. */
	CHANNEL_USER_TIME_DIFF_ERROR("CHU051"),

	/** The allowed days required error. */
	ALLOWED_DAYS_REQUIRED_ERROR("CHU052"),

	/** The allowed to time required error. */
	ALLOWED_TO_TIME_REQUIRED_ERROR("CHU053"),

	/** The invalid msisdn length. */
	INVALID_MSISDN_LENGTH("CHU054"),

	/** The msisdn muststart with valid prefix. */
	MSISDN_MUSTSTART_WITH_VALID_PREFIX("CHU055"),

	/** The msisdn not operator prefix. */
	MSISDN_NOT_OPERATOR_PREFIX("CHU056"),

	/** The password min max lenth same. */
	PASSWORD_MIN_MAX_LENTH_SAME("CHU057"),

	/** The password between length. */
	PASSWORD_BETWEEN_LENGTH("CHU058"),

	/** The msisdn alredy exists. */
	MSISDN_ALREDY_EXISTS("CHU059"),

	/** The login id alredy exists. */
	LOGIN_ID_ALREDY_EXISTS("CHU060"),

	/** The external code alredy exists. */
	EXTERNAL_CODE_ALREDY_EXISTS("CHU061"),

	/** The msisdn alredy blacklisted. */
	MSISDN_ALREDY_BLACKLISTED("CHU062"),

	/** The without default provider error. */
	WITHOUT_DEFAULT_PROVIDER_ERROR("CHU063"),

	/** The multiple primary account error. */
	MULTIPLE_PRIMARY_ACCOUNT_ERROR("CHU064"),

	/** The same multiple wallet error. */
	SAME_MULTIPLE_WALLET_ERROR("CHU065"),

	/** The wallet type error. */
	WALLET_TYPE_ERROR("CHU066"),

	/** The nouser found formate error. */
	NOUSER_FOUND_FORMATE_ERROR("CHU067"),

	/** The firstname formate error. */
	FIRSTNAME_FORMATE_ERROR("CHU068"),

	/** The account number error. */
	ACCOUNT_NUMBER_ERROR("CHU069"),

	/** The nicname alredy blacklisted. */
	NICNAME_ALREDY_BLACKLISTED("CHU070"),

	/** The nicname alredy exist. */
	NICNAME_ALREDY_EXIST("CHU071"),

	/** The nicname reserved. */
	NICNAME_RESERVED("CHU072"),

	/** The upload file size error. */
	UPLOAD_FILE_SIZE_ERROR("CHU073"),

	/** The customer id not unique as per bank error. */
	CUSTOMER_ID_NOT_UNIQUE_AS_PER_BANK_ERROR("CHU074"),

	/** The password alphabets small case error. */
	PASSWORD_ALPHABETS_SMALL_CASE_ERROR("CHU075"),

	/** The password alphabets captial case error. */
	PASSWORD_ALPHABETS_CAPTIAL_CASE_ERROR("CHU076"),

	/** The password number one error. */
	PASSWORD_NUMBER_ONE_ERROR("CHU077"),

	/** The password specilal character error. */
	PASSWORD_SPECILAL_CHARACTER_ERROR("CHU078"),

	/** The password confrim password not same error. */
	PASSWORD_CONFRIM_PASSWORD_NOT_SAME_ERROR("CHU079"),

	/** The account number not unique error. */
	ACCOUNT_NUMBER_NOT_UNIQUE_ERROR("CHU080"),

	/** The sms pin length error. */
	SMS_PIN_LENGTH_ERROR("CHU082"),

	/** The customerid already exists. */
	CUSTOMERID_ALREADY_EXISTS("CHU083"),

	/** The no bank detail found. */
	NO_BANK_DETAIL_FOUND("CHU084"),

	/** The liqudation account number not unique error. */
	LIQUDATION_ACCOUNT_NUMBER_NOT_UNIQUE_ERROR("CHU085"),

	/** The validation input success. */
	VALIDATION_INPUT_SUCCESS("CHU101"),

	/** The chuser one field input required. */
	CHUSER_ONE_FIELD_INPUT_REQUIRED("CHU150"),

	/** The chuser user not exist. */
	CHUSER_USER_NOT_EXIST("CHU151"),

	/** The chuser multiple users exist. */
	CHUSER_MULTIPLE_USERS_EXIST("CHU152"),

	/** The identical txn update success. */
	// Identical Transaction
	IDENTICAL_TXN_UPDATE_SUCCESS("ITXN001"),

	/** The transfer rule doesnt exist. */
	// Stock Approval Limits
	TRANSFER_RULE_DOESNT_EXIST("00209"),

	/** The pseudo user not associated parent. */
	PSEUDO_USER_NOT_ASSOCIATED_PARENT("PSD001"),

	/** The stock approval limits update success. */
	STOCK_APPROVAL_LIMITS_UPDATE_SUCCESS("STO001"),

	/** The pseudo user parent mismatch. */
	// PSEUDO User
	PSEUDO_USER_PARENT_MISMATCH("PUSER001"),

	/** The operator user add initiated success. */
	OPERATOR_USER_ADD_INITIATED_SUCCESS("OPRU001"),

	/** The operator user update initiated success. */
	OPERATOR_USER_UPDATE_INITIATED_SUCCESS("OPRU002"),

	/** The operator user delete initiated success. */
	OPERATOR_USER_DELETE_INITIATED_SUCCESS("OPRU003"),

	/** The operator user delete approval success. */
	OPERATOR_USER_DELETE_APPROVAL_SUCCESS("OPRU004"),

	/** The operator user added success. */
	OPERATOR_USER_ADDED_SUCCESS("OPRU005"),

	/** The operator user updated success. */
	OPERATOR_USER_UPDATED_SUCCESS("OPRU006"),

	/** The operator user one input required. */
	OPERATOR_USER_ONE_INPUT_REQUIRED("OPRU007"),

	/** The operator user update already initiated. */
	OPERATOR_USER_UPDATE_ALREADY_INITIATED("OPRU008"),

	/** The operator user rejected. */
	OPERATOR_USER_REJECTED("OPRU009"),

	/** The operator user already approved. */
	OPERATOR_USER_ALREADY_APPROVED("OPRU010"),

	/** The operator user update approval success. */
	OPERATOR_USER_UPDATE_APPROVAL_SUCCESS("OPRU011"),

	/** The operator user add approval success. */
	OPERATOR_USER_ADD_APPROVAL_SUCCESS("OPRU012"),

	/** The operator user add reject success. */
	OPERATOR_USER_ADD_REJECT_SUCCESS("OPRU013"),

	/** The operator user update reject success. */
	OPERATOR_USER_UPDATE_REJECT_SUCCESS("OPRU014"),

	/** The operator user fromtime totime validation error. */
	OPERATOR_USER_FROMTIME_TOTIME_VALIDATION_ERROR("OPRU015"),

	/** The operator global search atleast one error. */
	OPERATOR_GLOBAL_SEARCH_ATLEAST_ONE_ERROR("OPRU016"),

	/** The operator global search only one error. */
	OPERATOR_GLOBAL_SEARCH_ONLY_ONE_ERROR("OPRU017"),

	/** The pseudo user add initiate success. */
	PSEUDO_USER_ADD_INITIATE_SUCCESS("PUSER002"),

	/** The pseudo user add success. */
	PSEUDO_USER_ADD_SUCCESS("PUSER003"),

	/** The pseudo user reject success. */
	PSEUDO_USER_REJECT_SUCCESS("PUSER016"),

	/** The pseudo user add appr success. */
	PSEUDO_USER_ADD_APPR_SUCCESS("PUSER013"),

	/** The pseudo user update appr success. */
	PSEUDO_USER_UPDATE_APPR_SUCCESS("PUSER014"),

	/** The pseudo user delete appr success. */
	PSEUDO_USER_DELETE_APPR_SUCCESS("PUSER015"),

	/** The pseudo user alrady initiated error. */
	PSEUDO_USER_ALRADY_INITIATED_ERROR("PUSER004"),

	/** The pseudo user delete initiate success. */
	PSEUDO_USER_DELETE_INITIATE_SUCCESS("PUSER005"),

	/** The pseudo user delete success. */
	PSEUDO_USER_DELETE_SUCCESS("PUSER006"),

	/** The pseudo user delete msisdn mismatch. */
	PSEUDO_USER_DELETE_MSISDN_MISMATCH("PUSER007"),

	/** The pseudo user norecord msisdn. */
	PSEUDO_USER_NORECORD_MSISDN("PUSER008"),

	/** The pseudo user update already initiated. */
	PSEUDO_USER_UPDATE_ALREADY_INITIATED("PUSER009"),

	/** The pseudo user norecord userid. */
	PSEUDO_USER_NORECORD_USERID("PUSER010"),

	/** The pseudo user update initiate success. */
	PSEUDO_USER_UPDATE_INITIATE_SUCCESS("PUSER011"),

	/** The pseudo user update success. */
	PSEUDO_USER_UPDATE_SUCCESS("PUSER012"),

	/** The opt already present. */
	OPT_ALREADY_PRESENT("ROPR001"),

	/** The opt multi operator support. */
	OPT_MULTI_OPERATOR_SUPPORT("ROPR002"),

	/** The opt denominations invalid format. */
	OPT_DENOMINATIONS_INVALID_FORMAT("ROPR003"),

	/** The opt duplicate denominations. */
	OPT_DUPLICATE_DENOMINATIONS("ROPR004"),

	/** The opt invalid denominations prefix. */
	OPT_INVALID_DENOMINATIONS_PREFIX("ROPR005"),

	/** The opt denominations not zero. */
	OPT_DENOMINATIONS_NOT_ZERO("ROPR006"),

	/** The opt min max amount req. */
	OPT_MIN_MAX_AMOUNT_REQ("ROPR007"),

	/** The opt invalid min amount. */
	OPT_INVALID_MIN_AMOUNT("ROPR008"),

	/** The opt invalid max amount. */
	OPT_INVALID_MAX_AMOUNT("ROPR009"),

	/** The opt inactive statuse. */
	OPT_INACTIVE_STATUSE("ROPR010"),

	/** The opt invalid idproof. */
	OPT_INVALID_IDPROOF("ROPR011"),

	/** The opt invalid mfs providers. */
	OPT_INVALID_MFS_PROVIDERS("ROPR012"),

	/** The opt add success. */
	OPT_ADD_SUCCESS("ROPR015"),

	/** The opt update success. */
	OPT_UPDATE_SUCCESS("ROPR020"),

	/** The opt fic balance nonzero. */
	OPT_FIC_BALANCE_NONZERO("ROPR021"),

	/** The opt user balance nonzero. */
	OPT_USER_BALANCE_NONZERO("ROPR022"),

	/** The opt delete success. */
	OPT_DELETE_SUCCESS("ROPR023"),

	/** The agentcode alredy exists. */
	AGENTCODE_ALREDY_EXISTS("PUSER030"),

	/** The biller add initiated success. */
	// Biller
	BILLER_ADD_INITIATED_SUCCESS("BILLU001"),

	/** The biller add created success. */
	BILLER_ADD_CREATED_SUCCESS("BILLU002"),

	/** The biller update initiated success. */
	BILLER_UPDATE_INITIATED_SUCCESS("BILLU003"),

	/** The biller code alerdy exists. */
	BILLER_CODE_ALERDY_EXISTS("BILLU004"),

	/** The biller already initiated. */
	BILLER_ALREADY_INITIATED("BILLU005"),

	/** The biller updated success. */
	BILLER_UPDATED_SUCCESS("BILLU006"),

	/** The biller suspend delete alreadyinitiated. */
	BILLER_SUSPEND_DELETE_ALREADYINITIATED("BILLU080"),

	/** The biller suspend initiated success. */
	BILLER_SUSPEND_INITIATED_SUCCESS("BILLU081"),

	/** The biller suspend success. */
	BILLER_SUSPEND_SUCCESS("BILLU082"),

	/** The biller suspend reject success. */
	BILLER_SUSPEND_REJECT_SUCCESS("BILLU083"),

	/** The biller suspend appr success. */
	BILLER_SUSPEND_APPR_SUCCESS("BILLU084"),

	/** The biller resume success. */
	BILLER_RESUME_SUCCESS("BILLU085"),

	/** The biller resume initiated success. */
	BILLER_RESUME_INITIATED_SUCCESS("BILLU086"),

	/** The biller resume suspended alreadyinitiated. */
	BILLER_RESUME_SUSPENDED_ALREADYINITIATED("BILLU087"),

	/** The biller resume reject success. */
	BILLER_RESUME_REJECT_SUCCESS("BILLU088"),

	/** The biller resume appr success. */
	BILLER_RESUME_APPR_SUCCESS("BILLU089"),

	/** The biller cannot perform this action. */
	BILLER_CANNOT_PERFORM_THIS_ACTION("BILLU090"),

	/** The no active biller record. */
	NO_ACTIVE_BILLER_RECORD("BILLU091"),

	/** The biller upload success. */
	BILLER_UPLOAD_SUCCESS("BILLU092"),

	/** The biller delete appr success. */
	BILLER_DELETE_APPR_SUCCESS("BILLU071"),

	/** The biller reject success. */
	BILLER_REJECT_SUCCESS("BILLU072"),

	/** The biller update appr success. */
	BILLER_UPDATE_APPR_SUCCESS("BILLU073"),

	/** The biller delete merchant pendingreq. */
	BILLER_DELETE_MERCHANT_PENDINGREQ("BILLU074"),

	/** The biller delete init success. */
	BILLER_DELETE_INIT_SUCCESS("BILLU075"),

	/** The biller delete pending wallet balance. */
	BILLER_DELETE_PENDING_WALLET_BALANCE("BILLU076"),

	/** The biller delete pending reimbursements. */
	BILLER_DELETE_PENDING_REIMBURSEMENTS("BILLU077"),

	/** The biller delete user in not available. */
	BILLER_DELETE_USER_IN_NOT_AVAILABLE("BILLU078"),

	/** The billval already biller val. */
	// Biller validation
	BILLVAL_ALREADY_BILLER_VAL("BILLVAL001"),

	/** The billval one row req. */
	BILLVAL_ONE_ROW_REQ("BILLVAL002"),

	/** The billval max row exceeed. */
	BILLVAL_MAX_ROW_EXCEEED("BILLVAL003"),

	/** The billval label similar. */
	BILLVAL_LABEL_SIMILAR("BILLVAL004"),

	/** The billval minmax invalid. */
	BILLVAL_MINMAX_INVALID("BILLVAL005"),

	/** The billval min gt max. */
	BILLVAL_MIN_GT_MAX("BILLVAL006"),

	/** The billval invalid type. */
	BILLVAL_INVALID_TYPE("BILLVAL007"),

	/** The billval type num spe contain req. */
	BILLVAL_TYPE_NUM_SPE_CONTAIN_REQ("BILLVAL008"),

	/** The billval type num spe contain invalid. */
	BILLVAL_TYPE_NUM_SPE_CONTAIN_INVALID("BILLVAL009"),

	/** The billval type num spe startwith invalid. */
	BILLVAL_TYPE_NUM_SPE_STARTWITH_INVALID("BILLVAL010"),

	/** The billval contain req for nsp. */
	BILLVAL_CONTAIN_REQ_FOR_NSP("BILLVAL011"),

	/** The billval alpha num req. */
	BILLVAL_ALPHA_NUM_REQ("BILLVAL012"),

	/** The billval numeric req. */
	BILLVAL_NUMERIC_REQ("BILLVAL013"),

	/** The billval alpha only. */
	BILLVAL_ALPHA_ONLY("BILLVAL014"),

	/** The billval ac max length. */
	BILLVAL_AC_MAX_LENGTH("BILLVAL015"),

	/** The billval add success. */
	BILLVAL_ADD_SUCCESS("BILLVAL016"),

	/** The billval no update row. */
	BILLVAL_NO_UPDATE_ROW("BILLVAL017"),

	/** The billval update success. */
	BILLVAL_UPDATE_SUCCESS("BILLVAL018"),

	/** The billval no record for delete. */
	BILLVAL_NO_RECORD_FOR_DELETE("BILLVAL019"),

	/** The billval delete success. */
	BILLVAL_DELETE_SUCCESS("BILLVAL020"),

	/** The bill ass deleted success. */
	BILL_ASS_DELETED_SUCCESS("BILASS001"),

	/** The bill ass msisdn not active. */
	BILL_ASS_MSISDN_NOT_ACTIVE("BILASS002"),

	/** The bill ass msisdn registered need. */
	BILL_ASS_MSISDN_REGISTERED_NEED("BILASS003"),

	/** The bill ass msisdn no record. */
	BILL_ASS_MSISDN_NO_RECORD("BILASS004"),

	/** The bill ass msisdn no match bill. */
	BILL_ASS_MSISDN_NO_MATCH_BILL("BILASS005"),

	/** The bill ass msisdn no record sys. */
	BILL_ASS_MSISDN_NO_RECORD_SYS("BILASS006"),

	/** The bill ass added success. */
	BILL_ASS_ADDED_SUCCESS("BILASS007"),

	/** The bill ass company not exist. */
	BILL_ASS_COMPANY_NOT_EXIST("BILASS008"),

	/** The bill ass company suspended. */
	BILL_ASS_COMPANY_SUSPENDED("BILASS009"),

	/** The bill ass msisdn already associated for bill pay. */
	BILL_ASS_MSISDN_ALREADY_ASSOCIATED_FOR_BILL_PAY("BILASS010"),

	/** The bill ass account not exist. */
	BILL_ASS_ACCOUNT_NOT_EXIST("BILASS011"),

	/** The operator user already initiated. */
	OPERATOR_USER_ALREADY_INITIATED("OPRU008"),

	/** The operator user already initiated. */
	GATWAY_USER_IS_NOT_ACTIVE("GWUACT001"),

	USER_AUTHENDICATION_ERROR("ULAE0001"),

	USER_PASSWORD_BLOCKED_ERROR("ULAE0002"),

	PARTY_STATUS_SUSPEND("ULAE0003"), 
	
	WEB_INTERFACENOTALLOWED("ULAE0004"), 
	
	DOMAIN_SUSPENDED("ULAE0005"),
	
	CATEGORY_SUSPENDED("ULAE0006"), 
	
	USER_APPROVAL_PENDING("ULAE0007"),
	
	PARENT_USER_REQUESTED_SUSPEND("ULAE0008"),
	
	USER_STATUS_BLOCKED("ULAE0009"), 
	
	USER_STATUS_DEREGISTER("ULAE0010"),
	
	USER_PASSWD_BLOCKED("ULAE0011"),
	
	USER_LOGOUT_SUCCESS("ULAE0012"),

	/** The Locale List. */
	LOCALE_LIST_SUCCESS("LOCALELST001"),

	MAX_LOGIN_NETWORK_REACHED("MAXLNR001"),

	MAX_LOGIN_NETWORK_CATG_REACHED("MAXLNCR001"),

	DENOMINATION_EXISTS("DEXTS0001"),

	DENOMINATION_SHORT_EXISTS("DEXTS0002"),

	DENOMINATION_MRP_EXISTS("DEXTS0003"),

	VALIDATION_TYPE("DEXTS0004"),

	UNAUTHORIZED_REQUEST("401"),

	ADD_DENOMINATION_APPROVE_SUCCESS("ADDENO001"),

	ADD_DENOMINATION_REJECT_SUCCESS("ADDENO002"),

	ADD_DENOMINATION_ADD_SUCCESS("ADDENO003"),

	ADD_DENOMINATION_INITIATE_SUCCESS("ADDENO004"),

	IS_VOU_DEN_PROFILE_ZERO_ALLOW("ADDENO005"),

	MAX_DENOMINATION_VAL("ADDENO006"),

	MODIFY_DENOMINATION_APPROVE_SUCCESS("MODENO001"),

	MODIFY_DENOMINATION_REJECT_SUCCESS("MODENO002"),

	MODIFY_DENOMINATION_INITIATE_SUCCESS("MODENO003"),

	MODIFY_DENOMINATION_SUCCESS("MODENO004"),

	PROFILE_NAME_EXIST("PRONA001"),
	
	PROFILE_SHORTNAME_EXIST("PRONA002"),
	
	

	PROFILE_EXPIRY_DATE_EXIST("ADDPROF001"),

	PROFILE_EXPIRY_DATE_EXIST_MANDATE("ADDPROF002"),

	PROFILE_EXPIRY_DATE_SHOULDNOT_SAME("ADDPROF003"),

	PROFILE_MAXIMUM_QUANTITY("ADDPROF004"),

	PROFILE_DESCRIPTION("ADDPROF005"),

	PROFILE_SUCCESS("ADDPROF006"),

	PROFILE_INITIATE_SUCCESS("ADDPROF007"),

	PROFILE_MODIFY_EXIST("ADDPROF008"),

	PROFILE_MODIFY_APPROVE_SUCCESS("ADDPROF009"),

	PROFILE_MODIFY_REJECT_SUCCESS("ADDPROF010"),

	DENOMINATION_RECORD_NOT_FOUND("ADDPROF011"),

	ADD_PROFILE_APPROVE_SUCCESS("ADDPROF012"),

	ADD_PROFILE_REJECTED_SUCCESS("ADDPROF013"),

	PROFILE_DELETE_APPROVE_SUCCESS("ADDPROF014"),

	PROFILE_DELETE_REJECT_SUCCESS("ADDPROF015"),

	PROFILE_DELETE_INITIATE_SUCCESS("ADDPROF016"),

	PROFILE_MODIFY_INITIATE_SUCCESS("ADDPROF017"),

	PROFILE_MODIFY_SUCCESS("ADDPROF018"),

	PROFILE_INITIATED_OTHER_PROCESS("ADDPROF019"),

	PROFILE_DELETE_SUCCESS("ADDPROF020"),

	INVALID_SLAB_RANGE("INV_SLAB_RANGE"),

	BATCH_GEN_SUCCESS("GENBTCHO001"),

	BATCH_GEN_FAILURE("GENBTCHO002"),

	BATCH_GEN_VAL001("GENVAL001"),

	BATCH_GEN_VAL002("GENVAL002"), INVALID_FIELD_VALUE("INVFIELDV001"),

	BACTH_NOT_FOUND("STATUS0001"),

	MORE_THAN_ONE_EXIST("MORETHA001"),

	IS_ASSOCIATED("ISASSOCIA001"),

	IS_TOASSOCIATED("TOASSICIA002"),

	BATCH_APPROVAL_SUCCESS("GEBTCHAPPRO001"), BATCH_APPR_VAL005("APPRVAL005"),

	BATCH_GEN_VAL003("CGENVAL002"),

	BATCH_GEN_VAL004("GENVAL004"),

	BATCH_NUMBER_ISSUE("STATUSEX001"),

	BATCH_NUMBER_ADD_ISSUE("STATUSEX002"),

	BATCH_NUMBER_SUCCESS("STATUSUC001"),

	STATUS_ARRAYLIST_ERROR("STATUSER005"),

	MINIMUM_MAXIMUM_CHECK("ADDPROF114"),

	BATCH_REJECT_VAL001("BATCHREJECT001"), BATCH_GEN_VAL005("INVALID_SEGMENT"), BATCH_GEN_VAL006("INVALID_NTWK_CODE"),
	BATCH_GEN_VAL007("INVALID_BTCH_QUNTITY"), BATCH_GEN_VAL008("INVALID_FILE_FORMAT"),
	BATCH_GEN_VAL009("INVALID_FILE_FORMAT_ROW"), SYSPREFSUCCESS("SYSPREFSUCCESS"),

	APPRVLISTSUCCESS("APPRVLISTSUCCESS"), APPRVLISTFAILURE("APPRVLISTFAILURE"),

	UNSUCCESSUPLOAD("UPLOADVOU001"),

	DIRNOTCREATED("UPLOADVOU002"),

	LARGEFILESIZE("UPLOADVOU003"),

	FILE_EXISTS("UPLOADVOU004"),

	NO_FILE_EXISTS("UPLOADVOU005"),

	FILESIZEZERO("UPLOADVOU006"),

	INVALID_CONTENT_TYPE("UPLOADVOU007"),

	GENERAL_FILE_PROCESSING_ERROR("UPLOADVOU008"),

	GENERAL_5930("UPLOADVOU009"),

	UNSUCCESS_FILE("UPLOADVOU010"),

	ANOTHER_PROCESS("UPLOADVOU011"),

	GENERAL_PROCESS_ERROR("UPLOADVOU012"),

	ERROR_EXCEPTION_ERROR_1999("UPLOADVOU013"),

	PEAK_HOUR_ERROR("UPLOADVOU014"),

	VOUCHER_ERROR_TOTAL_ERROR_COUNT("UPLOADVOU015"),

	VOUCHERTYPENOTFOUND("VTYPENOTFOUND"), VENQUIRYSUCCESS("VENQUIRYSUCCESS"), VENQUIRYFAILURE("VENQUIRYFAILURE"),

	RECORD_NOT_EQUAL("UPLOADVOU016"),

	MOD_PREF_SUCCESS("MODPREFSUCCESS"), MOD_PREF_FAILURE("MODPREFFAILURE"),

	BATCH_APPR_VAL006("APPRVAL006"),

	UPLOADVOU017("UPLOADVOU017"),

	U19021("19021"),

	U5953("5953"),

	U19022("19022"),

	U19031("19031"),

	U19029("19029"),

	U5929("5929"),

	FILESIZE001("FILESIZE001"), INVFILEFORMT("INVFILEFORMT"),

	DENOMINATION_ASSOCIATED_WITH_PROFILES("ADDENO020"),

	ACTION_TYPE_NOT_FOUND("ADDENO021"),

	ALREADY_INITIATED_UPDATE("ADDPROF118"),

	DENO_ALREADY_INITIATED_UPDATE("ADDENO115"),

	RECORD_NOT_FOUND_RESPECTIVE_OPERATION("ADDENO118"),

	FRMSERNOgtTOSERIALNO("VALDFRMTOSERIALNO"),

	VOUCHERUPLOAD_INVALID_BASE64("INVALID_FILE_FORMAT_VOUCHERUPLOAD"),

	INVALID_VOUCHER_PIN_LENGTH("INVALID_VOUCHER_PIN_LENGTH"),
	
	DUPLICATE_PIN_NUMBER("DUPLICATE_PIN_NUMBER"),

	INVALID_VOUCHER_SERIALNO_LENGTH("INVALID_VOUCHER_SERIALNO_LENGTH"),

	SERIALNO_IS_NOT_CONTINOUS("SERIALNO_IS_NOT_CONTINOUS"),

	EXPIRY_DATE_ALREADY_EXPIRED("EXPIRY_DATE_ALREADY_EXPIRED"),

	NOT_VALID_EXPIRY_DATE_FORMATS("NOT_VALID_EXPIRY_DATE_FORMATS"),

	NETWORKCODE_NOT_MAPPED_PRODUCT("NETWORKCODE_NOT_MAPPED_PRODUCT"),

	DUPLICATE_ROW_NUM("DUPLICATE_ROW_NUM"),

	DUPLICATE_RECORD("DUPLICATE_RECORD"),

	FILEPROCESSINGERROR("FILEPROCESSINGERROR"),

	DIRECTORY_NOT_CREATED("DIRECTORY_NOT_CREATED"), INVALID_CHECKSUM("vms.request.invalid.checksum"),
	FILE_IS_NOT_EXIST("FILE_IS_NOT_EXIST"),

	INVALID_LANG_COUNTRY("INVALID_LANG_COUNTRY"), REGEX_SUCCESS("REGEX_SUCCESS"), REGEX_FAILURE("REGEX_FAILURE"),
	DENOMINATION_SHORTNAME_LENGTH("DENOMINATION_SHORTNAME_LENGTH"),

	MULTI_VALIDATION_ERROR("MULTI_VALIDATION_ERROR"),
	
	SERIAL_NUMBER_EXIST("SERIAL_NUMBER_EXIST"),
	
	PIN_NUMBER_EXIST("PIN_NUMBER_EXIST"),
	
	FIELD_MAXLENGTH_VALIDATION("field.maxLength.validation"),
	
	BASE64_IS_NOT_VALID("BASE64_IS_NOT_VALID"),
	
	PRODUCT_NETWORKCODE_OR_PRODUCT_STATUS_INVALID("INVALID_NETWORKCODE_OR_STATUS"),
	
	
	
	SERIAL_NUMBER_IS_NOT_NUMERIC("SRLNO_IS_NOT_NUMERIC"),
	

	API_TIME_OUT("API_TIME_OUT"),
	API_TIME_OUT_STATUS("408"),

	VOUCHERSEXISTS("VOUCHERSEXISTS"),
	
	ALREADY_ASSOCIATED_PRO("ALREADY_ASSOCIATED_PRO"),
	
	ALREADY_ACTIVATED("ALREADY_ACTIVATED"),
	
	NO_ROLE_DEFINED("NO_ROLE_DEFINED"),
	
	WEB_INTERFACE_NOT_ALLOWED("WEB_INTERFCE_NOT_ALLOWED"),
	
	PASSWORD_EXPIRED("PASSWORD_EXPIRED"),

	GROUPROLE_SUSPENDED("GRPRL0001"),
	
	RESET_PASSWORD_EXPIRED("RESET_PASSWORD_EXPIRED"),


	;

	/** The str value. */
	private String strValue;

	/**
	 * Instantiates a new message codes.
	 *
	 * @param strValue the str value
	 */
	MessageCodes(String strValue) {
		this.strValue = strValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	
}
