
package com.inter.gp.cs5;


public interface CS5GpI
{
	public int ACTION_ACCOUNT_INFO=90;
	public int ACTION_ACCOUNT_INFO_AGAIN=92;//for testing
	public int ACTION_IMMEDIATE_DEBIT=1;
	public int ACTION_RECHARGE_CREDIT=2;
	public int ACTION_ACCOUNT_DETAILS=91;
	public String HTTP_STATUS_200="200";
	public String RESULT_OK="0,1,2";
	public String SUBSCRIBER_NOT_FOUND="102";	
	public String OLD_TRANSACTION_ID="162";
	public String LDCC_SERVICE_OFFERING_ID="15";
	public String LDCC_SERVICE_OFFERING_ACT_FLAG="1";
	public String RESPONSE_CODE_100_HANDLING="100";
	public String RESULT_NOT_OK="105,106,107,108,109,110,111,112,114,116,117,118,119,127,121,122,137,160,161,163,164,167,204,212,165,176,177,178,179,225,248,256,260,262,197";
	public String ACCOUNT_BARRED_FROM_REFILL="103";
	public String ACCOUNT_TEMPORARY_BLOCKED="104";
	public String VOUCHER_STATUS_PENDING="113";
	public String VOUCHER_GROUP_SERVICE_CLASS="115";
	public String INVALID_PAYMENT_PROFILE="120";
	public String MAX_CREDIT_LIMIT="123";
	public String BELOW_MIN_BAL="124";
	public String SYSTEM_UNAVAILABLE="125";
	public String ACCOUNT_NOT_ACTIVE="126";
	public String DATE_ADJUSTMENT_ISSUE="136";
	public String IN_CONN_FAIL="314";
	public String IN_RESPONSE_FAIL="413";
	public String DEDICATED_ACCOUNT_NOT_ACTIVE="139";
	public String ACC_MAX_CREDIT_LIMIT="153";
	public String DEDICATED_ACCOUNT_ID="100";
	public String OTHER_IN_EXCEPTION="999";
	public int ADD_DAYS_INSTALLED=365;
	public String	DEDICATED_ACCOUNT_NOT_ALLOWED="105";
	public String	DEDICATED_ACCOUNT_NEGATIVE="106";
	public String	VOUCHER_STATUS_USED_BY_SAME="107";
	public String	VOUCHER_STATUS_USED_BY_DIFFERENT="108";
	public String	VOUCHER_STATUS_UNAVAILABLE="109";
	public String	VOUCHER_STATUS_EXPIRED="110";
	public String	VOUCHER_STATUS_STOLEN_OR_MISSING="111";
	public String	VOUCHER_STATUS_DAMAGED="112";
	public String	VOUCHER_TYPE_NOT_ACCEPTED="114";
	public String	SERVICE_CLASS_CHANGE_NOT_ALLOWED="117";
	public String	INVALID_VOUCHER_ACTIVATION_CODE="119";
	public String	SUPERVISION_PERIOD_TOO_LONG="121";
	public String	SERVICE_FEE_PERIOD_TOO_LONG="122";
	public String	ACCUMULATOR_NOT_AVAILABLE="127";
	public String	GET_BALANCE_AND_DATE_NOT_ALLOWED="137";
	public String	OPERATION_NOT_ALLOWED_FROM_CURRENT_LOCATION="160";
	public String	FAILED_TO_GET_LOCATION_INFORMATION="161";
	public String	INVALID_DEDICATED_ACCOUNT_PERIOD="163";
	public String	INVALID_DEDICATED_ACCOUNT_START_DATE="164";
	public String	OFFER_NOT_FOUND="165";
	public String	INVALID_UNIT_TYPE="167";
	public String	REFILL_DENIED_FIRST_IVR_CALL_NOT_MADE="176";
	public String	REFILL_DENIED_ACCOUNT_NOT_ACTIVE="177";
	public String	REFILL_DENIED_SERVICE_FEE_PERIOD_EXPIRED="178";
	public String	REFILL_DENIED_SUPERVISION_PERIOD_EXPIRED="179";
	public String	PERIODIC_ACCOUNT_MANAGEMENT_EVALUATION_FAILED="197";
	public String	OFFER_START_DATE_NOT_CHANGED_AS_OFFER_ALREADY_ACTIVE="225";
	public String	SHARED_ACCOUNT_OFFER_NOT_ALLOWED_SUBORDINATE_SUBSCRIBER="248";
	public String	ATTRIBUTE_NAME_NOT_EXIST="256";
	public String	CAPABILITY_NOT_AVAILABLE="260";
	public String	ATTRIBUTE_UPDATE_NOT_ALLOWED_FOR_ATTRIBUTE="262";
}