--##########################################################################################################
--##
--##      PreTUPS_v7.0.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.1.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.2.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.3.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.4.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.5.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.6.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.7.0 DB Script
--##
--##########################################################################################################

ALTER TABLE VOMS_CATEGORIES DROP COLUMN NETWORK_CODE;

ALTER TABLE VOMS_PRODUCTS DROP COLUMN NETWORK_CODE;

DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE IN ('NATIONAL_VOUCHER_ENABLE', 'NATIONAL_VOUCHER_NETWORK_CODE', 'ONLINE_BATCH_EXP_DATE_LIMIT', 'MAX_VOUCHER_EXPIRY_EXTN_LIMIT', 'VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN', 'VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN', 'VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN', 'VOMS_PROF_TALKTIME_MANDATORY', 'VOMS_PROF_VALIDITY_MANDATORY', 'VOMS_PROFILE_DEF_MINMAXQTY', 'VOMS_PROFILE_MIN_REORDERQTY', 'VOMS_PROFILE_MAX_REORDERQTY', 'DOWNLD_BATCH_BY_BATCHID');

DELETE FROM CATEGORY_ROLES WHERE CATEGORY_CODE = 'NWADM' AND APPLICATION_ID = '1' AND ROLE_CODE IN ('INITVOMS', 'APP1VOMS', 'APP2VOMS', 'APP3VOMS', 'VOMSADDCAT', 'VOMSADDPROF', 'VOMSMODIPROF', 'VOMSVIEWPRF', 'VOMSADACTPR', 'VOMSVIEWACT', 'VOMSTPMGT', 'VOMSMODDENO', 'VOMSVWDEN', 'VOMSCHGSTATUS', 'VOMSOTCHGSTATUS', 'VIEWBATCHLIST', 'VOUFIL') ;

DELETE FROM CATEGORY_ROLES WHERE CATEGORY_CODE = 'SUNADM' AND APPLICATION_ID = '1' AND ROLE_CODE IN ('VOMSADDCAT', 'VOMSADDPROF', 'VOMSMODIPROF', 'VOMSVIEWPRF', 'VOMSADACTPR', 'VOMSMODACTPR', 'VOMSVIEWACT', 'VOMSMODDENO', 'VOMSVWDEN', 'VOMSCHGSTATUS', 'VOMSOTCHGSTATUS', 'VIEWBATCHLIST', 'VOUFIL') ;

SET DEFINE OFF;

DELETE FROM MESSAGES_MASTER WHERE MESSAGE_CODE IN ('LOW_NETWORK_STOCK_NOTIFICATION_SUBJECT', 'LOW_NETWORK_STOCK_NOTIFICATION_HEADER', 'LOW_NETWORK_STOCK_NOTIFICATION_FOOTER', 'LOW_BALANCE_ALERT_NOTIFICATION_SUBJECT', 'LOW_BALANCE_ALERT_NOTIFICATION_CONTENT', 'CHANNELUSER_MSISDN', 'CHANNELUSER_Name', 'CHANNELUSER_PRODUCT', 'CHANNELUSER_Balance');

COMMIT;

DELETE FROM service_keywords WHERE keyword = 'VMSPINEXT';

DELETE FROM service_type WHERE keyword = 'VMSPINEXT';

DROP TABLE voms_pin_exp_ext CASCADE;

ALTER TABLE VOMS_VOUCHERS DROP COLUMN pre_expiry_date;

ALTER TABLE VOMS_VOUCHERS DROP COLUMN info1;

--##########################################################################################################
--##
--##      PreTUPS_v7.8.0 DB Script
--##
--##########################################################################################################

DELETE FROM LOOKUPS WHERE LOOKUP_CODE = 'LC';
DELETE FROM LOOKUPS WHERE LOOKUP_CODE = 'NL';
DELETE FROM LOOKUP_TYPES WHERE LOOKUP_TYPE = 'VMSSEG';
COMMIT;

DROP TABLE USER_VOUCHER_SEGMENTS CASCADE;

ALTER TABLE VOMS_PRODUCTS DROP COLUMN VOUCHER_SEGMENT;
  
ALTER TABLE VOMS_CATEGORIES DROP COLUMN VOUCHER_SEGMENT;

ALTER TABLE VOMS_BATCHES DROP COLUMN VOUCHER_SEGMENT;
 
DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE = 'USER_VOUCHERSEGMENT_ALLOWED';
DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE = 'NW_NATIONAL_PREFIX';
DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE = 'NW_CODE_NW_PREFIX_MAPPING';
DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE = 'VOMS_NATIONAL_LOCAL_PREFIX_ENABLE';

ALTER TABLE CHANNEL_VOUCHER_ITEMS DROP COLUMN VOUCHER_SEGMENT;

ALTER TABLE CHANNEL_VOUCHER_ITEMS DROP COLUMN NETWORK_CODE;
  
ALTER TABLE VOMS_VOUCHERS DROP COLUMN VOUCHER_SEGMENT;;

  
--##########################################################################################################
--##
--##      PreTUPS_v7.9.0 DB Script
--##
--##########################################################################################################

--##########################################################################################################
--##
--##      PreTUPS_v7.10.0 DB Script
--##
--##########################################################################################################

DELETE FROM WEB_SERVICES_TYPES WHERE WEB_SERVICE_TYPE IN ('DFLTCARDGRP', 'LOADCARDGROUPSET', 'SUSPENDCARDGROUP', 'DELETECARDGROUP', 'ADDCARDGROUP');