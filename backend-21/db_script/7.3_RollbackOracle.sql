
UPDATE PAGES SET  SPRING_PAGE_URL='/zeroBalCounterDetail.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALDET001';
UPDATE PAGES SET  SPRING_PAGE_URL='/zeroBalCounterDetail.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALDET01A';
UPDATE PAGES SET  SPRING_PAGE_URL='/zeroBalCounterDetail.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALDETDMM';

UPDATE PAGES SET  SPRING_PAGE_URL='/c2sTransfer.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALDET001';
UPDATE PAGES SET  SPRING_PAGE_URL='/c2sTransfer.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALDET01A';
UPDATE PAGES SET  SPRING_PAGE_URL='/c2sTransfer.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALDETDMM';


UPDATE PAGES SET SPRING_PAGE_URL='/zeroBalCounterSummary.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALSUM001' ;
UPDATE PAGES SET SPRING_PAGE_URL='/zeroBalCounterSummary.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALSUMDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/zeroBalCounterSummary.do?method=loadUserBalance' WHERE PAGE_CODE='ZBALSUM01A' ;

UPDATE PAGES SET SPRING_PAGE_URL='/channelUserRole.do?method=loadO2cUserRoles' WHERE PAGE_CODE='ROEU01A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/channelUserRole.do?method=loadO2cUserRoles' WHERE PAGE_CODE='ROEUDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/channelUserRole.do?method=loadO2cUserRoles' WHERE PAGE_CODE='ROEU001' ;



UPDATE PAGES SET SPRING_PAGE_URL='/o2cTransferAckAction.do?method=transferAckAuthorise' WHERE PAGE_CODE='O2CACK001A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/o2cTransferAckAction.do?method=transferAckAuthorise' WHERE PAGE_CODE='O2CACK001' ;
UPDATE PAGES SET SPRING_PAGE_URL='/o2cTransferAckAction.do?method=transferAckAuthorise' WHERE PAGE_CODE='O2CACKDMM' ;

--ChangePin
INSERT INTO pages (page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url)
VALUES('CHNGPINCU1', 'CUSERS', '/changePinAction.do?method=loadDomainList', 'Change PIN', 'Y', 30, '2', '1', '/user/change-pin.form');

INSERT INTO page_roles(role_code, page_code, application_id) VALUES('CHANGEPINCU', 'CHNGPINCU1', '1 ');

UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='SE' AND role_code='CHANGEPIN';
UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='AG' AND role_code='CHANGEPIN';
UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='DIST' AND role_code='CHANGEPIN';
UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='RET' AND role_code='CHANGEPIN';

UPDATE roles SET role_code='CHANGEPINCU' WHERE role_code='CHANGEPIN' AND domain_type='DISTB_CHAN';
COMMIT;

--UserBalance
INSERT INTO pages(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url)
VALUES('CUSRBALCU', 'C2SENQ', '/channelUserBalanceAction.do?method=loadDomainList', 'Other''s Balance', 'Y', 30, '2', '1', '/balances/userBalance.form');


INSERT INTO page_roles(role_code, page_code, application_id) VALUES('OTHERBALANCECU', 'CUSRBALCU', '1');


UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='SE' AND role_code='OTHERBALANCE';
UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='AG' AND role_code='OTHERBALANCE';
UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='DIST' AND role_code='OTHERBALANCE';
UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='RET' AND role_code='OTHERBALANCE';

UPDATE roles SET role_code='OTHERBALANCECU' WHERE role_code='OTHERBALANCE' AND domain_type='DISTB_CHAN';
COMMIT;


UPDATE pages
SET spring_page_url='/changePinAction.do?method=loadDomainList' WHERE page_code='CHNGPIN001';

UPDATE pages
SET spring_page_url='/channelUserBalanceAction.do?method=loadDomainList' WHERE page_code='CUSRBALV01';
COMMIT;

UPDATE pages
SET spring_page_url='/additionalCommissionSummary.do?method=loadC2CUser' WHERE page_code='RPTADCS001';
COMMIT;

UPDATE PAGES SET SPRING_PAGE_URL='/userDailyBalMovement.do?method=loadUserBalance' WHERE page_code='UBALMOV001';
UPDATE PAGES SET SPRING_PAGE_URL='/userDailyBalMovement.do?method=loadUserBalance' WHERE page_code='UBALMOV01A';
UPDATE PAGES SET SPRING_PAGE_URL='/userDailyBalMovement.do?method=loadUserBalance' WHERE page_code='UBALMOVDMM';
COMMIT;

--operation Summary Report
update pages set spring_page_url = '/operationSummaryAction.do?method=loadUserSummary' where page_code = 'OPTSRPT001';
commit;
update pages set spring_page_url = '/operationSummaryAction.do?method=loadUserSummary' where page_code = 'OPTSRPT00A';
commit;
update pages set spring_page_url = '/operationSummaryAction.do?method=loadUserSummary' where page_code = 'OPTSRPTDMM';
commit;

--Rollback to UNDO the changes for support of struts in BAR USER module_code
UPDATE pages SET  page_url='/baruser/barreduser.form', spring_page_url='/baruser/barreduser.form' WHERE page_code='BAR01';

UPDATE pages SET page_url='/subscriber/confirmBarredUser.jsp', spring_page_url='/subscriber/confirmBarredUser.jsp' WHERE page_code='BAR02';

UPDATE pages SET  page_url='/baruser/barreduser.form?method=loadBarredUser', spring_page_url='/baruser/barreduser.form?method=loadBarredUser' WHERE page_code='BAR1Dmm';


UPDATE pages SET page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/barreduser.do?method=loadBarredUser'
WHERE page_code='BAR01A';
COMMIT;


------ Rollback for Struts & Spring support for UNBAR USER screen
UPDATE pages SET page_url='/baruser/unbaruser.form', spring_page_url='/baruser/unbaruser.form' WHERE page_code='UNBAR01';

UPDATE pages SET  page_url='/subscriber/selectUserToUnbarr.jsp',spring_page_url='/subscriber/selectUserToUnbarr.jsp' WHERE page_code='UNBAR02';

UPDATE pages SET page_url='/baruser/unbaruser.form',spring_page_url='/baruser/unbaruser.form' WHERE page_code='UNBAR1Dmm';

UPDATE pages SET page_url='/unbaruser.do?method=unBarUser',spring_page_url='/unbaruser.do?method=unBarUser' WHERE page_code='UNBAR01A';

--Rollback for Struts & Spring support for view barred list screen
UPDATE pages SET page_url='/baruser/viewBarredUserAction.form',  spring_page_url='/baruser/viewBarredUserAction.form' WHERE page_code='VIEWBAR01';

UPDATE pages SET  page_url='/viewBarredUserAction.do?method=viewBarredList', spring_page_url='/viewBarredUserAction.do?method=viewBarredList' WHERE page_code='VIEWBAR01A';

UPDATE pages SET  page_url='/baruser/viewBarredUserAction.form', spring_page_url='/baruser/viewBarredUserAction.form' WHERE page_code='VIEWBARDmm';

UPDATE pages SET  page_url='/jsp/subscriber/viewBarredList.jsp',spring_page_url='/jsp/subscriber/viewBarredList.jsp' WHERE page_code='VIEWBAR02';

---Staff self c2c reports
UPDATE PAGES SET SPRING_PAGE_URL='/staffSelfC2CReport.do?method=loadStaffC2cTransferDetails' WHERE PAGE_CODE='STFSLF01A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/staffSelfC2CReport.do?method=loadStaffC2cTransferDetails' WHERE PAGE_CODE='STFSLFDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/staffSelfC2CReport.do?method=loadStaffC2cTransferDetails' WHERE PAGE_CODE='STFSLF001' ;


--Associate Profile
UPDATE pages
SET spring_page_url='/userChannelAssociateAction.do?method=loadDomainList&page=0' WHERE page_code='ASSCUSR001';
COMMIT;

DELETE FROM LOOKUPS WHERE LOOKUP_TYPE ='COMMT';

DELETE FROM LOOKUP_TYPES WHERE LOOKUP_TYPE ='COMMT';

ALTER TABLE COMMISSION_PROFILE_SET_VERSION DROP COLUMN DUAL_COMM_TYPE;

ALTER TABLE CHANNEL_TRANSFERS DROP COLUMN DUAL_COMM_TYPE;

ALTER TABLE FOC_BATCH_ITEMS DROP COLUMN DUAL_COMM_TYPE;

ALTER TABLE O2C_BATCH_ITEMS DROP COLUMN DUAL_COMM_TYPE;

ALTER TABLE C2C_BATCH_ITEMS DROP COLUMN DUAL_COMM_TYPE;
COMMIT;

--Commission profile rollback :Spring to Struts
-----------------------------------------------------
UPDATE pages SET page_url='/commission-profile/status.form',spring_page_url='/commission-profile/status.form'
WHERE page_code='COMMPS001';

--Schedule RC Rollback script :Spring and struts compatibility
---------------------------------------------------------------
UPDATE pages SET  page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUP01';

UPDATE pages SET  page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUP1A';

UPDATE pages SET page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUPDM';


UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp', spring_page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp' WHERE page_code='SCHTOPUP02';

--------------------------------------Other Commision ---------------

ALTER TABLE COMMISSION_PROFILE_SET_VERSION drop 
 (OTH_COMM_PRF_SET_ID  );
 
 ALTER TABLE CHANNEL_TRANSFERS  drop (OTH_COMM_PRF_SET_ID  VARCHAR2(30 BYTE));
 
 ALTER TABLE CHANNEL_TRANSFERS_ITEMS
 DROP (
  OTH_COMMISSION_TYPE               ,
  OTH_COMMISSION_RATE               ,
  OTH_COMMISSION_VALUE              
  );


ALTER TABLE OTHER_COMM_PRF_SET DROP PRIMARY KEY CASCADE;
DROP TABLE OTHER_COMM_PRF_SET CASCADE CONSTRAINTS;

ALTER TABLE OTHER_COMM_PRF_DETAILS DROP PRIMARY KEY CASCADE;
DROP TABLE OTHER_COMM_PRF_DETAILS CASCADE CONSTRAINTS;


delete from  LOOKUP_TYPES where LOOKUP_TYPE='OTCTP';

delete from  LOOKUPS where LOOKUP_TYPE='OTCTP';  

delete from PAGES where PAGE_CODE in ('OCOMP001','OCOMP01A','OCOMPDMM','OCOMP005','OCOMP004','OCOMP003','OCOMP002') ;

 
delete from ROLES where role_code='OTHCOMPROMGMT';

delete from PAGE_ROLES where role_code='OTHCOMPROMGMT';

delete from CATEGORY_ROLES where role_code='OTHCOMPROMGMT';

delete  SYSTEM_PREFERENCES where PREFERENCE_CODE ='OTH_COM_CHNL'; 

delete  IDS where ID_TYPE in ('OT_COM_SID','OT_COM_DID');

Commit;


	
=======
UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp', spring_page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp' WHERE page_code='SCHTOPUP02';

ALTER TABLE USERS DROP COLUMN MIGRATION_STATUS;



--Reschedule Rollback Batch Recharge :Spring to Struts compatibility
---------------------------------------------------------------------

UPDATE pages SET  page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUP01';

UPDATE pages SET  page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUP1A';

UPDATE pages SET  page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUPDM';

UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp', spring_page_url='/jsp/restrictedsubs/rescheduleTopUpDetails.jsp' WHERE page_code='RSHTOPUP02';


--Cancel Schedule TopUp Rollback :Spring to Struts compatibility
--------------------------------------------------------------------
UPDATE pages SET  page_url='/restrictedsubs/cancel_schedule_recharge.form', spring_page_url='/restrictedsubs/cancel_schedule_recharge.form' WHERE page_code='CNCLSCH001';

UPDATE pages SET  page_url='/restrictedsubs/cancel_schedule_recharge_details.form',  spring_page_url='/restrictedsubs/cancel_schedule_recharge_details.form' WHERE page_code='CNCLSCH002';

UPDATE pages SET  page_url='/restrictedsubs/cancel_schedule_recharge_viewMsisdn.form', spring_page_url='/restrictedsubs/cancel_schedule_recharge_viewMsisdn.form' WHERE page_code='CNCLSCH003';

UPDATE pages SET  page_url='/restrictedsubs/cancel_schedule_recharge.form', spring_page_url='/restrictedsubs/cancel_schedule_recharge.form' WHERE page_code='CNCLSCH004';


--Cancel Schedule Batch Rollback :Spring to Struts compatibility
--------------------------------------------------------------------
UPDATE pages SET  page_url='/restrictedsubs/cancel_batch_schedule_recharge.form', spring_page_url='/restrictedsubs/cancel_batch_schedule_recharge.form' WHERE page_code='CNSCHTR01';

UPDATE pages SET  page_url='/restrictedsubs/cancel_schedule_recharge_batch.form', spring_page_url='/restrictedsubs/cancel_schedule_recharge_batch.form' WHERE page_code='CNSCHTR02';


drop table tps_details;
delete from service_type where service_type='MAXTPS';
delete from SERVICE_KEYWORDS where KEYWORD='MAXTPSHOURLYREQ';

-- IRIS Changes Required 
ALTER TABLE DAILY_C2S_TRANS_DETAILS DROP COLUMN Promo_count ;
ALTER TABLE DAILY_C2S_TRANS_DETAILS DROP COLUMN Promo_amount;
ALTER TABLE C2S_TRANSFERS DROP COLUMN BONUS_AMOUNT; 
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS DROP COLUMN Promo_count;
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS DROP COLUMN Promo_amount;
Delete from  LOOKUPS where lookup_code in ('DIFF','PROMO','IRIS');
update SERVICE_TYPE set status='Y',REQUEST_HANDLER='com.btsl.pretups.channel.transfer.requesthandler.C2SPrepaidReversalController' where service_type='RCREV';

commit;


--View Schedule Rollback
---------------------------------------------------------------------------------------------------------

UPDATE pages SET page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH001';

UPDATE pages SET page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH01A';

UPDATE pages SET page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCHDMM';

UPDATE pages SET page_url='/jsp/restrictedsubs/viewSingleScheduleRecharge.jsp', spring_page_url='/jsp/restrictedsubs/viewSingleScheduleRecharge.jsp' WHERE page_code='VIEWSCH002';

--------------View Schedule Batch Rollback-------------------
UPDATE pages SET page_url='/restrictedsubs/view_schedule_rc_batch.form', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR01';


UPDATE pages SET page_url='/restrictedsubs/view_schedule_rc_batch.form', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR1A';

------------------view  network Rollback :Struts and Spring compatibility---------------------
UPDATE pages SET  page_url='/network/network_view_action.form', spring_page_url='/network/network_view_action.form' WHERE page_code='NW3001';


UPDATE pages SET  page_url='/network/viewNetworkListSpring.form',  spring_page_url='/network/viewNetworkListSpring.form' WHERE page_code='NW3002';


UPDATE pages SET  page_url='/network/network_view_action.form', spring_page_url='/networkViewAction.do?method=loadNetworkListForView&page=0' WHERE page_code='NW3Dmm';

---------------Network status : Rollback struts and spring compatibility----------------------

UPDATE pages SET page_url='/network/network_Status.form', spring_page_url='/network/network_Status.form' WHERE page_code='NS001';


UPDATE pages SET page_url='/network/save-network-status.form',spring_page_url='/network/save-network-status.form' WHERE page_code='NS002';

--------------P2P DWH file creation info tag rollback------
CREATE OR REPLACE PROCEDURE PRETUPS73.P2PDWHTEMPPRC 
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



ALTER TABLE SUBSCRIBER_TRANSFERS DROP  COLUMN  VOUCHER_SERIAL_NUMBER ;
ALTER TABLE SUBSCRIBER_TRANSFERS DROP (INFO1 ,INFO2 ,INFO3 ,INFO4 ,INFO5 );
ALTER TABLE CHANNEL_TRANSFERS DROP ( INFO3 ,INFO4 ,INFO5 );

UPDATE PAGES  SET SPRING_PAGE_URL='/staffC2CTrfRetWid.do?method=loadStaffC2cTransferDetails' WHERE PAGE_CODE='STFC2C001' ;
UPDATE PAGES  SET SPRING_PAGE_URL='/staffC2CTrfRetWid.do?method=loadStaffC2cTransferDetails' WHERE PAGE_CODE='STFC2C00A' ;
UPDATE PAGES  SET SPRING_PAGE_URL='/staffC2CTrfRetWid.do?method=loadStaffC2cTransferDetails' WHERE PAGE_CODE='STFC2CDMM' ;


--fraud management
ALTER TABLE P2P_SUBSCRIBERS_COUNTERS DROP  COLUMN  VPIN_INVALID_COUNT ;
DELETE FROM system_preferences WHERE preference_code='VPIN_INVALID_COUNT';
DELETE FROM system_preferences WHERE preference_code='VOMS_PIN_BLK_EXP_DRN';

--Persian Calendar--
DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE IN ('DATE_FORMAT_CAL_JAVA', 'DATE_TIME_FORMAT', 'LOCALE_CALENDAR', 'LOCALE_ENGLISH','TIMEZONE_ID','CALENDAR_TYPE','CALENDER_DATE_FORMAT','CALENDAR_SYSTEM','FORMAT_MONTH_YEAR','EXTERNAL_CALENDAR_TYPE','IS_CAL_ICON_VISIBLE','IS_MON_DATE_ON_UI');
UPDATE SYSTEM_PREFERENCES SET DEFAULT_VALUE = '3' WHERE PREFERENCE_CODE = 'FINANCIAL_YEAR_START';
COMMIT;


drop table USER_VOUCHERTYPES;
delete from SYSTEM_PREFERENCES where PREFERENCE_CODE='USER_VOUCHERTYPE_ALLOWED';
COMMIT;

--- Claro Colombia code merge - Begin
DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE IN ('USER_CLOSING_BALANCE_REPORT_FROM_AMOUNT', 'USER_CLOSING_BALANCE_REPORT_TO_AMOUNT', 'MAX_HOST_TEXTBOX', 'COMMA_ALLOW_IN_LOGIN', 'XML_DOC_ENCODING', 'LAST_X_C2S_TXNSTATUS_ALLOWED', 'LAST_X_CHNL_TXNSTATUS_ALLOWED', 'CAT_GATEWAY_PRODUCT_PREF', 'WIRC_ACCOUNT_MSISDN_OPT', 'USER_EXTERNAL_CODE_DOMAINWISE', 'LOAD_BAL_IP_ALLOWED', 'CHK_LAST_TXN_BY_USER_PHONES', 'DISPLAY_LANGUAGE', 'DISPLAY_COUNTRY', 'DEL_CUSER_WITH_BALANCE_ALLOWED', 'ALWD_SRVCS_TO_FAIL_WHEN_AMBIGUOUS', 'USSD_RC_LANG_PARAM_REQ', 'VOUCHER_THIRDPARTY_STATUS');
COMMIT;
--- Claro Colombia code merge - End

delete from user_vouchertypes uv where uv.USER_ID = 'SU0001';

DELETE FROM system_preferences WHERE preference_code='VOMS_DAMG_PIN_LNTH_ALLOW';


delete from PAGES where PAGE_CODE in ('VOMVC001','VOMVCDMM','VOMVC01A') ;

 
delete from ROLES where role_code='VCDETAILREPORT';

delete from PAGE_ROLES where role_code='VCDETAILREPORT';

delete from CATEGORY_ROLES where role_code='VCDETAILREPORT';

COMMIT:

-----Sold voucher impact changes - Tejeshvi ---------------
alter table VOMS_VOUCHERS DROP COLUMN SOLD_DATE;
alter table VOMS_VOUCHERS DROP COLUMN SOLD_STATUS;

DELETE FROM LOOKUPS WHERE LOOKUP_CODE = 'SL' AND LOOKUP_TYPE = 'VSTAT';
-----Sold voucher impact changes - Tejeshvi ---------------



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

ALTER TABLE SOS_TRANSACTION_DETAILS DROP (INFO1 ,INFO2 ,INFO3 ,INFO4 ,INFO5 );
COMMIT;

delete from PAGES where PAGE_CODE in ('SOLDVC001','SOLDVCDMM','SOLDVC01A') ;

delete from ROLES where role_code='SOLDVCREPORT';

delete from PAGE_ROLES where role_code='SOLDVCREPORT';

delete from CATEGORY_ROLES where role_code='SOLDVCREPORT';

delete from PAGES where PAGE_CODE in ('VOMDLH001','VOMDLH01A','VOMDLHDMM','VOMSLSM001','VOMSLSM01A','VOMSLSMDMM') ;

delete from ROLES where role_code='VOMSDLHREPORT';

delete from PAGE_ROLES where role_code='VOMSDLHREPORT';

delete from CATEGORY_ROLES where role_code='VOMSDLHREPORT';

delete from ROLES where role_code='VOMSLSUMMREPORT';

delete from PAGE_ROLES where role_code='VOMSLSUMMREPORT';

delete from CATEGORY_ROLES where role_code='VOMSLSUMMREPORT';


delete from LOOKUPS where LOOKUP_TYPE='VSLTYPE';
delete from LOOKUP_TYPES where LOOKUP_TYPE='VSLTYPE';

COMMIT;

delete from LOOKUPS where LOOKUP_TYPE='TTYPE';
delete from LOOKUP_TYPES where LOOKUP_TYPE='TTYPE';
delete from PROCESS_STATUS where PROCESS_ID='SALESRPT';

drop table VOMS_DAILY_REPORT_DETAILS;
drop table VOMS_DAILY_REPORT_MASTER;

--This to be true only if external voucher uploaded in system
UPDATE SYSTEM_PREFERENCES  SET DEFAULT_VALUE='false' WHERE PREFERENCE_CODE='HASHING_ENABLE';
COMMIT;

delete from SYSTEM_PREFERENCES where PREFERENCE_CODE='PIN_REQUIRED_P2P';
COMMIT;

ALTER TABLE SUBSCRIBER_CONTROL DROP  COLUMN  VPIN_INVALID_COUNT ;
COMMIT;