package com.inter.vasthoml;

public interface VASTHI {

    public int ACTION_VALIDATE_REQUEST = 1;
    public int ACTION_RECHARGE_REQUEST = 2;
    public String HTTP_STATUS_SUCCESS = "200";
    public String HTTP_STATUS_FAIL = "206";
    public String VAS_SUCCESS = "0";
    public String VAS_PROMO_NOT_AVL_SUBSTYPE = "-1";
    public String VAS_PROMO_INCOMPATIBLE = "-2";
    public String VAS_INCORRECT_MSISDN = "-3";
    public String VAS_INCORRECT_CODE = "-4";
    public String VAS_INCORRECT_AMOUNT = "-5";
    public String VAS_INVALID_TRANSID_PARAM = "-6";
    public String VAS_INVALID_SUBS_PHONE_PARAM = "-7";
    public String VAS_INVALID_CODE_PARAM = "-8";
    public String VAS_INVALID_AMOUNT_PARAM = "-9";
    public String VAS_INVALID_SELLER_PARAM = "-10";
    public String VAS_UNKNOWN_ERROR = "-99";
}
