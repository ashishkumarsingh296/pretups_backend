UPDATE pages SET  spring_page_url='/zeroBalCounterDetail.do?method=loadUserBalance' WHERE page_code='ZBALDET001';
UPDATE pages SET  spring_page_url='/zeroBalCounterDetail.do?method=loadUserBalance' WHERE page_code='ZBALDET01A';
UPDATE pages SET  spring_page_url='/zeroBalCounterDetail.do?method=loadUserBalance' WHERE page_code='ZBALDETDMM';

UPDATE pages SET  spring_page_url='/c2sTransfer.do?method=loadUserBalance' WHERE page_code='ZBALDET001';
UPDATE pages SET  spring_page_url='/c2sTransfer.do?method=loadUserBalance' WHERE page_code='RPTTRCS01A';
UPDATE pages SET  spring_page_url='/c2sTransfer.do?method=loadUserBalance' WHERE page_code='RPTTRCSDMM';

UPDATE pages SET spring_page_url='/zeroBalCounterSummary.do?method=loadUserBalance' WHERE page_code='ZBALSUM001' ;
UPDATE pages SET spring_page_url='/zeroBalCounterSummary.do?method=loadUserBalance' WHERE page_code='ZBALSUMDMM' ;
UPDATE pages SET spring_page_url='/zeroBalCounterSummary.do?method=loadUserBalance' WHERE page_code='ZBALSUM01A' ;

UPDATE pages SET spring_page_url='/channelUserRole.do?method=loadO2cUserRoles' WHERE page_code='ROEU01A' ;
UPDATE pages SET spring_page_url='/channelUserRole.do?method=loadO2cUserRoles' WHERE page_code='ROEUDMM' ;
UPDATE pages SET spring_page_url='/channelUserRole.do?method=loadO2cUserRoles' WHERE page_code='ROEU001' ;

UPDATE pages SET spring_page_url='/o2cTransferAckAction.do?method=transferAckAuthorise' WHERE page_code='O2CACKDMM' ;
UPDATE pages SET spring_page_url='/o2cTransferAckAction.do?method=transferAckAuthorise' WHERE page_code='O2CACK001A' ;
UPDATE pages SET spring_page_url='/o2cTransferAckAction.do?method=transferAckAuthorise' WHERE page_code='O2CACK001' ;



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

UPDATE pages SET spring_page_url='/userDailyBalMovement.do?method=loadUserBalance' WHERE page_code='UBALMOV001';
UPDATE pages SET spring_page_url='/userDailyBalMovement.do?method=loadUserBalance' WHERE page_code='UBALMOV01A';
UPDATE pages SET spring_page_url='/userDailyBalMovement.do?method=loadUserBalance' WHERE page_code='UBALMOVDMM';
COMMIT;

--operation Summary Report
update pages set spring_page_url = '/operationSummaryAction.do?method=loadUserSummary' where page_code = 'OPTSRPT001';

update pages set spring_page_url = '/operationSummaryAction.do?method=loadUserSummary' where page_code = 'OPTSRPT00A';

update pages set spring_page_url = '/operationSummaryAction.do?method=loadUserSummary' where page_code = 'OPTSRPTDMM';


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

---Rollback for Staff self c2c reports
UPDATE pages SET spring_page_url='/staffSelfC2CReport.do?method=loadStaffC2cTransferDetails' WHERE page_code='STFSLF01A' ;
UPDATE pages SET spring_page_url='/staffSelfC2CReport.do?method=loadStaffC2cTransferDetails' WHERE page_code='STFSLF001' ;
UPDATE pages SET spring_page_url='/staffSelfC2CReport.do?method=loadStaffC2cTransferDetails' WHERE page_code='STFSLFDMM' ;


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

Commit;


--View Schedule Rollback
---------------------------------------------------------------------------------------------------------

UPDATE pages SET page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH001';

UPDATE pages SET page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH01A';

UPDATE pages SET page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCHDMM';

UPDATE pages SET page_url='/jsp/restrictedsubs/viewSingleScheduleRecharge.jsp', spring_page_url='/jsp/restrictedsubs/viewSingleScheduleRecharge.jsp' WHERE page_code='VIEWSCH002';

--------------View Schedule batch Rollback-------------------
UPDATE pages SET page_url='/restrictedsubs/view_schedule_rc_batch.form', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR01';


UPDATE pages SET page_url='/restrictedsubs/view_schedule_rc_batch.form', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR1A';


------------------view  network Rollback---------------------
UPDATE pages SET  page_url='/network/network_view_action.form', spring_page_url='/network/network_view_action.form' WHERE page_code='NW3001';


UPDATE pages SET  page_url='/network/viewNetworkListSpring.form',  spring_page_url='/network/viewNetworkListSpring.form' WHERE page_code='NW3002';


UPDATE pages SET  page_url='/network/network_view_action.form', spring_page_url='/networkViewAction.do?method=loadNetworkListForView&page=0' WHERE page_code='NW3Dmm';

---------------Network status : Rollback struts and spring compatibility----------------------

UPDATE pages SET page_url='/network/network_Status.form', spring_page_url='/network/network_Status.form' WHERE page_code='NS001';


UPDATE pages SET page_url='/network/save-network-status.form',spring_page_url='/network/save-network-status.form' WHERE page_code='NS002';

--------------P2P DWH file creation info tag rollback------

CREATE OR REPLACE FUNCTION pretupsdatabase.p2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_transcnt integer, OUT p_message character varying)
 RETURNS record
 LANGUAGE plpgsql
AS $function$
DECLARE 

v_srno 	 INT;
v_data	VARCHAR (1000);



  DECLARE P2P_MASTER CURSOR  FOR
			SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||

	case when TO_CHAR(PS.LAST_TRANSFER_ON,'DD/MM/YYYY HH12:MI:SS') is null then COALESCE(to_char(PS.LAST_TRANSFER_ON, 'DD/MM/YYYY HH12:MI:SS'), '') else TO_CHAR(PS.LAST_TRANSFER_ON,'DD/MM/YYYY HH12:MI:SS') end ||','||
	--case when PS.LAST_TRANSFER_ON is null then '' else PS.LAST_TRANSFER_ON end ||','||

	REPLACE(case when  KV.VALUE is null then '' else  KV.VALUE end,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||
         case when PS.CREDIT_LIMIT is null then '' else PS.CREDIT_LIMIT::varchar(10) end||','||
        PS.REGISTERED_ON||','||case when  PS.LAST_TRANSFER_ID is null then '' else PS.LAST_TRANSFER_ID end||','
       ||  case when PS.LAST_TRANSFER_MSISDN is null then '' else PS.LAST_TRANSFER_MSISDN end||','
        ||PS.LANGUAGE||','||case when PS.COUNTRY is null then '' else PS.COUNTRY end||','
        ||REPLACE(case when PS.USER_NAME is null then '' else PS.USER_NAME end,',',' ')||','
        FROM KEY_VALUES KV right outer join  P2P_SUBSCRIBERS PS on PS.LAST_TRANSFER_STATUS=KV.KEY AND 'P2P_STATUS'=KV.TYPE ,LOOKUPS LK  
        WHERE  date_trunc('day',PS.ACTIVATED_ON::TIMESTAMP) < p_date AND LK.LOOKUP_CODE = PS.STATUS AND LK.LOOKUP_TYPE = 'SSTAT'  AND  PS.STATUS IN('Y','S') ;

 DECLARE P2P_TRANS CURSOR FOR
		SELECT STR.transfer_id||','||STR.transfer_date||','||STR.transfer_date_time||','||TRI1.msisdn||','||TRI2.msisdn||','
		||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
		||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
		||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
		||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
		||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
		||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
		FROM    KEY_VALUES KV1 right outer join TRANSFER_ITEMS TRI1 on TRI1.transfer_status= KV1.KEY AND KV1.TYPE = 'P2P_STATUS' , 
        KEY_VALUES KV2 right outer join TRANSFER_ITEMS TRI2 on TRI2.transfer_status=KV2.KEY AND KV2.TYPE = 'P2P_STATUS', 
        KEY_VALUES KV right outer join SUBSCRIBER_TRANSFERS STR on STR.transfer_status= KV.KEY AND KV.TYPE = 'P2P_STATUS' 
		WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
		AND TRI2.sno = 2 AND STR.transfer_date = p_date;

		BEGIN
			
	   		RAISE NOTICE '%','Start P2P DWH PROC1';
	   		
			v_srno := 0;
			v_data	:= NULL;

RAISE NOTICE '%','Start P2P DWH PROC..............100';
			DELETE from temp_p2p_dwh_master;
			DELETE from temp_p2p_dwh_trans;
	
			RAISE NOTICE '%','Start P2P DWH PROC..............1';

		   OPEN P2P_MASTER;
		   LOOP
		   RAISE NOTICE '%','Start P2P DWH PROC..............inside loop 1';
			FETCH  P2P_MASTER INTO v_data;
			    IF NOT FOUND THEN EXIT;
                            END IF;
			 RAISE NOTICE '%','Start P2P DWH PROC..............outside if 1';
			v_srno := v_srno+1; 
			 RAISE NOTICE '%','Start P2P DWH PROC..............before inseert 1';
			INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
			VALUES (v_srno, v_data);
			raise notice '%','i am here:6=';

			IF (MOD(v_srno , 10000) = 0)
			THEN  COMMIT; 
			END IF;
			
		  END LOOP;
		  CLOSE P2P_MASTER;

			p_masterCnt := v_srno;
			RAISE NOTICE '%','p_masterCnt = '||p_masterCnt;
			v_srno := 0;
			v_data	:= NULL;

		   OPEN P2P_TRANS;
		   LOOP
		   RAISE NOTICE '%','Start P2P DWH PROC..............inside loop 2';
			FETCH P2P_TRANS INTO v_data;
			    IF NOT FOUND THEN EXIT;
                            END IF;
			
			v_srno := v_srno+1;
			INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
			VALUES (v_srno, v_data);

			END LOOP;
			CLOSE P2P_TRANS;

			p_transCnt :=v_srno;
			RAISE NOTICE '%','p_transCnt = '||p_transCnt;

		/* COMMIT; */
		RAISE NOTICE '%','P2P DWH PROC Completed';
		p_message:='SUCCESS';

		EXCEPTION
		WHEN OTHERS THEN
		
			 p_message:='Not able to migrate data, Exception occoured';
	RAISE EXCEPTION 'Not able to migrate data, Exception occoured';


END;
$function$

ALTER TABLE SUBSCRIBER_TRANSFERS DROP COLUMN VOUCHER_SERIAL_NUMBER ;
ALTER TABLE SUBSCRIBER_TRANSFERS DROP COLUMN INFO1,
DROP column INFO2 ,
DROP column INFO3 ,
DROP column INFO4 ,
DROP column INFO5 ;

ALTER TABLE CHANNEL_TRANSFERS DROP column INFO3 ,
DROP column INFO4 ,
DROP column INFO5 ;

--Staff C2C transfer details
UPDATE pages SET spring_page_url='/staffC2CTrfRetWid.do?method=loadStaffC2cTransferDetails' WHERE page_code='STFC2CDMM' ;
UPDATE pages SET spring_page_url='/staffC2CTrfRetWid.do?method=loadStaffC2cTransferDetails' WHERE page_code='STFC2C00A' ;
UPDATE pages SET spring_page_url='/staffC2CTrfRetWid.do?method=loadStaffC2cTransferDetails' WHERE page_code='STFC2C001' ;

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

-----Sold voucher impact changes - Tejeshvi ---------------
alter table VOMS_VOUCHERS DROP COLUMN SOLD_DATE;
alter table VOMS_VOUCHERS DROP COLUMN SOLD_STATUS;

DELETE FROM LOOKUPS WHERE LOOKUP_CODE = 'SL' AND LOOKUP_TYPE = 'VSTAT';
-----Sold voucher impact changes - Tejeshvi ---------------

ALTER TABLE SOS_TRANSACTION_DETAILS DROP COLUMN INFO1,
DROP column INFO2 ,
DROP column INFO3 ,
DROP column INFO4 ,
DROP column INFO5 ;

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


--This to be true only if external voucher uploaded in system
UPDATE SYSTEM_PREFERENCES  SET DEFAULT_VALUE='false' WHERE PREFERENCE_CODE='HASHING_ENABLE';
COMMIT;

delete from SYSTEM_PREFERENCES where preference_code='PIN_REQUIRED_P2P';
COMMIT;

ALTER TABLE SUBSCRIBER_CONTROL DROP  COLUMN  VPIN_INVALID_COUNT ;
COMMIT;