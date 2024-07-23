CREATE OR REPLACE FUNCTION postgres_dw.dualwallet_autoc2c_cal ( p_date timestamp, p_message OUT text, p_message_fail OUT text, ret_array OUT text[] ) AS $body$
DECLARE

--This procedure is used for calculation of reverse hirerachy commision
   p_user_id                    channel_transfers.to_user_id%TYPE;
   p_user_amount                channel_transfers.transfer_mrp%TYPE;
   p_user_date                  channel_transfers.transfer_date%TYPE;
   on_amount                    channel_transfers.transfer_mrp%TYPE;
   u_user_amount                varchar(20);
   u_final_amount               varchar(100);
   u_commision_id               channel_users.comm_profile_set_id%TYPE;
   u_msisdn                     users.msisdn%TYPE;
   var_check                     numeric;
   V_HIERARCYCOMMISSION           varchar(10);
   V_PARENT_HIERARCYCOMMISSION     NUMERIC(10,2);
   V_OWNER_HIERARCYCOMMISSION     NUMERIC(10,2);
   v_CommPercent                NUMERIC(10,2);
   --TYPE chartype IS TABLE OF varchar(30) INDEX BY integer;
   /* TYPE hierarcy_user_records_type IS RECORD (
       v_through_user           channel_transfers.to_user_id%TYPE,
	   v_beneficary_parent_id   channel_transfers.to_user_id%TYPE,
	   v_owner_id               channel_transfers.to_user_id%TYPE,
	   v_isowner                varchar(100)
    ); */
   -- hierarcy_user_records   hierarcy_user_records_type;
   hierarcy_user_records	RECORD;
   v_through_user           channel_transfers.to_user_id%TYPE;
   v_beneficary_parent_id   channel_transfers.to_user_id%TYPE;
   v_owner_id               channel_transfers.to_user_id%TYPE;
   v_isowner                varchar(100);
   array_index bigint;

   user_list_cur CURSOR FOR
      SELECT   SUM(REQUESTED_QUANTITY) on_amount, to_user_id, ct.transfer_date,ct.status,count(*) total
      FROM channel_transfers ct, channel_transfers_items cti
      WHERE ct.transfer_id = cti.transfer_id
      AND ct.status IN ('CLOSE')
      AND ct.transfer_date = p_date
      AND ct.from_user_id=(SELECT DEFAULT_VALUE FROM system_preferences WHERE preference_code = 'DUAL_WALL_USER_ID')
      GROUP BY to_user_id, ct.transfer_date, ct.status;
BEGIN
        p_message    :='FAIL';
        array_index    :=1;
        --ret_array     := NEW T();
        --ret_array.EXTEND(10000);
		--ret_array TEXT[]      DEFAULT  ARRAY[]::TEXT[]; -- empty array-constructors need a cast
		--ret_array := array[10000];

   FOR user_records IN user_list_cur

   LOOP

      p_user_id     := user_records.to_user_id;
      p_user_amount := user_records.on_amount;
      p_user_date   := user_records.transfer_date;
      RAISE NOTICE 'Before DUAL_WALL_USER_ID, for the userId=%',p_user_id;
	  
	 BEGIN 
	 RAISE NOTICE '===========BEGIN, for the userId=%=================================',p_user_id;

	  FOR hierarcy_user_records IN 
	   WITH RECURSIVE  cte AS (
	   
			 SELECT  u.user_id,u.parent_id,u.owner_id
			 FROM users u
			 --WHERE user_id != p_user_id
			
			UNION 
			
			SELECT  u2.user_id,u2.parent_id,u2.owner_id
			FROM users u2
			JOIN cte c ON (c.user_id = u2.parent_id AND c.user_id != u2.user_id )
		) SELECT  cte.user_id,cte.parent_id, cte.owner_id,CASE cte.owner_id WHEN cte.parent_id THEN 'OWNER' ELSE 'PARENT' END as owner
		FROM cte WHERE user_id = p_user_id		
			
		LOOP
		v_through_user           :=hierarcy_user_records.user_id;
		v_beneficary_parent_id   :=hierarcy_user_records.parent_id;
		v_owner_id               :=hierarcy_user_records.owner_id;
		v_isowner                :=hierarcy_user_records.owner;
		
		 RAISE NOTICE 'v_through_user=%, v_beneficary_parent_id=%, v_owner_id=%, v_isowner=%', v_through_user,v_beneficary_parent_id,v_owner_id,v_isowner;
		 RAISE NOTICE 'Before DW_HC_USERTYPE';
		 
         SELECT DEFAULT_VALUE
         INTO STRICT V_HIERARCYCOMMISSION
         FROM system_preferences
         WHERE preference_code = 'DW_HC_USERTYPE';

		 RAISE NOTICE 'Before DW_HC_PARENT_PCT';
         
		 SELECT DEFAULT_VALUE
         INTO STRICT V_PARENT_HIERARCYCOMMISSION
         FROM system_preferences
         WHERE preference_code = 'DW_HC_PARENT_PCT';

		 RAISE NOTICE 'Before DW_HC_OWNER_PCT';
         
		 SELECT DEFAULT_VALUE
         INTO STRICT V_OWNER_HIERARCYCOMMISSION
         FROM system_preferences
         WHERE preference_code = 'DW_HC_OWNER_PCT';
		
		 RAISE NOTICE 'V_HIERARCYCOMMISSION=%, V_PARENT_HIERARCYCOMMISSION=%, V_OWNER_HIERARCYCOMMISSION=%',V_HIERARCYCOMMISSION,V_PARENT_HIERARCYCOMMISSION,V_OWNER_HIERARCYCOMMISSION;
         RAISE NOTICE 'Before v_beneficary_parent_id EXISTS';
         IF v_beneficary_parent_id !='ROOT'
         THEN
           -- FOR temp_cnt IN
            --   v_beneficary_parent_id.FIRST .. v_beneficary_parent_id.COUNT
            --LOOP
                RAISE NOTICE 'Before V_HIERARCYCOMMISSION=%, v_isowner=%', V_HIERARCYCOMMISSION, v_isowner;
                IF V_HIERARCYCOMMISSION ='PARENT' AND  v_isowner='PARENT'
                  THEN
                  v_CommPercent:=V_PARENT_HIERARCYCOMMISSION;
                ELSE IF V_HIERARCYCOMMISSION ='OWNER' AND  v_isowner='OWNER'
                  THEN
                  v_CommPercent:=V_OWNER_HIERARCYCOMMISSION;
                ELSE IF V_HIERARCYCOMMISSION ='BOTH' AND v_isowner='PARENT'
                  THEN
                  v_CommPercent:=V_PARENT_HIERARCYCOMMISSION;
                ELSE IF V_HIERARCYCOMMISSION ='BOTH' AND v_isowner='OWNER'
                  THEN
                  v_CommPercent:=V_OWNER_HIERARCYCOMMISSION;
                  ELSE
                  v_CommPercent:=0;
                END IF;
                END IF;
                END IF;
                END IF;
                RAISE NOTICE 'Before v_CommPercent=%', v_CommPercent;
              BEGIN

                 SELECT msisdn
                   INTO STRICT u_msisdn
                   FROM users
                  WHERE user_id = v_beneficary_parent_id;

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
                    u_final_amount :=pretups733_osl.getfinalcommisionforfixvalue(v_beneficary_parent_id, p_user_amount,v_CommPercent);
                END;

                var_check:= position(':' in u_final_amount);
                u_commision_id:='NA';
                RAISE NOTICE 'Before u_final_amount=%,u_commision_id=%,var_check=%', u_final_amount, u_commision_id,var_check;
				IF var_check > 0
                then
                RAISE NOTICE 'The Funct %MSISDN%ISOWNER%', substr(u_final_amount,position(':' in u_final_amount)+1), u_msisdn, v_isowner;
                ret_array[array_index]:=u_final_amount||','||u_msisdn;
                array_index:=array_index+1;
				ELSE IF var_check = 0  and u_final_amount ='0'
                then
                RAISE NOTICE 'Commission is not defined or is zero for the userId,%,u_msisdn=%,ISOWNER=%', v_beneficary_parent_id, u_msisdn, v_isowner;
                ret_array[array_index]:='0:,Commission is not defined or is zero for the userId,'||v_beneficary_parent_id||', u_msisdn='||u_msisdn||', ISOWNER='||v_isowner;
                array_index:=array_index+1;
				ELSE
                 RAISE NOTICE 'None of the error Occured ,u_final_amount=%,u_msisdn=%,var_check=%,ISOWNER=%', u_final_amount, u_msisdn, var_check, v_isowner;
                 INSERT INTO dual_wallet_auto_c2c(through_user,beneficary_parent_id,transaction_date, on_amount,comm_profile_set_id, foc_amount,file_generated, msisdn)
                 VALUES (v_through_user,v_beneficary_parent_id,date_trunc('day', p_user_date), p_user_amount,u_commision_id,  CAST (u_final_amount AS DOUBLE PRECISION),'N', u_msisdn);
				END IF;
           END IF;
		END IF;
	END LOOP;
   END ;
   END LOOP;
 --Commit;

   p_message := 'SUCCESS';
   RAISE NOTICE 'The sucess for the day%', p_date;
   EXCEPTION
   WHEN unique_violation
   THEN
      --INSERT into Error_log(v_Through_user(Temp_cnt)||v_Beneficary_parent_id(Temp_cnt)||TRUNC(SYSDATE))
      RAISE NOTICE ' DuPlicate Data% %', SQLSTATE, SQLERRM;
      p_message := 'FAIL';
   WHEN OTHERS
   THEN
      --INSERT into Error_log(v_Through_user(Temp_cnt)||v_Beneficary_parent_id(Temp_cnt)||TRUNC(SYSDATE))
      RAISE NOTICE ' Other Errors:% %', SQLSTATE, SQLERRM;
      p_message := 'FAIL';
END;
$body$
LANGUAGE PLPGSQL
;

ALTER FUNCTION postgres_dw.dualwallet_autoc2c_cal ( p_date timestamp, p_message OUT text, p_message_fail OUT text, ret_array OUT text[] ) OWNER TO postgres;
 
 CREATE OR REPLACE FUNCTION postgres_dw.getfinalcommisionforfixvalue ( p_user_id text, p_amount NUMERIC, p_commPct NUMERIC ) RETURNS character varying AS $body$
DECLARE

--This method is used for calculation of  commision for particular user_id and amount
   b_unitvalue         numeric  := 1;
   commvalue           NUMERIC(12,2)  := 0;
   v_commission_rate   NUMERIC(12,2)  := 0;

BEGIN
   RAISE NOTICE 'getfinalcommisionforfixvalue: p_user_id=%,p_amount=%,p_commPct=%', p_user_id, p_amount, p_commPct;
   IF (p_user_id IS NULL)
   THEN
      RAISE NOTICE 'USER ID IS NULL';
      RETURN '0:,USER_ID WAS NULL OR 0,';
   END IF;

   IF (p_amount IS NULL OR p_amount <= 0)
   THEN
      RAISE NOTICE 'AMOUNT  IS NULL OR 0';
      RETURN '0:,AMOUNT FOR CALC WAS NULL OR 0,'||p_user_id;
   END IF;

    SELECT unit_value
    INTO STRICT b_unitvalue
    FROM products
    WHERE product_code = 'ETOPUP';

    v_commission_rate:=p_commPct;
    commvalue := (v_commission_rate * p_amount)/100;

    if (commvalue <= 0 )then
    RETURN '0:, The value of commision is not defined correctly into the preference,'||p_user_id;
    end if;

   RETURN round((commvalue)::numeric,0);

END;
$body$
LANGUAGE PLPGSQL
SECURITY DEFINER
 STABLE;
-- REVOKE ALL ON FUNCTION getfinalcommisionforfixvalue ( p_user_id text, p_amount bigint, p_commPct NUMERIC ) FROM PUBLIC;

ALTER FUNCTION postgres_dw.getfinalcommisionforfixvalue ( p_user_id text, p_amount bigint, p_commPct NUMERIC ) OWNER TO postgres;

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DW_HC_USERTYPE', 'Hirerachy Commission User Type', 'SYSTEMPRF', 'STRING', 'OWNER', 
    NULL, NULL, 20, 'Hirerachy Commission to be given either Parent or Owner or Both', 'Y', 
    'Y', 'C2S', 'This is applied only for Dual Wallet and Hirerachy Commission for Parent=PARENT, Owner=OWNER, Both=BOTH', now(), 'ADMIN', 
    now(), 'SU0001', 'OWNER,PARENT,BOTH', 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DW_HC_PARENT_PCT', 'Hirerachy Commission Parent', 'SYSTEMPRF', 'DECIMAL', '0', 
    0, 100, 20, 'Hirerachy Commission to be calculated for Parent commission in PCT', 'Y', 
    'Y', 'C2S', 'This is applied only for Dual Wallet and Hirerachy Commission to be calculated for Parent commission in PCT', now(), 'ADMIN', 
    now(), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DW_HC_OWNER_PCT', 'Hirerachy Commission Owner', 'SYSTEMPRF', 'DECIMAL', '1.4', 
    0, 100, 20, 'Hirerachy Commission to be calculated for Parent commission in PCT', 'Y', 
    'Y', 'C2S', 'This is applied only for Dual Wallet and Hirerachy Commission to be calculated for Parent commission in PCT', now(), 'ADMIN', 
    now(), 'SU0001', NULL, 'Y');
COMMIT;
