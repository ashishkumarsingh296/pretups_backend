INSERT INTO SERVICE_TYPE (SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, REQUEST_PARAM) VALUES('LOANOPTIN', 'C2S', 'ALL', 'TYPE EXTNWCODE PRODUCTCODE', 'com.btsl.pretups.user.requesthandler.LoanOptInOptOutRequestHandler', 'Loan OPT In request', 'Loan OPT In request', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN', 'Loan OPT In request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,DATE,EXTREFNUM,MESSAGE', 'Y', 'TYPE,EXTNWCODE,PRODUCTCODE');


INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM) VALUES('LOANOPTINREQ', 'EXTGW', '190', 'LOANOPTIN', 'Loan OPT In request', 'Y', NULL, NULL, NULL, 'Y', sysdate, 'SU0001',sysdate, 'SU0001', 'SVK0021254', NULL, 'TYPE,EXTNWCODE,PRODUCTCODE');


INSERT INTO SERVICE_TYPE (SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, REQUEST_PARAM) VALUES('LOANOPTOUT', 'C2S', 'ALL', 'TYPE EXTNWCODE PRODUCTCODE', 'com.btsl.pretups.user.requesthandler.LoanOptInOptOutRequestHandler', 'Loan OPT Out request', 'Loan OPT Out request', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN', 'Loan OPT Out request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,DATE,EXTREFNUM,MESSAGE', 'Y', 'TYPE,EXTNWCODE,PRODUCTCODE');


INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM) VALUES('LOANOPTOUTREQ', 'EXTGW', '190', 'LOANOPTOUT', 'Loan OPT Out request', 'Y', NULL, NULL, NULL, 'Y', sysdate, 'SU0001',sysdate, 'SU0001', 'SVK0021255', NULL, 'TYPE,EXTNWCODE,PRODUCTCODE');


INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('CAT_USERWISE_LOAN_ENABLE', 'Category Loan Enable or disable User Wise', 'CATPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Category Loan Enable or disable User Wise', 'Y', 'Y', 'C2S', 'Category Loan Enable or disable User Wise', sysdate, 'ADMIN', sysdate, 'ADMIN', 'true/false', 'Y');


INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM) VALUES('LOANOPTINREQ', 'USSD', '190', 'LOANOPTIN', 'Loan OPT In request', 'Y', NULL, NULL, NULL, 'Y', sysdate, 'SU0001', sysdate, 'SU0001', 'SVK0031254', NULL, 'TYPE,EXTNWCODE,PRODUCTCODE');

INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM) VALUES('LOANOPTOUTREQ', 'USSD', '190', 'LOANOPTOUT', 'Loan OPT Out request', 'Y', NULL, NULL, NULL, 'Y', sysdate, 'SU0001', sysdate, 'SU0001', 'SVK0031255', NULL, 'TYPE,EXTNWCODE,PRODUCTCODE');



---DWH----
INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('DWHLOAN', TIMESTAMP '2021-10-16 18:47:02.000000', 'C', TIMESTAMP '2021-10-15 00:00:00.000000', TIMESTAMP '2021-10-16 00:00:00.000000', 360, 1440, 'Loan data DWH', 'NG', 0);


---loan enq API---
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, REQUEST_PARAM)
VALUES('LSTLOANENQ', 'C2S', 'ALL', 'TYPE EXTNWCODE ', 'com.btsl.pretups.user.requesthandler.LastLoanEnqRequestHandler', 'Last Loan Enquiry request', 'Last Loan Enquiry request', 'Y', TIMESTAMP '2021-10-12 04:40:21.000000', 'ADMIN', TIMESTAMP '2021-10-12 04:40:21.000000', 'ADMIN', 'Last Loan Enquiry request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,DATE,MESSAGE', 'Y', 'TYPE,EXTNWCODE');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('LSTLOANENQREQ', 'EXTGW', '190', 'LSTLOANENQ', 'Last Loan Enquiry request', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2021-10-12 04:44:17.000000', 'SU0001', TIMESTAMP '2021-10-12 04:44:17.000000', 'SU0001', 'SVK0021256', NULL, 'TYPE,EXTNWCODE');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('LSTLOANENQREQ', 'USSD', '190', 'LSTLOANENQ', 'Last Loan Enquiry request', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2021-10-13 07:01:51.000000', 'SU0001', TIMESTAMP '2021-10-13 07:01:51.000000', 'SU0001', 'SVK0021257', NULL, 'TYPE,MSISDN,PIN');

----LINKS-----

--VIEW--

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VIEWLOANPRF', 'View Loan Profile', 'Profile Management', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VIEWLOANPRF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'VIEWLOANPRF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('CCE', 'VIEWLOANPRF', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VIEWLP001', 'PROFILES', '/loanProfileAction.do?method=selectDomainAndCategory&page=6', 'View Loan Profile', 'Y', 6, '2', '1', '/loanProfileAction.do?method=selectDomainAndCategory&page=6');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VIEWLP01A', 'PROFILES', '/loanProfileAction.do?method=selectDomainAndCategory&page=6', 'View Loan Profile', 'Y', 6, '1', '1', '/loanProfileAction.do?method=selectDomainAndCategory&page=6');


INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VIEWLOANPRF', 'LOANPRF003', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VIEWLOANPRF', 'VIEWLP001', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VIEWLOANPRF', 'VIEWLP01A', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VIEWLOANPRF', 'LOANPRF004', '1');


--loan enq--

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'LSTLOANENQ', 'Last Loan Enquiry', 'Channel Enquiry', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('CCE', 'LSTLOANENQ', '1');


INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LSTLOAN001', 'C2SENQ', '/lastLoanEnquiryAction.do?method=selectDetailsForLoanEnquiry', 'Last Loan Enquiry', 'Y', 3, '2', '1', '/lastLoanEnquiryAction.do?method=selectDetailsForLoanEnquiry');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LSTLOAN002', 'C2SENQ', '/jsp/profile/lastLoanEnquiry.jsp', 'Last Loan Enquiry', 'N', 3, '2', '1', '/jsp/profile/lastLoanEnquiry.jsp');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LSTLOAN01A', 'C2SENQ', '/lastLoanEnquiryAction.do?method=selectDetailsForLoanEnquiry', 'Last Loan Enquiry', 'N', 3, '2', '1', '/lastLoanEnquiryAction.do?method=selectDetailsForLoanEnquiry');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LSTLOANENQ', 'LSTLOAN001', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LSTLOANENQ', 'LSTLOAN01A', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LSTLOANENQ', 'LSTLOAN002', '1');


------------------phase 3-------------------------
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CATEGORIES_LIFECYCLECHANGE', 'CATEGORIES_APPLY_LIFECYCLECHANGE', 'SYSTEMPRF', 'STRING', 'RETA,SA', NULL, NULL, 5, 'Categories applicable for Life Cycle management (e.g. RETA,SA) {comma seperated category_code values}', 'N', 'N', 'C2S', 'Categories applicable for Life Cycle management (e.g. RETA,SA) {comma seperated values}', sysdate, 'ADMIN', sysdate, 'SU0001', NULL, 'Y');



INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, REQUEST_PARAM)
VALUES('SELFCUBAR', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.user.requesthandler.SelfChannelUserBarHandler', 'BARRED USER', 'BARRED USER FROM THE SYSTEM', 'Y', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', 'C2S Barred', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,MESSAGE', 'Y', 'TYPE,MSISDN,PIN,PARENTMSISDN,BALANCE,PRODUCTCODE');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, REQUEST_PARAM)
VALUES('SLFCUUNBAR', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.user.requesthandler.SelfChannelUserBarHandler', 'UNBARRED USER', 'UNBARRED USER FROM THE SYSTEM', 'Y', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', 'C2S UNBarred', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,MESSAGE', 'Y', 'TYPE,MSISDN,PIN,PARENTMSISDN,BALANCE,PRODUCTCODE');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('SELFCUBARREQ', 'EXTGW', '190', 'SELFCUBAR', 'Self Channel user Bar', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', 'SVK0000976', NULL, 'TYPE,EXTNWCODE,MSISDN,PIN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('SELFCUBARREQ', 'USSD', '190', 'SELFCUBAR', 'Self Channel user Bar', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', 'SVK0000974', NULL, NULL);

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('SELFCUUNBARREQ', 'EXTGW', '190', 'SLFCUUNBAR', 'Self Channel user unBar', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', 'SVK0000977', NULL, 'TYPE,EXTNWCODE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('SELFCUUNBARREQ', 'USSD', '190', 'SLFCUUNBAR', 'Self Channel user unBar', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', TIMESTAMP '2021-10-20 03:40:49.000000', 'SU0001', 'SVK0000975', NULL, NULL);



---self pin reset api-----

INSERT INTO SERVICE_TYPE (SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, REQUEST_PARAM) VALUES('SPINRESET', 'C2S', 'PRE', 'TYPE MSISDN BALANCE PARENTMSISDN PRODUCTCODE', 'com.btsl.pretups.requesthandler.SelfPinResetController', 'Self PIN Reset', 'Self PIN Reset', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN', 'Self PIN Reset', 'N', 'N', 'Y', NULL, 'Y', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'Y', 'TYPE,MSISDN,BALANCE,PRODUCTCODE');

INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM) VALUES('SPINRESET', 'USSD', '190', 'SPINRESET', 'Self PIN Reset', 'Y', NULL, NULL, NULL, 'Y', sysdate, 'SU0001', sysdate, 'SU0001', 'SVK0051399', NULL, 'TYPE,MSISDN,BALANCE,PRODUCTCODE');
INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM) VALUES('SPINRESET', 'EXTGW', '190', 'SPINRESET', 'Self PIN Reset', 'Y', NULL, NULL, NULL, 'Y', sysdate, 'SU0001', sysdate, 'SU0001', 'SVK0061399', NULL, 'TYPE,MSISDN,BALANCE,PRODUCTCODE');

COMMIT;

----loan reports----
 
INSERT INTO MODULES (MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
VALUES ('LOANRPT', 'Loan Reports', '15', '1');



INSERT INTO ROLES (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES ('OPERATOR', 'LOANDBMT', 'Loan Disbursement Report', 'Loan Reports', 'Y', 'A', 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO ROLES (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES ('DISTB_CHAN', 'LOANDBMT', 'Loan Disbursement Report', 'Loan Reports', 'Y', 'A', 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO ROLES (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES ('OPERATOR', 'LOANSTMT', 'Loan Settlement Report', 'Loan Reports', 'Y', 'A', 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO ROLES (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES ('DISTB_CHAN', 'LOANSTMT', 'Loan Settlement Report', 'Loan Reports', 'Y', 'A', 'N', '1', 'WEB', 'B', 'N', 'N', 'B');



INSERT INTO CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES ('BCU', 'LOANDBMT', '1');

INSERT INTO CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES ('DIST', 'LOANDBMT', '1');

INSERT INTO CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES ('BCU', 'LOANSTMT', '1');

INSERT INTO CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES ('DIST', 'LOANSTMT', '1');



INSERT INTO PAGES (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES ('LOANRPT001', 'LOANRPT', '/LoanReportAction.do?method=selectDetailsForLoanReport', 'Loan Disbursement Report', 'Y', '1', '2', '1', '/LoanReportAction.do?method=selectDetailsForLoanReport');

INSERT INTO PAGES (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES ('LOANRPT01A', 'LOANRPT', '/LoanReportAction.do?method=selectDetailsForLoanReport', 'Loan Disbursement Report', 'Y', '1', '1', '1', '/LoanReportAction.do?method=selectDetailsForLoanReport');

INSERT INTO PAGES (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES ('LOANRPT002', 'LOANRPT', '/LoanStmtReportAction.do?method=selectDetailsForLoanReport', 'Loan Settlement Report', 'Y', '2', '2', '1', '/LoanStmtReportAction.do?method=selectDetailsForLoanReport');



INSERT INTO PAGE_ROLES (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES ('LOANDBMT', 'LOANRPT001', '1');

INSERT INTO PAGE_ROLES (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES ('LOANDBMT', 'LOANRPT01A', '1');

INSERT INTO PAGE_ROLES (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES ('LOANSTMT', 'LOANRPT002', '1');



------------------------missed in RFR----------------

ALTER TABLE CHANNEL_USER_LOAN_INFO ADD BALANCE_BEFORE_LOAN NUMBER (20);
ALTER TABLE CHANNEL_USER_LOAN_INFO_HISTORY ADD BALANCE_BEFORE_LOAN NUMBER (20);


DROP TRIGGER TRIG_CHANNEL_USER_LOAN_HISTORY;
CREATE OR REPLACE TRIGGER "TRIG_CHANNEL_USER_LOAN_HISTORY" 
AFTER INSERT OR UPDATE OR DELETE ON CHANNEL_USER_LOAN_INFO FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,:NEW.balance_before_loan,'I',sysdate);
ELSIF UPDATING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,:NEW.balance_before_loan,'U',sysdate);
ELSIF DELETING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,operation_performed,entry_date)
VALUES(:OLD.user_id,:OLD.profile_id,:OLD.product_code,:OLD.loan_threhold,:OLD.loan_amount,:OLD.loan_given,:OLD.loan_given_amount,:OLD.last_loan_date,:OLD.last_loan_txn_id,:OLD.settlement_id,:OLD.settlement_date,:OLD.settlement_loan_amount,:OLD.settlement_loan_interest,:OLD.loan_taken_from,:OLD.settlement_to,:OLD.optinout_allowed,:OLD.optinout_on,:OLD.optinout_by,:OLD.created_by,:OLD.created_on,:OLD.modified_by,:OLD.modified_on,:OLD.balance_before_loan,'D',sysdate);
END IF;
END;

INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('TXN_RECEIVER_USER_STATUS_CHANG', 'TXN_RECEIVER_USER_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'W:CH,EX:Y,CH:Y,PA:Y', NULL, NULL, 50, 'Changing Status Administratively ', 'N', 'Y', 'C2S', 'FROM to TO Status Allowed for change ', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

ALTER TABLE CHANNEL_USER_LOAN_INFO_HISTORY ADD TRANS_TYPE VARCHAR2(15);


CREATE  INDEX PK_LOAN_DATE_HISTORY ON CHANNEL_USER_LOAN_INFO_HISTORY
(LAST_LOAN_DATE);


CREATE  INDEX PK_SETTLEMENT_DATE_HISTORY ON CHANNEL_USER_LOAN_INFO_HISTORY
(SETTLEMENT_DATE);

DROP TRIGGER TRIG_CHANNEL_USER_LOAN_HISTORY;
CREATE OR REPLACE TRIGGER "TRIG_CHANNEL_USER_LOAN_HISTORY" 
AFTER INSERT OR UPDATE OR DELETE ON CHANNEL_USER_LOAN_INFO FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,:NEW.balance_before_loan,'I',sysdate);
ELSIF UPDATING THEN
IF :NEW.loan_given <> :OLD.loan_given AND :NEW.loan_given='Y' THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,TRANS_TYPE,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,:NEW.balance_before_loan,'L','U',sysdate);
END IF;
IF :NEW.loan_given <> :OLD.loan_given AND :NEW.loan_given='N' THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,TRANS_TYPE,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,:NEW.balance_before_loan,'S','U',sysdate);
ELSE
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,:NEW.balance_before_loan,'U',sysdate);

END IF;
ELSIF DELETING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,balance_before_loan,operation_performed,entry_date)
VALUES(:OLD.user_id,:OLD.profile_id,:OLD.product_code,:OLD.loan_threhold,:OLD.loan_amount,:OLD.loan_given,:OLD.loan_given_amount,:OLD.last_loan_date,:OLD.last_loan_txn_id,:OLD.settlement_id,:OLD.settlement_date,:OLD.settlement_loan_amount,:OLD.settlement_loan_interest,:OLD.loan_taken_from,:OLD.settlement_to,:OLD.optinout_allowed,:OLD.optinout_on,:OLD.optinout_by,:OLD.created_by,:OLD.created_on,:OLD.modified_by,:OLD.modified_on,:OLD.balance_before_loan,'D',sysdate);
END IF;
END;


