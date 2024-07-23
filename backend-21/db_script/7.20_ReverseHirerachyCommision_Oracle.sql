CREATE OR REPLACE PROCEDURE DUALWALLET_AUTOC2C_CAL (
   p_date      		IN     DATE,
   p_message   		OUT    VARCHAR2,
   p_message_fail   OUT    VARCHAR2,
   ret_array   		OUT    T
)
IS
--This procedure is used for calculation of reverse hirerachy commision
   p_user_id                	channel_transfers.to_user_id%TYPE;
   p_user_amount            	channel_transfers.transfer_mrp%TYPE;
   p_user_date              	channel_transfers.transfer_date%TYPE;
   on_amount                	channel_transfers.transfer_mrp%TYPE;
   u_user_amount            	varchar2(20);
   u_final_amount           	varchar2(100);
   u_commision_id           	channel_users.comm_profile_set_id%TYPE;
   u_msisdn                 	users.msisdn%TYPE;
   var_check             		number(2);
   V_HIERARCYCOMMISSION   		varchar2(10);
   V_PARENT_HIERARCYCOMMISSION 	NUMERIC(10,2);
   V_OWNER_HIERARCYCOMMISSION 	NUMERIC(10,2);
   v_CommPercent				NUMERIC(10,2);
   TYPE chartype IS TABLE OF VARCHAR2 (30) INDEX BY BINARY_INTEGER;
   v_through_user           chartype;
   v_beneficary_parent_id   chartype;
   v_owner_id				chartype;
   v_isowner				chartype;
   sqlexception             EXCEPTION;
   array_index number(15);

   CURSOR user_list_cur
   IS
      SELECT   SUM (REQUESTED_QUANTITY) on_amount, to_user_id, ct.transfer_date,ct.status,count(*) total
      FROM channel_transfers ct, channel_transfers_items cti
      WHERE ct.transfer_id = cti.transfer_id
      AND ct.status IN ('CLOSE')
      AND ct.transfer_date = p_date
      AND ct.from_user_id=(SELECT DEFAULT_VALUE FROM system_preferences WHERE preference_code = 'DUAL_WALL_USER_ID')
      GROUP BY to_user_id, ct.transfer_date, ct.status;
BEGIN
        p_message	:='FAIL';
        array_index	:=1;
        ret_array 	:= NEW T();
        ret_array.EXTEND(10000);

   FOR user_records IN user_list_cur

   LOOP

      p_user_id 	:= user_records.to_user_id;
      p_user_amount := user_records.on_amount;
      p_user_date 	:= user_records.transfer_date;
	  DBMS_OUTPUT.put_line ('Before DUAL_WALL_USER_ID');
      BEGIN
         SELECT  user_id, CONNECT_BY_ROOT (user_id) Parent , owner_id,CASE owner_id WHEN CONNECT_BY_ROOT (user_id) THEN 'OWNER' ELSE 'PARENT' END OWNER
         BULK COLLECT INTO v_through_user, v_beneficary_parent_id,v_owner_id,v_isowner
         FROM users u
         WHERE user_id = p_user_id
         START WITH user_id != p_user_id
         CONNECT BY parent_id = PRIOR user_id AND user_id != PRIOR user_id;

		 SELECT DEFAULT_VALUE
		 INTO V_HIERARCYCOMMISSION
         FROM system_preferences
         WHERE preference_code = 'DW_HC_USERTYPE';

		 SELECT DEFAULT_VALUE
		 INTO V_PARENT_HIERARCYCOMMISSION
         FROM system_preferences
         WHERE preference_code = 'DW_HC_PARENT_PCT';

		 SELECT DEFAULT_VALUE
		 INTO V_OWNER_HIERARCYCOMMISSION
         FROM system_preferences
         WHERE preference_code = 'DW_HC_OWNER_PCT';

		 DBMS_OUTPUT.put_line ('Before v_beneficary_parent_id EXISTS');
         IF v_beneficary_parent_id.EXISTS (1)
         THEN
            FOR temp_cnt IN
               v_beneficary_parent_id.FIRST .. v_beneficary_parent_id.COUNT
            LOOP
                DBMS_OUTPUT.put_line ('Before V_HIERARCYCOMMISSION='||V_HIERARCYCOMMISSION||', v_isowner='||v_isowner(temp_cnt));
				IF V_HIERARCYCOMMISSION ='PARENT' AND  v_isowner(temp_cnt)='PARENT'
                  THEN
                  v_CommPercent:=V_PARENT_HIERARCYCOMMISSION;
                ELSE IF V_HIERARCYCOMMISSION ='OWNER' AND  v_isowner(temp_cnt)='OWNER'
                  THEN
                  v_CommPercent:=V_OWNER_HIERARCYCOMMISSION;
                ELSE IF V_HIERARCYCOMMISSION ='BOTH' AND v_isowner(temp_cnt)='PARENT'
                  THEN
                  v_CommPercent:=V_PARENT_HIERARCYCOMMISSION;
                ELSE IF V_HIERARCYCOMMISSION ='BOTH' AND v_isowner(temp_cnt)='OWNER'
                  THEN
                  v_CommPercent:=V_OWNER_HIERARCYCOMMISSION;
                  ELSE
                  v_CommPercent:=0;
				END IF ;
				END IF ;
				END IF ;
				END IF ;
                DBMS_OUTPUT.put_line ('Before v_CommPercent='||v_CommPercent);
			  BEGIN

				 SELECT msisdn
				   INTO u_msisdn
				   FROM users
				  WHERE user_id = v_beneficary_parent_id (temp_cnt);

				EXCEPTION

					  WHEN NO_DATA_FOUND
					  THEN
						 u_msisdn := 0;
						 p_message:='FAIL';

				 WHEN OTHERS
				 THEN
					u_msisdn := 0;
					p_message:='FAIL';

				END;

				BEGIN
					u_final_amount :=getfinalcommisionforfixValue(v_beneficary_parent_id (temp_cnt), p_user_amount,v_CommPercent);
				END;

				var_check:= instr (u_final_amount,':');
				u_commision_id:='NA';
				DBMS_OUTPUT.put_line ('Before u_final_amount='||u_final_amount||',u_commision_id='||u_commision_id);
			  IF var_check >0
				then
				DBMS_OUTPUT.put_line ('The Funct '||substr (u_final_amount,instr(u_final_amount,':')+1)||'MSISDN'||u_msisdn||'ISOWNER'||v_isowner(temp_cnt) );
				ret_array (array_index):=u_final_amount||','||u_msisdn;
				array_index:=array_index+1;
			   ELSE IF var_check = 0  and u_final_amount =0
				then
				DBMS_OUTPUT.put_line ('Commission is not defined or is zero for the userId,'||v_beneficary_parent_id (temp_cnt)||',u_msisdn='||u_msisdn||',ISOWNER='||v_isowner(temp_cnt));
				ret_array (array_index):='0:,Commission is not defined or is zero for the userId,'||v_beneficary_parent_id (temp_cnt)||', u_msisdn='||u_msisdn||', ISOWNER='||v_isowner(temp_cnt);
				array_index:=array_index+1;
			   ELSE
				 DBMS_OUTPUT.put_line ('None of the error Occured ,u_final_amount='||u_final_amount||',u_msisdn='||u_msisdn||',var_check='||var_check||',ISOWNER='||v_isowner(temp_cnt));
				 INSERT INTO dual_wallet_auto_c2c
				 (through_user,beneficary_parent_id,transaction_date, on_amount,comm_profile_set_id, foc_amount,file_generated, msisdn)
				 VALUES (v_through_user (temp_cnt),v_beneficary_parent_id (temp_cnt),TRUNC (p_user_date), p_user_amount,u_commision_id, u_final_amount,'N', u_msisdn);
			  END IF;
           END IF;
 END LOOP;
   END IF;
 END;
   END LOOP;
Commit;

   p_message := 'SUCCESS';
   DBMS_OUTPUT.put_line ('The sucess for the day'||p_message_fail);
EXCEPTION
   WHEN DUP_VAL_ON_INDEX
   THEN
      --INSERT into Error_log(v_Through_user(Temp_cnt)||v_Beneficary_parent_id(Temp_cnt)||TRUNC(SYSDATE))
      DBMS_OUTPUT.put_line (' DuPlicate Data' || SQLCODE ||SQLERRM);
      p_message := 'FAIL';
   WHEN OTHERS
   THEN
      --INSERT into Error_log(v_Through_user(Temp_cnt)||v_Beneficary_parent_id(Temp_cnt)||TRUNC(SYSDATE))
      DBMS_OUTPUT.put_line (' Other Errors:' || SQLCODE ||SQLERRM);
      p_message := 'FAIL';
END;
/

CREATE OR REPLACE FUNCTION getfinalcommisionforfixValue (
   p_user_id   VARCHAR2,
   p_amount    NUMBER,
   p_commPct   NUMERIC
)
   RETURN VARCHAR2
IS
--This method is used for calculation of  commision for particular user_id and amount
   b_unitvalue         NUMBER (5)  DEFAULT 1;
   commvalue           NUMERIC (12,2)  DEFAULT 0;
   v_commission_rate   NUMERIC (12,2)  DEFAULT 0;
BEGIN
   DBMS_OUTPUT.put_line ('p_user_id='||p_user_id||',p_amount='||p_amount||',p_commPct='||p_commPct);
   IF (p_user_id IS NULL)
   THEN
      DBMS_OUTPUT.PUT_LINE('USER ID IS NULL');
      RETURN '0:,USER_ID WAS NULL OR 0,';
   END IF;

   IF (p_amount IS NULL OR p_amount <= 0)
   THEN
      DBMS_OUTPUT.PUT_LINE('AMOUNT  IS NULL OR 0');
      RETURN '0:,AMOUNT FOR CALC WAS NULL OR 0,'||p_user_id ;
   END IF;

    SELECT unit_value
    INTO b_unitvalue
    FROM products
    WHERE product_code = 'ETOPUP';

    v_commission_rate:=p_commPct;
    commvalue := ((v_commission_rate * p_amount)/ 100) ;

    if (commvalue <= 0 )then 
    RETURN '0:, The value of commision is not defined correctly into the preference,'||p_user_id;
    end if;

   RETURN round(commvalue,0);

END;
/

SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DW_HC_USERTYPE', 'Hirerachy Commission User Type', 'SYSTEMPRF', 'STRING', 'OWNER', 
    NULL, NULL, 20, 'Hirerachy Commission to be given either Parent or Owner or Both', 'Y', 
    'Y', 'C2S', 'This is applied only for Dual Wallet and Hirerachy Commission for Parent=PARENT, Owner=OWNER, Both=BOTH', sysdate, 'ADMIN', 
    sysdate, 'SU0001', 'OWNER,PARENT,BOTH', 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DW_HC_PARENT_PCT', 'Hirerachy Commission Parent', 'SYSTEMPRF', 'DECIMAL', '0', 
    0, 100, 20, 'Hirerachy Commission to be calculated for Parent commission in PCT', 'Y', 
    'Y', 'C2S', 'This is applied only for Dual Wallet and Hirerachy Commission to be calculated for Parent commission in PCT', sysdate, 'ADMIN', 
    sysdate, 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DW_HC_OWNER_PCT', 'Hirerachy Commission Owner', 'SYSTEMPRF', 'DECIMAL', '1.4', 
    0, 100, 20, 'Hirerachy Commission to be calculated for Parent commission in PCT', 'Y', 
    'Y', 'C2S', 'This is applied only for Dual Wallet and Hirerachy Commission to be calculated for Parent commission in PCT', sysdate, 'ADMIN', 
    sysdate, 'SU0001', NULL, 'Y');
COMMIT;

