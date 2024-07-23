UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/zeroBalCounterDetail.form' WHERE PAGE_CODE='ZBALDET001';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/zeroBalCounterDetail.form' WHERE PAGE_CODE='ZBALDET01A';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/zeroBalCounterDetail.form' WHERE PAGE_CODE='ZBALDETDMM';

UPDATE PAGES SET SPRING_PAGE_URL='/reports/load-user-balances.form' WHERE PAGE_CODE='ZBALSUM001' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/load-user-balances.form' WHERE PAGE_CODE='ZBALSUMDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/load-user-balances.form' WHERE PAGE_CODE='ZBALSUM01A' ;

UPDATE PAGES SET SPRING_PAGE_URL='/reports/channel-user.form' WHERE PAGE_CODE='ROEU01A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/channel-user.form' WHERE PAGE_CODE='ROEUDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/channel-user.form' WHERE PAGE_CODE='ROEU001' ;


UPDATE PAGES SET SPRING_PAGE_URL='/channeltransfer/o2cTransferAckAction.form' WHERE PAGE_CODE='O2CACK001A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/channeltransfer/o2cTransferAckAction.form' WHERE PAGE_CODE='O2CACK001' ;
UPDATE PAGES SET SPRING_PAGE_URL='/channeltransfer/o2cTransferAckAction.form' WHERE PAGE_CODE='O2CACKDMM' ;

UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/c2sTransfer.form' WHERE PAGE_CODE='RPTTRCS001';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/c2sTransfer.form' WHERE PAGE_CODE='RPTTRCS01A';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/c2sTransfer.form' WHERE PAGE_CODE='RPTTRCSDMM';


--ChangePin Module
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='SE' AND role_code='CHANGEPINCU';
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='AG' AND role_code='CHANGEPINCU';
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='DIST' AND role_code='CHANGEPINCU';
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='RET' AND role_code='CHANGEPINCU';

DELETE FROM page_roles
WHERE role_code='CHANGEPINCU' AND page_code='CHNGPINCU1';

DELETE FROM pages
WHERE page_code='CHNGPINCU1';

UPDATE roles SET role_code='CHANGEPIN' WHERE role_code='CHANGEPINCU' AND domain_type='DISTB_CHAN';
COMMIT;



--UserBalance Module
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='SE' AND role_code='OTHERBALANCECU';
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='AG' AND role_code='OTHERBALANCECU';
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='DIST' AND role_code='OTHERBALANCECU';
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='RET' AND role_code='OTHERBALANCECU';


DELETE FROM page_roles
WHERE role_code='OTHERBALANCECU' AND page_code='CUSRBALCU';

DELETE FROM pages
WHERE page_code='CUSRBALCU';

UPDATE roles SET role_code='OTHERBALANCE' WHERE role_code='OTHERBALANCECU' AND domain_type='DISTB_CHAN'; 

COMMIT;

UPDATE pages
SET spring_page_url='/user/change-pin.form' WHERE page_code='CHNGPIN001';

UPDATE pages
SET spring_page_url='/balances/userBalance.form' WHERE page_code='CUSRBALV01';
COMMIT;

UPDATE pages
SET spring_page_url='/channelreport/load-additional-commission-summary.form' WHERE page_code='RPTADCS001';
COMMIT;

UPDATE PAGES SET SPRING_PAGE_URL='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOV001';
UPDATE PAGES SET SPRING_PAGE_URL='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOV01A';
UPDATE PAGES SET SPRING_PAGE_URL='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOVDMM';
COMMIT;

--Operation Summary Report
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPT001';
commit;
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPT00A';
commit;
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPTDMM';
commit;

--Struts support for BAR USER screen
UPDATE pages SET  page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/baruser/barreduser.form' WHERE page_code='BAR01';

UPDATE pages SET page_url='/subscriber/confirmBarredUser.jsp', spring_page_url='/subscriber/confirmBarredUser.jsp' WHERE page_code='BAR02';

UPDATE pages SET  page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/baruser/barreduser.form?method=loadBarredUser' WHERE page_code='BAR1Dmm';


UPDATE pages SET page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/barreduser.do?method=loadBarredUser'
WHERE page_code='BAR01A';
commit;


------Struts & Spring support for UNBAR USER screen

UPDATE pages SET  page_url='/unbaruser.do?method=unBarUser', spring_page_url='/baruser/unbaruser.form'
WHERE page_code='UNBAR01';

UPDATE pages SET  page_url='/subscriber/selectUserToUnbarrSts.jsp', spring_page_url='/subscriber/selectUserToUnbarr.jsp' WHERE page_code='UNBAR02';

UPDATE pages SET page_url='/unbaruser.do?method=unBarUser', spring_page_url='/baruser/unbaruser.form' WHERE page_code='UNBAR1Dmm';

UPDATE pages SET  page_url='/unbaruser.do?method=unBarUser', spring_page_url='/unbaruser.do?method=unBarUser' WHERE page_code='UNBAR01A';

-------Struts & Spring support for view Barred list  screen
UPDATE pages SET page_url='/viewBarredUserAction.do?method=selectBarredUser', spring_page_url='/baruser/viewBarredUserAction.form' WHERE page_code='VIEWBAR01';

UPDATE pages SET  page_url='/viewBarredUserAction.do?method=selectBarredUser', spring_page_url='/viewBarredUserAction.do?method=viewBarredList' WHERE page_code='VIEWBAR01A';


UPDATE pages SET page_url='/viewBarredUserAction.do?method=selectBarredUser', spring_page_url='/baruser/viewBarredUserAction.form' WHERE page_code='VIEWBARDmm';

UPDATE pages SET  page_url='/jsp/subscriber/viewBarredList.jsp',spring_page_url='/jsp/subscriber/viewBarredList.jsp' WHERE page_code='VIEWBAR02';

---Staff self c2c reports

UPDATE PAGES SET SPRING_PAGE_URL='/reports/staffSelfC2CReport.form' WHERE PAGE_CODE='STFSLF01A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/staffSelfC2CReport.form' WHERE PAGE_CODE='STFSLFDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/staffSelfC2CReport.form' WHERE PAGE_CODE='STFSLF001' ;


--Associate Profile
UPDATE pages
SET spring_page_url='/user/load-associate-profile.form' WHERE page_code='ASSCUSR001';
COMMIT;

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
Values
   ('COMMT', 'Commission Type', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('12/02/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');

Insert into LOOKUPS (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values ('NC', 'Normal Commissioning', 'COMMT', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values ('PC', 'Positive Commissioning', 'COMMT', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
ALTER TABLE COMMISSION_PROFILE_SET ADD LAST_DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE COMMISSION_PROFILE_SET_VERSION ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE CHANNEL_TRANSFERS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE FOC_BATCH_ITEMS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE O2C_BATCH_ITEMS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE C2C_BATCH_ITEMS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
COMMIT;


--Commission profile Status: Spring to struts changes
------------------------------------------------------
UPDATE pages SET page_url='/commissionProfileAction.do?method=loadDomainListForSuspend&page=0',spring_page_url='/commission-profile/status.form'
WHERE page_code='COMMPS001';


--Schedule RC script : Spring And struts compatibility
------------------------------------------------------
UPDATE pages SET  page_url='/scheduleTopUp.do?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUP01';

UPDATE pages SET  page_url='/scheduleTopUp.do?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUP1A';

UPDATE pages SET page_url='/scheduleTopUp.do?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUPDM';


UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetailsSts.jsp', spring_page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp' WHERE page_code='SCHTOPUP02';

------------Other Commision--------

ALTER TABLE COMMISSION_PROFILE_SET_VERSION
 ADD (OTH_COMM_PRF_SET_ID  VARCHAR2(10 BYTE));
 
 ALTER TABLE CHANNEL_TRANSFERS  ADD (OTH_COMM_PRF_SET_ID  VARCHAR2(30 BYTE));
 
 ALTER TABLE CHANNEL_TRANSFERS_ITEMS
 ADD(
  OTH_COMMISSION_TYPE               VARCHAR2(10 BYTE),
  OTH_COMMISSION_RATE               NUMBER(16,4),
  OTH_COMMISSION_VALUE              NUMBER(20)
);

CREATE TABLE OTHER_COMM_PRF_SET
(
  OTH_COMM_PRF_SET_ID      VARCHAR2(10 BYTE)    NOT NULL,
  OTH_COMM_PRF_SET_NAME    NVARCHAR2(40)        NOT NULL,
  OTH_COMM_PRF_TYPE        VARCHAR2(10 BYTE)    NOT NULL,
  OTH_COMM_PRF_TYPE_VALUE  VARCHAR2(10 BYTE)    NOT NULL,
  NETWORK_CODE             VARCHAR2(2 BYTE)     NOT NULL,
  CREATED_ON               DATE                 NOT NULL,
  CREATED_BY               VARCHAR2(20 BYTE)    NOT NULL,
  MODIFIED_ON              DATE                 NOT NULL,
  MODIFIED_BY              VARCHAR2(20 BYTE),
  STATUS                   VARCHAR2(1 BYTE),
  O2C_CHECK_FLAG           VARCHAR2(1 BYTE)     DEFAULT 'N',
  C2C_CHECK_FLAG           VARCHAR2(1 BYTE)     DEFAULT 'N'
);

ALTER TABLE OTHER_COMM_PRF_SET ADD (
  CONSTRAINT PK_OTHER_COMM_PRF_SET
PRIMARY KEY
(OTH_COMM_PRF_SET_ID));

CREATE UNIQUE INDEX PK_OTHER_COMM_PRF_NAME ON OTHER_COMM_PRF_SET
(OTH_COMM_PRF_SET_NAME);




CREATE TABLE OTHER_COMM_PRF_DETAILS
(
  OTH_COMM_PRF_DETAIL_ID  VARCHAR2(10 BYTE),
  OTH_COMM_PRF_SET_ID     VARCHAR2(5 BYTE)      NOT NULL,
  START_RANGE             NUMBER(20),
  END_RANGE               NUMBER(20),
  OTH_COMMISSION_TYPE     VARCHAR2(5 BYTE)      NOT NULL,
  OTH_COMMISSION_RATE     NUMBER(16,4)
);

ALTER TABLE OTHER_COMM_PRF_DETAILS ADD (
  CONSTRAINT PK_OTHER_COMM_PRF_DETAILS
 PRIMARY KEY
 (OTH_COMM_PRF_DETAIL_ID));

	
	ALTER TABLE OTHER_COMM_PRF_DETAILS ADD (
  FOREIGN KEY (OTH_COMM_PRF_SET_ID) 
 REFERENCES OTHER_COMM_PRF_SET (OTH_COMM_PRF_SET_ID) );

Insert into LOOKUP_TYPES(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED) Values ('OTCTP', 'Other Commission Type', sysdate, 'ADMIN', sysdate, 'ADMIN', 'N');

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)  Values ('CAT', 'Category Code', 'OTCTP', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY) Values ('GRAD', 'Grade', 'OTCTP', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY) Values('GAT', 'Gateway Code', 'OTCTP', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN');

Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP001', 'PROFILES', '/otherCommissionProfileAction.do?method=loadDomainList&page=0', 'Profile management', 'Y', 28, '2', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP01A', 'PROFILES', '/otherCommissionProfileAction.do?method=loadDomainList&page=0', 'Profile management', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMPDMM', 'PROFILES', '/otherCommissionProfileAction.do?method=loadDomainList&page=0', 'Profile management', 'Y', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP005', 'MASTER', '/jsp/profile/selectOtherCommissionProfileSetForView.jsp', 'Other Commission View detail', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP004', 'MASTER', '/jsp/profile/selectOtherCommissionProfileSet.jsp', 'Other Commission modify', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP003', 'MASTER', '/jsp/profile/otherCommissionProfileDetailView.jsp', 'Other Commission detail view', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP002', 'MASTER', '/jsp/profile/setTypeForOtherCommission.jsp', 'Other Commission Profile add', 'N', 28, '1', '1');
 
Insert into ROLES(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE) Values ('OPERATOR', 'OTHCOMPROMGMT', 'Add/Modify Other Commission Profile', 'Profile Management', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N');

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP001', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP002', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP003', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP004', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP005', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP01A', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMPDMM', '1');
 
Insert into CATEGORY_ROLES(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) Values ('NWADM', 'OTHCOMPROMGMT', '1');


Insert into SYSTEM_PREFERENCES    (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,    MIN_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY,    MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON,     MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE, MAX_VALUE) Values   ('OTH_COM_CHNL', 'Other Commission O2C and C2C', 'SYSTEMPRF', 'BOOLEAN', 'true',     NULL, 5, 'Other commission applicable flag', 'N', 'N',     'C2S', 'Other commission applicable flag', sysdate, 'ADMIN', sysdate,     'SU0001', 'true,false', 'Y', NULL);
	

Insert into IDS(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
Values    ('ALL', 'OT_COM_SID', 'ALL', 1, sysdate,'NA', 'Commission profile set ID');
Insert into IDS    (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE,     FREQUENCY, DESCRIPTION) 
Values    ('ALL', 'OT_COM_DID', 'ALL', 1, sysdate ,'NA', 'Commission profile detail ID');

Commit;=======
UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetailsSts.jsp', spring_page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp' WHERE page_code='SCHTOPUP02';



ALTER TABLE USERS ADD (MIGRATION_STATUS  VARCHAR2(5 BYTE));
COMMIT;

--Reschedule Batch Recharge :Spring to Struts compatibility
----------------------------------------

UPDATE pages SET  page_url='/rescheduleTopUp.do?method=rescheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUP01';

UPDATE pages SET  page_url='/rescheduleTopUp.do?method=rescheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUP1A';

UPDATE pages SET  page_url='/rescheduleTopUp.do?method=rescheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUPDM';

UPDATE pages SET  page_url='/jsp/restrictedsubs/rescheduleTopUpDetailsSts.jsp', spring_page_url='/jsp/restrictedsubs/rescheduleTopUpDetails.jsp' WHERE page_code='RSHTOPUP02';


--Cancel Schedule TopUp :Spring to Struts compatibility
-----------------------------------------
UPDATE pages SET  page_url='/cancelScheduleRecharge.do?method=selectDetailsForSingleAuthorise', spring_page_url='/restrictedsubs/cancel_schedule_recharge.form' WHERE page_code='CNCLSCH001';

UPDATE pages SET  page_url='/jsp/restrictedsubs/displayDetailsForCancelSingleSub.jsp',  spring_page_url='/restrictedsubs/cancel_schedule_recharge_details.form' WHERE page_code='CNCLSCH002';

UPDATE pages SET  page_url='/jsp/restrictedsubs/confirmDisplayDetailsForCancelSingleSub.jsp', spring_page_url='/restrictedsubs/cancel_schedule_recharge_viewMsisdn.form' WHERE page_code='CNCLSCH003';

UPDATE pages SET  page_url='/jsp/restrictedsubs/selectBatchCancelSingleSchedule.jsp', spring_page_url='/restrictedsubs/cancel_schedule_recharge.form' WHERE page_code='CNCLSCH004';


--Cancel Schedule Batch :Spring to Struts compatibility
------------------------------------------------------------
UPDATE pages SET  page_url='/loadCancelSchedule.do?method=viewScheduleTrf', spring_page_url='/restrictedsubs/cancel_batch_schedule_recharge.form' WHERE page_code='CNSCHTR01';

UPDATE pages SET  page_url='/jsp/restrictedsubs/cancelScheduleBatch.jsp', spring_page_url='/restrictedsubs/cancel_schedule_recharge_batch.form' WHERE page_code='CNSCHTR02';


CREATE TABLE TPS_DETAILS
(
  TPS_DATE_TIME                    DATE           NOT NULL,
  INSTANCE_CODE                 VARCHAR2(3),
  TPS							NUMBER(10),
  TPS_DATE 						DATE  
);

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('MAXTPS', 'C2S', 'ALL', 'TYPE QDATE QHOUR', 'com.btsl.pretups.requesthandler.MaxTPSHandler', 
    'c2s.change.pin', 'MAX TPS DETAILS', 'Y', sysdate, 'ADMIN', 
   sysdate, 'ADMIN', 'Max TPS Calculation Per Hour', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MAXTPS,MESSAGE', 'TYPE,MSISDN,PIN,QDATE,QHOUR,LANGUAGE1', 
    'Y');


Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('MAXTPSHOURLYREQ', 'EXTGW', '190', 'MAXTPS', 'MAX TPS CALCULATION', 
    'Y', NULL, NULL, NULL, 'Y', 
   sysdate, 'SU0001',sysdate, 'SU0001', 'SVK9000076', 
    NULL, 'TYPE,QDATE,QHOUR');
	

update pages set menu_item='Y',menu_level=1 where PAGE_CODE in ('O2CBWDR01M','O2CWRAP01M');
  
 update pages set menu_level=2 where  PAGE_CODE in ('O2CBWDR01A','O2CWRAP01A');
commit;

-- IRIS Changes Required 
ALTER TABLE DAILY_C2S_TRANS_DETAILS ADD (Promo_count Number(20));
ALTER TABLE DAILY_C2S_TRANS_DETAILS ADD (Promo_amount Number(24,2));
ALTER TABLE C2S_TRANSFERS add (    BONUS_AMOUNT NUMBER(20));
ALTER TABLE USER_DAILY_BALANCES  modify LAST_TRANSFER_NO          VARCHAR2(25 BYTE); 
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS ADD (Promo_count Number(20));
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS ADD (Promo_amount Number(24,2));

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('DIFF', 'Additional Bonus', 'COTYP', 'Y', sysdate,  'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('PROMO', 'Promotional Bonus', 'COTYP', 'Y', sysdate,
 'ADMIN', sysdate, 'ADMIN'); 

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('IRIS', 'Promo Vas Interface', 'INTCT', 'Y', sysdate,  'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('IRIS', 'Promo Vas Interface', 'INCAT', 'Y', sysdate,  'ADMIN', sysdate, 'ADMIN'); 
update SERVICE_TYPE set status='Y',REQUEST_HANDLER='com.btsl.pretups.channel.transfer.requesthandler.SAPVASReversalController' where service_type='RCREV';

commit;

----------------------------View Schedule------------------------
UPDATE pages  SET page_url='/viewIndvTransfer.do?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH001';

UPDATE pages SET page_url='/viewIndvTransfer.do?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH01A';

UPDATE pages SET page_url='/viewIndvTransfer.do?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCHDMM';

UPDATE pages SET page_url='/jsp/restrictedsubs/viewSingleScheduleRechargeSts.jsp', spring_page_url='/jsp/restrictedsubs/viewSingleScheduleRecharge.jsp' WHERE page_code='VIEWSCH002';


----------------View Schedule Batch------------------
UPDATE pages SET page_url='/loadViewTransferSchedule.do?method=viewScheduleTrf', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR01';


UPDATE pages SET page_url='/loadViewTransferSchedule.do?method=viewScheduleTrf', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR1A';


---------------------------------------View Network : Spring to Struts compatibility -------------------
UPDATE pages SET  page_url='/networkViewAction.do?method=loadNetworkListForView&page=0', spring_page_url='/network/network_view_action.form' WHERE page_code='NW3001';


UPDATE pages SET  page_url='/network/viewNetworkDetail.jsp',  spring_page_url='/network/viewNetworkListSpring.form' WHERE page_code='NW3002';


UPDATE pages SET  page_url='/networkViewAction.do?method=loadNetworkListForView&page=0', spring_page_url='/networkViewAction.do?method=loadNetworkListForView&page=0' WHERE page_code='NW3Dmm';


-------------------network status :struts and spring compatibility----------------------

UPDATE pages SET page_url='/networkStatusAction.do?method=loadNetworkStatusList&page=0', spring_page_url='/network/network_Status.form' WHERE page_code='NS001';


UPDATE pages SET page_url='/network/networkStatusView.jsp',spring_page_url='/network/save-network-status.form' WHERE page_code='NS002';

----------------P2P DWH file creation info tag
CREATE OR REPLACE PROCEDURE P2PDWHTEMPPRC 
(
 	   p_date IN DATE,
	   p_masterCnt OUT NUMBER,
	   p_transCnt  	  OUT 	 NUMBER,
	   p_message OUT VARCHAR2
)
IS

v_srno 				  		 	   NUMBER;
v_data							   VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION 					 EXCEPTION;

CURSOR   P2P_MASTER  IS
		SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||PS.LAST_TRANSFER_ON
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        PS.REGISTERED_ON||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||STR.transfer_date||','||TO_CHAR(STR.transfer_date_time,'DD-MON-YY HH24:MI:SS')||','||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        ||STR.INFO1||','||STR.INFO2||','||STR.INFO3||','||STR.INFO4||','||STR.INFO5||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END;
/



ALTER TABLE SUBSCRIBER_TRANSFERS ADD VOUCHER_SERIAL_NUMBER varchar2(20);
ALTER TABLE SUBSCRIBER_TRANSFERS ADD (INFO1 varchar2(100),INFO2 varchar2(100),INFO3 varchar2(100),INFO4 varchar2(100),INFO5 varchar2(100));
ALTER TABLE CHANNEL_TRANSFERS ADD ( INFO3 varchar2(100),INFO4 varchar2(100),INFO5 varchar2(100));

--- Staff C2C Transfer
UPDATE PAGES  SET SPRING_PAGE_URL='/pretups/staffC2CTransferView.form'  WHERE PAGE_CODE='STFC2C001' ;
UPDATE PAGES  SET SPRING_PAGE_URL='/pretups/staffC2CTransferView.form'  WHERE PAGE_CODE='STFC2C00A' ;
UPDATE PAGES  SET SPRING_PAGE_URL='/pretups/staffC2CTransferView.form'  WHERE PAGE_CODE='STFC2CDMM' ;

--For Fraud Management
ALTER TABLE P2P_SUBSCRIBERS_COUNTERS ADD VPIN_INVALID_COUNT NUMBER(5) DEFAULT 0;

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VPIN_INVALID_COUNT', 'Invalid Pin Count For Voucher Recharge', 'SYSTEMPRF', 'INT', '5', 0, 10, 50, 'Invalid Pin Count For Voucher Recharge', 'N', 'N', 'P2P', 'Invalid Pin Count For Voucher Recharge', TIMESTAMP '2018-05-29 11:50:25.000000', 'ADMIN', TIMESTAMP '2018-05-29 15:50:25.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PIN_BLK_EXP_DRN', 'Voucher PIN block Expiry Duration', 'SYSTEMPRF', 'NUMBER', '300000', 0, 300000, 50, 'Voucher PIN block Expiry Duration in munutes', 'N', 'N', 'P2P', 'Voucher PIN block Expiry Duration in munutes', TIMESTAMP '2007-02-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', NULL, 'Y');


ALTER TABLE USERS ADD DOCUMENT_TYPE VARCHAR(20);
ALTER TABLE USERS ADD DOCUMENT_NO VARCHAR(20);
ALTER TABLE USERS ADD PAYMENT_TYPE VARCHAR(20);

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('DOCTP', 'User Document Type', TO_DATE('10/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('10/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'Y');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('PAN', 'PAN', 'DOCTP', 'Y', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('PASSPORT', 'Passport', 'DOCTP', 'Y', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('AADHAR', 'Aadhar No', 'DOCTP', 'Y', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

UPDATE pages SET page_url='/userOperatorViewAction.do?method=loadSelfDetails',spring_page_url='/user/user_Operator_View_Action.form' WHERE page_code='VIEWUSRS01';
UPDATE pages SET page_url='/networkAction.do?method=loadNetworkListForChange&page=0',spring_page_url='/network/change-network.form' WHERE page_code in ('CNW001','CHNW001','CHNWDmm');
UPDATE pages SET page_url='/c2sreverse.do?method=c2sReversal',spring_page_url='/c2srecharge/reversal.form' WHERE page_code in ('C2SREV001','C2SREVDMM','C2SREV002');
commit;

ALTER TABLE VOMS_CATEGORIES MODIFY type VARCHAR2(10);
COMMIT;

--Persian Calendar - Starts --
INSERT ALL
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DATE_FORMAT_CAL_JAVA', 'Date format for Java', 'SYSTEMPRF', 'STRING', 'yyyy/MM/dd', NULL, NULL, 50, 'Date format accepted by the system', 'Y', 'Y', 'C2S', 'Date format accepted by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DATE_TIME_FORMAT', 'Date time format for Java', 'SYSTEMPRF', 'STRING', 'yyyy/MM/dd HH:mm:ss', NULL, NULL, 50, 'Date Time format accepted by the system', 'Y', 'Y', 'C2S', 'Date Time format accepted by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOCALE_CALENDAR', 'Locale used for regional Calendar', 'SYSTEMPRF', 'STRING', 'fa_IR@calendar=persian', NULL, NULL, 50, 'Locale used by the system', 'Y', 'Y', 'C2S', 'Locale used by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOCALE_ENGLISH', 'Locale used for english Calendar', 'SYSTEMPRF', 'STRING', '@calendar=persian', NULL, NULL, 50, 'Locale used by the system', 'Y', 'Y', 'C2S', 'Locale used by the system', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TIMEZONE_ID', 'Timezone id for Current location', 'SYSTEMPRF', 'STRING', 'Iran', NULL, NULL, 50, 'Timezone id used by the system', 'Y', 'Y', 'C2S', 'Timezone id used by the system', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDAR_TYPE', 'Calendar type on GUI', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar type to be displayed on GUI', 'Y', 'Y', 'C2S', 'Calendar type to be displayed on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDER_DATE_FORMAT', 'Date format for date selected by Cal on GUI', 'SYSTEMPRF', 'STRING', 'yyyy/mm/dd', NULL, NULL, 50, 'Date format for date selected by Calendar on GUI', 'Y', 'Y', 'C2S', 'Date format for date selected by Calendar on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDAR_SYSTEM', 'Calendar system used in Query', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar system used in Query', 'Y', 'Y', 'C2S', 'Calendar system used in Query', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('FORMAT_MONTH_YEAR', 'Month and year format used', 'SYSTEMPRF', 'STRING', 'yyyy/mm', NULL, NULL, 50, 'Month and year format used', 'Y', 'Y', 'C2S', 'Month and year format used', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('EXTERNAL_CALENDAR_TYPE', 'Calendar type for external gateway', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar type for external gateway', 'Y', 'Y', 'C2S', 'Calendar type for external gateway', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IS_CAL_ICON_VISIBLE', 'Is calendar icon required on GUI', 'SYSTEMPRF', 'STRING', 'Y', NULL, NULL, 50, 'Is calendar icon required on GUI', 'Y', 'Y', 'C2S', 'Is calendar icon required on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IS_MON_DATE_ON_UI', 'Is date format contains MMM in date format', 'SYSTEMPRF', 'STRING', 'N', NULL, NULL, 50, 'Is date format contains MMM in date format', 'Y', 'Y', 'C2S', 'Is date format contains MMM in date format', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
SELECT * FROM dual
COMMIT;

UPDATE SYSTEM_PREFERENCES SET DEFAULT_VALUE = '0' WHERE PREFERENCE_CODE = 'FINANCIAL_YEAR_START';
COMMIT;
--Persian Calendar - Ends --

--Voucher changes for O2C module
ALTER TABLE VOMS_BATCHES
ADD (EXT_TXN_NO  VARCHAR2(20 BYTE));

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('V', 'VOUCHER', 'TRFT', 'Y', TO_DATE('03/19/2012 11:14:17', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('03/19/2012 11:14:17', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('VOUCHER_THIRDPARTY_STATUS', 'Voucher Status after thirdparty download', 'SYSTEMPRF', 'STRING', 'WH', 
    NULL, NULL, 50, 'Voucher Status after thirdparty download', 'N', 
    'Y', 'C2S', 'Voucher Status after thirdparty download', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


CREATE TABLE USER_VOUCHERTYPES
(
  USER_ID       VARCHAR2(15 BYTE)               NOT NULL,
  VOUCHER_TYPE  VARCHAR2(10 BYTE)               NOT NULL,
  STATUS        VARCHAR2(1 BYTE)                DEFAULT 'Y'                   NOT NULL,
  CONSTRAINT PK_USER_VOUCHER_TYPE PRIMARY KEY (USER_ID,VOUCHER_TYPE)
  
)

 Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_VOUCHERTYPE_ALLOWED', 'User voucher type is allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'User voucher type is allowed', 'N', 
    'Y', 'C2S', 'User voucher type is allowed', TO_DATE('06/16/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2019 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
	
COMMIT;	

--- Claro Colombia code merge - Begin
SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('COMMA_ALLOW_IN_LOGIN', 'Comma space in login id', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Comma space in login id', 'N', 
    'N', 'C2S', 'Comma space in login id', TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('XML_DOC_ENCODING', 'XML encoding declaration', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'XML encoding declaration', 'N', 
    'N', 'C2S', 'XML encoding declaration', TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_EXTERNAL_CODE_DOMAINWISE', ' External Code Domain Based', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'User External Code Domain Based Validation', 'Y', 
    'N', 'C2S', 'User External Code Domain Based Validation', TO_DATE('08/09/2011 04:43:03', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('03/18/2016 15:26:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DISPLAY_COUNTRY', 'Display Country', 'SYSTEMPRF', 'STRING', 'US', 
    NULL, NULL, 50, 'Display Country', 'N', 
    'Y', 'C2S', NULL, TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DISPLAY_LANGUAGE', 'Display Language', 'SYSTEMPRF', 'STRING', 'en', 
    NULL, NULL, 50, 'Display Language', 'N', 
    'Y', 'C2S', NULL, TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LOAD_BAL_IP_ALLOWED', 'Remote IP validation allow', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Remote IP validation allow', 'N', 
    'N', 'C2S', 'Remote IP validation allow', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/17/2005 16:17:53', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('CHK_LAST_TXN_BY_USER_PHONES', 'Check Last Txn by USER_PHONES', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Check Last Txn using USER_PHONES', 'N', 
    'N', 'C2S', 'Check Last Txn using USER_PHONES', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/17/2005 16:17:53', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LAST_X_CHNL_TXNSTATUS_ALLOWED', 'Status for last x C2C/O2C', 'SYSTEMPRF', 'STRING', 'CLOSE', 
    1, 10, 5, 'Status allowed for last x C2C/O2C ', 'N', 
    'N', 'C2S', 'Status allowed for last x C2C/O2C', TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'C2S', 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LAST_X_C2S_TXNSTATUS_ALLOWED', 'Allowed status for last x C2S', 'SYSTEMPRF', 'STRING', '200', 
    1, 10, 5, 'Allowed status for last x C2S', 'N', 
    'N', 'C2S', 'Allowed status for last x C2S ', TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'C2S', 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('MAX_HOST_TEXTBOX', 'Max length of Host Textbox', 'SYSTEMPRF', 'INT', '200', 
    15, 200, 50, 'Max length of Host Text box', 'N', 
    'Y', 'C2S', 'Max length of Host', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DEL_CUSER_WITH_BALANCE_ALLOWED', 'Delete user with bal allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Delete Channel user with balance allowed', 'N', 
    'N', 'C2S', 'Delete Channel user with balance allowed', TO_DATE('08/23/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/23/2017 11:51:08', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_CLOSING_BALANCE_REPORT_FROM_AMOUNT', 'CLOSING BALANCERPT FROM_AMOUNT', 'SYSTEMPRF', 'STRING', '0', 
    NULL, NULL, 50, 'Previously it was hardcoded in code. so we make a entry in SystemPreferences for to amount for user closing balance report', 'N', 
    'Y', 'C2S', 'from amount for user closing balance report', TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('CAT_GATEWAY_PRODUCT_PREF', 'CATEGORY GATEWAY PRODUCT', 'SYSTEMPRF', 'STRING', 'RLMW:EXTGW:DEF2,RLMW:WEB:DEF2', 
    NULL, NULL, 50, 'CATEGORY GATEWAY PRODUCT', 'Y', 
    'Y', 'C2S', 'CATEGORY GATEWAY PRODUCT', TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_CLOSING_BALANCE_REPORT_TO_AMOUNT', 'CLOSING BALANCERPT TO_AMOUNT', 'SYSTEMPRF', 'STRING', '999999999999999999', 
    NULL, NULL, 50, 'Previously it was hardcoded in code. so we make a entry in SystemPreferences for to amount for user closing balance report', 'N', 
    'Y', 'C2S', 'to amount for user closing balance report', TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('WIRC_ACCOUNT_MSISDN_OPT', 'ACCOUNTID OR MSISDN REQUIRED', 'SYSTEMPRF', 'STRING', 'ACCOUNTID', 
    NULL, NULL, 50, 'CATEGORY GATEWAY PRODUCT', 'Y', 
    'Y', 'C2S', 'ACCOUNTID OR MSISDN ', TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;
--- Claro Colombia code merge - End


Insert into USER_VOUCHERTYPES
   (USER_ID, VOUCHER_TYPE, STATUS)
 Values
   ('SU0001', 'eletronic', 'Y');
Insert into USER_VOUCHERTYPES
   (USER_ID, VOUCHER_TYPE, STATUS)
 Values
   ('SU0001', 'physical', 'Y');
COMMIT;

--- User Closing Balance : Persian Date starts -------
CREATE OR REPLACE FUNCTION USERCLOSINGBALANCE
--This function is used for generate report of closing balance
--(p_userId  VARCHAR2) RETURN VARCHAR2
-- p_startDate date
-- p_endDate date
(p_userId  VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) RETURN VARCHAR2
IS
p_userCloBalDateWise VARCHAR2(4000) DEFAULT '';
balDate DATE;
balance NUMBER(15) DEFAULT 0;
productCode VARCHAR(10);
CURSOR c_userCloBal(p_userId VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) IS
        SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC, product_code ASC;
            BEGIN
        FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||'::'||TO_CHAR(balDate,'dd-mon-yy','nls_calendar=gregorian')||'::'||balance||',';
        END LOOP;
        
                        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise)-1);
         
        END IF;
            RETURN p_userCloBalDateWise;
END; 
COMMIT;
--- User Closing Balance : Persian Date ends -------
---Mapping correct C2S Reversal Controller starts ------
UPDATE SERVICE_TYPE
SET  REQUEST_HANDLER='com.btsl.pretups.channel.transfer.requesthandler.C2SPrepaidReversalController'
WHERE SERVICE_TYPE='RCREV' AND MODULE='C2S'AND TYPE='PRE' AND NAME='Customer Recharge Reversal';
COMMIT;
---Mapping correct C2S Reversal Controller ends ------

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('VOMS_DAMG_PIN_LNTH_ALLOW', 'Minimum pin length for Damage Voucher', 'SYSTEMPRF', 'INT', '3', 
    0, 10, 50, 'Minimum pin length for Damage Voucher', 'Y', 
    'N', 'C2S', 'Minimum pin length for Damage Voucher', TO_DATE('05/29/2018 11:50:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('05/29/2018 15:50:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'VCDETAILREPORT', '1'
            );


INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMVC001', 'VOMSREPORT', '/VCReport.do?method=detailVCReport',
             'VC Detail Report', 'Y', 581, '2', '1',
             '/VCReport.do?method=detailVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMVCDMM', 'VOMSREPORT', '/VCReport.do?method=detailVCReport',
             'VC Detail Report', 'Y', 581, '1', '1',
             '/VCReport.do?method=detailVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMVC01A', 'VOMSREPORT', '/VCReport.do?method=detailVCReport',
             'VC Detail Report', 'N', 581, '2', '1',
             '/VCReport.do?method=detailVCReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCDETAILREPORT', 'VOMVC001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCDETAILREPORT', 'VOMVCDMM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCDETAILREPORT', 'VOMVC01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'VCDETAILREPORT', 'VC Detail Report',
             'Voucher Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );
			
COMMIT:	

---- Disabling IAT, C2S Slab wise, Number of recharges slab wise, C2S Scheduling Download Report links	starts ---------
UPDATE ROLES SET STATUS = 'N' WHERE ROLE_CODE IN ('IATBATCHIDRPT','IATBULKASCRPT','IATBULKRESRPT','IATBULKAPP','IATBTCHVIEW','IATSCHVIEW','IATSCHRPTSTS','IATBATCHIDRPT','IATBULKREG','IATBULKVIEW','IATBULKSUSPND','IATBULKRES','IATSCHDRC','IATBULKASC','IATBULKDEASC','IATBULKASCRPT','IATBULKRESRPT','IATSCHRPTSTS','IATCNCLSCH','IATCNCBTCH','IATRESCHD','IATBULKDEL','IATCNTRYMGMTAD','IATCNTRYMGMTMO','IATNWMGMTAD','IATNWMGMTMO','IATTRSERPT','IATTRFSUMRPT','IATTRFANSFERENQ','C2SSLABWISE','NORECHARGESLABWISE','NETWORKSUMRPT','RPTSCHDC2SRPT','RPTSCHDUDRRPT','RPTSCHDP2PRPT','DOWNLOADFILES');

COMMIT;
---- Disabling IAT, C2S Slab wise, Number of recharges slab wise, C2S Scheduling Download Report links	ends ---------


-----Sold voucher impact changes - Tejeshvi ---------------
alter table VOMS_VOUCHERS add SOLD_STATUS VARCHAR2(1) default 'N' not null;
alter table VOMS_VOUCHERS add SOLD_DATE DATE;

alter table VOMS_VOUCHERS_SNIFFER add SOLD_STATUS VARCHAR2(1) default 'N' not null;
alter table VOMS_VOUCHERS_SNIFFER add SOLD_DATE DATE;

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('SL', 'Sold', 'VSTAT', 'Y', TIMESTAMP '2018-07-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-07-04 00:00:00.000000', 'ADMIN');

COMMIT;
-----Sold voucher impact changes - Tejeshvi ---------------



-------------IFGO TAG FOR LMB REQUEST--
ALTER TABLE SOS_TRANSACTION_DETAILS ADD (INFO1 varchar2(100),INFO2 varchar2(100),INFO3 varchar2(100),INFO4 varchar2(100),INFO5 varchar2(100));
COMMIT;
-------------IFGO TAG FOR LMB REQUEST--

-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi-------------- 
CREATE OR REPLACE PROCEDURE P2PDWHTEMPPRC 
(
 	   p_date IN DATE,
 	   p_masterCnt OUT NUMBER,
	   p_transCnt  	  OUT 	 NUMBER,
	   p_message OUT VARCHAR2
)
IS

v_srno 				  		 	   NUMBER;
v_data							   VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION 					 EXCEPTION;

CURSOR   P2P_MASTER  IS
		SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||TO_CHAR(PS.LAST_TRANSFER_ON,'DD-fmMON-YY fmHH24:MI:SS')
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        TO_CHAR(PS.REGISTERED_ON,'DD-fmMON-YY fmHH24:MI:SS')||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||STR.transfer_date||','||TO_CHAR(STR.transfer_date_time,'DD-fmMON-YY fmHH24:MI:SS')||','||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END; 
COMMIT;

-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi--------------

-------Procedure USER_DAILY_CLOSING_BALANCE-Mohit-------------
CREATE OR REPLACE PROCEDURE USER_DAILY_CLOSING_BALANCE (
								rtn_message           OUT      VARCHAR2,
                            rtn_messageforlog     OUT      VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT      VARCHAR2
                                   )
IS
/*       ############## TEMP table FOR USER daily  closing balance#####################
CREATE TABLE USER_DAILY_BAL_TEMP
(
  START_TIME DATE,
  END_TIME DATE,
  PROCESS VARCHAR2(20),
  STATUS_LOG VARCHAR2(100)
);              */

p_user_id  USER_BALANCES.user_id%TYPE;
p_product_code USER_BALANCES.product_code%TYPE;
p_network_code USER_BALANCES.network_code%TYPE;
p_network_code_for USER_BALANCES.network_code_for%TYPE;

q_user_id USER_DAILY_BALANCES.user_id%TYPE ;
q_network_code USER_DAILY_BALANCES.network_code%TYPE;
q_network_code_for USER_DAILY_BALANCES.network_code_for%TYPE;
q_product_code USER_DAILY_BALANCES.product_code%TYPE;
q_balance USER_DAILY_BALANCES.balance%TYPE;
q_prev_balance USER_DAILY_BALANCES.prev_balance%TYPE;
q_last_transfer_type USER_DAILY_BALANCES.last_transfer_type%TYPE;
q_last_transfer_no USER_DAILY_BALANCES.last_transfer_no%TYPE;
q_last_transfer_on USER_DAILY_BALANCES.last_transfer_on%TYPE;


q_daily_balance_updated_on DATE;
q_created_on DATE;
dayDifference NUMBER (5):= 0;
startCount NUMBER(3);
dateCounter DATE;




sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR user_list_cur IS
        SELECT ub.user_id,ub.product_code,ub.network_code,ub.network_code_for
        FROM USER_BALANCES ub, USERS u
        WHERE ub.USER_ID=u.USER_ID
        AND TRUNC(daily_balance_updated_on) <>TRUNC(SYSDATE)
        AND TRUNC (u.modified_on) >= CASE WHEN (u.status='N') THEN (SYSDATE-366) WHEN (u.status='C') THEN (SYSDATE-366) ELSE TRUNC (u.modified_on) END;

--BEGIN

        --INSERT INTO USER_DAILY_BAL_TEMP(PROCESS,START_TIME) VALUES ('USER_DAILY_CLOSING_BALANCE_PROCESS',SYSDATE);


BEGIN
             FOR user_records IN user_list_cur
             LOOP
                     p_user_id:=user_records.user_id;
                    p_product_code:=user_records.product_code;
                    p_network_code:=user_records.network_code;
                    p_network_code_for:=user_records.network_code_for;
             BEGIN
                        SELECT user_id,network_code,network_code_for,product_code,balance,prev_balance,last_transfer_type,
                    last_transfer_no,last_transfer_on,TRUNC(daily_balance_updated_on) daily_balance_updated_on
                    INTO q_user_id,q_network_code,q_network_code_for,q_product_code,q_balance,q_prev_balance,
                    q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,q_daily_balance_updated_on
                    FROM USER_BALANCES
                    WHERE user_id=p_user_id
                    AND network_code=p_network_code
                    AND network_code_for=p_network_code_for
                    AND product_code=p_product_code
                    FOR UPDATE;

                    IF SQL%NOTFOUND
                    THEN
                          DBMS_OUTPUT.PUT_LINE ('Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM );
                         rtn_messageforlog:='Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
                         rtn_sqlerrmsgforlog:=SQLERRM;
                         RAISE sqlexception;
                    END IF;

                    EXCEPTION
                       WHEN NO_DATA_FOUND
                       THEN
                          DBMS_OUTPUT.PUT_LINE ('Exception NO_DATA_FOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM );

                       WHEN OTHERS
                       THEN
                          DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id || SQLERRM );
                          rtn_messageforlog := 'OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
                          rtn_sqlerrmsgforlog := SQLERRM;
                          RAISE sqlexception;

             END;

             BEGIN

                     q_created_on  :=SYSDATE;
                  startCount := 1;
                  dateCounter:= q_daily_balance_updated_on;
                  dayDifference:= TRUNC(q_created_on) - q_daily_balance_updated_on;

                  DBMS_OUTPUT.PUT_LINE(' No Of dayDifference::'||dayDifference);


                FOR xyz IN startCount .. dayDifference
                LOOP


                     BEGIN


                       INSERT INTO USER_DAILY_BALANCES
                                  (balance_date,user_id,network_code,network_code_for,
                                    product_code,balance,prev_balance,last_transfer_type,
                                  last_transfer_no,last_transfer_on,created_on
                                  )
                              VALUES(dateCounter,q_user_id,q_network_code,
                                   q_network_code_for,q_product_code,q_balance,q_prev_balance,
                                   q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,
                                   q_created_on
                                  );
                      EXCEPTION
                        WHEN OTHERS
                        THEN
                           DBMS_OUTPUT.PUT_LINE ('Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id || SQLERRM );
                           rtn_messageforlog := 'Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id ;
                           rtn_sqlerrmsgforlog := SQLERRM;
                           RAISE sqlexception;



                    END;-- End of insert SQL

                    BEGIN

                        UPDATE USER_BALANCES SET
                               daily_balance_updated_on=q_created_on
                        WHERE user_id=p_user_id
                        AND product_code=p_product_code
                        AND network_code=p_network_code
                        AND network_code_for=p_network_code_for;

                        EXCEPTION
                           WHEN OTHERS
                           THEN
                              DBMS_OUTPUT.PUT_LINE ('Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id || SQLERRM );
                              rtn_messageforlog := 'Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id ;
                                 rtn_sqlerrmsgforlog := SQLERRM;
                              RAISE sqlexception;

                    END;-- End of update SQL

                 startCount:= startCount+1;
                dateCounter:= dateCounter+1;



           END LOOP;--End of daydiffrence loop

           COMMIT;
                DBMS_OUTPUT.PUT_LINE ('RECORDS COMMITED::'||p_user_id);

        END;--End oF Outer begin

            COMMIT;


     END LOOP;--End of outer for loop

                 rtn_message:='SUCCESS';
                 rtn_messageForLog :='PreTUPS USER_DAILY_CLOSING_BALANCE MIS successfully executed, Date Time:'||TO_CHAR(SYSDATE,'DD-fmMON-YY fmHH24:MI:SS');
                 rtn_sqlerrMsgForLog :=' ';

                 --UPDATE USER_DAILY_BAL_TEMP SET END_TIME=SYSDATE,STATUS_LOG=rtn_message WHERE PROCESS='USER_DAILY_CLOSING_BALANCE_PROCESS' AND trunc(START_TIME)=trunc(sysdate);
                 COMMIT;

         EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;

         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in USER_DAILY_CLOSING_BALANCE procedure:='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;



END;

--END; 

-------Procedure USER_DAILY_CLOSING_BALANCE-------------


INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'SOLDVCREPORT', '1'
            );			
			

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('SOLDVC001', 'VOMSREPORT', '/soldVCCard.do?method=loadsoldVCReport',
             'Detail of Sold VC Card', 'Y', 582, '2', '1',
             '/soldVCCard.do?method=loadsoldVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('SOLDVCDMM', 'VOMSREPORT', '/soldVCCard.do?method=loadsoldVCReport',
             'Detail of Sold VC Card', 'Y', 582, '1', '1',
             '/soldVCCard.do?method=loadsoldVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('SOLDVC01A', 'VOMSREPORT', '/soldVCCard.do?method=loadsoldVCReport',
             'Detail of Sold VC Card', 'N', 582, '2', '1',
             '/soldVCCard.do?method=loadsoldVCReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('SOLDVCREPORT', 'SOLDVC001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('SOLDVCREPORT', 'SOLDVCDMM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('SOLDVCREPORT', 'SOLDVC01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'SOLDVCREPORT', 'Detail of Sold VC Card',
             'Voucher Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );
			
			
			
		
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('P2P_DEBITCREDIT_COMMON', 'P2P API debit credit in same Request', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'P2P API debit credit in same Request', 'N', 
    'Y', 'C2S', 'P2P API debit credit in same Request', TO_DATE('06/16/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2019 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
COMMIT;


########### Voucher Delivery History #######
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMDLHDMM', 'VOMSREPORT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Voucher Delivery History', 'Y', 
    8, '1', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMDLH01A', 'VOMSREPORT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Voucher Delivery History', 'N', 
    8, '2', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMDLH001', 'VOMSREPORT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Voucher Delivery History', 'Y', 
    8, '2', '1', NULL);

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSDLHREPORT', 'VOMDLH001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSDLHREPORT', 'VOMDLH01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSDLHREPORT', 'VOMDLHDMM', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VOMSDLHREPORT', 'Voucher Delivery History', 'Voucher Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'VOMSDLHREPORT', '1');

COMMIT;

########Voucher sold summary Report ################

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMSLSM001', 'VOMSREPORT', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport', 'Voucher Sold Summary', 'Y', 
    9, '2', '1', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMSLSM01A', 'VOMSREPORT', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport', 'Voucher Sold Summary', 'N', 
    9, '2', '1', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMSLSMDMM', 'VOMSREPORT', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport', 'Voucher Sold Summary', 'Y', 
    9, '1', '1', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSLSUMMREPORT', 'VOMSLSM001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSLSUMMREPORT', 'VOMSLSM01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSLSUMMREPORT', 'VOMSLSMDMM', '1');


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VOMSLSUMMREPORT', 'Voucher Sold Summary', 'Voucher Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'VOMSLSUMMREPORT', '1');

COMMIT;


######### LOOK Up ENTRY - Voucher sold ##############

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('VSLTYPE', 'Voucher Sold Summary', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CITY', 'City', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SLDATE', 'Sold Date', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CHANNEL', 'Channel', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('BANK', 'Bank', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;


SET DEFINE OFF;
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('TTYPE', 'Terminal Type', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;


SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('02', 'ATM', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('03', 'Branchs Terminal', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('05', 'SMS', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('07', 'Telephone Bank', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('13', 'Web Kiosk', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('14', 'POS', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('59', 'Internet', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

SET DEFINE OFF;
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('SALESRPT', TO_DATE('06/06/2018 00:33:18', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('06/06/2018 06:14:32', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('06/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 0, 'Rightel Sales Daily Report', 'IR', 0);
COMMIT;

CREATE TABLE VOMS_DAILY_REPORT_DETAILS
(
  BATCH_ID           VARCHAR2(20 BYTE)          NOT NULL,
  BRANCH_CODE        VARCHAR2(9 BYTE),
  CITY_CODE          VARCHAR2(7 BYTE),
  TERMINAL_TYPE      VARCHAR2(3 BYTE),
  TERMINAL_CODE      VARCHAR2(9 BYTE),
  CREDIT_SALE_DATE   DATE,
  SERIAL_NO          VARCHAR2(16 BYTE),
  SERIAL_NO_CDIGIT   VARCHAR2(2 BYTE),
  PAYMENT_ID_NUMBER  VARCHAR2(12 BYTE),
  PAYMENT_TYPE       VARCHAR2(2 BYTE),
  TXN_ID             NUMBER(13),
  CARD_NUMBER        NUMBER(20),
  CREATED_BY         VARCHAR2(20 BYTE),
  CREATED_ON         DATE,
  MODIFIED_BY        VARCHAR2(20 BYTE),
  MODIFIED_ON        DATE,
  STATUS             VARCHAR2(10 BYTE),
  REMARKS            VARCHAR2(100 BYTE),
  EXTERNAL_CODE      VARCHAR2(20 BYTE),
  USER_ID            VARCHAR2(20 BYTE),
  VOUCHER_STATUS     VARCHAR2(10 BYTE),
  PRODUCT_ID         VARCHAR2(5 BYTE),
  DELIVER_DATE       DATE
)

CREATE TABLE VOMS_DAILY_REPORT_MASTER
(
  BATCH_ID          VARCHAR2(20 BYTE)           NOT NULL,
  USER_ID           VARCHAR2(15 BYTE)           NOT NULL,
  NETWORK_CODE      VARCHAR2(2 BYTE),
  NETWORK_CODE_FOR  VARCHAR2(2 BYTE),
  COMPANY_NAME      VARCHAR2(2 BYTE),
  OPERATOR_CODE     VARCHAR2(4 BYTE),
  BANK_CODE         VARCHAR2(3 BYTE),
  BATCH_FILE_NAME   VARCHAR2(100 BYTE),
  TOTAL_RECORD      NUMBER(12),
  TOTAL_AMOUNT      NUMBER(12),
  BATCH_DATE        DATE,
  CREATED_BY        VARCHAR2(20 BYTE),
  CREATED_ON        DATE,
  MODIFIED_BY       VARCHAR2(20 BYTE),
  MODIFIED_ON       DATE
)

drop table VOMS_DAILY_REPORT_DETAILS;
drop table VOMS_DAILY_REPORT_MASTER;

--This to be true only if external voucher uploaded in system
UPDATE SYSTEM_PREFERENCES  SET DEFAULT_VALUE='false' WHERE PREFERENCE_CODE='HASHING_ENABLE';
COMMIT;

---Date format for Response for API----
UPDATE SYSTEM_PREFERENCES
SET DEFAULT_VALUE='yyyy/MM/dd'
WHERE PREFERENCE_CODE='EXTERNAL_DATE_FORMAT';
commit;
------------------------------------

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PIN_REQUIRED_P2P', 'PIN Required for P2P', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Preference to define either PIN is required or not in P2P. Values may be TRUE or FALSE', 'N', 'Y', 'P2P', 'Preference to define either PIN is required or not in P2P. Values may be TRUE or FALSE', TIMESTAMP '2005-07-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-17 16:17:53.000000', 'SU0001', NULL, 'Y');
COMMIT;

ALTER TABLE SUBSCRIBER_CONTROL ADD VPIN_INVALID_COUNT NUMBER(5) DEFAULT 0;

-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi--------------
CREATE OR REPLACE PROCEDURE P2PDWHTEMPPRC 
(
 	   p_date IN DATE,
	   p_masterCnt OUT NUMBER,
	   p_transCnt  	  OUT 	 NUMBER,
	   p_message OUT VARCHAR2
)
IS

v_srno 				  		 	   NUMBER;
v_data							   VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION 					 EXCEPTION;

CURSOR   P2P_MASTER  IS
        SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||TO_CHAR(PS.LAST_TRANSFER_ON,'DD-fmMON-YY fmHH24:MI:SS')
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        TO_CHAR(PS.REGISTERED_ON,'DD-fmMON-YY fmHH24:MI:SS')||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||TO_CHAR(STR.transfer_date,'DD-fmMON-YY fmHH24:MI:SS')||','||TO_CHAR(STR.transfer_date_time,'DD-fmMON-YY fmHH24:MI:SS')||','
		||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        ||STR.INFO1||','||STR.INFO2||','||STR.INFO3||','||STR.INFO4||','||STR.INFO5||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END;
-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi--------------


ALTER TABLE
	SOS_SETTLEMENT_FAIL_RECCORDS DROP
		CONSTRAINT PK_SOS_SETTLE_FAIL_REC;
COMMIT;


--------Batch user initiate------
ALTER TABLE BATCHES MODIFY FILE_NAME VARCHAR2(35);
COMMIT;
------------------------------------