---DEF1016 fixed---

update roles set status='N' where role_code='BONUSENQY';
COMMIT;

---DEF2285 fixed----

update service_type set MODULE='OPT' where service_type='USERDEL';
COMMIT;

--function DEF 948 FIXED

CREATE OR REPLACE FUNCTION "GETUSERROLES" 
--This function is used for generate report for assigned roles
--(p_userId  VARCHAR2) RETURN CLOB
--p_roleName(if Y then role_name else role_code)
(p_userId  VARCHAR2,p_roleName VARCHAR2) RETURN VARCHAR
IS
--p_userRoles CLOB;
p_userRoles VARCHAR(32767) DEFAULT '';
role_name_code VARCHAR2(100) DEFAULT '';
role_type VARCHAR2(1);
group_role VARCHAR2(1) DEFAULT '';
oldGroupName VARCHAR2(100)DEFAULT '';
newGroupName VARCHAR2(100)DEFAULT '';
var NUMBER(5) DEFAULT 0;
CURSOR c_userRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE r.role_code=ur.role_code
            AND ur.user_id=p_userId
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
CURSOR c_userGroupRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name ,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,GROUP_ROLES gr,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE ur.ROLE_CODE=gr.GROUP_ROLE_CODE
            AND gr.ROLE_CODE=r.ROLE_CODE
            AND ur.user_id=p_userId
            AND r.status='Y'
            --AND cr.CATEGORY_CODE=u.CATEGORY_CODE
            --AND cr.ROLE_CODE=r.ROLE_CODE
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;

CURSOR c_userFixRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,TRIM(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USERS ur,CATEGORIES C,CATEGORY_ROLES CR ,ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,DOMAINS D
            WHERE ur.user_id=p_userId
            AND ur.CATEGORY_CODE=C.CATEGORY_CODE
            AND C.FIXED_ROLES='Y'
            AND C.CATEGORY_CODE=CR.CATEGORY_CODE
            AND CR.ROLE_CODE=R.ROLE_CODE
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MODULE_CODE=m.MODULE_CODE
            AND p.MENU_LEVEL=1
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
BEGIN
       oldGroupName:='###';
       FOR tr IN c_userRoles(p_userId)
       LOOP
               role_type:=tr.GROUP_ROLE;
             IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr.role_name;
            ELSE
               role_name_code:=tr.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
                  newGroupName:=UPPER(trim(tr.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
               IF newGroupName <> oldGroupName THEN
                     oldGroupName:=newGroupName;
                  IF p_roleName LIKE 'Y' THEN
                          p_userRoles:=p_userRoles||'<b>'||'('||tr.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                  ELSE
                       p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
               ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
               END IF;
            END IF;

        END LOOP;
        FOR tr1 IN c_userGroupRoles(p_userId)
        LOOP
            role_type:=tr1.GROUP_ROLE;
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr1.role_name;
            ELSE
               role_name_code:=tr1.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
               --p_userRoles:=p_userRoles||to_clob(tr.role_name)||',';
                newGroupName:=UPPER(trim(tr1.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
                IF newGroupName <> oldGroupName THEN
                   oldGroupName:=newGroupName;
                   IF p_roleName LIKE 'Y' THEN
                         p_userRoles:=p_userRoles||'<b>'||'('||tr1.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                   ELSE
                         p_userRoles:=p_userRoles||role_name_code||', ';
                   END IF;
                ELSE
                    p_userRoles:=p_userRoles||role_name_code||', ';
                END IF;
              END IF;
        END LOOP;
        FOR tr2 IN c_userFixRoles(p_userId)
        LOOP
            role_type:='F';
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr2.role_name;
            ELSE
               role_name_code:=tr2.ROLE_CODE;
            END IF;
            newGroupName:=UPPER(trim(tr2.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
            IF newGroupName <> oldGroupName THEN
               oldGroupName:=newGroupName;
               IF p_roleName LIKE 'Y' THEN
                     p_userRoles:=p_userRoles||'<b>'||'('||tr2.MODULE_NAME||')'||'</b>'||role_name_code||', ';
               ELSE
                          p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
            ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
            END IF;

          END LOOP;

      IF LENGTH(p_userRoles) > 0 THEN
         p_userRoles:=SUBSTR(p_userRoles,0,LENGTH(p_userRoles)-2);
      END IF;
      --p_userRoles:='<div align="left" style="white-space: 0; letter-spacing: 0; " >'||p_userRoles||'</div>';

      RETURN p_userRoles;
END;.

--Defect 2371 fix for removing o2c from superadmin

delete from page_roles where role_code='BTO2CAPR2' and page_code='BATO2C0002';
delete from category_roles where role_code='BTO2CAPR2' and category_code='SUADM';
COMMIT;

update lookups set status='N' where lookup_type='VSTAT' and lookup_code='PE';
COMMIT;

---DEF 898 Ext system enquiry
SET DEFINE OFF;
Insert into INTF_NTWRK_PRFX_MAPPING
   (NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('NG', 'INTID00015', '24', 'V', 'PRE', 
    TO_DATE('10/21/2016 17:50:31', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407', TO_DATE('10/21/2016 17:50:31', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407');
Insert into INTF_NTWRK_PRFX_MAPPING
   (NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('NG', 'INTID00015', '24', 'U', 'PRE', 
    TO_DATE('10/21/2016 17:50:31', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407', TO_DATE('10/21/2016 17:50:31', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407');
COMMIT;


DROP INDEX UNQ_EXT_NO;
COMMIT;


---DEF 986 Fixed----
ALTER TABLE users
MODIFY (BARRED_DELETION_BATCHID varchar2(20));
COMMIT;

-- trigger updated for change password
DROP TRIGGER TRIG_USERS_PASSWORD_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_USERS_PASSWORD_HISTORY"      
AFTER INSERT OR UPDATE
OF PASSWORD,MSISDN ON  USERS
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE

   vappvalue VARCHAR2 (10);
   vpin VARCHAR2 (50 BYTE);
   vphoneprofile VARCHAR2 (10 BYTE);
BEGIN

     SELECT DEFAULT_VALUE  INTO vappvalue FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='USRLEVELAPPROVAL';


BEGIN
 IF :NEW.login_id IS NOT NULL THEN --condition for web,gateway etc user
IF INSERTING THEN
   IF  (:NEW.USER_TYPE='OPERATOR' OR :NEW.USER_TYPE='STAFF' OR vappvalue='0') THEN
     INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
        VALUES(:NEW.user_id, :NEW.login_id, :NEW.PASSWORD, :NEW.user_type, :NEW.category_code, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PWD');
   END IF;
ELSIF UPDATING THEN

     IF :NEW.MSISDN <> :OLD.MSISDN AND :NEW.STATUS ='Y' AND :NEW.USER_TYPE='CHANNEL' THEN

        SELECT sms_pin,PHONE_PROFILE  INTO vpin,vphoneprofile FROM USER_PHONES WHERE  user_id=:OLD.user_id AND PRIMARY_NUMBER='Y';

         INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
          VALUES(:NEW.user_id,:OLD.MSISDN,vpin, 'CHANNEL', vphoneprofile, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'MSN');

    END IF;

    IF (:NEW.USER_TYPE='OPERATOR' OR :NEW.USER_TYPE='STAFF' OR vappvalue='0') AND :NEW.PASSWORD <> :OLD.PASSWORD THEN
      INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
           VALUES(:NEW.user_id, :NEW.login_id, :NEW.PASSWORD, :NEW.user_type, :NEW.category_code, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PWD');

    ELSIF :NEW.USER_TYPE='CHANNEL' AND vappvalue <> '0' THEN  --change password or modify password case

        IF :NEW.PASSWORD <> :OLD.PASSWORD AND :NEW.LEVEL1_APPROVED_BY IS NOT NULL AND (:NEW.STATUS ='Y' or :NEW.STATUS ='PA') THEN
          INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(:NEW.user_id, :NEW.login_id, :NEW.PASSWORD, :NEW.user_type, :NEW.category_code, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PWD');

        ELSE IF :NEW.PSWD_MODIFIED_ON IS NULL AND :NEW.STATUS ='Y'  THEN  --first time user creation without change password
             INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(:NEW.user_id, :NEW.login_id, :NEW.PASSWORD, :NEW.user_type, :NEW.category_code, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PWD');
        END IF;
        END IF;



    END IF;
END IF;
END IF;
    END;
END;
/


----------------------DEF 2215 fixed------------------------------


update key_values set value='Sender amount is not between minimum and maximum amount' where key='6019';

update key_values set value='User balance does not exist SUBKEY' where key='5034';

update key_values set value='Low Balance Alert SUBKEY' where key='3504';

update key_values set value='Channel Return successful TXNSUBKEY' where key='5040';

update key_values set value='Channel Return successful BALSUBKEY' where key='8102';

update key_values set value='User transfer product error for minimum transfer SUBKEY' where key='5014';

update key_values set value='User transfer product error for maximum transfer SUBKEY.' where key='5016';

update key_values set value='User transfer product error for multiple of SUBKEY.' where key='5018';

update key_values set value='User transfer product error for less balance SUBKEY.' where key='5020';

update key_values set value='User transfer product error for residual balance less SUBKEY.' where key='5023';

update key_values set value='User transfer product error for maximum balance SUBKEY.' where key='5025';

update key_values set value='Commission slab not defined SUBKEY.' where key='5028';

update key_values set value='user balance does not exist SUBKEY.' where key='5034';

update key_values set value='Channel transfer success TXNSUBKEY.' where key='5036';

update key_values set value='Channel transfer success BALSUBKEY.' where key='8101';
.
update key_values set value='Channel withdraw success TXNSUBKEY.' where key='5038';

update key_values set value='Channel withdraw success BALSUBKEY.' where key='8103';

update key_values set value='C2S OPT transfer cancel TXNSUBKEY.' where key='8005';

update key_values set value='C2S OPT transfer cancel BALSUBKEY.' where key='8006';

update key_values set value='C2S OPT transfer SMS BALSUBKEY.' where key='8096';

update key_values set value='FOC OPT transfer SMS BALSUBKEY.' where key='8097';

update key_values set value='FOC OPT transfer cancel TXNSUBKEY.' where key='8098';

update key_values set value='FOC OPT transfer cancel BALSUBKEY.' where key='8099';

update key_values set value='C2S OPT transfer Withdraw TXNSUBKEY.' where key='8086';

update key_values set value='C2S OPT transfer Withdraw BALSUBKEY.' where key='8100';

update key_values set value='C2S Channel to channel transfer receiver TXNSUBKEY.' where key='8088';

update key_values set value='C2S Channel to channel transfer receiver BALSUBKEY.' where key='8089';

update key_values set value='C2S Channel to channel return receiver TXNSUBKEY.' where key='8091';

update key_values set value='C2S Channel to channel return receiver BALSUBKEY.' where key='8092';

update key_values set value='C2S Channel to channel withdraw receiver TXNSUBKEY.' where key='8094';

update key_values set value='C2S Channel to channel withdraw receiver BALSUBKEY.' where key='8095';

commit;



--Batch o2c Approval 2 link`
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('BTO2CAPR2', 'BATO2C0002', '1');
COMMIT;


UPDATE System_preferences SET Default_value = 'false' WHERE preference_code='PP_DEF_STATUS_ACT';
commit;

update lookups set status='N' where lookup_name='Auto' and lookup_type='TRFT';

ALTER TABLE USER_DAILY_BALANCES MODIFY(LAST_TRANSFER_NO VARCHAR2(25 BYTE));
commit;

update roles set status='N' where role_code='ASSTRFRULE';
commit;
update service_type set status='Y' where service_type='EXTGRPH';
commit;

-- Login password minimum length should be 3
update SYSTEM_PREFERENCES set DEFAULT_VALUE='3' where PREFERENCE_CODE='MIN_LOGIN_PWD_LENGTH';
COMMIT;

update roles set status='N' where role_code='ASSOPTTRFRULE';
commit;


----DEF 2215----

Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('17042', 'Transaction is ambiguous', 'C2S_ERR_CD', 'ERROR_REVERSAL_AMBIGOUS');
COMMIT;

--DEF 2166 Lite RC
Update SERVICE_KEYWORDS set REQUEST_PARAM='TYPE,MSISDN1,PIN,MSISDN2,AMOUNT,SELECTOR' where KEYWORD='RCTRFSERREQ';
COMMIT;

--DEF2490
Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('2016', 'LPT', 'NG', 0, TO_DATE('03/30/2016 15:50:59', 'MM/DD/YYYY HH24:MI:SS'), 
    'DAY', NULL);
COMMIT;


--def 2558
update SERVICE_TYPE
 SET EXTERNAL_INTERFACE = 'N' 
 where SERVICE_TYPE='GMBBAR';
COMMIT;


--def 2559
Update sub_lookups SET SUB_LOOKUP_NAME = 'BARRED For Give Me Balance' where SUB_LOOKUP_CODE='GMBBAR';
COMMIT;



ALTER TABLE p2p_subscribers_counters ADD last_success_date DATE;
Commit;

--Fixed DEF2286
UPDATE SERVICE_KEYWORDS SET SERVICE_PORT = 190 WHERE KEYWORD = 'BRCREV' AND REQ_INTERFACE_TYPE = 'WEB';

--DEF 2576
Update Categories set SMS_INTERFACE_ALLOWED ='Y' where category_code='SUBCU';


delete from messages_master where message_code='2600001';
delete from messages_master where message_code='2600002';
delete from messages_master where message_code='2600004';
UPDATE ROLES SET STATUS='N'WHERE ROLE_CODE='ASSOPTTRFRULE';

COMMIT;

--Added script for adding Low Base roles to Super Channel Amdin

SET DEFINE OFF;
Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) Values ('SUBCU', 'LBSEENQ', '1');
Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) Values ('SUBCU', 'LOWBALRCH', '1');
COMMIT;

--DEF 2549 P2PDWH
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
			v_data	:= NULL;

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
			v_data	:= NULL;

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




------------------DEF 2249 for LMS program detailed report-------------


ALTER TABLE C2S_TRANSFERS_MISTMP ADD LMS_PROFILE  VARCHAR2(10);
commit;

CREATE OR REPLACE PROCEDURE insert_dly_no_c2s_lms_smry (
                            aiv_Date               IN      VARCHAR2,
                                   rtn_message           OUT   VARCHAR2,
                            rtn_messageforlog     OUT   VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT   VARCHAR2
                                   )
IS
p_trans_date            DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id                DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code            DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile            DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_txn_amount            DAILY_C2S_LMS_SUMMARY.TRANSACTION_AMOUNT%type;
p_txn_count                DAILY_C2S_LMS_SUMMARY.TRANSACTION_COUNT%type;
p_accumulatepoint        DAILY_C2S_LMS_SUMMARY.ACCUMULATED_POINTS%type;
p_count NUMBER;

sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

CURSOR insert_cur is
        select distinct cu.user_id,cu.LMS_PROFILE, ps.PRODUCT_CODE  from channel_users cu,  users U, PROFILE_SET_VERSION ps
        where cu.LMS_PROFILE is not null and u.USER_ID=cu.USER_ID and u.status not in ('N','C') and cu.LMS_PROFILE=ps.SET_ID
        minus 
        select ds.user_id,ds.LMS_PROFILE, ds.PRODUCT_CODE from DAILY_C2S_LMS_SUMMARY ds where ds.TRANS_DATE=to_date(aiv_Date,'dd/mm/yy');
        
    BEGIN
      p_count:=0;
      p_trans_date:=to_date(aiv_Date,'dd/mm/yy');    
      p_txn_amount:=0;
      p_txn_count:=0;
      p_accumulatepoint:=0;      
      FOR user_records IN insert_cur
             LOOP
                     p_user_id:=user_records.USER_ID;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.LMS_PROFILE;
                    
                BEGIN
                    p_count:=p_count+1;
                    insert into DAILY_C2S_LMS_SUMMARY(trans_date,user_id,product_code,lms_profile,TRANSACTION_AMOUNT,TRANSACTION_COUNT,ACCUMULATED_POINTS) VALUES  (p_trans_date,p_user_id,p_product_code,p_lms_profile,p_txn_amount,p_txn_count,p_accumulatepoint);

                    EXCEPTION
                        WHEN OTHERS       THEN
                                  DBMS_OUTPUT.PUT_LINE ('Exception in insert_dly_no_c2s_lms_smry Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM );
                                  rtn_messageforlog := 'Exception in insert_dly_no_c2s_lms_smry Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
                                  rtn_sqlerrmsgforlog := SQLERRM;
                                  RAISE sqlexception;
                 END;
             END LOOP;
                 rtn_message:='SUCCESS';
                rtn_messageForLog :='PreTUPS insert_dly_no_c2s_lms_smry successfully executed, Excuted Date Time:'||SYSDATE||' For date:'||p_trans_date||' Number updates:'||p_count;
                rtn_sqlerrMsgForLog :=' ';
        
        EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_messageForLog :='insert_dly_no_c2s_lms_smry sqlException Caught='||SQLERRM;
              rtn_message :='FAILED';
              RAISE sqlexception;

         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in insert_dly_no_c2s_lms_smry procedure:='||SQLERRM);
              rtn_messageForLog :='OTHERS ERROR in insert_dly_no_c2s_lms_smry procedure'||SQLERRM;
              rtn_message :='FAILED';
              RAISE sqlexception;

    END;
/

CREATE OR REPLACE PROCEDURE update_accpnt_dly_c2s_lms_smry (
                            aiv_Date               IN      VARCHAR2,
                                   rtn_message           OUT   VARCHAR2,
                            rtn_messageforlog     OUT   VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT   VARCHAR2
                                   )
IS
p_trans_date            DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id                DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code            DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile            DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_accumulated_points    DAILY_C2S_LMS_SUMMARY.accumulated_points%type;
p_count NUMBER;

sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR update_cur is
        SELECT USER_ID_OR_MSISDN, PRODUCT_CODE, PROFILE_ID, sum(points) as ACCUMULATED_POINTS,POINTS_DATE from  BONUS_HISTORY where PROFILE_TYPE='LMS' and POINTS_DATE=to_date(aiv_Date,'dd/mm/yy') and LAST_REDEMPTION_ID is null  group by USER_ID_OR_MSISDN,PRODUCT_CODE, PROFILE_ID,POINTS_DATE
        ;
    BEGIN
      p_count:=0;
      FOR user_records IN update_cur
             LOOP
                     p_user_id:=user_records.USER_ID_OR_MSISDN;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.PROFILE_ID;
                    p_accumulated_points:=user_records.accumulated_points;
                 BEGIN
                    p_count:=p_count+1;
                    UPDATE DAILY_C2S_LMS_SUMMARY SET  accumulated_points=p_accumulated_points
                    WHERE user_id=p_user_id
                    AND product_code=p_product_code
                    AND trans_date=to_date(aiv_Date,'dd/mm/yy')
                    AND lms_profile=p_lms_profile;
                    COMMIT;
                    EXCEPTION
                        WHEN OTHERS       THEN
                                  DBMS_OUTPUT.PUT_LINE ('Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM );
                                  rtn_messageforlog := 'Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
                                  rtn_sqlerrmsgforlog := SQLERRM;
                                  RAISE sqlexception;
                 END;
             END LOOP;
                 rtn_message:='SUCCESS';
                rtn_messageForLog :='PreTUPS update_acc_pnt_daily_c2s_lms_summary successfully executed, Excuted Date Time:'||SYSDATE||' For date:'||p_trans_date||' Number updates:'||p_count;
                rtn_sqlerrMsgForLog :=' ';
        
        EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;

         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in update_acc_pnt_daily_c2s_lms_summary procedure:='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;

    END;
/





CREATE OR REPLACE PACKAGE Mis_Data_Pkg_New
AS

   v_message              VARCHAR2 (500);
   v_messageforlog        VARCHAR2 (500);
   v_sqlerrmsgforlog      VARCHAR2 (500);
   n_date_for_mis         PROCESS_STATUS.executed_upto%TYPE;
   gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
   gv_transdate           TEMP_DAILY_CHNL_TRANS_MAIN.trans_date%TYPE;
   gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
   gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
   gv_openingbalance      TEMP_DAILY_CHNL_TRANS_MAIN.opening_balance%TYPE;
   gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
   gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
   gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
   gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
   gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
   gv_transfercategory    CHANNEL_TRANSFERS.transfer_category%TYPE;
   gv_transfertype        CHANNEL_TRANSFERS.transfer_type%TYPE;
   gv_transfersubtype     CHANNEL_TRANSFERS.transfer_sub_type%TYPE;
   gd_transaction_date    DATE;
   gv_currentdate         DATE;
   gd_createdon           DATE;
   sqlexception           EXCEPTION;-- Handles SQL or other Exception while checking records Exist
   procexception          EXCEPTION;-- Thrown from sqlException to stop the processing
   retmainexception       EXCEPTION;--Thrown from proc back to main Procedure
   mainexception          EXCEPTION;-- Thrown from inside the main procedure to stop the loop
   finalinsertexception   EXCEPTION;-- Thrown in Final insertion of data
   alreadyDoneException   EXCEPTION;-- Thrown if the MIS has been executed already, and the process termnates
   user_rcd_count         NUMBER (5):= 0;--to determine if user already exists
   v_nullvalue               VARCHAR2(5):='N.A.';
   TYPE t_array IS TABLE OF VARCHAR2(50) INDEX BY BINARY_INTEGER;

---Procedure to get the date range for which the MIS process is to be executed.
   PROCEDURE sp_get_mis_data_dtrange (
      aiv_fromdate          IN       VARCHAR2,
      aiv_todate            IN       VARCHAR2,
      aov_message           OUT      VARCHAR2,
      aov_messageforlog     OUT      VARCHAR2,
      aov_sqlerrmsgforlog   OUT      VARCHAR2
   );

   PROCEDURE sp_get_mis_mon_data_dtrange (
      aiv_fromdate          IN       VARCHAR2,
      aiv_todate            IN       VARCHAR2,
      aov_message           OUT      VARCHAR2,
      aov_messageforlog     OUT      VARCHAR2,
      aov_sqlerrmsgforlog   OUT      VARCHAR2
   );

   PROCEDURE sp_chnl_transfer_out_data_proc (
      p_date              IN   DATE,
      p_transfersubtype   IN   VARCHAR2
   );

   PROCEDURE sp_chnl_transfer_in_data_proc (
      p_date              IN   DATE,
      p_transfersubtype   IN   VARCHAR2
   );

   PROCEDURE sp_insert_opening_bal (
      p_date             IN   DATE
     );

   PROCEDURE move_to_final_data (p_date DATE);

   PROCEDURE move_users_to_new_date (p_date DATE);

   PROCEDURE ret_refills_data_proc (p_date DATE);
   
   PROCEDURE ret_reverse_data_proc (p_date DATE);

   PROCEDURE Sp_Update_Daily_Chnl_Trans_Det (aiv_date IN DATE);

  PROCEDURE Sp_Update_Monthly_Data1 (aiv_date DATE);

  PROCEDURE Sp_Update_Monthly_Data2 (aiv_date DATE);

  PROCEDURE Sp_Update_Monthly_Data3 (aiv_date DATE);
  
  PROCEDURE Sp_Update_Mon_AmbSettData1 (aiv_date DATE);

  --PROCEDURE Sp_Update_Mon_AmbSettData3 (aiv_date DATE);

  PROCEDURE sp_update_roam_c2s_trans (
      p_user_id          IN   VARCHAR2,
      p_date             IN   DATE,
      p_productid        IN   VARCHAR2,
      p_categorycode     IN   VARCHAR2,
      p_homelocation     IN   VARCHAR2,
      p_targetlocation   IN   VARCHAR2,
      p_domaincode       IN   VARCHAR2,
      p_productmrp       IN   NUMBER,
      p_geodomaincode    IN   VARCHAR2,
      p_trans_amt        IN   NUMBER
   );

   PROCEDURE sp_update_c2s_sub_denom (aiv_date IN DATE);

   PROCEDURE sp_update_daily_trn_summary (aiv_date IN DATE,v_choice VARCHAR2);

   PROCEDURE sp_update_c2s_success_failure (aiv_date IN DATE);

   PROCEDURE sp_update_c2s_msisdn_usage (aiv_date IN DATE);

   PROCEDURE sp_update_c2s_bonuses (aiv_date IN DATE);

   PROCEDURE ret_refills_failure_data_proc (p_date DATE);

   PROCEDURE sp_split_bonus_details (aiv_date DATE);

   FUNCTION split(p_in_string VARCHAR2, p_delim VARCHAR2) return t_array;

   PROCEDURE refills_lms_c2s_summary_proc (p_date DATE);


END Mis_Data_Pkg_New;
/

CREATE OR REPLACE PACKAGE BODY Mis_Data_Pkg_New 
AS
PROCEDURE SP_GET_MIS_DATA_DTRANGE (
                                  aiv_fromDate          IN  VARCHAR2,
                                  aiv_toDate             IN  VARCHAR2,
                                  aov_message            OUT VARCHAR2,
                                  aov_messageForLog    OUT VARCHAR2,
                                  aov_sqlerrMsgForLog    OUT VARCHAR2
                                    )
IS
  ld_from_date DATE;
  ld_to_date DATE;
  ld_created_on DATE;
  flag NUMBER(1);
  mis_sno NUMBER(3);
  status NUMBER(1);
  mis_already_executed NUMBER(1);
  msisdn_usage_summ_flag NUMBER(1);
  sql_stmt1        VARCHAR2 (2000);
  DIFFERENTIAL_COUNT NUMBER(5);
  LN_C2S_DIFF_COUNT NUMBER(5);

BEGIN
  ld_from_date   :=TO_DATE(aiv_fromDate,'dd/mm/yy');
  ld_to_date     :=TO_DATE(aiv_toDate,'dd/mm/yy');
  n_date_for_mis :=ld_from_date;
  flag           :=0;
  ld_created_on  :=SYSDATE;  -- Initailaize Created On date
  gd_createdon := ld_created_on;
  mis_already_executed :=0;
  msisdn_usage_summ_flag :=0;
  sql_stmt1 := '';

WHILE n_date_for_mis <= ld_to_date ---run the MIS process for each date less than the To Date
   LOOP
    DBMS_OUTPUT.PUT_LINE('EXCEUTING FOR ::::::::'||n_date_for_mis);
    BEGIN
         ---Check if MIS process has already run for the date
         mis_sno := 0;
         SELECT 1 INTO mis_already_executed
         FROM PROCESS_STATUS
         WHERE PROCESS_ID='C2SMIS' AND EXECUTED_UPTO>=n_date_for_mis;
         DBMS_OUTPUT.PUT_LINE('PreTUPS C2S MIS already Executed, Date:' || n_date_for_mis);
         aov_message :='FAILED';
         aov_messageForLog:='PreTUPS C2S MIS already Executed, Date:' || n_date_for_mis;
         aov_sqlerrMsgForLog:=' ';
         RAISE alreadyDoneException;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
         /*BEGIN
             ---Check if Underprocess or Ambigous transactions are found in the Transaction table for the date
             SELECT 1 INTO status FROM C2S_TRANSFERS ct
             WHERE ct.TRANSFER_DATE = n_date_for_mis AND
             ct.TRANSFER_STATUS IN ('205','250');
              DBMS_OUTPUT.PUT_LINE('Underprocess or ambigous transaction found. PreTUPS C2S MIS cannot continue, Date:' || n_date_for_mis);
              aov_messageForLog:='Underprocess or ambigous transaction found. PreTUPS C2S MIS cannot continue, Date:' || n_date_for_mis;
              aov_sqlerrMsgForLog:=' ';
              flag:=1;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
             ----If MIS not executed for the date and no Underprocess or Ambigous transactions found then update all the MIS tables
            */
            BEGIN
                DBMS_OUTPUT.PUT_LINE('before truncation : '||TO_CHAR(SYSDATE, 'DD-MON-YY HH24:MI:SS'));
                mis_sno := mis_sno+1;
                INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before table truncate',n_date_for_mis,SYSDATE);
                sql_stmt1 := 'TRUNCATE TABLE adjustments_MISTMP';
                EXECUTE IMMEDIATE sql_stmt1;
                sql_stmt1 := 'TRUNCATE TABLE C2S_TRANSFERS_MISTMP';
                EXECUTE IMMEDIATE sql_stmt1;
                DBMS_OUTPUT.PUT_LINE('middle truncation : '||TO_CHAR(SYSDATE, 'DD-MON-YY HH24:MI:SS'));
                mis_sno := mis_sno+1;
                INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'after table truncate and before tmp tbale populate',n_date_for_mis,SYSDATE);
                INSERT INTO C2S_TRANSFERS_MISTMP
                    SELECT TRANSFER_ID, TRANSFER_DATE, NETWORK_CODE, SENDER_ID, SENDER_CATEGORY, PRODUCT_CODE, RECEIVER_NETWORK_CODE,
                    TRANSFER_VALUE, ERROR_CODE ,SERVICE_TYPE, TRANSFER_STATUS, SENDER_TRANSFER_VALUE, RECEIVER_ACCESS_FEE,
                    RECEIVER_TAX1_VALUE, RECEIVER_TAX2_VALUE, RECEIVER_VALIDITY, RECEIVER_TRANSFER_VALUE, RECEIVER_BONUS_VALUE,
                    RECEIVER_BONUS_VALIDITY, GRPH_DOMAIN_CODE, SUB_SERVICE, RECEIVER_MSISDN,
                    INTERFACE_ID,PREFIX_ID,SERVICE_CLASS_ID, SERVICE_CLASS_CODE,BONUS_DETAILS,TRANSFER_TYPE,TXN_TYPE,REVERSAL_ID,PENALTY,OWNER_PENALTY,RECONCILIATION_DATE,LMS_PROFILE
                        FROM C2S_TRANSFERS WHERE transfer_date = n_date_for_mis AND TRANSFER_STATUS in ('200','206') and RECONCILIATION_DATE is NULL;
                
                INSERT INTO C2S_TRANSFERS_MISTMP
                    SELECT TRANSFER_ID, TRANSFER_DATE, NETWORK_CODE, SENDER_ID, SENDER_CATEGORY, PRODUCT_CODE, RECEIVER_NETWORK_CODE,
                    TRANSFER_VALUE, ERROR_CODE ,SERVICE_TYPE, TRANSFER_STATUS, SENDER_TRANSFER_VALUE, RECEIVER_ACCESS_FEE,
                    RECEIVER_TAX1_VALUE, RECEIVER_TAX2_VALUE, RECEIVER_VALIDITY, RECEIVER_TRANSFER_VALUE, RECEIVER_BONUS_VALUE,
                    RECEIVER_BONUS_VALIDITY, GRPH_DOMAIN_CODE, SUB_SERVICE, RECEIVER_MSISDN,
                    INTERFACE_ID,PREFIX_ID,SERVICE_CLASS_ID, SERVICE_CLASS_CODE,BONUS_DETAILS,TRANSFER_TYPE,TXN_TYPE,REVERSAL_ID,PENALTY,OWNER_PENALTY,RECONCILIATION_DATE,LMS_PROFILE 
                        FROM C2S_TRANSFERS WHERE RECONCILIATION_DATE = n_date_for_mis AND TRANSFER_STATUS in ('200','206');
                
                --INSERT INTO C2S_TRANSFER_ITEMS_MISTMP
                    --SELECT transfer_id, msisdn, transfer_value, interface_id, sno , prefix_id, service_class_id, service_class_code
                        --FROM C2S_TRANSFER_ITEMS WHERE transfer_date = n_date_for_mis;
                INSERT INTO ADJUSTMENTS_MISTMP
                    SELECT ADJUSTMENT_ID, ADJUSTMENT_DATE, USER_ID, TRANSFER_VALUE, TAX1_VALUE, TAX2_VALUE, TAX3_VALUE, REFERENCE_ID
                        FROM ADJUSTMENTS WHERE adjustment_date = n_date_for_mis;
                        
                INSERT INTO ADJUSTMENTS_MISTMP
                    SELECT ADJUSTMENT_ID, ADJUSTMENT_DATE, USER_ID, TRANSFER_VALUE, TAX1_VALUE, TAX2_VALUE, TAX3_VALUE, REFERENCE_ID
                        FROM ADJUSTMENTS WHERE reference_id in (select transfer_id from C2S_TRANSFERS_MISTMP 
                        where RECONCILIATION_DATE = n_date_for_mis and transfer_date != n_date_for_mis);
                -- specify the column which is required in mis.
                DBMS_OUTPUT.PUT_LINE('after truncation : '||TO_CHAR(SYSDATE, 'DD-MON-YY HH24:MI:SS'));
                mis_sno := mis_sno+1;
                INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'after tmp table populate',n_date_for_mis,SYSDATE);
                COMMIT;
            EXCEPTION
               WHEN retmainexception THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception in SP_GET_MIS_DATA_DTRANGE :'|| SQLERRM);
                  aov_messageForLog:=v_messageforlog;
                   aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS in SP_GET_MIS_DATA_DTRANGE :'|| SQLERRM);
                   aov_messageForLog:='OTHERS in SP_GET_MIS_DATA_DTRANGE';
                   aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc T',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_out_data_proc (n_date_for_mis, 'T');
            EXCEPTION
               WHEN retmainexception THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception in SP_CHNL_TRANSFER_OUT_DATA_PROC for transfer :'|| SQLERRM);
                  aov_messageForLog:=v_messageforlog;
                   aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC for transfer :'|| SQLERRM);
                   aov_messageForLog:='OTHERS Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC for transfer';
                   aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc R',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_out_data_proc (n_date_for_mis, 'R');
            EXCEPTION
               WHEN retmainexception THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception in SP_CHNL_TRANSFER_OUT_DATA_PROC for return:'|| SQLERRM);
                    aov_messageForLog:=v_messageforlog;
                    aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for return:'|| SQLERRM);
                   aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for return';
                    aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
               END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc W',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_out_data_proc (n_date_for_mis, 'W');
            EXCEPTION
               WHEN retmainexception THEN
                  DBMS_OUTPUT.PUT_LINE(   'retmainexception in SP_CHNL_TRANSFER_OUT_DATA_PROC for withdraw:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                   RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for withdraw:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for withdraw';
              aov_sqlerrMsgForLog:=SQLERRM;
                 RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc X',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_out_data_proc (n_date_for_mis, 'X');
            EXCEPTION
               WHEN retmainexception THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception in SP_CHNL_TRANSFER_OUT_DATA_PROC for reversal:'|| SQLERRM);
                    aov_messageForLog:=v_messageforlog;
                    aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for reversal:'|| SQLERRM);
                   aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for reversal';
                    aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
               END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc T',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_in_data_proc (n_date_for_mis, 'T');
            EXCEPTION
               WHEN retmainexception THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in SP_CHNL_TRANSFER_IN_DATA_PROC for transfer:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for transfer:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for transfer';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc R',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_in_data_proc (n_date_for_mis, 'R');
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in SP_CHNL_TRANSFER_IN_DATA_PROC for return:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS THEN
                  DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for return:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for return';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc W',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_in_data_proc (n_date_for_mis, 'W');
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE(   'retmainexception in SP_CHNL_TRANSFER_IN_DATA_PROC for withdraw:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for withdraw:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for withdraw';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;
            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc X',n_date_for_mis,SYSDATE);
               sp_chnl_transfer_in_data_proc (n_date_for_mis, 'X');
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE
                       (   'retmainexception in SP_CHNL_TRANSFER_IN_DATA_PROC for reversal:'
                        || SQLERRM
                       );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE
                       (   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for reversal:'
                        || SQLERRM
                       );
              aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for reversal';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before ret_refills_data_proc',n_date_for_mis,SYSDATE);
               ret_refills_data_proc (n_date_for_mis);

            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in RET_REFILLS_DATA_PROC:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in RET_REFILLS_DATA_PROC:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in RET_REFILLS_DATA_PROC';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before ret_reverse_data_proc',n_date_for_mis,SYSDATE);
               ret_reverse_data_proc(n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in ret_reverse_data_proc:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in ret_reverse_data_proc:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in ret_reverse_data_proc';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;
            
            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Daily_Chnl_Trans_Det',n_date_for_mis,SYSDATE);
               Sp_Update_Daily_Chnl_Trans_Det (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in SP_UPDATE_DAILY_CHNL_TRANS_DET:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_insert_opening_bal',n_date_for_mis,SYSDATE);
               sp_insert_opening_bal (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in sp_insert_opening_bal:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in sp_insert_opening_bal:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in sp_insert_opening_bal';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
                mis_sno := mis_sno+1;
                INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before move_to_final_data',n_date_for_mis,SYSDATE);
                move_to_final_data (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in MOVE_TO_FINAL_DATA:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in MOVE_TO_FINAL_DATA:' || SQLERRM );
              aov_messageForLog:='EXCEPTION in MOVE_TO_FINAL_DATA';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before move_users_to_new_date',n_date_for_mis,SYSDATE);
               move_users_to_new_date (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in MOVE_USERS_TO_NEW_DATE:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in MOVE_USERS_TO_NEW_DATE:'|| SQLERRM );
              aov_messageForLog:='EXCEPTION in MOVE_USERS_TO_NEW_DATE';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_c2s_sub_denom',n_date_for_mis,SYSDATE);
               sp_update_c2s_sub_denom (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception in SP_UPDATE_C2S_SUB_DENOM:'|| SQLERRM);
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
             RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_UPDATE_C2S_SUB_DENOM:'|| SQLERRM);
              aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_SUB_DENOM';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_daily_trn_summary',n_date_for_mis,SYSDATE);
               sp_update_daily_trn_summary (n_date_for_mis,'N');
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception EXCEPTION in SP_UPDATE_DAILY_TRN_SUMMARY'|| SQLERRM);
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
             RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE(' OTHERS EXCEPTION in SP_UPDATE_DAILY_TRN_SUMMARY'|| SQLERRM);
              aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_DAILY_TRN_SUMMARY';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_c2s_success_failure',n_date_for_mis,SYSDATE);
               sp_update_c2s_success_failure (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE('retmainexception in SP_UPDATE_C2S_SUCCESS_FAILURE'|| SQLERRM);
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
             RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE'|| SQLERRM);
              aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before ret_refills_failure_data_proc',n_date_for_mis,SYSDATE);
               ret_refills_failure_data_proc (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'retmainexception in RET_REFILLS_FAILURE_DATA_PROC:' || SQLERRM );
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                  RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC:' || SQLERRM );
              aov_messageForLog:='OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
             ---check msisdn usage summary need to populate or not
             SELECT 1 INTO msisdn_usage_summ_flag
             FROM SYSTEM_PREFERENCES
             WHERE PREFERENCE_CODE='MSISDN_USAGE_SUMM_FLAG' AND DEFAULT_VALUE<>'TRUE';
             EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    BEGIN
                       mis_sno := mis_sno+1;
                       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_c2s_msisdn_usage',n_date_for_mis,SYSDATE);
                       sp_update_c2s_msisdn_usage (n_date_for_mis);
                    EXCEPTION
                       WHEN retmainexception
                       THEN
                          DBMS_OUTPUT.PUT_LINE('retmainexception in SP_UPDATE_C2S_MSISDN_USAGE'|| SQLERRM);
                     aov_messageForLog:=v_messageforlog;
                     aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                     RAISE mainexception;
                       WHEN OTHERS
                       THEN
                          DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE'|| SQLERRM);
                     aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE';
                     aov_sqlerrMsgForLog:=SQLERRM;
                          RAISE mainexception;
                    END;
            END;
            --new procedure added for c2s_bonus removal
            BEGIN
               sp_split_bonus_details (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE('EXCEPTION in sp_split_bonus_details 3'|| SQLERRM);
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
             RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE(' OTHERS EXCEPTION in sp_split_bonus_details 3'|| SQLERRM);
              aov_messageForLog:='OTHERS EXCEPTION in sp_split_bonus_details';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

            BEGIN
               sp_update_c2s_bonuses (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE('EXCEPTION in sp_update_c2s_bonuses'|| SQLERRM);
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
             RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE('EXCEPTION in sp_update_c2s_bonuses'|| SQLERRM);
              aov_messageForLog:='EXCEPTION in sp_update_c2s_bonuses';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;

         BEGIN
            mis_sno := mis_sno+1;
               INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before refills_lms_c2s_summary_proc',n_date_for_mis,SYSDATE);
               refills_lms_c2s_summary_proc (n_date_for_mis);
            EXCEPTION
               WHEN retmainexception
               THEN
                  DBMS_OUTPUT.PUT_LINE('EXCEPTION in refills_lms_c2s_summary_proc'|| SQLERRM);
              aov_messageForLog:=v_messageforlog;
              aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
             RAISE mainexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE('EXCEPTION in refills_lms_c2s_summary_proc'|| SQLERRM);
              aov_messageForLog:='EXCEPTION in refills_lms_c2s_summary_proc';
              aov_sqlerrMsgForLog:=SQLERRM;
                  RAISE mainexception;
            END;
        
            --  monthly table update for ambigues settled case
            
            UPDATE PROCESS_STATUS SET executed_upto=n_date_for_mis, executed_on=SYSDATE WHERE PROCESS_ID='C2SMIS';
            mis_sno := mis_sno+1;
            INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'C2S MIS Successfully Executed for the date',n_date_for_mis,SYSDATE);
            COMMIT;

            aov_message :='SUCCESS';
            aov_messageForLog :='PreTUPS C2S MIS successfully executed, Date Time:'||SYSDATE;
            aov_sqlerrMsgForLog :=' ';

             /*WHEN TOO_MANY_ROWS THEN
             DBMS_OUTPUT.PUT_LINE('Underprocess or ambigous transaction found. PreTUPS C2S MIS cannot continue, Date:' || n_date_for_mis);
             aov_messageForLog:='Underprocess or ambigous transaction found. PreTUPS C2S MIS cannot continue, Date:' || n_date_for_mis;
             aov_sqlerrMsgForLog:=' ';
             flag:=1;

             WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('OTHERS Error when checking for underprocess or ambigous transactions'||SQLERRM);
                aov_messageForLog:='Error when checking for underprocess or ambigous transactions, Date:'|| n_date_for_mis;
                 aov_sqlerrMsgForLog:=SQLERRM;
                RAISE mainException;
          END;*/


         WHEN alreadyDoneException THEN--exception handled in case MIS already executed
          aov_sqlerrMsgForLog:=SQLERRM;
          RAISE mainException;

         WHEN OTHERS THEN
          DBMS_OUTPUT.PUT_LINE('OTHERS Error when checking if MIS process has already been executed'||SQLERRM);
          aov_messageForLog:='OTHERS Error when checking if MIS process has already been executed, Date:'|| n_date_for_mis;
          aov_sqlerrMsgForLog:=SQLERRM;
          RAISE mainException;

    END;

IF flag = 1 THEN
n_date_for_mis := ld_to_date; ---If Underprocess or Anbigous transaction found then stop the MIS process for further execution of other dates
RAISE mainException;
ELSE
n_date_for_mis:=n_date_for_mis+1;
END IF;
END LOOP;

EXCEPTION --Exception Handling of main procedure
  WHEN mainException THEN
  ROLLBACK;
  DBMS_OUTPUT.PUT_LINE('mainException Caught='||SQLERRM);
  aov_message :='FAILED';

  WHEN OTHERS THEN
  ROLLBACK;
  DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in Main procedure:='||SQLERRM);
  aov_message :='FAILED';

END SP_GET_MIS_DATA_DTRANGE; --End of main procedure


PROCEDURE SP_GET_MIS_MON_DATA_DTRANGE (
                                  aiv_fromDate          IN  VARCHAR2,
                                  aiv_toDate             IN  VARCHAR2,
                                  aov_message            OUT VARCHAR2,
                                  aov_messageForLog        OUT VARCHAR2,
                                  aov_sqlerrMsgForLog    OUT VARCHAR2
                                    )
IS
  ld_from_date DATE;
  ld_to_date DATE;
  ld_created_on DATE;
  flag NUMBER(1);
  mis_sno NUMBER(3);
  status NUMBER(1);
  mis_already_executed NUMBER(1);
  sql_stmt1        VARCHAR2 (2000);

BEGIN
  ld_from_date   :=TO_DATE(aiv_fromDate,'dd/mm/yy');
  ld_to_date     :=TO_DATE(aiv_toDate,'dd/mm/yy');
  n_date_for_mis :=ld_from_date;
  flag           :=0;
  ld_created_on  :=SYSDATE;  -- Initailaize Created On date
  gd_createdon := ld_created_on;
  mis_already_executed :=0;
  sql_stmt1 := '';

WHILE n_date_for_mis <= ld_to_date ---run the MIS process for each date less than the To Date
   LOOP
    DBMS_OUTPUT.PUT_LINE('EXCEUTING FOR ::::::::'||n_date_for_mis);
    BEGIN
         ---Check if MIS process has already run for the date
         mis_sno := 19;
         SELECT 1 INTO mis_already_executed
         FROM PROCESS_STATUS
         WHERE PROCESS_ID='C2SMISMON' AND EXECUTED_UPTO>=n_date_for_mis;
         DBMS_OUTPUT.PUT_LINE('PreTUPS C2S MIS Monthly Data already Executed, Date:' || n_date_for_mis);
         aov_message :='FAILED';
         aov_messageForLog:='PreTUPS C2S MIS Monthly Data already Executed, Date:' || n_date_for_mis;
         aov_sqlerrMsgForLog:=' ';
         RAISE alreadyDoneException;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
             BEGIN
             ---Check if Underprocess or Ambigous transactions are found in the Transaction table for the date
             SELECT 1 INTO mis_already_executed
            FROM PROCESS_STATUS
            WHERE PROCESS_ID='C2SMIS' AND EXECUTED_UPTO<n_date_for_mis;
              DBMS_OUTPUT.PUT_LINE('PreTUPS C2S MIS for Monthly Data cannot continue, first execute C2S MIS Daily Data process, Date:' || n_date_for_mis);
              aov_messageForLog:='PreTUPS C2S MIS for Monthly Data cannot continue, first execute C2S MIS Daily Data process, Date:' || n_date_for_mis;
              aov_sqlerrMsgForLog:=' ';
             flag:=1;
          EXCEPTION
              WHEN NO_DATA_FOUND THEN

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Monthly_Data1',n_date_for_mis,SYSDATE);
                   Sp_Update_Monthly_Data1 (n_date_for_mis);
                EXCEPTION
                   WHEN retmainexception
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'retmainexception in SP_UPDATE_MONTHLY_DATA1:'|| SQLERRM );
                 aov_messageForLog:=v_messageforlog;
                 aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                      RAISE mainexception;
                   WHEN OTHERS
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_UPDATE_MONTHLY_DATA1:' || SQLERRM );
                 aov_messageForLog:='EXCEPTION in SP_UPDATE_MONTHLY_DATA1';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE mainexception;
                END;

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Monthly_Data2',n_date_for_mis,SYSDATE);
                   Sp_Update_Monthly_Data2 (n_date_for_mis);
                EXCEPTION
                   WHEN retmainexception
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'retmainexception in SP_UPDATE_MONTHLY_DATA2:'|| SQLERRM );
                 aov_messageForLog:=v_messageforlog;
                 aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                      RAISE mainexception;
                   WHEN OTHERS
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_UPDATE_MONTHLY_DATA2:' || SQLERRM );
                 aov_messageForLog:='EXCEPTION in SP_UPDATE_MONTHLY_DATA2';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE mainexception;
                END;

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Monthly_Data3',n_date_for_mis,SYSDATE);
                   Sp_Update_Monthly_Data3 (n_date_for_mis);
                EXCEPTION
                   WHEN retmainexception
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'retmainexception in SP_UPDATE_MONTHLY_DATA3:'|| SQLERRM );
                 aov_messageForLog:=v_messageforlog;
                 aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                      RAISE mainexception;
                   WHEN OTHERS
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_UPDATE_MONTHLY_DATA3:' || SQLERRM );
                 aov_messageForLog:='EXCEPTION in SP_UPDATE_MONTHLY_DATA3';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE mainexception;
                END;

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Mon_AmbSettData1',n_date_for_mis,SYSDATE);
                   Sp_Update_Mon_AmbSettData1 (n_date_for_mis);
                EXCEPTION
                   WHEN retmainexception
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'retmainexception in Sp_Update_Mon_AmbSettData1:'|| SQLERRM );
                 aov_messageForLog:=v_messageforlog;
                 aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                      RAISE mainexception;
                   WHEN OTHERS
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1:' || SQLERRM );
                 aov_messageForLog:='EXCEPTION in Sp_Update_Mon_AmbSettData1';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE mainexception;
                END;
                
                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Mon_AmbSettData3',n_date_for_mis,SYSDATE);
                   --Sp_Update_Mon_AmbSettData3 (n_date_for_mis);
                EXCEPTION
                   WHEN retmainexception
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'retmainexception in Sp_Update_Mon_AmbSettData3:'|| SQLERRM );
                 aov_messageForLog:=v_messageforlog;
                 aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
                      RAISE mainexception;
                   WHEN OTHERS
                   THEN
                      DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData3:' || SQLERRM );
                 aov_messageForLog:='EXCEPTION in Sp_Update_Mon_AmbSettData3';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE mainexception;
                END;

                
                
                 UPDATE PROCESS_STATUS SET executed_upto=n_date_for_mis, executed_on=SYSDATE WHERE PROCESS_ID='C2SMISMON';
                 mis_sno := mis_sno+1;
                 INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'C2S MIS For Monthly Data Successfully Executed for the date',n_date_for_mis,SYSDATE);

                 COMMIT;

                  aov_message :='SUCCESS';
                  aov_messageForLog :='PreTUPS C2S MIS for Monthly Data successfully executed, Date Time:'||SYSDATE;
                  aov_sqlerrMsgForLog :=' ';

             WHEN OTHERS THEN
                 DBMS_OUTPUT.PUT_LINE('OTHERS Error when checking for underprocess or ambigous transactions'||SQLERRM);
                 aov_messageForLog:='Error when checking for underprocess or ambigous transactions, Date:'|| n_date_for_mis;
                 aov_sqlerrMsgForLog:=SQLERRM;
                 RAISE mainException;
          END;


          WHEN alreadyDoneException THEN--exception handled in case MIS already executed
           aov_sqlerrMsgForLog:=SQLERRM;
          RAISE mainException;

          WHEN OTHERS THEN
          DBMS_OUTPUT.PUT_LINE('OTHERS Error when checking if MIS process has already been executed'||SQLERRM);
          aov_messageForLog:='OTHERS Error when checking if MIS process has already been executed, Date:'|| n_date_for_mis;
           aov_sqlerrMsgForLog:=SQLERRM;
          RAISE mainException;

  END;

IF flag = 1 THEN
n_date_for_mis := ld_to_date;
RAISE mainException;
ELSE
n_date_for_mis:=n_date_for_mis+1;
END IF;
END LOOP;

EXCEPTION
  WHEN mainException THEN
  ROLLBACK;
  DBMS_OUTPUT.PUT_LINE('mainException Caught='||SQLERRM);
  aov_message :='FAILED';

  WHEN OTHERS THEN
  ROLLBACK;
  DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in Main procedure:='||SQLERRM);
  aov_message :='FAILED';

END SP_GET_MIS_MON_DATA_DTRANGE; --End of main procedure






   PROCEDURE sp_chnl_transfer_out_data_proc (
      p_date              IN   DATE,
      p_transfersubtype   IN   VARCHAR2
   )
   IS
      ln_o2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_c2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_o2creturns         TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_count%TYPE;
      ln_o2creturn_amt      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_amount%TYPE;
      ln_o2cwithdraws       TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_count%TYPE;
      ln_o2cwithdraw_amt    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_amount%TYPE;
      ln_c2ctransfers       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_count%TYPE;
      ln_c2ctransfers_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_amount%TYPE;
      ln_c2creturns         TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_count%TYPE;
      ln_c2creturn_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_amount%TYPE;
      ln_c2cwithdraws       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_count%TYPE;
      ln_c2cwithdraws_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_amount%TYPE;
      ln_adjustment_amt     TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      sql_stmt              VARCHAR2 (2000);

      /* Cursor Declaration */
      CURSOR chnl_data (p_date DATE, p_transfersubtype VARCHAR2)
      IS
         SELECT   ch.from_user_id, ch.network_code, ch.network_code_for,
                  chi.product_code, ug.grph_domain_code,chi.user_unit_price unit_value,
                  ch.sender_category_code category_code , ch.domain_code,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN 1 ELSE 0 END) o2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN chi.approved_quantity ELSE 0 END) o2c_amount,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN 1 ELSE 0 END) c2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN chi.approved_quantity ELSE 0 END) c2c_amount
             FROM CHANNEL_TRANSFERS ch,
                  CHANNEL_TRANSFERS_ITEMS chi,
          USER_GEOGRAPHIES ug
            WHERE ch.transfer_date<=p_date+1
              AND TRUNC (ch.close_date) = p_date
              AND ch.transfer_sub_type = p_transfersubtype
              AND ch.transfer_id = chi.transfer_id
          AND ch.from_user_id=ug.user_id
              AND ch.status = 'CLOSE'
              AND ch.from_user_id <> 'OPT'
         GROUP BY ch.from_user_id,
                  ch.network_code,
                  ch.network_code_for,
                  chi.product_code,
                  ch.sender_category_code,
                  ug.grph_domain_code,
                  ch.domain_code,
                  chi.user_unit_price;
   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      gv_productmrp:='';

      /* Iterate CHNL_DATA cursor */
      FOR chnl_data_cur IN chnl_data (n_date_for_mis, p_transfersubtype)
      LOOP
         gv_userid := chnl_data_cur.from_user_id;
         gv_networkcode := chnl_data_cur.network_code;
         gv_networkcodefor := chnl_data_cur.network_code_for;
         gv_grphdomaincode := chnl_data_cur.grph_domain_code;
         gv_productcode := chnl_data_cur.product_code;
         gv_categorycode := chnl_data_cur.category_code;
         gv_domaincode := chnl_data_cur.domain_code;
         gd_transaction_date := p_date;
         user_rcd_count := 0;
         ln_o2c_trans := chnl_data_cur.o2c_num_trans;
         ln_o2c_trans_amt := chnl_data_cur.o2c_amount;
         ln_c2c_trans := chnl_data_cur.c2c_num_trans;
         ln_c2c_trans_amt := chnl_data_cur.c2c_amount;
         gv_productmrp := chnl_data_cur.unit_value;
         sql_stmt := '';

         BEGIN
            BEGIN
                SELECT 1 INTO user_rcd_count
                FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE user_id = gv_userid
                AND network_code = gv_networkcode
                AND network_code_for = gv_networkcodefor
                AND product_code = gv_productcode
                AND grph_domain_code = gv_grphdomaincode
                AND trans_date = gd_transaction_date;
               --DBMS_OUTPUT.PUT_LINE('dist_rcd_count='||rcd_count);
               IF SQL%NOTFOUND
               THEN
                  v_messageforlog := 'SQL Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC 1, User ' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                  --DBMS_OUTPUT.PUT_LINE('No Record found for Dist. in Orders');
                  user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  v_messageforlog := 'OTHERS Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC 1, User ' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            IF user_rcd_count = 0
            THEN
               sql_stmt :=
                  'INSERT INTO temp_daily_chnl_trans_main (user_id, trans_date, product_code, category_code, network_code, network_code_for, sender_domain_code, ';
               sql_stmt :=
                   sql_stmt || ' created_on, product_mrp,grph_domain_code,';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_return_out_count, o2c_return_out_amount, c2c_return_out_count, c2c_return_out_amount ';
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_withdraw_out_count, o2c_withdraw_out_amount, c2c_withdraw_out_count, c2c_withdraw_out_amount ';
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_transfer_out_count, c2c_transfer_out_amount ';

           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_reverse_out_count, o2c_reverse_out_amount, c2c_reverse_out_count, c2c_reverse_out_amount ';


               END IF;

               sql_stmt := sql_stmt || ')';

               IF (p_transfersubtype = 'T')
               THEN
                  sql_stmt := sql_stmt || ' VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12)';

               ELSE
                  sql_stmt := sql_stmt || ' VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13,:14)';
               END IF;

             IF (p_transfersubtype = 'T')
             THEN
               EXECUTE IMMEDIATE sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_c2c_trans,ln_c2c_trans_amt;
             ELSE
                EXECUTE IMMEDIATE sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_o2c_trans,ln_o2c_trans_amt,ln_c2c_trans,ln_c2c_trans_amt;

             END IF;

            ELSE
               sql_stmt := 'UPDATE temp_daily_chnl_trans_main  SET  ';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_return_out_count='
                     || ln_o2c_trans
                     || ', o2c_return_out_amount='
                     || ln_o2c_trans_amt
                     || ', c2c_return_out_count='
                     || ln_c2c_trans
                     || ', c2c_return_out_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_withdraw_out_count='
                     || ln_o2c_trans
                     || ',o2c_withdraw_out_amount='
                     || ln_o2c_trans_amt
                     || ',c2c_withdraw_out_count='
                     || ln_c2c_trans
                     || ',c2c_withdraw_out_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_transfer_out_count='
                     || ln_c2c_trans
                     || ', c2c_transfer_out_amount ='
                     || ln_c2c_trans_amt;
           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_reverse_out_count='
                     || ln_c2c_trans
                     || ', c2c_reverse_out_amount ='
                     || ln_c2c_trans_amt
             || ', o2c_reverse_out_count='
                     || ln_o2c_trans
                     || ', o2c_reverse_out_amount ='
                     || ln_o2c_trans_amt;
               END IF;


               sql_stmt := sql_stmt|| ' WHERE user_id=:1 AND trans_date=:2 AND product_code=:3 AND network_code=:4 AND network_code_for=:5';

               EXECUTE IMMEDIATE sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_networkcode,gv_networkcodefor;
            END IF;
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE (   'sqlexception in SP_CHNL_TRANSFER_OUT_DATA_PROC 2, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'sqlexception in SP_CHNL_TRANSFER_OUT_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION CAUGHT while Inserting/Updating record, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'Others Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC, User:' || gv_userid || ' Date:' || gd_transaction_date || sql_stmt;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                             --end of distributor insertion block
      END LOOP;                                     --end of ORDER_CURSOR Loop
   --CLOSE ORDER_DATE; --closing the cursor
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception CAUGHT in SP_CHNL_TRANSFER_OUT_DATA_PROC 3');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS CAUGHT in SP_CHNL_TRANSFER_OUT_DATA_PROC 3');
         RAISE retmainexception;
   END;                                                 --end of orders values

   PROCEDURE move_users_to_new_date (p_date DATE)
   AS
      ld_closing_stock   DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;


      CURSOR user_data (pv_date DATE)
      IS
         SELECT dtr.user_id, dtr.product_code, dtr.network_code,
                dtr.category_code, dtr.sender_domain_code domain_code, dtr.network_code_for,
                dtr.grph_domain_code, dtr.closing_balance
           FROM DAILY_CHNL_TRANS_MAIN dtr, USERS us
          WHERE dtr.trans_date = pv_date - 1
            AND us.user_type = 'CHANNEL'
            AND dtr.user_id = us.user_id
           AND TRUNC (us.modified_on) >=
CASE WHEN (us.status='N') THEN pv_date ELSE TRUNC (us.modified_on) END;
   BEGIN
      BEGIN
         gv_userid := '';
         gv_networkcode := '';
         gv_networkcodefor := '';
         gv_grphdomaincode := '';
         gv_productcode := '';
         gv_categorycode := '';
         gv_domaincode := '';
         gd_transaction_date := p_date;
         ld_closing_stock := 0;
         gv_productmrp := '';

         /* Iterate DIST_DATA cursor */
         FOR user_data_cur IN user_data (p_date)
         LOOP
            user_rcd_count := 0;                          --reinitialize to 0
            ld_closing_stock := user_data_cur.closing_balance;
            gv_userid := user_data_cur.user_id;
            gv_productcode := user_data_cur.product_code;
            gv_networkcode := user_data_cur.network_code;
            gv_networkcodefor := user_data_cur.network_code_for;
            gv_grphdomaincode := user_data_cur.grph_domain_code;
            ld_closing_stock := user_data_cur.closing_balance;
            gv_categorycode := user_data_cur.category_code;
            gv_domaincode := user_data_cur.domain_code;

            BEGIN
               BEGIN
                  SELECT 1 INTO user_rcd_count
                  FROM DAILY_CHNL_TRANS_MAIN
                  WHERE user_id = gv_userid
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor
                  AND product_code = gv_productcode
                  AND trans_date = gd_transaction_date;

                  --DBMS_OUTPUT.PUT_LINE('dist_rcd_count='||dist_rcd_count);
                  IF SQL%NOTFOUND
                  THEN
                  DBMS_OUTPUT.PUT_LINE('SQL%NOTFOUND EXCEPTION while selecting in MOVE_USERS_TO_NEW_DATE 1, User:'|| gv_userid || SQLERRM);
                     v_messageforlog := 'SQL%NOTFOUND  Exception while selecting data in MOVE_USERS_TO_NEW_DATE 1, User:'|| gv_userid || ' Date:' || gd_transaction_date;
                     v_sqlerrmsgforlog := SQLERRM;
                     RAISE sqlexception;
                  END IF;
               EXCEPTION
                  WHEN NO_DATA_FOUND
                  THEN              --when no row returned for the distributor
                     --DBMS_OUTPUT.PUT_LINE('No Record found for Dist. in Moving Data');
                     user_rcd_count := 0;
                  WHEN OTHERS
                  THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS while selecting in MOVE_USERS_TO_NEW_DATE 2, User:' || gv_userid || SQLERRM);
                     v_messageforlog :='OTHERS while selecting data in MOVE_USERS_TO_NEW_DATE 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                     v_sqlerrmsgforlog := SQLERRM;
                     RAISE sqlexception;
               END;

               IF user_rcd_count = 0
               THEN
                  INSERT INTO DAILY_CHNL_TRANS_MAIN
                              (user_id, trans_date,
                               product_code, category_code,
                               network_code, network_code_for,
                               sender_domain_code, created_on,
                               grph_domain_code, opening_balance,
                               closing_balance
                              )
                       VALUES (gv_userid, gd_transaction_date,
                               gv_productcode, gv_categorycode,
                               gv_networkcode, gv_networkcodefor,
                               gv_domaincode, gd_createdon,
                               gv_grphdomaincode, ld_closing_stock,
                               ld_closing_stock
                              );
               END IF;
            EXCEPTION
               WHEN sqlexception
               THEN
                  DBMS_OUTPUT.PUT_LINE(   'sqlexception while inserting in MOVE_USERS_TO_NEW_DATE 3, User:' || gv_userid || SQLERRM );
                  v_messageforlog := 'sqlexception while inserting in MOVE_USERS_TO_NEW_DATE 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE procexception;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE('OTHERS CAUGHT in MOVE_USERS_TO_NEW_DATE 3, User' || gv_userid || SQLERRM );
                  v_messageforlog := 'Others Exception in MOVE_USERS_TO_NEW_DATE 3, User' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE procexception;
            END;--end of user insertion block
         END LOOP;--end of USER_DATA Loop
      END;--End for moving user data
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in MOVE_USERS_TO_NEW_DATE 4');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in MOVE_USERS_TO_NEW_DATE 4');
         RAISE retmainexception;
   END;

   -----this procedure has been modified on 12/09

    PROCEDURE move_to_final_data (p_date DATE)
   IS
      /* variables to store order related amounts */
      ln_roam_c2s_out           TEMP_DAILY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE;
      ln_c2s_trans_out_ct       TEMP_DAILY_CHNL_TRANS_MAIN.c2s_transfer_out_count%TYPE;
      ln_c2s_trans_out_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2s_transfer_out_amount%TYPE;
      ln_o2c_trans_in_ct        TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_in_amt       TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_o2c_return_out_ct      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_count%TYPE;
      ln_o2c_return_out_amt     TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_amount%TYPE;
      ln_o2c_withdraw_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_count%TYPE;
      ln_o2c_withdraw_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_amount%TYPE;
      ln_c2c_transfer_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_transfer_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_c2c_transfer_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_count%TYPE;
      ln_c2c_transfer_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_amount%TYPE;
      ln_c2c_return_in_ct       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_count%TYPE;
      ln_c2c_return_in_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_amount%TYPE;
      ln_c2c_return_out_ct      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_count%TYPE;
      ln_c2c_return_out_amt     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_amount%TYPE;
      ln_c2c_withdraw_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_count%TYPE;
      ln_c2c_withdraw_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_amount%TYPE;
      ln_c2c_withdraw_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_count%TYPE;
      ln_c2c_withdraw_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_amount%TYPE;
      ln_differential           TEMP_DAILY_CHNL_TRANS_MAIN.differential%TYPE;
      ln_adjustment_in          TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      ln_adjustment_out         TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      ln_opening_stock          TEMP_DAILY_CHNL_TRANS_MAIN.opening_balance%TYPE;
      ln_closing_stock          DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      ln_stock_updated          DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      ln_productmrp             TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;

      ln_c2c_reverse_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_in_count%TYPE;
      ln_c2c_reverse_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_in_amount%TYPE;
      ln_c2c_reverse_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_out_count%TYPE;
      ln_c2c_reverse_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_out_amount%TYPE;
      ln_o2c_reverse_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_in_count%TYPE;
      ln_o2c_reverse_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_in_amount%TYPE;
      ln_o2c_reverse_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_out_count%TYPE;
      ln_o2c_reverse_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_out_amount%TYPE;
      ln_C2S_TRANSFER_IN_AMOUNT    TEMP_DAILY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_AMOUNT%TYPE;
      ln_C2S_TRANSFER_IN_COUNT    TEMP_DAILY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_COUNT%TYPE;
      ln_REV_DIFFERENTIAL   TEMP_DAILY_CHNL_TRANS_MAIN.REV_DIFFERENTIAL%TYPE;

      /* Cursor Declaration */
      CURSOR user_data (p_date DATE)
      IS
         SELECT user_id, trans_date, product_code, category_code,
                network_code, network_code_for, sender_domain_code,
                roam_c2s_transfer_out_amount, c2s_transfer_out_count,
                c2s_transfer_out_amount, o2c_transfer_in_count,
                o2c_transfer_in_amount, o2c_return_out_count,
                o2c_return_out_amount, o2c_withdraw_out_count,
                o2c_withdraw_out_amount, c2c_transfer_in_count,
                c2c_transfer_in_amount, c2c_transfer_out_count,
                c2c_transfer_out_amount, c2c_return_in_count,
                c2c_return_in_amount, c2c_return_out_count,
                c2c_return_out_amount, c2c_withdraw_in_count,
                c2c_withdraw_in_amount, c2c_withdraw_out_count,
                c2c_withdraw_out_amount, differential, adjustment_in,
                adjustment_out, created_on, opening_balance,product_mrp,
                grph_domain_code,c2c_reverse_in_count,c2c_reverse_in_amount,
        c2c_reverse_out_count,c2c_reverse_out_amount,o2c_reverse_in_count,
        o2c_reverse_in_amount,o2c_reverse_out_count,o2c_reverse_out_amount ,
        C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT,REV_DIFFERENTIAL
       FROM TEMP_DAILY_CHNL_TRANS_MAIN;
          --WHERE trans_date = p_date;
   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      ln_closing_stock := 0;
      ln_stock_updated := 0;
      gv_productmrp := '';

      /* Iterate RETAILER_DATA cursor */
      FOR user_data_cur IN user_data (n_date_for_mis)
      LOOP
         --  DBMS_OUTPUT.PUT_LINE('IN RETAILER CURSOR ');
         gv_userid := user_data_cur.user_id;
         --gd_transaction_date := p_date;
         gd_transaction_date := user_data_cur.trans_date;
         gv_productcode := user_data_cur.product_code;
         gv_categorycode := user_data_cur.category_code;
         gv_networkcode := user_data_cur.network_code;
         gv_networkcodefor := user_data_cur.network_code_for;
         gv_domaincode := user_data_cur.sender_domain_code;
         ln_roam_c2s_out := user_data_cur.roam_c2s_transfer_out_amount;
         ln_c2s_trans_out_ct := user_data_cur.c2s_transfer_out_count;
         ln_c2s_trans_out_amt := user_data_cur.c2s_transfer_out_amount;
         ln_o2c_trans_in_ct := user_data_cur.o2c_transfer_in_count;
         ln_o2c_trans_in_amt := user_data_cur.o2c_transfer_in_amount;
         ln_o2c_return_out_ct := user_data_cur.o2c_return_out_count;
         ln_o2c_return_out_amt := user_data_cur.o2c_return_out_amount;
         ln_o2c_withdraw_out_ct := user_data_cur.o2c_withdraw_out_count;
         ln_o2c_withdraw_out_amt := user_data_cur.o2c_withdraw_out_amount;
         ln_c2c_transfer_in_ct := user_data_cur.c2c_transfer_in_count;
         ln_c2c_transfer_in_amt := user_data_cur.c2c_transfer_in_amount;
         ln_c2c_transfer_out_ct := user_data_cur.c2c_transfer_out_count;
         ln_c2c_transfer_out_amt := user_data_cur.c2c_transfer_out_amount;
         ln_c2c_return_in_ct := user_data_cur.c2c_return_in_count;
         ln_c2c_return_in_amt := user_data_cur.c2c_return_in_amount;
         ln_c2c_return_out_ct := user_data_cur.c2c_return_out_count;
         ln_c2c_return_out_amt := user_data_cur.c2c_return_out_amount;
         ln_c2c_withdraw_in_ct := user_data_cur.c2c_withdraw_in_count;
         ln_c2c_withdraw_in_amt := user_data_cur.c2c_withdraw_in_amount;
         ln_c2c_withdraw_out_ct := user_data_cur.c2c_withdraw_out_count;
         ln_c2c_withdraw_out_amt := user_data_cur.c2c_withdraw_out_amount;
         ln_differential := user_data_cur.differential;
         ln_adjustment_in := user_data_cur.adjustment_in;
         ln_adjustment_out := user_data_cur.adjustment_out;
         ln_opening_stock := user_data_cur.opening_balance;
         gv_grphdomaincode := user_data_cur.grph_domain_code;
         ln_productmrp := user_data_cur.product_mrp;
         ln_c2c_reverse_in_ct := user_data_cur.c2c_reverse_in_count;
         ln_c2c_reverse_in_amt := user_data_cur.c2c_reverse_in_amount;
         ln_c2c_reverse_out_ct := user_data_cur.c2c_reverse_out_count;
         ln_c2c_reverse_out_amt := user_data_cur.c2c_reverse_out_amount;
     ln_o2c_reverse_in_ct := user_data_cur.o2c_reverse_in_count;
         ln_o2c_reverse_in_amt := user_data_cur.o2c_reverse_in_amount;
         ln_o2c_reverse_out_ct := user_data_cur.o2c_reverse_out_count;
         ln_o2c_reverse_out_amt := user_data_cur.o2c_reverse_out_amount;

         ---added on 12/09
         ln_C2S_TRANSFER_IN_AMOUNT := user_data_cur.C2S_TRANSFER_IN_AMOUNT;
         ln_C2S_TRANSFER_IN_COUNT  := user_data_cur.C2S_TRANSFER_IN_COUNT;
         ln_REV_DIFFERENTIAL  :=  user_data_cur.REV_DIFFERENTIAL;
         -- TO DO CHANGE THE FORMULAE
         ln_stock_updated :=
              ln_o2c_trans_in_amt
            + ln_c2c_transfer_in_amt
            + ln_c2c_return_in_amt
            + ln_c2c_withdraw_in_amt
            + ln_differential
            + ln_adjustment_in
            - ln_c2s_trans_out_amt
            - ln_o2c_return_out_amt
            - ln_o2c_withdraw_out_amt
            - ln_c2c_transfer_out_amt
            - ln_c2c_return_out_amt
            - ln_c2c_withdraw_out_amt
            - ln_adjustment_out
        + ln_c2c_reverse_in_amt --added for C2C reversal
        - ln_c2c_reverse_out_amt --added for C2C reversal
        + ln_o2c_reverse_in_amt --added for O2C reversal
        - ln_o2c_reverse_out_amt; --added for O2C reversal

     --ln_stock_updated := ln_stock_updated / ln_productmrp; TBD

         BEGIN
            IF (ln_stock_updated > 0)
            THEN
               ln_closing_stock := ln_opening_stock + ln_stock_updated;
            ELSE
               ln_closing_stock := ln_opening_stock + ln_stock_updated;
            END IF;
            
            BEGIN
                
                UPDATE DAILY_CHNL_TRANS_MAIN
                SET c2s_transfer_out_count=c2s_transfer_out_count+ln_c2s_trans_out_ct,
                    c2s_transfer_out_amount=c2s_transfer_out_amount+ln_c2s_trans_out_amt,
                    differential=differential+ln_differential,
                    adjustment_in=adjustment_in+ln_adjustment_in, 
                    adjustment_out=adjustment_out+ln_adjustment_out,
                    C2S_TRANSFER_IN_AMOUNT=C2S_TRANSFER_IN_AMOUNT+ln_C2S_TRANSFER_IN_AMOUNT ,
                    C2S_TRANSFER_IN_COUNT=C2S_TRANSFER_IN_COUNT+ln_C2S_TRANSFER_IN_COUNT,
                    REV_DIFFERENTIAL=REV_DIFFERENTIAL+ln_REV_DIFFERENTIAL
                  WHERE user_id = gv_userid
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor
                  AND product_code = gv_productcode
                  AND trans_date = gd_transaction_date;
                
                IF SQL%NOTFOUND
                THEN
                    INSERT INTO DAILY_CHNL_TRANS_MAIN
                        (user_id, trans_date, product_code,
                         category_code, network_code, network_code_for,
                         sender_domain_code, roam_c2s_transfer_out_amount,opening_balance,
                         closing_balance,c2s_transfer_out_count, c2s_transfer_out_amount,
                         o2c_transfer_in_count, o2c_transfer_in_amount,o2c_return_out_count,
                         o2c_return_out_amount,o2c_withdraw_out_count, o2c_withdraw_out_amount,
                         c2c_transfer_in_count, c2c_transfer_in_amount,c2c_transfer_out_count,
                         c2c_transfer_out_amount,c2c_return_in_count, c2c_return_in_amount,
                         c2c_return_out_count, c2c_return_out_amount,c2c_withdraw_in_count,
                         c2c_withdraw_in_amount,c2c_withdraw_out_count, c2c_withdraw_out_amount,
                         differential, adjustment_in, adjustment_out,
                         created_on, grph_domain_code, c2c_reverse_in_count,
                         c2c_reverse_in_amount,c2c_reverse_out_count, c2c_reverse_out_amount,
                         o2c_reverse_in_count, o2c_reverse_in_amount,o2c_reverse_out_count,
                         o2c_reverse_out_amount,C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT,REV_DIFFERENTIAL
                        )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                         gv_categorycode, gv_networkcode, gv_networkcodefor,
                         gv_domaincode, ln_roam_c2s_out,
                         ln_opening_stock, ln_closing_stock,
                         ln_c2s_trans_out_ct, ln_c2s_trans_out_amt,
                         ln_o2c_trans_in_ct, ln_o2c_trans_in_amt,
                         ln_o2c_return_out_ct, ln_o2c_return_out_amt,
                         ln_o2c_withdraw_out_ct, ln_o2c_withdraw_out_amt,
                         ln_c2c_transfer_in_ct, ln_c2c_transfer_in_amt,
                         ln_c2c_transfer_out_ct, ln_c2c_transfer_out_amt,
                         ln_c2c_return_in_ct, ln_c2c_return_in_amt,
                         ln_c2c_return_out_ct, ln_c2c_return_out_amt,
                         ln_c2c_withdraw_in_ct, ln_c2c_withdraw_in_amt,
                         ln_c2c_withdraw_out_ct, ln_c2c_withdraw_out_amt,
                         ln_differential, ln_adjustment_in,
                         ln_adjustment_out, gd_createdon, gv_grphdomaincode,
                         ln_c2c_reverse_in_ct, ln_c2c_reverse_in_amt,
                         ln_c2c_reverse_out_ct, ln_c2c_reverse_out_amt,
                         ln_o2c_reverse_in_ct, ln_o2c_reverse_in_amt,
                         ln_o2c_reverse_out_ct, ln_o2c_reverse_out_amt,
                         ln_C2S_TRANSFER_IN_AMOUNT ,ln_C2S_TRANSFER_IN_COUNT, ln_REV_DIFFERENTIAL
                        );
                END IF;
            EXCEPTION
                WHEN OTHERS
                THEN
                    DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in MOVE_TO_FINAL_DATA 1, User:' || gv_userid || SQLERRM );
                    v_messageforlog := 'OTHERS Exception in MOVE_TO_FINAL_DATA 1, User:'|| gv_userid || gd_transaction_date;
                    v_sqlerrmsgforlog := SQLERRM;
                    RAISE procexception;
            END;
         EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION CAUGHT in MOVE_TO_FINAL_DATA 2, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'Exception in MOVE_TO_FINAL_DATA 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                                    --end of user insertion block
      END LOOP;                                                       --end of
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception CAUGHT in MOVE_TO_FINAL_DATA 3');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in MOVE_TO_FINAL_DATA 3');
         RAISE retmainexception;
   END;                                              -- of Retailer Moving data

   PROCEDURE ret_refills_data_proc (p_date DATE)
   IS
      /* Variables for refill amount values */
      lv_servicetype            DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_sub_service            DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;
      ln_c2s_trans_ct           DAILY_C2S_TRANS_DETAILS.transaction_count%TYPE:= 0;
      ln_c2s_trans_amt          DAILY_C2S_TRANS_DETAILS.transaction_amount%TYPE:= 0;
      ln_c2s_trans_tax1         DAILY_C2S_TRANS_DETAILS.total_tax1%TYPE  := 0;
      ln_c2s_trans_tax2         DAILY_C2S_TRANS_DETAILS.total_tax2%TYPE  := 0;
      ln_c2s_trans_tax3         DAILY_C2S_TRANS_DETAILS.total_tax3%TYPE  := 0;
      ln_c2s_sender_trans_amt   DAILY_C2S_TRANS_DETAILS.sender_transfer_amount%TYPE:= 0;
      ln_c2s_rec_credit_amt     DAILY_C2S_TRANS_DETAILS.receiver_credit_amount%TYPE:= 0;
      ln_c2s_rec_access_fee     DAILY_C2S_TRANS_DETAILS.receiver_access_fee%TYPE:= 0;
      ln_c2s_diff_tax1          DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax1%TYPE:= 0;
      ln_c2s_diff_tax2          DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax2%TYPE:= 0;
      ln_c2s_diff_tax3          DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax3%TYPE:= 0;
      ln_c2s_receiver_bonus     DAILY_C2S_TRANS_DETAILS.receiver_bonus%TYPE:= 0;
      ln_c2s_diff_amt           DAILY_C2S_TRANS_DETAILS.differential_amount%TYPE:= 0;
      ln_c2s_diff_count           DAILY_C2S_TRANS_DETAILS.differential_count%TYPE:= 0;
      ln_roam_c2s_amount        DAILY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE:= 0;
      lv_sendercategory DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE :=0;
      lv_receiverserviceclassid DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE :=0;
      ln_receiver_validity        DAILY_C2S_TRANS_DETAILS.receiver_validity%TYPE:=0;
      ln_receiver_bonus_validity    DAILY_C2S_TRANS_DETAILS.receiver_bonus_validity%TYPE:=0;
      ln_c2s_sender_penalty DAILY_C2S_TRANS_DETAILS.PENALTY%TYPE:=0; 
      ln_c2s_sender_ownwer_penalty DAILY_C2S_TRANS_DETAILS.OWNER_PENALTY%TYPE:=0; 
      ln_c2s_sender_roam_amount DAILY_C2S_TRANS_DETAILS.Roam_amount%TYPE:=0; 
      ln_c2s_penalty_count DAILY_C2S_TRANS_DETAILS.PENALTY_COUNT%TYPE:=0;


      /* Cursor Declaration */
      CURSOR refill_data (p_date DATE)
      IS
    SELECT   Mast.transfer_date,Mast.sender_id, Mast.network_code,Mast.service_class_id,
                  Mast.receiver_network_code, Mast.product_code,
                  Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                  Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                  COUNT (Mast.transfer_id) c2s_count,
                  SUM(Mast.transfer_value) c2s_amount,
                  NVL(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                  NVL(SUM(Mast.receiver_access_fee),0) c2s_recfee,
                  NVL(SUM(Mast.receiver_tax1_value),0) c2s_rectax1,
                  NVL(SUM(Mast.receiver_tax2_value),0) c2s_rectax2,
                  NVL(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                  NVL(SUM(Mast.receiver_bonus_value),0) c2s_rec_bonus,
                  NVL(SUM(adj.tax1_value),0) diff_tax1,
                  NVL(SUM(adj.tax2_value),0) diff_tax2,
                  NVL(SUM(adj.tax3_value),0) diff_tax3,
                  NVL(SUM(adj.transfer_value),0) diff_amount,
                  SUM(CASE  WHEN NVL(adj.transfer_value,0) <>0 THEN 1 ELSE 0 END) diff_count,
                  NVL(SUM(Mast.receiver_validity),0) validity,
                  NVL(SUM(Mast.receiver_bonus_validity),0) bonus_validity,
                  NVL(SUM(Mast.PENALTY),0) PENALTY,
                  NVL(SUM(Mast.OWNER_PENALTY),0) OWNER_PENALTY,
                  NVL(SUM(case when NVL(PENALTY,0)>0 then 1 else 0 end),0) PENALTY_COUNT
          FROM (SELECT
                  c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                  c2strans.receiver_network_code, p.product_code,
                  p.unit_value,ug.grph_domain_code, cat.category_code,
                  cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                  c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                  c2strans.receiver_access_fee,c2strans.receiver_tax1_value,c2strans.receiver_tax2_value,
                  c2strans.sender_transfer_value,c2strans.receiver_bonus_value,c2strans.receiver_validity,
                  c2strans.receiver_bonus_validity,
                  c2strans.PENALTY,
                  c2strans.owner_penalty
        FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P,
        CATEGORIES cat,USER_GEOGRAPHIES ug
        WHERE --(c2strans.transfer_date = p_date or c2strans.reconciliation_date = p_date) AND
          c2strans.transfer_status = '200'
        AND  c2strans.transfer_type = 'TXN'
        AND   p.product_code=c2strans.product_code
        AND   cat.category_code=c2strans.sender_category
        AND ug.user_id=c2strans.sender_id
        ) Mast,ADJUSTMENTS_MISTMP adj
        WHERE  Mast.transfer_id =adj.reference_id (+)
        AND    Mast.sender_id   = adj.user_id(+)
        GROUP BY Mast.transfer_date,Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
             Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;


   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      lv_servicetype := '';
      lv_sub_service := '';
      lv_sendercategory := '';
      lv_receiverserviceclassid := '';
      gv_productmrp := '';

      /* Iterate thru. the Refill cursor */
      FOR ret_data_cur IN refill_data (n_date_for_mis)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         --cnt := 0;
         lv_sendercategory := ret_data_cur.category_code;
         lv_receiverserviceclassid := ret_data_cur.service_class_id;
         gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
     lv_sub_service:= ret_data_cur.sub_service;
         --gd_transaction_date := p_date;
         gd_transaction_date := ret_data_cur.transfer_date;
         ln_c2s_trans_ct := ret_data_cur.c2s_count;
         ln_c2s_trans_amt := ret_data_cur.c2s_amount;
         ln_c2s_trans_tax1 := ret_data_cur.c2s_rectax1;
         ln_c2s_trans_tax2 := ret_data_cur.c2s_rectax2;
         ln_c2s_trans_tax3 := 0;
         ln_c2s_sender_trans_amt := ret_data_cur.c2s_sender_value;
         ln_c2s_rec_credit_amt := ret_data_cur.c2s_rectrans;
         ln_c2s_rec_access_fee := ret_data_cur.c2s_recfee;
         ln_c2s_diff_tax1 := ret_data_cur.diff_tax1;
         ln_c2s_diff_tax2 := ret_data_cur.diff_tax2;
         ln_c2s_diff_tax3 := ret_data_cur.diff_tax3;
         ln_c2s_receiver_bonus := ret_data_cur.c2s_rec_bonus;
         ln_c2s_diff_amt := ret_data_cur.diff_amount;
         ln_receiver_validity := ret_data_cur.validity;
         ln_receiver_bonus_validity := ret_data_cur.bonus_validity;
         gv_productmrp := ret_data_cur.unit_value;
         ln_c2s_diff_count:= ret_data_cur.diff_count;
         ln_c2s_sender_penalty:=ret_data_cur.penalty;
         ln_c2s_sender_ownwer_penalty:=ret_data_cur.OWNER_PENALTY ; 
         ln_c2s_sender_roam_amount:=0;
         ln_c2s_penalty_count:=ret_data_cur.PENALTY_COUNT ; 
         
         IF (gv_networkcode <> gv_networkcodefor)
            THEN
            ln_c2s_sender_roam_amount:=ln_c2s_trans_amt;
        END IF;

         /* insert into temp_retailer_mis table */
         BEGIN
               SELECT 1 INTO user_rcd_count
               FROM DAILY_C2S_TRANS_DETAILS
               WHERE user_id = gv_userid    ----  ,sender_category_code, receiver_service_class_id,SERVICE_TYPE, sub_service,
               AND receiver_network_code = gv_networkcodefor
               AND trans_date = gd_transaction_date
               AND sender_category_code=gv_categorycode
               AND receiver_service_class_id=lv_receiverserviceclassid
               AND SERVICE_TYPE=lv_servicetype
               AND sub_service=lv_sub_service;

               IF SQL%NOTFOUND
               THEN
                  v_messageforlog := 'sqlexception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                 --cnt := 0;
                 user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            --IF cnt = 0
            IF user_rcd_count = 0
            THEN
                BEGIN
                    INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
                         sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, sub_service, total_tax1,
                         total_tax2, total_tax3,
                         sender_transfer_amount, receiver_credit_amount,
                         receiver_access_fee, differential_adjustment_tax1,
                         differential_adjustment_tax2,
                         differential_adjustment_tax3, receiver_bonus,
                         created_on, transaction_amount, transaction_count,
                         differential_amount,receiver_validity,receiver_bonus_validity,differential_count,
                          penalty,owner_penalty,roam_amount,penalty_count
                        )
                    VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
                          lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype, lv_sub_service,ln_c2s_trans_tax1,
                         ln_c2s_trans_tax2, ln_c2s_trans_tax3,
                         ln_c2s_sender_trans_amt, ln_c2s_rec_credit_amt,
                         ln_c2s_rec_access_fee, ln_c2s_diff_tax1,
                         ln_c2s_diff_tax2,
                         ln_c2s_diff_tax3, ln_c2s_receiver_bonus,
                         gd_createdon, ln_c2s_trans_amt, ln_c2s_trans_ct,
                         ln_c2s_diff_amt,ln_receiver_validity,
                         ln_receiver_bonus_validity,ln_c2s_diff_count,
                         ln_c2s_sender_penalty,ln_c2s_sender_ownwer_penalty,
                         ln_c2s_sender_roam_amount,ln_c2s_penalty_count
                        );
                EXCEPTION
                    WHEN sqlexception
                    THEN
                        DBMS_OUTPUT.PUT_LINE(   'sqlexception in RET_REFILLS_DATA_PROC 1, User:' || gv_userid || SQLERRM );
                        v_messageforlog :='sqlexception in RET_REFILLS_DATA_procedure 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                        v_sqlerrmsgforlog := SQLERRM;
                        RAISE procexception;
                    WHEN OTHERS
                    THEN
                        DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 1, User:' || gv_userid || SQLERRM );
                        v_messageforlog := 'OTHERS EXCEPTION in RET_REFILLS_DATA_procedure 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                        v_sqlerrmsgforlog := SQLERRM;
                        RAISE procexception;
                END;                                --end of retailer insertion block
            ELSE
                UPDATE DAILY_C2S_TRANS_DETAILS
                SET total_tax1=total_tax1+ln_c2s_trans_tax1,
                    total_tax2=total_tax2+ln_c2s_trans_tax2,
                    total_tax3=total_tax3+ln_c2s_trans_tax3,
                    sender_transfer_amount=sender_transfer_amount+ln_c2s_sender_trans_amt,
                    receiver_credit_amount=receiver_credit_amount+ln_c2s_rec_credit_amt,
                    receiver_access_fee=receiver_access_fee+ln_c2s_rec_access_fee,
                    differential_adjustment_tax1=differential_adjustment_tax1+ln_c2s_diff_tax1,
                    differential_adjustment_tax2=differential_adjustment_tax2+ln_c2s_diff_tax2,
                    differential_adjustment_tax3=differential_adjustment_tax3+ln_c2s_diff_tax3,
                    receiver_bonus=receiver_bonus+ln_c2s_receiver_bonus,
                    transaction_amount=transaction_amount+ln_c2s_trans_amt,
                    transaction_count=transaction_count+ln_c2s_trans_ct,
                    differential_amount=differential_amount+ln_c2s_diff_amt,
                    differential_count=differential_count+ln_c2s_diff_count,
                    penalty=penalty+ln_c2s_sender_penalty,
                    owner_penalty=owner_penalty+ln_c2s_sender_ownwer_penalty,
                    roam_amount=roam_amount+ln_c2s_sender_roam_amount,
                    penalty_count=penalty_count+ln_c2s_penalty_count
                WHERE user_id = gv_userid
                AND receiver_network_code = gv_networkcodefor
                AND sender_category_code = lv_sendercategory
                AND receiver_service_class_id = lv_receiverserviceclassid
                AND SERVICE_TYPE = lv_servicetype
                AND sub_service = lv_sub_service
                AND trans_date = gd_transaction_date;
            END IF;
            
         BEGIN
            BEGIN
               SELECT 1 INTO user_rcd_count
               FROM TEMP_DAILY_CHNL_TRANS_MAIN
               WHERE user_id = gv_userid
               AND network_code = gv_networkcode
               AND network_code_for = gv_networkcodefor
               AND product_code = gv_productcode
               AND grph_domain_code = gv_grphdomaincode
               AND trans_date = gd_transaction_date;

               IF SQL%NOTFOUND
               THEN
                  v_messageforlog := 'sqlexception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                 user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            IF user_rcd_count = 0
            THEN
               INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                           (user_id, trans_date, product_code,
                            category_code, network_code,
                            network_code_for, sender_domain_code,ROAM_C2S_TRANSFER_OUT_AMOUNT,
                            created_on, product_mrp,grph_domain_code,
                            c2s_transfer_out_count, c2s_transfer_out_amount,
                            differential
                           )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                            gv_categorycode, gv_networkcode,
                            gv_networkcodefor, gv_domaincode,ln_c2s_sender_roam_amount,
                            gd_createdon, gv_productmrp,gv_grphdomaincode,
                            ln_c2s_trans_ct, ln_c2s_trans_amt,
                            ln_c2s_diff_amt
                           );
            ELSE
               UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
                  SET c2s_transfer_out_count = c2s_transfer_out_count + ln_c2s_trans_ct,
                      c2s_transfer_out_amount = c2s_transfer_out_amount + ln_c2s_trans_amt,
                      differential = differential + ln_c2s_diff_amt,
                      ROAM_C2S_TRANSFER_OUT_AMOUNT = ROAM_C2S_TRANSFER_OUT_AMOUNT + ln_c2s_sender_roam_amount
                WHERE user_id = gv_userid
                  AND trans_date = gd_transaction_date
                  AND product_code = gv_productcode
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor;
            END IF;

            --Update Roam Counts
            /*IF (gv_networkcode <> gv_networkcodefor)
            THEN
               sp_update_roam_c2s_trans (gv_userid,
                                         gd_transaction_date,
                                         gv_productcode,
                                         gv_categorycode,
                                         gv_networkcode,
                                         gv_networkcodefor,
                                         gv_domaincode,
                                         gv_productmrp,
                                         gv_grphdomaincode,
                                         ln_c2s_trans_amt
                                        );
            END IF;*/
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE (   ' sqlexception SQL/OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'sqlexception Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' date=' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid  || SQLERRM );
               v_messageforlog :='OTHERS Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                             --end of distributor insertion block
      END LOOP;
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
   END;                                                 --end of Refill values

      ----added on 12/09 ret_reverse_data_proc

    PROCEDURE ret_reverse_data_proc (p_date DATE)
   IS
      /* Variables for refill amount values */
      lv_servicetype                DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_sub_service                DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;

      ln_c2s_rev_ct                   DAILY_C2S_TRANS_DETAILS.REVERSE_COUNT%TYPE:= 0;
      ln_c2s_rev_amt                  DAILY_C2S_TRANS_DETAILS.REVERSE_AMOUNT%TYPE:= 0;

      ln_c2s_sender_rev_amt           DAILY_C2S_TRANS_DETAILS.SENDER_REVERSE_AMOUNT%TYPE:= 0;
      ln_c2s_rec_debit_amt             DAILY_C2S_TRANS_DETAILS.RECEIVER_DEBIT_AMOUNT%TYPE:= 0;

      ln_c2s_rev_diff_amt           DAILY_C2S_TRANS_DETAILS.REVERSE_DIFF_AMOUNT%TYPE:= 0;
      ln_c2s_rev_diff_count         DAILY_C2S_TRANS_DETAILS.REVERSE_DIFF_COUNT%TYPE:= 0;

      lv_sendercategory             DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE :=0;
      lv_receiverserviceclassid     DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE :=0;
      ln_c2s_sender_penalty         DAILY_C2S_TRANS_DETAILS.PENALTY%TYPE:=0; 
      ln_c2s_sender_owner_penalty     DAILY_C2S_TRANS_DETAILS.OWNER_PENALTY%TYPE:=0; 
      ln_c2s_sender_roam_amount     DAILY_C2S_TRANS_DETAILS.Roam_amount%TYPE:=0; 
      /* Cursor Declaration */
        CURSOR refill_data (p_date DATE)
        IS
        SELECT  Mast.transfer_date,Mast.sender_id, Mast.network_code,Mast.service_class_id,
                Mast.receiver_network_code, Mast.product_code,
                Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                COUNT (Mast.transfer_id) c2s_count,
                SUM(Mast.transfer_value) c2s_amount,
                NVL(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                NVL(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                NVL(SUM(adj.transfer_value),0) diff_amount,
                SUM(CASE  WHEN NVL(adj.transfer_value,0) <> 0 THEN 1 ELSE 0 END) diff_count,
                NVL(SUM(Mast.penalty),0) penalty,
                NVL(SUM(Mast.owner_penalty),0) owner_penalty
                FROM (SELECT
                        c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                        c2strans.receiver_network_code, p.product_code,
                        p.unit_value,ug.grph_domain_code, cat.category_code,
                        cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                        c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                        c2strans.sender_transfer_value,c2strans.PENALTY,
                        c2strans.owner_penalty
                        FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P, CATEGORIES cat,USER_GEOGRAPHIES ug
                        WHERE --(c2strans.transfer_date = p_date or c2strans.reconciliation_date = p_date) AND 
                           c2strans.transfer_status = '200'
                        AND   SERVICE_TYPE = 'REV'
                        AND   p.product_code=c2strans.product_code
                        AND   cat.category_code=c2strans.sender_category
                        AND   ug.user_id=c2strans.sender_id
                      ) Mast, ADJUSTMENTS_MISTMP adj
                WHERE  Mast.transfer_id = adj.reference_id (+)
                AND    Mast.sender_id   = adj.user_id(+)
                GROUP BY Mast.transfer_date,Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
                Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;


   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      lv_servicetype := '';
      lv_sub_service := '';
      lv_sendercategory := '';
      lv_receiverserviceclassid := '';
      gv_productmrp := '';

      /* Iterate thru. the Refill cursor */
      FOR ret_data_cur IN refill_data (n_date_for_mis)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         lv_sendercategory := ret_data_cur.category_code;
         lv_receiverserviceclassid := ret_data_cur.service_class_id;
         gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
         lv_sub_service:= ret_data_cur.sub_service;
         --gd_transaction_date := p_date;
         gd_transaction_date := ret_data_cur.transfer_date;
         gv_productmrp := ret_data_cur.unit_value;
         ln_c2s_rev_ct := ret_data_cur.c2s_count;
         ln_c2s_rev_amt := ret_data_cur.c2s_amount;
         ln_c2s_sender_rev_amt := ret_data_cur.c2s_sender_value;
         ln_c2s_rec_debit_amt := ret_data_cur.c2s_rectrans;
         ln_c2s_rev_diff_amt := ret_data_cur.diff_amount;
         ln_c2s_rev_diff_count:= ret_data_cur.diff_count;
         ln_c2s_sender_penalty:=ret_data_cur.penalty;
         ln_c2s_sender_owner_penalty:=ret_data_cur.owner_penalty;
        ln_c2s_sender_roam_amount:=0;
        
         /* insert into DAILY_C2S_TRANS_DETAILS table */
        IF (gv_networkcode <> gv_networkcodefor)
            THEN
            ln_c2s_sender_roam_amount:=ln_c2s_rev_amt;
        END IF;

         BEGIN
               SELECT 1 INTO user_rcd_count
               FROM DAILY_C2S_TRANS_DETAILS
               WHERE user_id = gv_userid
               AND receiver_network_code = gv_networkcodefor
               AND sender_category_code = lv_sendercategory
               AND receiver_service_class_id = lv_receiverserviceclassid
               AND SERVICE_TYPE = lv_servicetype
               AND sub_service = lv_sub_service
               AND trans_date = gd_transaction_date;

               EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                 user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
         END;

         IF user_rcd_count = 0
            THEN
                INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
                         sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, sub_service,SENDER_REVERSE_AMOUNT ,
                        RECEIVER_DEBIT_AMOUNT ,REVERSE_AMOUNT ,REVERSE_COUNT,
                        REVERSE_DIFF_AMOUNT ,REVERSE_DIFF_COUNT,PENALTY,
                        OWNER_PENALTY,ROAM_AMOUNT
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
                          lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype, lv_sub_service,ln_c2s_sender_rev_amt,
                         ln_c2s_rec_debit_amt,ln_c2s_rev_amt,ln_c2s_rev_ct,
                         ln_c2s_rev_diff_amt,ln_c2s_rev_diff_count,ln_c2s_sender_penalty,
                        ln_c2s_sender_owner_penalty,ln_c2s_sender_roam_amount
                        );
         ELSE
                UPDATE DAILY_C2S_TRANS_DETAILS
                SET SENDER_REVERSE_AMOUNT =  SENDER_REVERSE_AMOUNT + ln_c2s_sender_rev_amt,
                    RECEIVER_DEBIT_AMOUNT =  RECEIVER_DEBIT_AMOUNT + ln_c2s_rec_debit_amt,
                    REVERSE_AMOUNT        = REVERSE_AMOUNT + ln_c2s_rev_amt,
                    REVERSE_COUNT         = REVERSE_COUNT + ln_c2s_rev_ct,
                    REVERSE_DIFF_AMOUNT   = REVERSE_DIFF_AMOUNT + ln_c2s_rev_diff_amt,
                    REVERSE_DIFF_COUNT    = REVERSE_DIFF_COUNT + ln_c2s_rev_diff_count,
                    PENALTY = PENALTY+ln_c2s_sender_penalty,
                    OWNER_PENALTY = OWNER_PENALTY+ln_c2s_sender_owner_penalty,
                    roam_amount=roam_amount+ln_c2s_sender_roam_amount
                WHERE user_id = gv_userid
                AND receiver_network_code = gv_networkcodefor
                AND sender_category_code = lv_sendercategory
                AND receiver_service_class_id = lv_receiverserviceclassid
                AND SERVICE_TYPE = lv_servicetype
                AND sub_service = lv_sub_service
                AND trans_date = gd_transaction_date;
         END IF;

          /* insert into TEMP_DAILY_CHNL_TRANS_MAIN table */

         BEGIN
            BEGIN
                SELECT 1 INTO user_rcd_count
                FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE user_id = gv_userid
                AND network_code = gv_networkcode
                AND network_code_for = gv_networkcodefor
                AND product_code = gv_productcode
                AND grph_domain_code = gv_grphdomaincode
                AND trans_date = gd_transaction_date;

                IF SQL%NOTFOUND
                THEN
                  v_messageforlog := 'sqlexception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
                END IF;
                EXCEPTION
                WHEN NO_DATA_FOUND
                THEN                        --when no row returned for the user
                 user_rcd_count := 0;
                WHEN OTHERS
                THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid);
                  v_messageforlog := 'OTHERS SQL Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            IF user_rcd_count = 0 THEN
                INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                           (user_id, trans_date, product_code,
                            category_code, network_code,
                            network_code_for, sender_domain_code,
                            created_on, product_mrp,grph_domain_code,ROAM_C2S_TRANSFER_OUT_AMOUNT,
                            C2S_TRANSFER_IN_COUNT , C2S_TRANSFER_IN_AMOUNT,
                            REV_DIFFERENTIAL
                           )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                            gv_categorycode, gv_networkcode,
                            gv_networkcodefor, gv_domaincode,
                            gd_createdon, gv_productmrp,gv_grphdomaincode, (0-ln_c2s_sender_roam_amount),
                            ln_c2s_rev_ct, ln_c2s_rev_amt,ln_c2s_rev_diff_amt
                           );
            ELSE
                UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
                  SET C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + ln_c2s_rev_ct,
                      C2S_TRANSFER_IN_AMOUNT = C2S_TRANSFER_IN_AMOUNT + ln_c2s_rev_amt,
                      REV_DIFFERENTIAL = REV_DIFFERENTIAL + ln_c2s_rev_diff_amt,
                      ROAM_C2S_TRANSFER_OUT_AMOUNT = ROAM_C2S_TRANSFER_OUT_AMOUNT - ln_c2s_sender_roam_amount
                  WHERE user_id = gv_userid
                  AND trans_date = gd_transaction_date
                  AND product_code = gv_productcode
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor;
            END IF;

            EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE (   ' sqlexception SQL/OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'sqlexception Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' date=' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:' || gv_userid  || SQLERRM );
               v_messageforlog :='OTHERS Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                             --end of distributor insertion block
      END LOOP;
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
   END;



   PROCEDURE sp_update_roam_c2s_trans (
      p_user_id          IN   VARCHAR2,
      p_date             IN   DATE,
      p_productid        IN   VARCHAR2,
      p_categorycode     IN   VARCHAR2,
      p_homelocation     IN   VARCHAR2,
      p_targetlocation   IN   VARCHAR2,
      p_domaincode       IN   VARCHAR2,
      p_productmrp       IN   NUMBER,
      p_geodomaincode    IN   VARCHAR2,
      p_trans_amt        IN   NUMBER
      )
   IS
   BEGIN
      BEGIN
         BEGIN
            SELECT 1 INTO user_rcd_count
            FROM TEMP_DAILY_CHNL_TRANS_MAIN
            WHERE user_id = gv_userid
            AND network_code = gv_networkcode
            AND network_code_for = gv_networkcodefor
            AND product_code = gv_productcode
            AND grph_domain_code = gv_grphdomaincode
            AND trans_date = gd_transaction_date;

            IF SQL%NOTFOUND
            THEN
               v_messageforlog := 'SQL%NOTFOUND in SP_UPDATE_ROAM_C2S_TRANS 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE sqlexception;
            END IF;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN                           --when no row returned for the user
               user_rcd_count := 0;
            WHEN OTHERS
            THEN
               v_messageforlog := 'OTHERS SQL Exception in SP_UPDATE_ROAM_C2S_TRANS 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE sqlexception;
         END;

         IF user_rcd_count = 0
         THEN
            INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                        (user_id, trans_date, product_code, category_code,
                         network_code, network_code_for, sender_domain_code,
                         created_on, product_mrp,grph_domain_code,
                         roam_c2s_transfer_out_amount
                        )
                 VALUES (p_user_id, p_date, p_productid, p_categorycode,
                         p_homelocation, p_homelocation, p_domaincode,
                         gd_createdon, p_productmrp,p_geodomaincode,
                         p_trans_amt
                        );
         ELSE
            UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount =
                                    roam_c2s_transfer_out_amount + p_trans_amt
             WHERE user_id = p_user_id
               AND trans_date = p_date
               AND product_code = p_productid
               AND network_code = p_homelocation
               AND network_code_for = p_homelocation;
         END IF;
      EXCEPTION
         WHEN sqlexception
         THEN
            DBMS_OUTPUT.PUT_LINE (   'sqlexception in SP_UPDATE_ROAM_C2S_TRANS 2, User:' || gv_userid || SQLERRM );
            v_messageforlog := 'sqlexception in SP_UPDATE_ROAM_C2S_TRANS 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
            v_sqlerrmsgforlog := SQLERRM;
            RAISE procexception;
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_UPDATE_ROAM_C2S_TRANS 2, User:' || gv_userid || SQLERRM );
            v_messageforlog := 'OTHERS Exception in SP_UPDATE_ROAM_C2S_TRANS 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
            v_sqlerrmsgforlog := SQLERRM;
            RAISE procexception;
      END;                                --end of distributor insertion block
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception IN SP_UPDATE_ROAM_C2S_TRANS 3;' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION IN SP_UPDATE_ROAM_C2S_TRANS 3:' || SQLERRM);
         RAISE retmainexception;
   END;

   PROCEDURE sp_chnl_transfer_in_data_proc (
      p_date              IN   DATE,
      p_transfersubtype   IN   VARCHAR2
   )
   IS
      /* variables to store order related amounts */
      ln_o2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_c2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_c2creturns         TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_count%TYPE;
      ln_c2creturn_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_amount%TYPE;
      ln_c2cwithdraws       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_count%TYPE;
      ln_c2cwithdraws_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_amount%TYPE;
      ln_adjustment_amt     TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      sql_stmt              VARCHAR2 (2000);



      /* Cursor Declaration */
      CURSOR chnl_data (p_date DATE, p_transfersubtype VARCHAR2)
      IS
         SELECT   ch.to_user_id, ch.network_code, ch.network_code_for,
                  chi.product_code, ug.grph_domain_code grph_domain_code,
                  ch.receiver_category_code category_code, ch.to_domain_code domain_code, chi.user_unit_price unit_value,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN 1 ELSE 0 END) o2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN chi.approved_quantity ELSE 0 END) o2c_amount,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN 1 ELSE 0 END) c2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN chi.approved_quantity ELSE 0 END) c2c_amount
             FROM CHANNEL_TRANSFERS ch,
                  CHANNEL_TRANSFERS_ITEMS chi,
          USER_GEOGRAPHIES ug
              WHERE ch.transfer_date<=p_date+1
              AND TRUNC(ch.close_date) = p_date
              AND ch.transfer_sub_type = p_transfersubtype
              AND ch.transfer_id = chi.transfer_id
          AND ch.to_user_id=ug.user_id
              AND ch.status = 'CLOSE'
              AND ch.to_user_id <> 'OPT'
         GROUP BY ch.to_user_id,
                  ch.network_code,
                  ch.network_code_for,
                  chi.product_code,
                  ch.receiver_category_code,
                  ug.grph_domain_code,
                  ch.to_domain_code,
                  chi.user_unit_price;
   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      gv_productmrp := '';

      /* Iterate CHNL_DATA cursor */
      FOR chnl_data_cur IN chnl_data (n_date_for_mis, p_transfersubtype)
      LOOP
         gv_userid := chnl_data_cur.to_user_id;
         gv_networkcode := chnl_data_cur.network_code;
         gv_networkcodefor := chnl_data_cur.network_code_for;
         gv_grphdomaincode := chnl_data_cur.grph_domain_code;
         gv_productcode := chnl_data_cur.product_code;
         gv_categorycode := chnl_data_cur.category_code;
         gv_domaincode := chnl_data_cur.domain_code;
         gd_transaction_date := p_date;
         gv_productmrp := chnl_data_cur.unit_value;
         user_rcd_count := 0;
         ln_o2c_trans := chnl_data_cur.o2c_num_trans;
         ln_o2c_trans_amt := chnl_data_cur.o2c_amount;
         ln_c2c_trans := chnl_data_cur.c2c_num_trans;
         ln_c2c_trans_amt := chnl_data_cur.c2c_amount;
         sql_stmt := '';

         BEGIN
            BEGIN
               SELECT 1 INTO user_rcd_count
               FROM TEMP_DAILY_CHNL_TRANS_MAIN
               WHERE user_id = gv_userid
               AND network_code = gv_networkcode
               AND network_code_for = gv_networkcodefor
               AND product_code = gv_productcode
               AND grph_domain_code = gv_grphdomaincode
               AND trans_date = gd_transaction_date;

               IF SQL%NOTFOUND
               THEN
                  v_messageforlog := 'SQL%NOTFOUND Exception NOTFOUND DATA in SP_CHNL_TRANSFER_IN_DATA_PROC 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                        --when no row returned for the user
                  user_rcd_count := 0;
               WHEN OTHERS
               THEN
                  v_messageforlog := 'OTHERS SQL Exception in SP_CHNL_TRANSFER_IN_DATA_PROC 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;

            IF user_rcd_count = 0
            THEN
               sql_stmt :=
                  'INSERT INTO temp_daily_chnl_trans_main (user_id, trans_date, product_code, category_code, network_code, network_code_for, sender_domain_code, ';
               sql_stmt :=
                   sql_stmt || ' created_on, product_mrp,grph_domain_code, ';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_return_in_count, c2c_return_in_amount ';
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_withdraw_in_count, c2c_withdraw_in_amount ';
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_transfer_in_count, o2c_transfer_in_amount,c2c_transfer_in_count, c2c_transfer_in_amount ';
           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_reverse_in_count, o2c_reverse_in_amount, c2c_reverse_in_count, c2c_reverse_in_amount ';
               END IF;

               sql_stmt := sql_stmt || ')';

               IF (p_transfersubtype = 'T' OR p_transfersubtype = 'X')
               THEN
                  sql_stmt := sql_stmt || ' VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13,:14)';
               ELSE
                  sql_stmt := sql_stmt || ' VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12)';
               END IF;

             IF (p_transfersubtype = 'T' OR p_transfersubtype = 'X')
             THEN
               EXECUTE IMMEDIATE sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_o2c_trans,ln_o2c_trans_amt,ln_c2c_trans,ln_c2c_trans_amt;
             ELSE
                EXECUTE IMMEDIATE sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_c2c_trans,ln_c2c_trans_amt;

             END IF;

            ELSE
               sql_stmt := 'UPDATE temp_daily_chnl_trans_main  SET  ';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_return_in_count='
                     || ln_c2c_trans
                     || ', c2c_return_in_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_withdraw_in_count='
                     || ln_c2c_trans
                     || ',c2c_withdraw_in_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_transfer_in_count='
                     || ln_o2c_trans
                     || ', o2c_transfer_in_amount='
                     || ln_o2c_trans_amt
                     || ', c2c_transfer_in_count='
                     || ln_c2c_trans
                     || ', c2c_transfer_in_amount ='
                     || ln_c2c_trans_amt;
           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || 'c2c_reverse_in_count='
                     || ln_c2c_trans
                     || ', c2c_reverse_in_amount ='
                     || ln_c2c_trans_amt
             || ', o2c_reverse_in_count='
                     || ln_o2c_trans
                     || ', o2c_reverse_in_amount ='
                     || ln_o2c_trans_amt;
               END IF;

               sql_stmt := sql_stmt|| 'WHERE user_id=:1 AND trans_date=:2 AND product_code=:3 AND network_code=:4 AND network_code_for=:5';

              EXECUTE IMMEDIATE sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_networkcode,gv_networkcodefor;

            END IF;
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE (   'sqlexception in SP_CHNL_TRANSFER_IN_DATA_PROC 2:' || gv_userid || SQLERRM);
               v_messageforlog := 'sqlexception in SP_CHNL_TRANSFER_IN_DATA_PROC 2:' || gv_userid || ' date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC 2, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'OTHERS Exception in SP_CHNL_TRANSFER_IN_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                             --end of distributor insertion block
      END LOOP;                                     --end of ORDER_CURSOR Loop
   --CLOSE ORDER_DATE; --closing the cursor
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in SP_CHNL_TRANSFER_IN_DATA_PROC 3'|| SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC 3'|| SQLERRM);
         RAISE retmainexception;
   END;                                                 --end of orders values

   PROCEDURE Sp_Update_Daily_Chnl_Trans_Det (aiv_date IN DATE)
   IS
      iv_user_id                  DAILY_CHNL_TRANS_DETAILS.user_id%TYPE;
      id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
      iv_receiver_category_code   DAILY_CHNL_TRANS_DETAILS.receiver_category_code%TYPE;
      iv_product_code             DAILY_CHNL_TRANS_DETAILS.product_code%TYPE;
      iv_type                     DAILY_CHNL_TRANS_DETAILS.TYPE%TYPE;
      in_total_tax1               DAILY_CHNL_TRANS_DETAILS.total_tax1_in%TYPE;
      in_total_tax2               DAILY_CHNL_TRANS_DETAILS.total_tax2_in%TYPE;
      in_total_tax3               DAILY_CHNL_TRANS_DETAILS.total_tax3_in%TYPE;
      iv_transfer_category        DAILY_CHNL_TRANS_DETAILS.transfer_category%TYPE;
      iv_transfer_type            DAILY_CHNL_TRANS_DETAILS.transfer_type%TYPE;
      iv_transfer_sub_type        DAILY_CHNL_TRANS_DETAILS.transfer_sub_type%TYPE;
      in_trans_count              DAILY_CHNL_TRANS_DETAILS.trans_in_count%TYPE;
      in_trans_amount             DAILY_CHNL_TRANS_DETAILS.trans_in_amount%TYPE;

      ---Cursor Declaration
      CURSOR daily_chnl_trans_det1_cursor (aiv_date DATE)
      IS
         SELECT   ct.from_user_id, TRUNC(ct.close_date) transfer_date, cti.product_code,
                  ct.receiver_category_code, ct.TYPE, ct.transfer_category,
                  ct.transfer_type, ct.transfer_sub_type,
                  SUM (NVL(cti.tax1_value,0)) tax1_value,
                  SUM (NVL(cti.tax2_value,0)) tax2_value,
                  SUM (NVL(cti.tax3_value,0)) tax3_value,
                  SUM (NVL(cti.approved_quantity,0)) amount, COUNT (ct.transfer_id) COUNT
             FROM CHANNEL_TRANSFERS ct, CHANNEL_TRANSFERS_ITEMS cti
            WHERE ct.transfer_id = cti.transfer_id
              AND TRUNC (ct.close_date) = aiv_date
              AND ct.status = 'CLOSE'
              AND ct.from_user_id <> 'OPT'
         GROUP BY ct.from_user_id,
                  TRUNC(ct.close_date),
                  cti.product_code,
                  ct.receiver_category_code,
                  ct.TYPE,
                  ct.transfer_category,
                  ct.transfer_type,
                  ct.transfer_sub_type;

      CURSOR daily_chnl_trans_det2_cursor (aiv_date DATE)
      IS
         SELECT   ct.to_user_id, TRUNC(ct.close_date) transfer_date,
                  cti.product_code, ct.sender_category_code, ct.TYPE,
                  ct.transfer_category, ct.transfer_type,
                  ct.transfer_sub_type, SUM (NVL(cti.tax1_value,0)) tax1_value,
                  SUM (NVL(cti.tax2_value,0)) tax2_value,
                  SUM (NVL(cti.tax3_value,0)) tax3_value,
                  SUM (NVL(cti.approved_quantity,0)) amount, COUNT (ct.transfer_id) COUNT
             FROM CHANNEL_TRANSFERS ct, CHANNEL_TRANSFERS_ITEMS cti
            WHERE ct.transfer_id = cti.transfer_id
              AND TRUNC (ct.close_date) = aiv_date
              AND ct.status = 'CLOSE'
              AND ct.to_user_id <> 'OPT'
         GROUP BY ct.to_user_id,
                  TRUNC(ct.close_date),
                  cti.product_code,
                  ct.sender_category_code,
                  ct.TYPE,
                  ct.transfer_category,
                  ct.transfer_type,
                  ct.transfer_sub_type;
   BEGIN
      FOR transaction_record IN daily_chnl_trans_det1_cursor (aiv_date)
      LOOP
         in_total_tax1 := 0;
         in_total_tax2 := 0;
         in_total_tax3 := 0;
         in_trans_count := 0;
         in_trans_amount := 0;
         iv_user_id := transaction_record.from_user_id;
         id_trans_date := transaction_record.transfer_date;
         iv_receiver_category_code :=
                                    transaction_record.receiver_category_code;
         iv_product_code := transaction_record.product_code;
         iv_type := transaction_record.TYPE;
         iv_transfer_category := transaction_record.transfer_category;
         iv_transfer_type := transaction_record.transfer_type;
         iv_transfer_sub_type := transaction_record.transfer_sub_type;
         in_total_tax1 := transaction_record.tax1_value;
         in_total_tax2 := transaction_record.tax2_value;
         in_total_tax3 := transaction_record.tax3_value;
         in_trans_count := transaction_record.COUNT;
         in_trans_amount := transaction_record.amount;

         BEGIN
            UPDATE DAILY_CHNL_TRANS_DETAILS
               SET total_tax1_out = total_tax1_out + in_total_tax1,
                   total_tax2_out = total_tax2_out + in_total_tax2,
                   total_tax3_out = total_tax3_out + in_total_tax3,
                   trans_out_count = trans_out_count + in_trans_count,
                   trans_out_amount = trans_out_amount + in_trans_amount
             WHERE user_id = iv_user_id
               AND trans_date = id_trans_date
               AND receiver_category_code = iv_receiver_category_code
               AND product_code = iv_product_code
               AND TYPE = iv_type
               AND transfer_category = iv_transfer_category
               AND transfer_type = iv_transfer_type
               AND transfer_sub_type = iv_transfer_sub_type;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO DAILY_CHNL_TRANS_DETAILS
                                (user_id, trans_date,
                            receiver_category_code, product_code,
                            TYPE, total_tax1_in, total_tax2_in,
                            total_tax3_in,total_tax1_out, total_tax2_out,
                            total_tax3_out, transfer_category,
                            transfer_type, transfer_sub_type, created_on,
                            trans_in_count, trans_in_amount, trans_out_count, trans_out_amount)
                    VALUES (iv_user_id, id_trans_date,
                            iv_receiver_category_code, iv_product_code,
                            iv_type, 0, 0, 0, in_total_tax1, in_total_tax2,
                            in_total_tax3, iv_transfer_category,
                            iv_transfer_type, iv_transfer_sub_type, gd_createdon,
                            0, 0, in_trans_count, in_trans_amount);
            END IF;
         EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET 1, User:' || iv_user_id || SQLERRM );
               v_messageforlog := 'OTHERS Exception in SP_UPDATE_DAILY_CHNL_TRANS_DET 1, User:'|| iv_user_id || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;
      END LOOP;

      FOR transaction_record IN daily_chnl_trans_det2_cursor (aiv_date)
      LOOP
         in_total_tax1 := 0;
         in_total_tax2 := 0;
         in_total_tax3 := 0;
         in_trans_count := 0;
         in_trans_amount := 0;
         iv_user_id := transaction_record.to_user_id;
         id_trans_date := transaction_record.transfer_date;
         iv_receiver_category_code :=
                                    transaction_record.sender_category_code;
         iv_product_code := transaction_record.product_code;
         iv_type := transaction_record.TYPE;
         iv_transfer_category := transaction_record.transfer_category;
         iv_transfer_type := transaction_record.transfer_type;
         iv_transfer_sub_type := transaction_record.transfer_sub_type;
         in_total_tax1 := transaction_record.tax1_value;
         in_total_tax2 := transaction_record.tax2_value;
         in_total_tax3 := transaction_record.tax3_value;
         in_trans_count := transaction_record.COUNT;
         in_trans_amount := transaction_record.amount;

         BEGIN
            UPDATE DAILY_CHNL_TRANS_DETAILS
               SET total_tax1_in = total_tax1_in + in_total_tax1,
                   total_tax2_in = total_tax2_in + in_total_tax2,
                   total_tax3_in = total_tax3_in + in_total_tax3,
                   trans_in_count = trans_in_count + in_trans_count,
                   trans_in_amount = trans_in_amount + in_trans_amount
             WHERE user_id = iv_user_id
               AND trans_date = id_trans_date
               AND receiver_category_code = iv_receiver_category_code
               AND product_code = iv_product_code
               AND TYPE = iv_type
               AND transfer_category = iv_transfer_category
               AND transfer_type = iv_transfer_type
               AND transfer_sub_type = iv_transfer_sub_type;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO DAILY_CHNL_TRANS_DETAILS
                                (user_id, trans_date,
                            receiver_category_code, product_code,
                            TYPE, total_tax1_in, total_tax2_in,
                            total_tax3_in,total_tax1_out, total_tax2_out,
                            total_tax3_out, transfer_category,
                            transfer_type, transfer_sub_type, created_on,
                            trans_in_count, trans_in_amount, trans_out_count, trans_out_amount)
                    VALUES (iv_user_id, id_trans_date,
                            iv_receiver_category_code, iv_product_code,
                            iv_type, in_total_tax1, in_total_tax2,
                            in_total_tax3, 0, 0, 0, iv_transfer_category,
                            iv_transfer_type, iv_transfer_sub_type, gd_createdon,
                            in_trans_count, in_trans_amount, 0, 0);
            END IF;
         EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE(   'OTHERS EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET 2, User:' || iv_user_id || SQLERRM );
               v_messageforlog := 'OTHERS Error in SP_UPDATE_DAILY_CHNL_TRANS_DET 2,User:' || iv_user_id|| ', Date :' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         --Raise procHException;
         END;
      END LOOP;
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in SP_UPDATE_DAILY_CHNL_TRANS_DET 3');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in SP_UPDATE_DAILY_CHNL_TRANS_DET 3, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
   END ;


  
  
  PROCEDURE Sp_Update_Monthly_Data1 (aiv_date DATE)
   IS
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
 
   --Cursor Declaration
   CURSOR monthly_chnl_main  (aiv_date DATE)
   IS
      SELECT   user_id, trans_date,
               product_code, category_code, network_code, network_code_for,
               sender_domain_code, grph_domain_code,
               roam_c2s_transfer_out_amount,
               c2s_transfer_out_count,c2s_transfer_out_amount,
               o2c_transfer_in_count,o2c_transfer_in_amount,
               o2c_return_out_count,o2c_return_out_amount,
               o2c_withdraw_out_count,o2c_withdraw_out_amount,
               c2c_transfer_in_count,c2c_transfer_in_amount,
               c2c_transfer_out_count,c2c_transfer_out_amount,
               c2c_return_in_count,c2c_return_in_amount,
               c2c_return_out_count,c2c_return_out_amount,
               c2c_withdraw_in_count,c2c_withdraw_in_amount,
               c2c_withdraw_out_count,c2c_withdraw_out_amount,
               differential,adjustment_in,adjustment_out,
               c2c_reverse_in_count,c2c_reverse_in_amount,
               c2c_reverse_out_count,c2c_reverse_out_amount,
               o2c_reverse_in_count,o2c_reverse_in_amount,
               o2c_reverse_out_count,o2c_reverse_out_amount,
               C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT ,REV_DIFFERENTIAL
          FROM DAILY_CHNL_TRANS_MAIN
          WHERE trans_date = aiv_date;

BEGIN
   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_chnl_main (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount = roam_c2s_transfer_out_amount + t_r.roam_c2s_transfer_out_amount,
                   c2s_transfer_out_count = c2s_transfer_out_count + t_r.c2s_transfer_out_count,
                   c2s_transfer_out_amount = c2s_transfer_out_amount + t_r.c2s_transfer_out_amount,
                   o2c_transfer_in_count = o2c_transfer_in_count + t_r.o2c_transfer_in_count,
                   o2c_transfer_in_amount = o2c_transfer_in_amount + t_r.o2c_transfer_in_amount,
                   o2c_return_out_count = o2c_return_out_count + t_r.o2c_return_out_count,
                   o2c_return_out_amount = o2c_return_out_amount + t_r.o2c_return_out_amount,
                   o2c_withdraw_out_count = o2c_withdraw_out_count + t_r.o2c_withdraw_out_count,
                   o2c_withdraw_out_amount = o2c_withdraw_out_amount + t_r.o2c_withdraw_out_amount,
                   c2c_transfer_in_count = c2c_transfer_in_count + t_r.c2c_transfer_in_count,
                   c2c_transfer_in_amount = c2c_transfer_in_amount + t_r.c2c_transfer_in_amount,
                   c2c_transfer_out_count = c2c_transfer_out_count + t_r.c2c_transfer_out_count,
                   c2c_transfer_out_amount = c2c_transfer_out_amount + t_r.c2c_transfer_out_amount,
                   c2c_return_in_count = c2c_return_in_count + t_r.c2c_return_in_count,
                   c2c_return_in_amount = c2c_return_in_amount + t_r.c2c_return_in_amount,
                   c2c_return_out_count = c2c_return_out_count + t_r.c2c_return_out_count,
                   c2c_return_out_amount = c2c_return_out_amount + t_r.c2c_return_out_amount,
                   c2c_withdraw_in_count = c2c_withdraw_in_count + t_r.c2c_withdraw_in_count,
                   c2c_withdraw_in_amount = c2c_withdraw_in_amount + t_r.c2c_withdraw_in_amount,
                   c2c_withdraw_out_count = c2c_withdraw_out_count + t_r.c2c_withdraw_out_count,
                   c2c_withdraw_out_amount = c2c_withdraw_out_amount + t_r.c2c_withdraw_out_amount,
                   differential = differential + t_r.differential,
                   adjustment_in = adjustment_in + t_r.adjustment_in,
                   adjustment_out = adjustment_out + t_r.adjustment_out,
                   c2c_reverse_in_count = c2c_reverse_in_count + t_r.c2c_reverse_in_count,
                   c2c_reverse_in_amount = c2c_reverse_in_amount + t_r.c2c_reverse_in_amount,
                   c2c_reverse_out_count = c2c_reverse_out_count + t_r.c2c_reverse_out_count,
                   c2c_reverse_out_amount = c2c_reverse_out_amount + t_r.c2c_reverse_out_amount,
                   o2c_reverse_in_count = o2c_reverse_in_count + t_r.o2c_reverse_in_count,
                   o2c_reverse_in_amount = o2c_reverse_in_amount + t_r.o2c_reverse_in_amount,
                   o2c_reverse_out_count = o2c_reverse_out_count + t_r.o2c_reverse_out_count,
                   o2c_reverse_out_amount = o2c_reverse_out_amount + t_r.o2c_reverse_out_amount,
                   C2S_TRANSFER_IN_AMOUNT  = C2S_TRANSFER_IN_AMOUNT + t_r.C2S_TRANSFER_IN_AMOUNT,
                   C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + t_r.C2S_TRANSFER_IN_COUNT,
                   REV_DIFFERENTIAL  = REV_DIFFERENTIAL +  t_r.REV_DIFFERENTIAL

             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND product_code = t_r.product_code
               AND category_code = t_r.category_code
               AND network_code = t_r.network_code
               AND network_code_for = t_r.network_code_for
               AND sender_domain_code = t_r.sender_domain_code
               AND grph_domain_code = t_r.grph_domain_code;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_MAIN
                                (user_id,trans_date,
                            product_code, category_code,
                            network_code, network_code_for,
                            sender_domain_code,
                            roam_c2s_transfer_out_amount,
                            c2s_transfer_out_count,
                            c2s_transfer_out_amount,
                            o2c_transfer_in_count,
                            o2c_transfer_in_amount,
                            o2c_return_out_count,
                            o2c_return_out_amount,
                            o2c_withdraw_out_count,
                            o2c_withdraw_out_amount,
                            c2c_transfer_in_count,
                            c2c_transfer_in_amount,
                            c2c_transfer_out_count,
                            c2c_transfer_out_amount,
                            c2c_return_in_count,
                            c2c_return_in_amount,
                            c2c_return_out_count,
                            c2c_return_out_amount,
                            c2c_withdraw_in_count,
                            c2c_withdraw_in_amount,
                            c2c_withdraw_out_count,
                            c2c_withdraw_out_amount, differential,
                            adjustment_in, adjustment_out, created_on,
                            grph_domain_code,
                            c2c_reverse_in_count,
                            c2c_reverse_in_amount,
                            c2c_reverse_out_count,
                            c2c_reverse_out_amount,
                            o2c_reverse_in_count,
                            o2c_reverse_in_amount,
                            o2c_reverse_out_count,
                            o2c_reverse_out_amount,
                            C2S_TRANSFER_IN_AMOUNT ,
                            C2S_TRANSFER_IN_COUNT ,
                            REV_DIFFERENTIAL
            )
                    VALUES (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date,'mm-yy'),'dd-mm-yy'),
                            t_r.product_code, t_r.category_code,
                            t_r.network_code, t_r.network_code_for,
                            t_r.sender_domain_code,
                            t_r.roam_c2s_transfer_out_amount,
                            t_r.c2s_transfer_out_count,
                            t_r.c2s_transfer_out_amount,
                            t_r.o2c_transfer_in_count,
                            t_r.o2c_transfer_in_amount,
                            t_r.o2c_return_out_count,
                            t_r.o2c_return_out_amount,
                            t_r.o2c_withdraw_out_count,
                            t_r.o2c_withdraw_out_amount,
                            t_r.c2c_transfer_in_count,
                            t_r.c2c_transfer_in_amount,
                            t_r.c2c_transfer_out_count,
                            t_r.c2c_transfer_out_amount,
                            t_r.c2c_return_in_count,
                            t_r.c2c_return_in_amount,
                            t_r.c2c_return_out_count,
                            t_r.c2c_return_out_amount,
                            t_r.c2c_withdraw_in_count,
                            t_r.c2c_withdraw_in_amount,
                            t_r.c2c_withdraw_out_count,
                            t_r.c2c_withdraw_out_amount, t_r.differential,
                            t_r.adjustment_in, t_r.adjustment_out, SYSDATE,
                            t_r.grph_domain_code,
                            t_r.c2c_reverse_in_count,
                            t_r.c2c_reverse_in_amount,
                            t_r.c2c_reverse_out_count,
                            t_r.c2c_reverse_out_amount,
                            t_r.o2c_reverse_in_count,
                            t_r.o2c_reverse_in_amount,
                            t_r.o2c_reverse_out_count,
                            t_r.o2c_reverse_out_amount,
                            t_r.C2S_TRANSFER_IN_AMOUNT ,
                            t_r.C2S_TRANSFER_IN_COUNT ,
                            t_r.REV_DIFFERENTIAL
                );
            END IF;
          EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Monthly_Data1 1, User:'  || t_r.user_id || SQLERRM);
               v_messageforlog := 'OTHERS EXCEPTION in Sp_Update_Monthly_Data1 1, User:' || t_r.user_id || ', Date:' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
      END LOOP;
   EXCEPTION
      WHEN procexception
     THEN
        DBMS_OUTPUT.PUT_LINE ('procexception EXCEPTION in Sp_Update_Monthly_Data1 2');
        RAISE mainexception;

      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
         RAISE mainexception;
   END;
 EXCEPTION
      WHEN mainexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('mainexception in Sp_Update_Monthly_Data1 7');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data1 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END;


PROCEDURE Sp_Update_Monthly_Data2 (aiv_date DATE)
   IS
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
   --Cursor Declaration

   CURSOR monthly_chnl_details (aiv_date DATE)
   IS
      SELECT   user_id, trans_date,
               receiver_category_code, product_code, TYPE, transfer_category,
               transfer_type, transfer_sub_type,
               total_tax1_in,total_tax2_in,total_tax3_in,
               total_tax1_out,total_tax2_out,total_tax3_out,
               trans_in_count,trans_in_amount,
               trans_out_count,trans_out_amount
          FROM DAILY_CHNL_TRANS_DETAILS
         WHERE trans_date=aiv_date;
BEGIN
   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_chnl_details (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_DETAILS
               SET total_tax1_in = total_tax1_in + t_r.total_tax1_in,
                   total_tax2_in = total_tax2_in + t_r.total_tax2_in,
                   total_tax3_in = total_tax3_in + t_r.total_tax3_in,
                   total_tax1_out = total_tax1_out + t_r.total_tax1_out,
                   total_tax2_out = total_tax2_out + t_r.total_tax2_out,
                   total_tax3_out = total_tax3_out + t_r.total_tax3_out,
                   trans_in_count = trans_in_count + t_r.trans_in_count,
                   trans_in_amount = trans_in_amount + t_r.trans_in_amount,
                   trans_out_count = trans_out_count + t_r.trans_out_count,
                   trans_out_amount = trans_out_amount + t_r.trans_out_amount
             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND receiver_category_code = t_r.receiver_category_code
               AND product_code = t_r.product_code
               AND TYPE = t_r.TYPE
               AND transfer_category = t_r.transfer_category
               AND transfer_type = t_r.transfer_type
               AND transfer_sub_type = t_r.transfer_sub_type;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_DETAILS
                                (user_id,
                            trans_date,
                            receiver_category_code, product_code,
                            TYPE, total_tax1_in, total_tax2_in,
                            total_tax3_in, total_tax1_out,
                            total_tax2_out, total_tax3_out,
                            transfer_category, transfer_type,
                            transfer_sub_type, created_on,
                            trans_in_count, trans_in_amount,
                            trans_out_count, trans_out_amount)
                    VALUES (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.receiver_category_code, t_r.product_code,
                            t_r.TYPE, t_r.total_tax1_in, t_r.total_tax2_in,
                            t_r.total_tax3_in, t_r.total_tax1_out,
                            t_r.total_tax2_out, t_r.total_tax3_out,
                            t_r.transfer_category, t_r.transfer_type,
                            t_r.transfer_sub_type, SYSDATE,
                            t_r.trans_in_count, t_r.trans_in_amount,
                            t_r.trans_out_count, t_r.trans_out_amount);
            END IF;
         EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Monthly_Data2 3,User:' || t_r.user_id || SQLERRM);
               v_messageforlog := 'OTHERS Error in Sp_Update_Monthly_Data2 3,User:'|| t_r.user_id || ', Date:' ||id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
      END LOOP;
   EXCEPTION
      WHEN procexception
     THEN
        DBMS_OUTPUT.PUT_LINE ('procexception in Sp_Update_Monthly_Data2 4');
        RAISE mainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
         RAISE mainexception;
   END;

 EXCEPTION
      WHEN mainexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('mainexception in Sp_Update_Monthly_Data2 7');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data2 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END;

PROCEDURE Sp_Update_Monthly_Data3 (aiv_date DATE)
   IS
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
   --Cursor Declaration

   -----this procedure has been modified on 12/09


   CURSOR monthly_c2s_details (aiv_date DATE)
   IS
      SELECT   user_id, trans_date,
               receiver_network_code, SERVICE_TYPE, Sub_Service,
               sender_category_code,receiver_service_class_id,
               total_tax1,total_tax2,total_tax3,
               sender_transfer_amount,receiver_credit_amount,
               receiver_access_fee,
               differential_adjustment_tax1,
               differential_adjustment_tax2,
               differential_adjustment_tax3,
               receiver_bonus,
               transaction_amount,
               transaction_count,
               differential_amount,
               receiver_validity,
               receiver_bonus_validity,
               SENDER_REVERSE_AMOUNT ,
               RECEIVER_DEBIT_AMOUNT ,
               REVERSE_AMOUNT ,
               REVERSE_COUNT ,
               REVERSE_DIFF_AMOUNT,
               REVERSE_DIFF_COUNT, 
               PENALTY,
               OWNER_PENALTY,
               roam_amount,
               penalty_count
          FROM DAILY_C2S_TRANS_DETAILS
         WHERE trans_date=aiv_date;
BEGIN
   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_c2s_details (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_C2S_TRANS_DETAILS
               SET total_tax1 = total_tax1 + t_r.total_tax1,
                   total_tax2 = total_tax2 + t_r.total_tax2,
                   total_tax3 = total_tax3 + t_r.total_tax3,
                   sender_transfer_amount = sender_transfer_amount + t_r.sender_transfer_amount,
                   receiver_credit_amount = receiver_credit_amount + t_r.receiver_credit_amount,
                   receiver_access_fee = receiver_access_fee + t_r.receiver_access_fee,
                   differential_adjustment_tax1 = differential_adjustment_tax1 + t_r.differential_adjustment_tax1,
                   differential_adjustment_tax2 = differential_adjustment_tax2 + t_r.differential_adjustment_tax2,
                   differential_adjustment_tax3 = differential_adjustment_tax3 + t_r.differential_adjustment_tax3,
                   receiver_bonus = receiver_bonus + t_r.receiver_bonus,
                   receiver_validity = receiver_validity + t_r.receiver_validity,
                   receiver_bonus_validity = receiver_bonus_validity + t_r.receiver_bonus_validity,
                   transaction_count=transaction_count+t_r.transaction_count,
                   transaction_amount=transaction_amount+t_r.transaction_amount,
                   differential_amount=differential_amount+t_r.differential_amount,
                    SENDER_REVERSE_AMOUNT = SENDER_REVERSE_AMOUNT + t_r.SENDER_REVERSE_AMOUNT,
                    RECEIVER_DEBIT_AMOUNT = RECEIVER_DEBIT_AMOUNT + t_r.RECEIVER_DEBIT_AMOUNT,
                    REVERSE_AMOUNT = REVERSE_AMOUNT + t_r.REVERSE_AMOUNT,
                    REVERSE_COUNT = REVERSE_COUNT + t_r.REVERSE_COUNT,
                    REVERSE_DIFF_AMOUNT = REVERSE_DIFF_AMOUNT + t_r.REVERSE_DIFF_AMOUNT,
                    REVERSE_DIFF_COUNT = REVERSE_DIFF_COUNT + t_r.REVERSE_DIFF_COUNT,
                    PENALTY=PENALTY+t_r.PENALTY,
                    OWNER_PENALTY=OWNER_PENALTY+t_r.OWNER_PENALTY,
                    roam_amount=roam_amount+t_r.roam_amount,
                    PENALTY_COUNT=PENALTY_COUNT+t_r.PENALTY_COUNT
             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND receiver_network_code = t_r.receiver_network_code
               AND sender_category_code = t_r.sender_category_code
               AND receiver_service_class_id = t_r.receiver_service_class_id
               AND SERVICE_TYPE = t_r.SERVICE_TYPE
           AND SUB_SERVICE = t_r.SUB_SERVICE;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_C2S_TRANS_DETAILS
                              (user_id,
                            trans_date,
                            sender_category_code,
                            receiver_service_class_id,
                            receiver_network_code, SERVICE_TYPE,sub_service,
                            total_tax1, total_tax2, total_tax3,
                            sender_transfer_amount,
                            receiver_credit_amount,
                            receiver_access_fee,
                            differential_adjustment_tax1,
                            differential_adjustment_tax2,
                            differential_adjustment_tax3,
                            receiver_bonus, created_on,
                            receiver_validity,
                            receiver_bonus_validity,
                            transaction_amount,
                            transaction_count,
                            differential_amount,
                            SENDER_REVERSE_AMOUNT ,
                            RECEIVER_DEBIT_AMOUNT ,
                            REVERSE_AMOUNT ,
                            REVERSE_COUNT ,
                            REVERSE_DIFF_AMOUNT,
                            REVERSE_DIFF_COUNT,
                            PENALTY,
                            OWNER_PENALTY,
                            roam_amount,
                            PENALTY_COUNT
                            )
                    VALUES (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.sender_category_code,
                            t_r.receiver_service_class_id,
                            t_r.receiver_network_code, t_r.SERVICE_TYPE,t_r.sub_service,
                            t_r.total_tax1, t_r.total_tax2, t_r.total_tax3,
                            t_r.sender_transfer_amount,
                            t_r.receiver_credit_amount,
                            t_r.receiver_access_fee,
                            t_r.differential_adjustment_tax1,
                            t_r.differential_adjustment_tax2,
                            t_r.differential_adjustment_tax3,
                            t_r.receiver_bonus, SYSDATE,
                            t_r.receiver_validity,t_r.receiver_bonus_validity,
                            t_r.transaction_amount,t_r.transaction_count,
                            t_r.differential_amount,
                            t_r.SENDER_REVERSE_AMOUNT ,
                            t_r.RECEIVER_DEBIT_AMOUNT ,
                            t_r.REVERSE_AMOUNT ,
                            t_r.REVERSE_COUNT ,
                            t_r.REVERSE_DIFF_AMOUNT,
                            t_r.REVERSE_DIFF_COUNT,
                            t_r.PENALTY,
                            t_r.OWNER_PENALTY,
                            t_r.roam_amount,
                            t_r.PENALTY_COUNT
                            );
            END IF;

            EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Monthly_Data3 5, User:'|| t_r.user_id || SQLERRM);
               v_messageforlog := 'OTHERS Error in Sp_Update_Monthly_Data3 5, User:'|| t_r.user_id;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
        END LOOP;
   EXCEPTION
       WHEN procexception
     THEN
        DBMS_OUTPUT.PUT_LINE ('procexception in Sp_Update_Monthly_Data3 6');
        RAISE mainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
     RAISE mainexception;
   END;
 EXCEPTION
      WHEN mainexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('mainexception in Sp_Update_Monthly_Data3 7');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data3 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END;


PROCEDURE sp_update_c2s_sub_denom (aiv_date IN DATE)
IS
   iv_service_class_code   C2S_SUB_DENOM_DETAILS.service_class_code%TYPE;
   iv_service_class_id     C2S_SUB_DENOM_DETAILS.service_class_id%TYPE;
   iv_interface_id         C2S_SUB_DENOM_DETAILS.interface_id%TYPE;
   iv_denomination_slab    C2S_SUB_DENOM_DETAILS.denomination_slab%TYPE;
   iv_service_type           C2S_SUB_DENOM_DETAILS.SERVICE_TYPE%TYPE;
   iv_sub_service           C2S_SUB_DENOM_DETAILS.SUB_SERVICE%TYPE;
   in_transfer_amount      C2S_SUB_DENOM_DETAILS.transfer_amount%TYPE;
   in_requested_amount     C2S_SUB_DENOM_DETAILS.requested_amount%TYPE;
   in_transfer_count       C2S_SUB_DENOM_DETAILS.transfer_count%TYPE;
   in_access_fee           C2S_SUB_DENOM_DETAILS.access_fee%TYPE;
   id_transfer_date        C2S_SUB_DENOM_DETAILS.transfer_date%TYPE;
   iv_network_code         C2S_SUB_DENOM_DETAILS.network_code%TYPE;


   --Cursor Declaration
   CURSOR c2s_sub_denom_details_cursor (aiv_date DATE)
   IS

   SELECT   ct.service_class_id,ct.service_class_code,
               ct.interface_id, sm.slab_id,ct.SERVICE_TYPE, ct.sub_service,
               ct.transfer_date, ct.receiver_network_code,
               SUM(ct.receiver_access_fee) receiver_access_fee,
               NVL (SUM (ct.transfer_value), 0) amount,
                  NVL(SUM(ct.transfer_value), 0) requested_amount,
               COUNT (ct.transfer_date) COUNT
          FROM C2S_TRANSFERS_MISTMP ct, SLAB_MASTER sm
         WHERE --ct.transfer_date = aiv_date AND 
            ct.transfer_status='200'
           AND    ct.transfer_value >= sm.from_range
         AND    ct.transfer_value <= sm.to_range
           AND sm.slab_date=(SELECT MAX(slab_date) FROM SLAB_MASTER WHERE
                                          slab_date<=SYSDATE AND
                                    ct.SERVICE_TYPE=sm.SERVICE_TYPE)
      GROUP BY ct.service_class_code,
               ct.service_class_id,
               ct.interface_id,
               sm.slab_id,
               ct.transfer_date,
               ct.receiver_network_code,
               ct.SERVICE_TYPE,ct.sub_service;


BEGIN
   FOR transaction_record IN c2s_sub_denom_details_cursor (aiv_date)
   LOOP
      iv_service_class_code := transaction_record.service_class_code;
      iv_service_class_id := transaction_record.service_class_id;
      iv_interface_id := transaction_record.interface_id;
      iv_denomination_slab := transaction_record.slab_id;
      iv_service_type := transaction_record.SERVICE_TYPE;
      iv_sub_service := transaction_record.SUB_SERVICE;
      in_transfer_amount := transaction_record.amount;
      in_requested_amount := transaction_record.requested_amount;
      in_transfer_count := transaction_record.COUNT;
      in_access_fee := transaction_record.receiver_access_fee;
      --id_transfer_date := transaction_record.transfer_date;
      id_transfer_date := aiv_date;
      iv_network_code := transaction_record.receiver_network_code;

      BEGIN
         UPDATE C2S_SUB_DENOM_DETAILS
            SET transfer_amount = transfer_amount + in_transfer_amount,
                transfer_count = transfer_count + in_transfer_count,
                access_fee = access_fee + in_access_fee,
                requested_amount=requested_amount+in_requested_amount
          WHERE service_class_code = iv_service_class_code
            AND service_class_id = iv_service_class_id
            AND denomination_slab = iv_denomination_slab
            AND interface_id = iv_interface_id
            AND transfer_date = id_transfer_date
            AND network_code = iv_network_code
            AND SERVICE_TYPE = iv_service_type
        AND SUB_SERVICE = iv_sub_service ;

         IF SQL%NOTFOUND
         THEN
            INSERT INTO C2S_SUB_DENOM_DETAILS
                           (service_class_code, service_class_id,
                         interface_id, denomination_slab,
                         transfer_amount, transfer_count,
                         access_fee, transfer_date, network_code,
                         SERVICE_TYPE,SUB_SERVICE,requested_amount)
                 VALUES (iv_service_class_code, iv_service_class_id,
                         iv_interface_id, iv_denomination_slab,
                         in_transfer_amount, in_transfer_count,
                         in_access_fee, id_transfer_date, iv_network_code,
                         iv_service_type,iv_sub_service, in_requested_amount);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in sp_update_c2s_sub_denom 1:' || SQLERRM );
            v_messageforlog := 'OTHERS Error in sp_update_c2s_sub_denom 1, Date:'|| aiv_date;
            v_sqlerrmsgforlog := SQLERRM;
            RAISE procexception;
      END;
   END LOOP;
EXCEPTION
   WHEN procexception
   THEN
      DBMS_OUTPUT.PUT_LINE ('procexception in sp_update_c2s_sub_denom 2');
      RAISE retmainexception;
   WHEN OTHERS
   THEN
      DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
      v_messageforlog := 'OTHERS Exception in sp_update_c2s_sub_denom 2, Date:' || aiv_date;
      v_sqlerrmsgforlog := SQLERRM;
END;


PROCEDURE sp_update_daily_trn_summary (
   aiv_date            IN       DATE,
   v_choice            IN       VARCHAR2
)
IS
   id_trans_date                    C2S_DAILY_TRANSACTIONS.trans_date%TYPE;
   iv_network_code                  C2S_DAILY_TRANSACTIONS.network_code%TYPE;
   iv_receiver_network_code         C2S_DAILY_TRANSACTIONS.receiver_network_code%TYPE;
   iv_service_type                  C2S_DAILY_TRANSACTIONS.SERVICE_TYPE%TYPE;
   iv_sub_service_type              C2S_DAILY_TRANSACTIONS.sub_service_type%TYPE;
   iv_sender_category               C2S_DAILY_TRANSACTIONS.sender_category%TYPE;
   iv_receiver_service_class_id     C2S_DAILY_TRANSACTIONS.receiver_service_class_id%TYPE;
   iv_receiver_service_class_code   C2S_DAILY_TRANSACTIONS.receiver_service_class_code%TYPE;
   iv_transfer_status               C2S_TRANSFERS_MISTMP.transfer_status%TYPE;
   in_receiver_access_fee           C2S_DAILY_TRANSACTIONS.receiver_access_fee%TYPE;
   in_receiver_tax1_value           C2S_DAILY_TRANSACTIONS.receiver_tax1_value%TYPE;
   in_receiver_tax2_value           C2S_DAILY_TRANSACTIONS.receiver_tax2_value%TYPE;
   in_receiver_tax3_value           C2S_DAILY_TRANSACTIONS.receiver_tax3_value%TYPE;
   in_success_count                 C2S_DAILY_TRANSACTIONS.success_count%TYPE;
   in_success_amount                C2S_DAILY_TRANSACTIONS.success_amount%TYPE;
   in_failure_count                 C2S_DAILY_TRANSACTIONS.failure_count%TYPE;
   in_failure_amount                C2S_DAILY_TRANSACTIONS.failure_amount%TYPE;
   in_bonus_amount                    C2S_DAILY_TRANSACTIONS.bonus_amount%TYPE;
   in_validity                           C2S_DAILY_TRANSACTIONS.validity%TYPE;
   in_bonus_validity                C2S_DAILY_TRANSACTIONS.bonus_validity%TYPE;
   in_penalty                         C2S_DAILY_TRANSACTIONS.penalty%TYPE;
   in_owner_penalty                 C2S_DAILY_TRANSACTIONS.owner_penalty%TYPE;
   in_penalty_count                 C2S_DAILY_TRANSACTIONS.penalty_count%TYPE;

   ---Cursor Declaration
   CURSOR daily_transaction_cursor (aiv_date DATE)
   IS
      SELECT   ct.transfer_date, ct.network_code, ct.receiver_network_code,
               ct.SERVICE_TYPE,  ct.sender_category,
               NVL(ct.sub_service,v_nullvalue) sub_service,
               NVL(ct.service_class_id,v_nullvalue) service_class_id,
               NVL(ct.service_class_code,v_nullvalue) service_class_code,
               ct.transfer_status,
               SUM (ct.receiver_access_fee) receiver_access_fee,
               SUM (ct.receiver_tax1_value) tax1,
               SUM (ct.receiver_tax2_value) tax2,
               SUM (ct.transfer_value) transfer_value,
               COUNT (ct.transfer_date) COUNT,
               SUM (ct.receiver_bonus_value    ) bonus_amount,
               SUM (ct.receiver_validity) validity,
               SUM (ct.receiver_bonus_validity) bonus_validity,
               SUM(ct.penalty) penalty,
               SUM(ct.owner_penalty) owner_penalty,
               NVL(SUM(case when NVL(PENALTY,0)>0 then 1 else 0 end),0) PENALTY_COUNT
          FROM C2S_TRANSFERS_MISTMP ct 
         --WHERE ct.Transfer_date=aiv_date
      GROUP BY ct.transfer_date,
               ct.network_code,
               ct.receiver_network_code,
               ct.SERVICE_TYPE,
               ct.sub_service,
               ct.sender_category,
               ct.service_class_id,
               ct.service_class_code,
               ct.transfer_status;

BEGIN
   FOR transaction_record IN daily_transaction_cursor  (aiv_date)
   LOOP
      in_success_count := 0;
      in_failure_count := 0;
      in_success_amount := 0;
      in_failure_amount := 0;
      in_receiver_access_fee := 0;
      in_receiver_tax1_value := 0;
      in_receiver_tax2_value := 0;
      in_receiver_tax3_value := 0;
      in_bonus_amount        :=0;
      in_validity            :=0;
         in_bonus_validity      :=0;
      in_penalty:=0;
      in_owner_penalty:=0;
      in_penalty_count:=0;


      in_receiver_access_fee := transaction_record.receiver_access_fee;
      in_receiver_tax1_value := transaction_record.tax1;
      in_receiver_tax2_value := transaction_record.tax2;
      id_trans_date := transaction_record.transfer_date;
      --id_trans_date := aiv_date;
      iv_transfer_status := transaction_record.transfer_status;
      iv_network_code := transaction_record.network_code;
      iv_receiver_network_code := transaction_record.receiver_network_code;
      iv_service_type := transaction_record.SERVICE_TYPE;
      iv_sub_service_type := transaction_record.sub_service;
      iv_sender_category := transaction_record.sender_category;
      iv_receiver_service_class_id := transaction_record.service_class_id;
      iv_receiver_service_class_code := transaction_record.service_class_code;

      IF iv_transfer_status = '200'
      THEN
         in_success_count := transaction_record.COUNT;
         in_success_amount := transaction_record.transfer_value;
         in_bonus_amount        :=transaction_record.bonus_amount;
         in_validity            :=transaction_record.validity;
              in_bonus_validity      :=transaction_record.bonus_validity;
        in_penalty:=transaction_record.penalty;
        in_owner_penalty:=transaction_record.owner_penalty;
        in_penalty_count:=transaction_record.penalty_count;
      END IF;

      IF iv_transfer_status = '206'
      THEN
         in_failure_count := transaction_record.COUNT;
         in_failure_amount := transaction_record.transfer_value;
      END IF;

      IF v_choice = 'Y'
      THEN
         iv_receiver_service_class_id := 'ALL';
      END IF;
      BEGIN
         UPDATE C2S_DAILY_TRANSACTIONS
            SET success_count = success_count + in_success_count,
                failure_count = failure_count + in_failure_count,
                success_amount = success_amount + in_success_amount,
                failure_amount = failure_amount + in_failure_amount,
                receiver_access_fee = receiver_access_fee + in_receiver_access_fee,
                receiver_tax1_value = receiver_tax1_value + in_receiver_tax1_value,
                receiver_tax2_value = receiver_tax2_value + in_receiver_tax2_value,
                receiver_tax3_value = receiver_tax3_value + in_receiver_tax3_value,
                bonus_amount = bonus_amount+         in_bonus_amount ,
                validity = validity + in_validity ,
                     bonus_validity = bonus_validity + in_bonus_validity,
                penalty=penalty+in_penalty,
                owner_penalty= owner_penalty+in_owner_penalty,
                penalty_count=penalty_count+in_penalty_count
          WHERE trans_date = id_trans_date
            AND network_code = iv_network_code
            AND receiver_network_code = iv_receiver_network_code
            AND SERVICE_TYPE = iv_service_type
            AND sub_service_type = iv_sub_service_type
            AND sender_category = iv_sender_category
            AND receiver_service_class_id = iv_receiver_service_class_id
            AND receiver_service_class_code = iv_receiver_service_class_code;

         IF SQL%NOTFOUND
         THEN
            INSERT INTO C2S_DAILY_TRANSACTIONS
            (trans_date, network_code,
                         receiver_network_code, SERVICE_TYPE,
                         sub_service_type, sender_category,
                         receiver_service_class_id,
                         receiver_service_class_code,
                         receiver_access_fee, receiver_tax1_value,
                         receiver_tax2_value, receiver_tax3_value,
                         success_count, success_amount,
                         failure_count, failure_amount, created_on,
                         bonus_amount,validity,bonus_validity,penalty,owner_penalty,penalty_count)
            VALUES (id_trans_date, iv_network_code,
                         iv_receiver_network_code, iv_service_type,
                         iv_sub_service_type, iv_sender_category,
                         iv_receiver_service_class_id,
                         iv_receiver_service_class_code,
                         in_receiver_access_fee, in_receiver_tax1_value,
                         in_receiver_tax2_value, in_receiver_tax3_value,
                         in_success_count, in_success_amount,
                         in_failure_count, in_failure_amount, SYSDATE,
                          in_bonus_amount,in_validity,in_bonus_validity,in_penalty,in_owner_penalty,in_penalty_count);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in sp_update_daily_trn_summary 1, '|| SQLERRM);
            v_messageforlog :='OTHERS Error in sp_update_daily_trn_summary 1, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE procException;
      END;
   END LOOP;
EXCEPTION
      WHEN procException
      THEN
         DBMS_OUTPUT.PUT_LINE('procException in sp_update_daily_trn_summary 2');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog :='OTHERS Exception in sp_update_daily_trn_summary 2, Date:'|| id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END sp_update_daily_trn_summary;


PROCEDURE sp_update_c2s_success_failure (aiv_date IN DATE)
IS
  id_trans_date                       C2S_DAILY_FAILURE_DETAILS.trans_date%TYPE;
  iv_service_type                C2S_DAILY_FAILURE_DETAILS.SERVICE_TYPE%TYPE;
  iv_sub_service_type            C2S_DAILY_FAILURE_DETAILS.sub_service_type%TYPE;
  iv_error_code                    C2S_DAILY_FAILURE_DETAILS.error_code%TYPE;
  in_count                        C2S_DAILY_FAILURE_DETAILS.COUNT%TYPE;
  in_amount                        C2S_DAILY_FAILURE_DETAILS.amount%TYPE;
  iv_sender_network_code        C2S_DAILY_FAILURE_DETAILS.sender_network_code%TYPE;
  iv_receiver_network_code       C2S_DAILY_FAILURE_DETAILS.receiver_network_code%TYPE;

  in_trans_count                C2S_SUMMARY_DAILY.total_trans_count%TYPE;
  in_trans_amount               C2S_SUMMARY_DAILY.total_trans_amount%TYPE;
  in_fail_count                 C2S_SUMMARY_DAILY.fail_count%TYPE;
  in_fail_amount                C2S_SUMMARY_DAILY.fail_amount%TYPE;
  iv_transfer_status            C2S_TRANSFERS_MISTMP.transfer_status%TYPE;
  in_ambg_count                 C2S_SUMMARY_DAILY.fail_count%TYPE;
  in_ambg_amount                C2S_SUMMARY_DAILY.fail_amount%TYPE;


   --Cursor Declaration
   CURSOR SUCCESS_FAILURE_CURSOR (aiv_date IN DATE)
   IS
        SELECT transfer_date,network_code,receiver_network_code,
     SERVICE_TYPE,sub_service,transfer_status,NVL(error_code,v_nullvalue) error_code,
     SUM(transfer_value) amount,
     COUNT(transfer_date) COUNT
     FROM C2S_TRANSFERS_MISTMP
     --WHERE transfer_date=aiv_date
     GROUP BY transfer_date,network_code,receiver_network_code,
     SERVICE_TYPE,sub_service,transfer_status,error_code;
BEGIN
   FOR TRANSACTION_RECORD IN SUCCESS_FAILURE_CURSOR (aiv_date)
   LOOP

  id_trans_date            :=TRANSACTION_RECORD.transfer_date;
  --id_trans_date            :=aiv_date;
  iv_service_type          :=TRANSACTION_RECORD.SERVICE_TYPE;
  iv_sub_service_type      :=TRANSACTION_RECORD.sub_service;
  iv_error_code            :=TRANSACTION_RECORD.error_code;
  iv_transfer_status       :=TRANSACTION_RECORD.transfer_status;
  iv_sender_network_code   :=TRANSACTION_RECORD.network_code;
  iv_receiver_network_code :=TRANSACTION_RECORD.receiver_network_code;
  in_count:=0;
  in_amount:=0;
  in_trans_count:=0;
  in_trans_amount:=0;
  in_fail_count:=0;
  in_fail_amount:=0;
  in_ambg_count:=0;
  in_ambg_amount:=0;

      IF iv_transfer_status = '200' THEN
           in_trans_count := TRANSACTION_RECORD.COUNT;
           in_trans_amount := TRANSACTION_RECORD.amount;
      ELSIF iv_transfer_status = '206' THEN
         in_fail_count := TRANSACTION_RECORD.COUNT;
         in_fail_amount := TRANSACTION_RECORD.amount;
      END IF;

      BEGIN
             UPDATE C2S_SUMMARY_DAILY SET
           TOTAL_TRANS_COUNT = TOTAL_TRANS_COUNT + in_trans_count,
           TOTAL_TRANS_AMOUNT = TOTAL_TRANS_AMOUNT + in_trans_amount,
           FAIL_COUNT = FAIL_COUNT + in_fail_count,
           FAIL_AMOUNT = FAIL_AMOUNT + in_fail_amount
           WHERE TRANS_DATE=id_trans_date
           AND   SENDER_NETWORK_CODE=iv_sender_network_code
           AND     RECEIVER_NETWORK_CODE=iv_receiver_network_code
           AND   SERVICE_TYPE=iv_service_type
           AND   SUB_SERVICE_TYPE=iv_sub_service_type;


         IF SQL%NOTFOUND THEN
            INSERT INTO C2S_SUMMARY_DAILY
            (trans_date, sender_network_code,
                 receiver_network_code,SERVICE_TYPE,
                 sub_service_type, total_trans_count,
                 total_trans_amount,fail_count,fail_amount)
            VALUES (id_trans_date, iv_sender_network_code,
                 iv_receiver_network_code,iv_service_type,
                 iv_sub_service_type, in_trans_count,
                 in_trans_amount,in_fail_count,in_fail_amount);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE 1:'|| SQLERRM);
            v_messageforlog :='OTHERS Error in SP_UPDATE_C2S_SUCCESS_FAILURE 1, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE procException;
      END;

      IF iv_transfer_status='206' THEN
           in_count:=TRANSACTION_RECORD.COUNT;
         in_amount:=TRANSACTION_RECORD.amount;
      BEGIN
       UPDATE C2S_DAILY_FAILURE_DETAILS SET
                COUNT=COUNT+in_count,
             amount=amount+in_amount
             WHERE trans_date=id_trans_date
             AND SERVICE_TYPE=iv_service_type
             AND sub_service_type=iv_sub_service_type
             AND error_code=iv_error_code
             AND sender_network_code=iv_sender_network_code
             AND receiver_network_code=iv_receiver_network_code;


         IF SQL%NOTFOUND
         THEN
            INSERT INTO C2S_DAILY_FAILURE_DETAILS
            (trans_date,SERVICE_TYPE,sub_service_type,
                 error_code,COUNT,amount,sender_network_code,
                 receiver_network_code)
            VALUES (id_trans_date,iv_service_type,iv_sub_service_type,
                 iv_error_code,in_count,in_amount,iv_sender_network_code,
                 iv_receiver_network_code);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE 2:'|| SQLERRM);
            v_messageforlog :='OTHERS Error in SP_UPDATE_C2S_SUCCESS_FAILURE 2, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
            RAISE procException;
      END;
      END IF;
   END LOOP;
EXCEPTION
      WHEN procException
      THEN
         DBMS_OUTPUT.PUT_LINE('procException in SP_UPDATE_C2S_SUCCESS_FAILURE 3');
         RAISE mainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog :='OTHERS Exception in SP_UPDATE_C2S_SUCCESS_FAILURE 3, Date:'|| id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE mainexception;
END sp_update_c2s_success_failure;

PROCEDURE sp_update_c2s_msisdn_usage (
   aiv_date            IN       DATE)
IS
  iv_msisdn                           MSISDN_USAGE_SUMMARY.msisdn%TYPE;
  iv_prefix_id                    MSISDN_USAGE_SUMMARY.prefix_id%TYPE;
  iv_network_code                MSISDN_USAGE_SUMMARY.network_code%TYPE;
  in_success_count                MSISDN_USAGE_SUMMARY.success_count%TYPE;
  in_success_amount                MSISDN_USAGE_SUMMARY.success_amount%TYPE;
  in_fail_count                    MSISDN_USAGE_SUMMARY.fail_count%TYPE;
  in_fail_amount                MSISDN_USAGE_SUMMARY.fail_amount%TYPE;
  iv_service_type                MSISDN_USAGE_SUMMARY.SERVICE_TYPE%TYPE;
    iv_sub_service                MSISDN_USAGE_SUMMARY.SUB_SERVICE%TYPE;
  in_receiver_access_fee        MSISDN_USAGE_SUMMARY.receiver_access_fee%TYPE;
  in_receiver_credit_amount        MSISDN_USAGE_SUMMARY.receiver_credit_amount%TYPE;
  in_receiver_bonus                MSISDN_USAGE_SUMMARY.receiver_bonus%TYPE;
  in_tax1                        MSISDN_USAGE_SUMMARY.tax1%TYPE;
  in_tax2                        MSISDN_USAGE_SUMMARY.tax2%TYPE;
  id_trans_date                      C2S_TRANSFERS_MISTMP.transfer_date%TYPE;
  iv_transfer_status            C2S_TRANSFERS_MISTMP.transfer_status%TYPE;

   ---Cursor Declaration
   CURSOR msisdn_usage_cursor (aiv_date DATE)
   IS
   SELECT ct.receiver_msisdn,ct.prefix_id,ct.transfer_date,
   ct.network_code,ct.SERVICE_TYPE,ct.sub_service,ct.transfer_status,
   SUM(ct.transfer_value) transfer_value,
   SUM(ct.receiver_access_fee) receiver_access_fee,
   SUM(ct.receiver_bonus_value) receiver_bonus_value,
   SUM(ct.receiver_transfer_value) receiver_transfer_value,
   SUM(ct.receiver_tax1_value) receiver_tax1_value,
   SUM(ct.receiver_tax2_value) receiver_tax2_value,
   COUNT(ct.transfer_date) COUNT
   FROM C2S_TRANSFERS_MISTMP ct
   --WHERE ct.transfer_date=aiv_date
   GROUP BY ct.transfer_date,ct.receiver_msisdn,ct.prefix_id,
   ct.network_code,ct.SERVICE_TYPE,ct.sub_service,ct.transfer_status;


BEGIN
   FOR transaction_record IN msisdn_usage_cursor  (aiv_date)
   LOOP
  in_success_count := 0;
  in_success_amount := 0;
  in_fail_count := 0;
  in_fail_amount := 0;
  in_receiver_access_fee := transaction_record.receiver_access_fee;
  in_receiver_credit_amount := transaction_record.receiver_transfer_value;
  in_receiver_bonus := transaction_record.receiver_bonus_value;
  in_tax1 := transaction_record.receiver_tax1_value;
  in_tax2 := transaction_record.receiver_tax2_value;

  iv_msisdn :=transaction_record.receiver_msisdn;
  iv_prefix_id :=transaction_record.prefix_id;
  iv_network_code :=transaction_record.network_code;
  iv_service_type :=transaction_record.SERVICE_TYPE;
  iv_sub_service :=transaction_record.SUB_SERVICE;
  id_trans_date :=transaction_record.transfer_date;
  --id_trans_date :=aiv_date;
  iv_transfer_status := transaction_record.transfer_status;

      IF iv_transfer_status = '200'
      THEN
         in_success_count := transaction_record.COUNT;
         in_success_amount := transaction_record.transfer_value;
      END IF;

      IF iv_transfer_status = '206'
      THEN
         in_fail_count := transaction_record.COUNT;
         in_fail_amount := transaction_record.transfer_value;
      END IF;


      BEGIN
         UPDATE MSISDN_USAGE_SUMMARY
            SET success_count = success_count + in_success_count,
                fail_count = fail_count + in_fail_count,
                success_amount = success_amount + in_success_amount,
                fail_amount = fail_amount + in_fail_amount,
                receiver_access_fee = receiver_access_fee + in_receiver_access_fee,
                tax1 = tax1 + in_tax1,
                tax2 = tax2 + in_tax2,
                receiver_credit_amount = receiver_credit_amount + in_receiver_credit_amount,
                receiver_bonus = receiver_bonus + in_receiver_bonus
        WHERE month_year = TO_DATE('01-'||TO_CHAR(id_trans_date,'mm-yy'),'dd-mm-yy')
        AND MONTH = TO_CHAR(id_trans_date,'mm')
        AND MODULE = 'C2S'
        AND network_code = iv_network_code
        AND msisdn = iv_msisdn
        AND prefix_id = iv_prefix_id
        AND SERVICE_TYPE = iv_service_type
    AND SUB_SERVICE=iv_sub_service;

         IF SQL%NOTFOUND
         THEN
            INSERT INTO MSISDN_USAGE_SUMMARY(msisdn, prefix_id, MONTH,
                 network_code, success_count, success_amount,
                 fail_count, fail_amount, SERVICE_TYPE,SUB_SERVICE,
                 receiver_access_fee, receiver_credit_amount,
                 receiver_bonus, tax1, tax2, tax3, MODULE, month_year)
                 VALUES (iv_msisdn,iv_prefix_id,TO_CHAR(id_trans_date,'mm'),
                 iv_network_code,in_success_count,in_success_amount,
                 in_fail_count,in_fail_amount,iv_service_type,iv_sub_service,
                 in_receiver_access_fee,in_receiver_credit_amount,
                   in_receiver_bonus,in_tax1,in_tax2,0,'C2S',
                 TO_DATE('01-'||TO_CHAR(id_trans_date,'mm-yy'),'dd-mm-yy'));
         END IF;

      EXCEPTION
         WHEN OTHERS
         THEN
            DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE 1, '|| SQLERRM);
            v_messageforlog :='OTHERS Error in SP_UPDATE_C2S_MSISDN_USAGE 1, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE procException;
      END;
   END LOOP;
EXCEPTION
      WHEN procException
      THEN
         DBMS_OUTPUT.PUT_LINE('procException in SP_UPDATE_C2S_MSISDN_USAGE 2');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog :='OTHERS Exception in SP_UPDATE_C2S_MSISDN_USAGE 2, Date:'|| id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END SP_UPDATE_C2S_MSISDN_USAGE;


PROCEDURE sp_insert_opening_bal (
      p_date             IN   DATE
   )
   IS
      n_previousdate     TEMP_DAILY_CHNL_TRANS_MAIN.trans_date%TYPE;
      n_user_id    TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
      n_opning_stock    TEMP_DAILY_CHNL_TRANS_MAIN.opening_balance%TYPE;
      n_closing_stock    TEMP_DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      n_network_code    TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
      n_network_code_for    TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
      n_product_code        TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
      n_grph_domain_code     TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
      rcd_count          NUMBER;
      n_max_balance_date TEMP_DAILY_CHNL_TRANS_MAIN.trans_date%TYPE;


  CURSOR msisdn_usage_cursor (p_date DATE)
   IS
   SELECT user_id, opening_balance, closing_balance, network_code, network_code_for, product_code, grph_domain_code
   FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE trans_date = p_date;

   BEGIN
   FOR transaction_record IN msisdn_usage_cursor  (p_date)
   LOOP
        n_user_id := transaction_record.user_id;
        n_network_code := transaction_record.network_code;
        n_network_code_for := transaction_record.network_code_for;
        n_product_code := transaction_record.product_code;
        n_grph_domain_code := transaction_record.grph_domain_code;

         BEGIN

               SELECT /*+ INDEX(USER_DAILY_BALANCES IND_UDB_DT_ID_PROD) */ balance INTO n_closing_stock
               FROM USER_DAILY_BALANCES
               WHERE user_id = n_user_id
               AND network_code_for = n_network_code_for
               AND balance_date = p_date - 1
               AND product_code = n_product_code
               AND network_code = n_network_code;

            IF SQL%NOTFOUND
            THEN
               DBMS_OUTPUT.PUT_LINE ('SQL%NOTFOUND Exception in SP_INSERT_OPENING_BAL 1, User:' || n_user_id || SQLERRM );
               v_messageforlog := 'SQL%NOTFOUND Exception in SP_INSERT_OPENING_BAL 1, User:' || n_user_id || ' Date:' || p_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE sqlexception;
            END IF;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
                rcd_count := 0;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in SP_INSERT_OPENING_BAL 1, User:' || n_user_id || SQLERRM );
               v_messageforlog := 'OTHERS Exception in SP_INSERT_OPENING_BAL 1, User' || n_user_id || ' Date:' || p_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE sqlexception;
         END;

         IF (rcd_count = 0)
         THEN
            BEGIN

                SELECT /*+ INDEX(USER_DAILY_BALANCES IND_UDB_DT_ID_PROD) */ balance INTO n_closing_stock
                FROM USER_DAILY_BALANCES
                    WHERE user_id = n_user_id
                    AND network_code_for = n_network_code_for
                    AND product_code = n_product_code
                    AND network_code = n_network_code
                    AND balance_date = (SELECT MAX(balance_date) FROM USER_DAILY_BALANCES
                        WHERE user_id = n_user_id
                        AND network_code_for = n_network_code_for
                        AND product_code = n_product_code
                        AND network_code = n_network_code
                        AND balance_date < p_date);

               IF SQL%NOTFOUND
               THEN
                  DBMS_OUTPUT.PUT_LINE ('SQL%NOTFOUND Exception in SP_INSERT_OPENING_BAL 2, User:' || n_user_id || SQLERRM );
                  v_messageforlog := 'SQL%NOTFOUND Exception in SP_INSERT_OPENING_BAL 2, User:' || n_user_id || ' Date:' || p_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
               END IF;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                   n_closing_stock := 0;
               WHEN OTHERS
               THEN
                  DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in SP_INSERT_OPENING_BAL 2, User:' || n_user_id || SQLERRM );
                  v_messageforlog := 'OTHERS Exception in SP_INSERT_OPENING_BAL 2, User:' || n_user_id || ' Date:' || p_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE sqlexception;
            END;
        END IF;

        BEGIN

         UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
           SET opening_balance = n_closing_stock
         WHERE user_id = n_user_id
           AND trans_date = p_date
           AND product_code = n_product_code
           AND network_code = n_network_code
           AND network_code_for = n_network_code_for;

        EXCEPTION
           WHEN OTHERS
           THEN
              DBMS_OUTPUT.PUT_LINE ('Exception in SP_INSERT_OPENING_BAL 3, User:' || n_user_id || SQLERRM );
              v_messageforlog := 'SQL Exception in SP_INSERT_OPENING_BAL 3, User:' || n_user_id || ' Date:' || p_date;
              v_sqlerrmsgforlog := SQLERRM;
              RAISE sqlexception;
        END;

    END LOOP;
EXCEPTION
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in SP_INSERT_OPENING_BAL');
         RAISE retmainexception;
END;

PROCEDURE sp_update_c2s_bonuses
(
  aiv_date            IN       DATE
)
IS
    in_transferDate        C2S_TRANSFERS.TRANSFER_DATE%TYPE;
    in_serviceType         C2S_TRANSFERS.SERVICE_TYPE%TYPE;
    in_sub_service         C2S_TRANSFERS.SUB_SERVICE%TYPE;
    in_bundleID            C2S_BONUSES_MISTMP.ACCOUNT_ID%TYPE;
    in_bundleType          C2S_BONUSES_MISTMP.ACCOUNT_TYPE%TYPE;
    in_amount              C2S_BONUSES_MISTMP.BALANCE%TYPE;
    in_trans_count         DAILY_C2S_BONUSES.trans_count%TYPE;
    in_servClassID         C2S_TRANSFERS.SERVICE_CLASS_ID%TYPE;
    in_servClassCode         C2S_TRANSFERS.SERVICE_CLASS_CODE%TYPE;

--------Declaration For The Cursor
    CURSOR c2s_bonus_cursor (aiv_date DATE)
    IS
    SELECT ct.TRANSFER_DATE, ct.SERVICE_TYPE,ct.sub_service, ct.SERVICE_CLASS_ID, ct.SERVICE_CLASS_CODE,
    cb.ACCOUNT_ID,cb.ACCOUNT_TYPE, COUNT(1) trans_count, SUM(cb.BALANCE) balance
    FROM C2S_TRANSFERS_MISTMP ct, C2S_BONUSES_MISTMP cb
    WHERE ct.TRANSFER_STATUS='200'
    AND ct.TRANSFER_ID=cb.TRANSFER_ID
    --AND ct.TRANSFER_DATE=aiv_date
    GROUP BY ct.TRANSFER_DATE, ct.SERVICE_TYPE,ct.sub_service, ct.SERVICE_CLASS_ID,
    ct.SERVICE_CLASS_CODE, cb.ACCOUNT_ID, cb.ACCOUNT_TYPE;
BEGIN
   FOR c2s_bonus_record IN c2s_bonus_cursor (aiv_date)
   LOOP
   in_transferDate :=c2s_bonus_record.TRANSFER_DATE;
   --in_transferDate :=aiv_date;
   in_serviceType :=c2s_bonus_record.SERVICE_TYPE;
   in_sub_service := c2s_bonus_record.SUB_SERVICE;
   in_bundleID :=c2s_bonus_record.ACCOUNT_ID;
   in_bundleType :=c2s_bonus_record.ACCOUNT_TYPE;
   in_amount :=c2s_bonus_record.balance;
   in_trans_count := c2s_bonus_record.trans_count;
   in_servClassID :=c2s_bonus_record.SERVICE_CLASS_ID;
   in_servClassCode :=c2s_bonus_record.SERVICE_CLASS_CODE;

          BEGIN
            INSERT INTO DAILY_C2S_BONUSES
            (
              TRANS_DATE,SERVICE_TYPE,SUB_SERVICE,BUNDLE_ID,BUNDLE_TYPE,TRANS_AMOUNT,
              TRANS_COUNT,SERVICE_CLASS_ID,SERVICE_CLASS_CODE
            )
            VALUES
            (
              in_transferDate,in_serviceType,in_sub_service,in_bundleID,in_bundleType,in_amount,in_trans_count,in_servClassID,   in_servClassCode
            );
          EXCEPTION
            WHEN OTHERS
            THEN
                DBMS_OUTPUT.PUT_LINE('OTHERS EXCEPTION in sp_update_c2s_bonuses 1'|| SQLERRM);
                v_messageforlog :='OTHERS Error in sp_update_c2s_bonuses 1:'||in_transferDate;
                v_sqlerrmsgforlog := SQLERRM;
          RAISE procException;
          END;
   END LOOP;
EXCEPTION
      WHEN procException
      THEN
         DBMS_OUTPUT.PUT_LINE('procException in sp_update_c2s_bonuses 2');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog :='OTHERS Exception in sp_update_c2s_bonuses 2, Date:'|| in_transferDate;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END sp_update_c2s_bonuses;


   PROCEDURE ret_refills_failure_data_proc (p_date DATE)
   IS
      /* Variables for refill amount values */
      lv_servicetype              DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_subservice              DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;
      ln_c2s_failure_ct           DAILY_C2S_TRANS_DETAILS.failure_count%TYPE:= 0;
      lv_sendercategory           DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE :=0;
      lv_receiverserviceclassid   DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE :=0;


      /* Cursor Declaration */
      CURSOR refill_failure_data (p_date DATE)
      IS
        SELECT
                c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,NVL(c2strans.service_class_id,' ') service_class_id,
                c2strans.receiver_network_code, p.product_code,
                p.unit_value,ug.grph_domain_code, cat.category_code,cat.domain_code, c2strans.SERVICE_TYPE,c2strans.SUB_SERVICE,
                COUNT(c2strans.transfer_id) c2s_failure_count
                FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P,CATEGORIES cat,USER_GEOGRAPHIES ug
                WHERE --c2strans.transfer_date = p_date AND
                 c2strans.transfer_status = '206'
                AND p.product_code=c2strans.product_code
                AND cat.category_code=c2strans.sender_category
                AND ug.user_id=c2strans.sender_id
                GROUP BY c2strans.transfer_date,c2strans.sender_id, c2strans.network_code, c2strans.receiver_network_code, p.product_code, ug.grph_domain_code,
                cat.category_code, cat.domain_code, c2strans.SERVICE_TYPE,c2strans.SUB_SERVICE, service_class_id, p.unit_value;

   BEGIN
      gv_userid := '';
      gv_networkcode := '';
      gv_networkcodefor := '';
      gv_grphdomaincode := '';
      gv_productcode := '';
      gv_categorycode := '';
      gv_domaincode := '';
      lv_servicetype := '';
      lv_subservice := '';
      lv_sendercategory := '';
      lv_receiverserviceclassid := '';
      gv_productmrp := '';

      /* Iterate thru. the Refill_failure cursor */
      FOR ret_data_cur IN refill_failure_data (n_date_for_mis)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         lv_sendercategory := ret_data_cur.category_code;
     lv_receiverserviceclassid := ret_data_cur.service_class_id;
     gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
     lv_subservice := ret_data_cur.SUB_SERVICE;
         --gd_transaction_date := p_date;
          gd_transaction_date := ret_data_cur.transfer_date;
         ln_c2s_failure_ct := ret_data_cur.c2s_failure_count;
         gv_productmrp := ret_data_cur.unit_value;

    BEGIN
            UPDATE DAILY_C2S_TRANS_DETAILS
               SET
                   failure_count = ln_c2s_failure_ct
        WHERE user_id = gv_userid
        AND trans_date = gd_transaction_date
        AND receiver_network_code = gv_networkcodefor
            AND receiver_service_class_id = lv_receiverserviceclassid
        AND SERVICE_TYPE = lv_servicetype
            AND SUB_SERVICE = lv_subservice
                AND sender_category_code = lv_sendercategory;

         IF SQL%NOTFOUND
         THEN
               INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
             sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, SUB_SERVICE,total_tax1,
                         total_tax2, total_tax3,
                         sender_transfer_amount, receiver_credit_amount,
                         receiver_access_fee, differential_adjustment_tax1,
                         differential_adjustment_tax2,
                         differential_adjustment_tax3, receiver_bonus,
                         created_on, transaction_amount, transaction_count,
                         differential_amount,receiver_validity,receiver_bonus_validity,differential_count,failure_count
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
             lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype,lv_subservice, 0,0, 0,0, 0,0, 0,0,0, 0,
                         gd_createdon, 0, 0,0,0,0,0,ln_c2s_failure_ct
                        );
         END IF;
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE(   'sqlexception in RET_REFILLS_FAILURE_DATA_PROC 1, User:' || gv_userid || SQLERRM );
               v_messageforlog :='sqlexception in RET_REFILLS_FAILURE_DATA_PROC 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC 1, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                                --end of retailer insertion block

      END LOOP;
      EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in RET_REFILLS_FAILURE_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC 4:' || SQLERRM);
         RAISE retmainexception;
   END;                                                 --end of Refill values


   --new added for c2s_bonuses removal
PROCEDURE sp_split_bonus_details
(
    aiv_date IN DATE
)
IS
    sp_transferID           C2S_TRANSFERS.TRANSFER_ID%TYPE;
    sp_bundleType     C2S_BONUSES_MISTMP.ACCOUNT_TYPE%TYPE;
    sp_amount            C2S_BONUSES_MISTMP.BALANCE%TYPE;
    sp_bundleID          C2S_BONUSES_MISTMP.ACCOUNT_ID%TYPE;
    bonus_details VARCHAR2(2000);
    bon_det_separated_by_pipe  Mis_Data_Pkg_New.t_array;
    bon_det_separated_by_colon  Mis_Data_Pkg_New.t_array;
    colon_separated_string VARCHAR2(50);

    --------Declaration For The Cursor

    CURSOR split_bonus_cursor (in_date DATE)
    IS
    SELECT ct.transfer_date,ct.BONUS_DETAILS, ct.TRANSFER_ID FROM C2S_TRANSFERS_MISTMP ct
    WHERE ct.TRANSFER_STATUS='200'; --AND ct.TRANSFER_DATE=in_date;
    BEGIN
        FOR split_bonus_record IN split_bonus_cursor (aiv_date)
        LOOP
            bonus_details := split_bonus_record.BONUS_DETAILS;
            sp_transferID   :=split_bonus_record.TRANSFER_ID;
            BEGIN
                bon_det_separated_by_pipe := SPLIT(bonus_details,'|');
                FOR i IN 1..bon_det_separated_by_pipe.COUNT LOOP
                    colon_separated_string := bon_det_separated_by_pipe(i);
                    bon_det_separated_by_colon := SPLIT(colon_separated_string,':');
                    sp_bundleID := RTRIM(bon_det_separated_by_colon(1),':');
                    sp_bundleType := RTRIM(bon_det_separated_by_colon(2),':');
                    sp_amount := TO_NUMBER(RTRIM(bon_det_separated_by_colon(3),':'));
                    --DBMS_OUTPUT.PUT_LINE( 'sp_bundleID:  ' || sp_bundleID);
                    --DBMS_OUTPUT.PUT_LINE( 'sp_amount:  ' || sp_amount);
                    --DBMS_OUTPUT.PUT_LINE( 'sp_bundleType:  ' || sp_bundleType);
                    BEGIN
                        INSERT INTO C2S_BONUSES_MISTMP (transfer_id,account_id,account_type,balance) VALUES
                            (sp_transferID,(select bundle_id from bonus_bundle_master where bundle_code=sp_bundleID),sp_bundleType,sp_amount);
                        EXCEPTION
                        WHEN sqlexception
                        THEN
                            DBMS_OUTPUT.PUT_LINE(   'sqlexception in SP_SPLIT_BONUS_DETAILS 1'|| SQLERRM );
                            v_messageforlog :='sqlexception in SP_SPLIT_BONUS_DETAILS 1' ;
                            v_sqlerrmsgforlog := SQLERRM;
                        RAISE procexception;
                        WHEN OTHERS
                        THEN
                            DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in SP_SPLIT_BONUS_DETAILS 1'|| SQLERRM);
                            v_messageforlog := 'OTHERS EXCEPTION in SP_SPLIT_BONUS_DETAILS 1';
                            v_sqlerrmsgforlog := SQLERRM;
                            RAISE procexception;
                    END;
                END LOOP;
            END;
        END LOOP;
EXCEPTION
WHEN procexception
   THEN
        DBMS_OUTPUT.PUT_LINE ('procexception in SP_SPLIT_BONUS_DETAILS 2:' || SQLERRM);
        RAISE retmainexception;
WHEN OTHERS
    THEN
        DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in SP_SPLIT_BONUS_DETAILS 2:' || SQLERRM);
        RAISE retmainexception;


END sp_split_bonus_details;

FUNCTION SPLIT (p_in_string VARCHAR2, p_delim VARCHAR2) RETURN t_array
IS

    i       NUMBER :=0;
    pos     NUMBER :=0;
    bonus_det  VARCHAR2(50) := p_in_string;
    strings t_array;

BEGIN
     pos := INSTR(bonus_det,p_delim,1,1);
     IF pos = 0 THEN
            strings(i+1) := bonus_det;
     END IF;
    WHILE ( pos != 0) LOOP
        i := i + 1;
        strings(i) := SUBSTR(bonus_det,1,pos);
        bonus_det := SUBSTR(bonus_det,pos+1,LENGTH(bonus_det ));
        pos := INSTR(bonus_det,p_delim,1,1);
        IF pos = 0 THEN
            strings(i+1) := bonus_det;
        END IF;
    END LOOP;
    RETURN strings;

END SPLIT;

   PROCEDURE refills_lms_c2s_summary_proc (p_date DATE)
   IS
      /* Variables for refill amount values */
      lv_productCode            DAILY_C2S_LMS_SUMMARY.PRODUCT_CODE%TYPE;
      lv_lmsProfiletype         DAILY_C2S_LMS_SUMMARY.LMS_PROFILE%TYPE;
      ln_c2s_trans_ct           DAILY_C2S_LMS_SUMMARY.transaction_count%TYPE:= 0;
      ln_c2s_trans_amt          DAILY_C2S_LMS_SUMMARY.transaction_amount%TYPE:= 0;
          /* Cursor Declaration */
      CURSOR refill_lms_data (p_date DATE)
      IS
        SELECT  CT.sender_id,CT.PRODUCT_CODE,CT.LMS_PROFILE, COUNT (CT.transfer_id) c2s_count, SUM(CT.transfer_value) c2s_amount
        FROM   C2S_TRANSFERS_MISTMP CT
        WHERE CT.transfer_date = p_date                  
        AND  CT.transfer_status = '200'
        AND  CT.transfer_type = 'TXN'
        and    CT.LMS_PROFILE is not NULL    
        GROUP BY CT.sender_id,CT.PRODUCT_CODE,CT.LMS_PROFILE;
   BEGIN
      gv_userid := '';
      lv_productCode := '';
      lv_lmsProfiletype := '';
      FOR ret_data_cur IN refill_lms_data (n_date_for_mis)
      LOOP
         gv_userid := ret_data_cur.sender_id;
         lv_productCode := ret_data_cur.PRODUCT_CODE;
         lv_lmsProfiletype := ret_data_cur.LMS_PROFILE;
         gd_transaction_date := p_date;
         ln_c2s_trans_ct := ret_data_cur.c2s_count;
         ln_c2s_trans_amt := ret_data_cur.c2s_amount;
        
         BEGIN
            INSERT INTO DAILY_C2S_LMS_SUMMARY (user_id, trans_date, PRODUCT_CODE, LMS_PROFILE, transaction_amount, transaction_count)
                VALUES (gv_userid, gd_transaction_date, lv_productCode, lv_lmsProfiletype, ln_c2s_trans_amt, ln_c2s_trans_ct);
         EXCEPTION
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.PUT_LINE(   'sqlexception in refills_lms_c2s_summary_proc 1, User:' || gv_userid || SQLERRM );
               v_messageforlog :='sqlexception in refills_lms_c2s_summary_proc 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in refills_lms_c2s_summary_proc 2, User:' || gv_userid || SQLERRM );
               v_messageforlog := 'OTHERS EXCEPTION in refills_lms_c2s_summary_proc 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
         END;                           
     END LOOP;
   EXCEPTION
      WHEN procexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('procexception in refills_lms_c2s_summary_proc 3:' || SQLERRM);
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in refills_lms_c2s_summary_proc 4:' || SQLERRM);
         RAISE retmainexception;
   END refills_lms_c2s_summary_proc;

PROCEDURE Sp_Update_Mon_AmbSettData1 (aiv_date DATE)
   IS
    id_trans_date                       DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
      ln_roam_c2s_transfer_out_amt           MONTHLY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE;
      ln_c2s_transfer_out_count          MONTHLY_CHNL_TRANS_MAIN.c2s_transfer_out_count%TYPE;
      ln_c2s_transfer_out_amount        MONTHLY_CHNL_TRANS_MAIN.c2s_transfer_out_amount%TYPE;
      ln_differential                      MONTHLY_CHNL_TRANS_MAIN.differential%TYPE;
      ln_adjustment_in                     MONTHLY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      ln_adjustment_out                  MONTHLY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      ln_C2S_TRANSFER_IN_AMOUNT           MONTHLY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_AMOUNT%TYPE;
      ln_C2S_TRANSFER_IN_COUNT            MONTHLY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_COUNT%TYPE;
      ln_REV_DIFFERENTIAL               MONTHLY_CHNL_TRANS_MAIN.REV_DIFFERENTIAL%TYPE;
     
  --Cursor Declaration
   CURSOR monthly_chnl_main  (aiv_date DATE)
   IS
      SELECT   Mast.transfer_date,Mast.sender_id, Mast.network_code,Mast.service_class_id,
                  Mast.receiver_network_code, Mast.product_code,
                  Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                  Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                  COUNT (Mast.transfer_id) c2s_count,
                  SUM(Mast.transfer_value) c2s_amount,
                  NVL(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                  NVL(SUM(Mast.receiver_access_fee),0) c2s_recfee,
                  NVL(SUM(Mast.receiver_tax1_value),0) c2s_rectax1,
                  NVL(SUM(Mast.receiver_tax2_value),0) c2s_rectax2,
                  NVL(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                  NVL(SUM(Mast.receiver_bonus_value),0) c2s_rec_bonus,
                  NVL(SUM(adj.tax1_value),0) diff_tax1,
                  NVL(SUM(adj.tax2_value),0) diff_tax2,
                  NVL(SUM(adj.tax3_value),0) diff_tax3,
                  NVL(SUM(adj.transfer_value),0) diff_amount,
                  SUM(CASE  WHEN NVL(adj.transfer_value,0) <>0 THEN 1 ELSE 0 END) diff_count,
                  NVL(SUM(Mast.receiver_validity),0) validity,
                  NVL(SUM(Mast.receiver_bonus_validity),0) bonus_validity,
                  NVL(SUM(Mast.PENALTY),0)PENALTY,
                  NVL(SUM(Mast.OWNER_PENALTY),0)OWNER_PENALTY
          FROM (SELECT
                  c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                  c2strans.receiver_network_code, p.product_code,
                  p.unit_value,ug.grph_domain_code, cat.category_code,
                  cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                  c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                  c2strans.receiver_access_fee,c2strans.receiver_tax1_value,c2strans.receiver_tax2_value,
                  c2strans.sender_transfer_value,c2strans.receiver_bonus_value,c2strans.receiver_validity,
                  c2strans.receiver_bonus_validity,
                  c2strans.PENALTY,
                  c2strans.owner_penalty
        FROM   C2S_TRANSFERS c2strans,PRODUCTS P,
        CATEGORIES cat,USER_GEOGRAPHIES ug
        WHERE (c2strans.transfer_date != aiv_date and c2strans.reconciliation_date = aiv_date) AND
          c2strans.transfer_status = '200'
        AND  c2strans.transfer_type = 'TXN'
        AND   p.product_code=c2strans.product_code
        AND   cat.category_code=c2strans.sender_category
        AND ug.user_id=c2strans.sender_id
        ) Mast,ADJUSTMENTS adj
        WHERE  Mast.transfer_id =adj.reference_id (+)
        AND    Mast.sender_id   = adj.user_id(+)
        GROUP BY Mast.transfer_date,Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
             Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;

BEGIN
   id_trans_date:=aiv_date;
 
   BEGIN
      FOR t_r IN monthly_chnl_main (aiv_date)
      LOOP
        id_trans_date:=t_r.transfer_date;
      ln_roam_c2s_transfer_out_amt:=0;
      ln_c2s_transfer_out_count :=0;
        ln_c2s_transfer_out_amount :=0;
        ln_adjustment_out   :=0;
        ln_differential  :=0;
        ln_C2S_TRANSFER_IN_COUNT  :=0;
        ln_C2S_TRANSFER_IN_AMOUNT  :=0;
        ln_adjustment_in  :=0;
        ln_REV_DIFFERENTIAL   :=0;

              
       IF (t_r.network_code <> t_r.receiver_network_code)
      THEN
            ln_roam_c2s_transfer_out_amt:=t_r.c2s_amount;
      END IF;
      IF (t_r.SERVICE_TYPE <> 'RCREV')
      THEN
        ln_c2s_transfer_out_count :=t_r.c2s_count;
        ln_c2s_transfer_out_amount :=t_r.c2s_amount;
        ln_adjustment_out   :=t_r.diff_count;
        ln_differential  :=t_r.diff_amount;
      ELSE
        ln_C2S_TRANSFER_IN_COUNT  :=t_r.c2s_count;
        ln_C2S_TRANSFER_IN_AMOUNT  :=t_r.c2s_amount;
        ln_adjustment_in  :=t_r.diff_count;
        ln_REV_DIFFERENTIAL   :=t_r.diff_amount;
      END IF;

         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount = roam_c2s_transfer_out_amount + ln_roam_c2s_transfer_out_amt,
                   c2s_transfer_out_count = c2s_transfer_out_count + ln_c2s_transfer_out_count,
                   c2s_transfer_out_amount = c2s_transfer_out_amount + ln_c2s_transfer_out_amount,
                   differential = differential + ln_differential,
                   adjustment_in = adjustment_in + ln_adjustment_in,
                   adjustment_out = adjustment_out + ln_adjustment_out,
                   C2S_TRANSFER_IN_AMOUNT  = C2S_TRANSFER_IN_AMOUNT + ln_C2S_TRANSFER_IN_AMOUNT,
                   C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + ln_C2S_TRANSFER_IN_COUNT,
                   REV_DIFFERENTIAL  = REV_DIFFERENTIAL +  ln_REV_DIFFERENTIAL

             WHERE user_id = t_r.sender_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(id_trans_date,'mm-yy')
               AND product_code = t_r.product_code
               AND category_code = t_r.category_code
               AND network_code = t_r.network_code
               AND network_code_for = t_r.receiver_network_code
               AND sender_domain_code = t_r.domain_code
               AND grph_domain_code = t_r.grph_domain_code;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_MAIN
                                (user_id,trans_date,
                            product_code, category_code,
                            network_code, network_code_for,
                            sender_domain_code,
                            roam_c2s_transfer_out_amount,
                            c2s_transfer_out_count,
                            c2s_transfer_out_amount,
                            o2c_transfer_in_count,
                            o2c_transfer_in_amount,
                            o2c_return_out_count,
                            o2c_return_out_amount,
                            o2c_withdraw_out_count,
                            o2c_withdraw_out_amount,
                            c2c_transfer_in_count,
                            c2c_transfer_in_amount,
                            c2c_transfer_out_count,
                            c2c_transfer_out_amount,
                            c2c_return_in_count,
                            c2c_return_in_amount,
                            c2c_return_out_count,
                            c2c_return_out_amount,
                            c2c_withdraw_in_count,
                            c2c_withdraw_in_amount,
                            c2c_withdraw_out_count,
                            c2c_withdraw_out_amount, differential,
                            adjustment_in, adjustment_out, created_on,
                            grph_domain_code,
                            c2c_reverse_in_count,
                            c2c_reverse_in_amount,
                            c2c_reverse_out_count,
                            c2c_reverse_out_amount,
                            o2c_reverse_in_count,
                            o2c_reverse_in_amount,
                            o2c_reverse_out_count,
                            o2c_reverse_out_amount,
                            C2S_TRANSFER_IN_AMOUNT ,
                            C2S_TRANSFER_IN_COUNT ,
                            REV_DIFFERENTIAL
                    )
                    VALUES (t_r.sender_id,
                            TO_DATE('01-'||TO_CHAR (id_trans_date,'mm-yy'),'dd-mm-yy'),
                            t_r.product_code, t_r.category_code,
                            t_r.network_code, t_r.receiver_network_code,
                            t_r.domain_code,
                            ln_roam_c2s_transfer_out_amt,
                            ln_c2s_transfer_out_count,
                            ln_c2s_transfer_out_amount,
                            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, ln_differential,
                            ln_adjustment_in, ln_adjustment_out, SYSDATE,
                            t_r.grph_domain_code,0,0,0,0,0,0,0,0,
                            ln_C2S_TRANSFER_IN_AMOUNT ,
                            ln_C2S_TRANSFER_IN_COUNT ,
                            ln_REV_DIFFERENTIAL
                );
            END IF;
          EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1 1, User:'  || t_r.sender_id || SQLERRM);
               v_messageforlog := 'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1 1, User:' || t_r.sender_id || ', Date:' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
          
          -- 3 start here 
          BEGIN
            UPDATE MONTHLY_C2S_TRANS_DETAILS
               SET total_tax1 = total_tax1 + t_r.c2s_rectax1,
                   total_tax2 = total_tax2 + t_r.c2s_rectax2,
                   --total_tax3 = total_tax3 + t_r.total_tax3,
                   sender_transfer_amount = sender_transfer_amount + t_r.c2s_sender_value,
                   receiver_credit_amount = receiver_credit_amount + t_r.c2s_rectrans,
                   receiver_access_fee = receiver_access_fee + t_r.c2s_recfee,
                   differential_adjustment_tax1 = differential_adjustment_tax1 + t_r.diff_tax1,
                   differential_adjustment_tax2 = differential_adjustment_tax2 + t_r.diff_tax2,
                   differential_adjustment_tax3 = differential_adjustment_tax3 + t_r.diff_tax3,
                   receiver_bonus = receiver_bonus + t_r.c2s_rec_bonus,
                   transaction_count=transaction_count+t_r.c2s_count,
                   transaction_amount=transaction_amount+t_r.c2s_amount,
                   differential_amount=differential_amount+t_r.diff_amount,
                    --SENDER_REVERSE_AMOUNT = SENDER_REVERSE_AMOUNT + t_r.SENDER_REVERSE_AMOUNT,
                    --RECEIVER_DEBIT_AMOUNT = RECEIVER_DEBIT_AMOUNT + t_r.RECEIVER_DEBIT_AMOUNT,
                    --REVERSE_AMOUNT = REVERSE_AMOUNT + t_r.REVERSE_AMOUNT,
                    --REVERSE_COUNT = REVERSE_COUNT + t_r.REVERSE_COUNT,
                    --REVERSE_DIFF_AMOUNT = REVERSE_DIFF_AMOUNT + t_r.REVERSE_DIFF_AMOUNT,
                    --REVERSE_DIFF_COUNT = REVERSE_DIFF_COUNT + t_r.REVERSE_DIFF_COUNT,
                    PENALTY=PENALTY+t_r.PENALTY,
                    OWNER_PENALTY=OWNER_PENALTY+t_r.OWNER_PENALTY
                    --roam_amount=roam_amount+t_r.roam_amount
             WHERE user_id = t_r.sender_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(id_trans_date,'mm-yy')
               AND receiver_network_code = t_r.receiver_network_code
               AND sender_category_code = t_r.category_code
               AND receiver_service_class_id = t_r.service_class_id
               AND SERVICE_TYPE = t_r.SERVICE_TYPE
                AND SUB_SERVICE = t_r.SUB_SERVICE;

            IF SQL%NOTFOUND
            THEN
               INSERT INTO MONTHLY_C2S_TRANS_DETAILS
                              (user_id,
                            trans_date,
                            sender_category_code,
                            receiver_service_class_id,
                            receiver_network_code, SERVICE_TYPE,sub_service,
                            total_tax1, total_tax2, total_tax3,
                            sender_transfer_amount,
                            receiver_credit_amount,
                            receiver_access_fee,
                            differential_adjustment_tax1,
                            differential_adjustment_tax2,
                            differential_adjustment_tax3,
                            receiver_bonus, created_on,
                            receiver_validity,
                            receiver_bonus_validity,
                            transaction_amount,
                            transaction_count,
                            differential_amount,
                            SENDER_REVERSE_AMOUNT ,
                            RECEIVER_DEBIT_AMOUNT ,
                            REVERSE_AMOUNT ,
                            REVERSE_COUNT ,
                            REVERSE_DIFF_AMOUNT,
                            REVERSE_DIFF_COUNT,
                            PENALTY,
                            OWNER_PENALTY,
                            roam_amount
                            )
                    VALUES (t_r.sender_id,
                            TO_DATE('01-'||TO_CHAR (id_trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.category_code,
                            t_r.service_class_id,
                            t_r.receiver_network_code, t_r.SERVICE_TYPE,t_r.sub_service,
                            t_r.c2s_rectax1, t_r.c2s_rectax2, 0,
                            t_r.c2s_sender_value,
                            t_r.c2s_rectrans,
                            t_r.c2s_recfee,
                            t_r.diff_tax1,
                            t_r.diff_tax2,
                            t_r.diff_tax3,
                            t_r.c2s_rec_bonus, SYSDATE,
                            0,0,
                            t_r.c2s_amount,t_r.c2s_count,
                            t_r.diff_amount,
                            0 ,0 ,0 ,0 ,0,0,
                            t_r.PENALTY,
                            t_r.OWNER_PENALTY,
                            0 
                            );
            END IF;

            EXCEPTION
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.PUT_LINE (   'OTHERS EXCEPTION in Sp_Update_Monthly_Data3 5, User:'|| t_r.sender_id || SQLERRM);
               v_messageforlog := 'OTHERS Error in Sp_Update_Monthly_Data3 5, User:'|| t_r.sender_id;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE procexception;
          END;
  
      END LOOP;
   EXCEPTION
      WHEN procexception
     THEN
        DBMS_OUTPUT.PUT_LINE ('procexception EXCEPTION in Sp_Update_Mon_AmbSettData1 2');
        RAISE mainexception;

      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION:' || SQLERRM);
         RAISE mainexception;
   END;
 EXCEPTION
      WHEN mainexception
      THEN
         DBMS_OUTPUT.PUT_LINE ('mainexception in Sp_Update_Mon_AmbSettData1 7');
         RAISE retmainexception;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION =' || SQLERRM);
         v_messageforlog := 'OTHERS Exception in Sp_Update_Mon_AmbSettData1 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE retmainexception;
END;

END Mis_Data_Pkg_New;
/
commit;


--- Batch Assoc Profile Link disabled
Update PAGES set MENU_ITEM='N' where PAGE_CODE ='BASSPRF001';
COMMIT;
Update PAGES set MENU_ITEM='N' where PAGE_CODE ='OTASPR001';
COMMIT;
Update PAGES set MENU_ITEM='N' where PAGE_CODE ='ACTBON001';
COMMIT;
Update PAGES set MENU_ITEM='N' where PAGE_CODE ='ACTVSUB001';
COMMIT;

--roam recharge add commission
Update System_preferences set default_value='true' where preference_code='ALLOW_ROAM_ADDCOMM';
COMMIT;


--For fixing DEF2286



SET DEFINE OFF;

UPDATE SERVICE_TYPE SET MESSAGE_FORMAT = 'TYPE TXNID LANGUAGE1 LANGUAGE2' , REQUEST_PARAM = 'TYPE,MSISDN1,PIN,MSISDN2,AMOUNT,LANGUAGE1,LANGUAGE2,SELECTOR,SERVICECLASS,TXNID', RESPONSE_PARAM = 'TYPE,TXNSTATUS,TXNID' WHERE SERVICE_TYPE = 'BRCREV';

Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('BRCREVREQ', 'EXTGW', '190', 'BRCREV', 'Recharge REV', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('04/16/2015 11:00:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/24/2015 10:48:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0001228', 
    NULL, 'TYPE,MSISDN1,PIN,MSISDN2,AMOUNT,LANGUAGE1,LANGUAGE2,SELECTOR,SERVICECLASS,TXNID');
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('BRCREVREQ', 'USSD', '190', 'BRCREV', 'Recharge REV', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('04/16/2015 11:00:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/24/2015 10:48:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0001229', 
    NULL, 'TYPE,MSISDN1,PIN,MSISDN2,AMOUNT,LANGUAGE1,LANGUAGE2,SELECTOR,SERVICECLASS,TXNID');
COMMIT;


update roles set  status = 'Y' where role_code='BONUSENQY';
commit;

update service_type set unregistered_access_allowed ='Y' where service_type='GMBBAR';
commit;
ALTER TABLE P2P_SUBSCRIBERS_COUNTERS
MODIFY(SERVICE_TYPE VARCHAR2(15 BYTE));
COMMIT;